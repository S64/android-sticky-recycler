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

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public interface IStickyCreator<ITEM, IDENTIFIER, VH extends RecyclerView.ViewHolder> {

    @NonNull
    VH createStickyViewHolder(ViewGroup parent);

    @NonNull
    IDENTIFIER getIdentifier(@NonNull ITEM item);

    void bindStickyViewHolder(@NonNull VH viewHolder, @NonNull IDENTIFIER identifier);

}
