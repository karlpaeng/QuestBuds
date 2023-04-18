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

public class RecAdapterQuests extends RecyclerView.Adapter<RecAdapterQuests.MyViewHolder>{
    public final RecViewInterfaceQuests recViewInterface;
    private ArrayList<ModelQuests> list;
    Context context;


    public RecAdapterQuests(ArrayList<ModelQuests> list, RecViewInterfaceQuests recViewInterface, Context context) {
        this.list = list;
        this.recViewInterface = recViewInterface;
        this.context = context;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView time, text;
        ConstraintLayout clBg;

        public MyViewHolder(final View view, RecViewInterfaceQuests recViewInterface, Context context) {
            super(view);

            time = view.findViewById(R.id.tvTimeOnQuests);
            text = view.findViewById(R.id.tvTextOnQuests);
            clBg = view.findViewById(R.id.clQuests);


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
    public RecAdapterQuests.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recview_quests, parent, false);
        return new RecAdapterQuests.MyViewHolder(itemView, recViewInterface, context);


    }
    @Override
    public void onBindViewHolder(@NonNull RecAdapterQuests.MyViewHolder holder, int position) {
        String timeStr = "12:30 PM";
        String amOrPm = "AM";
        int hour = list.get(position).hour;
        //convert to string
        timeStr = (hour > 12 ? hour - 12 : (hour>0?hour:12)) + ":" + (list.get(position).min < 10 ? "0" : "") + list.get(position).min + " " + (hour >= 12 ? "PM" : "AM");
        holder.time.setText(timeStr);
        holder.text.setText(list.get(position).text);

        if (list.get(position).done) {
            holder.clBg.setBackgroundResource(R.drawable.ripple_white_on_dark);
            holder.time.setTextColor(ContextCompat.getColorStateList(context, R.color.white));
            holder.time.setCompoundDrawableTintList(ContextCompat.getColorStateList(context, R.color.white));
            holder.text.setTextColor(ContextCompat.getColorStateList(context, R.color.white));
        } else {
            holder.clBg.setBackgroundResource(R.drawable.ripple_dark_on_white);
            holder.time.setTextColor(ContextCompat.getColorStateList(context, R.color.gray));
            holder.time.setCompoundDrawableTintList(ContextCompat.getColorStateList(context, R.color.gray));
            holder.text.setTextColor(ContextCompat.getColorStateList(context, R.color.charc));
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
