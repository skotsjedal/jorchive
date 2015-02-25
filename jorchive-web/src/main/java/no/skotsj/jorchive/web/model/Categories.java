package no.skotsj.jorchive.web.model;

import java.util.List;

/**
 * Category container and utility class
 * Created by jason on 25.02.15.
 */
public final class Categories
{

    private static Categories instance;

    private List<Category> categories;

    private Categories()
    {

    }

    public List<Category> getCategories()
    {
        return categories;
    }

    public void setCategories(List<Category> categories)
    {
        this.categories = categories;
    }

    public Category get(int i)
    {
        return categories.get(i);
    }

    public static Categories getInstance()
    {
        if (instance == null)
        {
            instance = new Categories();
        }
        return instance;
    }
}
