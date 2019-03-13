/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.geode.distributed.internal;


import java.util.Set;
import junitparams.JUnitParamsRunner;
import org.apache.geode.cache.configuration.CacheConfig;
import org.apache.geode.cache.configuration.JndiBindingsType;
import org.apache.geode.cache.configuration.RegionConfig;
import org.apache.geode.internal.config.JAXBServiceTest;
import org.apache.geode.internal.config.JAXBServiceTest.ElementOne;
import org.apache.geode.internal.config.JAXBServiceTest.ElementTwo;
import org.apache.geode.internal.lang.SystemPropertyHelper;
import org.apache.geode.management.internal.configuration.domain.Configuration;
import org.apache.geode.management.internal.configuration.utils.XmlUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.w3c.dom.Document;


@RunWith(JUnitParamsRunner.class)
public class InternalConfigurationPersistenceServiceTest {
    private InternalConfigurationPersistenceService service;

    private InternalConfigurationPersistenceService service2;

    private Configuration configuration;

    @Rule
    public RestoreSystemProperties restore = new RestoreSystemProperties();

    @Test
    public void updateRegionConfig() {
        service.updateCacheConfig("cluster", ( cacheConfig) -> {
            RegionConfig regionConfig = new RegionConfig();
            regionConfig.setName("regionA");
            regionConfig.setType("REPLICATE");
            cacheConfig.getRegions().add(regionConfig);
            return cacheConfig;
        });
        System.out.println(configuration.getCacheXmlContent());
        assertThat(configuration.getCacheXmlContent()).contains("<region name=\"regionA\" refid=\"REPLICATE\">");
    }

    @Test
    public void jndiBindings() {
        service.updateCacheConfig("cluster", ( cacheConfig) -> {
            JndiBindingsType.JndiBinding jndiBinding = new JndiBindingsType.JndiBinding();
            jndiBinding.setJndiName("jndiOne");
            jndiBinding.setJdbcDriverClass("com.sun.ABC");
            jndiBinding.setType("SimpleDataSource");
            jndiBinding.getConfigProperties().add(new JndiBindingsType.JndiBinding.ConfigProperty("test", "test", "test"));
            cacheConfig.getJndiBindings().add(jndiBinding);
            return cacheConfig;
        });
        assertThat(configuration.getCacheXmlContent()).containsOnlyOnce("</jndi-bindings>");
        assertThat(configuration.getCacheXmlContent()).contains("<jndi-binding jdbc-driver-class=\"com.sun.ABC\" jndi-name=\"jndiOne\" type=\"SimpleDataSource\">");
        assertThat(configuration.getCacheXmlContent()).contains("config-property-name>test</config-property-name>");
    }

    @Test
    public void addCustomCacheElement() {
        JAXBServiceTest.ElementOne customOne = new JAXBServiceTest.ElementOne("testOne");
        service.updateCacheConfig("cluster", ( config) -> {
            config.getCustomCacheElements().add(customOne);
            return config;
        });
        System.out.println(configuration.getCacheXmlContent());
        assertThat(configuration.getCacheXmlContent()).contains("custom-one>");
        JAXBServiceTest.ElementTwo customTwo = new JAXBServiceTest.ElementTwo("testTwo");
        service.updateCacheConfig("cluster", ( config) -> {
            config.getCustomCacheElements().add(customTwo);
            return config;
        });
        System.out.println(configuration.getCacheXmlContent());
        assertThat(configuration.getCacheXmlContent()).contains("custom-one>");
        assertThat(configuration.getCacheXmlContent()).contains("custom-two>");
    }

    // in case a locator in the cluster doesn't have the plugin installed
    @Test
    public void xmlWithCustomElementsCanBeUnMarshalledByAnotherService() {
        service.updateCacheConfig("cluster", ( config) -> {
            config.getCustomCacheElements().add(new ElementOne("one"));
            config.getCustomCacheElements().add(new ElementTwo("two"));
            return config;
        });
        String prettyXml = configuration.getCacheXmlContent();
        System.out.println(prettyXml);
        // the xml is sent to another locator with no such plugin installed, it can be parsed
        // but the element couldn't be recognized by the locator without the plugin
        service2.updateCacheConfig("cluster", ( cc) -> cc);
        CacheConfig config = service2.getCacheConfig("cluster");
        assertThat(config.findCustomCacheElement("one", JAXBServiceTest.ElementOne.class)).isNull();
        String uglyXml = configuration.getCacheXmlContent();
        System.out.println(uglyXml);
        assertThat(uglyXml).isNotEqualTo(prettyXml);
        // the xml can be unmarshalled correctly by the first locator
        CacheConfig cacheConfig = service.getCacheConfig("cluster");
        service.updateCacheConfig("cluster", ( cc) -> cc);
        assertThat(cacheConfig.getCustomCacheElements()).hasSize(2);
        assertThat(cacheConfig.getCustomCacheElements().get(0)).isInstanceOf(JAXBServiceTest.ElementOne.class);
        assertThat(cacheConfig.getCustomCacheElements().get(1)).isInstanceOf(JAXBServiceTest.ElementTwo.class);
        assertThat(configuration.getCacheXmlContent()).isEqualTo(prettyXml);
    }

