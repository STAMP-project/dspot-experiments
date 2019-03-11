/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.clustered.client.internal.config.xml;


import ConnectionSource.ServerList;
import Timeouts.DEFAULT;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import org.ehcache.clustered.client.config.ClusteringServiceConfiguration;
import org.ehcache.clustered.client.config.Timeouts;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.clustered.client.config.builders.TimeoutsBuilder;
import org.ehcache.clustered.client.internal.ConnectionSource;
import org.ehcache.config.Configuration;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.spi.service.ServiceUtils;
import org.ehcache.core.util.ClassLoading;
import org.ehcache.spi.service.ServiceCreationConfiguration;
import org.ehcache.xml.CacheManagerServiceConfigurationParser;
import org.ehcache.xml.XmlConfiguration;
import org.ehcache.xml.XmlModel;
import org.ehcache.xml.exceptions.XmlConfigurationException;
import org.ehcache.xml.model.TimeType;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.xmlunit.diff.ElementSelectors;


public class ClusteringCacheManagerServiceConfigurationParserTest {
    @ClassRule
    public static final TemporaryFolder folder = new TemporaryFolder();

    @Rule
    public final TestName testName = new TestName();

    /**
     * Ensures the {@link ClusteringCacheManagerServiceConfigurationParser} is locatable as a
     * {@link CacheManagerServiceConfigurationParser} instance.
     */
    @Test
    public void testServiceLocator() throws Exception {
        Assert.assertThat(StreamSupport.stream(Spliterators.spliterator(ClassLoading.servicesOfType(CacheManagerServiceConfigurationParser.class).iterator(), Long.MAX_VALUE, 0), false).map(Object::getClass).collect(Collectors.toList()), IsCollectionContaining.hasItem(ClusteringCacheManagerServiceConfigurationParser.class));
    }

