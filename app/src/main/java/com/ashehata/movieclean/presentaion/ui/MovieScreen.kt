package com.ashehata.movieclean.presentaion.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.ashehata.movieclean.R
import com.ashehata.movieclean.domain.models.Movie
import com.ashehata.movieclean.presentaion.models.MoviesType
import com.ashehata.movieclean.presentaion.util.ToRateColor
import com.ashehata.movieclean.presentaion.util.compose.items
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.flow.Flow

@Composable
fun MoviesScreen(
    moviesFlow: Flow<PagingData<Movie>>,
    onFilterClicked: (MoviesType) -> Unit = {},
    onMovieClicked: (Movie) -> Unit = {},
) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(scaffoldState = scaffoldState,
        topBar = {
            TopBar(onFilterClicked)
        }, content = {
            MoviesContent(scaffoldState, it, moviesFlow)
        })
}

@Composable
fun MoviesContent(
    scaffoldState: ScaffoldState,
    it: PaddingValues,
    moviesFlow: Flow<PagingData<Movie>>
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(false),
        onRefresh = {

        },
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Last refresh: 2 min ago",
                fontSize = 20.sp,
                color = MaterialTheme.colors.primary
            )
            MoviesList(scaffoldState, it, moviesFlow)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TopBar(onFilterItemClicked: (MoviesType) -> Unit = {}) {
    var mDisplayMenu by remember { mutableStateOf(false) }
    val mContext = LocalContext.current
    TopAppBar(title = {
        Title()
    }, actions = {
        DotsIcon(onFilterClicked = {
            mDisplayMenu = !mDisplayMenu
        })
        // Creating a dropdown menu
        DropdownMenu(
            expanded = mDisplayMenu,
            onDismissRequest = { mDisplayMenu = false }
        ) {

            DropdownMenuItem(onClick = {
                onFilterItemClicked(MoviesType.TOP_RATED)
                mDisplayMenu = false
            }) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(imageVector = Icons.Filled.Star, contentDescription = null)
                    Text(text = stringResource(id = R.string.top_rated))
                }
            }

            DropdownMenuItem(onClick = {
                onFilterItemClicked(MoviesType.POPULAR)
                mDisplayMenu = false
            }) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = null)
                    Text(text = stringResource(id = R.string.popular))
                }
            }
        }
    })

}

@Composable
fun Title() {
    Text(
        text = stringResource(id = R.string.movie_app_name),
        color = Color.White,
        fontSize = 22.sp
    )
}

@Composable
fun MoviesList(
    scaffoldState: ScaffoldState,
    paddingValues: PaddingValues,
    moviesFlow: Flow<PagingData<Movie>>
) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 4.dp)
    ) {

        val moviesItems = moviesFlow.collectAsLazyPagingItems()

        LazyVerticalGrid(
            columns = GridCells.Adaptive(100.dp),
            contentPadding = PaddingValues(
                start = 8.dp,
                top = 8.dp,
                end = 8.dp,
                bottom = 8.dp
            ),
        ) {
            items(moviesItems) { movie ->
                MovieItem(movie)
            }

        }
    }
}

@Composable
fun MovieItem(movie: Movie?) {
    movie?.let {
        Column(
            Modifier
                .padding(12.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                }
                .background(MaterialTheme.colors.secondary)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MovieImageRate(movie)
            Text(
                text = movie.name,
                fontSize = 18.sp,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun MovieImageRate(movie: Movie) {
    Box {
        MovieImage(movie.imageUrl)
        MovieRateBadge(movie.voteAverage, Modifier.align(Alignment.TopEnd))
    }
}

@Composable
fun MovieRateBadge(voteAverage: Double, Modifier: Modifier) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(voteAverage.ToRateColor())

    ) {
        Text(
            modifier = Modifier
                .padding(4.dp),
            text = voteAverage.toString(),
            fontSize = 16.sp,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun MovieImage(imageUrl: String) {
    Card(
        modifier = Modifier
            .size(70.dp)
            .clip(CircleShape)
            .border(
                1.5.dp,
                MaterialTheme.colors.secondary,
                CircleShape
            )
            .background(MaterialTheme.colors.primaryVariant)
            .clickable {

            }, elevation = 2.dp
    ) {
        LoadImageAsync(
            modifier = Modifier
                .padding(2.dp)
                .clip(CircleShape),
            imageUrl
        )
    }
}

@Composable
fun LoadImageAsync(
    modifier: Modifier,
    imageUrl: String?,
    @DrawableRes placeholder: Int = R.drawable.placeholder_image,
    @DrawableRes onError: Int = R.drawable.error_image,
    cacheEnabled: Boolean = true,
    contentScale: ContentScale = ContentScale.Crop
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .also {
                if (cacheEnabled) {
                    it.diskCachePolicy(CachePolicy.ENABLED)
                }
            }
            .build(),
        placeholder = painterResource(placeholder),
        contentDescription = "Image",
        contentScale = contentScale,
        error = painterResource(onError),
        modifier = modifier
    )
}

@Composable
fun DotsIcon(
    onFilterClicked: () -> Unit,
    size: Dp = 50.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(50))
            .padding(4.dp)
            .clickable {
                onFilterClicked()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_filter_list),
            tint = MaterialTheme.colors.secondary,
            contentDescription = null
        )
    }
}

@Composable
fun StatusBarColor() {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = MaterialTheme.colors.primary
    )
}