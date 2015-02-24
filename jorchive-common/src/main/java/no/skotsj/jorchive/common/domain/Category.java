package no.skotsj.jorchive.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * File Category
 * Created by Skotsj on 24.02.2015.
 */
public class Category
{
    private String name;
    private String faIcon;
    @JsonIgnore
    private Path path;

    public Category(String faIcon, String name, String path)
    {
        this.faIcon = faIcon;
        this.name = name;
        this.path = Paths.get(path);
    }

    public String getName()
    {
        return name;
    }

    public String getFaIcon()
    {
        return faIcon;
    }

    public Path getPath()
    {
        return path;
    }

}
