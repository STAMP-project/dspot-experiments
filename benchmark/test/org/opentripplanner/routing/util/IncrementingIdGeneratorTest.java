package org.opentripplanner.routing.util;


import org.junit.Assert;
import org.junit.Test;


public class IncrementingIdGeneratorTest {
    @Test
    public void testConstruct() {
        UniqueIdGenerator<String> gen = new IncrementingIdGenerator<String>();
        Assert.assertEquals(0, gen.getId(""));
        Assert.assertEquals(1, gen.getId("fake"));
        Assert.assertEquals(2, gen.getId("foo"));
    }

    @Test
    public void testConstructWithStart() {
        int start = 102;
        UniqueIdGenerator<String> gen = new IncrementingIdGenerator<String>(start);
        Assert.assertEquals(start, gen.getId(""));
        Assert.assertEquals((start + 1), gen.getId("fake"));
        Assert.assertEquals((start + 2), gen.getId("foo"));
    }
}

