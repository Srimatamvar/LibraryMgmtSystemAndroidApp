package com.example.firebasesample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Dashboard extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ImageView imageView;
    Button add;
    final FirebaseAuth fauth = FirebaseAuth.getInstance();
    EditText bookname,authorname,quantity;
    DocumentReference df;
    FirebaseFirestore fstore;
    public Uri imageuri;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        drawerLayout = findViewById(R.id.drawer_layout);
        imageView = findViewById(R.id.bookcover);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        bookname = findViewById(R.id.bookname);
        fstore = FirebaseFirestore.getInstance();
        authorname = findViewById(R.id.author);
        quantity = findViewById(R.id.quantity);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        TextView t1 = findViewById(R.id.adminLoginEmail);
        FirebaseAuth fauth = FirebaseAuth.getInstance();
        FirebaseUser id = fauth.getCurrentUser();
        String email = id.getEmail();
        t1.setText(email);

        final ImageView i1 = findViewById(R.id.logoImage);
        DocumentReference df3 = firebaseFirestore.collection("Users").document(fauth.getCurrentUser().getEmail());
        df3.addSnapshotListener(Dashboard.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null){
                    String ul = value.getString("profileImage");
                    Glide.with(Dashboard.this).load(ul).into(i1);

                }
            }
        });

        final Switch my = findViewById(R.id.switch1);

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


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("starting");
                choosePicture();
            }
        });

    }

    private void geturl(final String random) {
        StorageReference ref = storageReference.child("images/"+ random);
        add = findViewById(R.id.addbtn);
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //This is the url of image to show in the layout
                final String url = uri.toString();
                System.out.println(bookname.getText().toString() + authorname.getText().toString() + quantity.getText().toString());
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = bookname.getText().toString();
                        String author = authorname.getText().toString();
                        String quant = quantity.getText().toString();
                        df = fstore.collection("Books").document(name);
                        Map<String, Object> bookinfo = new HashMap<>();
                        bookinfo.put("bookname",name);
                        bookinfo.put("author",author);
                        bookinfo.put("quantity",quant);
                        bookinfo.put("bookcover",url);
                        df.set(bookinfo);
                        bookname.setText("");
                        authorname.setText("");
                        quantity.setText("");
                        Toast.makeText(Dashboard.this, "New Book added to list", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Dashboard.this, "Failed to add a book", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data != null && data.getData() != null){
            imageuri = data.getData();
            imageView.setImageURI(imageuri);
            uploadPicture();
        }
    }

    private void uploadPicture() {
        final String Random = UUID.randomUUID().toString();
        final ProgressDialog pd = new ProgressDialog(Dashboard.this);
        pd.setTitle("uploading....");
        pd.show();
        System.out.println("1");
        StorageReference ref = storageReference.child("images/" + Random);
        System.out.println("good");
        ref.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Toast.makeText(Dashboard.this, "image uploaded", Toast.LENGTH_SHORT).show();
                geturl(Random);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(Dashboard.this, "image not uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercentage = (100.00 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                System.out.println("2");
                pd.setMessage("Percentage: "+ (int)progressPercentage + "%");
            }
        });



    }

    public void clickMenu(View view){
        ForAdmin.openDrawer(drawerLayout);
    }

    public void clickLogo(View view){
        ForAdmin.closeDrawer(drawerLayout);
    }
    public void clickHome(View view){
        ForAdmin.redirectActivity(this,ForAdmin.class);
    }
    public void clickDashboard(View view){
        recreate();
    }
    public void clickAvailableBooks(View view){
        ForAdmin.redirectActivity(this,AvailableBooks.class);
    }

    public void clickProfile(View view){
        ForAdmin.redirectActivity(this,ProfilePage.class);
    }
    public void clickIssuedBooks(View view){
        ForAdmin.redirectActivity(this,IssuedBooks.class);
    }
    public void clickLogout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),SplashScreen.class));
    }

    public void clickAddUser(View view){
        ForAdmin.redirectActivity(this, AddAUser.class);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ForAdmin.closeDrawer(drawerLayout);
    }
}
