package com.baeldung.enums.values;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author chris
 */
public class Element1UnitTest {
    public Element1UnitTest() {
    }

    @Test
    public void whenAccessingToString_thenItShouldEqualName() {
        for (Element1 e1 : Element1.values()) {
            Assert.assertEquals(e1.name(), e1.toString());
        }
    }

    @Test
    public void whenCallingValueOf_thenReturnTheCorrectEnum() {
        for (Element1 e1 : Element1.values()) {
            Assert.assertSame(e1, Element1.valueOf(e1.name()));
        }
    }
}

