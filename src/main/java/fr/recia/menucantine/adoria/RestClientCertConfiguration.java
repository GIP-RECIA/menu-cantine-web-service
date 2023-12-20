/**
 * Copyright (C) 2019 GIP-RECIA https://www.recia.fr/
 * @Author (C) 2019 Pierre Legay <pierre.legay@recia.fr>
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
package fr.recia.menucantine.adoria;

import java.util.function.Supplier;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RestClientCertConfiguration {

	@Value("${server.ssl.key-store}")
    private String keyStore;
	
	@Value("${server.ssl.key-password}")
	private String keyPass;
	
	@Value("${server.ssl.key-store-password}")
	private String keyStorePass;
	
	

	 @Bean
	 public RestTemplate restTemplate(RestTemplateBuilder builder) throws Exception {
		 SSLContext sslContext = SSLContextBuilder.create()
				 	.loadKeyMaterial(
				 			ResourceUtils.getFile(keyStore), 
				 			keyStorePass.toCharArray(),  
				 			keyPass.toCharArray())
	               // .loadTrustMaterial(ResourceUtils.getFile("classpath:truststore.jks"), keyStorePass.toCharArray())
	                .build();

		 
		 
		 HttpClient client = HttpClients.custom()
	                .setSSLContext(sslContext)
	                .build();

		 return builder
				 .requestFactory(
						 new Supplier<ClientHttpRequestFactory>() {
								@Override
								public ClientHttpRequestFactory get() {
									return new HttpComponentsClientHttpRequestFactory(client);
								}
							})
				 .build();
	 }
	 
	 @Bean
	 public WebClient webClient() throws Exception {
		 
		 /*
		 SSLContext sslContext = SSLContextBuilder.create()
				 	.loadKeyMaterial(
				 			ResourceUtils.getFile(keyStore), 
				 			keyStorePass.toCharArray(),  
				 			keyPass.toCharArray())
	               // .loadTrustMaterial(ResourceUtils.getFile("classpath:truststore.jks"), keyStorePass.toCharArray())
	                .build();
		  */
		 
		 reactor.netty.http.client.HttpClient httpClient = 
				 	reactor.netty.http.client.HttpClient.create().secure();
		 
		 return WebClient.builder()
		            .clientConnector(new ReactorClientHttpConnector(httpClient))
		            .build();
	 }
}
