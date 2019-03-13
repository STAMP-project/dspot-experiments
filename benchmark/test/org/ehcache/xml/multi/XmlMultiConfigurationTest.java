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
package org.ehcache.xml.multi;


import java.net.URISyntaxException;
import java.net.URL;
import org.ehcache.config.Configuration;
import org.ehcache.xml.XmlConfiguration;
import org.ehcache.xml.XmlConfigurationTest;
import org.ehcache.xml.exceptions.XmlConfigurationException;
import org.hamcrest.core.IsNull;
import org.hamcrest.core.IsSame;
import org.junit.Assert;
import org.junit.Test;
import org.xmlunit.builder.Input;


public class XmlMultiConfigurationTest {
    @Test
    public void testEmptyConfigurationFromBuilder() {
        XmlMultiConfiguration xmlMultiConfiguration = XmlMultiConfiguration.fromNothing().build();
        Assert.assertThat(xmlMultiConfiguration.configuration("foo"), IsNull.nullValue());
        Assert.assertThat(xmlMultiConfiguration.configuration("foo", "prod"), IsNull.nullValue());
        Assert.assertThat(xmlMultiConfiguration.identities(), empty());
        XmlMultiConfigurationTest.assertThrows(() -> xmlMultiConfiguration.variants("foo"), IllegalArgumentException.class);
        Assert.assertThat(xmlMultiConfiguration.toString(), isSimilarTo("<configurations xmlns='http://www.ehcache.org/v3/multi'/>").ignoreWhitespace().ignoreComments());
    }

    @Test
    public void testSimpleConfigurationBuiltFromEmpty() {
        Configuration config = XmlMultiConfigurationTest.emptyConfiguration();
        XmlMultiConfiguration xmlMultiConfiguration = XmlMultiConfiguration.fromNothing().withManager("foo", config).build();
        Assert.assertThat(xmlMultiConfiguration.configuration("foo"), IsSame.sameInstance(config));
        Assert.assertThat(xmlMultiConfiguration.configuration("foo", "prod"), IsSame.sameInstance(config));
        Assert.assertThat(xmlMultiConfiguration.identities(), containsInAnyOrder("foo"));
        Assert.assertThat(xmlMultiConfiguration.variants("foo"), empty());
        Assert.assertThat(xmlMultiConfiguration.toString(), isSimilarTo(("<configurations xmlns='http://www.ehcache.org/v3/multi'>" + ("<configuration identity='foo'><config xmlns='http://www.ehcache.org/v3'/></configuration>" + "</configurations>"))).ignoreWhitespace().ignoreComments());
    }

    @Test
    public void testVariantConfigurationBuiltFromEmpty() {
        Configuration barVariant = XmlMultiConfigurationTest.emptyConfiguration();
        Configuration bazVariant = XmlMultiConfigurationTest.emptyConfiguration();
        XmlMultiConfiguration xmlMultiConfiguration = XmlMultiConfiguration.fromNothing().withManager("foo").variant("bar", barVariant).variant("baz", bazVariant).build();
        XmlMultiConfigurationTest.assertThrows(() -> xmlMultiConfiguration.configuration("foo"), IllegalStateException.class);
        Assert.assertThat(xmlMultiConfiguration.configuration("foo", "bar"), IsSame.sameInstance(barVariant));
        Assert.assertThat(xmlMultiConfiguration.configuration("foo", "baz"), IsSame.sameInstance(bazVariant));
        Assert.assertThat(xmlMultiConfiguration.identities(), containsInAnyOrder("foo"));
        Assert.assertThat(xmlMultiConfiguration.variants("foo"), containsInAnyOrder("bar", "baz"));
        Assert.assertThat(xmlMultiConfiguration.toString(), isSimilarTo(("<configurations xmlns='http://www.ehcache.org/v3/multi'>" + (((("<configuration identity='foo'>" + "<variant type='bar'><config xmlns='http://www.ehcache.org/v3'/></variant>") + "<variant type='baz'><config xmlns='http://www.ehcache.org/v3'/></variant>") + "</configuration>") + "</configurations>"))).ignoreWhitespace().ignoreComments());
    }

