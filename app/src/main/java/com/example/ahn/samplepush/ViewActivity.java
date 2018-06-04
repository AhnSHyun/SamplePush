package com.example.ahn.samplepush;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;

public class ViewActivity extends Activity {

    private static final String TAG = "MyIID";
    static int waitingNum = 0;
    String waitingNum2;
    String nfcValue;

    TextView editNFCValue, editWaitingNum;


    ListView listView;
    ListAdapter adapter;
    List<Bbs> datas = new ArrayList<>();

    // Write a message to the database
    FirebaseDatabase database;
    DatabaseReference bbsRef;

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);


        // 1. 파이어베이스 연결 - DB Connection
        database = FirebaseDatabase.getInstance();

        // 2. CRUD 작업의 기준이 되는 노드를 레퍼러느로 가져온다.
        bbsRef = database.getReference("bbs");

        // 3. 레퍼런스 기준으로 데이터베이스에 쿼리를 날리는데, 자동으로 쿼리가 된다.
        //    ( * 파이어 베이스가
        bbsRef.addValueEventListener(postListener);

        // 4. 리스트뷰에 목록 세팅
        listView = (ListView)findViewById(R.id.listView);
        adapter = new ListAdapter(datas, this);
        listView.setAdapter(adapter);

        // 위젯.
        editNFCValue = (TextView)findViewById(R.id.editNFCValue);
        editWaitingNum = (TextView)findViewById(R.id.editWaitingNum);


                String NFCValue = editNFCValue.getText().toString();
                String WaitingNum = editWaitingNum.getText().toString();

                // 6
                // 6.1 bbs 레퍼런스 (테이블)에 키를 생성한다.
                String key = bbsRef.push().getKey();

                // 6.2 입력될 키, 값 세트 (레코드)를 생성.
                Map<String, String > postValues = new HashMap<>();
                postValues.put("nfcValue", nfcValue);
                postValues.put("waitingNum", waitingNum2);


                // 6.3 생성된 레코드를 데이터베이스에 입력.

                // 3.1 방식
                //Map<String, Object> keyMap = new HashMap<>();
                //keyMap.put(key, postValues);
                //bbsRef.updateChildren(keyMap);

                // 3.2.1 방식
                DatabaseReference keyRef = bbsRef.child(key);
                keyRef.setValue(postValues);

                // 3.2.2 하위 트리 내용 가져오기
                //DatabaseReference titleRef = bbsRef.child(key).child("title");
                //titleRef.setValue("해결!");






    }

    @Override
    protected void onResume(){
        super.onResume();
        Intent intent = getIntent();



        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {

            waitingNum += 1;
            waitingNum2 = String.valueOf(waitingNum);





            Parcelable[] rawMessages = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);

            NdefMessage message = (NdefMessage) rawMessages[0]; // only one message transferred
            mTextView.setText(new String(message.getRecords()[0].getPayload()));
            nfcValue = new String(message.getRecords()[0].getPayload());

            // 여기다가 데이터전송만 넣으면 될거같은데
            //바로 nfcvalue 받아서
            String key = bbsRef.push().getKey();
            Map<String, String > postValues = new HashMap<>();
            postValues.put("nfcValue", nfcValue);
            postValues.put("waitingNum", waitingNum2);
            DatabaseReference keyRef = bbsRef.child(key);
            keyRef.setValue(postValues);

        }


    }

    // 5. 파이어베이스가 호출해주는 이벤트 리스너 콜백
    // ValueEventListener : 경로의 전체 내용에 대한 변경을 읽고 수신 대기합니다.
    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // 위에 선언한 저장소인 datas를 초기화하고
            datas.clear();
            // bbs 레퍼런스의 스냅샷을 가져와서 레퍼런스의 자식노드를 바복문을 통해 하나씩 꺼내서 처리.
            for( DataSnapshot snapshot : dataSnapshot.getChildren() ) {
                String key  = snapshot.getKey();
                Bbs bbs = snapshot.getValue(Bbs.class); // 컨버팅되서 Bbs로........
                bbs.key = key;
                datas.add(bbs);

            }
            adapter.notifyDataSetChanged();
        }


        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w("MainActivity", "loadPost:onCancelled", databaseError.toException());
            // ...
        }
    };

}