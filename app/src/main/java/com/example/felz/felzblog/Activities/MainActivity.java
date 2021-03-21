package com.example.felz.felzblog.Activities;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.felz.felzblog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText login_Email, login_pwd;
    private Button login_btn;
    private TextView dont_have;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login_Email = findViewById(R.id.login_email);
        login_pwd = findViewById(R.id.login_password);
        login_btn = findViewById(R.id.login_button);
        dont_have = findViewById(R.id.dont_have_account);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();

                if (mUser != null ){
                    Toast.makeText(MainActivity.this, "Signed In!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, PostListActivity.class));
                    finish();
                }
                else{
                    Toast.makeText(MainActivity.this, "Not Signed In!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = login_Email.getText().toString();
                String pwd = login_pwd.getText().toString();

                if (email.isEmpty()||pwd.isEmpty()){
                    Toast.makeText(MainActivity.this, "Empty fields!", Toast.LENGTH_SHORT).show();
                }
                else{
                    login(email,pwd);
                }
            }
        });

        dont_have.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
                startActivity(intent);
            }
        });



    }

    private void login(String email, String pwd){
        mAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Login Button clicked!!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, PostListActivity.class));
                }
                else{
                    Toast.makeText(MainActivity.this, "Failed Signed In", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_signOut){
            mAuth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
