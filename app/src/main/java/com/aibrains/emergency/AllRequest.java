package com.aibrains.emergency;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aibrains.emergency.adapters.RecyclerAllRequestAdapter;
import com.aibrains.emergency.models.AllRequestList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

// solid -> S

public class AllRequest extends AppCompatActivity {
    ArrayList<AllRequestList> arrayList = new ArrayList<>();
    LinearLayout cardId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_request);
        RecyclerView recyclerView = findViewById(R.id.allRequestCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("emReq");

        RecyclerAllRequestAdapter allRequestAdapter = new RecyclerAllRequestAdapter(this,arrayList);
        recyclerView.setAdapter(allRequestAdapter);



        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                arrayList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    AllRequestList list = dataSnapshot.getValue(AllRequestList.class);
                    String id = list.uniqueId;
                    String userId = list.userId;
                    String name = list.name;
                    String phone = list.phone;
                    String comment = list.comment;
                    String status = list.status;
                    String action_by = list.action_by;
                    String timeStamp = list.timeStamp;

                    // arrayList.add(list);
                    arrayList.add(new AllRequestList(id,userId,name,phone,comment,status,action_by,timeStamp));

                }

                allRequestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}