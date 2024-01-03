/**
 * Copyright (C) 2024 GIP-RECIA https://www.recia.fr/
 * @Author (C) 2024 GIP-RECIA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration{

	@Value("${soffit.jwt.signatureKey:Changeme}")
    private String signatureKey;

	private static final Logger log = LoggerFactory.getLogger(SecurityConfiguration.class);

	@Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		 /*
		 * Provide a SoffitApiPreAuthenticatedProcessingFilter (from uPortal) that is NOT a
		 * top-level bean in the Spring Application Context.
		 */
		log.debug("configure signatureKey = {}", signatureKey);
        final AbstractPreAuthenticatedProcessingFilter filter =
                new SoffitApiPreAuthenticatedProcessingFilter(signatureKey);
        
        filter.setAuthenticationManager(authenticationManager());

        http.csrf().disable(); // attention le crsf().disable est pour les tests a partir de page static (sans csrf)

        http
            .addFilter(filter)
            .authorizeRequests()
            	.antMatchers(HttpMethod.GET, "/health-check").anonymous()
                .antMatchers(HttpMethod.GET,"/api/**").authenticated()
                .antMatchers(HttpMethod.POST,"/api/menu").authenticated()
                .antMatchers(HttpMethod.DELETE,"/api/**").denyAll()
                .antMatchers(HttpMethod.PUT,"/api/**").denyAll()
                .anyRequest().permitAll() 
            .and() // pour la dev en localhost autorisation du cross domaine
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

			return http.build();
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
	public CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		boolean abilitaCors = true;
		if( abilitaCors ) {
			if( log.isWarnEnabled() ) {
				log.warn("CORS ABILITATI! CORS est autorisé");
			}
			CorsConfiguration configuration = new CorsConfiguration();

			configuration.setAllowedOrigins(Arrays.asList(
				"http://localhost:8080",
				"https://localhost:8443",
				"http://192.168.45.196:8080",
				"http://192.168.45.156:8080",
				"http://192.168.3.13:8080",
				"https://test-lycee.giprecia.net")
			);

			configuration.setAllowedMethods(Arrays.asList(
				RequestMethod.GET.name(),
				RequestMethod.POST.name(),
				RequestMethod.OPTIONS.name(),
				RequestMethod.DELETE.name(),
				RequestMethod.PUT.name())
			);

			configuration.setExposedHeaders(Arrays.asList("x-auth-token", "x-requested-with", "x-xsrf-token"));
			configuration.setAllowedHeaders(Arrays.asList("content-type", "authorization", "x-com-persist", "X-Auth-Token","x-auth-token", "x-requested-with", "x-xsrf-token"));
			source.registerCorsConfiguration("/**", configuration);

			}

		return source;

	}
}
