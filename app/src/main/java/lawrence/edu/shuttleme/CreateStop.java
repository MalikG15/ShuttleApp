package lawrence.edu.shuttleme;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.io.InputStreamReader;

public class CreateStop extends AppCompatActivity {

    // UI references
    private EditText mNameView;
    private EditText mAddressView;
    private EditText mCityView;
    private EditText mStateView;
    private EditText mZipCodeView;
    private EditText mLatitudeView;
    private EditText mLongitudeView;

    // Network URI
    public static final String hostName = "143.44.78.173:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_stop);

        mNameView = (EditText) findViewById(R.id.stop_name);
        mAddressView = (EditText) findViewById(R.id.address);
        mCityView = (EditText) findViewById(R.id.city);
        mStateView = (EditText) findViewById(R.id.state);
        mZipCodeView = (EditText) findViewById(R.id.zip_code);
        mLatitudeView = (EditText) findViewById(R.id.latitude);
        mLongitudeView = (EditText) findViewById(R.id.longitude);

        Button mCreateStopButton = (Button) findViewById(R.id.create_stop);
        mCreateStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createStop();
            }
        });
    }

    public void createStop() {
        // Reset errors.
        mNameView.setError(null);
        mAddressView.setError(null);
        mCityView.setError(null);
        mStateView.setError(null);
        mZipCodeView.setError(null);
        mLatitudeView.setError(null);
        mLongitudeView.setError(null);


        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString();
        String address = mAddressView.getText().toString();
        String city = mCityView.getText().toString();
        String state = mStateView.getText().toString();
        String zipcode = mZipCodeView.getText().toString();
        String latitude = mLatitudeView.getText().toString();
        String longitude = mLongitudeView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check that the user entered a name
        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }
        // Check that the user entered a name
        if (TextUtils.isEmpty(address)) {
            mAddressView.setError(getString(R.string.error_field_required));
            focusView = mAddressView;
            cancel = true;
        }
        // Check that the user entered a name
        if (TextUtils.isEmpty(city)) {
            mCityView.setError(getString(R.string.error_field_required));
            focusView = mCityView;
            cancel = true;
        }
        // Check that the user entered a name
        if (TextUtils.isEmpty(state)) {
            mStateView.setError(getString(R.string.error_field_required));
            focusView = mStateView;
            cancel = true;
        }
        // Check that the user entered a name
        if (TextUtils.isEmpty(zipcode)) {
            mZipCodeView.setError(getString(R.string.error_field_required));
            focusView = mZipCodeView;
            cancel = true;
        }
        // Check that the user entered a name
        if (TextUtils.isEmpty(latitude)) {
            mLatitudeView.setError(getString(R.string.error_field_required));
            focusView = mLatitudeView;
            cancel = true;
        }
        // Check that the user entered a name
        if (TextUtils.isEmpty(longitude)) {
            mLongitudeView.setError(getString(R.string.error_field_required));
            focusView = mLongitudeView;
            cancel = true;
        } else {
            new Stop(name, address, city, state, zipcode, latitude, longitude, hostName, this).execute();
        }

    }

    // Return to Route Manager activity if creation is successful
    public void onPostExecuted(Integer success) {
        if (success == 1) {

            Context context = getApplicationContext();
            CharSequence text = "Successfully Created Stop!";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), RouteManager.class);
            startActivity(intent);

            Log.d("Create Stop Activity", "Stop Creation successful: " + success);

        } else if (success == 0) {

            Log.d("Create Stop Activity", "Failed to create stop: " + success);
            // TODO: Need to notify the user that the email given already exists
        } else {
            Log.d("Create Stop Activity", "Server issue:  " + success);
            // TODO: Some useful message about the error
        }

    }

}

class Stop extends AsyncTask<String, Void, Integer> {

    private final String uri;
    private String json;
    private CreateStop callback;

    // Create stop based off the required fields for stops
    Stop(String name, String address, String city, String state, String zipcode, String latitude, String longitude, String hostName, CreateStop cb) {
        uri = "http://" + hostName + "/stop/create";
        json = "{\"name\":" + "\"" + name + "\"" + ",\"address\":" + "\"" + address + " " + city + ", " + state +" "+ zipcode + "\"" +
                ",\"latitude\":" + "\"" + latitude + "\"" + ",\"longitude\":" + "\"" + longitude + "\"" +"}";
        callback = cb;
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
            Log.d("Create Stop Activity","Exception in doPost:" + ex.toString());
        }
        return return_value;
    }

    protected void onPostExecute(final Integer success) {
        callback.onPostExecuted(success);
    }
}
