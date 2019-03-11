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
package com.alipay.sofa.rpc.client.aft;


import InvocationStatFactory.ALL_STATS;
import com.alipay.sofa.rpc.client.ProviderInfo;
import java.util.concurrent.Callable;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:zhanggeng.zg@antfin.com">GengZhang</a>
 */
public class ServiceHorizontalRegulationStrategyTest extends FaultBaseServiceTest {
    @Test
    public void testAll() throws InterruptedException {
        FaultToleranceConfig config = new FaultToleranceConfig();
        config.setDegradeEffective(true);
        config.setRegulationEffective(true);
        config.setTimeWindow(1);
        config.setLeastWindowCount(10);
        config.setWeightDegradeRate(0.5);
        config.setDegradeLeastWeight(30);
        config.setLeastWindowExceptionRateMultiple(1.0);
        FaultToleranceConfigManager.putAppConfig(FaultBaseTest.APP_NAME1, config);
        /**
         * test degrade normal
         */
        for (int i = 0; i < 1; i++) {
            try {
                helloService.sayHello("liangen");
            } catch (Exception e) {
                FaultBaseTest.LOGGER.info("??");
            }
        }
        Thread.sleep(100);
        final ProviderInfo providerInfo = FaultBaseTest.getProviderInfoByHost(consumerConfig, "127.0.0.1");
        final InvocationStatDimension statDimension = new InvocationStatDimension(providerInfo, consumerConfig);
        InvocationStat invocationStat = ALL_STATS.get(statDimension);
        Assert.assertNotNull(invocationStat);
        // ???10000ms ???????
        Assert.assertNull(FaultBaseTest.delayGet(new Callable<InvocationStat>() {
            @Override
            public InvocationStat call() throws Exception {
                return ALL_STATS.get(statDimension);
            }
        }, null, 100, 100));
    }
}

