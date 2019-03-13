package org.jsoup.nodes;


import org.jsoup.Jsoup;
import org.junit.Assert;
import org.junit.Test;


public class EntitiesTest {
    @Test
    public void escape() {
        String text = "Hello &<> ? ? ? ? there ? ? ?";
        String escapedAscii = Entities.escape(text, new Document.OutputSettings().charset("ascii").escapeMode(base));
        String escapedAsciiFull = Entities.escape(text, new Document.OutputSettings().charset("ascii").escapeMode(extended));
        String escapedAsciiXhtml = Entities.escape(text, new Document.OutputSettings().charset("ascii").escapeMode(xhtml));
        String escapedUtfFull = Entities.escape(text, new Document.OutputSettings().charset("UTF-8").escapeMode(extended));
        String escapedUtfMin = Entities.escape(text, new Document.OutputSettings().charset("UTF-8").escapeMode(xhtml));
        Assert.assertEquals("Hello &amp;&lt;&gt; &Aring; &aring; &#x3c0; &#x65b0; there &frac34; &copy; &raquo;", escapedAscii);
        Assert.assertEquals("Hello &amp;&lt;&gt; &angst; &aring; &pi; &#x65b0; there &frac34; &copy; &raquo;", escapedAsciiFull);
        Assert.assertEquals("Hello &amp;&lt;&gt; &#xc5; &#xe5; &#x3c0; &#x65b0; there &#xbe; &#xa9; &#xbb;", escapedAsciiXhtml);
        Assert.assertEquals("Hello &amp;&lt;&gt; ? ? ? ? there ? ? ?", escapedUtfFull);
        Assert.assertEquals("Hello &amp;&lt;&gt; ? ? ? ? there ? ? ?", escapedUtfMin);
        // odd that it's defined as aring in base but angst in full
        // round trip
        Assert.assertEquals(text, Entities.unescape(escapedAscii));
        Assert.assertEquals(text, Entities.unescape(escapedAsciiFull));
        Assert.assertEquals(text, Entities.unescape(escapedAsciiXhtml));
        Assert.assertEquals(text, Entities.unescape(escapedUtfFull));
        Assert.assertEquals(text, Entities.unescape(escapedUtfMin));
    }

    @Test
    public void escapedSupplementary() {
        String text = "\ud835\udd59";
        String escapedAscii = Entities.escape(text, new Document.OutputSettings().charset("ascii").escapeMode(base));
        Assert.assertEquals("&#x1d559;", escapedAscii);
        String escapedAsciiFull = Entities.escape(text, new Document.OutputSettings().charset("ascii").escapeMode(extended));
        Assert.assertEquals("&hopf;", escapedAsciiFull);
        String escapedUtf = Entities.escape(text, new Document.OutputSettings().charset("UTF-8").escapeMode(extended));
        Assert.assertEquals(text, escapedUtf);
    }

    @Test
    public void unescapeMultiChars() {
        String text = "&NestedGreaterGreater; &nGg; &nGt; &nGtv; &Gt; &gg;";// gg is not combo, but 8811 could conflict with NestedGreaterGreater or others

        String un = "? ?? ?? ?? ? ?";
        Assert.assertEquals(un, Entities.unescape(text));
        String escaped = Entities.escape(un, new Document.OutputSettings().charset("ascii").escapeMode(extended));
        Assert.assertEquals("&Gt; &Gg;&#x338; &Gt;&#x20d2; &Gt;&#x338; &Gt; &Gt;", escaped);
        Assert.assertEquals(un, Entities.unescape(escaped));
    }

    @Test
    public void xhtml() {
        String text = "&amp; &gt; &lt; &quot;";
        Assert.assertEquals(38, xhtml.codepointForName("amp"));
        Assert.assertEquals(62, xhtml.codepointForName("gt"));
        Assert.assertEquals(60, xhtml.codepointForName("lt"));
        Assert.assertEquals(34, xhtml.codepointForName("quot"));
        Assert.assertEquals("amp", xhtml.nameForCodepoint(38));
        Assert.assertEquals("gt", xhtml.nameForCodepoint(62));
        Assert.assertEquals("lt", xhtml.nameForCodepoint(60));
        Assert.assertEquals("quot", xhtml.nameForCodepoint(34));
    }

