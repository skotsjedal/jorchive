package no.skotsj.jorchive.web.model;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import no.skotsj.jorchive.common.util.FileUtils;
import no.skotsj.jorchive.web.util.CommonUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Strings.repeat;
import static no.skotsj.jorchive.web.util.CommonUtils.RAR_SEPARATOR;
import static no.skotsj.jorchive.web.util.StyleHelper.FOLDER_OPEN;
import static no.skotsj.jorchive.web.util.StyleHelper.addIcon;
import static no.skotsj.jorchive.web.util.StyleHelper.fileSizeWithHtmlColor;
import static no.skotsj.jorchive.web.util.StyleHelper.icon;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;


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
            files.add(new FileInfo(path, depth, subDirs.size()));
            subDirs.forEach(p -> parsePath(p, depth + 1));
        } else
        {
            FileInfo newFile = new FileInfo(path, depth, 0);
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
        } catch (RarException | IOException e)
        {
            throw new RuntimeException(e);
        }
        List<FileHeader> fileHeaders = archive.getFileHeaders();
        files.addAll(fileHeaders.stream()
                .map(fileHeader -> new FileInfo(archive, fileHeader, fileInfo.getRelativePath(), fileInfo.getDepth() + 1))
                .collect(Collectors.toList()));
    }

    public class FileInfo
    {
        public static final String IGNORED_ARCHIVE_PATTERN = "r\\d\\d";

        private int depth;
        private int children;
        private String name;
        private String size;
        private String context;
        private String id;

        private String relativePath;
        private boolean ignored = false;
        private boolean hardIgnored = false;

        private Path path;
        private Archive archive;
        private FileHeader fileHeader;
        private EntryType entryType;
        private String ext;

        private FileInfo(String name, int depth)
        {
            this.depth = depth;
            this.name = name;
            this.ext = substringAfterLast(name, ".").toLowerCase();
        }

        public FileInfo(final Path path, final int depth, int children)
        {
            this(path.getFileName().toString(), depth);
            this.path = path;
            String fullPath = path.toAbsolutePath().toString();
            String prefix = Strings.commonPrefix(fullPath, root.toString() + File.separator);
            this.relativePath = fullPath.substring(prefix.length());
            this.children = children;
            this.size = fileSizeWithHtmlColor(path);
            this.entryType = Files.isDirectory(path) ? EntryType.DIR : EntryType.FILE;
            this.hardIgnored = ext.matches(IGNORED_ARCHIVE_PATTERN);
            generateHashes();
        }

        public FileInfo(Archive archive, FileHeader fileHeader, String rarFile, int depth)
        {
            this(fileHeader.getFileNameString(), depth);
            this.archive = archive;
            this.fileHeader = fileHeader;
            this.relativePath = rarFile + RAR_SEPARATOR + fileHeader.getFileNameString();
            this.children = 0;
            this.size = fileSizeWithHtmlColor(fileHeader.getUnpSize());
            this.entryType = EntryType.ARCHIVE_ENTRY;
            generateHashes();
        }

        private void generateHashes()
        {
            LinkedList<String> hashes = CommonUtils.createHash(relativePath);
            this.id = hashes.pollLast();
            this.context = Joiner.on(" ").join(hashes);
        }

        public int getDepth()
        {
            return depth;
        }

        public String getId()
        {
            return id;
        }

        public String getContext()
        {
            return context;
        }

        public String getName()
        {
            if (depth == 0)
            {
                return (isDir() ? icon(FOLDER_OPEN) : "") + name;
            }
            return repeat("&nbsp;", depth * 4) + "\\- " + iconify(name);
        }

        public String getSize()
        {
            return size;
        }

        public String getRelativePath()
        {
            return relativePath;
        }

        public int getChildren()
        {
            return children;
        }

        public String getColor()
        {
            return "#" + repeat(Integer.toHexString(255 - depth * 20), 3);
        }

        public boolean isIgnored()
        {
            return ignored || hardIgnored;
        }

        public void setIgnored(boolean ignored)
        {
            this.ignored = ignored;
        }

        public Path getPath()
        {
            return path;
        }

        public FileHeader getFileHeader()
        {
            return fileHeader;
        }

        public EntryType getEntryType()
        {
            return entryType;
        }

        public String getExt()
        {
            return ext;
        }

        private boolean isDir()
        {
            return entryType == EntryType.DIR;
        }

        private String iconify(String name)
        {
            if (isDir())
            {
                return icon(FOLDER_OPEN) + name;
            }
            return addIcon(ext, name);
        }

        public void extract(Path out)
        {
            if (entryType != EntryType.ARCHIVE_ENTRY)
            {
                throw new RuntimeException("not arch");
            }
            try (FileOutputStream os = new FileOutputStream(out.resolve(name).toFile()))
            {
                archive.extractFile(fileHeader, os);
            } catch (RarException | IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
}
