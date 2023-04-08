package com.example.favfood.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.favfood.Models.GetOrder;
import com.example.favfood.R;

import java.util.ArrayList;

public class OrderAdapter extends  RecyclerView.Adapter<OrderAdapter.viewHolder> {

    private final ArrayList<GetOrder> arrayList;

    public OrderAdapter(ArrayList<GetOrder> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.samplefood, parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        GetOrder model = arrayList.get(position);
        holder.Image.setImageResource(model.getImage());
        holder.name.setText(model.getName());
        holder.deliver.setText(model.getDeliver());
        holder.price.setText(model.getPrice());
        holder.date.setText(model.getDate());
        holder.item.setText(model.getItem());
        holder.saved.setText(model.getSaved());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {

        ImageView Image;
        TextView name,deliver,price,item,date,saved;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            Image = itemView.findViewById(R.id.imageView);
            item = itemView.findViewById(R.id.textView2);
            name = itemView.findViewById(R.id.textView3);
            deliver = itemView.findViewById(R.id.textView4);
            price = itemView.findViewById(R.id.textView5);
            date = itemView.findViewById(R.id.textView);
            saved = itemView.findViewById(R.id.textView6);
        }
    }
}
