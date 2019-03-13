/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baeldung.enums.values;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author chris
 */
public class Element3UnitTest {
    public Element3UnitTest() {
    }

    @Test
    public void whenLocatebyLabel_thenReturnCorrectValue() {
        for (Element3 e3 : Element3.values()) {
            Assert.assertSame(e3, Element3.valueOfLabel(e3.label));
        }
    }

    /**
     * Test of toString method, of class Element3.
     */
    @Test
    public void whenCallingToString_thenReturnLabel() {
        for (Element3 e3 : Element3.values()) {
            Assert.assertEquals(e3.label, e3.toString());
        }
    }
}

