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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.aggreyclifford.active.ancactivation.R;
import com.aggreyclifford.active.ancactivation.endpoint.Endpoint;
import com.aggreyclifford.active.ancactivation.helpers.CustomDateFormat;
import com.aggreyclifford.active.ancactivation.models.AppHttp;
import com.aggreyclifford.active.ancactivation.models.Brand;
import com.aggreyclifford.active.ancactivation.models.Member;
import com.aggreyclifford.active.ancactivation.models.RouteMember;
import com.aggreyclifford.active.ancactivation.models.RouteOutlet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutletActivationActivity extends AppCompatActivity {
    private static Spinner _outletsSpinner = null;
    private static Spinner _membersSpinner = null;
    private ProgressDialog progressDialog;
    private CoordinatorLayout _snackBarView;
    private ArrayAdapter spinnerArrayAdapter;
    private ArrayAdapter spinnerArrayAdapter1;
    private int _outletId, _memberId;

    private static final String DEFAULT = "N/A";
    private static final int DEFAULTINT = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlet_activation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set back button for the activity
      //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        _snackBarView = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        _outletsSpinner = (Spinner) this.findViewById(R.id.outlet_spinner);
        _membersSpinner = (Spinner) findViewById(R.id.member_spinner);

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

        _membersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                RouteMember member = (RouteMember) _membersSpinner.getSelectedItem();
                // Show it via a toast
                toastStateMember(member);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        _spinner.setOnItemSelectedListener(this);
        progressDialog = new ProgressDialog(OutletActivationActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("loading data..");
        progressDialog.setCancelable(false);


        SharedPreferences preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        int userId = preferences.getInt("userId", DEFAULTINT);
        final int projectId = preferences.getInt("projectId", DEFAULTINT);
        final int teamId = preferences.getInt("teamId", DEFAULTINT);
        getOutlets(userId, projectId, teamId);
        getMembers(userId, projectId, teamId);
        //Toast.makeText(this,""+userId+ " "+projectId + " "+ teamId,Toast.LENGTH_LONG).show();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
                int userId = preferences.getInt("userId", DEFAULTINT);
                //String dateAndtime = CustomDateFormat.getFormattedDateAndTime();
                setOutletMembers(userId, _outletId, _memberId);
            }
        });

    }



    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }


    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.dismiss();
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
            Intent intent = new Intent(OutletActivationActivity.this, MainActivity.class);
            intent.putExtra("isRefresh", "true");
            startActivity(intent);


        }else if (id == R.id.action_register_new_outlet) {
            Intent intent = new Intent(OutletActivationActivity.this, OutletRegistrationActivity.class);
            startActivity(intent);



        }else if(id == R.id.action_logout){

            Intent intent = new Intent(OutletActivationActivity.this, LoginActivity.class);
            startActivity(intent);

        }

//        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//        startActivityForResult(intent, 0);

        return super.onOptionsItemSelected(item);
    }


    public void toastStateOutlet(RouteOutlet routeOutlet) {
        if (routeOutlet != null) {
            _outletId = routeOutlet.getId();
            //Toast.makeText(OutletActivationActivity.this, "" + _outletId, Toast.LENGTH_LONG).show();
        }
    }
    private void toastStateMember(RouteMember routeMember) {
        if (routeMember != null) {
            _memberId = routeMember.getId();
          //  Toast.makeText(OutletActivationActivity.this, "" + _memberId, Toast.LENGTH_LONG).show();
        }
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
                            spinnerArrayAdapter = new ArrayAdapter(OutletActivationActivity.this,
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


    private void getMembers(int userId, int projectId, int teamId) {
        progressDialog.show();

        //set url
        Endpoint.setUrl("models/ourOutlets.php");
        //log user
        AppHttp appHttp = new AppHttp();
        appHttp.postData(this, Endpoint.getRootUrl(), getBrandActivationParameters(userId, projectId, teamId), new AppHttp.VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                List<RouteMember> members = new ArrayList<>();


                // Log.d("success log", result);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject result = jsonArray.getJSONObject(i);

                        //get members
                        JSONArray membersArray = result.getJSONArray("members");

                        for (int k = 0; k <= membersArray.length(); k++) {
                            JSONObject membersObj = membersArray.getJSONObject(k);
                            members.add(new RouteMember(membersObj.getString("fullName"),membersObj.getInt("idUser")));

                            // Step 2: Create and fill an ArrayAdapter with a bunch of "Brand" objects
                            spinnerArrayAdapter1 = new ArrayAdapter(OutletActivationActivity.this,
                                    R.layout.custom_spinner_textview, members);

                            // Step 3: Tell the spinner about our adapter
                            _membersSpinner.setAdapter(spinnerArrayAdapter1);

                            progressDialog.hide();

                        }
                        Log.d("SUCCESSLOG", "" + result);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.hide();
                }

                progressDialog.hide();

            }
        });
    }

    //set activation outlets and members
    private void setOutletMembers(int userId, int outletId, int ambassadorId) {
        progressDialog.show();

        //set url
        Endpoint.setUrl("models/ourOutlets.php");
        //log user
        AppHttp appHttp = new AppHttp();
        appHttp.postData(this, Endpoint.getRootUrl(), setOutletActivationParameters(userId, outletId,  ambassadorId), new AppHttp.VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject result = jsonArray.getJSONObject(0);
                    progressDialog.hide();

                    Snackbar snackbar = Snackbar.make(_snackBarView, result.getString("txtError"), Snackbar.LENGTH_INDEFINITE)
                            .setAction("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            });


                    snackbar.show();
//                        .setAction("Action", null).show();
                    // Toast.makeText(BrandActivationActivity.this, result.getString("txtError"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    progressDialog.hide();
                    e.printStackTrace();
                }

            }
        });
    }

    private Map<String, String> setOutletActivationParameters(int userId, int outletId, int ambassadorId) {
        Map<String, String> params = new HashMap<>();


        //current date and time
        String dateNtime = CustomDateFormat.getFormattedDateAndTime();
        //Toast.makeText(this, ""+userId +" "+outletId+ " "+ambassadorId +" date:"+dateNtime,Toast.LENGTH_LONG).show();

        // the POST parameters:
        params.put("idUser", "" + userId);
        params.put("idOutlet", "" + outletId);
        params.put("idAmbassador", ""+ ambassadorId);
        params.put("timeStamp", dateNtime);
        params.put("Gate", "outletSignin");
        return params;
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
