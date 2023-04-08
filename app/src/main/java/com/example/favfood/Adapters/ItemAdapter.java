package com.example.favfood.Adapters;

import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.favfood.Activities.Deals;
import com.example.favfood.Activities.ItemInfo;
import com.example.favfood.Models.GetItem;
import com.example.favfood.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.itemsViewHolder> {
    private final ArrayList<GetItem> arrayList;
    View view;
    public ItemAdapter(ArrayList<GetItem> arrayList) {
        this.arrayList = arrayList;
    }
    @NonNull
    @Override
    public itemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new itemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull itemsViewHolder holder, int position) {
        GetItem model = arrayList.get(position);
        holder.itemName.setText(model.getFood_name());
        holder.itemRestaurant.setText(model.getRestaurant());
        holder.Price.setText(model.getPrice());
        holder.Rating.setRating(Float.parseFloat(model.getRating()));
        Picasso.get().load(model.getFood_Image()).into(holder.imageView);
        holder.openDetail.setOnClickListener(v ->
        {
            final GetItem getItem = new GetItem(model.getFood_Id(), model.getFood_Image(), model.getFood_name(), model.getPrice(), model.getRating(), model.getRestaurant(), model.getCarrier());
            final Bundle bundle = new Bundle();
            bundle.putBinder("object_value", new ObjectWrapperForBinder(getItem));
            v.getContext().startActivity(new Intent(v.getContext(), ItemInfo.class).putExtras(bundle));
        });
        holder.findDeals.setOnClickListener(v-> new Deals().FindBestDeal(v,model.getFood_name(),"BestDeal",""));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    static class itemsViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemRestaurant, Price;
        LinearLayout openDetail;
        ImageView imageView;
        RatingBar Rating;
        Button findDeals;
        public itemsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemRestaurant = itemView.findViewById(R.id.itemRestaurant);
            Price = itemView.findViewById(R.id.itemPrice);
            Rating = itemView.findViewById(R.id.itemRatingBar);
            findDeals = itemView.findViewById(R.id.findDeals1);
            openDetail = itemView.findViewById(R.id.detailLayout);
            imageView = itemView.findViewById(R.id.itemImageView);
        }
    }
    public static class ObjectWrapperForBinder extends Binder {

        private final GetItem mData;

        public ObjectWrapperForBinder(GetItem data) {
            mData = data;
        }

        public GetItem getData() {
            return mData;
        }
    }
}
