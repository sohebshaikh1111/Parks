package com.naindroidtech.parks.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.naindroidtech.parks.R;
import com.naindroidtech.parks.model.Park;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class ParkRecyclerViewAdapter extends RecyclerView.Adapter<ParkRecyclerViewAdapter.ViewHolder> {
    private final List<Park> parkList;
    private final OnParkClickListener parkClickListener;
    private Context mContext;

    public ParkRecyclerViewAdapter(List<Park> parkList, OnParkClickListener parkClickListener, Context context) {
        this.parkList = parkList;
        this.parkClickListener = parkClickListener;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.park_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Park park = parkList.get(position);
        holder.parkName.setText(park.getName());
        holder.parkType.setText(park.getDesignation());
        holder.parkState.setText(park.getStates());
        holder.row_layout.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_animation));

        if (park.getImages().size() > 0) {
            Picasso.get()
                    .load(park.getImages().get(0).getUrl())
                    .placeholder(android.R.drawable.stat_sys_download)
                    .error(android.R.drawable.stat_notify_error)
                    .resize(100, 100)
                    .centerCrop()
                    .into(holder.parkImage);
        }
    }

    @Override
    public int getItemCount() {
        return parkList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView parkImage;
        public TextView parkName;
        public TextView parkType;
        public TextView parkState;
        public ConstraintLayout row_layout;
        OnParkClickListener onParkClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parkImage = itemView.findViewById(R.id.row_park_imageview);
            parkName = itemView.findViewById(R.id.row_par_name_textview);
            parkType = itemView.findViewById(R.id.row_park_type_textview);
            parkState = itemView.findViewById(R.id.row_part_state_textview);
            row_layout = itemView.findViewById(R.id.row_layout);

            this.onParkClickListener = parkClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Park currPark = parkList.get(getAdapterPosition());
            onParkClickListener.onParkClicked(currPark);

        }
    }
}
