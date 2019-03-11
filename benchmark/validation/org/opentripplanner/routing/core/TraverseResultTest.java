package org.opentripplanner.routing.core;


import org.junit.Assert;
import org.junit.Test;


public class TraverseResultTest {
    @Test
    public void testAddToExistingResultChain() {
        State resultChain = null;
        /* note: times are rounded to seconds toward zero */
        for (int i = 0; i < 4; i++) {
            State r = new State(null, (i * 1000), new RoutingRequest());
            resultChain = r.addToExistingResultChain(resultChain);
        }
        Assert.assertEquals(3000, resultChain.getTimeSeconds(), 0.0);
        resultChain = resultChain.getNextResult();
        Assert.assertEquals(2000, resultChain.getTimeSeconds(), 0.0);
        resultChain = resultChain.getNextResult();
        Assert.assertEquals(1000, resultChain.getTimeSeconds(), 0.0);
        resultChain = resultChain.getNextResult();
        Assert.assertEquals(0, resultChain.getTimeSeconds(), 0.0);
        resultChain = resultChain.getNextResult();
        Assert.assertNull(resultChain);
    }
}

