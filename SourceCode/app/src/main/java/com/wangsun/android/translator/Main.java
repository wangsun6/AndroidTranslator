package com.wangsun.android.translator;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class Main extends AppCompatActivity {
    TextView txt_hindi, txt_english, txt_greeting;
    ImageView img_convert;
    LinearLayout linearLayout;

    //voice
    private static final int REQ_CODE_VOICE_IN = 143;

    boolean isUsedOnce=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_english=(TextView) findViewById(R.id.id_input);
        txt_hindi=(TextView) findViewById(R.id.id_output);
        txt_greeting=(TextView) findViewById(R.id.id_greeting);
        img_convert=(ImageView) findViewById(R.id.id_convert);
        linearLayout=(LinearLayout) findViewById(R.id.id_layout);



        img_convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline()){
                    if (!isUsedOnce){
                        txt_greeting.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                    startVoiceToTextService();
                }
            }
        });

    }

    //voice
    private void startVoiceToTextService() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //You can set here own local Language.
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "I am listening...");
        try {
            startActivityForResult(intent, REQ_CODE_VOICE_IN);
        }
        catch (ActivityNotFoundException a) {
            Toast.makeText(Main.this,a.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_VOICE_IN: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String voiceText=result.get(0);
                    txt_english.setText(voiceText);

                    if(!txt_english.getText().toString().equals("")){
                        HttpRequest httpRequest=new HttpRequest(Main.this,txt_hindi);
                        httpRequest.execute(txt_english.getText().toString());
                    }
                    else {
                        Toast.makeText(Main.this,"Enter input...",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }

        }
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected())
            return true;
        else {
            Toast.makeText(Main.this,"Check network connection.",Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
