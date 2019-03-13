/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.homematic.internal.handler;


import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


/**
 * Tests for {@link SimplePortPool}.
 *
 * @author Florian Stolte - Initial Contribution
 */
public class SimplePortPoolTest {
    private SimplePortPool simplePortPool;

    @Test
    public void testPoolOnlyGivesOutPortsNotInUse() {
        final List<Integer> ports = acquireSomePorts();
        final int newPort = simplePortPool.getNextPort();
        for (int port : ports) {
            MatcherAssert.assertThat(port, CoreMatchers.not(CoreMatchers.is(newPort)));
        }
    }

    @Test
    public void testPoolReusesReleasedPort() {
        final List<Integer> ports = acquireSomePorts();
        final int firstPort = ports.get(0);
        simplePortPool.release(firstPort);
        int newPortAfterRelease = simplePortPool.getNextPort();
        MatcherAssert.assertThat(newPortAfterRelease, CoreMatchers.is(firstPort));
    }

    @Test
    public void testPoolSkipsPortsThatAreSetInUse() {
        final int firstPort = simplePortPool.getNextPort();
        simplePortPool.setInUse((firstPort + 1));
        int newPortAfterSetInUse = simplePortPool.getNextPort();
        MatcherAssert.assertThat(newPortAfterSetInUse, CoreMatchers.is((firstPort + 2)));
    }
}

