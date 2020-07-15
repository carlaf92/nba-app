package it.laface.network

import it.laface.api.NbaServices
import it.laface.api.models.NbaTeam
import it.laface.domain.NetworkResult
import it.laface.domain.model.PlayerModel
import it.laface.domain.datasource.PlayersDataSource
import it.laface.domain.model.RankedTeam
import it.laface.domain.datasource.RankingDataSource
import it.laface.domain.datasource.ScheduleDataSource
import it.laface.domain.model.Game
import it.laface.domain.model.RankingLists

class NbaApiMapper(private val api: NbaServices) :
    PlayersDataSource,
    ScheduleDataSource,
    RankingDataSource {

    override suspend fun getPlayers(): NetworkResult<List<PlayerModel>> {
        return api.playerList().toNetworkResult { players ->
            players.league.standard.map { nbaPlayer ->
                PlayerModel(
                    name = nbaPlayer.firstName,
                    surname = nbaPlayer.lastName,
                    id = nbaPlayer.personId,
                    teamId = nbaPlayer.teamId,
                    jerseyNumber = nbaPlayer.jerseyNumber,
                    position = nbaPlayer.position
                )
            }
        }
    }

    override suspend fun getRanking(): NetworkResult<RankingLists> {
        fun toDomain(team: NbaTeam) = RankedTeam(
            rankingPosition = team.rankingPosition,
            id = team.id,
            code = team.info.tricode,
            name = "${team.info.name} ${team.info.nickname}"
        )
        return api.ranking().toNetworkResult { response ->
            val conference = response.league.standard.conference
            RankingLists(
                westCoastRanking = conference.west.map(::toDomain),
                eastCoastRanking = conference.east.map(::toDomain)
            )
        }
    }

    override suspend fun getSchedule(): List<Game> {
        return listOf()
    }
}
