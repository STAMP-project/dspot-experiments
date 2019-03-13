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
package org.apache.dubbo.metadata.support;


import MetadataIdentifier.KeyTypeEnum.UNIQUE_KEY;
import com.google.gson.Gson;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.utils.NetUtils;
import org.apache.dubbo.metadata.definition.model.FullServiceDefinition;
import org.apache.dubbo.metadata.identifier.MetadataIdentifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 */
public class AbstractMetadataReportTest {
    private AbstractMetadataReportTest.NewMetadataReport abstractMetadataReport;

    @Test
    public void testGetProtocol() {
        URL url = URL.valueOf((("dubbo://" + (NetUtils.getLocalAddress().getHostName())) + ":4444/org.apache.dubbo.TestService?version=1.0.0&application=vic&side=provider"));
        String protocol = abstractMetadataReport.getProtocol(url);
        Assertions.assertEquals(protocol, "provider");
        URL url2 = URL.valueOf((("consumer://" + (NetUtils.getLocalAddress().getHostName())) + ":4444/org.apache.dubbo.TestService?version=1.0.0&application=vic"));
        String protocol2 = abstractMetadataReport.getProtocol(url2);
        Assertions.assertEquals(protocol2, "consumer");
    }

    @Test
    public void testStoreProviderUsual() throws ClassNotFoundException, InterruptedException {
        String interfaceName = "org.apache.dubbo.metadata.integration.InterfaceNameTestService";
        String version = "1.0.0";
        String group = null;
        String application = "vic";
        MetadataIdentifier providerMetadataIdentifier = storePrivider(abstractMetadataReport, interfaceName, version, group, application);
        Thread.sleep(1500);
        Assertions.assertNotNull(abstractMetadataReport.store.get(providerMetadataIdentifier.getUniqueKey(UNIQUE_KEY)));
    }

    @Test
    public void testStoreProviderSync() throws ClassNotFoundException, InterruptedException {
        String interfaceName = "org.apache.dubbo.metadata.integration.InterfaceNameTestService";
        String version = "1.0.0";
        String group = null;
        String application = "vic";
        abstractMetadataReport.syncReport = true;
        MetadataIdentifier providerMetadataIdentifier = storePrivider(abstractMetadataReport, interfaceName, version, group, application);
        Assertions.assertNotNull(abstractMetadataReport.store.get(providerMetadataIdentifier.getUniqueKey(UNIQUE_KEY)));
    }

    @Test
    public void testFileExistAfterPut() throws ClassNotFoundException, InterruptedException {
        // just for one method
        URL singleUrl = URL.valueOf((("redis://" + (NetUtils.getLocalAddress().getHostName())) + ":4444/org.apache.dubbo.metadata.integration.InterfaceNameTestService?version=1.0.0&application=singleTest"));
        AbstractMetadataReportTest.NewMetadataReport singleMetadataReport = new AbstractMetadataReportTest.NewMetadataReport(singleUrl);
        Assertions.assertFalse(singleMetadataReport.file.exists());
        String interfaceName = "org.apache.dubbo.metadata.integration.InterfaceNameTestService";
        String version = "1.0.0";
        String group = null;
        String application = "vic";
        MetadataIdentifier providerMetadataIdentifier = storePrivider(singleMetadataReport, interfaceName, version, group, application);
        Thread.sleep(2000);
        Assertions.assertTrue(singleMetadataReport.file.exists());
        Assertions.assertTrue(singleMetadataReport.properties.containsKey(providerMetadataIdentifier.getUniqueKey(UNIQUE_KEY)));
    }

