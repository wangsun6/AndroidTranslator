package com.wangsun.android.translator;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


/**
 * Created by WANGSUN on 03-Aug-18.
 */

public class HttpRequest extends AsyncTask<String,Void,String> {
    String API_KEY="Your API KEY";

    Context context;

    JSONObject root_object,data_object,translated_object;
    JSONArray jsonArray;

    TextView txt_hindi;

    ProgressDialog progressDialog;


    public HttpRequest(Context context, TextView txt_hindi){
        this.context=context;
        this.txt_hindi=txt_hindi;
    }

    @Override
    protected void onPreExecute() {
        progressDialog=new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Converting....");
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        String temp_input=params[0];

        try {
            URL url = new URL("https://translation.googleapis.com/language/translate/v2?target=hi&key="+API_KEY+"&q="+temp_input);
            HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();

            String json_string;
            while((json_string=bufferedReader.readLine())!=null){
                stringBuilder.append(json_string+"\n");
            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            return stringBuilder.toString().trim();
        }

        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        convertJson(result);
    }

    private void convertJson(String result){
        try {
            root_object = new JSONObject(result);
            data_object = root_object.getJSONObject("data"); //new JSONObject("data");
            jsonArray = data_object.getJSONArray("translations");
            translated_object = jsonArray.getJSONObject(0);
            String temp_hindi=translated_object.getString("translatedText");

            txt_hindi.setText(temp_hindi);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialog.dismiss();
    }
}
