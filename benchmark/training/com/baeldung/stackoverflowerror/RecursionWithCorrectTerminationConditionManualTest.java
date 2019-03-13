package com.baeldung.stackoverflowerror;


import junit.framework.TestCase;
import org.junit.Test;


public class RecursionWithCorrectTerminationConditionManualTest {
    @Test
    public void givenNegativeInt_whenCalcFact_thenCorrectlyCalc() {
        int numToCalcFactorial = -1;
        RecursionWithCorrectTerminationCondition rctc = new RecursionWithCorrectTerminationCondition();
        TestCase.assertEquals(1, rctc.calculateFactorial(numToCalcFactorial));
    }
}

