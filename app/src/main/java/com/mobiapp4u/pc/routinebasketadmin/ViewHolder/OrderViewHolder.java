package com.mobiapp4u.pc.routinebasketadmin.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mobiapp4u.pc.routinebasketadmin.Interface.ItemClickListner;
import com.mobiapp4u.pc.routinebasketadmin.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress,txtOrderDate;
    public Button btnUpdate,btnDelete,btnDetails;

    private ItemClickListner itemClickListner;

    public OrderViewHolder(View itemView) {
        super(itemView);
        txtOrderId = (TextView)itemView.findViewById(R.id.order_id);
        txtOrderStatus = (TextView)itemView.findViewById(R.id.order_status);
        txtOrderPhone = (TextView)itemView.findViewById(R.id.order_phone);
        txtOrderAddress = (TextView)itemView.findViewById(R.id.order_address);
        txtOrderDate = (TextView)itemView.findViewById(R.id.order_date);
        btnDelete = (Button)itemView.findViewById(R.id.btn_delete_os);
        btnUpdate = (Button)itemView.findViewById(R.id.btn_update_os);
        btnDetails = (Button)itemView.findViewById(R.id.btn_details_os);


        itemView.setOnClickListener(this);
      //  itemView.setOnCreateContextMenuListener(this);
        //itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListner.onClick(v, getAdapterPosition(), false);
    }

    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }


//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        menu.add(0,1,getAdapterPosition(), Common.UPDATE);
//        menu.add(0,1,getAdapterPosition(),Common.DELETE);
//        menu.add(0,1,getAdapterPosition(),Common.DETAILS);
//    }

 //   @Override
  //  public boolean onLongClick(View v) {
   //     itemClickListner.onClick(v, getAdapterPosition(), true);
   //     return true;
 //   }
}
