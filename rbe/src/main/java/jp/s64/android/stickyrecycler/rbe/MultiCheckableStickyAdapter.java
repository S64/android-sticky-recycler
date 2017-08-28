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

package jp.s64.android.stickyrecycler.rbe;

import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;

import com.google.common.collect.ImmutableSet;

import jp.s64.android.radiobuttonextended.recycler.adapter.MultiCheckableAdapter;
import jp.s64.android.stickyrecycler.IStickyCreator;
import jp.s64.android.stickyrecycler.IStickyRecyclerAdapter;
import jp.s64.android.stickyrecycler.StickyRecyclerAdapterHelper;

public abstract class MultiCheckableStickyAdapter<ITEM, IDENTIFIER, K, VH extends RecyclerView.ViewHolder & MultiCheckableAdapter.IMultiCheckableViewHolder<VH, K>> extends RecyclerView.Adapter<VH> implements IStickyRecyclerAdapter<ITEM, IDENTIFIER, VH> {

    private final StickyRecyclerAdapterHelper<ITEM, IDENTIFIER, VH> mStickyHelper;
    private final MultiCheckableAdapter.Helper<VH, K> mCheckableHelper;

    public MultiCheckableStickyAdapter(Class<? extends VH> viewHolderClass, IStickyCreator<ITEM, IDENTIFIER, VH> stickyCreator, MultiCheckableAdapter.IOnMultipleCheckedChangeListener<VH, K> listener, MultiCheckableAdapter.IPayloadGenerator<VH, K> generator) {
        this(viewHolderClass, stickyCreator, null, null, listener, generator);
    }

    public MultiCheckableStickyAdapter(Class<? extends VH> viewHolderClass, IStickyCreator<ITEM, IDENTIFIER, VH> stickyCreator, @Nullable Integer stickyViewType, @Nullable Integer defaultItemType, MultiCheckableAdapter.IOnMultipleCheckedChangeListener<VH, K> listener, MultiCheckableAdapter.IPayloadGenerator<VH, K> generator) {
        mStickyHelper = new StickyRecyclerAdapterHelper<>(stickyCreator, stickyViewType, defaultItemType);
        mCheckableHelper = new MultiCheckableAdapter.Helper<>(viewHolderClass, listener, generator);
    }

    @CallSuper
    @Nullable
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        VH ret = mStickyHelper.onCreateViewHolder(parent, viewType);
        {
            mCheckableHelper.onCreateViewHolder(parent, viewType);
        }
        return ret;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        if (!mStickyHelper.onBindViewHolder(holder, position)) {
            onBindNormalViewHolder(holder, position);
        }
        {
            mCheckableHelper.onBindViewHolder(holder, position);
        }
    }

    public abstract void onBindNormalViewHolder(VH holder, int position);

    @CallSuper
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mCheckableHelper.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return mStickyHelper.getItemCount();
    }

    @Override
    public void add(ITEM... items) {
        mStickyHelper.add(items);
    }

    @Override
    public void remove(ITEM item) {
        mStickyHelper.remove(item);
    }

    @Override
    public void clear() {
        mStickyHelper.clear();
    }

    @Override
    public ITEM getItemByPosition(int position) {
        return mStickyHelper.getItemByPosition(position);
    }

    @Override
    public IDENTIFIER getIdentifierByPosition(int position) {
        return mStickyHelper.getIdentifierByPosition(position);
    }

    @Override
    public IStickyCreator<ITEM, IDENTIFIER, VH> getStickyCreator() {
        return mStickyHelper.getStickyCreator();
    }

    @Override
    public int getDefaultViewType() {
        return mStickyHelper.getDefaultViewType();
    }

    @Override
    public int getStickyViewType() {
        return mStickyHelper.getStickyViewType();
    }

    @Override
    public int findStickyPositionByIdentifier(IDENTIFIER identifier) {
        return mStickyHelper.findStickyPositionByIdentifier(identifier);
    }

    public <V extends View & Checkable> boolean updateCheckedIds(K key, boolean isChecked) {
        return mCheckableHelper.updateCheckedIds(key, isChecked).first;
    }

    public ImmutableSet<K> getCheckedIds() {
        return mCheckableHelper.getCheckedIds();
    }

    public MultiCheckableAdapter.IPayloadGenerator<VH, K> getPayloadGenerator() {
        return mCheckableHelper.getPayloadGenerator();
    }

    @Override
    public int getItemViewType(int position) {
        return mStickyHelper.getItemViewType(position);
    }

}
