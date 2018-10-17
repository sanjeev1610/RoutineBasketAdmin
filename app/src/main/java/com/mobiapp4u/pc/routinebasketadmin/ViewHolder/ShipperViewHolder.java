package com.mobiapp4u.pc.routinebasketadmin.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mobiapp4u.pc.routinebasketadmin.Interface.ItemClickListner;
import com.mobiapp4u.pc.routinebasketadmin.R;

public class ShipperViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView txtShipName,txtShipPhone;
    public Button btnedit,btnremove;
    private ItemClickListner itemClickListner;

    public ShipperViewHolder(View itemView) {
        super(itemView);
        txtShipName = (TextView)itemView.findViewById(R.id.shipper_name);
        txtShipPhone = (TextView)itemView.findViewById(R.id.shipper_phone);
        btnedit = (Button)itemView.findViewById(R.id.btn_edit_ship);
        btnremove = (Button)itemView.findViewById(R.id.btn_remove_ship);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }

    @Override
    public void onClick(View v) {
        itemClickListner.onClick(v,getAdapterPosition(),false);
    }
}
