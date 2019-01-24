package measurelet.tablet.Fragments;

import android.annotation.SuppressLint;
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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import androidx.fragment.app.DialogFragment;
import androidx.navigation.fragment.NavHostFragment;
import measurelet.tablet.Factories.IntakeFactory;
import measurelet.tablet.Model.Intake;
import measurelet.tablet.R;


public class AddliquidFragment extends DialogFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private MaterialButton add;
    private TextInputEditText enteredml, enteredname;
    private String liqtyp, id;
    private boolean other;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog, container, false);
        enteredname = v.findViewById(R.id.liquidtype);
        enteredml = v.findViewById(R.id.amountofliquid);
        id = getArguments().getString("Id");
        add = v.findViewById(R.id.plusbut);

        add.setOnClickListener(this);
        liqtyp = "";
        enteredml.setText("");
        enteredname.setText("");
        Spinner spin = v.findViewById(R.id.scrollvalg);
        String[] items = new String[]{ "Vand","Sodavand", "Kaffe", "Saftevand", "IV", "Andet"};
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
            int ml = Integer.parseInt(mil);

            Intake intake = new Intake(liqtyp, ml);
            IntakeFactory.InsertNewIntake(intake, id);
            Bundle b = new Bundle();
            b.putString("Id", id);
            NavHostFragment.findNavController(this).navigate(R.id.fadegraph, b);
            dismiss();


        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        switch (i) {
            case 0:
                liqtyp = "Vand";
                break;
            case 1:
                liqtyp ="Sodavand";
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


    @SuppressLint("SetTextI18n")
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