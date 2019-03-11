package hudson.util;


import java.io.File;
import java.nio.channels.ClosedChannelException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class FileChannelWriterTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    File file;

    FileChannelWriter writer;

    @Test
    public void write() throws Exception {
        writer.write("helloooo");
        writer.close();
        assertContent("helloooo");
    }

    @Test
    public void flush() throws Exception {
        writer.write("hello ? ? ?".toCharArray());
        writer.flush();
        assertContent("hello ? ? ?");
    }

    @Test(expected = ClosedChannelException.class)
    public void close() throws Exception {
        writer.write("helloooo");
        writer.close();
        writer.write("helloooo");
        Assert.fail("Should have failed the line above");
    }
}

