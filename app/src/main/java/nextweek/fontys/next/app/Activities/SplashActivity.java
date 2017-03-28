package nextweek.fontys.next.app.Activities;

import android.Manifest;
import android.animation.Animator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.codetail.animation.ViewAnimationUtils;
import nextweek.fontys.next.R;

public class SplashActivity extends AppCompatActivity {

    private final static int ANIMATION_TIME_CONST = 1000;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private boolean signed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);;

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("SplashActivity", "onAuthStateChanged:signed_in:" + user.getEmail());
                    signed = true;
                } else {
                    // User is signed out
                    Log.d("SplashActivity", "onAuthStateChanged:signed_out");
                    signed = false;
                }
            }
        };

        final ImageView logo = (ImageView) findViewById(R.id.splash_logo);
        //Set invisible to hide this image until the reveal is done
        logo.setVisibility(View.INVISIBLE);

        final View view = findViewById(R.id.splash_reveal);
        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                startCircleReveal(view);

                //Start logo animation after a delay
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startLogoReveal(logo);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (checkConnectivity()) {
                                    if (signed) {
                                        openScanActivity();
                                    } else {
                                        openLogActivity();
                                    }
                                } else {
                                    showAlertDialog("No Internet", "You have no internet connectivity");
                                }
                            }
                        }, ANIMATION_TIME_CONST);
                    }
                }, ANIMATION_TIME_CONST);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * Shows an animation of a circle expanding through the screen.
     * @param view to animate.
     */
    private void startCircleReveal(View view) {
        // get the center for the clipping circle
        int cx = view.getRight();
        int cy = view.getBottom();

        // get the final radius for the clipping circle
        int dx = Math.max(cx, view.getWidth() - cx);
        int dy = Math.max(cy, view.getHeight() - cy);
        float finalRadius = (float) Math.hypot(dx, dy) + 50;

        // Android native animator
        Animator animator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(ANIMATION_TIME_CONST);
        animator.start();
    }

    /**
     * Shows an animation of the logo fading in.
     * @param view to animate.
     */
    private void startLogoReveal(View view) {
        //Set invisible view visible again
        view.setVisibility(View.VISIBLE);

        //Start fade in animation
        Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        view.startAnimation(animationFadeIn);
    }

    /**
     * Switches to the scan activity.
     */
    private void openScanActivity() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

    /**
     * Switches to the log activity.
     */
    private void openLogActivity() {
        Intent intent = new Intent(this, LogActivity.class);
        startActivity(intent);
    }

    /**
     * Chekcs the conenctivity to the internet
     * @return true if connected, else false
     */
    private boolean checkConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        else {
            return false;
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
                        finish();
                        System.exit(0);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


}
