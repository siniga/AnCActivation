package com.aggreyclifford.active.ancactivation.fragments;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aggreyclifford.active.ancactivation.R;
import com.aggreyclifford.active.ancactivation.activities.MemberMainActivity;
import com.aggreyclifford.active.ancactivation.adapters.kyc_adapters.AgeAdapter;
import com.aggreyclifford.active.ancactivation.adapters.kyc_adapters.BrandPurchasedAdapter;
import com.aggreyclifford.active.ancactivation.endpoint.Endpoint;
import com.aggreyclifford.active.ancactivation.helpers.CustomDateFormat;
import com.aggreyclifford.active.ancactivation.helpers.CustomNetworkManager;
import com.aggreyclifford.active.ancactivation.models.AppHttp;
import com.aggreyclifford.active.ancactivation.models.BrandModel;
import com.aggreyclifford.active.ancactivation.models.BrandSob;
import com.aggreyclifford.active.ancactivation.models.RouteOutlet;

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
public class SurveyFragment_2 extends Fragment {
    private FragmentActivity c;
    private TextView _date;
    private SharedPreferences preferences;
    private static final String DEFAULT = "N/A";
    private static final int DEFAULTINT = 0;
    private String _purchasedBrandString;
    private EditText _stickSold, _amountPaid;
    private Spinner _brandSpinner;
    private String _brandName, _brandAbbrev;
    private ArrayAdapter spinnerArrayAdapter;
    private ProgressDialog progressDialog;
    private CoordinatorLayout _snackBarView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_kyc_2, container, false);
        c = getActivity();

        _date = (TextView) view.findViewById(R.id.date_txt);
        _snackBarView = (CoordinatorLayout) c.findViewById(R.id.coordinatorLayout);

        progressDialog = new ProgressDialog(c,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("loading data..");
        progressDialog.setCancelable(false);

        //format date then display it
        String date = CustomDateFormat.getFormattedDate("dd/MM/yyyy");
        String day = CustomDateFormat.convertDateIntoDay(date);

        //set route spinner,date
        _date.setText(day + " " + date);

        //brand purchased
        _brandSpinner = (Spinner) view.findViewById(R.id.brand_purchased_spinner);


        _brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                BrandSob brand = (BrandSob) _brandSpinner.getSelectedItem();
                //Toast.makeText(c, "" +brand, Toast.LENGTH_LONG).show();

                _brandAbbrev = brand.abbrev;
                _brandName = brand.name;

                //    Toast.makeText(c, "" + _brandAbbrev + _brandName, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        SharedPreferences preferences = c.getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        String actualBrandName = preferences.getString("actualBrandName", DEFAULT);
        int brandId = preferences.getInt("brandId", DEFAULTINT);
        int clientId = preferences.getInt("clientId", DEFAULTINT);

        //check for network availability
        Boolean isNetworkAvailable = CustomNetworkManager.isNetworkAvailable(c);
        if (isNetworkAvailable == true) {
            getPurchasedBrands(brandId, clientId, actualBrandName);
        } else {

            Snackbar snackbar = Snackbar.make(_snackBarView, "network is not available", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
            snackbar.show();
        }



        _stickSold = (EditText) view.findViewById(R.id.stick_sold);
        _amountPaid = (EditText) view.findViewById(R.id.amount_paid);


        final FloatingActionButton fab = (FloatingActionButton) c.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new SurveyFragment_3();
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
        editor.putString("purchasedBrand", _brandAbbrev + "-" + _brandName);
        editor.putString("stickSold", _stickSold.getText().toString());
        editor.putString("amountPaid", _amountPaid.getText().toString());


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

    private void getPurchasedBrands(int brandId, int clientId, String actualBrandName) {
        progressDialog.show();

        //set url
        Endpoint.setUrl("models/salesReports.php");
        //log user
        AppHttp appHttp = new AppHttp();
        appHttp.postData(c, Endpoint.getRootUrl(), getBrandActivationParameters(brandId, clientId, actualBrandName), new AppHttp.VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                List<BrandSob> brands = new ArrayList<>();

                // Log.d("success log", result);
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject result = jsonArray.getJSONObject(i);

                            brands.add(new BrandSob(0, result.getString("brandName"), result.getString("idBrand")));

                            // Create and fill an ArrayAdapter with a bunch of "Brand" objects
                            spinnerArrayAdapter = new ArrayAdapter(c,
                                    R.layout.custom_spinner_textview, brands);

                            //  Tell the spinner about our adapter
                            _brandSpinner.setAdapter(spinnerArrayAdapter);

                            //Toast.makeText(OutletActivationActivity.this, "" + outletsObj.getString("outletName"), Toast.LENGTH_LONG).show();
                            progressDialog.hide();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private Map<String, String> getBrandActivationParameters(int brandId, int clientId, String actualBrandName) {
        Map<String, String> params = new HashMap<>();
        //current date and time
        String dateNtime = CustomDateFormat.getFormattedDateAndTime();
        //Toast.makeText(this, ""+userId+" "+projectId+" "+teamId, Toast.LENGTH_LONG).show();
        // the POST parameters:
        params.put("idBrand", "" + brandId);
        params.put("idClient", "" + clientId);
        params.put("actualBrandName", "" + actualBrandName);
        params.put("Gate", "getActivatedBrand");
        return params;
    }

}
