package org.drools.core.base.accumulators;


import org.drools.core.base.accumulators.VarianceAccumulateFunction.VarianceData;
import org.junit.Assert;
import org.junit.Test;


public class VarianceNaNTest {
    @Test
    public void shouldNotProduceNaNAfterBackout() {
        VarianceAccumulateFunction varianceAccumulateFunction = new VarianceAccumulateFunction();
        VarianceData data = varianceAccumulateFunction.createContext();
        varianceAccumulateFunction.init(data);
        // Before being initialized result is NaN.
        Assert.assertEquals(Double.NaN, varianceAccumulateFunction.getResult(data), 0);
        Double value = 1.5;
        // With single value variance should be 0
        varianceAccumulateFunction.accumulate(data, value);
        Assert.assertEquals(0.0, varianceAccumulateFunction.getResult(data), 0.001);
        // should be back to NaN after backout
        varianceAccumulateFunction.reverse(data, value);
        Assert.assertEquals(Double.NaN, varianceAccumulateFunction.getResult(data), 0);
        // should be zero after adding number back
        varianceAccumulateFunction.accumulate(data, value);
        Assert.assertEquals(0.0, varianceAccumulateFunction.getResult(data), 0.001);
    }
}

