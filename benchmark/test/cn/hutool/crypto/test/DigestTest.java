package cn.hutool.crypto.test;


import CharsetUtil.CHARSET_UTF_8;
import cn.hutool.core.io.IoUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.Digester;
import org.junit.Assert;
import org.junit.Test;


/**
 * ????????
 *
 * @author Looly
 */
public class DigestTest {
    @Test
    public void digesterTest() {
        String testStr = "test??";
        Digester md5 = new Digester(DigestAlgorithm.MD5);
        String digestHex = md5.digestHex(testStr);
        Assert.assertEquals("5393554e94bf0eb6436f240a4fd71282", digestHex);
    }

    @Test
    public void md5Test() {
        String testStr = "test??";
        String md5Hex1 = DigestUtil.md5Hex(testStr);
        Assert.assertEquals("5393554e94bf0eb6436f240a4fd71282", md5Hex1);
        String md5Hex2 = DigestUtil.md5Hex(IoUtil.toStream(testStr, CHARSET_UTF_8));
        Assert.assertEquals("5393554e94bf0eb6436f240a4fd71282", md5Hex2);
    }

    @Test
    public void md5WithSaltTest() {
        String testStr = "test??";
        Digester md5 = new Digester(DigestAlgorithm.MD5);
        // ??
        md5.setSalt("saltTest".getBytes());
        String md5Hex1 = md5.digestHex(testStr);
        Assert.assertEquals("762f7335200299dfa09bebbb601a5bc6", md5Hex1);
        String md5Hex2 = md5.digestHex(IoUtil.toUtf8Stream(testStr));
        Assert.assertEquals("762f7335200299dfa09bebbb601a5bc6", md5Hex2);
        // ??2?
        md5.setDigestCount(2);
        String md5Hex3 = md5.digestHex(testStr);
        Assert.assertEquals("2b0616296f6755d25efc07f90afe9684", md5Hex3);
        String md5Hex4 = md5.digestHex(IoUtil.toUtf8Stream(testStr));
        Assert.assertEquals("2b0616296f6755d25efc07f90afe9684", md5Hex4);
    }

    @Test
    public void sha1Test() {
        String testStr = "test??";
        String sha1Hex1 = DigestUtil.sha1Hex(testStr);
        Assert.assertEquals("ecabf586cef0d3b11c56549433ad50b81110a836", sha1Hex1);
        String sha1Hex2 = DigestUtil.sha1Hex(IoUtil.toStream(testStr, CHARSET_UTF_8));
        Assert.assertEquals("ecabf586cef0d3b11c56549433ad50b81110a836", sha1Hex2);
    }

    @Test
    public void hash256Test() {
        String testStr = "Test??";
        String hex = DigestUtil.sha256Hex(testStr);
        Assert.assertEquals(64, hex.length());
    }
}

