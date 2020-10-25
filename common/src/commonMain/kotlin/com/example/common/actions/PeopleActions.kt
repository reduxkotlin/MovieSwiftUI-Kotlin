package com.example.common.actions

import com.example.common.models.*
import com.example.common.services.APIService
import com.example.common.services.APIService.Endpoint.*
import com.example.common.state.AppState
import ru.pocketbyte.kydra.log.KydraLog
import kotlinx.serialization.Serializable
import org.reduxkotlin.Thunk
import ru.pocketbyte.kydra.log.info

class PeopleActions(private val apiService: APIService) {

    fun fetchDetail(people: String): Thunk<AppState> = { dispatch, _, _ ->
        apiService.GET<People>(
            endpoint = personDetail(person = people),
            params = null
        ) {
            onSuccess { dispatch(SetDetail(person = it)) }
            onFailure { }
        }
    }

    @Serializable
    data class ImagesResponse(
        val id: Int,
        val profiles: List<ImageData>
    )

    fun fetchImages(people: String): Thunk<AppState> = { dispatch, _, _ ->
        apiService.GET<ImagesResponse>(
            endpoint = personImages(person = people),
            params = null
        ) {
            onSuccess { dispatch(SetImages(people = people, images = it.profiles)) }
            onFailure {}
        }
    }

    @Serializable
    data class PeopleCreditsResponse(
        val cast: List<Movie>?,
        val crew: List<Movie>?
    )

    fun fetchPeopleCredits(people: String): Thunk<AppState> = { dispatch, _, _ ->
        apiService.GET<PeopleCreditsResponse>(
            endpoint = personMovieCredits(person = people),
            params = null
        ) {
            onSuccess { dispatch(SetPeopleCredits(people = people, response = it)) }
            onFailure { }
        }
    }

    fun fetchMovieCasts(movie: String): Thunk<AppState> = { dispatch, _, _ ->
        apiService.GET<CastResponse>(endpoint = credits(movie = movie), params = null) {
            onSuccess { dispatch(SetMovieCasts(movie = movie, response = it)) }
            onFailure { }
        }
    }

    fun fetchSearch(query: String, page: Int): Thunk<AppState> = { dispatch, _, _ ->
        apiService.GET<PeoplePaginatedResponse>(
            endpoint = searchPerson,
            params = mapOf("query" to query, "page" to page.toString())
        ) {
            onSuccess { dispatch(SetSearch(query = query, page = page, response = it)) }
            onFailure {}
        }
    }

    fun fetchPopular(page: Int): Thunk<AppState> = { dispatch, getState, extraArgument ->
        apiService.GET<PeoplePaginatedResponse>(
            endpoint = popularPersons,
            params = mapOf("page" to "$page", "region" to "us")
        ) {
            onSuccess { dispatch(SetPopular(page = page, response = it)) }
            onFailure { KydraLog.info(it.message ?: "error") }
        }
    }


    data class SetDetail(val person: People)

    data class SetImages(
        val people: String,
        val images: List<ImageData>
    )

    data class SetMovieCasts(
        val movie: String,
        val response: CastResponse
    )

    data class SetSearch(
        val query: String,
        val page: Int,
        val response: PeoplePaginatedResponse
    )

    data class SetPopular(
        val page: Int,
        val response: PeoplePaginatedResponse
    )

    data class SetPeopleCredits(
        val people: String,
        val response: PeopleCreditsResponse
    )

    data class AddToFanClub(val people: String)

    data class RemoveFromFanClub(val people: String)
}