    @Test
    public void testRetry() throws ClassNotFoundException, InterruptedException {
        String interfaceName = "org.apache.dubbo.metadata.integration.RetryTestService";
        String version = "1.0.0.retry";
        String group = null;
        String application = "vic.retry";
        URL storeUrl = URL.valueOf((("retryReport://" + (NetUtils.getLocalAddress().getHostName())) + ":4444/org.apache.dubbo.TestServiceForRetry?version=1.0.0.retry&application=vic.retry"));
        AbstractMetadataReportTest.RetryMetadataReport retryReport = new AbstractMetadataReportTest.RetryMetadataReport(storeUrl, 2);
        retryReport.metadataReportRetry.retryPeriod = 400L;
        URL url = URL.valueOf((("dubbo://" + (NetUtils.getLocalAddress().getHostName())) + ":4444/org.apache.dubbo.TestService?version=1.0.0&application=vic"));
        Assertions.assertNull(retryReport.metadataReportRetry.retryScheduledFuture);
        Assertions.assertTrue(((retryReport.metadataReportRetry.retryCounter.get()) == 0));
        Assertions.assertTrue(retryReport.store.isEmpty());
        Assertions.assertTrue(retryReport.failedReports.isEmpty());
        storePrivider(retryReport, interfaceName, version, group, application);
        Thread.sleep(150);
        Assertions.assertTrue(retryReport.store.isEmpty());
        Assertions.assertFalse(retryReport.failedReports.isEmpty());
        Assertions.assertNotNull(retryReport.metadataReportRetry.retryScheduledFuture);
        Thread.sleep(2000L);
        Assertions.assertTrue(((retryReport.metadataReportRetry.retryCounter.get()) != 0));
        Assertions.assertTrue(((retryReport.metadataReportRetry.retryCounter.get()) >= 3));
        Assertions.assertFalse(retryReport.store.isEmpty());
        Assertions.assertTrue(retryReport.failedReports.isEmpty());
    }

    @Test
    public void testRetryCancel() throws ClassNotFoundException, InterruptedException {
        String interfaceName = "org.apache.dubbo.metadata.integration.RetryTestService";
        String version = "1.0.0.retrycancel";
        String group = null;
        String application = "vic.retry";
        URL storeUrl = URL.valueOf((("retryReport://" + (NetUtils.getLocalAddress().getHostName())) + ":4444/org.apache.dubbo.TestServiceForRetryCancel?version=1.0.0.retrycancel&application=vic.retry"));
        AbstractMetadataReportTest.RetryMetadataReport retryReport = new AbstractMetadataReportTest.RetryMetadataReport(storeUrl, 2);
        retryReport.metadataReportRetry.retryPeriod = 150L;
        retryReport.metadataReportRetry.retryTimesIfNonFail = 2;
        storePrivider(retryReport, interfaceName, version, group, application);
        Thread.sleep(80);
        Assertions.assertFalse(retryReport.metadataReportRetry.retryScheduledFuture.isCancelled());
        Assertions.assertFalse(retryReport.metadataReportRetry.retryExecutor.isShutdown());
        Thread.sleep(1000L);
        Assertions.assertTrue(retryReport.metadataReportRetry.retryScheduledFuture.isCancelled());
        Assertions.assertTrue(retryReport.metadataReportRetry.retryExecutor.isShutdown());
    }

