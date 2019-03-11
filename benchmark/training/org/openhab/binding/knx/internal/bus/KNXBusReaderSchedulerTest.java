/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.knx.internal.bus;


import org.junit.Assert;
import org.junit.Test;
import tuwien.auto.calimero.datapoint.Datapoint;
import tuwien.auto.calimero.exception.KNXFormatException;


/**
 *
 *
 * @author Volker Daube
 * @since 1.6.0
 */
public class KNXBusReaderSchedulerTest {
    private KNXBusReaderScheduler kNXBindingAutoRefreshScheduler;

    private int dpCount = 0;

    @Test
    public void testStart() throws KNXFormatException {
        Assert.assertFalse(kNXBindingAutoRefreshScheduler.isRunning());
        kNXBindingAutoRefreshScheduler.start();
        Assert.assertTrue(kNXBindingAutoRefreshScheduler.isRunning());
        kNXBindingAutoRefreshScheduler.stop();
        Assert.assertFalse(kNXBindingAutoRefreshScheduler.isRunning());
    }

    @Test
    public void testAdd() throws KNXFormatException {
        kNXBindingAutoRefreshScheduler.start();
        Assert.assertTrue(kNXBindingAutoRefreshScheduler.isRunning());
        Datapoint datapoint = createDP("1.001");
        Assert.assertTrue(kNXBindingAutoRefreshScheduler.scheduleRead(datapoint, 0));
        Assert.assertFalse(kNXBindingAutoRefreshScheduler.scheduleRead(null, 0));
        Assert.assertFalse(kNXBindingAutoRefreshScheduler.scheduleRead(null, 1));
        Assert.assertFalse(kNXBindingAutoRefreshScheduler.scheduleRead(datapoint, (-1)));
        Assert.assertTrue(kNXBindingAutoRefreshScheduler.scheduleRead(datapoint, 2));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(kNXBindingAutoRefreshScheduler.scheduleRead(datapoint, 3));
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        kNXBindingAutoRefreshScheduler.clear();
        kNXBindingAutoRefreshScheduler.stop();
        Assert.assertFalse(kNXBindingAutoRefreshScheduler.isRunning());
    }

    @Test
    public void testLargeNumberOfDPs() throws KNXFormatException {
        kNXBindingAutoRefreshScheduler.start();
        Assert.assertTrue(kNXBindingAutoRefreshScheduler.isRunning());
        Datapoint datapoint = createDP("1.001");
        Assert.assertTrue(kNXBindingAutoRefreshScheduler.scheduleRead(datapoint, 1));
        datapoint = createDP("1.001");
        Assert.assertTrue(kNXBindingAutoRefreshScheduler.scheduleRead(datapoint, 1));
        datapoint = createDP("1.001");
        Assert.assertTrue(kNXBindingAutoRefreshScheduler.scheduleRead(datapoint, 1));
        datapoint = createDP("1.001");
        Assert.assertTrue(kNXBindingAutoRefreshScheduler.scheduleRead(datapoint, 1));
        datapoint = createDP("2.001");
        Assert.assertTrue(kNXBindingAutoRefreshScheduler.scheduleRead(datapoint, 2));
        datapoint = createDP("2.001");
        Assert.assertTrue(kNXBindingAutoRefreshScheduler.scheduleRead(datapoint, 2));
        datapoint = createDP("2.001");
        Assert.assertTrue(kNXBindingAutoRefreshScheduler.scheduleRead(datapoint, 2));
        datapoint = createDP("2.001");
        Assert.assertTrue(kNXBindingAutoRefreshScheduler.scheduleRead(datapoint, 2));
        datapoint = createDP("2.001");
        Assert.assertTrue(kNXBindingAutoRefreshScheduler.scheduleRead(datapoint, 10));
        datapoint = createDP("2.001");
        Assert.assertTrue(kNXBindingAutoRefreshScheduler.scheduleRead(datapoint, 11));
        datapoint = createDP("2.001");
        Assert.assertTrue(kNXBindingAutoRefreshScheduler.scheduleRead(datapoint, 12));
        datapoint = createDP("2.001");
        Assert.assertTrue(kNXBindingAutoRefreshScheduler.scheduleRead(datapoint, 13));
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        kNXBindingAutoRefreshScheduler.clear();
        kNXBindingAutoRefreshScheduler.stop();
        Assert.assertFalse(kNXBindingAutoRefreshScheduler.isRunning());
    }

    @Test
    public void testClear() {
        kNXBindingAutoRefreshScheduler.clear();
    }
}

