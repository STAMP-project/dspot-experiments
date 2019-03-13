package com.tencent.angel.decay;


import org.junit.Test;


public class DecayTest {
    private double eta = 1.0;

    @Test
    public void standardDecayTest() {
        StandardDecay decay = new StandardDecay(eta, 0.05);
        calNext(decay);
    }

    @Test
    public void WarmRestartsTest() {
        WarmRestarts decay = new WarmRestarts(eta, 0.001, 0.05);
        calNext(decay);
    }
}

