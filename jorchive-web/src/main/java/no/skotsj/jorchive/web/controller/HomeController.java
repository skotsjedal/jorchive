package no.skotsj.jorchive.web.controller;

import com.google.common.collect.Lists;
import no.skotsj.jorchive.common.domain.Category;
import no.skotsj.jorchive.common.domain.FilterType;
import no.skotsj.jorchive.common.prop.DirectorySettings;
import no.skotsj.jorchive.service.ArchiveService;
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

    private static final long CACHE_DURATION = 60L;

    private FilterType filter = FilterType.ALL;
    private FileList fileList;
    private DateTime cachedTime;

    @Autowired
    private ArchiveService archiveService;
    @Autowired
    private DirectorySettings directorySettings;

    private List<Category> categories;
    private Category activeCategory;

    @Override
    public void afterPropertiesSet() throws Exception
    {
        categories = Lists.newArrayList(
                new Category("fa-download", "Download", directorySettings.getDownload()),
                new Category("fa-clock-o", "Temp", directorySettings.getTemp()),
                new Category("fa-film", "Movies", directorySettings.getMovie()),
                new Category("fa-database", "Movie Archive", directorySettings.getMovieArchive()),
                new Category("fa-play-circle", "Tv", directorySettings.getTv()),
                new Category("fa-star", "Anime", directorySettings.getAnime())
        );
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
        if (fileList == null || age.isLongerThan(new Duration(CACHE_DURATION * 1000L)))
        {
            fileList = new FileList(activeCategory);
            fileList.filter(filter);
            cachedTime = DateTime.now();
        }
    }

    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    @ResponseBody
    public List<Category> categories()
    {
        return categories;
    }

    @RequestMapping(value = "/category", method = RequestMethod.POST)
    @ResponseBody
    public void category(@RequestBody String category)
    {
        activeCategory = categories.stream().filter(c -> c.getName().equals(category)).findFirst().get();
        fileList = null;
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
