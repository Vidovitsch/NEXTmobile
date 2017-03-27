package nextweek.fontys.next.app.Activities;

import android.animation.Animator;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import io.codetail.animation.ViewAnimationUtils;
import nextweek.fontys.next.R;
import nextweek.fontys.next.app.Models.PropertyHandler;

public class SplashActivity extends AppCompatActivity {

    private final static int ANIMATION_TIME_CONST = 1000;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private boolean authenticated = false;
    private boolean done = false;
    private Object lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("SplashActivity", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("SplashActivity", "onAuthStateChanged:signed_out");
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
                                checkLogProperties();
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

    private boolean signIn(String email, String password) {
        Log.d("signIn", "createUserWithEmail:onComplete LOGGINGISENFSHFHSIUEFHUISEHFUISEHFUISEHFUIH");
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("signIn", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            authenticated = true;
                        }
                        unlockFXThread();
                    }
                });

        lockFXThread();
        return authenticated;
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

    private void checkLogProperties() {
        try {
            ExecutorService pool = Executors.newFixedThreadPool(1);
            Future<String[]> futureData = pool.submit(new PropertyHandler(this));
            String[] data = futureData.get();

            if (signIn("david.de.bekker@live.nl", "asdfasdf")) {
                openScanActivity();
            } else {
                openLogActivity();
            }
        } catch (InterruptedException | ExecutionException ex) {
            Log.e("SplashActivity", "Exception", ex);
        }
    }

    /**
     * Switches to the scan activity.
     */
    private void openScanActivity() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

    private void openLogActivity() {
        Intent intent = new Intent(this, LogActivity.class);
        startActivity(intent);
    }

    /**
     * Tells a random object to wait while in a loop.
     * The loop stops, and won't cause any unnecessary cpu use.
     */
    private void lockFXThread() {
        lock = new Object();
        synchronized (lock) {
            while (!done) {
                try {
                    lock.wait();
                } catch (InterruptedException ex) {
                    Log.e("SplashActivity", "Exception", ex);
                }
            }
        }
        done = false;
    }

    /**
     * Wakes the lock. The while loop in the method 'lockFXThread' will proceed and break free.
     */
    private void unlockFXThread() {
        synchronized (lock) {
            done = true;
            lock.notifyAll();
        }
    }
}
