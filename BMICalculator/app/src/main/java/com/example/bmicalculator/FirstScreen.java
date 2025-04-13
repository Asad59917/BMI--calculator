package com.example.bmicalculator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FirstScreen extends AppCompatActivity {

    EditText nameInput;
    Button getStartedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);

        nameInput = findViewById(R.id.editTextText);
        getStartedButton = findViewById(R.id.getstartedbutton);

        getStartedButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();

            if (name.isEmpty()) {
                // Show error if name is empty
                Toast.makeText(FirstScreen.this, "Please enter your name", Toast.LENGTH_SHORT).show();
            } else {
                // Name is valid, go to MainActivity
                Intent intent = new Intent(FirstScreen.this, MainActivity.class);
                intent.putExtra("user_name", name);
                startActivity(intent);
            }
        });
    }
}
