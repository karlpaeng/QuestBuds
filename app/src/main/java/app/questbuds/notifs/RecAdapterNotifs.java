package app.questbuds.notifs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecAdapterNotifs extends RecyclerView.Adapter<RecAdapterNotifs.MyViewHolder> {
    public final RecViewInterfaceNotifs recViewInterface;
    private ArrayList<ModelNotifs> list;


    public RecAdapterNotifs(ArrayList<ModelNotifs> list, RecViewInterfaceNotifs recViewInterface) {
        this.list = list;
        this.recViewInterface = recViewInterface;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView time, message;

        public MyViewHolder(final View view, RecViewInterfaceNotifs recViewInterface) {
            super(view);

            time = view.findViewById(R.id.tvTimeOnRecViewNotifs);
            message = view.findViewById(R.id.tvMessageOnRecViewNotifs);


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
    public RecAdapterNotifs.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recview_notifs, parent, false);
        return new RecAdapterNotifs.MyViewHolder(itemView, recViewInterface);


    }
    @Override
    public void onBindViewHolder(@NonNull RecAdapterNotifs.MyViewHolder holder, int position) {
        String timeStr;
        int hour = list.get(position).hourNotif;
        //convert to string
        timeStr =   (hour > 12 ? hour - 12 : (hour>0?hour:12)) +
                    ":" + (list.get(position).minNotif < 10 ? "0" : "") +
                    list.get(position).minNotif + " " +
                    (hour >= 12 ? "PM" : "AM");
        holder.time.setText(timeStr);
        holder.message.setText(list.get(position).notifText.equals("") ? "none" : list.get(position).notifText);


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
