package measurelet.tablet;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class AppData extends Application {

    public static Bundle theb = new Bundle();
    public static boolean ani = false;
    private FirebaseDatabase DB_INSTANCE;
    public static DatabaseReference DB_REFERENCE;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);

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

}
