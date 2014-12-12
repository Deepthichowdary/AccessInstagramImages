package com.example.pranitha.instagram;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class MyActivity extends Activity {
    private static String AUTHURL = "https://api.instagram.com/oauth/authorize/";
    private static final String TOKENURL = "https://api.instagram.com/oauth/access_token";
    public static final String APIURL = "https://api.instagram.com/v1";
    public static String CALLBACKURL = "https://success";
    private static String authURLString,tokenURLString,clientId,clientSecret,jsonresultdata;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        clientId = getString(R.string.clientId);
        clientSecret = getString(R.string.clientSecret);
         authURLString = AUTHURL + "?client_id=" + clientId + "&redirect_uri=" + CALLBACKURL + "&response_type=code";
         tokenURLString = TOKENURL + "?client_id=" + clientId + "&client_secret=" + clientSecret + "&redirect_uri=" + CALLBACKURL + "&grant_type=authorization_code";
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new AuthWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
       // String gl = "https://www.google.com";
        webView.loadUrl(authURLString);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
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

    public class AuthWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            if (url.startsWith(CALLBACKURL))
            {
                System.out.println(url);
                String parts[] = url.split("=");
                String request_token = parts[1];

                AsyncTaskRunner runner = new AsyncTaskRunner();
                runner.execute(request_token);

                return true;
            }
            return false;
        }
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {
            String tagresponse = "";
            try
            {
                URL url = new URL(TOKENURL);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.setDoInput(true);
                httpsURLConnection.setDoOutput(true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
                outputStreamWriter.write("client_id="+clientId+"&client_secret="+ clientSecret +"&grant_type=authorization_code" +"&redirect_uri="+CALLBACKURL+"&code=" + params[0]);
                outputStreamWriter.flush();
                InputStream is1 = httpsURLConnection.getInputStream();
                String response = convertStreamToString(is1);
                JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
                String accessTokenString = jsonObject.getString("access_token");

                String id = jsonObject.getJSONObject("user").getString("id");
                String username = jsonObject.getJSONObject("user").getString("username");

                String urlString = "https://api.instagram.com/v1/tags/selfie/media/recent?access_token="+accessTokenString;
                URL urlForImageAccess = new URL(urlString);
                InputStream inputStream = urlForImageAccess.openConnection().getInputStream();
                tagresponse = convertStreamToString(inputStream);



            }catch (Exception e)
            {
                e.printStackTrace();
            }
            return tagresponse;
        }

        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            //jsonresultdata = result;

            Intent intent = new Intent(getApplicationContext(), ListView.class);
            if(result!=null) {
                intent.putExtra("JSON_STRING", result);
            } else{
                intent.putExtra("JSON_STRING", "");
            }
            startActivity(intent);
        }
    }

}

