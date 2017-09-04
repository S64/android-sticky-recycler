/*
 * Copyright (C) 2017 Shuma Yoshioka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.s64.android.stickyrecycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class StaticLinearLayoutManager<ITEM, IDENTIFIER, ADAPTER extends RecyclerView.Adapter<VH> & IStickyRecyclerAdapter<ITEM, IDENTIFIER, VH>, VH extends RecyclerView.ViewHolder> extends LinearLayoutManager {

    private final StaticLinearLayoutManagerHelper<ITEM, IDENTIFIER, ADAPTER, VH> mHelper = new StaticLinearLayoutManagerHelper<>(this);

    public StaticLinearLayoutManager(Context context) {
        super(context);
    }

    public StaticLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public StaticLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int ret = super.scrollVerticallyBy(dy, recycler, state);
        {
            mHelper.scrollVerticallyBy(ret);
        }
        return ret;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int ret = super.scrollHorizontallyBy(dx, recycler, state);
        {
            mHelper.scrollHorizontallyBy(ret);
        }
        return ret;
    }

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        super.onAdapterChanged(oldAdapter, newAdapter);
        mHelper.onAdapterChanged(oldAdapter, newAdapter);
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        mHelper.onAttachedToWindow(view);
    }

    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        mHelper.onDetachedFromWindow(view, recycler);
    }

    public void addStickyRecyclerAdapterListener(IStickyRecyclerAdapterListener<ITEM, IDENTIFIER> listener) {
        mHelper.addStickyRecyclerAdapterListener(listener);
    }

    public void removeStickyRecyclerAdapterListener(IStickyRecyclerAdapterListener<ITEM, IDENTIFIER> listener) {
        mHelper.removeStickyRecyclerAdapterListener(listener);
    }

    @Nullable
    public View getViewByAdapterPosition(int position) {
        return mHelper.getViewByAdapterPosition(position);
    }

}
