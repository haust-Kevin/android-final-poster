package com.wdb.poster.handler;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class ToastHandler {
    private Context context;
    private Handler handler;

    public ToastHandler(Context context) {
        this.context = context;
        handler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                Toast.makeText(context, (String) msg.obj, msg.what).show();

            }
        };
    }

    public void toast(String msgStr) {
        Message msg = new Message();
        msg.what = Toast.LENGTH_SHORT;
        msg.obj = msgStr;
        handler.sendMessage(msg);
    }

    public void toastLength(String msgStr) {
        Message msg = new Message();
        msg.what = Toast.LENGTH_LONG;
        msg.obj = msgStr;
        handler.sendMessage(msg);
    }

}
