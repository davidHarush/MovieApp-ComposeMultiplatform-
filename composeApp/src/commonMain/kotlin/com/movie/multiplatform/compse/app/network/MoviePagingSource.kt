package com.movie.multiplatform.compse.app.network

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import app.cash.paging.PagingSource
import app.cash.paging.PagingState
import kotlinx.coroutines.flow.Flow

fun getPagedMovies(category: MovieFetchType): Flow<PagingData<Movie>> {
    return Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { MoviePagingSource(category) }
    ).flow
}

class MoviePagingSource(
    private val category: MovieFetchType
) : PagingSource<Int, Movie>() {

    private val repo = MovieRepository()

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val currentPage = params.key ?: 1
        return try {
            val response = category.fetchFunction(repo, currentPage)
            LoadResult.Page(
                data = response.results,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (response.results.isEmpty()) null else currentPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition
    }
}

