package measurelet.tablet;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import measurelet.tablet.Model.Patient;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView re;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static HashMap<String, Patient> patientsHashmap;
    private CheckBox clear;
    private NavController navC;
    private Integer inputbed;
    private Typeface font;
    private AlertDialog ad;
    private TextView add;
    private ArrayList<Patient> patientArrayList;
    private Context con = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        font = Typeface.createFromAsset(getAssets(), "font/Helvetica.ttf");
        navC = Navigation.findNavController(findViewById(R.id.nav_host));

       /* add = findViewById(R.id.ny);
        add.setTypeface(font);
        clear = findViewById(R.id.checkall);
        clear.setTypeface(font);
        clear.setOnClickListener(this);*/
//        add.setOnClickListener(this);
        re = findViewById(R.id.bedlist);
        re.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(con);
        re.setLayoutManager(mLayoutManager);
        DividerItemDecoration itemDecor = new DividerItemDecoration(con, DividerItemDecoration.VERTICAL);
        re.addItemDecoration(itemDecor);
        AppData.DB_REFERENCE.child("patients_test").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                patientsHashmap = new HashMap<>();
                patientArrayList = new ArrayList<>();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Patient p = childSnapshot.getValue(Patient.class);
                    patientsHashmap.put(p.uuid, p);
                    patientArrayList.add(p);
                }


                patientArrayList.sort(Comparator.comparingInt(Patient::getBedNum));

                mAdapter = new RecyclerviewAdapteren(patientArrayList, re, navC, font, con);
                re.setAdapter(mAdapter);
                if (navC.getCurrentDestination().getId() == R.id.graphfragment) {
                    AppData.ani = false;
                    navC.navigate(R.id.graphfragment, AppData.theb);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onClick(View view) {
        if (view == clear) {
            for (Patient p : patientArrayList) {
                if (!p.getChecked() && clear.isChecked()) {
                    p.setChecked(true);

                }
                if (p.getChecked() && !clear.isChecked()) {
                    p.setChecked(false);

                }

            }
            mAdapter.notifyDataSetChanged();

        }
        if (add == view) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(con);
            builder.setTitle("Tast sengenummer");

            builder.setCancelable(true);

            final EditText input = new EditText(con);
            int maxLength = 3;
            input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);

            builder.setPositiveButton("OK", (dialog, which) -> {

                if (input.getText().length() == 0) {


                    Handler handler = new Handler();
                    handler.post(() -> ad.show());
                }
                if (input.getText().length() > 0) {
                    inputbed = Integer.parseInt(input.getText().toString());
                    Patient p = new Patient("def", inputbed);
                    AppData.DB_REFERENCE.child("patients").child(p.getUuid()).setValue(p);
                    patientArrayList.add(p);
                    patientArrayList.sort(Comparator.comparingInt(Patient::getBedNum));
                    mAdapter.notifyDataSetChanged();

                }

            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            ad = builder.show();

        }

    }

    @Override
    public void onBackPressed() {
        if (navC.getCurrentDestination().getId() == R.id.dialogFragment) {
            navC.navigate(R.id.graphfragment, AppData.theb);
        } else
            super.onBackPressed();
    }
}











