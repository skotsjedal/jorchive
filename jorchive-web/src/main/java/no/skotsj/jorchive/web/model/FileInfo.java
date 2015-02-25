package no.skotsj.jorchive.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import no.skotsj.jorchive.web.util.CommonUtils;
import org.joda.time.LocalDateTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.repeat;
import static no.skotsj.jorchive.web.util.CommonUtils.RAR_SEPARATOR;
import static no.skotsj.jorchive.web.util.StyleHelper.*;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;

/**
 * File Model
 * Created by Skotsj on 01.02.2015.
 */
public class FileInfo
{
    public static final String IGNORED_ARCHIVE_PATTERN = "r\\d\\d";

    private int depth;
    private int children;
    private String name;
    private LocalDateTime date;
    private long size;
    private String sizeColor;
    private String viewSize;
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

    private List<CategoryStatus> statuses;

    private FileInfo(String name, int depth)
    {
        this.depth = depth;
        this.name = name;
        this.ext = substringAfterLast(name, ".").toLowerCase();
    }

    public FileInfo(FileList fileList, final Path path, final int depth, int children)
    {
        this(path.getFileName().toString(), depth);
        this.path = path;
        String fullPath = path.toAbsolutePath().toString();
        String prefix = Strings.commonPrefix(fullPath, fileList.getRoot().toString() + File.separator);
        this.relativePath = fullPath.substring(prefix.length());
        this.children = children;
        this.size = findSize(path);
        FileTime fileTime;
        try
        {
            fileTime = Files.readAttributes(path, BasicFileAttributes.class).creationTime();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        this.date = new LocalDateTime(fileTime.toMillis());
        this.viewSize = humanReadableByteCount(size);
        this.sizeColor = colorForSize(size);
        this.entryType = Files.isDirectory(path) ? EntryType.DIR : EntryType.FILE;
        this.hardIgnored = ext.matches(IGNORED_ARCHIVE_PATTERN);
        generateHashes();
        generateStatuses(fileList.getCategory());
    }

    public FileInfo(FileList fileList, FileHeader fileHeader, String rarFile, int depth)
    {
        this(fileHeader.getFileNameString(), depth);
        this.fileHeader = fileHeader;
        this.relativePath = rarFile + RAR_SEPARATOR + fileHeader.getFileNameString();
        this.children = 0;
        this.size = fileHeader.getUnpSize();
        this.date = LocalDateTime.fromDateFields(fileHeader.getCTime());
        this.viewSize = humanReadableByteCount(size);
        this.sizeColor = colorForSize(size);
        this.entryType = EntryType.ARCHIVE_ENTRY;
        generateHashes();
        generateStatuses(fileList.getCategory());
    }

    private void generateStatuses(Category category)
    {
        statuses = category.getToCategories().stream()
                .map(c -> new CategoryStatus(c, c.getFiles().contains(name)))
                .collect(Collectors.toList());
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
        return name;
    }

    public long getSize()
    {
        return size;
    }

    public String getDate()
    {
        return date.toString("yyyy-MM-dd HH:mm:ss");
    }

    public String getViewSize()
    {
        return viewSize;
    }

    public String getSizeColor()
    {
        return sizeColor;
    }

    public String getRelativePath()
    {
        return relativePath;
    }

    public int getChildren()
    {
        return children;
    }

    public String getRowColor()
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

    @JsonIgnore
    public Path getPath()
    {
        return path;
    }

    @JsonIgnore
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

    public String getIcon()
    {
        if (isDir())
        {
            return FOLDER_OPEN;
        }
        return icon(ext);
    }

    public List<CategoryStatus> getStatuses()
    {
        return statuses;
    }

    public void extract(Path out)
    {
        if (entryType != EntryType.ARCHIVE_ENTRY)
        {
            throw new RuntimeException("not arch");
        }
        try (FileOutputStream os = new FileOutputStream(out.resolve(name).toFile()))
        {
            // Will most likely have to reopen archive first
            archive.extractFile(fileHeader, os);
        } catch (RarException | IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
