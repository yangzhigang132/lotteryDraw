package com.yanggang.lotterydraw.lottery;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DrawLotteryView extends View implements Runnable {
    public  interface CallBack{
        int getItems();
        String getTite(int postionss);
        Object getIcon(int postionss);
        Object getColor(int postionss);
        void setDrawwLottery(View virew);
    }
    private CallBack mCallBack;

    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    private float mViewWidth;
    private float mViewHeight;
    private int num = 6;
    private float everyPer;
    private Paint mPaint;
    private Paint mTextPaint;
    private float mTextHight= 20.0f;
    private float scaleH = 40;
    private float mIconTop= 10;
    private float mRadiu;
    private int select = 5;
    private float startRotation = 0;
    private Paint mBitmapPaint;
    private List<Bitmap> mBitmapsIcons;
    private Thread mThread ;
    private  int[] bgColors=new int[]{
            Color.parseColor("#FFF2B7"),
            Color.parseColor("#E9BFFF")
    };
    private float mIconRadiu;
    private float mMidIcon;
    public DrawLotteryView(Context context) {
        super(context);
        init();
    }

    public DrawLotteryView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DrawLotteryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DrawLotteryView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        mPaint = new Paint();
        mTextPaint = new Paint();
        mTextPaint.setTextSize(18.0F);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.BLUE);
        mPaint.setAntiAlias(true);
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setFilterBitmap(true);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mCallBack == null || mCallBack.getItems() == 0){
          return;
        }
        mViewWidth = canvas.getWidth();
        mViewHeight = canvas.getHeight();
        everyPer = 360 * 1.0F / mCallBack.getItems();
        mIconRadiu = ( mViewWidth - mTextHight * 2) / 2;
        mRadiu =  ( mViewWidth - mTextHight * 2 - mIconTop * 2 - scaleH * 2) / 2;
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG| Paint.FILTER_BITMAP_FLAG));
        Path path;
        RectF rectF = new RectF(0 + mTextHight,0 + mTextHight,mViewWidth - mTextHight,mViewHeight -mTextHight);
        for(int i = 0;i< mCallBack.getItems();i++){
            if(mCallBack.getColor(i) == null){
                mPaint.setColor(bgColors[i % bgColors.length]);
            }else {
                if(mCallBack.getColor(i) instanceof Integer){
                    mPaint.setColor((Integer)mCallBack.getColor(i));
                }else if(mCallBack.getColor(i) instanceof String){
                    mPaint.setColor(Color.parseColor((String)mCallBack.getColor(i)));
                }
            }
            canvas.drawArc(0,
                    0,mViewWidth
                    ,mViewHeight,-(90 + everyPer/ 2) + i * everyPer ,
                    everyPer,
                    true,mPaint);
//            float left = rectF.left;
            path = new Path();
//            String title = mCallBack.getTite(i);
            // 应该 根据弧长和文字长度计算 这里简单计算 微调
            path.addArc(rectF,-(90 + everyPer/ 2) + i * everyPer +  everyPer / 3,
                    everyPer);
            canvas.drawTextOnPath(mCallBack.getTite(i),path,0,0,mTextPaint);
            canvas.save();
            canvas.translate(mViewWidth / 2,mViewHeight/ 2);
            canvas.rotate(i * everyPer);
            canvas.translate(-mViewWidth / 2,-mViewHeight/ 2);
            Bitmap bitmap = mBitmapsIcons.get(i);
            if(bitmap!=null){
                canvas.drawBitmap(bitmap,mViewWidth / 2.0F - bitmap.getWidth() / 2.0F,mTextHight + mIconTop,mBitmapPaint);
            }
            canvas.restore();
        }
        mPaint.setColor(Color.parseColor("#5effffff"));
        LinearGradient gradient = new LinearGradient(mTextHight,mTextHight,mViewWidth / 2 + mTextHight,mViewWidth / 2 + mTextHight,
                Color.parseColor("#ffffffff"),
                Color.parseColor("#5effffff"),
                Shader.TileMode.CLAMP);
        mPaint.setShader(gradient);
        canvas.drawCircle(mViewWidth / 2,mViewHeight/ 2,mViewWidth / 2 - mTextHight,mPaint);
    }

    private Animation mAnimation;
    private ObjectAnimator mObjectAnimator;
    public void startAnim(int index){
        if(mObjectAnimator != null && mObjectAnimator.isRunning()){
            Toast.makeText(getContext(),"我还没转完，点我干嘛", Toast.LENGTH_LONG).show();
            return;
        }
        select = index + 1;
        Toast.makeText(getContext(),select+"", Toast.LENGTH_LONG).show();
        mObjectAnimator = ObjectAnimator.ofFloat(this,"rotation",startRotation,  360  * 15,  360   - everyPer * (select -1));
        startRotation =    360   - everyPer * (select -1);
        mObjectAnimator.setDuration(6000);
        mObjectAnimator.start();
    }

    public void  createBitmap(boolean isCache){
        if(mCallBack == null || mCallBack.getItems() == 0){
            return;
        }
        if(null == mBitmapsIcons){
            mBitmapsIcons = new ArrayList<>();
        }
        if(mBitmapsIcons!= null && mCallBack.getItems() == mBitmapsIcons.size()){
           invalidate();
           return;
        }
        if(isCache){
            getBitmap( mCallBack.getItems()-1);
        }else {
            if(mThread == null){
                mThread = new Thread(this);
            }
            mThread.start();
        }
    }

    private void getBitmap(final int index){
        if(index  <= -1){
            Collections.reverse(mBitmapsIcons );
            DrawLotteryView.this.invalidate();
            return;
        }
        Glide.with(getContext()).load(mCallBack.getIcon(index))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        mBitmapsIcons.add( null);
                        getBitmap(index - 1);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if(resource instanceof BitmapDrawable){
                            Bitmap bitmap  =((BitmapDrawable)resource).getBitmap();
                            Matrix matrix = new Matrix();
                            float scale =(float) Math.min(scaleH * 1.0/bitmap.getWidth(),scaleH * 1.0/bitmap.getHeight());
                            matrix.setScale(scale,scale);
                            bitmap = Bitmap.createBitmap(bitmap,0,0,
                                    bitmap.getWidth(),bitmap.getHeight(),matrix,false);
                            mBitmapsIcons.add(bitmap);
                        }else {
                            mBitmapsIcons.add( null);
                        }
                        getBitmap(index - 1);
                        return false;
                    }
                }).preload();
    }

    @Override
    public void run() {
//        getBitmAP(mCallBack.getItems() - 1);
        Bitmap bitmap=null;
        for(int i = 0;i< mCallBack.getItems();i++){
            if(mCallBack.getIcon(i) instanceof Integer){
                 bitmap = BitmapFactory.decodeResource(getResources(),(Integer)mCallBack.getIcon(i));
            }else if(mCallBack.getIcon(i) instanceof String){
                try{
                    bitmap = BitmapFactory.decodeStream(new URL((String)mCallBack.getIcon(i))
                            .openStream());
                }catch (Exception e){
                   e.printStackTrace();
                }
            }
            if(bitmap != null){
                Matrix matrix = new Matrix();
                float scale =(float) Math.min(scaleH * 1.0/bitmap.getWidth(),scaleH * 1.0/bitmap.getHeight());
                matrix.setScale(scale,scale);
                bitmap = Bitmap.createBitmap(bitmap,0,0,
                        bitmap.getWidth(),bitmap.getHeight(),matrix,false);
            }
            mBitmapsIcons.add(bitmap);
        }
        post(new Runnable() {
            @Override
            public void run() {
                DrawLotteryView.this.invalidate();
            }
        });
    }
    public void realse(){
        if(mBitmapsIcons != null  && mBitmapsIcons.size()>0){
            for(Bitmap bitmap  :mBitmapsIcons){
                if(!bitmap.isRecycled()){
                    bitmap.recycle();
                }
            }
            mBitmapsIcons.clear();
        }
    }
}
