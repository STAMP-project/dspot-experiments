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
public class Element4UnitTest {
    public Element4UnitTest() {
    }

    @Test
    public void whenLocatebyLabel_thenReturnCorrectValue() {
        for (Element4 e4 : Element4.values()) {
            Assert.assertSame(e4, Element4.valueOfLabel(e4.label));
        }
    }

    @Test
    public void whenLocatebyAtmNum_thenReturnCorrectValue() {
        for (Element4 e4 : Element4.values()) {
            Assert.assertSame(e4, Element4.valueOfAtomicNumber(e4.atomicNumber));
        }
    }

    @Test
    public void whenLocatebyAtmWt_thenReturnCorrectValue() {
        for (Element4 e4 : Element4.values()) {
            Assert.assertSame(e4, Element4.valueOfAtomicWeight(e4.atomicWeight));
        }
    }

    /**
     * Test of toString method, of class Element4.
     */
    @Test
    public void whenCallingToString_thenReturnLabel() {
        for (Element4 e4 : Element4.values()) {
            Assert.assertEquals(e4.label, e4.toString());
        }
    }
}

