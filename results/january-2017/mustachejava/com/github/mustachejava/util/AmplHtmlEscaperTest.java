

package com.github.mustachejava.util;


public class AmplHtmlEscaperTest extends junit.framework.TestCase {
    public void testEscape() throws java.lang.Exception {
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("Hello, world!", sw);
            junit.framework.TestCase.assertEquals("Hello, world!", sw.toString());
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("Hello & world!", sw);
            junit.framework.TestCase.assertEquals("Hello &amp; world!", sw.toString());
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("Hello &amp; world!", sw);
            junit.framework.TestCase.assertEquals("Hello &amp;amp; world!", sw.toString());
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("Hello &amp world!", sw);
            junit.framework.TestCase.assertEquals("Hello &amp;amp world!", sw.toString());
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp world!", sw);
            junit.framework.TestCase.assertEquals("&quot;Hello&quot; &amp;amp world!", sw.toString());
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp world!&#10;", sw);
            junit.framework.TestCase.assertEquals("&quot;Hello&quot; &amp;amp world!&amp;#10;", sw.toString());
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp <world>!\n", sw);
            junit.framework.TestCase.assertEquals("&quot;Hello&quot; &amp;amp &lt;world&gt;!&#10;", sw.toString());
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp world!\n&sam", sw);
            junit.framework.TestCase.assertEquals("&quot;Hello&quot; &amp;amp world!&#10;&amp;sam", sw.toString());
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp \'world\'!\n&sam", sw);
            junit.framework.TestCase.assertEquals("&quot;Hello&quot; &amp;amp &#39;world&#39;!&#10;&amp;sam", sw.toString());
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp \'world\'!\n&sam", sw);
            junit.framework.TestCase.assertEquals("&quot;Hello&quot; &amp;amp &#39;world&#39;!&#10;&amp;sam", sw.toString());
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp&#zz \'world\'!\n&sam", sw);
            junit.framework.TestCase.assertEquals("&quot;Hello&quot; &amp;amp&amp;#zz &#39;world&#39;!&#10;&amp;sam", sw.toString());
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp&#zz \'world\'!\n&sam&#", sw);
            junit.framework.TestCase.assertEquals("&quot;Hello&quot; &amp;amp&amp;#zz &#39;world&#39;!&#10;&amp;sam&amp;#", sw.toString());
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" =` \'world\'!", sw);
            junit.framework.TestCase.assertEquals("&quot;Hello&quot; &#61;&#96; &#39;world&#39;!", sw.toString());
        }
    }
}

