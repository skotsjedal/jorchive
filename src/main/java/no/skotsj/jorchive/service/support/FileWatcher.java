package no.skotsj.jorchive.service.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import java.nio.file.Path;
import java.util.concurrent.locks.Lock;

import static no.skotsj.jorchive.web.util.StyleHelper.humanReadableByteCount;

/**
 * Utils for watching processing progress
 *
 * @author Skotsj on 04.06.2015.
 */
public class FileWatcher
{
    public static final int INTERVAL = 2000;
    private Logger log = LoggerFactory.getLogger(getClass());

    @Async
    public void watch(Path path, ReportingOutputStream stream, long size, Lock lock)
    {
        long last = 0;
        while (!lock.tryLock())
        {
            try
            {
                Thread.sleep(INTERVAL);

                long current = stream.getWritten();
                long speed = (current - last) / (INTERVAL / 1000);
                last = current;
                log.info(String.format("%s %s/%s %s/s %.2f%%", path.getFileName(),
                        humanReadableByteCount(current), humanReadableByteCount(size), humanReadableByteCount(speed),
                        (double) current * 100 / size));
            } catch (Exception e)
            {
                log.error("error watch", e);
                throw new RuntimeException(e);
            }
        }
        log.info("{} done processing", path.getFileName());
    }
}
