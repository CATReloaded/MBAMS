package com.csed.absencechecker;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {
    public static final String DATA_PREFF = "dataPreff";
    public static String[] data = new String[2];
    JSONObject studentData = new JSONObject();
    private UserLoginTask mAuthTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void submitHandler(View target) {
        data = retrieveData();
        JSONObject stData = createJSON(data);
        submit(stData);
    }

    private void submit(JSONObject ob) {

        try {
            mAuthTask = new UserLoginTask(ob);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mAuthTask.execute((Void) null);

    }

    private String[] retrieveData() {
        String macAddress = getMacAddress();
        String strDate = getDate();

        data[0] = macAddress;
        data[1] = strDate;

        return data;
    }

    private String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getMacAddress() {
        SharedPreferences prefs = getSharedPreferences(DATA_PREFF, MODE_PRIVATE);
        String macAddress;

        macAddress = prefs.getString("mac", "FAILED");
        return macAddress;
    }

    public JSONObject createJSON(String[] data) {
        try {
            studentData.put("macAddress", data[0]);
            studentData.put("date", data[1]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return studentData;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private static final String SUBMIT_URL = "http://192.168.1.222:9000/students/submit";
        private String macAddress;
        private String date;
        private String resultstring;

        UserLoginTask(JSONObject stData) throws JSONException {
            date = stData.getString("date");
            macAddress = stData.getString("macAddress");
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {

                JSONObject jsData = getJsonObject();

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(SUBMIT_URL);
                StringEntity se = new StringEntity(jsData.toString());

                se.setContentType("application/json;charset=UTF-8");
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
                httpPost.setEntity(se);

                // Execute HTTP Post Request
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity responseEntity = response.getEntity();

                resultstring = getStatus(responseEntity);

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return true;
        }

        private JSONObject getJsonObject() {
            String[] stData = retrieveData();
            return createJSON(stData);
        }

        private String getStatus(HttpEntity entity) {
            String status = null;
            try {
                if (entity != null) {
                    String retSrc = EntityUtils.toString(entity);

                    // parsing JSON
                    JSONObject result = new JSONObject(retSrc);

                    status = result.getString("status");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return status;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            TextView statusView = (TextView) findViewById(R.id.statusTextView);
            String status = setStatus(resultstring);
            statusView.setText(status);
        }

        private String setStatus(String resultString){
            String message;
            if (resultString.equals("success")){
                message = "You have successfully registered your attendance !";
            }else{
                message = "Something went wrong, please contact your professor.";
            }
            return message;
        }

        @Override
        protected void onCancelled() {
        }

    }
}