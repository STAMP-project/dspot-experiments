package cn.hutool.core.util;


import org.junit.Assert;
import org.junit.Test;


public class EscapeUtilTest {
    @Test
    public void escapeHtml4Test() {
        String escapeHtml4 = EscapeUtil.escapeHtml4("<a>??</a>");
        Assert.assertEquals("&lt;a&gt;??&lt;/a&gt;", escapeHtml4);
        String result = EscapeUtil.unescapeHtml4("&#25391;&#33633;&#22120;&#31867;&#22411;");
        Assert.assertEquals("?????", result);
    }
}

