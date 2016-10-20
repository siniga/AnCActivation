package com.aggreyclifford.active.ancactivation.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aggreyclifford.active.ancactivation.R;
import com.aggreyclifford.active.ancactivation.models.Member;


import java.util.Collections;
import java.util.List;

/**
 * Created by alicephares on 8/5/16.
 */
public class TeamMembersListAdapter extends RecyclerView.Adapter<TeamMembersListAdapter.ViewHolder> {

    List<Member> members = Collections.emptyList();
    private LayoutInflater inflator;
    Context c;

    // Provide a suitable constructor (depends on the kind of dataset)
    public TeamMembersListAdapter(Context c, List<Member> members) {
        this.members = members;
        this.inflator = LayoutInflater.from(c);
        this.c = c;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View v = inflator.inflate(R.layout.card_team_members, parent, false);
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
        holder.mEffectiveContact.setText(currentWork.getEffectiveReach());
        holder.mEffectiveReach.setText(currentWork.getEffectiveReach());
        holder.mSourceBusiness.setText(currentWork.getSourceBusiness());
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mName, mEffectiveContact, mEffectiveReach, mSourceBusiness;
        public ImageView mImg;

        public ViewHolder(Context context, View view) {
            super(view);

            mName = (TextView) view.findViewById(R.id.name);
            mEffectiveContact = (TextView) view.findViewById(R.id.effective_contact);
            mEffectiveReach = (TextView) view.findViewById(R.id.effective_reach);
            mSourceBusiness = (TextView) view.findViewById(R.id.source_bussinees);



        }

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return members.size();
    }
}