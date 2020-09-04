package it.laface.stats.presentation.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import it.laface.common.ContentToShow
import it.laface.common.view.BaseAdapter
import it.laface.common.view.goneUnless
import it.laface.common.view.inflater
import it.laface.common.viewModels
import it.laface.navigation.Navigator
import it.laface.stats.domain.LeadersPageProvider
import it.laface.stats.domain.StatsDataSource
import it.laface.stats.domain.StatsSection
import it.laface.stats.presentation.databinding.FragmentStatsBinding
import it.laface.stats.presentation.databinding.ItemStatsGroupBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class StatsFragment(
    navigator: Navigator,
    leadersPageProvider: LeadersPageProvider,
    statsDataSource: StatsDataSource
) : Fragment() {

    private val viewModel: StatsViewModel by viewModels {
        StatsViewModel(
            navigator = navigator,
            leadersPageProvider = leadersPageProvider,
            statsDataSource = statsDataSource,
            jobDispatcher = Dispatchers.IO
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentStatsBinding
            .inflate(inflater, container, false)
            .apply {
                setView()
            }
            .root

    private fun FragmentStatsBinding.setView() {
        val groupAdapter = BaseAdapter { parent ->
            StatsGroupViewHolder(
                ItemStatsGroupBinding.inflate(parent.inflater, parent, false),
                viewModel::onStatsClicked
            )
        }
        backImageView.setOnClickListener {
            viewModel.navigateBack()
        }
        groupRecyclerView.adapter = groupAdapter
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewModel.statsCallState.collect { contentToShow ->
                bindContentToShow(contentToShow, groupAdapter)
            }
        }
    }

    private fun FragmentStatsBinding.bindContentToShow(
        contentToShow: ContentToShow<StatsSection>,
        newsAdapter: BaseAdapter<StatsSection>
    ) {
        if (contentToShow is ContentToShow.Success) {
            groupRecyclerView.visibility = View.VISIBLE
            newsAdapter.list = contentToShow.contentList
        } else {
            groupRecyclerView.visibility = View.GONE
        }
        progressBar.goneUnless(contentToShow is ContentToShow.Loading)
    }
}