package no.skotsj.jorchive.web.config;

import no.skotsj.jorchive.web.config.prop.DirectorySettings;
import no.skotsj.jorchive.web.config.prop.SecuritySettings;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

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
        FileSystemResource fileSystemResource = new FileSystemResource("/data/jorchive/application.yml");
        if (fileSystemResource.exists())
        {
            bean.setResources(fileSystemResource);
        } else
        {
            bean.setResources(new ClassPathResource("config/application.yml"));
        }
        return bean;
    }

}
