package no.skotsj.jorchive.common.util;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * @author Skotsj on 29.12.2014.
 */
public class FileUtils
{
    public static List<Path> listDir(Path dir)
    {
        List<Path> entries = Lists.newArrayList();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir))
        {
            directoryStream.forEach(entries::add);
        } catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
        return entries;
    }
}
