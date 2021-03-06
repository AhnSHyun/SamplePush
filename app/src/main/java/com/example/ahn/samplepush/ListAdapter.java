package com.example.ahn.samplepush;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ListAdapter extends BaseAdapter {
    private final static String TAG = "PINGPONG";
    List<Bbs>  datas;
    Context context;
    LayoutInflater inflater;

    public ListAdapter(List<Bbs> datas, Context context){
        Log.i(TAG, "=================================ListAdapter()");
        this.datas = datas;
        this.context = context;
        this.inflater = (LayoutInflater) context. getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        Log.i(TAG, "=================================getCount()");
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        Log.i(TAG, "=================================getItem()");
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        Log.i(TAG, "=================================getItemId()");
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(TAG, "=================================getView()");
        if(convertView == null){
            convertView = inflater.inflate(R.layout.list_item, null);
        }

        //   TextView txtTitle = (TextView)convertView.findViewById(R.id.txtTitle);
        //   TextView txtContent = (TextView)convertView.findViewById(R.id.txtContent);
        TextView txtNfcValue = (TextView)convertView.findViewById(R.id.txtNfcValue);
        TextView txtWaitingNum = (TextView)convertView.findViewById(R.id.txtWaitingNum);

        Bbs bbs = datas.get(position);
        //    txtTitle.setText(bbs.title);
        //    txtContent.setText(bbs.content);
        txtNfcValue.setText(bbs.nfcValue);
        txtWaitingNum.setText(bbs.waitingNum);

        return convertView;
    }
}