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
package com.alipay.sofa.rpc.registry.zk;


import ProviderInfoAttrs.ATTR_START_TIME;
import ProviderInfoAttrs.ATTR_WARMUP_TIME;
import ProviderInfoAttrs.ATTR_WARMUP_WEIGHT;
import ProviderInfoAttrs.ATTR_WARM_UP_END_TIME;
import ProviderInfoAttrs.ATTR_WEIGHT;
import ProviderStatus.AVAILABLE;
import ProviderStatus.WARMING_UP;
import com.alipay.sofa.rpc.client.ProviderHelper;
import com.alipay.sofa.rpc.client.ProviderInfo;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alipay.sofa.rpc.log.Logger;
import com.alipay.sofa.rpc.log.LoggerFactory;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:lw111072@antfin.com">LiWei.Liangen</a>
 */
public class ZookeeperRegistryHelperTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperRegistryHelperTest.class);

    @Test
    public void testWarmup() throws UnsupportedEncodingException, InterruptedException {
        long now = System.currentTimeMillis();
        ProviderInfo providerInfo = new ProviderInfo().setWeight(200).setStaticAttr(ATTR_WEIGHT, "200").setStaticAttr(ATTR_START_TIME, String.valueOf(now)).setStaticAttr(ATTR_WARMUP_TIME, String.valueOf(200)).setStaticAttr(ATTR_WARMUP_WEIGHT, String.valueOf(700));
        ZookeeperRegistryHelper.processWarmUpWeight(providerInfo);
        Assert.assertEquals("200", providerInfo.getStaticAttr(ATTR_WEIGHT));
        Assert.assertEquals((now + ""), providerInfo.getStaticAttr(ATTR_START_TIME));
        Assert.assertEquals(null, providerInfo.getStaticAttr(ATTR_WARMUP_TIME));
        Assert.assertEquals(null, providerInfo.getStaticAttr(ATTR_WARMUP_WEIGHT));
        Assert.assertEquals((now + 200), providerInfo.getDynamicAttr(ATTR_WARM_UP_END_TIME));
        Assert.assertEquals(700, providerInfo.getDynamicAttr(ATTR_WARMUP_WEIGHT));
        Assert.assertEquals(WARMING_UP, providerInfo.getStatus());
        Assert.assertEquals(700, providerInfo.getWeight());
        long elapsed = (System.currentTimeMillis()) - now;
        ZookeeperRegistryHelperTest.LOGGER.info((("elapsed time: " + elapsed) + "ms"));
        long sleepTime = 300 - elapsed;
        if (sleepTime >= 0) {
            Thread.sleep(sleepTime);
        }
        Assert.assertEquals(AVAILABLE, providerInfo.getStatus());
        Assert.assertEquals(200, providerInfo.getWeight());
    }

    @Test
    public void testNoWarmupTime() throws InterruptedException {
        long now = System.currentTimeMillis();
        ProviderInfo providerInfo = new ProviderInfo().setWeight(300).setStaticAttr(ATTR_WEIGHT, "300").setStaticAttr(ATTR_START_TIME, String.valueOf(now)).setStaticAttr(ATTR_WARMUP_WEIGHT, String.valueOf(800));
        ZookeeperRegistryHelper.processWarmUpWeight(providerInfo);
        Assert.assertEquals("300", providerInfo.getStaticAttr(ATTR_WEIGHT));
        Assert.assertEquals((now + ""), providerInfo.getStaticAttr(ATTR_START_TIME));
        Assert.assertEquals(null, providerInfo.getStaticAttr(ATTR_WARMUP_TIME));
        Assert.assertEquals(null, providerInfo.getStaticAttr(ATTR_WARMUP_WEIGHT));
        Assert.assertEquals(null, providerInfo.getDynamicAttr(ATTR_WARM_UP_END_TIME));
        Assert.assertEquals(null, providerInfo.getDynamicAttr(ATTR_WARMUP_WEIGHT));
        Assert.assertEquals(AVAILABLE, providerInfo.getStatus());
        Assert.assertEquals(300, providerInfo.getWeight());
    }

    @Test
    public void testNoWarmupWeight() throws InterruptedException {
        long now = System.currentTimeMillis();
        ProviderInfo providerInfo = new ProviderInfo().setWeight(600).setStaticAttr(ATTR_WEIGHT, "600").setStaticAttr(ATTR_START_TIME, String.valueOf(now)).setStaticAttr(ATTR_WARMUP_TIME, String.valueOf(30));
        ZookeeperRegistryHelper.processWarmUpWeight(providerInfo);
        Assert.assertEquals("600", providerInfo.getStaticAttr(ATTR_WEIGHT));
        Assert.assertEquals((now + ""), providerInfo.getStaticAttr(ATTR_START_TIME));
        Assert.assertEquals(null, providerInfo.getStaticAttr(ATTR_WARMUP_TIME));
        Assert.assertEquals(null, providerInfo.getStaticAttr(ATTR_WARMUP_WEIGHT));
        Assert.assertEquals(null, providerInfo.getDynamicAttr(ATTR_WARM_UP_END_TIME));
        Assert.assertEquals(null, providerInfo.getDynamicAttr(ATTR_WARMUP_WEIGHT));
        Assert.assertEquals(AVAILABLE, providerInfo.getStatus());
        Assert.assertEquals(600, providerInfo.getWeight());
    }

    @Test
    public void testCustomParams() {
        ProviderConfig providerConfig = new ProviderConfig();
        Map<String, String> map = new HashMap<String, String>();
        map.put("x", "y");
        map.put("a", "b");
        providerConfig.setParameters(map);
        ServerConfig server = new ServerConfig();
        providerConfig.setServer(server);
        List<String> urls = ZookeeperRegistryHelper.convertProviderToUrls(providerConfig);
        ZookeeperRegistryHelperTest.LOGGER.info(urls.toString());
        Assert.assertNotNull(urls);
        Assert.assertEquals(1, urls.size());
        String url = urls.get(0);
        ProviderInfo providerInfo = ProviderHelper.toProviderInfo(url);
        ZookeeperRegistryHelperTest.LOGGER.info(providerInfo.toString());
        Assert.assertEquals("b", providerInfo.getStaticAttr("a"));
        Assert.assertEquals("y", providerInfo.getStaticAttr("x"));
    }
}

