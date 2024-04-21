package com.example.spotifywrapper;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WrappedAdapter extends RecyclerView.Adapter<WrappedItemViewHolder> {
    private List<WrappedDataContainer> list;
    private LayoutInflater inflater;
    private ClickListener clickListener;
    WrappedAdapter(List<WrappedDataContainer> dataContainers, Context context, ClickListener listener) {
        this.list = dataContainers;
        Log.d("WrapAdapt", "listsize: " + this.list.size());
        this.inflater = LayoutInflater.from(context);
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public WrappedItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View wrapItem = inflater.inflate(R.layout.wrap_list_item, parent, false);
        return new WrappedItemViewHolder(wrapItem);
    }

    @Override
    public void onBindViewHolder(@NonNull WrappedItemViewHolder holder, int position) {
        final int index = holder.getAdapterPosition();
        WrappedDataContainer curr = list.get(position);
        holder.username.setText(curr.user);
        holder.date.setText(curr.date);
        holder.view.setOnClickListener((v) -> clickListener.click(index));

        float hue = (float) (curr.epoch % 360);
        int color = Color.HSVToColor(new float[] {(hue * 17) % 360, 0.8f, 0.6f});
        holder.view.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public WrappedDataContainer getItem(int i) {return list.get(i);}
}
