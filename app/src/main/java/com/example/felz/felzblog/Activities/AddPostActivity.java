package com.example.felz.felzblog.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.felz.felzblog.Model.Blog;
import com.example.felz.felzblog.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {
    private ImageButton mPostImage;
    private EditText mPostTitle;
    private EditText mPostDesc;
    private Button mSubmitButton;
    private StorageReference mStorage;
    private DatabaseReference mPostDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ProgressDialog mProgress;
    public static final int GALLERY_CODE = 1;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mPostDatabase = FirebaseDatabase.getInstance().getReference().child("mBlog");

        mPostImage = findViewById(R.id.imageButton);
        mPostTitle = findViewById(R.id.postTitleEt);
        mPostDesc = findViewById(R.id.postDescriptionEt);
        mSubmitButton = findViewById(R.id.submitPost);
        mStorage = FirebaseStorage.getInstance().getReference();

        mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);

            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Posting to the Database
                StarPosting();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            mImageUri = data.getData();
            mPostImage.setImageURI(mImageUri);
        }
    }

    private void StarPosting(){
        mProgress.setMessage("Posting to Blog");
        mProgress.show();

        final String titleVal = mPostTitle.getText().toString().trim();
        final String descVal = mPostDesc.getText().toString().trim();

        if (titleVal.isEmpty() && descVal.isEmpty() && mImageUri != null){
            Toast.makeText(this, "Empty Text Fields", Toast.LENGTH_SHORT).show();
        }
        else{
//            start the uploading
                StorageReference filePath = mStorage.child("mBlog_image").child(mImageUri.getLastPathSegment());
                filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadurl = taskSnapshot.getDownloadUrl();
                        DatabaseReference newPost = mPostDatabase.push();

                        Map<String, String> dataToSave = new HashMap<>();
                        dataToSave.put("title", titleVal);
                        dataToSave.put("desc", descVal);
                        dataToSave.put("image", downloadurl.toString());
                        dataToSave.put("timestamp", String.valueOf(java.lang.System.currentTimeMillis()));
                        dataToSave.put("userid", mUser.getUid());

                        newPost.setValue(dataToSave);

                        mProgress.dismiss();
                        Toast.makeText(AddPostActivity.this, "New post added", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(AddPostActivity.this, PostListActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        }
    }
}
