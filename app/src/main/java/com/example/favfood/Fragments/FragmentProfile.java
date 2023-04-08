package com.example.favfood.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.favfood.Activities.EditProfile;
import com.example.favfood.Activities.FAQ;
import com.example.favfood.Activities.Login;
import com.example.favfood.Activities.SharedPreference;
import com.example.favfood.MainActivity;
import com.example.favfood.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FragmentProfile extends Fragment implements
        CompoundButton.OnCheckedChangeListener{
    TextView name, email, changePass, logout,link,favorite,favRestaurant,Faq;
    SwitchCompat notification, locationAccess;
    Button edit;View view;
    Boolean isSelected, isNotificationSelected;
    ImageView image;
    List<String> userData;
    FirebaseUser user;FirebaseAuth auth;
    DatabaseReference databaseReference;
    String uid;

    public FragmentProfile() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        //        Declaring values for the fields
        name = view.findViewById(R.id.userName1);
        email = view.findViewById(R.id.email2);
        image = view.findViewById(R.id.profileImage2);
        link = view.findViewById(R.id.link);

        userData = new ArrayList<>();
        auth = FirebaseAuth.getInstance();user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        uid = user.getUid();

        //Shared Preferences - Retrieving Data
        SharedPreferences preferences = requireActivity().getSharedPreferences("Features",MODE_PRIVATE);
        isNotificationSelected= preferences.getBoolean("Notification", false);
        isSelected= preferences.getBoolean("Location", false);
        //If user is guest

        if (user.isAnonymous()){
            link.setVisibility(View.VISIBLE);
            String guestName = preferences.getString("Guest","No name");
            name.setText(guestName);
            email.setText("");
        }else{
            RetrieveData();
        }

        //Set the Location Switch
        locationAccess = view.findViewById(R.id.location);
        locationAccess.setSwitchPadding(30);
        locationAccess.setChecked(isSelected);
        locationAccess.setOnCheckedChangeListener(this);

        //Set the Notification Switch
        notification = view.findViewById(R.id.appNotifications);
        notification.setSwitchPadding(30);
        notification.setChecked(isNotificationSelected);
        notification.setOnCheckedChangeListener(this);

        //Change Password
        changePass = view.findViewById(R.id.changePass);
        changePass.setOnClickListener(v -> new FragmentChangePassword().Dialog(v));

        //No of Fav item
        int noOfFav = 0;
        favorite = view.findViewById(R.id.textView15);
        if(SharedPreference.getFavoriteItems(view.getContext())!=null) {
            noOfFav = Objects.requireNonNull(SharedPreference.getFavoriteItems(view.getContext())).size();
        }
        favorite.setText(String.valueOf(noOfFav));
        //No of Favorite Restaurant
        favRestaurant = view.findViewById(R.id.textView13);
        if(SharedPreference.getFavoriteRestaurants(view.getContext())!=null) {
            noOfFav = Objects.requireNonNull(SharedPreference.getFavoriteRestaurants(view.getContext())).size();
        }
        favRestaurant.setText(String.valueOf(noOfFav));

        //Logout
        logout = view.findViewById(R.id.logout2);
        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            SharedPreference.deleteAll(v.getContext());
            Toast.makeText(view.getContext(), "Logged Out", Toast.LENGTH_LONG).show();
            startActivity(new Intent(view.getContext(), Login.class));
            requireActivity().finish();
        });
        //Edit the Current user Profile
        edit = view.findViewById(R.id.editProfile);
        edit.setOnClickListener(v -> v.getContext().startActivity(new Intent(view.getContext(), EditProfile.class)));
        link.setOnClickListener(v -> Link());


        Faq = view.findViewById(R.id.Faq);
        Faq.setOnClickListener(v -> v.getContext().startActivity(new Intent(view.getContext(), FAQ.class)));
        //Return the view which was made
        return view;

    }
    public void SharedPreference(String key, Boolean bool){
        SharedPreferences preferences = requireActivity().getSharedPreferences("Features",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key,bool);
        editor.apply();
    }
    public void RetrieveData() {
        databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    userData.add(Objects.requireNonNull(postSnapshot.getValue()).toString());
                }
                if (userData.size() == 6) {
                    //For Name
                    if(!userData.get(4).equals("")){
                        name.setText(userData.get(4));
                    }else if(!Objects.equals(user.getDisplayName(), "")){
                        name.setText(user.getDisplayName());
                    }else{
                        String value = "No name";
                        name.setText(value);
                    }
                    // For Email or Phone Number
                    if(!Objects.equals(user.getEmail(), "")){
                        email.setText(user.getEmail());
                    } else if (!userData.get(5).equals("")){
                        email.setText(userData.get(5));
                    } else email.setText(user.getPhoneNumber());
                    //For Image
                    if (!userData.get(3).equals("")) {
                        if (new MainActivity().isConnected(view.getContext())) {
                            Picasso.get().load(userData.get(3)).into(image);
                        } else{
                            Picasso.get().load(userData.get(3)).networkPolicy(NetworkPolicy.OFFLINE).into(image);
                        }
                    } else if (!Objects.requireNonNull(user.getPhotoUrl()).toString().equals("")) {
                        Picasso.get().load(user.getPhotoUrl().toString()).into(image);
                    } else {
                        Picasso.get().load(R.drawable.dog).into(image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Profile", "Failed to read value.", error.toException());
            }
        });
    }
    public void Link(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
        View layout  = LayoutInflater.from(view.getContext()).inflate(R.layout.activity_link_email,null,false);
        EditText email = layout.findViewById(R.id.linkEmail);
        EditText password = layout.findViewById(R.id.linkPassword);
        Button reset = layout.findViewById(R.id.reset1);
        Button cancel = layout.findViewById(R.id.cancel1);
        alert.setView(layout);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        reset.setOnClickListener(v -> {
            String linkEmail = email.getText().toString();
            String linkPassword = password.getText().toString();
//            Toast.makeText(getContext(), forgotEmail, Toast.LENGTH_SHORT).show();
            if (TextUtils.isEmpty(linkEmail)|| TextUtils.isEmpty(linkPassword)) {
                Toast.makeText(requireContext(), "Enter fields", Toast.LENGTH_SHORT).show();
            }else {
                AuthCredential credential = EmailAuthProvider.getCredential(linkEmail, linkPassword);
                user.linkWithCredential(credential).addOnCompleteListener(requireActivity(), k -> {
                    if (k.isSuccessful()) {
                        Log.d("Profile", "linkWithCredential:success");
                        //FirebaseUser user = k.getResult().getUser();
                    } else {
                        Log.w("Profile", "linkWithCredential:failure", k.getException());
                        Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            alertDialog.dismiss();
        });
        cancel.setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.show();
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        int id = buttonView.getId();
        if (id == R.id.location) {
            SharedPreference("Location",isChecked);
        }else if (id == R.id.appNotifications) {
            SharedPreference("Notification",isChecked);
        } else {
            Log.e("Switch","Unexpected value: " + id);
        }
    }
}