package com.rayadev.byoc.ui.main;

import android.content.Context;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rayadev.byoc.R;
import com.rayadev.byoc.model.Converter;

import java.util.ArrayList;

public class HomeSetRecyclerViewAdapter extends RecyclerView.Adapter<HomeSetRecyclerViewAdapter.ConverterBoxViewHolder> {

    private ArrayList<Converter> mConverterArrayList; //Holds the Converter data to be populated into the RecyclerView
    private LayoutInflater mLayoutInflater;
    private ConverterClickListener mClickListener;
    private View mItemView;


    //Passes the data into the constructor the Adapter to use.
    public HomeSetRecyclerViewAdapter(Context context, ConverterClickListener converterClickListener) {
        mLayoutInflater = LayoutInflater.from(context);
        this.mClickListener = converterClickListener;

    }

    @NonNull
    @Override
    public ConverterBoxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        mItemView = mLayoutInflater.inflate(R.layout.converter_box, parent, false);
//        setClickListener(mClickListener);
        return new ConverterBoxViewHolder(mItemView);
    }


    //This is where the magic happens. Pushes all the Converter data on the View.
    @Override
    public void onBindViewHolder(@NonNull ConverterBoxViewHolder holder, int position) {

        if(mConverterArrayList != null) {
            Converter mConverter = mConverterArrayList.get(position);

           //Set the views for the holder.
            holder.mConverterUnitA_Name.setText(mConverter.getUnitAString());
            holder.mConverterUnitB_Name.setText(mConverter.getUnitBString());
//            //holder.mConverterImageView.setImageResource(mConverter.getConverterBoxImageID());
//
//            //Pass the data from converter down to the holder.
//            holder.converterID = mConverter.getConverterID();
            holder.unitAName = mConverter.getUnitAString();
            holder.unitBName = mConverter.getUnitBString();
            holder.unitAValue = mConverter.getUnitAValue();
            holder.unitBValue = mConverter.getUnitBValue();
            holder.unitCategory = mConverter.getUnitCategory();

//            if (position%2 == 0) {
//                holder.mConverterImageView.setImageResource(R.drawable.converter_icon_weight);
//
//            }
//            else {
//                holder.mConverterImageView.setImageResource(R.drawable.converter_icon_distance);
//            }

            if(holder.unitCategory.equals("Distance")){
                holder.mConverterImageView.setImageResource(R.drawable.converter_icon_distance);
            }

            else if(holder.unitCategory.equals("Area")) {
                holder.mConverterImageView.setImageResource(R.drawable.converter_icon_area);
            }

            else if(holder.unitCategory.equals("Time")) {
                holder.mConverterImageView.setImageResource(R.drawable.converter_icon_time);
            }

            else if(holder.unitCategory.equals("Weight")) {
                holder.mConverterImageView.setImageResource(R.drawable.converter_icon_weight);
            }

            else if(holder.unitCategory.equals("Currency")){
                holder.mConverterImageView.setImageResource(R.drawable.converter_icon_currency);
            }
            else if(holder.unitCategory.equals("Temperature")) {
                holder.mConverterImageView.setImageResource(R.drawable.converter_icon_temperature);
            }
            else if(holder.unitCategory.equals("Custom")) {
                holder.mConverterImageView.setImageResource(R.drawable.converter_icon_custom);
            }


        }
        else {
            Log.i("TAG", "ConverterArrayList error");
        }
    }

    void setConverterArrayList(ArrayList<Converter> converterList){
        mConverterArrayList = converterList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        //null check, size can't be null.
        if (mConverterArrayList != null)
            return mConverterArrayList.size();
        else return 0;
    }

    //Constructs the actually View from the Converter object in the ArrayList<Converter>
    public class ConverterBoxViewHolder extends RecyclerView.ViewHolder{

        private final TextView mConverterUnitA_Name;
        private final TextView mConverterUnitB_Name;
        private final ImageView mConverterImageView;
        //private int mConverterBoxImageID;


        //Converter Data stuff
        private int converterID;
        private String unitAName;
        private String unitBName;
        private String unitCategory;
        private double unitAValue;
        private double unitBValue;



        public ConverterBoxViewHolder(View itemView) {
            super(itemView);
            mConverterUnitA_Name = itemView.findViewById(R.id.converter_box_distance_unit_placeholder_1);
            mConverterUnitB_Name = itemView.findViewById(R.id.converter_box_distance_unit_placeholder_2);
            mConverterImageView = itemView.findViewById(R.id.converter_box_image_view);

            //Implements the interface onto the ViewHolder
            mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mClickListener.onConverterIconClick(unitAName, unitBName, unitCategory, unitAValue, unitBValue);
                }
            });

            mItemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    int position = getAdapterPosition();
                    mClickListener.onConverterLongClick(position);
                    return false;
                }
            });

        }

    }

    // parent fragment will implement this method to respond to click events
    public interface ConverterClickListener {
        //Passes all the Converter info to the fragment

        //Pass the category up so that we can switch between custom, currency, and favorites converters.
        void onConverterIconClick(String converterUnitA_Name, String converterUnitB_Name, String converterCategory, double converterUnitA_Value, double converterUnitB_Value);
        void onConverterLongClick(int converterID); //Need to get Converter ID to pass to the delete operation.
    }

    //Enables the Adapter to detect the touched word.
    public Converter getConverterAtPosition (int position) {

        return mConverterArrayList.get(position);
    }
}
