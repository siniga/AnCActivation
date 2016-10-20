package com.aggreyclifford.active.ancactivation.models;

import android.content.Context;
import android.widget.Toast;

import com.aggreyclifford.active.ancactivation.activities.LoginActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.ErrorManager;

/**
 * Created by alicephares on 9/27/16.
 */
public class AppHttp {
    private static final int SOCKET_TIMEOUT_MS = 15000;

    public void postData(final Context c, String url, final Map<String, String> params, final VolleyCallback callback) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                            callback.onSuccess(response);
//                        try {
////                            JSONArray jsonArray = new JSONArray(response);
//                            JSONObject result = new JSONObject(response);
//                            Toast.makeText(c, "works up to here" + result.getString("fullName"),Toast.LENGTH_LONG).show();
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//
//                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                        /*check if activity is not null when user change tabs
                        and retrieve data from server*/
                        //ErrorManager.activityIsNull(error, c);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {

                return params;
            }
        };
        Volley.newRequestQueue(c).add(postRequest);
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    public interface VolleyCallback{
        void onSuccess(String result);
    }

}
