package com.aggreyclifford.active.ancactivation.activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aggreyclifford.active.ancactivation.R;
import com.aggreyclifford.active.ancactivation.endpoint.Endpoint;
import com.aggreyclifford.active.ancactivation.helpers.CustomDateFormat;
import com.aggreyclifford.active.ancactivation.helpers.CustomNetworkManager;
import com.aggreyclifford.active.ancactivation.models.AppHttp;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";

    private EditText _usernameText;
    private EditText _passwordText;
    private Button _loginButton;
    private CoordinatorLayout _snackBarView;

    private String FULLNAME = "fulllname";
    private static final String DEFAULT = "N/A";
    private static final int DEFAULTINT = 0;


    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;


    //This is our root url
    //public static final String ROOT_URL = "http://v1dashboard.aggreyapps.com/";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        editor = preferences.edit();

        _usernameText = (EditText) findViewById(R.id.input_username);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _snackBarView = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        // _loginButton.setEnabled(false);
        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setCancelable(false);

        preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.clear();
        editor.commit();

        //check if user is already logged in
        int isLoggedIn = preferences.getInt("userId", DEFAULTINT);
        //Toast.makeText(LoginActivity.this,"ok"+isLoggedIn,Toast.LENGTH_LONG).show();

        if (isLoggedIn == DEFAULTINT) {
            //clean all the session data then log user out
            preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
            editor = preferences.edit();
            editor.clear();
            editor.commit();
            //Toast.makeText(LoginActivity.this, "not logged in" + isLoggedIn, Toast.LENGTH_LONG).show();
        } else {
            //Toast.makeText(LoginActivity.this,""+isLoggedIn,Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }


        //Adding listener to button
        _loginButton.setOnClickListener(this);

    }


    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.dismiss();
    }


    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }


        progressDialog.show();

        final String username = _usernameText.getText().toString();
        final String password = _passwordText.getText().toString();

        Log.d("success log", "hapa");

        //log user
        Endpoint.setUrl("models/userLogin.php");
        AppHttp appHttp = new AppHttp();
        appHttp.postData(this, Endpoint.getRootUrl(), getParameters(username, password), new AppHttp.VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                //Toast.makeText(LoginActivity.this, username + " " + password, Toast.LENGTH_LONG).show();
                try {
                    JSONObject result = new JSONObject(response);
                    if (result.getString("playsRole").equals("Member")) {
                        preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("brandCampaign", result.getString("brandCampaign"));
                        editor.putInt("salesId", result.getInt("idSales"));
                        editor.commit();
                        createUserSession(result);

                        //

                        Intent intent = new Intent(LoginActivity.this, MemberMainActivity.class);
                        startActivity(intent);

                        // Toast.makeText(LoginActivity.this, "this playrole is a mEmber"+result.getString("txtError"), Toast.LENGTH_LONG).show();
                    } else if (result.get("playsRole").equals("Supervisor")) {
                        preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("useActivity", result.getString("useActivity"));
                        editor.putString("outletTypes", result.getString("outletTypes"));
                        editor.commit();
                        Log.d("LoginActivity:", "works");

                        createUserSession(result);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("isRefresh", "false");
                        startActivity(intent);

                        //  Toast.makeText(LoginActivity.this, "this playrole is a supervisor", Toast.LENGTH_LONG).show();
                    }
                    //Toast.makeText(LoginActivity.this, ""+result.getString("txtError"), Toast.LENGTH_LONG).show();
//                    Snackbar snackbar = Snackbar.make(_snackBarView, result.getString("txtError"), Snackbar.LENGTH_INDEFINITE)
//                            .setAction("Ok", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                }
//                            });
//
//
//                    snackbar.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                onLoginSuccess();
            }
        });


    }

    @Override
    public void onBackPressed() {
        //disable going back to the MainActivity
        // moveTaskToBack(true);
        finish();
    }


    //chat post parameters
    public Map<String, String> getParameters(String username, String password) {
        Map<String, String> params = new HashMap<>();

        String dateNtime = CustomDateFormat.getFormattedDateAndTime();

        // the POST parameters:
        params.put("username", username);
        params.put("password", password);
        params.put("timeStamp", dateNtime);

        return params;

    }



    public void createUserSession(JSONObject result) {
        try {

            //check if brand is activated to create session for it
            if (!result.get("brandName").equals("Not Activated")) {
                editor.putString("brandname", result.getString("brandName"));
            }
            editor.putInt("userId", result.getInt("idUser"));
            editor.putString("fullname", result.getString("fullName"));

            editor.putInt("teamId", result.getInt("idTeam"));
            editor.putInt("portalId", result.getInt("idPortal"));
            editor.putInt("regionId", result.getInt("idRegion"));
            editor.putInt("projectId", result.getInt("idProject"));
            editor.putInt("brandId", result.getInt("idBrand"));
            editor.putInt("clientId", result.getInt("idClient"));
            editor.putString("actualBrandName", result.getString("actualBrandName"));
            editor.commit();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        progressDialog.dismiss();
        //finish();
    }

    public void onLoginFailed() {
        Snackbar snackbar = Snackbar.make(_snackBarView, "login failed", Snackbar.LENGTH_INDEFINITE)
                .setAction("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });


        snackbar.show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty()) {
            setErrorMsg("enter a valid username address", _usernameText);


            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            setErrorMsg("between 4 and 10 alphanumeric characters", _passwordText);
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public static void setErrorMsg(String msg, EditText viewId) {
        //change error color
        int ecolor = Color.WHITE;
        String estring = msg;
        ForegroundColorSpan fgcspan = new ForegroundColorSpan(ecolor);
        SpannableStringBuilder sbuilder = new SpannableStringBuilder(estring);
        sbuilder.setSpan(fgcspan, 0, estring.length(), 0);
        viewId.setError(sbuilder);

    }

    private void shareUserData(String fullname) {

    }
//
//    private boolean isNetworkAvailable() {
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//    }

    @Override
    public void onClick(View view) {
        Boolean isNetworkAvailable = CustomNetworkManager.isNetworkAvailable(this);
        if (isNetworkAvailable == true) {
            login();
        } else {
            Snackbar snackbar = Snackbar.make(_snackBarView, "network is not available", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
            snackbar.show();
        }


    }
}