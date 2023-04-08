package com.example.favfood.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.favfood.Activities.Deals;
import com.example.favfood.Models.GetCompareItem;
import com.example.favfood.R;

import java.util.ArrayList;

public class CompareAdapter extends RecyclerView.Adapter<CompareAdapter.itemsViewHolder> {
    private final ArrayList<GetCompareItem> arrayList;
    View view;
    public CompareAdapter(ArrayList<GetCompareItem> arrayList) {
        this.arrayList = arrayList;
    }
    @NonNull
    @Override
    public itemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_compare, parent, false);
        return new itemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull itemsViewHolder holder, int position) {
        GetCompareItem model = arrayList.get(position);
        holder.itemName.setText(model.getFood_name());
        holder.Price.setText(model.getPrice());
        holder.provider.setText(model.getCarrier());
        holder.placeOrder.setOnClickListener(v-> new Deals().place(v));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    static class itemsViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, Price,provider;
        Button placeOrder;
        public itemsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            itemName = itemView.findViewById(R.id.restaurantName1);
            Price = itemView.findViewById(R.id.itemPrice1);
            placeOrder = itemView.findViewById(R.id.placeOrder1);
            provider = itemView.findViewById(R.id.provider2);
        }
    }
}
