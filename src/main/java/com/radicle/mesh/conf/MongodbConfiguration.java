package com.radicle.mesh.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.radicle.mesh.ApplicationSettings;

@EnableMongoRepositories(basePackages = "com.radicle.mesh")

@Configuration
public class MongodbConfiguration extends AbstractMongoClientConfiguration {

	@Autowired private ApplicationSettings applicationSettings;

	@Override
	protected String getDatabaseName() {
		return applicationSettings.getMongoDbName();
	}

	@Override
	protected String getMappingBasePackage() {
		return "com.radicle";
	}

    @Override
    public MongoClient mongoClient() {
        return MongoClients.create(applicationSettings.getMongoIp() + ":" + applicationSettings.getMongoPort());
    }

}
