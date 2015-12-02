package com.shaiban.geo.geoapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Mohammed on 10/8/2015.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder>  {
    static Context context;
    ArrayList<MyMarker> nList;
    private OnItemClickListener mOnItemClickListener;
    LayoutInflater layoutInflater;
    static int viewType=0;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView name;
        public final ImageView photo;
        public final LinearLayout linearLayout;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            //mImageView = (ImageView) view.findViewById(R.id.avatar);
            photo = (ImageView) view.findViewById(R.id.photo);
            name=(TextView)view.findViewById(R.id.name);
            linearLayout=(LinearLayout)view.findViewById(R.id.nexScreen);
/*
            Typeface typeface_regular= Typeface.createFromAsset(context.getAssets(),"Roboto-Bold.ttf");
            monthtext.setTypeface(typeface_regular);

            Typeface typeface= Typeface.createFromAsset(context.getAssets(),"Roboto-Regular.ttf");
            hdesc.setTypeface(typeface);
            htype.setTypeface(typeface);
            hnumday.setTypeface(typeface);*/
        }
    }

    public HomeAdapter(ArrayList<MyMarker> items, OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        nList = items;
    }



    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context= parent.getContext();
        if(layoutInflater == null)
        {
            layoutInflater = LayoutInflater.from(context);
        }
        this.viewType = viewType;
        View view = new View(context);
        view = layoutInflater.inflate(R.layout.item_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MyMarker oList;
        oList = nList.get(position);
         //   holder.photo.setText(oList.getDate());
            holder.name.setText(oList.getName());
        Picasso.with(context)
                .load("http://172.31.98.112:8000"+oList.getmIcon())
                .error(R.drawable.abc_action_bar_item_background_material)
                .into(holder.photo);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, position);

                Intent i = new Intent(context,MainActivity.class);
                i.putExtra("lat", oList.getLatitude());
                i.putExtra("long", oList.getLongitude());
                i.putExtra("place",oList.getPlace());
                context.startActivity(i);
            }
        });


    }
    @Override
    public int getItemCount() {
        return nList.size();
    }


}
