package measurelet.tablet.Fragments;


import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import androidx.fragment.app.DialogFragment;
import androidx.navigation.fragment.NavHostFragment;
import measurelet.tablet.Factories.IntakeFactory;
import measurelet.tablet.Model.Intake;
import measurelet.tablet.R;


public class MyDialogFragment extends DialogFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    com.google.android.material.button.MaterialButton add;
    private com.google.android.material.textfield.TextInputEditText enteredml, enteredtime, enteredname;
    private TextView title;
    private int ml, time;
    private TextInputLayout gem;
    private String liqtyp, tim, id;

    private boolean other;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog, container, false);
        enteredname = v.findViewById(R.id.liquidtype);
        title = v.findViewById(R.id.overskrift);
        gem = v.findViewById(R.id.hide);
        enteredml = v.findViewById(R.id.amountofliquid);
        enteredtime = v.findViewById(R.id.time);
        id = getArguments().getString("Id");
        add = v.findViewById(R.id.plusbut);
        add.setOnClickListener(this);
        liqtyp = "";
        enteredml.setText("");
        enteredtime.setText("");
        enteredname.setText("");


        Spinner spin = v.findViewById(R.id.scrollvalg);
        String[] items = new String[]{"Sodavand", "Vand", "Kaffe", "Saftevand", "IV", "Andet"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(this);


        return v;
    }

    @Override
    public void onClick(View view) {

        if (view == add) {
            String mil = enteredml.getText().toString();
            String what = enteredname.getText().toString();


            if (mil.equals("")) {

                emptyFieldAlert("Hvor mange milliliter har du drukket?");
                return;
            }
            if (other) {
                if (what.equals("")) {
                    emptyFieldAlert("Hvilken type væske har du drukket?");
                    return;
                }
                liqtyp = enteredname.getText().toString();
            }
            ml = Integer.parseInt(mil);

            Intake intake = new Intake(liqtyp, ml);
            IntakeFactory.InsertNewIntake(intake, id);
            Bundle b = new Bundle();
            b.putString("Id", id);
            NavHostFragment.findNavController(this).navigate(R.id.fadegraph, b);
            dismiss();


        }
    }

    //EVT implementering, kommer an på tlf app.
            /*if(iv.isChecked()){
            tim = enteredtime.getText().toString();
            if(tim.equals("")){
                tim="0";
            }
            if(!iv.isChecked()){
                tim="0";
            }

            time =Integer.parseInt(tim);*/

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        switch (i) {
            case 0:
                liqtyp = "Sodavand";
                break;
            case 1:
                liqtyp = "Vand";
                break;
            case 2:
                liqtyp = "Kaffe";
                break;
            case 3:
                liqtyp = "Saftevand";
                break;
            case 4:
                liqtyp = "IV";
                break;
            case 5:
                liqtyp = "Andet";
                break;

        }
        if (liqtyp.equals("IV")) {
            getView().findViewById(R.id.hide).setVisibility(View.VISIBLE);
        }
        if (!liqtyp.equals("IV")) {
            getView().findViewById(R.id.hide).setVisibility(View.INVISIBLE);
        }
        if (liqtyp.equals("Andet")) {
            other = true;
            getView().findViewById(R.id.setsynligt).setVisibility(View.VISIBLE);
        } else {

            other = false;
            getView().findViewById(R.id.setsynligt).setVisibility(View.INVISIBLE);

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    private void emptyFieldAlert(String errorMsg) {
        TextView title = new TextView(getContext());
        title.setText("INTET INDTASTET!");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setText(errorMsg);
        title.setTextSize(28);
        new AlertDialog.Builder(getActivity())
                .setCustomTitle(title)
                .setCancelable(true)
                .show();
    }


}