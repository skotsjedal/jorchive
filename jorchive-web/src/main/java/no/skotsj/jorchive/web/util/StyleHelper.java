package no.skotsj.jorchive.web.util;

import com.google.common.collect.Maps;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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


    public static final String FOLDER_OPEN = "glyphicon-folder-open";
    public static final String FILM = "glyphicon-film";
    public static final String MUSIC = "glyphicon-music";
    public static final String ARCHIVE = "glyphicon-compressed";
    public static final String BOOK = "glyphicon-book";
    public static final String PICTURE = "glyphicon-picture";

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

    public static String fileSizeWithHtmlColor(long dataSize)
    {
        String displaySize = humanReadableByteCount(dataSize);
        displaySize = "<span style='color:" + getColor(dataSize) + "'>" + displaySize + "</span>";
        return displaySize;
    }

    private static String humanReadableByteCount(long bytes)
    {
        final int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    private static String getColor(long dataSize)
    {
        int mb = (int) (dataSize / (1024 * 1024));
        return colors.floorEntry(mb).getValue();
    }

    public static String fileSizeWithHtmlColor(Path path)
    {
        if (Files.isDirectory(path))
        {
            return "";
        }
        return fileSizeWithHtmlColor(findSize(path));
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

    public static String icon(String iconName)
    {
        return "<span class='glyphicon " + iconName + "'></span>&nbsp;&nbsp;";
    }

    public static String addIcon(String ext, String name)
    {
        switch (ext)
        {
            case "mkv":
            case "avi":
            case "mp4":
                return icon(FILM) + name;
            case "flac":
            case "mp3":
                return icon(MUSIC) + name;
            case "rar":
                return icon(ARCHIVE) + name;
            case "epub":
            case "mobi":
                return icon(BOOK) + name;
            case "jpg":
            case "jpeg":
            case "gif":
            case "png":
                return icon(PICTURE) + name;
        }
        return name;
    }
}
