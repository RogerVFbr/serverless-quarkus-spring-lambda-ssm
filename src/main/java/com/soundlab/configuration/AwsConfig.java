package com.soundlab.configuration;

import com.soundlab.utils.DurationMetric;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;

@Configuration
public class AwsConfig {

    private String region;

    public AwsConfig(@Value("${aws.service.region}") String region) {
        this.region = region;
    }

    @Bean
    public SsmClient getSsmClient() {
        DurationMetric duration = new DurationMetric();
        SsmClient client = SsmClient.builder()
            .region(Region.of(region))
            .httpClient(UrlConnectionHttpClient.builder().build())
            .build();
        duration.measure("AWS SSM client initialization completed in");
        return client;
    }
}
