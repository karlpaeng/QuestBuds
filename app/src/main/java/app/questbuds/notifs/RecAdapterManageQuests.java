package app.questbuds.notifs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecAdapterManageQuests extends RecyclerView.Adapter<RecAdapterManageQuests.MyViewHolder>{
    public final RecViewInterfaceManageQuests recViewInterface;
    private ArrayList<ModelQuests> list;
    Context context;


    public RecAdapterManageQuests(ArrayList<ModelQuests> list, RecViewInterfaceManageQuests recViewInterface, Context context) {
        this.list = list;
        this.recViewInterface = recViewInterface;
        this.context = context;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView time, text, days;
        ConstraintLayout clBg;

        public MyViewHolder(final View view, RecViewInterfaceManageQuests recViewInterface, Context context) {
            super(view);

            time = view.findViewById(R.id.tvTimeOnManageQuests);
            text = view.findViewById(R.id.tvTextOnManageQuestsQuests);
            days = view.findViewById(R.id.tvDaysOnManageQuests);
            clBg = view.findViewById(R.id.clManageQuests);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recViewInterface != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            recViewInterface.onClickItem(pos);


                        }

                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public RecAdapterManageQuests.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recview_manage_quests, parent, false);
        return new RecAdapterManageQuests.MyViewHolder(itemView, recViewInterface, context);


    }
    @Override
    public void onBindViewHolder(@NonNull RecAdapterManageQuests.MyViewHolder holder, int position) {
        String timeStr;
        int hour = list.get(position).hour;
        //convert to string
        timeStr = (hour > 12 ? hour - 12 : (hour>0?hour:12)) + ":" + (list.get(position).min < 10 ? "0" : "") + list.get(position).min + " " + (hour >= 12 ? "PM" : "AM");
        holder.time.setText(timeStr);
        holder.text.setText(list.get(position).text);
        holder.days.setText(arrayToPipeSeparatedString(list.get(position).daysWeek));


    }
    private String arrayToPipeSeparatedString(ArrayList<String> daysWeek){

        String retStr = "";
        for (String str: daysWeek) {
            retStr = retStr + str + (str.equals(daysWeek.get(daysWeek.size()-1)) ? "" : ", ");
        }
        return retStr;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void remove(int position){
        //
        list.remove(position);
        notifyItemRemoved(position);
    }
}