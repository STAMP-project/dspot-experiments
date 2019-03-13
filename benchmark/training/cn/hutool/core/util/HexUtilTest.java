package cn.hutool.core.util;


import CharsetUtil.CHARSET_UTF_8;
import org.junit.Assert;
import org.junit.Test;


/**
 * HexUtil????
 *
 * @author Looly
 */
public class HexUtilTest {
    @Test
    public void hexStrTest() {
        String str = "???????";
        String hex = HexUtil.encodeHexStr(str, CHARSET_UTF_8);
        String decodedStr = HexUtil.decodeHexStr(hex);
        Assert.assertEquals(str, decodedStr);
    }

    @Test
    public void toUnicodeHexTest() {
        String unicodeHex = HexUtil.toUnicodeHex('\u2001');
        Assert.assertEquals("\\u2001", unicodeHex);
        unicodeHex = HexUtil.toUnicodeHex('?');
        Assert.assertEquals("\\u4f60", unicodeHex);
    }

    @Test
    public void isHexNumberTest() {
        String a = "0x3544534F444";
        boolean isHex = HexUtil.isHexNumber(a);
        Assert.assertTrue(isHex);
    }
}

