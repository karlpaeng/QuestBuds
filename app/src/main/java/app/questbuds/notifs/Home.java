package app.questbuds.notifs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Home extends AppCompatActivity {
    ShapeableImageView userPic;
    TextView quests, add, notifs;
    FrameLayout fragLayout;
    GoogleSignInClient client;
    GoogleSignInOptions options;
    FirebaseFirestore fbfs;
    CollectionReference questsCollection;
    String userId;
    String dayWeek;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setNavigationBarColor(ContextCompat.getColor(Home.this, R.color.light_gray));
        getWindow().setStatusBarColor(ContextCompat.getColor(Home.this, R.color.light_gray));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        quests = findViewById(R.id.btnTasks);
        add = findViewById(R.id.btnAddTasks);
        notifs = findViewById(R.id.btnNotifs);

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

        selectFragment(1);

        userId = user.getEmail();//"sampleUserId";//
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        dayWeek = new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime());
        //Toast.makeText(this, dayWeek, Toast.LENGTH_SHORT).show();

        fbfs = FirebaseFirestore.getInstance();
        questsCollection = fbfs.collection("user").document(userId).collection("quests");

        //questsList = getQuestsListFromFS();
        userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialogsModule adm = new AlertDialogsModule();
                LayoutInflater inflater = getLayoutInflater();
                adm.alertDialogAccount(Home.this, inflater, user, client);

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
    public void updateQuestDoneStatus(String id, boolean value){
        questsCollection.document(id)
                .update("done", value)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            //
                        }else{
                            //
                            Toast.makeText(Home.this, "Failed to update", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Home.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public ArrayList<ModelQuests> getFinishedQuestsFromFS(){
        ArrayList<ModelQuests> retList = new ArrayList<>();
        //
        return retList;
    }

}