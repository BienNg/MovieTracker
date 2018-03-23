package com.example.bien_pc.movielist.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bien_pc.movielist.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {

    // Attributes
    final private String TAG = "SignInActivity";
    private Context context;
    private FirebaseAuth mAuth;

    // Views
    private EditText textEmail, textPassword;
    private Button bttnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        context = this;
        mAuth = FirebaseAuth.getInstance();

        // Init. Views
        textEmail = (EditText) findViewById(R.id.sign_in_email);
        textPassword = (EditText) findViewById(R.id.sign_in_password);
        bttnSignIn = (Button) findViewById(R.id.sign_in_button);

        // Setting up the button
        setupSignInButton();
    }


    /**
     * Sets up the sign in button.
     */
    private void setupSignInButton(){
        Log.d(TAG, "setupSignInButton: reached");
        bttnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Checking if username and password are given
                if(textEmail.getText().toString().equals("") | textPassword.getText().toString().equals("")){
                    Log.d(TAG, "onClick: if reached");
                    Toast.makeText(context, "Please fill in the email and password", Toast.LENGTH_SHORT).show();
                }else{
                    Log.d(TAG, "onClick: else reached");
                    // Call sign in method
                    signIn(textEmail.getText().toString(), textPassword.getText().toString());
                }
            }
        });
    }

    /**
     * Signs in with the given email or password. If email doesnt exist signUp() is called.
     * @param email
     * @param password
     */
    private void signIn(final String email, final String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());

                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                Log.d(TAG, "onComplete: FirebaseAuthWeakPasswordException");
                                Toast.makeText(SignInActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                Log.d(TAG, "onComplete: FirebaseAuthInvalidCredentialsException");
                                Toast.makeText(SignInActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            } catch(FirebaseAuthUserCollisionException e) {
                                Log.d(TAG, "onComplete: FirebaseAuthUserCollisionException");
                                Toast.makeText(SignInActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }catch(FirebaseAuthInvalidUserException e){
                                Log.d(TAG, "onComplete: FirebaseAuthInvalidUserException");
                                // If email doesnt exist a new account is created.
                                signUp(email, password);
                            } catch(Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    }
                });
    }

    private void signUp(final String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            DatabaseReference database = FirebaseDatabase
                                    .getInstance()
                                    .getReference("user")
                                    .child(email.replace(".","(dot)"));
                            database.setValue("n.a.");
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
