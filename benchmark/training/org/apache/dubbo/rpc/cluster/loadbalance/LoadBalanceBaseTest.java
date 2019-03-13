/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License")); you may not use this file except in compliance with
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
package org.apache.dubbo.rpc.cluster.loadbalance;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcInvocation;
import org.apache.dubbo.rpc.RpcStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * RoundRobinLoadBalanceTest
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class LoadBalanceBaseTest {
    Invocation invocation;

    List<Invoker<LoadBalanceBaseTest>> invokers = new ArrayList<Invoker<LoadBalanceBaseTest>>();

    Invoker<LoadBalanceBaseTest> invoker1;

    Invoker<LoadBalanceBaseTest> invoker2;

    Invoker<LoadBalanceBaseTest> invoker3;

    Invoker<LoadBalanceBaseTest> invoker4;

    Invoker<LoadBalanceBaseTest> invoker5;

    RpcStatus weightTestRpcStatus1;

    RpcStatus weightTestRpcStatus2;

    RpcStatus weightTestRpcStatus3;

    RpcInvocation weightTestInvocation;

    @Test
    public void testLoadBalanceWarmup() {
        Assertions.assertEquals(1, LoadBalanceBaseTest.calculateDefaultWarmupWeight(0));
        Assertions.assertEquals(1, LoadBalanceBaseTest.calculateDefaultWarmupWeight(13));
        Assertions.assertEquals(1, LoadBalanceBaseTest.calculateDefaultWarmupWeight((6 * 1000)));
        Assertions.assertEquals(2, LoadBalanceBaseTest.calculateDefaultWarmupWeight((12 * 1000)));
        Assertions.assertEquals(10, LoadBalanceBaseTest.calculateDefaultWarmupWeight((60 * 1000)));
        Assertions.assertEquals(50, LoadBalanceBaseTest.calculateDefaultWarmupWeight(((5 * 60) * 1000)));
        Assertions.assertEquals(50, LoadBalanceBaseTest.calculateDefaultWarmupWeight((((5 * 60) * 1000) + 23)));
        Assertions.assertEquals(50, LoadBalanceBaseTest.calculateDefaultWarmupWeight((((5 * 60) * 1000) + 5999)));
        Assertions.assertEquals(51, LoadBalanceBaseTest.calculateDefaultWarmupWeight((((5 * 60) * 1000) + 6000)));
        Assertions.assertEquals(90, LoadBalanceBaseTest.calculateDefaultWarmupWeight(((9 * 60) * 1000)));
        Assertions.assertEquals(98, LoadBalanceBaseTest.calculateDefaultWarmupWeight((((10 * 60) * 1000) - (12 * 1000))));
        Assertions.assertEquals(99, LoadBalanceBaseTest.calculateDefaultWarmupWeight((((10 * 60) * 1000) - (6 * 1000))));
        Assertions.assertEquals(100, LoadBalanceBaseTest.calculateDefaultWarmupWeight(((10 * 60) * 1000)));
        Assertions.assertEquals(100, LoadBalanceBaseTest.calculateDefaultWarmupWeight(((20 * 60) * 1000)));
    }

    /* ------------------------------------test invokers for weight--------------------------------------- */
    protected static class InvokeResult {
        private AtomicLong count = new AtomicLong();

        private int weight = 0;

        private int totalWeight = 0;

        public InvokeResult(int weight) {
            this.weight = weight;
        }

        public AtomicLong getCount() {
            return count;
        }

        public int getWeight() {
            return weight;
        }

        public int getTotalWeight() {
            return totalWeight;
        }

        public void setTotalWeight(int totalWeight) {
            this.totalWeight = totalWeight;
        }

        public int getExpected(int runCount) {
            return ((getWeight()) * runCount) / (getTotalWeight());
        }

        public float getDeltaPercentage(int runCount) {
            int expected = getExpected(runCount);
            return Math.abs((((expected - (getCount().get())) * 100.0F) / expected));
        }

        @Override
        public String toString() {
            return JSON.toJSONString(this);
        }
    }

    protected List<Invoker<LoadBalanceBaseTest>> weightInvokers = new ArrayList<Invoker<LoadBalanceBaseTest>>();

    protected Invoker<LoadBalanceBaseTest> weightInvoker1;

    protected Invoker<LoadBalanceBaseTest> weightInvoker2;

    protected Invoker<LoadBalanceBaseTest> weightInvoker3;

    protected Invoker<LoadBalanceBaseTest> weightInvokerTmp;
}

