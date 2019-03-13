/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.kafkarest.unit;


import KafkaRestConfig.HOST_NAME_CONFIG;
import KafkaRestConfig.LISTENERS_CONFIG;
import KafkaRestConfig.PORT_CONFIG;
import io.confluent.common.config.ConfigException;
import io.confluent.kafkarest.KafkaRestConfig;
import io.confluent.kafkarest.UriUtils;
import io.confluent.rest.RestConfigException;
import java.net.URI;
import java.util.Properties;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class UriUtilsTest {
    private UriInfo uriInfo;

    @Test
    public void testAbsoluteURIBuilderDefaultHost() throws RestConfigException {
        KafkaRestConfig config = new KafkaRestConfig();
        EasyMock.expect(uriInfo.getAbsolutePathBuilder()).andReturn(UriBuilder.fromUri("http://foo.com"));
        EasyMock.replay(uriInfo);
        Assert.assertEquals("http://foo.com", UriUtils.absoluteUriBuilder(config, uriInfo).build().toString());
        EasyMock.verify(uriInfo);
    }

    @Test
    public void testAbsoluteURIBuilderOverrideHost() throws RestConfigException {
        Properties props = new Properties();
        props.put(HOST_NAME_CONFIG, "bar.net");
        KafkaRestConfig config = new KafkaRestConfig(props);
        EasyMock.expect(uriInfo.getAbsolutePathBuilder()).andReturn(UriBuilder.fromUri("http://foo.com"));
        EasyMock.expect(uriInfo.getAbsolutePath()).andReturn(URI.create("http://foo.com"));
        EasyMock.replay(uriInfo);
        Assert.assertEquals("http://bar.net", UriUtils.absoluteUriBuilder(config, uriInfo).build().toString());
        EasyMock.verify(uriInfo);
    }

    @Test
    public void testAbsoluteURIBuilderWithPort() throws RestConfigException {
        Properties props = new Properties();
        props.put(HOST_NAME_CONFIG, "bar.net");
        props.put(PORT_CONFIG, 5000);
        KafkaRestConfig config = new KafkaRestConfig(props);
        EasyMock.expect(uriInfo.getAbsolutePathBuilder()).andReturn(UriBuilder.fromUri("http://foo.com:5000"));
        EasyMock.expect(uriInfo.getAbsolutePath()).andReturn(URI.create("http://foo.com:5000"));
        EasyMock.replay(uriInfo);
        Assert.assertEquals("http://bar.net:5000", UriUtils.absoluteUriBuilder(config, uriInfo).build().toString());
        EasyMock.verify(uriInfo);
    }

    @Test(expected = ConfigException.class)
    public void testAbsoluteURIBuilderWithInvalidListener() throws RestConfigException {
        Properties props = new Properties();
        props.put(HOST_NAME_CONFIG, "bar.net");
        props.put(LISTENERS_CONFIG, "http:||0.0.0.0:9091");
        KafkaRestConfig config = new KafkaRestConfig(props);
        EasyMock.expect(uriInfo.getAbsolutePathBuilder()).andReturn(UriBuilder.fromUri("http://foo.com:9091"));
        EasyMock.expect(uriInfo.getAbsolutePath()).andReturn(URI.create("http://foo.com:9091"));
        EasyMock.replay(uriInfo);
        UriUtils.absoluteUriBuilder(config, uriInfo);
    }

    @Test
    public void testAbsoluteURIBuilderWithListenerForHttp() throws RestConfigException {
        Properties props = new Properties();
        props.put(HOST_NAME_CONFIG, "bar.net");
        props.put(LISTENERS_CONFIG, "http://0.0.0.0:9091,https://0.0.0.0:9092");
        KafkaRestConfig config = new KafkaRestConfig(props);
        EasyMock.expect(uriInfo.getAbsolutePathBuilder()).andReturn(UriBuilder.fromUri("http://foo.com:9091"));
        EasyMock.expect(uriInfo.getAbsolutePath()).andReturn(URI.create("http://foo.com:9091"));
        EasyMock.replay(uriInfo);
        Assert.assertEquals("http://bar.net:9091", UriUtils.absoluteUriBuilder(config, uriInfo).build().toString());
        EasyMock.verify(uriInfo);
    }

    @Test
    public void testAbsoluteURIBuilderWithListenerForHttps() throws RestConfigException {
        Properties props = new Properties();
        props.put(HOST_NAME_CONFIG, "bar.net");
        props.put(LISTENERS_CONFIG, "http://0.0.0.0:9091,https://0.0.0.0:9092");
        KafkaRestConfig config = new KafkaRestConfig(props);
        EasyMock.expect(uriInfo.getAbsolutePathBuilder()).andReturn(UriBuilder.fromUri("https://foo.com:9092"));
        EasyMock.expect(uriInfo.getAbsolutePath()).andReturn(URI.create("https://foo.com:9092"));
        EasyMock.replay(uriInfo);
        Assert.assertEquals("https://bar.net:9092", UriUtils.absoluteUriBuilder(config, uriInfo).build().toString());
        EasyMock.verify(uriInfo);
    }

    @Test
    public void testAbsoluteURIBuilderWithIPV6Listener() throws RestConfigException {
        Properties props = new Properties();
        props.put(HOST_NAME_CONFIG, "bar.net");
        props.put(LISTENERS_CONFIG, "http://[fe80:0:1:2:3:4:5:6]:9092");
        KafkaRestConfig config = new KafkaRestConfig(props);
        EasyMock.expect(uriInfo.getAbsolutePathBuilder()).andReturn(UriBuilder.fromUri("http://foo.com:9092"));
        EasyMock.expect(uriInfo.getAbsolutePath()).andReturn(URI.create("http://foo.com:9092"));
        EasyMock.replay(uriInfo);
        Assert.assertEquals("http://bar.net:9092", UriUtils.absoluteUriBuilder(config, uriInfo).build().toString());
        EasyMock.verify(uriInfo);
    }

    @Test
    public void testAbsoluteURIBuilderWithTruncatedIPV6Listener() throws RestConfigException {
        Properties props = new Properties();
        props.put(HOST_NAME_CONFIG, "bar.net");
        props.put(LISTENERS_CONFIG, "http://[fe80::1]:9092");
        KafkaRestConfig config = new KafkaRestConfig(props);
        EasyMock.expect(uriInfo.getAbsolutePathBuilder()).andReturn(UriBuilder.fromUri("http://foo.com:9092"));
        EasyMock.expect(uriInfo.getAbsolutePath()).andReturn(URI.create("http://foo.com:9092"));
        EasyMock.replay(uriInfo);
        Assert.assertEquals("http://bar.net:9092", UriUtils.absoluteUriBuilder(config, uriInfo).build().toString());
        EasyMock.verify(uriInfo);
    }
}

