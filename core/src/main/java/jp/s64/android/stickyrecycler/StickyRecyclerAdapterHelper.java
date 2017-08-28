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
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class StickyRecyclerAdapterHelper<ITEM, IDENTIFIER, VH extends RecyclerView.ViewHolder> implements IStickyRecyclerAdapter<ITEM, IDENTIFIER, VH> {

    public static final int DEFAULT_STICKY_ITEM_TYPE = 1220887943;
    public static final int DEFAULT_ITEM_TYPE = 0;

    private final int mStickyViewType;
    private final int mDefaultItemType;

    private final IStickyCreator<ITEM, IDENTIFIER, VH> mStickyCreator;

    private final List<ITEM> mRawItems = new ArrayList<>();

    private final Set<IDENTIFIER> mStickyCaches = new HashSet<>();

    public StickyRecyclerAdapterHelper(IStickyCreator<ITEM, IDENTIFIER, VH> stickyCreator) {
        this(stickyCreator, null, null);
    }

    public StickyRecyclerAdapterHelper(IStickyCreator<ITEM, IDENTIFIER, VH> stickyCreator, @Nullable Integer stickyViewType, @Nullable Integer defaultItemType) {
        super();
        mStickyCreator = stickyCreator;
        mStickyViewType = stickyViewType != null ? stickyViewType : DEFAULT_STICKY_ITEM_TYPE;
        mDefaultItemType = defaultItemType != null ? defaultItemType : DEFAULT_ITEM_TYPE;
    }

    @Nullable
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == mStickyViewType) {
            return mStickyCreator.createStickyViewHolder(parent);
        }
        return null;
    }

    public boolean onBindViewHolder(VH holder, int position) {
        if (getItemViewType(position) == mStickyViewType) {
            mStickyCreator.bindStickyViewHolder(holder, get(position).identifier);
            return true;
        }
        return false;
    }

    public int getItemCount() {
        return mRawItems.size() + mStickyCaches.size();
    }

    public int getItemViewType(int position) {
        return get(position).isIdentifier() ? mStickyViewType : mDefaultItemType;
    }

    protected Item get(int position) {
        IDENTIFIER lastId = null;

        int skipped = 0;
        int i = -1;
        do {
            i++;

            ITEM item = mRawItems.get(i - skipped);
            IDENTIFIER currentId = mStickyCreator.getIdentifier(item);

            if (lastId == null || !lastId.equals(currentId)) {
                lastId = currentId;
                skipped++;
                if (i == position) {
                    return new Item(null, currentId);
                }
            }

            if (i == position) {
                return new Item(item, null);
            }
        } while (true);
    }

    @Override
    public void add(ITEM... items) {
        for (ITEM i : items) {
            mRawItems.add(i);
        }
        onItemsChanged();
    }

    @Override
    public void remove(ITEM item) {
        mRawItems.remove(item);
        onItemsChanged();
    }

    @Override
    public void clear() {
        mRawItems.clear();
        onItemsChanged();
    }

    @Override
    public ITEM getItemByPosition(int position) {
        return get(position).item;
    }

    @Override
    public IDENTIFIER getIdentifierByPosition(int position) {
        Item obj = get(position);
        return obj.isItem() ? mStickyCreator.getIdentifier(obj.item) : obj.identifier;
    }

    @Override
    public IStickyCreator<ITEM, IDENTIFIER, VH> getStickyCreator() {
        return mStickyCreator;
    }

    @Override
    public int getDefaultViewType() {
        return mDefaultItemType;
    }

    @Override
    public int getStickyViewType() {
        return mStickyViewType;
    }

    @Override
    public int findStickyPositionByIdentifier(IDENTIFIER identifier) {
        for (int i = 0; i < this.getItemCount(); i++) {
            Item itm = get(i);
            if (itm.isIdentifier() && itm.identifier.equals(identifier)) {
                return i;
            }
        }
        return RecyclerView.NO_POSITION;
    }

    protected void onItemsChanged() {
        List<IDENTIFIER> resolvedIds = new LinkedList<>();
        {
            for (ITEM i : mRawItems) {
                IDENTIFIER id = mStickyCreator.getIdentifier(i);
                if (!mStickyCaches.contains(id)) {
                    mStickyCaches.add(id);
                }
                resolvedIds.add(id);
            }
        }
        {
            for (IDENTIFIER cachedId : mStickyCaches) {
                if (!resolvedIds.contains(cachedId)) {
                    mStickyCaches.remove(cachedId);
                }
            }
        }
    }

    protected class Item {

        public final ITEM item;
        public final IDENTIFIER identifier;

        public Item(@Nullable ITEM item, @Nullable IDENTIFIER identifier) {
            this.item = item;
            this.identifier = identifier;
        }

        public boolean isItem() {
            return item != null;
        }

        public boolean isIdentifier() {
            return !isItem();
        }

    }

}
