package no.skotsj.jorchive.web.controller;

import com.google.common.collect.Maps;
import no.skotsj.jorchive.common.domain.FilterType;
import no.skotsj.jorchive.service.ArchiveService;
import no.skotsj.jorchive.web.model.FileList;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

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

    @ModelAttribute("fileList")
    public FileList fileList()
    {
        checkRefresh();
        return fileList;
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

    @ModelAttribute("filters")
    public Map<String, String> filters() {
        Map<String, String> map = Maps.newLinkedHashMap();
        map.put(FilterType.ALL.toString(), "All");
        map.put(FilterType.FILE.toString(), "Files");
        map.put(FilterType.ARCHIVE_ENTRY.toString(), "Archived Content");
        return map;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getUsersView()
    {
        return "MainContent";
    }

    @RequestMapping(value = "/filter/{filterType}", method = RequestMethod.GET)
    public
    @ResponseBody
    String filterAllRest(@PathVariable("filterType") String filterType)
    {
        filter(FilterType.valueOf(filterType));
        return "ok";
    }

    @RequestMapping(value = "/extract/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    String extract(@PathVariable("id") String id)
    {
        //fileList.get(id).extract(archiveService.getOutPath());
        return "ok";
    }

    private void filter(FilterType filter)
    {
        this.filter = filter;
        fileList.filter(filter);
    }
}
