package com.aibrains.emergency.Family;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.aibrains.emergency.Functions;
import com.aibrains.emergency.LostPhoneService.PhoneListAdapter;
import com.aibrains.emergency.R;
import com.aibrains.emergency.interfaces.GetID;
import com.aibrains.emergency.models.FamilyList;
import com.aibrains.emergency.models.PhoneList;

import java.util.ArrayList;

public class FamilyListAdapter extends RecyclerView.Adapter<FamilyListAdapter.ViewHolder> {
    Context context;
    ArrayList<FamilyList> arrayList;

    public FamilyListAdapter(Context context, ArrayList<FamilyList> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public FamilyListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.family_member_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FamilyListAdapter.ViewHolder holder, int position) {
        FamilyList familyList = arrayList.get(position);


        holder.member.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                Functions.addedBy = familyList.addedBy;
                Functions.memberUserID = familyList.userID;
            }
        });
        holder.name.setText(familyList.name);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        CardView member ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.fm_name);
            member = itemView.findViewById(R.id.familyMemberCard);
        }
    }
}
