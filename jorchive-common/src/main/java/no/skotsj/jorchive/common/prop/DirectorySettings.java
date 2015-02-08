package no.skotsj.jorchive.common.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Directory props
 * <p>
 * Created by Skotsj on 28.12.2014.
 */
@Component
@ConfigurationProperties(prefix = "jorchive.dir", ignoreUnknownFields = false)
public class DirectorySettings
{

    private String download;
    private String temp;
    private String tv;
    private String movie;
    private String movieArchive;
    private String anime;

    public String getDownload()
    {
        return download;
    }

    public void setDownload(String download)
    {
        this.download = download;
    }

    public String getTemp()
    {
        return temp;
    }

    public void setTemp(String temp)
    {
        this.temp = temp;
    }

    public String getTv()
    {
        return tv;
    }

    public void setTv(String tv)
    {
        this.tv = tv;
    }

    public String getMovie()
    {
        return movie;
    }

    public void setMovie(String movie)
    {
        this.movie = movie;
    }

    public String getMovieArchive()
    {
        return movieArchive;
    }

    public void setMovieArchive(String movieArchive)
    {
        this.movieArchive = movieArchive;
    }

    public String getAnime()
    {
        return anime;
    }

    public void setAnime(String anime)
    {
        this.anime = anime;
    }
}
