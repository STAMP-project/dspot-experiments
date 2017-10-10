package com.github.mustachejava.util;


public class AmplHtmlEscaperTest extends junit.framework.TestCase {
    @org.junit.Test(timeout = 10000)
    public void testEscape() throws java.lang.Exception {
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("Hello, world!", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape__5 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("Hello, world!", o_testEscape__5);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("Hello & world!", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape__10 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("Hello &amp; world!", o_testEscape__10);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("Hello &amp; world!", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape__15 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("Hello &amp;amp; world!", o_testEscape__15);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("Hello &amp world!", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape__20 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("Hello &amp;amp world!", o_testEscape__20);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp world!", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape__25 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp world!", o_testEscape__25);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp world!&#10;", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape__30 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp world!&amp;#10;", o_testEscape__30);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp <world>!\n", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape__35 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp &lt;world&gt;!&#10;", o_testEscape__35);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp world!\n&sam", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape__40 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp world!&#10;&amp;sam", o_testEscape__40);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp \'world\'!\n&sam", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape__45 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp &#39;world&#39;!&#10;&amp;sam", o_testEscape__45);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp \'world\'!\n&sam", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape__50 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp &#39;world&#39;!&#10;&amp;sam", o_testEscape__50);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp&#zz \'world\'!\n&sam", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape__55 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp&amp;#zz &#39;world&#39;!&#10;&amp;sam", o_testEscape__55);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp&#zz \'world\'!\n&sam&#", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape__60 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp&amp;#zz &#39;world&#39;!&#10;&amp;sam&amp;#", o_testEscape__60);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" =` \'world\'!", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape__65 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &#61;&#96; &#39;world&#39;!", o_testEscape__65);
        }
    }

    /* amplification of com.github.mustachejava.util.HtmlEscaperTest#testEscape */
    @org.junit.Test(timeout = 10000)
    public void testEscape_literalMutationString16() throws java.lang.Exception {
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("Hello, world!", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString16__5 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("Hello, world!", o_testEscape_literalMutationString16__5);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("Hello & world!", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString16__10 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("Hello &amp; world!", o_testEscape_literalMutationString16__10);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape(":ello &amp; world!", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString16__15 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(":ello &amp;amp; world!", o_testEscape_literalMutationString16__15);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("Hello &amp world!", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString16__20 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("Hello &amp;amp world!", o_testEscape_literalMutationString16__20);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp world!", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString16__25 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp world!", o_testEscape_literalMutationString16__25);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp world!&#10;", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString16__30 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp world!&amp;#10;", o_testEscape_literalMutationString16__30);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp <world>!\n", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString16__35 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp &lt;world&gt;!&#10;", o_testEscape_literalMutationString16__35);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp world!\n&sam", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString16__40 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp world!&#10;&amp;sam", o_testEscape_literalMutationString16__40);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp \'world\'!\n&sam", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString16__45 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp &#39;world&#39;!&#10;&amp;sam", o_testEscape_literalMutationString16__45);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp \'world\'!\n&sam", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString16__50 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp &#39;world&#39;!&#10;&amp;sam", o_testEscape_literalMutationString16__50);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp&#zz \'world\'!\n&sam", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString16__55 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp&amp;#zz &#39;world&#39;!&#10;&amp;sam", o_testEscape_literalMutationString16__55);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp&#zz \'world\'!\n&sam&#", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString16__60 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp&amp;#zz &#39;world&#39;!&#10;&amp;sam&amp;#", o_testEscape_literalMutationString16__60);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" =` \'world\'!", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString16__65 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &#61;&#96; &#39;world&#39;!", o_testEscape_literalMutationString16__65);
        }
    }

    /* amplification of com.github.mustachejava.util.HtmlEscaperTest#testEscape */
    /* amplification of com.github.mustachejava.util.HtmlEscaperTest#testEscape_literalMutationString42 */
    @org.junit.Test(timeout = 10000)
    public void testEscape_literalMutationString42_literalMutationString3542() throws java.lang.Exception {
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("Hello, world!", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString42__5 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("Hello, world!", o_testEscape_literalMutationString42__5);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("Hello & world!", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString42__10 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("Hello &amp; world!", o_testEscape_literalMutationString42__10);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("Hello &amp; world!", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString42__15 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("Hello &amp;amp; world!", o_testEscape_literalMutationString42__15);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("Hello &amp world!", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString42__20 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("Hello &amp;amp world!", o_testEscape_literalMutationString42__20);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp world!", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString42__25 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp world!", o_testEscape_literalMutationString42__25);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp world!&#10;", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString42__30 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp world!&amp;#10;", o_testEscape_literalMutationString42__30);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hllo\" &amp <world>!\n", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString42__35 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hllo&quot; &amp;amp &lt;world&gt;!&#10;", o_testEscape_literalMutationString42__35);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp world!\n&sam", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString42__40 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp world!&#10;&amp;sam", o_testEscape_literalMutationString42__40);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hell\" &amp \'world\'!\n&sam", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString42__45 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hell&quot; &amp;amp &#39;world&#39;!&#10;&amp;sam", o_testEscape_literalMutationString42__45);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp \'world\'!\n&sam", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString42__50 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp &#39;world&#39;!&#10;&amp;sam", o_testEscape_literalMutationString42__50);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp&#zz \'world\'!\n&sam", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString42__55 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp&amp;#zz &#39;world&#39;!&#10;&amp;sam", o_testEscape_literalMutationString42__55);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp&#zz \'world\'!\n&sam&#", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString42__60 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp&amp;#zz &#39;world&#39;!&#10;&amp;sam&amp;#", o_testEscape_literalMutationString42__60);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" =` \'world\'!", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString42__65 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &#61;&#96; &#39;world&#39;!", o_testEscape_literalMutationString42__65);
        }
    }

    /* amplification of com.github.mustachejava.util.HtmlEscaperTest#testEscape */
    /* amplification of com.github.mustachejava.util.HtmlEscaperTest#testEscape_literalMutationString54 */
    /* amplification of com.github.mustachejava.util.HtmlEscaperTest#testEscape_literalMutationString54_literalMutationString4483 */
    @org.junit.Test(timeout = 10000)
    public void testEscape_literalMutationString54_literalMutationString4483_literalMutationString13067() throws java.lang.Exception {
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("Hello, world!", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString54__5 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("Hello, world!", o_testEscape_literalMutationString54__5);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("Hello & world!", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString54__10 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("Hello &amp; world!", o_testEscape_literalMutationString54__10);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("Hello &amp; orld!", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString54__15 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("Hello &amp;amp; orld!", o_testEscape_literalMutationString54__15);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("Hello &amp world!", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString54__20 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("Hello &amp;amp world!", o_testEscape_literalMutationString54__20);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp world!", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString54__25 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp world!", o_testEscape_literalMutationString54__25);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp world!&#10;", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString54__30 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp world!&amp;#10;", o_testEscape_literalMutationString54__30);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp <world>!\n", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString54__35 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp &lt;world&gt;!&#10;", o_testEscape_literalMutationString54__35);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp world!\n&sam", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString54__40 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp world!&#10;&amp;sam", o_testEscape_literalMutationString54__40);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("dm7#=ToX)D7x>[Bob5_83OI`-k", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString54__45 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("dm7#&#61;ToX)D7x&gt;[Bob5_83OI&#96;-k", o_testEscape_literalMutationString54__45);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp \'world\'!\n&sam", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString54__50 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp &#39;world&#39;!&#10;&amp;sam", o_testEscape_literalMutationString54__50);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp&zz \'world\'!\n&sam", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString54__55 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp&amp;zz &#39;world&#39;!&#10;&amp;sam", o_testEscape_literalMutationString54__55);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" &amp&#zz \'world\'!\n&sam&#", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString54__60 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &amp;amp&amp;#zz &#39;world&#39;!&#10;&amp;sam&amp;#", o_testEscape_literalMutationString54__60);
        }
        {
            java.io.StringWriter sw = new java.io.StringWriter();
            com.github.mustachejava.util.HtmlEscaper.escape("\"Hello\" =` \'world\'!", sw);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_testEscape_literalMutationString54__65 = sw.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("&quot;Hello&quot; &#61;&#96; &#39;world&#39;!", o_testEscape_literalMutationString54__65);
        }
    }
}

