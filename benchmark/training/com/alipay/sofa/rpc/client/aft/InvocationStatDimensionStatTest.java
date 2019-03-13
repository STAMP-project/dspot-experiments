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


import RpcConstants.INVOKER_TYPE_CALLBACK;
import RpcConstants.INVOKER_TYPE_FUTURE;
import com.alipay.sofa.rpc.client.ProviderHelper;
import com.alipay.sofa.rpc.client.ProviderInfo;
import com.alipay.sofa.rpc.client.aft.bean.FaultHelloService;
import com.alipay.sofa.rpc.context.RpcInvokeContext;
import com.alipay.sofa.rpc.core.exception.RpcErrorType;
import com.alipay.sofa.rpc.core.exception.SofaRpcException;
import com.alipay.sofa.rpc.core.exception.SofaTimeOutException;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:lw111072@antfin.com">liangen</a>
 */
public class InvocationStatDimensionStatTest extends FaultBaseServiceTest {
    @Test
    public void testInvocationStatFactory() {
        InvocationStatDimension invocation = new InvocationStatDimension(ProviderHelper.toProviderInfo("ip"), consumerConfig);
        InvocationStat InvocationStat1Result = InvocationStatFactory.getInvocationStat(invocation);
        InvocationStat InvocationStat2Result = InvocationStatFactory.getInvocationStat(invocation);
        Assert.assertTrue((InvocationStat1Result == InvocationStat2Result));
    }

    @Test
    public void testInvocationStatStatic() {
        InvocationStatDimension invocation = new InvocationStatDimension(ProviderHelper.toProviderInfo("ip"), consumerConfig);
        InvocationStat invocationStat = new com.alipay.sofa.rpc.client.aft.impl.ServiceExceptionInvocationStat(invocation);
        /**
         * test info static
         */
        for (int i = 0; i < 10; i++) {
            invocationStat.invoke();
        }
        for (int i = 0; i < 5; i++) {
            invocationStat.catchException(new SofaTimeOutException(""));
        }
        for (int i = 0; i < 3; i++) {
            invocationStat.catchException(new SofaRpcException(RpcErrorType.SERVER_BUSY, ""));
        }
        Assert.assertTrue((10 == (invocationStat.getInvokeCount())));
        Assert.assertTrue((8 == (invocationStat.getExceptionCount())));
        Assert.assertTrue((0.8 == (invocationStat.getExceptionRate())));
        /**
         * test window update
         */
        InvocationStat snapshot = invocationStat.snapshot();
        Assert.assertTrue((10 == (snapshot.getInvokeCount())));
        Assert.assertTrue((8 == (snapshot.getExceptionCount())));
        Assert.assertTrue((0.8 == (snapshot.getExceptionRate())));
        for (int i = 0; i < 15; i++) {
            invocationStat.invoke();
        }
        for (int i = 0; i < 8; i++) {
            invocationStat.catchException(new SofaTimeOutException(""));
        }
        for (int i = 0; i < 2; i++) {
            invocationStat.catchException(new SofaRpcException(RpcErrorType.SERVER_BUSY, ""));
        }
        Assert.assertTrue((25 == (invocationStat.getInvokeCount())));
        Assert.assertTrue((18 == (invocationStat.getExceptionCount())));
        Assert.assertTrue((0.72 == (invocationStat.getExceptionRate())));
        // ??????
        invocationStat.update(snapshot);
        Assert.assertTrue((15 == (invocationStat.getInvokeCount())));
        Assert.assertTrue((10 == (invocationStat.getExceptionCount())));
        Assert.assertTrue((0.67 == (invocationStat.getExceptionRate())));
    }

    @Test
    public void testSync() {
        for (int i = 0; i < 5; i++) {
            try {
                helloService.sayHello("liangen");
            } catch (Exception e) {
                FaultBaseTest.LOGGER.info("??");
            }
        }
        final ProviderInfo providerInfo = FaultBaseTest.getProviderInfoByHost(consumerConfig, "127.0.0.1");
        InvocationStatDimension statDimension = new InvocationStatDimension(providerInfo, consumerConfig);
        InvocationStat invocationStat = InvocationStatFactory.getInvocationStat(statDimension);
        Assert.assertEquals(5, FaultBaseTest.delayGetCount(invocationStat, 10));
        InvocationStatFactory.removeInvocationStat(invocationStat);
    }

