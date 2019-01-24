package measurelet.tablet;

import android.animation.Animator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import measurelet.tablet.Model.Patient;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView re;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public static HashMap<String, Patient> patientsHashmap = new HashMap<>();
    private MaterialButton clear, add;
    private NavController navC;
    private Integer bednumber;
    private String name;
    private AlertDialog ad;
    private ArrayList<Patient> patientArrayList = new ArrayList<>();
    private Context con = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("font/helvetica.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());


        setContentView(R.layout.appbarlogo);
        androidx.appcompat.widget.Toolbar tool = findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        navC = Navigation.findNavController(findViewById(R.id.nav_host));
        add = findViewById(R.id.ny);
        clear = findViewById(R.id.checkall);
        clear.setOnClickListener(this);
        add.setOnClickListener(this);
        re = findViewById(R.id.bedlist);
        re.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(con);
        re.setLayoutManager(mLayoutManager);
        DividerItemDecoration itemDecor = new DividerItemDecoration(con, DividerItemDecoration.VERTICAL);
        re.addItemDecoration(itemDecor);
        AppData.DB_REFERENCE.child("patients").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Patient p = dataSnapshot.getValue(Patient.class);
                    assert p != null;
                    p.getRegistrations().removeIf(Objects::isNull);
                    p.getWeights().removeIf(Objects::isNull);
                    patientsHashmap.put(p.uuid, p);
                    patientArrayList.add(p);



                patientArrayList.sort(Comparator.comparingInt(Patient::getBedNum));
                if (mAdapter == null) {
                    mAdapter = new BedRecyclerviewAdapter(patientArrayList, re, navC);
                    re.setAdapter(mAdapter);

                }
                AppData.ani = false;
                mAdapter.notifyDataSetChanged();
                if (navC.getCurrentDestination().getId() == R.id.graphfragment) {
                    navC.navigate(R.id.fadegraph, AppData.theb);

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Patient oldpat = patientsHashmap.get(dataSnapshot.getKey());
                Patient changedpat = dataSnapshot.getValue(Patient.class);
                changedpat.setMarked(oldpat.getMarked());
                changedpat.setChecked(oldpat.getChecked());
                patientArrayList.removeIf(p -> dataSnapshot.getKey().equals(p.getUuid()));

                patientArrayList.add(changedpat);
                patientsHashmap.put(dataSnapshot.getKey(), changedpat);
                patientArrayList.sort(Comparator.comparingInt(Patient::getBedNum));
                mAdapter.notifyDataSetChanged();
                navC.navigate(R.id.fadegraph, AppData.theb);

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                patientArrayList.removeIf(p -> dataSnapshot.getKey().equals(p.getUuid()));
                patientsHashmap.remove(dataSnapshot.getKey());
                patientArrayList.sort(Comparator.comparingInt(Patient::getBedNum));
                mAdapter.notifyDataSetChanged();
                navC.navigate(R.id.fadegraph, AppData.theb);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


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
                if (p.getChecked()) {
                    p.setChecked(false);

                }

            }
            mAdapter.notifyDataSetChanged();

        }
        if (add == view) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(con);
            builder.setCancelable(true);
            final TextInputEditText inputbed = new TextInputEditText(con);
            final TextInputEditText inputname = new TextInputEditText(con);
            final int maxLength = 3;
            inputbed.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
            inputbed.setInputType(InputType.TYPE_CLASS_NUMBER);
            inputname.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
            TextInputLayout tbed = new TextInputLayout(con);
            tbed.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
            TextInputLayout tname = new TextInputLayout(con);
            tname.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
            tname.addView(inputname);
            tbed.addView(inputbed);
            tbed.setHint("Sengenummer");
            tname.setHint("Navn");
            LinearLayout linearLayout = new LinearLayout(con);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams Layoutparam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            Layoutparam.bottomMargin = 30;
            Layoutparam.topMargin=30;
            Layoutparam.leftMargin=30;
            Layoutparam.rightMargin=30;

            linearLayout.addView(tname,Layoutparam);
            linearLayout.addView(tbed,Layoutparam);
            builder.setView(linearLayout);

            builder.setPositiveButton("OK", (dialog, which) -> {

                if (Objects.requireNonNull(inputbed.getText()).length() == 0 && inputname.length() == 0) {


                    Handler handler = new Handler();
                    handler.post(() -> ad.show());
                }
                if (inputbed.getText().length() > 0 && inputname.length() > 0) {
                    this.bednumber = Integer.parseInt(inputbed.getText().toString());
                    this.name = Objects.requireNonNull(inputname.getText()).toString();

                    Patient p = new Patient(this.name, this.bednumber);
                    AppData.DB_REFERENCE.child("patients").child(p.getUuid()).setValue(p);

                }

            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            ad = builder.show();

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (loadAnimation().isAnimating()) {
            loadAnimation().cancelAnimation();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }


    public LottieAnimationView loadAnimation() {

        LottieAnimationView lw;

        lw = findViewById(R.id.lot_view);
        lw.setAnimation("trail_loading.json");
        lw.setRepeatCount(LottieDrawable.INFINITE);


        lw.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                lw.setVisibility(View.VISIBLE);
                lw.animate().alpha(0.9f);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                lw.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        return lw;
    }




}











