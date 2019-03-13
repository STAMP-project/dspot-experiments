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


import MeasureState.ABNORMAL;
import MeasureState.HEALTH;
import MeasureState.IGNORE;
import com.alipay.sofa.rpc.client.ProviderHelper;
import com.alipay.sofa.rpc.client.ProviderInfo;
import com.alipay.sofa.rpc.client.aft.impl.ServiceHorizontalMeasureStrategy;
import com.alipay.sofa.rpc.core.exception.RpcErrorType;
import com.alipay.sofa.rpc.core.exception.SofaTimeOutException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:lw111072@antfin.com">liangen</a>
 */
public class MeasureStrategyTest extends FaultBaseTest {
    @Test
    public void testHealthAndAbnormalAndIgnore() {
        FaultToleranceConfig config = new FaultToleranceConfig();
        config.setLeastWindowCount(10);
        config.setLeastWindowExceptionRateMultiple(3.0);
        FaultToleranceConfigManager.putAppConfig(FaultBaseTest.APP_NAME1, config);
        InvocationStatDimension invocation1 = new InvocationStatDimension(ProviderHelper.toProviderInfo("ip1"), consumerConfig);
        InvocationStatDimension invocation2 = new InvocationStatDimension(ProviderHelper.toProviderInfo("ip2"), consumerConfig);
        InvocationStatDimension invocation3 = new InvocationStatDimension(ProviderHelper.toProviderInfo("ip3"), consumerConfig);
        InvocationStatDimension invocation4 = new InvocationStatDimension(ProviderHelper.toProviderInfo("ip4"), consumerConfig);
        InvocationStatDimension invocation5 = new InvocationStatDimension(ProviderHelper.toProviderInfo("ip5"), consumerConfig);
        InvocationStatDimension invocation6 = new InvocationStatDimension(ProviderHelper.toProviderInfo("ip6"), consumerConfig);
        InvocationStat InvocationStat1 = InvocationStatFactory.getInvocationStat(invocation1);
        InvocationStat InvocationStat2 = InvocationStatFactory.getInvocationStat(invocation2);
        InvocationStat InvocationStat3 = InvocationStatFactory.getInvocationStat(invocation3);
        InvocationStat InvocationStat4 = InvocationStatFactory.getInvocationStat(invocation4);
        InvocationStat InvocationStat5 = InvocationStatFactory.getInvocationStat(invocation5);
        InvocationStat InvocationStat6 = InvocationStatFactory.getInvocationStat(invocation6);
        MeasureModel measureModel = new MeasureModel(FaultBaseTest.APP_NAME1, "service");
        measureModel.addInvocationStat(InvocationStat1);
        measureModel.addInvocationStat(InvocationStat2);
        measureModel.addInvocationStat(InvocationStat3);
        measureModel.addInvocationStat(InvocationStat4);
        measureModel.addInvocationStat(InvocationStat5);
        measureModel.addInvocationStat(InvocationStat6);
        /**
         * ??1-5???10??1-4??1??5??6?
         */
        /**
         * ??6??9????9?
         */
        for (int i = 0; i < 10; i++) {
            InvocationStat1.invoke();
            InvocationStat2.invoke();
            InvocationStat3.invoke();
            InvocationStat4.invoke();
            InvocationStat5.invoke();
        }
        InvocationStat1.catchException(new SofaTimeOutException(""));
        InvocationStat2.catchException(new SofaTimeOutException(""));
        InvocationStat3.catchException(new SofaTimeOutException(""));
        InvocationStat4.catchException(new SofaTimeOutException(""));
        for (int i = 0; i < 6; i++) {
            InvocationStat5.catchException(new SofaTimeOutException(""));
        }
        for (int i = 0; i < 9; i++) {
            InvocationStat6.invoke();
            InvocationStat6.catchException(new SofaTimeOutException(""));
        }
        /**
         * ??
         */
        MeasureStrategy measureStrategy = new ServiceHorizontalMeasureStrategy();
        MeasureResult measureResult = measureStrategy.measure(measureModel);
        /**
         * ????
         */
        List<MeasureResultDetail> measureDetais = measureResult.getAllMeasureResultDetails();
        for (MeasureResultDetail measureResultDetail : measureDetais) {
            MeasureState measureState = measureResultDetail.getMeasureState();
            double abnormalRate = measureResultDetail.getAbnormalRate();
            double averageAbnormalRate = measureResultDetail.getAverageAbnormalRate();
            if (measureResultDetail.getInvocationStatDimension().equals(invocation1)) {
                Assert.assertTrue(measureState.equals(HEALTH));
                Assert.assertTrue((abnormalRate == 0.1));
                Assert.assertTrue((averageAbnormalRate == 0.2));
            } else
                if (measureResultDetail.getInvocationStatDimension().equals(invocation2)) {
                    Assert.assertTrue(measureState.equals(HEALTH));
                    Assert.assertTrue((abnormalRate == 0.1));
                    Assert.assertTrue((averageAbnormalRate == 0.2));
                } else
                    if (measureResultDetail.getInvocationStatDimension().equals(invocation3)) {
                        Assert.assertTrue(measureState.equals(HEALTH));
                        Assert.assertTrue((abnormalRate == 0.1));
                        Assert.assertTrue((averageAbnormalRate == 0.2));
                    } else
                        if (measureResultDetail.getInvocationStatDimension().equals(invocation4)) {
                            Assert.assertTrue(measureState.equals(HEALTH));
                            Assert.assertTrue((abnormalRate == 0.1));
                            Assert.assertTrue((averageAbnormalRate == 0.2));
                        } else
                            if (measureResultDetail.getInvocationStatDimension().equals(invocation5)) {
                                Assert.assertTrue(measureState.equals(ABNORMAL));
                                Assert.assertTrue((abnormalRate == 0.6));
                                Assert.assertTrue((averageAbnormalRate == 0.2));
                            } else
                                if (measureResultDetail.getInvocationStatDimension().equals(invocation6)) {
                                    Assert.assertTrue(measureState.equals(IGNORE));
                                } else {
                                    Assert.fail("???????????????????");
                                }





        }
    }

