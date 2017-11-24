package local.viettel.minhbq.newactivity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListViewActivity extends AppCompatActivity {

    private static ListView listView;
    private static String [] NAMES = {"Minh", "Giang", "Diep", "Hieu", "Nam Anh"};
    private static int [] ICONS = {R.drawable.icon1, R.drawable.icon2,R.drawable.icon3, R.drawable.icon4,R.drawable.icon5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);

        onClickListviewListener();

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    public void onClickListviewListener() {
        //Fill the content to list view
        listView = (ListView) findViewById(R.id.listviewName);
       // ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.name_list, NAMES);
        CustomListAdapter listAdapter = new CustomListAdapter(this, NAMES, ICONS);
        listView.setAdapter(listAdapter);

        //Set OnItem click
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        /*CustomListAdapter gotValue = (CustomListAdapter) listView.getItemAtPosition(position);
                        String textView = gotValue.getItem(position);*/

                        TextView textView = view.findViewById(R.id.item);
                        String txt = textView.getText().toString();
                        Toast.makeText(ListViewActivity.this, "Got " + txt, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

}
