package measurelet.tablet.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import measurelet.tablet.MainActivity;
import measurelet.tablet.Model.Intake;
import measurelet.tablet.Model.Patient;
import measurelet.tablet.Model.Weight;
import measurelet.tablet.R;


public class Date_Selecter_Fragment extends DialogFragment implements OnDayClickListener {
    String id;
    String selection;
    com.applandeo.materialcalendarview.CalendarView cal;
    Bundle b= new Bundle();
    ArrayList<Weight> localw;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View datefrag= inflater.inflate(R.layout.fragment_date__selecter_, container, false);
        cal= datefrag.findViewById(R.id.pickdatecal);

        cal.setHeaderColor(R.color.colorAccent);
        id= getArguments().getString("Id", "");
        selection= getArguments().getString("selection", "");
        b.putString("Id",id);
        b.putString("selection",selection);


        Patient curpat= MainActivity.patientsHashmap.get(id);
        ArrayList<EventDay> events = new ArrayList<>();
        ArrayList<Calendar> calp = new ArrayList<>();

       if(selection.equalsIgnoreCase("Weight")){

           localw = new ArrayList<>(curpat.getSortedWeights());
            for(Weight w: localw){
                events.add(new EventDay(localDateTimeToCalendar(w.getDatetime()), R.drawable.ic_cake_black_24dp));

            }




        }
        if(selection.equalsIgnoreCase("Intake")){
            ArrayList<Intake> localintake= new ArrayList<>(curpat.getSortedRegs());
            for(Intake w: localintake){
                events.add(new EventDay(localDateTimeToCalendar(w.getDateTime()), R.drawable.ic_baseline_opacity_24px));


            }

        }


        cal.setEvents(events);

        cal.setOnDayClickListener(this);



        return datefrag;


    }


    private Calendar localDateTimeToCalendar(LocalDateTime localDateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(localDateTime.getYear(), localDateTime.getMonthValue() - 1, localDateTime.getDayOfMonth(),
                localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond());
        return calendar;
    }

    @Override
    public void onDayClick(EventDay eventDay) {
        Calendar temp = eventDay.getCalendar();
        LocalDate date = LocalDate.of(temp.get(Calendar.YEAR), temp.get(Calendar.MONTH) + 1, temp.get(Calendar.DAY_OF_MONTH));
        String s= date.toString();
        Patient curpat = MainActivity.patientsHashmap.get(id);
        if (selection.equalsIgnoreCase("Intake")) {

            if (curpat.getIntakesForDate(date).isEmpty()) {
                Toast.makeText(getActivity(), "Ingen registreringer på denne dag", Toast.LENGTH_LONG).show();
                return;
            }
        }
        if (selection.equalsIgnoreCase("Weight")) {
            if (curpat.getWeightForDate(date) == null) {
                Toast.makeText(getActivity(), "Ingen registreringer på denne dag", Toast.LENGTH_LONG).show();
                return;

            }
        }

        b.putString("date",s);
        if (selection.equals("Intake")) {
            DialogFragment dialog = new Editlist();
            dialog.setArguments(b);
            dialog.show(getFragmentManager(), "list");
        }
        if (selection.equals("Weight")) {
            DialogFragment dialog = new edit_liquid();
            b.putInt("position", 0);
            dialog.setArguments(b);
            dialog.show(getFragmentManager(), "dialog");
            dismiss();
        }

        this.dismiss();
    }
}
