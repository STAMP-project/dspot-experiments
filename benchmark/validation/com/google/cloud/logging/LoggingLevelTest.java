/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.logging;


import LoggingLevel.SEVERE;
import Severity.ALERT;
import Severity.CRITICAL;
import Severity.DEBUG;
import Severity.EMERGENCY;
import Severity.ERROR;
import Severity.NOTICE;
import java.util.logging.Level;
import org.junit.Assert;
import org.junit.Test;

import static LoggingLevel.ALERT;
import static LoggingLevel.CRITICAL;
import static LoggingLevel.DEBUG;
import static LoggingLevel.EMERGENCY;
import static LoggingLevel.ERROR;
import static LoggingLevel.NOTICE;


public class LoggingLevelTest {
    @Test
    public void testDebug() {
        LoggingLevel debug = DEBUG;
        Assert.assertEquals(DEBUG, debug.getSeverity());
        Assert.assertEquals("DEBUG", debug.getName());
        Assert.assertTrue(((debug.intValue()) < (Level.FINEST.intValue())));
        Assert.assertTrue(((debug.intValue()) > (Level.ALL.intValue())));
    }

    @Test
    public void testNotice() {
        LoggingLevel notice = NOTICE;
        Assert.assertEquals(NOTICE, notice.getSeverity());
        Assert.assertEquals("NOTICE", notice.getName());
        Assert.assertTrue(((notice.intValue()) > (Level.INFO.intValue())));
        Assert.assertTrue(((notice.intValue()) < (Level.WARNING.intValue())));
    }

    @Test
    public void testError() {
        LoggingLevel error = ERROR;
        Assert.assertEquals(ERROR, error.getSeverity());
        Assert.assertEquals("ERROR", error.getName());
        Assert.assertTrue(((error.intValue()) > (Level.WARNING.intValue())));
        Assert.assertTrue(((error.intValue()) < (Level.SEVERE.intValue())));
    }

    @Test
    public void testCritical() {
        LoggingLevel critical = CRITICAL;
        Assert.assertEquals(CRITICAL, critical.getSeverity());
        Assert.assertEquals("CRITICAL", critical.getName());
        Assert.assertTrue(((critical.intValue()) > (SEVERE.intValue())));
        Assert.assertTrue(((critical.intValue()) < (Level.OFF.intValue())));
    }

    @Test
    public void testAlert() {
        LoggingLevel alert = ALERT;
        Assert.assertEquals(ALERT, alert.getSeverity());
        Assert.assertEquals("ALERT", alert.getName());
        Assert.assertTrue(((alert.intValue()) > (LoggingLevel.CRITICAL.intValue())));
        Assert.assertTrue(((alert.intValue()) < (Level.OFF.intValue())));
    }

    @Test
    public void testEmergency() {
        LoggingLevel emergency = EMERGENCY;
        Assert.assertEquals(EMERGENCY, emergency.getSeverity());
        Assert.assertEquals("EMERGENCY", emergency.getName());
        Assert.assertTrue(((emergency.intValue()) > (LoggingLevel.ALERT.intValue())));
        Assert.assertTrue(((emergency.intValue()) < (Level.OFF.intValue())));
    }
}

