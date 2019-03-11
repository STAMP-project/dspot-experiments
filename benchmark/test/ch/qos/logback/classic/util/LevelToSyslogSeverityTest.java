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
package ch.qos.logback.classic.util;


import Level.DEBUG;
import Level.ERROR;
import Level.INFO;
import Level.TRACE;
import Level.WARN;
import SyslogConstants.DEBUG_SEVERITY;
import SyslogConstants.ERROR_SEVERITY;
import SyslogConstants.INFO_SEVERITY;
import SyslogConstants.WARNING_SEVERITY;
import org.junit.Assert;
import org.junit.Test;


public class LevelToSyslogSeverityTest {
    @Test
    public void smoke() {
        Assert.assertEquals(DEBUG_SEVERITY, LevelToSyslogSeverity.convert(createEventOfLevel(TRACE)));
        Assert.assertEquals(DEBUG_SEVERITY, LevelToSyslogSeverity.convert(createEventOfLevel(DEBUG)));
        Assert.assertEquals(INFO_SEVERITY, LevelToSyslogSeverity.convert(createEventOfLevel(INFO)));
        Assert.assertEquals(WARNING_SEVERITY, LevelToSyslogSeverity.convert(createEventOfLevel(WARN)));
        Assert.assertEquals(ERROR_SEVERITY, LevelToSyslogSeverity.convert(createEventOfLevel(ERROR)));
    }
}

