package it.laface.playerlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.laface.common.combine
import it.laface.domain.CallState
import it.laface.domain.NetworkResult
import it.laface.domain.datasource.PlayersDataSource
import it.laface.domain.model.PlayerModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class PlayerListViewModel(
    private val dataSource: PlayersDataSource,
    private val jobDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val playerListCallState: MutableLiveData<CallState<List<PlayerModel>>> =
        MutableLiveData(CallState.NotStarted)
    val nameToFilter: MutableLiveData<String> = MutableLiveData("")
    val contentToShow: MutableLiveData<ContentToShow> =
        playerListCallState.combine(nameToFilter) { callState, nameToFilter ->
            mapContentToShow(callState!!, nameToFilter!!)
        }

    init {
        getPlayers()
    }

    fun onRetry() {
        playerListCallState.value = CallState.InProgress
        getPlayers()
    }

    private fun getPlayers() {
        viewModelScope.launch(jobDispatcher) {
            val callState = when (val response = dataSource.getPlayers()) {
                is NetworkResult.Success -> {
                    CallState.Success(response.value)
                }
                is NetworkResult.Error -> CallState.Error(response.error)
            }
            playerListCallState.postValue(callState)
        }
    }

    fun setNameToFilter(text: String) {
        if (text != nameToFilter.value) {
            nameToFilter.postValue(text)
        }
    }
}