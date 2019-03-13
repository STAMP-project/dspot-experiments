/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.core;


import Status.ERROR;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusListener;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class BasicStatusManagerTest {
    BasicStatusManager bsm = new BasicStatusManager();

    @Test
    public void smoke() {
        bsm.add(new ErrorStatus("hello", this));
        Assert.assertEquals(ERROR, bsm.getLevel());
        List<Status> statusList = bsm.getCopyOfStatusList();
        Assert.assertNotNull(statusList);
        Assert.assertEquals(1, statusList.size());
        Assert.assertEquals("hello", statusList.get(0).getMessage());
    }

    @Test
    public void many() {
        int margin = 300;
        int len = ((BasicStatusManager.MAX_HEADER_COUNT) + (BasicStatusManager.TAIL_SIZE)) + margin;
        for (int i = 0; i < len; i++) {
            bsm.add(new ErrorStatus(("" + i), this));
        }
        List<Status> statusList = bsm.getCopyOfStatusList();
        Assert.assertNotNull(statusList);
        Assert.assertEquals(((BasicStatusManager.MAX_HEADER_COUNT) + (BasicStatusManager.TAIL_SIZE)), statusList.size());
        List<Status> witness = new ArrayList<Status>();
        for (int i = 0; i < (BasicStatusManager.MAX_HEADER_COUNT); i++) {
            witness.add(new ErrorStatus(("" + i), this));
        }
        for (int i = 0; i < (BasicStatusManager.TAIL_SIZE); i++) {
            witness.add(new ErrorStatus(("" + (((BasicStatusManager.MAX_HEADER_COUNT) + margin) + i)), this));
        }
        Assert.assertEquals(witness, statusList);
    }

    @Test
    public void duplicateInstallationsOfOnConsoleListener() {
        OnConsoleStatusListener sl0 = new OnConsoleStatusListener();
        sl0.start();
        OnConsoleStatusListener sl1 = new OnConsoleStatusListener();
        sl1.start();
        Assert.assertTrue(bsm.add(sl0));
        {
            List<StatusListener> listeners = bsm.getCopyOfStatusListenerList();
            Assert.assertEquals(1, listeners.size());
        }
        Assert.assertFalse(bsm.add(sl1));
        {
            List<StatusListener> listeners = bsm.getCopyOfStatusListenerList();
            Assert.assertEquals(1, listeners.size());
        }
    }
}

