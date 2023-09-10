package com.example.firebasesample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class ForAdmin extends AppCompatActivity {
    DrawerLayout drawerLayout;
    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;

    FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_admin);
        drawerLayout = findViewById(R.id.drawer_layout);
        recyclerView = findViewById(R.id.recylelist);
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        final Switch my = findViewById(R.id.switch1);
        askSMSPermission();

        final ImageView i1 = findViewById(R.id.logoImage);
        DocumentReference df3 = firebaseFirestore.collection("Users").document(auth.getCurrentUser().getEmail());
        df3.addSnapshotListener(ForAdmin.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null){
                    String ul = value.getString("profileImage");
                    Glide.with(ForAdmin.this).load(ul).into(i1);

                }

            }
        });

        SharedPreferences sharedPreferences
                = getSharedPreferences(
                "sharedPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor
                = sharedPreferences.edit();
        final boolean isDarkModeOn
                = sharedPreferences
                .getBoolean(
                        "isDarkModeOn", false);

        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            my.setChecked(true);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            my.setChecked(false);
        }


        my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDarkModeOn) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean("isDarkModeOn", false);
                    editor.apply();
                    my.setChecked(false);
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean("isDarkModeOn", true);
                    editor.apply();
                    my.setChecked(true);
                }
            }
        });

        TextView t1 = findViewById(R.id.adminLoginEmail);
        FirebaseAuth fauth = FirebaseAuth.getInstance();
        FirebaseUser id = fauth.getCurrentUser();
        String email = id.getEmail();
        t1.setText(email);

        Query query = firebaseFirestore.collection("Books");
        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>().setQuery(query,Product.class).build();
        adapter = new FirestoreRecyclerAdapter<Product, ProductsViewHolder>(options) {
            @NonNull
            @Override
            public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlelayout,parent,false);
                return new ProductsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductsViewHolder holder, int position, @NonNull final Product model) {
                holder.name.setText(model.getBookname());
                holder.email.setText(model.getAuthor());
                holder.phone.setText(model.getQuantity());
                Glide.with(ForAdmin.this).load(model.getBookcover().toString()).into(holder.imageView);
                holder.btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ForAdmin.this);
                        builder.setTitle("Delete book");
                        builder.setMessage("Are you sure you want to delete "+model.getBookname()+ " book");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                DocumentReference db = firebaseFirestore.collection("Books").document(model.getBookname().toString());
                                db.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ForAdmin.this, "Book Successfully deleted", Toast.LENGTH_SHORT).show();
                                        dialogInterface.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ForAdmin.this, "Book not removed", Toast.LENGTH_SHORT).show();
                                        dialogInterface.dismiss();
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });
                holder.updatebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(ForAdmin.this);
                        builder2.setTitle("Update Quantity");
                        builder2.setMessage("Are you sure you want to update quantity");
                        final EditText input = new EditText(ForAdmin.this);
                        input.setInputType(InputType.TYPE_CLASS_PHONE);
                        builder2.setView(input);
                        builder2.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                updateQuantity(input.getText().toString(),model.getBookname());
                                dialogInterface.dismiss();
                            }
                        });
                        builder2.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        AlertDialog alert1 = builder2.create();
                        alert1.show();
                    }
                });
            }
        };
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void updateQuantity(String toString, String bookname) {
        DocumentReference documentReference = firebaseFirestore.collection("Books").document(bookname);
        documentReference.update("quantity", toString).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ForAdmin.this, "Book Successfully Updated", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ForAdmin.this, "Book Not Updated", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void clickMenu(View view){
        openDrawer(drawerLayout);
    }

    static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);

    }
    public void clickLogo(View view){
        closeDrawer(drawerLayout);
    }

    static void closeDrawer(DrawerLayout drawerLayout) {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void clickHome(View view){
        recreate();
    }

    public void clickDashboard(View view){
        redirectActivity(this,Dashboard.class);
    }

    public void clickAvailableBooks(View view){
        redirectActivity(this,AvailableBooks.class);
    }

    public void clickIssuedBooks(View view){
        redirectActivity(this,IssuedBooks.class);
    }

    public void clickProfile(View view){
        ForAdmin.redirectActivity(this,ProfilePage.class);
    }

    public void clickLogout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),SplashScreen.class));
    }

    public void clickAddUser(View view){
        redirectActivity(this, AddAUser.class);
    }


    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

    static void redirectActivity(Activity activity, Class aclass) {
        Intent intent = new Intent(activity,aclass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    class ProductsViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView email;
        private TextView phone;
        private ImageView imageView;
        private Button btn;
        private Button updatebtn;
        // name = bookName, email= author, phone=quantity;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.fetchname);
            email = itemView.findViewById(R.id.fetchemail);
            phone = itemView.findViewById(R.id.fetchphone);
            imageView = itemView.findViewById(R.id.bookimage);
            btn = itemView.findViewById(R.id.removeBtn);
            updatebtn = itemView.findViewById(R.id.updateBtn);
        }
    }

    public void askSMSPermission(){
        if(ContextCompat.checkSelfPermission(ForAdmin.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(ForAdmin.this,Manifest.permission.SEND_SMS)){
                ActivityCompat.requestPermissions(ForAdmin.this, new String[]{Manifest.permission.SEND_SMS},43);
            }else{
                ActivityCompat.requestPermissions(ForAdmin.this,new String[]{Manifest.permission.SEND_SMS},43);
            }
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 43){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Again press the send button to send message", Toast.LENGTH_SHORT).show();
            }else{
                askSMSPermission();
            }
        }
    }

}
