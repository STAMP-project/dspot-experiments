package fj.data.test;


import fj.test.CheckResult;
import fj.test.Gen;
import fj.test.Property;
import org.junit.Assert;
import org.junit.Test;


public class TestCheck {
    @Test(timeout = 5000/* ms */
    )
    public void testExceptionsThrownFromGeneratorsArePropagated() {
        Gen<Integer> failingGen = Gen.value(0).map(( i) -> {
            throw new RuntimeException("test failure");
        });
        Property p = Property.property(failingGen, (Integer i) -> prop((i == 0)));
        CheckResult res = /* minSuccessful */
        /* maxDiscarded */
        /* minSize */
        /* maxSize */
        p.check(1, 0, 0, 1);
        Assert.assertTrue("Exception not propagated!", res.isGenException());
    }
}

