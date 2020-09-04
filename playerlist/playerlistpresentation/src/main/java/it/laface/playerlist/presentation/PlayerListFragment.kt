package it.laface.playerlist.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import it.laface.common.ContentToShow
import it.laface.common.view.BaseAdapter
import it.laface.common.view.goneUnless
import it.laface.common.view.inflater
import it.laface.common.viewModels
import it.laface.navigation.Navigator
import it.laface.player.domain.Player
import it.laface.player.domain.PlayerDetailPageProvider
import it.laface.player.domain.PlayersDataSource
import it.laface.player.domain.Position
import it.laface.player.domain.Position.Center
import it.laface.player.domain.Position.Forward
import it.laface.player.domain.Position.Guard
import it.laface.playerlist.presentation.databinding.FragmentPlayerListBinding
import it.laface.playerlist.presentation.databinding.ItemPlayerBinding
import it.laface.stats.domain.StatsPageProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PlayerListFragment(
    dataSource: PlayersDataSource,
    playerDetailPageProvider: PlayerDetailPageProvider,
    navigator: Navigator,
    statsPageProvider: StatsPageProvider
) : Fragment() {

    private val viewModel: PlayerListViewModel by viewModels {
        PlayerListViewModel(
            dataSource = dataSource,
            playerDetailPageProvider = playerDetailPageProvider,
            navigator = navigator,
            jobDispatcher = Dispatchers.IO,
            statsPageProvider = statsPageProvider
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentPlayerListBinding
            .inflate(inflater, container, false)
            .apply { setView() }.root

    private fun FragmentPlayerListBinding.setView() {
        val playersAdapter = BaseAdapter { parent ->
            PlayerViewHolder(
                ItemPlayerBinding.inflate(parent.inflater, parent, false),
                viewModel::onPlayerSelected
            )
        }
        playersRecyclerView.adapter = playersAdapter
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewModel.contentToShow.collect { contentToShow ->
                bindContentToShow(contentToShow, playersAdapter)
            }
            playerNameEditText.setText(viewModel.nameToFilter.value)
        }
        playerNameEditText.doAfterTextChanged { text ->
            viewModel.setNameToFilter(text.toString())
        }
        retryButton.setOnClickListener { viewModel.onRetry() }
        cardLeaders.root.setOnClickListener {
            viewModel.goToStatsPage()
        }
        bindPositionFilters()
    }

    private fun FragmentPlayerListBinding.bindContentToShow(
        contentToShow: ContentToShow<Player>,
        playersAdapter: BaseAdapter<Player>
    ) {
        if (contentToShow is ContentToShow.Success) {
            playersRecyclerView.visibility = View.VISIBLE
            playersAdapter.list = contentToShow.contentList
        } else {
            playersRecyclerView.visibility = View.GONE
        }
        progressBar.goneUnless(contentToShow is ContentToShow.Loading)
        retryButton.goneUnless(contentToShow is ContentToShow.Error)
        emptyListPlaceholder.goneUnless(contentToShow is ContentToShow.Placeholder)
    }

    private fun FragmentPlayerListBinding.bindPositionFilters() {
        bindChip(centerChip, Center)
        bindChip(guardChip, Guard)
        bindChip(forwardChip, Forward)
    }

    private fun bindChip(chip: Chip, position: Position) {
        chip.isChecked = viewModel.filters.value.contains(position)
        chip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                viewModel.addFilter(position)
            else
                viewModel.removeFilter(position)
        }
    }
}