package measurelet.tablet;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;
import androidx.navigation.NavController;
import measurelet.tablet.Model.Patient;

public class RecyclerviewAdapteren extends RecyclerView.Adapter<RecyclerviewAdapteren.MyViewHolder> implements View.OnClickListener {

    private boolean sortani = true;
    private NavController navC;
    private RecyclerView re;
    private Typeface font;
    private Bundle bund;
    private Context con;
    private int prevpos;
    private ArrayList<Patient> bedlist;

    private int getPrevpos() {
        return prevpos;
    }
    private void setPrevpos(int prevpos) {
        this.prevpos = prevpos;
    }

    public RecyclerviewAdapteren(ArrayList<Patient> beds,RecyclerView re, NavController nav, Typeface font, Bundle bundle, Context coni) {
        this.re=re;
        this.navC=nav;
        this.font=font;
        this.bund=bundle;
        this.con=coni;
        this.bedlist=beds;


    }

    //On click for changing bed.
    @Override
    public void onClick(View view) {


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
            bund.putString("Id",bedlist.get(itemPosition).getUuid());
            Graphfragment graph = new Graphfragment();
            graph.setArguments(bund);


            if (itemPosition < prev) {

                navC.navigate(R.id.action_enterleft,bund);
            }
            if (itemPosition >= prev) {
                navC.navigate(R.id.action_enterright,bund);

            }
        }
        if (bedlist.isEmpty()) {
            navC.popBackStack();
        }

    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView bednumber;
        private CheckBox checker;
        private ImageButton delete;

        private MyViewHolder(View v) {
            super(v);
            bednumber = v.findViewById(R.id.bedview);
            bednumber.setTypeface(font);
            checker = v.findViewById(R.id.bedcheck);
            checker.setOnClickListener(this);
            delete= v.findViewById(R.id.deletebutton);
            delete.setOnClickListener(this);


        }
        //On click for delete at marking.
        @Override
        public void onClick(View view) {
            int positionen = getAdapterPosition();


            if (view == checker) {
                if (!bedlist.get(positionen).getChecked()) {
                    checker.setChecked(true);
                    bedlist.get(positionen).setChecked(true);
                } else  if (bedlist.get(positionen).getChecked()){
                    checker.setChecked(false);
                    bedlist.get(positionen).setChecked(false);
                }
            }
            if(view==delete){
                final AlertDialog.Builder builder = new AlertDialog.Builder(con);
                builder.setTitle("Er du sikker på du vil slette?");
                builder.setCancelable(true);
                builder.setPositiveButton("OK", (dialog, which) -> {

                    bedlist.remove(positionen);
                    notifyItemRemoved(positionen);
                    notifyItemRangeChanged(positionen, bedlist.size());
                    navC.navigate(R.id.action_global_startFragment);



                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                builder.show();
            }

        }


        private void bind(int position) {
            if (bedlist.get(position).getChecked()) {
                checker.setChecked(true);
            } else if(!bedlist.get(position).getChecked()){
                checker.setChecked(false);
            }

        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listlayout, viewGroup, false);
        v.setOnClickListener(this);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        myViewHolder.bednumber.setText("Seng " + bedlist.get(position).getBedNum());
        myViewHolder.bind(position);
    }


    @Override
    public int getItemCount() {
        if (bedlist== null) {
            return 0;
        }
        return bedlist.size();
    }

}
