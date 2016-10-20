package com.aggreyclifford.active.ancactivation.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aggreyclifford.active.ancactivation.R;
import com.aggreyclifford.active.ancactivation.activities.LoginActivity;
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
public class SurveyFragment_4 extends Fragment {
    private FragmentActivity c;
    private ProgressDialog _progressDialog;
    private CoordinatorLayout _snackBarView;
    private SharedPreferences preferences;
    private static final String DEFAULT = "N/A";
    private static final int DEFAULTINT = 0;
    private TextView _date;
    private TextView _fullName;
    private TextView _phoneNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_kyc_4, container, false);
        c = getActivity();

        _fullName = (TextView) view.findViewById(R.id.fullname);
        _phoneNumber = (TextView) view.findViewById(R.id.phone_number);
        _date = (TextView) view.findViewById(R.id.date_txt);
        _snackBarView = (CoordinatorLayout) c.findViewById(R.id.coordinatorLayout);

        //format date then display it
        String date = CustomDateFormat.getFormattedDate("dd/MM/yyyy");
        String day = CustomDateFormat.convertDateIntoDay(date);
        //set route spinner,date
        _date.setText(day + " " + date);

        _progressDialog = new ProgressDialog(c,
                R.style.AppTheme_Dark_Dialog);
        _progressDialog.setIndeterminate(true);
        _progressDialog.setMessage("saving data...");
        _progressDialog.setCancelable(false);


        FloatingActionButton fab = (FloatingActionButton) c.findViewById(R.id.fab);
        FloatingActionButton fabDone = (FloatingActionButton) c.findViewById(R.id.fab_done);
        fab.hide();
        fabDone.show();

        fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // getBrand()
                getSharedSurveydata();

            }
        });
        return view;
    }

    private void getSharedSurveydata() {

        SharedPreferences preferences = c.getSharedPreferences("SurveyPreferences", Context.MODE_PRIVATE);

        String age = preferences.getString("age", DEFAULT);
        String gender = preferences.getString("gender", DEFAULT);
        String currentBrand = preferences.getString("currentBrand", DEFAULT);
        String awarenessCampaign = preferences.getString("awarenessCampaign", DEFAULT);
        String purchasedBrand = preferences.getString("purchasedBrand", DEFAULT);
        String stickSold = preferences.getString("stickSold", DEFAULT);
        String amountPaid = preferences.getString("amountPaid", DEFAULT);
        String sampled = preferences.getString("sampled", DEFAULT);
        String prized = preferences.getString("prized", DEFAULT);
        String prizeName = preferences.getString("prizeName", DEFAULT);

        //get id sales from login
        preferences = c.getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        int salesId = preferences.getInt("salesId", DEFAULTINT);

        //int teamId =  preferences.getInt("teamId",DEFAULTINT);

        if (age.equals(DEFAULT) || salesId == DEFAULTINT) {
            Toast.makeText(c, "Cant not submit survey data", Toast.LENGTH_LONG).show();
        } else {
            //Toast.makeText(c, ""+age+" "+currentBrand+" "+gender+" "+awarenessCampaign, Toast.LENGTH_LONG).show();
            //Toast.makeText(c, ""+purchasedBrand+" "+stickSold+" "+amountPaid, Toast.LENGTH_LONG).show();
            // Toast.makeText(c, ""+_reasonSwitch+" "+_spontaneous, Toast.LENGTH_LONG).show();
            saveKycData(salesId, age, gender, currentBrand, awarenessCampaign, purchasedBrand, stickSold, amountPaid, sampled, prized, prizeName, _fullName.getText().toString(), _phoneNumber.getText().toString());
        }

    }

    private void saveKycData(int salesId, String age, String gender, String currentBrand, String awarenessCampaign, String purchasedBrand, String stickSold, String amountPaid, String sampled, String prized, String prizeName, String fullName, String phoneNumber) {
        _progressDialog.show();

        //set url
        Endpoint.setUrl("models/salesReports.php");
        //log user
        AppHttp appHttp = new AppHttp();
        appHttp.postData(c, Endpoint.getRootUrl(), getKycParams(salesId, age, gender, currentBrand, awarenessCampaign, purchasedBrand, stickSold, amountPaid, sampled, prized, prizeName, fullName, phoneNumber), new AppHttp.VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                List<Brand> brands = new ArrayList<>();

                // Log.d("success log", result);
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject result = jsonArray.getJSONObject(i);


                        Log.d("SUCCESSLOG", "" + result);

                        // String brandName = result.getString("brandName");
                        // brands.add(new Brand(result.getInt("idBrand"), result.getString("brandName")));


                        Snackbar snackbar = Snackbar.make(_snackBarView, result.getString("txtError"), Snackbar.LENGTH_INDEFINITE)
                                .setAction("Ok", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
//

                                    }
                                });

                        if (!result.getString("txtError").equals("Customer feedback successfully saved.")) {
                            snackbar.show();
                        } else {
                            openPopUp(c);
                        }

                        _progressDialog.hide();


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    _progressDialog.hide();
                }

                _progressDialog.hide();

            }
        });
    }

    private Map<String, String> getKycParams(int salesId, String age, String gender, String currentBrand, String awarenessCampaign, String purchasedBrand, String stickSold, String amountPaid, String sampled, String prized, String prizeName, String fullName, String phoneNumber) {
        Map<String, String> params = new HashMap<>();
        Log.d("TESTONE1:", "age: " + age + " gender: " + gender + " currentBrand: " + currentBrand + " awarenessCampaign: " + awarenessCampaign + " purchasedBrand: " + purchasedBrand + " stickSold: " + stickSold + " amountPaid: " + amountPaid + " sampled: " + sampled + " prized: " + prized + " prizeName: " + prizeName);
        Log.d("TESTONE1:", "fullname: " + _fullName.getText().toString() + " phonenumber: " + _phoneNumber.getText().toString());
        //Toast.makeText(c,""+salesId, Toast.LENGTH_LONG).show();
        String dateNtime = CustomDateFormat.getFormattedDateAndTime();

        //The POST parameters:
        params.put("idSales", "" + salesId);
        params.put("ageRange", "" + age);
        params.put("gender", "" + gender);
        params.put("campainAwareness", "" + awarenessCampaign);
        params.put("currentBrand", "" + currentBrand);
        params.put("purchasedBrand", "" + purchasedBrand);
        params.put("purchasedQuantity", "" + stickSold);
        params.put("amountPaid", "" + amountPaid);
        params.put("customerSampled", "" + sampled);
        params.put("prizeGiven", "" + prized);
        params.put("prizeName", "" + prizeName);
        params.put("customerName", "" + fullName);
        params.put("contactNumber", "" + phoneNumber);
        params.put("timeStamp", dateNtime);
        params.put("Gate", "outletBrandKYC");
        return params;
    }

    private void displayView(Fragment fragment) {


        if (fragment != null) {
            FragmentManager fragmentManager = c.getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    public void openPopUp(final Context c) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
        alertDialogBuilder.setMessage("Customer feedback successfully saved, Do you want to continue?");

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //Toast.makeText(c,"You clicked yes button",Toast.LENGTH_LONG).show();
                Fragment fragment = new SurveyFragment_1();
                displayView(fragment);
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(c, LoginActivity.class);
                c.startActivity(intent);
                // finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
