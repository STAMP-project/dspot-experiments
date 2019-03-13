package cn.hutool.crypto.test;


import CharsetUtil.CHARSET_UTF_8;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import org.junit.Assert;
import org.junit.Test;


/**
 * SM????
 *
 * @author looly
 */
public class SmTest {
    @Test
    public void sm3Test() {
        String digestHex = SmUtil.sm3("aaaaa");
        Assert.assertEquals("136ce3c86e4ed909b76082055a61586af20b4dab674732ebd4b599eef080c9be", digestHex);
    }

    @Test
    public void sm4Test() {
        String content = "test??";
        SymmetricCrypto sm4 = SmUtil.sm4();
        String encryptHex = sm4.encryptHex(content);
        String decryptStr = sm4.decryptStr(encryptHex, CHARSET_UTF_8);
        Assert.assertEquals(content, decryptStr);
    }

    @Test
    public void sm4Test2() {
        String content = "test??";
        SymmetricCrypto sm4 = new SymmetricCrypto("SM4/ECB/PKCS5Padding");
        String encryptHex = sm4.encryptHex(content);
        String decryptStr = sm4.decryptStr(encryptHex, CHARSET_UTF_8);
        Assert.assertEquals(content, decryptStr);
    }
}

