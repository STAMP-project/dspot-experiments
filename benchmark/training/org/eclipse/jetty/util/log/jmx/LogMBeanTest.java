/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.util.log.jmx;


import com.acme.Managed;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class LogMBeanTest {
    private Managed managed;

    private LogMBean logMBean;

    private static final String MANAGED_CLASS = "Managed";

    @Test
    public void testKeySet() {
        // given
        MatcherAssert.assertThat("Managed is not registered with loggers", LogMBeanTest.MANAGED_CLASS, Matchers.not(Matchers.isIn(logMBean.getLoggers())));
        // when
        logMBean.setDebugEnabled(LogMBeanTest.MANAGED_CLASS, true);
        // then
        MatcherAssert.assertThat("Managed must be registered with loggers", LogMBeanTest.MANAGED_CLASS, Matchers.isIn(logMBean.getLoggers()));
        Assertions.assertTrue(logMBean.isDebugEnabled(LogMBeanTest.MANAGED_CLASS), "This must return true as debug is enabled for this class");
    }
}

