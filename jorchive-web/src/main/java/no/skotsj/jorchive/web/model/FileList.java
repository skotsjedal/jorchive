package no.skotsj.jorchive.web.model;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import no.skotsj.jorchive.common.util.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static no.skotsj.jorchive.web.util.StyleHelper.*;


/**
 * @author Skotsj on 29.12.2014.
 */
public class FileList {

    private Path root;
    private List<FileInfo> files = Lists.newArrayList();

    public FileList(List<Path> paths) {
        if (paths.size() > 0) {
            root = paths.get(0).getParent().toAbsolutePath();
        }
        for (Path path : paths) {
            parse(path, 0);
        }
    }

    private void parse(Path path, int depth) {
        if (Files.isDirectory(path)) {
            List<Path> subDirs = FileUtils.listDir(path);
            files.add(new FileInfo(path, depth, subDirs.size()));
            subDirs.forEach(p -> parse(p, depth + 1));
        } else {
            String ext = StringUtils.substringAfterLast(path.toString(), ".");
            if (ext.matches("r\\d\\d")) {
                return;
            }
            FileInfo newFile = new FileInfo(path, depth, 0);
            files.add(newFile);
            if (ext.equals("rar")) {
                tryRar(path, newFile);
            }
        }
    }

    private void tryRar(Path path, FileInfo newFile) {
        Archive archive;
        try {
            archive = new Archive(path.toFile());
        } catch (RarException | IOException e) {
            throw new RuntimeException(e);
        }
        List<FileHeader> fileHeaders = archive.getFileHeaders();
        files.addAll(fileHeaders.stream()
                .map(fileHeader -> new FileInfo(fileHeader, newFile.getRelativePath(), newFile.getDepth() + 1))
                .collect(Collectors.toList()));
    }

    public List<FileInfo> getFiles() {
        return files;
    }

    private class FileInfo {

        private int depth;
        private int children;
        private String name;
        private String size;
        private String hash;
        private String ownHash;

        private String relativePath;
        private boolean isDir = false;

        public FileInfo(final Path path, final int depth, int children) {
            String fullPath = path.toAbsolutePath().toString();
            String prefix = Strings.commonPrefix(fullPath, root.toString() + File.separator);
            this.relativePath = fullPath.substring(prefix.length());
            this.depth = depth;
            this.children = children;
            this.name = path.getFileName().toString();
            this.size = fileSizeWithHtmlColor(path);
            this.isDir = Files.isDirectory(path);
            generateHashes();
        }

        public FileInfo(FileHeader fileHeader, String rarFile, int depth) {
            this.relativePath = rarFile + "§" + fileHeader.getFileNameString();
            this.name = fileHeader.getFileNameString();
            this.depth = depth;
            this.children = 0;
            this.size = fileSizeWithHtmlColor(fileHeader.getUnpSize());
            generateHashes();
        }

        private void generateHashes() {
            LinkedList<String> hashes = createHash();
            this.ownHash = hashes.pollLast();
            this.hash = Joiner.on(" ").join(hashes);
        }

        private LinkedList<String> createHash() {
            final StringBuilder pathBuilder = new StringBuilder();
            List<String> hashes = Splitter.on(File.separator)
                    .splitToList(relativePath).stream()
                    .map(s -> {
                        pathBuilder.append(s);
                        return pathBuilder.toString();
                    })
                    .map(s -> Integer.toHexString(s.hashCode()))
                    .collect(Collectors.toList());
            return Lists.newLinkedList(hashes);
        }

        public int getDepth() {
            return depth;
        }

        public String getOwnHash() {
            return ownHash;
        }

        public String getHash() {
            return hash;
        }

        public String getName() {
            if (depth == 0) {
                return (isDir ? icon(FOLDER_OPEN) : "") + name;
            }
            return Strings.repeat("&nbsp;", depth * 4) + "\\- " + iconify(name);
        }

        private String iconify(String name) {
            if (isDir) {
                return icon(FOLDER_OPEN) + name;
            }
            String ext = StringUtils.substringAfterLast(name, ".");
            return addIcon(ext, name);
        }

        public String getSize() {
            return size;
        }

        public String getRelativePath() {
            return relativePath;
        }

        public int getChildren() {
            return children;
        }

        public String getColor() {
            return "#" + Strings.repeat(Integer.toHexString(255 - depth * 20), 3);
        }

        public void setSize(String size) {
            this.size = size;
        }
    }
}
