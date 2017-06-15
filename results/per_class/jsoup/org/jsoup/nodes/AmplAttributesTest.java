

package org.jsoup.nodes;


/**
 * Tests for Attributes.
 *
 * @author Jonathan Hedley
 */
public class AmplAttributesTest {
    @org.junit.Test
    public void html() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        a.put("Tot", "a&p");
        a.put("Hello", "There");
        a.put("data-name", "Jsoup");
        org.junit.Assert.assertEquals(3, a.size());
        org.junit.Assert.assertTrue(a.hasKey("Tot"));
        org.junit.Assert.assertTrue(a.hasKey("Hello"));
        org.junit.Assert.assertTrue(a.hasKey("data-name"));
        org.junit.Assert.assertFalse(a.hasKey("tot"));
        org.junit.Assert.assertTrue(a.hasKeyIgnoreCase("tot"));
        org.junit.Assert.assertEquals("There", a.getIgnoreCase("hEllo"));
        org.junit.Assert.assertEquals(1, a.dataset().size());
        org.junit.Assert.assertEquals("Jsoup", a.dataset().get("name"));
        org.junit.Assert.assertEquals("", a.get("tot"));
        org.junit.Assert.assertEquals("a&p", a.get("Tot"));
        org.junit.Assert.assertEquals("a&p", a.getIgnoreCase("tot"));
        org.junit.Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", a.html());
        org.junit.Assert.assertEquals(a.html(), a.toString());
    }

    @org.junit.Test
    public void testIteratorRemovable() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        a.put("Tot", "a&p");
        a.put("Hello", "There");
        a.put("data-name", "Jsoup");
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        iterator.next();
        iterator.remove();
        org.junit.Assert.assertEquals(2, a.size());
    }

    @org.junit.Test
    public void testIterator() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        java.lang.String[][] datas = new java.lang.String[][]{ new java.lang.String[]{ "Tot" , "raul" } , new java.lang.String[]{ "Hello" , "pismuth" } , new java.lang.String[]{ "data-name" , "Jsoup" } };
        for (java.lang.String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        org.junit.Assert.assertTrue(iterator.hasNext());
        int i = 0;
        for (org.jsoup.nodes.Attribute attribute : a) {
            org.junit.Assert.assertEquals(datas[i][0], attribute.getKey());
            org.junit.Assert.assertEquals(datas[i][1], attribute.getValue());
            i++;
        }
        org.junit.Assert.assertEquals(datas.length, i);
    }

    @org.junit.Test
    public void testIteratorEmpty() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        org.junit.Assert.assertFalse(iterator.hasNext());
    }

    @org.junit.Test
    public void removeCaseSensitive() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        a.put("Tot", "a&p");
        a.put("tot", "one");
        a.put("Hello", "There");
        a.put("hello", "There");
        a.put("data-name", "Jsoup");
        org.junit.Assert.assertEquals(5, a.size());
        a.remove("Tot");
        a.remove("Hello");
        org.junit.Assert.assertEquals(3, a.size());
        org.junit.Assert.assertTrue(a.hasKey("tot"));
        org.junit.Assert.assertFalse(a.hasKey("Tot"));
    }

    /* amplification of org.jsoup.nodes.AttributesTest#html */
    @org.junit.Test(timeout = 10000)
    public void html_cf109() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        a.put("Tot", "a&p");
        a.put("Hello", "There");
        a.put("data-name", "Jsoup");
        org.junit.Assert.assertEquals(3, a.size());
        org.junit.Assert.assertTrue(a.hasKey("Tot"));
        org.junit.Assert.assertTrue(a.hasKey("Hello"));
        org.junit.Assert.assertTrue(a.hasKey("data-name"));
        org.junit.Assert.assertFalse(a.hasKey("tot"));
        org.junit.Assert.assertTrue(a.hasKeyIgnoreCase("tot"));
        org.junit.Assert.assertEquals("There", a.getIgnoreCase("hEllo"));
        org.junit.Assert.assertEquals(1, a.dataset().size());
        org.junit.Assert.assertEquals("Jsoup", a.dataset().get("name"));
        org.junit.Assert.assertEquals("", a.get("tot"));
        org.junit.Assert.assertEquals("a&p", a.get("Tot"));
        org.junit.Assert.assertEquals("a&p", a.getIgnoreCase("tot"));
        org.junit.Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", a.html());
        // AssertGenerator replace invocation
        int o_html_cf109__34 = // StatementAdderMethod cloned existing statement
a.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_html_cf109__34, 1779676296);
        org.junit.Assert.assertEquals(a.html(), a.toString());
    }

    /* amplification of org.jsoup.nodes.AttributesTest#html */
    @org.junit.Test(timeout = 10000)
    public void html_cf87() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        a.put("Tot", "a&p");
        a.put("Hello", "There");
        a.put("data-name", "Jsoup");
        org.junit.Assert.assertEquals(3, a.size());
        org.junit.Assert.assertTrue(a.hasKey("Tot"));
        org.junit.Assert.assertTrue(a.hasKey("Hello"));
        org.junit.Assert.assertTrue(a.hasKey("data-name"));
        org.junit.Assert.assertFalse(a.hasKey("tot"));
        org.junit.Assert.assertTrue(a.hasKeyIgnoreCase("tot"));
        org.junit.Assert.assertEquals("There", a.getIgnoreCase("hEllo"));
        org.junit.Assert.assertEquals(1, a.dataset().size());
        org.junit.Assert.assertEquals("Jsoup", a.dataset().get("name"));
        org.junit.Assert.assertEquals("", a.get("tot"));
        org.junit.Assert.assertEquals("a&p", a.get("Tot"));
        org.junit.Assert.assertEquals("a&p", a.getIgnoreCase("tot"));
        org.junit.Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", a.html());
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_3 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_html_cf87__36 = // StatementAdderMethod cloned existing statement
a.equals(vc_3);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_html_cf87__36);
        org.junit.Assert.assertEquals(a.html(), a.toString());
    }

    /* amplification of org.jsoup.nodes.AttributesTest#html */
    @org.junit.Test(timeout = 10000)
    public void html_cf127() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        a.put("Tot", "a&p");
        a.put("Hello", "There");
        a.put("data-name", "Jsoup");
        org.junit.Assert.assertEquals(3, a.size());
        org.junit.Assert.assertTrue(a.hasKey("Tot"));
        org.junit.Assert.assertTrue(a.hasKey("Hello"));
        org.junit.Assert.assertTrue(a.hasKey("data-name"));
        org.junit.Assert.assertFalse(a.hasKey("tot"));
        org.junit.Assert.assertTrue(a.hasKeyIgnoreCase("tot"));
        org.junit.Assert.assertEquals("There", a.getIgnoreCase("hEllo"));
        org.junit.Assert.assertEquals(1, a.dataset().size());
        org.junit.Assert.assertEquals("Jsoup", a.dataset().get("name"));
        org.junit.Assert.assertEquals("", a.get("tot"));
        org.junit.Assert.assertEquals("a&p", a.get("Tot"));
        org.junit.Assert.assertEquals("a&p", a.getIgnoreCase("tot"));
        org.junit.Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", a.html());
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_3 = "Jsoup";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_3, "Jsoup");
        // AssertGenerator replace invocation
        java.lang.String o_html_cf127__36 = // StatementAdderMethod cloned existing statement
a.getIgnoreCase(String_vc_3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_html_cf127__36, "");
        org.junit.Assert.assertEquals(a.html(), a.toString());
    }

    /* amplification of org.jsoup.nodes.AttributesTest#html */
    @org.junit.Test(timeout = 10000)
    public void html_cf104() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        a.put("Tot", "a&p");
        a.put("Hello", "There");
        a.put("data-name", "Jsoup");
        org.junit.Assert.assertEquals(3, a.size());
        org.junit.Assert.assertTrue(a.hasKey("Tot"));
        org.junit.Assert.assertTrue(a.hasKey("Hello"));
        org.junit.Assert.assertTrue(a.hasKey("data-name"));
        org.junit.Assert.assertFalse(a.hasKey("tot"));
        org.junit.Assert.assertTrue(a.hasKeyIgnoreCase("tot"));
        org.junit.Assert.assertEquals("There", a.getIgnoreCase("hEllo"));
        org.junit.Assert.assertEquals(1, a.dataset().size());
        org.junit.Assert.assertEquals("Jsoup", a.dataset().get("name"));
        org.junit.Assert.assertEquals("", a.get("tot"));
        org.junit.Assert.assertEquals("a&p", a.get("Tot"));
        org.junit.Assert.assertEquals("a&p", a.getIgnoreCase("tot"));
        org.junit.Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", a.html());
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_11 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_11, "");
        // AssertGenerator replace invocation
        boolean o_html_cf104__36 = // StatementAdderMethod cloned existing statement
