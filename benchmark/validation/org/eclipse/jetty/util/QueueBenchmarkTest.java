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
package org.eclipse.jetty.util;


import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


// TODO: Review - this is a HIGH_CPU, HIGH_MEMORY test that takes 20 minutes to execute.
// perhaps this should not be a normal every day testcase?
// Move to a different module? make it not a junit testcase?
@Disabled
public class QueueBenchmarkTest {
    private static final Logger logger = Log.getLogger(QueueBenchmarkTest.class);

    private static final Runnable ELEMENT = () -> {
    };

    private static final Runnable END = () -> {
    };

    @Test
    public void testQueues() throws Exception {
        int cores = ProcessorUtils.availableProcessors();
        Assumptions.assumeTrue((cores > 1));
        final int readers = cores / 2;
        final int writers = readers;
        final int iterations = (16 * 1024) * 1024;
        final List<Queue<Runnable>> queues = new ArrayList<>();
        queues.add(new ConcurrentLinkedQueue<>());// JDK lock-free queue, allocating nodes

        queues.add(new ArrayBlockingQueue<>((iterations * writers)));// JDK lock-based, circular array queue

        queues.add(new BlockingArrayQueue((iterations * writers)));// Jetty lock-based, circular array queue

        testQueues(readers, writers, iterations, queues, false);
    }

    @Test
    public void testBlockingQueues() throws Exception {
        int cores = ProcessorUtils.availableProcessors();
        Assumptions.assumeTrue((cores > 1));
        final int readers = cores / 2;
        final int writers = readers;
        final int iterations = (16 * 1024) * 1024;
        final List<Queue<Runnable>> queues = new ArrayList<>();
        queues.add(new LinkedBlockingQueue<>());
        queues.add(new ArrayBlockingQueue<>((iterations * writers)));
        queues.add(new BlockingArrayQueue((iterations * writers)));
        testQueues(readers, writers, iterations, queues, true);
    }
}

