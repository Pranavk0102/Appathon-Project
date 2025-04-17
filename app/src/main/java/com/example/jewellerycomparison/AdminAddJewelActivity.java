package com.example.jewellerycomparison;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AdminAddJewelActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageViewProduct;
    private EditText editTextProductType, editTextGoldKartage, editTextGoldWeight,
            editTextDiamondWeight, editTextTotalPrice, editTextLabourCharges, editTextDesignCode;
    private Button buttonAddJewel;

    private Uri imageUri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        imageViewProduct = findViewById(R.id.imageViewProduct);
        editTextProductType = findViewById(R.id.editTextProductType);
        editTextGoldKartage = findViewById(R.id.editTextGoldKartage);
        editTextGoldWeight = findViewById(R.id.editTextGoldWeight);
        editTextDiamondWeight = findViewById(R.id.editTextDiamondWeight);
        editTextTotalPrice = findViewById(R.id.editTextTotalPrice);
        editTextLabourCharges = findViewById(R.id.editTextLabourCharges);
        editTextDesignCode = findViewById(R.id.editTextDesignCode);
        buttonAddJewel = findViewById(R.id.buttonAddJewel);

        storageReference = FirebaseStorage.getInstance().getReference("jewel_images");
        databaseReference = FirebaseDatabase.getInstance().getReference("jewels");

        imageViewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        buttonAddJewel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadJewelDetails();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageViewProduct.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadJewelDetails() {
        if (imageUri == null) {
            Toast.makeText(this, "Please upload a product image", Toast.LENGTH_SHORT).show();
            return;
        }

        String productType = editTextProductType.getText().toString().trim();
        String goldKartage = editTextGoldKartage.getText().toString().trim();
        String goldWeight = editTextGoldWeight.getText().toString().trim();
        String diamondWeight = editTextDiamondWeight.getText().toString().trim();
        String totalPrice = editTextTotalPrice.getText().toString().trim();
        String labourCharges = editTextLabourCharges.getText().toString().trim();
        String designCode = editTextDesignCode.getText().toString().trim();

        if (TextUtils.isEmpty(productType) || TextUtils.isEmpty(goldKartage) || TextUtils.isEmpty(goldWeight) ||
                TextUtils.isEmpty(diamondWeight) || TextUtils.isEmpty(totalPrice) || TextUtils.isEmpty(labourCharges) ||
                TextUtils.isEmpty(designCode)) {
            Toast.makeText(this, "Please fill in all the details", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload image to Firebase Storage
        StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

        fileReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                // Get the download URL of the image
                                String imageUrl = downloadUri.toString();

                                // Create a unique ID for the jewel
                                String jewelId = databaseReference.push().getKey();

                                // Create a map to store the jewel details
                                Map<String, Object> jewelData = new HashMap<>();
                                jewelData.put("imageUrl", imageUrl);
                                jewelData.put("productType", productType);
                                jewelData.put("goldKartage", goldKartage);
                                jewelData.put("goldWeight", goldWeight);
                                jewelData.put("diamondWeight", diamondWeight);
                                jewelData.put("totalPrice", totalPrice);
                                jewelData.put("labourCharges", labourCharges);
                                jewelData.put("designCode", designCode);
                                jewelData.put("jewelId", jewelId); // Include the unique ID

// Store the jewel details in Firebase Realtime Database
                                databaseReference.child(jewelId).setValue(jewelData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(AdminAddJewelActivity.this, "Jewel details uploaded successfully", Toast.LENGTH_SHORT).show();
                                                clearFields();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(AdminAddJewelActivity.this, "Failed to upload jewel details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminAddJewelActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getFileExtension(Uri uri) {
        return getContentResolver().getType(uri).split("/")[1];
    }

    private void clearFields() {
        imageViewProduct.setImageResource(R.drawable.ic_upload_image);
        imageUri = null;
        editTextProductType.setText("");
        editTextGoldKartage.setText("");
        editTextGoldWeight.setText("");
        editTextDiamondWeight.setText("");
        editTextTotalPrice.setText("");
        editTextLabourCharges.setText("");
        editTextDesignCode.setText("");
    }
}