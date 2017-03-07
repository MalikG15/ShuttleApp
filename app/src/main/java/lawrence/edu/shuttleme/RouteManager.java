package lawrence.edu.shuttleme;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

public class RouteManager extends AppCompatActivity {

    // References to UI objects
    Button mAssignRouteButton;
    Button mDeleteRouteButton;
    Button mCreateStopButton;
    Button mManagerDriversButton;
    Button mCreateRouteButton;
    Spinner mSpinnerView;
    TextView mRouteDescriptionView;

    // Hashmap to store route name, id and their descriptions
    Map<String, List<String>> routes_and_info;

    // ArrayList to store database routes in order they'll appear on drop down menu
    List routes;

    // ArrayAdapter that will contain the list of routes to add to drop down menu
    ArrayAdapter<String> adapter;

    // Get current position of selected route in drop down menu
    int current_position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_manager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ShuttleMe");

        mSpinnerView = (Spinner) findViewById(R.id.route_list_spinner);
        mRouteDescriptionView = (TextView) findViewById(R.id.route_description);

        // routes = new ArrayList<String>();

        // Execute a task to retrieve routes from database
        new RetrieveRoutesTask(this, "http://143.44.78.173:8080/route/getall").execute();

        // Dummy Data
        /*String one = "Main Route";
        String two = "Second Route";
        routes.add(one);
        routes.add(two);
        descriptions = new HashMap<String, String>();
        descriptions.put(one, "This is the main route! Hehehehehee");
        descriptions.put(two, "This is the second route! You know this MANNNN");*/

