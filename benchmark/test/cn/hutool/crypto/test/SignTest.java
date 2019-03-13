package cn.hutool.crypto.test;


import SignAlgorithm.MD2withRSA;
import SignAlgorithm.MD5withRSA;
import SignAlgorithm.NONEwithDSA;
import SignAlgorithm.NONEwithECDSA;
import SignAlgorithm.NONEwithRSA;
import SignAlgorithm.SHA1withDSA;
import SignAlgorithm.SHA1withECDSA;
import SignAlgorithm.SHA1withRSA;
import SignAlgorithm.SHA256withECDSA;
import SignAlgorithm.SHA256withRSA;
import SignAlgorithm.SHA384withECDSA;
import SignAlgorithm.SHA384withRSA;
import SignAlgorithm.SHA512withECDSA;
import SignAlgorithm.SHA512withRSA;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.Sign;
import org.junit.Assert;
import org.junit.Test;


/**
 * ??????
 *
 * @author looly
 */
public class SignTest {
    @Test
    public void signAndVerifyUseKeyTest() {
        String content = "??Hanley.";
        String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJ4fG8vJ0tzu7tjXMSJhyNjlE5B7GkTKMKEQlR6LY3IhIhMFVjuA6W+DqH1VMxl9h3GIM4yCKG2VRZEYEPazgVxa5/ifO8W0pfmrzWCPrddUq4t0Slz5u2lLKymLpPjCzboHoDb8VlF+1HOxjKQckAXq9q7U7dV5VxOzJDuZXlz3AgMBAAECgYABo2LfVqT3owYYewpIR+kTzjPIsG3SPqIIWSqiWWFbYlp/BfQhw7EndZ6+Ra602ecYVwfpscOHdx90ZGJwm+WAMkKT4HiWYwyb0ZqQzRBGYDHFjPpfCBxrzSIJ3QL+B8c8YHq4HaLKRKmq7VUF1gtyWaek87rETWAmQoGjt8DyAQJBAOG4OxsT901zjfxrgKwCv6fV8wGXrNfDSViP1t9r3u6tRPsE6Gli0dfMyzxwENDTI75sOEAfyu6xBlemQGmNsfcCQQCzVWQkl9YUoVDWEitvI5MpkvVKYsFLRXKvLfyxLcY3LxpLKBcEeJ/n5wLxjH0GorhJMmM2Rw3hkjUTJCoqqe0BAkATt8FKC0N2O5ryqv1xiUfuxGzW/cX2jzOwDdiqacTuuqok93fKBPzpyhUS8YM2iss7jj6Xs29JzKMOMxK7ZcpfAkAf21lwzrAu9gEgJhYlJhKsXfjJAAYKUwnuaKLs7o65mtp242ZDWxI85eK1+hjzptBJ4HOTXsfufESFY/VBovIBAkAltO886qQRoNSc0OsVlCi4X1DGo6x2RqQ9EsWPrxWEZGYuyEdODrc54b8L+zaUJLfMJdsCIHEUbM7WXxvFVXNv";
        Sign sign = SecureUtil.sign(SHA1withRSA, privateKey, null);
        Assert.assertNull(sign.getPublicKeyBase64());
        // ??
        byte[] signed = sign.sign(content.getBytes());
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCeHxvLydLc7u7Y1zEiYcjY5ROQexpEyjChEJUei2NyISITBVY7gOlvg6h9VTMZfYdxiDOMgihtlUWRGBD2s4FcWuf4nzvFtKX5q81gj63XVKuLdEpc+btpSyspi6T4ws26B6A2/FZRftRzsYykHJAF6vau1O3VeVcTsyQ7mV5c9wIDAQAB";
        sign = SecureUtil.sign(SHA1withRSA, null, publicKey);
        // ????
        boolean verify = sign.verify(content.getBytes(), signed);
        Assert.assertTrue(verify);
    }

    @Test
    public void signAndVerifyTest() {
        signAndVerify(NONEwithRSA);
        signAndVerify(MD2withRSA);
        signAndVerify(MD5withRSA);
        signAndVerify(SHA1withRSA);
        signAndVerify(SHA256withRSA);
        signAndVerify(SHA384withRSA);
        signAndVerify(SHA512withRSA);
        signAndVerify(NONEwithDSA);
        signAndVerify(SHA1withDSA);
        signAndVerify(NONEwithECDSA);
        signAndVerify(SHA1withECDSA);
        signAndVerify(SHA1withECDSA);
        signAndVerify(SHA256withECDSA);
        signAndVerify(SHA384withECDSA);
        signAndVerify(SHA512withECDSA);
    }

    /**
     * ??MD5withRSA??????????
     */
    @Test
    public void signAndVerify2() {
        String str = "wx2421b1c4370ec43b ???? JSAPI???? 10000100 1add1a30ac87aa2db72f57a2375d8fec http://wxpay.wxutil.com/pub_v2/pay/notify.v2.php oUpF8uMuAJO_M2pxb1Q9zNjWeS6o 1415659990 14.23.150.211 1 JSAPI 0CB01533B8C1EF103065174F50BCA001";
        byte[] data = StrUtil.utf8Bytes(str);
        Sign sign = SecureUtil.sign(MD5withRSA);
        // ??
        byte[] signed = sign.sign(data);
        // ????
        boolean verify = sign.verify(data, signed);
        Assert.assertTrue(verify);
    }
}