    @Test
    public void testMixedConfigurationBuiltFromEmpty() {
        Configuration barVariant = XmlMultiConfigurationTest.emptyConfiguration();
        Configuration bazVariant = XmlMultiConfigurationTest.emptyConfiguration();
        Configuration fiiConfig = XmlMultiConfigurationTest.emptyConfiguration();
        XmlMultiConfiguration xmlMultiConfiguration = XmlMultiConfiguration.fromNothing().withManager("foo").variant("bar", barVariant).variant("baz", bazVariant).withManager("fum").variant("bar", barVariant).withManager("fii", fiiConfig).build();
        XmlMultiConfigurationTest.assertThrows(() -> xmlMultiConfiguration.configuration("foo"), IllegalStateException.class);
        Assert.assertThat(xmlMultiConfiguration.configuration("foo", "bar"), IsSame.sameInstance(barVariant));
        Assert.assertThat(xmlMultiConfiguration.configuration("foo", "baz"), IsSame.sameInstance(bazVariant));
        Assert.assertThat(xmlMultiConfiguration.configuration("fum", "bar"), IsSame.sameInstance(barVariant));
        Assert.assertThat(xmlMultiConfiguration.configuration("fii", "bar"), IsSame.sameInstance(fiiConfig));
        Assert.assertThat(xmlMultiConfiguration.configuration("fii", "baz"), IsSame.sameInstance(fiiConfig));
        Assert.assertThat(xmlMultiConfiguration.configuration("fii"), IsSame.sameInstance(fiiConfig));
        Assert.assertThat(xmlMultiConfiguration.identities(), containsInAnyOrder("foo", "fii", "fum"));
        Assert.assertThat(xmlMultiConfiguration.variants("foo"), containsInAnyOrder("bar", "baz"));
        Assert.assertThat(xmlMultiConfiguration.variants("fum"), containsInAnyOrder("bar"));
        Assert.assertThat(xmlMultiConfiguration.variants("fii"), empty());
        Assert.assertThat(xmlMultiConfiguration.toString(), isSimilarTo(("<configurations xmlns='http://www.ehcache.org/v3/multi'>" + (((((((((("<configuration identity='foo'>" + "<variant type='bar'><config xmlns='http://www.ehcache.org/v3'/></variant>") + "<variant type='baz'><config xmlns='http://www.ehcache.org/v3'/></variant>") + "</configuration>") + "<configuration identity='fum'>") + "<variant type='bar'><config xmlns='http://www.ehcache.org/v3'/></variant>") + "</configuration>") + "<configuration identity='fii'>") + "<config xmlns='http://www.ehcache.org/v3'/>") + "</configuration>") + "</configurations>"))).ignoreWhitespace().ignoreComments());
    }

    @Test
    public void testEmptyConfigurationFromXml() throws URISyntaxException {
        URL resource = XmlConfigurationTest.class.getResource("/configs/multi/empty.xml");
        XmlMultiConfiguration xmlMultiConfiguration = XmlMultiConfiguration.from(resource).build();
        Assert.assertThat(xmlMultiConfiguration.configuration("foo"), IsNull.nullValue());
        Assert.assertThat(xmlMultiConfiguration.configuration("foo", "prod"), IsNull.nullValue());
        Assert.assertThat(xmlMultiConfiguration.identities(), empty());
        XmlMultiConfigurationTest.assertThrows(() -> xmlMultiConfiguration.variants("foo"), IllegalArgumentException.class);
        Assert.assertThat(xmlMultiConfiguration.toString(), isSimilarTo(Input.fromURI(resource.toURI())).ignoreWhitespace().ignoreComments());
    }

