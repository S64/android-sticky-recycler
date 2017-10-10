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

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection

class StandardActivity : AppCompatActivity() {

    companion object {
        private val TAG_FRAGMENT = "jp.s64.android.stickyrecycler.example.ui.standard.StandardActivity.TAG_FRAGMENT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        if (supportFragmentManager.findFragmentByTag(TAG_FRAGMENT) == null) {
            supportFragmentManager.beginTransaction().replace(android.R.id.content, StandardFragment()).commit()
        }
    }

}