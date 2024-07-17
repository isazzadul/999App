package com.aibrains.emergency;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.aibrains.emergency.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ArticleDetails extends AppCompatActivity {
    private ImageView image;
    private TextView headline , description ;
    String id;
    Context context;
    DatabaseReference article = FirebaseDatabase.getInstance().getReference("extras/articles");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_article_details);
        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");

        image = findViewById(R.id.mainImage);
        headline = findViewById(R.id.articleDetailsHeadline);
        description = findViewById(R.id.articleDetails);

        article.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Glide.with(ArticleDetails.this).load(snapshot.child("imageLink").getValue().toString()).into(image);
                headline.setText(snapshot.child("headline").getValue().toString());
                description.setText(snapshot.child("description").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


    }
}