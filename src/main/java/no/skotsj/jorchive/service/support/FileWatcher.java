package no.skotsj.jorchive.service.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;

/**
 * Utils for watching processing progress
 *
 * @author Skotsj on 04.06.2015.
 */
public class FileWatcher
{
    public static final int INTERVAL = 500;
    public static final int LOG_INTERVAL = 10;

    private Logger log = LoggerFactory.getLogger(getClass());

    private Set<ProgressInstance> instances = new ConcurrentSkipListSet<>();

    @Async
    public void watch(ProgressInstance pi)
    {
        instances.add(pi);
        try
        {
            double progress = 0;
            while (!pi.getLock().tryLock(INTERVAL, TimeUnit.MILLISECONDS))
            {
                pi.tick();
                if (progress + LOG_INTERVAL < pi.getProgress())
                {
                    progress = pi.getProgress();
                    log.debug(String.format(Locale.ENGLISH, "%s %s/%s %s %.2f%%", pi.getName(),
                            pi.getCurrent(), pi.getSize(), pi.getSpeed(),
                            pi.getProgress()));
                }
            }
        } catch (Exception e)
        {
            log.error("error watch", e);
            throw new RuntimeException(e);

        }
        pi.setDone();
        log.info("{} done processing", pi.getName());
    }

    public ProgressInstance getInstance(String id)
    {
        ProgressInstance progressInstance = instances.stream().filter(i -> i.getId().equals(id)).findFirst()
                .orElse(new ProgressInstance("", "N/A", null, 0));
        if (progressInstance != null && progressInstance.isDone())
        {
            instances.remove(progressInstance);
        }
        return progressInstance;
    }

}
