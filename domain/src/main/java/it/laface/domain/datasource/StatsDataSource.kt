package it.laface.domain.datasource

import it.laface.domain.network.NetworkResult
import it.laface.domain.model.StatsSection

interface StatsDataSource {

    suspend fun getLeaders(): NetworkResult<List<StatsSection>>
}
