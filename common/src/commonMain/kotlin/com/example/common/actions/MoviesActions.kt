package com.example.common.actions

import com.example.common.MoviesSort
import com.example.common.models.*
import com.example.common.preferences.AppUserDefaults
import com.example.common.services.APIService
import com.example.common.services.APIService.*
import com.example.common.state.AppState
import com.example.common.state.MoviesMenu
import ru.pocketbyte.kydra.log.KydraLog
import kotlinx.serialization.Serializable
import org.reduxkotlin.Thunk
import ru.pocketbyte.kydra.log.info

class MoviesActions(private val apiService: APIService,
                    private val appUserDefaults: AppUserDefaults) {

    fun fetchMoviesMenuList(list: MoviesMenu, page: Int): Thunk<AppState> = { dispatch, _, _ ->
        apiService.GET<MoviePaginatedResponse>(
            endpoint = list.endpoint,
            params = mapOf(
                "page" to page.toString(),
                "region" to appUserDefaults.region
            )
        ) {
            onSuccess {
                dispatch(
                    SetMovieMenuList(
                        page = page,
                        list = list,
                        response = it
                    )
                )
            }
            onFailure {
                KydraLog.info(it.message ?: "Error fetching")
            }
        }
    }

    fun fetchDetail(movie: String): Thunk<AppState> = { dispatch, _, _ ->
            apiService.GET<Movie>(
                endpoint = Endpoint.movieDetail(movie = movie),
                params = mapOf(
                    "append_to_response" to "keywords,images", "include_image_language" to
                            /*"${Locale.current.languageCode ?: */"en,en,null"
                )
            ) {
                onSuccess { dispatch(SetDetail(movie = movie, response = it)) }
                onFailure { }
            }
        }

    fun fetchRecommended(movie: String): Thunk<AppState> = { dispatch, _, _ ->
        apiService.GET<MoviePaginatedResponse>(
            endpoint = Endpoint.recommended(
                movie = movie
            ), params = null
        ) {
            onSuccess { dispatch(SetRecommended(movie = movie, response = it)) }
            onFailure { }
        }
    }

    fun fetchSimilar(movie: String): Thunk<AppState> = { dispatch, _, _ ->
        apiService.GET<MoviePaginatedResponse>(
            endpoint = Endpoint.similar(movie = movie),
            params = null
        ) {
            onSuccess { dispatch(SetSimilar(movie = movie, response = it)) }
            onFailure { }
        }
    }

    fun fetchSearch(query: String, page: Int): Thunk<AppState> = { dispatch, _, _ ->
        apiService.GET<MoviePaginatedResponse>(
            endpoint = Endpoint.searchMovie,
            params = mapOf("query" to query, "page" to "${page}")
        ) {
            onSuccess {
                dispatch(
                    SetSearch(
                        query = query,
                        page = page,
                        response = it
                    )
                )
            }
            onFailure { }
        }
    }

    fun fetchSearchKeyword(query: String): Thunk<AppState> = { dispatch, _, _ ->
        apiService.GET<KeywordPaginatedResponse>(
            endpoint = Endpoint.searchKeyword,
            params = mapOf("query" to query)
        ) {
            onSuccess {
                dispatch(
                    SetSearchKeyword(
                        query = query,
                        response = it
                    )
                )
            }
            onFailure { }
        }
    }

    fun fetchMoviesGenre(genre: Genre, page: Int, sortBy: MoviesSort): Thunk<AppState> = { dispatch, _, _ ->
        apiService.GET<MoviePaginatedResponse>(
            endpoint = Endpoint.discover,
            params = mapOf(
                "with_genres" to "${genre.id}",
                "page" to "$page",
                "sort_by" to sortBy.sortByAPI
            )
        ) {
            onSuccess {
                dispatch(
                    SetMovieForGenre(
                        genre = genre,
                        page = page,
                        response = it
                    )
                )
            }
            onFailure { }
        }
    }

    fun fetchMovieReviews(movie: String): Thunk<AppState> = { dispatch, _, _ ->
        apiService.GET<ReviewPaginatedResponse>(
            endpoint = Endpoint.review(
                movie =
                movie
            ), params = mapOf("language" to "en-US")
        ) {
            onSuccess { dispatch(SetMovieReviews(movie = movie, response = it)) }
            onFailure { }
        }
    }

    fun fetchMovieWithCrew(crew: String): Thunk<AppState> = { dispatch, _, _ ->
        apiService.GET<MoviePaginatedResponse>(
            endpoint = Endpoint.discover,
            params = mapOf("with_people" to "$crew")
        ) {
            onSuccess { dispatch(SetMovieWithCrew(crew = crew, response = it)) }
            onFailure { }
        }
    }

    fun fetchMovieWithKeywords(keyword: String, page: Int): Thunk<AppState> = { dispatch, _, _ ->
        apiService.GET<MoviePaginatedResponse>(
            endpoint = Endpoint.discover, params = mapOf(
                "page" to "$page",
                "with_keywords" to "$keyword"
            )
        ) {
            onSuccess {
                dispatch(
                    SetMovieWithKeyword(
                        keyword = keyword,
                        page = page,
                        response = it
                    )
                )
            }
            onFailure { }
        }
    }

    fun fetchRandomDiscover(filter: DiscoverFilter? = null): Thunk<AppState> = { dispatch, _, _ ->
        var filter = filter
        if (filter == null) {
            filter = DiscoverFilter.randomFilter()
        }
        apiService.GET<MoviePaginatedResponse>(
            endpoint = Endpoint.discover,
            params = filter.toParams()
        ) {
            onSuccess {
                dispatch(
                    SetRandomDiscover(filter = filter, response = it)
                )
            }
            onFailure { }
        }
    }

    @Serializable
    data class GenresResponse(val genres: List<Genre>)

    fun fetchGenres(): Thunk<AppState> = { dispatch, _, _ ->
        apiService.GET<GenresResponse>(endpoint = Endpoint.genres, params = null) {
            onSuccess { dispatch(SetGenres(genres = it.genres)) }
            onFailure { KydraLog.info(it.message ?: "Failure fetching Genres")}
        }
    }

    data class SetMovieMenuList(
        val page: Int,
        val list: MoviesMenu,
        val response: MoviePaginatedResponse
    )

    data class SetDetail(
        val movie: String,
        val response: Movie
    )

    data class SetRecommended(
        val movie: String,
        val response: MoviePaginatedResponse
    )


    data class SetSimilar(
        val movie: String,
        val response: MoviePaginatedResponse
    )


    data class KeywordResponse(
        val id: String,
        val keywords: List<Keyword>
    )


    data class SetSearch(
        val query: String,
        val page: Int,
        val response: MoviePaginatedResponse
    )


    data class SetGenres(val genres: List<Genre>)


    data class SetSearchKeyword(
        val query: String,
        val response: KeywordPaginatedResponse
    )


    data class AddToWishlist(val movie: String)


    data class RemoveFromWishlist(val movie: String)

    data class AddToSeenList(val movie: String)

    data class RemoveFromSeenList(val movie: String)

    data class SetMovieForGenre(
        val genre: Genre,
        val page: Int,
        val response: MoviePaginatedResponse
    )

    data class SetMovieWithCrew(
        val crew: String,
        val response: MoviePaginatedResponse
    )

    data class SetMovieWithKeyword(
        val keyword: String,
        val page: Int,
        val response: MoviePaginatedResponse
    )

    class ResetRandomDiscover

    data class SetRandomDiscover(
        val filter: DiscoverFilter,
        val response: MoviePaginatedResponse
    )


    data class PushRandomDiscover(val movie: String)

    class PopRandromDiscover

    data class SetMovieReviews(
        val movie: String,
        val response: ReviewPaginatedResponse
    )

    data class AddCustomList(val list: CustomList)

    data class EditCustomList(
        val list: String,
        val title: String?,
        val cover: String?
    )

    data class AddMovieToCustomList(
        val list: String,
        val movie: String
    )

    data class AddMoviesToCustomList(
        val list: String,
        val movies: List<String>
    )

    data class RemoveMovieFromCustomList(
        val list: String,
        val movie: String
    )

    data class RemoveCustomList(val list: String)

    data class SaveDiscoverFilter(val filter: DiscoverFilter)

    class ClearSavedDiscoverFilters
}
