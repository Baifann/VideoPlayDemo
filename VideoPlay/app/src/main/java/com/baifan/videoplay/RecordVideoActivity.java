package com.baifan.videoplay;

import android.app.Activity;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.baifan.videoplay.view.CircleProgressButton;

import java.io.File;
import java.io.IOException;

/**
 * Created by baifan on 16/3/18.
 */
public class RecordVideoActivity extends Activity implements
        CircleProgressButton.OnChangeProgressButtonListener,
        SurfaceHolder.Callback {
    private final static String VEDIO_PATH = "/sdcard/video.mp4";
    /**
     * 预览是的摄像头
     */
    private Camera mCamera;
    /**
     * 录制时的摄像头
     */
    private Camera mVedioCamera;
    /**
     * 显示摄像按钮
     */
    private SurfaceView mSvVideo;
    /**
     * 录制视频按钮
     */
    private CircleProgressButton mCpbRecordVideo;
    /**
     * 视频录制类
     */
    private MediaRecorder mMediaRecorder;
    /**
     * 检测是否正在录制
     */
    private boolean isRecording;
    /**
     * 视频播放类
     */
    private MediaPlayer mMediaPlayer;
    /**
     * holder
     */
    private SurfaceHolder mHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_video);
        initViews();
        initEvents();
    }

    /**
     * 初始化控件
     */
    private void initViews(){
        mSvVideo = (SurfaceView) findViewById(R.id.sv_video);
        mCpbRecordVideo = (CircleProgressButton) findViewById(R.id.cpb_record);
    }

    /**
     * 初始化事件
     */
    private void initEvents(){
        mCpbRecordVideo.setOnChangeProgressButtonListener(this);

        mHolder = mSvVideo.getHolder();
        //设置回调
        mHolder.addCallback(this);
    }

    /**
     * 开始录制视频
     */
    private void startVedio(){

        relaseCamera();
        final File file = new File(VEDIO_PATH);
        if(file.exists()){
            file.delete();
        }

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.reset();
        //设置后置摄像头
        mVedioCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        if(mVedioCamera != null){
            mVedioCamera.setDisplayOrientation(90);
            mVedioCamera.unlock();
            mMediaRecorder.setCamera(mVedioCamera);
        }
        //将视频角度旋转
        mMediaRecorder.setOrientationHint(90);
        //录制音频录入源
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置视频图像的录入源
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //设置录入媒体的输出格式
//        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        //设置音频的编码格式
//        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//        //设置视频的编码格式
//        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
//        //设置视频的采样率
//        mMediaRecorder.setVideoFrameRate(4);
        //设置高质量视频 如果使用这个那么上面就需要注视
        mMediaRecorder.setProfile(CamcorderProfile.get(1, CamcorderProfile.QUALITY_HIGH));
        //设置录制视频文件的输出路径
        mMediaRecorder.setOutputFile(file.getAbsolutePath());
        //设置捕捉视频图像的预览界面
        mMediaRecorder.setPreviewDisplay(mHolder.getSurface());

        mMediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                //发送错误，停止录制
                stopMediaRecoder();
                Toast.makeText(RecordVideoActivity.this, "录制出错" ,Toast.LENGTH_SHORT).show();
            }
        });

        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            isRecording = true;
            Toast.makeText(this, "开始录制", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放相机资源
     */
    private void relaseCamera(){
        if(mCamera != null){
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 开始预览相机内容
     */
    private void setStartPreview(Camera camera, SurfaceHolder surfaceHolder){
        try {
            //将camera和surfaceHolder绑定
            camera.setPreviewDisplay(surfaceHolder);
            //系统默认为横屏 转移画面
            camera.setDisplayOrientation(90);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Camera对象
     * 要获取硬件Camera类而不是图形的
     * @return
     */
    private Camera getCamera(){
        Camera camera;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            camera = null;
            e.printStackTrace();
        }
        return camera;
    }

    /**
     * 停止mediaPlayer
     */
    private void stopMediaRecoder(){
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
        isRecording = false;
    }

    /**
     * 停止播放视频
     */
    private void stopVedio(){
        if(isRecording){
            stopMediaRecoder();
//            Toast.makeText(this, "停止录制，并保存文件", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if(isRecording){
            stopMediaRecoder();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        relaseCamera();
    }

    @Override
    protected void onResume() {
        if(mCamera == null){
            mCamera = getCamera();
            if(mHolder != null){
                setStartPreview(mCamera, mHolder);
            }
        }
        super.onResume();
    }

    @Override
    public void onStartPressed() {
        startVedio();
    }

    @Override
    public void onEndPressed() {
        stopVedio();
    }

    @Override
    public void onReleasePressed() {
        stopVedio();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(mCamera != null){
            mCamera.stopPreview();
            setStartPreview(mCamera, holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