    @Test
    public void testLeastWindowCount() {
        FaultToleranceConfig config = new FaultToleranceConfig();
        config.setLeastWindowCount(10);
        config.setLeastWindowExceptionRateMultiple(1.5);
        FaultToleranceConfigManager.putAppConfig(FaultBaseTest.APP_NAME1, config);
        ProviderInfo providerInfo4 = ProviderHelper.toProviderInfo("ip4");
        InvocationStatDimension invocation1 = new InvocationStatDimension(ProviderHelper.toProviderInfo("ip1"), consumerConfig);
        InvocationStatDimension invocation2 = new InvocationStatDimension(ProviderHelper.toProviderInfo("ip2"), consumerConfig);
        InvocationStatDimension invocation3 = new InvocationStatDimension(ProviderHelper.toProviderInfo("ip3"), consumerConfig);
        InvocationStatDimension invocation4 = new InvocationStatDimension(providerInfo4, consumerConfig);
        InvocationStat InvocationStat1 = InvocationStatFactory.getInvocationStat(invocation1);
        InvocationStat InvocationStat2 = InvocationStatFactory.getInvocationStat(invocation2);
        InvocationStat InvocationStat3 = InvocationStatFactory.getInvocationStat(invocation3);
        InvocationStat InvocationStat4 = InvocationStatFactory.getInvocationStat(invocation4);
        MeasureModel measureModel = new MeasureModel(FaultBaseTest.APP_NAME1, "service");
        measureModel.addInvocationStat(InvocationStat1);
        measureModel.addInvocationStat(InvocationStat2);
        measureModel.addInvocationStat(InvocationStat3);
        measureModel.addInvocationStat(InvocationStat4);
        /**
         * ??1-3???10??1-2??1??3??8?
         */
        /**
         * ??4??9????9?
         */
        for (int i = 0; i < 10; i++) {
            InvocationStat1.invoke();
            InvocationStat2.invoke();
            InvocationStat3.invoke();
        }
        InvocationStat1.catchException(new SofaTimeOutException(""));
        InvocationStat2.catchException(new SofaTimeOutException(""));
        for (int i = 0; i < 8; i++) {
            InvocationStat3.catchException(new SofaTimeOutException(""));
        }
        for (int i = 0; i < 9; i++) {
            InvocationStat4.invoke();
            InvocationStat4.catchException(new SofaTimeOutException(""));
        }
        /**
         * ??
         */
        MeasureStrategy measureStrategy = new ServiceHorizontalMeasureStrategy();
        MeasureResult measureResult = measureStrategy.measure(measureModel);
        /**
         * ????
         */
        List<MeasureResultDetail> measureDetais = measureResult.getAllMeasureResultDetails();
        for (MeasureResultDetail measureResultDetail : measureDetais) {
            MeasureState measureState = measureResultDetail.getMeasureState();
            double abnormalRate = measureResultDetail.getAbnormalRate();
            double averageAbnormalRate = measureResultDetail.getAverageAbnormalRate();
            long leastWindowCount = measureResultDetail.getLeastWindowCount();
            long windowCount = measureResultDetail.getWindowCount();
            if (measureResultDetail.getInvocationStatDimension().equals(invocation1)) {
                Assert.assertTrue(measureState.equals(HEALTH));
                Assert.assertTrue((abnormalRate == 0.1));
                Assert.assertTrue((averageAbnormalRate == 0.33));
                Assert.assertTrue((leastWindowCount == 10));
            } else
                if (measureResultDetail.getInvocationStatDimension().equals(invocation2)) {
                    Assert.assertTrue(measureState.equals(HEALTH));
                    Assert.assertTrue((abnormalRate == 0.1));
                    Assert.assertTrue((averageAbnormalRate == 0.33));
                    Assert.assertTrue((leastWindowCount == 10));
                } else
                    if (measureResultDetail.getInvocationStatDimension().equals(invocation3)) {
                        Assert.assertTrue(measureState.equals(ABNORMAL));
                        Assert.assertTrue((abnormalRate == 0.8));
                        Assert.assertTrue((averageAbnormalRate == 0.33));
                        Assert.assertTrue((leastWindowCount == 10));
                    } else
                        if (measureResultDetail.getInvocationStatDimension().equals(invocation4)) {
                            Assert.assertTrue(measureState.equals(IGNORE));
                            Assert.assertTrue((windowCount == 9));
                            Assert.assertTrue((leastWindowCount == 10));
                        } else {
                            Assert.fail("???????????????????");
                        }



        }
        // ??4 ?????????????
        /**
         * ??4????
         */
        ProviderInfoWeightManager.degradeWeight(providerInfo4, 90);
        /**
         * ??1-3???10??1-2??1??3??8?
         */
        /**
         * ??4??9????9?
         */
        for (int i = 0; i < 10; i++) {
            InvocationStat1.invoke();
            InvocationStat2.invoke();
            InvocationStat3.invoke();
        }
        InvocationStat1.catchException(new SofaTimeOutException(""));
        InvocationStat2.catchException(new SofaTimeOutException(""));
        for (int i = 0; i < 8; i++) {
            InvocationStat3.catchException(new SofaTimeOutException(""));
        }
        for (int i = 0; i < 9; i++) {
            InvocationStat4.invoke();
            InvocationStat4.catchException(new SofaTimeOutException(""));
        }
        /**
         * ??
         */
        MeasureResult measureResult2 = measureStrategy.measure(measureModel);
        /**
         * ????
         */
        List<MeasureResultDetail> measureDetais2 = measureResult2.getAllMeasureResultDetails();
        for (MeasureResultDetail measureResultDetail : measureDetais2) {
            MeasureState measureState = measureResultDetail.getMeasureState();
            double abnormalRate = measureResultDetail.getAbnormalRate();
            double averageAbnormalRate = measureResultDetail.getAverageAbnormalRate();
            long leastWindowCount = measureResultDetail.getLeastWindowCount();
            long windowCount = measureResultDetail.getWindowCount();
            if (measureResultDetail.getInvocationStatDimension().equals(invocation1)) {
                Assert.assertEquals(measureState, HEALTH);
                Assert.assertEquals(abnormalRate, 0.1, 0);
                Assert.assertEquals(averageAbnormalRate, 0.49, 0);
                Assert.assertEquals(leastWindowCount, 10);
            } else
                if (measureResultDetail.getInvocationStatDimension().equals(invocation2)) {
                    Assert.assertEquals(measureState, HEALTH);
                    Assert.assertEquals(abnormalRate, 0.1, 0);
                    Assert.assertEquals(averageAbnormalRate, 0.49, 0);
                    Assert.assertEquals(leastWindowCount, 10);
                } else
                    if (measureResultDetail.getInvocationStatDimension().equals(invocation3)) {
                        Assert.assertEquals(measureState, ABNORMAL);
                        Assert.assertEquals(abnormalRate, 0.8, 0);
                        Assert.assertEquals(averageAbnormalRate, 0.49, 0);
                        Assert.assertEquals(leastWindowCount, 10);
                    } else
                        if (measureResultDetail.getInvocationStatDimension().equals(invocation4)) {
                            Assert.assertEquals(measureState, ABNORMAL);
                            Assert.assertEquals(abnormalRate, 1, 0);
                            Assert.assertEquals(averageAbnormalRate, 0.49, 0);
                            Assert.assertTrue((leastWindowCount == 9));
                            Assert.assertEquals(leastWindowCount, 9);
                        } else {
                            Assert.fail("???????????????????");
                        }



        }
    }

