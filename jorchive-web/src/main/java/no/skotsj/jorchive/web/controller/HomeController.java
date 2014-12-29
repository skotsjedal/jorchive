package no.skotsj.jorchive.web.controller;

import no.skotsj.jorchive.service.ArchiveService;
import no.skotsj.jorchive.web.model.FileList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Default Controller
 *
 * @author Skotsj on 26.12.2014.
 */
@Controller
@Scope("request")
public class HomeController {

    @Autowired
    private ArchiveService archiveService;

    @ModelAttribute("completed")
    public FileList completed() {
        FileList fileList = new FileList(archiveService.listCompleted());
        return fileList;
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
