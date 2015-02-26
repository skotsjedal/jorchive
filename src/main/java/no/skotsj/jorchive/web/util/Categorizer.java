package no.skotsj.jorchive.web.util;

import no.skotsj.jorchive.web.model.code.MediaType;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

/**
 * Stupid categorizer, going by a few simple rules
 *
 * @author Skotsj on 26.02.2015.
 */
public class Categorizer
{

    private static final Pattern anime = Pattern.compile("^\\[.+\\].+");
    private static final Pattern tv = Pattern.compile(".+s?\\d{1,2}[ex]\\d{1,2}.+", CASE_INSENSITIVE);

    public static MediaType categorize(String filename)
    {
        if (anime.matcher(filename).matches())
        {
            return MediaType.ANIME;
        }
        if (tv.matcher(filename).matches())
        {
            return MediaType.TV;
        }

        return MediaType.MOVIE;
    }
}
