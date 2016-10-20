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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aggreyclifford.active.ancactivation.R;
import com.aggreyclifford.active.ancactivation.adapters.BrandListAdapter;
import com.aggreyclifford.active.ancactivation.adapters.TeamMembersListAdapter;
import com.aggreyclifford.active.ancactivation.endpoint.Endpoint;
import com.aggreyclifford.active.ancactivation.helpers.CustomDateFormat;
import com.aggreyclifford.active.ancactivation.models.AppHttp;
import com.aggreyclifford.active.ancactivation.models.Brand;
import com.aggreyclifford.active.ancactivation.models.Member;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrandActivationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private TextView _date;
    private RecyclerView _list;
    private LinearLayoutManager _layoutManager;
    private BrandListAdapter _brandAdapter;
    ArrayAdapter spinnerArrayAdapter;
    static Spinner _spinner = null;

    private static final String DEFAULT = "N/A";
    private static final int DEFAULTINT = 0;
    private int _projectProductId;

    private ProgressDialog progressDialog;
    private CoordinatorLayout _snackBarView;

    private SharedPreferences preferences;
    private   SharedPreferences.Editor editor;

    EditText _awarenessCampain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_activation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _date = (TextView) findViewById(R.id.date_txt);
        _snackBarView = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        _spinner = (Spinner) this.findViewById(R.id.brand_spinner);
        _awarenessCampain = (EditText) findViewById(R.id.awareness_campaign);

        //set back button for the activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //format date then display it
        String date = CustomDateFormat.getFormattedDate("dd/MM/yyyy");
        String day = CustomDateFormat.convertDateIntoDay(date);

        //set route spinner,date
        _date.setText(day + " " + date);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
                int userId = preferences.getInt("userId", DEFAULTINT);
                //String dateAndtime = CustomDateFormat.getFormattedDateAndTime();
                setBrand(userId, _projectProductId, _awarenessCampain.getText().toString());
            }
        });


        _spinner.setOnItemSelectedListener(this);
        progressDialog = new ProgressDialog(BrandActivationActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("loadind data...");


        SharedPreferences preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        int projectId = preferences.getInt("projectId", DEFAULTINT);
        getBrand(projectId);
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
            Intent intent = new Intent(BrandActivationActivity.this, RouteSettingActivity.class);
            startActivity(intent);

            return true;
        }else if(id == R.id.action_logout){
            Intent intent = new Intent(BrandActivationActivity.this, LoginActivity.class);
            startActivity(intent);
           // finish();
        }

//        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//        startActivityForResult(intent, 0);

        return super.onOptionsItemSelected(item);
    }


    private void getBrand(int projectId) {
        progressDialog.show();

        //set url
        Endpoint.setUrl("models/actProjects.php");
        //log user
        AppHttp appHttp = new AppHttp();
        appHttp.postData(this, Endpoint.getRootUrl(), getBrandActivationParameters(projectId), new AppHttp.VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                List<Brand> brands = new ArrayList<>();

                // Log.d("success log", result);
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject result = jsonArray.getJSONObject(i);


                        Log.d("SUCCESSLOG", "" + result);


                        brands.add(new Brand(result.getInt("idprojectProduct"), result.getString("brandName")));

                        // Create and fill an ArrayAdapter with a bunch of "Brand" objects
                        spinnerArrayAdapter = new ArrayAdapter(BrandActivationActivity.this,
                                R.layout.custom_spinner_textview, brands);

                        progressDialog.hide();

                    }

                    //  Tell the spinner about our adapter
                    _spinner.setAdapter(spinnerArrayAdapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    //set activation route
    private void setBrand(int userId, int projProductId, String awarenesCampaign) {
        progressDialog.show();
        //Toast.makeText(BrandActivationActivity.this, "" + projProductId, Toast.LENGTH_LONG).show();
        //set url
        Endpoint.setUrl("models/actBrands.php");
        //log user
        AppHttp appHttp = new AppHttp();
        appHttp.postData(this, Endpoint.getRootUrl(), setBrandActivationParameters(userId, projProductId, awarenesCampaign), new AppHttp.VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject result = jsonArray.getJSONObject(0);
                    progressDialog.hide();

                    //show success msg
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

    private Map<String, String> setBrandActivationParameters(int userId, int projProductId, String awarenessCampaign) {
        Map<String, String> params = new HashMap<>();

        //current date and time
        String dateNtime = CustomDateFormat.getFormattedDateAndTime();

        // the POST parameters:
        params.put("idUser", "" + userId);
        params.put("idprojectProduct", "" + projProductId);
        params.put("brandCampaign", awarenessCampaign);
        params.put("timeStamp", dateNtime);
        params.put("Gate", "brandActivate");
        return params;
    }

    private Map<String, String> getBrandActivationParameters(int projectId) {
        Map<String, String> params = new HashMap<>();
        //Toast.makeText(this, "" + projectId, Toast.LENGTH_LONG).show();
        // the POST parameters:
        params.put("idProject", "" + projectId);
        params.put("Gate", "projBrands");
        return params;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Get the currently selected Brand object from the spinner
        Brand brand = (Brand) _spinner.getSelectedItem();
        // Show it via a toast
        toastState(brand);
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void toastState(Brand brand) {
        if (brand != null) {
            _projectProductId = brand.id;

         //   Toast.makeText(BrandActivationActivity.this, "" + _projectProductId, Toast.LENGTH_LONG).show();
        }
    }

}
