package no.skotsj.jorchive.web.vaadin;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.VaadinUI;

@VaadinUI
@Title("Jorchive")
@Theme("jorchive")
@Widgetset("jorchive.AppWidgetSet")
public class MainUi extends UI
{
    public static final String MAINVIEW = "main";
    private Navigator navigator;

    @Autowired
    private LoginView loginView;
    @Autowired
    private MainView mainView;

    @Override
    protected void init(VaadinRequest request)
    {
        navigator = new Navigator(this, this);
        navigator.addView("", loginView);
        navigator.addView(MAINVIEW, mainView);
        if (loginView.isAuthed())
        {
            navigator.navigateTo(MAINVIEW);
        }
    }

}
