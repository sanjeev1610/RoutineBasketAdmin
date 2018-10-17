package com.mobiapp4u.pc.routinebasketadmin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mobiapp4u.pc.routinebasketadmin.Common.Common;
import com.mobiapp4u.pc.routinebasketadmin.Interface.ItemClickListner;
import com.mobiapp4u.pc.routinebasketadmin.Modal.Category;
import com.mobiapp4u.pc.routinebasketadmin.Modal.Token;
import com.mobiapp4u.pc.routinebasketadmin.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView textfullName;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference category;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    RecyclerView recyclerView_menu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> firebaseRecyclerAdapter;

    MaterialEditText editMenuName;
    Button btnSelect, btnUpload;

    Category newCategory;
    Uri saveUri;
    private final int PICK_iMAGE_REQUEST = 71;

    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseDatabase = FirebaseDatabase.getInstance();
        category = firebaseDatabase.getReference("Category");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

          showAlertDialog();
               // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
               //         .setAction("Action", null).show();
            }
        });

         drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set nav header name
        View headerView = navigationView.getHeaderView(0);
        textfullName = (TextView)headerView.findViewById(R.id.nav_txtFullname);
        textfullName.setText(Common.currentUser.getName());

        //load menu

        recyclerView_menu = (RecyclerView)findViewById(R.id.recycler_menu);
        recyclerView_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_menu.setLayoutManager(layoutManager);

        loadMenu();
//
//        Intent intentService = new Intent(Home.this, ListenOrder.class);
//        startService(intentService);
    updateToken(FirebaseInstanceId.getInstance().getToken());

    }

    private void updateToken(String tokenref) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");

        Token token = new Token(tokenref,"true");
        tokens.child(Common.currentUser.getPhone()).setValue(token);
    }

    private void showAlertDialog(){
        AlertDialog.Builder alrt = new AlertDialog.Builder(this);
        alrt.setTitle("Add New Category");
        alrt.setMessage("Please fill Full Information");

        final LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_menu_item_layout, null);

        editMenuName = add_menu_layout.findViewById(R.id.edit_menu_name);
        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);

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

        alrt.setView(add_menu_layout);
        alrt.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        alrt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(newCategory!=null){
                    category.push().setValue(newCategory);
                    Snackbar.make(drawer, "New category"+newCategory.getName()+"was added", Snackbar.LENGTH_SHORT ).show();
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
                     newCategory = new Category(editMenuName.getText().toString(), uri.toString(),Common.currentUser.getName());
                        Toast.makeText(Home.this, "Uploaded", Toast.LENGTH_LONG).show();
                    }
                });
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == PICK_iMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
          saveUri = data.getData();
          btnSelect.setText("Image Selected !");
        }
    }

    private void loadMenu(){
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class, R.layout.menu_item, MenuViewHolder.class, category.orderByChild("admin").equalTo(Common.currentUser.getName())) {
            @Override
            protected void populateViewHolder(final MenuViewHolder viewHolder, Category model, int position) {

                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getLink()).into(viewHolder.imageView);
                final Category clickItem = model;

                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, Boolean isLongClick) {
                        Intent foodListIntent = new Intent(Home.this, FoodList.class);
                        foodListIntent.putExtra("categoryId", firebaseRecyclerAdapter.getRef(position).getKey());
                        startActivity(foodListIntent);
                        Toast.makeText(Home.this, ""+clickItem.getName(), Toast.LENGTH_LONG).show();

                    }
                });

            }

        };
        recyclerView_menu.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_menu) {
            // Handle the camera action

        } else if (id == R.id.nav_orders) {
            Intent intent = new Intent(Home.this, OrderStatus.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {
            Intent signIn = new Intent(Home.this, MainActivity.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);

        } else if (id == R.id.nav_banner) {
            Intent intent = new Intent(Home.this, BannerActivity.class);
            startActivity(intent);
        }  else if (id == R.id.nav_message) {
           Intent intent = new Intent(Home.this, SendMessage.class);
           startActivity(intent);
    }else if(id == R.id.nav_shipper){
            Intent intent = new Intent(Home.this, ShipperManagement.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //update, Delete

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle().equals(Common.UPDATE)){
         showUpdateDialog(firebaseRecyclerAdapter.getRef(item.getOrder()).getKey(), firebaseRecyclerAdapter.getItem(item.getOrder()));
        }else
            if(item.getTitle().equals((Common.DELETE))){
              deleteCategory(firebaseRecyclerAdapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteCategory(String key) {

        DatabaseReference db = firebaseDatabase.getReference("Foood");
        Query foodIncategory =  db.orderByChild("menuId").equalTo(key);
        foodIncategory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        category.child(key).removeValue();
        Toast.makeText(Home.this, "Item deleted", Toast.LENGTH_LONG).show();
    }

    private void showUpdateDialog(final String key, final Category item) {
        AlertDialog.Builder alrt = new AlertDialog.Builder(this);
        alrt.setTitle("Update Category");
        alrt.setMessage("Please fill Full Information");

        final LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_menu_item_layout, null);

        editMenuName = add_menu_layout.findViewById(R.id.edit_menu_name);
        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);
        editMenuName.setText(item.getName());
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

        alrt.setView(add_menu_layout);
        alrt.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        alrt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                item.setName(editMenuName.getText().toString());
                category.child(key).setValue(item);
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

    private void changeImage(final Category item) {
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
                            item.setLink(uri.toString());
                            Toast.makeText(Home.this, "Uploaded", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();

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

}
