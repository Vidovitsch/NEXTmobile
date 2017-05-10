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
import nextweek.fontys.next.app.Data.DBManipulatorStu;

public class InfoActivity extends AppCompatActivity {

    private TextView txtLocation = null;
    private DBManipulatorStu manipulator = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        txtLocation = (TextView) findViewById(R.id.txtLocation);

        manipulator = DBManipulatorStu.getInstance();
        manipulator.setInfoActivity(this);

        updateGroupLocation();

        Button btnUncheck = (Button) findViewById(R.id.btnUncheck);
        btnUncheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog("Are you sure?", "Are you sure you want to un-check your group from this table?");
            }
        });
    }

    /**
     * Updates the current group location.
     * If the location is changed to a positive number it changes the text within the screen.
     * If the location is changed to 0 the activity switches to the scan activity.
     */
    public void updateGroupLocation() {
        int groupLocation = manipulator.getCurrentGroupLocation();
        if (groupLocation != 0) {
            txtLocation.setText(String.valueOf(groupLocation));
        } else {
            openScanActivity();
        }
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
                        DBManipulatorStu.getInstance().setNewGroupLocation(0);
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
