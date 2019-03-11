/**
 * Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.weibo.api.motan.cluster;


import MotanConstants.NODE_TYPE_SERVICE;
import URLParamType.application;
import URLParamType.check;
import URLParamType.module;
import URLParamType.nodeType;
import com.weibo.api.motan.cluster.support.ClusterSupport;
import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.protocol.example.IHello;
import com.weibo.api.motan.registry.Registry;
import com.weibo.api.motan.registry.RegistryService;
import com.weibo.api.motan.rpc.Protocol;
import com.weibo.api.motan.rpc.Referer;
import com.weibo.api.motan.rpc.URL;
import com.weibo.api.motan.util.NetUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;


/**
 * ClusterSupport test.
 *
 * @author fishermen
 * @version V1.0 created at: 2013-6-22
 */
public class ClusterSupportTest {
    private static JUnit4Mockery mockery = new JUnit4Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private static ClusterSupport<IHello> clusterSupport;

    private static Protocol protocol = ClusterSupportTest.mockery.mock(Protocol.class);

    private static Map<String, Registry> registries = new HashMap<String, Registry>();

    private static String regProtocol1 = "reg_1";

    private static String regProtocol2 = "reg_2";

    private static String localAddress = NetUtils.getLocalAddress().getHostAddress();

    private static Map<String, Referer<IHello>> portReferers = new HashMap<String, Referer<IHello>>();

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testNotify() {
        // ???registry1 ????2?url
        final int urlsCount = 5;
        final List<URL> serviceUrls1 = new ArrayList<URL>();
        for (int i = 0; i < urlsCount; i++) {
            URL url = new URL(MotanConstants.PROTOCOL_MOTAN, ClusterSupportTest.localAddress, (1000 + i), IHello.class.getName());
            url.addParameter(nodeType.getName(), NODE_TYPE_SERVICE);
            serviceUrls1.add(url);
        }
        final URL reg1Url = new URL("reg_protocol_1", NetUtils.getLocalAddress().getHostAddress(), 0, RegistryService.class.getName());
        final URL reg2Url = new URL("reg_protocol_2", NetUtils.getLocalAddress().getHostAddress(), 0, RegistryService.class.getName());
        ClusterSupportTest.mockery.checking(new Expectations() {
            {
                for (int i = 0; i < urlsCount; i++) {
                    URL serviceUrl = serviceUrls1.get(i).createCopy();
                    URL refererUrl = serviceUrls1.get(i).createCopy();
                    String application = serviceUrl.getParameter(application.getName(), application.getValue());
                    String module = serviceUrl.getParameter(module.getName(), module.getValue());
                    refererUrl.addParameters(serviceUrl.getParameters());
                    refererUrl.addParameter(application.getName(), application);
                    refererUrl.addParameter(module.getName(), module);
                    refererUrl.addParameter(check.getName(), "false");
                    atLeast(1).of(ClusterSupportTest.protocol).refer(IHello.class, refererUrl, serviceUrl);
                    will(returnValue(mockReferer(refererUrl)));
                    atLeast(1).of(mockReferer(refererUrl)).getUrl();
                    will(returnValue(serviceUrls1.get(i)));
                    atLeast(1).of(mockReferer(refererUrl)).getServiceUrl();
                    will(returnValue(serviceUrls1.get(i)));
                }
                for (int i = 0; i < 3; i++) {
                    atLeast(1).of(mockReferer(serviceUrls1.get(i))).destroy();
                }
                atLeast(1).of(ClusterSupportTest.registries.get(ClusterSupportTest.regProtocol1)).getUrl();
                will(returnValue(reg1Url));
                atLeast(1).of(ClusterSupportTest.registries.get(ClusterSupportTest.regProtocol2)).getUrl();
                will(returnValue(reg2Url));
            }
        });
        List copy = new ArrayList<URL>();
        ClusterSupportTest.clusterSupport.notify(ClusterSupportTest.registries.get(ClusterSupportTest.regProtocol1).getUrl(), copy(copy, serviceUrls1.subList(0, 2)));
        Assert.assertEquals(ClusterSupportTest.clusterSupport.getCluster().getReferers().size(), 2);
        // ???registry1 ?????
        ClusterSupportTest.clusterSupport.notify(ClusterSupportTest.registries.get(ClusterSupportTest.regProtocol1).getUrl(), copy(copy, serviceUrls1.subList(0, 3)));
        Assert.assertEquals(ClusterSupportTest.clusterSupport.getCluster().getReferers().size(), 3);
        // ???registr1 ???0?
        ClusterSupportTest.clusterSupport.notify(ClusterSupportTest.registries.get(ClusterSupportTest.regProtocol1).getUrl(), copy(copy, serviceUrls1.subList(0, 0)));
        Assert.assertEquals(ClusterSupportTest.clusterSupport.getCluster().getReferers().size(), 3);
        // ???registr1 ???2??????
        ClusterSupportTest.clusterSupport.notify(ClusterSupportTest.registries.get(ClusterSupportTest.regProtocol1).getUrl(), copy(copy, serviceUrls1.subList(1, 3)));
        Assert.assertEquals(ClusterSupportTest.clusterSupport.getCluster().getReferers().size(), 2);
        // ???registry1 ?????
        ClusterSupportTest.clusterSupport.notify(ClusterSupportTest.registries.get(ClusterSupportTest.regProtocol1).getUrl(), copy(copy, serviceUrls1.subList(0, 3)));
        Assert.assertEquals(ClusterSupportTest.clusterSupport.getCluster().getReferers().size(), 3);
        // ??registry2????2?
        ClusterSupportTest.clusterSupport.notify(ClusterSupportTest.registries.get(ClusterSupportTest.regProtocol2).getUrl(), copy(copy, serviceUrls1.subList(3, 5)));
        Assert.assertEquals(ClusterSupportTest.clusterSupport.getCluster().getReferers().size(), 5);
        // ???registr1 ???2??????
        ClusterSupportTest.clusterSupport.notify(ClusterSupportTest.registries.get(ClusterSupportTest.regProtocol1).getUrl(), copy(copy, serviceUrls1.subList(1, 3)));
        Assert.assertEquals(ClusterSupportTest.clusterSupport.getCluster().getReferers().size(), 4);
        // ???registr1 ???registry1??url?
        ClusterSupportTest.clusterSupport.notify(ClusterSupportTest.registries.get(ClusterSupportTest.regProtocol1).getUrl(), null);
        Assert.assertEquals(ClusterSupportTest.clusterSupport.getCluster().getReferers().size(), 2);
        List<Referer<IHello>> oldReferers = ClusterSupportTest.clusterSupport.getCluster().getReferers();
        // ??registry2????2?
        ClusterSupportTest.clusterSupport.notify(ClusterSupportTest.registries.get(ClusterSupportTest.regProtocol2).getUrl(), copy(copy, serviceUrls1.subList(3, 5)));
        Assert.assertEquals(ClusterSupportTest.clusterSupport.getCluster().getReferers().size(), 2);
        for (Referer<IHello> referer : ClusterSupportTest.clusterSupport.getCluster().getReferers()) {
            if (!(oldReferers.contains(referer))) {
                Assert.assertTrue(false);
            }
        }
    }

    private static class ClusterSupportMask<T> extends ClusterSupport<T> {
        public ClusterSupportMask(Class<T> interfaceClass, List<URL> registryUrls) {
            super(interfaceClass, registryUrls);
        }

        @Override
        protected Protocol getDecorateProtocol(String protocolName) {
            return ClusterSupportTest.protocol;
        }

        @Override
        protected Registry getRegistry(URL url) {
            return ClusterSupportTest.registries.get(url.getProtocol());
        }
    }
}

