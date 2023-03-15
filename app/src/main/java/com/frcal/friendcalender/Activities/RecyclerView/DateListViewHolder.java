package com.frcal.friendcalender.Activities.RecyclerView;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DateListViewHolder extends RecyclerView.ViewHolder {

    public final View dateView;

    public DateListViewHolder(@NonNull View itemView) {
        super(itemView);
        dateView = itemView;
    }


}