    @Test
    public void testMultipleConfigurationsFromXml() throws URISyntaxException {
        URL resource = XmlConfigurationTest.class.getResource("/configs/multi/multiple-configs.xml");
        XmlMultiConfiguration xmlMultiConfiguration = XmlMultiConfiguration.from(resource).build();
        Assert.assertThat(xmlMultiConfiguration.configuration("foo").getCacheConfigurations(), hasKey("foo"));
        Assert.assertThat(xmlMultiConfiguration.configuration("foo", "prod").getCacheConfigurations(), hasKey("foo"));
        Assert.assertThat(xmlMultiConfiguration.configuration("bar").getCacheConfigurations(), hasKey("bar"));
        Assert.assertThat(xmlMultiConfiguration.configuration("bar", "prod").getCacheConfigurations(), hasKey("bar"));
        Assert.assertThat(xmlMultiConfiguration.identities(), containsInAnyOrder("foo", "bar"));
        Assert.assertThat(xmlMultiConfiguration.variants("foo"), empty());
        Assert.assertThat(xmlMultiConfiguration.variants("bar"), empty());
        Assert.assertThat(xmlMultiConfiguration.toString(), isSimilarTo(Input.fromURI(resource.toURI())).ignoreWhitespace().ignoreComments());
    }

    @Test
    public void testMultipleVariantsFromXml() throws URISyntaxException {
        URL resource = XmlConfigurationTest.class.getResource("/configs/multi/multiple-variants.xml");
        XmlMultiConfiguration xmlMultiConfiguration = XmlMultiConfiguration.from(resource).build();
        XmlMultiConfigurationTest.assertThrows(() -> xmlMultiConfiguration.configuration("foo"), IllegalStateException.class);
        Assert.assertThat(xmlMultiConfiguration.configuration("foo", "development").getCacheConfigurations(), hasKey("foo-dev"));
        Assert.assertThat(xmlMultiConfiguration.configuration("foo", "production").getCacheConfigurations(), hasKey("foo-prod"));
        Assert.assertThat(xmlMultiConfiguration.configuration("bar", "development").getCacheConfigurations(), hasKey("bar-dev"));
        Assert.assertThat(xmlMultiConfiguration.configuration("bar", "production").getCacheConfigurations(), hasKey("bar-prod"));
        Assert.assertThat(xmlMultiConfiguration.identities(), containsInAnyOrder("foo", "bar"));
        Assert.assertThat(xmlMultiConfiguration.variants("foo"), containsInAnyOrder("development", "production"));
        Assert.assertThat(xmlMultiConfiguration.variants("bar"), containsInAnyOrder("development", "production"));
        Assert.assertThat(xmlMultiConfiguration.toString(), isSimilarTo(Input.fromURI(resource.toURI())).ignoreWhitespace().ignoreComments());
    }

    @Test
    public void testManagerRemovedFromXml() throws URISyntaxException {
        URL resource = XmlConfigurationTest.class.getResource("/configs/multi/multiple-configs.xml");
        XmlMultiConfiguration xmlMultiConfiguration = XmlMultiConfiguration.from(resource).withoutManager("bar").build();
        Assert.assertThat(xmlMultiConfiguration.configuration("foo").getCacheConfigurations(), hasKey("foo"));
        Assert.assertThat(xmlMultiConfiguration.configuration("foo", "prod").getCacheConfigurations(), hasKey("foo"));
        Assert.assertThat(xmlMultiConfiguration.configuration("bar"), IsNull.nullValue());
        Assert.assertThat(xmlMultiConfiguration.configuration("bar", "prod"), IsNull.nullValue());
        Assert.assertThat(xmlMultiConfiguration.identities(), containsInAnyOrder("foo"));
        Assert.assertThat(xmlMultiConfiguration.variants("foo"), empty());
        Assert.assertThat(xmlMultiConfiguration.toString(), isSimilarTo(("<configurations xmlns='http://www.ehcache.org/v3/multi'>" + ((((("<configuration identity='foo'>" + "<ehcache:config xmlns:ehcache='http://www.ehcache.org/v3' xmlns:multi='http://www.ehcache.org/v3/multi' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>") + "<ehcache:cache alias='foo'><ehcache:heap unit='entries'>100</ehcache:heap></ehcache:cache>") + "</ehcache:config>") + "</configuration>") + "</configurations>"))).ignoreWhitespace().ignoreComments());
    }

