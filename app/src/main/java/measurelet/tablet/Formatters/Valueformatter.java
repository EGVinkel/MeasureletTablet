package measurelet.tablet.Formatters;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class Valueformatter extends DefaultValueFormatter {

    public Valueformatter(int digits) {
        super(digits);
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex,
                                    ViewPortHandler viewPortHandler) {
        if (value > 0) {
            return mFormat.format(value);
        } else {
            return "";
        }
    }
}
