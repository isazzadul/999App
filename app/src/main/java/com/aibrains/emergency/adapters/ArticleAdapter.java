package com.aibrains.emergency.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.aibrains.emergency.ArticleDetails;
import com.aibrains.emergency.R;
import com.aibrains.emergency.models.Article;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.articleViewHolder> {

    Context context;
    ArrayList<Article> articles ;
    public ArticleAdapter(Context context, ArrayList<Article> articles) {
        this.context = context ;
        this.articles = articles;
    }

    @Override
    public articleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.article_card,parent,false);
        articleViewHolder viewHolder = new articleViewHolder(view);
        return viewHolder ;
    }

    @Override
    public void onBindViewHolder(articleViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(context).load(articles.get(position).imageLink).into(holder.image);
        //holder.image.setImageURI(Uri.parse(articles.get(position).imageLink));
        holder.headline.setText(articles.get(position).headline);
        holder.subline.setText(articles.get(position).subline);
        holder.article.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ArticleDetails.class);
                intent.putExtra("id",articles.get(position).id);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }


    public class articleViewHolder extends RecyclerView.ViewHolder{
        TextView headline , subline ;
        ImageView image ;
        LinearLayout article;

        public articleViewHolder(View itemView) {
            super(itemView);
            headline = itemView.findViewById(R.id.articleHeadline);
            subline = itemView.findViewById(R.id.articleSubline);
            image = itemView.findViewById(R.id.articleImage);
            article = itemView.findViewById(R.id.articleCard);
        }
    }
}
