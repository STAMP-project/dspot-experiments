package com.alibaba.json.test;


import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import junit.framework.TestCase;


/**
 * Created by wenshao on 24/07/2017.
 */
public class UTF8Test extends TestCase {
    String T0 = "?????????82???????? ????????????????????????????????????????????????????????????????????22????????18?????????????????????????????????????????????????????82????????";

    String T1 = "Model and actress Emily Ratajkowski would you like you to know she has wonderful abs. We don?t know this because we?re psychic, but rather can surmise this desire from her many photos she posts on Instagram. Whether it?s due to genetics, diet, great Instagram techniques, or some combination of the above, the rising star takes ample opportunity to show you what she?s got, and her fans love it.";

    Charset charset = Charset.forName("UTF-8");

    String text = new StringBuilder().append(T0).append(System.currentTimeMillis()).toString();

    // String text = "Model and actress Emily Ratajkowski would you like you to know she has wonderful abs. We don?t know this because we?re psychic, but rather can surmise this desire from her many photos she posts on Instagram. Whether it?s due to genetics, diet, great Instagram techniques, or some combination of the above, the rising star takes ample opportunity to show you what she?s got, and her fans love it.";
    char[] chars = text.toCharArray();

    byte[] bytes = new byte[(chars.length) * 3];

    ByteBuffer byteBuffer;

    public void test_encode() throws Exception {
        // for (int i = 0; i < 5; ++i) {
        // f0();
        // }
        for (int i = 0; i < 5; ++i) {
            f1();
        }
        // for (int i = 0; i < 5; ++i) {
        // f2();
        // }
    }

    static final int COUNT = (1000 * 1000) * 5;
}

