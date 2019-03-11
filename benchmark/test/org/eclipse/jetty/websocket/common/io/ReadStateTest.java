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
package org.eclipse.jetty.websocket.common.io;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ReadStateTest {
    @Test
    public void testReading() {
        ReadState readState = new ReadState();
        MatcherAssert.assertThat("Initially reading", readState.isReading(), Matchers.is(true));
        MatcherAssert.assertThat("No prior suspending", readState.suspend(), Matchers.is(false));
        MatcherAssert.assertThat("No prior suspending", readState.isSuspended(), Matchers.is(false));
        Assertions.assertThrows(IllegalStateException.class, readState::resume, "No suspending to resume");
        MatcherAssert.assertThat("No suspending to resume", readState.isSuspended(), Matchers.is(false));
    }

    @Test
    public void testSuspendingThenResume() {
        ReadState readState = new ReadState();
        MatcherAssert.assertThat("Initially reading", readState.isReading(), Matchers.is(true));
        Assertions.assertTrue(readState.suspending());
        MatcherAssert.assertThat("Suspending doesn't take effect immediately", readState.isSuspended(), Matchers.is(false));
        MatcherAssert.assertThat("Resume from suspending requires no followup", readState.resume(), Matchers.is(false));
        MatcherAssert.assertThat("Resume from suspending requires no followup", readState.isSuspended(), Matchers.is(false));
        MatcherAssert.assertThat("Suspending was discarded", readState.suspend(), Matchers.is(false));
        MatcherAssert.assertThat("Suspending was discarded", readState.isSuspended(), Matchers.is(false));
    }

    @Test
    public void testSuspendingThenSuspendThenResume() {
        ReadState readState = new ReadState();
        MatcherAssert.assertThat("Initially reading", readState.isReading(), Matchers.is(true));
        MatcherAssert.assertThat(readState.suspending(), Matchers.is(true));
        MatcherAssert.assertThat("Suspending doesn't take effect immediately", readState.isSuspended(), Matchers.is(false));
        MatcherAssert.assertThat("Suspended", readState.suspend(), Matchers.is(true));
        MatcherAssert.assertThat("Suspended", readState.isSuspended(), Matchers.is(true));
        MatcherAssert.assertThat("Resumed", readState.resume(), Matchers.is(true));
        MatcherAssert.assertThat("Resumed", readState.isSuspended(), Matchers.is(false));
    }

    @Test
    public void testEof() {
        ReadState readState = new ReadState();
        readState.eof();
        MatcherAssert.assertThat(readState.isReading(), Matchers.is(false));
        MatcherAssert.assertThat(readState.isSuspended(), Matchers.is(true));
        MatcherAssert.assertThat(readState.suspend(), Matchers.is(true));
        MatcherAssert.assertThat(readState.suspending(), Matchers.is(false));
        MatcherAssert.assertThat(readState.isSuspended(), Matchers.is(true));
        MatcherAssert.assertThat(readState.suspend(), Matchers.is(true));
        MatcherAssert.assertThat(readState.isSuspended(), Matchers.is(true));
        MatcherAssert.assertThat(readState.resume(), Matchers.is(false));
        MatcherAssert.assertThat(readState.isSuspended(), Matchers.is(true));
    }
}

