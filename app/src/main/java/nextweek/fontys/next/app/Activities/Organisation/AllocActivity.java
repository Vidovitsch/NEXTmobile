package nextweek.fontys.next.app.Activities.Organisation;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import nextweek.fontys.next.R;
import nextweek.fontys.next.app.Activities.ScanActivity;
import nextweek.fontys.next.app.Data.DBManipulatorOrg;

public class AllocActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private final static int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private DBManipulatorOrg manipulator = null;
    private ProgressDialog progressDialog = null;
    private ArrayList<Integer> groupLocations = null;

    private TextView npLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alloc);

        //Fetching data needed for allocating QR-codes
        manipulator = new DBManipulatorOrg(this);
        showFetchProgress();
    }

    @Override
    public void handleResult(Result result) {

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

    public void setupViews() {
        progressDialog.dismiss();

        npLocation = (TextView) findViewById(R.id.alloc_txtLocation);
        setEventHandlers();
        groupLocations = manipulator.getGroupLocations();
    }

    private void setEventHandlers() {
        //Set event handler for adding location number
        Button btnAdd = (Button) findViewById(R.id.alloc_btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer nextLocation = getNextLocation(Integer.valueOf(String.valueOf(npLocation.getText())));
                npLocation.setText(String.valueOf(nextLocation));
            }
        });
        //Set event handler for removing location number
        Button btnRemove = (Button) findViewById(R.id.alloc_btnRemove);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer prevLocation = getPreviousLocation(Integer.valueOf(String.valueOf(npLocation.getText())));
                npLocation.setText(String.valueOf(prevLocation));
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
        int index = groupLocations.indexOf(oldLocation);
        if (index == groupLocations.size() - 1) {
            index = -1;
        }

        return groupLocations.get(index + 1);
    }

    private int getPreviousLocation(Integer oldLocation) {
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
}
