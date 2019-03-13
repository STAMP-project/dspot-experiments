/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.controller.state.providers.zookeeper;


import ZooKeeperStateProvider.ACCESS_CONTROL;
import ZooKeeperStateProvider.OPEN_TO_WORLD;
import ZooKeeperStateProvider.ROOT_NODE;
import ZooKeeperStateProvider.SESSION_TIMEOUT;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.curator.test.TestingServer;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.state.StateProvider;
import org.apache.nifi.components.state.exception.StateTooLargeException;
import org.apache.nifi.controller.state.providers.AbstractTestStateProvider;
import org.junit.Test;
import org.testng.Assert;


public class TestZooKeeperStateProvider extends AbstractTestStateProvider {
    private volatile StateProvider provider;

    private volatile TestingServer zkServer;

    private static final Map<PropertyDescriptor, String> defaultProperties = new HashMap<>();

    static {
        TestZooKeeperStateProvider.defaultProperties.put(SESSION_TIMEOUT, "3 secs");
        TestZooKeeperStateProvider.defaultProperties.put(ROOT_NODE, "/nifi/team1/testing");
        TestZooKeeperStateProvider.defaultProperties.put(ACCESS_CONTROL, OPEN_TO_WORLD.getValue());
    }

    @Test(timeout = 20000)
    public void testStateTooLargeExceptionThrownOnSetState() throws InterruptedException {
        final Map<String, String> state = new HashMap<>();
        final StringBuilder sb = new StringBuilder();
        // Build a string that is a little less than 64 KB, because that's
        // the largest value available for DataOutputStream.writeUTF
        for (int i = 0; i < 6500; i++) {
            sb.append("0123456789");
        }
        for (int i = 0; i < 20; i++) {
            state.put(("numbers." + i), sb.toString());
        }
        while (true) {
            try {
                getProvider().setState(state, componentId);
                Assert.fail("Expected StateTooLargeException");
            } catch (final StateTooLargeException stle) {
                // expected behavior.
                break;
            } catch (final IOException ioe) {
                // If we attempt to interact with the server too quickly, we will get a
                // ZooKeeper ConnectionLoss Exception, which the provider wraps in an IOException.
                // We will wait 1 second in this case and try again. The test will timeout if this
                // does not succeeed within 20 seconds.
                Thread.sleep(1000L);
            } catch (final Exception e) {
                e.printStackTrace();
                Assert.fail((("Expected StateTooLargeException but " + (e.getClass())) + " was thrown"), e);
            }
        } 
    }

    @Test(timeout = 20000)
    public void testStateTooLargeExceptionThrownOnReplace() throws IOException, InterruptedException {
        final Map<String, String> state = new HashMap<>();
        final StringBuilder sb = new StringBuilder();
        // Build a string that is a little less than 64 KB, because that's
        // the largest value available for DataOutputStream.writeUTF
        for (int i = 0; i < 6500; i++) {
            sb.append("0123456789");
        }
        for (int i = 0; i < 20; i++) {
            state.put(("numbers." + i), sb.toString());
        }
        final Map<String, String> smallState = new HashMap<>();
        smallState.put("abc", "xyz");
        while (true) {
            try {
                getProvider().setState(smallState, componentId);
                break;
            } catch (final IOException ioe) {
                // If we attempt to interact with the server too quickly, we will get a
                // ZooKeeper ConnectionLoss Exception, which the provider wraps in an IOException.
                // We will wait 1 second in this case and try again. The test will timeout if this
                // does not succeeed within 20 seconds.
                Thread.sleep(1000L);
            }
        } 
        try {
            getProvider().replace(getProvider().getState(componentId), state, componentId);
            Assert.fail("Expected StateTooLargeException");
        } catch (final StateTooLargeException stle) {
            // expected behavior.
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail("Expected StateTooLargeException", e);
        }
    }
}

