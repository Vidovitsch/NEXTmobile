package nextweek.fontys.next.app.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.ButterKnife;
import butterknife.InjectView;
import nextweek.fontys.next.R;

public class LogActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @InjectView(R.id.input_email) EditText txtEmail;
    @InjectView(R.id.input_password) EditText txtPassword;
    @InjectView(R.id.btn_login) Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        mAuth = FirebaseAuth.getInstance();
        ButterKnife.inject(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    public void login() {
        if (!validate()) {
            Log.d("Logging in", "aksjdfahfioawehfihaweuifaweiofawefhawefawefawef");
            onLoginFailed();
            return;
        }
        btnLogin.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LogActivity.this,
                R.style.Theme_AppCompat_DayNight);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        final String email = txtEmail.getText().toString();
        final String password = txtPassword.getText().toString();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(LogActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (!task.isSuccessful()) {
                                            onLoginFailed();
                                        } else {
                                            onLoginSuccess();
                                        }
                                        progressDialog.dismiss();
                                    }
                                });
                    }
                }, 3000);
    }

    public void onLoginSuccess() {
        btnLogin.setEnabled(true);
        openScanActivity();
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        btnLogin.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        //Email validation
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtEmail.setError("enter a valid email address");
            valid = false;
        } else if (!email.contains("@student.fontys.nl")) {
            txtEmail.setError("enter a Fontys email address");
            valid = false;
        }
        else {
            txtEmail.setError(null);
        }

        //Password validation
        if (password.isEmpty() || password.length() < 6 || password.length() > 16) {
            txtPassword.setError("between 4 and 16 characters");
            valid = false;
        } else {
            txtPassword.setError(null);
        }

        return valid;
    }

    /**
     * Switches to the scan activity.
     */
    private void openScanActivity() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
        finish();
    }
}
