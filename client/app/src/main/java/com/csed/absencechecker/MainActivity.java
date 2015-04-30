package com.csed.absencechecker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {
    public static String[] data = new String[3];
    JSONObject studentData = new JSONObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectToWifi("ICODER", "passwordXD");

        String macAdress = getMacAddress();

        data = retrieveData(macAdress);

        JSONObject ob = writeJSON(data);

        // new Submit(MainActivity.this).execute(data);

        submit(data);

    }

    private void submit(String[] data) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet("http://192.168.1.222:9000/students/submit/"
        + MainActivity.data[1] + "/" + MainActivity.data[2]);
        Toast.makeText(MainActivity.this, "http://192.168.1.222:9000/students/submit/"
                + MainActivity.data[1] + "/" + MainActivity.data[2], Toast.LENGTH_LONG).show();
        try {
            HttpResponse response = httpclient.execute(httpget);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getMacAddress() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String address = info.getMacAddress();
        return address;
    }

    private String[] retrieveData(String macAdress) {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        String retrievedData = prefs.getString(LoginActivity.DATA_PREFF, null);
        String id = null;
        if (retrievedData != null) {
            id = prefs.getString("id", "NONE");
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String strDate = dateFormat.format(date);

        data[0] = id;
        data[1] = macAdress;
        data[2] = strDate;
        Toast.makeText(MainActivity.this, data[0], Toast.LENGTH_LONG).show();
        Toast.makeText(MainActivity.this, data[1], Toast.LENGTH_LONG).show();
        Toast.makeText(MainActivity.this, data[2], Toast.LENGTH_LONG).show();

        return data;
    }

    private void connectToWifi(String SSID, String password) {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        // setup a wifi configuration
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = " \" " + SSID + " \" ";
        wc.preSharedKey = " \" " + password + " \" ";
        wc.status = WifiConfiguration.Status.ENABLED;
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        // connect and enable the connection
        int netId = wifiManager.addNetwork(wc);
        wifiManager.enableNetwork(netId, true);
        wifiManager.setWifiEnabled(true);
    }

    public JSONObject writeJSON(String[] data) {
        try {
            studentData.put("id", data[0]);
            studentData.put("macAdress", data[1]);
            studentData.put("date", data[2]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(studentData);
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

}
//class Submit extends AsyncTask<String [], Void, Void> {
//    private Context mContext;
//    public Submit (Context context){
//        mContext = context;
//    }
//    @Override
//    protected Void doInBackground(String[]... params) {
//        HttpClient httpclient = new DefaultHttpClient();
//        HttpPost httppost = new HttpPost("http://192.168.1.222:9000/students/submit/"
//                + MainActivity.data[1] + "/" + MainActivity.data[2]);
//        Toast.makeText(mContext, "http://192.168.1.222:9000/students/submit/"
//                + MainActivity.data[1] + "/" + MainActivity.data[2], Toast.LENGTH_LONG).show();
//
//        try {
//            HttpResponse response = httpclient.execute(httppost);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//}