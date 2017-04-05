package nextweek.fontys.next.app.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import nextweek.fontys.next.R;
import nextweek.fontys.next.app.Data.DBManipulator;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private final static int MY_PERMISSIONS_REQUEST_CAMERA = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

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
        DBManipulator.getInstance().setScanned();
        showAlertDialog(result.getText(), result.getText());
        setContentView(R.layout.activity_scan);

        ImageView btn = (ImageView) findViewById(R.id.btn_scan);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                askCameraPermission();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mScannerView != null) {
            mScannerView.stopCamera();
        }
    }

    private void askCameraPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(ScanActivity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ScanActivity.this,
                    Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(ScanActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mScannerView = new ZXingScannerView(ScanActivity.this);
                    setContentView(mScannerView);
                    mScannerView.setResultHandler(ScanActivity.this);
                    mScannerView.startCamera(0);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
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
}
