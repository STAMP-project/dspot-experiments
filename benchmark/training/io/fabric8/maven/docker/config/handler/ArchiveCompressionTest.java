package io.fabric8.maven.docker.config.handler;


import io.fabric8.maven.docker.config.ArchiveCompression;
import org.junit.Assert;
import org.junit.Test;


public class ArchiveCompressionTest {
    @Test
    public void fromFileName() throws Exception {
        ArchiveCompression c = ArchiveCompression.fromFileName("test.tar");
        Assert.assertEquals("tar", c.getFileSuffix());
        c = ArchiveCompression.fromFileName("test.tar.bzip2");
        Assert.assertEquals("tar.bz", c.getFileSuffix());
        c = ArchiveCompression.fromFileName("test.tar.bz2");
        Assert.assertEquals("tar.bz", c.getFileSuffix());
        c = ArchiveCompression.fromFileName("test.tar.gz");
        Assert.assertEquals("tar.gz", c.getFileSuffix());
        c = ArchiveCompression.fromFileName("test.tgz");
        Assert.assertEquals("tar.gz", c.getFileSuffix());
    }
}

