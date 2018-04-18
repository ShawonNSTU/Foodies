package com.example.shawon.foodies;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

/**
 * Created by SHAWON on 4/3/2018.
 */

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private RecyclerItemTouchHelperListener recyclerItemTouchHelperListener;

    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener recyclerItemTouchHelperListener) {
        super(dragDirs, swipeDirs);
        this.recyclerItemTouchHelperListener = recyclerItemTouchHelperListener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        if (recyclerItemTouchHelperListener != null){
            recyclerItemTouchHelperListener.onSwiped(viewHolder,direction,viewHolder.getAdapterPosition());
        }

    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        View forgroundView = ((CartViewHolder) viewHolder).mViewForground;
        getDefaultUIUtil().clearView(forgroundView);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View forgroundView = ((CartViewHolder) viewHolder).mViewForground;
        getDefaultUIUtil().onDraw(c,recyclerView,forgroundView,dX,dY,actionState,isCurrentlyActive);
    }

    /*@Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null){
            View forgroundView = ((CartViewHolder) viewHolder).mViewForground;
            getDefaultUIUtil().onSelected(forgroundView);
        }
    }*/

   /* @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View forgroundView = ((CartViewHolder) viewHolder).mViewForground;
        getDefaultUIUtil().onDrawOver(c,recyclerView,forgroundView,dX,dY,actionState,isCurrentlyActive);
    }*/
}