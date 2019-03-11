package com.fishercoder;


import _362.Solution1.HitCounter;
import org.junit.Assert;
import org.junit.Test;


public class _362Test {
    private static HitCounter hitCounter;

    @Test
    public void test1() {
        _362Test.hitCounter.hit(1);
        _362Test.hitCounter.hit(2);
        _362Test.hitCounter.hit(3);
        Assert.assertEquals(3, _362Test.hitCounter.getHits(4));
        _362Test.hitCounter.hit(300);
        Assert.assertEquals(4, _362Test.hitCounter.getHits(300));
        Assert.assertEquals(3, _362Test.hitCounter.getHits(301));
        _362Test.hitCounter.hit(301);
        Assert.assertEquals(4, _362Test.hitCounter.getHits(300));
    }
}

