package com.aibrains.emergency.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.aibrains.emergency.R;
import com.aibrains.emergency.models.CustomAds;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CustomAdsAdapter extends RecyclerView.Adapter<CustomAdsAdapter.AdsViewHolder> {
    Context context;
    ArrayList<CustomAds> customAds ;
    public CustomAdsAdapter(Context context, ArrayList<CustomAds> customAds) {
        this.context = context ;
        this.customAds = customAds ;
    }

    @Override
    public AdsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ads_card,parent,false);
        AdsViewHolder adsViewHolder = new AdsViewHolder(view);
        return adsViewHolder;
    }

    @Override
    public void onBindViewHolder(AdsViewHolder holder, int position) {
        Glide.with(context).load(customAds.get(position).image_url).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return customAds.size();
    }

    public class AdsViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView ;
        public AdsViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.adsImage);
        }
    }
}
