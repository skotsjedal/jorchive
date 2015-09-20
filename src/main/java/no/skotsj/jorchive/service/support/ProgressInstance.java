package no.skotsj.jorchive.service.support;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static no.skotsj.jorchive.web.util.StyleHelper.humanReadableByteCount;

/**
 * Progress for a given file operation
 * Created by Skotsj on 9/20/2015.
 */
public class ProgressInstance
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

    // Package local used only in FileWatcher
    void tick()
    {
        long current = current();
        speed = (current - last) / (FileWatcher.INTERVAL / 1000);
        last = current;
    }

    void setDone()
    {
        this.done = true;
    }

    Lock getLock()
    {
        return lock;
    }
}
