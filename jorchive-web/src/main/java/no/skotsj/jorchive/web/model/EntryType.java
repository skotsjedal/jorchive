package no.skotsj.jorchive.web.model;

/**
 * @author Skotsj on 31.12.2014.
 */
public enum EntryType
{
    DIR("Directory"), FILE("File"), ARCHIVE_ENTRY("Archived");

    private final String value;

    private EntryType(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
