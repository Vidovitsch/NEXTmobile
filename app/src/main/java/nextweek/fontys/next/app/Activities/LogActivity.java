package nextweek.fontys.next.app.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import butterknife.ButterKnife;
import butterknife.InjectView;
import nextweek.fontys.next.R;
import nextweek.fontys.next.app.Data.DBManipulator;

public class LogActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

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

    /**
     * Sings in a user.
     */
    public void login() {
        if (!validate()) {
            btnLogin.setEnabled(true);
            Toast.makeText(this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
            return;
        }
        showAuthProgress();
        final String email = txtEmail.getText().toString();
        final String password = txtPassword.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(LogActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    progressDialog.dismiss();
                    btnLogin.setEnabled(true);
                    Toast.makeText(LogActivity.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                } else {
                    final DBManipulator manipulator = DBManipulator.getInstance();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onLoginSuccess(manipulator.getCurrentGroupLocation());
                        }
                    }, 2000);
                }
                }
            });
    }

    /**
     * Runs when all the user data has been retrieved from de Firebase database.
     * @param groupLocation of the signed in user.
     */
    public void onLoginSuccess(int groupLocation) {
        progressDialog.dismiss();
        if (groupLocation != 0) {
            openInfoActivity();
        } else {
            openScanActivity();
        }
    }

    /**
     * Validates all the input-fields before submitter their values.
     * @return true if valid, else false.
     */
    public boolean validate() {
        boolean valid = true;
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        //Email validation
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtEmail.setError("enter a valid email address");
            valid = false;
        }
//        else if (!email.contains("fontys.nl")) {
//            txtEmail.setError("enter a Fontys email address");
//            valid = false;
//        }
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
     * Shows an progress dialog while authenticating an user
     */
    private void showAuthProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
    }

    /**
     * Switches to the scan activity.
     */
    private void openScanActivity() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Switches to the info activity.
     */
    private void openInfoActivity() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
        finish();
    }
}
