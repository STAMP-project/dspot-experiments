/**
 * Copyright (c) 2015, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 */
package com.facebook.network.connectionclass;


import ConnectionClassManager.DEFAULT_MODERATE_BANDWIDTH;
import ConnectionClassManager.DEFAULT_POOR_BANDWIDTH;
import ConnectionQuality.EXCELLENT;
import ConnectionQuality.GOOD;
import ConnectionQuality.MODERATE;
import ConnectionQuality.POOR;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import static ConnectionClassManager.DEFAULT_HYSTERESIS_PERCENT;
import static ConnectionClassManager.DEFAULT_SAMPLES_TO_QUALITY_CHANGE;


@RunWith(PowerMockRunner.class)
public class ConnectionClassTest {
    @Mock
    public ConnectionClassManager mConnectionClassManager;

    public ConnectionClassTest.TestBandwidthStateChangeListener mTestBandwidthStateChangeListener;

    private static final long BYTES_TO_BITS = 8;

    // Test the moving average to make sure correct results are returned.
    @Test
    public void TestMovingAverage() {
        mConnectionClassManager.addBandwidth(620000L, 1000L);
        mConnectionClassManager.addBandwidth(630000L, 1000L);
        mConnectionClassManager.addBandwidth(670000L, 1000L);
        mConnectionClassManager.addBandwidth(500000L, 1000L);
        mConnectionClassManager.addBandwidth(550000L, 1000L);
        mConnectionClassManager.addBandwidth(590000L, 1000L);
        Assert.assertEquals(EXCELLENT, mConnectionClassManager.getCurrentBandwidthQuality());
    }

    // Test that values under the lower bandwidth bound do not affect the final ConnectionClass values.
    @Test
    public void TestGarbageValues() {
        mConnectionClassManager.addBandwidth(620000L, 1000L);
        mConnectionClassManager.addBandwidth(0L, 1000L);
        mConnectionClassManager.addBandwidth(630000L, 1000L);
        mConnectionClassManager.addBandwidth(5L, 1000L);
        mConnectionClassManager.addBandwidth(10L, 1000L);
        mConnectionClassManager.addBandwidth(0L, 1000L);
        mConnectionClassManager.addBandwidth(90L, 1000L);
        mConnectionClassManager.addBandwidth(200L, 1000L);
        mConnectionClassManager.addBandwidth(670000L, 1000L);
        mConnectionClassManager.addBandwidth(500000L, 1000L);
        mConnectionClassManager.addBandwidth(550000L, 1000L);
        mConnectionClassManager.addBandwidth(590000L, 1000L);
        Assert.assertEquals(EXCELLENT, mConnectionClassManager.getCurrentBandwidthQuality());
    }

    @Test
    public void testStateChangeBroadcastNoBroadcast() {
        for (int i = 0; i < ((DEFAULT_SAMPLES_TO_QUALITY_CHANGE) - 1); i++) {
            mConnectionClassManager.addBandwidth(1000, 2);
        }
        Assert.assertEquals(0, mTestBandwidthStateChangeListener.getNumberOfStateChanges());
    }

    @Test
    public void testStateChangeBroadcastWithBroadcast() {
        mConnectionClassManager.reset();
        for (int i = 0; i < ((DEFAULT_SAMPLES_TO_QUALITY_CHANGE) + 1); i++) {
            mConnectionClassManager.addBandwidth(1000, 2);
        }
        Assert.assertEquals(1, mTestBandwidthStateChangeListener.getNumberOfStateChanges());
        Assert.assertEquals(EXCELLENT, mTestBandwidthStateChangeListener.getLastBandwidthState());
    }

    @Test
    public void testStateChangeBroadcastDoesNotRepeatItself() {
        mConnectionClassManager.reset();
        for (int i = 0; i < ((3 * (DEFAULT_SAMPLES_TO_QUALITY_CHANGE)) + 1); i++) {
            mConnectionClassManager.addBandwidth(1000, 2);
        }
        Assert.assertEquals(1, mTestBandwidthStateChangeListener.getNumberOfStateChanges());
    }

    @Test
    public void testStateChangeHysteresisRejectsLow() {
        runHysteresisTest(DEFAULT_POOR_BANDWIDTH, 1.02, MODERATE, ((100.0 - ((DEFAULT_HYSTERESIS_PERCENT) / 2)) / 100.0), MODERATE);
    }

    @Test
    public void testStateChangeHysteresisRejectsHigh() {
        runHysteresisTest(DEFAULT_MODERATE_BANDWIDTH, 0.98, MODERATE, (100.0 / (100.0 - ((DEFAULT_HYSTERESIS_PERCENT) / 2))), MODERATE);
    }

    @Test
    public void testStateChangeHysteresisAcceptsLow() {
        runHysteresisTest(DEFAULT_POOR_BANDWIDTH, 1.02, MODERATE, ((100.0 - ((DEFAULT_HYSTERESIS_PERCENT) * 2)) / 100.0), POOR);
    }

    @Test
    public void testStateChangeHysteresisAcceptsHigh() {
        runHysteresisTest(DEFAULT_MODERATE_BANDWIDTH, 0.98, MODERATE, (100.0 / (100.0 - ((DEFAULT_HYSTERESIS_PERCENT) * 2))), GOOD);
    }

    private class TestBandwidthStateChangeListener implements ConnectionClassManager.ConnectionClassStateChangeListener {
        private int mNumberOfStateChanges = 0;

        private ConnectionQuality mLastBandwidthState;

        private TestBandwidthStateChangeListener() {
            mConnectionClassManager.register(this);
        }

        @Override
        public void onBandwidthStateChange(ConnectionQuality bandwidthState) {
            mNumberOfStateChanges += 1;
            mLastBandwidthState = bandwidthState;
        }

        public int getNumberOfStateChanges() {
            return mNumberOfStateChanges;
        }

        public ConnectionQuality getLastBandwidthState() {
            return mLastBandwidthState;
        }
    }
}

