package cn.hutool.crypto.test;


import CharsetUtil.CHARSET_UTF_8;
import KeyType.PrivateKey;
import KeyType.PublicKey;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import java.security.KeyPair;
import org.junit.Assert;
import org.junit.Test;


/**
 * RSA??????
 *
 * @author Looly
 */
public class RSATest {
    @Test
    public void generateKeyPairTest() {
        KeyPair pair = SecureUtil.generateKeyPair("RSA");
        Assert.assertNotNull(pair.getPrivate());
        Assert.assertNotNull(pair.getPublic());
    }

    @Test
    public void rsaCustomKeyTest() {
        KeyPair pair = SecureUtil.generateKeyPair("RSA");
        byte[] privateKey = pair.getPrivate().getEncoded();
        byte[] publicKey = pair.getPublic().getEncoded();
        RSA rsa = SecureUtil.rsa(privateKey, publicKey);
        // ?????????
        byte[] encrypt = rsa.encrypt(StrUtil.bytes("??????aaaa", CHARSET_UTF_8), PublicKey);
        byte[] decrypt = rsa.decrypt(encrypt, PrivateKey);
        Assert.assertEquals("??????aaaa", StrUtil.str(decrypt, CHARSET_UTF_8));
        // ?????????
        byte[] encrypt2 = rsa.encrypt(StrUtil.bytes("??????aaaa", CHARSET_UTF_8), PrivateKey);
        byte[] decrypt2 = rsa.decrypt(encrypt2, PublicKey);
        Assert.assertEquals("??????aaaa", StrUtil.str(decrypt2, CHARSET_UTF_8));
    }

    @Test
    public void rsaTest() {
        final RSA rsa = new RSA();
        // ???????
        Assert.assertNotNull(rsa.getPrivateKey());
        Assert.assertNotNull(rsa.getPrivateKeyBase64());
        Assert.assertNotNull(rsa.getPublicKey());
        Assert.assertNotNull(rsa.getPrivateKeyBase64());
        // ?????????
        byte[] encrypt = rsa.encrypt(StrUtil.bytes("??????aaaa", CHARSET_UTF_8), PublicKey);
        byte[] decrypt = rsa.decrypt(encrypt, PrivateKey);
        Assert.assertEquals("??????aaaa", StrUtil.str(decrypt, CHARSET_UTF_8));
        // ?????????
        byte[] encrypt2 = rsa.encrypt(StrUtil.bytes("??????aaaa", CHARSET_UTF_8), PrivateKey);
        byte[] decrypt2 = rsa.decrypt(encrypt2, PublicKey);
        Assert.assertEquals("??????aaaa", StrUtil.str(decrypt2, CHARSET_UTF_8));
    }

    @Test
    public void rsaBcdTest() {
        String text = "??????aaaa";
        final RSA rsa = new RSA();
        // ?????????
        String encryptStr = rsa.encryptBcd(text, PublicKey);
        String decryptStr = StrUtil.utf8Str(rsa.decryptFromBcd(encryptStr, PrivateKey));
        Assert.assertEquals(text, decryptStr);
        // ?????????
        String encrypt2 = rsa.encryptBcd(text, PrivateKey);
        String decrypt2 = StrUtil.utf8Str(rsa.decryptFromBcd(encrypt2, PublicKey));
        Assert.assertEquals(text, decrypt2);
    }

    @Test
    public void rsaBase64Test() {
        String textBase = "??????????";
        String text = "";
        for (int i = 0; i < 10; i++) {
            text += textBase;
        }
        final RSA rsa = new RSA();
        // ?????????
        String encryptStr = rsa.encryptBase64(text, PublicKey);
        String decryptStr = StrUtil.utf8Str(rsa.decryptFromBase64(encryptStr, PrivateKey));
        Assert.assertEquals(text, decryptStr);
        // ?????????
        String encrypt2 = rsa.encryptBase64(text, PrivateKey);
        String decrypt2 = StrUtil.utf8Str(rsa.decryptFromBase64(encrypt2, PublicKey));
        Assert.assertEquals(text, decrypt2);
    }

    @Test
    public void rsaDecodeTest() {
        String PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIL7pbQ+5KKGYRhw7jE31hmA" + (((((((("f8Q60ybd+xZuRmuO5kOFBRqXGxKTQ9TfQI+aMW+0lw/kibKzaD/EKV91107xE384qOy6IcuBfaR5lv39OcoqNZ" + "5l+Dah5ABGnVkBP9fKOFhPgghBknTRo0/rZFGI6Q1UHXb+4atP++LNFlDymJcPAgMBAAECgYBammGb1alndta") + "xBmTtLLdveoBmp14p04D8mhkiC33iFKBcLUvvxGg2Vpuc+cbagyu/NZG+R/WDrlgEDUp6861M5BeFN0L9O4hz") + "GAEn8xyTE96f8sh4VlRmBOvVdwZqRO+ilkOM96+KL88A9RKdp8V2tna7TM6oI3LHDyf/JBoXaQJBAMcVN7fKlYP") + "Skzfh/yZzW2fmC0ZNg/qaW8Oa/wfDxlWjgnS0p/EKWZ8BxjR/d199L3i/KMaGdfpaWbYZLvYENqUCQQCobjsuCW") + "nlZhcWajjzpsSuy8/bICVEpUax1fUZ58Mq69CQXfaZemD9Ar4omzuEAAs2/uee3kt3AvCBaeq05NyjAkBme8SwB0iK") + "kLcaeGuJlq7CQIkjSrobIqUEf+CzVZPe+AorG+isS+Cw2w/2bHu+G0p5xSYvdH59P0+ZT0N+f9LFAkA6v3Ae56OrI") + "wfMhrJksfeKbIaMjNLS9b8JynIaXg9iCiyOHmgkMl5gAbPoH/ULXqSKwzBw5mJ2GW1gBlyaSfV3AkA/RJC+adIjsRGg") + "JOkiRjSmPpGv3FOhl9fsBPjupZBEIuoMWOC8GXK/73DHxwmfNmN7C9+sIi4RBcjEeQ5F5FHZ");
        RSA rsa = new RSA(PRIVATE_KEY, null);
        String a = "2707F9FD4288CEF302C972058712F24A5F3EC62C5A14AD2FC59DAB93503AA0FA17113A020EE4EA35EB53F" + ("75F36564BA1DABAA20F3B90FD39315C30E68FE8A1803B36C29029B23EB612C06ACF3A34BE815074F5EB5AA3A" + "C0C8832EC42DA725B4E1C38EF4EA1B85904F8B10B2D62EA782B813229F9090E6F7394E42E6F44494BB8");
        byte[] aByte = HexUtil.decodeHex(a);
        byte[] decrypt = rsa.decrypt(aByte, PrivateKey);
        Assert.assertEquals("?????,?????,??????", StrUtil.str(decrypt, CHARSET_UTF_8));
    }
}

