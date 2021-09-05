package com.liduo.dayswithyou.adapters;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liduo.dayswithyou.R;
import com.liduo.dayswithyou.beans.EventCardBean;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by LeeDuo on 2020/3/2.
 */

public class EventCardListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnTouchListener {

    private ArrayList<EventCardBean> eventCardBeanArrayList;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private float coordinateX,coordinateY;

    public EventCardListAdapter(ArrayList<EventCardBean> eventCardBeanArrayList){
        this.eventCardBeanArrayList = eventCardBeanArrayList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_view_show,parent,false);
        return new EventCardViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((EventCardViewHolder)holder).eventCardCV.setCardBackgroundColor(Color.parseColor(eventCardBeanArrayList.get(position).getColor()));
        setOnItemClickListener(((EventCardViewHolder)holder).eventBackGroundLL,position);
        int days = eventCardBeanArrayList.get(position).getDays();
        if(days>0)
            ((EventCardViewHolder)holder).eventDaysTV.setText("还有"+days+"天");
        else if(days<0)
            ((EventCardViewHolder)holder).eventDaysTV.setText("过去"+(-days)+"天");
        else
            ((EventCardViewHolder)holder).eventDaysTV.setText("今天");

        ((EventCardViewHolder)holder).eventNameTV.setText(eventCardBeanArrayList.get(position).getName());

        StringBuilder otherSb = new StringBuilder();
        otherSb.append(eventCardBeanArrayList.get(position).getDate());

        String people = eventCardBeanArrayList.get(position).getPeople();
        if(people!= null && !TextUtils.equals(people,"")){
            otherSb.append(" | ");
            otherSb.append(people);
        }
        String place = eventCardBeanArrayList.get(position).getPlace();
        if(place != null && !TextUtils.equals(place,"")){
            otherSb.append(" | ");
            otherSb.append(place);
        }
        ((EventCardViewHolder)holder).eventOthersTV.setText(otherSb.toString());
    }

    @Override
    public int getItemCount() {
        return eventCardBeanArrayList.size();
    }

    public void setData(ArrayList<EventCardBean> eventCardBeanArrayList){
        this.eventCardBeanArrayList = eventCardBeanArrayList;
        notifyDataSetChanged();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        coordinateX = event.getRawX();
        coordinateY = event.getRawY();
        return false;
    }

    public static  class EventCardViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.app_item_cv_event_card)
        public CardView eventCardCV;
        @BindView(R.id.app_item_tv_event_name)
        public TextView eventNameTV;
        @BindView(R.id.app_item_tv_event_others)
        public TextView eventOthersTV;
        @BindView(R.id.app_item_tv_event_days)
        public TextView eventDaysTV;
        @BindView(R.id.app_item_ll_event_bg)
        public LinearLayout eventBackGroundLL;

        public EventCardViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener){
        this.onItemLongClickListener = onItemLongClickListener;
    }

    private void setOnItemClickListener(final View v, final int position){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null)
                    onItemClickListener.onItemClick(position);
            }
        });
        v.setOnTouchListener(this);
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onItemLongClickListener != null)
                    return onItemLongClickListener.onItemLongClick(v, position,coordinateX,coordinateY);
                return false;
            }
        });
    }


}
