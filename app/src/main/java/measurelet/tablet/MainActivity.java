package measurelet.tablet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import static android.support.v7.widget.LinearLayoutManager.HORIZONTAL;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> beds= new ArrayList<>();
    private RecyclerView re;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentholder,new graphfragment()).commit();
        for(int i=1;i<20;i++){
            beds.add("Seng "+i);
        }
        re= findViewById(R.id.bedlist);
        re.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        re.setLayoutManager(mLayoutManager);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        re.addItemDecoration(itemDecor);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(beds);
        re.setAdapter(mAdapter);


    }


    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private ArrayList<String> bedlist;

        public MyAdapter(ArrayList<String> beds) {
            this.bedlist=beds;

        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView bednumber;

            public MyViewHolder(View v) {
                super(v);
                bednumber= v.findViewById(R.id.bedview);

            }
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listlayout, viewGroup, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
            myViewHolder.bednumber.setText(bedlist.get(position));
        }



        @Override
        public int getItemCount() {
            return bedlist.size();
        }


    }

}











