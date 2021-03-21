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
import android.widget.TextView;
import android.widget.Toast;

import com.example.felz.felzblog.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText firstnameAccount, lastnameAccount, emailAccount, passwordAccount;
    private Button createActBtn;
    private TextView already_have;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mFirebaseStorage;
    private ProgressDialog mProgressDailog;
    private ImageButton profilePic;
    private static final int GALLERY_CODE = 1;
    private Uri resultUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        firstnameAccount = findViewById(R.id.firstnameAct);
        lastnameAccount = findViewById(R.id.lastnameAct);
        emailAccount = findViewById(R.id.emailAct);
        passwordAccount = findViewById(R.id.passwordAct);
        createActBtn = findViewById(R.id.createAccountBtn);
        already_have = findViewById(R.id.already_have_Act);
        profilePic = findViewById(R.id.profile_pic);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("MUsers");
        mFirebaseStorage = FirebaseStorage.getInstance().getReference().child("MBlog_Profile_Pics");

        mAuth = FirebaseAuth.getInstance();
        mProgressDailog = new ProgressDialog(this);

        createActBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
            }
        });

    }

    private void createNewAccount() {
        final String name = firstnameAccount.getText().toString().trim();
        final String lname = lastnameAccount.getText().toString().trim();
        String em = emailAccount.getText().toString().trim();
        String pwd = passwordAccount.getText().toString().trim();

        if (name.isEmpty() && lname.isEmpty() && em.isEmpty() && pwd.isEmpty()){
            Toast.makeText(this, "Empty text fields", Toast.LENGTH_SHORT).show();
        }
        else {
            mProgressDailog.setMessage("Creating Account...");
            mProgressDailog.show();

            mAuth.createUserWithEmailAndPassword(em, pwd).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    if (authResult != null){

                        StorageReference imagePath = mFirebaseStorage.child("MBlog_Profile_Pics").child(resultUri.getLastPathSegment());
                        imagePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                String userid = mAuth.getCurrentUser().getUid();

                                DatabaseReference currentUserDb = mDatabaseReference.child(userid);
                                currentUserDb.child("firstname").setValue(name);
                                currentUserDb.child("lastname").setValue(lname);
                                currentUserDb.child("image").setValue(resultUri.toString());

                                mProgressDailog.dismiss();

                                Intent intent = new Intent(CreateAccountActivity.this, PostListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });
                    }

                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            Uri mImageUri = data.getData();

            CropImage.activity(mImageUri)
                    .setAspectRatio(1,1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                 resultUri = result.getUri();

                profilePic.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
