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

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
 * Use the {@link FragmentQuestsEdit#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentQuestsEdit extends Fragment implements RecViewInterfaceManageQuests{
    View v;

    RecyclerView recyclerView;
    ArrayList<ModelQuests> list = new ArrayList<>();
    ImageView addbtn;
    TextView noQuests;
    RecAdapterManageQuests adapter;

    FirebaseFirestore fbfs;
    CollectionReference questsCollection;

    int hourInt, minInt;
    boolean timeSelected;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentQuestsEdit() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentQuestsEdit.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentQuestsEdit newInstance(String param1, String param2) {
        FragmentQuestsEdit fragment = new FragmentQuestsEdit();
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
        v = inflater.inflate(R.layout.fragment_quests_edit, container, false);
        recyclerView = v.findViewById(R.id.rvManageQuests);
        addbtn = v.findViewById(R.id.btnAddQuests);
        noQuests = v.findViewById(R.id.tvNoManageQuests);

        fbfs = FirebaseFirestore.getInstance();
        questsCollection = fbfs.collection("user").document(((Home)getActivity()).userId).collection("quests");

        updateRecView();
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                alertDialogManageQuests(((Home)getActivity()).fbfs, ((Home)getActivity()).userId, null, null);
                //adm.
            }
        });


        return v;
    }


    void updateRecView(){
        //main rec view
//        Toast.makeText(getContext(), "1", Toast.LENGTH_SHORT).show();
        //list = ((Home) getActivity()).getAllQuestsListFromFS(adapter);


        //list = ((Home) getActivity()).getQuestsListFromFS();
        //handler
        //Handler handler = new Handler();
        //handler.postDelayed(notifyDataChangeRunner, 200);

//        Toast.makeText(getContext(), "2", Toast.LENGTH_SHORT).show();
        //proceed

        getAllQuestsListFromFS();
        adapter = new RecAdapterManageQuests(list, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

    }
    @Override
    public void onClickItem(int position) {

        alertDialogManageQuests(((Home)getActivity()).fbfs, ((Home)getActivity()).userId, list.get(position), position);
    }
    public void alertDialogManageQuests(FirebaseFirestore fbfs, String emailStr, @Nullable ModelQuests quest, @Nullable Integer index){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_quest_edit, null);
        //stuff
        TextView top = v.findViewById(R.id.tvTopDiaQuestEdit);
        EditText et = v.findViewById(R.id.etQuestText);
        Button btnTime = v.findViewById(R.id.btnSelectQuestTime);
        CheckBox cbSun = v.findViewById(R.id.cbSun);
        CheckBox cbMon = v.findViewById(R.id.cbMon);
        CheckBox cbTue = v.findViewById(R.id.cbTue);
        CheckBox cbWed = v.findViewById(R.id.cbWed);
        CheckBox cbThu = v.findViewById(R.id.cbThu);
        CheckBox cbFri = v.findViewById(R.id.cbFri);
        CheckBox cbSat = v.findViewById(R.id.cbSat);
        Button del = v.findViewById(R.id.btnDiaQuestEditDelete);
        Button done = v.findViewById(R.id.btnDiaQuestEditDone);

        //vars
        timeSelected = false;
        ArrayList<String> days = new ArrayList<>();
        if (quest == null){
            hourInt = 0;
            minInt = 0;
            //
            top.setText("Add a new Quest");
            done.setText("Add");
            del.setVisibility(View.INVISIBLE);
        }else{
            et.setText(quest.text);
            String timeStr = (quest.hour > 12 ? quest.hour - 12 : (quest.hour > 0 ? quest.hour : 12)) + ":" + (quest.min < 10 ? "0" : "") + quest.min + (quest.hour>=12 ? "PM" : "AM");
            btnTime.setText(timeStr);
            hourInt = quest.hour;
            minInt = quest.min;
            timeSelected = true;

            cbSun.setChecked(quest.daysWeek.contains("Sun"));
            cbMon.setChecked(quest.daysWeek.contains("Mon"));
            cbTue.setChecked(quest.daysWeek.contains("Tue"));
            cbWed.setChecked(quest.daysWeek.contains("Wed"));
            cbThu.setChecked(quest.daysWeek.contains("Thu"));
            cbFri.setChecked(quest.daysWeek.contains("Fri"));
            cbSat.setChecked(quest.daysWeek.contains("Sat"));
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
                if (quest == null) { timePickerDialog = new TimePickerDialog(getContext(), 3, onTimeSetListener, hourInt, minInt, false); }
                else { timePickerDialog = new TimePickerDialog(getContext(), 3, onTimeSetListener, hourInt, minInt, false); }
                timePickerDialog.setTitle("Specify the time");
                timePickerDialog.show();
            }
        });
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //
                fbfs.collection("user").document(emailStr).collection("quests").document(quest.taskId).delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getContext(), "deleted:"+quest.text, Toast.LENGTH_SHORT).show();
                                    list.remove(index);
                                    //update rec view
                                    adapter.remove(index);

                                    alertDialog.dismiss();

                                }else Toast.makeText(getContext(), "Failed:"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cbSun.isChecked()){ days.add("Sun"); }
                if (cbMon.isChecked()){ days.add("Mon"); }
                if (cbTue.isChecked()){ days.add("Tue"); }
                if (cbWed.isChecked()){ days.add("Wed"); }
                if (cbThu.isChecked()){ days.add("Thu"); }
                if (cbFri.isChecked()){ days.add("Fri"); }
                if (cbSat.isChecked()){ days.add("Sat"); }

                if(et.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Don't leave the Quest description blank!", Toast.LENGTH_SHORT).show();
                } else if (timeSelected == false) {
                    Toast.makeText(getContext(), "Select a time for this Quest", Toast.LENGTH_SHORT).show();
                } else if (days.isEmpty()) {
                    Toast.makeText(getContext(), "Select at least one day of the week", Toast.LENGTH_SHORT).show();
                } else {
                    //
                    if(quest == null){
                        String idStr = UUID.randomUUID().toString();
                        HashMap<String , Object> map = new HashMap<>();
                        map.put("text", et.getText().toString());
                        map.put("hour", hourInt);
                        map.put("min", minInt);
                        map.put("done", false);



                        map.put("daysOfWeek", days);

                        fbfs.collection("user").document(emailStr).collection("quests").document(idStr).set(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            //Toast.makeText(getContext(), ""+emailStr+map.toString(), Toast.LENGTH_SHORT).show();
                                            //update rec view
                                            list.add(new ModelQuests(
                                                    idStr,
                                                    et.getText().toString(),
                                                    hourInt,
                                                    minInt,
                                                    false,
                                                    days
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
                        fbfs.collection("user").document(emailStr).collection("quests").document(quest.taskId).update(
                                        "text", et.getText().toString(),
                                        "hour", hourInt,
                                        "min", minInt,
                                        "daysOfWeek", days
                                )
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getContext(), "Successfully updated", Toast.LENGTH_SHORT).show();
                                            //adapter.notifyDataSetChanged();
                                            //update rec view
                                            list.get(index).text = et.getText().toString();
                                            list.get(index).hour = hourInt;
                                            list.get(index).min = minInt;
                                            list.get(index).daysWeek = days;

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
    public void getAllQuestsListFromFS(){
        //ArrayList<ModelQuests> retList = new ArrayList<>();

        //Source source = (sourceStr.equals("server") ? Source.SERVER : Source.CACHE);

        questsCollection.orderBy("hour", Query.Direction.ASCENDING).orderBy("min", Query.Direction.ASCENDING).get( Source.CACHE)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        list.clear();
                        for (DocumentSnapshot snap: task.getResult()) {
                            //Toast.makeText(Home.this, "fs size:"+task.getResult().size(), Toast.LENGTH_SHORT).show();
                            ///snap.toObject(ModelTasks.class);
                            ModelQuests quest = new ModelQuests(
                                    snap.getId(),
                                    snap.getString("text"),
                                    snap.getLong("hour").intValue(),
                                    snap.getLong("min").intValue(),
                                    snap.getBoolean("done"),
                                    (ArrayList<String>) snap.get("daysOfWeek")
                            );
                            //Toast.makeText(Home.this, quest.toString(), Toast.LENGTH_SHORT).show();
                            list.add(quest);
                            //Toast.makeText(Home.this, "after add:"+questsList.size(), Toast.LENGTH_SHORT).show();

                        }
                        adapter.notifyDataSetChanged();
                        noQuests.setVisibility(list.isEmpty() ? View.VISIBLE : View.INVISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "failed to retrieve quests", Toast.LENGTH_SHORT).show();
                    }
                });

        //Toast.makeText(Home.this, "before return:"+questsList.size(), Toast.LENGTH_SHORT).show();
        //return retList;
    }
}