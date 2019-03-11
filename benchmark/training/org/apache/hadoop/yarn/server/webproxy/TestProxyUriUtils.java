/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.webproxy;


import com.google.common.collect.Lists;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.apache.hadoop.yarn.util.TrackingUriPlugin;
import org.junit.Assert;
import org.junit.Test;


public class TestProxyUriUtils {
    @Test
    public void testGetPathApplicationId() {
        Assert.assertEquals("/proxy/application_100_0001", ProxyUriUtils.getPath(BuilderUtils.newApplicationId(100L, 1)));
        Assert.assertEquals("/proxy/application_6384623_0005", ProxyUriUtils.getPath(BuilderUtils.newApplicationId(6384623L, 5)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPathApplicationIdBad() {
        ProxyUriUtils.getPath(null);
    }

    @Test
    public void testGetPathApplicationIdString() {
        Assert.assertEquals("/proxy/application_6384623_0005", ProxyUriUtils.getPath(BuilderUtils.newApplicationId(6384623L, 5), null));
        Assert.assertEquals("/proxy/application_6384623_0005/static/app", ProxyUriUtils.getPath(BuilderUtils.newApplicationId(6384623L, 5), "/static/app"));
        Assert.assertEquals("/proxy/application_6384623_0005/", ProxyUriUtils.getPath(BuilderUtils.newApplicationId(6384623L, 5), "/"));
        Assert.assertEquals("/proxy/application_6384623_0005/some/path", ProxyUriUtils.getPath(BuilderUtils.newApplicationId(6384623L, 5), "some/path"));
    }

    @Test
    public void testGetPathAndQuery() {
        Assert.assertEquals("/proxy/application_6384623_0005/static/app?foo=bar", ProxyUriUtils.getPathAndQuery(BuilderUtils.newApplicationId(6384623L, 5), "/static/app", "?foo=bar", false));
        Assert.assertEquals("/proxy/application_6384623_0005/static/app?foo=bar&bad=good&proxyapproved=true", ProxyUriUtils.getPathAndQuery(BuilderUtils.newApplicationId(6384623L, 5), "/static/app", "foo=bar&bad=good", true));
    }

    @Test
    public void testGetProxyUri() throws Exception {
        URI originalUri = new URI("http://host.com/static/foo?bar=bar");
        URI proxyUri = new URI("http://proxy.net:8080/");
        ApplicationId id = BuilderUtils.newApplicationId(6384623L, 5);
        URI expected = new URI("http://proxy.net:8080/proxy/application_6384623_0005/static/foo?bar=bar");
        URI result = ProxyUriUtils.getProxyUri(originalUri, proxyUri, id);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testGetProxyUriNull() throws Exception {
        URI originalUri = null;
        URI proxyUri = new URI("http://proxy.net:8080/");
        ApplicationId id = BuilderUtils.newApplicationId(6384623L, 5);
        URI expected = new URI("http://proxy.net:8080/proxy/application_6384623_0005/");
        URI result = ProxyUriUtils.getProxyUri(originalUri, proxyUri, id);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testGetProxyUriFromPluginsReturnsNullIfNoPlugins() throws URISyntaxException {
        ApplicationId id = BuilderUtils.newApplicationId(6384623L, 5);
        List<TrackingUriPlugin> list = Lists.newArrayListWithExpectedSize(0);
        Assert.assertNull(ProxyUriUtils.getUriFromTrackingPlugins(id, list));
    }

    @Test
    public void testGetProxyUriFromPluginsReturnsValidUriWhenAble() throws URISyntaxException {
        ApplicationId id = BuilderUtils.newApplicationId(6384623L, 5);
        List<TrackingUriPlugin> list = Lists.newArrayListWithExpectedSize(2);
        // Insert a plugin that returns null.
        list.add(new TrackingUriPlugin() {
            public URI getTrackingUri(ApplicationId id) throws URISyntaxException {
                return null;
            }
        });
        // Insert a plugin that returns a valid URI.
        list.add(new TrackingUriPlugin() {
            public URI getTrackingUri(ApplicationId id) throws URISyntaxException {
                return new URI("http://history.server.net/");
            }
        });
        URI result = ProxyUriUtils.getUriFromTrackingPlugins(id, list);
        Assert.assertNotNull(result);
    }
}

