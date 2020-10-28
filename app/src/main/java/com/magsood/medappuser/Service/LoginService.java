package com.magsood.medappuser.Service;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.magsood.medappuser.Activity.MainActivity;
import com.magsood.medappuser.Constants;
import com.magsood.medappuser.R;
import com.magsood.medappuser.SharedPrefrense.UserPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginService {


    String phoneNumber,password;
    String TAG ="RESPONSE";
    UserPreferences userPreferences;


    public void sendDate(Activity activity) {


        phoneNumber = ((EditText) activity.findViewById(R.id.phoneNumber)).getText().toString();
        password = ((EditText) activity.findViewById(R.id.password)).getText().toString();
        userPreferences = new UserPreferences(activity);




        if (TextUtils.isEmpty(password)) {
            ((EditText) activity.findViewById(R.id.password)).setError("Enter a password");
            ((EditText) activity.findViewById(R.id.password)).requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            ((EditText) activity.findViewById(R.id.phoneNumber)).setError("Enter a phone number");
            ((EditText) activity.findViewById(R.id.phoneNumber)).requestFocus();
            return;
        }




        Map<String, String> params = new HashMap<>();

        params.put("phoneNumber",phoneNumber);
        params.put("password", password);

        Log.e("response", String.valueOf(params));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Constants.LOGIN_URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            JSONObject userInfo = response.getJSONObject("userInfo");
                            userPreferences.setUserId(userInfo.getString("userID"));
                            userPreferences.setToken(response.getString("token"));
                            Intent intent = new Intent(activity, MainActivity.class);
                            activity.startActivity(intent);




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // As of f605da3 the following should work
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                        Log.e("responseError",obj.toString());
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }

            }
        }) {




            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                // Removed this line if you dont need it or Use application/json
                params.put("Content-Type", "application/json");
                return params;
            }


        };
//
        VolleySingleton.getInstance(activity).addToRequestQueue(jsonObjReq);

    }
}
