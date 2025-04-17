package com.example.jewellerycomparison;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout jewelsContainer; // Layout to hold the displayed jewels
    private Button compareButton;
    private Button logoutButton;
    private List<Jewel> jewelList = new ArrayList<>();
    private int selectedItemCount = 0;
    private List<String> selectedJewelIds = new ArrayList<>();
    private DatabaseReference jewelsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jewelsContainer = findViewById(R.id.jewelsContainer); // Make sure you have this ID in your updated activity_main.xml
        compareButton = findViewById(R.id.compareButton);
        logoutButton = findViewById(R.id.logoutButton);

        // Initialize Firebase Realtime Database reference to the "jewels" node
        jewelsRef = FirebaseDatabase.getInstance().getReference("jewels");

        // Fetch and display the jewel data
        fetchJewels();

        compareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedItemCount >= 2 && selectedItemCount <= 4) {
                    Intent intent = new Intent(MainActivity.this, ComparisonActivity.class);
                    intent.putStringArrayListExtra("selectedJewelIds", (ArrayList<String>) selectedJewelIds);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Please select 2 to 4 items for comparison", Toast.LENGTH_SHORT).show();
                }
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void fetchJewels() {
        jewelsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                jewelList.clear();
                jewelsContainer.removeAllViews(); // Clear previous views

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Jewel jewel = snapshot.getValue(Jewel.class);
                    if (jewel != null) {
                        jewel.setJewelId(snapshot.getKey()); // Set the unique ID
                        jewelList.add(jewel);
                        displayJewel(jewel);
                    }
                }

                if (jewelList.isEmpty()) {
                    TextView noDataTextView = new TextView(MainActivity.this);
                    noDataTextView.setText("No jewels added yet.");
                    jewelsContainer.addView(noDataTextView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", "Failed to fetch jewels: " + databaseError.getMessage());
                Toast.makeText(MainActivity.this, "Failed to load jewels.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayJewel(final Jewel jewel) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View jewelItemView = inflater.inflate(R.layout.item_jewel_main, jewelsContainer, false);

        ImageView jewelImageView = jewelItemView.findViewById(R.id.jewelImageView);
        TextView productTypeTextView = jewelItemView.findViewById(R.id.productTypeTextView);
        TextView goldKartageTextView = jewelItemView.findViewById(R.id.goldKartageTextView);
        TextView goldWeightTextView = jewelItemView.findViewById(R.id.goldWeightTextView);
        Button selectButton = jewelItemView.findViewById(R.id.selectButton);

        productTypeTextView.setText("Type: " + jewel.getProductType());
        goldKartageTextView.setText("Gold (Kt): " + jewel.getGoldKartage());
        goldWeightTextView.setText("Weight (g): " + jewel.getGoldWeight());

        // Load image using Glide library
        Glide.with(this)
                .load(jewel.getImageUrl())
                .placeholder(R.drawable.ph_image) // Placeholder image
                .error(R.drawable.error) // Error image if loading fails
                .into(jewelImageView);

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectedJewelIds.contains(jewel.getJewelId())) {
                    if (selectedItemCount < 4) {
                        selectedItemCount++;
                        selectedJewelIds.add(jewel.getJewelId());
                        selectButton.setText("Selected");
                        selectButton.setEnabled(false);
                        Toast.makeText(MainActivity.this, "Selected: " + jewel.getProductType(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "You can select only up to 4 items", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        jewelsContainer.addView(jewelItemView);
    }

    // Data model class for Jewel
    public static class Jewel {
        private String jewelId;
        private String imageUrl;
        private String productType;
        private String goldKartage;
        private String goldWeight;
        private String diamondWeight;
        private String totalPrice;
        private String labourCharges;
        private String designCode;

        public Jewel() {
            // Default constructor required for Firebase
        }

        public Jewel(String imageUrl, String productType, String goldKartage, String goldWeight, String diamondWeight, String totalPrice, String labourCharges, String designCode) {
            this.imageUrl = imageUrl;
            this.productType = productType;
            this.goldKartage = goldKartage;
            this.goldWeight = goldWeight;
            this.diamondWeight = diamondWeight;
            this.totalPrice = totalPrice;
            this.labourCharges = labourCharges;
            this.designCode = designCode;
        }

        public String getJewelId() {
            return jewelId;
        }

        public void setJewelId(String jewelId) {
            this.jewelId = jewelId;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getProductType() {
            return productType;
        }

        public String getGoldKartage() {
            return goldKartage;
        }

        public String getGoldWeight() {
            return goldWeight;
        }

        public String getDiamondWeight() {
            return diamondWeight;
        }

        public String getTotalPrice() {
            return totalPrice;
        }

        public String getLabourCharges() {
            return labourCharges;
        }

        public String getDesignCode() {
            return designCode;
        }

        // Setters (if needed)
        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public void setProductType(String productType) {
            this.productType = productType;
        }

        public void setGoldKartage(String goldKartage) {
            this.goldKartage = goldKartage;
        }

        public void setGoldWeight(String goldWeight) {
            this.goldWeight = goldWeight;
        }

        public void setDiamondWeight(String diamondWeight) {
            this.diamondWeight = diamondWeight;
        }

        public void setTotalPrice(String totalPrice) {
            this.totalPrice = totalPrice;
        }

        public void setLabourCharges(String labourCharges) {
            this.labourCharges = labourCharges;
        }

        public void setDesignCode(String designCode) {
            this.designCode = designCode;
        }
    }
}