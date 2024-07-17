package com.aibrains.emergency.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.aibrains.emergency.PoliceMapView;
import com.aibrains.emergency.R;
import com.aibrains.emergency.SessionManager;
import com.aibrains.emergency.UserHome;
import com.aibrains.emergency.models.AllRequestList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecyclerAllRequestAdapter extends RecyclerView.Adapter<RecyclerAllRequestAdapter.ViewHolder> {

    Context context;
    ArrayList<AllRequestList> arrayList;
    DatabaseReference database = FirebaseDatabase.getInstance().getReference("emReq");
    SessionManager sessionManager ;

    public RecyclerAllRequestAdapter(Context context, ArrayList<AllRequestList> arrayList){
        this.context = context ;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rescue_card,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.uniqueId.setText(arrayList.get(position).uniqueId);
        holder.victimName.setText(arrayList.get(position).name);
        holder.phone.setText(arrayList.get(position).phone);
        holder.comment.setText(arrayList.get(position).comment);
        holder.status.setText(arrayList.get(position).status);
        holder.actionBy.setText(arrayList.get(position).action_by);
        holder.time.setText(arrayList.get(position).timeStamp);
        holder.distanceV.setText("3.699 KM Away"); //need modification

        holder.cardID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,arrayList.get(position).uniqueId,Toast.LENGTH_SHORT).show();
                sessionManager = new SessionManager(context);
                sessionManager.victimIdMapView(arrayList.get(position).uniqueId);
                Query query = database.orderByChild("uniqueId").equalTo(arrayList.get(position).uniqueId);
                Intent intent = new Intent(context, PoliceMapView.class);
                context.startActivity(intent);
                /**
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){
                            ds.getRef().child("status").setValue("On Action");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
                 **/
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView uniqueId, victimName, phone, comment, status, actionBy, time, distanceV;
        LinearLayout cardID;
        public ViewHolder(View itemView) {
            super(itemView);
            uniqueId = itemView.findViewById(R.id.uniqueID);
            victimName = itemView.findViewById(R.id.victimName);
            phone = itemView.findViewById(R.id.cellNum);
            comment = itemView.findViewById(R.id.comment);
            status = itemView.findViewById(R.id.status);
            actionBy = itemView.findViewById(R.id.actionBy);
            time = itemView.findViewById(R.id.timeStamp);
            distanceV = itemView.findViewById(R.id.distanceV);
            cardID = itemView.findViewById(R.id.cardId);
        }
    }
}
