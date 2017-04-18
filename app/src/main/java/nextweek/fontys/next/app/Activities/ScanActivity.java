package nextweek.fontys.next.app.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import nextweek.fontys.next.R;
import nextweek.fontys.next.app.Data.DBManipulator;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private final static int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private  DBManipulator manipulator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        manipulator = DBManipulator.getInstance();
        manipulator.setScanActivity(this);

        ImageView btn = (ImageView) findViewById(R.id.btn_scan);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                askCameraPermission();
            }
        });
    }

    @Override
    public void handleResult(Result result) {
        String nr = result.getText();
        if (nr.length() < 4 && isNumeric(nr)) {
            manipulator.validateScan(this, Integer.valueOf(result.getText()));
        } else {
            loadScanContext(-1);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mScannerView != null) {
            mScannerView.stopCamera();
        }
    }

    /**
     * Validates the made scan.
     * If it's valid the screen changes to the info screen.
     * If it's invalid an alert dialog is shown.
     * @param valid scan or not
     * @param groupIDTaken group that is sitting currently at the scanned number
     */
    public void validateScan(boolean valid, int groupIDTaken) {
        if (valid) {
            openInfoActivity();
        } else {
            loadScanContext(groupIDTaken);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mScannerView = new ZXingScannerView(ScanActivity.this);
                    setContentView(mScannerView);
                    mScannerView.setResultHandler(ScanActivity.this);
                    mScannerView.startCamera(0);

                }
            }
        }
    }

    /**
     * Opens the info activity
     */
    public void openInfoActivity() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadScanContext(int groupIDTaken) {
        setContentView(R.layout.activity_scan);
        ImageView btn = (ImageView) findViewById(R.id.btn_scan);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                askCameraPermission();
            }
        });

        if (groupIDTaken != -1) {
            showAlertDialog("Table taken", "This table is taken by group " + groupIDTaken);
        } else {
            showAlertDialog("Invalid QR-code", "You scanned an invalid QR-code");
        }
    }

    private void askCameraPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(ScanActivity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (!ActivityCompat.shouldShowRequestPermissionRationale(ScanActivity.this,
                    Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(ScanActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

            }
        }
    }

    /**
     * Shows an alert dialog
     * Closes application on cancel
     * @param title of the dialog
     * @param message of the dialog
     */
    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private boolean isNumeric(String s) {
        return s.matches("[-+]?\\d*\\.?\\d+");
    }
}
