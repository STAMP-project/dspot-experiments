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
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6197() {
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
        java.lang.String String_19 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_19);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6197__7 = attr.html();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6197__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6197__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6197__9 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6197__9);
        // StatementAdd: add invocation of a method
        attr.getValue();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6197__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6197__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_19);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6197__7);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191_sd6642() {
        java.lang.Object __DSPOT_o_772 = new java.lang.Object();
        java.lang.String __DSPOT_key_732 = "r>0!aCVk!S!Cq&Z&g-HM";
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
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
        // AssertGenerator create local variable with return value of invocation
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191_sd6642__21 = // StatementAdd: add invocation of a method
        attr.equals(__DSPOT_o_772);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191_sd6642__21);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_13);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("r>0!aCVk!S!Cq&Z&g-HM=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("r>0!aCVk!S!Cq&Z&g-HM", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__10);
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
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190_sd6607() {
        java.lang.Object __DSPOT_o_769 = new java.lang.Object();
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
        java.lang.String String_12 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_12);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190__7 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190__9 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190__10 = // StatementAdd: add invocation of a method
        attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190__10);
        // AssertGenerator create local variable with return value of invocation
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190_sd6607__22 = // StatementAdd: add invocation of a method
        attr.equals(__DSPOT_o_769);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190_sd6607__22);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_12);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
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
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190_literalMutationString6591() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + ""));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_12 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_12);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190__7 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190__9 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190__10 = // StatementAdd: add invocation of a method
        attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6190__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_12);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191_literalMutationString6628() {
        java.lang.String __DSPOT_key_732 = "r>0!aCVk!S!Cq&Z&g-HM";
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "&"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1&amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1&", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_13 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_13);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1&amp;\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__9 = attr.html();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__10 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1&amp;\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__10);
        // StatementAdd: add invocation of a method
        attr.setKey(__DSPOT_key_732);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1&amp;\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1&amp;\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("r>0!aCVk!S!Cq&Z&g-HM=\"A\ud844\udcc1&amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("r>0!aCVk!S!Cq&Z&g-HM", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1&", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1&amp;\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_13);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191_sd6641() {
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
        // AssertGenerator create local variable with return value of invocation
        org.jsoup.nodes.Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191_sd6641__19 = // StatementAdd: add invocation of a method
        attr.clone();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("r>0!aCVk!S!Cq&Z&g-HM=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191_sd6641__19).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("r>0!aCVk!S!Cq&Z&g-HM", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191_sd6641__19).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191_sd6641__19).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("r>0!aCVk!S!Cq&Z&g-HM=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("r>0!aCVk!S!Cq&Z&g-HM", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_13);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193 */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193_sd6704 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193_sd6704_sd9982() {
        java.lang.String __DSPOT_value_1064 = "1A[>2]L3ga,%L?,A96x>";
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        java.lang.String String_15 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_15);
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
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193_sd6704__20 = // StatementAdd: add invocation of a method
        attr.html();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193_sd6704_sd9982__25 = // StatementAdd: add invocation of a method
        o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10.setValue(__DSPOT_value_1064);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193_sd6704_sd9982__25);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_15);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"1A[>2]L3ga,%L?,A96x>\"", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("1A[>2]L3ga,%L?,A96x>", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__10).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6193_sd6704__20);
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
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6197 */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6197_literalMutationString6825 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6197_literalMutationString6825_sd11360() {
        java.lang.String __DSPOT_value_1208 = "ZG*2b]t+%Z[5xg4*<ls2";
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
        java.lang.String String_19 = ((s + "=\"A") + s) + ":last-of-type";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1:last-of-type", String_19);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6197__7 = attr.html();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6197__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6197__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6197__9 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6197__9);
        // StatementAdd: add invocation of a method
        attr.getValue();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6197_literalMutationString6825_sd11360__19 = // StatementAdd: add invocation of a method
        attr.setValue(__DSPOT_value_1208);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6197_literalMutationString6825_sd11360__19);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6197__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6197__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1:last-of-type", String_19);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6197__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"ZG*2b]t+%Z[5xg4*<ls2\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ZG*2b]t+%Z[5xg4*<ls2", ((org.jsoup.nodes.Attribute)attr).getValue());
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
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195_literalMutationString6762 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195_literalMutationString6762_literalMutationNumber11790() {
        java.lang.String __DSPOT_value_734 = "&1&x#LaHAzB|sTDCK4;`";
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
        java.lang.String String_17 = ((s + ":last-of-type") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc2:last-of-type\ud844\udcc2B\"", String_17);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__9 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__10 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__10);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__11 = // StatementAdd: add invocation of a method
        attr.setValue(__DSPOT_value_734);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc2B", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc2=\"&amp;1&amp;x#LaHAzB|sTDCK4;`\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc2", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("&1&x#LaHAzB|sTDCK4;`", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc2", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc2:last-of-type\ud844\udcc2B\"", String_17);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__10);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195 */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195_literalMutationNumber6751 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195_literalMutationNumber6751_literalMutationString8960() {
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
        java.lang.String String_17 = ((s + "") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u0000\u0000B\"", String_17);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__9 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__9);
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
        org.junit.Assert.assertEquals("\u0000", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u0000\u0000B\"", String_17);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"&amp;1&amp;x#LaHAzB|sTDCK4;`\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("&1&x#LaHAzB|sTDCK4;`", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6195__9);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192 */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192_sd6671 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192_sd6671_sd7752() {
        java.lang.Object __DSPOT_o_775 = new java.lang.Object();
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
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
        // AssertGenerator create local variable with return value of invocation
        int o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__10 = // StatementAdd: add invocation of a method
        attr.hashCode();
        // AssertGenerator create local variable with return value of invocation
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192_sd6671__22 = // StatementAdd: add invocation of a method
        attr.equals(__DSPOT_o_775);
        // AssertGenerator create local variable with return value of invocation
        int o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192_sd6671_sd7752__26 = // StatementAdd: add invocation of a method
        attr.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(111849895, ((int) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192_sd6671_sd7752__26)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_14);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191 */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191_literalMutationNumber6619 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191_literalMutationNumber6619_sd7554() {
        java.lang.String __DSPOT_key_732 = "r>0!aCVk!S!Cq&Z&g-HM";
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(// TestDataMutator on numbers
        135360));
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc0B", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_13 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", String_13);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__9 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__10 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__10);
        // StatementAdd: add invocation of a method
        attr.setKey(__DSPOT_key_732);
        // AssertGenerator create local variable with return value of invocation
        org.jsoup.nodes.Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191_literalMutationNumber6619_sd7554__20 = // StatementAdd: add invocation of a method
        attr.clone();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("r>0!aCVk!S!Cq&Z&g-HM", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191_literalMutationNumber6619_sd7554__20).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("r>0!aCVk!S!Cq&Z&g-HM=\"A\ud844\udcc0B\"", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191_literalMutationNumber6619_sd7554__20).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc0B", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191_literalMutationNumber6619_sd7554__20).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6191__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("r>0!aCVk!S!Cq&Z&g-HM=\"A\ud844\udcc0B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("r>0!aCVk!S!Cq&Z&g-HM", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud844\udcc0B", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", String_13);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6183 */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6183_sd6395 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6183_sd6395_literalMutationString8740() {
        java.lang.String __DSPOT_key_747 = "TWPk#j%Ww$@P|3JXerRh";
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(// TestDataMutator on numbers
        270722));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud8c8\udd82", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("A" + s) + "B"));
        java.lang.String String_5 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud8c8\udd82=\"A\ud8c8\udd82B\"", String_5);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6183__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud8c8\udd82=\"A\ud8c8\udd82B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6183__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6183__9 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud8c8\udd82=\"A\ud8c8\udd82B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6183__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6183__10 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud8c8\udd82=\"A\ud8c8\udd82B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6183__10);
        // StatementAdd: add invocation of a method
        attr.setKey(__DSPOT_key_747);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud8c8\udd82=\"A\ud8c8\udd82B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6183__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud8c8\udd82=\"A\ud8c8\udd82B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6183__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud8c8\udd82=\"A\ud8c8\udd82B\"", String_5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud8c8\udd82=\"A\ud8c8\udd82B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationNumber6183__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("TWPk#j%Ww$@P|3JXerRh=\"A\ud8c8\udd82B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("TWPk#j%Ww$@P|3JXerRh", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A\ud8c8\udd82B", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud8c8\udd82", s);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6187 */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6187_literalMutationString6499 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6187_literalMutationString6499_sd7696() {
        java.lang.Object __DSPOT_o_820 = new java.lang.Object();
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(135361));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("q" + s) + ""));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"q\ud844\udcc1\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("q\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getValue());
        java.lang.String String_9 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6187__7 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"q\ud844\udcc1\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6187__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6187__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"q\ud844\udcc1\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6187__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6187__9 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"q\ud844\udcc1\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6187__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6187_literalMutationString6499_sd7696__18 = // StatementAdd: add invocation of a method
        attr.equals(__DSPOT_o_820);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6187_literalMutationString6499_sd7696__18);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"q\ud844\udcc1\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6187__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"q\ud844\udcc1\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6187__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"q\ud844\udcc1\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("q\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\ud844\udcc1=\"q\ud844\udcc1\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_literalMutationString6187__8);
    }

    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192 */
    /* amplification of org.jsoup.nodes.AttributeTest#testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192_literalMutationString6652 */
    @org.junit.Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192_literalMutationString6652_literalMutationNumber8917() {
        java.lang.String s = new java.lang.String(java.lang.Character.toChars(// TestDataMutator on numbers
        0));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u0000", s);
        org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(s, (("" + s) + "B"));
        java.lang.String String_14 = ((s + "=\"A") + s) + "B\"";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u0000=\"A\u0000B\"", String_14);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__7 = attr.html();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__8 = attr.html();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__9 = attr.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__9);
        // AssertGenerator create local variable with return value of invocation
        int o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__10 = // StatementAdd: add invocation of a method
        attr.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u0000", s);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"\u0000B\"", ((org.jsoup.nodes.Attribute)attr).html());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", ((org.jsoup.nodes.Attribute)attr).getKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u0000B", ((org.jsoup.nodes.Attribute)attr).getValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\u0000=\"A\u0000B\"", String_14);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=\"\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd6192__9);
    }
}

