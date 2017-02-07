package lawrence.edu.shuttleme;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient.*;
import com.google.android.gms.common.ConnectionResult;
import android.support.v4.content.ContextCompat;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.params.HttpParams;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.net.URL;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.net.MalformedURLException;


import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


import android.widget.*;
import android.view.*;
import android.util.Log;


import 	android.support.v4.app.ActivityCompat;
import lawrence.edu.shuttleme.MainActivity;


public class MainActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private Double latitude;
    private Double longitude;
    private Double prevLat = 44.26238209999999;
    private Double prevLong = -88.398101;
    private int num;
    private String[] stopid;
    private int sum = 0;
    private String loc;


    public MainActivity copy;

    private String routeInfo;

    private String API_KEY = GoogleAPI.Google_API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list);
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            mGoogleApiClient = new Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(AppIndex.API).build();
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
        new getRoutes().execute();
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
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //AppIndex.AppIndexApi.start(mGoogleApiClient, getIndexApiAction());
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        //AppIndex.AppIndexApi.end(mGoogleApiClient, getIndexApiAction());
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        // We are now connected!
        /*TextView latitudeT = (TextView) findViewById(R.id.latitudeText);
        TextView longitudeT = (TextView) findViewById(R.id.longitudeText);
        Button getLocation = (Button) findViewById(R.id.send_location);


        new AlertDialog.Builder(MainActivity.this)
                .setTitle("ShuttleMe")
                .setMessage("This app requires that you enable location services.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

        /*ActivityCompat.requestPermissions(
                this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        ActivityCompat.requestPermissions(
                this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        copy = this;


        /*if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                latitude.setText(String.valueOf(mLastLocation.getLatitude()));
                longitude.setText(String.valueOf(mLastLocation.getLongitude()));
            }
        }
        /*ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);*/

        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                // System.out.println("nah");
                new AlertDialog.Builder(copy)
                        .setTitle("ShuttleMe")
                        .setMessage("This app requires that you enable location services.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(
                                        copy,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }

        ActivityCompat.requestPermissions(
                copy,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        //TextView latitude = (TextView) findViewById(R.id.latitudeText);
        //TextView longitude = (TextView) findViewById(R.id.longitudeText);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();
                latitudeT.setText(String.valueOf(mLastLocation.getLatitude()));
                longitudeT.setText(String.valueOf(mLastLocation.getLongitude()));
            }
        }

        getLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                TextView latitudeT = (TextView) findViewById(R.id.latitudeText);
                TextView longitudeT = (TextView) findViewById(R.id.longitudeText);
                if (ContextCompat.checkSelfPermission(copy, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                            mGoogleApiClient);
                    if (mLastLocation != null) {
                        latitude = mLastLocation.getLatitude();
                        longitude = mLastLocation.getLongitude();
                        latitudeT.setText(String.valueOf(mLastLocation.getLatitude()));
                        longitudeT.setText(String.valueOf(mLastLocation.getLongitude()));
                    }
                }
                new retrieveCoordinates().execute();
            }
        });*/
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

                /*int TIMEOUT_MILLISEC = 10000;  // = 10 seconds
                String postMessage= "{\"email\":" + "@booty" + ",\"name\": \"TT\",\"password\":" + "TT" + ",\"phonenumber\":" + "234234" + ",\"role\": \"0\"}"; //HERE_YOUR_POST_STRING.
                System.out.println(postMessage);
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpClient client = new DefaultHttpClient(httpParams);

                HttpPost request = new HttpPost("http://143.44.78.173:8080/user/newuser");
                request.setEntity(new ByteArrayEntity(
                        postMessage.toString().getBytes("UTF8")));
                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                String json = reader.readLine();
                return Integer.valueOf(json);*/
                    if (longitude != null && latitude != null) {
                        int TIMEOUT_MILLISEC = 10000;  // = 10 seconds
                        HttpParams httpParams = new BasicHttpParams();
                        HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
                        HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
                        HttpClient client = new DefaultHttpClient(httpParams);
                        HttpPost request = new HttpPost("https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + prevLat + "," + prevLong +
                                "&destinations=" + latitude + "," + longitude + "&key=" + API_KEY);
                        HttpResponse response = client.execute(request);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

                        char[] party = new char[50000];
                        int bytesread = reader.read(party, 0, party.length);
                        String gResponse = new String(party, 0, bytesread);
                        JSONObject test = new JSONObject(gResponse);
                        //return test.getJSONArray("rows").getJSONArray("elements").getJSONObject("duration").getString("text");
                        return test.toString();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                return "nah, it didn't work boi";
            }


            protected void onPostExecute(String result) {
                System.out.println(result);
                System.out.println("AHAHAHAHA");
                TextView stop = (TextView) findViewById(R.id.stop_1);
                if (!stop.getText().equals("")) stop = (TextView) findViewById(R.id.stop_2);
                if (!stop.getText().equals("")) stop = (TextView) findViewById(R.id.stop_3);
                if (!stop.getText().equals("")) stop = (TextView) findViewById(R.id.stop_4);

                try {
                    JSONArray ETA = (new JSONObject(result)).getJSONArray("rows");
                    System.out.println(ETA.getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getString("text") + " boooooot");
                    // rows, elements, distance
                    String time = ETA.getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getString("text");

                    sum = Integer.valueOf(time.split(" ")[0]) + sum;
                    stop.setText(loc + "\n\n" + sum + " minutes away\n");
                    if (num + 1 < stopid.length) {
                        num++;
                        new getStopLocation().getStopLocations();
                    }
                    //num += Integer.valueOf(ETA.getString("text").split(" ")[0]);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                //TextView longitudeT = (TextView) findViewById(R.id.longitudeText);
            }
        }

        class getRoutes extends AsyncTask<String, String, String> {

            protected String doInBackground(String... urls) {
                try {
                    int TIMEOUT_MILLISEC = 10000;  // = 10 seconds
                    HttpParams httpParams = new BasicHttpParams();
                    HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
                    HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
                    HttpClient client = new DefaultHttpClient(httpParams);

                    HttpGet request = new HttpGet("http://143.44.78.173:8080/route/getroute?routeid=1");

                    HttpResponse response = client.execute(request);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

                    //JSONObject root = new JSONObject(reader.readLine());
                    return reader.readLine();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                return "nah, it didn't work boi";
            }


            protected void onPostExecute(String result) {
                routeInfo = result;
                new getStopLocation().getStopLocations();
                System.out.println("working!");
                System.out.println(result);
            }
        }


        class getStopLocation extends AsyncTask<String, String, String> {

            private String tempStop;

            public void getStopLocations() {
                try {
                    JSONObject info = new JSONObject(routeInfo);
                    String stops = info.getString("stops");
                    stopid = stops.split(",");
                    /*stopid = new String[listOfStopIds.length];
                    for (String stopid : stops.split(",")) {
                        this.stopid = stopid;
                        //doInBackground(new String[] {"hello"});
                        this.execute();
                    }*/
                    tempStop = stopid[num];
                    this.execute();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            protected String doInBackground(String... urls) {
                try {
                    //if (longitude != null && latitude != null) {
                        int TIMEOUT_MILLISEC = 10000;  // = 10 seconds
                        HttpParams httpParams = new BasicHttpParams();
                        HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
                        HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
                        HttpClient client = new DefaultHttpClient(httpParams);

                        HttpGet request = new HttpGet("http://143.44.78.173:8080/stop/getstoplocation?stopid=" + tempStop);

                        HttpResponse response = client.execute(request);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

                        //JSONObject root = new JSONObject(reader.readLine());
                        return reader.readLine();
                    //}
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                return "nah, it didn't work boi";
            }


            protected void onPostExecute(String result) {
                System.out.println(result);
                System.out.println("AHAHAHAHA");
                try {
                    JSONObject obj = new JSONObject(result);
                    if (latitude != null && longitude != null) {
                        prevLat = latitude;
                        prevLong = longitude;
                    }
                    latitude = Double.valueOf(obj.getInt("latitude"));
                    longitude = Double.valueOf(obj.getString("longitude"));
                    loc = obj.getString("address");
                    new retrieveCoordinates().execute();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
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
