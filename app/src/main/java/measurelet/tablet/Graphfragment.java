package measurelet.tablet;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.icu.text.DecimalFormat;
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
import com.github.mikephil.charting.components.MarkerImage;
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


public class Graphfragment extends Fragment implements View.OnClickListener, OnChartValueSelectedListener {
    private View view;
    private ArrayList<String> dates = new ArrayList<>();
    private ArrayList<BarEntry> datapoints = new ArrayList<>();
    private ArrayList<Entry> datapoints2 = new ArrayList<>();
    private int[] dummyvaluesml = new int[]{1700, 1800, 2000, 1500, 1600, 1700, 1800, 1500, 2400, 2200, 2800, 2300, 2000, 3000, 1100, 2100, 1900, 2400, 2600, 2700, 2300, 1900, 1800, 2000, 3000, 1900, 3000, 2900, 2100, 2400};
    private float[] dummyvalueskg = new float[]{70.5f, 71, 70.7f, 73, 72, 70.7f, 72, 70.7f, 72, 72, 70.7f, 70.5f, 70.7f, 70.5f, 70.5f, 71.3f, 71.3f, 71.3f, 71.5f, 71.7f, 71.6f, 72.3f, 73.3f, 75.3f, 69.4f, 69.3f, 74.3f, 72.3f, 73.3f, 70.3f};
    private ImageButton add;
    private DialogFragment væskreg = new DialogFragment();
    private TextView dato, mlday, weightday, header, nyreg;
    private XAxis xAxisml, xAxiskg;
    private BarChart graphml;
    private LineChart graphkg;
    private String temp;
    private BarData bardata;
    private LineData kgdata;
    private SimpleDateFormat formate;
    private Calendar cal;
    private Typeface font;
    private DecimalFormat df;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_graphfragment, container, false);
        font = Typeface.createFromAsset(getActivity().getAssets(), "font/Helvetica.ttf");
        header = view.findViewById(R.id.bedheader);
        int temp = getArguments().getInt("nr");
        header.setText("Seng " + temp);
        nyreg = view.findViewById(R.id.nyreg);
        nyreg.setTypeface(font);
        df = new DecimalFormat("#.##");

        formate = new SimpleDateFormat("dd-MM-yyyy");
        cal = Calendar.getInstance();
        graphml = view.findViewById(R.id.graphholder);
        dato = view.findViewById(R.id.dato);
        dato.setTypeface(font);
        mlday = view.findViewById(R.id.mldata);
        mlday.setTypeface(font);
        weightday = view.findViewById(R.id.kgdata);
        weightday.setTypeface(font);
        graphkg = view.findViewById(R.id.chartkg);
        dato.setText("Den: " + formate.format(cal.getTime()));
        add = view.findViewById(R.id.plusbut);
        add.setOnClickListener(this);

        createdata(getArguments().getInt("nr"));

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
        mlday.setText(df.format(datapoints.get((int) e.getX()).getY()));
        weightday.setText(df.format(datapoints2.get((int) e.getX()).getY()));
        graphkg.centerViewTo(e.getX(), 1f, YAxis.AxisDependency.LEFT);
        graphkg.highlightValue(h);
        graphml.centerViewTo(e.getX(), 1f, YAxis.AxisDependency.LEFT);
        graphml.highlightValue(h);

    }

    @Override
    public void onNothingSelected() {
        graphkg.highlightValue(null);
        graphml.highlightValue(null);
    }


    public void createdata(int j) {
        for (int i = 0; i <= 29; i++) {
            dates.add(formate.format(cal.getTime()));
            cal.add(Calendar.DATE, -1);
        }
        Collections.reverse(dates);
        for (int i = 0; i < dummyvaluesml.length; i++) {
            datapoints.add(new BarEntry((float) i, (dummyvaluesml[i]) + (j * 10)));
        }
        for (int i = 0; i < dummyvaluesml.length; i++) {
            datapoints2.add(new Entry((float) i, (dummyvalueskg[i]) - j));
        }
    }


    public void createmlgraph() {
        BarDataSet mlset = new BarDataSet(datapoints, "Væskeindtag (ml)");
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
        graphml.setDrawBorders(true);
        graphml.getDescription().setTypeface(font);
        graphml.invalidate();
    }

    public void createkggraph() {
        kgdata = new LineData();
        LineDataSet kgset = new LineDataSet(datapoints2, "Vægt (kg)");
        kgset.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        kgset.setLineWidth(4f);
        kgset.setHighlightLineWidth(3f);
        kgset.setHighLightColor(Color.BLUE);
        kgset.setCircleRadius(6);
        kgset.setDrawHighlightIndicators(false);
        kgdata.addDataSet(kgset);
        this.graphkg.setData(kgdata);
        MarkerImage markoer = new MarkerImage(getActivity(), R.drawable.ic_lens_black_24dp);
        graphkg.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getRealSize(size);
        System.out.println(size);
        float offx = ((size.x * 0.7f) * 0.65f) / 48;
        System.out.println(offx);
        markoer.setOffset(-offx, -offx);
        this.graphkg.setMarker(markoer);
        this.graphkg.getDescription().setEnabled(false);
        xAxiskg = this.graphkg.getXAxis();
        xAxiskg.setSpaceMax(0.3f);
        xAxiskg.setSpaceMin(0.3f);
        this.graphkg.getAxis(YAxis.AxisDependency.LEFT).setGranularity(1);
        this.graphkg.getAxisRight().setDrawGridLines(false);
        this.graphkg.getAxisLeft().setDrawGridLines(false);
        xAxiskg.setDrawGridLines(false);
        xAxiskg.setValueFormatter(getformatter());
        xAxiskg.setGranularity(1f);
        graphkg.setOnChartValueSelectedListener(this);
        this.graphkg.setVisibleXRangeMaximum(3);
        this.graphkg.setVisibleXRangeMinimum(3);
        this.graphkg.setTouchEnabled(true);
        this.graphkg.setDrawBorders(true);
        this.graphkg.getAxisRight().setEnabled(false);
        this.graphkg.getDescription().setTypeface(font);
        this.graphkg.invalidate();


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





