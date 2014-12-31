package no.skotsj.jorchive.web.controller;

import no.skotsj.jorchive.service.ArchiveService;
import no.skotsj.jorchive.web.model.FileList;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;

/**
 * Default Controller
 *
 * @author Skotsj on 26.12.2014.
 */
@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class HomeController {

    private FileList fileList;
    private DateTime cachedTime;

    @Autowired
    private ArchiveService archiveService;

    @ModelAttribute("fileList")
    public FileList fileList() {
        checkRefresh();
        return fileList;
    }

    private void checkRefresh() {
        Duration age = new Duration(cachedTime, DateTime.now());
        if (fileList == null || age.isLongerThan(new Duration(60000L))) {
            fileList = new FileList(archiveService.listCompleted());
            cachedTime = DateTime.now();
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getUsersView() {
        return "MainContent";
    }

    /**
     * Rest
     */
    @RequestMapping(value = "/hei", method = RequestMethod.GET)
    public
    @ResponseBody
    String getUsersRest() {
        return "hei";
    }
}
