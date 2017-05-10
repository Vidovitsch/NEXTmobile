package nextweek.fontys.next.app.Activities.Organisation;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import nextweek.fontys.next.R;
import nextweek.fontys.next.app.Data.DBManipulatorOrg;

public class AllocActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private boolean compeleted = false;

    private ZXingScannerView mScannerView;
    private final static int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private DBManipulatorOrg manipulator = null;
    private ProgressDialog progressDialog = null;
    private Integer selectedLocation;

    private TextView npLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alloc);
        //Fetching data needed for allocating QR-codes
        manipulator = new DBManipulatorOrg(this);
        if (manipulator.getGroupLocations().isEmpty()) {
            compeleted = true;
            setContentView(R.layout.activity_alloc_completed);
        } else {
            setContentView(R.layout.activity_alloc);
            showFetchProgress();
        }
    }

    @Override
    public void handleResult(Result result) {
        String code = result.getText();
        manipulator.validateScan(code, selectedLocation);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mScannerView != null) {
            mScannerView.stopCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mScannerView = new ZXingScannerView(AllocActivity.this);
                    setContentView(mScannerView);
                    mScannerView.setResultHandler(AllocActivity.this);
                    mScannerView.startCamera(0);
                }
            }
        }
    }

    public void handleScan(boolean successful, String message) {
        setContentView(R.layout.activity_alloc);
        if (successful) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            if (manipulator.getGroupLocations().isEmpty()) {
                compeleted = true;
                setContentView(R.layout.activity_alloc_completed);
            }
        } else {
            showAlertDialog(message);
        }
    }

    public void setupViews() {
        if (!compeleted) {
            progressDialog.dismiss();

            npLocation = (TextView) findViewById(R.id.alloc_txtLocation);
            setEventHandlers();
        }
    }

    private void setEventHandlers() {
        //Set event handler for adding location number
        Button btnAdd = (Button) findViewById(R.id.alloc_btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer nextLocation = getNextLocation(Integer.valueOf(String.valueOf(npLocation.getText())));
                npLocation.setText(String.valueOf(nextLocation));
                selectedLocation = nextLocation;
            }
        });
        //Set event handler for removing location number
        Button btnRemove = (Button) findViewById(R.id.alloc_btnRemove);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer prevLocation = getPreviousLocation(Integer.valueOf(String.valueOf(npLocation.getText())));
                npLocation.setText(String.valueOf(prevLocation));
                selectedLocation = prevLocation;
            }
        });
        //Set event handler for scanning a qr-code
        Button btnScan = (Button) findViewById(R.id.alloc_btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermission();
            }
        });
    }

    /**
     * Shows an progress dialog while fetching group locations
     */
    private void showFetchProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();
    }

    private Integer getNextLocation(Integer oldLocation) {
        ArrayList<Integer> groupLocations = manipulator.getGroupLocations();
        int index = groupLocations.indexOf(oldLocation);
        if (index == groupLocations.size() - 1) {
            index = -1;
        }

        return groupLocations.get(index + 1);
    }

    private int getPreviousLocation(Integer oldLocation) {
        ArrayList<Integer> groupLocations = manipulator.getGroupLocations();
        int index = groupLocations.indexOf(oldLocation);
        if (index == 0) {
            index = groupLocations.size();
        }

        return groupLocations.get(index - 1);
    }

    private void askCameraPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(AllocActivity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (!ActivityCompat.shouldShowRequestPermissionRationale(AllocActivity.this,
                    Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(AllocActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }
    }

    /**
     * Shows an alert dialog
     * Closes application on cancel
     * @param message of the dialog
     */
    private void showAlertDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Invalid QR-code")
                .setMessage(message)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
