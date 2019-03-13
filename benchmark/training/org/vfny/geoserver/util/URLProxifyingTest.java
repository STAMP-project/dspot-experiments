/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.vfny.geoserver.util;


import URLMangler.URLType.SERVICE;
import java.util.HashMap;
import org.geoserver.ows.ProxifyingURLMangler;
import org.junit.Assert;
import org.junit.Test;


public class URLProxifyingTest {
    ProxifyingURLMangler mangler;

    @Test
    public void testNullFlag() throws Exception {
        createAppContext(null, null, null, null, null, null, null, null);
        StringBuilder baseURL = new StringBuilder();
        this.mangler.mangleURL(baseURL, new StringBuilder(), new HashMap<String, String>(), SERVICE);
        Assert.assertEquals("", baseURL.toString());
    }

    @Test
    public void testNoProxyBaseURL() throws Exception {
        createAppContext(null, false, null, null, null, null, null, null);
        StringBuilder baseURL = new StringBuilder();
        this.mangler.mangleURL(baseURL, new StringBuilder(), new HashMap<String, String>(), SERVICE);
        Assert.assertEquals("", baseURL.toString());
    }

    @Test
    public void testProxyBaseURL() throws Exception {
        createAppContext("http://foo.org/geoserver", false, null, null, null, null, null, null);
        StringBuilder baseURL = new StringBuilder();
        this.mangler.mangleURL(baseURL, new StringBuilder(), new HashMap<String, String>(), SERVICE);
        Assert.assertEquals("http://foo.org/geoserver", baseURL.toString());
    }

    @Test
    public void testProxyBaseURLFlagSetNoTemplate() throws Exception {
        createAppContext("http://foo.org/geoserver", true, null, null, null, null, null, null);
        StringBuilder baseURL = new StringBuilder();
        this.mangler.mangleURL(baseURL, new StringBuilder(), new HashMap<String, String>(), SERVICE);
        Assert.assertEquals("http://foo.org/geoserver", baseURL.toString());
    }

    @Test
    public void testProxyBaseURLFlagSetWithTemplateNoHeaders() throws Exception {
        createAppContext("http://${X-Forwarded-Host}/${X-Forwarded-Path}/geoserver", true, null, null, null, null, null, null);
        StringBuilder baseURL = new StringBuilder();
        this.mangler.mangleURL(baseURL, new StringBuilder(), new HashMap<String, String>(), SERVICE);
        Assert.assertEquals("", baseURL.toString());
    }

    @Test
    public void testProxyBaseURLFlagSetWithTemplatePartialHeaders() throws Exception {
        createAppContext("http://${X-Forwarded-Host}/${X-Forwarded-Path}/geoserver", true, null, null, null, "example.com:8080", null, null);
        StringBuilder baseURL = new StringBuilder();
        this.mangler.mangleURL(baseURL, new StringBuilder(), new HashMap<String, String>(), SERVICE);
        Assert.assertEquals("", baseURL.toString());
    }

    @Test
    public void testProxyBaseURLFlagSetWithTemplateEmptyBaseURL() throws Exception {
        createAppContext("", true, null, null, null, "example.com:8080", null, null);
        StringBuilder baseURL = new StringBuilder();
        this.mangler.mangleURL(baseURL, new StringBuilder(), new HashMap<String, String>(), SERVICE);
        Assert.assertEquals("", baseURL.toString());
    }

    @Test
    public void testProxyBaseURLFlagSetWithTemplate() throws Exception {
        createAppContext("http://${X-Forwarded-Host}/geoserver", true, null, null, null, "example.com:8080", null, null);
        StringBuilder baseURL = new StringBuilder();
        this.mangler.mangleURL(baseURL, new StringBuilder(), new HashMap<String, String>(), SERVICE);
        Assert.assertEquals("http://example.com:8080/geoserver", baseURL.toString());
    }

