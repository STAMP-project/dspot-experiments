/**
 * Copyright (c) 2011-2017 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.core;


import io.vertx.core.impl.Deployment;
import io.vertx.test.core.Repeat;
import io.vertx.test.core.VertxTestBase;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.Test;


/**
 *
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class ComplexHATest extends VertxTestBase {
    private Random random = new Random();

    protected final int maxVerticlesPerNode = 20;

    protected Set<Deployment>[] deploymentSnapshots;

    protected volatile int totDeployed;

    protected volatile int killedNode;

    protected List<Integer> aliveNodes;

    @Test
    @Repeat(times = 10)
    public void testComplexFailover() {
        try {
            int numNodes = 8;
            createNodes(numNodes);
            deployRandomVerticles(() -> {
                killRandom();
            });
            await(10, TimeUnit.MINUTES);
        } catch (Throwable t) {
            // Need to explicitly catch throwables in repeats or they will be swallowed
            t.printStackTrace();
            // Don't forget to fail!
            fail(t.getMessage());
        }
    }
}

