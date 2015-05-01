package com.csed.absencechecker;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPInputStream;

public class MainActivity extends Activity {
    public static final String DATA_PREFF = "dataPreff";
    public static String[] data = new String[2];
    JSONObject studentData = new JSONObject();
    private UserLoginTask mAuthTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = retrieveData();

        JSONObject ob = createJSON(data);

        try {
            mAuthTask = new UserLoginTask(ob);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mAuthTask.execute((Void) null);

        // submit();

    }

//    private void submit() {
//        mAuthTask = new UserLoginTask(, pwd);
//        mAuthTask.execute((Void) null);
//
//    }

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

        private String macAddress;
        private String date;

        UserLoginTask(JSONObject stData) throws JSONException {
                date = stData.getString("date");
                macAddress = stData.getString("macAddress");
            }


        @Override
        protected Boolean doInBackground(Void... params) {
            Log.i("doInBackground", "doInBackground");

            try {
                String[] stData = retrieveData();
                JSONObject jsData = createJSON(stData);
                Log.i("TRY", jsData.toString());

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppostreq = new HttpPost("http://192.168.1.222:9000/students/submit");
                StringEntity se = new StringEntity(jsData.toString());
                Log.i("SE", se.toString());

                se.setContentType("application/json;charset=UTF-8");
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
                httppostreq.setEntity(se);
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppostreq);
                HttpEntity resultentity = response.getEntity();
                Log.i("RE", resultentity.toString());

                if (resultentity != null) {
                    InputStream inputstream = resultentity.getContent();
                    Header contentencoding = response.getFirstHeader("Content-Encoding");
                    if (contentencoding != null && contentencoding.getValue().equalsIgnoreCase("gzip")) {
                        inputstream = new GZIPInputStream(inputstream);
                    }
                    String resultstring = convertStreamToString(inputstream);
                    inputstream.close();
                }
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

        private String convertStreamToString(InputStream is) {
            String line = "";
            StringBuilder total = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            try {
                while ((line = rd.readLine()) != null) {
                    total.append(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return total.toString();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
        }

        @Override
        protected void onCancelled() {
        }

    }
}