

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
    public void html_cf103() {
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
        java.lang.String String_vc_1 = "a&p";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_1, "a&p");
        // AssertGenerator replace invocation
        boolean o_html_cf103__36 = // StatementAdderMethod cloned existing statement
a.hasKeyIgnoreCase(String_vc_1);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_html_cf103__36);
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
    @org.junit.Test(timeout = 10000)
    public void removeCaseSensitive_cf42862_failAssert65() {
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
            // StatementAdderOnAssert create null value
            java.lang.String vc_12136 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.put(vc_12136, vc_12138);
            // MethodAssertGenerator build local variable
            Object o_22_0 = a.hasKey("Tot");
            org.junit.Assert.fail("removeCaseSensitive_cf42862 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#removeCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void removeCaseSensitive_cf42895_failAssert82() {
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
            java.lang.String vc_12153 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.removeIgnoreCase(vc_12153);
            // MethodAssertGenerator build local variable
            Object o_20_0 = a.hasKey("Tot");
            org.junit.Assert.fail("removeCaseSensitive_cf42895 should have thrown IllegalArgumentException");
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
    public void removeCaseSensitive_cf42879_failAssert70() {
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
            // StatementAdderMethod cloned existing statement
            a.put(vc_12145);
            // MethodAssertGenerator build local variable
            Object o_20_0 = a.hasKey("Tot");
            org.junit.Assert.fail("removeCaseSensitive_cf42879 should have thrown IllegalArgumentException");
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
    public void removeCaseSensitive_cf42811_failAssert45() {
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
            java.lang.String vc_12108 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.get(vc_12108);
            // MethodAssertGenerator build local variable
            Object o_20_0 = a.hasKey("Tot");
            org.junit.Assert.fail("removeCaseSensitive_cf42811 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#removeCaseSensitive */
    @org.junit.Test
    public void removeCaseSensitive_literalMutation42748_failAssert16() {
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
            // MethodAssertGenerator build local variable
            Object o_16_0 = a.hasKey("Tot");
            org.junit.Assert.fail("removeCaseSensitive_literalMutation42748 should have thrown IllegalArgumentException");
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
    public void removeCaseSensitive_cf42806_literalMutation44983_failAssert0_literalMutation51226() {
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
            a.put("data-name", "Jsoup");
            // MethodAssertGenerator build local variable
            Object o_8_0 = a.size();
            a.remove("U9d");
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
    public void testIterator_cf53438() {
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
        // StatementAdderOnAssert create random local variable
        org.jsoup.nodes.Attributes vc_13210 = new org.jsoup.nodes.Attributes();
        // AssertGenerator replace invocation
        int o_testIterator_cf53438__23 = // StatementAdderMethod cloned existing statement
vc_13210.size();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testIterator_cf53438__23, 0);
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
    public void testIterator_cf53429() {
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
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_13206 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_13206, "");
        // AssertGenerator replace invocation
        boolean o_testIterator_cf53429__23 = // StatementAdderMethod cloned existing statement
a.hasKeyIgnoreCase(vc_13206);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testIterator_cf53429__23);
        org.junit.Assert.assertEquals(datas.length, i);
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53496_failAssert45() {
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
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_13240 = new org.jsoup.nodes.Attributes();
            // StatementAdderMethod cloned existing statement
            vc_13240.put(vc_13241, vc_13243);
            org.junit.Assert.fail("testIterator_cf53496 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53528_failAssert61() {
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
            java.lang.String vc_13259 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            a.removeIgnoreCase(vc_13259);
            org.junit.Assert.fail("testIterator_cf53528 should have thrown IllegalArgumentException");
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
    public void testIterator_cf53451_failAssert30() {
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
            java.lang.String vc_13217 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.getIgnoreCase(vc_13217);
            org.junit.Assert.fail("testIterator_cf53451 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
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
    public void testIterator_cf53461_cf56243_failAssert35() {
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
            // AssertGenerator replace invocation
            java.lang.String o_testIterator_cf53461__21 = // StatementAdderMethod cloned existing statement
a.toString();
            // MethodAssertGenerator build local variable
            Object o_23_0 = o_testIterator_cf53461__21;
            // StatementAdderOnAssert create random local variable
            boolean vc_14218 = false;
            // StatementAdderOnAssert create null value
            java.lang.String vc_14216 = (java.lang.String)null;
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_14215 = new org.jsoup.nodes.Attributes();
            // StatementAdderMethod cloned existing statement
            vc_14215.put(vc_14216, vc_14218);
            org.junit.Assert.fail("testIterator_cf53461_cf56243 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53452_cf55907_failAssert14() {
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
            java.lang.String String_vc_1633 = "data-name";
            // MethodAssertGenerator build local variable
            Object o_23_0 = String_vc_1633;
            // AssertGenerator replace invocation
            java.lang.String o_testIterator_cf53452__23 = // StatementAdderMethod cloned existing statement
a.getIgnoreCase(String_vc_1633);
            // MethodAssertGenerator build local variable
            Object o_27_0 = o_testIterator_cf53452__23;
            // StatementAdderOnAssert create random local variable
            boolean vc_14088 = true;
            // StatementAdderOnAssert create null value
            java.lang.String vc_14086 = (java.lang.String)null;
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_14085 = new org.jsoup.nodes.Attributes();
            // StatementAdderMethod cloned existing statement
            vc_14085.put(vc_14086, vc_14088);
            org.junit.Assert.fail("testIterator_cf53452_cf55907 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53460_failAssert35_literalMutation57799_failAssert50() {
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
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attributes vc_13221 = (org.jsoup.nodes.Attributes)null;
                // StatementAdderMethod cloned existing statement
                vc_13221.toString();
                org.junit.Assert.fail("testIterator_cf53460 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testIterator_cf53460_failAssert35_literalMutation57799 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53452_cf55862_failAssert41() {
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
            java.lang.String String_vc_1633 = "data-name";
            // MethodAssertGenerator build local variable
            Object o_23_0 = String_vc_1633;
            // AssertGenerator replace invocation
            java.lang.String o_testIterator_cf53452__23 = // StatementAdderMethod cloned existing statement
a.getIgnoreCase(String_vc_1633);
            // MethodAssertGenerator build local variable
            Object o_27_0 = o_testIterator_cf53452__23;
            // StatementAdderOnAssert create null value
            java.lang.String vc_14062 = (java.lang.String)null;
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_14061 = new org.jsoup.nodes.Attributes();
            // StatementAdderMethod cloned existing statement
            vc_14061.getIgnoreCase(vc_14062);
            org.junit.Assert.fail("testIterator_cf53452_cf55862 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53412_cf53831_failAssert20() {
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
            // StatementAdderOnAssert create null value
            java.lang.String vc_13347 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Attributes vc_13345 = (org.jsoup.nodes.Attributes)null;
            // StatementAdderMethod cloned existing statement
            vc_13345.getIgnoreCase(vc_13347);
            org.junit.Assert.fail("testIterator_cf53412_cf53831 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53427_cf54584_failAssert34() {
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
            java.lang.String vc_13205 = (java.lang.String)null;
            // MethodAssertGenerator build local variable
            Object o_23_0 = vc_13205;
            // AssertGenerator replace invocation
            boolean o_testIterator_cf53427__23 = // StatementAdderMethod cloned existing statement
a.hasKeyIgnoreCase(vc_13205);
            // MethodAssertGenerator build local variable
            Object o_27_0 = o_testIterator_cf53427__23;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Attributes vc_13597 = (org.jsoup.nodes.Attributes)null;
            // StatementAdderMethod cloned existing statement
            vc_13597.hashCode();
            org.junit.Assert.fail("testIterator_cf53427_cf54584 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53418_cf54130_failAssert30() {
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
            java.lang.String vc_13201 = (java.lang.String)null;
            // MethodAssertGenerator build local variable
            Object o_23_0 = vc_13201;
            // AssertGenerator replace invocation
            boolean o_testIterator_cf53418__23 = // StatementAdderMethod cloned existing statement
a.hasKey(vc_13201);
            // MethodAssertGenerator build local variable
            Object o_27_0 = o_testIterator_cf53418__23;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_13454 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            a.removeIgnoreCase(vc_13454);
            org.junit.Assert.fail("testIterator_cf53418_cf54130 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53419_cf54307_failAssert48() {
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
            // StatementAdderOnAssert create null value
            java.lang.String vc_13514 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.remove(vc_13514);
            org.junit.Assert.fail("testIterator_cf53419_cf54307 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53428_cf54789_failAssert8() {
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
            java.lang.String vc_13669 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            a.get(vc_13669);
            org.junit.Assert.fail("testIterator_cf53428_cf54789 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_cf53515_failAssert51_literalMutation58590_failAssert27_literalMutation60429_failAssert21() {
        // AssertGenerator generate try/catch block with fail statement
        try {
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
                    int i = -1;
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
                    java.lang.String String_vc_1636 = "raul";
                    // StatementAdderOnAssert create null value
                    org.jsoup.nodes.Attributes vc_13252 = (org.jsoup.nodes.Attributes)null;
                    // StatementAdderMethod cloned existing statement
                    vc_13252.remove(String_vc_1636);
                    org.junit.Assert.fail("testIterator_cf53515 should have thrown NullPointerException");
                } catch (java.lang.NullPointerException eee) {
                }
                org.junit.Assert.fail("testIterator_cf53515_failAssert51_literalMutation58590 should have thrown ArrayIndexOutOfBoundsException");
            } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("testIterator_cf53515_failAssert51_literalMutation58590_failAssert27_literalMutation60429 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61456_failAssert29() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // StatementAdderOnAssert create random local variable
            boolean vc_14283 = true;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_14282 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            a.put(vc_14282, vc_14283);
            // MethodAssertGenerator build local variable
            Object o_11_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61456 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61412_failAssert12() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // StatementAdderOnAssert create null value
            java.lang.String vc_14253 = (java.lang.String)null;
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_14252 = new org.jsoup.nodes.Attributes();
            // StatementAdderMethod cloned existing statement
            vc_14252.get(vc_14253);
            // MethodAssertGenerator build local variable
            Object o_11_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61412 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61479_failAssert43() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // StatementAdderOnAssert create null value
            java.lang.String vc_14298 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.removeIgnoreCase(vc_14298);
            // MethodAssertGenerator build local variable
            Object o_9_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61479 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61473_failAssert37() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // StatementAdderOnAssert create null value
            java.lang.String vc_14294 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.remove(vc_14294);
            // MethodAssertGenerator build local variable
            Object o_9_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61473 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61403() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        // AssertGenerator replace invocation
        int o_testIteratorEmpty_cf61403__5 = // StatementAdderMethod cloned existing statement
a.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testIteratorEmpty_cf61403__5, 0);
        org.junit.Assert.assertFalse(iterator.hasNext());
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61428() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        // StatementAdderOnAssert create random local variable
        org.jsoup.nodes.Attributes vc_14264 = new org.jsoup.nodes.Attributes();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_14264.equals(a));
        // AssertGenerator replace invocation
        java.util.Iterator<org.jsoup.nodes.Attribute> o_testIteratorEmpty_cf61428__7 = // StatementAdderMethod cloned existing statement
vc_14264.iterator();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testIteratorEmpty_cf61428__7.equals(iterator));
        org.junit.Assert.assertFalse(iterator.hasNext());
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61406() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        // AssertGenerator replace invocation
        int o_testIteratorEmpty_cf61406__5 = // StatementAdderMethod cloned existing statement
a.size();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testIteratorEmpty_cf61406__5, 0);
        org.junit.Assert.assertFalse(iterator.hasNext());
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61418_failAssert18() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // StatementAdderOnAssert create null value
            java.lang.String vc_14257 = (java.lang.String)null;
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_14256 = new org.jsoup.nodes.Attributes();
            // StatementAdderMethod cloned existing statement
            vc_14256.getIgnoreCase(vc_14257);
            // MethodAssertGenerator build local variable
            Object o_11_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61418 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61387() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_14238 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_testIteratorEmpty_cf61387__7 = // StatementAdderMethod cloned existing statement
a.equals(vc_14238);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testIteratorEmpty_cf61387__7);
        org.junit.Assert.assertFalse(iterator.hasNext());
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61398() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        // StatementAdderOnAssert create null value
        java.lang.String vc_14245 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_14245);
        // AssertGenerator replace invocation
        boolean o_testIteratorEmpty_cf61398__7 = // StatementAdderMethod cloned existing statement
a.hasKeyIgnoreCase(vc_14245);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testIteratorEmpty_cf61398__7);
        org.junit.Assert.assertFalse(iterator.hasNext());
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61424() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        // AssertGenerator replace invocation
        java.lang.String o_testIteratorEmpty_cf61424__5 = // StatementAdderMethod cloned existing statement
a.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testIteratorEmpty_cf61424__5, "");
        org.junit.Assert.assertFalse(iterator.hasNext());
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61467_failAssert33() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Attribute vc_14290 = (org.jsoup.nodes.Attribute)null;
            // StatementAdderMethod cloned existing statement
            a.put(vc_14290);
            // MethodAssertGenerator build local variable
            Object o_9_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61467 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61457_failAssert30() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // StatementAdderOnAssert create random local variable
            boolean vc_14283 = true;
            // StatementAdderOnAssert create null value
            java.lang.String vc_14281 = (java.lang.String)null;
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_14280 = new org.jsoup.nodes.Attributes();
            // StatementAdderMethod cloned existing statement
            vc_14280.put(vc_14281, vc_14283);
            // MethodAssertGenerator build local variable
            Object o_13_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61457 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61428_cf62838_failAssert16() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_14264 = new org.jsoup.nodes.Attributes();
            // MethodAssertGenerator build local variable
            Object o_7_0 = vc_14264.equals(a);
            // AssertGenerator replace invocation
            java.util.Iterator<org.jsoup.nodes.Attribute> o_testIteratorEmpty_cf61428__7 = // StatementAdderMethod cloned existing statement
vc_14264.iterator();
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testIteratorEmpty_cf61428__7.equals(iterator);
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Attribute vc_15005 = (org.jsoup.nodes.Attribute)null;
            // StatementAdderMethod cloned existing statement
            a.put(vc_15005);
            // MethodAssertGenerator build local variable
            Object o_17_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61428_cf62838 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61428_cf62829_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_14264 = new org.jsoup.nodes.Attributes();
            // MethodAssertGenerator build local variable
            Object o_7_0 = vc_14264.equals(a);
            // AssertGenerator replace invocation
            java.util.Iterator<org.jsoup.nodes.Attribute> o_testIteratorEmpty_cf61428__7 = // StatementAdderMethod cloned existing statement
vc_14264.iterator();
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testIteratorEmpty_cf61428__7.equals(iterator);
            // StatementAdderOnAssert create random local variable
            boolean vc_14998 = false;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_14997 = new java.lang.String();
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_14995 = new org.jsoup.nodes.Attributes();
            // StatementAdderMethod cloned existing statement
            vc_14995.put(vc_14997, vc_14998);
            // MethodAssertGenerator build local variable
            Object o_21_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61428_cf62829 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61419_failAssert19_add62873() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_14258 = new java.lang.String();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_14258, "");
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_14256 = new org.jsoup.nodes.Attributes();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(vc_14256.equals(a));
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_14256.getIgnoreCase(vc_14258);
            // StatementAdderMethod cloned existing statement
            vc_14256.getIgnoreCase(vc_14258);
            // MethodAssertGenerator build local variable
            Object o_11_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61419 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61421_cf62629_failAssert21() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // AssertGenerator replace invocation
            java.lang.String o_testIteratorEmpty_cf61421__5 = // StatementAdderMethod cloned existing statement
a.html();
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_testIteratorEmpty_cf61421__5;
            // StatementAdderOnAssert create null value
            java.lang.String vc_14883 = (java.lang.String)null;
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_14882 = new org.jsoup.nodes.Attributes();
            // StatementAdderMethod cloned existing statement
            vc_14882.removeIgnoreCase(vc_14883);
            // MethodAssertGenerator build local variable
            Object o_15_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61421_cf62629 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61398_cf62074_failAssert49() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // StatementAdderOnAssert create null value
            java.lang.String vc_14245 = (java.lang.String)null;
            // MethodAssertGenerator build local variable
            Object o_7_0 = vc_14245;
            // AssertGenerator replace invocation
            boolean o_testIteratorEmpty_cf61398__7 = // StatementAdderMethod cloned existing statement
a.hasKeyIgnoreCase(vc_14245);
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testIteratorEmpty_cf61398__7;
            // StatementAdderOnAssert create null value
            java.lang.String vc_14578 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.get(vc_14578);
            // MethodAssertGenerator build local variable
            Object o_17_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61398_cf62074 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61398_cf62085_failAssert85() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // StatementAdderOnAssert create null value
            java.lang.String vc_14245 = (java.lang.String)null;
            // MethodAssertGenerator build local variable
            Object o_7_0 = vc_14245;
            // AssertGenerator replace invocation
            boolean o_testIteratorEmpty_cf61398__7 = // StatementAdderMethod cloned existing statement
a.hasKeyIgnoreCase(vc_14245);
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testIteratorEmpty_cf61398__7;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_14583 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            a.getIgnoreCase(vc_14583);
            // MethodAssertGenerator build local variable
            Object o_17_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61398_cf62085 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61406_cf62482_failAssert47() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_7_1 = 0;
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // AssertGenerator replace invocation
            int o_testIteratorEmpty_cf61406__5 = // StatementAdderMethod cloned existing statement
a.size();
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_testIteratorEmpty_cf61406__5;
            // StatementAdderOnAssert create random local variable
            boolean vc_14803 = true;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_14802 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            a.put(vc_14802, vc_14803);
            // MethodAssertGenerator build local variable
            Object o_15_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61406_cf62482 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61398_cf62163_failAssert26() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            // StatementAdderOnAssert create null value
            java.lang.String vc_14245 = (java.lang.String)null;
            // MethodAssertGenerator build local variable
            Object o_7_0 = vc_14245;
            // AssertGenerator replace invocation
            boolean o_testIteratorEmpty_cf61398__7 = // StatementAdderMethod cloned existing statement
a.hasKeyIgnoreCase(vc_14245);
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testIteratorEmpty_cf61398__7;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_14624 = new java.lang.String();
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_14622 = new org.jsoup.nodes.Attributes();
            // StatementAdderMethod cloned existing statement
            vc_14622.removeIgnoreCase(vc_14624);
            // MethodAssertGenerator build local variable
            Object o_19_0 = iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmpty_cf61398_cf62163 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61405_failAssert7_add62861_cf63996_failAssert36() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
                java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attributes vc_14249 = (org.jsoup.nodes.Attributes)null;
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attribute vc_15590 = (org.jsoup.nodes.Attribute)null;
                // StatementAdderMethod cloned existing statement
                a.put(vc_15590);
                // MethodAssertGenerator build local variable
                Object o_13_0 = vc_14249;
                // StatementAdderMethod cloned existing statement
                // MethodCallAdder
                vc_14249.size();
                // StatementAdderMethod cloned existing statement
                vc_14249.size();
                // MethodAssertGenerator build local variable
                Object o_9_0 = iterator.hasNext();
                org.junit.Assert.fail("testIteratorEmpty_cf61405 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testIteratorEmpty_cf61405_failAssert7_add62861_cf63996 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61396_failAssert4_add62858_cf63046_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
                java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
                // StatementAdderOnAssert create null value
                java.lang.String vc_14245 = (java.lang.String)null;
                // MethodAssertGenerator build local variable
                Object o_9_0 = vc_14245;
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attributes vc_14243 = (org.jsoup.nodes.Attributes)null;
                // StatementAdderOnAssert create null value
                java.lang.String vc_15098 = (java.lang.String)null;
                // StatementAdderOnAssert create random local variable
                org.jsoup.nodes.Attributes vc_15097 = new org.jsoup.nodes.Attributes();
                // StatementAdderMethod cloned existing statement
                vc_15097.get(vc_15098);
                // MethodAssertGenerator build local variable
                Object o_19_0 = vc_14243;
                // StatementAdderMethod cloned existing statement
                // MethodCallAdder
                vc_14243.hasKeyIgnoreCase(vc_14245);
                // StatementAdderMethod cloned existing statement
                vc_14243.hasKeyIgnoreCase(vc_14245);
                // MethodAssertGenerator build local variable
                Object o_11_0 = iterator.hasNext();
                org.junit.Assert.fail("testIteratorEmpty_cf61396 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testIteratorEmpty_cf61396_failAssert4_add62858_cf63046 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61405_failAssert7_add62861_cf64005_failAssert34() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
                java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attributes vc_14249 = (org.jsoup.nodes.Attributes)null;
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_15595 = new java.lang.String();
                // StatementAdderOnAssert create random local variable
                org.jsoup.nodes.Attributes vc_15593 = new org.jsoup.nodes.Attributes();
                // StatementAdderMethod cloned existing statement
                vc_15593.remove(vc_15595);
                // MethodAssertGenerator build local variable
                Object o_15_0 = vc_14249;
                // StatementAdderMethod cloned existing statement
                // MethodCallAdder
                vc_14249.size();
                // StatementAdderMethod cloned existing statement
                vc_14249.size();
                // MethodAssertGenerator build local variable
                Object o_9_0 = iterator.hasNext();
                org.junit.Assert.fail("testIteratorEmpty_cf61405 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testIteratorEmpty_cf61405_failAssert7_add62861_cf64005 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61408_failAssert8_add62862_cf63255_failAssert11() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
                java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
                // StatementAdderOnAssert create null value
                java.lang.String vc_14253 = (java.lang.String)null;
                // MethodAssertGenerator build local variable
                Object o_9_0 = vc_14253;
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attributes vc_14251 = (org.jsoup.nodes.Attributes)null;
                // StatementAdderOnAssert create null value
                java.lang.String vc_15208 = (java.lang.String)null;
                // StatementAdderOnAssert create random local variable
                org.jsoup.nodes.Attributes vc_15207 = new org.jsoup.nodes.Attributes();
                // StatementAdderMethod cloned existing statement
                vc_15207.removeIgnoreCase(vc_15208);
                // MethodAssertGenerator build local variable
                Object o_19_0 = vc_14251;
                // StatementAdderMethod cloned existing statement
                // MethodCallAdder
                vc_14251.get(vc_14253);
                // StatementAdderMethod cloned existing statement
                vc_14251.get(vc_14253);
                // MethodAssertGenerator build local variable
                Object o_11_0 = iterator.hasNext();
                org.junit.Assert.fail("testIteratorEmpty_cf61408 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testIteratorEmpty_cf61408_failAssert8_add62862_cf63255 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61405_failAssert7_add62861_cf63948_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
                java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attributes vc_14249 = (org.jsoup.nodes.Attributes)null;
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_15558 = new java.lang.String();
                // StatementAdderOnAssert create random local variable
                org.jsoup.nodes.Attributes vc_15556 = new org.jsoup.nodes.Attributes();
                // StatementAdderMethod cloned existing statement
                vc_15556.getIgnoreCase(vc_15558);
                // MethodAssertGenerator build local variable
                Object o_15_0 = vc_14249;
                // StatementAdderMethod cloned existing statement
                // MethodCallAdder
                vc_14249.size();
                // StatementAdderMethod cloned existing statement
                vc_14249.size();
                // MethodAssertGenerator build local variable
                Object o_9_0 = iterator.hasNext();
                org.junit.Assert.fail("testIteratorEmpty_cf61405 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testIteratorEmpty_cf61405_failAssert7_add62861_cf63948 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_cf61402_failAssert6_add62860_cf63563_failAssert14() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
                java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attributes vc_14247 = (org.jsoup.nodes.Attributes)null;
                // StatementAdderOnAssert create random local variable
                boolean vc_15388 = false;
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_15387 = new java.lang.String();
                // StatementAdderOnAssert create random local variable
                org.jsoup.nodes.Attributes vc_15385 = new org.jsoup.nodes.Attributes();
                // StatementAdderMethod cloned existing statement
                vc_15385.put(vc_15387, vc_15388);
                // MethodAssertGenerator build local variable
                Object o_17_0 = vc_14247;
                // StatementAdderMethod cloned existing statement
                // MethodCallAdder
                vc_14247.hashCode();
                // StatementAdderMethod cloned existing statement
                vc_14247.hashCode();
                // MethodAssertGenerator build local variable
                Object o_9_0 = iterator.hasNext();
                org.junit.Assert.fail("testIteratorEmpty_cf61402 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testIteratorEmpty_cf61402_failAssert6_add62860_cf63563 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64340() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        a.put("Tot", "a&p");
        a.put("Hello", "There");
        a.put("data-name", "Jsoup");
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        iterator.next();
        iterator.remove();
        // StatementAdderOnAssert create random local variable
        org.jsoup.nodes.Attributes vc_15680 = new org.jsoup.nodes.Attributes();
        // AssertGenerator replace invocation
        int o_testIteratorRemovable_cf64340__12 = // StatementAdderMethod cloned existing statement
vc_15680.size();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testIteratorRemovable_cf64340__12, 0);
        org.junit.Assert.assertEquals(2, a.size());
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64395_failAssert37() {
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
            boolean vc_15713 = true;
            // StatementAdderOnAssert create null value
            java.lang.String vc_15711 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.put(vc_15711, vc_15713);
            // MethodAssertGenerator build local variable
            Object o_16_0 = a.size();
            org.junit.Assert.fail("testIteratorRemovable_cf64395 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64354() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        a.put("Tot", "a&p");
        a.put("Hello", "There");
        a.put("data-name", "Jsoup");
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        iterator.next();
        iterator.remove();
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_1811 = "data-name";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_1811, "data-name");
        // AssertGenerator replace invocation
        java.lang.String o_testIteratorRemovable_cf64354__12 = // StatementAdderMethod cloned existing statement
a.getIgnoreCase(String_vc_1811);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testIteratorRemovable_cf64354__12, "Jsoup");
        org.junit.Assert.assertEquals(2, a.size());
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64331() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        a.put("Tot", "a&p");
        a.put("Hello", "There");
        a.put("data-name", "Jsoup");
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        iterator.next();
        iterator.remove();
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_15676 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_15676, "");
        // AssertGenerator replace invocation
        boolean o_testIteratorRemovable_cf64331__12 = // StatementAdderMethod cloned existing statement
a.hasKeyIgnoreCase(vc_15676);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testIteratorRemovable_cf64331__12);
        org.junit.Assert.assertEquals(2, a.size());
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64314() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        a.put("Tot", "a&p");
        a.put("Hello", "There");
        a.put("data-name", "Jsoup");
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        iterator.next();
        iterator.remove();
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_15668 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_testIteratorRemovable_cf64314__12 = // StatementAdderMethod cloned existing statement
a.equals(vc_15668);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testIteratorRemovable_cf64314__12);
        org.junit.Assert.assertEquals(2, a.size());
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64353_failAssert24() {
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
            java.lang.String vc_15687 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.getIgnoreCase(vc_15687);
            // MethodAssertGenerator build local variable
            Object o_14_0 = a.size();
            org.junit.Assert.fail("testIteratorRemovable_cf64353 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64336() {
        org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
        a.put("Tot", "a&p");
        a.put("Hello", "There");
        a.put("data-name", "Jsoup");
        java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
        iterator.next();
        iterator.remove();
        // AssertGenerator replace invocation
        int o_testIteratorRemovable_cf64336__10 = // StatementAdderMethod cloned existing statement
a.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testIteratorRemovable_cf64336__10, 1777053277);
        org.junit.Assert.assertEquals(2, a.size());
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64428_failAssert54() {
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
            java.lang.String vc_15728 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.removeIgnoreCase(vc_15728);
            // MethodAssertGenerator build local variable
            Object o_14_0 = a.size();
            org.junit.Assert.fail("testIteratorRemovable_cf64428 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64346_failAssert18() {
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
            java.lang.String vc_15684 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            a.get(vc_15684);
            // MethodAssertGenerator build local variable
            Object o_14_0 = a.size();
            org.junit.Assert.fail("testIteratorRemovable_cf64346 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64419_failAssert47() {
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
            java.lang.String vc_15724 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.remove(vc_15724);
            // MethodAssertGenerator build local variable
            Object o_14_0 = a.size();
            org.junit.Assert.fail("testIteratorRemovable_cf64419 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test
    public void testIteratorRemovable_literalMutation64292_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("", "There");
            a.put("data-name", "Jsoup");
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            iterator.next();
            iterator.remove();
            // MethodAssertGenerator build local variable
            Object o_10_0 = a.size();
            org.junit.Assert.fail("testIteratorRemovable_literalMutation64292 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64412_failAssert42() {
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
            org.jsoup.nodes.Attribute vc_15720 = (org.jsoup.nodes.Attribute)null;
            // StatementAdderMethod cloned existing statement
            a.put(vc_15720);
            // MethodAssertGenerator build local variable
            Object o_14_0 = a.size();
            org.junit.Assert.fail("testIteratorRemovable_cf64412 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64329_cf65478_failAssert12() {
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
            java.lang.String vc_15675 = (java.lang.String)null;
            // MethodAssertGenerator build local variable
            Object o_12_0 = vc_15675;
            // AssertGenerator replace invocation
            boolean o_testIteratorRemovable_cf64329__12 = // StatementAdderMethod cloned existing statement
a.hasKeyIgnoreCase(vc_15675);
            // MethodAssertGenerator build local variable
            Object o_16_0 = o_testIteratorRemovable_cf64329__12;
            // StatementAdderOnAssert create null value
            java.lang.String vc_16077 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.getIgnoreCase(vc_16077);
            // MethodAssertGenerator build local variable
            Object o_22_0 = a.size();
            org.junit.Assert.fail("testIteratorRemovable_cf64329_cf65478 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64393_failAssert35_literalMutation68382_failAssert21() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
                a.put("", "a&p");
                a.put("Hello", "There");
                a.put("data-name", "Jsoup");
                java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
                iterator.next();
                iterator.remove();
                // StatementAdderOnAssert create random local variable
                boolean vc_15713 = true;
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_1812 = "data-name";
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attributes vc_15709 = (org.jsoup.nodes.Attributes)null;
                // StatementAdderMethod cloned existing statement
                vc_15709.put(String_vc_1812, vc_15713);
                // MethodAssertGenerator build local variable
                Object o_18_0 = a.size();
                org.junit.Assert.fail("testIteratorRemovable_cf64393 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testIteratorRemovable_cf64393_failAssert35_literalMutation68382 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64345_cf66583_failAssert7() {
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
            java.lang.String o_testIteratorRemovable_cf64345__12 = // StatementAdderMethod cloned existing statement
a.get(String_vc_1810);
            // MethodAssertGenerator build local variable
            Object o_16_0 = o_testIteratorRemovable_cf64345__12;
            // StatementAdderOnAssert create null value
            java.lang.String vc_16504 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            a.remove(vc_16504);
            // MethodAssertGenerator build local variable
            Object o_22_0 = a.size();
            org.junit.Assert.fail("testIteratorRemovable_cf64345_cf66583 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64345_cf66560_failAssert31() {
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
            java.lang.String o_testIteratorRemovable_cf64345__12 = // StatementAdderMethod cloned existing statement
a.get(String_vc_1810);
            // MethodAssertGenerator build local variable
            Object o_16_0 = o_testIteratorRemovable_cf64345__12;
            // StatementAdderOnAssert create random local variable
            boolean vc_16493 = false;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_16492 = new java.lang.String();
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_16490 = new org.jsoup.nodes.Attributes();
            // StatementAdderMethod cloned existing statement
            vc_16490.put(vc_16492, vc_16493);
            // MethodAssertGenerator build local variable
            Object o_26_0 = a.size();
            org.junit.Assert.fail("testIteratorRemovable_cf64345_cf66560 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64354_cf66789_failAssert35() {
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
            java.lang.String String_vc_1811 = "data-name";
            // MethodAssertGenerator build local variable
            Object o_12_0 = String_vc_1811;
            // AssertGenerator replace invocation
            java.lang.String o_testIteratorRemovable_cf64354__12 = // StatementAdderMethod cloned existing statement
a.getIgnoreCase(String_vc_1811);
            // MethodAssertGenerator build local variable
            Object o_16_0 = o_testIteratorRemovable_cf64354__12;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_16574 = new java.lang.String();
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_16572 = new org.jsoup.nodes.Attributes();
            // StatementAdderMethod cloned existing statement
            vc_16572.removeIgnoreCase(vc_16574);
            // MethodAssertGenerator build local variable
            Object o_24_0 = a.size();
            org.junit.Assert.fail("testIteratorRemovable_cf64354_cf66789 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64345_cf66577_failAssert39() {
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
            java.lang.String o_testIteratorRemovable_cf64345__12 = // StatementAdderMethod cloned existing statement
a.get(String_vc_1810);
            // MethodAssertGenerator build local variable
            Object o_16_0 = o_testIteratorRemovable_cf64345__12;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Attribute vc_16500 = (org.jsoup.nodes.Attribute)null;
            // StatementAdderOnAssert create random local variable
            org.jsoup.nodes.Attributes vc_16499 = new org.jsoup.nodes.Attributes();
            // StatementAdderMethod cloned existing statement
            vc_16499.put(vc_16500);
            // MethodAssertGenerator build local variable
            Object o_24_0 = a.size();
            org.junit.Assert.fail("testIteratorRemovable_cf64345_cf66577 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64351_failAssert22_literalMutation67937_cf69503_failAssert0() {
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
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_1811 = "";
                // MethodAssertGenerator build local variable
                Object o_14_0 = String_vc_1811;
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attributes vc_15685 = (org.jsoup.nodes.Attributes)null;
                // StatementAdderOnAssert create random local variable
                org.jsoup.nodes.Attributes vc_16791 = new org.jsoup.nodes.Attributes();
                // StatementAdderMethod cloned existing statement
                vc_16791.getIgnoreCase(String_vc_1811);
                // MethodAssertGenerator build local variable
                Object o_22_0 = vc_15685;
                // StatementAdderMethod cloned existing statement
                vc_15685.getIgnoreCase(String_vc_1811);
                // MethodAssertGenerator build local variable
                Object o_16_0 = a.size();
                org.junit.Assert.fail("testIteratorRemovable_cf64351 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testIteratorRemovable_cf64351_failAssert22_literalMutation67937_cf69503 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64338_failAssert13_add67577_cf69927_failAssert1() {
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
                org.jsoup.nodes.Attributes vc_15679 = (org.jsoup.nodes.Attributes)null;
                // StatementAdderOnAssert create null value
                java.lang.String vc_16963 = (java.lang.String)null;
                // StatementAdderMethod cloned existing statement
                a.removeIgnoreCase(vc_16963);
                // MethodAssertGenerator build local variable
                Object o_18_0 = vc_15679;
                // StatementAdderMethod cloned existing statement
                // MethodCallAdder
                vc_15679.size();
                // StatementAdderMethod cloned existing statement
                vc_15679.size();
                // MethodAssertGenerator build local variable
                Object o_14_0 = a.size();
                org.junit.Assert.fail("testIteratorRemovable_cf64338 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testIteratorRemovable_cf64338_failAssert13_add67577_cf69927 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64354_cf66767_failAssert41_literalMutation71429() {
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
            java.lang.String String_vc_1811 = "dataqname";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_1811, "dataqname");
            // MethodAssertGenerator build local variable
            Object o_12_0 = String_vc_1811;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_12_0, "dataqname");
            // AssertGenerator replace invocation
            java.lang.String o_testIteratorRemovable_cf64354__12 = // StatementAdderMethod cloned existing statement
a.getIgnoreCase(String_vc_1811);
            // MethodAssertGenerator build local variable
            Object o_16_0 = o_testIteratorRemovable_cf64354__12;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_16_0, "");
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Attributes vc_16567 = (org.jsoup.nodes.Attributes)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_16567);
            // StatementAdderMethod cloned existing statement
            vc_16567.remove(String_vc_1811);
            // MethodAssertGenerator build local variable
            Object o_22_0 = a.size();
            org.junit.Assert.fail("testIteratorRemovable_cf64354_cf66767 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64331_cf65864_failAssert54_literalMutation71884_failAssert12() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
                a.put("Tot", "a&p");
                a.put("Hello", "There");
                a.put("", "Jsoup");
                java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
                iterator.next();
                iterator.remove();
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_15676 = new java.lang.String();
                // MethodAssertGenerator build local variable
                Object o_12_0 = vc_15676;
                // AssertGenerator replace invocation
                boolean o_testIteratorRemovable_cf64331__12 = // StatementAdderMethod cloned existing statement
a.hasKeyIgnoreCase(vc_15676);
                // MethodAssertGenerator build local variable
                Object o_16_0 = o_testIteratorRemovable_cf64331__12;
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attributes vc_16215 = (org.jsoup.nodes.Attributes)null;
                // StatementAdderMethod cloned existing statement
                vc_16215.asList();
                // MethodAssertGenerator build local variable
                Object o_22_0 = a.size();
                org.junit.Assert.fail("testIteratorRemovable_cf64331_cf65864 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testIteratorRemovable_cf64331_cf65864_failAssert54_literalMutation71884 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_cf64351_failAssert22_literalMutation67937_cf69576_failAssert4() {
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
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_1811 = "";
                // MethodAssertGenerator build local variable
                Object o_14_0 = String_vc_1811;
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Attributes vc_15685 = (org.jsoup.nodes.Attributes)null;
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_16830 = new java.lang.String();
                // StatementAdderMethod cloned existing statement
                a.remove(vc_16830);
                // MethodAssertGenerator build local variable
                Object o_22_0 = vc_15685;
                // StatementAdderMethod cloned existing statement
                vc_15685.getIgnoreCase(String_vc_1811);
                // MethodAssertGenerator build local variable
                Object o_16_0 = a.size();
                org.junit.Assert.fail("testIteratorRemovable_cf64351 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testIteratorRemovable_cf64351_failAssert22_literalMutation67937_cf69576 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }
}

