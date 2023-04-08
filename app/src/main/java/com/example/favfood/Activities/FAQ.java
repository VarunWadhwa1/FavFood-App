package com.example.favfood.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.favfood.Adapters.FaqAdapter;
import com.example.favfood.Models.GetFaq;
import com.example.favfood.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class FAQ extends AppCompatActivity {
    RecyclerView FaqRecyclerView;
    ImageButton FaqBackButton;
    TextView faqContact;
    AdView adView;
    ArrayList<GetFaq> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        list = updateFaq();
        FaqRecyclerView = findViewById(R.id.faq_recycler_view);
        faqContact = findViewById(R.id.faqContactUs);
        FaqAdapter FaqAdapter = new FaqAdapter(list);
        FaqRecyclerView.setAdapter(FaqAdapter);
        FaqRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        FaqBackButton = findViewById(R.id.faqBackButton);
        FaqBackButton.setOnClickListener(v -> onBackPressed());

        faqContact.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_SENDTO);
            i.setData(Uri.parse("mailto:"));
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"favfood@gamail.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "Some Feedback regarding Application.");
            try {
                startActivity(i);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        });

        //Code for Displaying Ad in application.
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        Toast.makeText (getApplicationContext(), "Ad is loading... ", Toast.LENGTH_LONG).show();

    }
    private ArrayList<GetFaq> updateFaq()
    {
        ArrayList<GetFaq> list = new ArrayList<>();
        //Adding data in our List
        list.add( new GetFaq("What is the use of this Application?", "FavFood help you to compare the price of the restaurant nearby and give you the Best Deal."));
        list.add( new GetFaq("Where to get Location?", "Click on the icon near the location text available in your Home screen after enabling it from profile." ));
        list.add( new GetFaq("Map not getting my current Location?", "Sometime the map will not show location it is because the location was just turned on which cause the map to not calibrate. If this problem occurs, you may just open google map in background once and the problem will be solved." ));
        list.add( new GetFaq("What about the price accuracy?", "We are trying our best to get new data from the internet.So the app is currently accurate."));
        list.add( new GetFaq("Do you also have food delivery option.","Sorry but we don't deliver the food. As it is the app  which let us compare food price and We have also gave an option to order it directly from the restaurant"));
        list.add( new GetFaq("Will the app work offline", "Yes, it will work perfectly fine. But the new data from restaurant will not be loaded. So its advised to use internet connection when you are comparing food items"));
        list.add( new GetFaq("Privacy", "Your data is complete secure. We don't have any third party services, So it's secure."));
        list.add( new GetFaq("Difference b/w Best Deal and Compare","The Best deal will give you that restaurant's lowest price Main Dish while compare deals help you to compare all the item that matches your search result. "));
        list.add( new GetFaq("Why there are only few Restaurants available?","As we are currently a new application, We will try to add new things when the app is updated."));
        return list;
    }
}
//        adView.setAdListener(new AdListener() {
//
//            @Override public void onAdLoaded() {
//                Toast.makeText(getApplicationContext(), "Ad is Loaded", Toast.LENGTH_LONG).show();
//            }
//            @Override public void onAdFailedToLoad(@NonNull LoadAdError adError) {
//                Toast.makeText(getApplicationContext(), "Ad Failed to Load ", Toast.LENGTH_LONG).show();
//            }
//            @Override public void onAdOpened() {Toast.makeText(getApplicationContext(), "Ad Opened", Toast.LENGTH_LONG).show();}
//            @Override public void onAdClicked() {Toast.makeText(getApplicationContext(), "Ad Clicked", Toast.LENGTH_LONG).show(); }
//            @Override public void onAdClosed() { Toast.makeText(getApplicationContext(), "Ad is Closed", Toast.LENGTH_LONG).show(); }
//        });