package no.skotsj.jorchive.web.controller;

import no.skotsj.jorchive.common.domain.FilterType;
import no.skotsj.jorchive.service.ArchiveService;
import no.skotsj.jorchive.web.model.FileInfo;
import no.skotsj.jorchive.web.model.FileList;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

/**
 * Default Controller
 *
 * @author Skotsj on 26.12.2014.
 */
@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class HomeController
{

    private static final long CACHE_DURATION = 60L;

    private FilterType filter = FilterType.ALL;
    private FileList fileList;
    private DateTime cachedTime;

    @Autowired
    private ArchiveService archiveService;

    @RequestMapping(value = "/files", method = RequestMethod.GET)
    @ResponseBody
    public List<FileInfo> fileList()
    {
        checkRefresh();
        return fileList.getFiles();
    }

    private void checkRefresh()
    {
        Duration age = new Duration(cachedTime, DateTime.now());
        if (fileList == null || age.isLongerThan(new Duration(CACHE_DURATION * 1000L)))
        {
            fileList = new FileList(archiveService.listDownloaded());
            fileList.filter(filter);
            cachedTime = DateTime.now();
        }
    }

    @RequestMapping(value = "/filters", method = RequestMethod.GET)
    @ResponseBody
    public FilterType[] filters()
    {
        return FilterType.values();
    }

    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.HEAD})
    public String index()
    {
        return "index";
    }

    @RequestMapping(value = "/filter", method = RequestMethod.POST)
    @ResponseBody
    public void filterAllRest(@RequestBody String filterType)
    {
        filter = FilterType.valueOf(filterType);
        fileList.filter(filter);
    }

    @RequestMapping(value = "/extract/{id}", method = RequestMethod.GET)
    @ResponseBody
    public void extract(@PathVariable("id") String id)
    {
        archiveService.extract(id);
    }
}
