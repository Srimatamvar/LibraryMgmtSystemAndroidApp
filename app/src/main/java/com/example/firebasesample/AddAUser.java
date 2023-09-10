package com.example.firebasesample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class AddAUser extends AppCompatActivity {
    EditText name,email,phone,password;
    CheckBox isteacher,isstudent;
    Boolean valid = true;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_a_user);
        final FirebaseAuth fauth = FirebaseAuth.getInstance();
        final FirebaseFirestore fstore = FirebaseFirestore.getInstance();
        name = findViewById(R.id.Name);
        drawerLayout = findViewById(R.id.drawer_layout);
        email = findViewById(R.id.Email);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        isteacher = findViewById(R.id.isTeacher);
        isstudent = findViewById(R.id.isStudent);
        Button btn = findViewById(R.id.Addnewbtn);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        TextView t1 = findViewById(R.id.adminLoginEmail);
        FirebaseUser id = fauth.getCurrentUser();
        String emailss = id.getEmail();
        t1.setText(emailss);

        final ImageView i1 = findViewById(R.id.logoImage);
        DocumentReference df3 = firebaseFirestore.collection("Users").document(auth.getCurrentUser().getEmail());
        df3.addSnapshotListener(AddAUser.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null){
                    String ul = value.getString("profileImage");
                    Glide.with(AddAUser.this).load(ul).into(i1);

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


        isteacher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    isstudent.setChecked(false);
                }
            }
        });

        isstudent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    isteacher.setChecked(false);
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkfield(email);
                checkfield(password);
                checkfield(name);
                checkfield(phone);
                if(!(isteacher.isChecked() || isstudent.isChecked())){
                    Toast.makeText(AddAUser.this, "select account type", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (valid){
                    fauth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = fauth.getCurrentUser();
                            DocumentReference df = fstore.collection("Users").document(user.getEmail());
                            Map<String, Object> userinfo = new HashMap<>();
                            userinfo.put("username",name.getText().toString());
                            userinfo.put("phone",phone.getText().toString());
                            userinfo.put("email",email.getText().toString());
                            userinfo.put("profileImage","https://firebasestorage.googleapis.com/v0/b/sample-94bc9.appspot.com/o/images%2Fdownload.png?alt=media&token=52a0c084-1861-4a8e-af27-ab6734ef7efc");
                            if(isteacher.isChecked()){
                                userinfo.put("isAdmin","1");
                            }
                            if(isstudent.isChecked()){
                                userinfo.put("isUser","1");
                            }
                            df.set(userinfo);
                            Toast.makeText(AddAUser.this, "Successfully added a new user", Toast.LENGTH_SHORT).show();
                            name.setText("");
                            email.setText("");
                            phone.setText("");
                            password.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddAUser.this, "account not created", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });
    }


    public Boolean checkfield(EditText textfield){
        if(textfield.getText().toString().isEmpty()){
            textfield.setError("error");
            Toast.makeText(this, "fill all the entries", Toast.LENGTH_SHORT).show();
            valid = false;
        }else{
            valid = true;
        }
        return valid;
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
        ForAdmin.redirectActivity(this,Dashboard.class);

    }
    public void clickAvailableBooks(View view){
        ForAdmin.redirectActivity(this,AvailableBooks.class);

    }
    public void clickIssuedBooks(View view){

        ForAdmin.redirectActivity(this,IssuedBooks.class);
    }

    public void clickAddUser(View view){
        recreate();
    }

    public void clickLogout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),SplashScreen.class));
    }

    public void clickProfile(View view){
        ForAdmin.redirectActivity(this,ProfilePage.class);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ForAdmin.closeDrawer(drawerLayout);
    }

}
