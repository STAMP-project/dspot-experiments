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


public class AmplBasicStatusManagerTest {
    ch.qos.logback.core.BasicStatusManager bsm = new ch.qos.logback.core.BasicStatusManager();

    @org.junit.Test
    public void smoke() {
        bsm.add(new ch.qos.logback.core.status.ErrorStatus("hello", this));
        org.junit.Assert.assertEquals(ch.qos.logback.core.status.Status.ERROR, bsm.getLevel());
        java.util.List<ch.qos.logback.core.status.Status> statusList = bsm.getCopyOfStatusList();
        org.junit.Assert.assertNotNull(statusList);
        org.junit.Assert.assertEquals(1, statusList.size());
        org.junit.Assert.assertEquals("hello", statusList.get(0).getMessage());
    }

    @org.junit.Test
    public void many() {
        int margin = 300;
        int len = ((ch.qos.logback.core.BasicStatusManager.MAX_HEADER_COUNT) + (ch.qos.logback.core.BasicStatusManager.TAIL_SIZE)) + margin;
        for (int i = 0; i < len; i++) {
            bsm.add(new ch.qos.logback.core.status.ErrorStatus(("" + i), this));
        }
        java.util.List<ch.qos.logback.core.status.Status> statusList = bsm.getCopyOfStatusList();
        org.junit.Assert.assertNotNull(statusList);
        org.junit.Assert.assertEquals(((ch.qos.logback.core.BasicStatusManager.MAX_HEADER_COUNT) + (ch.qos.logback.core.BasicStatusManager.TAIL_SIZE)), statusList.size());
        java.util.List<ch.qos.logback.core.status.Status> witness = new java.util.ArrayList<ch.qos.logback.core.status.Status>();
        for (int i = 0; i < (ch.qos.logback.core.BasicStatusManager.MAX_HEADER_COUNT); i++) {
            witness.add(new ch.qos.logback.core.status.ErrorStatus(("" + i), this));
        }
        for (int i = 0; i < (ch.qos.logback.core.BasicStatusManager.TAIL_SIZE); i++) {
            witness.add(new ch.qos.logback.core.status.ErrorStatus(("" + (((ch.qos.logback.core.BasicStatusManager.MAX_HEADER_COUNT) + margin) + i)), this));
        }
        org.junit.Assert.assertEquals(witness, statusList);
    }

    @org.junit.Test
    public void duplicateInstallationsOfOnConsoleListener() {
        ch.qos.logback.core.status.OnConsoleStatusListener sl0 = new ch.qos.logback.core.status.OnConsoleStatusListener();
        sl0.start();
        ch.qos.logback.core.status.OnConsoleStatusListener sl1 = new ch.qos.logback.core.status.OnConsoleStatusListener();
        sl1.start();
        org.junit.Assert.assertTrue(bsm.add(sl0));
        {
            java.util.List<ch.qos.logback.core.status.StatusListener> listeners = bsm.getCopyOfStatusListenerList();
            org.junit.Assert.assertEquals(1, listeners.size());
        }
        org.junit.Assert.assertFalse(bsm.add(sl1));
        {
            java.util.List<ch.qos.logback.core.status.StatusListener> listeners = bsm.getCopyOfStatusListenerList();
            org.junit.Assert.assertEquals(1, listeners.size());
        }
    }

    /* amplification of ch.qos.logback.core.BasicStatusManagerTest#many */
    @org.junit.Test
    public void many_literalMutation148() {
        int margin = 299;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(margin, 299);
        int len = ((ch.qos.logback.core.BasicStatusManager.MAX_HEADER_COUNT) + (ch.qos.logback.core.BasicStatusManager.TAIL_SIZE)) + margin;
        for (int i = 0; i < len; i++) {
            bsm.add(new ch.qos.logback.core.status.ErrorStatus(("" + i), this));
        }
        java.util.List<ch.qos.logback.core.status.Status> statusList = bsm.getCopyOfStatusList();
        org.junit.Assert.assertNotNull(statusList);
        org.junit.Assert.assertEquals(((ch.qos.logback.core.BasicStatusManager.MAX_HEADER_COUNT) + (ch.qos.logback.core.BasicStatusManager.TAIL_SIZE)), statusList.size());
        java.util.List<ch.qos.logback.core.status.Status> witness = new java.util.ArrayList<ch.qos.logback.core.status.Status>();
        for (int i = 0; i < (ch.qos.logback.core.BasicStatusManager.MAX_HEADER_COUNT); i++) {
            witness.add(new ch.qos.logback.core.status.ErrorStatus(("" + i), this));
        }
        for (int i = 0; i < (ch.qos.logback.core.BasicStatusManager.TAIL_SIZE); i++) {
            witness.add(new ch.qos.logback.core.status.ErrorStatus(("" + (((ch.qos.logback.core.BasicStatusManager.MAX_HEADER_COUNT) + margin) + i)), this));
        }
        org.junit.Assert.assertEquals(witness, statusList);
    }
}

