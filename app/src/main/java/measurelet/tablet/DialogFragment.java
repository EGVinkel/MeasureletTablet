package measurelet.tablet;


import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class DialogFragment extends android.support.v4.app.DialogFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private EditText enteredml,enteredtime,enteredname;
    private CheckBox iv;
    private ImageButton add;
    private TextView timetitle, title, mltitle, typetitle;
    private int ml,time;
    private String liqtyp,tim;
    private LinearLayout hidden;
    private Typeface font;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog, container, false);
        font = Typeface.createFromAsset(getActivity().getAssets(), "font/Helvetica.ttf");
        enteredname=v.findViewById(R.id.liquidtype);
        title = v.findViewById(R.id.overskrift);
        title.setTypeface(font);
        mltitle = v.findViewById(R.id.ml);
        mltitle.setTypeface(font);
        typetitle = v.findViewById(R.id.typeliq);
        typetitle.setTypeface(font);




        enteredml=v.findViewById(R.id.amountofliquid);
        timetitle=v.findViewById(R.id.timetitle);
        timetitle.setTypeface(font);
        enteredtime=v.findViewById(R.id.time);
        hidden=v.findViewById(R.id.gemtid);
        iv=v.findViewById(R.id.checkBoxIV);
        iv.setTypeface(font);
        iv.setOnCheckedChangeListener(this);
        add=v.findViewById(R.id.plusbut);
        add.setOnClickListener(this);
        liqtyp="vand";


        return v;
    }

    @Override
    public void onClick(View view) {

        if (view == add) {
            String mil = enteredml.getText().toString();
            if(iv.isChecked()){
                tim = enteredtime.getText().toString();
                if(tim.equals("")){
                    tim="0";
                }
            }

            if (mil.equals("")) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Indtast mængde")
                        .setCancelable(true)
                        .show();
                return;
            }
            if(!iv.isChecked()){
                tim="0";
            }
            ml = Integer.parseInt(mil);
            time =Integer.parseInt(tim);
            liqtyp = enteredname.getText().toString();

            Toast.makeText(getActivity(),  ml + "ml " + liqtyp+" over "+time+" timer", Toast.LENGTH_LONG).show();
            this.dismiss();
        }

    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(compoundButton==iv){
            if(b){
                timetitle.setVisibility(View.VISIBLE);
                enteredtime.setVisibility(View.VISIBLE);
                hidden.setVisibility(View.VISIBLE);

            }
            if(!b){
                timetitle.setVisibility(View.INVISIBLE);
                enteredtime.setVisibility(View.INVISIBLE);
                hidden.setVisibility(View.INVISIBLE);

            }


        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(getActivity().getSupportFragmentManager(), "hej");
        enteredml.setText("");
        enteredtime.setText("");
        enteredname.setText("");

    }
}