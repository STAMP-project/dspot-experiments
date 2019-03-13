package com.bumptech.glide.load.engine;


import com.bumptech.glide.load.Key;
import com.bumptech.glide.tests.KeyTester;
import java.security.NoSuchAlgorithmException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;


@RunWith(JUnit4.class)
public class DataCacheKeyTest {
    @Rule
    public final KeyTester keyTester = new KeyTester();

    @Mock
    private Key firstKey;

    @Mock
    private Key firstSignature;

    @Mock
    private Key secondKey;

    @Mock
    private Key secondSignature;

    @Test
    public void testEqualsHashCodeDigest() throws NoSuchAlgorithmException {
        keyTester.addEquivalenceGroup(new DataCacheKey(firstKey, firstSignature), new DataCacheKey(firstKey, firstSignature)).addEquivalenceGroup(new DataCacheKey(firstKey, secondSignature)).addEquivalenceGroup(new DataCacheKey(secondKey, firstSignature)).addEquivalenceGroup(new DataCacheKey(secondKey, secondSignature)).addRegressionTest(new DataCacheKey(firstKey, firstSignature), "801d7440d65a0e7c9ad0097d417f346dac4d4c4d5630724110fa3f3fe66236d9").test();
    }
}

