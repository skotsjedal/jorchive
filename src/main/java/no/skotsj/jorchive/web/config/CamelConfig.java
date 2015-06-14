package no.skotsj.jorchive.web.config;

import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Camel config
 * Created by skotsj on 06.06.2015.
 */
@ComponentScan("no.skotsj.jorchive.camel")
public class CamelConfig extends CamelConfiguration
{
}
