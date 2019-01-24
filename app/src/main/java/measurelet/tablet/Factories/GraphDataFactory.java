package measurelet.tablet.Factories;

import android.util.ArraySet;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import measurelet.tablet.MainActivity;
import measurelet.tablet.Model.Intake;
import measurelet.tablet.Model.Patient;
import measurelet.tablet.Model.Weight;

public class GraphDataFactory {

    public static List<Entry> getKgEntries(String id) {
        List<Entry> datapoints = new ArrayList<>();
        Patient currentpatient = MainActivity.patientsHashmap.get(id);
        HashMap<String, Double> registrationsDateMap = new HashMap<>();
        assert currentpatient != null;
        String weight = "Weight";
        for (LocalDate local : dateSorter(id, weight)) {
            registrationsDateMap.put(local.toString(), (currentpatient.getWeightForDate(local).getWeightKG()));
        }


        for (int i = 0; i < dateSorter(id, weight).size(); i++) {

            datapoints.add(new Entry((float) i, Objects.requireNonNull(registrationsDateMap.get(dateSorter(id, weight).valueAt(i).toString())).floatValue()));
        }
        return datapoints;
    }

    public static ArraySet<LocalDate> dateSorter(String id, String type) {

        Patient currentpatient = MainActivity.patientsHashmap.get(id);

        ArraySet<LocalDate> dates = new ArraySet<>();
        if (type.equals("Intake")) {
            assert currentpatient != null;
            List<Intake> currentpatientRegistrations = currentpatient.getSortedRegs();
            for (Intake intake : currentpatientRegistrations) {
                dates.add(intake.getDateTime().toLocalDate());
            }
        }
        if (type.equals("Weight")) {
            assert currentpatient != null;
            List<Weight> currentpatientRegistrations = currentpatient.getSortedWeights();
            for (Weight weight : currentpatientRegistrations) {
                dates.add(weight.getDatetime().toLocalDate());

            }

        }
        return dates;
    }

    public static List<BarEntry> MlEntries(String id) {
        List<BarEntry> datapoints = new ArrayList<>();
        Patient currentpatient = MainActivity.patientsHashmap.get(id);
        HashMap<String, Integer> registrationsDateMap = new HashMap<>();
        HashMap<String, Integer> IVregistrationsDateMap = new HashMap<>();
        String intake = "Intake";
        assert currentpatient != null;
        for (LocalDate local : dateSorter(id, intake)) {
            int ml1 = 0;
            int ml2 = 0;
            for (Intake in : currentpatient.getIntakesForDate(local)) {
                if (in.getType().equalsIgnoreCase("Iv")) {
                    ml2 = ml2 + in.getSize();

                } else if (!in.getType().equalsIgnoreCase("Iv")) {
                    ml1 = ml1 + in.getSize();
                }

            }

            registrationsDateMap.put(local.toString(), ml1);
            IVregistrationsDateMap.put(local.toString(), ml2);


        }

        for (int i = 0; i < dateSorter(id, intake).size(); i++) {
            datapoints.add(new BarEntry((float) i, new float[]{(Objects.requireNonNull(registrationsDateMap.get(dateSorter(id, intake).valueAt(i).toString())).floatValue()), (Objects.requireNonNull(IVregistrationsDateMap.get(dateSorter(id, "Intake").valueAt(i).toString())).floatValue())}));

        }

        return datapoints;
    }

}
