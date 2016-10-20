package com.aggreyclifford.active.ancactivation.adapters.kyc_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aggreyclifford.active.ancactivation.R;

public class GenderAdapter extends ArrayAdapter<String> {
    String [] _stringValues;
    Context _ctx;

        public GenderAdapter(Context ctx, int txtViewResourceId, String[] objects) {
            super(ctx, txtViewResourceId, objects);

            this._ctx = ctx;
            this._stringValues= objects;
        }

        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
            return getCustomView(position, cnvtView, prnt);
        }

        @Override
        public View getView(int pos, View cnvtView, ViewGroup prnt) {
            return getCustomView(pos, cnvtView, prnt);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) _ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View mSpinner = inflater.inflate(R.layout.custom_spinner, parent, false);

            getAgeView(mSpinner, position);
            return mSpinner;
        }


        private void getAgeView(View mSpinner, int position) {
            TextView ageSpinner = (TextView) mSpinner.findViewById(R.id.sub_text_seen);
            ageSpinner.setText(_stringValues[position]);
        }


    }