package com.baeldung.neuroph;


import org.junit.Assert;
import org.junit.Test;
import org.neuroph.core.NeuralNetwork;


public class XORIntegrationTest {
    private NeuralNetwork ann = null;

    @Test
    public void leftDisjunctTest() {
        ann.setInput(0, 1);
        ann.calculate();
        print("0, 1", ann.getOutput()[0], 1.0);
        Assert.assertEquals(ann.getOutput()[0], 1.0, 0.0);
    }

    @Test
    public void rightDisjunctTest() {
        ann.setInput(1, 0);
        ann.calculate();
        print("1, 0", ann.getOutput()[0], 1.0);
        Assert.assertEquals(ann.getOutput()[0], 1.0, 0.0);
    }

    @Test
    public void bothFalseConjunctTest() {
        ann.setInput(0, 0);
        ann.calculate();
        print("0, 0", ann.getOutput()[0], 0.0);
        Assert.assertEquals(ann.getOutput()[0], 0.0, 0.0);
    }

    @Test
    public void bothTrueConjunctTest() {
        ann.setInput(1, 1);
        ann.calculate();
        print("1, 1", ann.getOutput()[0], 0.0);
        Assert.assertEquals(ann.getOutput()[0], 0.0, 0.0);
    }
}

