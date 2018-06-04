package com.example.ahn.samplepush;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyIID";
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    EditText messageInput;
    TextView messageOutput;
    TextView log;

    String regId;                                                               //1. 단말의 등록 ID를 저장해 둘 변수 선언

    String googleId;
    int waitNum = 0;
    RequestQueue queue;                                                         //2. 데이터 전송에 사용하는 volley의 큐

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("news");
        FirebaseInstanceId.getInstance().getToken();

        Intent idintent = getIntent();
        String data = idintent.getStringExtra("value");
        googleId = data;
        messageInput = (EditText) findViewById(R.id.messageInput);
        messageOutput = (TextView) findViewById(R.id.messageOutput);
        log = (TextView) findViewById(R.id.log);
        Button sendButton = (Button) findViewById(R.id.sendButton);             //3. 전송 버튼을 눌렀을 때 메시지 전송을 위해 만든 메소드 호출
        Button nfcSend = (Button) findViewById(R.id.nfcSend);
        Button viewButton = (Button) findViewById(R.id.viewButton);
        Button view2Button = (Button) findViewById(R.id.view2Button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = messageInput.getText().toString();
                send(input);
                waitNum += 1;
            }
        });

        nfcSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SendActivity.class);
                startActivity(intent);
            }
        });
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
                startActivity(intent);
            }
        });
        view2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), View2Activity.class);
                startActivity(intent);
            }
        });

        queue = Volley.newRequestQueue(getApplicationContext());                //4. 데이터 전송에 사용하는 volley의 큐 객체 생성

        getRegistrationId();                                                    //5. 등록 ID 확인을 위한 메소드 호출

        Intent intent = getIntent();
        if (intent != null) {
            processIntent(intent);
        }


    }

    public void getRegistrationId() {
        println("getRegistrationId() 호출됨.");

        regId = FirebaseInstanceId.getInstance().getToken();                  // 1. 등록 ID를 확인하여 변수에 할당
        println("regId : " + regId);
        Log.d(TAG, "Refreshed Token : " + regId);
    }

    public void send(String input) {

        JSONObject requestData = new JSONObject();      //1. 전송 정보를 담아둘 JSONObject 객체 생성

        try {
            requestData.put("priority", "high");    //2. 옵션 추가

            JSONObject dataObj = new JSONObject();      //3. 전송할 데이터 추가
            dataObj.put("contents", input);
            requestData.put("data", dataObj);

            JSONArray idArray = new JSONArray();        //4. 수신 단말 ID를 리스트에 넣고 추가
            idArray.put(0, regId);
            requestData.put("registration_ids", idArray);

        } catch(Exception e) {
            e.printStackTrace();
        }

        sendData(requestData, new SendResponseListener() {      //5. 푸시 전송을 위해 정의한 메소드 호출
            @Override
            public void onRequestCompleted() {
                println("onRequestCompleted() 호출됨.");
            }

            @Override
            public void onRequestStarted() {
                println("onRequestStarted() 호출됨.");
            }

            @Override
            public void onRequestWithError(VolleyError error) {
                println("onRequestWithError() 호출됨.");
            }
        });

    }

    public interface SendResponseListener {     //1. volley 요청 객체를 만들고 요청을 위한 데이터 설정
        public void onRequestStarted();
        public void onRequestCompleted();
        public void onRequestWithError(VolleyError error);
    }

    public void sendData(JSONObject requestData, final SendResponseListener listener) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                "https://fcm.googleapis.com/fcm/send",
                requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onRequestCompleted();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onRequestWithError(error);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {     //2. 요청을 위한 파라미터 설정
                Map<String,String> params = new HashMap<String,String>();

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {       //3. 요청을 위한 헤더 설정
                Map<String,String> headers = new HashMap<String,String>();
                headers.put("Authorization","key=AAAAxj3LYog:APA91bHwTwsscvPgtQKkvqFSf2SDMjs0qfyl9ore5-yJEZ7i67K6XSBkQ7SGo4v3Od8fnDtasFURggDA-8nnJfBUIl1Xanp_6VneEddruBGAEia4391qSRn6tk5He9gXGO70dEwmKJXj");

                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        request.setShouldCache(false);
        listener.onRequestStarted();
        queue.add(request);
    }

    @Override
    protected void onNewIntent(Intent intent) {         //1. 서비스로부터 인텐트를 받았을 때의 처리
        println("onNewIntent() called.");

        if (intent != null) {
            processIntent(intent);
        }

        super.onNewIntent(intent);
    }


    private void processIntent(Intent intent) {
        String from = intent.getStringExtra("from");
        if (from == null) {
            println("from is null.");
            return;
        }

        String contents = intent.getStringExtra("contents");    //2. 보낸 데이터는 contents 키(Key)를 사용해 확인

        println("DATA : " + from + ", " + contents);
        messageOutput.setText("[" + from + "]로부터 수신한 데이터 : " + googleId);
        databaseReference.child("waiting").child(regId).setValue(waitNum);
    }

    public void println(String data) {
        log.append(data + "\n");
    }

}
