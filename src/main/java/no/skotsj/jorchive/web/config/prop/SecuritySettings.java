package no.skotsj.jorchive.web.config.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Security props
 * <p>
 * Created by Skotsj on 31.01.2015.
 */
@Component
@ConfigurationProperties(prefix = "jorchive.security", ignoreUnknownFields = false)
public class SecuritySettings
{

    private String user;
    private String pwd;

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getPwd()
    {
        return pwd;
    }

    public void setPwd(String pwd)
    {
        this.pwd = pwd;
    }
}
