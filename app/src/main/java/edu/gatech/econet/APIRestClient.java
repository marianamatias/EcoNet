package edu.gatech.econet;

import org.json.*;
import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

class TwitterRestClientUsage {
    public void getPublicTimeline() throws JSONException {
        HttpUtils.get("statuses/public_timeline.json", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                //JSONObject firstEvent = timeline.get(0);
                //String response = firstEvent.getString("text");

                // Do something with the response
                //System.out.println(response);
            }
        });
    }
}