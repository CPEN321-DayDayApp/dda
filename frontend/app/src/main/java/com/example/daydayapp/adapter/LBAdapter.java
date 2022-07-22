package com.example.daydayapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daydayapp.MainActivity;
import com.example.daydayapp.model.LBModel;
import com.example.daydayapp.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class LBAdapter extends RecyclerView.Adapter<LBAdapter.ViewHolder> {
    private List<LBModel> LBList;
    private final MainActivity activity;

    public LBAdapter(FragmentActivity fragment) {
        this.activity = (MainActivity) fragment;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lb_list_layout, parent, false);
        return new ViewHolder(itemView);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        LBModel item = LBList.get(position);
        holder.name.setText(item.getName());
        holder.rank.setText(String.valueOf(item.getRank()));
        switch(item.getRank()) {
            case 1:
                holder.LB.setCardBackgroundColor(Color.parseColor("#FFD700"));
                break;
            case 2:
                holder.LB.setCardBackgroundColor(Color.parseColor("#C0C0C0"));
                break;
            case 3:
                holder.LB.setCardBackgroundColor(Color.parseColor("#B87333"));
                break;
            default:
        }

    }

    public int getItemCount(){
        if (LBList == null) return 0;
        return LBList.size();
    }

    public void setRank(List<LBModel> friendLBList) {
        this.LBList = friendLBList;
        notifyDataSetChanged();
    }

    public void setRank() {
        notifyDataSetChanged();
    }

    public Context getContext() {
        return activity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        MaterialButton rank;
        CardView LB;
        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.friendLB_name);
            rank = view.findViewById(R.id.rank_button);
            LB = view.findViewById(R.id.friendLB);
        }
    }
}
