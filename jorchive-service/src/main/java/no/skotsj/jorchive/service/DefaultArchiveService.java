package no.skotsj.jorchive.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service Class for Archiving
 *
 * @author Skotsj on 28.12.2014.
 */
@Service
public class DefaultArchiveService implements ArchiveService
{
    private static Logger log = LoggerFactory.getLogger(DefaultArchiveService.class);

    @Override
    public void extract(String id)
    {
        log.info("extract {}", id);
    }

    @Override
    public void copyFromInput(String id)
    {
        log.info("copyFromInput {}", id);
    }

}
