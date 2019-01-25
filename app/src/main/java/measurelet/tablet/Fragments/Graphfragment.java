package measurelet.tablet.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.icu.text.DecimalFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import measurelet.tablet.AppData;
import measurelet.tablet.Factories.GraphDataFactory;
import measurelet.tablet.Factories.WeightFactory;
import measurelet.tablet.Formatters.MinXAxisValueFormatter;
import measurelet.tablet.Formatters.Valueformatter;
import measurelet.tablet.MainActivity;
import measurelet.tablet.Model.Patient;
import measurelet.tablet.Model.Weight;
import measurelet.tablet.R;


public class Graphfragment extends Fragment implements View.OnClickListener, OnChartValueSelectedListener {
    private View view;
    private List<BarEntry> mldata = new ArrayList<>();
    private List<BarEntry> outputdata = new ArrayList<>();
    private List<Entry> kgdatas = new ArrayList<>();
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
    private String Id;
    private AlertDialog ad;
    private int weight;
    private DateTimeFormatter formate;

    @SuppressLint("StaticFieldLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_graphfragment, container, false);

        setHasOptionsMenu(true);
        setMenuVisibility(true);

        Id = getArguments().getString("Id");
        pat = MainActivity.patientsHashmap.get(Id);
        if (pat == null) {
            NavHostFragment.findNavController(this).popBackStack();
        }

        if (pat != null) {
            b.putString("Id", Id);
            //   df = new DecimalFormat("#.##");
            patient = view.findViewById(R.id.getname);
            if (!pat.getName().isEmpty()) {
                patient.setText(pat.getName());
            }
            formate = DateTimeFormatter.ofPattern("dd-MM-yyyy");
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
            if (!MainActivity.patientsHashmap.get(Id).getWeights().isEmpty()) {
                setupKgViews();
            }
            if (!MainActivity.patientsHashmap.get(Id).getRegistrations().isEmpty()) {

                setupMlViews();


            }

            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                view.setVisibility(View.INVISIBLE);
                ((MainActivity) getActivity()).loadAnimation().playAnimation();
            }, 500);

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onCancelled() {
                    super.onCancelled();
                }

                @Override
                protected Void doInBackground(Void... voids) {
                    kgdatas = GraphDataFactory.getKgEntries(Id);
                    mldata = GraphDataFactory.MlEntries(Id);
                    createdata();

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);

                    handler.removeCallbacksAndMessages(null);

                    if ((getActivity()) != null) {
                        ((MainActivity) getActivity()).loadAnimation().cancelAnimation();
                    }

