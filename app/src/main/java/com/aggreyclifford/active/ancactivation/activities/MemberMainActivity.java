package com.aggreyclifford.active.ancactivation.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.aggreyclifford.active.ancactivation.R;
import com.aggreyclifford.active.ancactivation.fragments.SurveyFragment_1;
import com.aggreyclifford.active.ancactivation.fragments.SurveyFragment_2;
import com.aggreyclifford.active.ancactivation.helpers.ComplexPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MemberMainActivity extends AppCompatActivity {

    private final static String KYC_CONTENT_TYPE_0 = "one";
    private final static String KYC_CONTENT_TYPE_1 = "two";
    private static final String DEFAULT = "N/A";
    private static final int DEFAULTINT = 0;
    JSONObject _jObj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //set default fragment
        Fragment fragment = new SurveyFragment_1();
        displayView(fragment);

        SharedPreferences preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        String brandname = preferences.getString("brandname", DEFAULT);
        getSupportActionBar().setTitle("KYC-" + brandname);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void displayView(Fragment fragment) {


        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.logout_menu_ambassador, menu);
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

            Intent intent = new Intent(MemberMainActivity.this, LoginActivity.class);
            startActivity(intent);
            //finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
