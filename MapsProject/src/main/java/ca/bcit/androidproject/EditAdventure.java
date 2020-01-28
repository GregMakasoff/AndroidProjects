package ca.bcit.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditAdventure extends AppCompatActivity {
    Adventure adventure;
    EditText etTitle, etDescription;
    Button btnSave, btnDelete, btnCancel;
    DatabaseReference dbReff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_adventure);

        init();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbReff.child("title").setValue(etTitle.getText().toString());
                dbReff.child("description").setValue(etDescription.getText().toString());
                startActivity(new Intent(EditAdventure.this, ProfileActivity.class));
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbReff.removeValue();
                startActivity(new Intent(EditAdventure.this, ProfileActivity.class));
            }
        });
    }

    private void init() {
        adventure = (Adventure) getIntent().getSerializableExtra("adventure");

        dbReff = FirebaseDatabase.getInstance().getReference().child("adventures").child(adventure.getKey());

        etTitle = (EditText) findViewById(R.id.edit_text_title);
        etDescription = (EditText) findViewById(R.id.edit_text_description);
        btnSave = (Button) findViewById(R.id.button_save_edit);
        btnCancel = (Button) findViewById(R.id.button_cancel_edit);
        btnDelete = (Button) findViewById(R.id.button_delete_edit);

        etTitle.setText(adventure.getTitle());
        etDescription.setText(adventure.getDescription());

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditAdventure.this, HomeActivity.class));
            }
        });
    }
}
