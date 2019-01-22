package measurelet.tablet.Factories;

import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import measurelet.tablet.AppData;
import measurelet.tablet.MainActivity;
import measurelet.tablet.Model.Intake;
//Fra Measurelet TLF appen. Modificeret til brug i tablet.

public class IntakeFactory {

    public static void InsertNewIntake(Intake intake, String id) {

        List<Intake> intakes = MainActivity.patientsHashmap.get(id) == null ? new ArrayList<>() : MainActivity.patientsHashmap.get(id).getRegistrations();
        intakes.add(intake);

        AppData.DB_REFERENCE.child("patients").child(id).child("registrations").setValue(intakes);
    }

        public static void UpdateNewIntake(Intake intake,String id) {
            List<Intake> intakes = MainActivity.patientsHashmap.get(id) == null ? new ArrayList<>() : MainActivity.patientsHashmap.get(id).getRegistrations();
            int findindex=0;
            for (int i = 0; i <intakes.size() ; i++) {
                if(intakes.get(i).uuid.equals(intake.uuid)){
                    findindex=i;
                }

            }
            intakes.remove(findindex);
            intakes.add(findindex,intake);
            AppData.DB_REFERENCE.child("patients").child(id).child("registrations").setValue(intakes);

    }



    public static HashMap<String, Integer> getIntakePrHour(ArrayList<Intake> dailyIntake) {

        DateTimeFormatter formatHour = DateTimeFormatter.ofPattern("HH");
        HashMap<String, Integer> hourMap = new HashMap<>();

        //samler mængder efter time
        int mængde = 0;
        for (Intake intake : dailyIntake) {
            String hour = intake.getDateTime().format(formatHour);
            if (hourMap.containsKey(hour)) {
                mængde = mængde + intake.getSize();
            } else {
                mængde = intake.getSize();
            }

            hourMap.put(hour, mængde);
        }


        return hourMap;
    }
    public static void DeleteIntake(Intake intake,String id) {
        List<Intake> intakes = MainActivity.patientsHashmap.get(id) == null ? new ArrayList<>() : MainActivity.patientsHashmap.get(id).getRegistrations();
        intakes.remove(intake);
        AppData.DB_REFERENCE.child("patients").child(id).child("registrations").setValue(intakes);

    }


}

