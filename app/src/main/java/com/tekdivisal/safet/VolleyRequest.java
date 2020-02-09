package com.tekdivisal.safet;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tekdivisal.safet.Model.KeyValuePair;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

interface Response {
    void getResponse(String response, int REQUEST_ID) throws JSONException;
}

public class VolleyRequest {
    RequestQueue requestQueue;
    Context context;
    Response response;

    VolleyRequest(Context context){
        this.context = context;
    }

    void connect(String url, KeyValuePair[] networkData, int method, int REQUEST_ID){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(method, url,
                (response) -> {
                    try {
                        this.response.getResponse(response, REQUEST_ID);
                    } catch (JSONException e) {
                        Log.d("INFO", e.getMessage());
                    }
                },
                (error) -> Log.d("INFO", error.toString())){
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                for(KeyValuePair data : networkData) params.put(data.key, data.value);
                return params;
            }
        };
        requestQueue.add(request);
    }
}
