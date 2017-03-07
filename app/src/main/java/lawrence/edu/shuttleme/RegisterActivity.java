package lawrence.edu.shuttleme;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

    // UI references
    private EditText mEmailView;
    private EditText mPasswordView;
    //private EditText mEmailConfirmView;
    //private EditText mPasswordConfirmView;
    private EditText mUsernameView;
    private EditText mPhoneNumberView;

    // Network URI
    public static final String hostName = "143.44.78.173:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.register_password);
        //mEmailConfirmView = (EditText) findViewById(R.id.confirm_email);
        //mPasswordConfirmView = (EditText) findViewById(R.id.confirm_password);
        mUsernameView = (EditText) findViewById(R.id.user_name);
        mPhoneNumberView = (EditText) findViewById(R.id.phone);

        Button mRegistrationButton = (Button) findViewById(R.id.register_button);
        mRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        Button mBackToLoginButton = (Button) findViewById(R.id.back_to_login);
        mBackToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("ShuttleMe");
    }

    @Override
    public void onBackPressed() {
    }

    private void attemptRegister() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        //mEmailConfirmView.setError(null);
        //mPasswordConfirmView.setError(null);
        mUsernameView.setError(null);
        mPhoneNumberView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        //String emailC = mEmailConfirmView.getText().toString();
        //String passwordC = mPasswordConfirmView.getText().toString();
        String phoneNumber = mPhoneNumberView.getText().toString();
        String name = mUsernameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check that the user entered a name
        if (TextUtils.isEmpty(name)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberView.setError(getString(R.string.error_field_required));
            focusView = mPhoneNumberView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one and that the passwords match.
        if (!TextUtils.isEmpty(password)&& !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        /*
        if(!(password.equals(passwordC))) {
            mPasswordView.setError(getString(R.string.error_password_mismatch));
            focusView = mPasswordView;
            cancel = true;
        }
        */

        // Check for a valid email address and that both emails match.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        /*
        if (TextUtils.isEmpty(emailC)) {
            mEmailConfirmView.setError(getString(R.string.error_field_required));
            focusView = mEmailConfirmView;
            cancel = true;
        }
        */
        if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        /*
        if (!isEmailValid(emailC)) {
            mEmailConfirmView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailConfirmView;
            cancel = true;
        }
        if(!(email.equals(emailC))){
            mEmailView.setError(getString(R.string.error_email_mismatch));
            focusView = mEmailView;
            cancel = true;
        }
        */
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            new RegisterActivity.UserRegisterTask(email, password, phoneNumber, name).execute();
        }
    }

    private void registerActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }


    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 0;
        // TODO: More validation for passwords?
    }

    // TODO: Validation for phone number. Probably a regex

    public class UserRegisterTask extends AsyncTask<String, Void, Integer> {

        private final String uri;
        private String json;

        UserRegisterTask(String email, String password, String phoneNumber, String name) {
            email = email.toLowerCase();
            uri = "http://" + hostName + "/user/newuser";
            json = "{\"email\":" + email + ",\"name\":" + name +
                    ",\"password\":" + password + ",\"phonenumber\":" + phoneNumber +
                    ",\"role\":\"0\"}";
        }

        @Override
        protected Integer doInBackground(String... params) {
            int return_value = -1;

            try {
                int TIMEOUT_MILLISEC = 10000;  // = 10 seconds
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpClient client = new DefaultHttpClient(httpParams);

                HttpPost request = new HttpPost(uri);
                request.setEntity(new ByteArrayEntity(
                        json.toString().getBytes("UTF8")));
                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                String result = reader.readLine();
                return_value = Integer.valueOf(result);

                return return_value;

            } catch(Exception ex) {
                Log.d("Register Activity","Exception in doPost:" + ex.toString());
            }
            return return_value;
        }

        protected void onPostExecute(final Integer success) {
            if (success == 1) {

                Context context = getApplicationContext();
                CharSequence text = "Successfully Registered!";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                Intent intent = new Intent();
                intent.setClass(getApplicationContext(),LoginActivity.class);
                startActivity(intent);

                Log.d("Register Activity", "Registration successful: " + success);
                Toast.makeText(getApplicationContext(), "Registration Successful!", Toast.LENGTH_SHORT).show();

            } else if (success == 0){
                mEmailView.setError(getString(R.string.error_email_taken));
                View focusView = mEmailView;
                focusView.requestFocus();

                Log.d("Register Activity", "Email already exists: " + success);

                // TODO: Need to notify the user that the email given already exists
            } else {
                Log.d("Register Activity", "Server issue:  " + success);
                Toast.makeText(getApplicationContext(), "There was a server issue", Toast.LENGTH_SHORT).show();
                // TODO: Some useful message about the error
            }

        }
    }
}
