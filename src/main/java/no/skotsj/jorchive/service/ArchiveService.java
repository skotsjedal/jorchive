package no.skotsj.jorchive.service;

import org.springframework.stereotype.Service;

/**
 * ArchiveService
 * <p>
 * Created by Skotsj on 28.12.2014.
 */
@Service
public interface ArchiveService
{
    void extract(String id);

    void copyFromInput(String id);
}
