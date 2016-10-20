package com.aggreyclifford.active.ancactivation.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aggreyclifford.active.ancactivation.R;
import com.aggreyclifford.active.ancactivation.activities.LoginActivity;
import com.aggreyclifford.active.ancactivation.adapters.kyc_adapters.AgeAdapter;
import com.aggreyclifford.active.ancactivation.adapters.kyc_adapters.CampaignAwarenessAdapter;
import com.aggreyclifford.active.ancactivation.adapters.kyc_adapters.GenderAdapter;
import com.aggreyclifford.active.ancactivation.helpers.CustomDateFormat;
import com.aggreyclifford.active.ancactivation.models.Brand;
import com.aggreyclifford.active.ancactivation.models.BrandModel;
import com.aggreyclifford.active.ancactivation.models.BrandSob;
import com.aggreyclifford.active.ancactivation.models.RouteOutlet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alicephares on 8/7/16.
 */
public class SurveyFragment_1 extends Fragment {
    private FragmentActivity c;
    private TextView _date, _campaignTxt;
    private String _ageSpinner, _genderSpinner, _awarenessSpinner;
    private String _brandName, _brandAbbrev;
    SharedPreferences preferences;

    private ArrayAdapter spinnerArrayAdapter;
    String[] _ageValues = {"18-25", "26-30", "31-35", "36-40", "40+"};
    String[] _genderValues = {"Male", "Female"};
    String[] _campaignAwareness = {"No", "Yes"};

    private static final String DEFAULT = "N/A";
    private static final int DEFAULTINT = 0;

    private Spinner _brandSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_kyc_1, container, false);
        c = getActivity();

        _date = (TextView) view.findViewById(R.id.date_txt);
        _campaignTxt = (TextView) view.findViewById(R.id.comapign_txt);


        final List<Brand> brands = new ArrayList<>();
        //format date then display it
        String date = CustomDateFormat.getFormattedDate("dd/MM/yyyy");
        String day = CustomDateFormat.convertDateIntoDay(date);

        //set route spinner,date
        _date.setText(day + " " + date);


        final Spinner ageSpinner = (Spinner) view.findViewById(R.id.age_spinner);
        ageSpinner.setAdapter(new AgeAdapter(c, R.layout.custom_spinner, _ageValues));

        //gender
        final Spinner genderSpinner = (Spinner) view.findViewById(R.id.gender_spinner);
        genderSpinner.setAdapter(new GenderAdapter(c, R.layout.custom_spinner, _genderValues));


        final Spinner campaignAwarenessSpinner = (Spinner) view.findViewById(R.id.awareness_spinner);
        campaignAwarenessSpinner.setAdapter(new CampaignAwarenessAdapter(c, R.layout.custom_spinner, _campaignAwareness));

        _brandSpinner = (Spinner) view.findViewById(R.id.current_brand);

        ageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                _ageSpinner = ageSpinner.getSelectedItem().toString();
                //  Toast.makeText(c, "" + _ageSpinner, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        _brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                BrandSob brand = (BrandSob) _brandSpinner.getSelectedItem();
                //Toast.makeText(c, "" +brand, Toast.LENGTH_LONG).show();

                _brandAbbrev = brand.abbrev;
                _brandName = brand.name;

              //  Toast.makeText(c, "" + _brandAbbrev + _brandName, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                _genderSpinner = genderSpinner.getSelectedItem().toString();
                // Toast.makeText(c, "" + _genderSpinner, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        campaignAwarenessSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                _awarenessSpinner = campaignAwarenessSpinner.getSelectedItem().toString();
                //Toast.makeText(c, "" + _awarenessSpinner, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        // Create and fill an ArrayAdapter with a bunch of "Brand" objects
        spinnerArrayAdapter = new ArrayAdapter(c,
                R.layout.custom_spinner_textview, BrandModel.getBrands());

        //  Tell the spinner about our adapter
        _brandSpinner.setAdapter(spinnerArrayAdapter);

        final FloatingActionButton fab = (FloatingActionButton) c.findViewById(R.id.fab);
        FloatingActionButton fabDone = (FloatingActionButton) c.findViewById(R.id.fab_done);
        fabDone.hide();
        fab.show();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new SurveyFragment_2();
                setSharedSurveydata();
                displayView(fragment);

            }
        });

        // Update the UI here on backstack.
        c.getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {

                        FloatingActionButton fabDone = (FloatingActionButton) c.findViewById(R.id.fab_done);
                        fabDone.hide();
                        fab.show();
                    }
                });


        getBrandCampaign();
        // c.onBackPressed();

        return view;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {

//            Intent intent = new Intent(c, LoginActivity.class);
//            startActivity(intent);
            //finish();


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getBrandCampaign() {
        SharedPreferences preferences = c.getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        String brandCompaign = preferences.getString("brandCampaign", DEFAULT);
        _campaignTxt.setText(brandCompaign);
    }

    private void setSharedSurveydata() {
        preferences = c.getSharedPreferences("SurveyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("age", _ageSpinner);
        editor.putString("gender", _genderSpinner);
        editor.putString("currentBrand", _brandAbbrev + "-" + _brandName);
        editor.putString("awarenessCampaign", _awarenessSpinner);

        editor.commit();
       // Toast.makeText(c, "" + _awarenessSpinner, Toast.LENGTH_LONG).show();
    }


    private void displayView(Fragment fragment) {


        if (fragment != null) {
            FragmentManager fragmentManager = c.getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).
                    addToBackStack(null).commit();
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }


}
