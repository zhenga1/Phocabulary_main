package com.projectmonterey;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UiMainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedBundleState) {
        super.onCreate(savedBundleState);
        setContentView(R.layout.activity_ui_main);

        FirebaseAuth authInstance = FirebaseAuth.getInstance();

        FirebaseUser user = authInstance.getCurrentUser();

        // user cannot be null as only logged-in users can reach this page.
        assert user != null;
        TextView username = findViewById(R.id.username);
        username.setText(user.getDisplayName());
    }
}