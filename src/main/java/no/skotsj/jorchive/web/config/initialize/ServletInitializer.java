package no.skotsj.jorchive.web.config.initialize;

import no.skotsj.jorchive.camel.RestRoute;
import no.skotsj.jorchive.web.config.MvcConfig;
import no.skotsj.jorchive.web.config.RootConfig;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * Servlet Initializer
 * <p/>
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

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException
    {
        super.onStartup(servletContext);
        ServletRegistration.Dynamic servlet = servletContext.addServlet(RestRoute.CAMEL_SERVLET, new CamelHttpTransportServlet());
        servlet.setLoadOnStartup(1);
        servlet.addMapping("/rest/*");
    }
}
