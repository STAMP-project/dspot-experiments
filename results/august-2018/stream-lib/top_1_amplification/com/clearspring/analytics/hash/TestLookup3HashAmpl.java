package com.clearspring.analytics.hash;


import org.junit.Assert;


public class TestLookup3HashAmpl {
    void tstEquiv(int[] utf32, int len) {
        int seed = 100;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.appendCodePoint(utf32[i]);
        }
        int hash = Lookup3Hash.lookup3(utf32, 0, len, (seed - (len << 2)));
        int hash2 = Lookup3Hash.lookup3ycs(utf32, 0, len, seed);
        Assert.assertEquals(hash, hash2);
        int hash3 = Lookup3Hash.lookup3ycs(sb, 0, sb.length(), seed);
        Assert.assertEquals(hash, hash3);
        long hash4 = Lookup3Hash.lookup3ycs64(sb, 0, sb.length(), seed);
        Assert.assertEquals(((int) (hash4)), hash);
    }
}

