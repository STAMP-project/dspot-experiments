package com.blankj.utilcode.util;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/08/12
 *     desc  : test EncodeUtils
 * </pre>
 */
public class EncodeUtilsTest extends BaseTest {
    @Test
    public void urlEncode_urlDecode() {
        String urlEncodeString = "%E5%93%88%E5%93%88%E5%93%88";
        Assert.assertEquals(urlEncodeString, EncodeUtils.urlEncode("???"));
        Assert.assertEquals(urlEncodeString, EncodeUtils.urlEncode("???", "UTF-8"));
        Assert.assertEquals("???", EncodeUtils.urlDecode(urlEncodeString));
        Assert.assertEquals("???", EncodeUtils.urlDecode(urlEncodeString, "UTF-8"));
    }

    @Test
    public void base64Decode_base64Encode() {
        Assert.assertTrue(Arrays.equals("blankj".getBytes(), EncodeUtils.base64Decode(EncodeUtils.base64Encode("blankj"))));
        Assert.assertTrue(Arrays.equals("blankj".getBytes(), EncodeUtils.base64Decode(EncodeUtils.base64Encode2String("blankj".getBytes()))));
        Assert.assertEquals("Ymxhbmtq", EncodeUtils.base64Encode2String("blankj".getBytes()));
        Assert.assertTrue(Arrays.equals("Ymxhbmtq".getBytes(), EncodeUtils.base64Encode("blankj".getBytes())));
    }

    @Test
    public void htmlEncode_htmlDecode() {
        String html = "<html>" + ((((((("<head>" + "<title>????? HTML ??</title>") + "</head>") + "<body>") + "<p>body ??????????????</p>") + "<p>title ??????????????????</p>") + "</body>") + "</html>");
        String encodeHtml = "&lt;html&gt;&lt;head&gt;&lt;title&gt;????? HTML ??&lt;/title&gt;&lt;/head&gt;&lt;body&gt;&lt;p&gt;body ??????????????&lt;/p&gt;&lt;p&gt;title ??????????????????&lt;/p&gt;&lt;/body&gt;&lt;/html&gt;";
        Assert.assertEquals(encodeHtml, EncodeUtils.htmlEncode(html));
        Assert.assertEquals(html, EncodeUtils.htmlDecode(encodeHtml).toString());
    }

    @Test
    public void binEncode_binDecode() {
        String test = "test";
        String binary = EncodeUtils.binEncode(test);
        Assert.assertEquals("test", EncodeUtils.binDecode(binary));
    }
}

