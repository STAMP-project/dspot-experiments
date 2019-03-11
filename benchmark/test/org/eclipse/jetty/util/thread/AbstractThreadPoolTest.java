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
package org.eclipse.jetty.util.thread;


import org.eclipse.jetty.util.thread.ThreadPool.SizedThreadPool;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public abstract class AbstractThreadPoolTest {
    private static int originalCoreCount;

    @Test
    public void testBudget_constructMaxThenLease() {
        SizedThreadPool pool = newPool(4);
        pool.getThreadPoolBudget().leaseTo(this, 2);
        try {
            pool.getThreadPoolBudget().leaseTo(this, 3);
            Assertions.fail();
        } catch (IllegalStateException e) {
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Insufficient configured threads"));
        }
        pool.getThreadPoolBudget().leaseTo(this, 1);
    }

    @Test
    public void testBudget_LeaseThenSetMax() {
        SizedThreadPool pool = newPool(4);
        pool.getThreadPoolBudget().leaseTo(this, 2);
        pool.setMaxThreads(3);
        try {
            pool.setMaxThreads(1);
            Assertions.fail();
        } catch (IllegalStateException e) {
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Insufficient configured threads"));
        }
        MatcherAssert.assertThat(pool.getMaxThreads(), Matchers.is(3));
    }
}

