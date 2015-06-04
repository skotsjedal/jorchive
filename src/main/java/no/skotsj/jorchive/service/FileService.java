package no.skotsj.jorchive.service;

import com.diffplug.common.base.Errors;
import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import no.skotsj.jorchive.common.util.FileUtils;
import no.skotsj.jorchive.service.support.FileWatcher;
import no.skotsj.jorchive.service.support.ReportingOutputStream;
import no.skotsj.jorchive.web.model.FileInfo;
import no.skotsj.jorchive.web.util.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
        Path targetFile = toPath.resolve(fromPath.getFileName().toString());
        try (ReportingOutputStream os = new ReportingOutputStream(Files.newOutputStream(targetFile)))
        {
            log.debug("Copying {}", fromPath.getFileName());

            performWatched(targetFile, os, Files.size(fromPath), Errors.rethrow().wrap(() -> {
                if (Files.exists(targetFile))
                {
                    Files.delete(targetFile);
                }
                Files.copy(fromPath, os);
            }));
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

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

            performWatched(targetFile, os, fileInfo.getSize(), Errors.rethrow().wrap(() -> {
                archive.extractFile(fileHeader, os);
            }));
        } catch (RarException | IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private Lock performWatched(Path file, ReportingOutputStream stream, long size, Runnable action)
    {
        Lock lock = new ReentrantLock();
        lock.lock();
        fileWatcher.watch(file, stream, size, lock);
        try
        {
            action.run();
        } finally
        {
            lock.unlock();
        }
        return lock;
    }

}
