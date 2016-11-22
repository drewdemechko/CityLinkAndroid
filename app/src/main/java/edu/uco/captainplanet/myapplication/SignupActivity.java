package edu.uco.captainplanet.myapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private static boolean auth = false;

    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.input_confirm_password) EditText _confirmPasswordText;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        // check if passwords match
            //Toast.makeText(getBaseContext(), "Password fields do not match", Toast.LENGTH_LONG).show();
            //_signupButton.setEnabled(true);
        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.CityLink);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        /*
         * Attempt to get JSON info
         * Reference: http://loopj.com/android-async-http/
        */
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(5000); // give enough time for client to get the JSON data
        client.get("http://uco-edmond-bus.herokuapp.com/api/userservice/users/create/"
                        + _emailText.getText().toString() + "/" + _passwordText.getText().toString() + "/client"
                , new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // Called when response HTTP status is "200 OK"
                        try {
                            if (response.getString("username").equals(_emailText.getText().toString())
                                    && response.getString("password").equals(_passwordText.getText().toString())) {
                                auth = true;
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                });

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (auth) {
                            onSignupSuccess();
                        } else {
                            onSignupFailed();
                        }
                        progressDialog.dismiss();
                    }
                }, 5000);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        UserInfoApplication.getInstance().setUsername(_emailText.getText().toString());
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Sign up failed", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String confirmPassword = _confirmPasswordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 12) {
            _passwordText.setError("Between 4 and 12 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (!password.equals(confirmPassword)) {
            _passwordText.setError("Password field does not match");
            _confirmPasswordText.setError("Confirm Password field does not match");
            valid = false;
        } else {
            _passwordText.setError(null);
            _confirmPasswordText.setError(null);
        }

        return valid;
    }
}