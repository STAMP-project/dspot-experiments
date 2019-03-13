package cn.hutool.core.util;


import org.junit.Assert;
import org.junit.Test;


/**
 * {@link ZipUtil}????
 *
 * @author Looly
 */
public class ZipUtilTest {
    @Test
    public void gzipTest() {
        String data = "?????????????????";
        byte[] bytes = StrUtil.utf8Bytes(data);
        byte[] gzip = ZipUtil.gzip(bytes);
        // ??gzip????
        Assert.assertEquals(68, gzip.length);
        byte[] unGzip = ZipUtil.unGzip(gzip);
        // ??????
        Assert.assertEquals(data, StrUtil.utf8Str(unGzip));
    }

    @Test
    public void zlibTest() {
        String data = "?????????????????";
        byte[] bytes = StrUtil.utf8Bytes(data);
        byte[] gzip = ZipUtil.zlib(bytes, 0);
        // ??gzip????
        Assert.assertEquals(56, gzip.length);
        byte[] unGzip = ZipUtil.unZlib(gzip);
        // ??????
        Assert.assertEquals(data, StrUtil.utf8Str(unGzip));
        gzip = ZipUtil.zlib(bytes, 9);
        // ??gzip????
        Assert.assertEquals(50, gzip.length);
        byte[] unGzip2 = ZipUtil.unZlib(gzip);
        // ??????
        Assert.assertEquals(data, StrUtil.utf8Str(unGzip2));
    }
}

