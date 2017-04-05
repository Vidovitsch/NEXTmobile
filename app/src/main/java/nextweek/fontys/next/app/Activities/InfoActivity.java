package nextweek.fontys.next.app.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import nextweek.fontys.next.R;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        String groupLocation = getIntent().getStringExtra("groupLocation");

        TextView location = (TextView) findViewById(R.id.txtLocation);
        location.setText(groupLocation);
    }
}
