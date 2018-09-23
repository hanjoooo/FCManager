package com.example.khanj.fcmanager.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.khanj.fcmanager.Model.DietRecord;
import com.example.khanj.fcmanager.R;

import java.util.ArrayList;

/**
 * Created by khanj on 2018-09-23.
 */

public class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.ItemViewHolder> {

    ArrayList<DietRecord> mItems;
    private RecordListAdapter.ItemClick itemClick;

    public interface ItemClick{
        public void onClick(View view,int position);
    }
    public void setItemClick(RecordListAdapter.ItemClick itemClick){
        this.itemClick = itemClick;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        public ViewHolder(View view){
            super(view);
            this.view=view;
        }
    }
    public RecordListAdapter(ArrayList<DietRecord> items){
        mItems = items;
    }

    @Override
    public RecordListAdapter.ItemViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_viewitem,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecordListAdapter.ItemViewHolder holder, final int position){
        holder.txDate.setText(mItems.get(position).getDate());
        holder.txmKcal.setText(String.valueOf(mItems.get(position).getmCal())+" kcal");
        holder.txpKcal.setText((String.valueOf(mItems.get(position).getpCal()))+" kcal");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClick != null){
                    itemClick.onClick(v,position);
                }
            }
        });
    }
    @Override
    public int getItemCount(){
        return mItems.size();
    }
    class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView txDate;
        private TextView txpKcal;
        private TextView txmKcal;
        public ItemViewHolder(View itemView) {
            super(itemView);
            txDate = (TextView) itemView.findViewById(R.id.date);
            txmKcal = (TextView) itemView.findViewById(R.id.mkcal);
            txpKcal = (TextView) itemView.findViewById(R.id.pkcal);
        }
    }
}
