package com.baifan.videoplay.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.baifan.videoplay.R;


/**
 * Created by baifan on 16/2/29.
 */
public class CircleProgressButton extends Button implements
        View.OnTouchListener,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        Runnable{
    /**
     * 开始角度
     */
    private float mStartAngle = -90;
    /**
     * 进度角度
     */
    private float mSweepAngle = 0;
    /**
     * 中心坐标X
     */
    private float mCenterX;
    /**
     * 中心坐标Y
     */
    private float mCenterY;
    /**
     * 进度的画笔
     */
    private Paint mPaintProgress;
    /**
     * 当前角度
     */
    private float mCurrentAngle;
    /**
     * 直径
     */
    private float mDiameter;

    private int mTop;
    private int mBottom;
    private int mLeft;
    private int mRight;
    /**
     * 是否开始进度
     */
    private boolean isStartProgress;
    /**
     * 手势类
     */
    private GestureDetectorCompat mDetector;

    /**
     * 线程
     */
    private Thread mThread;
    /**
     * progress与边界的距离
     */
    private final static int PROGRESS_PADDING = 10;
    /**
     * 画笔宽度
     */
    private  final static float PAINT_WIDTH = 10f;
    /**
     * 是否增加角度
     */
    private boolean isAddAngle;
    /**
     * 变化角度
     */
    private float mChangeAngle = 2;
    /**
     * 监听
     */
    private OnChangeProgressButtonListener mListener;

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public interface OnChangeProgressButtonListener{
        void onStartPressed();

        void onEndPressed();
        //释放手指
        void onReleasePressed();
    }

    public CircleProgressButton(Context context) {
        this(context, null);
    }

    public CircleProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        //初始化事件
        initEvent();
    }

    /**
     * 设置监听
     * @param listener
     */
    public void setOnChangeProgressButtonListener(OnChangeProgressButtonListener listener){
        mListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initDiameter(canvas.getClipBounds());
//        canvas.drawRect(mLeft, mTop, mRight, mBottom, mPaintProgress);
        RectF rectF = new RectF(
                mLeft + PROGRESS_PADDING,
                mTop + PROGRESS_PADDING, mRight - PROGRESS_PADDING,
                mBottom - PROGRESS_PADDING
        );
//        canvas.drawCircle(mCenterX, mCenterY, (mDiameter - PAINT_WIDTH) / 2, mPaintProgress);
        Path path = new Path();
        path.addArc(rectF, mStartAngle, mSweepAngle);
        canvas.drawPath(path, mPaintProgress);
    }

    /**
     * 改变sweepAngle
     * @param angle
     */
    private void setSweepAngle(float angle){
        mSweepAngle = angle;
    }

    /**
     * 初始化背景
     */
    private void initBackGround(){
        setBackgroundResource(R.drawable.selector_circle_btn);
    }

    /**
     * 初始化画笔
     */
    private void initPaint(){
        mPaintProgress = new Paint();
        mPaintProgress.setAntiAlias(true);
        mPaintProgress.setColor(Color.RED);
        mPaintProgress.setStrokeWidth(PAINT_WIDTH);
        mPaintProgress.setStyle(Paint.Style.STROKE);
    }

    /**
     * 初始化事件
     */
    private void initEvent(){
        initPaint();
        initBackGround();

        mDetector = new GestureDetectorCompat(getContext(), this);
        mDetector.setOnDoubleTapListener(this);
        setOnTouchListener(this);
    }

    /**
     * 初始化progress直径
     */
    private void initDiameter(Rect rect){
        int height = Math.abs(rect.bottom - rect.top);
        int width = Math.abs(rect.right - rect.left);
        mCenterY = rect.top + height / 2;
        mCenterX = rect.left + width / 2;
        mDiameter = Math.max(height, width);
        mTop = (int) (mCenterY - mDiameter / 2);
        mLeft = (int) (mCenterX - mDiameter / 2);
        mBottom = (int) (mCenterY + mDiameter / 2);
        mRight = (int) (mCenterX + mDiameter / 2);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(mDetector.onTouchEvent(event) == true){

        }else{
            if (event.getAction() == MotionEvent.ACTION_DOWN)
            {
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE)
            {
            }
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                isAddAngle = false;
                //抬起监听
                if(mListener != null && isStartProgress){
                    mListener.onReleasePressed();
                }
            }
        }
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    /**
     * 开始进度
     */
    private void startProgress(){
        isStartProgress = true;
        isAddAngle = true;
        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    public void run() {
        while(isStartProgress){
            long start = System.currentTimeMillis();
            if(isAddAngle){
                addSweepAngle();
            }else{
                reduceSeepAngle();
            }
            long end = System.currentTimeMillis();
            if(end - start < 50){
                try {
                    Thread.sleep(50 - (end - start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 减少角度
     */
    private void reduceSeepAngle(){
        mSweepAngle = mSweepAngle - mChangeAngle;
        if(mSweepAngle == 0){
            //当旋转角度等于0° 停止转动
            isStartProgress = false;
        }
        postInvalidate();
    }

    /**
     * 增加角度
     */
    private void addSweepAngle(){
        mSweepAngle = mSweepAngle + mChangeAngle;
        if(mSweepAngle == 360){
            //当旋转角度等于360° 停止转动
            isStartProgress = false;
            if(mListener != null){
                //调用结束回调
                mListener.onEndPressed();
            }
        }
        postInvalidate();
    }

    /**
     * 长按
     * @param e
     */
    @Override
    public void onLongPress(MotionEvent e) {
        //开始进度
        startProgress();
        //长按开始监听
        if(mListener != null){
            mListener.onStartPressed();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int maxLength = Math.max(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(maxLength, maxLength);
    }
}
