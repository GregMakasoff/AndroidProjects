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

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    private final static String TAG = "HomeActivity";
    TextView tvNameLabel;
    ListView lvAdventureList;
    Button btnProfile, btnNewAdventure, btnSignOut;
    ArrayAdapter<Adventure> adapter;
    List<Adventure> adventures;
    Context mContext;
    DatabaseReference dbReff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();

        adventures = new ArrayList<Adventure>();
        dbReff = FirebaseDatabase.getInstance().getReference().child("adventures");

        dbReff.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Adventure adventure = dataSnapshot.getValue(Adventure.class);
                adventure.setKey(dataSnapshot.getKey());
                adventures.add(adventure);
                adapter = new AdventureListAdapter(HomeActivity.this, R.layout.display_adventure, adventures);
                lvAdventureList.setAdapter(adapter);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey();

                for (int i = 0; i < adventures.size(); i++)
                    if (adventures.get(i).getKey().equals(key)) {
                        adventures.set(i, dataSnapshot.getValue(Adventure.class));
                        break;
                    }

                adapter = new AdventureListAdapter(HomeActivity.this, R.layout.display_adventure, adventures);
                lvAdventureList.setAdapter(adapter);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();

                for (int i = 0; i < adventures.size(); i++)
                    if (adventures.get(i).getKey().equals(key)) {
                        adventures.remove(i);
                        break;
                    }

                adapter = new AdventureListAdapter(HomeActivity.this, R.layout.display_adventure, adventures);
                lvAdventureList.setAdapter(adapter);
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

        tvNameLabel = (TextView) findViewById(R.id.text_view_name_label);
        lvAdventureList = (ListView) findViewById(R.id.list_view_adventure_list);
        btnProfile = (Button) findViewById(R.id.button_profile);
        btnNewAdventure = (Button) findViewById(R.id.button_new_adventure);
        btnSignOut = (Button) findViewById(R.id.button_sign_out);

        tvNameLabel.setText(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            }
        });

        btnNewAdventure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchMaps();
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(HomeActivity.this, MainActivity.class));
                    }
                });
    }

    private void launchMaps() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

}
