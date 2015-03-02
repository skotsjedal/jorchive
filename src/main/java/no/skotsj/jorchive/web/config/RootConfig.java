package no.skotsj.jorchive.web.config;

import no.skotsj.jorchive.web.model.Categories;
import no.skotsj.jorchive.web.model.Category;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

/**
 * @author Skotsj on 31.01.2015.
 */
@Configuration
@Import({ServiceConfig.class, SecurityConfig.class, CategoryConfig.class})
public class RootConfig
{
    @Bean
    public Categories categories(List<Category> categoryList)
    {
        Categories categories = Categories.getInstance();
        categories.setCategories(categoryList);
        return categories;
    }
}
