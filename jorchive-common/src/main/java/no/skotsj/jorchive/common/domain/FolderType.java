package no.skotsj.jorchive.common.domain;

/**
 * FolderType
 */
public enum FolderType
{
    DOWN("Download"),
    TV("TV"),
    ANIME("Anime"),
    MOVIE("Movies"),
    MOVIE_ARCHIVE("Movie Archive"),
    TEMP("Temporary");

    private final String value;

    private FolderType(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