    /**
     * Ensures the namespace declared by {@link ClusteringCacheManagerServiceConfigurationParser} and its
     * schema are the same.
     */
    @Test
    public void testSchema() throws Exception {
        final ClusteringCacheManagerServiceConfigurationParser parser = new ClusteringCacheManagerServiceConfigurationParser();
        final StreamSource schemaSource = ((StreamSource) (parser.getXmlSchema()));
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);
        final DocumentBuilder domBuilder = factory.newDocumentBuilder();
        final Element schema = domBuilder.parse(schemaSource.getInputStream()).getDocumentElement();
        final Attr targetNamespaceAttr = schema.getAttributeNode("targetNamespace");
        Assert.assertThat(targetNamespaceAttr, Matchers.is(Matchers.not(Matchers.nullValue())));
        Assert.assertThat(targetNamespaceAttr.getValue(), Matchers.is(parser.getNamespace().toString()));
    }

    @Test
    public void testGetTimeout() throws Exception {
        final String[] config = new String[]{ "<ehcache:config", "    xmlns:ehcache=\"http://www.ehcache.org/v3\"", "    xmlns:tc=\"http://www.ehcache.org/v3/clustered\">", "", "  <ehcache:service>", "    <tc:cluster>", "      <tc:connection url=\"terracotta://example.com:9540/cachemanager\"/>", "      <tc:read-timeout unit=\"minutes\">5</tc:read-timeout>", "      <tc:write-timeout unit=\"minutes\">10</tc:write-timeout>", "      <tc:connection-timeout unit=\"minutes\">15</tc:connection-timeout>", "    </tc:cluster>", "  </ehcache:service>", "", "</ehcache:config>" };
        final Configuration configuration = new XmlConfiguration(makeConfig(config));
        Collection<ServiceCreationConfiguration<?>> serviceCreationConfigurations = configuration.getServiceCreationConfigurations();
        Assert.assertThat(serviceCreationConfigurations, Matchers.is(Matchers.not(Matchers.empty())));
        ClusteringServiceConfiguration clusteringServiceConfiguration = ServiceUtils.findSingletonAmongst(ClusteringServiceConfiguration.class, serviceCreationConfigurations);
        Assert.assertThat(clusteringServiceConfiguration, Matchers.is(Matchers.notNullValue()));
        Timeouts timeouts = clusteringServiceConfiguration.getTimeouts();
        Assert.assertThat(timeouts.getReadOperationTimeout(), Matchers.is(Duration.of(5, ChronoUnit.MINUTES)));
        Assert.assertThat(timeouts.getWriteOperationTimeout(), Matchers.is(Duration.of(10, ChronoUnit.MINUTES)));
        Assert.assertThat(timeouts.getConnectionTimeout(), Matchers.is(Duration.of(15, ChronoUnit.MINUTES)));
    }

    @Test
    public void testGetTimeoutNone() throws Exception {
        final String[] config = new String[]{ "<ehcache:config", "    xmlns:ehcache=\"http://www.ehcache.org/v3\"", "    xmlns:tc=\"http://www.ehcache.org/v3/clustered\">", "", "  <ehcache:service>", "    <tc:cluster>", "      <tc:connection url=\"terracotta://example.com:9540/cachemanager\"/>", "    </tc:cluster>", "  </ehcache:service>", "", "</ehcache:config>" };
        final Configuration configuration = new XmlConfiguration(makeConfig(config));
        Collection<ServiceCreationConfiguration<?>> serviceCreationConfigurations = configuration.getServiceCreationConfigurations();
        Assert.assertThat(serviceCreationConfigurations, Matchers.is(Matchers.not(Matchers.empty())));
        ClusteringServiceConfiguration clusteringServiceConfiguration = ServiceUtils.findSingletonAmongst(ClusteringServiceConfiguration.class, serviceCreationConfigurations);
        Assert.assertThat(clusteringServiceConfiguration, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(clusteringServiceConfiguration.getTimeouts(), Matchers.is(TimeoutsBuilder.timeouts().build()));
    }

    @Test
    public void testGetTimeoutUnitDefault() throws Exception {
        final String[] config = new String[]{ "<ehcache:config", "    xmlns:ehcache=\"http://www.ehcache.org/v3\"", "    xmlns:tc=\"http://www.ehcache.org/v3/clustered\">", "", "  <ehcache:service>", "    <tc:cluster>", "      <tc:connection url=\"terracotta://example.com:9540/cachemanager\"/>", "      <tc:read-timeout>5</tc:read-timeout>", "    </tc:cluster>", "  </ehcache:service>", "", "</ehcache:config>" };
        final Configuration configuration = new XmlConfiguration(makeConfig(config));
        Collection<ServiceCreationConfiguration<?>> serviceCreationConfigurations = configuration.getServiceCreationConfigurations();
        Assert.assertThat(serviceCreationConfigurations, Matchers.is(Matchers.not(Matchers.empty())));
        ClusteringServiceConfiguration clusteringServiceConfiguration = ServiceUtils.findSingletonAmongst(ClusteringServiceConfiguration.class, serviceCreationConfigurations);
        Assert.assertThat(clusteringServiceConfiguration, Matchers.is(Matchers.notNullValue()));
        TemporalUnit defaultUnit = XmlModel.convertToJavaTimeUnit(new TimeType().getUnit());
        Assert.assertThat(clusteringServiceConfiguration.getTimeouts().getReadOperationTimeout(), Matchers.is(Matchers.equalTo(Duration.of(5, defaultUnit))));
    }

    @Test
    public void testGetTimeoutUnitBad() throws Exception {
        final String[] config = new String[]{ "<ehcache:config", "    xmlns:ehcache=\"http://www.ehcache.org/v3\"", "    xmlns:tc=\"http://www.ehcache.org/v3/clustered\">", "", "  <ehcache:service>", "    <tc:cluster>", "      <tc:connection url=\"terracotta://example.com:9540/cachemanager\"/>", "      <tc:read-timeout unit=\"femtos\">5</tc:read-timeout>", "    </tc:cluster>", "  </ehcache:service>", "", "</ehcache:config>" };
        try {
            new XmlConfiguration(makeConfig(config));
            Assert.fail("Expecting XmlConfigurationException");
        } catch (XmlConfigurationException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("Error parsing XML configuration "));
            Assert.assertThat(e.getCause().getMessage(), Matchers.containsString("Value 'femtos' is not facet-valid with respect to enumeration "));
        }
    }

    @Test
    public void testGetTimeoutValueTooBig() throws Exception {
        final String[] config = new String[]{ "<ehcache:config", "    xmlns:ehcache=\"http://www.ehcache.org/v3\"", "    xmlns:tc=\"http://www.ehcache.org/v3/clustered\">", "", "  <ehcache:service>", "    <tc:cluster>", "      <tc:connection url=\"terracotta://example.com:9540/cachemanager\"/>", ("      <tc:read-timeout unit=\"seconds\">" + (BigInteger.ONE.add(BigInteger.valueOf(Long.MAX_VALUE)))) + "</tc:read-timeout>", "    </tc:cluster>", "  </ehcache:service>", "", "</ehcache:config>" };
        try {
            new XmlConfiguration(makeConfig(config));
            Assert.fail("Expecting XmlConfigurationException");
        } catch (XmlConfigurationException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString(" exceeds allowed value "));
        }
    }

    @Test
    public void testGetTimeoutValueOmitted() throws Exception {
        final String[] config = new String[]{ "<ehcache:config", "    xmlns:ehcache=\"http://www.ehcache.org/v3\"", "    xmlns:tc=\"http://www.ehcache.org/v3/clustered\">", "", "  <ehcache:service>", "    <tc:cluster>", "      <tc:connection url=\"terracotta://example.com:9540/cachemanager\"/>", "      <tc:read-timeout unit=\"seconds\"></tc:read-timeout>", "    </tc:cluster>", "  </ehcache:service>", "", "</ehcache:config>" };
        try {
            new XmlConfiguration(makeConfig(config));
            Assert.fail("Expecting XmlConfigurationException");
        } catch (XmlConfigurationException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("Error parsing XML configuration "));
            Assert.assertThat(e.getCause().getMessage(), Matchers.containsString("'' is not a valid value for 'integer'"));
        }
    }

    @Test(expected = XmlConfigurationException.class)
    public void testUrlAndServers() throws Exception {
        final String[] config = new String[]{ "<ehcache:config", "    xmlns:ehcache=\"http://www.ehcache.org/v3\"", "    xmlns:tc=\"http://www.ehcache.org/v3/clustered\">", "", "  <ehcache:service>", "    <tc:cluster>", "      <tc:connection url=\"terracotta://example.com:9540/cachemanager\" />", "      <tc:cluster-connection cluster-tier-manager=\"cM\">", "        <tc:server host=\"blah\" port=\"1234\" />", "      </tc:cluster-connection>", "    </tc:cluster>", "  </ehcache:service>", "", "</ehcache:config>" };
        new XmlConfiguration(makeConfig(config));
    }

    @Test(expected = XmlConfigurationException.class)
    public void testServersOnly() throws Exception {
        final String[] config = new String[]{ "<ehcache:config", "    xmlns:ehcache=\"http://www.ehcache.org/v3\"", "    xmlns:tc=\"http://www.ehcache.org/v3/clustered\">", "", "  <ehcache:service>", "    <tc:cluster>", "      <tc:cluster-connection>", "        <tc:server host=\"blah\" port=\"1234\" />", "      </tc:cluster-connection>", "    </tc:cluster>", "  </ehcache:service>", "", "</ehcache:config>" };
        new XmlConfiguration(makeConfig(config));
    }

    @Test
    public void testServersWithClusterTierManager() throws Exception {
        final String[] config = new String[]{ "<ehcache:config", "    xmlns:ehcache=\"http://www.ehcache.org/v3\"", "    xmlns:tc=\"http://www.ehcache.org/v3/clustered\">", "", "  <ehcache:service>", "    <tc:cluster>", "      <tc:cluster-connection cluster-tier-manager=\"cM\">", "        <tc:server host=\"server-1\" port=\"9510\" />", "        <tc:server host=\"server-2\" port=\"9540\" />", "      </tc:cluster-connection>", "    </tc:cluster>", "  </ehcache:service>", "", "</ehcache:config>" };
        final Configuration configuration = new XmlConfiguration(makeConfig(config));
        Collection<ServiceCreationConfiguration<?>> serviceCreationConfigurations = configuration.getServiceCreationConfigurations();
        ClusteringServiceConfiguration clusteringServiceConfiguration = ServiceUtils.findSingletonAmongst(ClusteringServiceConfiguration.class, serviceCreationConfigurations);
        ConnectionSource.ServerList connectionSource = ((ConnectionSource.ServerList) (clusteringServiceConfiguration.getConnectionSource()));
        Iterable<InetSocketAddress> servers = connectionSource.getServers();
        InetSocketAddress firstServer = InetSocketAddress.createUnresolved("server-1", 9510);
        InetSocketAddress secondServer = InetSocketAddress.createUnresolved("server-2", 9540);
        List<InetSocketAddress> expectedServers = Arrays.asList(firstServer, secondServer);
        Assert.assertThat(connectionSource.getClusterTierManager(), Matchers.is("cM"));
        Assert.assertThat(servers, Matchers.is(expectedServers));
    }

    @Test
    public void testServersWithClusterTierManagerAndOptionalPorts() throws Exception {
        final String[] config = new String[]{ "<ehcache:config", "    xmlns:ehcache=\"http://www.ehcache.org/v3\"", "    xmlns:tc=\"http://www.ehcache.org/v3/clustered\">", "", "  <ehcache:service>", "    <tc:cluster>", "      <tc:cluster-connection cluster-tier-manager=\"cM\">", "        <tc:server host=\"100.100.100.100\" port=\"9510\" />", "        <tc:server host=\"server-2\" />", "        <tc:server host=\"[::1]\" />", "        <tc:server host=\"[fe80::1453:846e:7be4:15fe]\" port=\"9710\" />", "      </tc:cluster-connection>", "    </tc:cluster>", "  </ehcache:service>", "", "</ehcache:config>" };
        final Configuration configuration = new XmlConfiguration(makeConfig(config));
        Collection<ServiceCreationConfiguration<?>> serviceCreationConfigurations = configuration.getServiceCreationConfigurations();
        ClusteringServiceConfiguration clusteringServiceConfiguration = ServiceUtils.findSingletonAmongst(ClusteringServiceConfiguration.class, serviceCreationConfigurations);
        ConnectionSource.ServerList connectionSource = ((ConnectionSource.ServerList) (clusteringServiceConfiguration.getConnectionSource()));
        Iterable<InetSocketAddress> servers = connectionSource.getServers();
        InetSocketAddress firstServer = InetSocketAddress.createUnresolved("100.100.100.100", 9510);
        InetSocketAddress secondServer = InetSocketAddress.createUnresolved("server-2", 0);
        InetSocketAddress thirdServer = InetSocketAddress.createUnresolved("[::1]", 0);
        InetSocketAddress fourthServer = InetSocketAddress.createUnresolved("[fe80::1453:846e:7be4:15fe]", 9710);
        List<InetSocketAddress> expectedServers = Arrays.asList(firstServer, secondServer, thirdServer, fourthServer);
        Assert.assertThat(connectionSource.getClusterTierManager(), Matchers.is("cM"));
        Assert.assertThat(servers, Matchers.is(expectedServers));
    }

    @Test
    public void testTranslateServiceCreationConfiguration() throws Exception {
        URI connectionUri = new URI("terracotta://localhost:9510/my-application");
        ClusteringServiceConfiguration serviceConfig = ClusteringServiceConfigurationBuilder.cluster(connectionUri).timeouts(DEFAULT).autoCreate().defaultServerResource("main").resourcePool("primaryresource", 5, MemoryUnit.GB).resourcePool("secondaryresource", 10, MemoryUnit.GB, "optional").build();
        ClusteringCacheManagerServiceConfigurationParser parser = new ClusteringCacheManagerServiceConfigurationParser();
        Element returnElement = parser.unparseServiceCreationConfiguration(serviceConfig);
        String inputString = "<tc:cluster xmlns:tc = \"http://www.ehcache.org/v3/clustered\">" + (((((((("<tc:connection url = \"terracotta://localhost:9510/my-application\"/>" + "<tc:read-timeout unit = \"seconds\">5</tc:read-timeout>") + "<tc:write-timeout unit = \"seconds\">5</tc:write-timeout>") + "<tc:connection-timeout unit = \"seconds\">150</tc:connection-timeout>") + "<tc:server-side-config auto-create = \"true\">") + "<tc:default-resource from = \"main\"/>") + "<tc:shared-pool name = \"primaryresource\" unit = \"B\">5368709120</tc:shared-pool>") + "<tc:shared-pool from = \"optional\" name = \"secondaryresource\" unit = \"B\">10737418240</tc:shared-pool>") + "</tc:server-side-config></tc:cluster>");
        Assert.assertThat(returnElement, isSimilarTo(inputString).ignoreComments().ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testTranslateServiceCreationConfigurationWithNoResourcePoolAndAutoCreateFalse() throws Exception {
        URI connectionUri = new URI("terracotta://localhost:9510/my-application");
        ClusteringServiceConfiguration serviceConfig = ClusteringServiceConfigurationBuilder.cluster(connectionUri).timeouts(DEFAULT).expecting().defaultServerResource("main").build();
        ClusteringCacheManagerServiceConfigurationParser parser = new ClusteringCacheManagerServiceConfigurationParser();
        Element returnElement = parser.unparseServiceCreationConfiguration(serviceConfig);
        String inputString = "<tc:cluster xmlns:tc = \"http://www.ehcache.org/v3/clustered\">" + (((((("<tc:connection url = \"terracotta://localhost:9510/my-application\"/>" + "<tc:read-timeout unit = \"seconds\">5</tc:read-timeout>") + "<tc:write-timeout unit = \"seconds\">5</tc:write-timeout>") + "<tc:connection-timeout unit = \"seconds\">150</tc:connection-timeout>") + "<tc:server-side-config auto-create = \"false\">") + "<tc:default-resource from = \"main\"/>") + "</tc:server-side-config></tc:cluster>");
        Assert.assertThat(returnElement, isSimilarTo(inputString).ignoreComments().ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testTranslateServiceCreationConfigurationWithNoServerSideConfig() throws Exception {
        URI connectionUri = new URI("terracotta://localhost:9510/my-application");
        ClusteringServiceConfiguration serviceConfig = ClusteringServiceConfigurationBuilder.cluster(connectionUri).timeouts(DEFAULT).build();
        ClusteringCacheManagerServiceConfigurationParser parser = new ClusteringCacheManagerServiceConfigurationParser();
        Element returnElement = parser.unparseServiceCreationConfiguration(serviceConfig);
        String inputString = "<tc:cluster xmlns:tc = \"http://www.ehcache.org/v3/clustered\">" + ((((("<tc:connection url = \"terracotta://localhost:9510/my-application\"/>" + "<tc:read-timeout unit = \"seconds\">5</tc:read-timeout>") + "<tc:write-timeout unit = \"seconds\">5</tc:write-timeout>") + "<tc:connection-timeout unit = \"seconds\">150</tc:connection-timeout>") + "<tc:server-side-config auto-create = \"false\">") + "</tc:server-side-config></tc:cluster>");
        Assert.assertThat(returnElement, isSimilarTo(inputString).ignoreComments().ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testTranslateServiceCreationConfigurationWithInetSocketAddress() {
        InetSocketAddress firstServer = InetSocketAddress.createUnresolved("100.100.100.100", 9510);
        InetSocketAddress secondServer = InetSocketAddress.createUnresolved("server-2", 0);
        InetSocketAddress thirdServer = InetSocketAddress.createUnresolved("[::1]", 0);
        InetSocketAddress fourthServer = InetSocketAddress.createUnresolved("[fe80::1453:846e:7be4:15fe]", 9710);
        List<InetSocketAddress> servers = Arrays.asList(firstServer, secondServer, thirdServer, fourthServer);
        ClusteringServiceConfiguration serviceConfig = ClusteringServiceConfigurationBuilder.cluster(servers, "my-application").timeouts(DEFAULT).build();
        ClusteringCacheManagerServiceConfigurationParser parser = new ClusteringCacheManagerServiceConfigurationParser();
        Element returnElement = parser.unparseServiceCreationConfiguration(serviceConfig);
        String inputString = "<tc:cluster xmlns:tc = \"http://www.ehcache.org/v3/clustered\">" + ((((((((("<tc:cluster-connection cluster-tier-manager = \"my-application\">" + "<tc:server host = \"100.100.100.100\" port = \"9510\"/>") + "<tc:server host = \"server-2\"/>") + "<tc:server host = \"[::1]\"/>") + "<tc:server host = \"[fe80::1453:846e:7be4:15fe]\" port = \"9710\"/>") + "</tc:cluster-connection>") + "<tc:read-timeout unit = \"seconds\">5</tc:read-timeout>") + "<tc:write-timeout unit = \"seconds\">5</tc:write-timeout>") + "<tc:connection-timeout unit = \"seconds\">150</tc:connection-timeout>") + "<tc:server-side-config auto-create = \"false\"/></tc:cluster>");
        Assert.assertThat(returnElement, isSimilarTo(inputString).ignoreComments().ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }
}

