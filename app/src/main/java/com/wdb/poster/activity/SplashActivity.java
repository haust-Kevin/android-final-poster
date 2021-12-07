package com.wdb.poster.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIDrawableHelper;
import com.qmuiteam.qmui.util.QMUIWindowHelper;
import com.wdb.poster.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SplashActivity extends Activity {

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        getWindow().setBackgroundDrawableResource(R.drawable.startup);
        handler = new Handler(getMainLooper());
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://raw.githubusercontent.com/haust-Kevin/share/main/startup.jpg").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) {
                System.out.println(response.headers().toString());
                ResponseBody body = response.body();
                if (body != null) {
                    Drawable drawable = Drawable.createFromStream(body.byteStream(), null);
                    handler.post(() -> getWindow().setBackgroundDrawable(drawable)
                    );
                }
            }
        });
        waitEnterMain();
    }


    private void waitEnterMain() {
        new Thread(() -> {
            try {
                Thread.sleep(1500);
                Intent it = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(it);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


}