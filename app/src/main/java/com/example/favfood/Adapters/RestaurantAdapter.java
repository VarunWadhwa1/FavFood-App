package com.example.favfood.Adapters;

import android.content.Context;
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
import com.example.favfood.Activities.RestaurantInfo;
import com.example.favfood.Models.GetRestaurant;
import com.example.favfood.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.viewHolder> {

    private final ArrayList<GetRestaurant> arrayList;
    Context context;
    public RestaurantAdapter(ArrayList<GetRestaurant> arrayList) { this.arrayList = arrayList; }

    @NonNull
    @Override
    public RestaurantAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.activity_restaurants, parent, false);
        return new RestaurantAdapter.viewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RestaurantAdapter.viewHolder holder, int position) {
        GetRestaurant model = arrayList.get(position);

        holder.name.setText(model.getRestaurant_Name());
        holder.rating.setRating(Float.parseFloat(model.getRestaurant_Rating()));
        //Getting Location and Showing only city
        List<String> string = Arrays.asList(model.getRestaurant_Location().split(","));
        String city = string.get(string.size()-2);
        holder.location.setText(city);
        //Setting image
        Picasso.get().load(model.getRestaurant_Image()).into(holder.image);
        //Holder Buttons
        holder.findDeals.setOnClickListener(v-> new Deals().FindBestDeal(v,model.getRestaurant_MainDish(),"BestDeal",model.getRestaurant_Phone()));
        holder.compare.setOnClickListener(v-> new Deals().FindBestDeal(v,model.getRestaurant_MainDish(),"CompareAllItem",model.getRestaurant_Phone()));
        //Layout
        holder.restLayout.setOnClickListener(v -> {
            final GetRestaurant getRestaurant = new GetRestaurant(model.getOpeningTime(), model.getRestaurant_Image(),model.getRestaurant_Location(), model.getRestaurant_MainDish(), model.getRestaurant_Name()
                    ,model.getRestaurant_Phone(), model.getRestaurant_Rating());
            final Bundle bundle = new Bundle();
            bundle.putBinder("restaurant_detail", new ObjectWrapperForBinder(getRestaurant));
            v.getContext().startActivity(new Intent(v.getContext(), RestaurantInfo.class).putExtras(bundle));
        });
    }
    @Override
    public int getItemCount() { return arrayList.size(); }
    public static class viewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name,location;
        RatingBar rating;
        Button findDeals,compare;
        LinearLayout restLayout;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.imageView2);
            name = itemView.findViewById(R.id.textView11);
            location = itemView.findViewById(R.id.textView12);
            rating = itemView.findViewById(R.id.ratingBar);
            findDeals = itemView.findViewById(R.id.findBestDeal);
            compare = itemView.findViewById(R.id.compareAllDeals);
            restLayout = itemView.findViewById(R.id.restLayout);
        }
    }
    public static class ObjectWrapperForBinder extends Binder {

        private final GetRestaurant mData;

        public ObjectWrapperForBinder(GetRestaurant data) {
            mData = data;
        }

        public GetRestaurant getData() {
            return mData;
        }
    }
}
