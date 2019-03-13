package com.alibaba.json.test;


import java.nio.CharBuffer;
import java.nio.charset.Charset;
import junit.framework.TestCase;


/**
 * Created by wenshao on 24/07/2017.
 */
public class UTF8Test_decode extends TestCase {
    String T0 = "?????????82???????? ????????????????????????????????????????????????????????????????????22????????18?????????????????????????????????????????????????????82????????";

    String T1 = "Model and actress Emily Ratajkowski would you like you to know she has wonderful abs. We don?t know this because we?re psychic, but rather can surmise this desire from her many photos she posts on Instagram. Whether it?s due to genetics, diet, great Instagram techniques, or some combination of the above, the rising star takes ample opportunity to show you what she?s got, and her fans love it.";

    Charset charset = Charset.forName("UTF-8");

    char[] chars;

    byte[] bytes;

    CharBuffer charBuffer;

    public void test_encode() throws Exception {
        String text = new StringBuilder().append(T0).append(System.currentTimeMillis()).toString();
        bytes = text.getBytes(charset);
        chars = new char[bytes.length];
        charBuffer = CharBuffer.allocate(bytes.length);
        for (int i = 0; i < 10; ++i) {
            long start = System.currentTimeMillis();
            f0();// 764

            // f1(); // 695
            // f2(); // 975
            long millis = (System.currentTimeMillis()) - start;
            System.out.println(("millis : " + millis));
        }
    }

    public void test_encode_en() throws Exception {
        String text = new StringBuilder().append(T1).append(System.currentTimeMillis()).toString();
        bytes = text.getBytes(charset);
        chars = new char[bytes.length];
        charBuffer = CharBuffer.allocate(bytes.length);
        for (int i = 0; i < 10; ++i) {
            long start = System.currentTimeMillis();
            f0();// 407 394

            // f1(); // 1296 1058
            // f2(); // 615 635
            long millis = (System.currentTimeMillis()) - start;
            System.out.println(("millis : " + millis));
        }
    }
}

