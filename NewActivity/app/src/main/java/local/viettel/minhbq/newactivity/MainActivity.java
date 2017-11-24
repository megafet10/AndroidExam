package local.viettel.minhbq.newactivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btnShow2ndActivity;
    private Button btnShowLoginActivity;
    private Button btnShowListviewActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onClickChangeActivityListener();
        onClickLoginActivityListener ();
        onLickListviewActivityListener ();
    }

    public void onClickChangeActivityListener() {
        btnShow2ndActivity = (Button) findViewById(R.id.buttonShow2ndActivity);

        btnShow2ndActivity.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent("local.viettel.minhbq.newactivity.SecondActivity");
                        startActivity(intent);
                    }
                }
        );
    }

    public void onClickLoginActivityListener () {
        btnShowLoginActivity = (Button) findViewById(R.id.btnLoginExam);

        btnShowLoginActivity.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intentLogin = new Intent("local.viettel.minhbq.newactivity.LoginActivity");
                        startActivity (intentLogin);
                    }
                }
        );
    }

    public void onLickListviewActivityListener () {
        btnShowListviewActivity = (Button) findViewById(R.id.btnListviewExam);
        btnShowListviewActivity.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intentListview = new Intent("local.viettel.minhbq.newactivity.ListViewActivity");
                        startActivity(intentListview);

                    }
                }
        );
    }
}
