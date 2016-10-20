package com.aggreyclifford.active.ancactivation.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.aggreyclifford.active.ancactivation.R;
import com.aggreyclifford.active.ancactivation.adapters.kyc_adapters.GenderAdapter;
import com.aggreyclifford.active.ancactivation.endpoint.Endpoint;
import com.aggreyclifford.active.ancactivation.helpers.CustomDateFormat;
import com.aggreyclifford.active.ancactivation.models.AppHttp;
import com.aggreyclifford.active.ancactivation.models.Route;
import com.aggreyclifford.active.ancactivation.models.RouteOutlet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutletRegistrationActivity extends AppCompatActivity {
    private static Spinner _outletRoute = null;
    private static Spinner _outletType = null;
    private String[] _outletValues = {"Retail", "Horeka"};
    private int outletTypeId;
    private EditText _outletName;
    private ProgressDialog progressDialog;
    private ArrayAdapter spinnerArrayAdapter;
    private static final String DEFAULT = "N/A";
    private static final int DEFAULTINT = 0;
    private CoordinatorLayout _snackBarView;
    private int _routeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlet_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _outletRoute = (Spinner) this.findViewById(R.id.outlet_route);
        _outletType = (Spinner) this.findViewById(R.id.outlet_type);
        _outletName = (EditText) findViewById(R.id.outlet_name);
        _outletType.setAdapter(new GenderAdapter(this, R.layout.custom_spinner, _outletValues));
        _snackBarView = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);


        _outletType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String outletType = _outletType.getSelectedItem().toString();

                if (outletType.equals("Retail")) {
                    outletTypeId = 2;//retails
                } else {
                    outletTypeId = 1;//houreka
                }
                //  Toast.makeText(c, "" + _ageSpinner, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //set select listner
        _outletRoute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                // Get the currently selected Brand object from the spinner
                RouteOutlet outlet = (RouteOutlet)  _outletRoute.getSelectedItem();
                // Show it via a toast
                toastStateRoute(outlet);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        _outletType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//                String outletType = _outletType.getSelectedItem().toString();
//
//                if (outletType.equals("Retail")) {
//                    outletTypeId = 2;
//                } else {
//
//                    outletTypeId = 1;
//                }
//                //  Toast.makeText(c, "" + _ageSpinner, Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        progressDialog = new ProgressDialog(OutletRegistrationActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("loading data..");
        progressDialog.setCancelable(false);





        //get logged user sharedpreferences
        SharedPreferences preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        final int portalId = preferences.getInt("portalId", DEFAULTINT);
        int regionId = preferences.getInt("regionId", DEFAULTINT);

        Log.d("IDPORTAL:",""+regionId+" "+regionId);
        getRoute(regionId);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
                int userId = preferences.getInt("userId", DEFAULTINT);
                registerOutlets(userId, outletTypeId,  _outletName.getText().toString(),_routeId);


             //   Toast.makeText(OutletRegistrationActivity.this, "" + outletTypeId + " " + _outletName.getText().toString(), Toast.LENGTH_LONG).show();
            }
        });
    }



    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.outlet_registration_menu, menu);
        MenuItem item = menu.findItem(R.id.action_done);
        item.setVisible(false);
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
            SharedPreferences preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
            String outletType = preferences.getString("outletTypes", DEFAULT);


            if(outletType.equals("Retails")){
                Intent intent = new Intent(this, OutletActivationActivity.class);//TODO use fragments instead of activities
                startActivity(intent);
            }else {
                Intent intent = new Intent(this, OutletActivationActivity_Horeka.class);//TODO use fragments instead of activities
                startActivity(intent);
            }

            return true;
        }else if (id == R.id.action_logout) {

            //clean all the session data then log user out
//            SharedPreferences preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.clear();
//            editor.commit();

            Intent intent = new Intent(OutletRegistrationActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        }

//        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//        startActivityForResult(intent, 0);

        return super.onOptionsItemSelected(item);
    }


    private void getRoute(int regionId) {
        progressDialog.show();

        final List<RouteOutlet> routes = new ArrayList<>();
        //set url
        Endpoint.setUrl("models/ourRoutes.php");
        //log user
        AppHttp appHttp = new AppHttp();
        appHttp.postData(this, Endpoint.getRootUrl(), getRouteParameters(regionId), new AppHttp.VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                try {

                    JSONArray jsonArray = new JSONArray(response);

                    for (int j = 0; j <= jsonArray.length(); j++) {
                        JSONObject result  = jsonArray.getJSONObject(j);
                       routes.add(new RouteOutlet(result.getString("routeName"),result.getInt("idRoute")));

                        // Step 2: Create and fill an ArrayAdapter with a bunch of "route" objects
                        spinnerArrayAdapter = new ArrayAdapter(OutletRegistrationActivity.this,
                                R.layout.custom_spinner_textview, routes);

                        //  Tell the spinner about our adapter
                        _outletRoute.setAdapter(spinnerArrayAdapter);
                    }
                   progressDialog.hide();




                } catch (JSONException e) {
                    progressDialog.hide();
                    e.printStackTrace();
                }

            }
        });
    }

    private void registerOutlets(int userId, int typeId, String outletname,int routeId) {

        progressDialog.show();

        //set url
        Endpoint.setUrl("models/ourOutlets.php");
        //log user
        AppHttp appHttp = new AppHttp();
        appHttp.postData(this, Endpoint.getRootUrl(), setOutletRegistrationParameters(userId, typeId,outletname, routeId), new AppHttp.VolleyCallback() {
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
                                    Snackbar.make(view, "Assign another outlet or press done to continue. ", Snackbar.LENGTH_LONG)
                                            .setAction("Ok", null).show();
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
    private void toastStateRoute(RouteOutlet route) {
        if (route != null) {
            _routeId = route.getId();
            //Toast.makeText(OutletRegistrationActivity.this, "" + _routeId, Toast.LENGTH_LONG).show();
        }
    }

    private Map<String, String> setOutletRegistrationParameters(int userId, int typeId, String outletname,int routeId) {
        Map<String, String> params = new HashMap<>();


        //current date and time
        String dateNtime = CustomDateFormat.getFormattedDateAndTime();
        //Toast.makeText(this, ""+userId + " "+typeId +" date:"+dateNtime,Toast.LENGTH_LONG).show();

        // the POST parameters:
        params.put("idUser", "" + userId);
        params.put("idType", "" + typeId);
        params.put("idRoute", "" + routeId);
        params.put("timeStamp", dateNtime);
        params.put("outletName",outletname );
        params.put("Gate", "outletRegister");
        return params;
    }
    private Map<String, String> getRouteParameters(int regionId) {
        Map<String, String> params = new HashMap<>();

        // the POST parameters:
        params.put("idRegion", "" + regionId);
        params.put("Gate", "activationRoute");
        return params;
    }
}
