package com.baeldung.markerinterface;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class MarkerInterfaceUnitTest {
    @Test
    public void givenDeletableObjectThenTrueReturned() {
        ShapeDao shapeDao = new ShapeDao();
        Object rectangle = new Rectangle(2, 3);
        boolean result = shapeDao.delete(rectangle);
        Assertions.assertEquals(true, result);
    }

    @Test
    public void givenNonDeletableObjectThenFalseReturned() {
        ShapeDao shapeDao = new ShapeDao();
        Object object = new Object();
        boolean result = shapeDao.delete(object);
        Assertions.assertEquals(false, result);
    }
}

