package com.baeldung.java9.language;


import org.junit.Test;


public class PrivateInterfaceUnitTest {
    @Test
    public void test() {
        PrivateInterface piClass = new PrivateInterface() {};
        piClass.check();
    }
}

