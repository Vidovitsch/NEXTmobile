package nextweek.fontys.next.app.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import nextweek.fontys.next.R;
import nextweek.fontys.next.app.Data.DBManipulator;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        String groupLocation = getIntent().getStringExtra("groupLocation");

        TextView location = (TextView) findViewById(R.id.txtLocation);
        location.setText(groupLocation);

        Button btnUncheck = (Button) findViewById(R.id.btnUncheck);
        btnUncheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog("Are you sure?", "Are you sure you want to un-check your group from this table?");
            }
        });
    }

    /**
     * Switches to the scan activity.
     */
    public void openScanActivity() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Shows an alert dialog
     * @param title of the dialog
     * @param message of the dialog
     */
    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        openScanActivity();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) { }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
