package no.skotsj.jorchive.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import no.skotsj.jorchive.common.util.FileUtils;
import no.skotsj.jorchive.web.model.code.MediaType;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * File Category
 * Created by Skotsj on 24.02.2015.
 */
public class Category
{
    private static final Duration CACHE_DURATION = new Duration(10L * 1000L);

    private String name;
    private String faIcon;
    private Path path;
    private MediaType mediaType;

    private List<String> files;
    private DateTime cachedTime;
    private List<Category> feeds;

    public Category(String faIcon, String name, String path, MediaType mediaType)
    {
        this.feeds = Lists.newArrayList();
        this.name = name;
        this.faIcon = faIcon;
        this.path = Paths.get(path);
        this.mediaType = mediaType;
    }

    public String getName()
    {
        return name;
    }

    public String getFaIcon()
    {
        return faIcon;
    }

    @JsonIgnore
    public Path getPath()
    {
        return path;
    }

    public MediaType getMediaType()
    {
        return mediaType;
    }

    @JsonIgnore
    public List<String> getFiles()
    {
        Duration age = new Duration(cachedTime, DateTime.now());
        if (files == null || age.isLongerThan(CACHE_DURATION))
        {
            files = FileUtils.listDirNames(path);
            cachedTime = DateTime.now();
        }
        return files;
    }

    public void addFeed(Category category)
    {
        this.feeds.add(category);
    }

    @JsonIgnore
    public List<Category> getFeeds()
    {
        return feeds;
    }
}
