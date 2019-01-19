package measurelet.tablet.Factories;

import android.util.ArraySet;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import measurelet.tablet.MainActivity;
import measurelet.tablet.Model.Intake;
import measurelet.tablet.Model.Patient;
import measurelet.tablet.Model.Weight;

public class GraphDataFactory {


    public static ArrayList<BarEntry> getMlEntries(String id) {
        ArrayList<BarEntry> datapoints = new ArrayList<>();
        Patient currentpatient = MainActivity.patientsHashmap.get(id);
        HashMap<String, Integer> registrationsDateMap = new HashMap<>();
        HashMap<String, Integer> IVregistrationsDateMap = new HashMap<>();
        DateTimeFormatter formate = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        assert currentpatient != null;
        for (Intake intake : currentpatient.getRegistrations()) {
            int ml = 0;
            int mliv = 0;
            for (String s : dateSorter(id, "Intake")) {
                if (!registrationsDateMap.containsKey(s) && formate.format(intake.getDateTime()).equals(s)) {
                    ml = intake.getSize();
                }
                if (!IVregistrationsDateMap.containsKey(s) && formate.format(intake.getDateTime()).equals(s)) {
                    mliv = intake.getSize();
                }

            }

            if (registrationsDateMap.containsKey(formate.format(intake.getDateTime()))) {

                ml = intake.getSize() + registrationsDateMap.get(formate.format(intake.getDateTime()));
            }
            if (IVregistrationsDateMap.containsKey(formate.format(intake.getDateTime()))) {
                mliv = intake.getSize() + IVregistrationsDateMap.get(formate.format(intake.getDateTime()));
            }


            if (intake.getType().equalsIgnoreCase("Iv")) {
                IVregistrationsDateMap.put(formate.format((intake.getDateTime())), mliv);

            } else if (!intake.getType().equalsIgnoreCase("Iv")) {
                registrationsDateMap.put(formate.format((intake.getDateTime())), ml);
            }
        }


        for (int i = 0; i < dateSorter(id, "Intake").size(); i++) {
            if (IVregistrationsDateMap.get(dateSorter(id, "Intake").get(i)) == null) {
                IVregistrationsDateMap.put(dateSorter(id, "Intake").get(i), 0);
            }

            datapoints.add(new BarEntry((float) i, new float[]{(Objects.requireNonNull(registrationsDateMap.get(dateSorter(id, "Intake").get(i))).floatValue()), (Objects.requireNonNull(IVregistrationsDateMap.get(dateSorter(id, "Intake").get(i))).floatValue())}));


        }

        return datapoints;
    }

    public static ArrayList<Entry> getKgEntries(String id) {
        ArrayList<Entry> datapoints = new ArrayList<>();
        Patient currentpatient = MainActivity.patientsHashmap.get(id);
        HashMap<String, Double> registrationsDateMap = new HashMap<>();
        DateTimeFormatter formate = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        assert currentpatient != null;
        for (Weight weight : currentpatient.getWeights()) {
            registrationsDateMap.put(formate.format(weight.getDatetime()), weight.getWeightKG());
        }

        for (int i = 0; i < dateSorter(id, "Weight").size(); i++) {
            if (registrationsDateMap.get(dateSorter(id, "Weight").get(i)) == null) {
                registrationsDateMap.put(dateSorter(id, "Weight").get(i), 0.0);
            }
            datapoints.add(new Entry((float) i, Objects.requireNonNull(registrationsDateMap.get(dateSorter(id, "Weight").get(i))).floatValue()));
        }
        return datapoints;
    }

    public static ArrayList<String> dateSorter(String id, String type) {

        Patient currentpatient = MainActivity.patientsHashmap.get(id);
        DateTimeFormatter formate = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        ArraySet<String> dates = new ArraySet<>();
        if (type.equals("Intake")) {
            assert currentpatient != null;
            ArrayList<Intake> currentpatientRegistrations = currentpatient.getRegistrations();
            for (Intake intake : currentpatientRegistrations) {
                dates.add(formate.format(intake.getDateTime()));
            }
        }
        if (type.equals("Weight")) {
            assert currentpatient != null;
            ArrayList<Weight> currentpatientRegistrations = currentpatient.getWeights();
            for (Weight weight : currentpatientRegistrations) {
                dates.add(formate.format(weight.getDatetime()));

            }

        }

        ArrayList<String> findates = new ArrayList<>(dates);
        Collections.sort(findates);
        return findates;
    }


}