    @Test
    public void testAverageExceptionRateEqualTo0Health() {
        FaultToleranceConfig config = new FaultToleranceConfig();
        config.setLeastWindowCount(10);
        config.setLeastWindowExceptionRateMultiple(3.0);
        FaultToleranceConfigManager.putAppConfig(FaultBaseTest.APP_NAME1, config);
        InvocationStatDimension invocation1 = new InvocationStatDimension(ProviderHelper.toProviderInfo("ip1"), consumerConfig);
        InvocationStatDimension invocation2 = new InvocationStatDimension(ProviderHelper.toProviderInfo("ip2"), consumerConfig);
        InvocationStatDimension invocation3 = new InvocationStatDimension(ProviderHelper.toProviderInfo("ip3"), consumerConfig);
        InvocationStat InvocationStat1 = InvocationStatFactory.getInvocationStat(invocation1);
        InvocationStat InvocationStat2 = InvocationStatFactory.getInvocationStat(invocation2);
        InvocationStat InvocationStat3 = InvocationStatFactory.getInvocationStat(invocation3);
        MeasureModel measureModel = new MeasureModel(FaultBaseTest.APP_NAME1, "service");
        measureModel.addInvocationStat(InvocationStat1);
        measureModel.addInvocationStat(InvocationStat2);
        measureModel.addInvocationStat(InvocationStat3);
        /**
         * ??1-3???10????0?
         */
        for (int i = 0; i < 10; i++) {
            InvocationStat1.invoke();
            InvocationStat2.invoke();
            InvocationStat3.invoke();
        }
        /**
         * ??
         */
        MeasureStrategy measureStrategy = new ServiceHorizontalMeasureStrategy();
        MeasureResult measureResult = measureStrategy.measure(measureModel);
        /**
         * ????
         */
        List<MeasureResultDetail> measureDetais = measureResult.getAllMeasureResultDetails();
        for (MeasureResultDetail measureResultDetail : measureDetais) {
            MeasureState measureState = measureResultDetail.getMeasureState();
            double abnormalRate = measureResultDetail.getAbnormalRate();
            double averageAbnormalRate = measureResultDetail.getAverageAbnormalRate();
            if (measureResultDetail.getInvocationStatDimension().equals(invocation1)) {
                Assert.assertTrue(measureState.equals(HEALTH));
                Assert.assertTrue((abnormalRate == 0));
                Assert.assertTrue((averageAbnormalRate == 0));
            } else
                if (measureResultDetail.getInvocationStatDimension().equals(invocation2)) {
                    Assert.assertTrue(measureState.equals(HEALTH));
                    Assert.assertTrue((abnormalRate == 0));
                    Assert.assertTrue((averageAbnormalRate == 0));
                } else
                    if (measureResultDetail.getInvocationStatDimension().equals(invocation3)) {
                        Assert.assertTrue(measureState.equals(HEALTH));
                        Assert.assertTrue((abnormalRate == 0));
                        Assert.assertTrue((averageAbnormalRate == 0));
                    } else {
                        Assert.fail("???????????????????");
                    }


        }
    }

