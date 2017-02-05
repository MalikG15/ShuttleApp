package lawrence.edu.shuttleme;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient.*;
import com.google.android.gms.common.ConnectionResult;
import android.support.v4.content.ContextCompat;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.params.HttpParams;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.net.URLConnection;
import java.net.URL;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.net.MalformedURLException;


import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


import android.widget.*;
import android.view.*;
import android.util.Log;


import 	android.support.v4.app.ActivityCompat;

public class MainActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private android.location.Location mLastLocation;
    private int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private String latitude;
    private String longitude;
    public lawrence.edu.shuttleme.MainActivity copy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        TextView latitude = (TextView) findViewById(R.id.latitudeText);
        TextView longitude = (TextView) findViewById(R.id.longitudeText);
        Button getLocation = (Button) findViewById(R.id.send_location);


        ActivityCompat.requestPermissions(
                this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        copy = this;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                latitude.setText(String.valueOf(mLastLocation.getLatitude()));
                longitude.setText(String.valueOf(mLastLocation.getLongitude()));
            }
        }

        getLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                TextView latitude = (TextView) findViewById(R.id.latitudeText);
                TextView longitude = (TextView) findViewById(R.id.longitudeText);
                if (ContextCompat.checkSelfPermission(copy, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                            mGoogleApiClient);
                    if (mLastLocation != null) {
                        latitude.setText(String.valueOf(mLastLocation.getLatitude()));
                        longitude.setText(String.valueOf(mLastLocation.getLongitude()));
                    }
                }
                new retrieveCoordinates().execute();
            }
        });
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }*/

    /*public void sendCoordinates() {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://localhost:8080/shuttle/coordinates");


        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("shuttlename", "bob"));
        //nameValuePair.add(new BasicNameValuePair("password", "123456789"));


        //Encoding POST data
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            // log exception
            e.printStackTrace();
        }

        //making POST request.
        try {
            HttpResponse response = httpClient.execute(httpPost);
            // write response to log
            Log.d("Http Post Response:", response.toString());
        } catch (ClientProtocolException e) {
            // Log exception
            e.printStackTrace();
        } catch (IOException e) {
            // Log exception
            e.printStackTrace();
        }
    }*/

    class retrieveCoordinates extends AsyncTask<String, String, String> {

        protected String doInBackground(String... urls) {
            try {
                /*String argUrl = "http:143.44.78.173:8080/shuttle/getit";

                JSONObject rj = new JSONObject();

                String requestJSON = "{\"id\": \"1\",\"shuttleid\": \"1\", \"longitude\":" + 133 + ", \"latitude\":" + 66 + "}";
                URL url = new URL(argUrl);
                URLConnection con = url.openConnection();
                System.out.println("STRING" + requestJSON);
                //int responseCode = con.getRes
                // specify that we will send output and accept input
                //xcon.setDoInput(true);
                con.setDoOutput(true);
                con.setConnectTimeout(20000);  // long timeout, but not infinite
                con.setReadTimeout(20000);

                con.setUseCaches(false);
                con.setDefaultUseCaches(false);

                // tell the web server what we are sending
                con.setRequestProperty("Content-Type", "json");


                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                System.out.println("STRING2" + requestJSON);
                writer.write(requestJSON);
                writer.flush();
                writer.close();*/

                int TIMEOUT_MILLISEC = 10000;  // = 10 seconds
                String postMessage= "{\"id\":" + 10 + ",\"shuttleid\": \"1\", \"longitude\":" + 133 + ", \"latitude\":" + 66 + "}"; //HERE_YOUR_POST_STRING.
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpClient client = new DefaultHttpClient(httpParams);

                HttpPost request = new HttpPost("http://143.44.78.173:8080/shuttle/getit");
                request.setEntity(new ByteArrayEntity(
                        postMessage.toString().getBytes("UTF8")));
                HttpResponse response = client.execute(request);

            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

            return "";
        }

        protected void onPostExecute(String result) {
            System.out.println(result);
            System.out.println("AHAHAHAHA");
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // We tried to connect but failed!
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // We are not connected anymore!
    }
}
