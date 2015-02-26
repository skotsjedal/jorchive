package no.skotsj.jorchive.web.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Skotsj on 31.12.2014.
 */
public class CommonUtils
{
    public static final String RAR_SEPARATOR = "//";

    public static LinkedList<String> createHash(String relativePath)
    {
        final StringBuilder pathBuilder = new StringBuilder();
        List<String> hashes = Splitter.on(Pattern.compile("[" + RAR_SEPARATOR + Pattern.quote(File.separator) + "]"))
                .splitToList(relativePath).stream()
                .map(s -> {
                    pathBuilder.append(s);
                    return pathBuilder.toString();
                })
                .map(s -> Integer.toHexString(s.hashCode()))
                .collect(Collectors.toList());
        return Lists.newLinkedList(hashes);
    }
}
