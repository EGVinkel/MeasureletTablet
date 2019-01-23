package measurelet.tablet.Factories;

import java.util.ArrayList;
import java.util.List;

import measurelet.tablet.AppData;
import measurelet.tablet.MainActivity;
import measurelet.tablet.Model.Weight;

public class WeightFactory {

    public static void InsertNewWeight(Weight weight,String id) {
        List<Weight> weights = MainActivity.patientsHashmap.get(id) == null ? new ArrayList<>() : MainActivity.patientsHashmap.get(id).getWeights();
        weights.add(weight);

        AppData.DB_REFERENCE.child("patients").child(id).child("weights").setValue(weights);
    }

    public static void UpdateNewWeight(Weight weight,String id) {
        List<Weight> weights = MainActivity.patientsHashmap.get(id) == null ? new ArrayList<>() : MainActivity.patientsHashmap.get(id).getWeights();
        int findindex=0;
        for (int i = 0; i <weights.size() ; i++) {
            if(weights.get(i).uuid.equals(weight.uuid)){
                findindex = i;
            }

        }
        weights.remove(findindex);
        weights.add(findindex,weight);
        AppData.DB_REFERENCE.child("patients").child(id).child("weights").setValue(weights);
    }

    public static void DeleteIntake(Weight weight,String id) {
        List<Weight> weights = MainActivity.patientsHashmap.get(id) == null ? new ArrayList<>() : MainActivity.patientsHashmap.get(id).getWeights();
        weights.remove(weight);
        AppData.DB_REFERENCE.child("patients").child(id).child("weights").setValue(weights);

    }

}
