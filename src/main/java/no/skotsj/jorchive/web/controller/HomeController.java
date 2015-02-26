package no.skotsj.jorchive.web.controller;

import no.skotsj.jorchive.web.model.Category;
import no.skotsj.jorchive.web.model.code.CategoryType;
import no.skotsj.jorchive.web.model.code.FilterType;
import no.skotsj.jorchive.service.ArchiveService;
import no.skotsj.jorchive.web.model.Categories;
import no.skotsj.jorchive.web.model.FileInfo;
import no.skotsj.jorchive.web.model.FileList;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.beans.factory.InitializingBean;
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
public class HomeController implements InitializingBean
{

    private static final Duration CACHE_DURATION = new Duration(60L * 1000L);

    private FilterType filter = FilterType.ALL;
    private FileList fileList;
    private DateTime cachedTime;

    @Autowired
    private ArchiveService archiveService;
    @Autowired
    private Categories categories;
    private Category activeCategory;

    @Override
    public void afterPropertiesSet() throws Exception
    {
        activeCategory = categories.get(0);
    }

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
        if (fileList == null || age.isLongerThan(CACHE_DURATION))
        {
            fileList = new FileList(activeCategory);
            fileList.filter(filter);
            cachedTime = DateTime.now();
        }
    }

    @RequestMapping(value = "/category", method = RequestMethod.GET)
    @ResponseBody
    public List<Category> category()
    {
        return categories.getCategories();
    }

    @RequestMapping(value = "/category", method = RequestMethod.POST)
    @ResponseBody
    public void category(@RequestBody String category)
    {
        activeCategory = categories.getCategories().stream()
                .filter(c -> c.getName().equals(category)).findFirst().get();
        fileList = null;
    }

    @RequestMapping(value = "/categoryTypes", method = RequestMethod.GET)
    @ResponseBody
    public CategoryType[] categoryTypes()
    {
        return CategoryType.values();
    }

    @RequestMapping(value = "/filter", method = RequestMethod.GET)
    @ResponseBody
    public FilterType[] filters()
    {
        return FilterType.values();
    }

    @RequestMapping(value = "/filter", method = RequestMethod.POST)
    @ResponseBody
    public void filter(@RequestBody String filterType)
    {
        filter = FilterType.valueOf(filterType);
        fileList.filter(filter);
    }

    @RequestMapping(value = "/process/{id}", method = RequestMethod.POST)
    @ResponseBody
    public void process(@PathVariable("id") String id, @RequestBody String categoryName)
    {
        archiveService.extract(id + " " + categoryName);
    }

    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.HEAD})
    public String index()
    {
        return "index";
    }
}
