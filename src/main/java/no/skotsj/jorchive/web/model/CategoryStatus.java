package no.skotsj.jorchive.web.model;

/**
 * Status for category
 * Created by jason on 25.02.15.
 */
public class CategoryStatus
{
    public enum Status
    {
        NONE, CONTAINED, PROCESSING, PROCESSED, FAILED
    }

    private String categoryName;
    private Status status;

    public CategoryStatus(Category category, boolean contained)
    {
        this.categoryName = category.getName();
        this.status = contained ? Status.CONTAINED : Status.NONE;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public Status getStatus()
    {
        return status;
    }

}
