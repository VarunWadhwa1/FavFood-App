package com.example.favfood.Activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.favfood.Adapters.ItemAdapter;
import com.example.favfood.Models.GetItem;
import com.example.favfood.R;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemInfo extends AppCompatActivity {
    ImageView back,favorite,phone,itemImage;
    TextView itemName,price,rating,description,restaurantName,provider;
    MaterialButton deals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);
        description = findViewById(R.id.imageDescription);

        final GetItem item = ((ItemAdapter.ObjectWrapperForBinder)getIntent().getExtras().getBinder("object_value")).getData();

        itemImage = findViewById(R.id.itemImageView1);

        Picasso.get().load(item.getFood_Image()).into(itemImage);

        price = findViewById(R.id.priceItem);
        price.setText(item.getPrice());

        itemName = findViewById(R.id.itemName1);
        itemName.setText(item.getFood_name());

        rating = findViewById(R.id.rating);
        rating.setText(item.getRating());

        restaurantName = findViewById(R.id.restaurant1);
        restaurantName.setText(item.getRestaurant());

        provider = findViewById(R.id.provider);
        provider.setText(item.getCarrier());

        RatingBar ratingBar = findViewById(R.id.ratingBar1);
        ratingBar.setRating(Float.parseFloat(item.getRating()));

        favorite = findViewById(R.id.imageView8);

        if (checkFavoriteItem(item)) {
            favorite.setImageResource(R.drawable.ic_baseline_favorite_24);
            favorite.setTag("favorite");//
        } else {
            favorite.setImageResource(R.drawable.ic_baseline_favorite_order_24);
            favorite.setTag("favorite_empty");
        }
        favorite.setOnClickListener(v -> {
            String tag = favorite.getTag().toString();
            if (tag.equalsIgnoreCase("favorite_empty")) {
                SharedPreference.addFavoriteItem(getApplicationContext(), item);
                Toast.makeText(getApplicationContext(), "Item added to favorite", Toast.LENGTH_SHORT).show();
                favorite.setTag("favorite");
                favorite.setImageResource(R.drawable.ic_baseline_favorite_24);
            }
            else if(tag.equalsIgnoreCase("favorite")){
                favorite.setTag("favorite_empty");
                favorite.setImageResource(R.drawable.ic_baseline_favorite_order_24);
                SharedPreference.removeFavoriteItem(getApplicationContext(), item);
                Toast.makeText(getApplicationContext(), "Item removed from favorite", Toast.LENGTH_SHORT).show();
            }
        });
        back = findViewById(R.id.backPage);
        back.setOnClickListener(v -> onBackPressed());

        phone = findViewById(R.id.callOrder);
        phone.setOnClickListener(v -> new Deals().place(v));

        deals = findViewById(R.id.buttonDeals);
        deals.setOnClickListener(v -> new Deals().FindBestDeal(v,itemName.getText().toString(),"BestDeal",""));

    }
    public boolean checkFavoriteItem(GetItem getItem) {
        boolean check = false;

        ArrayList<GetItem> favorites = SharedPreference.getFavoriteItems(getApplicationContext());
        if (favorites != null) {
            for (GetItem item : favorites) {
                //Custom Equal function in Model
                if (item.equals(getItem)) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }
}