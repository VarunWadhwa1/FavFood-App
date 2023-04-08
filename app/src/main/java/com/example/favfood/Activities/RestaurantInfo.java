package com.example.favfood.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.favfood.Adapters.ItemAdapter;
import com.example.favfood.Adapters.RestaurantAdapter;
import com.example.favfood.MainActivity;
import com.example.favfood.Models.GetItem;
import com.example.favfood.Models.GetRestaurant;
import com.example.favfood.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class RestaurantInfo extends AppCompatActivity {

    ImageView back,favorite,restaurantImage;
    TextView restaurantName,rating,closingTime,restLocation;
    ArrayList<GetItem> items;
    RecyclerView recyclerMenu;
    ProgressBar progressBar3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_info);
        final GetRestaurant restaurant = ((RestaurantAdapter.ObjectWrapperForBinder)getIntent().getExtras().getBinder("restaurant_detail")).getData();
        items = new ArrayList<>();

        recyclerMenu = findViewById(R.id.recyclerMenu);
        restaurantImage = findViewById(R.id.restaurantImage);
        Picasso.get().load(restaurant.getRestaurant_Image()).into(restaurantImage);

        restaurantName = findViewById(R.id.restaurantName2);
        restaurantName.setText(restaurant.getRestaurant_Name());

        rating = findViewById(R.id.rating1);
        rating.setText(String.valueOf(restaurant.getRestaurant_Rating()));

        restLocation = findViewById(R.id.restLocation);
        restLocation.setText(restaurant.getRestaurant_Location());

        closingTime = findViewById(R.id.closingTime);
        closingTime.setText(restaurant.getOpeningTime());

        RatingBar ratingBar = findViewById(R.id.ratingBar2);
        ratingBar.setRating(Float.parseFloat(restaurant.getRestaurant_Rating()));

        favorite = findViewById(R.id.favoriteIcon1);

        if (checkFavoriteRestaurant(restaurant)) {
            favorite.setImageResource(R.drawable.ic_baseline_favorite_24);
            favorite.setTag("favorite");//
        } else {
            favorite.setImageResource(R.drawable.ic_baseline_favorite_order_24);
            favorite.setTag("favorite_empty");
        }
        favorite.setOnClickListener(v -> {
            String tag = favorite.getTag().toString();
            if (tag.equalsIgnoreCase("favorite_empty")) {
                SharedPreference.addFavoriteRestaurant(getApplicationContext(), restaurant);
                Toast.makeText(getApplicationContext(), "Restaurant added to favorite", Toast.LENGTH_SHORT).show();
                favorite.setTag("favorite");
                favorite.setImageResource(R.drawable.ic_baseline_favorite_24);
            }
            else if(tag.equalsIgnoreCase("favorite")){
                favorite.setTag("favorite_empty");
                favorite.setImageResource(R.drawable.ic_baseline_favorite_order_24);
                SharedPreference.removeFavoriteRestaurant(getApplicationContext(), restaurant);
                Toast.makeText(getApplicationContext(), "Restaurant removed from favorite", Toast.LENGTH_SHORT).show();
            }
        });
        back = findViewById(R.id.backPage1);
        back.setOnClickListener(v -> onBackPressed());

        progressBar3 = findViewById(R.id.progressBar3);
        progressBar3.setVisibility(View.VISIBLE);


        if(new MainActivity().isConnected(getApplicationContext())){
            getMenuData();
        } else{
            String item = SharedPreference.getJsonArrays(getApplicationContext(),"item");
            Log.e("Item",item);
            try {
                Data(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerMenu.setLayoutManager(manager);
    }
    public boolean checkFavoriteRestaurant(GetRestaurant getRestaurant) {
        boolean check = false;
        ArrayList<GetRestaurant> favorites = SharedPreference.getFavoriteRestaurants(getApplicationContext());
        if (favorites != null) {
            for (GetRestaurant Restaurant : favorites) {
                //Custom Equal function in Model
                if (Restaurant.equals(getRestaurant)) {
                    check = true;
                    break;
                }
            }
        }
        Log.e("Bool",String.valueOf(check));
        return check;
    }
    private void getMenuData()
    {
        String url = "https://jsonkeeper.com/b/AA22";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONObject responseObj = response.getJSONObject(0);

                JSONArray zomato = responseObj.getJSONArray("Zomato");
                JSONArray swiggy = responseObj.getJSONArray("Swiggy");
                String array = zomato + "#" + swiggy;
                SharedPreference.storeJsonArrays(getApplicationContext(),"item",array);
                Data(array);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(getApplicationContext(), "Fail to get the data..", Toast.LENGTH_SHORT).show());
        queue.add(jsonArrayRequest);
    }
    private void Data(String array) throws JSONException{

        JSONArray zomato = new JSONArray(array.split("#")[0]);
        JSONArray swiggy = new JSONArray(array.split("#")[1]);

        for(int j = 0; j < zomato.length(); j++){
            JSONObject json1 = zomato.getJSONObject(j);
            object(json1,"Zomato");
        }
        for(int j = 0; j < swiggy.length(); j++){
            JSONObject json2 = swiggy.getJSONObject(j);
            object(json2,"Swiggy");
        }
        Collections.shuffle(items);
        ItemAdapter itemAdapter = new ItemAdapter(items);
        progressBar3.setVisibility(View.GONE);
        recyclerMenu.setAdapter(itemAdapter);
    }
    private void object(JSONObject json,String provider) throws JSONException {

        String food_id = json.getString("Food_Id");
        String food_image = json.getString("Food_Image");
        String food_name = json.getString("Food_name");
        String price = json.getString("Price");
        String rating = json.getString("Rating");
        String restaurant = json.getString("Restaurant");
        if(restaurant.contains(restaurantName.getText().toString()))
            items.add(new GetItem(food_id, food_image, food_name,"₹" + price, rating, restaurant, provider));
    }
}