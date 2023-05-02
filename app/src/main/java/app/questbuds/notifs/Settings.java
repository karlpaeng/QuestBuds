package app.questbuds.notifs;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Settings extends AppCompatActivity {
    Button batt, autostart, dev, bug, signout;
    GoogleSignInClient client;
    GoogleSignInOptions options;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setNavigationBarColor(ContextCompat.getColor(Settings.this, R.color.light_gray));
        getWindow().setStatusBarColor(ContextCompat.getColor(Settings.this, R.color.light_gray));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        batt = findViewById(R.id.btnIgnoreBattOpti);
        autostart = findViewById(R.id.btnAllowAutoStart);
        dev = findViewById(R.id.btnAboutDev);
        bug = findViewById(R.id.btnReportBug);
        signout = findViewById(R.id.btnSettingsSignOut);

        options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_cient_id))
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(this, options);

        user = FirebaseAuth.getInstance().getCurrentUser();

        batt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ADGeneric(
                        "Unrestrict Battery Optimizations",
                        "Battery optimizations restrict the notification capabilities of apps. " +
                                "QuestBuds needs to be set as \"Unrestricted\" in the Settings. " +
                                "Tap on \"Okay\" to proceed.",
                        "batt"
                );
            }
        });
        autostart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ADGeneric(
                        "Autostarting needs to be manually set",
                        "Some devices do not allow auto starting on apps. " +
                                "Manually allow QuestBuds' permissions to autostart. " +
                                "Tap on \"Okay\" to proceed.",
                        "autostart"
                );
            }
        });
        dev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    ADAbout();
                } catch (PackageManager.NameNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        bug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ADGeneric(
                        "Found any bugs?",
                        "Contact the developer and provide details for the encountered bug or error. " +
                                "Tap on \"Okay\" to proceed.",
                        "bug"
                );
            }
        });
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ADGeneric(
                        "Currently signed in as " + user.getDisplayName(),
                        "You are about to be signed out",
                        "signout"
                );
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
        File filePath = new File(Settings.this.getExternalFilesDir(null) + "/" + fileName);
        try{
            if(filePath.exists()) filePath.createNewFile();
            else filePath = new File(Settings.this.getExternalFilesDir(null) + "/" + fileName);

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
        File filePath = new File(Settings.this.getExternalFilesDir(null) + "/" + fileName);
        try{
            if(filePath.exists()) filePath.createNewFile();
            else filePath = new File(Settings.this.getExternalFilesDir(null) + "/" + fileName);

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
    public void ADGeneric(String topTxt, String contentTxt, String tag){
        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
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
                }else if (tag.equals("batt")){
                    //
                    WriteToFile("qbBatteryOptimize.txt", "ok");
                    Intent intent = new Intent();
                    intent.setAction(android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    startActivity(intent);
                    alertDialog.dismiss();
                } else if (tag.equals("bug")) {
                    //

                    String mailto = "mailto:karlraphaelbrinas@gmail.com" +
                            "?cc=" +
                            "&subject=" + Uri.encode("QuestBuds Bug Report from " + user.getDisplayName()) +
                            "&body=" + Uri.encode("<Please fill in details, with screenshots if possible, thanks>");
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse(mailto));

                    try {
                        startActivity(emailIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(Settings.this, "Error, could not find email app", Toast.LENGTH_SHORT).show();
                    }
                    alertDialog.dismiss();
                } else if (tag.equals("signout")) {
                    //
                    alertDialog.dismiss();
                    client.signOut();
                    FirebaseAuth.getInstance().signOut();
                    Intent i = new Intent(Settings.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    //Toast.makeText(context, ":"+i.getDataString(), Toast.LENGTH_SHORT).show();
                    startActivity(i);
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
    private void ADAbout() throws PackageManager.NameNotFoundException {

        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
        View v = getLayoutInflater().inflate(R.layout.dialog_about_dev, null);

        TextView version = v. findViewById(R.id.tvVersion);
        TextView git = v.findViewById(R.id.tvGitHub);
        TextView mail = v.findViewById(R.id.tvEmail);
        TextView tree = v.findViewById(R.id.tvLinkTree);

        /*
        ShapeableImageView appLogo = v.findViewById(R.id.ivAppLogo);
        Picasso.get().load(R.drawable.ic_qb_white).into(appLogo);

         */

        PackageManager manager = Settings.this.getPackageManager();
        PackageInfo info = manager.getPackageInfo(
                Settings.this.getPackageName(), 0);
        String ver = info.versionName;
        version.setText("   Version: " + ver);

        builder.setView(v);

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.show();

        git.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //((MainActivity) getActivity()).copyToClip("github.com/karlpaeng");
                Uri uri = Uri.parse("https://github.com/karlpaeng");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ((MainActivity) getActivity()).copyToClip("karlraphaelbrinas@gmail.com");
                String mailto = "mailto:karlraphaelbrinas@gmail.com" +
                        "?cc=" +
                        "&subject=" + Uri.encode(user.getDisplayName() + ", QuestBuds user") ;
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse(mailto));

                try {
                    startActivity(emailIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(Settings.this, "Error, could not find email app", Toast.LENGTH_SHORT).show();
                }
            }
        });
        tree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //((MainActivity) getActivity()).copyToClip("linktr.ee/karlpaeng");
                Uri uri = Uri.parse("https://linktr.ee/karlpaeng");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }
}