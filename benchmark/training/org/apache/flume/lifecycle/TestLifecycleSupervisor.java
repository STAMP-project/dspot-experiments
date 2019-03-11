/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.lifecycle;


import LifecycleState.START;
import LifecycleState.STOP;
import org.apache.flume.CounterGroup;
import org.apache.flume.lifecycle.LifecycleSupervisor.SupervisorPolicy;
import org.junit.Assert;
import org.junit.Test;

import static LifecycleState.IDLE;
import static LifecycleState.START;
import static LifecycleState.STOP;


public class TestLifecycleSupervisor {
    private LifecycleSupervisor supervisor;

    @Test
    public void testLifecycle() throws InterruptedException, LifecycleException {
        supervisor.start();
        supervisor.stop();
    }

    @Test
    public void testSupervise() throws InterruptedException, LifecycleException {
        supervisor.start();
        /* Attempt to supervise a known-to-fail config. */
        /* LogicalNode node = new LogicalNode(); SupervisorPolicy policy = new
        SupervisorPolicy.OnceOnlyPolicy(); supervisor.supervise(node, policy,
        LifecycleState.START);
         */
        TestLifecycleSupervisor.CountingLifecycleAware node = new TestLifecycleSupervisor.CountingLifecycleAware();
        SupervisorPolicy policy = new SupervisorPolicy.OnceOnlyPolicy();
        supervisor.supervise(node, policy, START);
        Thread.sleep(10000);
        node = new TestLifecycleSupervisor.CountingLifecycleAware();
        policy = new SupervisorPolicy.OnceOnlyPolicy();
        supervisor.supervise(node, policy, START);
        Thread.sleep(5000);
        supervisor.stop();
    }

    @Test
    public void testSuperviseBroken() throws InterruptedException, LifecycleException {
        supervisor.start();
        /* Attempt to supervise a known-to-fail config. */
        LifecycleAware node = new LifecycleAware() {
            @Override
            public void stop() {
            }

            @Override
            public void start() {
                throw new NullPointerException("Boom!");
            }

            @Override
            public LifecycleState getLifecycleState() {
                return IDLE;
            }
        };
        SupervisorPolicy policy = new SupervisorPolicy.OnceOnlyPolicy();
        supervisor.supervise(node, policy, START);
        Thread.sleep(5000);
        supervisor.stop();
    }

    @Test
    public void testSuperviseSupervisor() throws InterruptedException, LifecycleException {
        supervisor.start();
        LifecycleSupervisor supervisor2 = new LifecycleSupervisor();
        TestLifecycleSupervisor.CountingLifecycleAware node = new TestLifecycleSupervisor.CountingLifecycleAware();
        SupervisorPolicy policy = new SupervisorPolicy.OnceOnlyPolicy();
        supervisor2.supervise(node, policy, START);
        supervisor.supervise(supervisor2, new SupervisorPolicy.AlwaysRestartPolicy(), START);
        Thread.sleep(10000);
        supervisor.stop();
    }

    @Test
    public void testUnsuperviseServce() throws InterruptedException, LifecycleException {
        supervisor.start();
        LifecycleAware service = new TestLifecycleSupervisor.CountingLifecycleAware();
        SupervisorPolicy policy = new SupervisorPolicy.OnceOnlyPolicy();
        supervisor.supervise(service, policy, START);
        supervisor.unsupervise(service);
        service.stop();
        supervisor.stop();
    }

    @Test
    public void testStopServce() throws InterruptedException, LifecycleException {
        supervisor.start();
        TestLifecycleSupervisor.CountingLifecycleAware service = new TestLifecycleSupervisor.CountingLifecycleAware();
        SupervisorPolicy policy = new SupervisorPolicy.OnceOnlyPolicy();
        Assert.assertEquals(Long.valueOf(0), service.counterGroup.get("start"));
        Assert.assertEquals(Long.valueOf(0), service.counterGroup.get("stop"));
        supervisor.supervise(service, policy, START);
        Thread.sleep(3200);
        Assert.assertEquals(Long.valueOf(1), service.counterGroup.get("start"));
        Assert.assertEquals(Long.valueOf(0), service.counterGroup.get("stop"));
        supervisor.setDesiredState(service, STOP);
        Thread.sleep(3200);
        Assert.assertEquals(Long.valueOf(1), service.counterGroup.get("start"));
        Assert.assertEquals(Long.valueOf(1), service.counterGroup.get("stop"));
        supervisor.stop();
    }

    public static class CountingLifecycleAware implements LifecycleAware {
        public CounterGroup counterGroup;

        private LifecycleState lifecycleState;

        public CountingLifecycleAware() {
            lifecycleState = LifecycleState.IDLE;
            counterGroup = new CounterGroup();
        }

        @Override
        public void start() {
            counterGroup.incrementAndGet("start");
            lifecycleState = START;
        }

        @Override
        public void stop() {
            counterGroup.incrementAndGet("stop");
            lifecycleState = STOP;
        }

        @Override
        public LifecycleState getLifecycleState() {
            return lifecycleState;
        }
    }
}

