/**
 * Copyright (c) 2011-2018 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.core.eventbus;


import io.vertx.core.impl.VertxInternal;
import io.vertx.core.json.JsonArray;
import io.vertx.test.core.VertxTestBase;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import org.junit.Test;


/**
 *
 *
 * @author Thomas Segismont
 */
public abstract class FaultToleranceTest extends VertxTestBase {
    protected static final int NODE_COUNT = 3;

    protected static final int ADDRESSES_COUNT = 10;

    protected final List<Process> externalNodes = new ArrayList<>();

    protected final AtomicLong externalNodesStarted = new AtomicLong();

    protected final AtomicLong pongsReceived = new AtomicLong();

    protected final AtomicLong noHandlersErrors = new AtomicLong();

    protected long timeoutMs = 60000;

    protected VertxInternal vertx;

    @Test
    public void testFaultTolerance() throws Exception {
        startNodes(1);
        vertx = ((VertxInternal) (vertices[0]));
        vertx.eventBus().<String>consumer("control", ( msg) -> {
            switch (msg.body()) {
                case "start" :
                    externalNodesStarted.incrementAndGet();
                    break;
                case "pong" :
                    pongsReceived.incrementAndGet();
                    break;
                case "noHandlers" :
                    noHandlersErrors.incrementAndGet();
                    break;
            }
        });
        for (int i = 0; i < (FaultToleranceTest.NODE_COUNT); i++) {
            Process process = startExternalNode(i);
            externalNodes.add(process);
            afterNodeStarted(i, process);
        }
        afterNodesStarted();
        JsonArray message1 = new JsonArray();
        IntStream.range(0, FaultToleranceTest.NODE_COUNT).forEach(message1::add);
        vertx.eventBus().publish("ping", message1);
        assertEqualsEventually("All pongs", Long.valueOf((((FaultToleranceTest.NODE_COUNT) * (FaultToleranceTest.NODE_COUNT)) * (FaultToleranceTest.ADDRESSES_COUNT))), pongsReceived::get);
        for (int i = 0; i < ((FaultToleranceTest.NODE_COUNT) - 1); i++) {
            Process process = externalNodes.get(i);
            process.destroyForcibly();
            afterNodeKilled(i, process);
        }
        afterNodesKilled();
        pongsReceived.set(0);
        JsonArray message2 = new JsonArray().add(((FaultToleranceTest.NODE_COUNT) - 1));
        vertx.eventBus().publish("ping", message2);
        assertEqualsEventually("Survivor pongs", Long.valueOf(FaultToleranceTest.ADDRESSES_COUNT), pongsReceived::get);
        JsonArray message3 = new JsonArray();
        IntStream.range(0, ((FaultToleranceTest.NODE_COUNT) - 1)).forEach(message3::add);
        vertx.eventBus().publish("ping", message3);
        assertEqualsEventually("Dead errors", Long.valueOf((((FaultToleranceTest.NODE_COUNT) - 1) * (FaultToleranceTest.ADDRESSES_COUNT))), noHandlersErrors::get);
    }
}

