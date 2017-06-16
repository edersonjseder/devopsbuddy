package com.devopsbuddy.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuration valid to all profiles.
 * Created by root on 10/06/17.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.devopsbuddy.backend.persistence.repositories") // Scan all JPA repositories
@EntityScan(basePackages = "com.devopsbuddy.backend.persistence.domain.backend") // Scan all JPA entities
@EnableTransactionManagement // This enables annotation based transaction management (JPA transaction is managed by default)
/** @PropertySource Gets the webmaster user credentials created from the .properties file in home directory */
@PropertySource(value = "file:///${user.home}/.devopsbuddy/application-common.properties", ignoreResourceNotFound = true)
public class ApplicationConfig {

    @Value("${aws.s3.profile}")
    private String awsProfile;

    @Value("${aws.s3.credentials.access.key}")
    private String accessKey;

    @Value("${aws.s3.credentials.secret.key}")
    private String secretKey;

    @Value("${aws.s3.region}")
    private String region;

    @Bean
    public BasicAWSCredentials basicAWSCredentials() {
        return new BasicAWSCredentials(accessKey, secretKey);
    }

    @Bean
    public AmazonS3Client s3Client(AWSCredentials credentials) {

//        AWSCredentials credentials = new ProfileCredentialsProvider(awsProfile).getCredentials();
//        AmazonS3Client s3Client = new AmazonS3Client(credentials);
//        Region region = Region.getRegion(Regions.SA_EAST_1);
//        s3Client.setRegion(region);

        AmazonS3Client s3Client = new AmazonS3Client(credentials);
        s3Client.setRegion(Region.getRegion(Regions.fromName(region)));

        return s3Client;
    }
}
