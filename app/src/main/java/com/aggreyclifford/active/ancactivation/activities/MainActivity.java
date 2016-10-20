package com.aggreyclifford.active.ancactivation.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aggreyclifford.active.ancactivation.R;
import com.aggreyclifford.active.ancactivation.adapters.TeamMembersListAdapter;
import com.aggreyclifford.active.ancactivation.endpoint.Endpoint;
import com.aggreyclifford.active.ancactivation.helpers.CustomDateFormat;
import com.aggreyclifford.active.ancactivation.models.AppHttp;
import com.aggreyclifford.active.ancactivation.models.Member;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RecyclerView _list;
    private LinearLayoutManager _layoutManager;
    private TeamMembersListAdapter _teamAdapter;
    private TextView _username, _sumEc, _sumEr, _sumSob;
    private String _brandname;

    ProgressDialog progressDialog;

    private static final String DEFAULT = "N/A";
    private static final int DEFAULTINT = 0;
    private String useActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // getSharedUserdata();

//                SharedPreferences preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
//                String useActivity = preferences.getString("useActivity", DEFAULT);
//
//                Toast.makeText(MainActivity.this,useActivity, Toast.LENGTH_LONG).show();

                progressDialog.show();
                refreshData("false");

            }
        });


        _sumEc = (TextView) findViewById(R.id.sum_ec);
        _sumEr = (TextView) findViewById(R.id.sum_er);
        _sumSob = (TextView) findViewById(R.id.sum_sob);

        _username = (TextView) findViewById(R.id.username_txt);
        _list = (RecyclerView) findViewById(R.id.list);
        _list.setHasFixedSize(true);


        _layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        _list.setLayoutManager(_layoutManager);

        _list.setNestedScrollingEnabled(false);

        // _loginButton.setEnabled(false);
        progressDialog = new ProgressDialog(MainActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setCancelable(false);


        //get data from login activity
        Bundle bundle = getIntent().getExtras();
        String isRefresh = bundle.getString("isRefresh");

        //if data returned from login activity
//        if(isRefresh.equals("false")){
//            getSharedUserdata();
//        }else{
//
//        }

        //preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        getSharedUserdata();


    }

    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // refreshData();
        //   Toast.makeText(this, "works on resume", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            //clean all the session data then log user out
