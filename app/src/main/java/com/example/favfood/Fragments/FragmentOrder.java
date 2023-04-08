package com.example.favfood.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.favfood.Activities.SharedPreference;
import com.example.favfood.Adapters.OrderAdapter;
import com.example.favfood.Adapters.RestaurantAdapter;
import com.example.favfood.Models.GetOrder;
import com.example.favfood.Models.GetRestaurant;
import com.example.favfood.R;

import java.util.ArrayList;

public class FragmentOrder extends Fragment {
    View view;
    RecyclerView recyclerView,recyclerFavRest;
    TextView result;
    ArrayList<GetRestaurant> restaurantList;
    public FragmentOrder() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order, container, false);

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerFavRest = view.findViewById(R.id.recyclerFavRest);
        result = view.findViewById(R.id.resultFav1);

        ArrayList<GetOrder> orders = new ArrayList<>();
        orders.add(new GetOrder(R.drawable.pizza,"Pizza", "Delivered By Varun","₹ 150", "Items: 1 x Pizza","Delivered on 12-05-2022" ,"Amount Saved: ₹150"));
        orders.add(new GetOrder(R.drawable.burgerking,"Burger", "Delivered By Faizan","₹ 250", "Items: 1 x Chicken Burger","Delivered on 12-06-2022" ,"Amount Saved: ₹150"));
        OrderAdapter adapter = new OrderAdapter(orders);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        ImageView backPressed = view.findViewById(R.id.backPage2);
        backPressed.setOnClickListener(v -> requireActivity().onBackPressed());
        return view;
    }
    private void setFavItem() {
        restaurantList = SharedPreference.getFavoriteRestaurants(view.getContext());

        if (restaurantList != null) {
            if(restaurantList.isEmpty()){
                recyclerFavRest.setVisibility(View.GONE);
                result.setVisibility(View.VISIBLE);
            }else{
                RestaurantAdapter restaurantAdapter = new RestaurantAdapter(restaurantList);
                LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
                recyclerFavRest.setLayoutManager(layoutManager);
                recyclerFavRest.setAdapter(restaurantAdapter);
            }
        }
    }
    public void onStart(){
        super.onStart();
        setFavItem();
    }
}