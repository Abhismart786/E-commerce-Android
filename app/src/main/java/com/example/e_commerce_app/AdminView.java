package com.example.e_commerce_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.e_commerce_app.R;
import com.example.e_commerce_app.SignUpActivity;

public class AdminView extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view);

        TextView signupRedirect = findViewById(R.id.signupRedirect);
        signupRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to SignupActivity
                Intent intent = new Intent(AdminView.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}
