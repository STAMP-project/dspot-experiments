package com.fishercoder;


import com.fishercoder.solutions._649;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/8/17.
 */
public class _649Test {
    private static _649 test;

    @Test
    public void test1() {
        Assert.assertEquals("Dire", _649Test.test.predictPartyVictory("RDDDR"));
    }

    @Test
    public void test2() {
        Assert.assertEquals("Radiant", _649Test.test.predictPartyVictory("RD"));
    }

    @Test
    public void test3() {
        Assert.assertEquals("Dire", _649Test.test.predictPartyVictory("RDD"));
    }

    @Test
    public void test4() {
        Assert.assertEquals("Radiant", _649Test.test.predictPartyVictory("RDDR"));
    }

    @Test
    public void test5() {
        Assert.assertEquals("Dire", _649Test.test.predictPartyVictory("RDDRD"));
    }

    @Test
    public void test6() {
        Assert.assertEquals("Dire", _649Test.test.predictPartyVictory("RDDDDDRR"));
    }

    @Test
    public void test7() {
        Assert.assertEquals("Dire", _649Test.test.predictPartyVictory("RDDDDRR"));
    }
}

