package com.example.common.services

import com.example.common.actions.MoviesActions
import com.example.common.actions.PeopleActions
import com.example.common.getPreferredLanguage
import com.example.common.models.*
import ru.pocketbyte.kydra.log.KydraLog
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import ru.pocketbyte.kydra.log.info
import kotlin.coroutines.CoroutineContext


class APIService(
    private val networkContext: CoroutineContext,
    private val uiContext: CoroutineContext
): CoroutineScope {
    val baseURL: String = "https://api.themoviedb.org/3"
    val apiKey: String = "1d9b898a212ea52e283351e521e17871"
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = networkContext + job
    val uiScope: CoroutineScope = CoroutineScope(coroutineContext)
    sealed class APIError : Error() {
        object noResponse : APIError()
        data class jsonDecodingError(val error: Error) : APIError()
        data class networkError(val error: Error) : APIError()
    }

    sealed class Endpoint {
        object popular : Endpoint()
        object topRated : Endpoint()
        object upcoming : Endpoint()
        object nowPlaying : Endpoint()
        object trending : Endpoint()
        data class movieDetail(val movie: String) : Endpoint()
        data class recommended(val movie: String) : Endpoint()
        data class similar(val movie: String) : Endpoint()
        data class credits(val movie: String) : Endpoint()
        data class review(val movie: String) : Endpoint()
        object searchMovie : Endpoint()
        object searchKeyword : Endpoint()
        object searchPerson : Endpoint()
        object popularPersons : Endpoint()
        data class personDetail(val person: String) : Endpoint()
        data class personMovieCredits(val person: String) : Endpoint()
        data class personImages(val person: String) : Endpoint()
        object genres : Endpoint()
        object discover : Endpoint()


        fun path(): String = when (this) {
            popular -> "movie/popular"
            is popularPersons -> "person/popular"
            is topRated -> "movie/top_rated"
            is upcoming -> "movie/upcoming"
            is nowPlaying -> "movie/now_playing"
            is trending -> "trending/movie/day"
            is movieDetail -> "movie/$movie"
            is personDetail -> "person/$person}"
            is credits -> "movie/$movie/credits"
            is review -> "movie/$movie/reviews"
            is recommended -> "movie/$movie/recommendations"
            is similar -> "movie/$movie/similar"
            is personMovieCredits -> "person/$person/movie_credits"
            is personImages -> "person/$person/images"
            is searchMovie -> "search/movie"
            is searchKeyword -> "search/keyword"
            is searchPerson -> "search/person"
            is genres -> "genre/movie/list"
            is discover -> "discover/movie"
        }
    }

    val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

    val client by lazy {
        return@lazy try {
            HttpClient {
                install(JsonFeature) {
                    serializer = KotlinxSerializer(json)
                }
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.ALL
                }
            }
        } catch (e: Exception) {
            throw RuntimeException("Error initializing: ${e.message}")
        }
    }

    inline fun <reified T> GET(
        endpoint: Endpoint,
        params: Map<String, String>?,
        crossinline completionHandler: (Result<T>).() -> Unit
    ) {
        launch {
            KydraLog.info("BASE_URL = $baseURL")
            try {
                val response = client.get<T> {
                    apiUrl(endpoint.path())
                    parameter("api_key", apiKey)
                    parameter("language", getPreferredLanguage())
                    params?.forEach { parameter(it.key, it.value) }
                    KydraLog.info("URL: ${url.buildString()}")
                }
//                uiScope.launch {
                    completionHandler(Result.success(response))
//                }
            } catch (e: Exception) {
                //TODO return appropriate APIErrors
                completionHandler(Result.failure(e))
            }
        }
    }

    fun HttpRequestBuilder.apiUrl(path: String) {
        header(HttpHeaders.CacheControl, io.ktor.client.utils.CacheControl.MAX_AGE)
        url {
            takeFrom(baseURL)
            encodedPath = "/3/$path"
        }
    }
}
