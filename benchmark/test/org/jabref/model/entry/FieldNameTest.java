package org.jabref.model.entry;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class FieldNameTest {
    @Test
    public void testOrFieldsTwoTerms() {
        Assertions.assertEquals("aaa/bbb", FieldName.orFields("aaa", "bbb"));
    }

    @Test
    public void testOrFieldsThreeTerms() {
        Assertions.assertEquals("aaa/bbb/ccc", FieldName.orFields("aaa", "bbb", "ccc"));
    }
}

