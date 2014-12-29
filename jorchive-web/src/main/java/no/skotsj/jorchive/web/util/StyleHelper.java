package no.skotsj.jorchive.web.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Skotsj on 29.12.2014.
 */
public class StyleHelper {

    public static final String WHITE = "#000000";
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

    public static String fileSizeWithHtmlColor(long dataSize) {
        String displaySize = humanReadableByteCount(dataSize);
        displaySize = "<span style='color:" + getColor(dataSize) + "'>" + displaySize + "</span>";
        return displaySize;
    }

    private static String humanReadableByteCount(long bytes) {
        final int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    private static String getColor(long dataSize) {
        dataSize /= 1024 * 1024;

        if (dataSize == 0) {
            return WHITE;
        }
        if (dataSize <= 45) {
            return DARK_GREEN;
        }
        if (dataSize <= 95) {
            return ORANGE;
        }
        if (dataSize <= 495) {
            return DARK_ORANGE;
        }
        if (dataSize <= 950) {
            return BRIGHT_RED;
        }
        if (dataSize <= 1950) {
            return RED;
        }
        return DARK_RED;
    }

    public static String fileSizeWithHtmlColor(Path path) {
        if (Files.isDirectory(path)) {
            return "";
        }
        try {
            return fileSizeWithHtmlColor(Files.size(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String icon(String iconName) {
        return "<span class='glyphicon " + iconName + "'></span>&nbsp;&nbsp;";
    }

    public static String addIcon(String ext, String name) {
        switch (ext) {
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
