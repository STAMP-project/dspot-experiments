package com.baeldung.algorithms.editdistance;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class EditDistanceUnitTest extends EditDistanceDataProvider {
    private String x;

    private String y;

    private int result;

    public EditDistanceUnitTest(String a, String b, int res) {
        super();
        x = a;
        y = b;
        result = res;
    }

    @Test
    public void testEditDistance_RecursiveImplementation() {
        Assert.assertEquals(result, EditDistanceRecursive.calculate(x, y));
    }

    @Test
    public void testEditDistance_givenDynamicProgrammingImplementation() {
        Assert.assertEquals(result, EditDistanceDynamicProgramming.calculate(x, y));
    }
}

