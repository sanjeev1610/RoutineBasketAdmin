package com.mobiapp4u.pc.routinebasketadmin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mobiapp4u.pc.routinebasketadmin.Common.Common;
import com.mobiapp4u.pc.routinebasketadmin.Interface.ItemClickListner;
import com.mobiapp4u.pc.routinebasketadmin.Modal.Food;
import com.mobiapp4u.pc.routinebasketadmin.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class FoodList extends AppCompatActivity {


    FirebaseDatabase firebaseDatabase;
    DatabaseReference food;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    RecyclerView recyclerView_food;
    RecyclerView.LayoutManager layoutManager;

    String categoryId = "";

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    Food newFood;
    Uri saveUri;
    private final int PICK_iMAGE_REQUEST = 71;




    MaterialEditText editFoodName, editFoodPrice, editFoodDesc, editFoodDiscount;
    Button btnSelect, btnUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        firebaseDatabase = FirebaseDatabase.getInstance();
        food = firebaseDatabase.getReference("Foood");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floating_add_food);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAlertDialog();

            }
        });

        recyclerView_food = (RecyclerView)findViewById(R.id.recycler_food_list);
        recyclerView_food.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_food.setLayoutManager(layoutManager);

        if(getIntent() != null)
            categoryId = getIntent().getStringExtra("categoryId");
        if(!categoryId.isEmpty() && categoryId != null) {
            loadFoodList(categoryId);
        }
    }

    private void showAlertDialog(){
        AlertDialog.Builder alrt = new AlertDialog.Builder(this);
        alrt.setTitle("Add New Category...Plz Fill");

        final LayoutInflater inflater = this.getLayoutInflater();
        View add_food_layout = inflater.inflate(R.layout.add_food_list_item_layout, null);

        editFoodName = add_food_layout.findViewById(R.id.edit_food_name);
        editFoodDesc = add_food_layout.findViewById(R.id.edit_food_desc);
        editFoodPrice = add_food_layout.findViewById(R.id.edit_food_price);
        editFoodDiscount = add_food_layout.findViewById(R.id.edit_food_discount);
        btnSelect = add_food_layout.findViewById(R.id.btnSelect);
        btnUpload = add_food_layout.findViewById(R.id.btnUpload);



        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImage();
            }
        });

        alrt.setView(add_food_layout);
        alrt.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        alrt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(newFood!=null){
                    food.push().setValue(newFood);
                    Snackbar.make(recyclerView_food, "New category"+newFood.getName1()+"was added", Snackbar.LENGTH_SHORT ).show();
                }
            }
        });
        alrt.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alrt.show();
    }


    private void loadFoodList(String categoryid){
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class, R.layout.food_list, FoodViewHolder.class,
                food.orderByChild("menuId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.txtFoodName.setText(model.getName1());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView);
                final Food foodItem = model;

                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, Boolean isLongClick) {
//                        Intent foodDetailIntent = new Intent(FoodList.this, FoodDetail.class);
//                        foodDetailIntent.putExtra("foodId", adapter.getRef(position).getKey());
//                        Toast.makeText(FoodList.this, ""+foodItem.getName1(), Toast.LENGTH_LONG).show();
//                        startActivity(foodDetailIntent);
                    }
                });
            }
        };
        recyclerView_food.setAdapter(adapter);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == PICK_iMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            saveUri = data.getData();
            btnSelect.setText("Image Selected !");
        }
    }

    private void UploadImage() {
        if(saveUri != null){
            final ProgressDialog mdialog = new ProgressDialog(this);
            mdialog.setMessage("Uploading...");
            mdialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imagefolder = storageReference.child("images/"+imageName);
            imagefolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mdialog.dismiss();
//                final Uri downloaduri = taskSnapshot.getDownloadUrl();
//                newCategory = new Category(editMenuName.getText().toString(), downloaduri.toString());
//                Toast.makeText(Home.this, "Uploaded", Toast.LENGTH_LONG).show();
//
                    imagefolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            newFood = new Food(editFoodName.getText().toString(), uri.toString(),editFoodDesc.getText().toString(),editFoodPrice.getText().toString(),categoryId.toString(),editFoodDiscount.getText().toString());
                            Toast.makeText(FoodList.this, "Uploaded", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(FoodList.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            Double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mdialog.setMessage("Upladed"+progress+"%..");

                        }
                    });

        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_iMAGE_REQUEST);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle().equals(Common.UPDATE)){
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }else
        if(item.getTitle().equals((Common.DELETE))){
            deleteCategory(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }



    private void showUpdateDialog(final String key, final Food item) {
        AlertDialog.Builder alrt = new AlertDialog.Builder(this);
        alrt.setTitle("Update Food Item...");
       // alrt.setMessage("Please fill Full Information");

        final LayoutInflater inflater = this.getLayoutInflater();
        View add_food_layout = inflater.inflate(R.layout.add_food_list_item_layout, null);

        editFoodName = add_food_layout.findViewById(R.id.edit_food_name);
        editFoodDesc = add_food_layout.findViewById(R.id.edit_food_desc);
        editFoodPrice = add_food_layout.findViewById(R.id.edit_food_price);
        editFoodDiscount = add_food_layout.findViewById(R.id.edit_food_discount);
        btnSelect = add_food_layout.findViewById(R.id.btnSelect);
        btnUpload = add_food_layout.findViewById(R.id.btnUpload);
        editFoodName.setText(item.getName1());
        editFoodDesc.setText(item.getDescription());
        editFoodPrice.setText(item.getPrice());
        editFoodDiscount.setText(item.getDiscount());


        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);
            }
        });

        alrt.setView(add_food_layout);
        alrt.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        alrt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                item.setName1(editFoodName.getText().toString());
                item.setDescription(editFoodDesc.getText().toString());
                item.setPrice(editFoodPrice.getText().toString());
                item.setDiscount(editFoodDiscount.getText().toString());
                item.setMenuId(categoryId.toString());
                food.child(key).setValue(item);
//                if(newCategory!=null){
//                    category.push().setValue(newCategory);
//                    Snackbar.make(drawer, "New category"+newCategory.getName()+"was added", Snackbar.LENGTH_SHORT ).show();
//                }
            }
        });
        alrt.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alrt.show();
    }
    private void changeImage(final Food item) {
        if(saveUri != null){
            final ProgressDialog mdialog = new ProgressDialog(this);
            mdialog.setMessage("Uploading...");
            mdialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imagefolder = storageReference.child("images/"+imageName);
            imagefolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mdialog.dismiss();
//                final Uri downloaduri = taskSnapshot.getDownloadUrl();
//                newCategory = new Category(editMenuName.getText().toString(), downloaduri.toString());
//                Toast.makeText(Home.this, "Uploaded", Toast.LENGTH_LONG).show();
//
                    imagefolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            item.setImage(uri.toString());
                            Toast.makeText(FoodList.this, "Uploaded", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(FoodList.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            Double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mdialog.setMessage("Upladed"+progress+"%..");

                        }
                    });

        }
    }

    private void deleteCategory(String key) {
        food.child(key).removeValue();
        Toast.makeText(FoodList.this, "Item deleted", Toast.LENGTH_LONG).show();
    }

}
