package no.skotsj.jorchive.service.support;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Reports on progress for wrapped OutputStream
 *
 * @author Skotsj on 04.06.2015.
 */
public class ReportingOutputStream extends OutputStream
{
    private OutputStream delegate;
    private volatile long written = 0;

    public ReportingOutputStream(OutputStream delegate)
    {
        this.delegate = delegate;
    }

    @Override
    public void write(int b) throws IOException
    {
        delegate.write(b);
        written++;
    }

    @Override
    public void write(byte[] b) throws IOException
    {
        delegate.write(b);
        written += b.length;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException
    {
        delegate.write(b, off, len);
        written += len;
    }

    @Override
    public void flush() throws IOException
    {
        delegate.flush();
    }

    @Override
    public void close() throws IOException
    {
        delegate.close();
    }

    public long getWritten()
    {
        return written;
    }
}
