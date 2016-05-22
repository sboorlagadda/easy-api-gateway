package io.easystack.apigateway.services.reviews;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import rx.Observable;
import rx.Subscriber;

@Service
public class ReviewsIntegrationService {

    @Autowired
    @Qualifier("loadBalancedRestTemplate")
    @LoadBalanced
    RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "stubReviews",
                    commandProperties = {
                            @HystrixProperty(name="execution.isolation.strategy", value="SEMAPHORE")
                    }
    )
    public Observable<List<Review>> reviewsFor(String mlId) {     
        return Observable.create(new Observable.OnSubscribe<List<Review>>() {
			@Override
			public void call(Subscriber<? super List<Review>> observer) {
				try {
					if (!observer.isUnsubscribed()) {
						ParameterizedTypeReference<List<Review>> responseType = new ParameterizedTypeReference<List<Review>>() {
		                };
						final List<Review> reviews = restTemplate.exchange("http://reviews/reviews/{mlId}", HttpMethod.GET, null, responseType, mlId).getBody();
						observer.onNext(reviews);
						observer.onCompleted();
					}
				} catch (Exception e) {
					observer.onError(e);
				}
			}
		});
    }

    @SuppressWarnings("unused")
    private List<Review> stubReviews(String mlId) {
        Review review = new Review();
        review.setMlId(mlId);
        review.setRating(4);
        review.setTitle("Interesting...the wrong title. Sssshhhh!");
        review.setReview("Awesome sauce!");
        review.setUserName("joeblow");
        return Arrays.asList(review);
    }

}
