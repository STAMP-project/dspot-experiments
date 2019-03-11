package com.iota.iri.crypto;


import org.junit.Assert;
import org.junit.Test;

import static Curl.HASH_LENGTH;


public class PearlDiverTest {
    private static final int TRYTE_LENGTH = 2673;

    private static final int MIN_WEIGHT_MAGNITUDE = 9;

    private static final int NUM_CORES = -1;// use n-1 cores


    private PearlDiver pearlDiver;

    private byte[] hashTrits;

    @Test
    public void testRandomTryteHash() {
        String hash = getHashFor(getRandomTrytes());
        boolean success = isAllNines(hash.substring((((HASH_LENGTH) / 3) - ((PearlDiverTest.MIN_WEIGHT_MAGNITUDE) / 3))));
        Assert.assertTrue("The hash should have n nines", success);
    }

    @Test
    public void testCancel() {
        pearlDiver.cancel();
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidMagnitude() {
        pearlDiver.search(new byte[8019], (-1), PearlDiverTest.NUM_CORES);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidTritsLength() {
        pearlDiver.search(new byte[0], PearlDiverTest.MIN_WEIGHT_MAGNITUDE, PearlDiverTest.NUM_CORES);
    }
}