    @Test
    public void testManagerRemovedFromXmlAndReadded() throws URISyntaxException {
        URL resource = XmlConfigurationTest.class.getResource("/configs/multi/multiple-configs.xml");
        XmlMultiConfiguration xmlMultiConfiguration = XmlMultiConfiguration.from(resource).withoutManager("bar").withManager("bar", XmlMultiConfigurationTest.emptyConfiguration()).build();
        Assert.assertThat(xmlMultiConfiguration.configuration("foo").getCacheConfigurations(), hasKey("foo"));
        Assert.assertThat(xmlMultiConfiguration.configuration("foo", "prod").getCacheConfigurations(), hasKey("foo"));
        Assert.assertThat(xmlMultiConfiguration.configuration("bar"), IsNull.notNullValue());
        Assert.assertThat(xmlMultiConfiguration.configuration("bar", "prod"), IsNull.notNullValue());
        Assert.assertThat(xmlMultiConfiguration.identities(), containsInAnyOrder("foo", "bar"));
        Assert.assertThat(xmlMultiConfiguration.variants("foo"), empty());
        Assert.assertThat(xmlMultiConfiguration.variants("bar"), empty());
        Assert.assertThat(xmlMultiConfiguration.toString(), isSimilarTo(("<configurations xmlns='http://www.ehcache.org/v3/multi'>" + (((((((("<configuration identity='bar'>" + "<config xmlns='http://www.ehcache.org/v3'/>") + "</configuration>") + "<configuration identity='foo'>") + "<ehcache:config xmlns:ehcache='http://www.ehcache.org/v3' xmlns:multi='http://www.ehcache.org/v3/multi' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>") + "<ehcache:cache alias='foo'><ehcache:heap unit='entries'>100</ehcache:heap></ehcache:cache>") + "</ehcache:config>") + "</configuration>") + "</configurations>"))).ignoreWhitespace().ignoreComments());
    }

    @Test
    public void testManagerAddedToXml() throws URISyntaxException {
        URL resource = XmlConfigurationTest.class.getResource("/configs/multi/multiple-configs.xml");
        XmlMultiConfiguration xmlMultiConfiguration = XmlMultiConfiguration.from(resource).withManager("baz", XmlMultiConfigurationTest.emptyConfiguration()).build();
        Assert.assertThat(xmlMultiConfiguration.configuration("foo").getCacheConfigurations(), hasKey("foo"));
        Assert.assertThat(xmlMultiConfiguration.configuration("foo", "prod").getCacheConfigurations(), hasKey("foo"));
        Assert.assertThat(xmlMultiConfiguration.configuration("bar").getCacheConfigurations(), hasKey("bar"));
        Assert.assertThat(xmlMultiConfiguration.configuration("bar", "prod").getCacheConfigurations(), hasKey("bar"));
        Assert.assertThat(xmlMultiConfiguration.configuration("baz"), IsNull.notNullValue());
        Assert.assertThat(xmlMultiConfiguration.configuration("baz", "prod"), IsNull.notNullValue());
        Assert.assertThat(xmlMultiConfiguration.identities(), containsInAnyOrder("foo", "bar", "baz"));
        Assert.assertThat(xmlMultiConfiguration.variants("foo"), empty());
        Assert.assertThat(xmlMultiConfiguration.variants("bar"), empty());
        Assert.assertThat(xmlMultiConfiguration.variants("baz"), empty());
        Assert.assertThat(xmlMultiConfiguration.toString(), isSimilarTo(("<configurations xmlns='http://www.ehcache.org/v3/multi'>" + ((((((((((((("<configuration identity='bar'>" + "<ehcache:config xmlns:ehcache='http://www.ehcache.org/v3' xmlns:multi='http://www.ehcache.org/v3/multi' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>") + "<ehcache:cache alias='bar'><ehcache:heap unit='entries'>100</ehcache:heap></ehcache:cache>") + "</ehcache:config>") + "</configuration>") + "<configuration identity='baz'>") + "<config xmlns='http://www.ehcache.org/v3'/>") + "</configuration>") + "<configuration identity='foo'>") + "<ehcache:config xmlns:ehcache='http://www.ehcache.org/v3' xmlns:multi='http://www.ehcache.org/v3/multi' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>") + "<ehcache:cache alias='foo'><ehcache:heap unit='entries'>100</ehcache:heap></ehcache:cache>") + "</ehcache:config>") + "</configuration>") + "</configurations>"))).ignoreWhitespace().ignoreComments());
    }

