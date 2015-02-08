package no.skotsj.jorchive.web.vaadin;

import com.ejt.vaadin.loginform.DefaultHorizontalLoginForm;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Notification;
import no.skotsj.jorchive.common.prop.SecuritySettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.navigator.annotation.VaadinView;

/**
 * Login view to limit access
 */
@VaadinView(name = "")
public class LoginView extends DefaultHorizontalLoginForm implements View
{
    private boolean authed = false;

    @Autowired
    private SecuritySettings securitySettings;

    @Override
    protected void login(String userName, String password)
    {
        if (securitySettings.getUser().equals(userName)
                && securitySettings.getPwd().equals(password))
        {
            authed = true;
            getUI().getNavigator().navigateTo(MainUi.MAINVIEW);
        }
        else
        {
            Notification.show("Invalid usename/password", Notification.Type.ERROR_MESSAGE);
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event)
    {
    }

    public boolean isAuthed()
    {
        return authed;
    }
}
