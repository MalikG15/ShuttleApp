package lawrence.edu.shuttleme;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.SupportMapFragment;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


public class Tab1Location extends Fragment {
    private boolean isCheckedIn = false;

    private String id;
    private ToggleButton toggleButton;

    public static final String hostName = "143.44.78.173:8080";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.tab1location,container,false);
        id = ((PassengerActivity) getActivity()).getUserID();
        toggleButton = (ToggleButton) rootView.findViewById(R.id.checkInButton) ;
/*        toggleButton.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {

            }
        });*/
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInNOut();
            }
        });

        //Check checked-in status
        new getCheckInStatus(this, "http://" + hostName  + "/clipboard/status?userid="+id).execute();

        return rootView;
    }

    public void checkInNOut(){
         if(isCheckedIn==false){
                new checkIn(this, "http://" + hostName  + "/clipboard/checkin", id).execute();
         }
         else{
             new checkOut(this, "http://" + hostName + "/clipboard/checkout?userid=" + id).execute();
             isCheckedIn = false;
         }
    }

    // Update toggle button with necessary option
    public void onCheckOutCompleted(Integer result){
        Log.d("Check out Activity: ", String.valueOf(result));
        Log.d("ID: ", id);
        if(Integer.valueOf(result) == -1){
            Toast.makeText(((PassengerActivity) getActivity()).getApplicationContext(), "You were not able to check out", Toast.LENGTH_SHORT).show();
            toggleButton.setChecked(true);
        }
        if(Integer.valueOf(result)==0){
            Toast.makeText(((PassengerActivity) getActivity()).getApplicationContext(), "You have successfully checked out", Toast.LENGTH_SHORT).show();
            isCheckedIn = false;
        }
    }

    // Update toggle button with necessary action
    public void onCheckInCompleted(Integer result){
        Log.d("Check In Attempt: ", String.valueOf(result));
        if(Integer.valueOf(result) == 0){
            Toast.makeText(((PassengerActivity) getActivity()).getApplicationContext(), "You were not able to check in", Toast.LENGTH_SHORT).show();
            toggleButton.setChecked(false);

        }
        else{
            Toast.makeText(((PassengerActivity) getActivity()).getApplicationContext(), "You have successfully checked in", Toast.LENGTH_SHORT).show();
            isCheckedIn = true;
        }
    }

    public void onGetCheckedInStatusCompleted(String result){
        int res = Integer.valueOf(result);
        //Fail
        if(res == 0){
            // Not checked in
        }//Success - checked in so toggle button to true;
        if(res == 1){
            toggleButton.setChecked(true);
            isCheckedIn = true;
        }
    }

}

// Sends check in/out information to server
class checkIn extends AsyncTask<String, String, Integer> {

    Tab1Location caller;
    String uRL;
    String json;

    public checkIn(Tab1Location t1l, String url, String id) {
        super();
        this.caller=t1l;
        this.uRL = url;
        json = "{\"userid\":" + id + ",\"latitude\":" + "\"44\"" + ",\"longitude\":" + "\"-88\"" + ",\"clipboardsessionid\":" + "\"1\"}";
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
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

            HttpPost request = new HttpPost(uRL);
            request.setEntity(new ByteArrayEntity(
                    json.toString().getBytes("UTF8")));
            HttpResponse response = client.execute(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            String result = reader.readLine();
            return_value = Integer.valueOf(result);

            return return_value;

        } catch(Exception ex) {
            Log.d("Check In Activity","Exception in doPost:" + ex.toString());
        }
        return return_value;
    }

    protected void onPostExecute(final Integer response) {
        caller.onCheckInCompleted(response);
    }

}

class checkOut extends AsyncTask<String, String, Integer> {

    Tab1Location caller;
    String uRL;

    public checkOut(Tab1Location t1l, String url) {
        super();
        this.caller=t1l;
        this.uRL = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected Integer doInBackground(String... params) {
        int return_value = -1;

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
                return_value = Integer.valueOf(response);
                return return_value;
            }
            else if(responseCode == HttpURLConnection.HTTP_CONFLICT){
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                while ((line=br.readLine()) != null) {
                    responseError+=line;
                }
                return_value = Integer.valueOf(responseError);
                return return_value;
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
        return return_value;
    }

    protected void onPostExecute(final Integer response) {
        caller.onCheckOutCompleted(response);
    }

}class getCheckInStatus extends AsyncTask<String, String, String> {

    Tab1Location caller;
    String uRL;

    public getCheckInStatus(Tab1Location dm, String url) {
        super();
        this.caller=dm;
        this.uRL = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        caller.onGetCheckedInStatusCompleted(result);
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