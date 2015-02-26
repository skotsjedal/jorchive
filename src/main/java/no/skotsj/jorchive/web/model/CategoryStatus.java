package no.skotsj.jorchive.web.model;

/**
 * Status for category
 * Created by jason on 25.02.15.
 */
public class CategoryStatus
{
    private String categoryName;
    private boolean contained;

    public CategoryStatus(Category category, boolean contained)
    {
        this.categoryName = category.getName();
        this.contained = contained;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public boolean getContained()
    {
        return contained;
    }

}
