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
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.common.base.Objects;

import java.util.LinkedList;
import java.util.List;

public class StickyVerticalLayoutManagerHelper<ITEM, IDENTIFIER, ADAPTER extends RecyclerView.Adapter<VH> & IStickyRecyclerAdapter<ITEM, IDENTIFIER, VH>, VH extends RecyclerView.ViewHolder> {

    protected final RecyclerView.LayoutManager self;
    protected final List<IStickyRecyclerAdapterListener<ITEM, IDENTIFIER>> mListeners = new LinkedList<>();

    @Nullable
    private ADAPTER mCurrentAdapter = null;

    @Nullable
    private RecyclerView mCurrentRecyclerView = null;

    @Nullable
    private View mCurrentStickyView = null;

    private final boolean mAutoDrawingOrder;

    public StickyVerticalLayoutManagerHelper(RecyclerView.LayoutManager self, boolean autoDrawingOrder) {
        this.self = self;
        mAutoDrawingOrder = autoDrawingOrder;
        self.setAutoMeasureEnabled(true);
    }

    public boolean canScrollVertically() {
        return true;
    }

    public boolean canScrollHorizontally() {
        return !canScrollVertically(); // false
    }

    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
        );
    }

    protected int calcParentLeft() {
        return self.getPaddingLeft();
    }

    protected int calcParentRight() {
        return self.getWidth() - self.getPaddingRight();
    }

    protected int calcParentBottom() {
        return self.getHeight() - self.getPaddingBottom();
    }

    protected int calcParentHeight() {
        return self.getHeight();
    }

    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (self.getChildCount() < 1) { // FIXME(shuma): improve this
            self.detachAndScrapAttachedViews(recycler);

            final int firstTop = self.getPaddingTop();
            final int firstPosition = 0;

            int parentLeft, parentRight, parentBottom;
            {
                parentLeft = calcParentLeft();
                parentRight = calcParentRight();
                parentBottom = calcParentBottom();
            }
            int nextTop;
            {
                nextTop = firstTop;
            }

            for (int i = firstPosition; i < state.getItemCount() && nextTop < parentBottom; i++) {
                View v = recycler.getViewForPosition(i);
                self.addView(v, i);

                self.measureChildWithMargins(v, 0, 0);

                int bottom = nextTop + self.getDecoratedMeasuredHeight(v);

                self.layoutDecoratedWithMargins(v, parentLeft, nextTop, parentRight, bottom);

                nextTop = bottom;
            }
        }
    }

    protected boolean setCurrentSticky(@Nullable View newView) {
        if (newView == null || isStickyViewItemType(newView)) {
            mCurrentStickyView = newView;
            return true;
        }
        return false;
    }

    @Nullable
    protected View getCurrentSticky() {
        if (mCurrentStickyView != null && isStickyViewItemType(mCurrentStickyView)) {
            return mCurrentStickyView;
        }
        return null;
    }

    protected boolean isStickyViewItemType(View v) {
        int pos = getAdapterPositionByView(v);
        return pos >= 0 ? mCurrentAdapter.getItemViewType(pos) == mCurrentAdapter.getStickyViewType() : false;
    }

    protected int getAdapterPositionByView(View v) {
        return mCurrentRecyclerView.getChildAdapterPosition(v);
    }

    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {

        final int parentLeft, parentRight, parentHeight;
        {
            parentLeft = calcParentLeft();
            parentRight = calcParentRight();
            parentHeight = calcParentHeight();
        }

        int scrolled = 0;
        if (dy >= 0) {
            while (scrolled < dy) {
                View bottomNormal = getLastNormalChild();
                int hangingBottom = Math.max(self.getDecoratedBottom(bottomNormal) - parentHeight, 0);
                int scrollBy = -Math.min(dy - scrolled, hangingBottom);

                scrolled -= scrollBy;

                self.offsetChildrenVertical(scrollBy);

                int nextPosition = getAdapterPositionByView(bottomNormal) + 1;
                if (nextPosition < state.getItemCount() && scrolled < dy) {
                    View v = recycler.getViewForPosition(nextPosition);

                    self.addView(v);

                    self.measureChildWithMargins(v, 0, 0);

                    int nextTop = self.getDecoratedBottom(bottomNormal);
                    self.layoutDecorated(
                            v,
                            parentLeft,
                            nextTop,
                            parentRight,
                            nextTop + self.getDecoratedMeasuredHeight(v)
                    );
                } else {
                    break;
                }
            }
            {
                View currentSticky = null, nextSticky = null;
                {
                    int maxBottom = Integer.MIN_VALUE;
                    int minTop = Integer.MAX_VALUE;
                    for (int i = 0; i < self.getChildCount(); i++) {
                        View v = self.getChildAt(i);
                        if (isStickyViewItemType(v)) {
                            int bottom = self.getDecoratedBottom(v);
                            int top = self.getDecoratedTop(v);
                            if (maxBottom <= bottom && top <= 0) {
                                maxBottom = bottom;
                                currentSticky = v;
                            }
                            if (minTop > top && currentSticky != v) {
                                minTop = top;
                                nextSticky = v;
                            }
                        }
                    }
                    if (currentSticky != null) {
                        resetCurrentStickyPosition(currentSticky);
                        setCurrentSticky(currentSticky);
                    }
                }
                {
                    int diff = 0;
                    if (currentSticky != null && nextSticky != null) {
                        int bottom = self.getDecoratedBottom(currentSticky);
                        int nextTop = self.getDecoratedTop(nextSticky);
                        diff = nextTop - bottom;
                    }
                    View notifySticky = currentSticky;
                    boolean isLinked = false;
                    if (currentSticky != null && diff < 0) {
                        self.layoutDecorated(
                                currentSticky,
                                parentLeft,
                                0 + diff,
                                parentRight,
                                0 + self.getDecoratedMeasuredHeight(currentSticky) + diff
                        );
                        setCurrentSticky(notifySticky = nextSticky);
                        isLinked = true;
                    }
                    if (notifySticky != null) {
                        notifyStickyChanged(
                                mCurrentAdapter.getIdentifierByPosition(getAdapterPositionByView(notifySticky)),
                                isLinked ? IStickyRecyclerAdapterListener.StickyState.LINKED : IStickyRecyclerAdapterListener.StickyState.ALONE,
                                IStickyRecyclerAdapterListener.ScrollDirection.FORWARD
                        );
                    }
                }
            }
        } else {
            while (scrolled > dy) {
                View topNormal = getFirstNormalChild();
                int hangingTop = Math.max(-self.getDecoratedTop(topNormal), 0);
                int scrollBy = Math.min(scrolled - dy, hangingTop);

                scrolled -= scrollBy;
                self.offsetChildrenVertical(scrollBy);

                {
                    View sticky = getCurrentSticky();
                    int nextPosition = getAdapterPositionByView(topNormal) - 1;
                    boolean isScoped = nextPosition >= 0;
                    if (isScoped && (sticky != null && nextPosition == getAdapterPositionByView(sticky))) {
                        int nextBottom = self.getDecoratedTop(topNormal);
                        self.layoutDecorated(
                                sticky,
                                parentLeft,
                                nextBottom - self.getDecoratedMeasuredHeight(sticky),
                                parentRight,
                                nextBottom
                        );
                        setCurrentSticky(null);
                    } else if (isScoped && scrolled > dy) {
                        View v = recycler.getViewForPosition(nextPosition);

                        self.addView(v, 0);

                        self.measureChildWithMargins(v, 0, 0);

                        int nextBottom = self.getDecoratedTop(topNormal);
                        int nextTop = nextBottom - self.getDecoratedMeasuredHeight(v);
                        self.layoutDecorated(
                                v,
                                parentLeft,
                                nextTop,
                                parentRight,
                                nextBottom
                        );
                    } else {
                        break;
                    }
                }
            }
            {
                View firstNormal = null, firstSticky = null;
                {
                    int minNormalTop = Integer.MAX_VALUE;
                    for (int i = 0; i < self.getChildCount(); i++) {
                        View v = self.getChildAt(i);
                        if (getCurrentSticky() == null || getCurrentSticky() != v) {
                            int top = self.getDecoratedTop(v);
                            if (minNormalTop > top) {
                                minNormalTop = top;
                                firstNormal = v;
                            }
                        }
                    }
                    int minStickyTop = Integer.MAX_VALUE;
                    for (int i = 0; i < self.getChildCount(); i++) {
                        View v = self.getChildAt(i);
                        if (isStickyViewItemType(v)) {
                            int top = self.getDecoratedTop(v);
                            if (minStickyTop > top) {
                                minStickyTop = top;
                                firstSticky = v;
                            }
                        }
                    }
                }
                {
                    int firstPosition = getAdapterPositionByView(firstNormal);
                    if (mCurrentAdapter.getItemViewType(firstPosition) != mCurrentAdapter.getStickyViewType()) {
                        IDENTIFIER requiredIdentifier = mCurrentAdapter.getIdentifierByPosition(firstPosition);

                        boolean resolved = false;
                        for (int i = 0; i < self.getChildCount(); i++) {
                            View v = self.getChildAt(i);
                            if (isStickyViewItemType(v) && mCurrentAdapter.getIdentifierByPosition(getAdapterPositionByView(v)).equals(requiredIdentifier)) {
                                resolved = true;
                                break;
                            }
                        }
                        if (!resolved && self.getDecoratedTop(firstSticky) >= 0) {
                            int i = mCurrentAdapter.findStickyPositionByIdentifier(requiredIdentifier);
                            View v = recycler.getViewForPosition(i);
                            self.addView(v, 0);
                            self.measureChildWithMargins(v, 0, 0);
                            int bottom = self.getDecoratedTop(firstNormal);
                            self.layoutDecorated(
                                    v,
                                    parentLeft,
                                    bottom - self.getDecoratedMeasuredHeight(v),
                                    parentRight,
                                    bottom
                            );
                        }
                    }
                }
            }
            {
                View currentSticky = null, previousSticky = null;
                {
                    for (int pass = 1; pass <= 2; pass++) {
                        for (int i = (self.getChildCount() - 1); i >= 0; i--) {
                            View v = self.getChildAt(i);
                            if (isStickyViewItemType(v)) {
                                int top = self.getDecoratedTop(v);
                                int bottom = self.getDecoratedBottom(v);
                                if (pass == 1) {
                                    if (top >= 0 && (previousSticky == null || self.getDecoratedTop(previousSticky) > top)) {
                                        previousSticky = v;
                                    }
                                } else if (pass == 2) {
                                    if ((previousSticky == null && (currentSticky == null || self.getDecoratedBottom(currentSticky) < bottom)) || (previousSticky != null && self.getDecoratedTop(previousSticky) >= bottom && (currentSticky == null || self.getDecoratedBottom(currentSticky) < bottom))) {
                                        currentSticky = v;
                                    }
                                }
                            }
                        }
                    }
                    if (currentSticky == null && previousSticky != null) {
                        currentSticky = previousSticky;
                        previousSticky = null;
                    }
                }
                {
                    if (currentSticky != null) {
                        resetCurrentStickyPosition(currentSticky);
                        setCurrentSticky(currentSticky);
                    }
                    int diff = 0;
                    if (currentSticky != null && previousSticky != null) {
                        diff = self.getDecoratedTop(previousSticky) - self.getDecoratedBottom(currentSticky);
                    }
                    boolean isLinked = false;
                    if (diff < 0) {
                        int top = 0 + diff;
                        self.layoutDecorated(
                                currentSticky,
                                parentLeft,
                                top,
                                parentRight,
                                top + self.getDecoratedMeasuredHeight(currentSticky)
                        );
                        isLinked = true;
                    }
                    if (currentSticky != null) {
                        notifyStickyChanged(
                                mCurrentAdapter.getIdentifierByPosition(getAdapterPositionByView(currentSticky)),
                                isLinked ? IStickyRecyclerAdapterListener.StickyState.LINKED : IStickyRecyclerAdapterListener.StickyState.ALONE,
                                IStickyRecyclerAdapterListener.ScrollDirection.BACKWARD
                        );
                    }
                }
            }
        }
        {
            final List<View> toRecycleViews = new LinkedList<>();
            for (int i = 0; i < self.getChildCount(); i++) {
                View v = self.getChildAt(i);
                final int orgTop, orgBottom;
                {
                    orgTop = self.getDecoratedTop(v);
                    orgBottom = self.getDecoratedBottom(v);
                }
                final boolean isHidedTop, isHidedBottom;
                {
                    isHidedTop = orgBottom < 0;
                    isHidedBottom = orgTop > parentHeight;
                }
                if (isHidedTop || isHidedBottom) {
                    toRecycleViews.add(v);
                }
            }
            for (View v : toRecycleViews) {
                self.removeAndRecycleView(v, recycler);
            }
        }

        return scrolled;
    }

    protected void resetCurrentStickyPosition(View currentSticky) {
        self.layoutDecorated(
                currentSticky,
                calcParentLeft(),
                0,
                calcParentRight(),
                0 + self.getDecoratedMeasuredHeight(currentSticky)
        );
    }

    public void scrollToPosition(int position) {
        throw new UnsupportedOperationException();
    }

    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        throw new UnsupportedOperationException();
    }

    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        mCurrentAdapter = (ADAPTER) newAdapter;
    }

    public void onAttachedToWindow(RecyclerView view) {
        mCurrentRecyclerView = view;
        if (mAutoDrawingOrder) {
            mCurrentRecyclerView.setChildDrawingOrderCallback(new RecyclerView.ChildDrawingOrderCallback() {

                @Override
                public int onGetChildDrawingOrder(int childCount, int i) {
                    Integer previousStickyChildPosition, currentStickyChildPosition;
                    {
                        previousStickyChildPosition = mPreviousStickyIdentifier != null ? getChildPositionByAdapterPosition(mCurrentAdapter.findStickyPositionByIdentifier(mPreviousStickyIdentifier)) : null;
                        currentStickyChildPosition = mLastStickyIdentifier != null ? getChildPositionByAdapterPosition(mCurrentAdapter.findStickyPositionByIdentifier(mLastStickyIdentifier)) : null;
                    }

                    int ret;

                    if (previousStickyChildPosition != null && i == (childCount - 2)) {
                        ret = previousStickyChildPosition;
                    } else if (currentStickyChildPosition != null && i == (childCount - 1)) {
                        ret = currentStickyChildPosition;
                    } else {
                        int firstSticky = Math.min(
                                previousStickyChildPosition != null ? previousStickyChildPosition : Integer.MAX_VALUE,
                                currentStickyChildPosition != null ? currentStickyChildPosition : Integer.MAX_VALUE
                        );
                        int lastSticky;
                        {
                            if (previousStickyChildPosition != null && currentStickyChildPosition != null) {
                                lastSticky = Math.max(
                                        previousStickyChildPosition != null ? previousStickyChildPosition : childCount + 2,
                                        currentStickyChildPosition != null ? currentStickyChildPosition : childCount + 2
                                );
                            } else {
                                lastSticky = previousStickyChildPosition != null && previousStickyChildPosition != firstSticky ? previousStickyChildPosition : currentStickyChildPosition != null && currentStickyChildPosition != firstSticky ? currentStickyChildPosition : Integer.MAX_VALUE;
                            }
                        }

                        int increment;
                        if (i > (lastSticky - 2)) {
                            increment = 2;
                        } else if (i > (firstSticky - 1)) {
                            increment = 1;
                        } else {
                            increment = 0;
                        }
                        ret = i + increment;
                    }

                    return ret;
                }

            });
        }
    }

    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        mCurrentRecyclerView = null;
    }

    protected View getFirstNormalChild() {
        View resolved = null;
        int lastMinTop = Integer.MAX_VALUE;
        for (int i = 0; i < self.getChildCount(); i++) {
            View v = self.getChildAt(i);
            if (getCurrentSticky() == null || v != getCurrentSticky()) {
                int top;
                if ((top = self.getDecoratedTop(v)) < lastMinTop) {
                    lastMinTop = top;
                    resolved = v;
                }
            }
        }
        return resolved;
    }

    protected View getLastNormalChild() {
        View resolved = null;
        int lastMaxBottom = Integer.MIN_VALUE;
        for (int i = (self.getChildCount() - 1); i >= 0; i--) {
            View v = self.getChildAt(i);
            RecyclerView.ViewHolder vh = mCurrentRecyclerView.findContainingViewHolder(v);
            if (getCurrentSticky() == null || vh.getAdapterPosition() != mCurrentRecyclerView.getChildAdapterPosition(getCurrentSticky())) {
                int bottom;
                if ((bottom = self.getDecoratedBottom(v)) > lastMaxBottom) {
                    lastMaxBottom = bottom;
                    resolved = v;
                }
            }
        }
        return resolved;
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

    @Nullable
    public View getViewByAdapterPosition(int position) {
        return self.getChildAt(getChildPositionByAdapterPosition(position));
    }

    public void addStickyRecyclerAdapterListener(IStickyRecyclerAdapterListener<ITEM, IDENTIFIER> listener) {
        mListeners.add(listener);
    }

    public void removeStickyRecyclerAdapterListener(IStickyRecyclerAdapterListener<ITEM, IDENTIFIER> listener) {
        mListeners.remove(listener);
    }

    private IDENTIFIER mPreviousStickyIdentifier = null;
    private IDENTIFIER mLastStickyIdentifier = null;
    private IStickyRecyclerAdapterListener.StickyState mLastStickyState = null;

    protected void notifyStickyChanged(IDENTIFIER identifier, IStickyRecyclerAdapterListener.StickyState state, IStickyRecyclerAdapterListener.ScrollDirection direction) {
        boolean changed = false;

        if (!Objects.equal(mLastStickyIdentifier, identifier)) {
            mPreviousStickyIdentifier = mLastStickyIdentifier;
            mLastStickyIdentifier = identifier;
            changed = true;
        }

        if (!Objects.equal(mLastStickyState, state)) {
            mLastStickyState = state;
            changed = true;
        }

        if (changed) {
            for (IStickyRecyclerAdapterListener<ITEM, IDENTIFIER> listener : mListeners) {
                listener.onStickyChanged(mPreviousStickyIdentifier, mLastStickyIdentifier, mLastStickyState, direction);
            }
        }

    }

}
