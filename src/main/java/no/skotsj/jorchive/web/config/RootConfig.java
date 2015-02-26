package no.skotsj.jorchive.web.config;

import com.google.common.collect.Lists;
import no.skotsj.jorchive.web.config.prop.DirectorySettings;
import no.skotsj.jorchive.web.model.Categories;
import no.skotsj.jorchive.web.model.Category;
import no.skotsj.jorchive.web.model.code.MediaType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

import static no.skotsj.jorchive.web.model.code.CategoryType.ARCHIVE;
import static no.skotsj.jorchive.web.model.code.CategoryType.IN;
import static no.skotsj.jorchive.web.model.code.CategoryType.OUT;

/**
 * @author Skotsj on 31.01.2015.
 */
@Configuration
@Import({ServiceConfig.class, SecurityConfig.class})
public class RootConfig
{
    @Bean
    public Categories categories(DirectorySettings directorySettings)
    {
        Categories categories = Categories.getInstance();
        List<Category> categoryList = Lists.newArrayList(
                new Category("fa-download", "Download", IN, directorySettings.getDownload()),
                new Category("fa-play-circle", "Tv", OUT, directorySettings.getTv(), MediaType.TV),
                new Category("fa-film", "Movie", OUT, directorySettings.getMovie(), MediaType.MOVIE),
                new Category("fa-database", "Movie Archive", ARCHIVE, directorySettings.getMovieArchive(), MediaType.MOVIE),
                new Category("fa-star", "Anime", OUT, directorySettings.getAnime(), MediaType.ANIME),
                new Category("fa-clock-o", "Temp", IN, directorySettings.getTemp()));
        categories.setCategories(categoryList);
        return categories;
    }
}
