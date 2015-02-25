package no.skotsj.jorchive.web.model;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author Skotsj on 31.12.2014.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum FilterType
{
    ALL("All"),
    FILE("Files"),
    ARCHIVE_ENTRY("Archived Content");

    private final String value;

    FilterType(String value)
    {
        this.value = value;
    }

    public String getName()
    {
        return name();
    }

    public String getValue()
    {
        return value;
    }
}
