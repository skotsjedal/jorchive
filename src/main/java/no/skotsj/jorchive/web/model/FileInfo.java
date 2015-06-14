package no.skotsj.jorchive.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.junrar.rarfile.FileHeader;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import no.skotsj.jorchive.web.model.code.EntryType;
import no.skotsj.jorchive.web.model.code.MediaType;
import no.skotsj.jorchive.web.util.Categorizer;
import org.joda.time.LocalDateTime;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.repeat;
import static no.skotsj.jorchive.web.util.CommonUtils.RAR_SEPARATOR;
import static no.skotsj.jorchive.web.util.StyleHelper.FOLDER_OPEN;
import static no.skotsj.jorchive.web.util.StyleHelper.colorForSize;
import static no.skotsj.jorchive.web.util.StyleHelper.findSize;
import static no.skotsj.jorchive.web.util.StyleHelper.humanReadableByteCount;
import static no.skotsj.jorchive.web.util.StyleHelper.icon;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;

/**
 * File Model
 * Created by Skotsj on 01.02.2015.
 */
public class FileInfo
{
    private static final String IGNORED_ARCHIVE_PATTERN = "(r(\\d\\d|ar))";
    private static final String IGNORE_PATTERN = "sfv|nfo|" + IGNORED_ARCHIVE_PATTERN;
    private static final String SAMPLE = ".*sample.*";

    private static final Pattern sample = Pattern.compile(SAMPLE, Pattern.CASE_INSENSITIVE);
    private static final Pattern extPattern = Pattern.compile(IGNORE_PATTERN, Pattern.CASE_INSENSITIVE);

    private int depth;
    private List<FileInfo> children;
    private String name;
    private LocalDateTime date;
    private long size;
    private String sizeColor;
    private String viewSize;
    private String id;

    private String relativePath;
    private boolean ignored = false;
    private boolean hardIgnored = false;

    private Path path;
    private FileHeader fileHeader;
    private EntryType entryType;
    private String ext;
    private Category category;

    private MediaType mediaType;
    private List<CategoryStatus> statuses;

    private FileInfo(FileList fileList, String name, int depth)
    {
        this.depth = depth;
        this.name = name;
        this.ext = substringAfterLast(name, ".").toLowerCase();
        this.hardIgnored = extPattern.matcher(ext).matches() || sample.matcher(name).matches();
        this.mediaType = Categorizer.categorize(name);
        this.category = fileList.getCategory();
    }

    public FileInfo(FileList fileList, final Path path, final int depth, List<FileInfo> children)
    {
        this(fileList, path.getFileName().toString(), depth);
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
        this.id = Integer.toHexString(this.relativePath.hashCode());
        generateStatuses();
    }

    public FileInfo(FileList fileList, FileHeader fileHeader, String rarFile, int depth)
    {
        this(fileList, fileHeader.getFileNameString(), depth);
        this.fileHeader = fileHeader;
        this.relativePath = rarFile + RAR_SEPARATOR + fileHeader.getFileNameString();
        this.children = Lists.newArrayList();
        this.size = fileHeader.getUnpSize();
        this.date = LocalDateTime.fromDateFields(fileHeader.getMTime());
        this.viewSize = humanReadableByteCount(size);
        this.sizeColor = colorForSize(size);
        this.entryType = EntryType.ARCHIVE_ENTRY;
        this.id = Integer.toHexString(this.relativePath.hashCode());
        generateStatuses();
    }

    private void generateStatuses()
    {
        statuses = category.getFeeds().stream().filter(c -> c.getMediaType() == mediaType)
                .map(c -> new CategoryStatus(c, c.getFiles().contains(name)))
                .collect(Collectors.toList());
    }

    public int getDepth()
    {
        return depth;
    }

    public String getId()
    {
        return id;
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

    @JsonIgnore
    public List<FileInfo> getChildren()
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

    public MediaType getMediaType()
    {
        return isDir() ? null : mediaType;
    }

    public List<CategoryStatus> getStatuses()
    {
        return statuses;
    }

    public Category getCategory()
    {
        return category;
    }
}
