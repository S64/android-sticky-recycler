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

package jp.s64.android.stickyrecycler.example.ui.standard

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import jp.s64.android.stickyrecycler.example.repository.ScheduleRepository
import javax.inject.Inject

class AppViewModelFactory @Inject constructor(val repository: ScheduleRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>?): T {
        if (modelClass?.let { it.isAssignableFrom(StandardViewModel::class.java) } ?: false) {
            return StandardViewModel(repository) as T
        } else {
            return super.create(modelClass)
        }
    }

}
