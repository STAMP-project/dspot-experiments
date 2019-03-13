package cn.hutool.http.test;


import cn.hutool.http.HtmlUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 * Html????
 *
 * @author looly
 */
public class HtmlUtilTest {
    @Test
    public void removeHtmlTagTest() {
        // ?????
        String str = "pre<img src=\"xxx/dfdsfds/test.jpg\">";
        String result = HtmlUtil.removeHtmlTag(str, "img");
        Assert.assertEquals("pre", result);
        // ????
        str = "pre<img>";
        result = HtmlUtil.removeHtmlTag(str, "img");
        Assert.assertEquals("pre", result);
        // ????
        str = "pre<img src=\"xxx/dfdsfds/test.jpg\" />";
        result = HtmlUtil.removeHtmlTag(str, "img");
        Assert.assertEquals("pre", result);
        // ????
        str = "pre<img />";
        result = HtmlUtil.removeHtmlTag(str, "img");
        Assert.assertEquals("pre", result);
        // ??????
        str = "pre<div class=\"test_div\">dfdsfdsfdsf</div>";
        result = HtmlUtil.removeHtmlTag(str, "div");
        Assert.assertEquals("pre", result);
        // ???
        str = "pre<div class=\"test_div\">\r\n\t\tdfdsfdsfdsf\r\n</div>";
        result = HtmlUtil.removeHtmlTag(str, "div");
        Assert.assertEquals("pre", result);
    }

    @Test
    public void unwrapHtmlTagTest() {
        // ?????
        String str = "pre<img src=\"xxx/dfdsfds/test.jpg\">";
        String result = HtmlUtil.unwrapHtmlTag(str, "img");
        Assert.assertEquals("pre", result);
        // ????
        str = "pre<img>";
        result = HtmlUtil.unwrapHtmlTag(str, "img");
        Assert.assertEquals("pre", result);
        // ????
        str = "pre<img src=\"xxx/dfdsfds/test.jpg\" />";
        result = HtmlUtil.unwrapHtmlTag(str, "img");
        Assert.assertEquals("pre", result);
        // ????
        str = "pre<img />";
        result = HtmlUtil.unwrapHtmlTag(str, "img");
        Assert.assertEquals("pre", result);
        // ??????
        str = "pre<div class=\"test_div\">abc</div>";
        result = HtmlUtil.unwrapHtmlTag(str, "div");
        Assert.assertEquals("preabc", result);
        // ???
        str = "pre<div class=\"test_div\">\r\n\t\tabc\r\n</div>";
        result = HtmlUtil.unwrapHtmlTag(str, "div");
        Assert.assertEquals("pre\r\n\t\tabc\r\n", result);
    }

    @Test
    public void escapeTest() {
        String html = "<html><body>123'123'</body></html>";
        String escape = HtmlUtil.escape(html);
        String restoreEscaped = HtmlUtil.unescape(escape);
        Assert.assertEquals(html, restoreEscaped);
    }

    @Test
    public void filterTest() {
        String html = "<alert></alert>";
        String filter = HtmlUtil.filter(html);
        Assert.assertEquals("", filter);
    }
}

