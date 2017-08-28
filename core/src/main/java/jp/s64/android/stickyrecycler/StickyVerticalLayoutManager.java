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

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class StickyVerticalLayoutManager<ITEM, IDENTIFIER, ADAPTER extends RecyclerView.Adapter<VH> & IStickyRecyclerAdapter<ITEM, IDENTIFIER, VH>, VH extends RecyclerView.ViewHolder> extends RecyclerView.LayoutManager {

    protected final StickyVerticalLayoutManagerHelper<ITEM, IDENTIFIER, ADAPTER, VH> mHelper;

    public StickyVerticalLayoutManager(boolean autoDrawingOrder, boolean enableSticky) {
        super();
        mHelper = new StickyVerticalLayoutManagerHelper<>(this, autoDrawingOrder, enableSticky);
    }

    @Override
    public boolean canScrollVertically() {
        return mHelper.canScrollVertically();
    }

    @Override
    public boolean canScrollHorizontally() {
        return mHelper.canScrollHorizontally();
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return mHelper.generateDefaultLayoutParams();
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        mHelper.onLayoutChildren(recycler, state);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return mHelper.scrollVerticallyBy(dy, recycler, state);
    }

    @Override
    public void scrollToPosition(int position) {
        mHelper.scrollToPosition(position);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        mHelper.smoothScrollToPosition(recyclerView, state, position);
    }

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
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

    public View getViewByAdapterPosition(int position) {
        return mHelper.getViewByAdapterPosition(position);
    }

    public void addStickyRecyclerAdapterListener(IStickyRecyclerAdapterListener<ITEM, IDENTIFIER> listener) {
        mHelper.addStickyRecyclerAdapterListener(listener);
    }

    public void removeStickyRecyclerAdapterListener(IStickyRecyclerAdapterListener<ITEM, IDENTIFIER> listener) {
        mHelper.removeStickyRecyclerAdapterListener(listener);
    }

}
