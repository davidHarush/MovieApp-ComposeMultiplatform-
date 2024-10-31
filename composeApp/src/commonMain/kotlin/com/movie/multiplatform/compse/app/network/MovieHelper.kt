package com.movie.multiplatform.compse.app.network

object MovieHelper {

   fun getImagesUrl(movie: Movie) :String=
       "https://image.tmdb.org/t/p/w500" + movie.poster_path




}