    @Test
    public void getByName() {
        Assert.assertEquals("??", Entities.getByName("nGt"));
        Assert.assertEquals("fj", Entities.getByName("fjlig"));
        Assert.assertEquals("?", Entities.getByName("gg"));
        Assert.assertEquals("?", Entities.getByName("copy"));
    }

    @Test
    public void escapeSupplementaryCharacter() {
        String text = new String(Character.toChars(135361));
        String escapedAscii = Entities.escape(text, new Document.OutputSettings().charset("ascii").escapeMode(base));
        Assert.assertEquals("&#x210c1;", escapedAscii);
        String escapedUtf = Entities.escape(text, new Document.OutputSettings().charset("UTF-8").escapeMode(base));
        Assert.assertEquals(text, escapedUtf);
    }

    @Test
    public void notMissingMultis() {
        String text = "&nparsl;";
        String un = "\u2afd\u20e5";
        Assert.assertEquals(un, Entities.unescape(text));
    }

    @Test
    public void notMissingSupplementals() {
        String text = "&npolint; &qfr;";
        String un = "\u2a14 \ud835\udd2e";// ?

        Assert.assertEquals(un, Entities.unescape(text));
    }

    @Test
    public void unescape() {
        String text = "Hello &AElig; &amp;&LT&gt; &reg &angst; &angst &#960; &#960 &#x65B0; there &! &frac34; &copy; &COPY;";
        Assert.assertEquals("Hello ? &<> ? ? &angst ? ? ? there &! ? ? ?", Entities.unescape(text));
        Assert.assertEquals("&0987654321; &unknown", Entities.unescape("&0987654321; &unknown"));
    }

    @Test
    public void strictUnescape() {
        // for attributes, enforce strict unescaping (must look like &#xxx; , not just &#xxx)
        String text = "Hello &amp= &amp;";
        Assert.assertEquals("Hello &amp= &", Entities.unescape(text, true));
        Assert.assertEquals("Hello &= &", Entities.unescape(text));
        Assert.assertEquals("Hello &= &", Entities.unescape(text, false));
    }

    @Test
    public void caseSensitive() {
        String unescaped = "? ? & &";
        Assert.assertEquals("&Uuml; &uuml; &amp; &amp;", Entities.escape(unescaped, new Document.OutputSettings().charset("ascii").escapeMode(extended)));
        String escaped = "&Uuml; &uuml; &amp; &AMP";
        Assert.assertEquals("? ? & &", Entities.unescape(escaped));
    }

    @Test
    public void quoteReplacements() {
        String escaped = "&#92; &#36;";
        String unescaped = "\\ $";
        Assert.assertEquals(unescaped, Entities.unescape(escaped));
    }

    @Test
    public void letterDigitEntities() {
        String html = "<p>&sup1;&sup2;&sup3;&frac14;&frac12;&frac34;</p>";
        Document doc = Jsoup.parse(html);
        doc.outputSettings().charset("ascii");
        Element p = doc.select("p").first();
        Assert.assertEquals("&sup1;&sup2;&sup3;&frac14;&frac12;&frac34;", p.html());
        Assert.assertEquals("??????", p.text());
        doc.outputSettings().charset("UTF-8");
        Assert.assertEquals("??????", p.html());
    }

    @Test
    public void noSpuriousDecodes() {
        String string = "http://www.foo.com?a=1&num_rooms=1&children=0&int=VA&b=2";
        Assert.assertEquals(string, Entities.unescape(string));
    }

    @Test
    public void escapesGtInXmlAttributesButNotInHtml() {
        // https://github.com/jhy/jsoup/issues/528 - < is OK in HTML attribute values, but not in XML
        String docHtml = "<a title='<p>One</p>'>One</a>";
        Document doc = Jsoup.parse(docHtml);
        Element element = doc.select("a").first();
        doc.outputSettings().escapeMode(base);
        Assert.assertEquals("<a title=\"<p>One</p>\">One</a>", element.outerHtml());
        doc.outputSettings().escapeMode(xhtml);
        Assert.assertEquals("<a title=\"&lt;p>One&lt;/p>\">One</a>", element.outerHtml());
    }
}

