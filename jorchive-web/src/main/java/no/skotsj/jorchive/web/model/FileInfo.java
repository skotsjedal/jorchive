package no.skotsj.jorchive.web.model;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import no.skotsj.jorchive.common.domain.EntryType;
import no.skotsj.jorchive.web.util.CommonUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;

import static com.google.common.base.Strings.repeat;
import static no.skotsj.jorchive.web.util.CommonUtils.RAR_SEPARATOR;
import static no.skotsj.jorchive.web.util.StyleHelper.*;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;

/**
* Created by Skotsj on 01.02.2015.
*/
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

    public FileInfo(FileList fileList, final Path path, final int depth, int children)
    {
        this(path.getFileName().toString(), depth);
        this.path = path;
        String fullPath = path.toAbsolutePath().toString();
        String prefix = Strings.commonPrefix(fullPath, fileList.getRoot().toString() + File.separator);
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
