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
package fr.recia.menucantine.config;

import fr.recia.menucantine.config.beans.SoffitProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.ErrorPageFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import fr.recia.notifications.soffit_java_client.SoffitJwtAuthenticationFilter;
import fr.recia.notifications.soffit_java_client.SoffitJwtValidator;


import jakarta.servlet.Filter;

@Profile("!test")
@Configuration
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfiguration{

	@Value("${menucantine.demo:false}")
	private boolean isDemoLocale;
	private final CorsConfig corsConfig;
	private final SoffitProperties jwtProperties;

	@Bean
	SoffitJwtValidator soffitJwtValidator() {
		return new SoffitJwtValidator(jwtProperties.getSignatureKey());
	}

	@Bean
	SoffitJwtAuthenticationFilter soffitJwtAuthenticationFilter(SoffitJwtValidator validator) {
		return new SoffitJwtAuthenticationFilter(validator);
	}
	@Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http, SoffitJwtAuthenticationFilter filter) throws Exception {
		 /*
		 * Provide a SoffitApiPreAuthenticatedProcessingFilter (from uPortal) that is NOT a
		 * top-level bean in the Spring Application Context.
		 */

		if(isDemoLocale){
			log.warn("Mode DEMO LOCALE activé !");
			http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
		}else {
			http	.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
					.authorizeHttpRequests(auth -> auth
							.requestMatchers(HttpMethod.GET, "/health-check").anonymous()
							.requestMatchers(HttpMethod.HEAD, "/health-check").anonymous()
							.requestMatchers(HttpMethod.GET, "/api/menu").authenticated()
							.requestMatchers(HttpMethod.GET, "/img/*").permitAll()
							.anyRequest().denyAll()
					);

			http.sessionManagement(config -> config.sessionFixation().none());
			/*
			 * À remplacer par la ligne ci-dessous quand cela sera sorti du portail
			 */
			//	http.sessionManagement(config -> config.sessionFixation().newSession());

		}

		if(corsConfig.isEnabled()) {
			log.debug("CORS est autorisé !");
			http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
		}
		else{
			log.debug("CORS désactivé.");
			http.cors(AbstractHttpConfigurer::disable);
		}

		return http.build();
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
	public FilterRegistrationBean<SoffitJwtAuthenticationFilter> disableSoffitJwtAuthenticationFilterAutoRegistration(SoffitJwtAuthenticationFilter filter) {
		FilterRegistrationBean<SoffitJwtAuthenticationFilter> filterRegistrationBean = new FilterRegistrationBean<>();
		filterRegistrationBean.setFilter(filter);
		filterRegistrationBean.setEnabled(false);
		return filterRegistrationBean;
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(corsConfig.getAllowedOrigins());
		configuration.setAllowedMethods(corsConfig.getAllowedMethods());
		configuration.setExposedHeaders(corsConfig.getExposedHeaders());
		configuration.setAllowedHeaders(corsConfig.getAllowedHeaders());
		source.registerCorsConfiguration("/**", configuration);
		return source;

	}
}
