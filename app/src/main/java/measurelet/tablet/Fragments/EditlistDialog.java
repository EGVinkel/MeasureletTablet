package measurelet.tablet.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import measurelet.tablet.MainActivity;
import measurelet.tablet.Model.Intake;
import measurelet.tablet.Model.Patient;
import measurelet.tablet.R;

public class EditlistDialog extends DialogFragment {
    private String selection,id,date;
    private RecyclerView re;
    private Bundle b = new Bundle();

    public EditlistDialog() {

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
        re.setAdapter(new EditRecycler(curpat.getIntakesForDate(LocalDate.parse(date))));
        re.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        re.setLayoutManager(mLayoutManager);
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        re.addItemDecoration(itemDecor);

        return editlist;
    }

    public class EditRecycler extends RecyclerView.Adapter<EditRecycler.MyViewHolder>{
        List<Intake> intakes;


        EditRecycler(List<Intake> intakes) {
            this.intakes = intakes;


        }




        @NonNull
        @Override
        public EditRecycler.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.editlist, viewGroup, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            org.threeten.bp.format.DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm");
            String placetype = intakes.get(position).getType();
            String placeml = intakes.get(position).getSize() + "";
            String placetime = intakes.get(position).getDateTime().format(format);



            holder.mængde.setText(placeml);
            holder.tid.setText(placetime);
            holder.type.setText(placetype);

        }

        @Override
        public int getItemCount() {
            return intakes.size();
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

                b.putInt("position", getAdapterPosition());
                DialogFragment dialog = new EditLiquidDialog();
                dialog.setArguments(b);
                dialog.show(getFragmentManager(),"dialog");
                dismiss();
            }
        }
    }


}
