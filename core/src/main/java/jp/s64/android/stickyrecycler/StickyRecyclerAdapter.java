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

import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public abstract class StickyRecyclerAdapter<ITEM, IDENTIFIER, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements IStickyRecyclerAdapter<ITEM, IDENTIFIER, VH> {

    private final StickyRecyclerAdapterHelper<ITEM, IDENTIFIER, VH> mHelper;

    public StickyRecyclerAdapter(IStickyCreator<ITEM, IDENTIFIER, VH> stickyCreator) {
        this(stickyCreator, null, null);
    }

    public StickyRecyclerAdapter(IStickyCreator<ITEM, IDENTIFIER, VH> stickyCreator, @Nullable Integer stickyViewType, @Nullable Integer defaultItemType) {
        mHelper = new StickyRecyclerAdapterHelper<>(stickyCreator, stickyViewType, defaultItemType);
    }

    @Override
    public int getItemViewType(int position) {
        return mHelper.getItemViewType(position);
    }

    @CallSuper
    @Nullable
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return mHelper.onCreateViewHolder(parent, viewType);
    }

    @Override
    public final void onBindViewHolder(VH holder, int position) {
        if (!mHelper.onBindViewHolder(holder, position)) {
            onBindNormalViewHolder(holder, position);
        }
    }

    public abstract void onBindNormalViewHolder(RecyclerView.ViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return mHelper.getItemCount();
    }

    @Override
    public void add(ITEM... items) {
        mHelper.add(items);
    }

    @Override
    public void remove(ITEM item) {
        mHelper.remove(item);
    }

    @Override
    public void clear() {
        mHelper.clear();
    }

    @Override
    public ITEM getItemByPosition(int position) {
        return mHelper.getItemByPosition(position);
    }

    @Override
    public IDENTIFIER getIdentifierByPosition(int position) {
        return mHelper.getIdentifierByPosition(position);
    }

    @Override
    public IStickyCreator getStickyCreator() {
        return mHelper.getStickyCreator();
    }

    @Override
    public int getDefaultViewType() {
        return mHelper.getDefaultViewType();
    }

    @Override
    public int getStickyViewType() {
        return mHelper.getStickyViewType();
    }

    @Override
    public int findStickyPositionByIdentifier(IDENTIFIER identifier) {
        return mHelper.findStickyPositionByIdentifier(identifier);
    }

}
