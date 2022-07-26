package com.projectmonterey.backend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.projectmonterey.ui_main.MenuPage;
import com.projectmonterey.R;
import com.projectmonterey.ui_main.ans_choosing;
import com.projectmonterey.backend.gsonSerializable.APIResponse;
import com.projectmonterey.backend.gsonSerializable.Credentials;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    public FirebaseAuth mAuth;
    public FirebaseUser user = null;
    private EditText uname,password;
    private Button registerButton;
    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        uname = findViewById(R.id.editTextUsername);
        password = findViewById(R.id.editTextPassword);
        registerButton = findViewById(R.id.registerbutton);
    }

    public void check(View view) {
        if ((uname.getText().toString().isEmpty()) || (password.getText().toString().isEmpty())) {
            Toast.makeText(getApplicationContext(),"Please fill in both username and password",Toast.LENGTH_SHORT).show();
        }
        else {
                Gson gson = new Gson();
                String body = gson.toJson(new Credentials(uname.getText().toString(), password.getText().toString()));

                Request post = new Request.Builder()
                        .url("http://192.168.20.229:5000/api/v1/auth/login")
                        .method("POST", RequestBody.create(JSON, body))
                        .header("Content-Type", "application/json")
                        .build();

                new RegisterTask().execute(post);

                loginNewUser();
//                Intent intent = new Intent(LoginActivity.this, MenuPage.class);
//                startActivity(intent);
        }
    }

    private void loginNewUser() {
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
        mAuth.signInWithEmailAndPassword(username+RegisterActivity.enddomain,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(LoginActivity.this.toString(), "signInWithEmail:success");
                            user = mAuth.getCurrentUser();
                            Intent intent = new Intent(LoginActivity.this,MenuPage.class);
                            startActivity(intent);
                        }else{
                            Log.w(LoginActivity.this.toString(), "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void registerUser(View view){
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
    public void teemp(View view){
        Intent intent = new Intent(this, ans_choosing.class);
        startActivity(intent);
    }
    public void backfinish(View view){finish();}
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
                } catch (Exception error) {
                    error.printStackTrace();
                }
            }

            return null;
        }
    }

}