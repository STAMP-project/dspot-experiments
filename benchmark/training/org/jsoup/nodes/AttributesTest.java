package org.jsoup.nodes;


import java.util.Iterator;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for Attributes.
 *
 * @author Jonathan Hedley
 */
public class AttributesTest {
    @Test
    public void html() {
        Attributes a = new Attributes();
        a.put("Tot", "a&p");
        a.put("Hello", "There");
        a.put("data-name", "Jsoup");
        Assert.assertEquals(3, a.size());
        Assert.assertTrue(a.hasKey("Tot"));
        Assert.assertTrue(a.hasKey("Hello"));
        Assert.assertTrue(a.hasKey("data-name"));
        Assert.assertFalse(a.hasKey("tot"));
        Assert.assertTrue(a.hasKeyIgnoreCase("tot"));
        Assert.assertEquals("There", a.getIgnoreCase("hEllo"));
        Map<String, String> dataset = a.dataset();
        Assert.assertEquals(1, dataset.size());
        Assert.assertEquals("Jsoup", dataset.get("name"));
        Assert.assertEquals("", a.get("tot"));
        Assert.assertEquals("a&p", a.get("Tot"));
        Assert.assertEquals("a&p", a.getIgnoreCase("tot"));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", a.html());
        Assert.assertEquals(a.html(), a.toString());
    }

    @Test
    public void testIteratorRemovable() {
        Attributes a = new Attributes();
        a.put("Tot", "a&p");
        a.put("Hello", "There");
        a.put("data-name", "Jsoup");
        Assert.assertTrue(a.hasKey("Tot"));
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot", attr.getKey());
        iterator.remove();
        Assert.assertEquals(2, a.size());
        attr = iterator.next();
        Assert.assertEquals("Hello", attr.getKey());
        Assert.assertEquals("There", attr.getValue());
        // make sure that's flowing to the underlying attributes object
        Assert.assertEquals(2, a.size());
        Assert.assertEquals("There", a.get("Hello"));
        Assert.assertFalse(a.hasKey("Tot"));
    }

    @Test
    public void testIteratorUpdateable() {
        Attributes a = new Attributes();
        a.put("Tot", "a&p");
        a.put("Hello", "There");
        Assert.assertFalse(a.hasKey("Foo"));
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        attr.setKey("Foo");
        attr = iterator.next();
        attr.setKey("Bar");
        attr.setValue("Qux");
        Assert.assertEquals("a&p", a.get("Foo"));
        Assert.assertEquals("Qux", a.get("Bar"));
        Assert.assertFalse(a.hasKey("Tot"));
        Assert.assertFalse(a.hasKey("Hello"));
    }

    @Test
    public void testIteratorHasNext() {
        Attributes a = new Attributes();
        a.put("Tot", "1");
        a.put("Hello", "2");
        a.put("data-name", "3");
        int seen = 0;
        for (Attribute attribute : a) {
            seen++;
            Assert.assertEquals(String.valueOf(seen), attribute.getValue());
        }
        Assert.assertEquals(3, seen);
    }

    @Test
    public void testIterator() {
        Attributes a = new Attributes();
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        Assert.assertTrue(iterator.hasNext());
        int i = 0;
        for (Attribute attribute : a) {
            Assert.assertEquals(datas[i][0], attribute.getKey());
            Assert.assertEquals(datas[i][1], attribute.getValue());
            i++;
        }
        Assert.assertEquals(datas.length, i);
    }

    @Test
    public void testIteratorEmpty() {
        Attributes a = new Attributes();
        Iterator<Attribute> iterator = a.iterator();
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void removeCaseSensitive() {
        Attributes a = new Attributes();
        a.put("Tot", "a&p");
        a.put("tot", "one");
        a.put("Hello", "There");
        a.put("hello", "There");
        a.put("data-name", "Jsoup");
        Assert.assertEquals(5, a.size());
        a.remove("Tot");
        a.remove("Hello");
        Assert.assertEquals(3, a.size());
        Assert.assertTrue(a.hasKey("tot"));
        Assert.assertFalse(a.hasKey("Tot"));
    }

    @Test
    public void testSetKeyConsistency() {
        Attributes a = new Attributes();
        a.put("a", "a");
        for (Attribute at : a) {
            at.setKey("b");
        }
        Assert.assertFalse("Attribute 'a' not correctly removed", a.hasKey("a"));
        Assert.assertTrue("Attribute 'b' not present after renaming", a.hasKey("b"));
    }
}

