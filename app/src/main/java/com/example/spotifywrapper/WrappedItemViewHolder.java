package com.example.spotifywrapper;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class WrappedItemViewHolder extends RecyclerView.ViewHolder {
    TextView username;
    TextView date;
    View view;

    WrappedItemViewHolder(View itemView) {
        super(itemView);

        username = itemView.findViewById(R.id.wrap_item_user);
        date = itemView.findViewById(R.id.wrap_item_date);
        view = itemView;
    }
}
