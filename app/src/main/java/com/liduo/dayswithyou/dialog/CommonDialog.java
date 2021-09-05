package com.liduo.dayswithyou.dialog;


import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


import com.liduo.dayswithyou.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by LeeDuo on 2019/6/16.
 */

public class CommonDialog extends android.support.v4.app.DialogFragment {

    private View mView;
    private Unbinder unbinder;
    private OnCommonDialogClickListener onCommonDialogClickListener;
    private String contentText,leftText,rightText;
    private int contextTextColor,leftTextColor,rightTextColor;

    @BindView(R.id.app_tv_dialog_content)
    public TextView dialogContentTextView;

    @BindView(R.id.app_tv_dialog_left_choice)
    public TextView dialogLeftTextView;

    @BindView(R.id.app_tv_dialog_right_choice)
    public TextView dialogRightTextView;

    @OnClick({R.id.app_tv_dialog_left_choice,R.id.app_tv_dialog_right_choice})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.app_tv_dialog_left_choice:
                if(onCommonDialogClickListener != null)
                    onCommonDialogClickListener.clickLeft(getDialog());
                break;
            case R.id.app_tv_dialog_right_choice:
                if(onCommonDialogClickListener != null)
                    onCommonDialogClickListener.clickRight(getDialog());

                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        mView = inflater.inflate(R.layout.app_dialog_common,container,false);
        unbinder = ButterKnife.bind(this,mView);
        setTextAndColor();
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.dimAmount = 0.6f;
        layoutParams.gravity = Gravity.CENTER;
//        layoutParams.windowAnimations = R.style.Animation_DialogFragment_normal;
        getDialog().getWindow().setAttributes(layoutParams);

        getDialog().getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.app_dialog_common_bg,null));

        getDialog().setCancelable(true);

    }

    private void setTextAndColor(){
        if(contentText != null) dialogContentTextView.setText(contentText);
        if(contextTextColor != 0) dialogContentTextView.setTextColor(contextTextColor);
        if(leftText != null) dialogLeftTextView.setText(leftText);
        if(leftTextColor != 0) dialogLeftTextView.setTextColor(leftTextColor);
        if(rightText != null) dialogRightTextView.setText(rightText);
        if(rightTextColor != 0) dialogRightTextView.setTextColor(rightTextColor);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void setDialogContent(String text){
        contentText = text;

    }

    public void setDialogContentTextColor(@ColorRes int resColor){
        contextTextColor = resColor;
    }

    public void setDialogLeftText(String lText){
        leftText = lText;
    }

    public void setDialogLeftTextColor(@ColorRes int resColor){
        leftTextColor = resColor;
    }

    public void setDialogRightText(String rText){
        rightText = rText;
    }

    public void setDialogRightTextColor(@ColorRes int resColor){
        rightTextColor = resColor;
    }

    public void setOnDialogClickListener(OnCommonDialogClickListener onCommonDialogClickListener){
        this.onCommonDialogClickListener = onCommonDialogClickListener;
    }

}
