package com.example.favfood.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.favfood.Models.GetFaq;
import com.example.favfood.R;

import java.util.ArrayList;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.FaqViewHolder> {
    ArrayList<GetFaq> arrayList;

    public FaqAdapter(ArrayList<GetFaq> arrayList) { this.arrayList = arrayList; }

    @NonNull
    @Override
    public FaqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View FaqView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_faq,parent,false);
        return new FaqViewHolder(FaqView);
    }

    @Override
    public void onBindViewHolder(@NonNull FaqViewHolder holder, int position) {
        GetFaq model = arrayList.get(position);
        holder.title.setText(model.getTitle());
        holder.title.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_expand_more_24,0);
        holder.description.setText(model.getContent());
        holder.title.setOnClickListener(v ->{
            if(holder.description.getVisibility()==View.GONE){
                holder.title.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_expand_less_24,0);
                holder.description.setVisibility(View.VISIBLE);
            }
            else{
                holder.title.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_expand_more_24,0);
                holder.description.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    public static class FaqViewHolder extends RecyclerView.ViewHolder {
        TextView title,description;
        public FaqViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.faq_recyclerView_title);
            description = itemView.findViewById(R.id.faq_recyclerView_description);
        }
    }
}