package com.bumptech.glide.signature;


import com.bumptech.glide.load.Key;
import com.bumptech.glide.tests.KeyTester;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class EmptySignatureTest {
    @Rule
    public final KeyTester keyTester = new KeyTester();

    @Test
    public void testEquals() {
        keyTester.addEquivalenceGroup(EmptySignature.obtain(), EmptySignature.obtain()).addEquivalenceGroup(Mockito.mock(Key.class)).addEmptyDigestRegressionTest(EmptySignature.obtain()).test();
    }
}

