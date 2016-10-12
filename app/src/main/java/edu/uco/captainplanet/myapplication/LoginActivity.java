package edu.uco.captainplanet.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static boolean auth = false;

    @InjectView(R.id.input_username) EditText _usernameText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_login) Button _loginButton;
    @InjectView(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        /*
         * Attempt to get JSON info
         * Reference: http://loopj.com/android-async-http/
        */
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(5000); // give enough time for client to get the JSON data
        client.get("https://uco-edmond-bus.herokuapp.com/api/userservice/users/"
                + _usernameText.getText().toString() + "/" + _passwordText.getText().toString()
                , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // Called when response HTTP status is "200 OK"
                try {
                    if (response.getString("username").equals(_usernameText.getText().toString())
                            && response.getString("password").equals(_passwordText.getText().toString())) {
                        UserInfoApplication.getInstance().setId(response.getInt("id"));
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
                        onLoginSuccess();
                    } else {
                        onLoginFailed();
                    }
                    progressDialog.dismiss();
                }
            }, 5000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, null);
                UserInfoApplication.getInstance().setLoggedIn(true);
                Toast.makeText(getBaseContext(), "Signup successful! You are now logged in", Toast.LENGTH_LONG).show();
                this.finish();
            }
        }
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        setResult(RESULT_OK, null);
        UserInfoApplication.getInstance().setUsername(_usernameText.getText().toString());
        UserInfoApplication.getInstance().setLoggedIn(true);
        Toast.makeText(getBaseContext(), "Success! You are now logged in", Toast.LENGTH_LONG).show();
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty()) {
            _usernameText.setError("Enter a valid username");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("Between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}