package com.example.favfood.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.favfood.Adapters.CompareAdapter;
import com.example.favfood.Models.GetCompareItem;
import com.example.favfood.Models.GetItem;
import com.example.favfood.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Deals extends AppCompatActivity {

    DatabaseReference databaseReference;
    List<String> itemList;
    ArrayList<GetCompareItem> compareItemsList;
    StringBuilder foodDetails, items, foodName, Price, BestDealItem, provider;
    float lowest;String mobile ="";
    int position;
    RecyclerView recyclerView;

    public void FindBestDeal(View view, String name, String method,String phone) {
        mobile = phone;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);
        itemList = new ArrayList<>();
        compareItemsList = new ArrayList<>();
        foodDetails = new StringBuilder();
        BestDealItem = new StringBuilder();
        Price = new StringBuilder();
        foodName = new StringBuilder();
        items = new StringBuilder();
        provider = new StringBuilder();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String carrier = postSnapshot.getKey();
                    if (!Objects.equals(postSnapshot.getKey(), "users")) {
                        for (DataSnapshot dataSnapshot1 : postSnapshot.getChildren()) {
                            //Getting value from Database using Model
                            GetItem getItem = dataSnapshot1.getValue(GetItem.class);
                            //Saving the detail in a string
                            foodName.append(Objects.requireNonNull(getItem).getFood_name()).append("\n");
                            Price.append(Objects.requireNonNull(getItem).getPrice()).append("\n");
                            provider.append(carrier).append("\n");
                        }
                    }
                }

                String[] temp = foodName.toString().split("\n");
                int[] index = new int[temp.length];
                int j = 0;
                float[] priceItem = new float[temp.length];
                for (int i = 0; i < temp.length; i++) {
                    String array = temp[i];
                    if (array.contains(name)) {
                        index[j] = i;
                        priceItem[j] = Float.parseFloat(Price.toString().split("\n")[i]);
                        j++;
                    }
                }
                //Declaring item for loop
                lowest = priceItem[0];
                position = index[0];
                //Getting Lowest Item Position From Loop
                for (int i = 0; i < j; i++) {
                    if (lowest > priceItem[i]) {
                        lowest = priceItem[i];
                        position = index[i];
                    } else {
                        foodDetails.append(foodName.toString().split("\n")[index[i]]).append(",").append(Price.toString().split("\n")[index[i]]).
                                append(",").append(provider.toString().split("\n")[index[i]]);
                        foodDetails.append("\n");
                    }
                }
//                Log.e("Tag", Arrays.toString(priceItem));

                //Getting Name and Price for cheapest item
                BestDealItem.append(foodName.toString().split("\n")[position]).append(",").append(Price.toString().split("\n")[position]).
                        append(",").append(provider.toString().split("\n")[position]);
                //Check if we are
                if (method.equals("CompareAllItem")) {
                    BestDealItem.append("\n");
                    BestDealItem.append(foodDetails);
//                    Log.e("Tag", BestDealItem.toString());
                }
//                Log.e("ListItem",BestDealItem.toString());
                //Opening a Dialog window with Details.
                dialog(view, BestDealItem.toString(), method);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(view.getContext(), error.toException().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void dialog(View view, String extra, String method) {
        AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
        View layout = LayoutInflater.from(view.getContext()).inflate(R.layout.popup_deals, null, false);
        TextView restaurant = layout.findViewById(R.id.restaurantName);
        TextView price = layout.findViewById(R.id.textView17);
        TextView carrier = layout.findViewById(R.id.provider1);
        Button placeOrder = layout.findViewById(R.id.placeOrder);
        Button close = layout.findViewById(R.id.closeDeal);
        LinearLayout recyclerLayout = layout.findViewById(R.id.recyclerLayout);
        recyclerView = layout.findViewById(R.id.recyclerDeal);

        //Setting the dialog view.
        alert.setView(layout);
        //Creating the dialog to show
        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        alertDialog.setCanceledOnTouchOutside(false);
        //Setting the value inside our textView
        itemList = Arrays.asList(extra.split("\n"));
        restaurant.setText(itemList.get(0).split(",")[0]);
        String price1 = "₹" + itemList.get(0).split(",")[1];
        price.setText(price1);
        carrier.setText(itemList.get(0).split(",")[2]);

        if (method.equals("CompareAllItem")) {

            recyclerLayout.setVisibility(View.VISIBLE);
            for (int i = 1; i < itemList.size(); i++) {
                GetCompareItem getCompareItem = new GetCompareItem(itemList.get(i).split(",")[0], "₹" + itemList.get(i).split(",")[1], itemList.get(i).split(",")[2]);
                compareItemsList.add(getCompareItem);
                Collections.shuffle(compareItemsList);
                CompareAdapter compareAdapter = new CompareAdapter(compareItemsList);

                LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(compareAdapter);
            }
        }
        placeOrder.setOnClickListener(this::place);
        close.setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.show();
    }

    public void place(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        if(mobile.equals("")){
            mobile = "7034232439";
        }
        intent.setData(Uri.parse("tel:"+ mobile));
        view.getContext().startActivity(intent);
    }
}