package measurelet.tablet;

import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<Testpatient> beds = new ArrayList<>();
    private RecyclerView re;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Bundle argbund;
    private Button add;
    private CheckBox clear;
    private Typeface font;
    private boolean sortani = true;
    private int prevpos;
    private Integer inputbed;
    private NavController navC;
    private AlertDialog ad;
    private Context con= this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        font = Typeface.createFromAsset(getAssets(), "font/Helvetica.ttf");
        for (int i = 1; i <= 15; i++) {
            beds.add(new Testpatient(false,i));
        }
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


        AppData.DB_REFERENCE.child("patients").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("APP","ASDASD");
                Log.d("APP",dataSnapshot.getChildrenCount() + "");

                //HashMap<String,Patient> patients = new HashMap();
                ArrayList<Patient> patients = new ArrayList<>();


                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    patients.add(childSnapshot.getValue(Patient.class));
                }

                Log.d("APP","qweqwe");

                mAdapter = new MyAdapter(patients);
                re.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public int getPrevpos() {
        return prevpos;
    }

    public void setPrevpos(int prevpos) {
        this.prevpos = prevpos;
    }

    @Override
    public void onClick(View view) {
        if (view == clear) {
           for(Testpatient p:beds){
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
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                        ad.show();
                        }
                    });
                }
                if(input.getText().length()>0) {
                    inputbed = Integer.parseInt(input.getText().toString());
                    beds.add(new Testpatient(false,inputbed));
                    beds.sort(Comparator.comparingInt(Testpatient::getBednumber));
                    mAdapter.notifyDataSetChanged();

                }

            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            ad=builder.show();

        }

    }


    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements View.OnClickListener {
        private ArrayList<Patient> bedlist;
        private MyAdapter(ArrayList<Patient> beds) {
            this.bedlist = beds;

        }


        @Override
        public void onClick(View view) {
//            System.out.println("Hej hej"+AppData.patientlist.get(0).getName());

            int itemPosition = re.getChildLayoutPosition(view);

            if (sortani) {
                setPrevpos(-1);
                sortani = false;

            }
            int prev = getPrevpos();
            if (itemPosition == -1) {
                itemPosition = itemPosition + 1;
                setPrevpos(getPrevpos() + 1);
            }
            setPrevpos(itemPosition);
            if (!beds.isEmpty()) {
                argbund.putInt("nr", beds.get(itemPosition).getBednumber());
                Graphfragment graph = new Graphfragment();
                graph.setArguments(argbund);


                if (itemPosition < prev) {

                    navC.navigate(R.id.action_enterleft,argbund);
                }
                if (itemPosition >= prev) {
                    navC.navigate(R.id.action_enterright,argbund);

                }
            }
            if (beds.isEmpty()) {
               navC.popBackStack();
            }

        }


        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView bednumber;
            public CheckBox checker;
            private ImageButton delete;

            public MyViewHolder(View v) {
                super(v);
                bednumber = v.findViewById(R.id.bedview);
                bednumber.setTypeface(font);
                checker = v.findViewById(R.id.bedcheck);
                checker.setOnClickListener(this);
                delete= v.findViewById(R.id.deletebutton);
                delete.setOnClickListener(this);


            }

            @Override
            public void onClick(View view) {
                int positionen = getAdapterPosition();


                if (view == checker) {
                    if (!bedlist.get(positionen).getChecked()) {
                        checker.setChecked(true);
                        bedlist.get(positionen).setChecked(true);
                    } else  if (bedlist.get(positionen).getChecked()){
                        checker.setChecked(false);
                        bedlist.get(positionen).setChecked(false);
                    }
                }
                if(view==delete){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(con);
                    builder.setTitle("Er du sikker på du vil slette?");
                    builder.setCancelable(true);
                    builder.setPositiveButton("OK", (dialog, which) -> {

                        beds.remove(positionen);
                        notifyItemRemoved(positionen);
                        notifyItemRangeChanged(positionen, beds.size());
                        navC.navigate(R.id.action_global_startFragment);



                    });
                    builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                    builder.show();
                }

            }


            private void bind(int position) {
                if (bedlist.get(position).getChecked()) {
                    checker.setChecked(true);
                } else if(!bedlist.get(position).getChecked()){
                    checker.setChecked(false);
                }

            }
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listlayout, viewGroup, false);
            v.setOnClickListener(this);

            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
            myViewHolder.bednumber.setText("Seng " + bedlist.get(position).getBedNum());
            myViewHolder.bind(position);
        }


        @Override
        public int getItemCount() {
            if (bedlist == null) {
                return 0;
            }
            return bedlist.size();
        }

    }


}











