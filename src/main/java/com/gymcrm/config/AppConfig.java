package com.gymcrm.config;

import com.gymcrm.facade.GymFacade;
import com.gymcrm.service.TraineeService;
import com.gymcrm.service.TrainerService;
import com.gymcrm.service.TrainingService;
import com.gymcrm.storage.InMemoryStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan(basePackages = "com.gymcrm")
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public InMemoryStorage inMemoryStorage() {
        return new InMemoryStorage();
    }

    @Bean
    public GymFacade gymFacade(TraineeService traineeService,
                               TrainerService trainerService,
                               TrainingService trainingService) {
        return new GymFacade(traineeService, trainerService, trainingService);
    }

}
