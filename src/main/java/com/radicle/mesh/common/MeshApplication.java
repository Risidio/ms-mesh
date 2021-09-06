package com.radicle.mesh.common;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.radicle.mesh.stacks.service.ClarityDeserialiser;
import com.radicle.mesh.stacks.service.ClaritySerialiser;
import com.radicle.mesh.stacks.service.ContractReader;
import com.radicle.mesh.stacks.service.GaiaHubReader;
import com.radicle.mesh.stacks.service.domain.AppMapContract;
import com.squareup.square.SquareClient;

@SpringBootApplication
@ComponentScan("com.radicle")
public class MeshApplication {

	@Autowired private Environment environment;
    private static final Logger logger = LogManager.getLogger(MeshApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(MeshApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
				.allowCredentials(true)
				.allowedMethods("GET", "HEAD", "POST", "PUT", "OPTIONS")
				.allowedHeaders("http://localhost:8085", "http://localhost:8080", "http://localhost:8081", "http://localhost:8082", "http://localhost:8083", "http://localhost:8084", "http://localhost:8085", "http://localhost:8086", "http://localhost:8087", "http://localhost:8088", "http://localhost:8089", "https://prom.risidio.com", "https://thisisnumberone.com", "https://staging.thisisnumberone.com", "https://tchange.risidio.com", "https://xchange.risidio.com", "https://truma.risidio.com", "https://ruma.risidio.com", "https://loopbomb.com", "https://test.loopbomb.com", "https://loopbomb.io", "https://test.loopbomb.io", "https://stacksmate.com", "https://test.stacksmate.com")
				.allowedOrigins("http://localhost:8080", "http://localhost:8081", "http://localhost:8082", "http://localhost:8083", "http://localhost:8084", "http://localhost:8085", "http://localhost:8086", "http://localhost:8087", "http://localhost:8088", "http://localhost:8089", "https://prom.risidio.com", "https://thisisnumberone.com", "https://staging.thisisnumberone.com", "https://tchange.risidio.com", "https://tchange.risidio.com", "https://xchange.risidio.com", "https://truma.risidio.com", "https://ruma.risidio.com", "https://loopbomb.com", "https://test.loopbomb.com", "https://loopbomb.io", "https://test.loopbomb.io", "https://stacksmate.com", "https://test.stacksmate.com")
				.exposedHeaders("IdentityAddress", "Authorization", "content-type", "x-auth-token");
			}
		};
	}
	@Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8085", "http://localhost:8080", "http://localhost:8081", "http://localhost:8082", "http://localhost:8083", "http://localhost:8084", "http://localhost:8085", "http://localhost:8086", "http://localhost:8087", "http://localhost:8088", "http://localhost:8089", "https://prom.risidio.com", "https://thisisnumberone.com", "https://staging.thisisnumberone.com", "https://tchange.risidio.com", "https://xchange.risidio.com", "https://truma.risidio.com", "https://ruma.risidio.com", "https://loopbomb.com", "https://test.loopbomb.com", "https://loopbomb.io", "https://test.loopbomb.io", "https://stacksmate.com", "https://test.stacksmate.com"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("IdentityAddress", "Authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(Arrays.asList("IdentityAddress", "Authorization", "content-type", "x-auth-token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
	}
	
	@Bean
	public SquareClient squareStacksMateClient() {
		com.squareup.square.Environment sqenv = com.squareup.square.Environment.PRODUCTION;
		if (environment.getProperty("SQUARE_SM_APPLICATION_ID").indexOf("sandbox") > -1) {
			sqenv = com.squareup.square.Environment.SANDBOX;
		}
		logger.info("PAYMENTS-Square: SQUARE_SM_ACCESS_TOKEN" + environment.getProperty("SQUARE_SM_APPLICATION_ID"));
		logger.info("PAYMENTS-Square: SQUARE_SM_ACCESS_TOKEN");
		SquareClient client = new SquareClient.Builder()
			    .environment(sqenv)
			    .accessToken(environment.getProperty("SQUARE_SM_ACCESS_TOKEN"))
			    .build();
		return client;
	}

	@Bean
	public SquareClient squareClient() {
		com.squareup.square.Environment sqenv = com.squareup.square.Environment.PRODUCTION;
		if (environment.getProperty("SQUARE_APPLICATION_ID").indexOf("sandbox") > -1) {
			sqenv = com.squareup.square.Environment.SANDBOX;
		}
		logger.info("PAYMENTS-Square: SQUARE_ACCESS_TOKEN");
		logger.info("PAYMENTS-Square: SQUARE_ACCESS_TOKEN");
		SquareClient client = new SquareClient.Builder()
			    .environment(sqenv)
			    .accessToken(environment.getProperty("SQUARE_ACCESS_TOKEN"))
			    .build();
		return client;
	}

	@Bean
	public RestOperations restTemplate() {
		return createRestTemplate();
	}

	@Bean
	public GaiaHubReader gaiaHubReader() {
		return new GaiaHubReader();
	}

	@Bean
	public ContractReader contractReader() {
		return new ContractReader();
	}

	@Bean
	public AppMapContract contractData() {
		return new AppMapContract();
	}

	@Bean
	public ClaritySerialiser claritySerialiser() {
		return new ClaritySerialiser();
	}

	@Bean
	public ClarityDeserialiser clarityDeserialiser() {
		return new ClarityDeserialiser();
	}

	public static RestTemplate createRestTemplate() {
		RestTemplate template = new RestTemplate();
		template.getMessageConverters().add(new StringHttpMessageConverter());
		return template;
	}

	@Bean
	public ObjectMapper mapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
		return mapper;
	}
	
    @Bean
    MappingJackson2HttpMessageConverter customizedJacksonMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(mapper());
        converter.setSupportedMediaTypes(
                Arrays.asList(
                        MediaType.APPLICATION_JSON,
                        new MediaType("application", "*+json"),
                        MediaType.APPLICATION_OCTET_STREAM));
        return converter;
    }
}
