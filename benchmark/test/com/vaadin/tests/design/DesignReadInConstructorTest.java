package com.vaadin.tests.design;


import org.junit.Assert;
import org.junit.Test;


public class DesignReadInConstructorTest {
    @Test
    public void useDesignReadInConstructor() {
        DesignReadInConstructor dric = new DesignReadInConstructor();
        Assert.assertEquals(3, getComponentCount());
    }
}

