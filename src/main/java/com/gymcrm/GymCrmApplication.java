package com.gymcrm;

import com.gymcrm.config.AppConfig;
import com.gymcrm.facade.GymFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class GymCrmApplication {
    private static final Logger logger = LoggerFactory.getLogger(GymCrmApplication.class);

    public static void main(String[] args) {
        logger.info("Starting Gym CRM Application...");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        GymFacade facade = context.getBean(GymFacade.class);

        context.close();
    }
}

