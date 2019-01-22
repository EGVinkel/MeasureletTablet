package measurelet.tablet.Fragments;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DateSorter;
import android.widget.CalendarView;
import android.widget.Toast;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.temporal.ChronoField;
import org.threeten.bp.temporal.TemporalField;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import measurelet.tablet.AppData;
import measurelet.tablet.Factories.GraphDataFactory;
import measurelet.tablet.MainActivity;
import measurelet.tablet.Model.Intake;
import measurelet.tablet.Model.Patient;
import measurelet.tablet.Model.Weight;
import measurelet.tablet.R;


public class Date_Selecter_Fragment extends DialogFragment implements  CalendarView.OnDateChangeListener {
    String id;
    String selection;
    CalendarView cal;
    Bundle b= new Bundle();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View datefrag= inflater.inflate(R.layout.fragment_date__selecter_, container, false);
        cal= datefrag.findViewById(R.id.pickdatecal);
        cal.setOnDateChangeListener(this);
        cal.setFirstDayOfWeek(2);


        id= getArguments().getString("Id", "");
        selection= getArguments().getString("selection", "");
        b.putString("Id",id);
        b.putString("selection",selection);

        Long min=(long)0;
        Long max=(long)0;
        Patient curpat= MainActivity.patientsHashmap.get(id);

       if(selection.equalsIgnoreCase("Weight")){

            ArrayList<Weight> localw= new ArrayList<>(curpat.getSortedWeights());
            for(Weight w: localw){
                System.out.println(w.getDatetime());
            }

            max=  localw.get(0).getDatetime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            min= localw.get(localw.size()-1).getDatetime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();


        }
        if(selection.equalsIgnoreCase("Intake")){
            ArrayList<Intake> localintake= new ArrayList<>(curpat.getSortedRegs());
            for(Intake w: localintake){
                System.out.println(w.getDateTime());
            }
            max=  localintake.get(0).getDateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            min= localintake.get( localintake.size()-1).getDateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }





        cal.setMinDate(min);
        cal.setMaxDate(max);


        return datefrag;


    }


    @Override
    public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {

        LocalDate date= LocalDate.of(year,month+1,day);
        String s= date.toString();
        Log.d("date format",s);
        for(Intake in: MainActivity.patientsHashmap.get(id).getIntakesForDate(LocalDate.parse(s))){
            System.out.println("Intakes" +in.getType()+ in.getSize() + in.getDateTime());
        }

        b.putString("date",s);
        DialogFragment dialog= new Editlist();

         dialog.setArguments(b);
        dialog.show(getFragmentManager(), "list");

        this.dismiss();
    }
}
