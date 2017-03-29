package nextweek.fontys.next.app.Activities;

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

public class LogActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

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
            onLoginFailed();
            return;
        }
        final String email = txtEmail.getText().toString();
        final String password = txtPassword.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(LogActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    onLoginFailed();
                } else {
                    onLoginSuccess();
                }
                }
            });
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
        } else if (!email.contains("fontys.nl")) {
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
