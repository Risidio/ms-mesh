package com.radicle.mesh;

import java.util.Arrays;

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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.radicle.mesh.stacks.model.stxbuffer.ClarityDeserialiser;
import com.radicle.mesh.stacks.model.stxbuffer.ClaritySerialiser;
import com.radicle.mesh.stacks.model.stxbuffer.ContractReader;
import com.radicle.mesh.stacks.model.stxbuffer.GaiaHubReader;
import com.radicle.mesh.stacks.model.stxbuffer.types.AppMapContract;
import com.squareup.square.SquareClient;

@SpringBootApplication
@ComponentScan("com.radicle")
public class MeshApplication {

	@Autowired private Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(MeshApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*");
			}
		};
	}

	@Bean
	public SquareClient squareClient() {
		com.squareup.square.Environment sqenv = com.squareup.square.Environment.PRODUCTION;
		if (environment.getProperty("SQUARE_APPLICATION_ID").indexOf("sandbox") > -1) {
			sqenv = com.squareup.square.Environment.SANDBOX;
		}
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
