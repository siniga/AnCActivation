package com.aggreyclifford.active.ancactivation.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aggreyclifford.active.ancactivation.R;
import com.aggreyclifford.active.ancactivation.adapters.PlacesAutoCompleteAdapter;
import com.aggreyclifford.active.ancactivation.endpoint.Endpoint;
import com.aggreyclifford.active.ancactivation.helpers.CustomDateFormat;
import com.aggreyclifford.active.ancactivation.models.AppHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RouteSettingActivity extends AppCompatActivity {

    private TextView _date;
    private Button _btnRoute;

    private static final String DEFAULT = "N/A";
    private static final int DEFAULTINT = 0;
    private String _routeName;

    private ProgressDialog progressDialog;
    private CoordinatorLayout _snackBarView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_route);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _date = (TextView) findViewById(R.id.date_txt);
        _snackBarView = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        getSupportActionBar().setTitle("Set Route");

        //set back button for the activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //format date then disolay it
        String date = CustomDateFormat.getFormattedDate("dd-MM-yyy");
        String day = CustomDateFormat.convertDateIntoDay(date);

        //set route spinner,date
        _date.setText(day + " " + date);





       // getRoutes(portalId, regionId);

       //location autocomplete
        AutoCompleteTextView autocompleteView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        autocompleteView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.card_places_autocomplete));

        autocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get data associated with the specified position
                // in the list (AdapterView)
                String description = (String) parent.getItemAtPosition(position);
                RouteSettingActivity.this._routeName = description;

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
                int userId = preferences.getInt("userId", DEFAULTINT);
                final int portalId = preferences.getInt("portalId", DEFAULTINT);
                String dateAndtime = CustomDateFormat.getFormattedDateAndTime();


               // Toast.makeText(RouteSettingActivity.this, dateAndtime, Toast.LENGTH_LONG).show();
                //Toast.makeText(RouteSettingActivity.this, "work" + activateId+" "+userId+" "+ portalId, Toast.LENGTH_LONG).show();
                setRoute(userId,portalId, _routeName, dateAndtime );
                //Toast.makeText(RouteSettingActivity.this , dateAndtime, Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog = new ProgressDialog(RouteSettingActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setCancelable(false);



    }

    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {

            Intent intent = new Intent(RouteSettingActivity.this, OutletActivationActivity.class);
            startActivity(intent);
            return true;
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intent, 0);

        return super.onOptionsItemSelected(item);
    }



    //set activation route
    private void setRoute(int userId, int portalId, String routename, String dateAndTime) {
        progressDialog.show();
        //set url
        Endpoint.setUrl("models/ourRoutes.php");
        //log user
        AppHttp appHttp = new AppHttp();
        appHttp.postData(this, Endpoint.getRootUrl(), getRouteActivationParameters(userId, portalId, routename, dateAndTime), new AppHttp.VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                        JSONObject result = jsonArray.getJSONObject(0);

                    progressDialog.hide();

                    SharedPreferences  preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("useActivity", result.getString("useActivity"));

                    Snackbar snackbar = Snackbar.make(_snackBarView, result.getString("txtError"), Snackbar.LENGTH_INDEFINITE)
                            .setAction("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            });;

                    snackbar.show();

                    //Toast.makeText(RouteSettingActivity.this, result.getString("txtError"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    progressDialog.hide();
                    e.printStackTrace();
                }

            }
        });
    }

    //set route activation paramenters
    public Map<String, String> getRouteActivationParameters(int userId,  int portalId, String routename, String dateAndTime) {
        Map<String, String> params = new HashMap<>();

        // the POST parameters:
        params.put("idUser", "" + userId);
        params.put("idPortal", "" + portalId);
        params.put("routeParams", routename);
        params.put("Gate", "activationRouteSetup");
        params.put("timeStamp", dateAndTime);
        return params;

    }

}
