package com.example.shawon.foodies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by SHAWON on 3/23/2018.
 */

public class ShowCommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


    public TextView mUserName,mUserComment;
    public RatingBar mRatingBar;

    private ItemClickListener itemClickListener;

    public ShowCommentViewHolder(View itemView) {
        super(itemView);

        mUserName = (TextView) itemView.findViewById(R.id.txt_user_name);
        mUserComment = (TextView) itemView.findViewById(R.id.txt_Comment);
        mRatingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);

        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

    }
}
