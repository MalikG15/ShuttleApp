package lawrence.edu.shuttleme;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
// import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

import javax.net.ssl.HttpsURLConnection;


public class LocateShuttle extends Fragment implements OnMapReadyCallback {

    LocateShuttle locateshuttleobject;
    View rootView;
    GoogleMap mGoogleMap = null;
    private Double longitude = -88.4154;
    private Double lat = 44.2619;

    // Network URI
    public static final String hostName = "143.44.78.173:8080";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Create fragment to put map into fragment
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /*
        */
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        LatLng appleton = new LatLng(lat, longitude);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(appleton, 14.0f));

        locateshuttleobject = this;

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //CALL YOUR ASSYNC TASK HERE.

                //Execute asynctask that will repeatedly retrieve coordinates
                new RetrieveCoords(locateshuttleobject, "http://" + hostName  + "/shuttle/get?shuttleid=de748d23-d9f6-4f5b-8448-89bf0c5302e7").execute();
            }
        };

        Timer timer = new Timer();

        //DELAY: the time to the first execution
        //PERIODICAL_TIME: the time between each execution of your task.
        timer.schedule(timerTask, 1000, 10000);


    }

    // Interpret lat and long and show it on listview
    public void onRetrieveCoordinatesCompleted(String result) {

        // Parse data - get lat and long values
        try {
            JSONObject jObject = new JSONObject(result);
                try {
                    // Pulling items from the object
                    String lati = jObject.getString("latitude");
                    String longi = jObject.getString("longitude");

                    // Add a marker for Shuttle, and move the camera.
                    if (lati == null || longi == null) {
                    }//Throw alert that coordinates were not retrieved
                    else{
                        lat = Double.valueOf(lati);
                        longitude = Double.valueOf(longi);
                        LatLng shuttle = new LatLng(lat, longitude);

                        mGoogleMap.addMarker(new MarkerOptions()
                                .title("Shuttle")
                                .position(shuttle)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_shuttle)));
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(shuttle, 14.5f));
                        System.out.print(lat+longitude);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    } // THROW ALERT IF EMPTY - otherwise show coords


public class RetrieveCoords extends AsyncTask<String, String, String> {

    LocateShuttle caller;
    String uRL;

    public RetrieveCoords(LocateShuttle ls, String url) {
        super();
        this.caller=ls;
        uRL = url;
    }

    @Override
    protected void onPostExecute(String result) {
        caller.onRetrieveCoordinatesCompleted(result);
    }

    @Override
    protected String doInBackground(String... params) {

        String response = "";
        String responseError = "";
        HttpURLConnection conn = null;
        try{
            //Connect to URL
            URL url = new URL(uRL);

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
}

}