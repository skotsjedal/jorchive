package no.skotsj.jorchive.service;

import no.skotsj.jorchive.web.model.FileInfo;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

/**
 * ArchiveService
 * <p>
 * Created by Skotsj on 28.12.2014.
 */
@Service
public interface ArchiveService
{
    void copy(FileInfo fileInfo, Path path);

    void extract(FileInfo fileInfo, Path path);
}
