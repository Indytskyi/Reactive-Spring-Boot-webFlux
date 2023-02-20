package com.indytskyi.moviesinfoservice.controllet;

import com.indytskyi.moviesinfoservice.model.MovieInfo;
import com.indytskyi.moviesinfoservice.service.MoviesInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Slf4j
public class MoviesInfoController {

    private final MoviesInfoService moviesInfoService;

    private final Sinks.Many<MovieInfo> moviesInfoSink = Sinks.many().replay().all();

    @GetMapping(value = "/movie-info/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Flux<MovieInfo> getMoviesInfoSink() {
        return moviesInfoSink.asFlux();
    }

    @PostMapping("/movie-info")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo) {
        return moviesInfoService.addMovieInfo(movieInfo)
                .doOnNext(moviesInfoSink::tryEmitNext);
    }

    @GetMapping("/movie-info")
    @ResponseStatus(HttpStatus.OK)
    public Flux<MovieInfo> getAllMoviesInfo(@RequestParam(value = "year", required = false) Integer year) {
        if (year != null) {
            return moviesInfoService.getMovieInfoByYear(year);
        }
        return moviesInfoService.getAllMoviesInfo();
    }

    @GetMapping("/movie-info/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<MovieInfo>> getMoviesInfoById(@PathVariable String id) {
        return moviesInfoService.getMovieInfoById(id)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PutMapping("/movie-info/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<MovieInfo>> updateMoviesInfo(
            @PathVariable String id,
            @RequestBody MovieInfo movieInfo) {
        return moviesInfoService.updateMovieInfo(movieInfo,id)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/movie-info/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMoviesInfo(@PathVariable String id) {
        return moviesInfoService.deleteMovieInfo(id);
    }


}
