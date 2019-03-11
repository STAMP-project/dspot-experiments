package com.alibaba.json.bvt.util;


import com.alibaba.fastjson.util.ThreadLocalCache;
import com.alibaba.fastjson.util.UTF8Decoder;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import junit.framework.TestCase;


public class UTF8DecoderTest extends TestCase {
    public void test_0() throws Exception {
        CharsetDecoder decoder = ThreadLocalCache.getUTF8Decoder();
        String str = "asdfl???????????????17??????????2015?????????????????14??????????????????????????????????????????2020??";
        {
            byte[] bytes = str.getBytes("UTF-8");
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            decoder.decode(byteBuffer);
        }
        try {
            byte[] bytes = str.getBytes("GB18030");
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            decoder.decode(byteBuffer);
        } catch (CharacterCodingException ex) {
        }
    }

    public void test_1() throws Exception {
        int len = ((Character.MAX_VALUE) - (Character.MIN_VALUE)) + 1;
        char[] chars = new char[len];
        for (int i = 0; i < len; ++i) {
            char ch = ((char) (((int) (Character.MAX_VALUE)) + i));
            if ((ch >= 55296) && (ch <= 57344)) {
                continue;
            }
            chars[i] = ch;
        }
        String str = new String(chars);
        CharsetDecoder decoder = ThreadLocalCache.getUTF8Decoder();
        {
            byte[] bytes = str.getBytes("UTF-8");
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            decoder.decode(byteBuffer);
        }
        try {
            byte[] bytes = str.getBytes("GB18030");
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            decoder.decode(byteBuffer);
        } catch (CharacterCodingException ex) {
        }
    }

    public void test_2() throws Exception {
        CharsetDecoder decoder = ThreadLocalCache.getUTF8Decoder();
        String str = "\u5ac9\u59ac\u5fc3\u3092\u6b62\u3081\u3089\u308c\u306a\u3044\n" + "?????????A????????????????????????????????????????????";
        {
            byte[] bytes = str.getBytes("UTF-8");
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            decoder.decode(byteBuffer);
        }
        try {
            byte[] bytes = str.getBytes("GB18030");
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            decoder.decode(byteBuffer);
        } catch (CharacterCodingException ex) {
        }
    }

    public void test_3() throws Exception {
        CharsetDecoder decoder = ThreadLocalCache.getUTF8Decoder();
        String str = "\u9762\u7684\u85cf\u6587\u6709\u4e00\u4e2a\u97f3\u8282\u201c\u0f56\u0f66\u0f92\u0fb2\u0f7c\u0f53\u0f51\u201d\uff08\u5a01\u5229\u8f6c\u5199\uff1absgrond\uff09\uff0c\u7531\u524d\u52a0\u5b57ba\u3001\u4e0a\u52a0\u5b57sa\uff0c\u57fa\u5b57ga,\u4e0b\u52a0\u5b57ra\uff0c\u5143\u97f3o\u3001\u7b2c\u4e00\u540e\u52a0\u5b57na\u3001\u7b2c\u4e8c\u540e\u52a0\u5b57da\u6784\u6210\u3002bsgrond\u662f7\u4e16\u7eaa\u7684\u85cf\u8bed\u8bed\u97f3\uff0c\u968f\u7740\u73b0\u5728\u62c9\u8428\u97f3\u91cc\u590d\u8f85\u97f3\u4ee5\u53ca\u90e8\u5206\u97f5\u5c3e\u7684\u6d88\u5931\u548c\u58f0\u8c03\u7684\u51fa\u73b0\uff0c\u8be5\u8bcd\u5df2\u8f6c\u53d8\u8bfb\u6210/\u0288\u0282\u00f8\u0303\u02e9\u02e8/\uff08\u85cf\u8bed\u62fc\u97f3\uff1azh\u00f6n\uff0c\u85cf\u6587\u62c9\u8428\u97f3\u62fc\u97f3\uff1azhoenv\uff09\u3002\n" + (((((("\n" + "\u524d\u52a0\u5b57\u53ea\u80fd\u662f \u0f42 /g/\u3001 \u0f51 /d/\u3001 \u0f56 /b/\u3001 \u0f58 /m/\u3001 \u0f60 /\u0266/\u3002\n") + "\u4e0a\u52a0\u5b57\u53ea\u80fd\u662f \u0f6a /r/\u3001 \u0f63 /l/\u3001 \u0f66 /s/\u3002\n") + "\u4e0b\u52a0\u5b57\u53ea\u80fd\u662f \u25cc\u0fb2 /r/\u3001 \u25cc\u0fb1 /j/\u3001 \u25cc\u0fad /w/\u3001 \u25cc\u0fb3 /l/ \u548c\u7528\u4e8e\u97f3\u8bd1\u68b5\u6587\u91cc\u9001\u6c14\u6d4a\u8f85\u97f3\u7684\u9001\u6c14\u7b26\u53f7 \u25cc\u0fb7\uff0c\u6709\u4e00\u4e2a\u590d\u8f85\u97f3 \u0f42\u0fb2\u0fad /grwa/ \u6709\u4e24\u4e2a\u4e0b\u52a0\u5b57 \u25cc\u0fb2 /r/ \u548c \u25cc\u0fad /w/\u3002\n") + "\u7b2c\u4e00\u540e\u52a0\u5b57\u53ea\u53ef\u80fd\u662f \u0f6a /r/\u3001 \u0f42 /g/\u3001 \u0f56 /b/\u3001 \u0f58 /m/\u3001 \u0f60 /\u0266/\u3001 \u0f44 /\u014b/\u3001 \u0f66 /s/\u3001 \u0f51 /d/\u3001 \u0f53 /n/\u3001 \u0f63 /l/\u3002\n") + "\u7b2c\u4e8c\u540e\u52a0\u5b57\u53ea\u53ef\u80fd\u662f \u0f66 /s/ \u548c \u0f51 /d/\uff0c\u5728\u73b0\u4ee3\u85cf\u8bed\u91cc\u4e0d\u518d\u53d1\u97f3\uff0c\u0f51 /d/ \u5728\u73b0\u4ee3\u85cf\u8bed\u4e2d\u5df2\u7ecf\u4e0d\u7528\u3002\n") + "??????????????????????????????????????????????????");
        {
            byte[] bytes = str.getBytes("UTF-8");
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            decoder.decode(byteBuffer);
        }
        try {
            byte[] bytes = str.getBytes("GB18030");
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            decoder.decode(byteBuffer);
        } catch (CharacterCodingException ex) {
        }
    }

