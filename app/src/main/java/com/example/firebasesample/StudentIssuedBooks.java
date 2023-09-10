package com.example.firebasesample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class StudentIssuedBooks extends AppCompatActivity {
    DrawerLayout drawerLayout;
    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore;

    FirestoreRecyclerAdapter adapter;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_issued_books);
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
        df3.addSnapshotListener(StudentIssuedBooks.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null){
                    String ul = value.getString("profileImage");
                    Glide.with(StudentIssuedBooks.this).load(ul).into(i1);

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

        Query query = firebaseFirestore.collection("IssuedBooks").whereEqualTo("studentEmail",email);
        FirestoreRecyclerOptions<IssuedBookList> options =  new FirestoreRecyclerOptions.Builder<IssuedBookList>().setQuery(query,IssuedBookList.class).build();
        adapter = new FirestoreRecyclerAdapter<IssuedBookList, IssuedBookListViewHolder>(options) {
            @NonNull
            @Override
            public IssuedBookListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.issued_book_layout,parent,false);
                return new StudentIssuedBooks.IssuedBookListViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull IssuedBookListViewHolder holder, int position, @NonNull IssuedBookList model) {
                holder.name.setText(model.getBookName());
                holder.author.setText(model.getBookAuthor());
                holder.issueDate.setText(model.getIssuedDate());
                holder.returnDate.setText(model.getReturnDate());
                Glide.with(StudentIssuedBooks.this).load(model.getBookCover().toString()).into(holder.imageView);
            }
        };
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    class IssuedBookListViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView author;
        private TextView returnDate;
        private TextView issueDate;
        private ImageView imageView;

        public IssuedBookListViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.issuedBookName);
            author = itemView.findViewById(R.id.issuedBookAuthorName);
            issueDate = itemView.findViewById(R.id.issuedBookDate);
            returnDate = itemView.findViewById(R.id.issuedBookReturnDate);
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
        redirectActivity(this,ForStudent.class);
    }


    public void clickIssuedBooks(View view){
        recreate();
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
    public void clickProfilest(View view){
        redirectActivity(this,StudentProfilePage.class);
    }

}
