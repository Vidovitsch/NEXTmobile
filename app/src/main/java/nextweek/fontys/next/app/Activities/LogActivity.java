package nextweek.fontys.next.app.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
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
import nextweek.fontys.next.app.Activities.Organisation.AllocActivity;
import nextweek.fontys.next.app.Data.DBManipulatorStu;

public class LogActivity extends AppCompatActivity {

    private final static String studentConst = "@student.fontys.nl";
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private DBManipulatorStu manipulator;
    private String signedMail;

    @InjectView(R.id.input_email) EditText txtEmail;
    @InjectView(R.id.input_password) EditText txtPassword;
    @InjectView(R.id.btn_login) Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        manipulator = DBManipulatorStu.getInstance();
        manipulator.setLogActivity(this);

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
                        //Reset all fields set in de DBManipulatorStu constructor
                        signedMail = email;
                        manipulator.setSignedInUser();
                        manipulator.setCurrentGroupID();
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
        if (signedMail.contains(studentConst)) {
            if (groupLocation != 0) {
                openInfoActivity();
            } else {
                openScanActivity();
            }
        } else {
            openAllocActivity();
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
        else {
            txtEmail.setError(null);
        }

        //Password validation
        if (password.isEmpty() || password.length() < 6 ) {
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

    /**
     * Switches to the allocation activity
     */
    private void openAllocActivity() {
        Intent intent = new Intent(this, AllocActivity.class);
        startActivity(intent);
        finish();
    }
}
