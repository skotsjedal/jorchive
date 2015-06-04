package no.skotsj.jorchive.service;

import no.skotsj.jorchive.service.support.FileWatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Spring config for Service
 * Created by Skotsj on 04.06.2015.
 */
@Configuration
@EnableAsync
public class ServiceConfig
{
    @Bean
    public FileWatcher fileWatcher()
    {
        return new FileWatcher();
    }
}
