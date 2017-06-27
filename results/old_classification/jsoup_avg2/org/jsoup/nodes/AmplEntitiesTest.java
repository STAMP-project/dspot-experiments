

package org.jsoup.nodes;


public class AmplEntitiesTest {
    @org.junit.Test
    public void escape() {
        java.lang.String text = "Hello &<> ? ? ? ? there ? ? ?";
        java.lang.String escapedAscii = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
        java.lang.String escapedAsciiFull = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.extended));
        java.lang.String escapedAsciiXhtml = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml));
        java.lang.String escapedUtfFull = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("UTF-8").escapeMode(org.jsoup.nodes.Entities.EscapeMode.extended));
        java.lang.String escapedUtfMin = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("UTF-8").escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml));
        org.junit.Assert.assertEquals("Hello &amp;&lt;&gt; &Aring; &aring; &#x3c0; &#x65b0; there &frac34; &copy; &raquo;", escapedAscii);
        org.junit.Assert.assertEquals("Hello &amp;&lt;&gt; &angst; &aring; &pi; &#x65b0; there &frac34; &copy; &raquo;", escapedAsciiFull);
        org.junit.Assert.assertEquals("Hello &amp;&lt;&gt; &#xc5; &#xe5; &#x3c0; &#x65b0; there &#xbe; &#xa9; &#xbb;", escapedAsciiXhtml);
        org.junit.Assert.assertEquals("Hello &amp;&lt;&gt; ? ? ? ? there ? ? ?", escapedUtfFull);
        org.junit.Assert.assertEquals("Hello &amp;&lt;&gt; ? ? ? ? there ? ? ?", escapedUtfMin);
        // odd that it's defined as aring in base but angst in full
        // round trip
        org.junit.Assert.assertEquals(text, org.jsoup.nodes.Entities.unescape(escapedAscii));
        org.junit.Assert.assertEquals(text, org.jsoup.nodes.Entities.unescape(escapedAsciiFull));
        org.junit.Assert.assertEquals(text, org.jsoup.nodes.Entities.unescape(escapedAsciiXhtml));
        org.junit.Assert.assertEquals(text, org.jsoup.nodes.Entities.unescape(escapedUtfFull));
        org.junit.Assert.assertEquals(text, org.jsoup.nodes.Entities.unescape(escapedUtfMin));
    }

    @org.junit.Test
    public void escapedSupplemtary() {
        java.lang.String text = "\ud835\udd59";
        java.lang.String escapedAscii = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
        org.junit.Assert.assertEquals("&#x1d559;", escapedAscii);
        java.lang.String escapedAsciiFull = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.extended));
        org.junit.Assert.assertEquals("&hopf;", escapedAsciiFull);
        java.lang.String escapedUtf = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("UTF-8").escapeMode(org.jsoup.nodes.Entities.EscapeMode.extended));
        org.junit.Assert.assertEquals(text, escapedUtf);
    }

    @org.junit.Test
    public void unescapeMultiChars() {
        java.lang.String text = "&NestedGreaterGreater; &nGg; &nGt; &nGtv; &Gt; &gg;";// gg is not combo, but 8811 could conflict with NestedGreaterGreater or others
        
        java.lang.String un = "? ?? ?? ?? ? ?";
        org.junit.Assert.assertEquals(un, org.jsoup.nodes.Entities.unescape(text));
        java.lang.String escaped = org.jsoup.nodes.Entities.escape(un, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.extended));
        org.junit.Assert.assertEquals("&Gt; &Gg;&#x338; &Gt;&#x20d2; &Gt;&#x338; &Gt; &Gt;", escaped);
        org.junit.Assert.assertEquals(un, org.jsoup.nodes.Entities.unescape(escaped));
    }

    @org.junit.Test
    public void xhtml() {
        java.lang.String text = "&amp; &gt; &lt; &quot;";
        org.junit.Assert.assertEquals(38, org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("amp"));
        org.junit.Assert.assertEquals(62, org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("gt"));
        org.junit.Assert.assertEquals(60, org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("lt"));
        org.junit.Assert.assertEquals(34, org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("quot"));
        org.junit.Assert.assertEquals("amp", org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(38));
        org.junit.Assert.assertEquals("gt", org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(62));
        org.junit.Assert.assertEquals("lt", org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(60));
        org.junit.Assert.assertEquals("quot", org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(34));
    }

    @org.junit.Test
    public void getByName() {
        org.junit.Assert.assertEquals("??", org.jsoup.nodes.Entities.getByName("nGt"));
        org.junit.Assert.assertEquals("fj", org.jsoup.nodes.Entities.getByName("fjlig"));
        org.junit.Assert.assertEquals("?", org.jsoup.nodes.Entities.getByName("gg"));
        org.junit.Assert.assertEquals("?", org.jsoup.nodes.Entities.getByName("copy"));
    }

    @org.junit.Test
    public void escapeSupplementaryCharacter() {
        java.lang.String text = new java.lang.String(java.lang.Character.toChars(135361));
        java.lang.String escapedAscii = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
        org.junit.Assert.assertEquals("&#x210c1;", escapedAscii);
        java.lang.String escapedUtf = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("UTF-8").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
        org.junit.Assert.assertEquals(text, escapedUtf);
    }

    @org.junit.Test
    public void notMissingMultis() {
        java.lang.String text = "&nparsl;";
        java.lang.String un = "\u2afd\u20e5";
        org.junit.Assert.assertEquals(un, org.jsoup.nodes.Entities.unescape(text));
    }

    @org.junit.Test
    public void notMissingSupplementals() {
        java.lang.String text = "&npolint; &qfr;";
        java.lang.String un = "\u2a14 \ud835\udd2e";// ?
        
        org.junit.Assert.assertEquals(un, org.jsoup.nodes.Entities.unescape(text));
    }

    @org.junit.Test
    public void unescape() {
        java.lang.String text = "Hello &AElig; &amp;&LT&gt; &reg &angst; &angst &#960; &#960 &#x65B0; there &! &frac34; &copy; &COPY;";
        org.junit.Assert.assertEquals("Hello ? &<> ? ? &angst ? ? ? there &! ? ? ?", org.jsoup.nodes.Entities.unescape(text));
        org.junit.Assert.assertEquals("&0987654321; &unknown", org.jsoup.nodes.Entities.unescape("&0987654321; &unknown"));
    }

    @org.junit.Test
    public void strictUnescape() {
        // for attributes, enforce strict unescaping (must look like &#xxx; , not just &#xxx)
        java.lang.String text = "Hello &amp= &amp;";
        org.junit.Assert.assertEquals("Hello &amp= &", org.jsoup.nodes.Entities.unescape(text, true));
        org.junit.Assert.assertEquals("Hello &= &", org.jsoup.nodes.Entities.unescape(text));
        org.junit.Assert.assertEquals("Hello &= &", org.jsoup.nodes.Entities.unescape(text, false));
    }

    @org.junit.Test
    public void caseSensitive() {
        java.lang.String unescaped = "? ? & &";
        org.junit.Assert.assertEquals("&Uuml; &uuml; &amp; &amp;", org.jsoup.nodes.Entities.escape(unescaped, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.extended)));
        java.lang.String escaped = "&Uuml; &uuml; &amp; &AMP";
        org.junit.Assert.assertEquals("? ? & &", org.jsoup.nodes.Entities.unescape(escaped));
    }

    @org.junit.Test
    public void quoteReplacements() {
        java.lang.String escaped = "&#92; &#36;";
        java.lang.String unescaped = "\\ $";
        org.junit.Assert.assertEquals(unescaped, org.jsoup.nodes.Entities.unescape(escaped));
    }

    @org.junit.Test
    public void letterDigitEntities() {
        java.lang.String html = "<p>&sup1;&sup2;&sup3;&frac14;&frac12;&frac34;</p>";
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(html);
        doc.outputSettings().charset("ascii");
        org.jsoup.nodes.Element p = doc.select("p").first();
        org.junit.Assert.assertEquals("&sup1;&sup2;&sup3;&frac14;&frac12;&frac34;", p.html());
        org.junit.Assert.assertEquals("??????", p.text());
        doc.outputSettings().charset("UTF-8");
        org.junit.Assert.assertEquals("??????", p.html());
    }

    @org.junit.Test
    public void noSpuriousDecodes() {
        java.lang.String string = "http://www.foo.com?a=1&num_rooms=1&children=0&int=VA&b=2";
        org.junit.Assert.assertEquals(string, org.jsoup.nodes.Entities.unescape(string));
    }

    @org.junit.Test
    public void escapesGtInXmlAttributesButNotInHtml() {
        // https://github.com/jhy/jsoup/issues/528 - < is OK in HTML attribute values, but not in XML
        java.lang.String docHtml = "<a title='<p>One</p>'>One</a>";
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(docHtml);
        org.jsoup.nodes.Element element = doc.select("a").first();
        doc.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.base);
        org.junit.Assert.assertEquals("<a title=\"<p>One</p>\">One</a>", element.outerHtml());
        doc.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);
        org.junit.Assert.assertEquals("<a title=\"&lt;p>One&lt;/p>\">One</a>", element.outerHtml());
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#escapeSupplementaryCharacter */
    @org.junit.Test(timeout = 10000)
    public void escapeSupplementaryCharacter_cf78_failAssert12() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String text = new java.lang.String(java.lang.Character.toChars(135361));
            java.lang.String escapedAscii = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
            java.lang.String escapedUtf = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("UTF-8").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
            // StatementAdderOnAssert create random local variable
            boolean vc_36 = true;
            // StatementAdderOnAssert create null value
            java.lang.String vc_34 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_32 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_32.unescape(vc_34, vc_36);
            org.junit.Assert.fail("escapeSupplementaryCharacter_cf78 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#escapeSupplementaryCharacter */
    @org.junit.Test(timeout = 10000)
    public void escapeSupplementaryCharacter_cf25() {
        java.lang.String text = new java.lang.String(java.lang.Character.toChars(135361));
        java.lang.String escapedAscii = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
        org.junit.Assert.assertEquals("&#x210c1;", escapedAscii);
        java.lang.String escapedUtf = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("UTF-8").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
        // StatementAdderOnAssert create random local variable
        int[] vc_13 = new int []{-2139907821,161647393};
        // StatementAdderOnAssert create null value
        org.jsoup.nodes.Entities vc_8 = (org.jsoup.nodes.Entities)null;
        // AssertGenerator replace invocation
        int o_escapeSupplementaryCharacter_cf25__19 = // StatementAdderMethod cloned existing statement
vc_8.codepointsForName(escapedUtf, vc_13);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_escapeSupplementaryCharacter_cf25__19, 0);
        org.junit.Assert.assertEquals(text, escapedUtf);
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#escapeSupplementaryCharacter */
    @org.junit.Test(timeout = 10000)
    public void escapeSupplementaryCharacter_cf63() {
        java.lang.String text = new java.lang.String(java.lang.Character.toChars(135361));
        java.lang.String escapedAscii = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
        org.junit.Assert.assertEquals("&#x210c1;", escapedAscii);
        java.lang.String escapedUtf = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("UTF-8").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
        // StatementAdderOnAssert create null value
        org.jsoup.nodes.Entities vc_24 = (org.jsoup.nodes.Entities)null;
        // AssertGenerator replace invocation
        java.lang.String o_escapeSupplementaryCharacter_cf63__17 = // StatementAdderMethod cloned existing statement
vc_24.getByName(text);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_escapeSupplementaryCharacter_cf63__17, "");
        org.junit.Assert.assertEquals(text, escapedUtf);
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#escapeSupplementaryCharacter */
    @org.junit.Test(timeout = 10000)
    public void escapeSupplementaryCharacter_cf80() {
        java.lang.String text = new java.lang.String(java.lang.Character.toChars(135361));
        java.lang.String escapedAscii = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
        org.junit.Assert.assertEquals("&#x210c1;", escapedAscii);
        java.lang.String escapedUtf = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("UTF-8").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
        // StatementAdderOnAssert create random local variable
        boolean vc_36 = true;
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_7 = "&#x210c1;";
        // StatementAdderOnAssert create null value
        org.jsoup.nodes.Entities vc_32 = (org.jsoup.nodes.Entities)null;
        // AssertGenerator replace invocation
        java.lang.String o_escapeSupplementaryCharacter_cf80__21 = // StatementAdderMethod cloned existing statement
vc_32.unescape(String_vc_7, vc_36);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_escapeSupplementaryCharacter_cf80__21, "\ud844\udcc1");
        org.junit.Assert.assertEquals(text, escapedUtf);
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#escapeSupplementaryCharacter */
    @org.junit.Test(timeout = 10000)
    public void escapeSupplementaryCharacter_cf38_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String text = new java.lang.String(java.lang.Character.toChars(135361));
            java.lang.String escapedAscii = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
            java.lang.String escapedUtf = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("UTF-8").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
            // StatementAdderOnAssert create null value
            java.lang.String vc_16 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_14 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_14.getCharacterByName(vc_16);
            org.junit.Assert.fail("escapeSupplementaryCharacter_cf38 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#escapeSupplementaryCharacter */
    /* amplification of org.jsoup.nodes.EntitiesTest#escapeSupplementaryCharacter_cf26 */
    @org.junit.Test(timeout = 10000)
    public void escapeSupplementaryCharacter_cf26_cf1315_failAssert13() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_23_1 = 0;
            java.lang.String text = new java.lang.String(java.lang.Character.toChars(135361));
            java.lang.String escapedAscii = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
            java.lang.String escapedUtf = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("UTF-8").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
            // StatementAdderOnAssert create null value
            int[] vc_12 = (int[])null;
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_2 = "ascii";
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_8 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            int o_escapeSupplementaryCharacter_cf26__21 = // StatementAdderMethod cloned existing statement
vc_8.codepointsForName(String_vc_2, vc_12);
            // MethodAssertGenerator build local variable
            Object o_23_0 = o_escapeSupplementaryCharacter_cf26__21;
            // StatementAdderOnAssert create random local variable
            boolean vc_358 = false;
            // StatementAdderOnAssert create null value
            java.lang.String vc_356 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_354 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_354.unescape(vc_356, vc_358);
            org.junit.Assert.fail("escapeSupplementaryCharacter_cf26_cf1315 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#escapeSupplementaryCharacter */
    /* amplification of org.jsoup.nodes.EntitiesTest#escapeSupplementaryCharacter_cf17 */
    @org.junit.Test(timeout = 10000)
    public void escapeSupplementaryCharacter_cf17_cf747_failAssert14() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String text = new java.lang.String(java.lang.Character.toChars(135361));
            java.lang.String escapedAscii = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
            java.lang.String escapedUtf = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("UTF-8").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_7 = new java.lang.String();
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_4 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            boolean o_escapeSupplementaryCharacter_cf17__19 = // StatementAdderMethod cloned existing statement
vc_4.isNamedEntity(vc_7);
            // MethodAssertGenerator build local variable
            Object o_21_0 = o_escapeSupplementaryCharacter_cf17__19;
            // StatementAdderOnAssert create null value
            java.lang.String vc_214 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            vc_4.unescape(vc_214);
            org.junit.Assert.fail("escapeSupplementaryCharacter_cf17_cf747 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#escapeSupplementaryCharacter */
    /* amplification of org.jsoup.nodes.EntitiesTest#escapeSupplementaryCharacter_cf15 */
    @org.junit.Test(timeout = 10000)
    public void escapeSupplementaryCharacter_cf15_cf348_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String text = new java.lang.String(java.lang.Character.toChars(135361));
            java.lang.String escapedAscii = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
            java.lang.String escapedUtf = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("UTF-8").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_4 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            boolean o_escapeSupplementaryCharacter_cf15__17 = // StatementAdderMethod cloned existing statement
vc_4.isNamedEntity(text);
            // MethodAssertGenerator build local variable
            Object o_19_0 = o_escapeSupplementaryCharacter_cf15__17;
            // StatementAdderOnAssert create null value
            java.lang.String vc_108 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            vc_4.getCharacterByName(vc_108);
            org.junit.Assert.fail("escapeSupplementaryCharacter_cf15_cf348 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#escapedSupplemtary */
    @org.junit.Test(timeout = 10000)
    public void escapedSupplemtary_cf4017_failAssert19() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String text = "\ud835\udd59";
            java.lang.String escapedAscii = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
            java.lang.String escapedAsciiFull = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.extended));
            java.lang.String escapedUtf = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("UTF-8").escapeMode(org.jsoup.nodes.Entities.EscapeMode.extended));
            // StatementAdderOnAssert create random local variable
            boolean vc_956 = true;
            // StatementAdderOnAssert create null value
            java.lang.String vc_954 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_952 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_952.unescape(vc_954, vc_956);
            org.junit.Assert.fail("escapedSupplemtary_cf4017 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#escapedSupplemtary */
    @org.junit.Test(timeout = 10000)
    public void escapedSupplemtary_cf3965() {
        java.lang.String text = "\ud835\udd59";
        java.lang.String escapedAscii = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
        org.junit.Assert.assertEquals("&#x1d559;", escapedAscii);
        java.lang.String escapedAsciiFull = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.extended));
        org.junit.Assert.assertEquals("&hopf;", escapedAsciiFull);
        java.lang.String escapedUtf = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("UTF-8").escapeMode(org.jsoup.nodes.Entities.EscapeMode.extended));
        // StatementAdderOnAssert create null value
        int[] vc_932 = (int[])null;
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_188 = "&#x1d559;";
        // StatementAdderOnAssert create null value
        org.jsoup.nodes.Entities vc_928 = (org.jsoup.nodes.Entities)null;
        // AssertGenerator replace invocation
        int o_escapedSupplemtary_cf3965__25 = // StatementAdderMethod cloned existing statement
vc_928.codepointsForName(String_vc_188, vc_932);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_escapedSupplemtary_cf3965__25, 0);
        org.junit.Assert.assertEquals(text, escapedUtf);
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#escapedSupplemtary */
    @org.junit.Test(timeout = 10000)
    public void escapedSupplemtary_cf3977_failAssert16() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String text = "\ud835\udd59";
            java.lang.String escapedAscii = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
            java.lang.String escapedAsciiFull = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.extended));
            java.lang.String escapedUtf = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("UTF-8").escapeMode(org.jsoup.nodes.Entities.EscapeMode.extended));
            // StatementAdderOnAssert create null value
            java.lang.String vc_936 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_934 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_934.getCharacterByName(vc_936);
            org.junit.Assert.fail("escapedSupplemtary_cf3977 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#escapedSupplemtary */
    @org.junit.Test(timeout = 10000)
    public void escapedSupplemtary_cf4018() {
        java.lang.String text = "\ud835\udd59";
        java.lang.String escapedAscii = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
        org.junit.Assert.assertEquals("&#x1d559;", escapedAscii);
        java.lang.String escapedAsciiFull = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.extended));
        org.junit.Assert.assertEquals("&hopf;", escapedAsciiFull);
        java.lang.String escapedUtf = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("UTF-8").escapeMode(org.jsoup.nodes.Entities.EscapeMode.extended));
        // StatementAdderOnAssert create random local variable
        boolean vc_956 = true;
        // StatementAdderOnAssert create null value
        org.jsoup.nodes.Entities vc_952 = (org.jsoup.nodes.Entities)null;
        // AssertGenerator replace invocation
        java.lang.String o_escapedSupplemtary_cf4018__23 = // StatementAdderMethod cloned existing statement
