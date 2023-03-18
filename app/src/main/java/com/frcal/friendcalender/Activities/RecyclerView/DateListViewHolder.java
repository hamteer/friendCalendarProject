package com.frcal.friendcalender.Activities.RecyclerView;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DateListViewHolder extends RecyclerView.ViewHolder {

    public final View dateView;
    public final DateListViewHolderListener dateListViewHolderListener;


    public DateListViewHolder(@NonNull View itemView, DateListViewHolderListener listener) {
        super(itemView);
        this.dateListViewHolderListener = listener;
        dateView = itemView;
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateListViewHolderListener.onViewHolderClicked(getAdapterPosition());
            }
        });
    }

    public interface DateListViewHolderListener {
        void onViewHolderClicked(int position);
    }

}