    @Test
    public void testProxyBaseURLFlagSetWithTemplateForwardedHost() throws Exception {
        createAppContext("http://${X-Forwarded-Host}/${X-Forwarded-Path}/geoserver", true, null, null, null, "example.com:8080", "public", null);
        StringBuilder baseURL = new StringBuilder();
        this.mangler.mangleURL(baseURL, new StringBuilder(), new HashMap<String, String>(), SERVICE);
        Assert.assertEquals("http://example.com:8080/public/geoserver", baseURL.toString());
    }

    @Test
    public void testProxyBaseURLFlagSetWithTemplateHost() throws Exception {
        createAppContext("http://${Host}/${X-Forwarded-Path}/geoserver", true, null, null, "example.com:8080", null, "public", null);
        StringBuilder baseURL = new StringBuilder();
        this.mangler.mangleURL(baseURL, new StringBuilder(), new HashMap<String, String>(), SERVICE);
        Assert.assertEquals("http://example.com:8080/public/geoserver", baseURL.toString());
    }

    @Test
    public void testProxyBaseURLFlagSetWithTemplateForwarded() throws Exception {
        createAppContext("${Forwarded.proto}://${Forwarded.host}/geoserver", true, null, null, null, null, null, "for=192.0.2.60; proto=http; by=203.0.113.43; host=example.com:8080");
        StringBuilder baseURL = new StringBuilder();
        this.mangler.mangleURL(baseURL, new StringBuilder(), new HashMap<String, String>(), SERVICE);
        Assert.assertEquals("http://example.com:8080/geoserver", baseURL.toString());
    }

    @Test
    public void testProxyBaseURLFlagSetWithTemplateForwardedPath() throws Exception {
        createAppContext("${Forwarded.proto}://${Forwarded.host}/${Forwarded.path}/geoserver", true, null, null, null, null, null, "proto=http; host=example.com:8080; path=public");
        StringBuilder baseURL = new StringBuilder();
        this.mangler.mangleURL(baseURL, new StringBuilder(), new HashMap<String, String>(), SERVICE);
        Assert.assertEquals("http://example.com:8080/public/geoserver", baseURL.toString());
    }

    @Test
    public void testProxyBaseURLFlagSetWithTwoTemplates() throws Exception {
        createAppContext((("http://${X-Forwarded-Host}/${X-Forwarded-Path}/geoserver" + (ProxifyingURLMangler.TEMPLATE_SEPARATOR)) + "http://${X-Forwarded-Host}/geoserver"), true, null, null, null, "example.com:8080", "public", null);
        StringBuilder baseURL = new StringBuilder();
        this.mangler.mangleURL(baseURL, new StringBuilder(), new HashMap<String, String>(), SERVICE);
        Assert.assertEquals("http://example.com:8080/public/geoserver", baseURL.toString());
    }

    @Test
    public void testProxyBaseURLFlagSetWithTwoTemplates2() throws Exception {
        createAppContext((("http://${X-Forwarded-Host}/${X-Forwarded-Path}/geoserver" + (ProxifyingURLMangler.TEMPLATE_SEPARATOR)) + "http://example.org/${X-Forwarded-Path}/geoserver"), true, null, null, null, null, "public", null);
        StringBuilder baseURL = new StringBuilder();
        this.mangler.mangleURL(baseURL, new StringBuilder(), new HashMap<String, String>(), SERVICE);
        Assert.assertEquals("http://example.org/public/geoserver", baseURL.toString());
    }

    @Test
    public void testProxyBaseURLFlagSetWithTemplateMixedCase() throws Exception {
        createAppContext("http://${X-ForwarDED-HoST}/${x-forwarded-PATH}/geoserver", true, null, null, null, "example.com:8080", "public", null);
        StringBuilder baseURL = new StringBuilder();
        this.mangler.mangleURL(baseURL, new StringBuilder(), new HashMap<String, String>(), SERVICE);
        Assert.assertEquals("http://example.com:8080/public/geoserver", baseURL.toString());
    }
}

