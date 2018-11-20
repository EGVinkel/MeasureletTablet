package measurelet.tablet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class graphfragment extends Fragment implements View.OnClickListener {
    private View view;
    private ArrayList<Date> dates=new ArrayList<>();
    private ArrayList<DataPoint> datapoints= new ArrayList<>();
    private int[] dummyvalues= new int[]{1700,1800,2000,1500,1600,1700,1800,1500,2400,2200,2800,2300,2000,3000,1100,2100,1900,2400,2600,2700,2300,1900,1800,2000,3000,1900,3000,2900,2100,2400};
    private ImageButton add;
    private DialogFragment væskreg = new DialogFragment();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            view= inflater.inflate(R.layout.fragment_graphfragment, container, false);
            GraphView graph = view.findViewById(R.id.graphholder);
            Calendar calendar = Calendar.getInstance();
            for(int i=1;i<=30;i++){

                dates.add(calendar.getTime());
                calendar.add(Calendar.DATE, 1);
            }
            int counter=0;
            for(Date d:dates){
               datapoints.add(new DataPoint(d,dummyvalues[counter]));
               counter++;
            }

            add= view.findViewById(R.id.plusbut);
            add.setOnClickListener(this);


            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(datapoints.toArray(new DataPoint[datapoints.size()]));
            graph.addSeries(series);


            graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));

            graph.getViewport().setBackgroundColor(R.color.colorPrimaryDark);
            graph.getViewport().setMinX(dates.get(0).getTime());
            graph.getViewport().setMaxX(dates.get(4).getTime());
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getGridLabelRenderer().setHorizontalAxisTitle("Dage");
            graph.getGridLabelRenderer().setVerticalAxisTitle("ml");
            graph.getViewport().setScrollable(true);
            graph.getViewport().setScrollableY(true);
            graph.getGridLabelRenderer().setHumanRounding(false);

            return view;
        }


    @Override
    public void onClick(View view) {
        if(view==add){

            væskreg.show( getActivity().getSupportFragmentManager().beginTransaction(),"hej");
        }
    }
}





