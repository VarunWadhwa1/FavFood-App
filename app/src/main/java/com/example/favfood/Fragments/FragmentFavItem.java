package com.example.favfood.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.favfood.Activities.SharedPreference;
import com.example.favfood.Adapters.ItemAdapter;
import com.example.favfood.MainActivity;
import com.example.favfood.Models.GetItem;
import com.example.favfood.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FragmentFavItem extends Fragment {
    RecyclerView recyclerView,moreItemView;
    TextView result;
    View view;
    ImageView backPressed;
    StringBuilder foodDetails;
    ArrayList<GetItem> items,itemList;
    List<String> itemData;
    ItemAdapter itemAdapter;
    private ProgressBar loadingPB;
    String url;
    DatabaseReference databaseReference;

    public FragmentFavItem() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fav_item, container, false);

        final int[] count = {0};
        url = "https://jsonkeeper.com/b/AA22";

        recyclerView = view.findViewById(R.id.recyclerview4);
        moreItemView = view.findViewById(R.id.recyclerview5);
        result = view.findViewById(R.id.resultFav);
        loadingPB = view.findViewById(R.id.idPBLoading);
        NestedScrollView nestedSV = view.findViewById(R.id.idNestedSV);
        foodDetails = new StringBuilder();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);
        itemData = new ArrayList<>();
        items = new ArrayList<>();

        setFavItem();

        if(new MainActivity().isConnected(view.getContext())){
            getData();
        } else{
            try {
                String item = SharedPreference.getJsonArrays(view.getContext(),"item");
                Log.e("Item",item);
                Data(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        LinearLayoutManager manager = new LinearLayoutManager(view.getContext());
        moreItemView.setLayoutManager(manager);
        nestedSV.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                count[0]++;
                loadingPB.setVisibility(View.VISIBLE);
                if (count[0] < 2) getData();
                else loadingPB.setVisibility(View.GONE);
            }
        });
        backPressed = view.findViewById(R.id.backPage1);
        backPressed.setOnClickListener(v -> requireActivity().onBackPressed());
        return view;
    }

    private void setFavItem() {
        itemList = SharedPreference.getFavoriteItems(view.getContext());

        if (itemList != null) {
            if(itemList.isEmpty()){
                recyclerView.setVisibility(View.GONE);
                result.setVisibility(View.VISIBLE);
            }else{
                itemAdapter = new ItemAdapter(itemList);
                LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(itemAdapter);
            }
        }
    }
    private void getData()
    {
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
                try {
                    JSONObject responseObj = response.getJSONObject(0);

                    JSONArray zomato = responseObj.getJSONArray("Zomato");
                    JSONArray swiggy = responseObj.getJSONArray("Swiggy");
                    String array = zomato + "#" + swiggy;
                    SharedPreference.storeJsonArrays(view.getContext(),"item",array);
                    Data(array);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

        }, error -> Toast.makeText(view.getContext(), "Fail to get the data..", Toast.LENGTH_SHORT).show());
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
        itemAdapter = new ItemAdapter(items);
        moreItemView.setAdapter(itemAdapter);
    }
    private void object(JSONObject json,String provider) throws JSONException {

        String food_id = json.getString("Food_Id");
        String food_image = json.getString("Food_Image");
        String food_name = json.getString("Food_name");
        String price = json.getString("Price");
        String rating = json.getString("Rating");
        String restaurant = json.getString("Restaurant");
        items.add(new GetItem(food_id, food_image, food_name,"₹" + price, rating, restaurant, provider));
    }
    public void onStart(){
        super.onStart();
        setFavItem();
    }
}