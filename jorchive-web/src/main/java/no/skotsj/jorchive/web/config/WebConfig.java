package no.skotsj.jorchive.web.config;

import no.skotsj.jorchive.web.vaadin.MainUi;
import no.skotsj.jorchive.web.vaadin.MainView;
import no.skotsj.jorchive.web.vaadin.LoginView;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;
import org.vaadin.spring.annotation.VaadinSessionScope;
import org.vaadin.spring.annotation.VaadinUIScope;

/**
 * Vaadin Web Config
 */
@Configuration
public class WebConfig
{
    @Bean
    @ConditionalOnMissingBean(RequestContextListener.class)
    public RequestContextListener requestContextListener()
    {
        return new RequestContextListener();
    }

    @Bean
    @VaadinUIScope
    public MainUi mainUi()
    {
        return new MainUi();
    }

    @Bean
    @VaadinSessionScope
    public MainView mainView()
    {
        return new MainView();
    }

    @Bean
    @VaadinSessionScope
    public LoginView loginView()
    {
        return new LoginView();
    }
}
