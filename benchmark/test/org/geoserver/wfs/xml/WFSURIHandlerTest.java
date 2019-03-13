/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.xml;


import WFSURIHandler.ADDITIONAL_HOSTNAMES;
import WFSURIHandler.ADDRESSES;
import org.eclipse.emf.common.util.URI;
import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.util.PropertyRule;
import org.geoserver.wfs.xml.WFSURIHandler.InitStrategy;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class WFSURIHandlerTest {
    @Rule
    public PropertyRule aliases = PropertyRule.system("org.geoserver.wfs.xml.WFSURIHandler.additionalHostnames");

    InitStrategy strategy;

    private GeoServer gs;

    private GeoServerInfo config;

    @Test
    public void testDefaultAliases() {
        WFSURIHandler.init(strategy);
        Assert.assertThat(ADDRESSES, Matchers.empty());
        Assert.assertThat(ADDITIONAL_HOSTNAMES, Matchers.contains("localhost"));
    }

    @Test
    public void testOverrideAliasesComma() {
        aliases.setValue("foo,bar , baz");
        WFSURIHandler.init(strategy);
        Assert.assertThat(ADDRESSES, Matchers.empty());
        Assert.assertThat(ADDITIONAL_HOSTNAMES, Matchers.containsInAnyOrder("foo", "bar", "baz"));
    }

    @Test
    public void testOverrideAliasesSpace() {
        aliases.setValue("foo bar  baz ");
        WFSURIHandler.init(strategy);
        Assert.assertThat(ADDRESSES, Matchers.empty());
        Assert.assertThat(ADDITIONAL_HOSTNAMES, Matchers.containsInAnyOrder("foo", "bar", "baz"));
    }

    @Test
    public void testRecognizeReflexiveSimple() {
        WFSURIHandler.init(strategy);
        WFSURIHandler handler = new WFSURIHandler(gs);
        final URI wrongHost = URI.createURI("http://example.com/geoserver/wfs?service=wfs&version=2.0.0&request=DescribeFeatureType");
        final URI notDFT = URI.createURI("http://localhost/geoserver/wfs?service=wfs&version=2.0.0&request=GetCapabilities");
        final URI localDFT = URI.createURI("http://localhost/geoserver/wfs?service=wfs&version=2.0.0&request=DescribeFeatureType");
        Assert.assertThat(handler.canHandle(wrongHost), Matchers.is(false));
        Assert.assertThat(handler.canHandle(notDFT), Matchers.is(false));
        Assert.assertThat(handler.canHandle(localDFT), Matchers.is(true));
    }

    @Test
    public void testRecognizeReflexiveUserAliases() {
        aliases.setValue("foo bar baz");
        WFSURIHandler.init(strategy);
        WFSURIHandler handler = new WFSURIHandler(gs);
        final URI wrongHost = URI.createURI("http://example.com/geoserver/wfs?service=wfs&version=2.0.0&request=DescribeFeatureType");
        final URI notDFT = URI.createURI("http://foo/geoserver/wfs?service=wfs&version=2.0.0&request=GetCapabilities");
        final URI fooDFT = URI.createURI("http://foo/geoserver/wfs?service=wfs&version=2.0.0&request=DescribeFeatureType");
        final URI barDFT = URI.createURI("http://bar/geoserver/wfs?service=wfs&version=2.0.0&request=DescribeFeatureType");
        final URI bazDFT = URI.createURI("http://baz/geoserver/wfs?service=wfs&version=2.0.0&request=DescribeFeatureType");
        final URI localhostDFT = URI.createURI("http://localhost/geoserver/wfs?service=wfs&version=2.0.0&request=DescribeFeatureType");
        Assert.assertThat(handler.canHandle(wrongHost), Matchers.is(false));
        Assert.assertThat(handler.canHandle(notDFT), Matchers.is(false));
        Assert.assertThat(handler.canHandle(fooDFT), Matchers.is(true));
        Assert.assertThat(handler.canHandle(barDFT), Matchers.is(true));
        Assert.assertThat(handler.canHandle(bazDFT), Matchers.is(true));
        Assert.assertThat(handler.canHandle(localhostDFT), Matchers.is(false));
    }

    @Test
    public void testRecognizeReflexiveProxy() {
        this.setProxyBase("http://foo/geoserver");
        WFSURIHandler.init(strategy);
        WFSURIHandler handler = new WFSURIHandler(gs);
        final URI wrongHost = URI.createURI("http://example.com/geoserver/wfs?service=wfs&version=2.0.0&request=DescribeFeatureType");
        final URI notDFT = URI.createURI("http://foo/geoserver/wfs?service=wfs&version=2.0.0&request=GetCapabilities");
        final URI fooDFT = URI.createURI("http://foo/geoserver/wfs?service=wfs&version=2.0.0&request=DescribeFeatureType");
        final URI uppercaseFooDFT = URI.createURI("http://FOO/geoserver/wfs?service=wfs&version=2.0.0&request=DescribeFeatureType");
        Assert.assertThat(handler.canHandle(wrongHost), Matchers.is(false));
        Assert.assertThat(handler.canHandle(notDFT), Matchers.is(false));
        Assert.assertThat(handler.canHandle(fooDFT), Matchers.is(true));
        Assert.assertThat(handler.canHandle(uppercaseFooDFT), Matchers.is(true));
    }
}

