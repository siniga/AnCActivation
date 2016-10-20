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
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.aggreyclifford.active.ancactivation.R;
import com.aggreyclifford.active.ancactivation.endpoint.Endpoint;
import com.aggreyclifford.active.ancactivation.helpers.CustomDateFormat;
import com.aggreyclifford.active.ancactivation.models.AppHttp;
import com.aggreyclifford.active.ancactivation.models.RouteOutlet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutletActivationActivity_Horeka extends AppCompatActivity {
    private Spinner _outletsSpinner;
    private int _outletId;
    private ProgressDialog progressDialog;
    private CoordinatorLayout _snackBarView;
    private ArrayAdapter spinnerArrayAdapter;

    private static final String DEFAULT = "N/A";
    private static final int DEFAULTINT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlet_activation_activity__horeka);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _outletsSpinner = (Spinner) this.findViewById(R.id.outlet_spinner);


        //set select listner
        _outletsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                // Get the currently selected Brand object from the spinner
                RouteOutlet outlet = (RouteOutlet) _outletsSpinner.getSelectedItem();
                // Show it via a toast
                toastStateOutlet(outlet);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //        _spinner.setOnItemSelectedListener(this);
        progressDialog = new ProgressDialog(OutletActivationActivity_Horeka.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("loading data..");
        progressDialog.setCancelable(false);


        SharedPreferences preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        int userId = preferences.getInt("userId", DEFAULTINT);
        final int projectId = preferences.getInt("projectId", DEFAULTINT);
        final int teamId = preferences.getInt("teamId", DEFAULTINT);
        getOutlets(userId, projectId, teamId);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    public void toastStateOutlet(RouteOutlet routeOutlet) {
        if (routeOutlet != null) {
            _outletId = routeOutlet.getId();
            //Toast.makeText(OutletActivationActivity.this, "" + _outletId, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.outlet_menu, menu);
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
            Intent intent = new Intent(OutletActivationActivity_Horeka.this, MainActivity.class);
            intent.putExtra("isRefresh", "true");
            startActivity(intent);


        } else if (id == R.id.action_register_new_outlet) {
            Intent intent = new Intent(OutletActivationActivity_Horeka.this, OutletRegistrationActivity.class);
            startActivity(intent);


        } else if (id == R.id.action_logout) {


            Intent intent = new Intent(OutletActivationActivity_Horeka.this, LoginActivity.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    private void getOutlets(int userId, int projectId, int teamId) {
        progressDialog.show();

        //set url
        Endpoint.setUrl("models/ourOutlets.php");
        //log user
        AppHttp appHttp = new AppHttp();
        appHttp.postData(this, Endpoint.getRootUrl(), getBrandActivationParameters(userId, projectId, teamId), new AppHttp.VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                List<RouteOutlet> outlets = new ArrayList<>();

                // Log.d("success log", result);
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject result = jsonArray.getJSONObject(i);

                        //get outlets
                        JSONArray outletArray = result.getJSONArray("outlets");

                        for (int j = 0; j <= outletArray.length(); j++) {
                            JSONObject outletsObj = outletArray.getJSONObject(j);
                            outlets.add(new RouteOutlet(outletsObj.getString("outletName"), outletsObj.getInt("idOutlet")));

                            // Step 2: Create and fill an ArrayAdapter with a bunch of "Brand" objects
                            spinnerArrayAdapter = new ArrayAdapter(OutletActivationActivity_Horeka.this,
                                    R.layout.custom_spinner_textview, outlets);

                            // Step 3: Tell the spinner about our adapter
                            _outletsSpinner.setAdapter(spinnerArrayAdapter);
                            //Toast.makeText(OutletActivationActivity.this, "" + outletsObj.getString("outletName"), Toast.LENGTH_LONG).show();
                            progressDialog.hide();

                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    private Map<String, String> getBrandActivationParameters(int userId, int projectId, int teamId) {
        Map<String, String> params = new HashMap<>();
        //current date and time
        String dateNtime = CustomDateFormat.getFormattedDateAndTime();
        //Toast.makeText(this, ""+userId+" "+projectId+" "+teamId, Toast.LENGTH_LONG).show();
        // the POST parameters:
        params.put("idUser", "" + userId);
        params.put("idProject", "" + projectId);
        params.put("idTeam", "" + teamId);
        params.put("idTeam", "" + teamId);
        params.put("timeStamp", dateNtime);
        params.put("Gate", "getRouteOutletsMembers");
        return params;
    }
}
