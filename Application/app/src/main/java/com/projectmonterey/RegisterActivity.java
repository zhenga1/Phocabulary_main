package com.projectmonterey;

import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;
import com.projectmonterey.gsonSerializable.APIResponse;
import com.projectmonterey.gsonSerializable.Credentials;
import okhttp3.*;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {
    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private EditText password,confpassword,uname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerpage);
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

                Intent intent = new Intent(this, MenuPage.class);
                startActivity(intent);
            }
        }
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