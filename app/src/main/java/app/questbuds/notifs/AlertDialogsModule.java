package app.questbuds.notifs;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import javax.annotation.Nullable;

public class AlertDialogsModule extends Activity {


    public AlertDialogsModule() {
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
}