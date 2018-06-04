package com.example.ahn.samplepush;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import android.app.Activity;
import android.widget.TextView;

public class View2Activity extends Activity {
    private static final String TAG = "MyIID";
    TextView wno;
    static String waitingNo;
    String regId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view2);

        wno = (TextView) findViewById(R.id.wno);
        regId = FirebaseInstanceId.getInstance().getToken();


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("bbs");
        myRef.orderByChild("nfcValue").equalTo(regId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "PARENT: "+ childDataSnapshot.getKey());
                    Log.d(TAG,"waitingNum: "+ childDataSnapshot.child("waitingNum").getValue());
                    waitingNo = childDataSnapshot.child("waitingNum").getValue().toString();
                    wno.setText("사용자의 대기번호 : " + waitingNo);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }

        });
    }
}
