package com.projectmonterey.backend;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.projectmonterey.MainActivity;
import com.projectmonterey.ui_main.MenuPage;
import com.projectmonterey.R;
import com.projectmonterey.backend.gsonSerializable.APIResponse;
import com.projectmonterey.backend.gsonSerializable.Credentials;
import okhttp3.*;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {
    public static String enddomain = "@phocabularyuser.com";
    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public FirebaseAuth mAuth;
    private EditText password,confpassword,uname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerpage);
        mAuth = FirebaseAuth.getInstance();
        password = findViewById(R.id.editTextTextPassword2);
        confpassword=findViewById(R.id.editTextTextPassword3);
        uname=findViewById(R.id.editTextTextPersonName2);

    }
    public void check(View view) throws IOException {
        if ((uname.getText().toString().isEmpty()) || (password.getText().toString().isEmpty())) {
            Toast.makeText(getApplicationContext(),"Please fill in both username and password",Toast.LENGTH_SHORT).show();
        }
        else {
            if(!(password.getText().toString().equals(confpassword.getText().toString()))){
                Toast.makeText(getApplicationContext(),"Password and Confirmation Password do not match",Toast.LENGTH_SHORT).show();
            }
            else{
                Gson gson = new Gson();
                String body = gson.toJson(new Credentials(uname.getText().toString(), password.getText().toString()));

                Request post = new Request.Builder()
                        .url("http://192.168.20.229:5000/api/v1/auth/register")
                        .method("POST", RequestBody.create(JSON, body))
                        .header("Content-Type", "application/json")
                        .build();

                new RegisterTask().execute(post);

                registerNewTask();
//                Intent intent = new Intent(this, MenuPage.class);
//                startActivity(intent);
            }
        }
    }
    public void registerNewTask() {
        String username, pass;
        username = uname.getText().toString();
        pass = password.getText().toString();

        // Validations for input email and password
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter email!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter password!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(username + enddomain, pass)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                            "Registration successful!",
                                            Toast.LENGTH_LONG)
                                    .show();
                            // hide the progress bar
                            // if the user created intent to login activity
                            Intent intent
                                    = new Intent(RegisterActivity.this,
                                    LoginActivity.class);
                            startActivity(intent);
                        }
                        else {
                            // Registration failed
                            Toast.makeText(
                                            getApplicationContext(),
                                            "Registration failed!!"
                                                    + " Please try again later",
                                            Toast.LENGTH_LONG)
                                    .show();
                            // hide the progress bar
                        }
                    }
                });
    }
    private static class RegisterTask extends AsyncTask<Request, Void, Void> {
        @Override
        protected Void doInBackground(Request... requests) {
            for (Request request : requests) {
                OkHttpClient client = new OkHttpClient();
                try (Response response = client.newCall(request).execute()) {
                    assert response.body() != null;
                    String json = response.body().string();
                    APIResponse apiResponse = new Gson().fromJson(json, APIResponse.class);
                    System.out.println(apiResponse.code);
                }
                catch (Exception error) {
                    error.printStackTrace();
                }
            }

            return null;
        }
    }
}