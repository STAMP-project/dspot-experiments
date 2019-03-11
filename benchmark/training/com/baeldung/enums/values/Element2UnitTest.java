/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baeldung.enums.values;


import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author chris
 */
public class Element2UnitTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(Element2UnitTest.class);

    public Element2UnitTest() {
    }

    @Test
    public void whenLocatebyLabel_thenReturnCorrectValue() {
        for (Element2 e2 : Element2.values()) {
            Assert.assertSame(e2, Element2.valueOfLabel(e2.label));
        }
    }

    /**
     * Test of toString method, of class Element2.
     */
    @Test
    public void whenCallingToString_thenReturnLabel() {
        for (Element2 e2 : Element2.values()) {
            Assert.assertEquals(e2.label, e2.toString());
        }
    }
}

