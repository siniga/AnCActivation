package com.aggreyclifford.active.ancactivation.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aggreyclifford.active.ancactivation.R;
import com.aggreyclifford.active.ancactivation.activities.OutletActivationActivity;
import com.aggreyclifford.active.ancactivation.models.Member;

import java.util.Collections;
import java.util.List;

/**
 * Created by alicephares on 8/5/16.
 */
public class BrandListAdapter extends RecyclerView.Adapter<BrandListAdapter.ViewHolder> {

    List<Member> members = Collections.emptyList();
    private LayoutInflater inflator;
    Context c;

    // Provide a suitable constructor (depends on the kind of dataset)
    public BrandListAdapter(Context c, List<Member> members) {
        this.members = members;
        this.inflator = LayoutInflater.from(c);
        this.c = c;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View v = inflator.inflate(R.layout.card_brands, parent, false);
        // set the view's size, margins, padding and layout parameters

        ViewHolder vh = new ViewHolder(c, v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //get a position of a current saleItem
        final Member currentWork = members.get(position);


        /*holder.mImg.setBackgroundResource(currentWork.getImg());*/
        holder.mName.setText(currentWork.getName());

        holder.brandContentWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(c, OutletActivationActivity.class);
                c.startActivity(intent);


            }
        });
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mName, mEmail, mType;
        public ImageView mImg;
        public LinearLayout  brandContentWrapper;
        public ViewHolder(Context context, View view) {
            super(view);

            brandContentWrapper = (LinearLayout) view.findViewById(R.id.brand_content_wrapper);
            //mTypeWrapper = (LinearLayout) view.findViewById(R.id.type_wrapper);*/
           /* mImg = (ImageView) view.findViewById(R.id.img);*/
            mName = (TextView) view.findViewById(R.id.name);
           // mType = (TextView) view.findViewById(R.id.type);


        }

    }



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return members.size();
    }
}