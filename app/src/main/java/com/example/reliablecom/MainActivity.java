package com.example.reliablecom;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reliablecom.main.Main;

public class MainActivity extends AppCompatActivity {

    public static TextView consoleArea;
    public static ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI components
        Button btnReceive = findViewById(R.id.btn_receive);
        consoleArea = findViewById(R.id.console_area);
        imageView = findViewById(R.id.image_view); // ImageView 초기화

        // Button click listeners
        btnReceive.setOnClickListener(v -> {
            logToConsole("Starting to receive data...");
            Thread main = new Thread(new Main());
            main.start();
        });
    }

    private void logToConsole(String message) {
        consoleArea.append("\n" + message);
    }
}
