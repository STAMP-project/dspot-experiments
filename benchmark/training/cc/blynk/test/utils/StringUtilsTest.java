package cc.blynk.test.utils;


import StringUtils.BODY_SEPARATOR_STRING;
import cc.blynk.utils.StringUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/18/2015.
 */
public class StringUtilsTest {
    @Test
    public void testCorrectFastNewSplit() {
        String in = "ar 1 2 3 4 5 6".replaceAll(" ", "\u0000");
        String res = StringUtils.fetchPin(in);
        Assert.assertNotNull(res);
        Assert.assertEquals("1", res);
        in = "ar 22222".replaceAll(" ", "\u0000");
        res = StringUtils.fetchPin(in);
        Assert.assertNotNull(res);
        Assert.assertEquals("22222", res);
        in = "1 1".replaceAll(" ", "\u0000");
        res = StringUtils.fetchPin(in);
        Assert.assertNotNull(res);
        Assert.assertEquals("", res);
    }

    @Test
    public void testCorrectSplit3() {
        String[] res = StringUtils.split3("aw 1 2".replaceAll(" ", BODY_SEPARATOR_STRING));
        Assert.assertEquals("aw", res[0]);
        Assert.assertEquals("1", res[1]);
        Assert.assertEquals("2", res[2]);
        res = StringUtils.split3("dw 11 22".replaceAll(" ", BODY_SEPARATOR_STRING));
        Assert.assertEquals("dw", res[0]);
        Assert.assertEquals("11", res[1]);
        Assert.assertEquals("22", res[2]);
        String s = "sdfsafdfdgfjdasfjds;lfjd;lsf dsfld;las fd;slaj fd;lsfj das";
        res = StringUtils.split3((("vw 255 ".replaceAll(" ", BODY_SEPARATOR_STRING)) + s));
        Assert.assertEquals("vw", res[0]);
        Assert.assertEquals("255", res[1]);
        Assert.assertEquals(s, res[2]);
        s = "sdfsafdfdgfjdasfjds;lfjd;lsf\u0000dsfld;las\u0000fd;slaj\u0000fd;lsfj\u0000das";
        res = StringUtils.split3((("vw 255 ".replaceAll(" ", BODY_SEPARATOR_STRING)) + s));
        Assert.assertEquals("vw", res[0]);
        Assert.assertEquals("255", res[1]);
        Assert.assertEquals(s, res[2]);
    }

    @Test
    public void splitOk() {
        String body = "vw 1 123".replaceAll(" ", "\u0000");
        String[] stringSplitResult = body.split(BODY_SEPARATOR_STRING, 3);
        String[] customSplitResult = split3(body);
        Assert.assertArrayEquals(stringSplitResult, customSplitResult);
        body = "vw 1 123 124".replaceAll(" ", "\u0000");
        stringSplitResult = body.split(BODY_SEPARATOR_STRING, 3);
        customSplitResult = split3(body);
        Assert.assertArrayEquals(stringSplitResult, customSplitResult);
        body = "vw".replaceAll(" ", "\u0000");
        stringSplitResult = body.split(BODY_SEPARATOR_STRING, 3);
        customSplitResult = split3(body);
        Assert.assertArrayEquals(stringSplitResult, customSplitResult);
        body = "1 vw".replaceAll(" ", "\u0000");
        stringSplitResult = body.split(BODY_SEPARATOR_STRING, 3);
        customSplitResult = split3(body);
        Assert.assertArrayEquals(stringSplitResult, customSplitResult);
        body = "vw 2".replaceAll(" ", "\u0000");
        stringSplitResult = body.split(BODY_SEPARATOR_STRING, 3);
        customSplitResult = split3(body);
        Assert.assertArrayEquals(stringSplitResult, customSplitResult);
        body = "2 vw 2 222".replaceAll(" ", "\u0000");
        stringSplitResult = body.split(BODY_SEPARATOR_STRING, 3);
        customSplitResult = split3(body);
        Assert.assertArrayEquals(stringSplitResult, customSplitResult);
    }
}

