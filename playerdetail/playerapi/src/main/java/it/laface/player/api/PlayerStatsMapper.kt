package it.laface.player.api

import it.laface.base.NetworkResult
import it.laface.networking.toNetworkResult
import it.laface.player.domain.PlayerStats
import it.laface.player.domain.PlayerStatsDataSource

class PlayerStatsMapper(private val service: PlayerStatsService) : PlayerStatsDataSource {

    override suspend fun getPlayerStats(playerId: String): NetworkResult<PlayerStats> =
        service.playerStats(playerId = playerId).toNetworkResult { players ->
            val stats = players.league.standard.stats.latest
            PlayerStats(
                pointsPerGame = getValue(stats.pointsPerGame),
                reboundsPerGame = getValue(stats.reboundsPerGame),
                assistsPerGame = getValue(stats.assistsPerGame),
            )
        }

    private fun getValue(stats: String): String =
        if (stats.toFloat() >= 0) stats else "0"
}
