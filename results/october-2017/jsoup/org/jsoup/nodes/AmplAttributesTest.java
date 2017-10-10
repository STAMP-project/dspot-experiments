

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

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_sd5432_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.lang.String[][] datas = new java.lang.String[][]{ new java.lang.String[]{ "Tot" , "raul" } , new java.lang.String[]{ "Hello" , "pismuth" } , new java.lang.String[]{ "data-name" , "Jsoup" } };
            for (java.lang.String[] atts : datas) {
                a.put(atts[0], atts[1]);
            }
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            iterator.hasNext();
            int i = 0;
            for (org.jsoup.nodes.Attribute attribute : a) {
                java.lang.String String_184 = datas[i][0];
                attribute.getKey();
                java.lang.String String_185 = datas[i][1];
                attribute.getValue();
                i++;
            }
            int int_186 = datas.length;
            int int_187 = i;
            // StatementAdd: add invocation of a method
            a.toString();
            org.junit.Assert.fail("testIterator_sd5432 should have thrown ExceptionInInitializerError");
        } catch (java.lang.ExceptionInInitializerError eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_literalMutationNumber5426_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.lang.String[][] datas = new java.lang.String[][]{ new java.lang.String[]{ "Tot" , "raul" } , new java.lang.String[]{ "Hello" , "pismuth" } , new java.lang.String[]{ "data-name" , "Jsoup" } };
            for (java.lang.String[] atts : datas) {
                a.put(atts[0], atts[1]);
            }
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            iterator.hasNext();
            int i = 1;
            for (org.jsoup.nodes.Attribute attribute : a) {
                java.lang.String String_164 = datas[i][0];
                attribute.getKey();
                java.lang.String String_165 = datas[i][1];
                attribute.getValue();
                i++;
            }
            int int_166 = datas.length;
            int int_167 = i;
            org.junit.Assert.fail("testIterator_literalMutationNumber5426 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_literalMutationNumber5427_failAssert6() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.lang.String[][] datas = new java.lang.String[][]{ new java.lang.String[]{ "Tot" , "raul" } , new java.lang.String[]{ "Hello" , "pismuth" } , new java.lang.String[]{ "data-name" , "Jsoup" } };
            for (java.lang.String[] atts : datas) {
                a.put(atts[0], atts[1]);
            }
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            iterator.hasNext();
            int i = -1;
            for (org.jsoup.nodes.Attribute attribute : a) {
                java.lang.String String_168 = datas[i][0];
                attribute.getKey();
                java.lang.String String_169 = datas[i][1];
                attribute.getValue();
                i++;
            }
            int int_170 = datas.length;
            int int_171 = i;
            org.junit.Assert.fail("testIterator_literalMutationNumber5427 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_literalMutationString5383_failAssert0() {
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
            int int_6 = datas.length;
            int int_7 = i;
            org.junit.Assert.fail("testIterator_literalMutationString5383 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_literalMutationString5394_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.lang.String[][] datas = new java.lang.String[][]{ new java.lang.String[]{ "Tot" , "raul" } , new java.lang.String[]{ "" , "pismuth" } , new java.lang.String[]{ "data-name" , "Jsoup" } };
            for (java.lang.String[] atts : datas) {
                a.put(atts[0], atts[1]);
            }
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            iterator.hasNext();
            int i = 0;
            for (org.jsoup.nodes.Attribute attribute : a) {
                java.lang.String String_48 = datas[i][0];
                attribute.getKey();
                java.lang.String String_49 = datas[i][1];
                attribute.getValue();
                i++;
            }
            int int_50 = datas.length;
            int int_51 = i;
            org.junit.Assert.fail("testIterator_literalMutationString5394 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIterator */
    @org.junit.Test(timeout = 10000)
    public void testIterator_sd5448_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.lang.String[][] datas = new java.lang.String[][]{ new java.lang.String[]{ "Tot" , "raul" } , new java.lang.String[]{ "Hello" , "pismuth" } , new java.lang.String[]{ "data-name" , "Jsoup" } };
            for (java.lang.String[] atts : datas) {
                a.put(atts[0], atts[1]);
            }
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            iterator.hasNext();
            int i = 0;
            for (org.jsoup.nodes.Attribute attribute : a) {
                java.lang.String String_248 = datas[i][0];
                attribute.getKey();
                java.lang.String String_249 = datas[i][1];
                attribute.getValue();
                i++;
            }
            int int_250 = datas.length;
            int int_251 = i;
            // StatementAdd: add invocation of a method
            a.html();
            org.junit.Assert.fail("testIterator_sd5448 should have thrown NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_sd9732_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            iterator.hasNext();
            // StatementAdd: add invocation of a method
            a.html();
            org.junit.Assert.fail("testIteratorEmpty_sd9732 should have thrown NoClassDefFoundError");
        } catch (java.lang.NoClassDefFoundError eee) {
        }
    }

    /* amplification of org.jsoup.nodes.AttributesTest#testIteratorEmpty */
    @org.junit.Test(timeout = 10000)
    public void testIteratorEmpty_sd9716_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Attributes a = new org.jsoup.nodes.Attributes();
            java.util.Iterator<org.jsoup.nodes.Attribute> iterator = a.iterator();
            iterator.hasNext();
            // StatementAdd: add invocation of a method
            a.toString();
            org.junit.Assert.fail("testIteratorEmpty_sd9716 should have thrown ExceptionInInitializerError");
        } catch (java.lang.ExceptionInInitializerError eee) {
        }
    }
}

