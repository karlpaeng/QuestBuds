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

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentQuests#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentQuests extends Fragment implements RecViewInterfaceQuests {
    View v;

    int prevCount;
    TextView tvFin, tvUnfin, tvNew, tvAll;
    RecyclerView recView;
    RecAdapterQuests adapter;
    CollectionReference questsCollection;
    FirebaseFirestore fbfs;

    ArrayList<ModelQuests> list = new ArrayList<>();
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

        prevCount = 4;// set initial category
        tvAll.setTextColor(ContextCompat.getColorStateList(getContext(),R.color.white));
        tvAll.setBackgroundTintList(ContextCompat.getColorStateList(getContext(),R.color.gray));
        tvAll.setBackgroundResource(R.drawable.ripple_white_on_dark);

        fbfs = FirebaseFirestore.getInstance();
        questsCollection = fbfs.collection("user").document(((Home)getActivity()).userId).collection("quests");
        updateRecView();

        tvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectCategory(prevCount, 4);
            }
        });
        tvNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectCategory(prevCount, 3);
            }
        });
        tvFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectCategory(prevCount, 1);
            }
        });

        tvUnfin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectCategory(prevCount, 2);
            }
        });

        return v;
    }
    private final Runnable notifyDataChangeRunner = new Runnable() {
        @Override
        public void run() {
//            Toast.makeText(getContext(), "3", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
        }
    };
    void updateRecView(){
        //main rec view
//        Toast.makeText(getContext(), "1", Toast.LENGTH_SHORT).show();



        //list = ((Home) getActivity()).
        getAllQuestsListFromFS();
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
    private void selectCategory(int prev, int curr){

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
        adapter.notifyItemChanged(position);
        ((Home) getActivity()).updateQuestDoneStatus(
                list.get(position).taskId,
                list.get(position).done
                );

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