a.hasKeyIgnoreCase(vc_11);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_html_cf104__36);
        org.junit.Assert.assertEquals(a.html(), a.toString());
    }

    /* amplification of org.jsoup.nodes.AttributesTest#removeCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void removeCaseSensitive_cf42803() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        a.put("Tot", "a&p");
        a.put("tot", "one");
        a.put("Hello", "There");
        a.put("hello", "There");
        a.put("data-name", "Jsoup");
        org.junit.Assert.assertEquals(5, a.size());
        a.remove("Tot");
        a.remove("Hello");
        org.junit.Assert.assertEquals(3, a.size());
        org.junit.Assert.assertTrue(a.hasKey("tot"));
        // AssertGenerator replace invocation
        int o_removeCaseSensitive_cf42803__16 = // StatementAdderMethod cloned existing statement
a.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_removeCaseSensitive_cf42803__16, -1564488751);
        org.junit.Assert.assertFalse(a.hasKey("Tot"));
    }

    /* amplification of org.jsoup.nodes.AttributesTest#removeCaseSensitive */
    @org.junit.Test
    public void removeCaseSensitive_literalMutation42709_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("", "a&p");
            a.put("tot", "one");
            a.put("Hello", "There");
            a.put("hello", "There");
            a.put("data-name", "Jsoup");
            // MethodAssertGenerator build local variable
            Object o_8_0 = a.size();
            a.remove("Tot");
            a.remove("Hello");
            // MethodAssertGenerator build local variable
            Object o_12_0 = a.size();
            // MethodAssertGenerator build local variable
            Object o_14_0 = a.hasKey("tot");
            // MethodAssertGenerator build local variable
            Object o_16_0 = a.hasKey("Tot");
            org.junit.Assert.fail("removeCaseSensitive_literalMutation42709 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#removeCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void removeCaseSensitive_cf42816_failAssert48() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("tot", "one");
            a.put("Hello", "There");
            a.put("hello", "There");
            a.put("data-name", "Jsoup");
            // MethodAssertGenerator build local variable
            Object o_8_0 = a.size();
            a.remove("Tot");
            a.remove("Hello");
            // MethodAssertGenerator build local variable
            Object o_12_0 = a.size();
            // MethodAssertGenerator build local variable
            Object o_14_0 = a.hasKey("tot");
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_12109 = new java.lang.String();
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_12107 = new org.jsoup.nodes.Attributes();
            // StatementAdderMethod cloned existing statement
            vc_12107.get(vc_12109);
            // MethodAssertGenerator build local variable
            Object o_22_0 = a.hasKey("Tot");
            org.junit.Assert.fail("removeCaseSensitive_cf42816 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#removeCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void removeCaseSensitive_cf42897_failAssert83() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("tot", "one");
            a.put("Hello", "There");
            a.put("hello", "There");
            a.put("data-name", "Jsoup");
            // MethodAssertGenerator build local variable
            Object o_8_0 = a.size();
            a.remove("Tot");
            a.remove("Hello");
            // MethodAssertGenerator build local variable
            Object o_12_0 = a.size();
            // MethodAssertGenerator build local variable
            Object o_14_0 = a.hasKey("tot");
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_12154 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            a.removeIgnoreCase(vc_12154);
            // MethodAssertGenerator build local variable
            Object o_20_0 = a.hasKey("Tot");
            org.junit.Assert.fail("removeCaseSensitive_cf42897 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#removeCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void removeCaseSensitive_cf42822_failAssert53() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("tot", "one");
            a.put("Hello", "There");
            a.put("hello", "There");
            a.put("data-name", "Jsoup");
            // MethodAssertGenerator build local variable
            Object o_8_0 = a.size();
            a.remove("Tot");
            a.remove("Hello");
            // MethodAssertGenerator build local variable
            Object o_12_0 = a.size();
            // MethodAssertGenerator build local variable
            Object o_14_0 = a.hasKey("tot");
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_12113 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            a.getIgnoreCase(vc_12113);
            // MethodAssertGenerator build local variable
            Object o_20_0 = a.hasKey("Tot");
            org.junit.Assert.fail("removeCaseSensitive_cf42822 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#removeCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void removeCaseSensitive_cf42796() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        a.put("Tot", "a&p");
        a.put("tot", "one");
        a.put("Hello", "There");
        a.put("hello", "There");
        a.put("data-name", "Jsoup");
        org.junit.Assert.assertEquals(5, a.size());
        a.remove("Tot");
        a.remove("Hello");
        org.junit.Assert.assertEquals(3, a.size());
        org.junit.Assert.assertTrue(a.hasKey("tot"));
        // StatementAdderOnAssert create null value
        java.lang.String vc_12100 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_12100);
        // AssertGenerator replace invocation
        boolean o_removeCaseSensitive_cf42796__18 = // StatementAdderMethod cloned existing statement
a.hasKeyIgnoreCase(vc_12100);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_removeCaseSensitive_cf42796__18);
        org.junit.Assert.assertFalse(a.hasKey("Tot"));
    }

    /* amplification of org.jsoup.nodes.AttributesTest#removeCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void removeCaseSensitive_cf42867_failAssert68() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("tot", "one");
            a.put("Hello", "There");
            a.put("hello", "There");
            a.put("data-name", "Jsoup");
            // MethodAssertGenerator build local variable
            Object o_8_0 = a.size();
            a.remove("Tot");
            a.remove("Hello");
            // MethodAssertGenerator build local variable
            Object o_12_0 = a.size();
            // MethodAssertGenerator build local variable
            Object o_14_0 = a.hasKey("tot");
            // StatementAdderOnAssert create random local variable
            boolean vc_12138 = false;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_12137 = new java.lang.String();
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_12135 = new org.jsoup.nodes.Attributes();
            // StatementAdderMethod cloned existing statement
            vc_12135.put(vc_12137, vc_12138);
            // MethodAssertGenerator build local variable
            Object o_24_0 = a.hasKey("Tot");
            org.junit.Assert.fail("removeCaseSensitive_cf42867 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#removeCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void removeCaseSensitive_cf42780() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        a.put("Tot", "a&p");
        a.put("tot", "one");
        a.put("Hello", "There");
        a.put("hello", "There");
        a.put("data-name", "Jsoup");
        org.junit.Assert.assertEquals(5, a.size());
        a.remove("Tot");
        a.remove("Hello");
        org.junit.Assert.assertEquals(3, a.size());
        org.junit.Assert.assertTrue(a.hasKey("tot"));
        // StatementAdderOnAssert create null value
        java.lang.Object vc_12092 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_12092);
        // AssertGenerator replace invocation
        boolean o_removeCaseSensitive_cf42780__18 = // StatementAdderMethod cloned existing statement
a.equals(vc_12092);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_removeCaseSensitive_cf42780__18);
        org.junit.Assert.assertFalse(a.hasKey("Tot"));
    }

    /* amplification of org.jsoup.nodes.AttributesTest#removeCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void removeCaseSensitive_cf42807() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        a.put("Tot", "a&p");
        a.put("tot", "one");
        a.put("Hello", "There");
        a.put("hello", "There");
        a.put("data-name", "Jsoup");
        org.junit.Assert.assertEquals(5, a.size());
        a.remove("Tot");
        a.remove("Hello");
        org.junit.Assert.assertEquals(3, a.size());
        org.junit.Assert.assertTrue(a.hasKey("tot"));
        // StatementAdderOnAssert create random local variable
        org.jsoup.nodes.Attributes vc_12105 = new org.jsoup.nodes.Attributes();
        // AssertGenerator replace invocation
        int o_removeCaseSensitive_cf42807__18 = // StatementAdderMethod cloned existing statement
vc_12105.size();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_removeCaseSensitive_cf42807__18, 0);
        org.junit.Assert.assertFalse(a.hasKey("Tot"));
    }

    /* amplification of org.jsoup.nodes.AttributesTest#removeCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void removeCaseSensitive_cf42881_failAssert71() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("tot", "one");
            a.put("Hello", "There");
            a.put("hello", "There");
            a.put("data-name", "Jsoup");
            // MethodAssertGenerator build local variable
            Object o_8_0 = a.size();
            a.remove("Tot");
            a.remove("Hello");
            // MethodAssertGenerator build local variable
            Object o_12_0 = a.size();
            // MethodAssertGenerator build local variable
            Object o_14_0 = a.hasKey("tot");
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Attribute vc_12145 = (org.jsoup.nodes.Attribute)null;
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_12144 = new org.jsoup.nodes.Attributes();
            // StatementAdderMethod cloned existing statement
            vc_12144.put(vc_12145);
            // MethodAssertGenerator build local variable
            Object o_22_0 = a.hasKey("Tot");
            org.junit.Assert.fail("removeCaseSensitive_cf42881 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#removeCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void removeCaseSensitive_cf42807_cf45264_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_20_1 = 0;
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("tot", "one");
            a.put("Hello", "There");
            a.put("hello", "There");
            a.put("data-name", "Jsoup");
            // MethodAssertGenerator build local variable
            Object o_8_0 = a.size();
            a.remove("Tot");
            a.remove("Hello");
            // MethodAssertGenerator build local variable
            Object o_12_0 = a.size();
            // MethodAssertGenerator build local variable
            Object o_14_0 = a.hasKey("tot");
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_12105 = new org.jsoup.nodes.Attributes();
            // AssertGenerator replace invocation
            int o_removeCaseSensitive_cf42807__18 = // StatementAdderMethod cloned existing statement
vc_12105.size();
            // MethodAssertGenerator build local variable
            Object o_20_0 = o_removeCaseSensitive_cf42807__18;
            // StatementAdderOnAssert create null value
            java.lang.String vc_12827 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            vc_12105.getIgnoreCase(vc_12827);
            // MethodAssertGenerator build local variable
            Object o_26_0 = a.hasKey("Tot");
            org.junit.Assert.fail("removeCaseSensitive_cf42807_cf45264 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#removeCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void removeCaseSensitive_cf42796_cf44266_failAssert35() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("tot", "one");
            a.put("Hello", "There");
            a.put("hello", "There");
            a.put("data-name", "Jsoup");
            // MethodAssertGenerator build local variable
            Object o_8_0 = a.size();
            a.remove("Tot");
            a.remove("Hello");
            // MethodAssertGenerator build local variable
            Object o_12_0 = a.size();
            // MethodAssertGenerator build local variable
            Object o_14_0 = a.hasKey("tot");
            // StatementAdderOnAssert create null value
            java.lang.String vc_12100 = (java.lang.String)null;
            // MethodAssertGenerator build local variable
            Object o_18_0 = vc_12100;
            // AssertGenerator replace invocation
            boolean o_removeCaseSensitive_cf42796__18 = // StatementAdderMethod cloned existing statement
a.hasKeyIgnoreCase(vc_12100);
            // MethodAssertGenerator build local variable
            Object o_22_0 = o_removeCaseSensitive_cf42796__18;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Attribute vc_12535 = (org.jsoup.nodes.Attribute)null;
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_12534 = new org.jsoup.nodes.Attributes();
            // StatementAdderMethod cloned existing statement
            vc_12534.put(vc_12535);
            // MethodAssertGenerator build local variable
            Object o_30_0 = a.hasKey("Tot");
            org.junit.Assert.fail("removeCaseSensitive_cf42796_cf44266 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#removeCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void removeCaseSensitive_cf42812_cf45524_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("tot", "one");
            a.put("Hello", "There");
            a.put("hello", "There");
            a.put("data-name", "Jsoup");
            // MethodAssertGenerator build local variable
            Object o_8_0 = a.size();
            a.remove("Tot");
            a.remove("Hello");
            // MethodAssertGenerator build local variable
            Object o_12_0 = a.size();
            // MethodAssertGenerator build local variable
            Object o_14_0 = a.hasKey("tot");
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_1496 = "hello";
            // MethodAssertGenerator build local variable
            Object o_18_0 = String_vc_1496;
            // AssertGenerator replace invocation
            java.lang.String o_removeCaseSensitive_cf42812__18 = // StatementAdderMethod cloned existing statement
a.get(String_vc_1496);
            // MethodAssertGenerator build local variable
            Object o_22_0 = o_removeCaseSensitive_cf42812__18;
            // StatementAdderOnAssert create random local variable
            boolean vc_12918 = false;
            // StatementAdderOnAssert create null value
            java.lang.String vc_12916 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.put(vc_12916, vc_12918);
            // MethodAssertGenerator build local variable
            Object o_30_0 = a.hasKey("Tot");
            org.junit.Assert.fail("removeCaseSensitive_cf42812_cf45524 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#removeCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void removeCaseSensitive_cf42789_cf43962_failAssert15() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("tot", "one");
            a.put("Hello", "There");
            a.put("hello", "There");
            a.put("data-name", "Jsoup");
            // MethodAssertGenerator build local variable
            Object o_8_0 = a.size();
            a.remove("Tot");
            a.remove("Hello");
            // MethodAssertGenerator build local variable
            Object o_12_0 = a.size();
            // MethodAssertGenerator build local variable
            Object o_14_0 = a.hasKey("tot");
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_12097 = new java.lang.String();
            // MethodAssertGenerator build local variable
            Object o_18_0 = vc_12097;
            // AssertGenerator replace invocation
            boolean o_removeCaseSensitive_cf42789__18 = // StatementAdderMethod cloned existing statement
a.hasKey(vc_12097);
            // MethodAssertGenerator build local variable
            Object o_22_0 = o_removeCaseSensitive_cf42789__18;
            // StatementAdderMethod cloned existing statement
            a.get(vc_12097);
            // MethodAssertGenerator build local variable
            Object o_26_0 = a.hasKey("Tot");
            org.junit.Assert.fail("removeCaseSensitive_cf42789_cf43962 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#removeCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void removeCaseSensitive_cf42798_cf44651_failAssert12() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("tot", "one");
            a.put("Hello", "There");
            a.put("hello", "There");
            a.put("data-name", "Jsoup");
            // MethodAssertGenerator build local variable
            Object o_8_0 = a.size();
            a.remove("Tot");
            a.remove("Hello");
            // MethodAssertGenerator build local variable
            Object o_12_0 = a.size();
            // MethodAssertGenerator build local variable
            Object o_14_0 = a.hasKey("tot");
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_12101 = new java.lang.String();
            // MethodAssertGenerator build local variable
            Object o_18_0 = vc_12101;
            // AssertGenerator replace invocation
            boolean o_removeCaseSensitive_cf42798__18 = // StatementAdderMethod cloned existing statement
a.hasKeyIgnoreCase(vc_12101);
            // MethodAssertGenerator build local variable
            Object o_22_0 = o_removeCaseSensitive_cf42798__18;
            // StatementAdderMethod cloned existing statement
            a.getIgnoreCase(vc_12101);
            // MethodAssertGenerator build local variable
            Object o_26_0 = a.hasKey("Tot");
            org.junit.Assert.fail("removeCaseSensitive_cf42798_cf44651 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#removeCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void removeCaseSensitive_cf42877_failAssert69_literalMutation49702_failAssert20() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
                a.put("Tot", "a&p");
                a.put("tot", "one");
                a.put("Hello", "There");
                a.put("hello", "There");
                a.put("data-name", "Jsoup");
                // MethodAssertGenerator build local variable
                Object o_8_0 = a.size();
                a.remove("Tot");
                a.remove("");
                // MethodAssertGenerator build local variable
                Object o_12_0 = a.size();
                // MethodAssertGenerator build local variable
                Object o_14_0 = a.hasKey("tot");
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attribute vc_12145 = (org.jsoup.nodes.Attribute)null;
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attributes vc_12143 = (org.jsoup.nodes.Attributes)null;
                // StatementAdderMethod cloned existing statement
                vc_12143.put(vc_12145);
                // MethodAssertGenerator build local variable
                Object o_22_0 = a.hasKey("Tot");
                org.junit.Assert.fail("removeCaseSensitive_cf42877 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("removeCaseSensitive_cf42877_failAssert69_literalMutation49702 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#removeCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void removeCaseSensitive_cf42832_failAssert58_literalMutation48818_failAssert27() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
                a.put("Tot", "a&p");
                a.put("tot", "one");
                a.put("Hello", "There");
                a.put("hello", "There");
                a.put("", "Jsoup");
                // MethodAssertGenerator build local variable
                Object o_8_0 = a.size();
                a.remove("Tot");
                a.remove("Hello");
                // MethodAssertGenerator build local variable
                Object o_12_0 = a.size();
                // MethodAssertGenerator build local variable
                Object o_14_0 = a.hasKey("tot");
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attributes vc_12118 = (org.jsoup.nodes.Attributes)null;
                // StatementAdderMethod cloned existing statement
                vc_12118.iterator();
                // MethodAssertGenerator build local variable
                Object o_20_0 = a.hasKey("Tot");
                org.junit.Assert.fail("removeCaseSensitive_cf42832 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("removeCaseSensitive_cf42832_failAssert58_literalMutation48818 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#removeCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void removeCaseSensitive_cf42781_cf43358_failAssert38() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("tot", "one");
            a.put("Hello", "There");
            a.put("hello", "There");
            a.put("data-name", "Jsoup");
            // MethodAssertGenerator build local variable
            Object o_8_0 = a.size();
            a.remove("Tot");
            a.remove("Hello");
            // MethodAssertGenerator build local variable
            Object o_12_0 = a.size();
            // MethodAssertGenerator build local variable
            Object o_14_0 = a.hasKey("tot");
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_12093 = new java.lang.Object();
            // AssertGenerator replace invocation
            boolean o_removeCaseSensitive_cf42781__18 = // StatementAdderMethod cloned existing statement
a.equals(vc_12093);
            // MethodAssertGenerator build local variable
            Object o_20_0 = o_removeCaseSensitive_cf42781__18;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Attribute vc_12275 = (org.jsoup.nodes.Attribute)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Attributes vc_12273 = (org.jsoup.nodes.Attributes)null;
            // StatementAdderMethod cloned existing statement
            vc_12273.put(vc_12275);
            // MethodAssertGenerator build local variable
            Object o_28_0 = a.hasKey("Tot");
            org.junit.Assert.fail("removeCaseSensitive_cf42781_cf43358 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#removeCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void removeCaseSensitive_cf42821_cf45793_failAssert32_literalMutation53199_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
                a.put("Tot", "a&p");
                a.put("tot", "one");
                a.put("Hello", "There");
                a.put("hello", "There");
                a.put("data-name", "Jsoup");
                // MethodAssertGenerator build local variable
                Object o_8_0 = a.size();
                a.remove("Tot");
                a.remove("Hello");
                // MethodAssertGenerator build local variable
                Object o_12_0 = a.size();
                // MethodAssertGenerator build local variable
                Object o_14_0 = a.hasKey("tot");
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_1497 = "";
                // MethodAssertGenerator build local variable
                Object o_18_0 = String_vc_1497;
                // AssertGenerator replace invocation
                java.lang.String o_removeCaseSensitive_cf42821__18 = // StatementAdderMethod cloned existing statement
a.getIgnoreCase(String_vc_1497);
                // MethodAssertGenerator build local variable
                Object o_22_0 = o_removeCaseSensitive_cf42821__18;
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_1605 = "Hello";
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attributes vc_12996 = (org.jsoup.nodes.Attributes)null;
                // StatementAdderMethod cloned existing statement
                vc_12996.removeIgnoreCase(String_vc_1605);
                // MethodAssertGenerator build local variable
                Object o_30_0 = a.hasKey("Tot");
                org.junit.Assert.fail("removeCaseSensitive_cf42821_cf45793 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("removeCaseSensitive_cf42821_cf45793_failAssert32_literalMutation53199 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#removeCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void removeCaseSensitive_cf42806_literalMutation44983_failAssert0_literalMutation51215() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_18_1 = 3;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_18_1, 3);
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("tot", "one");
            a.put("Hello", "There");
            a.put("", "There");
            a.put("KJ!&OhavG", "Jsoup");
            // MethodAssertGenerator build local variable
            Object o_8_0 = a.size();
            a.remove("Tot");
            a.remove("Hello");
            // MethodAssertGenerator build local variable
            Object o_12_0 = a.size();
            // MethodAssertGenerator build local variable
            Object o_14_0 = a.hasKey("tot");
            // AssertGenerator replace invocation
            int o_removeCaseSensitive_cf42806__16 = // StatementAdderMethod cloned existing statement
a.size();
            // MethodAssertGenerator build local variable
            Object o_18_0 = o_removeCaseSensitive_cf42806__16;
            // MethodAssertGenerator build local variable
            Object o_20_0 = a.hasKey("Tot");
            org.junit.Assert.fail("removeCaseSensitive_cf42806_literalMutation44983 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53510_failAssert48() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.lang.String[][] datas = new java.lang.String[][]{ new java.lang.String[]{ "Tot" , "raul" } , new java.lang.String[]{ "Hello" , "pismuth" } , new java.lang.String[]{ "data-name" , "Jsoup" } };
            for (java.lang.String[] atts : datas) {
                a.put(atts[0], atts[1]);
            }
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // MethodAssertGenerator build local variable
            Object o_10_0 = iterator.hasNext();
            int i = 0;
            for (org.jsoup.nodes.Attribute attribute : a) {
                // MethodAssertGenerator build local variable
                Object o_18_1 = attribute.getValue();
                // MethodAssertGenerator build local variable
                Object o_16_1 = attribute.getKey();
                // MethodAssertGenerator build local variable
                Object o_16_0 = datas[i][0];
                // MethodAssertGenerator build local variable
                Object o_18_0 = datas[i][1];
                i++;
            }
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Attribute vc_13250 = (org.jsoup.nodes.Attribute)null;
            // StatementAdderMethod cloned existing statement
            a.put(vc_13250);
            org.junit.Assert.fail("testIterator_cf53510 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53427() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        java.lang.String[][] datas = new java.lang.String[][]{ new java.lang.String[]{ "Tot" , "raul" } , new java.lang.String[]{ "Hello" , "pismuth" } , new java.lang.String[]{ "data-name" , "Jsoup" } };
        for (java.lang.String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        org.junit.Assert.assertTrue(iterator.hasNext());
        int i = 0;
        for (org.jsoup.nodes.Attribute attribute : a) {
            org.junit.Assert.assertEquals(datas[i][0], attribute.getKey());
            org.junit.Assert.assertEquals(datas[i][1], attribute.getValue());
            i++;
        }
        // StatementAdderOnAssert create null value
        java.lang.String vc_13205 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_13205);
        // AssertGenerator replace invocation
        boolean o_testIterator_cf53427__23 = // StatementAdderMethod cloned existing statement
a.hasKeyIgnoreCase(vc_13205);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testIterator_cf53427__23);
        org.junit.Assert.assertEquals(datas.length, i);
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53442_failAssert23() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.lang.String[][] datas = new java.lang.String[][]{ new java.lang.String[]{ "Tot" , "raul" } , new java.lang.String[]{ "Hello" , "pismuth" } , new java.lang.String[]{ "data-name" , "Jsoup" } };
            for (java.lang.String[] atts : datas) {
                a.put(atts[0], atts[1]);
            }
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // MethodAssertGenerator build local variable
            Object o_10_0 = iterator.hasNext();
            int i = 0;
            for (org.jsoup.nodes.Attribute attribute : a) {
                // MethodAssertGenerator build local variable
                Object o_18_1 = attribute.getValue();
                // MethodAssertGenerator build local variable
                Object o_16_1 = attribute.getKey();
                // MethodAssertGenerator build local variable
                Object o_16_0 = datas[i][0];
                // MethodAssertGenerator build local variable
                Object o_18_0 = datas[i][1];
                i++;
            }
            // StatementAdderOnAssert create null value
            java.lang.String vc_13213 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.get(vc_13213);
            org.junit.Assert.fail("testIterator_cf53442 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53517_failAssert53() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.lang.String[][] datas = new java.lang.String[][]{ new java.lang.String[]{ "Tot" , "raul" } , new java.lang.String[]{ "Hello" , "pismuth" } , new java.lang.String[]{ "data-name" , "Jsoup" } };
            for (java.lang.String[] atts : datas) {
                a.put(atts[0], atts[1]);
            }
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // MethodAssertGenerator build local variable
            Object o_10_0 = iterator.hasNext();
            int i = 0;
            for (org.jsoup.nodes.Attribute attribute : a) {
                // MethodAssertGenerator build local variable
                Object o_18_1 = attribute.getValue();
                // MethodAssertGenerator build local variable
                Object o_16_1 = attribute.getKey();
                // MethodAssertGenerator build local variable
                Object o_16_0 = datas[i][0];
                // MethodAssertGenerator build local variable
                Object o_18_0 = datas[i][1];
                i++;
            }
            // StatementAdderOnAssert create null value
            java.lang.String vc_13254 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.remove(vc_13254);
            org.junit.Assert.fail("testIterator_cf53517 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53493_failAssert43() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.lang.String[][] datas = new java.lang.String[][]{ new java.lang.String[]{ "Tot" , "raul" } , new java.lang.String[]{ "Hello" , "pismuth" } , new java.lang.String[]{ "data-name" , "Jsoup" } };
            for (java.lang.String[] atts : datas) {
                a.put(atts[0], atts[1]);
            }
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // MethodAssertGenerator build local variable
            Object o_10_0 = iterator.hasNext();
            int i = 0;
            for (org.jsoup.nodes.Attribute attribute : a) {
                // MethodAssertGenerator build local variable
                Object o_18_1 = attribute.getValue();
                // MethodAssertGenerator build local variable
                Object o_16_1 = attribute.getKey();
                // MethodAssertGenerator build local variable
                Object o_16_0 = datas[i][0];
                // MethodAssertGenerator build local variable
                Object o_18_0 = datas[i][1];
                i++;
            }
            // StatementAdderOnAssert create random local variable
            boolean vc_13243 = true;
            // StatementAdderOnAssert create null value
            java.lang.String vc_13241 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.put(vc_13241, vc_13243);
            org.junit.Assert.fail("testIterator_cf53493 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53526_failAssert60() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.lang.String[][] datas = new java.lang.String[][]{ new java.lang.String[]{ "Tot" , "raul" } , new java.lang.String[]{ "Hello" , "pismuth" } , new java.lang.String[]{ "data-name" , "Jsoup" } };
            for (java.lang.String[] atts : datas) {
                a.put(atts[0], atts[1]);
            }
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // MethodAssertGenerator build local variable
            Object o_10_0 = iterator.hasNext();
            int i = 0;
            for (org.jsoup.nodes.Attribute attribute : a) {
                // MethodAssertGenerator build local variable
                Object o_18_1 = attribute.getValue();
                // MethodAssertGenerator build local variable
                Object o_16_1 = attribute.getKey();
                // MethodAssertGenerator build local variable
                Object o_16_0 = datas[i][0];
                // MethodAssertGenerator build local variable
                Object o_18_0 = datas[i][1];
                i++;
            }
            // StatementAdderOnAssert create null value
            java.lang.String vc_13258 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.removeIgnoreCase(vc_13258);
            org.junit.Assert.fail("testIterator_cf53526 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53453_failAssert31() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.lang.String[][] datas = new java.lang.String[][]{ new java.lang.String[]{ "Tot" , "raul" } , new java.lang.String[]{ "Hello" , "pismuth" } , new java.lang.String[]{ "data-name" , "Jsoup" } };
            for (java.lang.String[] atts : datas) {
                a.put(atts[0], atts[1]);
            }
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // MethodAssertGenerator build local variable
            Object o_10_0 = iterator.hasNext();
            int i = 0;
            for (org.jsoup.nodes.Attribute attribute : a) {
                // MethodAssertGenerator build local variable
                Object o_18_1 = attribute.getValue();
                // MethodAssertGenerator build local variable
                Object o_16_1 = attribute.getKey();
                // MethodAssertGenerator build local variable
                Object o_16_0 = datas[i][0];
                // MethodAssertGenerator build local variable
                Object o_18_0 = datas[i][1];
                i++;
            }
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_13218 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            a.getIgnoreCase(vc_13218);
            org.junit.Assert.fail("testIterator_cf53453 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53452() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        java.lang.String[][] datas = new java.lang.String[][]{ new java.lang.String[]{ "Tot" , "raul" } , new java.lang.String[]{ "Hello" , "pismuth" } , new java.lang.String[]{ "data-name" , "Jsoup" } };
        for (java.lang.String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        org.junit.Assert.assertTrue(iterator.hasNext());
        int i = 0;
        for (org.jsoup.nodes.Attribute attribute : a) {
            org.junit.Assert.assertEquals(datas[i][0], attribute.getKey());
            org.junit.Assert.assertEquals(datas[i][1], attribute.getValue());
            i++;
        }
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_1633 = "data-name";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_1633, "data-name");
        // AssertGenerator replace invocation
        java.lang.String o_testIterator_cf53452__23 = // StatementAdderMethod cloned existing statement
a.getIgnoreCase(String_vc_1633);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testIterator_cf53452__23, "Jsoup");
        org.junit.Assert.assertEquals(datas.length, i);
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53411() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        java.lang.String[][] datas = new java.lang.String[][]{ new java.lang.String[]{ "Tot" , "raul" } , new java.lang.String[]{ "Hello" , "pismuth" } , new java.lang.String[]{ "data-name" , "Jsoup" } };
        for (java.lang.String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        org.junit.Assert.assertTrue(iterator.hasNext());
        int i = 0;
        for (org.jsoup.nodes.Attribute attribute : a) {
            org.junit.Assert.assertEquals(datas[i][0], attribute.getKey());
            org.junit.Assert.assertEquals(datas[i][1], attribute.getValue());
            i++;
        }
        // StatementAdderOnAssert create null value
        java.lang.Object vc_13197 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_13197);
        // AssertGenerator replace invocation
        boolean o_testIterator_cf53411__23 = // StatementAdderMethod cloned existing statement
a.equals(vc_13197);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testIterator_cf53411__23);
        org.junit.Assert.assertEquals(datas.length, i);
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53434() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        java.lang.String[][] datas = new java.lang.String[][]{ new java.lang.String[]{ "Tot" , "raul" } , new java.lang.String[]{ "Hello" , "pismuth" } , new java.lang.String[]{ "data-name" , "Jsoup" } };
        for (java.lang.String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        org.junit.Assert.assertTrue(iterator.hasNext());
        int i = 0;
        for (org.jsoup.nodes.Attribute attribute : a) {
            org.junit.Assert.assertEquals(datas[i][0], attribute.getKey());
            org.junit.Assert.assertEquals(datas[i][1], attribute.getValue());
            i++;
        }
        // AssertGenerator replace invocation
        int o_testIterator_cf53434__21 = // StatementAdderMethod cloned existing statement
a.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testIterator_cf53434__21, 1143632839);
        org.junit.Assert.assertEquals(datas.length, i);
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test
    public void testIterator_literalMutation53371_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.lang.String[][] datas = new java.lang.String[][]{ new java.lang.String[]{ "" , "raul" } , new java.lang.String[]{ "Hello" , "pismuth" } , new java.lang.String[]{ "data-name" , "Jsoup" } };
            for (java.lang.String[] atts : datas) {
                a.put(atts[0], atts[1]);
            }
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // MethodAssertGenerator build local variable
            Object o_10_0 = iterator.hasNext();
            int i = 0;
            for (org.jsoup.nodes.Attribute attribute : a) {
                // MethodAssertGenerator build local variable
                Object o_18_1 = attribute.getValue();
                // MethodAssertGenerator build local variable
                Object o_16_1 = attribute.getKey();
                // MethodAssertGenerator build local variable
                Object o_16_0 = datas[i][0];
                // MethodAssertGenerator build local variable
                Object o_18_0 = datas[i][1];
                i++;
            }
            org.junit.Assert.fail("testIterator_literalMutation53371 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53428_cf54888_failAssert36() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.lang.String[][] datas = new java.lang.String[][]{ new java.lang.String[]{ "Tot" , "raul" } , new java.lang.String[]{ "Hello" , "pismuth" } , new java.lang.String[]{ "data-name" , "Jsoup" } };
            for (java.lang.String[] atts : datas) {
                a.put(atts[0], atts[1]);
            }
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // MethodAssertGenerator build local variable
            Object o_10_0 = iterator.hasNext();
            int i = 0;
            for (org.jsoup.nodes.Attribute attribute : a) {
                // MethodAssertGenerator build local variable
                Object o_18_1 = attribute.getValue();
                // MethodAssertGenerator build local variable
                Object o_16_1 = attribute.getKey();
                // MethodAssertGenerator build local variable
                Object o_16_0 = datas[i][0];
                // MethodAssertGenerator build local variable
                Object o_18_0 = datas[i][1];
                i++;
            }
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_1631 = "data-name";
            // MethodAssertGenerator build local variable
            Object o_23_0 = String_vc_1631;
            // AssertGenerator replace invocation
            boolean o_testIterator_cf53428__23 = // StatementAdderMethod cloned existing statement
a.hasKeyIgnoreCase(String_vc_1631);
            // MethodAssertGenerator build local variable
            Object o_27_0 = o_testIterator_cf53428__23;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_13714 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            a.removeIgnoreCase(vc_13714);
            org.junit.Assert.fail("testIterator_cf53428_cf54888 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53420_cf54422_failAssert41() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.lang.String[][] datas = new java.lang.String[][]{ new java.lang.String[]{ "Tot" , "raul" } , new java.lang.String[]{ "Hello" , "pismuth" } , new java.lang.String[]{ "data-name" , "Jsoup" } };
            for (java.lang.String[] atts : datas) {
                a.put(atts[0], atts[1]);
            }
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // MethodAssertGenerator build local variable
            Object o_10_0 = iterator.hasNext();
            int i = 0;
            for (org.jsoup.nodes.Attribute attribute : a) {
                // MethodAssertGenerator build local variable
                Object o_18_1 = attribute.getValue();
                // MethodAssertGenerator build local variable
                Object o_16_1 = attribute.getKey();
                // MethodAssertGenerator build local variable
                Object o_16_0 = datas[i][0];
                // MethodAssertGenerator build local variable
                Object o_18_0 = datas[i][1];
                i++;
            }
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_13202 = new java.lang.String();
            // MethodAssertGenerator build local variable
            Object o_23_0 = vc_13202;
            // AssertGenerator replace invocation
            boolean o_testIterator_cf53420__23 = // StatementAdderMethod cloned existing statement
a.hasKey(vc_13202);
            // MethodAssertGenerator build local variable
            Object o_27_0 = o_testIterator_cf53420__23;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_13543 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            a.getIgnoreCase(vc_13543);
            org.junit.Assert.fail("testIterator_cf53420_cf54422 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53429_cf55063_failAssert47() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.lang.String[][] datas = new java.lang.String[][]{ new java.lang.String[]{ "Tot" , "raul" } , new java.lang.String[]{ "Hello" , "pismuth" } , new java.lang.String[]{ "data-name" , "Jsoup" } };
            for (java.lang.String[] atts : datas) {
                a.put(atts[0], atts[1]);
            }
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // MethodAssertGenerator build local variable
            Object o_10_0 = iterator.hasNext();
            int i = 0;
            for (org.jsoup.nodes.Attribute attribute : a) {
                // MethodAssertGenerator build local variable
                Object o_18_1 = attribute.getValue();
                // MethodAssertGenerator build local variable
                Object o_16_1 = attribute.getKey();
                // MethodAssertGenerator build local variable
                Object o_16_0 = datas[i][0];
                // MethodAssertGenerator build local variable
                Object o_18_0 = datas[i][1];
                i++;
            }
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_13206 = new java.lang.String();
            // MethodAssertGenerator build local variable
            Object o_23_0 = vc_13206;
            // AssertGenerator replace invocation
            boolean o_testIterator_cf53429__23 = // StatementAdderMethod cloned existing statement
a.hasKeyIgnoreCase(vc_13206);
            // MethodAssertGenerator build local variable
            Object o_27_0 = o_testIterator_cf53429__23;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_13775 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            a.remove(vc_13775);
            org.junit.Assert.fail("testIterator_cf53429_cf55063 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53525_failAssert59_literalMutation58796_failAssert29() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
                java.lang.String[][] datas = new java.lang.String[][]{ new java.lang.String[]{ "Tot" , "raul" } , new java.lang.String[]{ "Hello" , "pismuth" } , new java.lang.String[]{ "" , "Jsoup" } };
                for (java.lang.String[] atts : datas) {
                    a.put(atts[0], atts[1]);
                }
                java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
                // MethodAssertGenerator build local variable
                Object o_10_0 = iterator.hasNext();
                int i = 0;
                for (org.jsoup.nodes.Attribute attribute : a) {
                    // MethodAssertGenerator build local variable
                    Object o_18_1 = attribute.getValue();
                    // MethodAssertGenerator build local variable
                    Object o_16_1 = attribute.getKey();
                    // MethodAssertGenerator build local variable
                    Object o_16_0 = datas[i][0];
                    // MethodAssertGenerator build local variable
                    Object o_18_0 = datas[i][1];
                    i++;
                }
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_13259 = new java.lang.String();
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attributes vc_13256 = (org.jsoup.nodes.Attributes)null;
                // StatementAdderMethod cloned existing statement
                vc_13256.removeIgnoreCase(vc_13259);
                org.junit.Assert.fail("testIterator_cf53525 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testIterator_cf53525_failAssert59_literalMutation58796 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53434_cf55206_failAssert20() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_23_1 = 1143632839;
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.lang.String[][] datas = new java.lang.String[][]{ new java.lang.String[]{ "Tot" , "raul" } , new java.lang.String[]{ "Hello" , "pismuth" } , new java.lang.String[]{ "data-name" , "Jsoup" } };
            for (java.lang.String[] atts : datas) {
                a.put(atts[0], atts[1]);
            }
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // MethodAssertGenerator build local variable
            Object o_10_0 = iterator.hasNext();
            int i = 0;
            for (org.jsoup.nodes.Attribute attribute : a) {
                // MethodAssertGenerator build local variable
                Object o_18_1 = attribute.getValue();
                // MethodAssertGenerator build local variable
                Object o_16_1 = attribute.getKey();
                // MethodAssertGenerator build local variable
                Object o_16_0 = datas[i][0];
                // MethodAssertGenerator build local variable
                Object o_18_0 = datas[i][1];
                i++;
            }
            // AssertGenerator replace invocation
            int o_testIterator_cf53434__21 = // StatementAdderMethod cloned existing statement
a.hashCode();
            // MethodAssertGenerator build local variable
            Object o_23_0 = o_testIterator_cf53434__21;
            // StatementAdderOnAssert create random local variable
            boolean vc_13828 = false;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_13827 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            a.put(vc_13827, vc_13828);
            org.junit.Assert.fail("testIterator_cf53434_cf55206 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53412_cf53834_failAssert12() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.lang.String[][] datas = new java.lang.String[][]{ new java.lang.String[]{ "Tot" , "raul" } , new java.lang.String[]{ "Hello" , "pismuth" } , new java.lang.String[]{ "data-name" , "Jsoup" } };
            for (java.lang.String[] atts : datas) {
                a.put(atts[0], atts[1]);
            }
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // MethodAssertGenerator build local variable
            Object o_10_0 = iterator.hasNext();
            int i = 0;
            for (org.jsoup.nodes.Attribute attribute : a) {
                // MethodAssertGenerator build local variable
                Object o_18_1 = attribute.getValue();
                // MethodAssertGenerator build local variable
                Object o_16_1 = attribute.getKey();
                // MethodAssertGenerator build local variable
                Object o_16_0 = datas[i][0];
                // MethodAssertGenerator build local variable
                Object o_18_0 = datas[i][1];
                i++;
            }
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_13198 = new java.lang.Object();
            // AssertGenerator replace invocation
            boolean o_testIterator_cf53412__23 = // StatementAdderMethod cloned existing statement
a.equals(vc_13198);
            // MethodAssertGenerator build local variable
            Object o_25_0 = o_testIterator_cf53412__23;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_13348 = new java.lang.String();
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Attributes vc_13345 = (org.jsoup.nodes.Attributes)null;
            // StatementAdderMethod cloned existing statement
            vc_13345.getIgnoreCase(vc_13348);
            org.junit.Assert.fail("testIterator_cf53412_cf53834 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53419_cf54227_failAssert22() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.lang.String[][] datas = new java.lang.String[][]{ new java.lang.String[]{ "Tot" , "raul" } , new java.lang.String[]{ "Hello" , "pismuth" } , new java.lang.String[]{ "data-name" , "Jsoup" } };
            for (java.lang.String[] atts : datas) {
                a.put(atts[0], atts[1]);
            }
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // MethodAssertGenerator build local variable
            Object o_10_0 = iterator.hasNext();
            int i = 0;
            for (org.jsoup.nodes.Attribute attribute : a) {
                // MethodAssertGenerator build local variable
                Object o_18_1 = attribute.getValue();
                // MethodAssertGenerator build local variable
                Object o_16_1 = attribute.getKey();
                // MethodAssertGenerator build local variable
                Object o_16_0 = datas[i][0];
                // MethodAssertGenerator build local variable
                Object o_18_0 = datas[i][1];
                i++;
            }
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_1630 = "pismuth";
            // MethodAssertGenerator build local variable
            Object o_23_0 = String_vc_1630;
            // AssertGenerator replace invocation
            boolean o_testIterator_cf53419__23 = // StatementAdderMethod cloned existing statement
a.hasKey(String_vc_1630);
            // MethodAssertGenerator build local variable
            Object o_27_0 = o_testIterator_cf53419__23;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_13474 = new java.lang.String();
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_13472 = new org.jsoup.nodes.Attributes();
            // StatementAdderMethod cloned existing statement
            vc_13472.get(vc_13474);
            org.junit.Assert.fail("testIterator_cf53419_cf54227 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53443_cf55523_failAssert0_literalMutation59021_failAssert12() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
                java.lang.String[][] datas = new java.lang.String[][]{ new java.lang.String[]{ "Tot" , "raul" } , new java.lang.String[]{ "" , "pismuth" } , new java.lang.String[]{ "data-name" , "Jsoup" } };
                for (java.lang.String[] atts : datas) {
                    a.put(atts[0], atts[1]);
                }
                java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
                // MethodAssertGenerator build local variable
                Object o_10_0 = iterator.hasNext();
                int i = 0;
                for (org.jsoup.nodes.Attribute attribute : a) {
                    // MethodAssertGenerator build local variable
                    Object o_18_1 = attribute.getValue();
                    // MethodAssertGenerator build local variable
                    Object o_16_1 = attribute.getKey();
                    // MethodAssertGenerator build local variable
                    Object o_16_0 = datas[i][0];
                    // MethodAssertGenerator build local variable
                    Object o_18_0 = datas[i][1];
                    i++;
                }
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_1632 = "Jsoup";
                // MethodAssertGenerator build local variable
                Object o_23_0 = String_vc_1632;
                // AssertGenerator replace invocation
                java.lang.String o_testIterator_cf53443__23 = // StatementAdderMethod cloned existing statement
a.get(String_vc_1632);
                // MethodAssertGenerator build local variable
                Object o_27_0 = o_testIterator_cf53443__23;
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attributes vc_13942 = (org.jsoup.nodes.Attributes)null;
                // StatementAdderMethod cloned existing statement
                vc_13942.dataset();
                org.junit.Assert.fail("testIterator_cf53443_cf55523 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testIterator_cf53443_cf55523_failAssert0_literalMutation59021 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53434_cf55206_failAssert20_literalMutation59892() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_23_1 = 1143632839;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_23_1, 1143632839);
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.lang.String[][] datas = new java.lang.String[][]{ new java.lang.String[]{ "Tot" , "raul" } , new java.lang.String[]{ "" , "pismuth" } , new java.lang.String[]{ "data-name" , "Jsoup" } };
            for (java.lang.String[] atts : datas) {
                a.put(atts[0], atts[1]);
            }
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // MethodAssertGenerator build local variable
            Object o_10_0 = iterator.hasNext();
            int i = 0;
            for (org.jsoup.nodes.Attribute attribute : a) {
                // MethodAssertGenerator build local variable
                Object o_18_1 = attribute.getValue();
                // MethodAssertGenerator build local variable
                Object o_16_1 = attribute.getKey();
                // MethodAssertGenerator build local variable
                Object o_16_0 = datas[i][0];
                // MethodAssertGenerator build local variable
                Object o_18_0 = datas[i][1];
                i++;
            }
            // AssertGenerator replace invocation
            int o_testIterator_cf53434__21 = // StatementAdderMethod cloned existing statement
a.hashCode();
            // MethodAssertGenerator build local variable
            Object o_23_0 = o_testIterator_cf53434__21;
            // StatementAdderOnAssert create random local variable
            boolean vc_13828 = false;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_13827 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            a.put(vc_13827, vc_13828);
            org.junit.Assert.fail("testIterator_cf53434_cf55206 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61425() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        // AssertGenerator replace invocation
        java.lang.String o_testIteratorEmpty_cf61425__5 = // StatementAdderMethod cloned existing statement
a.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testIteratorEmpty_cf61425__5, "");
        org.junit.Assert.assertFalse(iterator.hasNext());
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61404() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        // AssertGenerator replace invocation
        int o_testIteratorEmpty_cf61404__5 = // StatementAdderMethod cloned existing statement
a.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testIteratorEmpty_cf61404__5, 0);
        org.junit.Assert.assertFalse(iterator.hasNext());
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61480_failAssert43() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // StatementAdderOnAssert create null value
            java.lang.String vc_14233 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.removeIgnoreCase(vc_14233);
            // MethodAssertGenerator build local variable
            Object o_9_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61480 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61456_failAssert28() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // StatementAdderOnAssert create random local variable
            boolean vc_14218 = false;
            // StatementAdderOnAssert create null value
            java.lang.String vc_14216 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.put(vc_14216, vc_14218);
            // MethodAssertGenerator build local variable
            Object o_11_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61456 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61468_failAssert33() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Attribute vc_14225 = (org.jsoup.nodes.Attribute)null;
            // StatementAdderMethod cloned existing statement
            a.put(vc_14225);
            // MethodAssertGenerator build local variable
            Object o_9_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61468 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61429() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        // StatementAdderOnAssert create random local variable
        org.jsoup.nodes.Attributes vc_14199 = new org.jsoup.nodes.Attributes();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_14199.equals(a));
        // AssertGenerator replace invocation
        java.util.Iterator<org.jsoup.nodes.Attribute> o_testIteratorEmpty_cf61429__7 = // StatementAdderMethod cloned existing statement
vc_14199.iterator();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testIteratorEmpty_cf61429__7.equals(iterator));
        org.junit.Assert.assertFalse(iterator.hasNext());
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61407() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        // AssertGenerator replace invocation
        int o_testIteratorEmpty_cf61407__5 = // StatementAdderMethod cloned existing statement
a.size();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testIteratorEmpty_cf61407__5, 0);
        org.junit.Assert.assertFalse(iterator.hasNext());
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61417_failAssert16() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // StatementAdderOnAssert create null value
            java.lang.String vc_14192 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.getIgnoreCase(vc_14192);
            // MethodAssertGenerator build local variable
            Object o_9_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61417 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61388() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_14173 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_testIteratorEmpty_cf61388__7 = // StatementAdderMethod cloned existing statement
a.equals(vc_14173);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testIteratorEmpty_cf61388__7);
        org.junit.Assert.assertFalse(iterator.hasNext());
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61399() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        // StatementAdderOnAssert create null value
        java.lang.String vc_14180 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_14180);
        // AssertGenerator replace invocation
        boolean o_testIteratorEmpty_cf61399__7 = // StatementAdderMethod cloned existing statement
a.hasKeyIgnoreCase(vc_14180);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testIteratorEmpty_cf61399__7);
        org.junit.Assert.assertFalse(iterator.hasNext());
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61412_failAssert11() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_14189 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            a.get(vc_14189);
            // MethodAssertGenerator build local variable
            Object o_9_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61412 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61429_cf62784_failAssert35() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_14199 = new org.jsoup.nodes.Attributes();
            // MethodAssertGenerator build local variable
            Object o_7_0 = vc_14199.equals(a);
            // AssertGenerator replace invocation
            java.util.Iterator<org.jsoup.nodes.Attribute> o_testIteratorEmpty_cf61429__7 = // StatementAdderMethod cloned existing statement
vc_14199.iterator();
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testIteratorEmpty_cf61429__7.equals(iterator);
            // StatementAdderOnAssert create null value
            java.lang.String vc_14903 = (java.lang.String)null;
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_14902 = new org.jsoup.nodes.Attributes();
            // StatementAdderMethod cloned existing statement
            vc_14902.get(vc_14903);
            // MethodAssertGenerator build local variable
            Object o_19_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61429_cf62784 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61399_cf62131_failAssert13() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // StatementAdderOnAssert create null value
            java.lang.String vc_14180 = (java.lang.String)null;
            // MethodAssertGenerator build local variable
            Object o_7_0 = vc_14180;
            // AssertGenerator replace invocation
            boolean o_testIteratorEmpty_cf61399__7 = // StatementAdderMethod cloned existing statement
a.hasKeyIgnoreCase(vc_14180);
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testIteratorEmpty_cf61399__7;
            // StatementAdderOnAssert create random local variable
            boolean vc_14543 = false;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_14542 = new java.lang.String();
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_14540 = new org.jsoup.nodes.Attributes();
            // StatementAdderMethod cloned existing statement
            vc_14540.put(vc_14542, vc_14543);
            // MethodAssertGenerator build local variable
            Object o_21_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61399_cf62131 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61388_cf61730_failAssert23() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_14173 = new java.lang.Object();
            // AssertGenerator replace invocation
            boolean o_testIteratorEmpty_cf61388__7 = // StatementAdderMethod cloned existing statement
a.equals(vc_14173);
            // MethodAssertGenerator build local variable
            Object o_9_0 = o_testIteratorEmpty_cf61388__7;
            // StatementAdderOnAssert create random local variable
            boolean vc_14348 = true;
            // StatementAdderOnAssert create null value
            java.lang.String vc_14346 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.put(vc_14346, vc_14348);
            // MethodAssertGenerator build local variable
            Object o_17_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61388_cf61730 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61393_cf61861_failAssert12() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // StatementAdderOnAssert create null value
            java.lang.String vc_14176 = (java.lang.String)null;
            // MethodAssertGenerator build local variable
            Object o_7_0 = vc_14176;
            // AssertGenerator replace invocation
            boolean o_testIteratorEmpty_cf61393__7 = // StatementAdderMethod cloned existing statement
a.hasKey(vc_14176);
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testIteratorEmpty_cf61393__7;
            // StatementAdderOnAssert create random local variable
            boolean vc_14413 = true;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_14412 = new java.lang.String();
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_14410 = new org.jsoup.nodes.Attributes();
            // StatementAdderMethod cloned existing statement
            vc_14410.put(vc_14412, vc_14413);
            // MethodAssertGenerator build local variable
            Object o_21_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61393_cf61861 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61399_cf62085_failAssert27() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // StatementAdderOnAssert create null value
            java.lang.String vc_14180 = (java.lang.String)null;
            // MethodAssertGenerator build local variable
            Object o_7_0 = vc_14180;
            // AssertGenerator replace invocation
            boolean o_testIteratorEmpty_cf61399__7 = // StatementAdderMethod cloned existing statement
a.hasKeyIgnoreCase(vc_14180);
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testIteratorEmpty_cf61399__7;
            // StatementAdderMethod cloned existing statement
            a.getIgnoreCase(vc_14180);
            // MethodAssertGenerator build local variable
            Object o_15_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61399_cf62085 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61476_failAssert39_add62900() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // StatementAdderOnAssert create null value
            java.lang.String vc_14229 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_14229);
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_14228 = new org.jsoup.nodes.Attributes();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(vc_14228.equals(a));
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_14228.remove(vc_14229);
            // StatementAdderMethod cloned existing statement
            vc_14228.remove(vc_14229);
            // MethodAssertGenerator build local variable
            Object o_11_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61476 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61400_cf62293_failAssert74() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_14181 = new java.lang.String();
            // MethodAssertGenerator build local variable
            Object o_7_0 = vc_14181;
            // AssertGenerator replace invocation
            boolean o_testIteratorEmpty_cf61400__7 = // StatementAdderMethod cloned existing statement
a.hasKeyIgnoreCase(vc_14181);
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testIteratorEmpty_cf61400__7;
            // StatementAdderMethod cloned existing statement
            a.remove(vc_14181);
            // MethodAssertGenerator build local variable
            Object o_15_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61400_cf62293 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61399_cf62145_failAssert78() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // StatementAdderOnAssert create null value
            java.lang.String vc_14180 = (java.lang.String)null;
            // MethodAssertGenerator build local variable
            Object o_7_0 = vc_14180;
            // AssertGenerator replace invocation
            boolean o_testIteratorEmpty_cf61399__7 = // StatementAdderMethod cloned existing statement
a.hasKeyIgnoreCase(vc_14180);
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testIteratorEmpty_cf61399__7;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Attribute vc_14550 = (org.jsoup.nodes.Attribute)null;
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_14549 = new org.jsoup.nodes.Attributes();
            // StatementAdderMethod cloned existing statement
            vc_14549.put(vc_14550);
            // MethodAssertGenerator build local variable
            Object o_19_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61399_cf62145 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61394_cf62034_failAssert52() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_14177 = new java.lang.String();
            // MethodAssertGenerator build local variable
            Object o_7_0 = vc_14177;
            // AssertGenerator replace invocation
            boolean o_testIteratorEmpty_cf61394__7 = // StatementAdderMethod cloned existing statement
a.hasKey(vc_14177);
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testIteratorEmpty_cf61394__7;
            // StatementAdderOnAssert create null value
            java.lang.String vc_14493 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.removeIgnoreCase(vc_14493);
            // MethodAssertGenerator build local variable
            Object o_17_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61394_cf62034 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61410_failAssert9_add62864_cf63649_failAssert38() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
                java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_14189 = new java.lang.String();
                // MethodAssertGenerator build local variable
                Object o_9_0 = vc_14189;
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attributes vc_14186 = (org.jsoup.nodes.Attributes)null;
                // StatementAdderOnAssert create null value
                java.lang.String vc_15293 = (java.lang.String)null;
                // StatementAdderOnAssert create random local variable
                org.jsoup.nodes.Attributes vc_15292 = new org.jsoup.nodes.Attributes();
                // StatementAdderMethod cloned existing statement
                vc_15292.get(vc_15293);
                // MethodAssertGenerator build local variable
                Object o_19_0 = vc_14186;
                // StatementAdderMethod cloned existing statement
                // MethodCallAdder
                vc_14186.get(vc_14189);
                // StatementAdderMethod cloned existing statement
                vc_14186.get(vc_14189);
                // MethodAssertGenerator build local variable
                Object o_11_0 = iterator.hasNext();
                org.junit.Assert.fail("testIteratorEmpty_cf61410 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testIteratorEmpty_cf61410_failAssert9_add62864_cf63649 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61398_failAssert5_add62860_cf63188_failAssert11() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
                java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_14181 = new java.lang.String();
                // MethodAssertGenerator build local variable
                Object o_9_0 = vc_14181;
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attributes vc_14178 = (org.jsoup.nodes.Attributes)null;
                // StatementAdderOnAssert create random local variable
                boolean vc_15063 = true;
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_15062 = new java.lang.String();
                // StatementAdderOnAssert create random local variable
                org.jsoup.nodes.Attributes vc_15060 = new org.jsoup.nodes.Attributes();
                // StatementAdderMethod cloned existing statement
                vc_15060.put(vc_15062, vc_15063);
                // MethodAssertGenerator build local variable
                Object o_21_0 = vc_14178;
                // StatementAdderMethod cloned existing statement
                // MethodCallAdder
                vc_14178.hasKeyIgnoreCase(vc_14181);
                // StatementAdderMethod cloned existing statement
                vc_14178.hasKeyIgnoreCase(vc_14181);
                // MethodAssertGenerator build local variable
                Object o_11_0 = iterator.hasNext();
                org.junit.Assert.fail("testIteratorEmpty_cf61398 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testIteratorEmpty_cf61398_failAssert5_add62860_cf63188 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61386_failAssert1_add62856_cf63457_failAssert40() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
                java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
                // StatementAdderOnAssert create random local variable
                java.lang.Object vc_14173 = new java.lang.Object();
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attributes vc_14170 = (org.jsoup.nodes.Attributes)null;
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attribute vc_15200 = (org.jsoup.nodes.Attribute)null;
                // StatementAdderOnAssert create random local variable
                org.jsoup.nodes.Attributes vc_15199 = new org.jsoup.nodes.Attributes();
                // StatementAdderMethod cloned existing statement
                vc_15199.put(vc_15200);
                // MethodAssertGenerator build local variable
                Object o_17_0 = vc_14170;
                // StatementAdderMethod cloned existing statement
                // MethodCallAdder
                vc_14170.equals(vc_14173);
                // StatementAdderMethod cloned existing statement
                vc_14170.equals(vc_14173);
                // MethodAssertGenerator build local variable
                Object o_11_0 = iterator.hasNext();
                org.junit.Assert.fail("testIteratorEmpty_cf61386 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testIteratorEmpty_cf61386_failAssert1_add62856_cf63457 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61398_failAssert5_add62860_cf63215_failAssert42() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
                java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_14181 = new java.lang.String();
                // MethodAssertGenerator build local variable
                Object o_9_0 = vc_14181;
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attributes vc_14178 = (org.jsoup.nodes.Attributes)null;
                // StatementAdderOnAssert create null value
                java.lang.String vc_15074 = (java.lang.String)null;
                // StatementAdderOnAssert create random local variable
                org.jsoup.nodes.Attributes vc_15073 = new org.jsoup.nodes.Attributes();
                // StatementAdderMethod cloned existing statement
                vc_15073.remove(vc_15074);
                // MethodAssertGenerator build local variable
                Object o_19_0 = vc_14178;
                // StatementAdderMethod cloned existing statement
                // MethodCallAdder
                vc_14178.hasKeyIgnoreCase(vc_14181);
                // StatementAdderMethod cloned existing statement
                vc_14178.hasKeyIgnoreCase(vc_14181);
                // MethodAssertGenerator build local variable
                Object o_11_0 = iterator.hasNext();
                org.junit.Assert.fail("testIteratorEmpty_cf61398 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testIteratorEmpty_cf61398_failAssert5_add62860_cf63215 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61406_failAssert7_add62862_cf63331_failAssert18() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
                java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attributes vc_14184 = (org.jsoup.nodes.Attributes)null;
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_15144 = new java.lang.String();
                // StatementAdderOnAssert create random local variable
                org.jsoup.nodes.Attributes vc_15142 = new org.jsoup.nodes.Attributes();
                // StatementAdderMethod cloned existing statement
                vc_15142.removeIgnoreCase(vc_15144);
                // MethodAssertGenerator build local variable
                Object o_15_0 = vc_14184;
                // StatementAdderMethod cloned existing statement
                // MethodCallAdder
                vc_14184.size();
                // StatementAdderMethod cloned existing statement
                vc_14184.size();
                // MethodAssertGenerator build local variable
                Object o_9_0 = iterator.hasNext();
                org.junit.Assert.fail("testIteratorEmpty_cf61406 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testIteratorEmpty_cf61406_failAssert7_add62862_cf63331 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61406_failAssert7_add62862_cf63306_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
                java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attributes vc_14184 = (org.jsoup.nodes.Attributes)null;
                // StatementAdderOnAssert create random local variable
                boolean vc_15128 = true;
                // StatementAdderOnAssert create null value
                java.lang.String vc_15126 = (java.lang.String)null;
                // StatementAdderOnAssert create random local variable
                org.jsoup.nodes.Attributes vc_15125 = new org.jsoup.nodes.Attributes();
                // StatementAdderMethod cloned existing statement
                vc_15125.put(vc_15126, vc_15128);
                // MethodAssertGenerator build local variable
                Object o_17_0 = vc_14184;
                // StatementAdderMethod cloned existing statement
                // MethodCallAdder
                vc_14184.size();
                // StatementAdderMethod cloned existing statement
                vc_14184.size();
                // MethodAssertGenerator build local variable
                Object o_9_0 = iterator.hasNext();
                org.junit.Assert.fail("testIteratorEmpty_cf61406 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testIteratorEmpty_cf61406_failAssert7_add62862_cf63306 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61398_failAssert5_add62860_cf63142_failAssert15() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
                java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_14181 = new java.lang.String();
                // MethodAssertGenerator build local variable
                Object o_9_0 = vc_14181;
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attributes vc_14178 = (org.jsoup.nodes.Attributes)null;
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_1794 = "";
                // StatementAdderOnAssert create random local variable
                org.jsoup.nodes.Attributes vc_15036 = new org.jsoup.nodes.Attributes();
                // StatementAdderMethod cloned existing statement
                vc_15036.getIgnoreCase(String_vc_1794);
                // MethodAssertGenerator build local variable
                Object o_19_0 = vc_14178;
                // StatementAdderMethod cloned existing statement
                // MethodCallAdder
                vc_14178.hasKeyIgnoreCase(vc_14181);
                // StatementAdderMethod cloned existing statement
                vc_14178.hasKeyIgnoreCase(vc_14181);
                // MethodAssertGenerator build local variable
                Object o_11_0 = iterator.hasNext();
                org.junit.Assert.fail("testIteratorEmpty_cf61398 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testIteratorEmpty_cf61398_failAssert5_add62860_cf63142 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64186() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        a.put("Tot", "a&p");
        a.put("Hello", "There");
        a.put("data-name", "Jsoup");
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        iterator.next();
        iterator.remove();
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_1809 = "Hello";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_1809, "Hello");
        // StatementAdderOnAssert create random local variable
        org.jsoup.nodes.Attributes vc_15487 = new org.jsoup.nodes.Attributes();
        // AssertGenerator replace invocation
        java.lang.String o_testIteratorRemovable_cf64186__14 = // StatementAdderMethod cloned existing statement
vc_15487.get(String_vc_1809);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testIteratorRemovable_cf64186__14, "");
        org.junit.Assert.assertEquals(2, a.size());
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test
    public void testIteratorRemovable_literalMutation64140_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("Hello", "There");
            a.put("", "Jsoup");
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            iterator.next();
            iterator.remove();
            // MethodAssertGenerator build local variable
            Object o_10_0 = a.size();
            org.junit.Assert.fail("testIteratorRemovable_literalMutation64140 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64174() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        a.put("Tot", "a&p");
        a.put("Hello", "There");
        a.put("data-name", "Jsoup");
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        iterator.next();
        iterator.remove();
        // AssertGenerator replace invocation
        int o_testIteratorRemovable_cf64174__10 = // StatementAdderMethod cloned existing statement
a.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testIteratorRemovable_cf64174__10, 1777053277);
        org.junit.Assert.assertEquals(2, a.size());
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64151() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        a.put("Tot", "a&p");
        a.put("Hello", "There");
        a.put("data-name", "Jsoup");
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        iterator.next();
        iterator.remove();
        // StatementAdderOnAssert create null value
        java.lang.Object vc_15472 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_15472);
        // AssertGenerator replace invocation
        boolean o_testIteratorRemovable_cf64151__12 = // StatementAdderMethod cloned existing statement
a.equals(vc_15472);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testIteratorRemovable_cf64151__12);
        org.junit.Assert.assertEquals(2, a.size());
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64250_failAssert42() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("Hello", "There");
            a.put("data-name", "Jsoup");
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            iterator.next();
            iterator.remove();
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Attribute vc_15525 = (org.jsoup.nodes.Attribute)null;
            // StatementAdderMethod cloned existing statement
            a.put(vc_15525);
            // MethodAssertGenerator build local variable
            Object o_14_0 = a.size();
            org.junit.Assert.fail("testIteratorRemovable_cf64250 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64191_failAssert24() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("Hello", "There");
            a.put("data-name", "Jsoup");
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            iterator.next();
            iterator.remove();
            // StatementAdderOnAssert create null value
            java.lang.String vc_15492 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.getIgnoreCase(vc_15492);
            // MethodAssertGenerator build local variable
            Object o_14_0 = a.size();
            org.junit.Assert.fail("testIteratorRemovable_cf64191 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64268_failAssert55() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("Hello", "There");
            a.put("data-name", "Jsoup");
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            iterator.next();
            iterator.remove();
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_15534 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            a.removeIgnoreCase(vc_15534);
            // MethodAssertGenerator build local variable
            Object o_14_0 = a.size();
            org.junit.Assert.fail("testIteratorRemovable_cf64268 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64167() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        a.put("Tot", "a&p");
        a.put("Hello", "There");
        a.put("data-name", "Jsoup");
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        iterator.next();
        iterator.remove();
        // StatementAdderOnAssert create null value
        java.lang.String vc_15480 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_15480);
        // AssertGenerator replace invocation
        boolean o_testIteratorRemovable_cf64167__12 = // StatementAdderMethod cloned existing statement
a.hasKeyIgnoreCase(vc_15480);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testIteratorRemovable_cf64167__12);
        org.junit.Assert.assertEquals(2, a.size());
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64236_failAssert39() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("Hello", "There");
            a.put("data-name", "Jsoup");
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            iterator.next();
            iterator.remove();
            // StatementAdderOnAssert create random local variable
            boolean vc_15518 = false;
            // StatementAdderOnAssert create null value
            java.lang.String vc_15516 = (java.lang.String)null;
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_15515 = new org.jsoup.nodes.Attributes();
            // StatementAdderMethod cloned existing statement
            vc_15515.put(vc_15516, vc_15518);
            // MethodAssertGenerator build local variable
            Object o_18_0 = a.size();
            org.junit.Assert.fail("testIteratorRemovable_cf64236 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64184_failAssert18() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("Hello", "There");
            a.put("data-name", "Jsoup");
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            iterator.next();
            iterator.remove();
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_15489 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            a.get(vc_15489);
            // MethodAssertGenerator build local variable
            Object o_14_0 = a.size();
            org.junit.Assert.fail("testIteratorRemovable_cf64184 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64192() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        a.put("Tot", "a&p");
        a.put("Hello", "There");
        a.put("data-name", "Jsoup");
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        iterator.next();
        iterator.remove();
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_1810 = "Hello";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_1810, "Hello");
        // AssertGenerator replace invocation
        java.lang.String o_testIteratorRemovable_cf64192__12 = // StatementAdderMethod cloned existing statement
a.getIgnoreCase(String_vc_1810);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testIteratorRemovable_cf64192__12, "There");
        org.junit.Assert.assertEquals(2, a.size());
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64169_cf65772_failAssert20() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("Hello", "There");
            a.put("data-name", "Jsoup");
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            iterator.next();
            iterator.remove();
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_15481 = new java.lang.String();
            // MethodAssertGenerator build local variable
            Object o_12_0 = vc_15481;
            // AssertGenerator replace invocation
            boolean o_testIteratorRemovable_cf64169__12 = // StatementAdderMethod cloned existing statement
a.hasKeyIgnoreCase(vc_15481);
            // MethodAssertGenerator build local variable
            Object o_16_0 = o_testIteratorRemovable_cf64169__12;
            // StatementAdderOnAssert create null value
            java.lang.String vc_16053 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.removeIgnoreCase(vc_16053);
            // MethodAssertGenerator build local variable
            Object o_22_0 = a.size();
            org.junit.Assert.fail("testIteratorRemovable_cf64169_cf65772 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64177_cf66084_failAssert22() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_12_1 = 2;
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("Hello", "There");
            a.put("data-name", "Jsoup");
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            iterator.next();
            iterator.remove();
            // AssertGenerator replace invocation
            int o_testIteratorRemovable_cf64177__10 = // StatementAdderMethod cloned existing statement
a.size();
            // MethodAssertGenerator build local variable
            Object o_12_0 = o_testIteratorRemovable_cf64177__10;
            // StatementAdderOnAssert create null value
            java.lang.String vc_16179 = (java.lang.String)null;
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_16178 = new org.jsoup.nodes.Attributes();
            // StatementAdderMethod cloned existing statement
            vc_16178.remove(vc_16179);
            // MethodAssertGenerator build local variable
            Object o_20_0 = a.size();
            org.junit.Assert.fail("testIteratorRemovable_cf64177_cf66084 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64169_literalMutation65613_failAssert16() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("", "There");
            a.put("data-name", "Jsoup");
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            iterator.next();
            iterator.remove();
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_15481 = new java.lang.String();
            // MethodAssertGenerator build local variable
            Object o_12_0 = vc_15481;
            // AssertGenerator replace invocation
            boolean o_testIteratorRemovable_cf64169__12 = // StatementAdderMethod cloned existing statement
a.hasKeyIgnoreCase(vc_15481);
            // MethodAssertGenerator build local variable
            Object o_16_0 = o_testIteratorRemovable_cf64169__12;
            // MethodAssertGenerator build local variable
            Object o_18_0 = a.size();
            org.junit.Assert.fail("testIteratorRemovable_cf64169_literalMutation65613 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64160_cf65122_failAssert15() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("Hello", "There");
            a.put("data-name", "Jsoup");
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            iterator.next();
            iterator.remove();
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_15477 = new java.lang.String();
            // MethodAssertGenerator build local variable
            Object o_12_0 = vc_15477;
            // AssertGenerator replace invocation
            boolean o_testIteratorRemovable_cf64160__12 = // StatementAdderMethod cloned existing statement
a.hasKey(vc_15477);
            // MethodAssertGenerator build local variable
            Object o_16_0 = o_testIteratorRemovable_cf64160__12;
            // StatementAdderOnAssert create null value
            java.lang.String vc_15813 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.get(vc_15813);
            // MethodAssertGenerator build local variable
            Object o_22_0 = a.size();
            org.junit.Assert.fail("testIteratorRemovable_cf64160_cf65122 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64192_cf66611_failAssert42() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("Hello", "There");
            a.put("data-name", "Jsoup");
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            iterator.next();
            iterator.remove();
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_1810 = "Hello";
            // MethodAssertGenerator build local variable
            Object o_12_0 = String_vc_1810;
            // AssertGenerator replace invocation
            java.lang.String o_testIteratorRemovable_cf64192__12 = // StatementAdderMethod cloned existing statement
a.getIgnoreCase(String_vc_1810);
            // MethodAssertGenerator build local variable
            Object o_16_0 = o_testIteratorRemovable_cf64192__12;
            // StatementAdderOnAssert create random local variable
            boolean vc_16363 = false;
            // StatementAdderOnAssert create null value
            java.lang.String vc_16361 = (java.lang.String)null;
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_16360 = new org.jsoup.nodes.Attributes();
            // StatementAdderMethod cloned existing statement
            vc_16360.put(vc_16361, vc_16363);
            // MethodAssertGenerator build local variable
            Object o_26_0 = a.size();
            org.junit.Assert.fail("testIteratorRemovable_cf64192_cf66611 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64158_cf64832_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("Hello", "There");
            a.put("data-name", "Jsoup");
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            iterator.next();
            iterator.remove();
            // StatementAdderOnAssert create null value
            java.lang.String vc_15476 = (java.lang.String)null;
            // MethodAssertGenerator build local variable
            Object o_12_0 = vc_15476;
            // AssertGenerator replace invocation
            boolean o_testIteratorRemovable_cf64158__12 = // StatementAdderMethod cloned existing statement
a.hasKey(vc_15476);
            // MethodAssertGenerator build local variable
            Object o_16_0 = o_testIteratorRemovable_cf64158__12;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Attribute vc_15720 = (org.jsoup.nodes.Attribute)null;
            // StatementAdderMethod cloned existing statement
            a.put(vc_15720);
            // MethodAssertGenerator build local variable
            Object o_22_0 = a.size();
            org.junit.Assert.fail("testIteratorRemovable_cf64158_cf64832 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64183_cf66188_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("Hello", "There");
            a.put("data-name", "Jsoup");
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            iterator.next();
            iterator.remove();
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_1809 = "Hello";
            // MethodAssertGenerator build local variable
            Object o_12_0 = String_vc_1809;
            // AssertGenerator replace invocation
            java.lang.String o_testIteratorRemovable_cf64183__12 = // StatementAdderMethod cloned existing statement
a.get(String_vc_1809);
            // MethodAssertGenerator build local variable
            Object o_16_0 = o_testIteratorRemovable_cf64183__12;
            // StatementAdderOnAssert create null value
            java.lang.String vc_16207 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.getIgnoreCase(vc_16207);
            // MethodAssertGenerator build local variable
            Object o_22_0 = a.size();
            org.junit.Assert.fail("testIteratorRemovable_cf64183_cf66188 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64232_failAssert36_literalMutation68320_cf69365_failAssert21() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
                a.put("Tot", "a&p");
                a.put("Hello", "There");
                a.put("data-name", "Jsoup");
                java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
                iterator.next();
                iterator.remove();
                // StatementAdderOnAssert create random local variable
                boolean vc_15518 = true;
                // MethodAssertGenerator build local variable
                Object o_15_0 = vc_15518;
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_15517 = new java.lang.String();
                // MethodAssertGenerator build local variable
                Object o_19_0 = vc_15517;
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attributes vc_15514 = (org.jsoup.nodes.Attributes)null;
                // StatementAdderMethod cloned existing statement
                a.getIgnoreCase(vc_15517);
                // MethodAssertGenerator build local variable
                Object o_25_0 = vc_15514;
                // StatementAdderMethod cloned existing statement
                vc_15514.put(vc_15517, vc_15518);
                // MethodAssertGenerator build local variable
                Object o_18_0 = a.size();
                org.junit.Assert.fail("testIteratorRemovable_cf64232 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testIteratorRemovable_cf64232_failAssert36_literalMutation68320_cf69365 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64164_failAssert9_add67295_cf69229_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
                a.put("Tot", "a&p");
                a.put("Hello", "There");
                a.put("data-name", "Jsoup");
                java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
                iterator.next();
                iterator.remove();
                // StatementAdderOnAssert create null value
                java.lang.String vc_15480 = (java.lang.String)null;
                // MethodAssertGenerator build local variable
                Object o_14_0 = vc_15480;
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attributes vc_15478 = (org.jsoup.nodes.Attributes)null;
                // StatementAdderOnAssert create random local variable
                boolean vc_16558 = true;
                // StatementAdderOnAssert create null value
                java.lang.String vc_16556 = (java.lang.String)null;
                // StatementAdderOnAssert create random local variable
                org.jsoup.nodes.Attributes vc_16555 = new org.jsoup.nodes.Attributes();
                // StatementAdderMethod cloned existing statement
                vc_16555.put(vc_16556, vc_16558);
                // MethodAssertGenerator build local variable
                Object o_26_0 = vc_15478;
                // StatementAdderMethod cloned existing statement
                // MethodCallAdder
                vc_15478.hasKeyIgnoreCase(vc_15480);
                // StatementAdderMethod cloned existing statement
                vc_15478.hasKeyIgnoreCase(vc_15480);
                // MethodAssertGenerator build local variable
                Object o_16_0 = a.size();
                org.junit.Assert.fail("testIteratorRemovable_cf64164 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testIteratorRemovable_cf64164_failAssert9_add67295_cf69229 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64164_failAssert9_add67295_literalMutation69109_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
                a.put("Tot", "a&p");
                a.put("", "There");
                a.put("data-name", "Jsoup");
                java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
                iterator.next();
                iterator.remove();
                // StatementAdderOnAssert create null value
                java.lang.String vc_15480 = (java.lang.String)null;
                // MethodAssertGenerator build local variable
                Object o_14_0 = vc_15480;
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attributes vc_15478 = (org.jsoup.nodes.Attributes)null;
                // MethodAssertGenerator build local variable
                Object o_18_0 = vc_15478;
                // StatementAdderMethod cloned existing statement
                // MethodCallAdder
                vc_15478.hasKeyIgnoreCase(vc_15480);
                // StatementAdderMethod cloned existing statement
                vc_15478.hasKeyIgnoreCase(vc_15480);
                // MethodAssertGenerator build local variable
                Object o_16_0 = a.size();
                org.junit.Assert.fail("testIteratorRemovable_cf64164 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testIteratorRemovable_cf64164_failAssert9_add67295_literalMutation69109 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64164_failAssert9_add67295_cf69259_failAssert10() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
                a.put("Tot", "a&p");
                a.put("Hello", "There");
                a.put("data-name", "Jsoup");
                java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
                iterator.next();
                iterator.remove();
                // StatementAdderOnAssert create null value
                java.lang.String vc_15480 = (java.lang.String)null;
                // MethodAssertGenerator build local variable
                Object o_14_0 = vc_15480;
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attributes vc_15478 = (org.jsoup.nodes.Attributes)null;
                // StatementAdderOnAssert create null value
                java.lang.String vc_16569 = (java.lang.String)null;
                // StatementAdderOnAssert create random local variable
                org.jsoup.nodes.Attributes vc_16568 = new org.jsoup.nodes.Attributes();
                // StatementAdderMethod cloned existing statement
                vc_16568.remove(vc_16569);
                // MethodAssertGenerator build local variable
                Object o_24_0 = vc_15478;
                // StatementAdderMethod cloned existing statement
                // MethodCallAdder
                vc_15478.hasKeyIgnoreCase(vc_15480);
                // StatementAdderMethod cloned existing statement
                vc_15478.hasKeyIgnoreCase(vc_15480);
                // MethodAssertGenerator build local variable
                Object o_16_0 = a.size();
                org.junit.Assert.fail("testIteratorRemovable_cf64164 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testIteratorRemovable_cf64164_failAssert9_add67295_cf69259 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }
}

