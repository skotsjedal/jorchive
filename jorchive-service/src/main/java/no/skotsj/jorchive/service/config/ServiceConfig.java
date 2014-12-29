package no.skotsj.jorchive.service.config;

import no.skotsj.jorchive.common.config.PropertiesConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Service Config
 * Created by Skotsj on 28.12.2014.
 */
@Configuration
@ComponentScan("no.skotsj.jorchive.service")
@Import(PropertiesConfig.class)
public class ServiceConfig {
}
