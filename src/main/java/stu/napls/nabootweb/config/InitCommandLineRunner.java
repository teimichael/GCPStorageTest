package stu.napls.nabootweb.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @Author Tei Michael
 * @Date 1/13/2020
 */
@Component
public class InitCommandLineRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitCommandLineRunner.class);

    @Override
    public void run(String... args) throws Exception {
        logger.info("Welcome to use NABootWeb.");
    }
}
