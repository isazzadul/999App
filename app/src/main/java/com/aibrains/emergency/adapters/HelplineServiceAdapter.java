package com.aibrains.emergency.adapters;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.aibrains.emergency.R;
import com.aibrains.emergency.models.HelplineService;
import java.util.ArrayList;
import java.util.List;

public class HelplineServiceAdapter extends RecyclerView.Adapter<HelplineServiceAdapter.helplineServiceViewHolder>{
    Context context;
    ArrayList<HelplineService> helplineServices;

    public HelplineServiceAdapter(Context context, ArrayList<HelplineService> helplineServices) {
        this.context = context;
        this.helplineServices = helplineServices;
    }
    public void setFilteredList(ArrayList<HelplineService> itemList){
        this.helplineServices = itemList ;
        notifyDataSetChanged();
    }

    @Override
    public helplineServiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.emergency_services,parent,false);
        helplineServiceViewHolder viewHolder = new helplineServiceViewHolder(view);
        return viewHolder ;
    }

    @Override
    public void onBindViewHolder(helplineServiceViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(helplineServices.get(position).name);
        holder.city.setText(helplineServices.get(position).city);
        holder.mobile.setText(helplineServices.get(position).mobile);
        holder.address.setText(helplineServices.get(position).address);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+helplineServices.get(position).mobile));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return helplineServices.size();
    }

    public class helplineServiceViewHolder extends RecyclerView.ViewHolder{
        TextView name , city , mobile , address;
        LinearLayout layout;
        public helplineServiceViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.serviceName);
            city = itemView.findViewById(R.id.serviceCity);
            mobile = itemView.findViewById(R.id.serviceMobile);
            address = itemView.findViewById(R.id.serviceAdress);
            layout = itemView.findViewById(R.id.service);
        }
    }
}
