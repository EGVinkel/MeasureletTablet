package measurelet.tablet;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerImage;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.navigation.fragment.NavHostFragment;
import measurelet.tablet.Factories.GraphDataFactory;
import measurelet.tablet.Formatters.MinXAxisValueFormatter;
import measurelet.tablet.Formatters.Valueformatter;
import measurelet.tablet.Model.Patient;


public class Graphfragment extends Fragment implements View.OnClickListener, OnChartValueSelectedListener {
    private View view;
    private ArrayList<BarEntry> mldata = new ArrayList<>();
    private ArrayList<BarEntry> outputdata = new ArrayList<>();
    private ArrayList<Entry> kgdatas = new ArrayList<>();
    private ImageButton add;
    private TextView intake, output, weightday, header, nyreg;
    private XAxis xAxisml, xAxiskg;
    private CombinedChart graphml;
    private LineChart graphkg;
    private BarData bardata;
    private LineData kgdata;
    private Calendar cal;
    private Typeface font;
    private DecimalFormat df;
    private Bundle b = new Bundle();
    private Patient pat;
    private String temp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_graphfragment, container, false);
        font = Typeface.createFromAsset(getActivity().getAssets(), "font/Helvetica.ttf");
        header = view.findViewById(R.id.bedheader);
        temp = getArguments().getString("Id");
        pat = MainActivity.patientsHashmap.get(temp);
        b.putString("Id", temp);
        header.setText("Seng " + pat.getBedNum() + ", " + pat.getName());
        nyreg = view.findViewById(R.id.nyreg);
        nyreg.setTypeface(font);
        df = new DecimalFormat("#.##");

        cal = Calendar.getInstance();
        graphml = view.findViewById(R.id.graphholder);
        intake = view.findViewById(R.id.intake);
        intake.setTypeface(font);
        output = view.findViewById(R.id.output);
        output.setTypeface(font);

        weightday = view.findViewById(R.id.kgdata);
        weightday.setTypeface(font);
        graphkg = view.findViewById(R.id.chartkg);
        add = view.findViewById(R.id.plusbut);
        add.setOnClickListener(this);


        //get databasedata
        kgdatas = GraphDataFactory.getKgEntries(temp);
        mldata = GraphDataFactory.getMlEntries(temp);
        //Create dummy output data, using a while loop to avoid graphs being drawn before the outputdata is done, should probably be changed, but pending if we are going to take output data into acount or not.
        Boolean wait = true;
        while (wait) {
            createdata();
            if (outputdata.size() == mldata.size()) {
                wait = false;
            }
        }
        //Precautions to avoid crashing when no regs have been made
        if (!MainActivity.patientsHashmap.get(temp).getWeights().isEmpty()) {
         //   createkggraph();
        }
        if (!MainActivity.patientsHashmap.get(temp).getRegistrations().isEmpty()) {
            createmlgraph();
        }


        return view;
    }


    @Override
    public void onClick(View view) {
        if (view == add) {
            NavHostFragment.findNavController(this).navigate(R.id.action_graphfragment_to_dialogFragment, b);
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

      /*  intake.setText((int) mldata.get((int) e.getX()).getY() + "ml ");
        output.setText((int) outputdata.get((int) e.getX()).getY() + "ml ");
        graphml.centerViewTo(e.getX(), 1f, YAxis.AxisDependency.LEFT);
        graphml.highlightValue(h);


        if (!kgdatas.isEmpty()) {
            if (kgdatas.size() < e.getX()) {
                weightday.setText(df.format(kgdatas.get(kgdatas.size() - 1).getY()) + "kg");
                graphkg.centerViewTo(kgdatas.size() - 1, 1f, YAxis.AxisDependency.LEFT);

            } else {
                weightday.setText(df.format(kgdatas.get((int) e.getX()).getY()) + "kg");
                graphkg.centerViewTo(e.getX(), 1f, YAxis.AxisDependency.LEFT);

            }
            graphkg.highlightValue(h);
        }*/

    }

    @Override
    public void onNothingSelected() {
        graphml.highlightValue(null);
    }


    public void createdata() {


        for (int i = 0; i < mldata.size(); i++) {

            outputdata.add(new BarEntry((float) i, new float[]{(float) ((mldata.get(i).getY() - mldata.get(i).getY() / 2) - 100 * ((Math.random()))), (float) ((Math.random() + 1) * 300)}));
        }
    }


    public void createmlgraph() {

        //kg data
        LineDataSet kgset = new LineDataSet(kgdatas, "Vægt (kg)");
        kgset.setAxisDependency(YAxis.AxisDependency.RIGHT);
        kgset.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        kgset.setLineWidth(4f);
        kgset.setHighlightLineWidth(3f);
        kgset.setHighLightColor(Color.BLUE);
        kgset.setCircleRadius(6);
        kgset.setDrawHighlightIndicators(false);
        kgset.setValueTextSize(16f);
        kgdata= new LineData(kgset);

        //ml data
        BarDataSet mlset = new BarDataSet(mldata, "");
        mlset.setStackLabels(new String[]{"pr. OS", "IV"});
        mlset.setColors(ColorTemplate.rgb("b3e5fc"), ColorTemplate.rgb("FF64B5F6"));
        BarDataSet mlset2 = new BarDataSet(outputdata, "  I ml");
        mlset2.setStackLabels(new String[]{"Urin", "Afføring"});
        mlset2.setColors(ColorTemplate.rgb("FFFFCC80"), ColorTemplate.rgb("FFF57F17"));
        bardata = new BarData(mlset, mlset2);
        bardata.setValueTextSize(16f);
        bardata.setValueFormatter(new Valueformatter(0));
        bardata.setBarWidth(0.4f);
        bardata.groupBars(0f, 0.2f, 0f);



        //Combine the data  and set
        CombinedData combi = new CombinedData();
        combi.setValueFormatter(new Valueformatter(0));
        combi.setData(kgdata);
        combi.setData(bardata);
        graphml.setData(combi);

        //x axis adjustments
        xAxisml = graphml.getXAxis();
        if(GraphDataFactory.dateSorter(temp, "Intake").size()>=GraphDataFactory.dateSorter(temp, "Weight").size()){
            xAxisml.setValueFormatter(new MinXAxisValueFormatter(GraphDataFactory.dateSorter(temp, "Intake")));
        }
        if(GraphDataFactory.dateSorter(temp, "Intake").size()<GraphDataFactory.dateSorter(temp, "Weight").size()){
            xAxisml.setValueFormatter(new MinXAxisValueFormatter(GraphDataFactory.dateSorter(temp, "Weight")));
        }

        xAxisml.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisml.setCenterAxisLabels(true);
        xAxisml.setGranularity(1f);
        xAxisml.setSpaceMax(0.3f);
        xAxisml.setSpaceMin(0.3f);
        xAxisml.setTextSize(20);
        xAxisml.setDrawGridLines(false);



        graphml.getDescription().setEnabled(false);
        graphml.setDoubleTapToZoomEnabled(false);
        graphml.setTouchEnabled(true);
        graphml.getDescription().setEnabled(false);
       // graphml.setOnChartValueSelectedListener(this);
        graphml.setVisibleXRangeMaximum(3);
        graphml.setExtraBottomOffset(10);
        graphml.getAxisRight().setDrawGridLines(false);
        graphml.getAxisLeft().setTextSize(20);
        graphml.getAxisRight().setTextSize(20);
        graphml.getAxisLeft().setDrawGridLines(false);
        graphml.getDescription().setPosition(195f, 670f);

        Legend l = graphml.getLegend();
        l.setTextSize(18);
        l.setFormSize(18);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        graphml.setDrawBorders(true);
        graphml.getDescription().setTypeface(font);
        graphml.getDescription().setTextSize(24);
        graphml.centerViewTo(mldata.size(), 1f, YAxis.AxisDependency.RIGHT);


        if (AppData.ani) {
            graphml.animateY(1500);
            AppData.ani = true;
        }

        graphml.invalidate();

    }

    /*public void createkggraph() {





        xAxisml = graphml.getXAxis();
        xAxiskg.setCenterAxisLabels(true);
        xAxiskg.setSpaceMax(1.1f);
        xAxiskg.setSpaceMin(0.1f);
        xAxiskg.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxiskg.setTextSize(18);
        graphkg.setExtraTopOffset(10);
        graphkg.getAxisLeft().setSpaceBottom(30);
        graphkg.getAxisLeft().setSpaceTop(30);
        graphkg.getAxisRight().setDrawGridLines(false);
        graphkg.getAxisLeft().setDrawGridLines(false);
        //   xAxiskg.setDrawGridLines(false);
        xAxiskg.setValueFormatter(new MinXAxisValueFormatter(GraphDataFactory.dateSorter(temp, "Weight")));
        xAxiskg.setGranularity(1f);
        graphkg.setOnChartValueSelectedListener(this);
        graphkg.getAxisLeft().setTextSize(20);
        graphkg.setVisibleXRangeMaximum(7);
        graphkg.setTouchEnabled(true);
        graphkg.setDrawBorders(true);
        Legend l = graphkg.getLegend();
        l.setTextSize(18);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setFormSize(18);
        graphkg.getAxisRight().setEnabled(false);
        graphkg.getDescription().setTypeface(font);
        graphkg.invalidate();


    }*/

}





