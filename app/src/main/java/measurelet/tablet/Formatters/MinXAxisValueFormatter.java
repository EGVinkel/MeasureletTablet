package measurelet.tablet.Formatters;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.List;

public class MinXAxisValueFormatter implements IAxisValueFormatter {
    private List datoer;

    public MinXAxisValueFormatter(List<String> dates) {
        this.datoer = dates;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        try {
            int index = (int) value;
            return String.valueOf(datoer.get(index));
        } catch (Exception e) {
            return "";
        }
    }


}
