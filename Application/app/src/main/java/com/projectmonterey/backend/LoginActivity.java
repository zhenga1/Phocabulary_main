package com.projectmonterey.backend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    private EditText uname,password;
    private Button registerButton;
    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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

                Intent intent = new Intent(LoginActivity.this, MenuPage.class);
                startActivity(intent);
        }
    }

    public void registerUser(View view){
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
    public void teemp(View view){
        Intent intent = new Intent(this, ans_choosing.class);
        startActivity(intent);
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
                } catch (Exception error) {
                    error.printStackTrace();
                }
            }

            return null;
        }
    }

}