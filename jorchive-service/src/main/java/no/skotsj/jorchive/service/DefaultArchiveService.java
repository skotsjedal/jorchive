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
    public List<Path> listCompleted()
    {
        Path dir = Paths.get(directorySettings.getCompleted());
        return FileUtils.listDir(dir);
    }

    @Override
    public List<Path> listTemp()
    {
        Path dir = Paths.get(directorySettings.getTemp());
        return FileUtils.listDir(dir);
    }

    @Override
    public List<Path> listDone()
    {
        Path dir = Paths.get(directorySettings.getOutput());
        return FileUtils.listDir(dir);
    }

    @Override
    public Path getOutPath()
    {
        return Paths.get(directorySettings.getOutput());
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
