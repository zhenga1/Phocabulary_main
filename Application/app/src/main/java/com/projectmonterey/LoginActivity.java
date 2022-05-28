package com.projectmonterey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private EditText password,confpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);
        password = findViewById(R.id.editTextTextPassword2);
        confpassword=findViewById(R.id.editTextTextPassword3);

    }
    public void check(View view){
        if(!(password.getText().toString().equals(confpassword.getText().toString()))){
            Toast.makeText(getApplicationContext(),"Password and Confirmation Password do not match",Toast.LENGTH_SHORT).show();
        }
        else{
            Intent intent = new Intent(this, CameraActivity.class);
            startActivity(intent);
        }
    }
}