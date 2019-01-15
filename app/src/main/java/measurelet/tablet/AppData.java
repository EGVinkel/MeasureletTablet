package measurelet.tablet;

import android.app.Application;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import measurelet.tablet.Model.Patient;

public class AppData extends Application {


    private FirebaseDatabase DB_INSTANCE;
    public static DatabaseReference DB_REFERENCE;
    //public static List<String> patientID= new ArrayList<>();
    public static List<Patient> patientlist = new ArrayList<>();
    public static DatabaseReference patientRef;
    public static DatabaseReference intakeRef;

    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
                createAppDatabase();
    }

    public DatabaseReference createAppDatabase() {


        if (DB_INSTANCE == null) {
            DB_INSTANCE = FirebaseDatabase.getInstance();
            DB_INSTANCE.setPersistenceEnabled(true);
            Log.d("Instance", "Instance created!");
        }
        if (DB_REFERENCE == null) {
            DB_REFERENCE = DB_INSTANCE.getReference();
            Log.d("Reference", "Reference created!");
        }
     //   DB_REFERENCE.addListenerForSingleValueEvent(update);
        DB_REFERENCE.keepSynced(true);
        return DB_REFERENCE;
    }

    private static ValueEventListener update = new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(DB_REFERENCE==null){
                onDataChange(dataSnapshot);
            }
            String patientID;
            for (DataSnapshot child: dataSnapshot.child("patient_identification").getChildren()) {
               patientID = child.getValue(String.class);
                patientlist.add(dataSnapshot.child("patientsHashmap").child(patientID).getValue(Patient.class));
            }

            System.out.println("Succeeded");
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            System.out.println("Cancelled");
        }
    };



}
