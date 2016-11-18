package edu.uco.captainplanet.myapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

public class AccountActivity extends AppCompatActivity {

    private static boolean auth = false;

    @InjectView(R.id.input_emailChange) EditText _emailText;
    @InjectView(R.id.input_passwordChange) EditText _passwordText;
    @InjectView(R.id.input_verifyPasswordChange) EditText _verifyPasswordText;
    @InjectView(R.id.btn_updateAccount) Button _updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        _updateButton = (Button)  findViewById(R.id.btn_updateAccount);

        _updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAccount();
            }
        });
    }

    public void updateAccount() {

        if (!validate()) {
            onUpdateFailed();
            return;
        }

        _updateButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(AccountActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating...");
        progressDialog.show();

        /*
         * Attempt to get JSON info
         * Reference: http://loopj.com/android-async-http/
        */
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(5000); // give enough time for client to get the JSON data
        client.get("https://uco-edmond-bus.herokuapp.com/api/userservice/users/edit/"
                        + _emailText.getText().toString() + "/" + UserInfoApplication.getInstance().getPassword() +
                            "/" + _passwordText.getText().toString()
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
                            onUpdateSuccess();
                        } else {
                            onUpdateFailed();
                        }
                        progressDialog.dismiss();
                    }
                }, 5000);
    }

    public void onUpdateSuccess() {
        _updateButton.setEnabled(true);
        setResult(RESULT_OK, null);
        UserInfoApplication.getInstance().setUsername(_emailText.getText().toString());
        UserInfoApplication.getInstance().setPassword(_passwordText.getText().toString());
        Toast.makeText(getBaseContext(), "Success! You have successfully updated your account.", Toast.LENGTH_LONG).show();
        finish();
    }

    public void onUpdateFailed() {
        Toast.makeText(getBaseContext(), "Update failed", Toast.LENGTH_LONG).show();
        _updateButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String verifyPassword = _verifyPasswordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("Between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (verifyPassword != password) {
            _verifyPasswordText.setError("Passwords do not match");
            valid = false;
        } else {
            _verifyPasswordText.setError(null);
        }

        return valid;
    }
}
