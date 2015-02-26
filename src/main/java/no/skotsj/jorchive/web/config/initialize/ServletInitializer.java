package no.skotsj.jorchive.web.config.initialize;

import no.skotsj.jorchive.web.config.MvcConfig;
import no.skotsj.jorchive.web.config.RootConfig;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Servlet Initializer
 * <p>
 * Created by Skotsj on 25.12.2014.
 */

public class ServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer
{

    @Override
    protected String[] getServletMappings()
    {
        return new String[]{"/"};
    }

    @Override
    protected Class<?>[] getRootConfigClasses()
    {
        return new Class[]{RootConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses()
    {
        return new Class[]{MvcConfig.class};
    }
}
