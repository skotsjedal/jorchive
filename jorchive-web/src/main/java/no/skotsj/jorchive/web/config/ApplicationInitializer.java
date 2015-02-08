package no.skotsj.jorchive.web.config;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.vaadin.spring.boot.VaadinAutoConfiguration;
import org.vaadin.spring.config.VaadinConfiguration;

/**
 * Application initializer
 */
public class ApplicationInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {

        return application.showBanner(false)
                .sources(RootConfig.class)
                .sources(VaadinAutoConfiguration.class, VaadinConfiguration.class)
                .sources(WebConfig.class);
    }
}
