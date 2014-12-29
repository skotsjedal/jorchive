package no.skotsj.jorchive.common.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Directory props
 * <p>
 * Created by Skotsj on 28.12.2014.
 */
@Component
@ConfigurationProperties(prefix = "jorchive.dir", ignoreUnknownFields = false)
public class DirectorySettings {

    private String completed;
    private String temp;

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}
