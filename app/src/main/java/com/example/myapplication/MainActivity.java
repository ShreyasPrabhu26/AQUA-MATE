package com.example.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Handler myhandler= new Handler();
    private static final String CHANNEL_ID = "my_channel_id";
    private static final int NOTIFICATION_ID = 1;
    private int waterCount = 0;
    private int targetWaterCount = 3000; // Target water count in milliliters
    private ImageView cupImageView;
    private TextView waterCountTextView;
    private TextView remainingWaterTextView;

    // Notification channel constants
    //private static final String CHANNEL_ID = "WaterReminderChannel";
    //private static final String CHANNEL_NAME = "Water Reminder";
    //private static final String CHANNEL_DESCRIPTION = "Channel for water reminder notifications";

    private Button notificationButton;
    public int notificationTimePeriod = 60; // Default notification time period in minutes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cupImageView = findViewById(R.id.cupImageView);
        waterCountTextView = findViewById(R.id.waterCountTextView);
        remainingWaterTextView = findViewById(R.id.remainingWaterTextView);
        notificationButton = findViewById(R.id.notificationButton);

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
        notificationButton.setOnClickListener(this);

        // Set current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd MMMM", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        TextView dateTextView = findViewById(R.id.dateTextView);
        dateTextView.setText(currentDate);

        // Retrieve water count for the current day
        SharedPreferences preferences = getSharedPreferences("WaterCounts", MODE_PRIVATE);
        waterCount = preferences.getInt("waterCount", 0);
        updateWaterCountTextView();

        // Calculate remaining water level
        int remainingWaterLevel = targetWaterCount - waterCount;
        updateRemainingWaterTextView(remainingWaterLevel);

        // Check if water count reaches 0
        if (waterCount >= targetWaterCount) {
            createNotificationChannel();
            congratulateUser();
        }

        myhandler.postDelayed(ThreadCount,(notificationTimePeriod*1000));


        // Create the notification channel
        createNotificationChannel();
    }
    public Runnable ThreadCount=new Runnable() {
        @Override
        public void run() {
            createNotification();
            myhandler.postDelayed(ThreadCount,(notificationTimePeriod*1000));
        }
    };

    @Override
    public void onClick(View v) {
        int amountToAdd = 0;

        switch (v.getId()) {
            case R.id.button50ml:
                amountToAdd = 50;
                break;
            case R.id.button100ml:
                amountToAdd = 100;
                createNotification();

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
            case R.id.notificationButton:
                showNotificationTimeInputDialog();
                break;
        }

        if (amountToAdd > 0) {
            waterCount += amountToAdd;
            updateWaterCountTextView();
            Toast.makeText(MainActivity.this, "Added " + amountToAdd + "ml of water", Toast.LENGTH_SHORT).show();

            // Store the water count in SharedPreferences
            SharedPreferences preferences = getSharedPreferences("WaterCounts", MODE_PRIVATE);
            preferences.edit().putInt("waterCount", waterCount).apply();

            // Calculate remaining water level
            int remainingWaterLevel = targetWaterCount - waterCount;
            updateRemainingWaterTextView(remainingWaterLevel);

            // Check if water count reaches 0
            if (waterCount >= targetWaterCount) {
                congratulateUser();
            }
        }
    }

    private void updateWaterCountTextView() {
        waterCountTextView.setText(waterCount + " ml");
    }

    private void updateRemainingWaterTextView(int remainingWaterLevel) {
        remainingWaterTextView.setText(remainingWaterLevel + " ml remaining");
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

                    // Store the water count in SharedPreferences
                    SharedPreferences preferences = getSharedPreferences("WaterCounts", MODE_PRIVATE);
                    preferences.edit().putInt("waterCount", waterCount).apply();

                    // Calculate remaining water level
                    int remainingWaterLevel = targetWaterCount - waterCount;
                    updateRemainingWaterTextView(remainingWaterLevel);

                    // Check if water count reaches 0
                    if (waterCount >= targetWaterCount) {
                        congratulateUser();
                    }
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

    private void showNotificationTimeInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notification Time Period (minutes)");

        // Inflate the dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_notification_time_input, null);
        builder.setView(dialogView);

        final EditText customTimeEditText = dialogView.findViewById(R.id.editTextCustomTime);
        customTimeEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String customTimeString = customTimeEditText.getText().toString();
                if (!customTimeString.isEmpty()) {
                    notificationTimePeriod = Integer.parseInt(customTimeString);
                    Toast.makeText(MainActivity.this, "Notification time period set to " + notificationTimePeriod + " seconds", Toast.LENGTH_SHORT).show();
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

    private void congratulateUser() {
        Intent intent = new Intent(MainActivity.this, CongratulatoryActivity.class);
        startActivity(intent);
        finish(); // Finish the current activity to prevent going back to it from the congratulatory activity
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Channel";
            String description = "My Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.cup_image)
                .setContentTitle("Aqua-Mate")
                .setContentText("Drink Water.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }




}
