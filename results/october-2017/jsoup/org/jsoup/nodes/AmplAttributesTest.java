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
    public void html_literalMutationString68_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("Hello", "There");
            a.put("data-name", "Jsoup");
            a.size();
            a.hasKey("Tot");
            a.hasKey("Hello");
            a.hasKey("data-name");
            a.hasKey("tot");
            a.hasKeyIgnoreCase("tot");
            a.getIgnoreCase("");
            a.dataset().size();
            a.dataset().get("name");
            a.get("tot");
            a.get("Tot");
            a.getIgnoreCase("tot");
            a.html();
            a.html();
            a.toString();
            org.junit.Assert.fail("html_literalMutationString68 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#html */
    @org.junit.Test(timeout = 10000)
    public void html_literalMutationString25_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("Hello", "There");
            a.put("", "Jsoup");
            a.size();
            a.hasKey("Tot");
            a.hasKey("Hello");
            a.hasKey("data-name");
            a.hasKey("tot");
            a.hasKeyIgnoreCase("tot");
            a.getIgnoreCase("hEllo");
            a.dataset().size();
            a.dataset().get("name");
            a.get("tot");
            a.get("Tot");
            a.getIgnoreCase("tot");
            a.html();
            a.html();
            a.toString();
            org.junit.Assert.fail("html_literalMutationString25 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#html */
    @org.junit.Test(timeout = 10000)
    public void html_literalMutationString86_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("Hello", "There");
            a.put("data-name", "Jsoup");
            a.size();
            a.hasKey("Tot");
            a.hasKey("Hello");
            a.hasKey("data-name");
            a.hasKey("tot");
            a.hasKeyIgnoreCase("tot");
            a.getIgnoreCase("hEllo");
            a.dataset().size();
            a.dataset().get("name");
            a.get("tot");
            a.get("");
            a.getIgnoreCase("tot");
            a.html();
            a.html();
            a.toString();
            org.junit.Assert.fail("html_literalMutationString86 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#removeCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void removeCaseSensitive_literalMutationString2411_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("tot", "one");
            a.put("", "There");
            a.put("hello", "There");
            a.put("data-name", "Jsoup");
            a.size();
            a.remove("Tot");
            a.remove("Hello");
            a.size();
            a.hasKey("tot");
            a.hasKey("Tot");
            org.junit.Assert.fail("removeCaseSensitive_literalMutationString2411 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#removeCaseSensitive */
    @org.junit.Test(timeout = 10000)
    public void removeCaseSensitive_literalMutationString2449_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("tot", "one");
            a.put("Hello", "There");
            a.put("hello", "There");
            a.put("data-name", "Jsoup");
            a.size();
            a.remove("");
            a.remove("Hello");
            a.size();
            a.hasKey("tot");
            a.hasKey("Tot");
            org.junit.Assert.fail("removeCaseSensitive_literalMutationString2449 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_literalMutationString4600_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.lang.String[][] datas = new java.lang.String[][]{ new java.lang.String[]{ "" , "raul" } , new java.lang.String[]{ "Hello" , "pismuth" } , new java.lang.String[]{ "data-name" , "Jsoup" } };
            for (java.lang.String[] atts : datas) {
                a.put(atts[0], atts[1]);
            }
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            iterator.hasNext();
            int i = 0;
            for (org.jsoup.nodes.Attribute attribute : a) {
                java.lang.String String_4 = datas[i][0];
                attribute.getKey();
                java.lang.String String_5 = datas[i][1];
                attribute.getValue();
                i++;
            }
            org.junit.Assert.fail("testIterator_literalMutationString4600 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    /* amplification of testIteratorEmpty_sd7049 */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_sd7049_literalMutationString7282_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_key_619 = "";
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            iterator.hasNext();
            // StatementAdd: add invocation of a method
            a.removeIgnoreCase(__DSPOT_key_619);
            org.junit.Assert.fail("testIteratorEmpty_sd7049_literalMutationString7282 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    /* amplification of testIteratorEmpty_sd7044 */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_sd7044_literalMutationString7155_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_key_616 = "";
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            iterator.hasNext();
            // StatementAdd: add invocation of a method
            a.remove(__DSPOT_key_616);
            org.junit.Assert.fail("testIteratorEmpty_sd7044_literalMutationString7155 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorRemovable */
    @org.junit.Test(timeout = 10000)
    public void testIteratorRemovable_literalMutationString8118_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            a.put("Tot", "a&p");
            a.put("", "There");
            a.put("data-name", "Jsoup");
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            iterator.next();
            iterator.remove();
            a.size();
            org.junit.Assert.fail("testIteratorRemovable_literalMutationString8118 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }
}

