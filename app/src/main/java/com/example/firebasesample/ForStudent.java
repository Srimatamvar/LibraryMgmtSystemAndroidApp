package com.example.firebasesample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class ForStudent extends AppCompatActivity {
    DrawerLayout drawerLayout;
    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore;

    FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_student);
        drawerLayout = findViewById(R.id.drawer_layout);
        recyclerView = findViewById(R.id.recylelist);
        firebaseFirestore = FirebaseFirestore.getInstance();
        TextView t1 = findViewById(R.id.loginUserEmail);
        FirebaseAuth fauth = FirebaseAuth.getInstance();



        FirebaseUser id = fauth.getCurrentUser();
        String email = id.getEmail();
        t1.setText(email);
        final ImageView i1 = findViewById(R.id.logoImage);
        DocumentReference df3 = firebaseFirestore.collection("Users").document(fauth.getCurrentUser().getEmail());
        df3.addSnapshotListener(ForStudent.this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(value!=null){
                        String ul = value.getString("profileImage");
                        Glide.with(ForStudent.this).load(ul).into(i1);
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



        Query query = firebaseFirestore.collection("Books");
        FirestoreRecyclerOptions<StudentBooks> options =  new FirestoreRecyclerOptions.Builder<StudentBooks>().setQuery(query,StudentBooks.class).build();
        adapter = new FirestoreRecyclerAdapter<StudentBooks, StudentBooksViewHolder>(options) {
            @NonNull
            @Override
            public StudentBooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_layout,parent,false);
                return new ForStudent.StudentBooksViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull StudentBooksViewHolder holder, int position, @NonNull StudentBooks model) {
                holder.name.setText(model.getBookname());
                holder.email.setText(model.getAuthor());
                holder.phone.setText(model.getQuantity());
                Glide.with(ForStudent.this).load(model.getBookcover().toString()).into(holder.imageView);
            }
        };
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    class StudentBooksViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView email;
        private TextView phone;
        private ImageView imageView;
        // name = bookName, email= author, phone=quantity;

        public StudentBooksViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.fetchname);
            email = itemView.findViewById(R.id.fetchemail);
            phone = itemView.findViewById(R.id.fetchphone);
            imageView = itemView.findViewById(R.id.bookimage);
        }
    }

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

    public void clickProfilest(View view){
        redirectActivity(this,StudentProfilePage.class);
    }

    static void redirectActivity(Activity activity, Class aclass) {
        Intent intent = new Intent(activity,aclass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

}
