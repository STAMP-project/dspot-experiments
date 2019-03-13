package com.baeldung;


import com.baeldung.bridge.Blue;
import com.baeldung.bridge.Red;
import com.baeldung.bridge.Shape;
import org.junit.Assert;
import org.junit.Test;


public class BridgePatternIntegrationTest {
    @Test
    public void whenBridgePatternInvoked_thenConfigSuccess() {
        // a square with red color
        Shape square = new com.baeldung.bridge.Square(new Red());
        Assert.assertEquals(square.draw(), "Square drawn. Color is Red");
        // a triangle with blue color
        Shape triangle = new com.baeldung.bridge.Triangle(new Blue());
        Assert.assertEquals(triangle.draw(), "Triangle drawn. Color is Blue");
    }
}

