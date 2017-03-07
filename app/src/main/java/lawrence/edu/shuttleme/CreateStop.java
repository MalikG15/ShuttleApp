package lawrence.edu.shuttleme;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class CreateStop extends AppCompatActivity {

    // UI references
    private EditText mNameView;
    private EditText mAddressView;
    private EditText mCityView;
    private EditText mStateView;

    // Network URI
    public static final String hostName = "143.44.78.173:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_stop);

        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("ShuttleMe");

        mNameView = (EditText) findViewById(R.id.stop_name);
        mAddressView = (EditText) findViewById(R.id.address);
        mCityView = (EditText) findViewById(R.id.city);
        mStateView = (EditText) findViewById(R.id.state);

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


        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString();
        String address = mAddressView.getText().toString();
        String city = mCityView.getText().toString();
        String state = mStateView.getText().toString();

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
        else {
            // Retrieve Coordinates
            String adr = mAddressView.getText().toString();
            adr.replaceAll("\\s+", "+");
            String ste = mStateView.getText().toString();
            ste.replaceAll("\\s+", "+");
            String cty = mCityView.getText().toString();
            ste.replaceAll("\\s+", "+");

            new getCoordinates(adr, cty, ste, this).execute();
        }
    }

    // After retrieving coordinates, post stop to database
    public void onGetCoordinatesCompleted(String result){

        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString();
        String address = mAddressView.getText().toString();
        String city = mCityView.getText().toString();
        String state = mStateView.getText().toString();
        String latitude = "";
        String longitude = "";

        // Parse data
        try {
            JSONArray res = new JSONObject(result).getJSONArray("results");
            JSONObject addcomp = res.getJSONObject(0);
            JSONObject geom = addcomp.getJSONObject("geometry");
            // Pulling items from the array
            JSONObject loc = geom.getJSONObject("location");
            latitude = loc.getString("lat");
            longitude = loc.getString("lng");

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Couldn't find coordinates for this address", Toast.LENGTH_SHORT).show();
        }
        if(latitude != "" && longitude != ""){
            // Post Stop
            new Stop(name, address, city, state, latitude, longitude, hostName, this).execute();
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
            Toast.makeText(getApplicationContext(), "Failed to create stop. Stop either exists or failed to post in database", Toast.LENGTH_SHORT).show();
            Log.d("Create Stop Activity", "Failed to create stop: " + success);
            // TODO: Need to notify the user that the email given already exists
        } else {
            Toast.makeText(getApplicationContext(), "There was a server issue, failed to create stop", Toast.LENGTH_SHORT).show();
            Log.d("Create Stop Activity", "Server issue:  " + success);
            // TODO: Some useful message about the error
        }

    }
}

class getCoordinates extends AsyncTask<String, Void, String> {
    CreateStop caller;
    String _url;
    private String API_KEY = GoogleAPI.Google_geocode_API_KEY;

    getCoordinates(String _address, String _city, String _state, CreateStop cs){
        _url = "https://maps.googleapis.com/maps/api/geocode/json?address="+_address+_city+_state+"&key="+API_KEY;
        caller = cs;
    }
    @Override
    protected String doInBackground(String... params) {
        String response = "";
        String responseError = "";
        HttpURLConnection conn = null;
        try{
            //Connect to URL
            URL url = new URL(_url);

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode=conn.getResponseCode();

            //HTTP_OK --> 200
            //HTTP_CONFLICT --> 409
            if (responseCode == HttpsURLConnection.HTTP_OK ) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
                return response;
            }
            else if(responseCode == HttpURLConnection.HTTP_CONFLICT){
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                while ((line=br.readLine()) != null) {
                    responseError+=line;
                }
                System.out.print(responseError);
                return responseError;
            }
            else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null){
                conn.disconnect();
            }
        }
        return response;
    }

    protected void onPostExecute(final String success) {
        caller.onGetCoordinatesCompleted(success);
    }
}

class Stop extends AsyncTask<String, Void, Integer> {

    private final String uri;
    private String json;
    private CreateStop callback;

    // Create stop based off the required fields for stops
    Stop(String name, String address, String city, String state, String latitude, String longitude, String hostName, CreateStop cb) {
        uri = "http://" + hostName + "/stop/create";
        json = "{\"name\":" + "\"" + name + "\"" + ",\"address\":" + "\"" + address + " " + city + ", " + state + "\"" +
                ",\"latitude\":" + "\"" + latitude + "\"" + ",\"longitude\":" + "\"" + longitude + "\"" +"}";
        callback = cb;
    }

    @Override
    protected Integer doInBackground(String... params) {
        int return_value = 0;

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
            Toast.makeText(callback.getApplicationContext(), "Failed to post to server", Toast.LENGTH_SHORT).show();
        }
        return return_value;
    }

    protected void onPostExecute(final Integer success) {
        callback.onPostExecuted(success);
    }
}
