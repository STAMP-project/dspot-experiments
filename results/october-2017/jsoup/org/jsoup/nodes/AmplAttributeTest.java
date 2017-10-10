package org.jsoup.nodes;


public class AmplAttributeTest {
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_0 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_0);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue__7 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue__8 = attr.html();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue__9 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue__8);
    }

    @org.junit.Test(timeout = 10000)
    public void html() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html__3 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html__3);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html__4 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html__5 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)attr).getValue());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#html */
    @org.junit.Test(timeout = 10000)
    public void html_sd14() {
        java.lang.String __DSPOT_key_0 = ",y(q2 5[gpbL[{$QV5:W";
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html_sd14__4 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd14__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html_sd14__5 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd14__5);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html_sd14__6 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd14__6);
        // StatementAdd: add invocation of a method
        attr.setKey(__DSPOT_key_0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd14__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(",y(q2 5[gpbL[{$QV5:W=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(",y(q2 5[gpbL[{$QV5:W", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd14__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd14__6);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#html */
    @org.junit.Test(timeout = 10000)
    public void html_sd15() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html_sd15__3 = attr.html();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html_sd15__4 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd15__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html_sd15__5 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd15__5);
        // AssertGenerator create local variable with return value of invocation
        int o_html_sd15__6 = // StatementAdd: add invocation of a method
        attr.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(234891960, ((int) (o_html_sd15__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd15__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd15__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd15__5);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#html */
    @org.junit.Test(timeout = 10000)
    public void html_literalMutationString4() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("Gdh", "value &");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Gdh=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Gdh", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html_literalMutationString4__3 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Gdh=\"value &amp;\"", o_html_literalMutationString4__3);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html_literalMutationString4__4 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Gdh=\"value &amp;\"", o_html_literalMutationString4__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html_literalMutationString4__5 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Gdh=\"value &amp;\"", o_html_literalMutationString4__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Gdh=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Gdh", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Gdh=\"value &amp;\"", o_html_literalMutationString4__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Gdh=\"value &amp;\"", o_html_literalMutationString4__3);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#html */
    @org.junit.Test(timeout = 10000)
    public void html_sd16() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html_sd16__3 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd16__3);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html_sd16__4 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd16__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html_sd16__5 = attr.toString();
        // AssertGenerator create local variable with return value of invocation
        org.jsoup.nodes.Attribute o_html_sd16__6 = // StatementAdd: add invocation of a method
        attr.clone();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)o_html_sd16__6).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)o_html_sd16__6).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)o_html_sd16__6).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd16__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd16__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd16__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)attr).getValue());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#html */
    @org.junit.Test(timeout = 10000)
    public void html_sd17() {
        java.lang.Object __DSPOT_o_1 = new java.lang.Object();
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html_sd17__5 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd17__5);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html_sd17__6 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd17__6);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html_sd17__7 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd17__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_html_sd17__8 = // StatementAdd: add invocation of a method
        attr.equals(__DSPOT_o_1);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_html_sd17__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd17__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd17__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd17__6);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#html */
    @org.junit.Test(timeout = 10000)
    public void html_sd18() {
        java.lang.String __DSPOT_value_2 = "2[|+mr6#-VtX(r!Fs2l>";
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "value &");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html_sd18__4 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd18__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html_sd18__5 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd18__5);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html_sd18__6 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd18__6);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html_sd18__7 = // StatementAdd: add invocation of a method
        attr.setValue(__DSPOT_value_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("value &", o_html_sd18__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd18__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd18__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"value &amp;\"", o_html_sd18__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"2[|+mr6#-VtX(r!Fs2l>\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("2[|+mr6#-VtX(r!Fs2l>", ((org.jsoup.nodes.Attribute)attr).getValue());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#html */
    @org.junit.Test(timeout = 10000)
    public void html_literalMutationString9() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html_literalMutationString9__3 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"\"", o_html_literalMutationString9__3);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html_literalMutationString9__4 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"\"", o_html_literalMutationString9__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html_literalMutationString9__5 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"\"", o_html_literalMutationString9__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"\"", o_html_literalMutationString9__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"\"", o_html_literalMutationString9__3);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#html */
    @org.junit.Test(timeout = 10000)
    public void html_literalMutationString10() {
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("key", "vclue &");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"vclue &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("vclue &", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html_literalMutationString10__3 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"vclue &amp;\"", o_html_literalMutationString10__3);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html_literalMutationString10__4 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"vclue &amp;\"", o_html_literalMutationString10__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_html_literalMutationString10__5 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"vclue &amp;\"", o_html_literalMutationString10__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"vclue &amp;\"", o_html_literalMutationString10__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"vclue &amp;\"", o_html_literalMutationString10__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key=\"vclue &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("vclue &", ((org.jsoup.nodes.Attribute)attr).getValue());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#html */
    @org.junit.Test(timeout = 10000)
    public void html_literalMutationString1_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute("", "value &");
            attr.html();
            attr.html();
            attr.toString();
            org.junit.Assert.fail("html_literalMutationString1 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_14 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_14);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__7 = attr.html();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__9 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__9);
        // AssertGenerator create local variable with return value of invocation
        int o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__10 = // StatementAdd: add invocation of a method
        attr.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(111849895, ((int) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_14);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191() {
        java.lang.String __DSPOT_key_732 = "r>0!aCVk!S!Cq&Z&g-HM";
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_13 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_13);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__9 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__10 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__10);
        // StatementAdd: add invocation of a method
        attr.setKey(__DSPOT_key_732);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_13);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("r>0!aCVk!S!Cq&Z&g-HM=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("r>0!aCVk!S!Cq&Z&g-HM", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6194() {
        java.lang.Object __DSPOT_o_733 = new java.lang.Object();
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_16 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_16);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6194__9 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6194__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6194__10 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6194__10);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6194__11 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6194__11);
        // AssertGenerator create local variable with return value of invocation
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6194__12 = // StatementAdd: add invocation of a method
        attr.equals(__DSPOT_o_733);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6194__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6194__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6194__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_16);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6194__10);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_15 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__7 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__9 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__9);
        // AssertGenerator create local variable with return value of invocation
        org.jsoup.nodes.Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10 = // StatementAdd: add invocation of a method
        attr.clone();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_15);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__9);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6188() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + ":last-of-type"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1:last-of-type\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1:last-of-type", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_10 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_10);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6188__7 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1:last-of-type\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6188__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6188__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1:last-of-type\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6188__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6188__9 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1:last-of-type\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6188__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1:last-of-type\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1:last-of-type", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1:last-of-type\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6188__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1:last-of-type\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6188__8);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195() {
        java.lang.String __DSPOT_value_734 = "&1&x#LaHAzB|sTDCK4;`";
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_17 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_17);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__9 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__10 = attr.toString();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__11 = // StatementAdd: add invocation of a method
        attr.setValue(__DSPOT_value_734);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_17);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"&amp;1&amp;x#LaHAzB|sTDCK4;`\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("&1&x#LaHAzB|sTDCK4;`", ((org.jsoup.nodes.Attribute)attr).getValue());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(// TestDataMutator on numbers
        135362));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc2", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc2", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc2B", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_1 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179__9 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179__10 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc2", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc2B", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", String_1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc2", s);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195_literalMutationNumber6751() {
        java.lang.String __DSPOT_value_734 = "&1&x#LaHAzB|sTDCK4;`";
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(// TestDataMutator on numbers
        0));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u0000", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"A\u0000B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\u0000B", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_17 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u0000=\"A\u0000B\"", String_17);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__9 = attr.html();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__10 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__10);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__11 = // StatementAdd: add invocation of a method
        attr.setValue(__DSPOT_value_734);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\u0000B", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u0000", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u0000=\"A\u0000B\"", String_17);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"&amp;1&amp;x#LaHAzB|sTDCK4;`\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("&1&x#LaHAzB|sTDCK4;`", ((org.jsoup.nodes.Attribute)attr).getValue());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6182 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6182_literalMutationNumber6347() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(// TestDataMutator on numbers
        33840));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430=\"A\u8430B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\u8430B", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_4 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430=\"A\u8430B\"", String_4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6182__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430=\"A\u8430B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6182__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6182__9 = attr.html();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6182__10 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430=\"A\u8430B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6182__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430=\"A\u8430B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6182__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430=\"A\u8430B\"", String_4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430=\"A\u8430B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\u8430B", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430=\"A\u8430B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6182__9);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193_sd6700() {
        java.lang.Object __DSPOT_o_778 = new java.lang.Object();
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_15 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__7 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__9 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__9);
        // AssertGenerator create local variable with return value of invocation
        org.jsoup.nodes.Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10 = // StatementAdd: add invocation of a method
        attr.clone();
        // AssertGenerator create local variable with return value of invocation
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193_sd6700__22 = // StatementAdd: add invocation of a method
        attr.equals(__DSPOT_o_778);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193_sd6700__22);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_15);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__9);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179_literalMutationNumber6259() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(// TestDataMutator on numbers
        67681));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud802\udc61=\"A\ud802\udc61B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud802\udc61", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud802\udc61B", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_1 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud802\udc61=\"A\ud802\udc61B\"", String_1);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud802\udc61=\"A\ud802\udc61B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179__9 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud802\udc61=\"A\ud802\udc61B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179__10 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud802\udc61=\"A\ud802\udc61B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud802\udc61=\"A\ud802\udc61B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud802\udc61", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud802\udc61B", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud802\udc61=\"A\ud802\udc61B\"", String_1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud802\udc61", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud802\udc61=\"A\ud802\udc61B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud802\udc61=\"A\ud802\udc61B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179__8);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180_sd6313() {
        java.lang.String __DSPOT_value_740 = "Zc]C[i58St7E@+,}lns>";
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(// TestDataMutator on numbers
        135360));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc0B", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_2 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", String_2);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180__9 = attr.html();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180__10 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180__10);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180_sd6313__18 = // StatementAdd: add invocation of a method
        attr.setValue(__DSPOT_value_740);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc0B", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180_sd6313__18);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\"Zc]C[i58St7E@+,}lns>\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Zc]C[i58St7E@+,}lns>", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", String_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180__10);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6189 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6189_sd6575() {
        java.lang.String __DSPOT_key_765 = "(*HOUk%dv72/A8OJTm=Q";
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "4"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc14\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc14", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_11 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_11);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6189__7 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc14\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6189__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6189__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc14\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6189__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6189__9 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc14\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6189__9);
        // StatementAdd: add invocation of a method
        attr.setKey(__DSPOT_key_765);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_11);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc14\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6189__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(*HOUk%dv72/A8OJTm=Q=\"A\ud844\udcc14\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(*HOUk%dv72/A8OJTm=Q", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc14", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc14\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6189__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc14\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6189__7);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192_sd6669() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_14 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_14);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__7 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__9 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__9);
        // AssertGenerator create local variable with return value of invocation
        int o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__10 = // StatementAdd: add invocation of a method
        attr.hashCode();
        // AssertGenerator create local variable with return value of invocation
        int o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192_sd6669__20 = // StatementAdd: add invocation of a method
        attr.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(111849895, ((int) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192_sd6669__20)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_14);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6184 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6184_literalMutationString6411() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("" + s) + ""));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"\ud844\udcc1\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_6 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_6);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6184__7 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"\ud844\udcc1\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6184__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6184__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"\ud844\udcc1\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6184__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6184__9 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"\ud844\udcc1\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6184__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"\ud844\udcc1\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"\ud844\udcc1\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6184__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"\ud844\udcc1\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6184__7);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192_literalMutationNumber6649() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(// TestDataMutator on numbers
        0));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u0000", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        java.lang.String String_14 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u0000=\"A\u0000B\"", String_14);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__7 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__9 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__9);
        // AssertGenerator create local variable with return value of invocation
        int o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__10 = // StatementAdd: add invocation of a method
        attr.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u0000", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u0000=\"A\u0000B\"", String_14);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"A\u0000B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\u0000B", ((org.jsoup.nodes.Attribute)attr).getValue());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193_literalMutationString6693() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        java.lang.String String_15 = ((s + "=\"A") + s) + "";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1", String_15);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__7 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__9 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__9);
        // AssertGenerator create local variable with return value of invocation
        org.jsoup.nodes.Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10 = // StatementAdd: add invocation of a method
        attr.clone();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1", String_15);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__9);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180 */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180_literalMutationString6294 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180_literalMutationString6294_sd11779() {
        java.lang.Object __DSPOT_o_1252 = new java.lang.Object();
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(// TestDataMutator on numbers
        135360));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, ((":last-of-type" + s) + "B"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\":last-of-type\ud844\udcc0B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(":last-of-type\ud844\udcc0B", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_2 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", String_2);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\":last-of-type\ud844\udcc0B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180__9 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\":last-of-type\ud844\udcc0B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180__10 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\":last-of-type\ud844\udcc0B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180__10);
        // AssertGenerator create local variable with return value of invocation
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180_literalMutationString6294_sd11779__19 = // StatementAdd: add invocation of a method
        attr.equals(__DSPOT_o_1252);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180_literalMutationString6294_sd11779__19);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\":last-of-type\ud844\udcc0B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(":last-of-type\ud844\udcc0B", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\":last-of-type\ud844\udcc0B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", String_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\":last-of-type\ud844\udcc0B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\":last-of-type\ud844\udcc0B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180__10);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193 */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193_literalMutationString6683 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193_literalMutationString6683_sd9768() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("Y" + s) + "B"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"Y\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Y\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_15 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_15);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__7 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"Y\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"Y\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__9 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"Y\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__9);
        // AssertGenerator create local variable with return value of invocation
        org.jsoup.nodes.Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10 = // StatementAdd: add invocation of a method
        attr.clone();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"Y\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Y\ud844\udcc1B", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10).getValue());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193_literalMutationString6683_sd9768__20 = // StatementAdd: add invocation of a method
        attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"Y\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193_literalMutationString6683_sd9768__20);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"Y\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Y\ud844\udcc1B", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_15);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"Y\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Y\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"Y\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"Y\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"Y\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__7);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192 */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192_literalMutationNumber6649 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192_literalMutationNumber6649_sd12967() {
        java.lang.String __DSPOT_key_1374 = "M{,!iFw?e1n!O_U4/S;]";
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(// TestDataMutator on numbers
        0));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u0000", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"A\u0000B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\u0000B", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_14 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u0000=\"A\u0000B\"", String_14);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__7 = attr.html();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__8 = attr.html();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__9 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__9);
        // AssertGenerator create local variable with return value of invocation
        int o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__10 = // StatementAdd: add invocation of a method
        attr.hashCode();
        // StatementAdd: add invocation of a method
        attr.setKey(__DSPOT_key_1374);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u0000", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("M{,!iFw?e1n!O_U4/S;]=\"A\u0000B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("M{,!iFw?e1n!O_U4/S;]", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\u0000B", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u0000=\"A\u0000B\"", String_14);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__7);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6187 */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6187_literalMutationString6498 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6187_literalMutationString6498_literalMutationString7480() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("" + s) + ""));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"\ud844\udcc1\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_9 = ((s + ".]k") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1.]k\ud844\udcc1B\"", String_9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6187__7 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"\ud844\udcc1\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6187__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6187__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"\ud844\udcc1\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6187__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6187__9 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"\ud844\udcc1\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6187__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"\ud844\udcc1\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6187__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1.]k\ud844\udcc1B\"", String_9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"\ud844\udcc1\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"\ud844\udcc1\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6187__7);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6194 */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6194_literalMutationString6726 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6194_literalMutationString6726_literalMutationString10876() {
        java.lang.Object __DSPOT_o_733 = new java.lang.Object();
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("" + s) + "B"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_16 = ((s + ":last-of-type") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1:last-of-type\ud844\udcc1B\"", String_16);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6194__9 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6194__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6194__10 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6194__10);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6194__11 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6194__11);
        // AssertGenerator create local variable with return value of invocation
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6194__12 = // StatementAdd: add invocation of a method
        attr.equals(__DSPOT_o_733);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6194__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6194__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1:last-of-type\ud844\udcc1B\"", String_16);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6194__9);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6182 */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6182_literalMutationNumber6347 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6182_literalMutationNumber6347_sd13557() {
        java.lang.String __DSPOT_value_1436 = ")$7{%!$v4E1ev0u?c4^b";
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(// TestDataMutator on numbers
        33840));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430=\"A\u8430B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\u8430B", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_4 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430=\"A\u8430B\"", String_4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6182__8 = attr.html();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6182__9 = attr.html();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6182__10 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430=\"A\u8430B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6182__10);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6182_literalMutationNumber6347_sd13557__18 = // StatementAdd: add invocation of a method
        attr.setValue(__DSPOT_value_1436);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\u8430B", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6182_literalMutationNumber6347_sd13557__18);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430=\"A\u8430B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6182__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430=\"A\u8430B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6182__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430=\"A\u8430B\"", String_4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430=\")$7{%!$v4E1ev0u?c4^b\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(")$7{%!$v4E1ev0u?c4^b", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430=\"A\u8430B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6182__9);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179 */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179_literalMutationNumber6259 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179_literalMutationNumber6259_literalMutationNumber7895() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(// TestDataMutator on numbers
        33840));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430=\"A\u8430B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\u8430B", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_1 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430=\"A\u8430B\"", String_1);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430=\"A\u8430B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179__9 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430=\"A\u8430B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179__10 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430=\"A\u8430B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430=\"A\u8430B\"", String_1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430=\"A\u8430B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430=\"A\u8430B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6179__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430=\"A\u8430B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u8430", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\u8430B", ((org.jsoup.nodes.Attribute)attr).getValue());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195 */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195_literalMutationNumber6753 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195_literalMutationNumber6753_literalMutationString12145() {
        java.lang.String __DSPOT_value_734 = "&1&x#LaHAzB|sTDCK4;`";
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(// TestDataMutator on numbers
        270722));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud8c8\udd82", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud8c8\udd82=\"A\ud8c8\udd82B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud8c8\udd82", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud8c8\udd82B", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_17 = ((s + "") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud8c8\udd82\ud8c8\udd82B\"", String_17);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud8c8\udd82=\"A\ud8c8\udd82B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__9 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud8c8\udd82=\"A\ud8c8\udd82B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__10 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud8c8\udd82=\"A\ud8c8\udd82B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__10);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__11 = // StatementAdd: add invocation of a method
        attr.setValue(__DSPOT_value_734);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud8c8\udd82B", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud8c8\udd82=\"A\ud8c8\udd82B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud8c8\udd82=\"&amp;1&amp;x#LaHAzB|sTDCK4;`\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud8c8\udd82", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("&1&x#LaHAzB|sTDCK4;`", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud8c8\udd82=\"A\ud8c8\udd82B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud8c8\udd82\ud8c8\udd82B\"", String_17);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud8c8\udd82", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud8c8\udd82=\"A\ud8c8\udd82B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__8);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193 */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193_literalMutationString6693 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193_literalMutationString6693_sd12499() {
        java.lang.String __DSPOT_value_1325 = "$CI13p%lvD+<4A2-(r|!";
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_15 = ((s + "=\"A") + s) + "";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1", String_15);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__7 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__8 = attr.html();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__9 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__9);
        // AssertGenerator create local variable with return value of invocation
        org.jsoup.nodes.Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10 = // StatementAdd: add invocation of a method
        attr.clone();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10).getValue());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193_literalMutationString6693_sd12499__21 = // StatementAdd: add invocation of a method
        attr.setValue(__DSPOT_value_1325);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193_literalMutationString6693_sd12499__21);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1", String_15);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"$CI13p%lvD+<4A2-(r|!\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("$CI13p%lvD+<4A2-(r|!", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__8);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6196 */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6196_literalMutationString6791 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6196_literalMutationString6791_sd10836() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_18 = ((s + ":last-of-type") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1:last-of-type\ud844\udcc1B\"", String_18);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6196__7 = attr.html();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6196__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6196__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6196__9 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6196__9);
        // StatementAdd: add invocation of a method
        attr.getKey();
        // AssertGenerator create local variable with return value of invocation
        int o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6196_literalMutationString6791_sd10836__18 = // StatementAdd: add invocation of a method
        attr.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(111849895, ((int) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6196_literalMutationString6791_sd10836__18)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6196__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1:last-of-type\ud844\udcc1B\"", String_18);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6196__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6196__7);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191 */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191_literalMutationString6635 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191_literalMutationString6635_literalMutationString12853() {
        java.lang.String __DSPOT_key_732 = ":last-of-type";
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_13 = ((s + "=\"A") + s) + "";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1", String_13);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__9 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__10 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__10);
        // StatementAdd: add invocation of a method
        attr.setKey(__DSPOT_key_732);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(":last-of-type=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(":last-of-type", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1", String_13);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__9);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180 */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180_sd6313 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180_sd6313_literalMutationString11383() {
        java.lang.String __DSPOT_value_740 = "Zc]C[i58St7E@+,}lns>";
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(// TestDataMutator on numbers
        135360));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc0B", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_2 = ((s + "3\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc03\"A\ud844\udcc0B\"", String_2);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180__9 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180__10 = attr.toString();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180_sd6313__18 = // StatementAdd: add invocation of a method
        attr.setValue(__DSPOT_value_740);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc0B", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180_sd6313__18);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc03\"A\ud844\udcc0B\"", String_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\"Zc]C[i58St7E@+,}lns>\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Zc]C[i58St7E@+,}lns>", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6180__10);
    }
}