                    if (view != null) {

                        view.setVisibility(View.VISIBLE);


                        if (!MainActivity.patientsHashmap.get(Id).getWeights().isEmpty() && !kgdatas.isEmpty()) {
                            addDataKg();
                        }
                        if (!MainActivity.patientsHashmap.get(Id).getRegistrations().isEmpty() && !outputdata.isEmpty() && !mldata.isEmpty()) {
                            addDataMl();
                        }
                    }
                }
            }.execute();


        }
        return view;
    }


    @Override
    public void onClick(View view) {
        if (view == add) {
            DialogFragment dialog = new AddliquidFragment();
            dialog.setArguments(b);
            dialog.show(getFragmentManager(), "dialog");

        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        int tempin = 0;
        int tempout = 0;

        if (kgdatas.size() < e.getX() && !kgdatas.isEmpty()) {
            weightday.setText(kgdatas.get(kgdatas.size() - 1).getY() + "kg");
            graphkg.centerViewTo(kgdatas.size() - 1, 1f, YAxis.AxisDependency.LEFT);

        } else if (kgdatas.size() > (int) e.getX() && !MainActivity.patientsHashmap.get(Id).getWeights().isEmpty()) {
            weightday.setText(kgdatas.get((int) e.getX()).getY() + "kg");
            graphkg.centerViewTo(e.getX(), 1f, YAxis.AxisDependency.LEFT);
            graphkg.highlightValue(h);


        }

        if (mldata.size() < e.getX() && !mldata.isEmpty()) {
            tempin = (int) mldata.get(mldata.size() - 1).getY();
            tempout = (int) mldata.get(mldata.size() - 1).getY();
            intake.setText(tempin + "ml");
            output.setText(tempout + "ml");
            graphml.centerViewTo(mldata.size() - 1, 1f, YAxis.AxisDependency.LEFT);
        } else if (mldata.size() > (int) e.getX() && !MainActivity.patientsHashmap.get(Id).getRegistrations().isEmpty()) {
            tempin = (int) mldata.get((int) e.getX()).getY();
            tempout = (int) outputdata.get((int) e.getX()).getY();
            intake.setText(tempin + "ml ");
            output.setText(tempout + "ml ");
            dif.setText((tempin - tempout) + "ml ");
            graphml.centerViewTo(e.getX(), 1f, YAxis.AxisDependency.LEFT);
            graphml.highlightValue(h);

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


    public void setupMlViews() {
        xAxisml = graphml.getXAxis();
        xAxisml.setSpaceMax(1.1f);
        xAxisml.setSpaceMin(0.1f);
        graphml.setDoubleTapToZoomEnabled(false);
        graphml.setTouchEnabled(true);
        graphml.getDescription().setEnabled(false);
        graphml.getAxisRight().setEnabled(false);
        graphml.setOnChartValueSelectedListener(this);
        graphml.setExtraBottomOffset(20);
        graphml.getAxisRight().setDrawGridLines(false);
        graphml.getAxisLeft().setTextSize(20);
        graphml.getAxisLeft().setDrawGridLines(false);
        graphml.getDescription().setPosition(195f, 670f);
        Legend l = graphml.getLegend();
        l.setTextSize(18);
        l.setFormSize(18);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        graphml.setHighlightFullBarEnabled(true);
        //graphml.setDrawBorders(true);
        graphml.getDescription().setTextSize(24);




    }

    public void setupKgViews() {
        graphkg.setDoubleTapToZoomEnabled(false);
        MarkerImage markoer = new MarkerImage(getActivity(), R.drawable.ic_lens_black_24dp);
        graphkg.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getRealSize(size);
        float offx = ((size.x * 0.7f) * 0.65f) / 48;
        markoer.setOffset(-offx, -offx);
        xAxiskg = graphkg.getXAxis();
        xAxiskg.setSpaceMax(0.9f);
        xAxiskg.setSpaceMin(0.1f);
        graphkg.getDescription().setEnabled(false);
        graphkg.setMarker(markoer);
        graphkg.setExtraBottomOffset(28);
        graphkg.getAxisLeft().setSpaceBottom(30);
        graphkg.getAxisLeft().setSpaceTop(30);
        graphkg.getAxisRight().setDrawGridLines(false);
        graphkg.getAxisLeft().setDrawGridLines(false);
        graphkg.setOnChartValueSelectedListener(this);
        graphkg.getAxisLeft().setTextSize(20);
        graphkg.centerViewTo(kgdatas.size(), 1f, YAxis.AxisDependency.LEFT);
        graphkg.setTouchEnabled(true);
        Legend l = graphkg.getLegend();
        l.setTextSize(18);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setFormSize(18);
        graphkg.getAxisRight().setEnabled(false);


    }


    public void addDataMl() {

        BarDataSet mlset = new BarDataSet(mldata, "");
        mlset.setStackLabels(new String[]{"pr. OS", "IV"});
        mlset.setColors(ColorTemplate.rgb("b3e5fc"), ColorTemplate.rgb("FF64B5F6"));

        BarDataSet mlset2 = new BarDataSet(outputdata, "  I ml");
        mlset2.setStackLabels(new String[]{"Urin", "Afføring"});
        mlset2.setColors(ColorTemplate.rgb("215981"), ColorTemplate.rgb("90caf9"));
        ArrayList<String> dates = new ArrayList<>();
        for (LocalDate lolc : GraphDataFactory.dateSorter(Id, "Intake")) {
            dates.add(formate.format(lolc));
        }
        bardata = new BarData(mlset, mlset2);
        graphml.setData(bardata);

        xAxisml.setDrawGridLines(false);
        xAxisml.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisml.setCenterAxisLabels(true);
        xAxisml.setGranularity(1f);

        xAxisml.setTextSize(20);
        xAxisml.setValueFormatter(new MinXAxisValueFormatter(dates));
        graphml.setVisibleXRangeMaximum(5);
        bardata.setValueTextSize(16f);
        bardata.setValueFormatter(new Valueformatter(0));
        bardata.setBarWidth(0.4f);
        if (AppData.ani) {
            graphml.animateY(1500);
            AppData.ani = true;
        }
        graphml.groupBars(0f, 0.2f, 0f);
        graphml.centerViewTo(mldata.size(), 1f, YAxis.AxisDependency.RIGHT);
        graphml.highlightValue(mldata.size(), 0);
        graphml.invalidate();
    }

    public void addDataKg() {

        LineDataSet kgset = new LineDataSet(kgdatas, "Vægt (kg)");
        kgset.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        kgset.setLineWidth(4f);
        kgset.setHighlightLineWidth(3f);
        kgset.setHighLightColor(Color.BLUE);
        kgset.setCircleRadius(6);
        kgset.setDrawHighlightIndicators(false);
        kgdata = new LineData(kgset);
        graphkg.setData(kgdata);

        xAxiskg.setGranularity(1f);
        kgdata.setValueTextSize(16f);

        graphkg.setVisibleXRangeMaximum(3);

        xAxiskg.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxiskg.setTextSize(18);
        xAxiskg.setDrawGridLines(false);
        graphkg.getAxisLeft().setAxisMaximum(graphkg.getYMax() + 20);
        graphkg.getAxisLeft().setAxisMinimum(graphkg.getYMin() - 20);


        ArrayList<String> dates = new ArrayList<>();
        for (LocalDate lolc : GraphDataFactory.dateSorter(Id, "Weight")) {
            dates.add(formate.format(lolc));
        }
        xAxiskg.setValueFormatter(new MinXAxisValueFormatter(dates));
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

                AppData.DB_REFERENCE.child("patients").child(Id).removeValue();
                NavHostFragment.findNavController(this).navigate(R.id.startFragment);


            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();

        }

        if(item.getItemId()==R.id.addweight){
            if (!(MainActivity.patientsHashmap.get(Id).getWeightForDate(LocalDate.now()) == null)) {
                Toast.makeText(getActivity(),"Der er registreret vægt i dag", Toast.LENGTH_LONG).show();
                return false;
            }
            Context con = getActivity();
            assert con != null;
            final AlertDialog.Builder builder = new AlertDialog.Builder(con);
            builder.setCancelable(true);
            final TextInputEditText inputweight = new TextInputEditText(con);
            final int maxLength = 3;
            inputweight.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
            inputweight.setInputType(InputType.TYPE_CLASS_NUMBER);
            TextInputLayout tbed = new TextInputLayout(con);
            tbed.addView(inputweight);
            tbed.setHint("Vægt");
            tbed.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);

            builder.setView(tbed);



            builder.setPositiveButton("OK", (dialog, which) -> {

                if (Objects.requireNonNull(inputweight.getText()).length() == 0) {


                    Handler handler = new Handler();
                    handler.post(() -> ad.show());
                }
                if (inputweight.getText().length() > 0) {
                    this.weight = Integer.parseInt(inputweight.getText().toString());
                    Weight weight = new Weight(this.weight);
                    WeightFactory.InsertNewWeight(weight, Id);

                }

            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
              ad=builder.show();


        }
        if(item.getItemId()==R.id.editweight||item.getItemId()==R.id.editliq){
            DialogFragment dialog = new Date_Selecter_Fragment();
            if(item.getItemId()==R.id.editweight){
                if((!MainActivity.patientsHashmap.get(Id).getWeights().isEmpty())){
                    b.putString("selection","Weight");
                    dialog.setArguments(b);
                    dialog.show(getFragmentManager(), "cal");
                }
                else Toast.makeText(getActivity(),"Ingen registreringer af vægt", Toast.LENGTH_LONG).show();


            }
            if(item.getItemId()==R.id.editliq){
                if (!MainActivity.patientsHashmap.get(Id).getRegistrations().isEmpty()) {
                    b.putString("selection", "Intake");
                    dialog.setArguments(b);
                    dialog.show(getFragmentManager(), "cal");
                }
                else Toast.makeText(getActivity(),"Ingen registreringer af væske", Toast.LENGTH_LONG).show();
            }

        }


        return false;
    }


}


