package measurelet.tablet;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;
import measurelet.tablet.Fragments.Graphfragment;
import measurelet.tablet.Model.Patient;

public class RecyclerviewAdapteren extends RecyclerView.Adapter<RecyclerviewAdapteren.MyViewHolder> {

    private boolean sortani = true;
    private NavController navC;
    private RecyclerView re;
    private Context con;
    private int prevpos;
    private ArrayList<Patient> bedlist;
    int selectedPosition = -1;


    private int getPrevpos() {
        return prevpos;
    }
    private void setPrevpos(int prevpos) {
        this.prevpos = prevpos;
    }

    public RecyclerviewAdapteren(ArrayList<Patient> beds, RecyclerView re, NavController nav, Context coni) {
        this.re = re;
        this.navC = nav;
        this.con = coni;
        this.bedlist = beds;


    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        myViewHolder.bednumber.setText("Seng " + bedlist.get(position).getBedNum());
        myViewHolder.bind(position);

        if (selectedPosition == position) {
            myViewHolder.itemView.setBackgroundColor(Color.parseColor("#406699d1"));
        } else
            myViewHolder.itemView.setBackgroundColor(Color.parseColor("#00000000"));

        //On click for changing bed and marking
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPosition = position;
                notifyDataSetChanged();

                AppData.ani = true;

                int itemPosition = re.getChildLayoutPosition(view);

                if (sortani) {
                    setPrevpos(-1);
                    sortani = false;

                }
                int prev = getPrevpos();
                if (itemPosition == -1) {
                    itemPosition = itemPosition + 1;
                    setPrevpos(getPrevpos() + 1);
                }
                setPrevpos(itemPosition);
                if (!bedlist.isEmpty()) {
                    AppData.theb.putString("Id", bedlist.get(itemPosition).getUuid());
                    Graphfragment graph = new Graphfragment();
                    graph.setArguments(AppData.theb);


                    if (itemPosition < prev) {

                        navC.navigate(R.id.action_enterleft, AppData.theb);
                    }
                    if (itemPosition >= prev) {
                        navC.navigate(R.id.action_enterright, AppData.theb);

                    }
                }
                if (bedlist.isEmpty()) {
                    navC.popBackStack();
                }
            }
        });



    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listlayout, viewGroup, false);
        //v.setOnClickListener(this);

        return new MyViewHolder(v);
    }

    @Override
    public int getItemCount() {
        if (bedlist == null) {
            return 0;
        }
        return bedlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView bednumber;
        private CheckBox checker;


        private MyViewHolder(View v) {
            super(v);
            bednumber = v.findViewById(R.id.bedview);
            checker = v.findViewById(R.id.bedcheck);
            checker.setOnClickListener(this);


        }
        //On click for delete at marking.
        @Override
        public void onClick(View view) {
            int positionen = getAdapterPosition();


            if (view == checker) {
                if (!bedlist.get(positionen).getChecked()) {
                    checker.setChecked(true);
                    bedlist.get(positionen).setChecked(true);
                } else if (bedlist.get(positionen).getChecked()) {
                    checker.setChecked(false);
                    bedlist.get(positionen).setChecked(false);
                }
            }

        }


        private void bind(int position) {
            if (bedlist.get(position).getChecked()) {
                checker.setChecked(true);
            } else if (!bedlist.get(position).getChecked()) {
                checker.setChecked(false);
            }

        }
    }

}
