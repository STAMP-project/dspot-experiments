package com.baeldung.migration.junit5;


import java.util.function.Supplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class AssumptionUnitTest {
    @Test
    public void trueAssumption() {
        Assumptions.assumeTrue((5 > 1), () -> "5 is greater the 1");
        Assertions.assertEquals((5 + 2), 7);
    }

    @Test
    public void falseAssumption() {
        Assumptions.assumeFalse((5 < 1), () -> "5 is less then 1");
        Assertions.assertEquals((5 + 2), 7);
    }

    @Test
    public void assumptionThat() {
        String someString = "Just a string";
        Assumptions.assumingThat(someString.equals("Just a string"), () -> Assertions.assertEquals((2 + 2), 4));
    }
}

