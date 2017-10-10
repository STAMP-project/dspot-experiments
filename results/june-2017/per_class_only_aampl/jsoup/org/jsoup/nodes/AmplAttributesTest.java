

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
}

