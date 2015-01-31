package no.skotsj.jorchive.web.config;

import no.skotsj.jorchive.service.config.ServiceConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Skotsj on 31.01.2015.
 */
@Configuration
@Import({ServiceConfig.class, SecurityConfig.class})
public class RootConfig
{
}
