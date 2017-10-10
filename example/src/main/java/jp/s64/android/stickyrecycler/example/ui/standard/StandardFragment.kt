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

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.*
import dagger.android.support.AndroidSupportInjection
import jp.s64.android.stickyrecycler.IStickyCreator
import jp.s64.android.stickyrecycler.StickyRecyclerAdapter
import jp.s64.android.stickyrecycler.StickyVerticalLayoutManager
import jp.s64.android.stickyrecycler.example.R
import jp.s64.android.stickyrecycler.example.databinding.FragmentStandardBinding
import jp.s64.android.stickyrecycler.example.databinding.ItemStandardBinding
import jp.s64.android.stickyrecycler.example.databinding.ItemStandardStickyBinding
import jp.s64.android.stickyrecycler.example.model.Schedule
import org.joda.time.LocalDate
import javax.inject.Inject

class StandardFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(StandardViewModel::class.java) }
    private lateinit var binding: FragmentStandardBinding

    private val adapter = StandardAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return FragmentStandardBinding.inflate(inflater, container, false).also { binding = it }.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycler.layoutManager = StickyVerticalLayoutManager<Schedule, LocalDate, StandardAdapter, AbsStandardViewHolder>(true)
        binding.recycler.adapter = this.adapter
        viewModel.schedules.observe(this, Observer {
            adapter.clear()
            adapter.add(*it!!.toTypedArray())
            adapter.notifyDataSetChanged()
        })
        viewModel.loadSchedules()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.menu_standard, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_issues -> {
                openUrl("https://github.com/S64/android-sticky-recycler/issues")
            }
            R.id.menu_contact -> {
                openUrl("https://twitter.com/shuma_yoshioka")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun openUrl(url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.setData(Uri.parse(url))
        startActivity(i)
    }

}

class StandardAdapter : StickyRecyclerAdapter<Schedule, LocalDate, AbsStandardViewHolder>(StandardStickyCreator(), NORMAL_VIEW_TYPE_ID, STICKY_VIEW_TYPE_ID) {

    companion object {
        const val NORMAL_VIEW_TYPE_ID = 0
        const val STICKY_VIEW_TYPE_ID = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AbsStandardViewHolder? {
        var vh = super.onCreateViewHolder(parent, viewType)
        if (vh == null) {
            vh = StandardViewHolder(
                    LayoutInflater.from(parent?.context).inflate(R.layout.item_standard, parent, false)
            )
        }
        return vh
    }

    override fun onBindNormalViewHolder(org: RecyclerView.ViewHolder?, position: Int) {
        val vh = org as StandardViewHolder
        val item = getItemByPosition(position)
        vh.binding.title.setText(item.title)
        vh.binding.time.setText(item.dateTime.toString("HH:mm"))
    }

}

class StandardStickyCreator : IStickyCreator<Schedule, LocalDate, AbsStandardViewHolder> {

    override fun getIdentifier(item: Schedule): LocalDate {
        return item.dateTime.toLocalDate()
    }

    override fun createStickyViewHolder(parent: ViewGroup?): AbsStandardViewHolder {
        return StandardStickyViewHolder(
                LayoutInflater.from(parent?.context).inflate(R.layout.item_standard_sticky, parent, false)
        )
    }

    override fun bindStickyViewHolder(org: AbsStandardViewHolder, identifier: LocalDate) {
        val vh = org as StandardStickyViewHolder
        vh.binding.date.setText(identifier.toString("MM/dd"))
    }

}

abstract class AbsStandardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class StandardViewHolder(itemView: View) : AbsStandardViewHolder(itemView) {

    val binding = ItemStandardBinding.bind(itemView)

}

class StandardStickyViewHolder(itemView: View) : AbsStandardViewHolder(itemView) {

    val binding = ItemStandardStickyBinding.bind(itemView)

}