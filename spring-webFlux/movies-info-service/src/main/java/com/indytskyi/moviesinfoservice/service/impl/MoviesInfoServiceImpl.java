package com.indytskyi.moviesinfoservice.service.impl;

import com.indytskyi.moviesinfoservice.model.MovieInfo;
import com.indytskyi.moviesinfoservice.repository.MovieInfoRepository;
import com.indytskyi.moviesinfoservice.service.MoviesInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MoviesInfoServiceImpl implements MoviesInfoService {

    private final MovieInfoRepository movieInfoRepository;

    @Override
    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {
        return movieInfoRepository.save(movieInfo);
    }

    @Override
    public Flux<MovieInfo> getAllMoviesInfo() {
        return movieInfoRepository.findAll();
    }

    @Override
    public Mono<MovieInfo> getMovieInfoById(String id) {
        return movieInfoRepository.findById(id);
    }

    @Override
    public Mono<MovieInfo> updateMovieInfo(MovieInfo updatedMovieInfo, String id) {
        return movieInfoRepository
                .findById(id)
                .flatMap(oldMovieInfo -> {
                    updateOldDataOfMoviesInfoToUpdated(oldMovieInfo, updatedMovieInfo);
                    return movieInfoRepository.save(oldMovieInfo);
                });

    }

    private void updateOldDataOfMoviesInfoToUpdated(
            MovieInfo oldMovieInfo, MovieInfo upDatedMovieInfo) {

        oldMovieInfo.setCast(upDatedMovieInfo.getCast());
        oldMovieInfo.setName(upDatedMovieInfo.getName());
        oldMovieInfo.setYear(upDatedMovieInfo.getYear());
        oldMovieInfo.setReleaseDate(upDatedMovieInfo.getReleaseDate());
    }

    @Override
    public Mono<Void> deleteMovieInfo(String id) {
        return movieInfoRepository.deleteById(id);
    }

    @Override
    public Flux<MovieInfo> getMovieInfoByYear(Integer year) {
        return movieInfoRepository.findByYear(year);
    }
}


