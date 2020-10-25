package com.example.kmpstarter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.example.common.models.Movie
import com.example.common.services.ImageUrl
import com.example.common.state.MoviesMenu
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.reduxkotlin.Store

val dividerColor = Color(0xFFC6C6C6.toInt())

@Composable
fun <TSlice, TState> Store<TState>.select(selector: (TState) -> TSlice): TSlice {
    var result by mutableStateOf(selector(state))
    onCommit(selector) {
        val observer = {
            CoroutineScope(Dispatchers.Main).launch {
                val newState = selector(state)
                if (result != newState) {
                    result = selector(state)
                }
            }
            Unit
        }
        val unsubscribe = subscribe(observer)
        onDispose {
            unsubscribe()
        }
    }
    return result
}

@ExperimentalLazyDsl
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                HomeView()
            }
        }
        store.dispatch(movieActions.fetchMoviesMenuList(list = MoviesMenu.nowPlaying, page = 1))
        store.dispatch(movieActions.fetchGenres())
    }
}

@ExperimentalLazyDsl
@Composable
@Preview
fun HomeView() {
    val movies = store.select { it.moviesState.movies.values.toList() }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Now showing",
                style = MaterialTheme.typography.h3,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        itemsIndexed(movies) { index, movie ->
            Item(movie = movie)
            if (index != movies.size - 1) {
                Divider(
                    color = dividerColor,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun Item(movie: Movie) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding()
    ) {
        movie.poster_path?.let {
            CoilImage(
                data = ImageUrl.medium.path(it),
                fadeIn = true,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        Text(
            text = movie.title,
            style = MaterialTheme.typography.h6,
            maxLines = 2,
            textAlign = TextAlign.Center
        )

        Text(
            text = movie.overview,
            maxLines = 5,
            textAlign = TextAlign.Center,
            softWrap = true,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp),
            style = MaterialTheme.typography.body1
        )
    }
}
