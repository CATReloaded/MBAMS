package com.csed.absencechecker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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


/**
 * Sign up screen via id.
 */
public class SignupActivity extends Activity implements LoaderCallbacks<Cursor> {

    public static final String DATA_PREFF = "dataPreff";
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    // UI references.
    private EditText mIDView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        String wifiName = getString(R.string.wifiName);
        String wifiPassword = getString(R.string.wifiPassword);
        connectToWifi(wifiName, wifiPassword);

        SharedPreferences settings = getSharedPreferences(DATA_PREFF, 0);
        //Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);

        if (hasLoggedIn) {
            Intent intent = new Intent();
            intent.setClass(SignupActivity.this, MainActivity.class);
            startActivity(intent);
            SignupActivity.this.finish();
        }

        // Set up the login form.

        mIDView = (EditText) findViewById(R.id.s_id);
        mIDView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.name_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
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


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mIDView.setError(null);

        // Store values at the time of the login attempt.
        String password = mIDView.getText().toString();
        Long pwd = Long.parseLong(password);
        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isIDValid(password)) {
            mIDView.setError(getString(R.string.error_invalid_password));
            focusView = mIDView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(pwd);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isIDValid(String id) {
        return id.length() == 8;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    private String getMacAddress() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String address = info.getMacAddress();
        return address;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String SIGNUP_URL = getString(R.string.serverURL)+ getString(R.string.siginURL);
        private final Long id;
        private final String macAddress;
        private String resultstring;

        UserLoginTask(Long id) {
            this.id = id;
            macAddress = getMacAddress();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                // Add your data
                JSONObject jsonobj = new JSONObject();
                try {
                    // adding keys
                    jsonobj.put("id", id);
                    jsonobj.put("macAddress", macAddress);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(SIGNUP_URL);
                StringEntity se = new StringEntity(jsonobj.toString());

                se.setContentType("application/json;charset=UTF-8");
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
                httpPost.setEntity(se);

                // Execute HTTP Post Request
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity resultentity = response.getEntity();

                resultstring = getStatus(resultentity);

                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                SharedPreferences settings = getSharedPreferences(DATA_PREFF, 0); // 0 - for private mode
                SharedPreferences.Editor editor = settings.edit();  //Set "hasLoggedIn" to true
                editor.putLong("id", id);
                editor.putString("mac", macAddress);
                // Commit the edits!
                editor.commit();
                updateUI();
            } else {
                mIDView.setError(getString(R.string.error_incorrect_id));
                mIDView.requestFocus();
            }
        }


        private void updateUI() {
            String status = setStatus(resultstring);
            Toast.makeText(SignupActivity.this, status, Toast.LENGTH_LONG).show();
            if (status.equals(getString(R.string.success_signup))) {
                SharedPreferences settings = getSharedPreferences(DATA_PREFF, 0); // 0 - for private mode
                SharedPreferences.Editor editor = settings.edit();  //Set "hasLoggedIn" to true
                editor.putBoolean("hasLoggedIn", true);
                editor.commit();
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }

        private String setStatus(String resultString) {
            String message;
            if (resultString != null) {
                System.out.println(resultString);
                if (resultString.equals("success")) {
                    message = getString(R.string.success_signup);
                } else {
                    message = getString(R.string.failed_signup);
                }
            } else {
                message = getString(R.string.network_error);
            }
            return message;
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
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}