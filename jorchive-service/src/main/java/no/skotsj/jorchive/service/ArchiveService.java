package no.skotsj.jorchive.service;

import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

/**
 * ArchiveService
 * <p>
 * Created by Skotsj on 28.12.2014.
 */
@Service
public interface ArchiveService
{
    List<Path> listCompleted();
}
