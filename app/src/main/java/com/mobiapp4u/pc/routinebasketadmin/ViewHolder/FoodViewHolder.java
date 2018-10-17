package com.mobiapp4u.pc.routinebasketadmin.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobiapp4u.pc.routinebasketadmin.Common.Common;
import com.mobiapp4u.pc.routinebasketadmin.Interface.ItemClickListner;
import com.mobiapp4u.pc.routinebasketadmin.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener{

    public TextView txtFoodName;
    public ImageView imageView;

    private ItemClickListner itemClickListner;

    public FoodViewHolder(View itemView) {
        super(itemView);

        txtFoodName = (TextView)itemView.findViewById(R.id.menu_name_food);
        imageView = (ImageView)itemView.findViewById(R.id.menu_image_food);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListner(ItemClickListner itemClickListner){
        this.itemClickListner = itemClickListner;
    }

    @Override
    public void onClick(View v) {

        itemClickListner.onClick(v, getAdapterPosition(), false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0,0, getAdapterPosition(), Common.UPDATE);
        menu.add(0,1, getAdapterPosition(), Common.DELETE);
    }
}
