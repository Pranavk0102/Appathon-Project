package com.example.jewellerycomparison;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class ComparisonActivity extends AppCompatActivity {

    private TableLayout comparisonTable;
    private Button logoutButton;
    private DatabaseReference databaseReference;
    private ArrayList<String> selectedJewelIds;
    private ArrayList<MainActivity.Jewel> jewelList = new ArrayList<>();
    private int jewelsFetched = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        comparisonTable = findViewById(R.id.comparisonTable);
        logoutButton = findViewById(R.id.logoutButton);
        databaseReference = FirebaseDatabase.getInstance().getReference("jewels");

        selectedJewelIds = getIntent().getStringArrayListExtra("selectedJewelIds");

        for (String id : selectedJewelIds) {
            databaseReference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    MainActivity.Jewel jewel = snapshot.getValue(MainActivity.Jewel.class);
                    if (jewel != null) {
                        jewelList.add(jewel);
                    }
                    jewelsFetched++;
                    if (jewelsFetched == selectedJewelIds.size()) {
                        displayComparisonTable();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }

        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(ComparisonActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void displayComparisonTable() {
        addRow("Image", true);
        addRow("Type", false);
        addRow("Gold (Kt)", false);
        addRow("Gold (g)", false);
        addRow("Diamond/Gem (g)", false);
        addRow("Labour Price", false);
        addRow("Design Code", false);
    }

    private void addRow(String label, boolean isImageRow) {
        TableRow row = new TableRow(this);
        row.setPadding(8, 16, 8, 16);
        row.setGravity(Gravity.CENTER_VERTICAL);

        TextView labelView = new TextView(this);
        labelView.setText(label);
        labelView.setPadding(16, 8, 16, 8);
        labelView.setTextSize(16);
        labelView.setGravity(Gravity.CENTER);
        row.addView(labelView);

        for (MainActivity.Jewel jewel : jewelList) {
            if (isImageRow) {
                ImageView imageView = new ImageView(this);
                Glide.with(this).load(jewel.getImageUrl()).into(imageView);
                imageView.setLayoutParams(new TableRow.LayoutParams(200, 200));
                row.addView(imageView);
            } else {
                String value = getJewelFieldValue(jewel, label);
                row.addView(createCell(value));
            }
        }

        comparisonTable.addView(row);
    }

    private TextView createCell(String content) {
        TextView cell = new TextView(this);
        cell.setText(content);
        cell.setPadding(16, 8, 16, 8);
        cell.setTextSize(14);
        cell.setGravity(Gravity.CENTER);
        return cell;
    }

    private String getJewelFieldValue(MainActivity.Jewel jewel, String fieldName) {
        switch (fieldName) {
            case "Type":
                return jewel.getProductType();
            case "Gold (Kt)":
                return jewel.getGoldKartage();
            case "Gold (g)":
                return jewel.getGoldWeight();
            case "Diamond/Gem (g)":
                return jewel.getDiamondWeight();
            case "Labour Price":
                return jewel.getLabourCharges();
            case "Design Code":
                return jewel.getDesignCode();
            default:
                return "";
        }
    }
}