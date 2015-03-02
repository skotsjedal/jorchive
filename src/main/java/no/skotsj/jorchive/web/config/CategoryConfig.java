package no.skotsj.jorchive.web.config;

import no.skotsj.jorchive.web.config.prop.DirectorySettings;
import no.skotsj.jorchive.web.model.Category;
import no.skotsj.jorchive.web.model.code.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Categories
 *
 * @author Skotsj on 02.03.2015.
 */
@Configuration
public class CategoryConfig
{
    @Autowired
    private DirectorySettings directorySettings;

    @Bean
    @Order(1)
    public Category down()
    {
        Category download = new Category("fa-download", "Download", directorySettings.getDownload(), MediaType.MIXED);
        download.addFeed(tv());
        download.addFeed(movie());
        download.addFeed(anime());
        return download;
    }

    @Bean
    @Order(2)
    public Category tv()
    {
        return new Category("fa-play-circle", "Tv", directorySettings.getTv(), MediaType.TV);
    }

    @Bean
    @Order(3)
    public Category movie()
    {
        Category movie = new Category("fa-film", "Movie", directorySettings.getMovie(), MediaType.MOVIE);
        movie.addFeed(movieArchive());
        return movie;
    }

    @Bean
    @Order(4)
    public Category anime()
    {
        return new Category("fa-star", "Anime", directorySettings.getAnime(), MediaType.ANIME);
    }

    @Bean
    @Order(5)
    public Category movieArchive()
    {
        return new Category("fa-database", "Movie Archive", directorySettings.getMovieArchive(), MediaType.MOVIE);
    }

    @Bean
    public Category temp()
    {
        return new Category("fa-clock-o", "Temp", directorySettings.getTemp(), MediaType.MIXED);
    }
}
