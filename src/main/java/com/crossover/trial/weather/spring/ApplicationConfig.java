package com.crossover.trial.weather.spring;

import com.crossover.trial.weather.server.WeatherServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Created by sony on 10/23/2016.
 */
@Configuration
@PropertySource(value = "classpath:application.properties")
public class ApplicationConfig {

    @Bean(initMethod = "init")
    public WeatherServer weatherServer() {
        return new WeatherServer();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
