package fr.recia.menucantine;


import java.util.Arrays;

import javax.servlet.Filter;

import org.apereo.portal.soffit.security.SoffitApiAuthenticationManager;
import org.apereo.portal.soffit.security.SoffitApiPreAuthenticatedProcessingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.ErrorPageFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;




@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Value("${soffit.jwt.signatureKey:Changeme}")
    private String signatureKey;
	
	@Value("${soffit.anonymous.acces}")
	private String forTest;
	
	private static final Logger log = LoggerFactory.getLogger(SecurityConfiguration.class);	
	@Override
    public void configure(WebSecurity web) throws Exception {
        /*
         * Since this module includes portlets, we only want to apply Spring Security to requests
         * targeting our REST APIs.
         */
        final RequestMatcher pathMatcher = new AntPathRequestMatcher("/api/**");
        final RequestMatcher inverseMatcher = new NegatedRequestMatcher(pathMatcher);
        web.ignoring().requestMatchers(inverseMatcher);
        log.debug("configure(WebSecurity)");
        
    }
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
		  /*
         * Provide a SoffitApiPreAuthenticatedProcessingFilter (from uPortal) that is NOT a
         * top-level bean in the Spring Application Context.
         */
		log.debug("configure signatureKey = {}", signatureKey);
        final AbstractPreAuthenticatedProcessingFilter filter =
                new SoffitApiPreAuthenticatedProcessingFilter(signatureKey);
        filter.setAuthenticationManager(authenticationManager());

        http.csrf().disable(); // attention c'est pour le crsf().disable  est pour les tests a partir de page static (sans csrf)
        http
            .addFilter(filter)
            .authorizeRequests()
           		.antMatchers(HttpMethod.GET, forTest).anonymous()
            	.antMatchers(HttpMethod.POST, forTest).anonymous()
                .antMatchers(HttpMethod.GET,"/api/**").authenticated()
                .antMatchers(HttpMethod.POST,"/api/**").authenticated()
                .antMatchers(HttpMethod.DELETE,"/api/**").denyAll()
                .antMatchers(HttpMethod.PUT,"/api/**").denyAll()
                .anyRequest().permitAll() 
            .and() // pour la dev en local host autorisation du cross domaine
            	.cors()
            	.configurationSource(corsConfigurationSource())
            .and()
            /*
             * Session fixation protection is provided by uPortal.  Since portlet tech requires
             * sessionCookiePath=/, we will make the portal unusable if other modules are changing
             * the sessionId as well.
             */
            .sessionManagement()
                .sessionFixation().none();
	}
	@Bean
    public AuthenticationManager authenticationManager() {
        return new SoffitApiAuthenticationManager();
    }

	 @Bean
	    public ErrorPageFilter errorPageFilter() {
	        return new ErrorPageFilter();
	    }
	 @Bean
	    public FilterRegistrationBean<Filter> disableSpringBootErrorFilter() {
	        /*
	         * The ErrorPageFilter (Spring) makes extra calls to HttpServletResponse.flushBuffer(),
	         * and this behavior produces many warnings in the portal logs during portlet requests.
	         */
	        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
	        filterRegistrationBean.setFilter(errorPageFilter());
	        filterRegistrationBean.setEnabled(false);
	        return filterRegistrationBean;
	    }

	 @Bean
	    CorsConfigurationSource corsConfigurationSource() {
	        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

	        boolean abilitaCors = true;
	        if( abilitaCors )
	        {
	            if( log.isWarnEnabled() )
	            {
	                log.warn("CORS ABILITATI! CORS est autorisé");
	            }
	            CorsConfiguration configuration = new CorsConfiguration();
	           	            configuration.setAllowedOrigins( Arrays.asList("http://localhost:8080"));
	            configuration.setAllowedMethods(Arrays.asList(  RequestMethod.GET.name(),
	                    RequestMethod.POST.name(), 
	                    RequestMethod.OPTIONS.name(), 
	                    RequestMethod.DELETE.name(),
	                    RequestMethod.PUT.name()));
	            configuration.setExposedHeaders(Arrays.asList("x-auth-token", "x-requested-with", "x-xsrf-token"));
	            configuration.setAllowedHeaders(Arrays.asList("content-type", "x-com-persist", "X-Auth-Token","x-auth-token", "x-requested-with", "x-xsrf-token"));
	            source.registerCorsConfiguration("/**", configuration);
	        }
	        return  source;
	    }
}
