package no.skotsj.jorchive.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.skotsj.jorchive.common.util.FileUtils;
import no.skotsj.jorchive.web.model.code.CategoryType;
import no.skotsj.jorchive.web.model.code.MediaType;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * File Category
 * Created by Skotsj on 24.02.2015.
 */
public class Category
{
    private static final Duration CACHE_DURATION = new Duration(10L * 1000L);

    private String name;
    private CategoryType type;
    private String faIcon;
    private Path path;
    private MediaType mediaType;

    private List<String> files;
    private DateTime cachedTime;

    public Category(String faIcon, String name, CategoryType type, String path)
    {
        this.name = name;
        this.type = type;
        this.faIcon = faIcon;
        this.path = Paths.get(path);
    }

    public Category(String faIcon, String name, CategoryType categoryType, String path, MediaType mediaType)
    {
        this(faIcon, name, categoryType, path);
        this.mediaType = mediaType;
    }

    public String getName()
    {
        return name;
    }

    public CategoryType getType()
    {
        return type;
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

    @JsonIgnore
    public List<Category> getToCategories()
    {
        CategoryType toType = CategoryType.values()[(type.ordinal() + 1) % CategoryType.values().length];
        return Categories.getInstance().getCategories().stream().filter(c -> c.type == toType).collect(
                Collectors.toList());
    }

}
