package com.example.favfood.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.favfood.Activities.Login;
import com.example.favfood.Activities.SharedPreference;
import com.example.favfood.Adapters.ItemAdapter;
import com.example.favfood.Adapters.RestaurantAdapter;
import com.example.favfood.Adapters.SlideshowAdapter;
import com.example.favfood.MainActivity;
import com.example.favfood.MapLocation;
import com.example.favfood.Models.GetItem;
import com.example.favfood.Models.GetRestaurant;
import com.example.favfood.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class FragmentHome extends Fragment {

    ViewPager2 viewPager; EditText location,search;
    TextView foodItem; TextInputLayout filter,locationSearch;
    RecyclerView recyclerView,recyclerView2,recyclerView3;
    SlideshowAdapter slideshowAdapter;
    ItemAdapter itemAdapter;
    ArrayList<GetItem> itemList; ArrayList<GetRestaurant> restaurants;
    DatabaseReference databaseReference;
    View view;StringBuilder foodDetails;
    List<String> itemData;
    ProgressBar progressBar1,progressBar2;
    RestaurantAdapter restaurantAdapter;

    public FragmentHome() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);
        itemData = new ArrayList<>();
        foodDetails = new StringBuilder();
        itemList = new ArrayList<>();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(view.getContext(), Login.class));
            requireActivity().finish();
        }
        retrieveFavorites();
        location = view.findViewById(R.id.EditLocation);
        locationSearch = view.findViewById(R.id.textInputLocation);
        search = view.findViewById(R.id.search);
        filter = view.findViewById(R.id.textInputSearch);
        foodItem = view.findViewById(R.id.foodItem);
        recyclerView = view.findViewById(R.id.recyclerView2);
        recyclerView2 = view.findViewById(R.id.recyclerView3);
        recyclerView3 = view.findViewById(R.id.recyclerViewItem);
        restaurants = new ArrayList<>();

        progressBar1 = view.findViewById(R.id.progressBar1);
        progressBar2 = view.findViewById(R.id.progressBar2);

        //View Pager
        viewPager = view.findViewById(R.id.viewpager);
        slideshowAdapter = new SlideshowAdapter(view.getContext());
        viewPager.setAdapter(slideshowAdapter);
        viewPager.setBackgroundColor(Color.TRANSPARENT);
        automateViewPagerSwiping();
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        //For RecyclerView.
        progressBar1.setVisibility(View.VISIBLE);
        progressBar2.setVisibility(View.VISIBLE);

        if(new MainActivity().isConnected(view.getContext())){
            getRestaurantData();
        } else{
            try {
                JSONArray restaurantJson = new JSONArray(SharedPreference.getJsonArrays(view.getContext(),"restaurant"));
                Data(restaurantJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Setting the view of Recycler View to Horizontal
        LinearLayoutManager manager = new LinearLayoutManager(view.getContext(),RecyclerView.HORIZONTAL,false);
        recyclerView.setLayoutManager(manager);
        LinearLayoutManager manager1 = new LinearLayoutManager(view.getContext(),RecyclerView.HORIZONTAL,false);
        recyclerView2.setLayoutManager(manager1);

        //Getting if Location is On/Off.
        SharedPreferences preferences = requireActivity().getSharedPreferences("Features",MODE_PRIVATE);
        boolean isSelected= preferences.getBoolean("Location", false);
        locationSearch.setStartIconOnClickListener(v -> {
            if (!isSelected)
                Toast.makeText(view.getContext(), "Enable Location from Profile and try Again", Toast.LENGTH_SHORT).show();
            else{
                Intent mapIntent = new Intent(view.getContext(), MapLocation.class);
                mapIntent.putExtra("Location",location.getText().toString());
                startActivity(mapIntent);
            }
        });

        //Searching the food Item
        filter.setEndIconOnClickListener(v -> searchMethod());
        filter.setStartIconOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
            try {
                speechIntentResultLauncher.launch((intent));
            }
            catch (Exception e) {
                Toast.makeText(view.getContext(), " " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        search.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                searchMethod();
                return true;
            }
            return false;
        });

        //Getting the values from Map.
        Intent in =  requireActivity().getIntent();
        if(in.getExtras()!= null){
            String loc = in.getStringExtra("Address");
            location.setText(loc);
        }
        storeFavorites();
        return view;
    }

    private void searchMethod() {
        recyclerView3.setVisibility(View.VISIBLE);
        if(!search.getText().toString().isEmpty()) {
            RetrieveItem(search.getText().toString(), "Search");
        }
        else{
            Toast.makeText(view.getContext(), "Enter the item to search", Toast.LENGTH_SHORT).show();
        }
    }
    ActivityResultLauncher<Intent> speechIntentResultLauncher = registerForActivityResult
            (new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK ) {
                        Intent data = result.getData();
                        if(data!=null){
                            ArrayList<String> result1 = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                            search.setText(Objects.requireNonNull(result1).get(0));
                            searchMethod();
                        }
                    }
                }
            });
    //Getting The Restaurant Details
    private void getRestaurantData()
    {
        String url = "https://jsonkeeper.com/b/3DPH";
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONObject responseObj = response.getJSONObject(0);
                JSONArray restaurantJson;
                restaurantJson = responseObj.getJSONArray("Restaurant");
                SharedPreference.storeJsonArrays(view.getContext(),"restaurant",restaurantJson.toString());
                Data(restaurantJson);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(view.getContext(), "Fail to get the data..", Toast.LENGTH_SHORT).show());
        queue.add(jsonArrayRequest);
    }
    public void Data(JSONArray restaurantJson) throws JSONException {
        for(int j = 0; j < restaurantJson.length(); j++){

            JSONObject json = restaurantJson.getJSONObject(j);

            String restaurant_image = json.getString("Restaurant_Image");
            String restaurant_name = json.getString("Restaurant_Name");
            String restaurant_location = json.getString("Restaurant_Location");
            String restaurant_mainDish = json.getString("Restaurant_MainDish");
            String restaurant_rating = json.getString("Restaurant_Rating");
            String restaurant_opening = json.getString("OpeningTime");
            String restaurant_phone = json.getString("Restaurant_Phone");
            restaurants.add(new GetRestaurant(restaurant_opening,restaurant_image,restaurant_location,restaurant_mainDish,restaurant_name,
                    restaurant_phone,restaurant_rating));
        }
        //Restaurants
        Collections.shuffle(restaurants);
        restaurantAdapter = new RestaurantAdapter(restaurants);
        recyclerView.setAdapter(restaurantAdapter);
        progressBar1.setVisibility(View.GONE);

        //Recent Order Recycler View
        recyclerView2.setAdapter(restaurantAdapter);
        progressBar2.setVisibility(View.GONE);
    }
    // For View Pager automatic slider
    private void automateViewPagerSwiping() {
        final long DELAY_MS = 1500;
        final long PERIOD_MS = 6000;
        final Handler handler = new Handler();
        final Runnable update = () -> {
            if (viewPager.getCurrentItem() == slideshowAdapter.getItemCount() - 1) {
                viewPager.setCurrentItem(0);
            }
            else {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
            }
        };
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, DELAY_MS, PERIOD_MS);
    }

    //Get the item From Database
    public void RetrieveItem(@NonNull String food,String method) {
        itemList.clear();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if(!Objects.equals(postSnapshot.getKey(), "users")) {
                        String carrier = postSnapshot.getKey();
                        for(DataSnapshot dataSnapshot1 : postSnapshot.getChildren()){
                            for(DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren())
                                foodDetails.append(Objects.requireNonNull(dataSnapshot2.getValue())).append(",");

                            if(method.equals("Search")) {
                                if (foodDetails.toString().toLowerCase().contains(food)) {
                                    itemData = Arrays.asList(String.valueOf(foodDetails).split(","));

                                    GetItem item = new GetItem(itemData.get(0), itemData.get(1), itemData.get(2), "₹ " + itemData.get(3),
                                            itemData.get(4),itemData.get(5),carrier);
                                    itemList.add(item);
                                }
                                foodDetails.setLength(0);
                            }
                        }
                    }
                }
                if(method.equals("Search")){
                    Collections.shuffle(itemList);
                    itemAdapter = new ItemAdapter(itemList);
                    if(itemList.isEmpty()) foodItem.setText(getString(R.string.not_found));
                    else foodItem.setText(getString(R.string.food_item));
                    LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL, false);
                    recyclerView3.setLayoutManager(layoutManager);
                    recyclerView3.setAdapter(itemAdapter);
                    foodItem.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void storeFavorites(){
        Map<String, Object> data = new HashMap<>();
        SharedPreferences settings = view.getContext().getSharedPreferences("Fav_Item", Context.MODE_PRIVATE);
        String item = settings.getString("foods","");

        SharedPreferences settings1 = view.getContext().getSharedPreferences("Fav_Restaurant", Context.MODE_PRIVATE);
        String restaurantList = settings1.getString("Restaurant","");

        if(!item.equals("") && !restaurantList.equals("")) {
            data.put("FavItem", item);
            data.put("FavRestaurant", restaurantList);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Favorite").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).set(data)
                    .addOnSuccessListener(documentReference -> Log.d("TAG", "Successful"))
                    .addOnFailureListener(e -> Log.w("TAG", "Error adding document", e));
        }
    }
    public void retrieveFavorites(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Favorite").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).
                get().addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.contains("FavItem") && documentSnapshot.contains("FavRestaurant")) {
                        String item = Objects.requireNonNull(documentSnapshot.get("FavItem")).toString();
                        String restaurant = Objects.requireNonNull(documentSnapshot.get("FavRestaurant")).toString();

                        Log.e("Tag", item + " " + restaurant);

                        Gson gson = new Gson();
                        GetItem[] favoriteItems = gson.fromJson(item, GetItem[].class);
                        GetRestaurant[] favoriteRestaurants = gson.fromJson(restaurant, GetRestaurant[].class);

                        //Favourites
                        if (!item.equals("") && !restaurant.equals("")) {
                            SharedPreference.saveFavoriteItem(view.getContext(), new ArrayList<>(Arrays.asList(favoriteItems)));
                            SharedPreference.saveFavoriteRestaurant(view.getContext(), new ArrayList<>(Arrays.asList(favoriteRestaurants)));
                        }
                    }
        }).addOnFailureListener(e -> Toast.makeText(view.getContext(), e.toString(), Toast.LENGTH_SHORT).show());
    }
}