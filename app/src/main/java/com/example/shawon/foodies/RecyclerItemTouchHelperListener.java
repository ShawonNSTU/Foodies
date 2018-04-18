package com.example.shawon.foodies;

import android.support.v7.widget.RecyclerView;

/**
 * Created by SHAWON on 4/3/2018.
 */

public interface RecyclerItemTouchHelperListener {

    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);

}
