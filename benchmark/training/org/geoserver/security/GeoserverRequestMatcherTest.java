/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security;


import HTTPMethod.GET;
import HTTPMethod.POST;
import HTTPMethod.PUT;
import org.geoserver.test.GeoServerMockTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.web.util.matcher.RequestMatcher;


/**
 *
 *
 * @author christian
 */
public class GeoserverRequestMatcherTest extends GeoServerMockTestSupport {
    GeoServerSecurityFilterChainProxy proxy;

    @Test
    public void testMacher() {
        // match all
        VariableFilterChain chain = new ServiceLoginFilterChain("/**");
        RequestMatcher matcher = proxy.matcherForChain(chain);
        Assert.assertTrue(matcher.matches(createRequest(GET, "/wms")));
        // set methods, but match is inactvie
        chain = new ServiceLoginFilterChain("/**");
        chain.getHttpMethods().add(GET);
        chain.getHttpMethods().add(POST);
        matcher = proxy.matcherForChain(chain);
        Assert.assertTrue(matcher.matches(createRequest(GET, "/wms")));
        Assert.assertTrue(matcher.matches(createRequest(POST, "/wms")));
        Assert.assertTrue(matcher.matches(createRequest(PUT, "/wms")));
        // active method matching
        chain.setMatchHTTPMethod(true);
        matcher = proxy.matcherForChain(chain);
        Assert.assertTrue(matcher.matches(createRequest(GET, "/wms")));
        Assert.assertTrue(matcher.matches(createRequest(POST, "/wms")));
        Assert.assertFalse(matcher.matches(createRequest(PUT, "/wms")));
        chain = new ServiceLoginFilterChain("/wfs/**,/web/**");
        matcher = proxy.matcherForChain(chain);
        Assert.assertFalse(matcher.matches(createRequest(GET, "/wms/abc")));
        Assert.assertTrue(matcher.matches(createRequest(GET, "/wfs/acc")));
        Assert.assertTrue(matcher.matches(createRequest(GET, "/web/abc")));
        chain.getHttpMethods().add(GET);
        chain.getHttpMethods().add(POST);
        matcher = proxy.matcherForChain(chain);
        Assert.assertFalse(matcher.matches(createRequest(GET, "/wms/abc")));
        Assert.assertTrue(matcher.matches(createRequest(POST, "/wfs/acc")));
        Assert.assertTrue(matcher.matches(createRequest(PUT, "/web/abc")));
        chain.setMatchHTTPMethod(true);
        matcher = proxy.matcherForChain(chain);
        Assert.assertFalse(matcher.matches(createRequest(GET, "/wms/abc")));
        Assert.assertTrue(matcher.matches(createRequest(POST, "/wfs/acc")));
        Assert.assertFalse(matcher.matches(createRequest(PUT, "/web/abc")));
    }

    @Test
    public void testMacherWithQueryString() {
        VariableFilterChain chain = new ServiceLoginFilterChain("/wms/**|.*request=getcapabilities.*");
        RequestMatcher matcher = proxy.matcherForChain(chain);
        Assert.assertFalse(matcher.matches(createRequest(GET, "/wms")));
        Assert.assertTrue(matcher.matches(createRequest(GET, "/wms?service=WMS&version=1.1.1&request=GetCapabilities")));
        Assert.assertFalse(matcher.matches(createRequest(GET, "/wms?service=WMS&version=1.1.1&request=GetMap")));
        // regex for parameters in any order
        chain = new ServiceLoginFilterChain("/wms/**|(?=.*request=getmap)(?=.*format=image/png).*");
        matcher = proxy.matcherForChain(chain);
        Assert.assertTrue(matcher.matches(createRequest(GET, "/wms?service=WMS&version=1.1.1&request=GetMap&format=image/png")));
        Assert.assertTrue(matcher.matches(createRequest(GET, "/wms?service=WMS&version=1.1.1&format=image/png&request=GetMap")));
        Assert.assertFalse(matcher.matches(createRequest(GET, "/wms?service=WMS&version=1.1.1&format=image/jpg&request=GetMap")));
        // regex for parameters not contained
        chain = new ServiceLoginFilterChain("/wms/**|(?=.*request=getmap)(?!.*format=image/png).*");
        matcher = proxy.matcherForChain(chain);
        Assert.assertTrue(matcher.matches(createRequest(GET, "/wms?service=WMS&version=1.1.1&format=image/jpg&request=GetMap")));
        Assert.assertFalse(matcher.matches(createRequest(GET, "/wms?service=WMS&version=1.1.1&format=image/png&request=GetMap")));
    }
}

