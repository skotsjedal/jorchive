package no.skotsj.jorchive.web.util;

import com.google.common.collect.Maps;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.TreeMap;

/**
 * Helper methods for HTML styling
 *
 * @author Skotsj on 29.12.2014.
 */
public class StyleHelper
{

    private static TreeMap<Integer, String> colors = Maps.newTreeMap();

    public static final String BLACK = "#000000";
    public static final String DARK_GREEN = "#4C9900";
    public static final String ORANGE = "#FF8000";
    public static final String DARK_ORANGE = "#CC6600";
    public static final String BRIGHT_RED = "#FF3333";
    public static final String RED = "#FF0000";
    public static final String DARK_RED = "#CC0000";


    public static final String FOLDER_OPEN = "fa-folder-open";
    public static final String FILM = "fa-film";
    public static final String MUSIC = "fa-music";
    public static final String ARCHIVE = "fa-archive";
    public static final String BOOK = "fa-book";
    public static final String PICTURE = "fa-picture";

    static
    {
        colors.put(0, BLACK);
        colors.put(45, DARK_GREEN);
        colors.put(95, ORANGE);
        colors.put(195, DARK_ORANGE);
        colors.put(495, BRIGHT_RED);
        colors.put(950, RED);
        colors.put(1950, DARK_RED);
    }

    public static String humanReadableByteCount(long bytes)
    {
        final int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format(Locale.ENGLISH, "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String colorForSize(long dataSize)
    {
        int mb = (int) (dataSize / (1024 * 1024));
        return colors.floorEntry(mb).getValue();
    }

    public static long findSize(Path path)
    {
        try
        {
            return Files.size(path);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String icon(String ext)
    {
        switch (ext)
        {
            case "mkv":
            case "avi":
            case "mp4":
                return FILM;
            case "flac":
            case "mp3":
                return MUSIC;
            case "rar":
                return ARCHIVE;
            case "epub":
            case "mobi":
                return BOOK;
            case "jpg":
            case "jpeg":
            case "gif":
            case "png":
                return PICTURE;
        }
        return "";
    }
}
