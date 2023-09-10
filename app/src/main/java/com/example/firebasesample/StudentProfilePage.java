package com.example.firebasesample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.UUID;

public class StudentProfilePage extends AppCompatActivity {
    DrawerLayout drawerLayout;
    FirebaseFirestore fstore;
    ImageView imageView;
    TextView em;
    EditText phone,name;
    String u;
    public Uri imageuri;
    private FirebaseAuth auth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile_page);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        drawerLayout = findViewById(R.id.drawer_layout);
        Button upd = findViewById(R.id.updatePassword);

        imageView = findViewById(R.id.userImage);

        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();


        phone = findViewById(R.id.phone);
        name = findViewById(R.id.name);
        em = findViewById(R.id.email);


        TextView t1 = findViewById(R.id.loginUserEmail);
        t1.setText(auth.getCurrentUser().getEmail());

        final ImageView i1 = findViewById(R.id.logoImage);
        DocumentReference df3 = fstore.collection("Users").document(auth.getCurrentUser().getEmail());
        df3.addSnapshotListener(StudentProfilePage.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null){
                    String ul = value.getString("profileImage");
                    Glide.with(StudentProfilePage.this).load(ul).into(i1);

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

        upd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(StudentProfilePage.this);
                builder2.setTitle("Update Password");
                builder2.setMessage("Are you sure you want to update your Password");
                final EditText input = new EditText(StudentProfilePage.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder2.setView(input);
                builder2.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        auth.getCurrentUser().updatePassword(input.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(StudentProfilePage.this, "Password updated Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println(e);
                                Toast.makeText(StudentProfilePage.this, "Password not updated", Toast.LENGTH_SHORT).show();
                            }
                        });
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


        DocumentReference df1 = fstore.collection("Users").document(auth.getCurrentUser().getEmail());
        df1.addSnapshotListener(StudentProfilePage.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null){
                    String setemail = value.getString("email");
                    String n = value.getString("username");
                    String p = value.getString("phone");
                    u = value.getString("profileImage");

                    phone.setText(p);
                    name.setText(n);
                    em.setText(setemail);
                    Glide.with(StudentProfilePage.this).load(u).into(imageView);

                }

            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });

        Button save2 = findViewById(R.id.saveChanges);
        save2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference df = fstore.collection("Users").document(auth.getCurrentUser().getEmail());
                Map<String, Object> userinfo = new HashMap<>();
                userinfo.put("username",name.getText().toString());
                userinfo.put("phone",phone.getText().toString());
                userinfo.put("profileImage",u);
                df.update(userinfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(StudentProfilePage.this, "changes saved", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StudentProfilePage.this, "Failed to change", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void geturl(final String random) {
        StorageReference ref = storageReference.child("images/"+ random);
        final Button save1 = findViewById(R.id.saveChanges);
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //This is the url of image to show in the layout
                final String url = uri.toString();
                save1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String getEmail = auth.getCurrentUser().getEmail();
                        DocumentReference df = fstore.collection("Users").document(getEmail);
                        Map<String, Object> userinfo = new HashMap<>();
                        userinfo.put("username",name.getText().toString());
                        userinfo.put("phone",phone.getText().toString());
                        userinfo.put("profileImage",url);
                        df.update(userinfo);
                        Toast.makeText(StudentProfilePage.this, "Changes successfully saved", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(StudentProfilePage.this, "Failed to saved", Toast.LENGTH_SHORT).show();
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
        final ProgressDialog pd = new ProgressDialog(StudentProfilePage.this);
        pd.setTitle("uploading....");
        pd.show();
        System.out.println("1");
        StorageReference ref = storageReference.child("images/" + Random);
        System.out.println("good");
        ref.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Toast.makeText(StudentProfilePage.this, "image uploaded", Toast.LENGTH_SHORT).show();
                geturl(Random);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(StudentProfilePage.this, "image not uploaded", Toast.LENGTH_SHORT).show();
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
        redirectActivity(this,ForStudent.class);
    }


    public void clickIssuedBooks(View view){
        redirectActivity(this,StudentIssuedBooks.class);
    }

    public void clickLogout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),SplashScreen.class));
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
    public void clickProfile(View view){
        recreate();
    }
}
