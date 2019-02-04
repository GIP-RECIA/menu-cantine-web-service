package fr.recia.menucantine.adoria;

import java.util.Map;

import javax.annotation.ManagedBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;


@Configuration
@ManagedBean
public class RestAdoriaClient {
	private static final Logger log = LoggerFactory.getLogger(RestAdoriaClient.class);	

	public class classTest {
		String id; 
		int quantity;
		String name;
	}
	
	
	
	public RestAdoriaClient() {
		super();
	}

	@Autowired
    private RestTemplate restTemplate;
	
	@Autowired
    private WebClient webClient ;
	
	public  Map<String, Object> callTest(){
		//Mono<classTest> result = 
				try {
		/*
					Mono<String> reponse =  webClient.get()
					.uri("https://api.myjson.com/bins/74l63")
					.accept(MediaType.APPLICATION_JSON)
					.retrieve()
					.bodyToMono(String.class);
			*/	
					Mono<Map<String, Object>> reponse =  webClient.get()
							.uri("https://api.myjson.com/bins/74l63")
							.accept(MediaType.APPLICATION_JSON)
							.retrieve()
							.bodyToMono(new ParameterizedTypeReference<Map<String,Object>>() {});
					
				//	reponse.subscribe((i) -> System.out.println(i));
					log.debug("reponse = {}", reponse.block());
					return reponse.block();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			return null;
	}
	
	
	 
}
