package com.baeldung.inheritance;


import junit.framework.TestCase;


public class AppUnitTest extends TestCase {
    public AppUnitTest(String testName) {
        super(testName);
    }

    public void testAssignArmoredCarToCar() {
        Employee e1 = new Employee("Shreya", new ArmoredCar());
        TestCase.assertNotNull(e1.getCar());
    }

    public void testAssignSpaceCarToCar() {
        Employee e2 = new Employee("Paul", new SpaceCar());
        TestCase.assertNotNull(e2.getCar());
    }

    public void testBMWToCar() {
        Employee e3 = new Employee("Pavni", new BMW());
        TestCase.assertNotNull(e3.getCar());
    }
}

