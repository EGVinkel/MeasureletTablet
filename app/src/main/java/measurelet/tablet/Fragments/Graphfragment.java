package measurelet.tablet.Fragments;

import android.graphics.Color;
import android.graphics.Point;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import measurelet.tablet.AppData;
import measurelet.tablet.Factories.GraphDataFactory;
import measurelet.tablet.Formatters.MinXAxisValueFormatter;
import measurelet.tablet.Formatters.Valueformatter;
import measurelet.tablet.MainActivity;
import measurelet.tablet.Model.Patient;
import measurelet.tablet.R;


public class Graphfragment extends Fragment implements View.OnClickListener, OnChartValueSelectedListener {
    private View view;
    private ArrayList<BarEntry> mldata = new ArrayList<>();
    private ArrayList<BarEntry> outputdata = new ArrayList<>();
    private ArrayList<Entry> kgdatas = new ArrayList<>();
    private MaterialButton add;
    private TextView intake, output, weightday, patient, bed, dif;
    private XAxis xAxisml, xAxiskg;
    private BarChart graphml;
    private LineChart graphkg;
    private BarData bardata;
    private LineData kgdata;
    private DecimalFormat df;
    private Bundle b = new Bundle();
    private Patient pat;
    private String temp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_graphfragment, container, false);

        setHasOptionsMenu(true);
        setMenuVisibility(true);

        temp = getArguments().getString("Id");
        pat = MainActivity.patientsHashmap.get(temp);
        b.putString("Id", temp);
        df = new DecimalFormat("#.##");
        patient = view.findViewById(R.id.getname);
        patient.setText(pat.getName());
        bed = view.findViewById(R.id.getbed);
        bed.setText(pat.getBedNum() + "");
        dif = view.findViewById(R.id.dif);

        graphml = view.findViewById(R.id.graphholder);
        intake = view.findViewById(R.id.intake);
        output = view.findViewById(R.id.output);

        weightday = view.findViewById(R.id.kgdata);
        graphkg = view.findViewById(R.id.chartkg);
        add = view.findViewById(R.id.plusbut);
        add.setOnClickListener(this);


        //get databasedata
        kgdatas = GraphDataFactory.getKgEntries(temp);
        mldata = GraphDataFactory.getMlEntries(temp);


        //Precautions to avoid crashing when no regs have been made
        if (!MainActivity.patientsHashmap.get(temp).getWeights().isEmpty()) {
            createkggraph();
        }
        if (!MainActivity.patientsHashmap.get(temp).getRegistrations().isEmpty()) {
            //Ugly but functional solution to avoid graph being drawn before dummy data is created;
            Boolean wait = true;
            while (wait) {
                createdata();
                if (!outputdata.isEmpty()) {
                    wait = false;
                }
            }
            createmlgraph();


        }


        return view;
    }


    @Override
    public void onClick(View view) {
        if (view == add) {
            DialogFragment dialog = new MyDialogFragment();
            dialog.setArguments(b);
            dialog.show(getFragmentManager(), "dialog");

        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        int tempin = (int) mldata.get((int) e.getX()).getY();
        int tempout = (int) outputdata.get((int) e.getX()).getY();


        intake.setText(tempin + "ml ");
        output.setText(tempout + "ml ");
        dif.setText((tempin - tempout) + "ml ");
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
        }

    }

    @Override
    public void onNothingSelected() {
        graphkg.highlightValue(null);
        graphml.highlightValue(null);
    }


    public void createdata() {


        for (int i = 0; i < mldata.size(); i++) {

            outputdata.add(new BarEntry((float) i, new float[]{(float) ((mldata.get(i).getY() - mldata.get(i).getY() / 2) - 100 * ((Math.random()))), (float) ((Math.random() + 1) * 300)}));
        }
    }


    public void createmlgraph() {
        BarDataSet mlset = new BarDataSet(mldata, "");
        mlset.setStackLabels(new String[]{"pr. OS", "IV"});
        mlset.setColors(ColorTemplate.rgb("b3e5fc"), ColorTemplate.rgb("FF64B5F6"));

        BarDataSet mlset2 = new BarDataSet(outputdata, "  I ml");
        mlset2.setStackLabels(new String[]{"Urin", "Afføring"});
        mlset2.setColors(ColorTemplate.rgb("215981"), ColorTemplate.rgb("90caf9"));

        bardata = new BarData(mlset, mlset2);
        bardata.setValueTextSize(16f);
        bardata.setValueFormatter(new Valueformatter(0));
        bardata.setBarWidth(0.4f);
        graphml.setData(bardata);
        xAxisml = graphml.getXAxis();
        xAxisml.setValueFormatter(new MinXAxisValueFormatter(GraphDataFactory.dateSorter(temp, "Intake")));
        xAxisml.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisml.setCenterAxisLabels(true);
        xAxisml.setGranularity(1f);
        xAxisml.setSpaceMax(1.1f);
        xAxisml.setSpaceMin(0.1f);
        xAxisml.setTextSize(20);
        graphml.setDoubleTapToZoomEnabled(false);
        graphml.setTouchEnabled(true);
        graphml.getDescription().setEnabled(false);
        graphml.getAxisRight().setEnabled(false);
        graphml.setOnChartValueSelectedListener(this);
        graphml.setVisibleXRangeMaximum(5);
        graphml.setExtraBottomOffset(20);
        graphml.getAxisRight().setDrawGridLines(false);
        graphml.getAxisLeft().setTextSize(20);
        graphml.getAxisLeft().setDrawGridLines(false);
        xAxisml.setDrawGridLines(false);
        graphml.getDescription().setPosition(195f, 670f);
        Legend l = graphml.getLegend();
        l.setTextSize(18);
        l.setFormSize(18);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        graphml.setHighlightFullBarEnabled(true);
        //graphml.setDrawBorders(true);
        graphml.getDescription().setTextSize(24);
        graphml.groupBars(0f, 0.2f, 0f);
        graphml.centerViewTo(mldata.size(), 1f, YAxis.AxisDependency.RIGHT);
        graphml.highlightValue(mldata.size(), 0);

        if (AppData.ani) {
            graphml.animateY(1500);
            AppData.ani = true;
        }

        graphml.invalidate();

    }

    public void createkggraph() {


        kgdata = new LineData();
        LineDataSet kgset = new LineDataSet(kgdatas, "Vægt (kg)");
        kgset.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        kgset.setLineWidth(4f);
        kgset.setHighlightLineWidth(3f);
        kgset.setHighLightColor(Color.BLUE);
        kgset.setCircleRadius(6);
        kgset.setDrawHighlightIndicators(false);
        kgdata.addDataSet(kgset);
        graphkg.setDoubleTapToZoomEnabled(false);
        kgdata.setValueTextSize(16f);
        graphkg.setData(kgdata);
        MarkerImage markoer = new MarkerImage(getActivity(), R.drawable.ic_lens_black_24dp);
        graphkg.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getRealSize(size);
        float offx = ((size.x * 0.7f) * 0.65f) / 48;
        markoer.setOffset(-offx, -offx);
        graphkg.setMarker(markoer);
        graphkg.getDescription().setEnabled(false);
  /*      graphkg.getAxisLeft().setAxisMaximum(50);
        graphkg.getAxisLeft().setAxisMinimum(100);*/
        xAxiskg = graphkg.getXAxis();
        xAxiskg.setSpaceMax(0.5f);
        xAxiskg.setSpaceMin(0.1f);
        xAxiskg.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxiskg.setTextSize(18);
        graphkg.setExtraBottomOffset(28);
        graphkg.getAxisLeft().setSpaceBottom(30);
        graphkg.getAxisLeft().setSpaceTop(30);
        graphkg.getAxisRight().setDrawGridLines(false);
        graphkg.getAxisLeft().setDrawGridLines(false);
        xAxiskg.setDrawGridLines(false);
        xAxiskg.setValueFormatter(new MinXAxisValueFormatter(GraphDataFactory.dateSorter(temp, "Weight")));
        xAxiskg.setGranularity(1f);
        graphkg.setOnChartValueSelectedListener(this);
        graphkg.getAxisLeft().setTextSize(20);
        graphkg.setVisibleXRangeMaximum(3);
        graphkg.setTouchEnabled(true);
        // graphkg.setDrawBorders(true);
        Legend l = graphkg.getLegend();
        l.setTextSize(18);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setFormSize(18);
        graphkg.getAxisRight().setEnabled(false);
        graphkg.invalidate();


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.deletebed) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Er du sikker på du vil slette?");
            builder.setCancelable(true);
            builder.setPositiveButton("OK", (dialog, which) -> {

                AppData.DB_REFERENCE.child("patients").child(temp).removeValue();
                NavHostFragment.findNavController(this).navigate(R.id.startFragment);


            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();

        }


        return false;
    }

}

