package no.skotsj.jorchive.web.config;

import no.skotsj.jorchive.common.prop.SecuritySettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter
{

    public static final String ROLE_ADMIN = "ADMIN";

    @Autowired
    private SecuritySettings securitySettings;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception
    {
        auth.inMemoryAuthentication().withUser(securitySettings.getUser()).password(securitySettings.getPwd()).roles(ROLE_ADMIN);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http.addFilterAfter(new CsrfTokenGeneratorFilter(), CsrfFilter.class)
                .authorizeRequests()
                .antMatchers("/**").access("hasRole('ROLE_" + ROLE_ADMIN + "')")
                .and().formLogin();
    }

    private class CsrfTokenGeneratorFilter extends OncePerRequestFilter
    {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
        {
            CsrfToken token = (CsrfToken) request.getAttribute("_csrf");

            // Spring Security will allow the Token to be included in this header name
            response.setHeader("X-CSRF-HEADER", token.getHeaderName());

            // Spring Security will allow the token to be included in this parameter name
            response.setHeader("X-CSRF-PARAM", token.getParameterName());

            // this is the value of the token to be included as either a header or an HTTP parameter
            response.setHeader("X-CSRF-TOKEN", token.getToken());

            filterChain.doFilter(request, response);
        }
    }
}