    @Test
    public void testAllNotReachLeastWindowCountIgnore() {
        FaultToleranceConfig config = new FaultToleranceConfig();
        config.setLeastWindowCount(10);
        config.setLeastWindowExceptionRateMultiple(3.0);
        FaultToleranceConfigManager.putAppConfig(FaultBaseTest.APP_NAME1, config);
        InvocationStatDimension invocation1 = new InvocationStatDimension(ProviderHelper.toProviderInfo("ip1"), consumerConfig);
        InvocationStatDimension invocation2 = new InvocationStatDimension(ProviderHelper.toProviderInfo("ip2"), consumerConfig);
        InvocationStatDimension invocation3 = new InvocationStatDimension(ProviderHelper.toProviderInfo("ip3"), consumerConfig);
        InvocationStatDimension invocation4 = new InvocationStatDimension(ProviderHelper.toProviderInfo("ip4"), consumerConfig);
        InvocationStat InvocationStat1 = InvocationStatFactory.getInvocationStat(invocation1);
        InvocationStat InvocationStat2 = InvocationStatFactory.getInvocationStat(invocation2);
        InvocationStat InvocationStat3 = InvocationStatFactory.getInvocationStat(invocation3);
        InvocationStat InvocationStat4 = InvocationStatFactory.getInvocationStat(invocation4);
        MeasureModel measureModel = new MeasureModel(FaultBaseTest.APP_NAME1, "service");
        measureModel.addInvocationStat(InvocationStat1);
        measureModel.addInvocationStat(InvocationStat2);
        measureModel.addInvocationStat(InvocationStat3);
        measureModel.addInvocationStat(InvocationStat4);
        /**
         * ??1-4???9????5?
         */
        for (int i = 0; i < 9; i++) {
            InvocationStat1.invoke();
            InvocationStat2.invoke();
            InvocationStat3.invoke();
            InvocationStat4.invoke();
        }
        for (int i = 0; i < 5; i++) {
            InvocationStat1.catchException(new SofaTimeOutException(""));
            InvocationStat2.catchException(new SofaTimeOutException(""));
            InvocationStat3.catchException(new com.alipay.sofa.rpc.core.exception.SofaRpcException(RpcErrorType.SERVER_BUSY, ""));
            InvocationStat4.catchException(new com.alipay.sofa.rpc.core.exception.SofaRpcException(RpcErrorType.SERVER_BUSY, ""));
        }
        /**
         * ??
         */
        MeasureStrategy measureStrategy = new ServiceHorizontalMeasureStrategy();
        MeasureResult measureResult = measureStrategy.measure(measureModel);
        /**
         * ????
         */
        List<MeasureResultDetail> measureDetais = measureResult.getAllMeasureResultDetails();
        for (MeasureResultDetail measureResultDetail : measureDetais) {
            MeasureState measureState = measureResultDetail.getMeasureState();
            if (measureResultDetail.getInvocationStatDimension().equals(invocation1)) {
                Assert.assertTrue(measureState.equals(IGNORE));
            } else
                if (measureResultDetail.getInvocationStatDimension().equals(invocation2)) {
                    Assert.assertTrue(measureState.equals(IGNORE));
                } else
                    if (measureResultDetail.getInvocationStatDimension().equals(invocation3)) {
                        Assert.assertTrue(measureState.equals(IGNORE));
                    } else
                        if (measureResultDetail.getInvocationStatDimension().equals(invocation4)) {
                            Assert.assertTrue(measureState.equals(IGNORE));
                        } else {
                            Assert.fail("???????????????????");
                        }



        }
    }

