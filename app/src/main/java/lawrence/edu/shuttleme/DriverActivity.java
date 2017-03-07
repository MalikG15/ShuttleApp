package lawrence.edu.shuttleme;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient.*;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.net.ssl.HttpsURLConnection;

public class DriverActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private ClipBoard clipBoard;
    private String driverName;
    private String driverID;

    private ListView listView;
    private Map<Integer, String> listOfUsers;
    private TextView passengerTextView;
    private Button refresh;
    private TextView latitudeText;
    private TextView longitudeText;
    private TextView latitudeLabel;
    private TextView longitudeLabel;
    CustomAdapter customadapter;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    // Malik's phone MUST be connected to wifi
    private Location mLastLocation;
    private int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public DriverActivity copy;
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
        getSupportActionBar().setTitle("ShuttleMe");

        listView = (ListView) findViewById(R.id.check_list_view);
        //passengerTextView = (TextView) findViewById(R.id.checked_in_passengers);
        refresh = (Button) findViewById(R.id.refresh_clipboard);
        refresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                clipBoard.updatePassengerList();
            }
        });

        // latitudeLabel = (TextView) findViewById(R.id.latitudeLabel);
        // longitudeLabel = (TextView) findViewById(R.id.longitudeLabel);
        latitudeText = (TextView) findViewById(R.id.latitudeText);
        longitudeText = (TextView) findViewById(R.id.longitudeText);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                driverName = null;
                driverID = null;
            } else {
                driverName = extras.getString("DRIVER_NAME");
                driverID = extras.getString("DRIVER_ID");
            }
        } else {
            driverName = (String) savedInstanceState.getSerializable("DRIVER_NAME");
            driverID = (String) savedInstanceState.getSerializable("DRIVER_ID");
        }

        // new CreateClipBoardSession(driverName, driverID).execute();

        // TODO: Once the driver logins, clipboardsession is created, need the drivers ID
        // TODO: Clipboard ID is going to be returned to access the passengers
        // TODO: Need clipboard sessionID in clipboard to update count
        // TODO: Clipboard id to get

        // TODO: Make populate passengers update automatically after 30 seconds or create a refresh button
        populatePassengers();

        if (mGoogleApiClient == null) {
            // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            mGoogleApiClient = new Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(AppIndex.API)
                    .build();
        }
    }

    @Override
    public void onBackPressed() {
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(mGoogleApiClient, getIndexApiAction());
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(mGoogleApiClient, getIndexApiAction());
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // We are now connected!

        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        copy = this;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Use mLastLocation as a base to start from, so that the app has a location before starting the periodic updates
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                latitude = String.valueOf(mLastLocation.getLatitude());
                longitude = String.valueOf(mLastLocation.getLongitude());
                //updateUI();
                new PostDriverLocationTask(latitude, longitude, driverID).execute();
            }

            mLocationRequest = new LocationRequest();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(10000); // Update location every 10 seconds

            mLocationRequest.setFastestInterval(4000);
            // Fastest interval to recognize location changes, 4 seconds
            // Since we are tracking faster moving target we need to be more precise about location

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this); // Begins process of periodic update
        }
    }
    // For more information about automatic location updates: https://developer.android.com/training/location/receive-location-updates.html

    // Callback method for when Location has changed
    public void onLocationChanged(Location location) {
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
        mLastLocation = location;
        // updateUI();
        new PostDriverLocationTask(latitude, longitude, driverID).execute();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // We tried to connect but failed!
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // We are not connected anymore!
    }

    void updateUI() {
        latitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
        longitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
    }

    public void populatePassengers() {
        clipBoard = new ClipBoard(this);
        clipBoard.updatePassengerList();
    }

    public void getAssignedRoute() {
        // TODO: Once RouteManager is completed
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Driver Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_passenger, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, LoginActivity.class);
                this.startActivity(intent);
                this.finish();
                break;
            /*
            case R.id.menu_item2:
                // another startActivity, this is for item with id "menu_item2"
                break;
            */
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public class ClipBoard {

        ArrayList<String> clipboard;
        private List current_passengers;
        private ArrayList<String> arrayList;
        private ArrayAdapter<String> adapter;
        private DriverActivity driverActivity;

        public ClipBoard(DriverActivity DR){
            driverActivity = DR;
            // Store id and names of users
            listOfUsers = new HashMap<Integer,String>();
        }

        public void updatePassengerList() {
            // TODO: Persist checked in list
            new RetrieveCheckedInPassengers().execute();
            //filterUserID("");
        }

        public void filterUserID(String result) {
            String JSON_userIDs = "{\"users\":[";

            /*
            String[] items = {"David - 7731234567", "Malik - 4372637463", "Elkin - 372827376"};
            arrayList = new ArrayList<>(Arrays.asList(items));
            adapter = new ArrayAdapter<String>(driverActivity, R.layout.list_view, R.id.textitem, arrayList);
            listView.setAdapter(adapter);
            passengerTextView.setText("Currently " + arrayList.size() + " Passengers CheckedIn");
            */

            // Parse data - get name of passenger and phone number
            try {
                JSONArray jArray = new JSONArray(result);
                for (int i=0; i < jArray.length(); i++)
                {
                    try {
                        JSONObject root = jArray.getJSONObject(i);
                        // Pulling items from the array
                        String id = root.getString("userid");
                        JSON_userIDs = JSON_userIDs + "{\"userid\":\"" + id + "\"}";
                        if(i+1 < jArray.length())
                            JSON_userIDs += ",";
                        listOfUsers.put(i,id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                JSON_userIDs = JSON_userIDs + "]}";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(JSON_userIDs != "") {
                new RetrieveList(this, JSON_userIDs).execute();
            }
            else {
                Context context = getApplicationContext();
                CharSequence text = "No new passengers checked in!";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            // Update current list of passengers
            //adapter = new ArrayAdapter<String>(driverActivity, R.layout.list_view, R.id.textitem, current_passengers);
            //listView.setAdapter(adapter);
        }
        public void populateClipboard(String result) {
            Log.d("Driver Activity", "Result: " + result);
            current_passengers = new ArrayList<String>();

            // Store id and names of users
            listOfUsers = new HashMap<Integer,String>();

            /*
            String[] items = {"David - 7731234567", "Malik - 4372637463", "Elkin - 372827376"};
            arrayList = new ArrayList<>(Arrays.asList(items));
            adapter = new ArrayAdapter<String>(driverActivity, R.layout.list_view, R.id.textitem, arrayList);
            listView.setAdapter(adapter);
            passengerTextView.setText("Currently " + arrayList.size() + " Passengers CheckedIn");
            */

            // Parse data - get name of passenger and phone number
            try {
                JSONObject jobject = new JSONObject(result);
                JSONArray jsonArray = jobject.getJSONArray("users");
                for (int i=0; i < jsonArray.length(); i++)
                {
                    try {
                        JSONObject root = jsonArray.getJSONObject(i);
                        // Pulling items from the array
                        String name = root.getString("name");
                        String phonenumber = root.getString("phonenumber");
                        current_passengers.add(name + " - " + phonenumber);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(current_passengers.size() > 0) {
                // Update current list of passengers
                adapter = new ArrayAdapter<String>(driverActivity, R.layout.list_view, R.id.textitem, current_passengers);
                listView.setAdapter(adapter);
            }
            else {
                // TODO: Tell driver there are no passengers checked in
            }
        }

        class RetrieveCheckedInPassengers extends AsyncTask<String, String, String> {

            String uri = "";

            public RetrieveCheckedInPassengers() {
                uri = "http://" + hostName + "/clipboard/get";
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
                        return response;
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
                filterUserID(result);
            }
        }

        class RetrieveList extends AsyncTask<String, String, String> {

            String uri;
            ClipBoard clipBoard;
            String JSON;

            public RetrieveList(ClipBoard context, String JSON) {
                uri = "http://" + hostName + "/clipboard/getuserinfo";
                clipBoard = context;
                this.JSON = JSON;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String result) {
                clipBoard.populateClipboard(result);
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onCancelled(String s) {
                super.onCancelled(s);
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
            }

            @Override
            protected String doInBackground(String... params) {

                List<String> listOfPassengers = new ArrayList<String>();

                String return_response = "";
                String responseError = "";
                HttpURLConnection conn = null;
                try{
                    int TIMEOUT_MILLISEC = 10000;  // = 10 seconds
                    HttpParams httpParams = new BasicHttpParams();
                    HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
                    HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
                    HttpClient client = new DefaultHttpClient(httpParams);

                    HttpPost request = new HttpPost(uri);
                    request.setEntity(new ByteArrayEntity(
                            JSON.toString().getBytes("UTF8")));
                    HttpResponse response = client.execute(request);
                    String line;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                    while ((line=reader.readLine()) != null) {
                        return_response += line;
                    }
                    return return_response;

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (conn != null){
                        conn.disconnect();
                    }
                }
                //return response;
                return "";
            }

        }
    }
    public class CreateClipBoardSession extends AsyncTask<String, Void, String[]> {

        private final String driverID;
        private final String driverName;
        private final String uri;
        private String json;

        CreateClipBoardSession(String driverid, String drivername) {
            driverID = driverid;
            driverName = drivername;
            uri = "http://" + hostName + "/clipboardsession/newclipboardsession";
            json = "{\"name\":" + drivername + ",\"userid\":" + driverID + "}";
        }

        @Override
        protected String[] doInBackground(String... params) {

            String[] result = {"", "", ""};

            try {
                int TIMEOUT_MILLISEC = 10000;  // = 10 seconds
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpClient client = new DefaultHttpClient(httpParams);

                HttpGet request = new HttpGet(uri);

                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

                JSONObject root = new JSONObject(reader.readLine());
                result[0] = root.getString("name");

                return result;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }
        @Override
        protected void onPostExecute(final String[] success) {

        }

        @Override
        protected void onCancelled() {
        }

    }

    public class PostDriverLocationTask extends AsyncTask<String, Void, Integer> {

        private final String uri;
        private String json;

        private String latitude;
        private String longitude;
        private String driverID;

        PostDriverLocationTask(String Lat, String Long, String driverid) {
            latitude = Lat;
            longitude = Long;
            driverID = driverid;

            uri = "http://" + hostName + "/shuttle/sendlocation";
            json = "{\"latitude\":" + "\"" + latitude + "\"" + ",\"longitude\":" + "\"" + longitude + "\"" +
                    ",\"driverid\":" +"\"" + driverID + "\"}";

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
                Log.d("Driver Activity","Exception in doPost:" + ex.toString());
            }
            return return_value;
        }

        @Override
        protected void onPostExecute(final Integer success) {
            if (success == 1) {

                Context context = getApplicationContext();
                CharSequence text = "Location Updated";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

            } else if (success == 0){
                Context context = getApplicationContext();
                CharSequence text = "No location change";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

            } else if(success == -1) {

                Context context = getApplicationContext();
                CharSequence text = "Failed to change - server issue";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                Log.d("Driver Activity", "Must be a server issue: " + success);
                // TODO: Need to differentiate bewtween duplicate coordinates and server issue
            }
        }
    }
}



