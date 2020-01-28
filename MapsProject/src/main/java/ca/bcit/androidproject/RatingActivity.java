package ca.bcit.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RatingActivity extends AppCompatActivity {

    Button saveRating;
    RatingBar ratingBar;
    TextView advTitle;
    DatabaseReference dbRef;
    Adventure adventure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        adventure = (Adventure) getIntent().getSerializableExtra("adventure");
        advTitle = findViewById(R.id.adventure_title_textView);
        advTitle.setText(adventure.getTitle());
        saveRating = findViewById(R.id.save_rating);
        ratingBar = findViewById(R.id.rating_bar);
        dbRef = FirebaseDatabase.getInstance().getReference().child("adventures").child(adventure.getKey());

        saveRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double barRate = (double) ratingBar.getRating();
                double rating = adventure.getRating() * adventure.getReviews() + barRate;
                adventure.setReviews(adventure.getReviews() + 1);
                rating /= adventure.getReviews();
                adventure.setRating(rating);
                dbRef.setValue(adventure);
                startActivity(new Intent(RatingActivity.this, HomeActivity.class));
            }
        });
    }

}
