package com.example.common.state

import com.example.common.services.APIService
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.pocketbyte.kydra.log.KydraLog
import ru.pocketbyte.kydra.log.debug

@Serializable
data class AppState(
    val moviesState: MoviesState = MoviesState(),
    val peoplesState: PeoplesState = PeoplesState()
) {
    fun encode(): String {
        val encoded = json.encodeToJsonElement(serializer(), this).toString()
        KydraLog.debug(encoded)
        return encoded
    }

    fun getSaveState() = copy(moviesState = moviesState.getSaveState(),
        peoplesState = peoplesState.getSaveState())

    companion object {
        val json = Json {
            isLenient = true
            ignoreUnknownKeys = true
        }

        fun decode(jsonStr: String): AppState {
            KydraLog.debug(jsonStr)
            return json.decodeFromString(serializer(), jsonStr)
        }
        fun initialValue() = AppState()
    }
}


enum class MoviesMenu(val title: String, val endpoint: APIService.Endpoint) {
    popular("Popular", APIService.Endpoint.popular),
    topRated("Top Rated", APIService.Endpoint.topRated),
    upcoming("Upcoming", APIService.Endpoint.upcoming),
    nowPlaying("Now Playing", APIService.Endpoint.nowPlaying),
    trending("Trending", APIService.Endpoint.trending),
    genres("Genres", APIService.Endpoint.genres);

    companion object {
        fun allValues(): List<MoviesMenu> = values().toList()
        fun fromOrdinal(ordinal: Int): MoviesMenu = MoviesMenu.values()[ordinal]
    }
}
fun allMovieMenuValues() = MoviesMenu.values().toList()