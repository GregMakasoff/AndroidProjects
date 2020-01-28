package ca.bcit.androidproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    private final static String TAG = "ProfileActivity";
    Button btnHome;
    TextView tvNameLabel;
    ListView lvAdventureList;
    ArrayAdapter<Adventure> adapter;
    List<Adventure> adventures;
    Context mContext;
    DatabaseReference dbReff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();

        adventures = new ArrayList<Adventure>();
        dbReff = FirebaseDatabase.getInstance().getReference().child("adventures");

        dbReff.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.child("user").getValue().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                    Adventure adventure = dataSnapshot.getValue(Adventure.class);
                    adventure.setKey(dataSnapshot.getKey());
                    adventures.add(adventure);
                    adapter = new ProfileAdventureListAdapter(ProfileActivity.this, R.layout.display_adventure_profile, adventures);
                    lvAdventureList.setAdapter(adapter);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.child("user").getValue().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                    String key = dataSnapshot.getKey();

                    for (int i = 0; i < adventures.size(); i++)
                        if (adventures.get(i).getKey().equals(key)) {
                            adventures.set(i, dataSnapshot.getValue(Adventure.class));
                            break;
                        }

                    adapter = new ProfileAdventureListAdapter(ProfileActivity.this, R.layout.display_adventure_profile, adventures);
                    lvAdventureList.setAdapter(adapter);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("user").getValue().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                    String key = dataSnapshot.getKey();

                    for (int i = 0; i < adventures.size(); i++)
                        if (adventures.get(i).getKey().equals(key)) {
                            adventures.remove(i);
                            break;
                        }

                    adapter = new ProfileAdventureListAdapter(ProfileActivity.this, R.layout.display_adventure_profile, adventures);
                    lvAdventureList.setAdapter(adapter);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(mContext, "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        mContext = this;

        tvNameLabel = (TextView) findViewById(R.id.text_view_name_label_profile);
        lvAdventureList = (ListView) findViewById(R.id.list_view_adventure_list_profile);
        btnHome = (Button) findViewById(R.id.button_home_profile);

        tvNameLabel.setText(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            }
        });
    }
}