//            SharedPreferences preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.clear();
//            editor.commit();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            //finish();

            return true;
        } else if (id == R.id.action_refresh) {
            getSharedUserdata();

        }

        return super.onOptionsItemSelected(item);
    }

    private void getSharedUserdata() {
        SharedPreferences preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        String uname = preferences.getString("fullname", DEFAULT);
        String brandname = preferences.getString("brandname", DEFAULT);
        int teamId = preferences.getInt("teamId", DEFAULTINT);
        // Toast.makeText(this,""+uname, Toast.LENGTH_LONG).show();

        if (uname.equals(DEFAULT) || teamId == DEFAULTINT) {
            Toast.makeText(this, "Cant not retrive team members", Toast.LENGTH_LONG).show();
        } else {
            getTeamMembers(teamId);
            _username.setText(uname);
            //Toast.makeText(this,"brand name"+brandname, Toast.LENGTH_LONG).show();
            if (brandname.equals(DEFAULT)) {
                getSupportActionBar().setTitle("Brand is not set yet");
            } else {
                Log.d("MainActivity", brandname);
                getSupportActionBar().setTitle(brandname);
            }


        }

    }

    private void refreshData(String refreshState) {

        SharedPreferences preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        int uId = preferences.getInt("userId", DEFAULTINT);
        String outletTypes = preferences.getString("outletTypes", DEFAULT);

        //Toast.makeText(this, "" + uId, Toast.LENGTH_LONG).show();
        if (uId != DEFAULTINT) {
            getNewData(uId, refreshState, outletTypes);
        }
    }

    private void getNewData(int userId, final String refreshState, final String outletType) {
        //set url
        Endpoint.setUrl("models/userLogin.php");
        //log user
        AppHttp appHttp = new AppHttp();
        appHttp.postData(this, Endpoint.getRootUrl(), getNewDataParameters(userId), new AppHttp.VolleyCallback() {
            @Override
            public void onSuccess(String response) {

                // Log.d("success log", result);
                try {
                    JSONObject result = new JSONObject(response);
                    //Log.d("Fullname:",""+result.getString("fullName"));
                    getSupportActionBar().setTitle(result.getString("brandName"));

                    //share new data
                    // _brandname = result.getString("brandName");
//                    SharedPreferences  preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = preferences.edit();
//                    editor.putString("useActivity", result.getString("useActivity"));
//                    editor.commit();

                    String useActivity = result.getString("useActivity");
                    // Toast.makeText(MainActivity.this, useActivity+" "+"why route activate", Toast.LENGTH_LONG).show();

                    if (refreshState.equals("false")) {

                        if (useActivity.equals("Route Activate")) {
                            Intent intent = new Intent(MainActivity.this, RouteSettingActivity.class);
                            startActivity(intent);
                        } else if (useActivity.equals("Brand Activate")) {
                            Intent intent = new Intent(MainActivity.this, BrandActivationActivity.class);
                            startActivity(intent);

                        } else if (useActivity.equals("Outlets Assigning")) {

                            if(outletType.equals("Retails")){
                                Intent intent = new Intent(MainActivity.this, OutletActivationActivity.class);//TODO use fragments instead of activities
                                startActivity(intent);
                            }else {
                                Intent intent = new Intent(MainActivity.this, OutletActivationActivity_Horeka.class);//TODO use fragments instead of activities
                                startActivity(intent);
                            }


                            // Toast.makeText(MainActivity.this,useActivity, Toast.LENGTH_LONG).show();
                        } else {
                            //Toast.makeText(MainActivity.this,useActivity, Toast.LENGTH_LONG).show();
                        }
                    }

                    progressDialog.hide();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void getTeamMembers(int teamId) {
        progressDialog.show();
        //set url
        Endpoint.setUrl("models/userLogin.php");
        //log user
        AppHttp appHttp = new AppHttp();
        appHttp.postData(this, Endpoint.getRootUrl(), getParameters(teamId), new AppHttp.VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                List<Member> members = new ArrayList<>();

                // Log.d("success log", result);
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject result = jsonArray.getJSONObject(i);
                        Log.d("SUCCESSLOG", "" + result.getString("fullName"));
                        //set ec,er,sob
                        members.add(new Member(result.getString("fullName"), result.getString("EC"), result.getString("ER"), result.getString("SOB")));
                        _teamAdapter = new TeamMembersListAdapter(MainActivity.this, members);
                        _list.setAdapter(_teamAdapter);

                        //set sum of ec,er,sob
                        _sumEc.setText(result.getString("sumEC"));
                        _sumEr.setText(result.getString("sumER"));
                        _sumSob.setText(result.getString("sumSOB"));
                        progressDialog.hide();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    //chat post parameters
    public Map<String, String> getParameters(int teamId) {
        Map<String, String> params = new HashMap<>();

        //current date and time
        String dateNtime = CustomDateFormat.getFormattedDateAndTime();

        // the POST parameters:
        params.put("idTeam", "" + teamId);
        params.put("timeStamp", dateNtime);
        params.put("Gate", "teamMembers");
        return params;

    }

    private Map<String, String> getNewDataParameters(int userId) {
        Map<String, String> params = new HashMap<>();

        //current date and time
        String dateNtime = CustomDateFormat.getFormattedDateAndTime();
        // the POST parameters:
        params.put("idUser", "" + userId);
        params.put("timeStamp", dateNtime);
        params.put("Gate", "dataRefresh");
        return params;
    }

}
