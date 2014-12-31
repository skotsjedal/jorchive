package no.skotsj.jorchive.service;

import no.skotsj.jorchive.common.prop.DirectorySettings;
import no.skotsj.jorchive.common.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Service Class For Archiving
 *
 * @author Skotsj on 28.12.2014.
 */
@Service
public class DefaultArchiveService implements ArchiveService
{

    @Autowired
    private DirectorySettings directorySettings;

    @Override
    public List<Path> listCompleted()
    {
        Path dir = Paths.get(directorySettings.getCompleted());
        return FileUtils.listDir(dir);
    }

}