        // Execute a AssignRoute task using selected route when button pressed
        mAssignRouteButton = (Button) findViewById(R.id.assign_route);
        mAssignRouteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                assignRoute();
            }
        });

        // Execute a DeleteRoute task using selected route when button pressed
        mDeleteRouteButton = (Button) findViewById(R.id.delete_route);
        mDeleteRouteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                deleteRoute();
            }
        });

        // Go to Create Stop Activity when button pressed
        mCreateStopButton = (Button) findViewById(R.id.create_stop);
        mCreateStopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                goToCreateStopActivity();
            }
        });

        // Go to Create Route Activity when button pressed
        mCreateRouteButton = (Button) findViewById(R.id.finalize_route);
        mCreateRouteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                goToCreateRouteActivity();
            }
        });

        // Go to Driver Manager Activity when button pressed
        mManagerDriversButton = (Button) findViewById(R.id.manage_drivers);
        mManagerDriversButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                goToDriverManagerActivity();
            }
        });

    }

    @Override
    public void onBackPressed() {
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

    // After response from server, parse JSON, get all routes
    // Store routes and present it on drop down menu
    public void onRetrieveRoutesCompleted(String result){

        // Store id and names of users
        routes = new ArrayList<String>();

        // Key = name of route, list will contain ID and description of each route
        routes_and_info = new HashMap<String, List<String>>();
        List<String> tempRouteList = new ArrayList<String>();

        // Parse data - get id and names
        try {
            JSONArray jArray = new JSONArray(result);
            for (int i=0; i < jArray.length(); i++)
            {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);

                    // Pulling items from the array
                    String route_id = oneObject.getString("routeid");
                    String route_name = oneObject.getString("name");
                    String route_description = oneObject.getString("description");
                    String route_assignment = oneObject.getString("assigned");
                    int assignment = Integer.valueOf(route_assignment);

                    // Put assigned route as the first one in the list
                    if(assignment == 0){
                        tempRouteList.add(route_name);
                    }
                    else{
                        routes.add(route_name);
                    }
                    // Add id and description and assignment to the temp info list
                    List<String> temporary_info_list = new ArrayList<String>();
                    temporary_info_list.add(route_id);
                    temporary_info_list.add(route_description);
                    temporary_info_list.add(route_assignment);

                    // Add name of the route with its list of id and description to the hashmap
                    routes_and_info.put(route_name, temporary_info_list);
                } catch (JSONException e) {
                    // Oops
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Add all other routes to route list to prepare for spinner
        for(String route : tempRouteList){
            routes.add(route);
        }

        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, routes);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        mSpinnerView.setAdapter(adapter);
        mSpinnerView.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                // Get list stored in hashmap by route name of position selection
                List<String> tempList = routes_and_info.get(routes.get(pos));

                // Index 1 stores descriptions
                String desc = tempList.get(1);

                // Update text view with route description
                mRouteDescriptionView.setText(desc);
                current_position = pos;
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    // Find route selected, update database
    public void assignRoute(){

        // Get list containing route info stored in hashmap by route name of position selection
        List<String> tempList = routes_and_info.get(routes.get(current_position));

        // Trying to assign the already assigned route
        // Check if route is assigned
        if(Integer.valueOf(tempList.get(2)) == 1){
            Toast.makeText(getApplicationContext(), "This is already the assigned route", Toast.LENGTH_SHORT).show();
        }
        // Change assignment of previously assigned route
        // And new assigned route
        else{
            // Index 0 stores id
            String newid = tempList.get(0);
            new changeAssignmentOfRouteTask(this, "http://143.44.78.173:8080/route/changeassignment?routeid="+newid).execute();

            tempList = routes_and_info.get(routes.get(0));
            if(Integer.valueOf(tempList.get(2)) == 1){
                String oldid = tempList.get(0);
                new changeAssignmentOfRouteTask(this, "http://143.44.78.173:8080/route/changeassignment?routeid="+oldid).execute();
            }

            // Execute a task to retrieve updated routes from database
            new RetrieveRoutesTask(this, "http://143.44.78.173:8080/route/getall").execute();
        }
    }

    // Delete route selected, update database
    public void deleteRoute(){

        // Get list stored in hashmap by route name of position selection
        List<String> tempList = routes_and_info.get(routes.get(current_position));

        // Index 0 stores id
        String id = tempList.get(0);
        // If trying to delete assigned route
        if(Integer.valueOf(tempList.get(2))==1){
            // Can't delete assigned route
            Toast.makeText(getApplicationContext(), "Cannot delete assigned route", Toast.LENGTH_SHORT).show();
        }
        else{
            new deleteRouteTask(this, "http://143.44.78.173:8080/route/deleteroute?routeid="+id).execute();
        }

    }

    // If successful in deleting
    // Refresh route, with current route on top
    public void onDeleteCompleted(Integer success){
        // If succeeding refresh current route
        if(success == 1){
            // Execute a task to retrieve routes from database
            new RetrieveRoutesTask(this, "http://143.44.78.173:8080/route/getall").execute();
            Log.d("Post Route Activity:"," Successfully deleted route");
        }
        if(success == 0){
            Log.d("Post Route Activity:"," Assigning/Deleting route failed");
        }
        else{
            Log.d("Post Route Activity:"," Server is down");
        }
    }

    public void onChangeCompleted(Integer success){
        // If succeeding refresh current route
        if(success == 1){
            Log.d("Post Route Activity:"," Successfully changed route");
        }
        if(success == 0){
            Log.d("Post Route Activity:"," Assigning/Deleting route failed");
        }
        else{
            Log.d("Post Route Activity:"," Server is down");
        }
    }
    // Go to create stop activity
    public void goToCreateStopActivity(){
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), CreateStop.class);
        startActivity(intent);
    }

    // Go to create route activity
    public void goToCreateRouteActivity(){
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), CreateRoute.class);
        startActivity(intent);
    }

    // Go to driver manager activity
    public void goToDriverManagerActivity(){
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), DriverManager.class);
        startActivity(intent);
    }

}

// Retrieve routes task

class RetrieveRoutesTask extends AsyncTask<String, String, String> {

    RouteManager caller;
    String uRL;

    public RetrieveRoutesTask(RouteManager cb, String url) {
        super();
        this.caller=cb;
        this.uRL = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        caller.onRetrieveRoutesCompleted(result);
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

// Assign route task
class
changeAssignmentOfRouteTask extends AsyncTask<String, String, Integer> {

    RouteManager caller;
    String uRL;

    public changeAssignmentOfRouteTask(RouteManager rm, String url) {
        super();
        this.caller=rm;
        this.uRL = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Integer result) {
        caller.onChangeCompleted(result);
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

    //and Delete route task
class deleteRouteTask extends AsyncTask<String, String, Integer> {

    RouteManager caller;
    String uRL;

    public deleteRouteTask(RouteManager rm, String url) {
        super();
        this.caller=rm;
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
