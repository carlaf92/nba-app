package it.laface.ranking.api

import it.laface.networking.getApiService
import it.laface.networking.getClient
import it.laface.networking.getConverter

object RankingApi {

    val service: RankingService by lazy {
        val client = getClient()
        val converter = getConverter()
        getApiService(converterFactory = converter, client = client)
    }
}