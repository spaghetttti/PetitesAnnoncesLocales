package com.example.petitesannonceslocales.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petitesannonceslocales.R;

import java.util.List;

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.AdViewHolder> {
    public enum Mode {
        HOME_VIEW, MANAGE_VIEW
    }

    private List<Ad> adList;
    private Context context;
    private AdActionListener listener;
    private Mode mode;

    public AdAdapter(List<Ad> adList, Mode mode) {
        this.adList = adList;
        this.mode = mode;
    }

    public AdAdapter(Context context, List<Ad> adList, AdActionListener listener, Mode mode) {
        this.context = context;
        this.adList = adList;
        this.listener = listener;
        this.mode = mode;
    }

    public interface AdActionListener {
        void onAdSelected(Ad ad);
        void onAdEdit(Ad ad);
        void onAdDelete(Ad ad);
        void onAdLike(Ad ad);
        void onAdContact(Ad ad);

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

        // Adjust UI and actions based on mode
        if (mode == Mode.HOME_VIEW) {
            holder.likeButton.setVisibility(View.VISIBLE);
            holder.contactButton.setVisibility(View.VISIBLE);
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);

            LikedAdsManager likedAdsManager = new LikedAdsManager(context);
            // Update Like button appearance
            if (likedAdsManager.isAdLiked(ad.getId())) {
                holder.likeButton.setText("Liked");
            } else {
                holder.likeButton.setText("Like");
            }

            // Handle Like button click
            holder.likeButton.setOnClickListener(v -> {
                Log.d("ad info", ad.toString());
                if (likedAdsManager.isAdLiked(ad.getId())) {
                    likedAdsManager.unlikeAd(ad.getId());
                    holder.likeButton.setText("Like");
                } else {
                    likedAdsManager.likeAd(ad.getId());
                    holder.likeButton.setText("Liked");
                }
                Toast.makeText(context, "Ad Liked!", Toast.LENGTH_SHORT).show();
            });

            // holder.likeButton.setOnClickListener(v -> listener.onAdLike(ad));
            holder.contactButton.setOnClickListener(v -> listener.onAdContact(ad));
        } else if (mode == Mode.MANAGE_VIEW) {
            holder.likeButton.setVisibility(View.GONE);
            holder.contactButton.setVisibility(View.GONE);
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);

            holder.editButton.setOnClickListener(v -> listener.onAdEdit(ad));
            holder.deleteButton.setOnClickListener(v -> listener.onAdDelete(ad));
        }

        holder.itemView.setOnClickListener(v -> listener.onAdSelected(ad));
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
        Button editButton, deleteButton, likeButton, contactButton;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            descriptionTextView = itemView.findViewById(R.id.textViewDescription);
            imageView = itemView.findViewById(R.id.imageViewAd);
            editButton = itemView.findViewById(R.id.buttonModify);
            deleteButton = itemView.findViewById(R.id.buttonDelete);
            likeButton = itemView.findViewById(R.id.buttonLike);
            contactButton = itemView.findViewById(R.id.buttonContact);

        }
    }
}

