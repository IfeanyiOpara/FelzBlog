package com.example.felz.felzblog.Activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.*;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.felz.felzblog.Data.BlogRecyclerAdapter;
import com.example.felz.felzblog.Model.Blog;
import com.example.felz.felzblog.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PostListActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private RecyclerView recyclerView;
    private BlogRecyclerAdapter blogRecyclerAdapter;
    private List<Blog> blogList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);



        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("mBlog");
        mDatabaseReference.keepSynced(true);

        blogList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_add:
                if(mUser != null && mAuth != null){
                    startActivity(new Intent(PostListActivity.this, AddPostActivity.class));
                    finish();
                }else{
                    Toast.makeText(this, "Please Log In", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.action_signOut:
                if (mUser != null && mAuth != null){
                    mAuth.signOut();
                    startActivity(new Intent(PostListActivity.this, MainActivity.class));
                    finish();

                }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Blog blog = dataSnapshot.getValue(Blog.class);

                blogList.add(blog);

                Collections.reverse(blogList);

                blogRecyclerAdapter = new BlogRecyclerAdapter(PostListActivity.this, blogList);
                recyclerView.setAdapter(blogRecyclerAdapter);
                blogRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
