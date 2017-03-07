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


package ch.qos.logback.core.recovery;


public class AmplRecoveryCoordinatorTest {
    long now = java.lang.System.currentTimeMillis();

    ch.qos.logback.core.recovery.RecoveryCoordinator rc = new ch.qos.logback.core.recovery.RecoveryCoordinator(now);

    @org.junit.Test
    public void recoveryNotNeededAfterInit() {
        ch.qos.logback.core.recovery.RecoveryCoordinator rc = new ch.qos.logback.core.recovery.RecoveryCoordinator();
        org.junit.Assert.assertTrue(rc.isTooSoon());
    }

    @org.junit.Test
    public void recoveryNotNeededIfAsleepForLessThanBackOffTime() throws java.lang.InterruptedException {
        rc.setCurrentTime(((now) + ((ch.qos.logback.core.recovery.RecoveryCoordinator.BACKOFF_COEFFICIENT_MIN) / 2)));
        org.junit.Assert.assertTrue(rc.isTooSoon());
    }

    @org.junit.Test
    public void recoveryNeededIfAsleepForMoreThanBackOffTime() throws java.lang.InterruptedException {
        rc.setCurrentTime((((now) + (ch.qos.logback.core.recovery.RecoveryCoordinator.BACKOFF_COEFFICIENT_MIN)) + 20));
        org.junit.Assert.assertFalse(rc.isTooSoon());
    }

    @org.junit.Test
    public void recoveryNotNeededIfCurrentTimeSetToBackOffTime() throws java.lang.InterruptedException {
        rc.setCurrentTime(((now) + (ch.qos.logback.core.recovery.RecoveryCoordinator.BACKOFF_COEFFICIENT_MIN)));
        org.junit.Assert.assertTrue(rc.isTooSoon());
    }

    @org.junit.Test
    public void recoveryNeededIfCurrentTimeSetToExceedBackOffTime() {
        rc.setCurrentTime((((now) + (ch.qos.logback.core.recovery.RecoveryCoordinator.BACKOFF_COEFFICIENT_MIN)) + 1));
        org.junit.Assert.assertFalse(rc.isTooSoon());
    }

    @org.junit.Test
    public void recoveryConditionDetectedEvenAfterReallyLongTimesBetweenRecovery() {
        // Since backoff time quadruples whenever recovery is needed,
        // we double the offset on each for-loop iteration, causing
        // every other iteration to trigger recovery.
        long offset = ch.qos.logback.core.recovery.RecoveryCoordinator.BACKOFF_COEFFICIENT_MIN;
        for (int i = 0; i < 16; i++) {
            rc.setCurrentTime(((now) + offset));
            if ((i % 2) == 0) {
                org.junit.Assert.assertTrue(("recovery should've been needed at " + offset), rc.isTooSoon());
            }else {
                org.junit.Assert.assertFalse(("recovery should NOT have been needed at " + offset), rc.isTooSoon());
            }
            offset *= 2;
        }
    }
}

