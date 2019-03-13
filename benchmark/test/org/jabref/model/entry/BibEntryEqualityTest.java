package org.jabref.model.entry;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class BibEntryEqualityTest {
    @Test
    public void identicObjectsareEqual() throws Exception {
        BibEntry e1 = new BibEntry();
        BibEntry e2 = e1;
        Assertions.assertTrue(e1.equals(e2));
    }

    @Test
    public void compareToNullObjectIsFalse() throws Exception {
        BibEntry e1 = new BibEntry();
        Assertions.assertFalse(e1.equals(null));
    }

    @Test
    public void compareToDifferentClassIsFalse() throws Exception {
        BibEntry e1 = new BibEntry();
        Object e2 = new Object();
        Assertions.assertFalse(e1.equals(e2));
    }

    @Test
    public void compareIsTrueWhenIdAndFieldsAreEqual() throws Exception {
        BibEntry e1 = new BibEntry();
        e1.setId("1");
        e1.setField("key", "value");
        BibEntry e2 = new BibEntry();
        e2.setId("1");
        Assertions.assertFalse(e1.equals(e2));
        e2.setField("key", "value");
        Assertions.assertTrue(e1.equals(e2));
    }
}

