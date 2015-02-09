package no.skotsj.jorchive.web.model;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import com.google.common.collect.Lists;
import no.skotsj.jorchive.common.domain.EntryType;
import no.skotsj.jorchive.common.domain.FilterType;
import no.skotsj.jorchive.common.util.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Model Class for file lists
 * TODO refactor logic to service layer and away from model
 *
 * @author Skotsj on 29.12.2014.
 */
public class FileList
{

    private Path root;
    private List<FileInfo> files = Lists.newArrayList();

    public List<FileInfo> getFiles()
    {
        return files;
    }

    public FileList(List<Path> paths)
    {
        if (paths.size() > 0)
        {
            root = paths.get(0).getParent().toAbsolutePath();
        }
        for (Path path : paths)
        {
            parsePath(path, 0);
        }
    }

    public FileInfo get(String id)
    {
        return files.stream().filter(fileInfo -> fileInfo.getId().equals(id)).findFirst().orElse(null);
    }

    public void filter(FilterType filterType)
    {
        filterType = filterType == null ? FilterType.ALL : filterType;
        files.forEach(file -> file.setIgnored(false));
        Stream<FileInfo> ignored = Stream.empty();
        switch (filterType)
        {
            case FILE:
                ignored = findAllNot(EntryType.FILE);
                break;
            case ARCHIVE_ENTRY:
                ignored = findAllNot(EntryType.ARCHIVE_ENTRY);
                break;

        }
        ignored.forEach(file -> file.setIgnored(true));
    }

    private Stream<FileInfo> findAllNot(EntryType entryType)
    {
        return files.stream().filter(file -> file.getEntryType() != entryType && file.getEntryType() != EntryType.DIR);
    }

    private void parsePath(Path path, int depth)
    {
        if (Files.isDirectory(path))
        {
            List<Path> subDirs = FileUtils.listDir(path);
            files.add(new FileInfo(this, path, depth, subDirs.size()));
            subDirs.forEach(p -> parsePath(p, depth + 1));
        } else
        {
            FileInfo newFile = new FileInfo(this, path, depth, 0);
            files.add(newFile);
            if (newFile.getExt().equals("rar"))
            {
                parseRar(newFile);
            }
        }
    }

    private void parseRar(FileInfo fileInfo)
    {
        Archive archive;
        try
        {
            archive = new Archive(fileInfo.getPath().toFile());
            archive.close();
        } catch (RarException | IOException e)
        {
            throw new RuntimeException(e);
        }
        List<FileHeader> fileHeaders = archive.getFileHeaders();
        files.addAll(fileHeaders.stream()
                .map(fileHeader -> new FileInfo(archive, fileHeader, fileInfo.getRelativePath(), fileInfo.getDepth() + 1))
                .collect(Collectors.toList()));
    }

    public Path getRoot() {
        return root;
    }
}
