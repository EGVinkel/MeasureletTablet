package measurelet.tablet.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import measurelet.tablet.Factories.IntakeFactory;
import measurelet.tablet.Factories.WeightFactory;
import measurelet.tablet.MainActivity;
import measurelet.tablet.Model.Intake;
import measurelet.tablet.Model.Weight;
import measurelet.tablet.R;

public class EditLiquidDialog extends DialogFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, TimePicker.OnTimeChangedListener {

    private TextInputEditText selftyped, amount_input;
    private TextView title;
    private TimePicker timePicker;
    private MaterialButton gemReg,sletReg;
    private Spinner spinner;
    private String selection,id,datestring, type;
    private int hour, minute;
    private boolean other, weightflow;
    private Intake Intake;
    private Weight Weight;
    private TextInputLayout selftypedlayout;


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_edit_liquid, container, false);

        this.setCancelable(false);

        Bundle bundle = getArguments();
        if (bundle==null){
            dismiss();
            return null;
        }//Argumenter
        TextInputLayout inputsethint = view.findViewById(R.id.inputlayout);
        selftypedlayout=view.findViewById(R.id.selftype);
        int position = bundle.getInt("position");
        LinearLayout lin= view.findViewById(R.id.hidethis);
        id= getArguments().getString("Id", "");
        selection= getArguments().getString("selection", "");
        datestring=getArguments().getString("date", "");
        title= view.findViewById(R.id.titleview);
        sletReg = view.findViewById(R.id.deleteReg);
        sletReg.setOnClickListener(this);
        gemReg = view.findViewById(R.id.saveChanges);
        gemReg.setOnClickListener(this);
        selftyped = view.findViewById(R.id.selftyped1);
        amount_input = view.findViewById(R.id.amount_input);
        timePicker = view.findViewById(R.id.timepicker);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(this);


        if(selection.equalsIgnoreCase("Weight")){
            lin.setVisibility(View.INVISIBLE);
            title.setText("Rediger Vægt");
            weightflow=true;
            inputsethint.setHint("Indtast vægt");
            Weight = MainActivity.patientsHashmap.get(id).getWeightForDate(LocalDate.parse(datestring));
            timePicker.setVisibility(View.INVISIBLE);
            amount_input.setText(Double.toString(Weight.getWeightKG()));
            amount_input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            selftypedlayout.setVisibility(View.INVISIBLE);
            selftyped.setVisibility(view.INVISIBLE);
            other=false;

        }
        if(selection.equalsIgnoreCase("Intake")){
            Intake = MainActivity.patientsHashmap.get(id).getIntakesForDate(LocalDate.parse(datestring)).get(position);
            type = Intake.getType();
            inputsethint.setHint("Indtast mængde");
            timePicker.setHour(Intake.getDateTime().getHour());
            timePicker.setMinute(Intake.getDateTime().getMinute());
            amount_input.setText(Integer.toString(Intake.getSize()));
            weightflow=false;

        }

        spinner = view.findViewById(R.id.scrollvalg1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.fluidtypes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        if(weightflow){
            spinner.setVisibility(View.INVISIBLE);
            selftyped.setVisibility(View.INVISIBLE);
        }

        return view;
    }






    @Override
    public void onClick(View vv) {
        if (vv == gemReg) {
            LocalDateTime date;


            if(!weightflow){
                if (other){
                    if(selftyped.getText().toString().equals("")){
                        type="Andet";
                    }
                    else {
                        type = selftyped.getText().toString();
                    }

                }
                date = LocalDate.parse(datestring).atTime(timePicker.getHour(), timePicker.getMinute());
                Intake intake = new Intake(type, Integer.parseInt(amount_input.getText().toString()) , Intake.getUuid(), date.toString());
                IntakeFactory.UpdateNewIntake(intake, id);
            }
            if(weightflow){
                Log.d("hej", datestring);
                date = LocalDate.parse(datestring).atTime(LocalDateTime.now().getHour(), LocalDateTime.now().getMinute());
                Weight weight = new Weight(Double.parseDouble(amount_input.getText().toString()),Weight.uuid,date.toString());
                WeightFactory.UpdateNewWeight(weight,id);

            }


            dismiss();
        }
        if (vv == sletReg) {
            Context context = getActivity();
            String title = "Slet";
            String message = "Er du sikker på du vil slette denne registrering?";
            String button1String = "Slet";
            String button2String = "Fortryd";

            final AlertDialog.Builder ad = new AlertDialog.Builder(context);
            ad.setTitle(title);
            ad.setMessage(message);

            ad.setPositiveButton(
                    button1String,
                    (dialog, which) -> {
                        if(weightflow){
                            WeightFactory.DeleteIntake(Weight,id);
                        }
                        if(!weightflow){
                            IntakeFactory.DeleteIntake(Intake,id);
                        }

                        dismiss();                        }
            );

            ad.setNegativeButton(
                    button2String,
                    (dialog, which) -> dismiss()
            );

            ad.show();


        }
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

        switch (position) {
            case 0:
                type = "Vand";
                break;
            case 1:
                type = "IV";
                break;
            case 2:
                type = "Kaffe";
                break;
            case 3:
                type = "Sodavand";
                break;
            case 4:
                type = "Saftevand";
                break;
            case 5:
                type = "Andet";
                break;
        }

        if (type.equals("Andet")) {
            other = true;
            selftypedlayout.setVisibility(View.VISIBLE);
        } else {

            other = false;
            selftypedlayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        this.hour = hourOfDay;
        this.minute = minute;

    }

}
