package lawrence.edu.shuttleme;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient.*;
import android.support.v4.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class DriverActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private ClipBoard clipBoard;

    private ListView listView;
    private TextView passengerTextView;
    private Button getLocation;

    private GoogleApiClient mGoogleApiClient;
    private android.location.Location mLastLocation;
    private int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public lawrence.edu.shuttleme.DriverActivity copy;
    private String latitude;
    private String longitude;

    // Network URI
    public static final String hostName = "143.44.78.173:8080";

    // TODO: Make flashing text appear that indicates location is being given out
    // TODO: "Broadcasting signal"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.check_list_view);
        passengerTextView = (TextView) findViewById(R.id.checked_in_passengers);
        getLocation = (Button) findViewById(R.id.send_location);

        // TODO: Make populate passengers update automatically after 30 seconds or create a refresh button
        populatePassengers();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onBackPressed() {
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // We are now connected!

        ActivityCompat.requestPermissions(
                this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        copy = this;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                latitude = (String.valueOf(mLastLocation.getLatitude()));
                longitude = (String.valueOf(mLastLocation.getLongitude()));
            }
        }

        // TODO: Make an event that repeats every 5 seconds

        getLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(copy, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                            mGoogleApiClient);
                    if (mLastLocation != null) {
                        latitude = (String.valueOf(mLastLocation.getLatitude()));
                        longitude = (String.valueOf(mLastLocation.getLongitude()));
                    }
                }
            }
        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // We tried to connect but failed!
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // We are not connected anymore!
    }

    public void populatePassengers() {
        clipBoard = new ClipBoard(this);
        clipBoard.updatePassengerList();
    }

    public void updateCurrentLocation(String latitude, String longitude) {
        // TODO: Persistent update for location
    }

    public void getAssignedRoute() {
        // TODO: Once RouteManager is completed driver
    }

    public class ClipBoard {

        ArrayList<String> clipboard;
        private List current_passengers;
        private ArrayList<String> arrayList;
        private ArrayAdapter<String> adapter;
        private DriverActivity driverActivity;

        public ClipBoard(DriverActivity DR){
            driverActivity = DR;
        }

        public void updatePassengerList() {
            // TODO: Persist checked in list
            populateList("");
            //new RetrieveCheckedIn().execute();
        }

        public void populateList(String result) {
            current_passengers = new ArrayList<String>();
            String[] items = {"David - 7731234567", "Malik - 4372637463", "Elkin - 372827376"};
            arrayList = new ArrayList<>(Arrays.asList(items));
            adapter = new ArrayAdapter<String>(driverActivity, R.layout.list_view, R.id.textitem, arrayList);
            listView.setAdapter(adapter);
            passengerTextView.setText("Currently " + arrayList.size() + " Passengers CheckedIn");
            /*
            // Parse data - get name of passenger and phone number
            try {
                JSONArray jArray = new JSONArray(result);
                for (int i=0; i < jArray.length(); i++)
                {
                    try {
                        JSONObject root = jArray.getJSONObject(i);
                        // Pulling items from the array
                        String id = root.getString("name");
                        String name = root.getString("phonenumber");
                        //listOfUsers.put(i,id);
                        //users.add(name);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            */

            // Update current list of passengers
            //adapter = new ArrayAdapter<String>(driverActivity, R.layout.list_view, R.id.textitem, current_passengers);
            //listView.setAdapter(adapter);
        }

        class RetrieveCheckedIn extends AsyncTask<String, String, String> {

            String uri = "";

            public RetrieveCheckedIn() {
                // uri = "http://" + hostName + "/user/checkuser?email=" + mEmail + "&password=" + mPassword;
            }

            @Override
            protected String doInBackground(String... params) {

                List<String> listOfPassengers = new ArrayList<String>();

                String response = "";
                String responseError = "";
                HttpURLConnection conn = null;

                try{
                    //Connect to URL
                    URL url = new URL(uri);

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


            @Override
            protected void onPostExecute(String result) {
                populateList(result);
            }

        }
    }
}



