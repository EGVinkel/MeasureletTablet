package measurelet.tablet;

import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;


public class graphfragment extends Fragment implements View.OnClickListener, OnChartValueSelectedListener {
    private View view;
    private ArrayList<String> dates = new ArrayList<>();
    private ArrayList<BarEntry> datapoints = new ArrayList<>();
    private ArrayList<Entry> datapoints2 = new ArrayList<>();
    private int[] dummyvaluesml = new int[]{1700, 1800, 2000, 1500, 1600, 1700, 1800, 1500, 2400, 2200, 2800, 2300, 2000, 3000, 1100, 2100, 1900, 2400, 2600, 2700, 2300, 1900, 1800, 2000, 3000, 1900, 3000, 2900, 2100, 2400};
    private float[] dummyvalueskg = new float[]{70.5f, 71, 70.7f, 73, 72, 70.7f, 72, 70.7f, 72, 72, 70.7f, 70.5f, 70.7f, 70.5f, 70.5f, 71.3f, 71.3f, 71.3f, 71.5f, 71.7f, 71.6f, 72.3f, 73.3f, 75.3f, 69.4f, 69.3f, 74.3f, 72.3f, 73.3f, 70.3f};
    private ImageButton add;
    private DialogFragment væskreg = new DialogFragment();
    private TextView dato, mlday, weightday;
    private XAxis xAxisml, xAxiskg;
    private BarChart graphml;
    private LineChart graphkg;
    private String temp;
    private BarData bardata;
    private LineData kgdata;
    private SimpleDateFormat formate;
    private Calendar cal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            view= inflater.inflate(R.layout.fragment_graphfragment, container, false);
        formate = new SimpleDateFormat("dd-MM-yyyy");
        cal = Calendar.getInstance();
        graphml = view.findViewById(R.id.graphholder);
            dato= view.findViewById(R.id.dato);
        mlday = view.findViewById(R.id.mldata);
        weightday = view.findViewById(R.id.kgdata);
        graphkg = view.findViewById(R.id.chartkg);
        dato.setText("Den: " + formate.format(cal.getTime()));
            add= view.findViewById(R.id.plusbut);
            add.setOnClickListener(this);

        createdata();
        createkggraph();
        createmlgraph();







            return view;
        }


    @Override
    public void onClick(View view) {
        if(view==add){

            væskreg.show( getActivity().getSupportFragmentManager().beginTransaction(),"hej");
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        dato.setText("Den: " + dates.get((int) e.getX()));
        mlday.setText(Float.toString(e.getY()));
        temp = Float.toString(dummyvalueskg[((int) e.getX())]);
        weightday.setText(temp);
        graphkg.centerViewTo(e.getX(), 1f, YAxis.AxisDependency.LEFT);
        graphkg.highlightValue(h);


    }

    @Override
    public void onNothingSelected() {
        graphkg.highlightValue(null);
    }


    public void createdata() {
        for (int i = 0; i <= 29; i++) {
            dates.add(formate.format(cal.getTime()));
            cal.add(Calendar.DATE, -1);
        }
        Collections.reverse(dates);
        for (int i = 0; i < dummyvaluesml.length; i++) {
            datapoints.add(new BarEntry((float) i, dummyvaluesml[i]));
        }
        for (int i = 0; i < dummyvaluesml.length; i++) {
            datapoints2.add(new Entry((float) i, dummyvalueskg[i]));
        }
    }


    public void createmlgraph() {
        BarDataSet mlset = new BarDataSet(datapoints, "Væskeindtag");
        bardata = new BarData(mlset);
        bardata.setBarWidth(0.7f);
        graphml.setData(bardata);
        graphml.getDescription().setEnabled(false);
        xAxisml = graphml.getXAxis();
        xAxisml.setValueFormatter(getformatter());
        xAxisml.setGranularity(1f);
        graphml.setTouchEnabled(true);
        graphml.getAxisLeft().setEnabled(false);
        graphml.setOnChartValueSelectedListener(this);
        graphml.setVisibleXRangeMaximum(7);
        graphml.setVisibleXRangeMinimum(7);
        graphml.centerViewTo(30.5f, 1f, YAxis.AxisDependency.RIGHT);
        graphml.getAxisRight().setDrawGridLines(false);
        graphml.getAxisLeft().setDrawGridLines(false);
        xAxisml.setDrawGridLines(false);
        graphml.highlightValue(29, 0);
        graphml.invalidate();
        graphml.setDrawBorders(true);
    }

    public void createkggraph() {
        kgdata = new LineData();
        LineDataSet kgset = new LineDataSet(datapoints2, "kg");
        kgset.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        kgset.setLineWidth(5f);
        kgset.setHighlightLineWidth(3f);
        kgset.setHighLightColor(Color.BLUE);
        kgdata.addDataSet(kgset);
        graphkg.setData(kgdata);
        graphkg.getDescription().setEnabled(false);
        xAxiskg = graphkg.getXAxis();
        xAxiskg.setSpaceMax(0.3f);
        xAxiskg.setSpaceMin(0.2f);
        graphkg.getAxis(YAxis.AxisDependency.LEFT).setAxisMinimum(68);
        graphkg.getAxis(YAxis.AxisDependency.LEFT).setGranularity(1);
        graphkg.getAxisRight().setDrawGridLines(false);
        graphkg.getAxisLeft().setDrawGridLines(false);
        xAxiskg.setDrawGridLines(false);
        xAxiskg.setValueFormatter(getformatter());
        xAxiskg.setGranularity(1f);
        graphkg.setVisibleXRangeMaximum(4);
        graphkg.setVisibleXRangeMinimum(4);
        graphkg.setTouchEnabled(false);
        graphkg.invalidate();
        graphkg.setDrawBorders(true);
        graphkg.getAxisRight().setEnabled(false);
    }

    public IAxisValueFormatter getformatter() {
        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dates.toArray(new String[dates.size()])[(int) value];
            }

        };
        return formatter;
    }


}





