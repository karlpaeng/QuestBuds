package app.questbuds.notifs;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentNotifs#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentNotifs extends Fragment implements RecViewInterfaceNotifs {
    View v;
    RecyclerView recyclerView;
    ArrayList<ModelNotifs> list = new ArrayList<>();
    ImageView addbtn;
    TextView noNotifs;
    RecAdapterNotifs adapter;

    FirebaseFirestore fbfs;
    CollectionReference notifsCollection;

    int hourInt, minInt;
    boolean timeSelected;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentNotifs() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentNotifs.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentNotifs newInstance(String param1, String param2) {
        FragmentNotifs fragment = new FragmentNotifs();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_notifs, container, false);
        recyclerView = v.findViewById(R.id.rvNotifs);
        addbtn = v.findViewById(R.id.ivAddNotif);
        noNotifs = v.findViewById(R.id.tvNoNotifs);

        fbfs = FirebaseFirestore.getInstance();
        notifsCollection = fbfs.collection("user").document(((Home)getActivity()).userId).collection("notifs");

        updateRecView();
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogManageNotifs(((Home)getActivity()).fbfs, ((Home)getActivity()).userId, null, null);
            }
        });




        return v;
    }
    private void updateRecView(){
        getAllNotifsFromFS();
        adapter = new RecAdapterNotifs(list, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClickItem(int position) {
        //alertdia
        alertDialogManageNotifs(((Home)getActivity()).fbfs, ((Home)getActivity()).userId, list.get(position), position);

    }
    public void getAllNotifsFromFS(){
        notifsCollection.orderBy("hour", Query.Direction.ASCENDING).orderBy("min", Query.Direction.ASCENDING).get( Source.CACHE)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //
                        list.clear();
                        for (DocumentSnapshot snap: task.getResult()) {
                            //
                            ModelNotifs notifs = new ModelNotifs(
                                    snap.getId(),
                                    snap.getString("text"),
                                    snap.getLong("hour").intValue(),
                                    snap.getLong("min").intValue()
                            );
                            list.add(notifs);
                        }
                        adapter.notifyDataSetChanged();
                        noNotifs.setVisibility(list.isEmpty() ? View.VISIBLE : View.INVISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "failed to retrieve notifications", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void alertDialogManageNotifs(FirebaseFirestore fbfs, String emailStr, @Nullable ModelNotifs notifs, @Nullable Integer index){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_notif_edit, null);
        //stuff
        TextView top = v.findViewById(R.id.tvTopDiaNotifEdit);
        EditText et = v.findViewById(R.id.etNotifMsg);
        Button btnTime = v.findViewById(R.id.btnSelectNotifTime);

        Button del = v.findViewById(R.id.btnDiaNotifDelete);
        Button done = v.findViewById(R.id.btnDiaNotifEditDone);

        //vars
        timeSelected = false;
        String timeStr = "";
        if (notifs == null){
            hourInt = 0;
            minInt = 0;
            //
            top.setText("Set a new Notification");
            done.setText("Add");
            del.setVisibility(View.INVISIBLE);
        }else{
            et.setText(notifs.notifText);
            timeStr = (notifs.hourNotif > 12 ? notifs.hourNotif - 12 : (notifs.hourNotif > 0 ? notifs.hourNotif : 12)) + ":" + (notifs.minNotif< 10 ? "0" : "") + notifs.minNotif + (notifs.hourNotif >= 12 ? "PM" : "AM");
            btnTime.setText(timeStr);
            hourInt = notifs.hourNotif;
            minInt = notifs.minNotif;
            timeSelected = true;

            /*
            for (String str : quest.daysWeek) {

            }*/

        }



        builder.setView(v);
        //builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //time
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        hourInt = i;
                        minInt = i1;
                        timeSelected = true;
                        btnTime.setText((hourInt > 12 ? hourInt - 12 : (hourInt > 0 ? hourInt : 12)) + ":" + (minInt < 10 ? "0" : "") + minInt + (hourInt>=12 ? "PM" : "AM"));
                    }
                };
                TimePickerDialog timePickerDialog;
                if (notifs == null) { timePickerDialog = new TimePickerDialog(getContext(), 3, onTimeSetListener, hourInt, minInt, false); }
                else { timePickerDialog = new TimePickerDialog(getContext(), 3, onTimeSetListener, hourInt, minInt, false); }
                timePickerDialog.setTitle("Specify the time");
                timePickerDialog.show();
            }
        });
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //
                fbfs.collection("user").document(emailStr).collection("notifs").document(notifs.notifId).delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    String timeStr2 = (notifs.hourNotif > 12 ? notifs.hourNotif - 12 : (notifs.hourNotif > 0 ? notifs.hourNotif : 12)) + ":" + (notifs.minNotif< 10 ? "0" : "") + notifs.minNotif + (notifs.hourNotif >= 12 ? "PM" : "AM");
                                    Toast.makeText(getContext(), "deleted: "+timeStr2+" notification", Toast.LENGTH_SHORT).show();
                                    list.remove(index);
                                    //update rec view
                                    adapter.remove(index);

                                    alertDialog.dismiss();
                                    noNotifs.setVisibility(list.isEmpty() ? View.VISIBLE : View.INVISIBLE);

                                }else Toast.makeText(getContext(), "Failed:"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (timeSelected == false) {
                    Toast.makeText(getContext(), "Select a time for this Notification", Toast.LENGTH_SHORT).show();
                } else {
                    //
                    if(notifs == null){
                        String idStr = UUID.randomUUID().toString();
                        HashMap<String , Object> map = new HashMap<>();
                        map.put("text", et.getText().toString());
                        map.put("hour", hourInt);
                        map.put("min", minInt);

                        fbfs.collection("user").document(emailStr).collection("notifs").document(idStr).set(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(getContext(), ""+emailStr+map.toString(), Toast.LENGTH_SHORT).show();
                                            //update rec view
                                            list.add(new ModelNotifs(
                                                    idStr,
                                                    et.getText().toString(),
                                                    hourInt,
                                                    minInt
                                            ));
                                            adapter.notifyDataSetChanged();
                                            alertDialog.dismiss();

                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Not saved", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }else{
                        //update
                        fbfs.collection("user").document(emailStr).collection("notifs").document(notifs.notifId).update(
                                        "text", et.getText().toString(),
                                        "hour", hourInt,
                                        "min", minInt
                                )
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getContext(), "Successfully updated", Toast.LENGTH_SHORT).show();
                                            //adapter.notifyDataSetChanged();
                                            //update rec view
                                            list.get(index).notifText = et.getText().toString();
                                            list.get(index).hourNotif = hourInt;
                                            list.get(index).minNotif = minInt;

                                            adapter.notifyDataSetChanged();
                                            alertDialog.dismiss();


                                        }
                                        else Toast.makeText(getContext(), "Failed to update", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }

            }
        });

    }
}