    public void test_4() throws Exception {
        CharsetDecoder decoder = ThreadLocalCache.getUTF8Decoder();
        String str = "\ud83e\udd17 on Instagram\n" + (((((((((((((("\ud83e\udd17 on Twitter\n" + "\ud83e\udd17 on Wikipedia\n") + "\ud83e\udd17 on Yelp\n") + "\ud83e\udd17 on YouTube\n") + "\ud83e\udd17 on Google Trends\n") + "See also\n") + "\ud83c\udfe5 Hospital\n") + "\ud83d\udc50 Open Hands\n") + "\ud83e\udd68 Pretzel\n") + "\ud83d\ude42 Slightly Smiling Face\n") + "\ud83e\udd27 Sneezing Face\n") + "\ud83e\udd14 Thinking Face\n") + "\ud83d\udc95 Two Hearts\n") + "\u263a Smiling Face\n") + "\ud83d\udd00 Random emoji");
        {
            byte[] bytes = str.getBytes("UTF-8");
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            decoder.decode(byteBuffer);
        }
        try {
            byte[] bytes = str.getBytes("GB18030");
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            decoder.decode(byteBuffer);
        } catch (CharacterCodingException ex) {
        }
    }

    /**
     *
     *
     * @deprecated 
     */
    public void test_5() throws Exception {
        UTF8Decoder decoder = new UTF8Decoder();
        String str = "\u231b\ufe0e\u20ac\ud83d\udc69\ud83d\udc68\ud83d\udc68\ud83c\udffb\ud83d\udc69\ud83c\udfffU+1F9D2: Child\tText\t\ud83e\uddd2\t\ud83e\uddd2\ud83c\udffb\t\ud83e\uddd2\ud83c\udffc\t\ud83e\uddd2\ud83c\udffd\t\ud83e\uddd2\ud83c\udffe\t\ud83e\uddd2\ud83c\udfff\n\ud83e\uddd1\ud83c\udfff\ud83c\ude1a\ufe0f\ud83c\udc04\ufe0f\u2764\ufe0f";
        byte[] bytes = str.getBytes("UTF-8");
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        decoder.decode(byteBuffer);
    }

    /**
     *
     *
     * @deprecated 
     */
    public void test_6() throws Exception {
        UTF8Decoder decoder = new UTF8Decoder();
        String str = "\u20ac";
        byte[] bytes = str.getBytes("UTF-8");
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        decoder.decode(byteBuffer);
    }
}