    @Test
    public void getNonExistingGroupConfigShouldReturnNull() {
        assertThat(service.getCacheConfig("non-existing-group")).isNull();
    }

    @Test
    public void getExistingGroupConfigShouldReturnNullIfNoXml() {
        Configuration groupConfig = new Configuration("some-new-group");
        Mockito.doReturn(groupConfig).when(service).getConfiguration("some-new-group");
        CacheConfig groupCacheConfig = service.getCacheConfig("some-new-group");
        assertThat(groupCacheConfig).isNull();
    }

    @Test
    public void updateShouldInsertIfNotExist() {
        Mockito.doCallRealMethod().when(service).updateCacheConfig(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doCallRealMethod().when(service).getCacheConfig(ArgumentMatchers.any());
        org.apache.geode.cache.Region region = Mockito.mock(org.apache.geode.cache.Region.class);
        Mockito.doReturn(region).when(service).getConfigurationRegion();
        service.updateCacheConfig("non-existing-group", ( cc) -> cc);
        Mockito.verify(region).put(ArgumentMatchers.eq("non-existing-group"), ArgumentMatchers.any());
    }

    @Test
    public void getPackagesToScanWithoutSystemProperty() {
        Set<String> packages = service.getPackagesToScan();
        assertThat(packages).containsExactly("*");
    }

    @Test
    public void getPackagesToScanWithSystemProperty() {
        System.setProperty(("geode." + (SystemPropertyHelper.PACKAGES_TO_SCAN)), "org.apache.geode,io.pivotal");
        Set<String> packages = service.getPackagesToScan();
        assertThat(packages).containsExactly("org.apache.geode", "io.pivotal");
    }

    @Test
    public void updateGatewayReceiverConfig() {
        service.updateCacheConfig("cluster", ( cacheConfig) -> {
            CacheConfig.GatewayReceiver receiver = new CacheConfig.GatewayReceiver();
            cacheConfig.setGatewayReceiver(receiver);
            return cacheConfig;
        });
        System.out.println(configuration.getCacheXmlContent());
        assertThat(configuration.getCacheXmlContent()).contains("<gateway-receiver/>");
    }

    @Test
    public void removeDuplicateGatewayReceiversWithDefaultProperties() throws Exception {
        Document document = XmlUtils.createDocumentFromXml(getDuplicateReceiversWithDefaultPropertiesXml());
        System.out.println(("Initial document:\n" + (XmlUtils.prettyXml(document))));
        assertThat(document.getElementsByTagName("gateway-receiver").getLength()).isEqualTo(2);
        service.removeDuplicateGatewayReceivers(document);
        System.out.println(("Processed document:\n" + (XmlUtils.prettyXml(document))));
        assertThat(document.getElementsByTagName("gateway-receiver").getLength()).isEqualTo(1);
    }

    @Test
    public void removeInvalidGatewayReceiversWithDifferentHostNameForSenders() throws Exception {
        Document document = XmlUtils.createDocumentFromXml(getDuplicateReceiversWithDifferentHostNameForSendersXml());
        System.out.println(("Initial document:\n" + (XmlUtils.prettyXml(document))));
        assertThat(document.getElementsByTagName("gateway-receiver").getLength()).isEqualTo(2);
        service.removeInvalidGatewayReceivers(document);
        System.out.println(("Processed document:\n" + (XmlUtils.prettyXml(document))));
        assertThat(document.getElementsByTagName("gateway-receiver").getLength()).isEqualTo(0);
    }

    @Test
    public void removeInvalidGatewayReceiversWithDifferentBindAddresses() throws Exception {
        Document document = XmlUtils.createDocumentFromXml(getDuplicateReceiversWithDifferentBindAddressesXml());
        System.out.println(("Initial document:\n" + (XmlUtils.prettyXml(document))));
        assertThat(document.getElementsByTagName("gateway-receiver").getLength()).isEqualTo(2);
        service.removeInvalidGatewayReceivers(document);
        System.out.println(("Processed document:\n" + (XmlUtils.prettyXml(document))));
        assertThat(document.getElementsByTagName("gateway-receiver").getLength()).isEqualTo(0);
    }

    @Test
    public void keepValidGatewayReceiversWithDefaultBindAddress() throws Exception {
        Document document = XmlUtils.createDocumentFromXml(getSingleReceiverWithDefaultBindAddressXml());
        System.out.println(("Initial document:\n" + (XmlUtils.prettyXml(document))));
        assertThat(document.getElementsByTagName("gateway-receiver").getLength()).isEqualTo(1);
        service.removeInvalidGatewayReceivers(document);
        System.out.println(("Processed document:\n" + (XmlUtils.prettyXml(document))));
        assertThat(document.getElementsByTagName("gateway-receiver").getLength()).isEqualTo(1);
    }
}

