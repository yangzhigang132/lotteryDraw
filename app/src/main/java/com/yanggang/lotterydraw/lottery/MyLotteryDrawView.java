package com.yanggang.lotterydraw.lottery;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yanggang.lotterydraw.R;


public class MyLotteryDrawView extends FrameLayout {
    private ImageView mImageViewBg;
    private ImageView mImageViewCenter;
    private AnimationDrawable mDrawable;
    private TextView mTextViewCenterTitle;
    private DrawLotteryView mDrawLotteryView;
    public MyLotteryDrawView(Context context){
          super(context);
        init(context);
    }
    public MyLotteryDrawView(Context context, AttributeSet attrs){
        super(context,attrs);
        init(context);
    }
    public MyLotteryDrawView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        init(context);
    }
    private  void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.merger_layout_lottery,this,true);
        mImageViewBg = findViewById(R.id.iv_img);
        mImageViewCenter = findViewById(R.id.btn_lottery);
        mTextViewCenterTitle = findViewById(R.id.tv_title);
        mDrawLotteryView  = findViewById(R.id.view_Lottery);
    }
    public MyLotteryDrawView setBasckgourd(int duringTime,int ... resIds){
         if(resIds == null || resIds.length<1){
             return this;
         }
         if(resIds.length == 1){
             mImageViewBg.setBackground(getResources().getDrawable(resIds[0]));
             return this;
         }
         mDrawable = new AnimationDrawable();
         for(int i = 0;i<resIds.length ;i++){
             mDrawable.addFrame(getResources().getDrawable(resIds[i]),duringTime);
         }
        return this;
    }
    public MyLotteryDrawView start(){
        if(mDrawable != null){
            mImageViewBg.setBackground(mDrawable);
            mDrawable.setOneShot(false);
            mDrawable.start();
        }
        return this;
    }
    public MyLotteryDrawView setBasckgourdCenter(int ids){
        mImageViewCenter.setBackground(getResources().getDrawable(ids));
        return this;
    }
    public MyLotteryDrawView setCenterTitle(String title){
        mTextViewCenterTitle.setText(title);
        return this;
    }
    public MyLotteryDrawView setCenterTitle(final DrawLotteryView.CallBack callBack){
        mDrawLotteryView.setCallBack(callBack);
        mImageViewCenter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callBack!= null){
                    callBack.setDrawwLottery(v);
                }
            }
        });
        return this;
    }

    /**
     *   初始化view
     * @param isCache  true 缓冲
     */
    public void updateView(boolean isCache){
        mDrawLotteryView.createBitmap(isCache);
    }

    /**
     *   旋转 开始
     * @param index  表示第几个
     */
    public void startDraw(int index){
        mDrawLotteryView.startAnim(index);
    }

    /**
     * 是否支援
     */
    public void realease(){
        if(mDrawLotteryView != null){
            mDrawLotteryView.realse();
        }
    }
}

