package io.github.scrapery.scheduler;

import io.github.scrapery.scheduler.config.ApplicationProperties;
import io.github.scrapery.scheduler.config.DefaultProfileUtil;

import io.github.simlife.config.SimlifeConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collection;

@SpringBootApplication
@EnableConfigurationProperties({LiquibaseProperties.class, ApplicationProperties.class})
@EnableDiscoveryClient
public class ScraperSchedulerApp {

    private static final Logger log = LoggerFactory.getLogger(ScraperSchedulerApp.class);

    private final Environment env;

    public ScraperSchedulerApp(Environment env) {
        this.env = env;
    }

    /**
     * Initializes ScraperScheduler.
     * <p>
     * Spring profiles can be configured with a program arguments --spring.profiles.active=your-active-profile
     * <p>
     * You can find more information on how profiles work with Simlife on <a href="https://www.simlife.io/profiles/">https://www.simlife.io/profiles/</a>.
     */
    @PostConstruct
    public void initApplication() {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains(SimlifeConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(SimlifeConstants.SPRING_PROFILE_PRODUCTION)) {
            log.error("You have misconfigured your application! It should not run " +
                "with both the 'dev' and 'prod' profiles at the same time.");
        }
        if (activeProfiles.contains(SimlifeConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(SimlifeConstants.SPRING_PROFILE_CLOUD)) {
            log.error("You have misconfigured your application! It should not " +
                "run with both the 'dev' and 'cloud' profiles at the same time.");
        }
    }

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ScraperSchedulerApp.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        log.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\t{}://localhost:{}\n\t" +
                "External: \t{}://{}:{}\n\t" +
                "Profile(s): \t{}\n----------------------------------------------------------",
            env.getProperty("spring.application.name"),
            protocol,
            env.getProperty("server.port"),
            protocol,
            hostAddress,
            env.getProperty("server.port"),
            env.getActiveProfiles());

        String configServerStatus = env.getProperty("configserver.status");
        log.info("\n----------------------------------------------------------\n\t" +
                "Config Server: \t{}\n----------------------------------------------------------",
            configServerStatus == null ? "Not found or not setup for this application" : configServerStatus);
    }
}
