package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CongratulatoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congratulatory);

        TextView congratulatoryTextView = findViewById(R.id.congratulatoryTextView);
        congratulatoryTextView.setText("Congratulations! You've reached your daily water consumption target!");

        Button resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset water count
                SharedPreferences preferencess = getSharedPreferences("WaterCounts", MODE_PRIVATE);
                preferencess.edit().remove("waterCount").apply();

                // Redirect to MainActivity
                Intent intent = new Intent(CongratulatoryActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}
