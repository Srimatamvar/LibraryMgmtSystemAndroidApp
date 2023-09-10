package com.example.firebasesample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.OnClickAction;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    final FirebaseAuth fauth = FirebaseAuth.getInstance();
    final FirebaseFirestore fstore = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = findViewById(R.id.textView2);
        Button b = findViewById(R.id.button);
        final EditText name = findViewById(R.id.loginname);
        final EditText pass = findViewById(R.id.loginpass);
        final Intent intent = new Intent(this, Register.class);


        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fauth.signInWithEmailAndPassword(name.getText().toString(),pass.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(MainActivity.this, "User successfully log in", Toast.LENGTH_SHORT).show();
                        checkuser(authResult.getUser().getEmail());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private void checkuser(String uid) {
        System.out.println(uid);
        DocumentReference df = fstore.collection("Users").document(uid);

        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getString("isAdmin") != null){
                    //start admin activity
                    //Toast.makeText(MainActivity.this, "he is admin", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),ForAdmin.class));
                    finish();
                }
                if(documentSnapshot.getString("isUser") != null){
                    //start normal activity
                    //Toast.makeText(MainActivity.this, "normal user", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),ForStudent.class));
                    finish();
                }

            }
        });

    }

    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            System.out.println(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.getString("isAdmin") != null){
                        startActivity(new Intent(getApplicationContext(),ForAdmin.class));
                        finish();

                    }
                    if(documentSnapshot.getString("isUser") != null){
                        //start normal activity
                        startActivity(new Intent(getApplicationContext(),ForStudent.class));
                        finish();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }
            });

        }
    }

}
