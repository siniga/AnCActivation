package com.aggreyclifford.active.ancactivation.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aggreyclifford.active.ancactivation.R;
import com.aggreyclifford.active.ancactivation.activities.MainActivity;
import com.aggreyclifford.active.ancactivation.activities.RouteSettingActivity;
import com.aggreyclifford.active.ancactivation.adapters.kyc_adapters.BrandPurchasedAdapter;
import com.aggreyclifford.active.ancactivation.adapters.kyc_adapters.SpontaneousAdapter;
import com.aggreyclifford.active.ancactivation.endpoint.Endpoint;
import com.aggreyclifford.active.ancactivation.helpers.CustomDateFormat;
import com.aggreyclifford.active.ancactivation.models.AppHttp;
import com.aggreyclifford.active.ancactivation.models.Brand;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alicephares on 8/7/16.
 */
public class SurveyFragment_3 extends Fragment   {
    private FragmentActivity c;
    private TextView _date;
    String[] _arrIsSampled = {"No", "Yes"};
    String[] _arrIsPrize = {"No", "Yes"};

    private ProgressDialog _progressDialog;
    private CoordinatorLayout _snackBarView;
    private SharedPreferences preferences;

    private String _strIsSampled;
    private String _strIsPrize;
    private EditText _prizeName;
    private TextView _prizeNameHdr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_kyc_3, container, false);
        c = getActivity();

        _date = (TextView) view.findViewById(R.id.date_txt);
        _snackBarView = (CoordinatorLayout) c.findViewById(R.id.coordinatorLayout);
        _prizeName = (EditText) view.findViewById(R.id.prize_name_txt);
        _prizeNameHdr = (TextView) view.findViewById(R.id.prize_name_header);

        _prizeName.setVisibility(View.INVISIBLE);
        _prizeNameHdr.setVisibility(View.INVISIBLE);

        _progressDialog = new ProgressDialog(c,
                R.style.AppTheme_Dark_Dialog);
        _progressDialog.setIndeterminate(true);
        _progressDialog.setMessage("saving data...");
        _progressDialog.setCancelable(false);


        //format date then display it
        String date = CustomDateFormat.getFormattedDate("dd/MM/yyyy");
        String day = CustomDateFormat.convertDateIntoDay(date);

        //set route spinner,date
        _date.setText(day + " " + date);

        //brand purchased
        final Spinner isSampledSpinner = (Spinner) view.findViewById(R.id.is_sampled);
        isSampledSpinner.setAdapter(new SpontaneousAdapter(c, R.layout.custom_spinner, _arrIsSampled));//TODO work on the adapter

        //brand purchased
        final Spinner isPrize = (Spinner) view.findViewById(R.id.is_prize);
        isPrize.setAdapter(new SpontaneousAdapter(c, R.layout.custom_spinner, _arrIsPrize));//TODO work on the adapter

        isSampledSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                _strIsSampled = isSampledSpinner.getSelectedItem().toString();
                Toast.makeText(c, _strIsSampled, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        isPrize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                _strIsPrize = isPrize.getSelectedItem().toString();
                Toast.makeText(c, "" + _strIsPrize, Toast.LENGTH_SHORT).show();

                if (_strIsPrize.equals("Yes")) {
                    _prizeName.setVisibility(View.VISIBLE);
                    _prizeNameHdr.setVisibility(View.VISIBLE);
                } else {
                    _prizeName.setVisibility(View.INVISIBLE);
                    _prizeNameHdr.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        final FloatingActionButton fab = (FloatingActionButton) c.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new SurveyFragment_4();
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

        return view;
    }

    private void setSharedSurveydata() {
        preferences = c.getSharedPreferences("SurveyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("sampled", _strIsSampled);
        editor.putString("prized", _strIsPrize);
        editor.putString("prizeName", _prizeName.getText().toString());

        editor.commit();
    }


    private void displayView(Fragment fragment) {

        if (fragment != null) {
            FragmentManager fragmentManager = c.getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).addToBackStack(null).commit();
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }



}
