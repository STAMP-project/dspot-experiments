/**
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.bootique.shutdown;


import java.time.Duration;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class DefaultShutdownManagerIT {
    private AutoCloseable mockCloseable1;

    private AutoCloseable mockCloseable2;

    @Test
    public void testShutdown() throws Exception {
        Duration timeout = Duration.ofMillis(10000L);
        DefaultShutdownManager shutdownManager = new DefaultShutdownManager(timeout);
        shutdownManager.addShutdownHook(mockCloseable1);
        shutdownManager.addShutdownHook(mockCloseable2);
        shutdownManager.shutdown();
        Mockito.verify(mockCloseable1).close();
        Mockito.verify(mockCloseable2).close();
    }

    @Test
    public void testShutdown_Unresponsive_Timeout() throws Exception {
        Duration timeout = Duration.ofMillis(500L);
        DefaultShutdownManager shutdownManager = new DefaultShutdownManager(timeout);
        Mockito.doAnswer(( i) -> {
            while (true) {
                // spinning...
            } 
        }).when(mockCloseable2).close();
        shutdownManager.addShutdownHook(mockCloseable2);
        long t0 = System.currentTimeMillis();
        shutdownManager.shutdown();
        long t1 = System.currentTimeMillis();
        Mockito.verify(mockCloseable2).close();
        Assert.assertTrue(((t1 - t0) >= (timeout.toMillis())));
        // too optimistic??
        Assert.assertTrue(((t1 - t0) < ((timeout.toMillis()) + 1000)));
    }
}

