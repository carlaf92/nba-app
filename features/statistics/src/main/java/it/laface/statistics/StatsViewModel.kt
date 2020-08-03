package it.laface.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.laface.domain.NetworkResult
import it.laface.domain.datasource.StatsDataSource
import it.laface.domain.model.StatsGroup
import it.laface.domain.navigation.Navigator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Suppress("EXPERIMENTAL_API_USAGE")
class StatsViewModel(
    private val navigator: Navigator,
    private val statsDataSource: StatsDataSource,
    private val jobDispatcher: CoroutineDispatcher
) : ViewModel() {

    val statsCallState: MutableStateFlow<ContentToShow> =
        MutableStateFlow(ContentToShow.Loading)

    init {
        getStats()
    }

    private fun getStats() {
        viewModelScope.launch(jobDispatcher) {
            statsCallState.value = when (val response = statsDataSource.getLeaders()) {
                is NetworkResult.Success -> {
                    ContentToShow.Success(response.value)
                }
                is NetworkResult.Error ->
                    ContentToShow.Error
            }
        }
    }

    fun onStatsClicked(group: StatsGroup) {
        TODO()
    }

    fun navigateBack() {
        navigator.navigateBack()
    }
}
