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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerImage;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import androidx.navigation.fragment.NavHostFragment;


public class Graphfragment extends Fragment implements View.OnClickListener, OnChartValueSelectedListener {
    private View view;
    private ArrayList<String> dates = new ArrayList<>();
    private ArrayList<BarEntry> datapoints = new ArrayList<>();
    private ArrayList<BarEntry> datapoints3 = new ArrayList<>();
    private ArrayList<Entry> datapoints2 = new ArrayList<>();
    private int[] dummyvaluesml = new int[]{1700, 1800, 2000, 1500, 1600, 1700, 1800, 1500, 2400, 2200, 2800, 2300, 2000, 3000, 1100, 2100, 1900, 2400, 2600, 2700, 2300, 1900, 1800, 2000, 3000, 1900, 3000, 2900, 2100, 2400};
    private float[] dummyvalueskg = new float[]{70.5f, 71, 70.7f, 73, 72, 70.7f, 72, 70.7f, 72, 72, 70.7f, 70.5f, 70.7f, 70.5f, 70.5f, 71.3f, 71.3f, 71.3f, 71.5f, 71.7f, 71.6f, 72.3f, 73.3f, 75.3f, 69.4f, 69.3f, 74.3f, 72.3f, 73.3f, 70.3f};
    private ImageButton add;
    private TextView intake,output, weightday, header, nyreg;
    private XAxis xAxisml, xAxiskg;
    private BarChart graphml;
    private LineChart graphkg;
    private BarData bardata;
    private LineData kgdata;
    private SimpleDateFormat formate;
    private Calendar cal;
    private Typeface font;
    private DecimalFormat df;
    private Bundle b= new Bundle();

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
        intake = view.findViewById(R.id.intake);
        intake.setTypeface(font);
        output=view.findViewById(R.id.output);
        output.setTypeface(font);

        weightday = view.findViewById(R.id.kgdata);
        weightday.setTypeface(font);
        graphkg = view.findViewById(R.id.chartkg);
        add = view.findViewById(R.id.plusbut);
        add.setOnClickListener(this);
        b.putInt("nr",temp);
        createdata(getArguments().getInt("nr"));

        createkggraph();
        createmlgraph();


            return view;
        }


    @Override
    public void onClick(View view) {
        if(view==add){
            NavHostFragment.findNavController(this).popBackStack();
            NavHostFragment.findNavController(this).navigate(R.id.action_graphfragment_to_dialogFragment,b);
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        intake.setText(df.format(datapoints.get((int) e.getX()).getY())+"ml ");
        weightday.setText(df.format(datapoints2.get((int) e.getX()).getY())+"kg");
        output.setText(df.format(datapoints3.get((int) e.getX()).getY())+"ml ");
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
            //(dummyvaluesml[i]) + (j * 10))
            datapoints.add(new BarEntry((float) i, new float[]{dummyvaluesml[i] + (j * 10)-1000,dummyvaluesml[i] + (j * 10)-1100f}));
            datapoints3.add(new BarEntry((float) i, new float[]{dummyvaluesml[i] + (j * 10)-1200,dummyvaluesml[i] + (j * 10)-1000f}));
        }
        for (int i = 0; i < dummyvaluesml.length; i++) {
            datapoints2.add(new Entry((float) i, (dummyvalueskg[i]) - j));
        }
    }


    public void createmlgraph() {

        BarDataSet mlset = new BarDataSet(datapoints, "");
        mlset.setStackLabels(new String[]{"pr. OS","IV"});
        mlset.setColors(ColorTemplate.rgb("b3e5fc"),ColorTemplate.rgb("FF64B5F6"));

        BarDataSet mlset2 = new BarDataSet(datapoints3, "  I ml");
        mlset2.setStackLabels(new String[]{"Urin","Dræn"});
        mlset2.setColors(ColorTemplate.rgb("FFFFCC80"),ColorTemplate.rgb("FFF57F17"));

        bardata = new BarData(mlset,mlset2);
        bardata.setValueTextSize(16f);
        bardata.setBarWidth(0.4f);
        graphml.setData(bardata);
        xAxisml = graphml.getXAxis();
        xAxisml.setValueFormatter(new MinXAxisValueFormatter(dates));
        xAxisml.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxisml.setCenterAxisLabels(true);
        xAxisml.setGranularity(1f);
        xAxisml.setSpaceMax(1.1f);
        xAxisml.setSpaceMin(0.1f);
        xAxisml.setTextSize(20);
        graphml.setTouchEnabled(true);
        graphml.getDescription().setEnabled(false);
        graphml.getAxisRight().setEnabled(false);
        graphml.setOnChartValueSelectedListener(this);
        graphml.setVisibleXRangeMaximum(7);
        graphml.setExtraTopOffset(10);
        graphml.getAxisRight().setDrawGridLines(false);
        graphml.getAxisLeft().setTextSize(20);
        graphml.getAxisLeft().setDrawGridLines(false);
        xAxisml.setDrawGridLines(false);
        graphml.getDescription().setPosition(195f,670f);
        Legend l=graphml.getLegend();
        l.setTextSize(18);
        l.setFormSize(18);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        graphml.setHighlightFullBarEnabled(true);
        graphml.setDrawBorders(true);
        graphml.getDescription().setTypeface(font);
        graphml.getDescription().setTextSize(24);
        graphml.groupBars(0f, 0.2f, 0f);
        graphml.centerViewTo(29f, 1f, YAxis.AxisDependency.RIGHT);
        graphml.highlightValue(29, 0);
        graphml.animateY(1500);
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
        kgdata.setValueTextSize(16f);
        this.graphkg.setData(kgdata);
        MarkerImage markoer = new MarkerImage(getActivity(), R.drawable.ic_lens_black_24dp);
        graphkg.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getRealSize(size);
        float offx = ((size.x * 0.7f) * 0.65f) / 48;
        markoer.setOffset(-offx, -offx);
        graphkg.setMarker(markoer);
        graphkg.getDescription().setEnabled(false);
        xAxiskg = this.graphkg.getXAxis();
        xAxiskg.setSpaceMax(0.3f);
        xAxiskg.setSpaceMin(0.3f);
        xAxiskg.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxiskg.setTextSize(20);
        graphkg.setExtraTopOffset(10);
        graphkg.getAxisLeft().setSpaceBottom(30);
        graphkg.getAxisLeft().setSpaceTop(30);
        graphkg.getAxis(YAxis.AxisDependency.LEFT).setGranularity(1);
        graphkg.getAxisRight().setDrawGridLines(false);
        graphkg.getAxisLeft().setDrawGridLines(false);
        xAxiskg.setDrawGridLines(false);
        xAxiskg.setValueFormatter(new MinXAxisValueFormatter(dates));
        xAxiskg.setGranularity(1f);
        graphkg.setOnChartValueSelectedListener(this);
        graphkg.getAxisLeft().setTextSize(20);
        graphkg.setVisibleXRangeMaximum(3);
        graphkg.setVisibleXRangeMinimum(3);
        graphkg.setTouchEnabled(true);
        graphkg.setDrawBorders(true);
        Legend l= graphkg.getLegend();
        l.setTextSize(18);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setFormSize(18);
        graphkg.getAxisRight().setEnabled(false);
        graphkg.getDescription().setTypeface(font);
        graphkg.invalidate();


    }

}





