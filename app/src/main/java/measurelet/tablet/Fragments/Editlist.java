package measurelet.tablet.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import measurelet.tablet.AppData;
import measurelet.tablet.BedRecyclerviewAdapter;
import measurelet.tablet.MainActivity;
import measurelet.tablet.Model.Intake;
import measurelet.tablet.Model.Patient;
import measurelet.tablet.Model.Weight;
import measurelet.tablet.R;

public class Editlist extends DialogFragment {
    private String selection,id,date;
    private RecyclerView re;
    private int recyclersize;
    private Bundle b = new Bundle();

    public Editlist() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View editlist = inflater.inflate(R.layout.fragment_editlist, container, false);
        re = editlist.findViewById(R.id.editreg);
        id= getArguments().getString("Id", "");
        selection= getArguments().getString("selection", "");
        date=getArguments().getString("date", "");
        Patient curpat= MainActivity.patientsHashmap.get(id);
        b.putString("date",date);
        b.putString("Id",id);
        b.putString("selection",selection);

        if(selection.equalsIgnoreCase("Intake")){
            recyclersize=curpat.getIntakesForDate(LocalDate.parse(date)).size();
        }
        if(selection.equalsIgnoreCase("Weight")){
            recyclersize=curpat.getWeightForDate(LocalDate.parse(date)).size();
        }

        re.setAdapter(new EditRecycler(id,date,selection));
        re.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        re.setLayoutManager(mLayoutManager);
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        re.addItemDecoration(itemDecor);




        return editlist;
    }

    public class EditRecycler extends RecyclerView.Adapter<EditRecycler.MyViewHolder>{
        String id, date, type;
        ArrayList<Intake> intakes;
        ArrayList<Weight> weights;



        EditRecycler(String id,String date,String type){
            this.id=id;
            this.date=date;
            this.type= type;


        }




        @NonNull
        @Override
        public EditRecycler.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.editdaily, viewGroup, false);
            Patient curpat= MainActivity.patientsHashmap.get(id);
            if(type.equalsIgnoreCase("Weight")){
                weights=new ArrayList<>(curpat.getWeightForDate(LocalDate.parse(date)));
                i=weights.size();
            }

            if(type.equalsIgnoreCase("Intake")){
                intakes=new ArrayList<>(curpat.getIntakesForDate(LocalDate.parse(date)));
                i=intakes.size();
            }



            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            org.threeten.bp.format.DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm");
            String placetype="";
            String placeml="";
            String placetime="";
            if(type.equalsIgnoreCase("Intake")){
                 placetype =  intakes.get(position).getType();
                 placeml=    intakes.get(position).getSize()+"";
                 placetime=  intakes.get(position).getDateTime().format(format);
            }
            if(type.equalsIgnoreCase("Weight")){
                 placeml=    weights.get(position).getWeightKG()+" Kg";
                 placetime=  weights.get(position).getDatetime().format(format);
            }

            holder.mængde.setText(placeml);
            holder.tid.setText(placetime);
            holder.type.setText(placetype);

        }

        @Override
        public int getItemCount() {
           return recyclersize;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView tid;
            TextView mængde;
            TextView type;


            private MyViewHolder(View v) {
                super(v);

                tid = v.findViewById(R.id.tid_daily);
                mængde = v.findViewById(R.id.ml_daily);
                type = v.findViewById(R.id.type_daily);

                ImageButton edit_button = v.findViewById(R.id.edit_daily);

                edit_button.setOnClickListener(this);
            }


            @Override
            public void onClick(View view) {
                System.out.println(getAdapterPosition());

                b.putInt("position", getAdapterPosition());
                DialogFragment dialog = new edit_liquid();
                dialog.setArguments(b);
                dialog.show(getFragmentManager(),"dialog");
                dismiss();
            }
        }
    }


}
