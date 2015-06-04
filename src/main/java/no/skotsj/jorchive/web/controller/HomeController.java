package no.skotsj.jorchive.web.controller;

import no.skotsj.jorchive.service.FileService;
import no.skotsj.jorchive.web.model.Categories;
import no.skotsj.jorchive.web.model.Category;
import no.skotsj.jorchive.web.model.FileInfo;
import no.skotsj.jorchive.web.model.FileList;
import no.skotsj.jorchive.web.model.code.FilterType;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static no.skotsj.jorchive.web.model.code.EntryType.ARCHIVE_ENTRY;

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
    private FileService fileService;
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
        return getFileList().getFiles();
    }

    private FileList getFileList()
    {
        Duration age = new Duration(cachedTime, DateTime.now());
        if (fileList == null || age.isLongerThan(CACHE_DURATION))
        {
            fileList = new FileList(activeCategory);
            fileList.filter(filter);
            cachedTime = DateTime.now();
        }
        return fileList;
    }

    @RequestMapping(value = "/category", method = RequestMethod.GET)
    @ResponseBody
    public List<Category> category()
    {
        return categories.getCategories();
    }

    @RequestMapping(value = "/category/{id}", method = RequestMethod.POST)
    @ResponseBody
    public void category(@PathVariable("id") String category)
    {
        activeCategory = categories.getCategories().stream()
                .filter(c -> c.getName().equals(category)).findFirst().get();
        fileList = null;
    }

    @RequestMapping(value = "/filter", method = RequestMethod.GET)
    @ResponseBody
    public FilterType[] filters()
    {
        return FilterType.values();
    }

    @RequestMapping(value = "/filter/{id}", method = RequestMethod.POST)
    @ResponseBody
    public void filter(@PathVariable("id") String filterType)
    {
        filter = FilterType.valueOf(filterType);
        getFileList().filter(filter);
    }

    @RequestMapping(value = "/process/{id}", method = RequestMethod.POST)
    @ResponseBody
    public void process(@PathVariable("id") String id, @RequestBody String categoryName)
    {
        Category targetCategory = category().stream()
                .filter(c -> c.getName().equals(categoryName)).findFirst().get();
        FileInfo fileInfo = getFileList().get(id);

        if (fileInfo.getEntryType() == ARCHIVE_ENTRY)
        {
            fileService.extract(fileInfo, targetCategory.getPath());
        } else
        {
            fileService.copy(fileInfo, targetCategory.getPath());
        }
    }

    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.HEAD})
    public String index()
    {
        return "index";
    }
}
