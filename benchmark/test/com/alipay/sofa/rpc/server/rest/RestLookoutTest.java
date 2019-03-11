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
package com.alipay.sofa.rpc.server.rest;


import com.alipay.lookout.api.Metric;
import com.alipay.lookout.api.Tag;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alipay.sofa.rpc.log.Logger;
import com.alipay.sofa.rpc.log.LoggerFactory;
import com.alipay.sofa.rpc.test.ActivelyDestroyTest;
import java.util.concurrent.TimeUnit;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:lw111072@antfin.com">LiWei.Liangen</a>
 */
public class RestLookoutTest extends ActivelyDestroyTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestLookoutTest.class);

    private static ServerConfig serverConfig;

    private ProviderConfig<RestService> providerConfig;

    private ConsumerConfig<RestService> consumerConfig;

    /**
     * test provider service stats
     */
    @Test
    public void testProviderServiceStats() {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Metric metric = fetchWithName("rpc.provider.service.stats");
        for (Tag tag : metric.id().tags()) {
            if (tag.key().equalsIgnoreCase("method")) {
                String methodName = tag.value();
                if (methodName.equals("query")) {
                    assertMethod(metric, true, 2, "query", 0, 0);
                } else {
                    System.out.println(("provider do not fix,methodName=" + methodName));
                }
            }
        }
    }

    /**
     * test consumer service stats
     */
    @Test
    public void testConsumerServiceStats() {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Metric metric = fetchWithName("rpc.consumer.service.stats");
        for (Tag tag : metric.id().tags()) {
            if (tag.key().equalsIgnoreCase("method")) {
                String methodName = tag.value();
                if (methodName.equals("query")) {
                    assertMethod(metric, false, 2, "query", 1203, 352);
                } else {
                    System.out.println("consumer do not fix");
                }
            }
        }
    }
}