vc_952.unescape(escapedAscii, vc_956);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_escapedSupplemtary_cf4018__23, "\ud835\udd59");
        org.junit.Assert.assertEquals(text, escapedUtf);
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#escapedSupplemtary */
    @org.junit.Test(timeout = 10000)
    public void escapedSupplemtary_cf4002() {
        java.lang.String text = "\ud835\udd59";
        java.lang.String escapedAscii = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
        org.junit.Assert.assertEquals("&#x1d559;", escapedAscii);
        java.lang.String escapedAsciiFull = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.extended));
        org.junit.Assert.assertEquals("&hopf;", escapedAsciiFull);
        java.lang.String escapedUtf = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("UTF-8").escapeMode(org.jsoup.nodes.Entities.EscapeMode.extended));
        // StatementAdderOnAssert create null value
        org.jsoup.nodes.Entities vc_944 = (org.jsoup.nodes.Entities)null;
        // AssertGenerator replace invocation
        java.lang.String o_escapedSupplemtary_cf4002__21 = // StatementAdderMethod cloned existing statement
vc_944.getByName(escapedAsciiFull);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_escapedSupplemtary_cf4002__21, "");
        org.junit.Assert.assertEquals(text, escapedUtf);
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#escapedSupplemtary */
    /* amplification of org.jsoup.nodes.EntitiesTest#escapedSupplemtary_cf4012 */
    @org.junit.Test(timeout = 10000)
    public void escapedSupplemtary_cf4012_cf7346_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String text = "\ud835\udd59";
            java.lang.String escapedAscii = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
            java.lang.String escapedAsciiFull = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.extended));
            java.lang.String escapedUtf = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("UTF-8").escapeMode(org.jsoup.nodes.Entities.EscapeMode.extended));
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_951 = new java.lang.String();
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_948 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            java.lang.String o_escapedSupplemtary_cf4012__23 = // StatementAdderMethod cloned existing statement
vc_948.unescape(vc_951);
            // MethodAssertGenerator build local variable
            Object o_25_0 = o_escapedSupplemtary_cf4012__23;
            // StatementAdderOnAssert create null value
            java.lang.String vc_1778 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            vc_948.unescape(vc_1778);
            org.junit.Assert.fail("escapedSupplemtary_cf4012_cf7346 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#escapedSupplemtary */
    /* amplification of org.jsoup.nodes.EntitiesTest#escapedSupplemtary_cf4020 */
    @org.junit.Test(timeout = 10000)
    public void escapedSupplemtary_cf4020_cf8091_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String text = "\ud835\udd59";
            java.lang.String escapedAscii = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
            java.lang.String escapedAsciiFull = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.extended));
            java.lang.String escapedUtf = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("UTF-8").escapeMode(org.jsoup.nodes.Entities.EscapeMode.extended));
            // StatementAdderOnAssert create random local variable
            boolean vc_956 = true;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_955 = new java.lang.String();
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_952 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            java.lang.String o_escapedSupplemtary_cf4020__25 = // StatementAdderMethod cloned existing statement
vc_952.unescape(vc_955, vc_956);
            // MethodAssertGenerator build local variable
            Object o_27_0 = o_escapedSupplemtary_cf4020__25;
            // StatementAdderOnAssert create null value
            java.lang.String vc_1902 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_1900 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_1900.getCharacterByName(vc_1902);
            org.junit.Assert.fail("escapedSupplemtary_cf4020_cf8091 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#escapedSupplemtary */
    /* amplification of org.jsoup.nodes.EntitiesTest#escapedSupplemtary_cf3945 */
    @org.junit.Test(timeout = 10000)
    public void escapedSupplemtary_cf3945_failAssert12_literalMutation8332_cf8433_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String text = "";
                // StatementAdderOnAssert create random local variable
                boolean vc_1968 = false;
                // StatementAdderOnAssert create null value
                java.lang.String vc_1966 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Entities vc_1964 = (org.jsoup.nodes.Entities)null;
                // StatementAdderMethod cloned existing statement
                vc_1964.unescape(vc_1966, vc_1968);
                // MethodAssertGenerator build local variable
                Object o_12_0 = text;
                java.lang.String escapedAscii = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.base));
                java.lang.String escapedAsciiFull = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("ascii").escapeMode(org.jsoup.nodes.Entities.EscapeMode.extended));
                java.lang.String escapedUtf = org.jsoup.nodes.Entities.escape(text, new org.jsoup.nodes.Document.OutputSettings().charset("UTF-8").escapeMode(org.jsoup.nodes.Entities.EscapeMode.extended));
                // StatementAdderOnAssert create null value
                java.lang.String vc_922 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Entities vc_920 = (org.jsoup.nodes.Entities)null;
                // StatementAdderMethod cloned existing statement
                vc_920.isBaseNamedEntity(vc_922);
                org.junit.Assert.fail("escapedSupplemtary_cf3945 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("escapedSupplemtary_cf3945_failAssert12_literalMutation8332_cf8433 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#escapesGtInXmlAttributesButNotInHtml */
    @org.junit.Test(timeout = 10000)
    public void escapesGtInXmlAttributesButNotInHtml_cf8610_failAssert22() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // https://github.com/jhy/jsoup/issues/528 - < is OK in HTML attribute values, but not in XML
            java.lang.String docHtml = "<a title='<p>One</p>'>One</a>";
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(docHtml);
            org.jsoup.nodes.Element element = doc.select("a").first();
            doc.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.base);
            // MethodAssertGenerator build local variable
            Object o_10_0 = element.outerHtml();
            doc.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);
            // StatementAdderOnAssert create random local variable
            boolean vc_2014 = true;
            // StatementAdderOnAssert create null value
            java.lang.String vc_2012 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_2010 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_2010.unescape(vc_2012, vc_2014);
            // MethodAssertGenerator build local variable
            Object o_22_0 = element.outerHtml();
            org.junit.Assert.fail("escapesGtInXmlAttributesButNotInHtml_cf8610 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#escapesGtInXmlAttributesButNotInHtml */
    @org.junit.Test(timeout = 10000)
    public void escapesGtInXmlAttributesButNotInHtml_cf8570_failAssert19() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // https://github.com/jhy/jsoup/issues/528 - < is OK in HTML attribute values, but not in XML
            java.lang.String docHtml = "<a title='<p>One</p>'>One</a>";
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(docHtml);
            org.jsoup.nodes.Element element = doc.select("a").first();
            doc.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.base);
            // MethodAssertGenerator build local variable
            Object o_10_0 = element.outerHtml();
            doc.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);
            // StatementAdderOnAssert create null value
            java.lang.String vc_1994 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_1992 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_1992.getCharacterByName(vc_1994);
            // MethodAssertGenerator build local variable
            Object o_20_0 = element.outerHtml();
            org.junit.Assert.fail("escapesGtInXmlAttributesButNotInHtml_cf8570 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#noSpuriousDecodes */
    @org.junit.Test(timeout = 10000)
    public void noSpuriousDecodes_cf8777() {
        java.lang.String string = "http://www.foo.com?a=1&num_rooms=1&children=0&int=VA&b=2";
        // StatementAdderOnAssert create null value
        int[] vc_2036 = (int[])null;
        // StatementAdderOnAssert create null value
        org.jsoup.nodes.Entities vc_2032 = (org.jsoup.nodes.Entities)null;
        // AssertGenerator replace invocation
        int o_noSpuriousDecodes_cf8777__6 = // StatementAdderMethod cloned existing statement
vc_2032.codepointsForName(string, vc_2036);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_noSpuriousDecodes_cf8777__6, 0);
        org.junit.Assert.assertEquals(string, org.jsoup.nodes.Entities.unescape(string));
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#noSpuriousDecodes */
    @org.junit.Test(timeout = 10000)
    public void noSpuriousDecodes_cf8791_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String string = "http://www.foo.com?a=1&num_rooms=1&children=0&int=VA&b=2";
            // StatementAdderOnAssert create null value
            java.lang.String vc_2040 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_2038 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_2038.getCharacterByName(vc_2040);
            // MethodAssertGenerator build local variable
            Object o_8_0 = org.jsoup.nodes.Entities.unescape(string);
            org.junit.Assert.fail("noSpuriousDecodes_cf8791 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#noSpuriousDecodes */
    @org.junit.Test(timeout = 10000)
    public void noSpuriousDecodes_cf8831_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String string = "http://www.foo.com?a=1&num_rooms=1&children=0&int=VA&b=2";
            // StatementAdderOnAssert create random local variable
            boolean vc_2060 = true;
            // StatementAdderOnAssert create null value
            java.lang.String vc_2058 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_2056 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_2056.unescape(vc_2058, vc_2060);
            // MethodAssertGenerator build local variable
            Object o_10_0 = org.jsoup.nodes.Entities.unescape(string);
            org.junit.Assert.fail("noSpuriousDecodes_cf8831 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#noSpuriousDecodes */
    @org.junit.Test(timeout = 10000)
    public void noSpuriousDecodes_cf8818() {
        java.lang.String string = "http://www.foo.com?a=1&num_rooms=1&children=0&int=VA&b=2";
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_2051 = new java.lang.String();
        // StatementAdderOnAssert create null value
        org.jsoup.nodes.Entities vc_2048 = (org.jsoup.nodes.Entities)null;
        // AssertGenerator replace invocation
        java.lang.String o_noSpuriousDecodes_cf8818__6 = // StatementAdderMethod cloned existing statement
vc_2048.getByName(vc_2051);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_noSpuriousDecodes_cf8818__6, "");
        org.junit.Assert.assertEquals(string, org.jsoup.nodes.Entities.unescape(string));
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#noSpuriousDecodes */
    /* amplification of org.jsoup.nodes.EntitiesTest#noSpuriousDecodes_cf8816 */
    @org.junit.Test(timeout = 10000)
    public void noSpuriousDecodes_cf8816_cf11154_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String string = "http://www.foo.com?a=1&num_rooms=1&children=0&int=VA&b=2";
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_2048 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            java.lang.String o_noSpuriousDecodes_cf8816__4 = // StatementAdderMethod cloned existing statement
vc_2048.getByName(string);
            // MethodAssertGenerator build local variable
            Object o_6_0 = o_noSpuriousDecodes_cf8816__4;
            // StatementAdderOnAssert create random local variable
            boolean vc_2658 = true;
            // StatementAdderOnAssert create null value
            java.lang.String vc_2656 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_2654 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_2654.unescape(vc_2656, vc_2658);
            // MethodAssertGenerator build local variable
            Object o_16_0 = org.jsoup.nodes.Entities.unescape(string);
            org.junit.Assert.fail("noSpuriousDecodes_cf8816_cf11154 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#noSpuriousDecodes */
    /* amplification of org.jsoup.nodes.EntitiesTest#noSpuriousDecodes_cf8780 */
    @org.junit.Test(timeout = 10000)
    public void noSpuriousDecodes_cf8780_cf10514_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_10_1 = 0;
            java.lang.String string = "http://www.foo.com?a=1&num_rooms=1&children=0&int=VA&b=2";
            // StatementAdderOnAssert create random local variable
            int[] vc_2037 = new int []{};
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_410 = "http://www.foo.com?a=1&num_rooms=1&children=0&int=VA&b=2";
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_2032 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            int o_noSpuriousDecodes_cf8780__8 = // StatementAdderMethod cloned existing statement
vc_2032.codepointsForName(String_vc_410, vc_2037);
            // MethodAssertGenerator build local variable
            Object o_10_0 = o_noSpuriousDecodes_cf8780__8;
            // StatementAdderOnAssert create null value
            java.lang.String vc_2494 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_2492 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_2492.codepointsForName(vc_2494, vc_2037);
            // MethodAssertGenerator build local variable
            Object o_18_0 = org.jsoup.nodes.Entities.unescape(string);
            org.junit.Assert.fail("noSpuriousDecodes_cf8780_cf10514 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#noSpuriousDecodes */
    /* amplification of org.jsoup.nodes.EntitiesTest#noSpuriousDecodes_cf8834 */
    @org.junit.Test(timeout = 10000)
    public void noSpuriousDecodes_cf8834_cf12764_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String string = "http://www.foo.com?a=1&num_rooms=1&children=0&int=VA&b=2";
            // StatementAdderOnAssert create random local variable
            boolean vc_2060 = true;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_2059 = new java.lang.String();
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_2056 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            java.lang.String o_noSpuriousDecodes_cf8834__8 = // StatementAdderMethod cloned existing statement
vc_2056.unescape(vc_2059, vc_2060);
            // MethodAssertGenerator build local variable
            Object o_10_0 = o_noSpuriousDecodes_cf8834__8;
            // StatementAdderOnAssert create null value
            java.lang.String vc_3006 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_3004 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_3004.getCharacterByName(vc_3006);
            // MethodAssertGenerator build local variable
            Object o_18_0 = org.jsoup.nodes.Entities.unescape(string);
            org.junit.Assert.fail("noSpuriousDecodes_cf8834_cf12764 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#noSpuriousDecodes */
    /* amplification of org.jsoup.nodes.EntitiesTest#noSpuriousDecodes_cf8816 */
    @org.junit.Test(timeout = 10000)
    public void noSpuriousDecodes_cf8816_cf11154_failAssert7_literalMutation13473() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String string = "http://www.foo.com?a=1&num_rooms=1&children=0&i}t=VA&b=2";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(string, "http://www.foo.com?a=1&num_rooms=1&children=0&i}t=VA&b=2");
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_2048 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            java.lang.String o_noSpuriousDecodes_cf8816__4 = // StatementAdderMethod cloned existing statement
vc_2048.getByName(string);
            // MethodAssertGenerator build local variable
            Object o_6_0 = o_noSpuriousDecodes_cf8816__4;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_6_0, "");
            // StatementAdderOnAssert create random local variable
            boolean vc_2658 = true;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(vc_2658);
            // StatementAdderOnAssert create null value
            java.lang.String vc_2656 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2656);
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_2654 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2654);
            // StatementAdderMethod cloned existing statement
            vc_2654.unescape(vc_2656, vc_2658);
            // MethodAssertGenerator build local variable
            Object o_16_0 = org.jsoup.nodes.Entities.unescape(string);
            org.junit.Assert.fail("noSpuriousDecodes_cf8816_cf11154 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#noSpuriousDecodes */
    /* amplification of org.jsoup.nodes.EntitiesTest#noSpuriousDecodes_cf8780 */
    @org.junit.Test(timeout = 10000)
    public void noSpuriousDecodes_cf8780_cf10514_failAssert1_literalMutation13436() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_10_1 = 0;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_10_1, 0);
            java.lang.String string = "http://www.foo.com?a=1&num_rooms=1&children=0&int=VA&b=2";
            // StatementAdderOnAssert create random local variable
            int[] vc_2037 = new int []{};
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_410 = "http:/)/www.foo.com?a=1&num_rooms=1&children=0&int=VA&b=2";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_410, "http:/)/www.foo.com?a=1&num_rooms=1&children=0&int=VA&b=2");
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_2032 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            int o_noSpuriousDecodes_cf8780__8 = // StatementAdderMethod cloned existing statement
vc_2032.codepointsForName(String_vc_410, vc_2037);
            // MethodAssertGenerator build local variable
            Object o_10_0 = o_noSpuriousDecodes_cf8780__8;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_10_0, 0);
            // StatementAdderOnAssert create null value
            java.lang.String vc_2494 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2494);
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_2492 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2492);
            // StatementAdderMethod cloned existing statement
            vc_2492.codepointsForName(vc_2494, vc_2037);
            // MethodAssertGenerator build local variable
            Object o_18_0 = org.jsoup.nodes.Entities.unescape(string);
            org.junit.Assert.fail("noSpuriousDecodes_cf8780_cf10514 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#notMissingMultis */
    @org.junit.Test(timeout = 10000)
    public void notMissingMultis_cf13580() {
        java.lang.String text = "&nparsl;";
        java.lang.String un = "\u2afd\u20e5";
        // StatementAdderOnAssert create null value
        org.jsoup.nodes.Entities vc_3198 = (org.jsoup.nodes.Entities)null;
        // AssertGenerator replace invocation
        java.lang.String o_notMissingMultis_cf13580__5 = // StatementAdderMethod cloned existing statement
vc_3198.getByName(un);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_notMissingMultis_cf13580__5, "");
        org.junit.Assert.assertEquals(un, org.jsoup.nodes.Entities.unescape(text));
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#notMissingMultis */
    @org.junit.Test(timeout = 10000)
    public void notMissingMultis_cf13595_failAssert14() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String text = "&nparsl;";
            java.lang.String un = "\u2afd\u20e5";
            // StatementAdderOnAssert create random local variable
            boolean vc_3210 = true;
            // StatementAdderOnAssert create null value
            java.lang.String vc_3208 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_3206 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_3206.unescape(vc_3208, vc_3210);
            // MethodAssertGenerator build local variable
            Object o_11_0 = org.jsoup.nodes.Entities.unescape(text);
            org.junit.Assert.fail("notMissingMultis_cf13595 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#notMissingMultis */
    @org.junit.Test(timeout = 10000)
    public void notMissingMultis_cf13541() {
        java.lang.String text = "&nparsl;";
        java.lang.String un = "\u2afd\u20e5";
        // StatementAdderOnAssert create null value
        int[] vc_3186 = (int[])null;
        // StatementAdderOnAssert create null value
        org.jsoup.nodes.Entities vc_3182 = (org.jsoup.nodes.Entities)null;
        // AssertGenerator replace invocation
        int o_notMissingMultis_cf13541__7 = // StatementAdderMethod cloned existing statement
vc_3182.codepointsForName(un, vc_3186);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_notMissingMultis_cf13541__7, 0);
        org.junit.Assert.assertEquals(un, org.jsoup.nodes.Entities.unescape(text));
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#notMissingMultis */
    @org.junit.Test(timeout = 10000)
    public void notMissingMultis_cf13555_failAssert11() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String text = "&nparsl;";
            java.lang.String un = "\u2afd\u20e5";
            // StatementAdderOnAssert create null value
            java.lang.String vc_3190 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_3188 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_3188.getCharacterByName(vc_3190);
            // MethodAssertGenerator build local variable
            Object o_9_0 = org.jsoup.nodes.Entities.unescape(text);
            org.junit.Assert.fail("notMissingMultis_cf13555 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#notMissingMultis */
    /* amplification of org.jsoup.nodes.EntitiesTest#notMissingMultis_cf13580 */
    @org.junit.Test(timeout = 10000)
    public void notMissingMultis_cf13580_cf15927_failAssert4_literalMutation18424() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String text = "&nprsl;";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(text, "&nprsl;");
            java.lang.String un = "\u2afd\u20e5";
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_3198 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            java.lang.String o_notMissingMultis_cf13580__5 = // StatementAdderMethod cloned existing statement
vc_3198.getByName(un);
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_notMissingMultis_cf13580__5;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_7_0, "");
            // StatementAdderOnAssert create null value
            java.lang.String vc_3802 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3802);
            // StatementAdderMethod cloned existing statement
            vc_3198.unescape(vc_3802);
            // MethodAssertGenerator build local variable
            Object o_13_0 = org.jsoup.nodes.Entities.unescape(text);
            org.junit.Assert.fail("notMissingMultis_cf13580_cf15927 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#notMissingMultis */
    /* amplification of org.jsoup.nodes.EntitiesTest#notMissingMultis_cf13544 */
    @org.junit.Test(timeout = 10000)
    public void notMissingMultis_cf13544_cf15288_failAssert2_literalMutation18419() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_11_1 = 0;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_11_1, 0);
            java.lang.String text = "&nparsl;";
            java.lang.String un = "\u2afd\u20e5";
            // StatementAdderOnAssert create random local variable
            int[] vc_3187 = new int []{843654213,-159728083,-451510052};
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_641 = "f";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_641, "f");
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_3182 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            int o_notMissingMultis_cf13544__9 = // StatementAdderMethod cloned existing statement
vc_3182.codepointsForName(String_vc_641, vc_3187);
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_notMissingMultis_cf13544__9;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_11_0, 0);
            // StatementAdderOnAssert create null value
            int[] vc_3646 = (int[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3646);
            // StatementAdderOnAssert create null value
            java.lang.String vc_3644 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3644);
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_3642 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3642);
            // StatementAdderMethod cloned existing statement
            vc_3642.codepointsForName(vc_3644, vc_3646);
            // MethodAssertGenerator build local variable
            Object o_21_0 = org.jsoup.nodes.Entities.unescape(text);
            org.junit.Assert.fail("notMissingMultis_cf13544_cf15288 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#notMissingSupplementals */
    @org.junit.Test(timeout = 10000)
    public void notMissingSupplementals_cf18595_failAssert17() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String text = "&npolint; &qfr;";
            java.lang.String un = "\u2a14 \ud835\udd2e";// ?
            
            // StatementAdderOnAssert create random local variable
            boolean vc_4406 = true;
            // StatementAdderOnAssert create null value
            java.lang.String vc_4404 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_4402 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_4402.unescape(vc_4404, vc_4406);
            // MethodAssertGenerator build local variable
            Object o_12_0 = org.jsoup.nodes.Entities.unescape(text);
            org.junit.Assert.fail("notMissingSupplementals_cf18595 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#notMissingSupplementals */
    @org.junit.Test(timeout = 10000)
    public void notMissingSupplementals_cf18582() {
        java.lang.String text = "&npolint; &qfr;";
        java.lang.String un = "\u2a14 \ud835\udd2e";// ?
        
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_4397 = new java.lang.String();
        // StatementAdderOnAssert create null value
        org.jsoup.nodes.Entities vc_4394 = (org.jsoup.nodes.Entities)null;
        // AssertGenerator replace invocation
        java.lang.String o_notMissingSupplementals_cf18582__8 = // StatementAdderMethod cloned existing statement
vc_4394.getByName(vc_4397);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_notMissingSupplementals_cf18582__8, "");
        org.junit.Assert.assertEquals(un, org.jsoup.nodes.Entities.unescape(text));
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#notMissingSupplementals */
    @org.junit.Test(timeout = 10000)
    public void notMissingSupplementals_cf18555_failAssert14() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String text = "&npolint; &qfr;";
            java.lang.String un = "\u2a14 \ud835\udd2e";// ?
            
            // StatementAdderOnAssert create null value
            java.lang.String vc_4386 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_4384 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_4384.getCharacterByName(vc_4386);
            // MethodAssertGenerator build local variable
            Object o_10_0 = org.jsoup.nodes.Entities.unescape(text);
            org.junit.Assert.fail("notMissingSupplementals_cf18555 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#notMissingSupplementals */
    @org.junit.Test(timeout = 10000)
    public void notMissingSupplementals_cf18541() {
        java.lang.String text = "&npolint; &qfr;";
        java.lang.String un = "\u2a14 \ud835\udd2e";// ?
        
        // StatementAdderOnAssert create null value
        int[] vc_4382 = (int[])null;
        // StatementAdderOnAssert create null value
        org.jsoup.nodes.Entities vc_4378 = (org.jsoup.nodes.Entities)null;
        // AssertGenerator replace invocation
        int o_notMissingSupplementals_cf18541__8 = // StatementAdderMethod cloned existing statement
vc_4378.codepointsForName(un, vc_4382);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_notMissingSupplementals_cf18541__8, 0);
        org.junit.Assert.assertEquals(un, org.jsoup.nodes.Entities.unescape(text));
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#notMissingSupplementals */
    /* amplification of org.jsoup.nodes.EntitiesTest#notMissingSupplementals_cf18580 */
    @org.junit.Test(timeout = 10000)
    public void notMissingSupplementals_cf18580_cf20879_failAssert26() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String text = "&npolint; &qfr;";
            java.lang.String un = "\u2a14 \ud835\udd2e";// ?
            
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_4394 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            java.lang.String o_notMissingSupplementals_cf18580__6 = // StatementAdderMethod cloned existing statement
vc_4394.getByName(text);
            // MethodAssertGenerator build local variable
            Object o_8_0 = o_notMissingSupplementals_cf18580__6;
            // StatementAdderOnAssert create null value
            java.lang.String vc_4970 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            vc_4394.isBaseNamedEntity(vc_4970);
            // MethodAssertGenerator build local variable
            Object o_14_0 = org.jsoup.nodes.Entities.unescape(text);
            org.junit.Assert.fail("notMissingSupplementals_cf18580_cf20879 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#notMissingSupplementals */
    /* amplification of org.jsoup.nodes.EntitiesTest#notMissingSupplementals_cf18546 */
    @org.junit.Test(timeout = 10000)
    public void notMissingSupplementals_cf18546_cf20743_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_12_1 = 0;
            java.lang.String text = "&npolint; &qfr;";
            java.lang.String un = "\u2a14 \ud835\udd2e";// ?
            
            // StatementAdderOnAssert create random local variable
            int[] vc_4383 = new int []{};
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_4381 = new java.lang.String();
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_4378 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            int o_notMissingSupplementals_cf18546__10 = // StatementAdderMethod cloned existing statement
vc_4378.codepointsForName(vc_4381, vc_4383);
            // MethodAssertGenerator build local variable
            Object o_12_0 = o_notMissingSupplementals_cf18546__10;
            // StatementAdderOnAssert create null value
            java.lang.String vc_4938 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_4936 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_4936.getCharacterByName(vc_4938);
            // MethodAssertGenerator build local variable
            Object o_20_0 = org.jsoup.nodes.Entities.unescape(text);
            org.junit.Assert.fail("notMissingSupplementals_cf18546_cf20743 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#notMissingSupplementals */
    /* amplification of org.jsoup.nodes.EntitiesTest#notMissingSupplementals_cf18542 */
    @org.junit.Test(timeout = 10000)
    public void notMissingSupplementals_cf18542_cf19951_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_10_1 = 0;
            java.lang.String text = "&npolint; &qfr;";
            java.lang.String un = "\u2a14 \ud835\udd2e";// ?
            
            // StatementAdderOnAssert create random local variable
            int[] vc_4383 = new int []{};
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_4378 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            int o_notMissingSupplementals_cf18542__8 = // StatementAdderMethod cloned existing statement
vc_4378.codepointsForName(un, vc_4383);
            // MethodAssertGenerator build local variable
            Object o_10_0 = o_notMissingSupplementals_cf18542__8;
            // StatementAdderOnAssert create random local variable
            int[] vc_4751 = new int []{690456478,1308090055,199209743};
            // StatementAdderOnAssert create null value
            java.lang.String vc_4748 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            vc_4378.codepointsForName(vc_4748, vc_4751);
            // MethodAssertGenerator build local variable
            Object o_18_0 = org.jsoup.nodes.Entities.unescape(text);
            org.junit.Assert.fail("notMissingSupplementals_cf18542_cf19951 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#notMissingSupplementals */
    /* amplification of org.jsoup.nodes.EntitiesTest#notMissingSupplementals_cf18544 */
    @org.junit.Test(timeout = 10000)
    public void notMissingSupplementals_cf18544_cf20423_failAssert6_literalMutation23682() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_12_1 = 0;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_12_1, 0);
            java.lang.String text = "Y>Q@=x&G:v]aXfi";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(text, "Y>Q@=x&G:v]aXfi");
            java.lang.String un = "\u2a14 \ud835\udd2e";// ?
            
            // StatementAdderOnAssert create random local variable
            int[] vc_4383 = new int []{};
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_883 = "&npolint; &qfr;";
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_4378 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            int o_notMissingSupplementals_cf18544__10 = // StatementAdderMethod cloned existing statement
vc_4378.codepointsForName(String_vc_883, vc_4383);
            // MethodAssertGenerator build local variable
            Object o_12_0 = o_notMissingSupplementals_cf18544__10;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_12_0, 0);
            // StatementAdderOnAssert create random local variable
            boolean vc_4866 = false;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(vc_4866);
            // StatementAdderOnAssert create null value
            java.lang.String vc_4864 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_4864);
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_4862 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_4862);
            // StatementAdderMethod cloned existing statement
            vc_4862.unescape(vc_4864, vc_4866);
            // MethodAssertGenerator build local variable
            Object o_22_0 = org.jsoup.nodes.Entities.unescape(text);
            org.junit.Assert.fail("notMissingSupplementals_cf18544_cf20423 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#notMissingSupplementals */
    /* amplification of org.jsoup.nodes.EntitiesTest#notMissingSupplementals_cf18582 */
    @org.junit.Test(timeout = 10000)
    public void notMissingSupplementals_cf18582_cf21352_failAssert31_literalMutation23869() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String text = "&npolint; &qfr;";
            java.lang.String un = "\u2a14P \ud835\udd2e";// ?
            
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(un, "\u2a14P \ud835\udd2e");
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_4397 = new java.lang.String();
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_4394 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            java.lang.String o_notMissingSupplementals_cf18582__8 = // StatementAdderMethod cloned existing statement
vc_4394.getByName(vc_4397);
            // MethodAssertGenerator build local variable
            Object o_10_0 = o_notMissingSupplementals_cf18582__8;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_10_0, "");
            // StatementAdderOnAssert create random local variable
            boolean vc_5096 = false;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(vc_5096);
            // StatementAdderOnAssert create null value
            java.lang.String vc_5094 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_5094);
            // StatementAdderMethod cloned existing statement
            vc_4394.unescape(vc_5094, vc_5096);
            // MethodAssertGenerator build local variable
            Object o_18_0 = org.jsoup.nodes.Entities.unescape(text);
            org.junit.Assert.fail("notMissingSupplementals_cf18582_cf21352 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#quoteReplacements */
    @org.junit.Test(timeout = 10000)
    public void quoteReplacements_cf23940() {
        java.lang.String escaped = "&#92; &#36;";
        java.lang.String unescaped = "\\ $";
        // StatementAdderOnAssert create null value
        org.jsoup.nodes.Entities vc_5636 = (org.jsoup.nodes.Entities)null;
        // AssertGenerator replace invocation
        java.lang.String o_quoteReplacements_cf23940__5 = // StatementAdderMethod cloned existing statement
vc_5636.getByName(escaped);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_quoteReplacements_cf23940__5, "");
        org.junit.Assert.assertEquals(unescaped, org.jsoup.nodes.Entities.unescape(escaped));
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#quoteReplacements */
    @org.junit.Test(timeout = 10000)
    public void quoteReplacements_cf23903() {
        java.lang.String escaped = "&#92; &#36;";
        java.lang.String unescaped = "\\ $";
        // StatementAdderOnAssert create null value
        int[] vc_5624 = (int[])null;
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_1134 = "&#92; &#36;";
        // StatementAdderOnAssert create null value
        org.jsoup.nodes.Entities vc_5620 = (org.jsoup.nodes.Entities)null;
        // AssertGenerator replace invocation
        int o_quoteReplacements_cf23903__9 = // StatementAdderMethod cloned existing statement
vc_5620.codepointsForName(String_vc_1134, vc_5624);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_quoteReplacements_cf23903__9, 0);
        org.junit.Assert.assertEquals(unescaped, org.jsoup.nodes.Entities.unescape(escaped));
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#quoteReplacements */
    @org.junit.Test(timeout = 10000)
    public void quoteReplacements_cf23915_failAssert14() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String escaped = "&#92; &#36;";
            java.lang.String unescaped = "\\ $";
            // StatementAdderOnAssert create null value
            java.lang.String vc_5628 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_5626 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_5626.getCharacterByName(vc_5628);
            // MethodAssertGenerator build local variable
            Object o_9_0 = org.jsoup.nodes.Entities.unescape(escaped);
            org.junit.Assert.fail("quoteReplacements_cf23915 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#quoteReplacements */
    @org.junit.Test(timeout = 10000)
    public void quoteReplacements_cf23883_failAssert10() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String escaped = "&#92; &#36;";
            java.lang.String unescaped = "\\ $";
            // StatementAdderOnAssert create null value
            java.lang.String vc_5614 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_5612 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_5612.isBaseNamedEntity(vc_5614);
            // MethodAssertGenerator build local variable
            Object o_9_0 = org.jsoup.nodes.Entities.unescape(escaped);
            org.junit.Assert.fail("quoteReplacements_cf23883 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#quoteReplacements */
    @org.junit.Test(timeout = 10000)
    public void quoteReplacements_cf23955_failAssert17() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String escaped = "&#92; &#36;";
            java.lang.String unescaped = "\\ $";
            // StatementAdderOnAssert create random local variable
            boolean vc_5648 = true;
            // StatementAdderOnAssert create null value
            java.lang.String vc_5646 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_5644 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_5644.unescape(vc_5646, vc_5648);
            // MethodAssertGenerator build local variable
            Object o_11_0 = org.jsoup.nodes.Entities.unescape(escaped);
            org.junit.Assert.fail("quoteReplacements_cf23955 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#quoteReplacements */
    /* amplification of org.jsoup.nodes.EntitiesTest#quoteReplacements_cf23947 */
    @org.junit.Test(timeout = 10000)
    public void quoteReplacements_cf23947_failAssert16_literalMutation28304() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String escaped = "&#92 &#36;";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(escaped, "&#92 &#36;");
            java.lang.String unescaped = "\\ $";
            // StatementAdderOnAssert create null value
            java.lang.String vc_5642 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_5640 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_5640.unescape(vc_5642);
            // MethodAssertGenerator build local variable
            Object o_9_0 = org.jsoup.nodes.Entities.unescape(escaped);
            org.junit.Assert.fail("quoteReplacements_cf23947 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#quoteReplacements */
    /* amplification of org.jsoup.nodes.EntitiesTest#quoteReplacements_cf23941 */
    @org.junit.Test(timeout = 10000)
    public void quoteReplacements_cf23941_cf26508_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String escaped = "&#92; &#36;";
            java.lang.String unescaped = "\\ $";
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_1137 = "\\ $";
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_5636 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            java.lang.String o_quoteReplacements_cf23941__7 = // StatementAdderMethod cloned existing statement
vc_5636.getByName(String_vc_1137);
            // MethodAssertGenerator build local variable
            Object o_9_0 = o_quoteReplacements_cf23941__7;
            // StatementAdderOnAssert create null value
            java.lang.String vc_6282 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            vc_5636.getByName(vc_6282);
            // MethodAssertGenerator build local variable
            Object o_15_0 = org.jsoup.nodes.Entities.unescape(escaped);
            org.junit.Assert.fail("quoteReplacements_cf23941_cf26508 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#quoteReplacements */
    /* amplification of org.jsoup.nodes.EntitiesTest#quoteReplacements_cf23902 */
    @org.junit.Test(timeout = 10000)
    public void quoteReplacements_cf23902_cf25373_failAssert14_literalMutation29225() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_9_1 = 0;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_9_1, 0);
            java.lang.String escaped = "&#92; &#36;";
            java.lang.String unescaped = "\\f $";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(unescaped, "\\f $");
            // StatementAdderOnAssert create random local variable
            int[] vc_5625 = new int []{-1463504167,-1236258930,-833971921,1633813483};
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_5620 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            int o_quoteReplacements_cf23902__7 = // StatementAdderMethod cloned existing statement
vc_5620.codepointsForName(escaped, vc_5625);
            // MethodAssertGenerator build local variable
            Object o_9_0 = o_quoteReplacements_cf23902__7;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_9_0, 0);
            // StatementAdderOnAssert create null value
            java.lang.String vc_6006 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_6006);
            // StatementAdderMethod cloned existing statement
            vc_5620.getByName(vc_6006);
            // MethodAssertGenerator build local variable
            Object o_15_0 = org.jsoup.nodes.Entities.unescape(escaped);
            org.junit.Assert.fail("quoteReplacements_cf23902_cf25373 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#quoteReplacements */
    /* amplification of org.jsoup.nodes.EntitiesTest#quoteReplacements_cf23905 */
    @org.junit.Test(timeout = 10000)
    public void quoteReplacements_cf23905_cf25913_failAssert33_literalMutation29408() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_11_1 = 0;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_11_1, 0);
            java.lang.String escaped = "&#92;&#36;";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(escaped, "&#92;&#36;");
            java.lang.String unescaped = "\\ $";
            // StatementAdderOnAssert create null value
            int[] vc_5624 = (int[])null;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_5623 = new java.lang.String();
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_5620 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            int o_quoteReplacements_cf23905__9 = // StatementAdderMethod cloned existing statement
vc_5620.codepointsForName(vc_5623, vc_5624);
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_quoteReplacements_cf23905__9;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_11_0, 0);
            // StatementAdderOnAssert create null value
            java.lang.String vc_6134 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_6134);
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_6132 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_6132);
            // StatementAdderMethod cloned existing statement
            vc_6132.getCharacterByName(vc_6134);
            // MethodAssertGenerator build local variable
            Object o_19_0 = org.jsoup.nodes.Entities.unescape(escaped);
            org.junit.Assert.fail("quoteReplacements_cf23905_cf25913 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#quoteReplacements */
    /* amplification of org.jsoup.nodes.EntitiesTest#quoteReplacements_cf23958 */
    @org.junit.Test(timeout = 10000)
    public void quoteReplacements_cf23958_cf28067_failAssert9_literalMutation29168() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String escaped = "&#92; &#36;";
            java.lang.String unescaped = "YN8";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(unescaped, "YN8");
            // StatementAdderOnAssert create random local variable
            boolean vc_5648 = true;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_5647 = new java.lang.String();
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_5644 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            java.lang.String o_quoteReplacements_cf23958__9 = // StatementAdderMethod cloned existing statement
vc_5644.unescape(vc_5647, vc_5648);
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_quoteReplacements_cf23958__9;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_11_0, "");
            // StatementAdderOnAssert create random local variable
            boolean vc_6614 = true;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(vc_6614);
            // StatementAdderOnAssert create null value
            java.lang.String vc_6612 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_6612);
            // StatementAdderMethod cloned existing statement
            vc_5644.unescape(vc_6612, vc_6614);
            // MethodAssertGenerator build local variable
            Object o_19_0 = org.jsoup.nodes.Entities.unescape(escaped);
            org.junit.Assert.fail("quoteReplacements_cf23958_cf28067 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#quoteReplacements */
    /* amplification of org.jsoup.nodes.EntitiesTest#quoteReplacements_cf23941 */
    @org.junit.Test(timeout = 10000)
    public void quoteReplacements_cf23941_cf26508_failAssert7_literalMutation29153() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String escaped = "&#92; &#36;";
            java.lang.String unescaped = "A $";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(unescaped, "A $");
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_1137 = "\\ $";
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_5636 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            java.lang.String o_quoteReplacements_cf23941__7 = // StatementAdderMethod cloned existing statement
vc_5636.getByName(String_vc_1137);
            // MethodAssertGenerator build local variable
            Object o_9_0 = o_quoteReplacements_cf23941__7;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_9_0, "");
            // StatementAdderOnAssert create null value
            java.lang.String vc_6282 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_6282);
            // StatementAdderMethod cloned existing statement
            vc_5636.getByName(vc_6282);
            // MethodAssertGenerator build local variable
            Object o_15_0 = org.jsoup.nodes.Entities.unescape(escaped);
            org.junit.Assert.fail("quoteReplacements_cf23941_cf26508 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#strictUnescape */
    @org.junit.Test(timeout = 10000)
    public void strictUnescape_cf29493() {
        // for attributes, enforce strict unescaping (must look like &#xxx; , not just &#xxx)
        java.lang.String text = "Hello &amp= &amp;";
        org.junit.Assert.assertEquals("Hello &amp= &", org.jsoup.nodes.Entities.unescape(text, true));
        org.junit.Assert.assertEquals("Hello &= &", org.jsoup.nodes.Entities.unescape(text));
        // StatementAdderOnAssert create null value
        org.jsoup.nodes.Entities vc_6924 = (org.jsoup.nodes.Entities)null;
        // AssertGenerator replace invocation
        java.lang.String o_strictUnescape_cf29493__9 = // StatementAdderMethod cloned existing statement
vc_6924.getByName(text);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_strictUnescape_cf29493__9, "");
        org.junit.Assert.assertEquals("Hello &= &", org.jsoup.nodes.Entities.unescape(text, false));
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#strictUnescape */
    @org.junit.Test(timeout = 10000)
    public void strictUnescape_cf29468_failAssert24() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // for attributes, enforce strict unescaping (must look like &#xxx; , not just &#xxx)
            java.lang.String text = "Hello &amp= &amp;";
            // MethodAssertGenerator build local variable
            Object o_3_0 = org.jsoup.nodes.Entities.unescape(text, true);
            // MethodAssertGenerator build local variable
            Object o_5_0 = org.jsoup.nodes.Entities.unescape(text);
            // StatementAdderOnAssert create null value
            java.lang.String vc_6916 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_6914 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_6914.getCharacterByName(vc_6916);
            // MethodAssertGenerator build local variable
            Object o_13_0 = org.jsoup.nodes.Entities.unescape(text, false);
            org.junit.Assert.fail("strictUnescape_cf29468 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#strictUnescape */
    @org.junit.Test(timeout = 10000)
    public void strictUnescape_cf29454() {
        // for attributes, enforce strict unescaping (must look like &#xxx; , not just &#xxx)
        java.lang.String text = "Hello &amp= &amp;";
        org.junit.Assert.assertEquals("Hello &amp= &", org.jsoup.nodes.Entities.unescape(text, true));
        org.junit.Assert.assertEquals("Hello &= &", org.jsoup.nodes.Entities.unescape(text));
        // StatementAdderOnAssert create null value
        int[] vc_6912 = (int[])null;
        // StatementAdderOnAssert create null value
        org.jsoup.nodes.Entities vc_6908 = (org.jsoup.nodes.Entities)null;
        // AssertGenerator replace invocation
        int o_strictUnescape_cf29454__11 = // StatementAdderMethod cloned existing statement
vc_6908.codepointsForName(text, vc_6912);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_strictUnescape_cf29454__11, 0);
        org.junit.Assert.assertEquals("Hello &= &", org.jsoup.nodes.Entities.unescape(text, false));
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#strictUnescape */
    @org.junit.Test(timeout = 10000)
    public void strictUnescape_cf29512() {
        // for attributes, enforce strict unescaping (must look like &#xxx; , not just &#xxx)
        java.lang.String text = "Hello &amp= &amp;";
        org.junit.Assert.assertEquals("Hello &amp= &", org.jsoup.nodes.Entities.unescape(text, true));
        org.junit.Assert.assertEquals("Hello &= &", org.jsoup.nodes.Entities.unescape(text));
        // StatementAdderOnAssert create literal from method
        boolean boolean_vc_1398 = true;
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_1397 = "Hello &amp= &";
        // StatementAdderOnAssert create null value
        org.jsoup.nodes.Entities vc_6932 = (org.jsoup.nodes.Entities)null;
        // AssertGenerator replace invocation
        java.lang.String o_strictUnescape_cf29512__13 = // StatementAdderMethod cloned existing statement
vc_6932.unescape(String_vc_1397, boolean_vc_1398);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_strictUnescape_cf29512__13, "Hello &amp= &");
        org.junit.Assert.assertEquals("Hello &= &", org.jsoup.nodes.Entities.unescape(text, false));
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#strictUnescape */
    @org.junit.Test(timeout = 10000)
    public void strictUnescape_cf29509_failAssert28() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // for attributes, enforce strict unescaping (must look like &#xxx; , not just &#xxx)
            java.lang.String text = "Hello &amp= &amp;";
            // MethodAssertGenerator build local variable
            Object o_3_0 = org.jsoup.nodes.Entities.unescape(text, true);
            // MethodAssertGenerator build local variable
            Object o_5_0 = org.jsoup.nodes.Entities.unescape(text);
            // StatementAdderOnAssert create random local variable
            boolean vc_6936 = true;
            // StatementAdderOnAssert create null value
            java.lang.String vc_6934 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_6932 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_6932.unescape(vc_6934, vc_6936);
            // MethodAssertGenerator build local variable
            Object o_15_0 = org.jsoup.nodes.Entities.unescape(text, false);
            org.junit.Assert.fail("strictUnescape_cf29509 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#strictUnescape */
    /* amplification of org.jsoup.nodes.EntitiesTest#strictUnescape_cf29513 */
    @org.junit.Test(timeout = 10000)
    public void strictUnescape_cf29513_cf35198_failAssert15() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // for attributes, enforce strict unescaping (must look like &#xxx; , not just &#xxx)
            java.lang.String text = "Hello &amp= &amp;";
            // MethodAssertGenerator build local variable
            Object o_3_0 = org.jsoup.nodes.Entities.unescape(text, true);
            // MethodAssertGenerator build local variable
            Object o_5_0 = org.jsoup.nodes.Entities.unescape(text);
            // StatementAdderOnAssert create random local variable
            boolean vc_6936 = true;
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_1397 = "Hello &amp= &";
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_6932 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            java.lang.String o_strictUnescape_cf29513__13 = // StatementAdderMethod cloned existing statement
vc_6932.unescape(String_vc_1397, vc_6936);
            // MethodAssertGenerator build local variable
            Object o_15_0 = o_strictUnescape_cf29513__13;
            // StatementAdderOnAssert create null value
            java.lang.String vc_7928 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_7926 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_7926.getCharacterByName(vc_7928);
            // MethodAssertGenerator build local variable
            Object o_23_0 = org.jsoup.nodes.Entities.unescape(text, false);
            org.junit.Assert.fail("strictUnescape_cf29513_cf35198 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#strictUnescape */
    /* amplification of org.jsoup.nodes.EntitiesTest#strictUnescape_cf29494 */
    @org.junit.Test(timeout = 10000)
    public void strictUnescape_cf29494_cf32966_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // for attributes, enforce strict unescaping (must look like &#xxx; , not just &#xxx)
            java.lang.String text = "Hello &amp= &amp;";
            // MethodAssertGenerator build local variable
            Object o_3_0 = org.jsoup.nodes.Entities.unescape(text, true);
            // MethodAssertGenerator build local variable
            Object o_5_0 = org.jsoup.nodes.Entities.unescape(text);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_1395 = "Hello &amp= &";
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_6924 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            java.lang.String o_strictUnescape_cf29494__11 = // StatementAdderMethod cloned existing statement
vc_6924.getByName(String_vc_1395);
            // MethodAssertGenerator build local variable
            Object o_13_0 = o_strictUnescape_cf29494__11;
            // StatementAdderOnAssert create random local variable
            int[] vc_7557 = new int []{-107849837,-427424743,599138541,867131021};
            // StatementAdderOnAssert create null value
            java.lang.String vc_7554 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            vc_6924.codepointsForName(vc_7554, vc_7557);
            // MethodAssertGenerator build local variable
            Object o_21_0 = org.jsoup.nodes.Entities.unescape(text, false);
            org.junit.Assert.fail("strictUnescape_cf29494_cf32966 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#strictUnescape */
    /* amplification of org.jsoup.nodes.EntitiesTest#strictUnescape_cf29500 */
    @org.junit.Test(timeout = 10000)
    public void strictUnescape_cf29500_failAssert26_literalMutation36100() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // for attributes, enforce strict unescaping (must look like &#xxx; , not just &#xxx)
            java.lang.String text = "Hello amp= &amp;";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(text, "Hello amp= &amp;");
            // MethodAssertGenerator build local variable
            Object o_3_0 = org.jsoup.nodes.Entities.unescape(text, true);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_3_0, "Hello amp= &");
            // MethodAssertGenerator build local variable
            Object o_5_0 = org.jsoup.nodes.Entities.unescape(text);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_5_0, "Hello amp= &");
            // StatementAdderOnAssert create null value
            java.lang.String vc_6930 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_6928 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_6928.unescape(vc_6930);
            // MethodAssertGenerator build local variable
            Object o_13_0 = org.jsoup.nodes.Entities.unescape(text, false);
            org.junit.Assert.fail("strictUnescape_cf29500 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#strictUnescape */
    /* amplification of org.jsoup.nodes.EntitiesTest#strictUnescape_cf29494 */
    @org.junit.Test(timeout = 10000)
    public void strictUnescape_cf29494_cf32966_failAssert3_literalMutation36819() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // for attributes, enforce strict unescaping (must look like &#xxx; , not just &#xxx)
            java.lang.String text = "Hello &Hamp= &amp;";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(text, "Hello &Hamp= &amp;");
            // MethodAssertGenerator build local variable
            Object o_3_0 = org.jsoup.nodes.Entities.unescape(text, true);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_3_0, "Hello &Hamp= &");
            // MethodAssertGenerator build local variable
            Object o_5_0 = org.jsoup.nodes.Entities.unescape(text);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_5_0, "Hello &Hamp= &");
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_1395 = "Hello &amp= &";
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_6924 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            java.lang.String o_strictUnescape_cf29494__11 = // StatementAdderMethod cloned existing statement
vc_6924.getByName(String_vc_1395);
            // MethodAssertGenerator build local variable
            Object o_13_0 = o_strictUnescape_cf29494__11;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_13_0, "");
            // StatementAdderOnAssert create random local variable
            int[] vc_7557 = new int []{-107849837,-427424743,599138541,867131021};
            // AssertGenerator add assertion
            int[] array_425334860 = new int[]{-107849837, -427424743, 599138541, 867131021};
	int[] array_287949804 = (int[])vc_7557;
	for(int ii = 0; ii <array_425334860.length; ii++) {
		org.junit.Assert.assertEquals(array_425334860[ii], array_287949804[ii]);
	};
            // StatementAdderOnAssert create null value
            java.lang.String vc_7554 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_7554);
            // StatementAdderMethod cloned existing statement
            vc_6924.codepointsForName(vc_7554, vc_7557);
            // MethodAssertGenerator build local variable
            Object o_21_0 = org.jsoup.nodes.Entities.unescape(text, false);
            org.junit.Assert.fail("strictUnescape_cf29494_cf32966 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#strictUnescape */
    /* amplification of org.jsoup.nodes.EntitiesTest#strictUnescape_cf29501 */
    @org.junit.Test(timeout = 10000)
    public void strictUnescape_cf29501_cf33552_failAssert26_literalMutation36936() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // for attributes, enforce strict unescaping (must look like &#xxx; , not just &#xxx)
            java.lang.String text = "";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(text, "");
            // MethodAssertGenerator build local variable
            Object o_3_0 = org.jsoup.nodes.Entities.unescape(text, true);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_3_0, "");
            // MethodAssertGenerator build local variable
            Object o_5_0 = org.jsoup.nodes.Entities.unescape(text);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_5_0, "");
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_6928 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            java.lang.String o_strictUnescape_cf29501__9 = // StatementAdderMethod cloned existing statement
vc_6928.unescape(text);
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_strictUnescape_cf29501__9;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_11_0, "");
            // StatementAdderOnAssert create literal from method
            boolean boolean_vc_1574 = true;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(boolean_vc_1574);
            // StatementAdderOnAssert create null value
            java.lang.String vc_7670 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_7670);
            // StatementAdderMethod cloned existing statement
            vc_6928.unescape(vc_7670, boolean_vc_1574);
            // MethodAssertGenerator build local variable
            Object o_19_0 = org.jsoup.nodes.Entities.unescape(text, false);
            org.junit.Assert.fail("strictUnescape_cf29501_cf33552 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#strictUnescape */
    /* amplification of org.jsoup.nodes.EntitiesTest#strictUnescape_cf29459 */
    @org.junit.Test(timeout = 10000)
    public void strictUnescape_cf29459_cf32420_failAssert9_literalMutation36847() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_15_1 = 0;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_15_1, 0);
            // for attributes, enforce strict unescaping (must look like &#xxx; , not just &#xxx)
            java.lang.String text = "Hello &amp= &mp;";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(text, "Hello &amp= &mp;");
            // MethodAssertGenerator build local variable
            Object o_3_0 = org.jsoup.nodes.Entities.unescape(text, true);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_3_0, "Hello &amp= \u2213");
            // MethodAssertGenerator build local variable
            Object o_5_0 = org.jsoup.nodes.Entities.unescape(text);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_5_0, "Hello &= \u2213");
            // StatementAdderOnAssert create random local variable
            int[] vc_6913 = new int []{91288898};
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_6911 = new java.lang.String();
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_6908 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            int o_strictUnescape_cf29459__13 = // StatementAdderMethod cloned existing statement
vc_6908.codepointsForName(vc_6911, vc_6913);
            // MethodAssertGenerator build local variable
            Object o_15_0 = o_strictUnescape_cf29459__13;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_15_0, 0);
            // StatementAdderOnAssert create null value
            java.lang.String vc_7454 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_7454);
            // StatementAdderMethod cloned existing statement
            vc_6908.isBaseNamedEntity(vc_7454);
            // MethodAssertGenerator build local variable
            Object o_21_0 = org.jsoup.nodes.Entities.unescape(text, false);
            org.junit.Assert.fail("strictUnescape_cf29459_cf32420 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#xhtml */
    @org.junit.Test(timeout = 10000)
    public void xhtml_cf37041() {
        java.lang.String text = "&amp; &gt; &lt; &quot;";
        org.junit.Assert.assertEquals(38, org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("amp"));
        org.junit.Assert.assertEquals(62, org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("gt"));
        org.junit.Assert.assertEquals(60, org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("lt"));
        org.junit.Assert.assertEquals(34, org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("quot"));
        org.junit.Assert.assertEquals("amp", org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(38));
        org.junit.Assert.assertEquals("gt", org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(62));
        org.junit.Assert.assertEquals("lt", org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(60));
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_1714 = "&amp; &gt; &lt; &quot;";
        // StatementAdderOnAssert create null value
        org.jsoup.nodes.Entities vc_8258 = (org.jsoup.nodes.Entities)null;
        // AssertGenerator replace invocation
        java.lang.String o_xhtml_cf37041__20 = // StatementAdderMethod cloned existing statement
vc_8258.getByName(String_vc_1714);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_xhtml_cf37041__20, "");
        org.junit.Assert.assertEquals("quot", org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(34));
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#xhtml */
    @org.junit.Test(timeout = 10000)
    public void xhtml_cf37015_failAssert35() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String text = "&amp; &gt; &lt; &quot;";
            // MethodAssertGenerator build local variable
            Object o_2_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("amp");
            // MethodAssertGenerator build local variable
            Object o_4_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("gt");
            // MethodAssertGenerator build local variable
            Object o_6_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("lt");
            // MethodAssertGenerator build local variable
            Object o_8_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("quot");
            // MethodAssertGenerator build local variable
            Object o_10_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(38);
            // MethodAssertGenerator build local variable
            Object o_12_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(62);
            // MethodAssertGenerator build local variable
            Object o_14_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(60);
            // StatementAdderOnAssert create null value
            java.lang.String vc_8250 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_8248 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_8248.getCharacterByName(vc_8250);
            // MethodAssertGenerator build local variable
            Object o_22_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(34);
            org.junit.Assert.fail("xhtml_cf37015 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#xhtml */
    @org.junit.Test(timeout = 10000)
    public void xhtml_cf37002() {
        java.lang.String text = "&amp; &gt; &lt; &quot;";
        org.junit.Assert.assertEquals(38, org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("amp"));
        org.junit.Assert.assertEquals(62, org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("gt"));
        org.junit.Assert.assertEquals(60, org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("lt"));
        org.junit.Assert.assertEquals(34, org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("quot"));
        org.junit.Assert.assertEquals("amp", org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(38));
        org.junit.Assert.assertEquals("gt", org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(62));
        org.junit.Assert.assertEquals("lt", org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(60));
        // StatementAdderOnAssert create random local variable
        int[] vc_8247 = new int []{-359420628};
        // StatementAdderOnAssert create null value
        org.jsoup.nodes.Entities vc_8242 = (org.jsoup.nodes.Entities)null;
        // AssertGenerator replace invocation
        int o_xhtml_cf37002__20 = // StatementAdderMethod cloned existing statement
vc_8242.codepointsForName(text, vc_8247);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_xhtml_cf37002__20, 0);
        org.junit.Assert.assertEquals("quot", org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(34));
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#xhtml */
    @org.junit.Test(timeout = 10000)
    public void xhtml_cf37055_failAssert38() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String text = "&amp; &gt; &lt; &quot;";
            // MethodAssertGenerator build local variable
            Object o_2_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("amp");
            // MethodAssertGenerator build local variable
            Object o_4_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("gt");
            // MethodAssertGenerator build local variable
            Object o_6_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("lt");
            // MethodAssertGenerator build local variable
            Object o_8_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("quot");
            // MethodAssertGenerator build local variable
            Object o_10_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(38);
            // MethodAssertGenerator build local variable
            Object o_12_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(62);
            // MethodAssertGenerator build local variable
            Object o_14_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(60);
            // StatementAdderOnAssert create random local variable
            boolean vc_8270 = true;
            // StatementAdderOnAssert create null value
            java.lang.String vc_8268 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_8266 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_8266.unescape(vc_8268, vc_8270);
            // MethodAssertGenerator build local variable
            Object o_24_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(34);
            org.junit.Assert.fail("xhtml_cf37055 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#xhtml */
    /* amplification of org.jsoup.nodes.EntitiesTest#xhtml_cf37042 */
    @org.junit.Test(timeout = 10000)
    public void xhtml_cf37042_cf39957_failAssert36() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String text = "&amp; &gt; &lt; &quot;";
            // MethodAssertGenerator build local variable
            Object o_2_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("amp");
            // MethodAssertGenerator build local variable
            Object o_4_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("gt");
            // MethodAssertGenerator build local variable
            Object o_6_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("lt");
            // MethodAssertGenerator build local variable
            Object o_8_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("quot");
            // MethodAssertGenerator build local variable
            Object o_10_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(38);
            // MethodAssertGenerator build local variable
            Object o_12_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(62);
            // MethodAssertGenerator build local variable
            Object o_14_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(60);
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_8261 = new java.lang.String();
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_8258 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            java.lang.String o_xhtml_cf37042__20 = // StatementAdderMethod cloned existing statement
vc_8258.getByName(vc_8261);
            // MethodAssertGenerator build local variable
            Object o_22_0 = o_xhtml_cf37042__20;
            // StatementAdderOnAssert create random local variable
            boolean vc_8914 = true;
            // StatementAdderOnAssert create null value
            java.lang.String vc_8912 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_8910 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_8910.unescape(vc_8912, vc_8914);
            // MethodAssertGenerator build local variable
            Object o_32_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(34);
            org.junit.Assert.fail("xhtml_cf37042_cf39957 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#xhtml */
    /* amplification of org.jsoup.nodes.EntitiesTest#xhtml_cf37001 */
    @org.junit.Test(timeout = 10000)
    public void xhtml_cf37001_cf38463_failAssert35() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_22_1 = 0;
            java.lang.String text = "&amp; &gt; &lt; &quot;";
            // MethodAssertGenerator build local variable
            Object o_2_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("amp");
            // MethodAssertGenerator build local variable
            Object o_4_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("gt");
            // MethodAssertGenerator build local variable
            Object o_6_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("lt");
            // MethodAssertGenerator build local variable
            Object o_8_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("quot");
            // MethodAssertGenerator build local variable
            Object o_10_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(38);
            // MethodAssertGenerator build local variable
            Object o_12_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(62);
            // MethodAssertGenerator build local variable
            Object o_14_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(60);
            // StatementAdderOnAssert create null value
            int[] vc_8246 = (int[])null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_8242 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            int o_xhtml_cf37001__20 = // StatementAdderMethod cloned existing statement
vc_8242.codepointsForName(text, vc_8246);
            // MethodAssertGenerator build local variable
            Object o_22_0 = o_xhtml_cf37001__20;
            // StatementAdderOnAssert create null value
            java.lang.String vc_8586 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_8584 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_8584.unescape(vc_8586);
            // MethodAssertGenerator build local variable
            Object o_30_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(34);
            org.junit.Assert.fail("xhtml_cf37001_cf38463 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#xhtml */
    /* amplification of org.jsoup.nodes.EntitiesTest#xhtml_cf37058 */
    @org.junit.Test(timeout = 10000)
    public void xhtml_cf37058_cf41392_failAssert48() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String text = "&amp; &gt; &lt; &quot;";
            // MethodAssertGenerator build local variable
            Object o_2_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("amp");
            // MethodAssertGenerator build local variable
            Object o_4_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("gt");
            // MethodAssertGenerator build local variable
            Object o_6_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("lt");
            // MethodAssertGenerator build local variable
            Object o_8_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("quot");
            // MethodAssertGenerator build local variable
            Object o_10_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(38);
            // MethodAssertGenerator build local variable
            Object o_12_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(62);
            // MethodAssertGenerator build local variable
            Object o_14_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(60);
            // StatementAdderOnAssert create random local variable
            boolean vc_8270 = true;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_8269 = new java.lang.String();
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_8266 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            java.lang.String o_xhtml_cf37058__22 = // StatementAdderMethod cloned existing statement
vc_8266.unescape(vc_8269, vc_8270);
            // MethodAssertGenerator build local variable
            Object o_24_0 = o_xhtml_cf37058__22;
            // StatementAdderOnAssert create null value
            java.lang.String vc_9170 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_9168 = (org.jsoup.nodes.Entities)null;
            // StatementAdderMethod cloned existing statement
            vc_9168.getCharacterByName(vc_9170);
            // MethodAssertGenerator build local variable
            Object o_32_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(34);
            org.junit.Assert.fail("xhtml_cf37058_cf41392 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.EntitiesTest#xhtml */
    /* amplification of org.jsoup.nodes.EntitiesTest#xhtml_cf37004 */
    @org.junit.Test(timeout = 10000)
    public void xhtml_cf37004_cf38811_failAssert41_literalMutation42110() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_24_1 = 1;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_24_1, 1);
            java.lang.String text = "&amp; &gt; &lt; &quot;";
            // MethodAssertGenerator build local variable
            Object o_2_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("amp");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_2_0, 38);
            // MethodAssertGenerator build local variable
            Object o_4_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("gt");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_4_0, 62);
            // MethodAssertGenerator build local variable
            Object o_6_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("lt");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_6_0, 60);
            // MethodAssertGenerator build local variable
            Object o_8_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.codepointForName("quot");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_8_0, 34);
            // MethodAssertGenerator build local variable
            Object o_10_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(38);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_10_0, "amp");
            // MethodAssertGenerator build local variable
            Object o_12_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(62);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_12_0, "gt");
            // MethodAssertGenerator build local variable
            Object o_14_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(60);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_14_0, "lt");
            // StatementAdderOnAssert create random local variable
            int[] vc_8247 = new int []{-359420628};
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_1711 = "";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_1711, "");
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_8242 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator replace invocation
            int o_xhtml_cf37004__22 = // StatementAdderMethod cloned existing statement
vc_8242.codepointsForName(String_vc_1711, vc_8247);
            // MethodAssertGenerator build local variable
            Object o_24_0 = o_xhtml_cf37004__22;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_24_0, 0);
            // StatementAdderOnAssert create null value
            int[] vc_8660 = (int[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_8660);
            // StatementAdderOnAssert create null value
            java.lang.String vc_8658 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_8658);
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Entities vc_8656 = (org.jsoup.nodes.Entities)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_8656);
            // StatementAdderMethod cloned existing statement
            vc_8656.codepointsForName(vc_8658, vc_8660);
            // MethodAssertGenerator build local variable
            Object o_34_0 = org.jsoup.nodes.Entities.EscapeMode.xhtml.nameForCodepoint(34);
            org.junit.Assert.fail("xhtml_cf37004_cf38811 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

