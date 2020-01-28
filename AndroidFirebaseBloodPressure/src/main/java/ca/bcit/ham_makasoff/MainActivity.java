package ca.bcit.ham_makasoff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editTextUserID;
    EditText editTextSystolic;
    EditText editTextDiastolic;
    Button buttonAddTask;
    ListView lvBloodPressure;
    List<BloodPressure> bloodPressureList;

    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance().getReference("bloodPressure");

        editTextUserID = findViewById(R.id.editTextUserID);
        editTextSystolic = findViewById(R.id.editTextSystolic);
        editTextDiastolic = findViewById(R.id.editTextDiastolic);
        buttonAddTask = findViewById(R.id.buttonAdd);
        lvBloodPressure = findViewById(R.id.lvBloodPressure);
        bloodPressureList = new ArrayList<>();

        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBloodPressure();
            }
        });

        lvBloodPressure.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                BloodPressure bp = bloodPressureList.get(position);

                showUpdateDialog(
                        bp.getId(),
                        bp.getUserID(),
                        bp.getSystolic(),
                        bp.getDiastolic(),
                        bp.getDate(),
                        bp.getTime());

                return false;
            }
        });
    }

    private String getCurrentDate() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }

    private String getCurrentTime() {
        DateFormat df = new SimpleDateFormat("HH:mm");
        String time = df.format(Calendar.getInstance().getTime());
        return time;
    }

    private Date stringToDate(String date) {
        SimpleDateFormat dateFormat= new SimpleDateFormat("dd/MMM/yyyy");
        Date d = new Date();

        try {
            d = dateFormat.parse(date);
            System.out.println("DATE"+d);
            System.out.println("Formatted"+dateFormat.format(d));
        }
        catch(Exception e) {
            //java.text.ParseException: Unparseable date: Geting error
            System.out.println("Exception"+e);
        }

        return d;
    }

    private Date stringToTime(String time) {
        SimpleDateFormat dateFormat= new SimpleDateFormat("HH:mm");
        Date d = new Date();

        try {
            d = dateFormat.parse(time);
            System.out.println("DATE"+d);
            System.out.println("Formatted"+dateFormat.format(d));
        }
        catch(Exception e) {
            //java.text.ParseException: Unparseable date: Geting error
            System.out.println("Exception"+e);
        }

        return d;
    }

    private void addBloodPressure() {

        String userID = editTextUserID.getText().toString().trim();
        String sSystolic = editTextSystolic.getText().toString().trim();
        String sDiastolic = editTextDiastolic.getText().toString().trim();
        String sDate = getCurrentDate();
        Date date = stringToDate(sDate);
        String sTime = getCurrentTime();
        Date time = stringToTime(sTime);

        if (TextUtils.isEmpty(userID)) {
            Toast.makeText(this, "You must enter a user ID.", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(sSystolic)) {
            Toast.makeText(this, "You must enter a systolic pressure.", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(sDiastolic)) {
            Toast.makeText(this, "You must enter a diastolic pressure.", Toast.LENGTH_LONG).show();
            return;
        }

        double systolic = Double.parseDouble(sSystolic);
        double diastolic = Double.parseDouble(sDiastolic);

        String id = db.push().getKey();

        final BloodPressure bp = new BloodPressure(id, userID, systolic, diastolic, date, time);

        com.google.android.gms.tasks.Task setValueTask = db.child(id).setValue(bp);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                if (bp.getCondition().equals(getString(R.string.hcCondition))) {
                    System.out.println("same");
                    alertUser();
                }
                Toast.makeText(MainActivity.this,"Blood pressure added.",Toast.LENGTH_LONG).show();

                editTextUserID.setText("");
                editTextSystolic.setText("");
                editTextDiastolic.setText("");
            }
        });

        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,
                        "something went wrong.\n" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bloodPressureList.clear();
                for (DataSnapshot bpSnapshot : dataSnapshot.getChildren()) {
                    BloodPressure bp = bpSnapshot.getValue(BloodPressure.class);
                    bloodPressureList.add(bp);
                }

                BloodPressureListAdapter adapter = new BloodPressureListAdapter(MainActivity.this, bloodPressureList);
                lvBloodPressure.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void updateBloodPressure(String id, String userName, double systolic, double diastolic, Date date, Date time) {
        DatabaseReference dbRef = db.child(id);

        final BloodPressure bp = new BloodPressure(id, userName, systolic, diastolic, date, time);

        com.google.android.gms.tasks.Task setValueTask = dbRef.setValue(bp);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                if (bp.getCondition().equals(getString(R.string.hcCondition))) {
                    System.out.println("same");
                    alertUser();
                }
                Toast.makeText(MainActivity.this,
                        "Reading Updated.",Toast.LENGTH_LONG).show();
            }
        });

        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,
                        "Something went wrong.\n" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteTask(String id) {
        DatabaseReference dbRef = db.child(id);

        com.google.android.gms.tasks.Task setRemoveTask = dbRef.removeValue();
        setRemoveTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this,
                        "Reading Deleted.",Toast.LENGTH_LONG).show();
            }
        });

        setRemoveTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,
                        "Something went wrong.\n" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void alertUser() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setTitle("Warning!");
        dialogBuilder.setMessage(getString(R.string.warning));

        final AlertDialog alertDialog = dialogBuilder.create();

        alertDialog.show();
    }

    private void showUpdateDialog(final String id, String userID, double systolic, double diastolic, final Date date, final Date time) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText dialogUserID = dialogView.findViewById(R.id.dialogUserID);
        dialogUserID.setText(userID);

        final EditText dialogSystolic = dialogView.findViewById(R.id.dialogSystolic);
        dialogSystolic.setText(Double.toString(systolic));

        final EditText dialogDiastolic = dialogView.findViewById(R.id.dialogDiastolic);
        dialogDiastolic.setText(Double.toString(diastolic));

        final Button btnUpdate = dialogView.findViewById(R.id.btnUpdate);
        final Button btnDelete = dialogView.findViewById(R.id.btnDelete);

        dialogBuilder.setTitle("Update Blood Pressure Reading");

        final AlertDialog alertDialog = dialogBuilder.create();

        alertDialog.show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = dialogUserID.getText().toString().trim();
                String sSystolic = dialogSystolic.getText().toString().trim();
                String sDiastolic = dialogDiastolic.getText().toString().trim();

                if (TextUtils.isEmpty(userID)) {
                    dialogUserID.setError("User ID is required");
                    return;
                } else if (TextUtils.isEmpty(sSystolic)) {
                    dialogSystolic.setError("Systolic pressure is required");
                    return;
                } else if (TextUtils.isEmpty(sDiastolic)) {
                    dialogDiastolic.setError("Diastolic pressure is required");
                    return;
                }

                double systolic = Double.parseDouble(sSystolic);
                double diastolic = Double.parseDouble(sDiastolic);
                updateBloodPressure(id, userID, systolic, diastolic, date, time);

                alertDialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask(id);

                alertDialog.dismiss();
            }
        });

    }
}
