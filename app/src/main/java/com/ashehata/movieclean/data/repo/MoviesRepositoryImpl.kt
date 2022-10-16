package com.ashehata.movieclean.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.ashehata.movieclean.data.local.LocalData
import com.ashehata.movieclean.data.local.MoviesLocalPagingSource
import com.ashehata.movieclean.data.mappers.toLocalMovie
import com.ashehata.movieclean.data.models.MovieLocal
import com.ashehata.movieclean.data.models.MoviesRemoteResponse
import com.ashehata.movieclean.data.remote.MoviesPagingSource
import com.ashehata.movieclean.data.remote.MoviesType
import com.ashehata.movieclean.data.remote.RemoteData
import com.ashehata.movieclean.data.util.PAGE_SIZE_PAGING_LOCAL_MOVIE
import com.ashehata.movieclean.data.util.PAGE_SIZE_PAGING_REMOTE_MOVIE
import com.ashehata.movieclean.domain.repo.MoviesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class MoviesRepositoryImpl @Inject constructor(
    private val moviesLocalPagingSource: MoviesLocalPagingSource,
    private val moviesPagingSource: MoviesPagingSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : MoviesRepository {


    override suspend fun getPopularMovies(): Pager<Int, MoviesRemoteResponse.Movie> =
        withContext(dispatcher) {
            // change movies type
            moviesPagingSource.setMoviesType(MoviesType.POPULAR)
            moviesPagingSource.setForceCaching(true)
            // get from API
            return@withContext Pager(config = PagingConfig(
                pageSize = PAGE_SIZE_PAGING_REMOTE_MOVIE,
                enablePlaceholders = false
            ), pagingSourceFactory = {
                moviesPagingSource
            })
        }

    override suspend fun getTopRatedMovies(): Pager<Int, MoviesRemoteResponse.Movie> =
        withContext(dispatcher) {
            moviesPagingSource.setMoviesType(MoviesType.TOP_RATED)
            return@withContext Pager(config = PagingConfig(
                pageSize = PAGE_SIZE_PAGING_REMOTE_MOVIE,
                enablePlaceholders = false
            ), pagingSourceFactory = {
                moviesPagingSource
            })
        }

    override suspend fun getCachedPopularMovies(): Pager<Int, MovieLocal> =
        withContext(dispatcher) {
            return@withContext Pager(config = PagingConfig(
                pageSize = PAGE_SIZE_PAGING_LOCAL_MOVIE,
                enablePlaceholders = false
            ), pagingSourceFactory = {
                moviesLocalPagingSource
            })
        }

}