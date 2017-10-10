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

package jp.s64.android.stickyrecycler.example

import android.arch.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import jp.s64.android.stickyrecycler.example.api.ISchedulesApi
import jp.s64.android.stickyrecycler.example.api.impl.SchedulesApiImpl
import jp.s64.android.stickyrecycler.example.repository.ScheduleRepository
import jp.s64.android.stickyrecycler.example.ui.standard.AppViewModelFactory

@Module
class AppModule {

    @Provides
    fun api(): ISchedulesApi {
        return SchedulesApiImpl()
    }

    @Provides
    fun repository(api: ISchedulesApi): ScheduleRepository {
        return ScheduleRepository(api)
    }

    @Provides
    fun viewModelFactory(viewModelFactory: AppViewModelFactory): ViewModelProvider.Factory {
        return viewModelFactory
    }

}