    @Test
    public void testCreateMeasureModel() {
        MeasureStrategy measureStrategy = new ServiceHorizontalMeasureStrategy();
        InvocationStatDimension invocation1 = new InvocationStatDimension(ProviderHelper.toProviderInfo("ip1"), consumerConfig);
        InvocationStat invocationStat1 = new com.alipay.sofa.rpc.client.aft.impl.ServiceExceptionInvocationStat(invocation1);
        MeasureModel measureModel1 = measureStrategy.buildMeasureModel(invocationStat1);
        Assert.assertTrue((measureModel1 != null));
        /**
         * ?????????
         */
        InvocationStatDimension invocation2 = new InvocationStatDimension(ProviderHelper.toProviderInfo("ip1"), consumerConfig2);
        InvocationStat invocationStat2 = new com.alipay.sofa.rpc.client.aft.impl.ServiceExceptionInvocationStat(invocation2);
        MeasureModel measureModel2 = measureStrategy.buildMeasureModel(invocationStat2);
        Assert.assertTrue((measureModel2 != null));
        /**
         * ?????????
         */
        InvocationStatDimension invocation3 = new InvocationStatDimension(ProviderHelper.toProviderInfo("ip1"), consumerConfigAnotherApp);
        InvocationStat invocationStat3 = new com.alipay.sofa.rpc.client.aft.impl.ServiceExceptionInvocationStat(invocation3);
        MeasureModel measureModel3 = measureStrategy.buildMeasureModel(invocationStat3);
        Assert.assertTrue((measureModel3 != null));
        /**
         * ????????????IP
         */
        InvocationStatDimension invocation4 = new InvocationStatDimension(ProviderHelper.toProviderInfo("ip2"), consumerConfig);
        InvocationStat invocationStat4 = new com.alipay.sofa.rpc.client.aft.impl.ServiceExceptionInvocationStat(invocation4);
        MeasureModel measureModel4 = measureStrategy.buildMeasureModel(invocationStat4);
        Assert.assertTrue((measureModel4 == null));
        Assert.assertTrue(measureModel1.getInvocationStats().contains(invocationStat4));
        /**
         * ????????????IP
         */
        InvocationStatDimension invocation5 = new InvocationStatDimension(ProviderHelper.toProviderInfo("ip1"), consumerConfig);
        InvocationStat invocationStat5 = new com.alipay.sofa.rpc.client.aft.impl.ServiceExceptionInvocationStat(invocation5);
        MeasureModel measureModel5 = measureStrategy.buildMeasureModel(invocationStat5);
        Assert.assertTrue((measureModel5 == null));
        Assert.assertTrue(measureModel1.getInvocationStats().contains(invocationStat1));
        Assert.assertTrue(measureModel1.getInvocationStats().contains(invocationStat5));
    }

    @Test
    public void concurrentTestCreateMeasureModel() throws InterruptedException {
        final MeasureStrategy measureStrategy = new ServiceHorizontalMeasureStrategy();
        final AtomicInteger isNullCount = new AtomicInteger(0);
        final CountDownLatch countDownLatch = new CountDownLatch(20);
        for (int i = 0; i < 20; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    InvocationStatDimension invocation1 = new InvocationStatDimension(ProviderHelper.toProviderInfo("ip1"), consumerConfig);
                    MeasureModel measureModel1 = measureStrategy.buildMeasureModel(new com.alipay.sofa.rpc.client.aft.impl.ServiceExceptionInvocationStat(invocation1));
                    if (measureModel1 == null) {
                        isNullCount.incrementAndGet();
                    }
                    countDownLatch.countDown();
                }
            }).start();
        }
        countDownLatch.await();
        Assert.assertTrue(((isNullCount.get()) == 19));
    }
}

