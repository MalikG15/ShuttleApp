package lawrence.edu.shuttleme;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class CreateRoute extends AppCompatActivity {

    // References to UI objects
    Button mFinalizeButton;
    Button mAddToRouteButton;
    Button mDeleteStopButton;
    Spinner mSpinnerView;
    ListView mRouteList;

    // Network URI
    public static final String hostName = "143.44.78.173:8080";

    // Hashmap to store route name, id and their descriptions
    Map<String, String> stops_and_id;

    // List to store database stops in order they'll appear on drop down menu
    List<String> stops;

    // ArrayList to store stops that are being added to route
    ArrayList<String> temporary_route;

    // ArrayAdapter that will contain the list of stops to add to drop down menu
    ArrayAdapter<String> adapter;
    CustomAdapter customadapter;

    // Get current position of selected route in drop down menu
    int current_position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_route);

        temporary_route = new ArrayList<String>();

        mSpinnerView = (Spinner) findViewById(R.id.stop_list_spinner);

        // Execute a task to retrieve stops from database
        new RetrieveStopsTask(this, "http://"+hostName+"/stop/all").execute();

        // Execute a AddStopToRoute task using selected stop when button pressed
        mAddToRouteButton = (Button) findViewById(R.id.add_stop_to_route);
        mAddToRouteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                addStopToRoute();
            }
        });

        mDeleteStopButton = (Button) findViewById(R.id.delete_stop);
        mDeleteStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteStop();
            }
        });

        mFinalizeButton = (Button) findViewById(R.id.finalize_route);
        mFinalizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRoute();
            }
        });

        //instantiate custom adapter
        customadapter = new CustomAdapter(temporary_route, this);

        //handle listview and assign adapter
        mRouteList = (ListView) findViewById(R.id.stop_list_for_route);
        mRouteList.setAdapter(customadapter);


    }

    public void addStopToRoute(){

        // Add currently selected stop to temporary route if it's not already there
        if(temporary_route.contains(stops.get(current_position))){
            Toast.makeText(getApplicationContext(), "This stop is already on the route", Toast.LENGTH_SHORT).show();
        }
        else{
            temporary_route.add(stops.get(current_position));

            //instantiate custom adapter
            customadapter = new CustomAdapter(temporary_route, this);

            //handle listview and assign adapter
            mRouteList = (ListView) findViewById(R.id.stop_list_for_route);
            mRouteList.setAdapter(customadapter);
        }
    }

    public void deleteStop(){

        // Get id stored in hashmap by stop name of position selection
        String id = stops_and_id.get(stops.get(current_position));

        new deleteStopTask(this, "http://"+hostName+"/stop/delete?stopid="+id).execute();
    }


    public void createRoute(){
        Log.d("Pressed finalize: ", "succeeded");
        //generates description
        //checks if temporary list is large enough > 1
        // route name, desc, stop list, url, this

        if(temporary_route.size()>1){
            String routename = "";
            String description = "";
            String stoplist = "";
            int count = 0;
            for(String stop : temporary_route){
                String id = stops_and_id.get(stop);
                if(count == 0){
                    description = "Shuttle will go to " + stop;
                    routename = stop;
                    stoplist = id;
                }
                else{
                    description = description + " then to " + stop;
                    routename = routename + "-" + stop;
                    stoplist = stoplist+","+id;
                }
                count++;
            }
            description = description + " and then return back to " + temporary_route.get(0);
            new createRouteTask(routename,description,stoplist,hostName, this).execute();

        }
        else{
            Toast.makeText(getApplicationContext(), "Route needs to have at least two stops", Toast.LENGTH_SHORT).show();
        }
    }

    // After response from server, parse JSON, get all routes
    // Store routes and present it on drop down menu
    public void onRetrieveStopsCompleted(String result){

        // Store id and names of users
        stops = new ArrayList<String>();

        ArrayList<String> tempStopList = new ArrayList<String>();

        // Key = name of route, list will contain ID and description of each route
        stops_and_id = new HashMap<String, String>();

        // Parse data - get id and names
        try {
            JSONArray jArray = new JSONArray(result);
            for (int i=0; i < jArray.length(); i++)
            {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);

                    // Pulling items from the array
                    String stop_id = oneObject.getString("stopid");
                    String stop_name = oneObject.getString("name");

                    // Add name of the stop with its id to hashmap and add name to list of stops to display on spinner
                    stops_and_id.put(stop_name, stop_id);

                    // Put Plantz Hall as the first stop in the list
                    if(stop_name.equals("Plantz Hall")){
                        stops.add(stop_name);
                    }
                    // Otherwise put it in a temp list of stops
                    else{
                        tempStopList.add(stop_name);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Add the rest of stops to stop list
        for(String stop : tempStopList){
            stops.add(stop);
        }

        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stops);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        mSpinnerView.setAdapter(adapter);
        mSpinnerView.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                current_position = pos;
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    // If successful in deleting
    // Refresh stops, with current route on top
    public void onDeleteCompleted(Integer success) {

        // If succeeding refresh current stops
        if (success == 1) {
            // Execute a task to retrieve stops from database
            new RetrieveStopsTask(this, "http://" + hostName + "/stop/all").execute();
            Log.d("Post Stop Activity:", " Successfully deleted stop");
            Toast.makeText(getApplicationContext(), "Successfully deleted stop", Toast.LENGTH_SHORT).show();
        }
        if (success == 0) {
            Log.d("Post Stop Activity:", " Assigning/Deleting route failed");
            Toast.makeText(getApplicationContext(), "Assigning/Deleting route failed", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("Post Stop Activity:", " Server is down");
            Toast.makeText(getApplicationContext(), "Server is down, failed to assign route", Toast.LENGTH_SHORT).show();
        }
    }

    // Return to Route Manager activity if creation is successful
    public void onCreationCompleted(Integer success) {
        if (success == 1) {

            Context context = getApplicationContext();
            CharSequence text = "Successfully Created Route!";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), RouteManager.class);
            startActivity(intent);

            Log.d("Create Route Activity", "Route Creation successful: " + success);
            Toast.makeText(getApplicationContext(), "Route Creation successful", Toast.LENGTH_SHORT).show();
        } else if (success == 0) {

            Log.d("Create Route Activity", "Failed to create route: " + success);
            Toast.makeText(getApplicationContext(), "Failed to create route", Toast.LENGTH_SHORT).show();
            // TODO: Need to notify the user that the email given already exists
        } else {
            Log.d("Create Route Activity", "Server issue:  " + success);
            Toast.makeText(getApplicationContext(), "Failed to create route due to server issues", Toast.LENGTH_SHORT).show();
            // TODO: Some useful message about the error
        }

    }


    // Task retrieves stops
    class RetrieveStopsTask extends AsyncTask<String, String, String> {

        CreateRoute caller;
        String uRL;

        public RetrieveStopsTask(CreateRoute cr, String url) {
            super();
            this.caller=cr;
            this.uRL = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
           caller.onRetrieveStopsCompleted(result);
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

    //and Delete route task
    class deleteStopTask extends AsyncTask<String, String, Integer> {

        CreateRoute caller;
        String uRL;

        public deleteStopTask(CreateRoute cr, String url) {
            super();
            this.caller=cr;
            this.uRL = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer result) {
            caller.onDeleteCompleted(result);
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
                    return Integer.valueOf(response);
                }
                else if(responseCode == HttpURLConnection.HTTP_CONFLICT){
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    while ((line=br.readLine()) != null) {
                        responseError+=line;
                    }

                    return Integer.valueOf(responseError);
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
            return Integer.valueOf(response);
        }
    }


    class createRouteTask extends AsyncTask<String, Void, Integer> {

        private final String uri;
        private String json;
        private CreateRoute callback;

        // Create stop based off the required fields for stops
        createRouteTask( String routename, String description, String stoplist, String hostName, CreateRoute cb) {
            uri = "http://" + hostName + "/route/create";
            json = "{\"name\":" + "\"" + routename + "\"" + ",\"description\":" + "\"" + description + "\"" + ",\"stops\":" + "\"" + stoplist + "\"" +"}";
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
            }
            return return_value;
        }

        protected void onPostExecute(final Integer success) {
            callback.onCreationCompleted(success);
        }
    }
}
