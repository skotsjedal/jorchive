package no.skotsj.jorchive.service;

import com.diffplug.common.base.Errors;
import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import no.skotsj.jorchive.service.support.FileWatcher;
import no.skotsj.jorchive.service.support.ProgressInstance;
import no.skotsj.jorchive.service.support.ReportingOutputStream;
import no.skotsj.jorchive.web.model.FileInfo;
import no.skotsj.jorchive.web.model.code.EntryType;
import no.skotsj.jorchive.web.util.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
public class FileService
{
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private FileWatcher fileWatcher;

    @Async
    public void copy(FileInfo fileInfo, Path toPath)
    {
        log.info("copy {} to {}", fileInfo.getId(), toPath.toString());
        deepCopy(fileInfo, toPath);
    }

    private void deepCopy(FileInfo fileInfo, Path toPath)
    {
        if (fileInfo.getEntryType() == EntryType.DIR)
        {
            fileInfo.getChildren().forEach(p -> deepCopy(p, toPath));
            return;
        }
        Path targetFile = toPath.resolve(fileInfo.getName());
        try (ReportingOutputStream os = new ReportingOutputStream(Files.newOutputStream(targetFile)))
        {
            log.debug("Copying {}", fileInfo.getName());

            ProgressInstance pi = new ProgressInstance(fileInfo.getId(), fileInfo.getName(), os, fileInfo.getSize());
            performWatched(pi, Errors.rethrow().wrap(() -> {
                if (Files.exists(targetFile))
                {
                    Files.delete(targetFile);
                }
                Files.copy(fileInfo.getPath(), os);
            }));
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Async
    public void extract(FileInfo fileInfo, Path path)
    {
        log.info("extract {} to {}", fileInfo.getId(), path.toString());

        String rarFilename = StringUtils.substringBefore(fileInfo.getRelativePath(), CommonUtils.RAR_SEPARATOR);
        Path rarFile = Paths.get(fileInfo.getCategory().getPath().toAbsolutePath().toString(), rarFilename);
        Path targetFile = path.resolve(fileInfo.getName());
        try (
                Archive archive = new Archive(rarFile.toFile());
                ReportingOutputStream os = new ReportingOutputStream(Files.newOutputStream(targetFile))
        )
        {
            FileHeader fileHeader = archive.getFileHeaders().stream()
                    .filter(h -> h.getPositionInFile() == fileInfo.getFileHeader().getPositionInFile()).findFirst().get();

            ProgressInstance pi = new ProgressInstance(fileInfo.getId(), fileInfo.getName(), os, fileInfo.getSize());
            performWatched(pi, Errors.rethrow().wrap(() -> archive.extractFile(fileHeader, os)));
        } catch (RarException | IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void performWatched(ProgressInstance pi, Runnable action)
    {
        fileWatcher.watch(pi);
        try
        {
            action.run();
        } catch (Exception e) {
            pi.setFailure(e.getMessage());
            log.error("Processing failed for file " + pi.getId(), e);
        }
        finally
        {
            pi.unlock();
        }
    }

}
