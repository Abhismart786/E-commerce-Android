package com.example.e_commerce_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddProducts extends AppCompatActivity {
    private String categoryName;
    private Button addProductButton;
    private ImageView productImage;
    private EditText productNameInput, productDescriptionInput, productPriceInput;
    private static final int GalleryPick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryName = getIntent().getStringExtra("category");

        addProductButton = (Button) findViewById(R.id.addProductButton);
        productImage = (ImageView) findViewById(R.id.productImageContainer);
        productNameInput = (EditText) findViewById(R.id.productNameInput);
        productDescriptionInput = (EditText) findViewById(R.id.productDescriptionInput);
        productPriceInput = (EditText) findViewById(R.id.productPriceInput);
       productImage.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              openGallery();
           }

       });
    }
    private void openGallery() {
        // TODO: Implement gallery selection logic
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }
}