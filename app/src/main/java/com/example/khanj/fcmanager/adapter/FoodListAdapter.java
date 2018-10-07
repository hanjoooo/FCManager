package com.example.khanj.fcmanager.adapter;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.khanj.fcmanager.Model.FoodCalroie;
import com.example.khanj.fcmanager.R;

import java.util.ArrayList;

/**
 * Created by khanj on 2018-10-06.
 */

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.ItemViewHolder> {

    ArrayList<FoodCalroie> mItems;
    private FoodListAdapter.ItemClick itemClick;

    public interface ItemClick{
        public void onClick(View view,int position);
    }
    public void setItemClick(FoodListAdapter.ItemClick itemClick){
        this.itemClick = itemClick;
    }

    public  FoodListAdapter(ArrayList<FoodCalroie>items){
        mItems = items;

    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        public ViewHolder(View view){
            super(view);
            this.view=view;
        }
    }
    @Override
    public FoodListAdapter.ItemViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fcalorie_viewitem,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FoodListAdapter.ItemViewHolder holder, final int position){
        holder.txfName.setText(mItems.get(position).getfName());
        holder.txfCal.setText(""+mItems.get(position).getfCal()+" kcal");
        holder.txfFat.setText(""+mItems.get(position).getfFat()+" g");
        holder.txfProtein.setText(""+mItems.get(position).getfProtiens()+" g");
        holder.txfCarbs.setText(""+mItems.get(position).getfCarbs()+" g");
        holder.txfFibers.setText(""+mItems.get(position).getfMinerals()+" g");
        holder.txfVitamin.setText(""+mItems.get(position).getfVitamin()+" g");
        if(mItems.get(position).getfName().equals("허니버터칩")){
            holder.imageView.setImageResource(R.drawable.honeybutter);
        }

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
        private TextView txfName;
        private TextView txfCal;
        private TextView txfProtein;
        private TextView txfCarbs;
        private TextView txfFat;
        private TextView txfFibers;
        private TextView txfVitamin;
        private ImageView imageView;
        public ItemViewHolder(View itemView) {
            super(itemView);
            txfName = (TextView)itemView.findViewById(R.id.fname);
            txfCal =(TextView)itemView.findViewById(R.id.fcal);
            txfFat=(TextView)itemView.findViewById(R.id.ffat);
            txfProtein=(TextView)itemView.findViewById(R.id.fpro);
            txfCarbs=(TextView)itemView.findViewById(R.id.fcarb);
            txfFibers=(TextView)itemView.findViewById(R.id.ffib);
            txfVitamin=(TextView)itemView.findViewById(R.id.fvit);
            imageView=(ImageView)itemView.findViewById(R.id.fimage);
        }
    }
}