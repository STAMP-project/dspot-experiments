package org.springside.modules.utils.text;


import org.junit.Test;

import static Charsets.UTF_8;


public class EncodeUtilTest {
    @Test
    public void hexEncode() {
        String input = "haha,i am a very long message";
        String result = EncodeUtil.encodeHex(input.getBytes());
        assertThat(new String(EncodeUtil.decodeHex(result), UTF_8)).isEqualTo(input);
        byte[] bytes = new byte[]{ 1, 2, 15, 17 };
        result = EncodeUtil.encodeHex(bytes);
        assertThat(result).isEqualTo("01020F11");
        input = "01020F11";
        assertThat(EncodeUtil.decodeHex(input)).hasSize(4).containsSequence(((byte) (1)), ((byte) (2)), ((byte) (15)), ((byte) (17)));
        try {
            input = "01020G11";
            EncodeUtil.decodeHex(input);
            fail("should throw exception before");
        } catch (Throwable t) {
            assertThat(t).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    public void base64Encode() {
        String input = "haha,i am a very long message";
        String result = EncodeUtil.encodeBase64(input.getBytes());
        assertThat(new String(EncodeUtil.decodeBase64(result), UTF_8)).isEqualTo(input);
        byte[] bytes = new byte[]{ 5 };
        result = EncodeUtil.encodeBase64(bytes);
        assertThat(result).isEqualTo("BQ==");
        bytes = new byte[]{ 1, 2, 15, 17, 127 };
        result = EncodeUtil.encodeBase64(bytes);
        assertThat(result).isEqualTo("AQIPEX8=");
    }

    @Test
    public void base64UrlSafeEncode() {
        String input = "haha,i am a very long message";
        String result = EncodeUtil.encodeBase64UrlSafe(input.getBytes());
        assertThat(new String(EncodeUtil.decodeBase64UrlSafe(result), UTF_8)).isEqualTo(input);
        try {
            assertThat(result).isEqualTo(EncodeUtil.decodeBase64UrlSafe("AQIPE+8="));
            fail("should throw exception before");
        } catch (Throwable t) {
            assertThat(t).isInstanceOf(IllegalArgumentException.class);
        }
    }
}

