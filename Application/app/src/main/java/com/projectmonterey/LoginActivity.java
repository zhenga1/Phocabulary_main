package com.projectmonterey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;
import com.projectmonterey.gsonSerializable.Registrar;
import okhttp3.*;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private EditText password,confpassword,uname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);
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
                String body = gson.toJson(new Registrar(uname.getText().toString(), password.getText().toString()));

                Request post = new Request.Builder()
                        .url("http://192.168.30.152:5000/api/v1/auth/register")
                        .method("POST", RequestBody.create(JSON, body))
                        .header("Content-Type", "application/json")
                        .build();

                OkHttpClient client = new OkHttpClient();
                try (Response response = client.newCall(post).execute()) {
                    System.out.println("nice");
                }
                catch (IOException error) {
                    error.printStackTrace();
                }

                Intent intent = new Intent(this, MenuPage.class);
                startActivity(intent);
            }
        }

    }
}