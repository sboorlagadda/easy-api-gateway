package io.easystack.apigateway;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.util.WebUtils;

@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
@EnableOAuth2Sso
@EnableHystrix
public class ZuulServerApplication  {

    public static void main(String[] args) {
        SpringApplication.run(ZuulServerApplication.class, args);
    }
    
	@Bean
    @Qualifier("loadBalancedRestTemplate")
	@LoadBalanced
	public RestTemplate loadBalancedRestTemplate() {
		return new RestTemplate();
	}

    @Configuration
    protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.logout().and().antMatcher("/**").authorizeRequests()
                    .antMatchers("/index.html", "/home.html", "/", "/login", "/beans").permitAll()
                    .antMatchers(HttpMethod.GET, "/recommendations/**","/reviews/**","/people/**","/movie/**","/catalog/**","/likes/**").permitAll()
                    .anyRequest().authenticated().and().csrf()
                    .csrfTokenRepository(csrfTokenRepository()).and()
                    .addFilterBefore(new RequestContextFilter(), HeaderWriterFilter.class)
                    .addFilterAfter(csrfHeaderFilter(), CsrfFilter.class);
        }

        private Filter csrfHeaderFilter() {
            return new OncePerRequestFilter() {
                @Override
                protected void doFilterInternal(HttpServletRequest request,
                                                HttpServletResponse response, FilterChain filterChain)
                        throws ServletException, IOException {
                    CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class
                            .getName());
                    if (csrf != null) {
                        Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
                        String token = csrf.getToken();
                        if (cookie == null || token != null
                                && !token.equals(cookie.getValue())) {
                            cookie = new Cookie("XSRF-TOKEN", token);
                            cookie.setPath("/");
                            response.addCookie(cookie);
                        }
                    }
                    filterChain.doFilter(request, response);
                }
            };
        }

        private CsrfTokenRepository csrfTokenRepository() {
            HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
            repository.setHeaderName("X-XSRF-TOKEN");
            return repository;
        }

    }
}
