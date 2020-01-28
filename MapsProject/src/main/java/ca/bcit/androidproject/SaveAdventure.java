package ca.bcit.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SaveAdventure extends AppCompatActivity {
    Adventure adventure;
    EditText etTitle, etDescription;
    Button btnSave, btnDiscard;
    DatabaseReference dbReff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_adventure);

        init();
    }

    private void init() {
        etTitle = (EditText) findViewById(R.id.edit_text_title_save);
        etDescription = (EditText) findViewById(R.id.edit_text_description_save);
        btnSave = (Button) findViewById(R.id.button_save_save);
        btnDiscard = (Button) findViewById(R.id.button_discard_save);

        adventure = (Adventure) getIntent().getSerializableExtra("adventure");

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAdventure();
            }
        });

        btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SaveAdventure.this, HomeActivity.class));
            }
        });

        dbReff = FirebaseDatabase.getInstance().getReference().child("adventures");
    }

    private void saveAdventure() {
        adventure.setTitle(etTitle.getText().toString());
        adventure.setDescription(etDescription.getText().toString());
        adventure.setRating(0);
        adventure.setReviews(0);
        adventure.setUser(FirebaseAuth.getInstance().getCurrentUser().getUid());

        dbReff.push().setValue(adventure);

        startActivity(new Intent(SaveAdventure.this, HomeActivity.class));
    }
}
