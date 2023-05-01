package app.questbuds.notifs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentQuests#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentQuests extends Fragment implements RecViewInterfaceQuests {
    View v;

    int prevCount;
    TextView tvFin, tvUnfin, tvNew, tvAll, noQuests;
    RecyclerView recView;
    RecAdapterQuests adapter;
    CollectionReference questsCollection;
    FirebaseFirestore fbfs;

    ArrayList<ModelQuests> list = new ArrayList<>();

    int currHr, currMin, startHr, startMin, endHr, endMin;
    int mixedTimeStart, mixedTimeEnd, mixedCurrTime;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentQuests() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentQuests.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentQuests newInstance(String param1, String param2) {
        FragmentQuests fragment = new FragmentQuests();
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
        v = inflater.inflate(R.layout.fragment_quests, container, false);

        tvUnfin = v.findViewById(R.id.tvTasksSortByUnfinished);
        tvFin = v.findViewById(R.id.tvTasksSortByFinished);
        tvNew = v.findViewById(R.id.tvTasksSortByNew);
        tvAll = v.findViewById(R.id.tvTasksSortByAll);
        recView = v.findViewById(R.id.rvYourQuests);
        noQuests = v.findViewById(R.id.tcNoQuests);


        //prevCount = ((Home)getActivity()).showCurrent ? 3 : 4;
        if (((Home)getActivity()).showCurrent){
            prevCount = 3;
            tvNew.setTextColor(ContextCompat.getColorStateList(getContext(),R.color.white));
            tvNew.setBackgroundTintList(ContextCompat.getColorStateList(getContext(),R.color.gray));
            tvNew.setBackgroundResource(R.drawable.ripple_white_on_dark);
        }else {
            prevCount = 4;
            tvAll.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.white));
            tvAll.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.gray));
            tvAll.setBackgroundResource(R.drawable.ripple_white_on_dark);
        }


        fbfs = FirebaseFirestore.getInstance();
        questsCollection = fbfs.collection("user").document(((Home)getActivity()).userId).collection("quests");

        checkLastSignIfEmpty();
        //updateRecView(3);

        tvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateRecView(3);

                selectCategory(prevCount, 4);
            }
        });
        tvNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateRecView(2);

                selectCategory(prevCount, 3);
            }
        });
        tvFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateRecView(0);

                selectCategory(prevCount, 1);
            }
        });

        tvUnfin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateRecView(1);
                selectCategory(prevCount, 2);
            }
        });

        return v;
    }

    public void updateRecView(int cat){
        //main rec view
//        Toast.makeText(getContext(), "1", Toast.LENGTH_SHORT).show();



        //list = ((Home) getActivity()).
        getAllQuestsListFromFS(cat);
        //handler
        //Handler handler = new Handler();
        //handler.postDelayed(notifyDataChangeRunner, 200);

//        Toast.makeText(getContext(), "2", Toast.LENGTH_SHORT).show();
        //proceed

        adapter = new RecAdapterQuests(list, this, getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recView.setLayoutManager(layoutManager);
        recView.setItemAnimator(new DefaultItemAnimator());
        recView.setAdapter(adapter);


    }
    public void selectCategory(int prev, int curr){

        switch (prev){
            case 1:
                tvFin.setTextColor(ContextCompat.getColorStateList(getContext(),R.color.gray));
                tvFin.setBackgroundTintList(ContextCompat.getColorStateList(getContext(),R.color.white));
                break;
            case 2:
                tvUnfin.setTextColor(ContextCompat.getColorStateList(getContext(),R.color.gray));
                tvUnfin.setBackgroundTintList(ContextCompat.getColorStateList(getContext(),R.color.white));
                break;
            case 3:
                tvNew.setTextColor(ContextCompat.getColorStateList(getContext(),R.color.gray));
                tvNew.setBackgroundTintList(ContextCompat.getColorStateList(getContext(),R.color.white));
                break;
            case 4:
                tvAll.setTextColor(ContextCompat.getColorStateList(getContext(),R.color.gray));
                tvAll.setBackgroundTintList(ContextCompat.getColorStateList(getContext(),R.color.white));
                break;
        }


        switch (curr){
            case 1:
                tvFin.setTextColor(ContextCompat.getColorStateList(getContext(),R.color.white));
                tvFin.setBackgroundTintList(ContextCompat.getColorStateList(getContext(),R.color.gray));
                tvFin.setBackgroundResource(R.drawable.ripple_white_on_dark);
                break;
            case 2:
                tvUnfin.setTextColor(ContextCompat.getColorStateList(getContext(),R.color.white));
                tvUnfin.setBackgroundTintList(ContextCompat.getColorStateList(getContext(),R.color.gray));
                tvUnfin.setBackgroundResource(R.drawable.ripple_white_on_dark);
                break;
            case 3:
                tvNew.setTextColor(ContextCompat.getColorStateList(getContext(),R.color.white));
                tvNew.setBackgroundTintList(ContextCompat.getColorStateList(getContext(),R.color.gray));
                tvNew.setBackgroundResource(R.drawable.ripple_white_on_dark);
                break;
            case 4:
                tvAll.setTextColor(ContextCompat.getColorStateList(getContext(),R.color.white));
                tvAll.setBackgroundTintList(ContextCompat.getColorStateList(getContext(),R.color.gray));
                tvAll.setBackgroundResource(R.drawable.ripple_white_on_dark);
                break;
        }
        prevCount = curr;
    }

    @Override
    public void onClickItem(int position) {

        list.get(position).done = !(list.get(position).done);
//        adapter.notifyItemRemoved(position);
        adapter.notifyItemChanged(position);
        updateQuestDoneStatus(
                list.get(position).taskId,
                list.get(position).done
                );

    }

    private void getAllQuestsListFromFS(int category){
        //ArrayList<ModelQuests> retList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        currHr = Integer.parseInt (new SimpleDateFormat("H", Locale.ENGLISH).format(date.getTime()));
        currMin = Integer.parseInt (new SimpleDateFormat("m", Locale.ENGLISH).format(date.getTime()));

        mixedTimeStart = 0;
        mixedTimeEnd = 1440;
        mixedCurrTime = (currHr*60) + currMin;


        //Source source = (sourceStr.equals("server") ? Source.SERVER : Source.CACHE);
        //for past quests
        //((Home)getActivity()).dayWeek = new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime());

        Query query = null;
        switch (category){
            case 0:
                query = questsCollection
                        .whereArrayContains("daysOfWeek", ((Home)getActivity()).dayWeek)
                        .whereEqualTo("done", true)
                        .orderBy("hour", Query.Direction.ASCENDING)
                        .orderBy("min", Query.Direction.ASCENDING);
                fbfs.collection("user").document(((Home)getActivity()).userId).collection("notifs")
                        .orderBy("hour").orderBy("min")
                        .get(Source.CACHE)
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                //
                                //list.clear();

                                mixedTimeStart = 0;
                                mixedTimeEnd = 1440;
                                for (DocumentSnapshot snap : task.getResult()) {
                                    //
                                    int mixedSnapTime = (snap.getLong("hour").intValue() * 60) + snap.getLong("min").intValue();
                                    if (mixedCurrTime >= mixedSnapTime) {
                                        mixedTimeEnd = mixedSnapTime;
                                    }
                                    if (mixedCurrTime < mixedSnapTime) {
                                        break;
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "failed to retrieve notifications", Toast.LENGTH_SHORT).show();
                            }
                        });

                break;
            case 1:
                query = questsCollection
                        .whereArrayContains("daysOfWeek", ((Home)getActivity()).dayWeek)
                        .whereEqualTo("done", false)
                        .orderBy("hour", Query.Direction.ASCENDING)
                        .orderBy("min", Query.Direction.ASCENDING);
                fbfs.collection("user").document(((Home)getActivity()).userId).collection("notifs")
                        .orderBy("hour").orderBy("min")
                        .get(Source.CACHE)
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                //
                                //list.clear();
                                mixedTimeStart = 0;
                                mixedTimeEnd = 1440;

                                for (DocumentSnapshot snap : task.getResult()) {
                                    //
                                    int mixedSnapTime = (snap.getLong("hour").intValue() * 60) + snap.getLong("min").intValue();
                                    if (mixedCurrTime >= mixedSnapTime) {
                                        mixedTimeEnd = mixedSnapTime;
                                    }
                                    if (mixedCurrTime < mixedSnapTime) {
                                        break;
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "failed to retrieve notifications", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case 2:
                query = questsCollection
                        .whereArrayContains("daysOfWeek", ((Home)getActivity()).dayWeek)
                        .orderBy("hour", Query.Direction.ASCENDING)
                        .orderBy("min", Query.Direction.ASCENDING);

                fbfs.collection("user").document(((Home)getActivity()).userId).collection("notifs")
                        .orderBy("hour").orderBy("min")
                        .get(Source.CACHE)
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                //
                                mixedTimeStart = 0;
                                mixedTimeEnd = 1440;
                                for (DocumentSnapshot snap: task.getResult()) {
                                    //
                                    int mixedSnapTime = (snap.getLong("hour").intValue() * 60) + snap.getLong("min").intValue();
                                    if (mixedCurrTime < mixedSnapTime){
                                        mixedTimeEnd = mixedSnapTime;
                                        break;
                                    }
                                    if (mixedCurrTime >= mixedSnapTime){
                                        mixedTimeStart = mixedSnapTime;
                                    }
//                                    list.add(notifs);
                                }
//                                adapter.notifyDataSetChanged();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "failed to retrieve notifications", Toast.LENGTH_SHORT).show();
                            }
                        });

                break;
            case 3:
                query = questsCollection
                        .whereArrayContains("daysOfWeek", ((Home)getActivity()).dayWeek)
                        .orderBy("hour", Query.Direction.ASCENDING)
                        .orderBy("min", Query.Direction.ASCENDING);

                mixedTimeStart = 0;
                mixedTimeEnd = 1440;
                break;
        }



        query.get( Source.CACHE)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        list.clear();
                        for (DocumentSnapshot snap: task.getResult()) {
                            //Toast.makeText(Home.this, "fs size:"+task.getResult().size(), Toast.LENGTH_SHORT).show();
                            ///snap.toObject(ModelTasks.class);

                            int questMixedTime = (snap.getLong("hour").intValue()*60) + snap.getLong("min").intValue();
                            if (questMixedTime >= mixedTimeStart && questMixedTime < mixedTimeEnd){
                                ModelQuests quest = new ModelQuests(
                                        snap.getId(),
                                        snap.getString("text"),
                                        snap.getLong("hour").intValue(),
                                        snap.getLong("min").intValue(),
                                        snap.getBoolean("done"),
                                        (ArrayList<String>) snap.get("daysOfWeek")
                                );
                                list.add(quest);
                            }
                            //Toast.makeText(Home.this, quest.toString(), Toast.LENGTH_SHORT).show();

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

    private void updateQuestDoneStatus(String id, boolean value){
        questsCollection.document(id)
                .update("done", value)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            //
                        }else{
                            //
                            Toast.makeText(getContext(), "Failed to update", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void checkLastSignIfEmpty(){

        fbfs.collection("user").document(((Home)getActivity()).userId).collection("last_sign")
                .whereEqualTo("id", "1").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Calendar calendar = Calendar.getInstance();
                        Date date = calendar.getTime();
                        String dateNow = new SimpleDateFormat("dd-MMM-YYYY", Locale.ENGLISH).format(date.getTime());
                        if (task.isSuccessful()){
                            if (task.getResult().size() == 1){
                                for (DocumentSnapshot doc : task.getResult()){
                                    if(!dateNow.equals(doc.getString("date"))){
                                        //update all, update lastsign
                                        fbfs.collection("user").document(((Home)getActivity()).userId).collection("quests").get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        for (DocumentSnapshot snap: task.getResult()) {
                                                            snap.getReference().update("done", false);

                                                        }
                                                        updateRecView(((Home) getActivity()).showCurrent ? 2 : 3);

                                                        selectCategory(prevCount, (((Home) getActivity()).showCurrent ? 3 : 4));
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                    }
                                                });
                                        //
                                        doc.getReference().update("date", dateNow);

                                    }else {
                                        updateRecView(((Home) getActivity()).showCurrent ? 2 : 3);
                                        selectCategory(prevCount, (((Home) getActivity()).showCurrent ? 3 : 4));
                                    }
                                }
                            }else if (task.getResult().size() == 0){
                                HashMap<String , Object> map = new HashMap<>();
                                map.put("id", "1");
                                map.put("date", dateNow);

                                fbfs.collection("user").document(((Home)getActivity()).userId).collection("last_sign").document("1")
                                        .set(map);
                                updateRecView(3);
                            }
                            //Toast.makeText(Home.this, "last sign:" + lastSignIfEmpty, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "failed to retrieve sign-in details from server", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}