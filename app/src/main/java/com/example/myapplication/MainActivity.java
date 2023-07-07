package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private int waterCount = 0;
    private ImageView cupImageView;
    private TextView waterCountTextView;
    private TextView dateTextView;
    private TextView waterCountValueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cupImageView = findViewById(R.id.cupImageView);
        waterCountTextView = findViewById(R.id.waterCountTextView);
        dateTextView = findViewById(R.id.dateTextView);
        waterCountValueTextView = findViewById(R.id.buttonCustom);

        Button button50ml = findViewById(R.id.button50ml);
        Button button100ml = findViewById(R.id.button100ml);
        Button button150ml = findViewById(R.id.button150ml);
        Button button200ml = findViewById(R.id.button200ml);
        Button button250ml = findViewById(R.id.button250ml);
        Button buttonCustom = findViewById(R.id.buttonCustom);

        button50ml.setOnClickListener(this);
        button100ml.setOnClickListener(this);
        button150ml.setOnClickListener(this);
        button200ml.setOnClickListener(this);
        button250ml.setOnClickListener(this);
        buttonCustom.setOnClickListener(this);

        // Set current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd MMMM", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        dateTextView.setText(currentDate);
    }

    @Override
    public void onClick(View v) {
        int amountToAdd = 0;

        switch (v.getId()) {
            case R.id.button50ml:
                amountToAdd = 50;
                break;
            case R.id.button100ml:
                amountToAdd = 100;
                break;
            case R.id.button150ml:
                amountToAdd = 150;
                break;
            case R.id.button200ml:
                amountToAdd = 200;
                break;
            case R.id.button250ml:
                amountToAdd = 250;
                break;
            case R.id.buttonCustom:
                showCustomInputDialog();
                break;
        }

        if (amountToAdd > 0) {
            waterCount += amountToAdd;
            updateWaterCountTextView();
            Toast.makeText(MainActivity.this, "Added " + amountToAdd + "ml of water", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateWaterCountTextView() {
        waterCountTextView.setText(waterCount + " ml");
    }

    private void showCustomInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Custom Amount");

        // Inflate the dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_custom_input, null);
        builder.setView(dialogView);

        final EditText customAmountEditText = dialogView.findViewById(R.id.editTextCustomAmount);
        customAmountEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String customAmountString = customAmountEditText.getText().toString();
                if (!customAmountString.isEmpty()) {
                    int customAmount = Integer.parseInt(customAmountString);
                    waterCount += customAmount;
                    updateWaterCountTextView();
                    Toast.makeText(MainActivity.this, "Added " + customAmount + "ml of water", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