    @Test
    public void testPublishAll() throws ClassNotFoundException, InterruptedException {
        Assertions.assertTrue(abstractMetadataReport.store.isEmpty());
        Assertions.assertTrue(abstractMetadataReport.allMetadataReports.isEmpty());
        String interfaceName = "org.apache.dubbo.metadata.integration.InterfaceNameTestService";
        String version = "1.0.0";
        String group = null;
        String application = "vic";
        MetadataIdentifier providerMetadataIdentifier1 = storePrivider(abstractMetadataReport, interfaceName, version, group, application);
        Thread.sleep(1000);
        Assertions.assertEquals(abstractMetadataReport.allMetadataReports.size(), 1);
        Assertions.assertTrue(getParameters().containsKey("testPKey"));
        MetadataIdentifier providerMetadataIdentifier2 = storePrivider(abstractMetadataReport, interfaceName, (version + "_2"), (group + "_2"), application);
        Thread.sleep(1000);
        Assertions.assertEquals(abstractMetadataReport.allMetadataReports.size(), 2);
        Assertions.assertTrue(getParameters().containsKey("testPKey"));
        Assertions.assertEquals(getParameters().get("version"), (version + "_2"));
        Map<String, String> tmpMap = new HashMap<>();
        tmpMap.put("testKey", "value");
        MetadataIdentifier consumerMetadataIdentifier = storeConsumer(abstractMetadataReport, interfaceName, (version + "_3"), (group + "_3"), application, tmpMap);
        Thread.sleep(1000);
        Assertions.assertEquals(abstractMetadataReport.allMetadataReports.size(), 3);
        Map tmpMapResult = ((Map) (abstractMetadataReport.allMetadataReports.get(consumerMetadataIdentifier)));
        Assertions.assertEquals(tmpMapResult.get("testPKey"), "9090");
        Assertions.assertEquals(tmpMapResult.get("testKey"), "value");
        Assertions.assertTrue(((abstractMetadataReport.store.size()) == 3));
        abstractMetadataReport.store.clear();
        Assertions.assertTrue(((abstractMetadataReport.store.size()) == 0));
        publishAll();
        Thread.sleep(200);
        Assertions.assertTrue(((abstractMetadataReport.store.size()) == 3));
        String v = abstractMetadataReport.store.get(providerMetadataIdentifier1.getUniqueKey(UNIQUE_KEY));
        Gson gson = new Gson();
        FullServiceDefinition data = gson.fromJson(v, FullServiceDefinition.class);
        checkParam(data.getParameters(), application, version);
        String v2 = abstractMetadataReport.store.get(providerMetadataIdentifier2.getUniqueKey(UNIQUE_KEY));
        gson = new Gson();
        data = gson.fromJson(v2, FullServiceDefinition.class);
        checkParam(data.getParameters(), application, (version + "_2"));
        String v3 = abstractMetadataReport.store.get(consumerMetadataIdentifier.getUniqueKey(UNIQUE_KEY));
        gson = new Gson();
        Map v3Map = gson.fromJson(v3, Map.class);
        checkParam(v3Map, application, (version + "_3"));
    }

    @Test
    public void testCalculateStartTime() {
        for (int i = 0; i < 300; i++) {
            long t = (calculateStartTime()) + (System.currentTimeMillis());
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(t);
            Assertions.assertTrue(((c.get(Calendar.HOUR_OF_DAY)) >= 2));
            Assertions.assertTrue(((c.get(Calendar.HOUR_OF_DAY)) <= 6));
        }
    }

    private static class NewMetadataReport extends AbstractMetadataReport {
        Map<String, String> store = new ConcurrentHashMap<>();

        public NewMetadataReport(URL metadataReportURL) {
            super(metadataReportURL);
        }

        @Override
        protected void doStoreProviderMetadata(MetadataIdentifier providerMetadataIdentifier, String serviceDefinitions) {
            store.put(providerMetadataIdentifier.getUniqueKey(UNIQUE_KEY), serviceDefinitions);
        }

        @Override
        protected void doStoreConsumerMetadata(MetadataIdentifier consumerMetadataIdentifier, String serviceParameterString) {
            store.put(consumerMetadataIdentifier.getUniqueKey(UNIQUE_KEY), serviceParameterString);
        }
    }

    private static class RetryMetadataReport extends AbstractMetadataReport {
        Map<String, String> store = new ConcurrentHashMap<>();

        int needRetryTimes;

        int executeTimes = 0;

        public RetryMetadataReport(URL metadataReportURL, int needRetryTimes) {
            super(metadataReportURL);
            this.needRetryTimes = needRetryTimes;
        }

        @Override
        protected void doStoreProviderMetadata(MetadataIdentifier providerMetadataIdentifier, String serviceDefinitions) {
            ++(executeTimes);
            System.out.println(((("***" + (executeTimes)) + ";") + (System.currentTimeMillis())));
            if ((executeTimes) <= (needRetryTimes)) {
                throw new RuntimeException(("must retry:" + (executeTimes)));
            }
            store.put(providerMetadataIdentifier.getUniqueKey(UNIQUE_KEY), serviceDefinitions);
        }

        @Override
        protected void doStoreConsumerMetadata(MetadataIdentifier consumerMetadataIdentifier, String serviceParameterString) {
            ++(executeTimes);
            if ((executeTimes) <= (needRetryTimes)) {
                throw new RuntimeException(("must retry:" + (executeTimes)));
            }
            store.put(consumerMetadataIdentifier.getUniqueKey(UNIQUE_KEY), serviceParameterString);
        }
    }
}

