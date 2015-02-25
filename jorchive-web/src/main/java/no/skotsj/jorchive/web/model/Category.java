package no.skotsj.jorchive.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.skotsj.jorchive.common.util.FileUtils;

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
    private String name;
    private CategoryType type;
    private String faIcon;
    private Path path;

    public Category(String faIcon, String name, CategoryType type, String path)
    {
        this.name = name;
        this.type = type;
        this.faIcon = faIcon;
        this.path = Paths.get(path);
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

    @JsonIgnore
    public List<String> getFiles()
    {
        return FileUtils.listDir(path).stream().map(p -> p.getFileName().toString()).collect(Collectors.toList());
    }

    @JsonIgnore
    public List<Category> getToCategories()
    {
        CategoryType toType = CategoryType.values()[(type.ordinal() + 1) % CategoryType.values().length];
        return Categories.getInstance().getCategories().stream().filter(c -> c.type == toType).collect(
                Collectors.toList());
    }

}
