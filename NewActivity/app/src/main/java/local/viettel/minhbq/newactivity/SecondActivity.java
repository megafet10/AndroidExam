package local.viettel.minhbq.newactivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;

public class SecondActivity extends AppCompatActivity {

    private TextClock clkText;
    private Button btnShowClock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        onClickShowClockListener ();
    }

    public void onClickShowClockListener () {
        clkText = (TextClock) findViewById(R.id.textClock);
        btnShowClock = (Button) findViewById(R.id.btnShowClk);

        btnShowClock.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (clkText.getVisibility() == TextClock.GONE) {
                            clkText.setVisibility(TextClock.VISIBLE);
                        } else {
                            clkText.setVisibility(TextClock.GONE);
                        }

                    }
                }
        );
    }
}
