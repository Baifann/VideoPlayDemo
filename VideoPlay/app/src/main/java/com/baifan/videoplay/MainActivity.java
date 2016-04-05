package com.baifan.videoplay;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.baifan.videoplay.view.CircleProgressButton;

/**
 * 一个视频录制并且播放的项目
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mBtnPlay;

    private ImageView mImgVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initEvents();
    }

    /**
     * 初始化控件
     */
    private void initViews(){
        mBtnPlay = (Button) findViewById(R.id.btn_play);
        mImgVideo = (ImageView) findViewById(R.id.img_video);
    }

    /**
     * 初始化事件
     */
    private void initEvents(){
        mBtnPlay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, RecordVideoActivity.class);
        this.startActivity(intent);
    }
}
