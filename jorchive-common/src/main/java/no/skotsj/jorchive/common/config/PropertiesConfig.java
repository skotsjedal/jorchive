package no.skotsj.jorchive.common.config;

import no.skotsj.jorchive.common.prop.DirectorySettings;
import no.skotsj.jorchive.common.prop.SecuritySettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

/**
 * Properties
 * <p>
 * Created by Skotsj on 28.12.2014.
 */
@Configuration
@EnableConfigurationProperties
public class PropertiesConfig
{

    @Bean
    public DirectorySettings directorySettings()
    {
        return new DirectorySettings();
    }

    @Bean
    public SecuritySettings securitySettings()
    {
        return new SecuritySettings();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer()
    {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setProperties(yamlPropertiesFactoryBean().getObject());
        return configurer;
    }

    @Bean
    public static YamlPropertiesFactoryBean yamlPropertiesFactoryBean()
    {
        YamlPropertiesFactoryBean bean = new YamlPropertiesFactoryBean();
        bean.setResources(new ClassPathResource("config/application.yml"));
        return bean;
    }

}
