package io.easystack.apigateway.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import io.easystack.apigateway.domain.MovieDetails;
import io.easystack.apigateway.services.catalog.CatalogIntegrationService;
import io.easystack.apigateway.services.reviews.ReviewsIntegrationService;
import rx.Observable;
import rx.Observer;

@RestController
public class ApiGatewayController {

    Log log = LogFactory.getLog(ApiGatewayController.class);

    @Autowired
    CatalogIntegrationService catalogIntegrationService;

    @Autowired
    ReviewsIntegrationService reviewsIntegrationService;

    @RequestMapping("/movie/{mlId}")
    public DeferredResult<MovieDetails> movieDetails(@PathVariable String mlId) {

        log.debug(String.format("Loading anonymous movie details for mlId: %s", mlId));

        Observable<MovieDetails> movieDetails = anonymousMovieDetails(mlId);
        return toDeferredResult(movieDetails);
    }

    private Observable<MovieDetails> anonymousMovieDetails(String mlId) {
        return Observable.zip(
                catalogIntegrationService.getMovie(mlId),
                reviewsIntegrationService.reviewsFor(mlId),
                (movie, reviews) -> {
                    MovieDetails movieDetails = new MovieDetails();
                    movieDetails.setMlId(movie.getMlId());
                    movieDetails.setTitle(movie.getTitle());
                    movieDetails.setReviews(reviews);
                    movieDetails.setGenres(movie.getGenres());
                    return movieDetails;
                }
        );
    }

    public DeferredResult<MovieDetails> toDeferredResult(Observable<MovieDetails> details) {
        DeferredResult<MovieDetails> result = new DeferredResult<>();
        details.subscribe(new Observer<MovieDetails>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onNext(MovieDetails movieDetails) {
                result.setResult(movieDetails);
            }
        });
        return result;
    }


}
