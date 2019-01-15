package measurelet.tablet;import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import measurelet.tablet.Model.Patient;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView re;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Bundle argbund;
    private Button add;
    private CheckBox clear;
    private NavController navC;
    private Integer inputbed;
    private Typeface font;
    private AlertDialog ad;
    private Context con= this;
    private ArrayList<Patient> patientArrayList;
    static HashMap<String,Patient> patientsHashmap;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        font = Typeface.createFromAsset(getAssets(), "font/Helvetica.ttf");
      /*  for (int i = 1; i <= 15; i++) {
            beds.add(new Patient(false,i));
        }*/
        if (argbund == null) {
            argbund = new Bundle();
        }
        navC=Navigation.findNavController(findViewById(R.id.nav_host));

        add = findViewById(R.id.ny);
        clear = findViewById(R.id.checkall);
        clear.setOnClickListener(this);
        add.setOnClickListener(this);
        re= findViewById(R.id.bedlist);
        re.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(con);
        re.setLayoutManager(mLayoutManager);
        DividerItemDecoration itemDecor = new DividerItemDecoration(con, DividerItemDecoration.VERTICAL);
        re.addItemDecoration(itemDecor);
        AppData.DB_REFERENCE.child("patients").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //HashMap<String,Patient> patientsHashmap = new HashMap();
                patientsHashmap = new HashMap<>();
                patientArrayList= new ArrayList<>();

                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    Patient p= childSnapshot.getValue(Patient.class);
                    Log.d("Patientseng",p.getBedNum()+"");
                    patientsHashmap.put(p.uuid,p);
                    patientArrayList.add(p);
                }


                patientArrayList.sort(Comparator.comparingInt(Patient::getBedNum));

                Log.d("APP","qweqwe");
                mAdapter = new RecyclerviewAdapteren(patientArrayList,re,navC,font,argbund,con);
                re.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    @Override
    public void onClick(View view) {
        if (view == clear) {
           for(Patient p:patientArrayList){
               if(!p.getChecked()&&clear.isChecked()){
                   p.setChecked(true);

               }
               if(p.getChecked()&&!clear.isChecked()){
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
            input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);

            builder.setPositiveButton("OK", (dialog, which) -> {

                if (input.getText().length()==0) {


                    Handler handler = new Handler();
                    handler.post(() -> ad.show());
                }
                if(input.getText().length()>0) {
                    inputbed = Integer.parseInt(input.getText().toString());
                    Patient p=new Patient("def",inputbed);
                    AppData.DB_REFERENCE.child("patients").child(p.getUuid()).setValue(p);
                    patientArrayList.add(p);
                    patientArrayList.sort(Comparator.comparingInt(Patient::getBedNum));
                    mAdapter.notifyDataSetChanged();

                }

            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            ad=builder.show();

        }

    }



}











