package com.example.favfood.Activities;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.favfood.Models.GetItem;
import com.example.favfood.Models.GetRestaurant;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SharedPreference {

    public static final String PREFS_ITEM_NAME = "Fav_Item";
    public static final String FAVORITES_ITEM  = "foods";
    public static final String PREFS_RESTAURANT_NAME = "Fav_Restaurant";
    public static final String FAVORITES_RESTAURANT  = "Restaurant";

    public SharedPreference() {
        super();
    }

    public static void saveFavoriteItem(Context context, List<GetItem> getItems) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_ITEM_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(getItems);
        editor.putString(FAVORITES_ITEM, jsonFavorites);
        editor.apply();
    }
    public static void saveFavoriteRestaurant(Context context, List<GetRestaurant> getRestaurants) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_RESTAURANT_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(getRestaurants);

        editor.putString(FAVORITES_RESTAURANT, jsonFavorites);
        editor.apply();
    }
    public static void addFavoriteItem(Context context, GetItem getItem) {
        List<GetItem> favorites =  getFavoriteItems(context);
        if (favorites == null) {
            favorites = new ArrayList<>();
        }
        favorites.add(getItem);
        saveFavoriteItem(context, favorites);
    }
    public static void addFavoriteRestaurant(Context context, GetRestaurant getRestaurant) {
        List<GetRestaurant> favorites =  getFavoriteRestaurants(context);
        if (favorites == null) {
            favorites = new ArrayList<>();
        }
        favorites.add(getRestaurant);
        saveFavoriteRestaurant(context, favorites);
    }
    public static void removeFavoriteItem(Context context, GetItem getItem) {
        ArrayList<GetItem> favorites = getFavoriteItems(context);
        if (favorites != null) {
            favorites.remove(getItem);
            saveFavoriteItem(context, favorites);
        }
    }
    public static void removeFavoriteRestaurant(Context context, GetRestaurant getRestaurant) {
        ArrayList<GetRestaurant> favorites = getFavoriteRestaurants(context);
        if (favorites != null) {
            favorites.remove(getRestaurant);
            saveFavoriteRestaurant(context, favorites);
        }
    }
    public static ArrayList<GetItem> getFavoriteItems(Context context) {
        SharedPreferences settings;
        List<GetItem> favorites;

        settings = context.getSharedPreferences(PREFS_ITEM_NAME, Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES_ITEM)) {
            String jsonFavorites = settings.getString(FAVORITES_ITEM, null);
            Gson gson = new Gson();
            GetItem[] favoriteItems = gson.fromJson(jsonFavorites, GetItem[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<>(favorites);
        } else {
            return null;
        }
        return (ArrayList<GetItem>) favorites;
    }
    public static ArrayList<GetRestaurant> getFavoriteRestaurants(Context context) {
        SharedPreferences settings;
        List<GetRestaurant> favorites;

        settings = context.getSharedPreferences(PREFS_RESTAURANT_NAME, Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES_RESTAURANT)) {
            String jsonFavorites = settings.getString(FAVORITES_RESTAURANT, null);
            Gson gson = new Gson();
            GetRestaurant[] favoriteItems = gson.fromJson(jsonFavorites, GetRestaurant[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<>(favorites);
        } else {
            return null;
        }
        return (ArrayList<GetRestaurant>) favorites;
    }
    public static void storeJsonArrays(Context context,String key, String value){
        SharedPreferences preferences = context.getSharedPreferences("Json",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key,value);
        editor.apply();
    }
    public static String getJsonArrays(Context context,String key){
        SharedPreferences settings = context.getSharedPreferences("Json", Context.MODE_PRIVATE);
        return settings.getString(key,"");
    }
    public static void deleteAll(Context context){
        context.getSharedPreferences("Fav_Item", Context.MODE_PRIVATE).edit().clear().apply();
        context.getSharedPreferences("Fav_Restaurant", Context.MODE_PRIVATE).edit().clear().apply();
        context.getSharedPreferences("Features", Context.MODE_PRIVATE).edit().clear().apply();
    }
}