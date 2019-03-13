package com.baeldung.java9.language;


import org.junit.Test;


public class DiamondUnitTest {
    static class FooClass<X> {
        FooClass(X x) {
        }

        <Z> FooClass(X x, Z z) {
        }
    }

    @Test
    public void diamondTest() {
        DiamondUnitTest.FooClass<Integer> fc = new DiamondUnitTest.FooClass(1) {};
        DiamondUnitTest.FooClass<? extends Integer> fc0 = new DiamondUnitTest.FooClass(1) {};
        DiamondUnitTest.FooClass<?> fc1 = new DiamondUnitTest.FooClass(1) {};
        DiamondUnitTest.FooClass<? super Integer> fc2 = new DiamondUnitTest.FooClass(1) {};
        DiamondUnitTest.FooClass<Integer> fc3 = new DiamondUnitTest.FooClass(1, "") {};
        DiamondUnitTest.FooClass<? extends Integer> fc4 = new DiamondUnitTest.FooClass(1, "") {};
        DiamondUnitTest.FooClass<?> fc5 = new DiamondUnitTest.FooClass(1, "") {};
        DiamondUnitTest.FooClass<? super Integer> fc6 = new DiamondUnitTest.FooClass(1, "") {};
    }
}

