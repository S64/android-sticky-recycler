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

import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.common.base.Objects;

import java.util.LinkedList;
import java.util.List;

public class StaticLinearLayoutManagerHelper<ITEM, IDENTIFIER, ADAPTER extends RecyclerView.Adapter<VH> & IStickyRecyclerAdapter<ITEM, IDENTIFIER, VH>, VH extends RecyclerView.ViewHolder> {

    private final LinearLayoutManager self;
    protected final List<IStickyRecyclerAdapterListener<ITEM, IDENTIFIER>> mListeners = new LinkedList<>();

    @Nullable
    private ADAPTER mCurrentAdapter = null;

    @Nullable
    private RecyclerView mCurrentRecyclerView = null;

    public StaticLinearLayoutManagerHelper(LinearLayoutManager self) {
        this.self = self;
    }

    public void scrollVerticallyBy(int ret) {
        onScroll(ret);
    }

    public void scrollHorizontallyBy(int ret) {
        onScroll(ret);
    }

    protected void onScroll(int scrolled) {
        int adapterPosition;
        if (scrolled == 0 || (adapterPosition = self.findFirstVisibleItemPosition()) == RecyclerView.NO_POSITION) {
            return;
        }
        Integer currentSticky = null;

        for (int itr = adapterPosition; itr >= 0; itr--) {
            if (mCurrentAdapter.getItemViewType(itr) == mCurrentAdapter.getStickyViewType()) {
                if (currentSticky == null) {
                    currentSticky = itr;
                    break;
                }
            }
        }

        if (currentSticky != null) {
            notifyStickyChanged(
                    mCurrentAdapter.getIdentifierByPosition(currentSticky),
                    scrolled > 0 ? IStickyRecyclerAdapterListener.ScrollDirection.FORWARD : IStickyRecyclerAdapterListener.ScrollDirection.BACKWARD
            );
        }
    }

    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        mCurrentAdapter = (ADAPTER) newAdapter;
    }

    public void onAttachedToWindow(RecyclerView view) {
        mCurrentRecyclerView = view;
    }

    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        mCurrentRecyclerView = view;
    }

    public void addStickyRecyclerAdapterListener(IStickyRecyclerAdapterListener<ITEM, IDENTIFIER> listener) {
        mListeners.add(listener);
    }

    public void removeStickyRecyclerAdapterListener(IStickyRecyclerAdapterListener<ITEM, IDENTIFIER> listener) {
        mListeners.remove(listener);
    }

    private IDENTIFIER mPreviousStickyIdentifier = null;
    private IDENTIFIER mLastStickyIdentifier = null;

    protected void notifyStickyChanged(IDENTIFIER identifier, IStickyRecyclerAdapterListener.ScrollDirection direction) {
        boolean changed = false;

        if (!Objects.equal(mLastStickyIdentifier, identifier)) {
            mPreviousStickyIdentifier = mLastStickyIdentifier;
            mLastStickyIdentifier = identifier;
            changed = true;
        }

        if (changed) {
            for (IStickyRecyclerAdapterListener<ITEM, IDENTIFIER> listener : mListeners) {
                listener.onStickyChanged(mPreviousStickyIdentifier, mLastStickyIdentifier, null, direction);
            }
        }

    }

    @Nullable
    public View getViewByAdapterPosition(int position) {
        Integer childAt;
        if ((childAt = getChildPositionByAdapterPosition(position)) != null) {
            return self.getChildAt(childAt);
        }
        return null;
    }

    @Nullable
    protected Integer getChildPositionByAdapterPosition(int adapterPosition) {
        for (int i = 0; i < self.getChildCount(); i++) {
            View v = self.getChildAt(i);
            if (getAdapterPositionByView(v) == adapterPosition) {
                return i;
            }
        }
        return null;
    }

    protected int getAdapterPositionByView(View v) {
        return mCurrentRecyclerView.getChildAdapterPosition(v);
    }

}
