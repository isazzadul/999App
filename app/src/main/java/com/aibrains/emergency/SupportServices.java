package com.aibrains.emergency;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.aibrains.emergency.adapters.ArticleAdapter;
import com.aibrains.emergency.adapters.HelplineServiceAdapter;
import com.aibrains.emergency.models.HelplineService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SupportServices extends AppCompatActivity {
    private SearchView searchView;
    HelplineServiceAdapter helplineServiceAdapter;
    ArrayList<HelplineService> helplineServices = new ArrayList<>();
    DatabaseReference helpline = FirebaseDatabase.getInstance().getReference("extras/supportServices");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_support_services);

        searchView = findViewById(R.id.serviceSearch);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });
        RecyclerView supportService = findViewById(R.id.serviceLists);
        supportService.setLayoutManager(new LinearLayoutManager(this));
        helplineServiceAdapter = new HelplineServiceAdapter(this,helplineServices);
        supportService.setAdapter(helplineServiceAdapter);
        helpline.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    HelplineService helplineService = dataSnapshot.getValue(HelplineService.class);
                    helplineServices.add(helplineService);
                }
                helplineServiceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


    }

    private void filterList(String text) {
        ArrayList<HelplineService> filteredList = new ArrayList<>();
        for(HelplineService item : helplineServices){
            if(item.name.toLowerCase().contains(text.toLowerCase())||
                    item.city.toLowerCase().contains(text.toLowerCase())||
                    item.mobile.toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }
        helplineServiceAdapter.setFilteredList(filteredList);
    }
}