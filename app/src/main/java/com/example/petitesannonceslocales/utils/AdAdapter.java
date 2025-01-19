package com.example.petitesannonceslocales.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petitesannonceslocales.R;

import java.util.List;

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.AdViewHolder> {
    private List<Ad> adList;
    private Context context;
    private AdActionListener listener;

    public AdAdapter(List<Ad> adList) {
        this.adList = adList;
    }

    public AdAdapter(Context context, List<Ad> adList, AdActionListener listener) {
        this.context = context;
        this.adList = adList;
        this.listener = listener;
    }

    public interface AdActionListener {
        void onAdSelected(Ad ad);
        void onAdEdit(Ad ad);
        void onAdDelete(Ad ad);
    }

    @NonNull
    @Override
    public AdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext(); // Dynamically set context if it's null
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_ad, parent, false);
        return new AdViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdViewHolder holder, int position) {
        Ad ad = adList.get(position);
        holder.titleTextView.setText(ad.getTitle());
        holder.descriptionTextView.setText(ad.getDescription());
        Glide.with(context).load(ad.getImageUri()).into(holder.imageView);

        holder.itemView.setOnClickListener(v -> listener.onAdSelected(ad));
        holder.editButton.setOnClickListener(v -> listener.onAdEdit(ad));
        holder.deleteButton.setOnClickListener(v -> listener.onAdDelete(ad));
    }

    @Override
    public int getItemCount() {
        if (adList != null) {
            return adList.size();
        }
        return 0;
    }

    public static class AdViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView;
        ImageView imageView;
        Button editButton, deleteButton;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            descriptionTextView = itemView.findViewById(R.id.textViewDescription);
            imageView = itemView.findViewById(R.id.imageViewAd);
            editButton = itemView.findViewById(R.id.buttonModify);
            deleteButton = itemView.findViewById(R.id.buttonDelete);
        }
    }
}

