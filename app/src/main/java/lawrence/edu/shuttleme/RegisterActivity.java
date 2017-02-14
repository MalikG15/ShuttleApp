package lawrence.edu.shuttleme;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import io.fabric.sdk.android.Fabric;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;

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

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "jAs4mkAg8hRw3Ds8ZHyAjamT6";
    private static final String TWITTER_SECRET = "woDFoGcB2iW14Mvlos8JQaXqGnS9G6ovssc0xUnbBWjafHZ67z";

    // UI references
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mEmailConfirmView;
    private EditText mPasswordConfirmView;
    private EditText mUsernameView;
    private EditText mPhoneNumberView;

    // Network URI
    public static final String hostName = "143.44.78.173:8080";

    // SMS permissions
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;

    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits.Builder().build());
        setContentView(R.layout.activity_register);

        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailConfirmView = (EditText) findViewById(R.id.confirm_email);
        mPasswordConfirmView = (EditText) findViewById(R.id.confirm_password);
        mUsernameView = (EditText) findViewById(R.id.user_name);
        mPhoneNumberView = (EditText) findViewById(R.id.phone);


        DigitsAuthButton digitsButton = (DigitsAuthButton) findViewById(R.id.auth_button);
        digitsButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                // TODO: associate the session userID with your user model
                Toast.makeText(getApplicationContext(), "Authentication successful for "
                        + phoneNumber, Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(DigitsException exception) {
                Log.d("Digits", "Sign in with Digits failure", exception);
            }
        });



        Button mRegistrationButton = (Button) findViewById(R.id.register_button);
        mRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sendSMS();
                //attemptRegister();
            }
        });

        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("Register");

        /*

        <android.support.design.widget.FloatingActionButton
        android:id="@+id/fab"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|end"
        android:layout_margin="@dimen/fab_margin"
        app:srcCompat="@android:drawable/ic_dialog_email" />

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */


    }

    protected void sendSMS() {
        phoneNumber = mPhoneNumberView.getText().toString();

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNumber, null, "Test message", null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }

    private void attemptRegister() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mEmailConfirmView.setError(null);
        mPasswordConfirmView.setError(null);
        mUsernameView.setError(null);
        mPhoneNumberView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String emailC = mEmailConfirmView.getText().toString();
        String passwordC = mPasswordConfirmView.getText().toString();
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
        if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(passwordC) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if(!(password.equals(passwordC))) {
            mPasswordView.setError(getString(R.string.error_password_mismatch));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address and that both emails match.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        if (TextUtils.isEmpty(emailC)) {
            mEmailConfirmView.setError(getString(R.string.error_field_required));
            focusView = mEmailConfirmView;
            cancel = true;
        }
        if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
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

            } else if (success == 0){
                mEmailView.setError(getString(R.string.error_email_taken));
                View focusView = mEmailView;
                focusView.requestFocus();

                Log.d("Register Activity", "Email already exists: " + success);
                // TODO: Need to notify the user that the email given already exists
            } else {
                Log.d("Register Activity", "Server issue:  " + success);
                // TODO: Some useful message about the error
            }

        }
    }
}
