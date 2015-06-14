package no.skotsj.jorchive.service.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static no.skotsj.jorchive.web.util.StyleHelper.humanReadableByteCount;

/**
 * Utils for watching processing progress
 *
 * @author Skotsj on 04.06.2015.
 */
public class FileWatcher
{
    public static final int INTERVAL = 500;
    private Logger log = LoggerFactory.getLogger(getClass());

    private Set<ProgressInstance> instances = new HashSet<>();

    @Async
    public void watch(ProgressInstance pi)
    {
        instances.add(pi);
        try
        {
            while (!pi.lock.tryLock(INTERVAL, TimeUnit.MILLISECONDS))
            {
                pi.tick();
                log.trace(String.format(Locale.ENGLISH, "%s %s/%s %s %.2f%%", pi.name,
                        pi.getCurrent(), pi.getSize(), pi.getSpeed(),
                        pi.getProgress()));
            }
        } catch (Exception e)
        {
            log.error("error watch", e);
            throw new RuntimeException(e);

        }
        pi.done = true;
        log.info("{} done processing", pi.name);
    }

    public ProgressInstance getInstance(String id)
    {
        ProgressInstance progressInstance = instances.stream().filter(i -> i.id.equals(id)).findFirst()
                .orElse(new ProgressInstance("", "N/A", null, 0));
        if (progressInstance != null && progressInstance.done)
        {
            instances.remove(progressInstance);
        }
        return progressInstance;
    }

    public static class ProgressInstance
    {
        private String id;
        private String name;
        private ReportingOutputStream stream;
        private long size;
        private Lock lock;
        private boolean done;

        private long last;
        private long speed;
        private String failure;

        public ProgressInstance(String id, String name, ReportingOutputStream stream, long size)
        {
            this.id = id;
            this.name = name;
            this.stream = stream;
            this.size = size;
            lock = new ReentrantLock();
            lock.lock();
        }

        public String getSpeed()
        {
            return humanReadableByteCount(speed) + "/s";
        }

        public String getCurrent()
        {
            return humanReadableByteCount(current());
        }

        public double getProgress()
        {
            return (double) current() * 100 / size;
        }

        public String getSize()
        {
            return humanReadableByteCount(size);
        }

        private void tick()
        {
            long current = current();
            speed = (current - last) / (INTERVAL / 1000);
            last = current;
        }

        private long current()
        {
            return stream == null ? 0 : stream.getWritten();
        }

        public void unlock()
        {
            lock.unlock();
        }

        public void setFailure(String failure)
        {
            this.failure = failure;
        }

        public String getFailure()
        {
            return failure;
        }

        public String getId()
        {
            return id;
        }

        public boolean isDone()
        {
            return done;
        }

        public String getName()
        {
            return name;
        }
    }
}
