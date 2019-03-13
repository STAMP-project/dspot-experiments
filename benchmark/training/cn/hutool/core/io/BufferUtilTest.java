package cn.hutool.core.io;


import CharsetUtil.CHARSET_UTF_8;
import cn.hutool.core.util.StrUtil;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;


/**
 * BufferUtil????
 *
 * @author looly
 */
public class BufferUtilTest {
    @Test
    public void copyTest() {
        byte[] bytes = "AAABBB".getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        ByteBuffer buffer2 = BufferUtil.copy(buffer, ByteBuffer.allocate(5));
        Assert.assertEquals("AAABB", StrUtil.utf8Str(buffer2));
    }

    @Test
    public void readBytesTest() {
        byte[] bytes = "AAABBB".getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        byte[] bs = BufferUtil.readBytes(buffer, 5);
        Assert.assertEquals("AAABB", StrUtil.utf8Str(bs));
    }

    @Test
    public void readBytes2Test() {
        byte[] bytes = "AAABBB".getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        byte[] bs = BufferUtil.readBytes(buffer, 5);
        Assert.assertEquals("AAABB", StrUtil.utf8Str(bs));
    }

    @Test
    public void readLineTest() {
        String text = "aa\r\nbbb\ncc";
        ByteBuffer buffer = ByteBuffer.wrap(text.getBytes());
        // ???
        String line = BufferUtil.readLine(buffer, CHARSET_UTF_8);
        Assert.assertEquals("aa", line);
        // ???
        line = BufferUtil.readLine(buffer, CHARSET_UTF_8);
        Assert.assertEquals("bbb", line);
        // ?????????????????null
        line = BufferUtil.readLine(buffer, CHARSET_UTF_8);
        Assert.assertNull(line);
        // ??????
        Assert.assertEquals("cc", StrUtil.utf8Str(BufferUtil.readBytes(buffer)));
    }
}