    @Test
    public void testCallback() throws InterruptedException {
        consumerConfig.setInvokeType(INVOKER_TYPE_CALLBACK);
        prepareInvokeContext();
        consumerConfig.unRefer();
        helloService = consumerConfig.refer();
        for (int i = 0; i < 5; i++) {
            helloService.sayHello("liangen");
        }
        Thread.sleep(1000);
        final ProviderInfo providerInfo = FaultBaseTest.getProviderInfoByHost(consumerConfig, "127.0.0.1");
        InvocationStatDimension statDimension = new InvocationStatDimension(providerInfo, consumerConfig);
        InvocationStat invocationStat = InvocationStatFactory.getInvocationStat(statDimension);
        Assert.assertEquals(5, FaultBaseTest.delayGetCount(invocationStat, 5));
        InvocationStatFactory.removeInvocationStat(invocationStat);
    }

    @Test
    public void testFuture() throws SofaRpcException, InterruptedException, ExecutionException {
        consumerConfig.setInvokeType(INVOKER_TYPE_FUTURE);
        consumerConfig.unRefer();
        helloService = consumerConfig.refer();
        for (int i = 0; i < 5; i++) {
            helloService.sayHello("liangen");
            try {
                RpcInvokeContext.getContext().getFuture().get();
            } catch (Exception e) {
                FaultBaseTest.LOGGER.info("future??");
            }
        }
        Thread.sleep(1000);
        final ProviderInfo providerInfo = FaultBaseTest.getProviderInfoByHost(consumerConfig, "127.0.0.1");
        InvocationStatDimension statDimension = new InvocationStatDimension(providerInfo, consumerConfig);
        InvocationStat invocationStat = InvocationStatFactory.getInvocationStat(statDimension);
        Assert.assertEquals(5, FaultBaseTest.delayGetCount(invocationStat, 5));
        InvocationStatFactory.removeInvocationStat(invocationStat);
    }

    @Test
    public void testRegulationEffective() throws InterruptedException {
        FaultToleranceConfig config = new FaultToleranceConfig();
        config.setDegradeEffective(true);
        config.setRegulationEffective(false);
        config.setTimeWindow(3);
        config.setLeastWindowCount(5);
        config.setWeightDegradeRate(0.5);
        config.setLeastWindowExceptionRateMultiple(1.0);
        FaultToleranceConfigManager.putAppConfig(FaultBaseTest.APP_NAME1, config);
        /**
         * ????????????
         */
        for (int i = 0; i < 5; i++) {
            try {
                helloService.sayHello("liangen");
            } catch (Exception e) {
                FaultBaseTest.LOGGER.info("??");
            }
        }
        final ProviderInfo providerInfo = FaultBaseTest.getProviderInfoByHost(consumerConfig, "127.0.0.1");
        InvocationStatDimension statDimension = new InvocationStatDimension(providerInfo, consumerConfig);
        InvocationStat invocationStat = InvocationStatFactory.getInvocationStat(statDimension);
        Assert.assertEquals(0, FaultBaseTest.delayGetCount(invocationStat, 0));
        Assert.assertTrue(((invocationStat.getExceptionCount()) == 0));
        Assert.assertTrue(((invocationStat.getExceptionRate()) == (-1)));
        /**
         * ???????????
         */
        FaultToleranceConfigManager.getConfig(FaultBaseTest.APP_NAME1).setRegulationEffective(true);
        for (int i = 0; i < 5; i++) {
            try {
                helloService.sayHello("liangen");
            } catch (Exception e) {
                FaultBaseTest.LOGGER.info("??");
            }
        }
        Assert.assertEquals(5, FaultBaseTest.delayGetCount(invocationStat, 5));
        Assert.assertTrue(((invocationStat.getExceptionCount()) == 1));
        Assert.assertTrue(((invocationStat.getExceptionRate()) == 0.2));
        /**
         * ?????????????
         */
        // ???????
        Assert.assertTrue((50 == (FaultBaseTest.delayGetWeight(providerInfo, 50, 52))));
        InvocationStatFactory.removeInvocationStat(invocationStat);
    }
}

