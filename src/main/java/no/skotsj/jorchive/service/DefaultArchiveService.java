package no.skotsj.jorchive.service;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import no.skotsj.jorchive.common.util.FileUtils;
import no.skotsj.jorchive.web.model.FileInfo;
import no.skotsj.jorchive.web.util.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Service Class for Archiving
 *
 * @author Skotsj on 28.12.2014.
 */
@Service
public class DefaultArchiveService implements ArchiveService
{
    private static Logger log = LoggerFactory.getLogger(DefaultArchiveService.class);

    @Override
    public void copy(FileInfo fileInfo, Path toPath)
    {
        log.info("copy {} to {}", fileInfo.getId(), toPath.toString());
        deepCopy(fileInfo.getPath(), toPath);
    }

    private void deepCopy(Path fromPath, Path toPath)
    {
        if (Files.isDirectory(fromPath))
        {
            FileUtils.listDir(fromPath).forEach(p -> deepCopy(p, toPath));
        }
        try
        {
            log.debug("Copying {}", fromPath.getFileName());
            Files.copy(fromPath, toPath.resolve(fromPath.getFileName().toString()));
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void extract(FileInfo fileInfo, Path path)
    {
        log.info("extract {} to {}", fileInfo.getId(), path.toString());

        String rarFilename = StringUtils.substringBefore(fileInfo.getRelativePath(), CommonUtils.RAR_SEPARATOR);
        Path rarFile = Paths.get(fileInfo.getCategory().getPath().toAbsolutePath().toString(), rarFilename);
        try (Archive archive = new Archive(rarFile.toFile()))
        {
            FileHeader fileHeader = archive.getFileHeaders().stream()
                    .filter(h -> h.getPositionInFile() == fileInfo.getFileHeader().getPositionInFile()).findFirst().get();

            archive.extractFile(fileHeader, Files.newOutputStream(path.resolve(fileInfo.getName())));
        } catch (RarException | IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
