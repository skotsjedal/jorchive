package no.skotsj.jorchive.camel;

import no.skotsj.jorchive.service.support.FileWatcher;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Rest route
 * Created by skotsj on 06.06.2015.
 */
@Component
public class RestRoute extends SpringRouteBuilder
{

    public static final String CAMEL_SERVLET = "CamelServlet";

    @Autowired
    private FileWatcher fileWatcher;

    @Override
    public void configure() throws Exception
    {
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true");

        rest("/status")

                .get("/{id}").outType(FileWatcher.ProgressInstance.class).route()
                .process(exchange -> {
                    String id = exchange.getIn().getHeader("id", String.class);
                    exchange.getIn().setBody(fileWatcher.getInstance(id));
                })
                .endRest();
    }
}
