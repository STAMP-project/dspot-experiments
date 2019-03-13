package zmq.util;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestUtils {
    @Test
    public void testUnhash() {
        // theoretically up to 65535 but let's be greedy and waste 10 ms
        for (int port = 0; port < 100000; ++port) {
            String hash = Utils.unhash(port);
            Assert.assertThat(hash, CoreMatchers.notNullValue());
            Assert.assertThat(hash.hashCode(), CoreMatchers.is(port));
        }
    }

    @Test
    public void testRealloc() {
        Integer[] src = new Integer[]{ 1, 3, 5 };
        Integer[] dest = Utils.realloc(Integer.class, src, 3, true);
        Assert.assertThat(src.length, CoreMatchers.is(3));
        Assert.assertThat(src, CoreMatchers.is(dest));
        dest = Utils.realloc(Integer.class, src, 5, true);
        Assert.assertThat(dest.length, CoreMatchers.is(5));
        Assert.assertThat(dest[0], CoreMatchers.is(1));
        Assert.assertThat(dest[1], CoreMatchers.is(3));
        Assert.assertThat(dest[2], CoreMatchers.is(5));
        Assert.assertThat(dest[4], CoreMatchers.nullValue());
        dest = Utils.realloc(Integer.class, src, 6, false);
        Assert.assertThat(dest.length, CoreMatchers.is(6));
        Assert.assertThat(dest[0], CoreMatchers.nullValue());
        Assert.assertThat(dest[1], CoreMatchers.nullValue());
        Assert.assertThat(dest[2], CoreMatchers.nullValue());
        Assert.assertThat(dest[3], CoreMatchers.is(1));
        Assert.assertThat(dest[4], CoreMatchers.is(3));
        Assert.assertThat(dest[5], CoreMatchers.is(5));
        src = new Integer[]{ 1, 3, 5, 7, 9, 11 };
        dest = Utils.realloc(Integer.class, src, 4, false);
        Assert.assertThat(dest.length, CoreMatchers.is(4));
        Assert.assertThat(dest[0], CoreMatchers.is(1));
        Assert.assertThat(dest[1], CoreMatchers.is(3));
        Assert.assertThat(dest[2], CoreMatchers.is(5));
        Assert.assertThat(dest[3], CoreMatchers.is(7));
        dest = Utils.realloc(Integer.class, src, 3, true);
        Assert.assertThat(dest.length, CoreMatchers.is(3));
        Assert.assertThat(dest[0], CoreMatchers.is(7));
        Assert.assertThat(dest[1], CoreMatchers.is(9));
        Assert.assertThat(dest[2], CoreMatchers.is(11));
    }

    @Test
    public void testDeleteFile() throws IOException {
        Path path = Files.createTempFile("test", "suffix");
        Assert.assertThat(Files.exists(path), CoreMatchers.is(true));
        Utils.delete(path.toFile());
        Assert.assertThat(Files.exists(path), CoreMatchers.is(false));
    }

    @Test
    public void testDeleteDir() throws IOException {
        Path dir = Files.createTempDirectory("test");
        Path path = Files.createTempFile(dir, "test", "suffix");
        Utils.delete(dir.toFile());
        Assert.assertThat(Files.exists(dir), CoreMatchers.is(false));
        Assert.assertThat(Files.exists(path), CoreMatchers.is(false));
    }

    @Test
    public void testDump() throws IOException {
        byte[] array = new byte[10];
        ByteBuffer buffer = ByteBuffer.wrap(array);
        String dump = Utils.dump(buffer, 0, 10);
        Assert.assertThat(dump, CoreMatchers.is("[0,0,0,0,0,0,0,0,0,0,]"));
    }

    @Test
    public void testBytes() throws IOException {
        byte[] array = new byte[10];
        ByteBuffer buffer = ByteBuffer.wrap(array);
        byte[] bytes = Utils.bytes(buffer);
        Assert.assertThat(bytes, CoreMatchers.is(array));
    }

    @Test
    public void testCheckingCorrectArgument() {
        try {
            Utils.checkArgument(true, "Error");
        } catch (Throwable t) {
            Assert.fail("Checking argument should not fail");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckingIncorrectArgument() {
        Utils.checkArgument(false, "Error");
    }
}

