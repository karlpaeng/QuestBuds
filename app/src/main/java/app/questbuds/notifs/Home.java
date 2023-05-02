package app.questbuds.notifs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Home extends AppCompatActivity {
    ShapeableImageView userPic;
    TextView quests, add, notifs, settings;
    FrameLayout fragLayout;
    GoogleSignInClient client;
    GoogleSignInOptions options;
    FirebaseFirestore fbfs;
    CollectionReference questsCollection;
    String userId;
    String dayWeek;

    boolean showCurrent;

    boolean lastSignIfEmpty;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setNavigationBarColor(ContextCompat.getColor(Home.this, R.color.light_gray));
        getWindow().setStatusBarColor(ContextCompat.getColor(Home.this, R.color.light_gray));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //fixAutoStarting();
        ActivityCompat.requestPermissions(Home.this, new String[]{
                android.Manifest.permission.SCHEDULE_EXACT_ALARM,
                android.Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                android.Manifest.permission.POST_NOTIFICATIONS,
                android.Manifest.permission.RECEIVE_BOOT_COMPLETED,

        }, PackageManager.PERMISSION_GRANTED);

        if (ReadFromFile("qbAutoStart.txt").equals("")){
            ADGeneric(
                    "Autostarting needs to be manually set",
                    "Some devices do not allow auto starting on apps. " +
                            "Manually allow QuestBuds' permissions to autostart. " +
                            "Tap on \"Okay\" to proceed.",
                    "autostart"
            );
        }
        if (ReadFromFile("qbBatteryOptimize.txt").equals("")){
            ADGeneric(
                    "Unrestrict Battery Optimizations",
                    "Battery optimizations restrict the notification capabilities of apps. " +
                            "QuestBuds needs to be set as \"Unrestricted\" in the Settings. " +
                            "Tap on \"Okay\" to proceed.",
                    "batt"
            );
        }


        quests = findViewById(R.id.btnTasks);
        add = findViewById(R.id.btnAddTasks);
        notifs = findViewById(R.id.btnNotifs);
        settings = findViewById(R.id.btnSettings);

        fragLayout = findViewById(R.id.fragmentLayout);

        userPic = findViewById(R.id.ivUserPicture);
        userPic.setVisibility(View.VISIBLE);

        options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_cient_id))
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(this, options);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Picasso.get().load(user.getPhotoUrl()).into(userPic);
        showCurrent = false;
        int selFrag = 1;



        if (getIntent().getStringExtra("to") != null) {
            if (getIntent().getStringExtra("to").equals("current")){
                showCurrent = true;
                /*
                FragmentQuests fragmentQuests = new FragmentQuests();
                fragmentQuests.updateRecView(2);
                fragmentQuests.selectCategory(fragmentQuests.prevCount, 3);

                 */
            } else if (getIntent().getStringExtra("to").equals("notifs")) {
                selFrag = 3;
            }
        }
        selectFragment(selFrag);




        userId = user.getEmail();//"sampleUserId";//
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        dayWeek = new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime());
        //Toast.makeText(this, dayWeek, Toast.LENGTH_SHORT).show();

        fbfs = FirebaseFirestore.getInstance();
        questsCollection = fbfs.collection("user").document(userId).collection("quests");
        //checkLastSignIfEmpty();


        //questsList = getQuestsListFromFS();
        userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater inflater = getLayoutInflater();
                alertDialogAccount(Home.this, inflater, user, client);

            }
        });
        quests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFragment(1);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFragment(2);
            }
        });
        notifs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFragment(3);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Home.this, Settings.class);
                startActivity(i);
            }
        });
    }

    public void selectFragment(int f){

        if (f!=1) {
            quests.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.gray)));
            quests.setBackgroundTintList(ContextCompat.getColorStateList(this,R.color.white));


        }
        if (f!=2) {
            add.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.gray)));
            add.setBackgroundTintList(ContextCompat.getColorStateList(this,R.color.white));
        }
        if (f!=3) {
            notifs.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.gray)));
            notifs.setBackgroundTintList(ContextCompat.getColorStateList(this,R.color.white));
        }
        Fragment fragment = new FragmentQuests();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (f){
            case 1:
                fragment = new FragmentQuests();
                quests.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.white)));
                quests.setBackgroundTintList(ContextCompat.getColorStateList(this,R.color.gray));
                quests.setBackgroundResource(R.drawable.ripple_white_on_dark);
                break;
            case 2:
                fragment = new FragmentQuestsEdit();
                add.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.white)));
                add.setBackgroundTintList(ContextCompat.getColorStateList(this,R.color.gray));
                add.setBackgroundResource(R.drawable.ripple_white_on_dark);
                break;
            case 3:
                fragment = new FragmentNotifs();
                notifs.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.white)));
                notifs.setBackgroundTintList(ContextCompat.getColorStateList(this,R.color.gray));
                notifs.setBackgroundResource(R.drawable.ripple_white_on_dark);
                break;
        }
        fragmentTransaction.replace(R.id.fragmentLayout,fragment);
        fragmentTransaction.commit();

    }
    public void alertDialogAccount(Context context, LayoutInflater inflater, FirebaseUser user, GoogleSignInClient cli){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = inflater.inflate(R.layout.dialog_account, null);

        //Toast.makeText(context, "asd", Toast.LENGTH_SHORT).show();

        ShapeableImageView pic = v.findViewById(R.id.ivUserPictureDia);
        TextView name = v.findViewById(R.id.tvUserNameDia);
        TextView email = v.findViewById(R.id.tvUserEmailDia);
        Button out = v.findViewById(R.id.btnGoogleLogout);

        Picasso.get().load(user.getPhotoUrl()).into(pic);
        name.setText(user.getDisplayName());
        email.setText(user.getEmail());

        builder.setView(v);
        //builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                cli.signOut();
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(context, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //Toast.makeText(context, ":"+i.getDataString(), Toast.LENGTH_SHORT).show();
                context.startActivity(i);
            }
        });


    }
    public void ADGeneric(String topTxt, String contentTxt, String tag){
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        View v = getLayoutInflater().inflate(R.layout.dialog_generic, null);

        TextView top = v.findViewById(R.id.tvTopDiaGeneric);
        TextView content = v.findViewById(R.id.tvContentDiaGeneric);
        Button ok = v.findViewById(R.id.btnOKDiaGeneric);
        Button canc = v.findViewById(R.id.btnCancelDiaGeneric);

        top.setText(topTxt);
        content.setText(contentTxt);

        builder.setView(v);
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tag.equals("autostart")){
                    fixAutoStarting();
                    WriteToFile("qbAutoStart.txt", "ok");
                    alertDialog.dismiss();
                } else if (tag.equals("batt")) {
                    WriteToFile("qbBatteryOptimize.txt", "ok");
                    Intent intent = new Intent();
                    intent.setAction(android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    startActivity(intent);
                    alertDialog.dismiss();
                }

            }
        });
        canc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });




    }
    private void fixAutoStarting(){
        String manufacturer = "xiaomi";
        if(manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
            //this will open auto start screen where user can enable permission for your app
            Intent intent2 = new Intent();
            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent2.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            startActivity(intent2);
        }
    }

    public void WriteToFile(String fileName, String content){
        File filePath = new File(Home.this.getExternalFilesDir(null) + "/" + fileName);
        try{
            if(filePath.exists()) filePath.createNewFile();
            else filePath = new File(Home.this.getExternalFilesDir(null) + "/" + fileName);

            FileOutputStream writer = new FileOutputStream(filePath);
            writer.write(content.getBytes());
            writer.flush();
            writer.close();
            //Log.e("TAG", "Wrote to file: "+fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String ReadFromFile(String fileName){
        String line,line1 = "";
        File filePath = new File(Home.this.getExternalFilesDir(null) + "/" + fileName);
        try{
            if(filePath.exists()) filePath.createNewFile();
            else filePath = new File(Home.this.getExternalFilesDir(null) + "/" + fileName);

            InputStream instream = new FileInputStream(filePath);
            if (instream != null) {
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                try {
                    while ((line = buffreader.readLine()) != null)
                        line1= line1 + line + "\n";
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            instream.close();
            //Log.e("TAG", "Update to file: "+fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return line1;
    }

}