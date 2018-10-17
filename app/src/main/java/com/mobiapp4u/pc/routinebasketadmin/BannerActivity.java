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
import com.mobiapp4u.pc.routinebasketadmin.Modal.Banner;
import com.mobiapp4u.pc.routinebasketadmin.ViewHolder.BannerViewHolder;
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

public class BannerActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference banner;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    RecyclerView recyclerView_banner;
    RecyclerView.LayoutManager layoutManager;

    String categoryId = "";

    FirebaseRecyclerAdapter<Banner, BannerViewHolder> adapter;


    Banner newFood;
    Uri saveUri;
    private final int PICK_iMAGE_REQUEST = 71;




    MaterialEditText editFoodName, editFoodId;
    Button btnSelect, btnUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        firebaseDatabase = FirebaseDatabase.getInstance();
        banner = firebaseDatabase.getReference("Banner");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floating_add_banner);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAlertDialog();

            }
        });

        recyclerView_banner = (RecyclerView)findViewById(R.id.recycler_banner);
        recyclerView_banner.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_banner.setLayoutManager(layoutManager);

//        if(getIntent() != null)
//            categoryId = getIntent().getStringExtra("categoryId");
//        if(!categoryId.isEmpty() && categoryId != null) {
//            loadFoodList(categoryId);
//        }
        loadBannerList();
    }

    private void showAlertDialog(){
        AlertDialog.Builder alrt = new AlertDialog.Builder(this);
        alrt.setTitle("Add New Banner");
        alrt.setMessage("Please fill Full Information");

        final LayoutInflater inflater = this.getLayoutInflater();
        View add_food_layout = inflater.inflate(R.layout.banner_update, null);

        editFoodName = add_food_layout.findViewById(R.id.edit_banner_name);
        editFoodId = add_food_layout.findViewById(R.id.edit_banner_foodid);
        btnSelect = add_food_layout.findViewById(R.id.btnSelect_banner);
        btnUpload = add_food_layout.findViewById(R.id.btnUpload_banner);



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
        alrt.setIcon(R.drawable.ic_laptop_black_24dp);
        alrt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(newFood!=null){
                    banner.push().setValue(newFood);
                    Snackbar.make(recyclerView_banner, "New Banner"+newFood.getName()+"was added", Snackbar.LENGTH_SHORT ).show();
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


    private void loadBannerList(){
        adapter = new FirebaseRecyclerAdapter<Banner, BannerViewHolder>(
                Banner.class, R.layout.banner_item, BannerViewHolder.class,
                banner) {
            @Override
            protected void populateViewHolder(final BannerViewHolder viewHolder, Banner model, int position) {
                viewHolder.txtBannerName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.bannerImage);
                final Banner bannerItem = model;

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
        recyclerView_banner.setAdapter(adapter);
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
                            newFood = new Banner(editFoodId.getText().toString(),editFoodName.getText().toString(), uri.toString());
                            Toast.makeText(BannerActivity.this, "Uploaded", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(BannerActivity.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();

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



    private void showUpdateDialog(final String key, final Banner item) {
        AlertDialog.Builder alrt = new AlertDialog.Builder(this);
        alrt.setTitle("Update Food Item");
        alrt.setMessage("Please fill Full Information");

        final LayoutInflater inflater = this.getLayoutInflater();
        View add_food_layout = inflater.inflate(R.layout.banner_update, null);

        editFoodName = add_food_layout.findViewById(R.id.edit_banner_name);
        editFoodId = add_food_layout.findViewById(R.id.edit_banner_foodid);
        btnSelect = add_food_layout.findViewById(R.id.btnSelect_banner);
        btnUpload = add_food_layout.findViewById(R.id.btnUpload_banner);
        editFoodName.setText(item.getName());
        editFoodId.setText(item.getId());


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
                item.setName(editFoodName.getText().toString());
                item.setId(editFoodId.getText().toString());
                banner.child(key).setValue(item);
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
    private void changeImage(final Banner item) {
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
                            Toast.makeText(BannerActivity.this, "Uploaded", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(BannerActivity.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();

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
        banner.child(key).removeValue();
        Toast.makeText(BannerActivity.this, "Item deleted", Toast.LENGTH_LONG).show();
    }

}