    @Test
    public void testManagerRemovedFromConfig() throws URISyntaxException {
        XmlMultiConfiguration source = XmlMultiConfiguration.fromNothing().withManager("foo", XmlMultiConfigurationTest.emptyConfiguration()).build();
        XmlMultiConfiguration xmlMultiConfiguration = XmlMultiConfiguration.from(source).withoutManager("foo").build();
        Assert.assertThat(xmlMultiConfiguration.configuration("foo"), IsNull.nullValue());
        Assert.assertThat(xmlMultiConfiguration.configuration("foo", "prod"), IsNull.nullValue());
        Assert.assertThat(xmlMultiConfiguration.identities(), empty());
        XmlMultiConfigurationTest.assertThrows(() -> xmlMultiConfiguration.variants("foo"), IllegalArgumentException.class);
        Assert.assertThat(xmlMultiConfiguration.toString(), isSimilarTo("<configurations xmlns='http://www.ehcache.org/v3/multi'></configurations>").ignoreWhitespace().ignoreComments());
    }

    @Test
    public void testManagerRemovedFromConfigAndReadded() throws URISyntaxException {
        XmlMultiConfiguration source = XmlMultiConfiguration.fromNothing().withManager("foo", XmlMultiConfigurationTest.emptyConfiguration()).build();
        XmlMultiConfiguration xmlMultiConfiguration = XmlMultiConfiguration.from(source).withoutManager("foo").withManager("foo", XmlMultiConfigurationTest.emptyConfiguration()).build();
        Assert.assertThat(xmlMultiConfiguration.configuration("foo"), IsNull.notNullValue());
        Assert.assertThat(xmlMultiConfiguration.configuration("foo", "prod"), IsNull.notNullValue());
        Assert.assertThat(xmlMultiConfiguration.identities(), containsInAnyOrder("foo"));
        Assert.assertThat(xmlMultiConfiguration.variants("foo"), empty());
        Assert.assertThat(xmlMultiConfiguration.toString(), isSimilarTo(("<configurations xmlns='http://www.ehcache.org/v3/multi'>" + ("<configuration identity='foo'><config xmlns='http://www.ehcache.org/v3'/></configuration>" + "</configurations>"))).ignoreWhitespace().ignoreComments());
    }

