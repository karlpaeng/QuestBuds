package app.questbuds.notifs;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    SplashScreen splashScreen;

    Button btn;

    GoogleSignInClient client;
    GoogleSignInOptions options;

    FirebaseFirestore fbfs;
    CollectionReference questsCollection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().setNavigationBarColor(ContextCompat.getColor(MainActivity.this, R.color.white));
        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.white));

        splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(new SplashScreen.KeepOnScreenCondition() {
            @Override
            public boolean shouldKeepOnScreen() {
                return true;
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(runner, 2000);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btn = findViewById(R.id.btnGoogleLogin);

        options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_cient_id))
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(this,options);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(MainActivity.this, Home.class));
                client.signOut();
                FirebaseAuth.getInstance().signOut();
                Intent i = client.getSignInIntent();
                resultLauncher.launch(i);
            }
        });
    }
    private void skipLogin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if(user!= null){
            fbfs = FirebaseFirestore.getInstance();
            HashMap<String , Object> map = new HashMap<>();

            fbfs.collection("user").document(user.getEmail()).set(map);
            fbfs.collection("user").document(user.getEmail()).collection("quests")
                    .get(Source.SERVER)
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "failed to retrieve quests from server", Toast.LENGTH_SHORT).show();
                        }
                    });

            fbfs.collection("user").document(user.getEmail()).collection("notifs")
                    .get(Source.SERVER)
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "failed to retrieve notifications from server", Toast.LENGTH_SHORT).show();
                        }
                    });
            fbfs.collection("user").document(user.getEmail()).collection("last_sign")
                    .whereEqualTo("id", "1").get(Source.SERVER)
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "failed to retrieve sign-in details from server", Toast.LENGTH_SHORT).show();
                        }
                    });

            Toast.makeText(this, "Welcome, "+user.getDisplayName(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (getIntent().getStringExtra("to") != null){
                intent.putExtra("to", getIntent().getStringExtra("to"));
            }
            startActivity(intent);
            finish();
        }
    }
    private final Runnable runner = new Runnable() {
        @Override
        public void run() {
            skipLogin();
            splashScreen.setKeepOnScreenCondition(new SplashScreen.KeepOnScreenCondition() {
                @Override
                public boolean shouldKeepOnScreen() {
                    return false;
                }
            });
        }
    };
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //Toast.makeText(MainActivity.this, ""+result.getResultCode(), Toast.LENGTH_SHORT).show();
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Task<GoogleSignInAccount> taskx = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            GoogleSignInAccount account = taskx.getResult(ApiException.class);

                            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
                            FirebaseAuth.getInstance().signInWithCredential(credential)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            //Toast.makeText(MainActivity.this, ""+task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                            if(task.isSuccessful()){
                                                skipLogin();

                                            }else {
                                                Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        } catch (ApiException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );
    public boolean isInternetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return cm.getActiveNetwork() != null && cm.getNetworkCapabilities(cm.getActiveNetwork()) != null;
        } else {
            return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
        }
    }

}