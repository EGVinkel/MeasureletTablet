package measurelet.tablet;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<Integer> beds = new ArrayList<>();
    private RecyclerView re;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Bundle argbund;
    private ImageButton clear, add;
    private Typeface font;
    private boolean sortani = true;
    private int prevpos;
    private Integer inputbed;
    private NavController navC;
    private AlertDialog ad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        font = Typeface.createFromAsset(getAssets(), "font/Helvetica.ttf");
        for (int i = 1; i <= 15; i++) {
            beds.add(i);
        }
        if (argbund == null) {
            argbund = new Bundle();
        }
        navC=Navigation.findNavController(findViewById(R.id.nav_host));


        add = findViewById(R.id.addbed);
        clear = findViewById(R.id.clearcheck);
        clear.setOnClickListener(this);
        add.setOnClickListener(this);
        re= findViewById(R.id.bedlist);
        re.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        re.setLayoutManager(mLayoutManager);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        re.addItemDecoration(itemDecor);
        mAdapter = new MyAdapter(beds);
        re.setAdapter(mAdapter);


    }

    public int getPrevpos() {
        return prevpos;
    }

    public void setPrevpos(int prevpos) {
        this.prevpos = prevpos;
    }

    @Override
    public void onClick(View view) {
        if (view == clear) {
            mAdapter = new MyAdapter(beds);
            re.setAdapter(mAdapter);


        }
        if (add == view) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Tast sengenummer");

            builder.setCancelable(true);

            final EditText input = new EditText(this);

            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (input.getText().length()==0) {


                        Handler handler = new Handler();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                            ad.show();
                            }
                        });
                    }
                    if(input.getText().length()>0) {
                        inputbed = Integer.parseInt(input.getText().toString());
                        beds.add(inputbed);
                        Collections.sort(beds);
                        mAdapter = new MyAdapter(beds);
                        re.setAdapter(mAdapter);
                    }

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            ad=builder.show();

        }

    }


    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements View.OnClickListener, View.OnLongClickListener {
        private ArrayList<Integer> bedlist;

        private SparseBooleanArray statusArray = new SparseBooleanArray();

        public MyAdapter(ArrayList<Integer> beds) {
            this.bedlist = beds;

        }


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
            if (!beds.isEmpty()) {
                argbund.putInt("nr", beds.get(itemPosition));
                Graphfragment graph = new Graphfragment();
                graph.setArguments(argbund);


                if (itemPosition < prev) {

                    navC.navigate(R.id.action_enterleft,argbund);
                }
                if (itemPosition >= prev) {
                    navC.navigate(R.id.action_enterright,argbund);

                }
            }
            if (beds.isEmpty()) {
               navC.popBackStack();
            }

        }

        @Override
        public boolean onLongClick(View view) {
            int position = re.getChildLayoutPosition(view);
            beds.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, beds.size());
            navC.navigate(R.id.action_global_startFragment);
            mAdapter = new MyAdapter(beds);
            re.setAdapter(mAdapter);
            return true;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView bednumber;
            public CheckBox checker;

            public MyViewHolder(View v) {
                super(v);
                bednumber = v.findViewById(R.id.bedview);
                bednumber.setTypeface(font);
                checker = v.findViewById(R.id.bedcheck);
                checker.setOnClickListener(this);


            }

            @Override
            public void onClick(View view) {
                int positionen = getAdapterPosition();


                if (view == checker) {
                    if (!statusArray.get(positionen, false)) {
                        checker.setChecked(true);
                        statusArray.put(positionen, true);
                    } else {
                        checker.setChecked(false);
                        statusArray.put(positionen, false);
                    }
                }

            }


            private void bind(int position) {
                if (!statusArray.get(position, false)) {
                    checker.setChecked(false);
                } else {
                    checker.setChecked(true);
                }

            }
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listlayout, viewGroup, false);
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);

            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
            myViewHolder.bednumber.setText("Seng " + bedlist.get(position));
            myViewHolder.bind(position);
        }


        @Override
        public int getItemCount() {
            if (bedlist == null) {
                return 0;
            }
            return bedlist.size();
        }

    }


}