    @Test
    public void testManagerAddedToConfig() throws URISyntaxException {
        XmlMultiConfiguration source = XmlMultiConfiguration.fromNothing().withManager("foo", XmlMultiConfigurationTest.emptyConfiguration()).build();
        XmlMultiConfiguration xmlMultiConfiguration = XmlMultiConfiguration.from(source).withManager("baz", XmlMultiConfigurationTest.emptyConfiguration()).build();
        Assert.assertThat(xmlMultiConfiguration.configuration("foo"), IsNull.notNullValue());
        Assert.assertThat(xmlMultiConfiguration.configuration("foo", "prod"), IsNull.notNullValue());
        Assert.assertThat(xmlMultiConfiguration.configuration("baz"), IsNull.notNullValue());
        Assert.assertThat(xmlMultiConfiguration.configuration("baz", "prod"), IsNull.notNullValue());
        Assert.assertThat(xmlMultiConfiguration.identities(), containsInAnyOrder("foo", "baz"));
        Assert.assertThat(xmlMultiConfiguration.variants("foo"), empty());
        Assert.assertThat(xmlMultiConfiguration.variants("baz"), empty());
        Assert.assertThat(xmlMultiConfiguration.toString(), isSimilarTo(("<configurations xmlns='http://www.ehcache.org/v3/multi'>" + (("<configuration identity='foo'><config xmlns='http://www.ehcache.org/v3'/></configuration>" + "<configuration identity='baz'><config xmlns='http://www.ehcache.org/v3'/></configuration>") + "</configurations>"))).ignoreWhitespace().ignoreComments().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(and(byNameAndText, byNameAndAllAttributes))));
    }

    @Test
    public void testGenerateExtendedConfiguration() throws URISyntaxException {
        XmlConfiguration extended = new XmlConfiguration(getClass().getResource("/configs/all-extensions.xml"));
        XmlMultiConfiguration xmlMultiConfiguration = XmlMultiConfiguration.fromNothing().withManager("foo", extended).build();
        Assert.assertThat(xmlMultiConfiguration.configuration("foo"), IsSame.sameInstance(extended));
        Assert.assertThat(xmlMultiConfiguration.identities(), containsInAnyOrder("foo"));
        Assert.assertThat(xmlMultiConfiguration.variants("foo"), empty());
        Assert.assertThat(xmlMultiConfiguration.toString(), isSimilarTo(("<configurations xmlns='http://www.ehcache.org/v3/multi'>" + (((((((((((((("<configuration identity='foo'>" + "<config xmlns='http://www.ehcache.org/v3' xmlns:bar='http://www.example.com/bar' xmlns:baz='http://www.example.com/baz' xmlns:foo='http://www.example.com/foo' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://www.example.com/baz baz.xsd http://www.ehcache.org/v3 ../../../main/resources/ehcache-core.xsd'>") + "<service><bar:bar/></service>") + "<cache alias='fancy'>") + "<key-type>java.lang.String</key-type>") + "<value-type>java.lang.String</value-type>") + "<resources>") + "<heap unit='entries'>10</heap>") + "<baz:baz/>") + "</resources>") + "<foo:foo/>") + "</cache>") + "</config>") + "</configuration>") + "</configurations>"))).ignoreWhitespace().ignoreComments());
    }

    @Test
    public void testParseExtendedConfiguration() {
        XmlMultiConfiguration xmlMultiConfiguration = XmlMultiConfiguration.from(getClass().getResource("/configs/multi/extended.xml")).build();
        Assert.assertThat(xmlMultiConfiguration.configuration("foo"), IsNull.notNullValue());
        Assert.assertThat(xmlMultiConfiguration.identities(), containsInAnyOrder("foo"));
        Assert.assertThat(xmlMultiConfiguration.variants("foo"), empty());
        Assert.assertThat(xmlMultiConfiguration.toString(), isSimilarTo(("<configurations xmlns='http://www.ehcache.org/v3/multi'>" + (((((((((((((("<configuration identity='foo'>" + "<config xmlns='http://www.ehcache.org/v3' xmlns:bar='http://www.example.com/bar' xmlns:baz='http://www.example.com/baz' xmlns:foo='http://www.example.com/foo' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://www.example.com/baz baz.xsd http://www.ehcache.org/v3 ../../../main/resources/ehcache-core.xsd'>") + "<service><bar:bar/></service>") + "<cache alias='fancy'>") + "<key-type>java.lang.String</key-type>") + "<value-type>java.lang.String</value-type>") + "<resources>") + "<heap unit='entries'>10</heap>") + "<baz:baz/>") + "</resources>") + "<foo:foo/>") + "</cache>") + "</config>") + "</configuration>") + "</configurations>"))).ignoreWhitespace().ignoreComments());
    }

    @Test(expected = XmlConfigurationException.class)
    public void testParseOrdinaryConfiguration() {
        XmlMultiConfiguration.from(getClass().getResource("/configs/one-cache.xml")).build();
    }
}

