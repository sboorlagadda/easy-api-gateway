package io.easystack.apigateway.services.catalog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import rx.Observable;
import rx.Subscriber;

@Service
public class CatalogIntegrationService {

    private Log log = LogFactory.getLog(CatalogIntegrationService.class);

    @Autowired
    @Qualifier("loadBalancedRestTemplate")
    @LoadBalanced
    RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "stubMovie",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
            }
    )
    public Observable<Movie> getMovie(final String mlId) {
        return Observable.create(new Observable.OnSubscribe<Movie>() {
			@Override
			public void call(Subscriber<? super Movie> observer) {
				try {
					if (!observer.isUnsubscribed()) {
						final Movie movie = restTemplate.getForObject("http://catalog/movies/{mlId}", Movie.class, mlId);
		                log.debug(movie);
						observer.onNext(movie);
						observer.onCompleted();
					}
				} catch (Exception e) {
					observer.onError(e);
				}
			}
		});
    }

    @SuppressWarnings("unused")
    private Movie stubMovie(final String mlId) {
        Movie stub = new Movie();
        stub.setMlId(mlId);
        stub.setTitle("Interesting...the wrong title. Sssshhhh!");
        return stub;
    }
}
