package no.skotsj.jorchive.service;

import no.skotsj.jorchive.common.prop.DirectorySettings;
import no.skotsj.jorchive.common.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Service Class for Archiving
 *
 * @author Skotsj on 28.12.2014.
 */
@Service
public class DefaultArchiveService implements ArchiveService
{
    private static Logger log = LoggerFactory.getLogger(DefaultArchiveService.class);

    @Autowired
    private DirectorySettings directorySettings;

    @Override
    public List<Path> listDownloaded()
    {
        return FileUtils.listDir(Paths.get(directorySettings.getDownload()));
    }

    @Override
    public List<Path> listTemp()
    {
        return FileUtils.listDir(Paths.get(directorySettings.getTemp()));
    }

    @Override
    public List<Path> listTv()
    {
        return FileUtils.listDir(Paths.get(directorySettings.getTv()));
    }

    @Override
    public List<Path> listMovie()
    {
        return FileUtils.listDir(Paths.get(directorySettings.getMovie()));
    }

    @Override
    public List<Path> listMovieArchive()
    {
        return FileUtils.listDir(Paths.get(directorySettings.getMovieArchive()));
    }

    @Override
    public List<Path> listAnime()
    {
        return FileUtils.listDir(Paths.get(directorySettings.getAnime()));
    }

    @Override
    public void extract(String id)
    {
        log.info("extract {}", id);
    }

    @Override
    public void copyFromInput(String id)
    {
        log.info("copyFromInput {}", id);
    }

}
