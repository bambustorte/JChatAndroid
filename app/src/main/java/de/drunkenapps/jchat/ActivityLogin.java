package de.drunkenapps.jchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ActivityLogin extends AppCompatActivity {

    Button submit;
    EditText editTextUserEmail;
    EditText editTextPassword;
    EditText editTextUsername;
    TextView switcher;
    ToggleButton toggleButton;

    boolean register;

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        submit = findViewById(R.id.login_bt_submit);
        editTextUserEmail = findViewById(R.id.login_et_useremail);
        editTextPassword = findViewById(R.id.login_et_passwd);
        editTextUsername = findViewById(R.id.login_et_name);
        switcher = findViewById(R.id.login_tv_switcher);
        toggleButton = findViewById(R.id.login_toggle_anon);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        register = false;
    }

    public void switchLoginSignin(View v){
        if (register){
            submit.setText(R.string.login_string_login);
            switcher.setText(R.string.login_string_switcher_tosignup);
            register = false;
        } else {
            submit.setText(R.string.login_string_signup);
            switcher.setText(R.string.login_string_switcher_tologin);
            register = true;
        }
    }

    public void submitPressed(View v){

        if (register){
            registerUser(editTextUserEmail.getText().toString(), editTextPassword.getText().toString());
        } else {
            signInUser(editTextUserEmail.getText().toString(), editTextPassword.getText().toString());
        }
    }

    public void registerUser(String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("login", "createUserWithEmail:success");
                        user = auth.getCurrentUser();
                        register = false;
                        switcher.setText(R.string.login_string_switcher_tosignup);
                        Toast.makeText(ActivityLogin.this.getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.d("test", task.getException().getMessage());
                        Log.w("login", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(ActivityLogin.this, "Authentication failed, " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    void signInUser(String email, String password){
        Task<AuthResult> signin;

        if (toggleButton.isChecked())
            signin = auth.signInAnonymously();
        else
            signin = auth.signInWithEmailAndPassword(email, password);

        signin.addOnCompleteListener(this,
            new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("login", "signInWithEmail:success");

                        //change display name of user
                        String newUserName = editTextUsername.getText().toString();
                        if (newUserName.equals("")){
                            newUserName = "anon";
                        }
                        user = FirebaseAuth.getInstance().getCurrentUser();
                        user.updateProfile(
                                new UserProfileChangeRequest.Builder().setDisplayName(newUserName).build()
                        ).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(ActivityLogin.this.getApplicationContext(), ActivityMain.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("login", "signInWithEmail:failure", task.getException());
                        Toast.makeText(ActivityLogin.this, "Authentication failed, " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        );
    }

    public void anonymousClicked(View v){
        if (toggleButton.isChecked()){
            editTextPassword.setEnabled(false);
            editTextUserEmail.setEnabled(false);
            editTextUsername.setVisibility(View.VISIBLE);
        } else {
            editTextPassword.setEnabled(true);
            editTextUserEmail.setEnabled(true);
            editTextUsername.setVisibility(View.INVISIBLE);
        }
    }

}
