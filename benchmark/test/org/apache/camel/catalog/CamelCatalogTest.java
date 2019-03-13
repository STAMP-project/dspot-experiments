/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.catalog;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CamelCatalogTest {
    static CamelCatalog catalog;

    private static final Logger LOG = LoggerFactory.getLogger(CamelCatalogTest.class);

    @Test
    public void testGetVersion() throws Exception {
        String version = CamelCatalogTest.catalog.getCatalogVersion();
        Assert.assertNotNull(version);
        String loaded = CamelCatalogTest.catalog.getLoadedVersion();
        Assert.assertNotNull(loaded);
        Assert.assertEquals(version, loaded);
    }

    @Test
    public void testLoadVersion() throws Exception {
        boolean result = CamelCatalogTest.catalog.loadVersion("1.0");
        Assert.assertFalse(result);
        String version = CamelCatalogTest.catalog.getCatalogVersion();
        result = CamelCatalogTest.catalog.loadVersion(version);
        Assert.assertTrue(result);
    }

    @Test
    public void testFindComponentNames() throws Exception {
        List<String> names = CamelCatalogTest.catalog.findComponentNames();
        Assert.assertNotNull(names);
        Assert.assertTrue(names.contains("file"));
        Assert.assertTrue(names.contains("log"));
        Assert.assertTrue(names.contains("docker"));
        Assert.assertTrue(names.contains("jms"));
        Assert.assertTrue(names.contains("activemq"));
        Assert.assertTrue(names.contains("zookeeper-master"));
    }

    @Test
    public void testFindOtherNames() throws Exception {
        List<String> names = CamelCatalogTest.catalog.findOtherNames();
        Assert.assertTrue(names.contains("hystrix"));
        Assert.assertTrue(names.contains("leveldb"));
        Assert.assertTrue(names.contains("kura"));
        Assert.assertTrue(names.contains("swagger-java"));
        Assert.assertTrue(names.contains("test-spring"));
        Assert.assertFalse(names.contains("http-common"));
        Assert.assertFalse(names.contains("core-osgi"));
        Assert.assertFalse(names.contains("file"));
        Assert.assertFalse(names.contains("ftp"));
        Assert.assertFalse(names.contains("jetty"));
    }

    @Test
    public void testFindDataFormatNames() throws Exception {
        List<String> names = CamelCatalogTest.catalog.findDataFormatNames();
        Assert.assertNotNull(names);
        Assert.assertTrue(names.contains("bindy-csv"));
        Assert.assertTrue(names.contains("hl7"));
        Assert.assertTrue(names.contains("jaxb"));
        Assert.assertTrue(names.contains("syslog"));
        Assert.assertTrue(names.contains("asn1"));
        Assert.assertTrue(names.contains("zipfile"));
    }

    @Test
    public void testFindLanguageNames() throws Exception {
        List<String> names = CamelCatalogTest.catalog.findLanguageNames();
        Assert.assertTrue(names.contains("simple"));
        Assert.assertTrue(names.contains("groovy"));
        Assert.assertTrue(names.contains("mvel"));
        Assert.assertTrue(names.contains("bean"));
        Assert.assertTrue(names.contains("file"));
        Assert.assertTrue(names.contains("xtokenize"));
        Assert.assertTrue(names.contains("hl7terser"));
    }

    @Test
    public void testFindModelNames() throws Exception {
        List<String> names = CamelCatalogTest.catalog.findModelNames();
        Assert.assertNotNull(names);
        Assert.assertTrue(names.contains("from"));
        Assert.assertTrue(names.contains("to"));
        Assert.assertTrue(names.contains("recipientList"));
        Assert.assertTrue(names.contains("aggregate"));
        Assert.assertTrue(names.contains("split"));
        Assert.assertTrue(names.contains("loadBalance"));
        Assert.assertTrue(names.contains("hystrix"));
        Assert.assertTrue(names.contains("saga"));
    }

    @Test
    public void testJsonSchema() throws Exception {
        String schema = CamelCatalogTest.catalog.componentJSonSchema("docker");
        Assert.assertNotNull(schema);
        schema = CamelCatalogTest.catalog.dataFormatJSonSchema("hl7");
        Assert.assertNotNull(schema);
        schema = CamelCatalogTest.catalog.languageJSonSchema("groovy");
        Assert.assertNotNull(schema);
        schema = CamelCatalogTest.catalog.modelJSonSchema("aggregate");
        Assert.assertNotNull(schema);
        schema = CamelCatalogTest.catalog.otherJSonSchema("swagger-java");
        Assert.assertNotNull(schema);
        // lets make it possible to find bean/method using both names
        schema = CamelCatalogTest.catalog.modelJSonSchema("method");
        Assert.assertNotNull(schema);
        schema = CamelCatalogTest.catalog.modelJSonSchema("bean");
        Assert.assertNotNull(schema);
    }

    @Test
    public void testXmlSchema() throws Exception {
        String schema = CamelCatalogTest.catalog.blueprintSchemaAsXml();
        Assert.assertNotNull(schema);
        schema = CamelCatalogTest.catalog.springSchemaAsXml();
        Assert.assertNotNull(schema);
    }

    @Test
    public void testArchetypeCatalog() throws Exception {
        String schema = CamelCatalogTest.catalog.archetypeCatalogAsXml();
        Assert.assertNotNull(schema);
    }

    @Test
    public void testAsEndpointUriMapFile() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("directoryName", "src/data/inbox");
        map.put("noop", "true");
        map.put("delay", "5000");
        String uri = CamelCatalogTest.catalog.asEndpointUri("file", map, true);
        Assert.assertEquals("file:src/data/inbox?delay=5000&noop=true", uri);
        String uri2 = CamelCatalogTest.catalog.asEndpointUriXml("file", map, true);
        Assert.assertEquals("file:src/data/inbox?delay=5000&amp;noop=true", uri2);
    }

    @Test
    public void testAsEndpointUriMapFtp() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("host", "someserver");
        map.put("port", "21");
        map.put("directoryName", "foo");
        map.put("connectTimeout", "5000");
        String uri = CamelCatalogTest.catalog.asEndpointUri("ftp", map, true);
        Assert.assertEquals("ftp:someserver:21/foo?connectTimeout=5000", uri);
        String uri2 = CamelCatalogTest.catalog.asEndpointUriXml("ftp", map, true);
        Assert.assertEquals("ftp:someserver:21/foo?connectTimeout=5000", uri2);
    }

    @Test
    public void testAsEndpointUriMapJms() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("destinationType", "queue");
        map.put("destinationName", "foo");
        String uri = CamelCatalogTest.catalog.asEndpointUri("jms", map, true);
        Assert.assertEquals("jms:queue:foo", uri);
    }

    @Test
    public void testAsEndpointUriNetty4http() throws Exception {
        Map<String, String> map = new HashMap<>();
        // use http protocol
        map.put("protocol", "http");
        map.put("host", "localhost");
        map.put("port", "8080");
        map.put("path", "foo/bar");
        map.put("disconnect", "true");
        String uri = CamelCatalogTest.catalog.asEndpointUri("netty4-http", map, true);
        Assert.assertEquals("netty4-http:http:localhost:8080/foo/bar?disconnect=true", uri);
        // lets switch protocol
        map.put("protocol", "https");
        uri = CamelCatalogTest.catalog.asEndpointUri("netty4-http", map, true);
        Assert.assertEquals("netty4-http:https:localhost:8080/foo/bar?disconnect=true", uri);
        // lets set a query parameter in the path
        map.put("path", "foo/bar?verbose=true");
        map.put("disconnect", "true");
        uri = CamelCatalogTest.catalog.asEndpointUri("netty4-http", map, true);
        Assert.assertEquals("netty4-http:https:localhost:8080/foo/bar?verbose=true&disconnect=true", uri);
    }

    @Test
    public void testAsEndpointUriTimer() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("timerName", "foo");
        map.put("period", "5000");
        String uri = CamelCatalogTest.catalog.asEndpointUri("timer", map, true);
        Assert.assertEquals("timer:foo?period=5000", uri);
    }

    @Test
    public void testAsEndpointDefaultValue() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("destinationName", "cheese");
        map.put("maxMessagesPerTask", "-1");
        String uri = CamelCatalogTest.catalog.asEndpointUri("jms", map, true);
        Assert.assertEquals("jms:cheese?maxMessagesPerTask=-1", uri);
    }

    @Test
    public void testAsEndpointUriPropertiesPlaceholders() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("timerName", "foo");
        map.put("period", "{{howoften}}");
        map.put("repeatCount", "5");
        String uri = CamelCatalogTest.catalog.asEndpointUri("timer", map, true);
        Assert.assertEquals("timer:foo?period=%7B%7Bhowoften%7D%7D&repeatCount=5", uri);
        uri = CamelCatalogTest.catalog.asEndpointUri("timer", map, false);
        Assert.assertEquals("timer:foo?period={{howoften}}&repeatCount=5", uri);
    }

    @Test
    public void testAsEndpointUriBeanLookup() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("resourceUri", "foo.xslt");
        map.put("converter", "#myConverter");
        String uri = CamelCatalogTest.catalog.asEndpointUri("xslt", map, true);
        Assert.assertEquals("xslt:foo.xslt?converter=%23myConverter", uri);
        uri = CamelCatalogTest.catalog.asEndpointUri("xslt", map, false);
        Assert.assertEquals("xslt:foo.xslt?converter=#myConverter", uri);
    }

    @Test
    public void testAsEndpointUriMapJmsRequiredOnly() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("destinationName", "foo");
        String uri = CamelCatalogTest.catalog.asEndpointUri("jms", map, true);
        Assert.assertEquals("jms:foo", uri);
        map.put("deliveryPersistent", "false");
        map.put("allowNullBody", "true");
        uri = CamelCatalogTest.catalog.asEndpointUri("jms", map, true);
        Assert.assertEquals("jms:foo?allowNullBody=true&deliveryPersistent=false", uri);
        String uri2 = CamelCatalogTest.catalog.asEndpointUriXml("jms", map, true);
        Assert.assertEquals("jms:foo?allowNullBody=true&amp;deliveryPersistent=false", uri2);
    }

    @Test
    public void testAsEndpointUriRestUriTemplate() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("method", "get");
        map.put("path", "api");
        map.put("uriTemplate", "user/{id}");
        String uri = CamelCatalogTest.catalog.asEndpointUri("rest", map, true);
        Assert.assertEquals("rest:get:api:user/{id}", uri);
    }

    @Test
    public void testEndpointProperties() throws Exception {
        Map<String, String> map = CamelCatalogTest.catalog.endpointProperties("ftp:someserver:21/foo?connectTimeout=5000");
        Assert.assertNotNull(map);
        Assert.assertEquals(4, map.size());
        Assert.assertEquals("someserver", map.get("host"));
        Assert.assertEquals("21", map.get("port"));
        Assert.assertEquals("foo", map.get("directoryName"));
        Assert.assertEquals("5000", map.get("connectTimeout"));
    }

    @Test
    public void testEndpointLenientProperties() throws Exception {
        Map<String, String> map = CamelCatalogTest.catalog.endpointLenientProperties("http:myserver?throwExceptionOnFailure=false&foo=123&bar=456");
        Assert.assertNotNull(map);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("123", map.get("foo"));
        Assert.assertEquals("456", map.get("bar"));
        map = CamelCatalogTest.catalog.endpointLenientProperties("http:myserver?throwExceptionOnFailure=false&foo=123&bar=456&httpClient.timeout=5000&httpClient.soTimeout=10000");
        Assert.assertNotNull(map);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("123", map.get("foo"));
        Assert.assertEquals("456", map.get("bar"));
        map = CamelCatalogTest.catalog.endpointLenientProperties("http:myserver?throwExceptionOnFailure=false&foo=123&bar=456&httpClient.timeout=5000&httpClient.soTimeout=10000&myPrefix.baz=beer");
        Assert.assertNotNull(map);
        Assert.assertEquals(3, map.size());
        Assert.assertEquals("123", map.get("foo"));
        Assert.assertEquals("456", map.get("bar"));
        Assert.assertEquals("beer", map.get("myPrefix.baz"));
    }

    @Test
    public void testEndpointPropertiesPlaceholders() throws Exception {
        Map<String, String> map = CamelCatalogTest.catalog.endpointProperties("timer:foo?period={{howoften}}&repeatCount=5");
        Assert.assertNotNull(map);
        Assert.assertEquals(3, map.size());
        Assert.assertEquals("foo", map.get("timerName"));
        Assert.assertEquals("{{howoften}}", map.get("period"));
        Assert.assertEquals("5", map.get("repeatCount"));
    }

    @Test
    public void testEndpointPropertiesNetty4Http() throws Exception {
        Map<String, String> map = CamelCatalogTest.catalog.endpointProperties("netty4-http:http:localhost:8080/foo/bar?disconnect=true&keepAlive=false");
        Assert.assertNotNull(map);
        Assert.assertEquals(6, map.size());
        Assert.assertEquals("http", map.get("protocol"));
        Assert.assertEquals("localhost", map.get("host"));
        Assert.assertEquals("8080", map.get("port"));
        Assert.assertEquals("foo/bar", map.get("path"));
        Assert.assertEquals("true", map.get("disconnect"));
        Assert.assertEquals("false", map.get("keepAlive"));
    }

    @Test
    public void testEndpointPropertiesNetty4HttpDefaultPort() throws Exception {
        Map<String, String> map = CamelCatalogTest.catalog.endpointProperties("netty4-http:http:localhost/foo/bar?disconnect=true&keepAlive=false");
        Assert.assertNotNull(map);
        Assert.assertEquals(5, map.size());
        Assert.assertEquals("http", map.get("protocol"));
        Assert.assertEquals("localhost", map.get("host"));
        Assert.assertEquals("foo/bar", map.get("path"));
        Assert.assertEquals("true", map.get("disconnect"));
        Assert.assertEquals("false", map.get("keepAlive"));
    }

    @Test
    public void testEndpointPropertiesNetty4HttpPlaceholder() throws Exception {
        Map<String, String> map = CamelCatalogTest.catalog.endpointProperties("netty4-http:http:{{myhost}}:{{myport}}/foo/bar?disconnect=true&keepAlive=false");
        Assert.assertNotNull(map);
        Assert.assertEquals(6, map.size());
        Assert.assertEquals("http", map.get("protocol"));
        Assert.assertEquals("{{myhost}}", map.get("host"));
        Assert.assertEquals("{{myport}}", map.get("port"));
        Assert.assertEquals("foo/bar", map.get("path"));
        Assert.assertEquals("true", map.get("disconnect"));
        Assert.assertEquals("false", map.get("keepAlive"));
    }

    @Test
    public void testEndpointPropertiesNetty4HttpWithDoubleSlash() throws Exception {
        Map<String, String> map = CamelCatalogTest.catalog.endpointProperties("netty4-http:http://localhost:8080/foo/bar?disconnect=true&keepAlive=false");
        Assert.assertNotNull(map);
        Assert.assertEquals(6, map.size());
        Assert.assertEquals("http", map.get("protocol"));
        Assert.assertEquals("localhost", map.get("host"));
        Assert.assertEquals("8080", map.get("port"));
        Assert.assertEquals("foo/bar", map.get("path"));
        Assert.assertEquals("true", map.get("disconnect"));
        Assert.assertEquals("false", map.get("keepAlive"));
    }

    @Test
    public void testAsEndpointUriLog() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("loggerName", "foo");
        map.put("loggerLevel", "WARN");
        map.put("multiline", "true");
        map.put("showAll", "true");
        map.put("showBody", "false");
        map.put("showBodyType", "false");
        map.put("showExchangePattern", "false");
        map.put("style", "Tab");
        Assert.assertEquals("log:foo?loggerLevel=WARN&multiline=true&showAll=true&style=Tab", CamelCatalogTest.catalog.asEndpointUri("log", map, false));
    }

    @Test
    public void testAsEndpointUriLogShort() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("loggerName", "foo");
        map.put("loggerLevel", "DEBUG");
        Assert.assertEquals("log:foo?loggerLevel=DEBUG", CamelCatalogTest.catalog.asEndpointUri("log", map, false));
    }

    @Test
    public void testAsEndpointUriWithplaceholder() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("query", "{{insert}}");
        Assert.assertEquals("sql:{{insert}}", CamelCatalogTest.catalog.asEndpointUri("sql", map, false));
        map.put("useMessageBodyForSql", "true");
        Assert.assertEquals("sql:{{insert}}?useMessageBodyForSql=true", CamelCatalogTest.catalog.asEndpointUri("sql", map, false));
        map.put("parametersCount", "{{count}}");
        Assert.assertEquals("sql:{{insert}}?parametersCount={{count}}&useMessageBodyForSql=true", CamelCatalogTest.catalog.asEndpointUri("sql", map, false));
    }

    @Test
    public void testAsEndpointUriStream() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("kind", "url");
        map.put("url", "http://camel.apache.org");
        Assert.assertEquals("stream:url?url=http://camel.apache.org", CamelCatalogTest.catalog.asEndpointUri("stream", map, false));
    }

    @Test
    public void testEndpointPropertiesJms() throws Exception {
        Map<String, String> map = CamelCatalogTest.catalog.endpointProperties("jms:queue:foo");
        Assert.assertNotNull(map);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("queue", map.get("destinationType"));
        Assert.assertEquals("foo", map.get("destinationName"));
        map = CamelCatalogTest.catalog.endpointProperties("jms:foo");
        Assert.assertNotNull(map);
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("foo", map.get("destinationName"));
    }

    @Test
    public void testEndpointPropertiesJmsWithDotInName() throws Exception {
        Map<String, String> map = CamelCatalogTest.catalog.endpointProperties("jms:browse.me");
        Assert.assertNotNull(map);
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("browse.me", map.get("destinationName"));
        map = CamelCatalogTest.catalog.endpointProperties("jms:browse.me");
        Assert.assertNotNull(map);
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("browse.me", map.get("destinationName"));
    }

    @Test
    public void testEndpointPropertiesJmsRequired() throws Exception {
        Map<String, String> map = CamelCatalogTest.catalog.endpointProperties("jms:foo");
        Assert.assertNotNull(map);
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("foo", map.get("destinationName"));
        map = CamelCatalogTest.catalog.endpointProperties("jms:foo?allowNullBody=true&deliveryPersistent=false");
        Assert.assertNotNull(map);
        Assert.assertEquals(3, map.size());
        Assert.assertEquals("foo", map.get("destinationName"));
        Assert.assertEquals("true", map.get("allowNullBody"));
        Assert.assertEquals("false", map.get("deliveryPersistent"));
    }

    @Test
    public void testEndpointPropertiesAtom() throws Exception {
        Map<String, String> map = CamelCatalogTest.catalog.endpointProperties("atom:file:src/test/data/feed.atom");
        Assert.assertNotNull(map);
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("file:src/test/data/feed.atom", map.get("feedUri"));
        map = CamelCatalogTest.catalog.endpointProperties("atom:file:src/test/data/feed.atom?splitEntries=false&delay=5000");
        Assert.assertNotNull(map);
        Assert.assertEquals(3, map.size());
        Assert.assertEquals("file:src/test/data/feed.atom", map.get("feedUri"));
        Assert.assertEquals("false", map.get("splitEntries"));
        Assert.assertEquals("5000", map.get("delay"));
    }

    @Test
    public void testEndpointPropertiesMultiValued() throws Exception {
        Map<String, String> map = CamelCatalogTest.catalog.endpointProperties("http:helloworld?httpClientOptions=httpClient.foo=123&httpClient.bar=456");
        Assert.assertNotNull(map);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("helloworld", map.get("httpUri"));
        Assert.assertEquals("httpClient.foo=123&httpClient.bar=456", map.get("httpClientOptions"));
    }

    @Test
    public void testEndpointPropertiesSshWithUserInfo() throws Exception {
        Map<String, String> map = CamelCatalogTest.catalog.endpointProperties("ssh:localhost:8101?username=scott&password=tiger");
        Assert.assertNotNull(map);
        Assert.assertEquals(4, map.size());
        Assert.assertEquals("8101", map.get("port"));
        Assert.assertEquals("localhost", map.get("host"));
        Assert.assertEquals("scott", map.get("username"));
        Assert.assertEquals("tiger", map.get("password"));
        map = CamelCatalogTest.catalog.endpointProperties("ssh://scott:tiger@localhost:8101");
        Assert.assertNotNull(map);
        Assert.assertEquals(4, map.size());
        Assert.assertEquals("8101", map.get("port"));
        Assert.assertEquals("localhost", map.get("host"));
        Assert.assertEquals("scott", map.get("username"));
        Assert.assertEquals("tiger", map.get("password"));
    }

    @Test
    public void validateActiveMQProperties() throws Exception {
        // add activemq as known component
        CamelCatalogTest.catalog.addComponent("activemq", "org.apache.camel.component.activemq.ActiveMQComponent");
        // activemq
        EndpointValidationResult result = CamelCatalogTest.catalog.validateEndpointProperties("activemq:temp-queue:cheese?jmsMessageType=Bytes");
        Assert.assertTrue(result.isSuccess());
        result = CamelCatalogTest.catalog.validateEndpointProperties("activemq:temp-queue:cheese?jmsMessageType=Bytes");
        Assert.assertTrue(result.isSuccess());
        result = CamelCatalogTest.catalog.validateEndpointProperties("activemq:temp-queue:cheese?jmsMessageType=Bytes", false, true, false);
        Assert.assertTrue(result.isSuccess());
        result = CamelCatalogTest.catalog.validateEndpointProperties("activemq:temp-queue:cheese?jmsMessageType=Bytes", false, false, true);
        Assert.assertTrue(result.isSuccess());
        // connection factory
        result = CamelCatalogTest.catalog.validateEndpointProperties("activemq:Consumer.Baz.VirtualTopic.FooRequest?connectionFactory=#pooledJmsConnectionFactory");
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void validateJmsProperties() throws Exception {
        // jms
        EndpointValidationResult result = CamelCatalogTest.catalog.validateEndpointProperties("jms:temp-queue:cheese?jmsMessageType=Bytes");
        Assert.assertTrue(result.isSuccess());
        result = CamelCatalogTest.catalog.validateEndpointProperties("jms:temp-queue:cheese?jmsMessageType=Bytes");
        Assert.assertTrue(result.isSuccess());
        result = CamelCatalogTest.catalog.validateEndpointProperties("jms:temp-queue:cheese?jmsMessageType=Bytes", false, true, false);
        Assert.assertTrue(result.isSuccess());
        result = CamelCatalogTest.catalog.validateEndpointProperties("jms:temp-queue:cheese?jmsMessageType=Bytes", false, false, true);
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void validateProperties() throws Exception {
        // valid
        EndpointValidationResult result = CamelCatalogTest.catalog.validateEndpointProperties("log:mylog");
        Assert.assertTrue(result.isSuccess());
        // unknown
        result = CamelCatalogTest.catalog.validateEndpointProperties("log:mylog?level=WARN&foo=bar");
        Assert.assertFalse(result.isSuccess());
        Assert.assertTrue(result.getUnknown().contains("foo"));
        Assert.assertEquals(1, result.getNumberOfErrors());
        // enum
        result = CamelCatalogTest.catalog.validateEndpointProperties("jms:unknown:myqueue");
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("unknown", result.getInvalidEnum().get("destinationType"));
        Assert.assertEquals("queue", result.getDefaultValues().get("destinationType"));
        Assert.assertEquals(1, result.getNumberOfErrors());
        // reference okay
        result = CamelCatalogTest.catalog.validateEndpointProperties("jms:queue:myqueue?jmsKeyFormatStrategy=#key");
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(0, result.getNumberOfErrors());
        // reference
        result = CamelCatalogTest.catalog.validateEndpointProperties("jms:queue:myqueue?jmsKeyFormatStrategy=foo");
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("foo", result.getInvalidEnum().get("jmsKeyFormatStrategy"));
        Assert.assertEquals(1, result.getNumberOfErrors());
        // okay
        result = CamelCatalogTest.catalog.validateEndpointProperties("yammer:MESSAGES?accessToken=aaa&consumerKey=bbb&consumerSecret=ccc&useJson=true&initialDelay=500");
        Assert.assertTrue(result.isSuccess());
        // required / boolean / integer
        result = CamelCatalogTest.catalog.validateEndpointProperties("yammer:MESSAGES?accessToken=aaa&consumerKey=&useJson=no&initialDelay=five");
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals(4, result.getNumberOfErrors());
        Assert.assertTrue(result.getRequired().contains("consumerKey"));
        Assert.assertTrue(result.getRequired().contains("consumerSecret"));
        Assert.assertEquals("no", result.getInvalidBoolean().get("useJson"));
        Assert.assertEquals("five", result.getInvalidInteger().get("initialDelay"));
        // okay
        result = CamelCatalogTest.catalog.validateEndpointProperties("mqtt:myqtt?reconnectBackOffMultiplier=2.5");
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(0, result.getNumberOfErrors());
        // number
        result = CamelCatalogTest.catalog.validateEndpointProperties("mqtt:myqtt?reconnectBackOffMultiplier=five");
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("five", result.getInvalidNumber().get("reconnectBackOffMultiplier"));
        Assert.assertEquals(1, result.getNumberOfErrors());
        // unknown component
        result = CamelCatalogTest.catalog.validateEndpointProperties("foo:bar?me=you");
        Assert.assertFalse(result.isSuccess());
        Assert.assertTrue(result.getUnknownComponent().equals("foo"));
        Assert.assertEquals(1, result.getNumberOfErrors());
        // invalid boolean but default value
        result = CamelCatalogTest.catalog.validateEndpointProperties("log:output?showAll=ggg");
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("ggg", result.getInvalidBoolean().get("showAll"));
        Assert.assertEquals(1, result.getNumberOfErrors());
        // dataset
        result = CamelCatalogTest.catalog.validateEndpointProperties("dataset:foo?minRate=50");
        Assert.assertTrue(result.isSuccess());
        // time pattern
        result = CamelCatalogTest.catalog.validateEndpointProperties("timer://foo?fixedRate=true&delay=0&period=2s");
        Assert.assertTrue(result.isSuccess());
        // reference lookup
        result = CamelCatalogTest.catalog.validateEndpointProperties("timer://foo?fixedRate=#fixed&delay=#myDelay");
        Assert.assertTrue(result.isSuccess());
        // optional consumer. prefix
        result = CamelCatalogTest.catalog.validateEndpointProperties("file:inbox?consumer.delay=5000&consumer.greedy=true");
        Assert.assertTrue(result.isSuccess());
        // optional without consumer. prefix
        result = CamelCatalogTest.catalog.validateEndpointProperties("file:inbox?delay=5000&greedy=true");
        Assert.assertTrue(result.isSuccess());
        // mixed optional without consumer. prefix
        result = CamelCatalogTest.catalog.validateEndpointProperties("file:inbox?delay=5000&consumer.greedy=true");
        Assert.assertTrue(result.isSuccess());
        // prefix
        result = CamelCatalogTest.catalog.validateEndpointProperties("file:inbox?delay=5000&scheduler.foo=123&scheduler.bar=456");
        Assert.assertTrue(result.isSuccess());
        // stub
        result = CamelCatalogTest.catalog.validateEndpointProperties("stub:foo?me=123&you=456");
        Assert.assertTrue(result.isSuccess());
        // lenient on
        result = CamelCatalogTest.catalog.validateEndpointProperties("dataformat:string:marshal?foo=bar");
        Assert.assertTrue(result.isSuccess());
        // lenient off
        result = CamelCatalogTest.catalog.validateEndpointProperties("dataformat:string:marshal?foo=bar", true);
        Assert.assertFalse(result.isSuccess());
        Assert.assertTrue(result.getUnknown().contains("foo"));
        // lenient off consumer only
        result = CamelCatalogTest.catalog.validateEndpointProperties("netty4-http:http://myserver?foo=bar", false, true, false);
        Assert.assertFalse(result.isSuccess());
        // consumer should still fail because we cannot use lenient option in consumer mode
        Assert.assertEquals("foo", result.getUnknown().iterator().next());
        Assert.assertNull(result.getLenient());
        // lenient off producer only
        result = CamelCatalogTest.catalog.validateEndpointProperties("netty4-http:http://myserver?foo=bar", false, false, true);
        Assert.assertTrue(result.isSuccess());
        // foo is the lenient option
        Assert.assertEquals(1, result.getLenient().size());
        Assert.assertEquals("foo", result.getLenient().iterator().next());
        // lenient on consumer only
        result = CamelCatalogTest.catalog.validateEndpointProperties("netty4-http:http://myserver?foo=bar", true, true, false);
        Assert.assertFalse(result.isSuccess());
        // consumer should still fail because we cannot use lenient option in consumer mode
        Assert.assertEquals("foo", result.getUnknown().iterator().next());
        Assert.assertNull(result.getLenient());
        // lenient on producer only
        result = CamelCatalogTest.catalog.validateEndpointProperties("netty4-http:http://myserver?foo=bar", true, false, true);
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("foo", result.getUnknown().iterator().next());
        Assert.assertNull(result.getLenient());
        // lenient on rss consumer only
        result = CamelCatalogTest.catalog.validateEndpointProperties("rss:file:src/test/data/rss20.xml?splitEntries=true&sortEntries=true&consumer.delay=50&foo=bar", false, true, false);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals("foo", result.getLenient().iterator().next());
        // data format
        result = CamelCatalogTest.catalog.validateEndpointProperties("dataformat:string:marshal?charset=utf-8", true);
        Assert.assertTrue(result.isSuccess());
        // 2 slash after component name
        result = CamelCatalogTest.catalog.validateEndpointProperties("atmos://put?remotePath=/dummy.txt");
        Assert.assertTrue(result.isSuccess());
        // userinfo in authority with username and password
        result = CamelCatalogTest.catalog.validateEndpointProperties("ssh://karaf:karaf@localhost:8101");
        Assert.assertTrue(result.isSuccess());
        // userinfo in authority without password
        result = CamelCatalogTest.catalog.validateEndpointProperties("ssh://scott@localhost:8101?certResource=classpath:test_rsa&useFixedDelay=true&delay=5000&pollCommand=features:list%0A");
        Assert.assertTrue(result.isSuccess());
        // userinfo with both user and password and placeholder
        result = CamelCatalogTest.catalog.validateEndpointProperties("ssh://smx:smx@localhost:8181?timeout=3000");
        Assert.assertTrue(result.isSuccess());
        // and should also work when port is using a placeholder
        result = CamelCatalogTest.catalog.validateEndpointProperties("ssh://smx:smx@localhost:{{port}}?timeout=3000");
        Assert.assertTrue(result.isSuccess());
        // placeholder for a bunch of optional options
        result = CamelCatalogTest.catalog.validateEndpointProperties("aws-swf://activity?{{options}}");
        Assert.assertTrue(result.isSuccess());
        // incapable to parse
        result = CamelCatalogTest.catalog.validateEndpointProperties("{{getFtpUrl}}?recursive=true");
        Assert.assertFalse(result.isSuccess());
        Assert.assertTrue(((result.getIncapable()) != null));
    }

    @Test
    public void validatePropertiesSummary() throws Exception {
        EndpointValidationResult result = CamelCatalogTest.catalog.validateEndpointProperties("yammer:MESSAGES?blah=yada&accessToken=aaa&consumerKey=&useJson=no&initialDelay=five&pollStrategy=myStrategy");
        Assert.assertFalse(result.isSuccess());
        String reason = result.summaryErrorMessage(true);
        CamelCatalogTest.LOG.info(reason);
        result = CamelCatalogTest.catalog.validateEndpointProperties("jms:unknown:myqueue");
        Assert.assertFalse(result.isSuccess());
        reason = result.summaryErrorMessage(false);
        CamelCatalogTest.LOG.info(reason);
    }

    @Test
    public void validateTimePattern() throws Exception {
        Assert.assertTrue(CamelCatalogTest.catalog.validateTimePattern("0"));
        Assert.assertTrue(CamelCatalogTest.catalog.validateTimePattern("500"));
        Assert.assertTrue(CamelCatalogTest.catalog.validateTimePattern("10000"));
        Assert.assertTrue(CamelCatalogTest.catalog.validateTimePattern("5s"));
        Assert.assertTrue(CamelCatalogTest.catalog.validateTimePattern("5sec"));
        Assert.assertTrue(CamelCatalogTest.catalog.validateTimePattern("5secs"));
        Assert.assertTrue(CamelCatalogTest.catalog.validateTimePattern("3m"));
        Assert.assertTrue(CamelCatalogTest.catalog.validateTimePattern("3min"));
        Assert.assertTrue(CamelCatalogTest.catalog.validateTimePattern("3minutes"));
        Assert.assertTrue(CamelCatalogTest.catalog.validateTimePattern("5m15s"));
        Assert.assertTrue(CamelCatalogTest.catalog.validateTimePattern("1h"));
        Assert.assertTrue(CamelCatalogTest.catalog.validateTimePattern("1hour"));
        Assert.assertTrue(CamelCatalogTest.catalog.validateTimePattern("2hours"));
        Assert.assertFalse(CamelCatalogTest.catalog.validateTimePattern("bla"));
        Assert.assertFalse(CamelCatalogTest.catalog.validateTimePattern("2year"));
        Assert.assertFalse(CamelCatalogTest.catalog.validateTimePattern("60darn"));
    }

    @Test
    public void testEndpointComponentName() throws Exception {
        String name = CamelCatalogTest.catalog.endpointComponentName("jms:queue:foo");
        Assert.assertEquals("jms", name);
    }

    @Test
    public void testListComponentsAsJson() throws Exception {
        String json = CamelCatalogTest.catalog.listComponentsAsJson();
        Assert.assertNotNull(json);
        // validate we can parse the json
        ObjectMapper mapper = new ObjectMapper();
        JsonNode tree = mapper.readTree(json);
        Assert.assertNotNull(tree);
    }

    @Test
    public void testListDataFormatsAsJson() throws Exception {
        String json = CamelCatalogTest.catalog.listDataFormatsAsJson();
        Assert.assertNotNull(json);
        // validate we can parse the json
        ObjectMapper mapper = new ObjectMapper();
        JsonNode tree = mapper.readTree(json);
        Assert.assertNotNull(tree);
    }

    @Test
    public void testListLanguagesAsJson() throws Exception {
        String json = CamelCatalogTest.catalog.listLanguagesAsJson();
        Assert.assertNotNull(json);
        // validate we can parse the json
        ObjectMapper mapper = new ObjectMapper();
        JsonNode tree = mapper.readTree(json);
        Assert.assertNotNull(tree);
    }

    @Test
    public void testListModelsAsJson() throws Exception {
        String json = CamelCatalogTest.catalog.listModelsAsJson();
        Assert.assertNotNull(json);
        // validate we can parse the json
        ObjectMapper mapper = new ObjectMapper();
        JsonNode tree = mapper.readTree(json);
        Assert.assertNotNull(tree);
    }

    @Test
    public void testListOthersAsJson() throws Exception {
        String json = CamelCatalogTest.catalog.listOthersAsJson();
        Assert.assertNotNull(json);
        // validate we can parse the json
        ObjectMapper mapper = new ObjectMapper();
        JsonNode tree = mapper.readTree(json);
        Assert.assertNotNull(tree);
    }

    @Test
    public void testSummaryAsJson() throws Exception {
        String json = CamelCatalogTest.catalog.summaryAsJson();
        Assert.assertNotNull(json);
        // validate we can parse the json
        ObjectMapper mapper = new ObjectMapper();
        JsonNode tree = mapper.readTree(json);
        Assert.assertNotNull(tree);
    }

    @Test
    public void testAddComponent() throws Exception {
        CamelCatalogTest.catalog.addComponent("dummy", "org.foo.camel.DummyComponent");
        Assert.assertTrue(CamelCatalogTest.catalog.findComponentNames().contains("dummy"));
        String json = CamelCatalogTest.catalog.componentJSonSchema("dummy");
        Assert.assertNotNull(json);
        // validate we can parse the json
        ObjectMapper mapper = new ObjectMapper();
        JsonNode tree = mapper.readTree(json);
        Assert.assertNotNull(tree);
    }

    @Test
    public void testAddComponentWithJson() throws Exception {
        String json = CatalogHelper.loadText(new FileInputStream("src/test/resources/org/foo/camel/dummy.json"));
        Assert.assertNotNull(json);
        CamelCatalogTest.catalog.addComponent("dummy", "org.foo.camel.DummyComponent", json);
        Assert.assertTrue(CamelCatalogTest.catalog.findComponentNames().contains("dummy"));
        json = CamelCatalogTest.catalog.componentJSonSchema("dummy");
        Assert.assertNotNull(json);
        // validate we can parse the json
        ObjectMapper mapper = new ObjectMapper();
        JsonNode tree = mapper.readTree(json);
        Assert.assertNotNull(tree);
    }

    @Test
    public void testAddComponentWithPrettyJson() throws Exception {
        String json = CatalogHelper.loadText(new FileInputStream("src/test/resources/org/foo/camel/dummy-pretty.json"));
        Assert.assertNotNull(json);
        CamelCatalogTest.catalog.addComponent("dummy", "org.foo.camel.DummyComponent", json);
        Assert.assertTrue(CamelCatalogTest.catalog.findComponentNames().contains("dummy"));
        json = CamelCatalogTest.catalog.componentJSonSchema("dummy");
        Assert.assertNotNull(json);
        // validate we can parse the json
        ObjectMapper mapper = new ObjectMapper();
        JsonNode tree = mapper.readTree(json);
        Assert.assertNotNull(tree);
    }

    @Test
    public void testAddDataFormat() throws Exception {
        CamelCatalogTest.catalog.addDataFormat("dummyformat", "org.foo.camel.DummyDataFormat");
        Assert.assertTrue(CamelCatalogTest.catalog.findDataFormatNames().contains("dummyformat"));
        String json = CamelCatalogTest.catalog.dataFormatJSonSchema("dummyformat");
        Assert.assertNotNull(json);
        // validate we can parse the json
        ObjectMapper mapper = new ObjectMapper();
        JsonNode tree = mapper.readTree(json);
        Assert.assertNotNull(tree);
    }

    @Test
    public void testAddDataFormatWithJSon() throws Exception {
        String json = CatalogHelper.loadText(new FileInputStream("src/test/resources/org/foo/camel/dummyformat.json"));
        Assert.assertNotNull(json);
        CamelCatalogTest.catalog.addDataFormat("dummyformat", "org.foo.camel.DummyDataFormat", json);
        Assert.assertTrue(CamelCatalogTest.catalog.findDataFormatNames().contains("dummyformat"));
        json = CamelCatalogTest.catalog.dataFormatJSonSchema("dummyformat");
        Assert.assertNotNull(json);
        // validate we can parse the json
        ObjectMapper mapper = new ObjectMapper();
        JsonNode tree = mapper.readTree(json);
        Assert.assertNotNull(tree);
    }

    @Test
    public void testAddDataFormatWithPrettyJSon() throws Exception {
        String json = CatalogHelper.loadText(new FileInputStream("src/test/resources/org/foo/camel/dummyformat-pretty.json"));
        Assert.assertNotNull(json);
        CamelCatalogTest.catalog.addDataFormat("dummyformat", "org.foo.camel.DummyDataFormat", json);
        Assert.assertTrue(CamelCatalogTest.catalog.findDataFormatNames().contains("dummyformat"));
        json = CamelCatalogTest.catalog.dataFormatJSonSchema("dummyformat");
        Assert.assertNotNull(json);
        // validate we can parse the json
        ObjectMapper mapper = new ObjectMapper();
        JsonNode tree = mapper.readTree(json);
        Assert.assertNotNull(tree);
    }

    @Test
    public void testSimpleExpression() throws Exception {
        LanguageValidationResult result = CamelCatalogTest.catalog.validateLanguageExpression(null, "simple", "${body}");
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals("${body}", result.getText());
        result = CamelCatalogTest.catalog.validateLanguageExpression(null, "simple", "${body");
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("${body", result.getText());
        CamelCatalogTest.LOG.info(result.getError());
        Assert.assertTrue(result.getError().startsWith("expected symbol functionEnd but was eol at location 5"));
        Assert.assertEquals("expected symbol functionEnd but was eol", result.getShortError());
        Assert.assertEquals(5, result.getIndex());
        result = CamelCatalogTest.catalog.validateLanguageExpression(null, "simple", "${bodyxxx}");
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("${bodyxxx}", result.getText());
        CamelCatalogTest.LOG.info(result.getError());
        Assert.assertEquals("Valid syntax: ${body.OGNL} was: bodyxxx", result.getShortError());
        Assert.assertEquals(0, result.getIndex());
    }

    @Test
    public void testSimplePredicate() throws Exception {
        LanguageValidationResult result = CamelCatalogTest.catalog.validateLanguagePredicate(null, "simple", "${body} == 'abc'");
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals("${body} == 'abc'", result.getText());
        result = CamelCatalogTest.catalog.validateLanguagePredicate(null, "simple", "${body} > ${header.size");
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("${body} > ${header.size", result.getText());
        CamelCatalogTest.LOG.info(result.getError());
        Assert.assertTrue(result.getError().startsWith("expected symbol functionEnd but was eol at location 22"));
        Assert.assertEquals("expected symbol functionEnd but was eol", result.getShortError());
        Assert.assertEquals(22, result.getIndex());
    }

    @Test
    public void testPredicatePlaceholder() throws Exception {
        LanguageValidationResult result = CamelCatalogTest.catalog.validateLanguagePredicate(null, "simple", "${body} contains '{{danger}}'");
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals("${body} contains '{{danger}}'", result.getText());
        result = CamelCatalogTest.catalog.validateLanguagePredicate(null, "simple", "${bdy} contains '{{danger}}'");
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("${bdy} contains '{{danger}}'", result.getText());
        CamelCatalogTest.LOG.info(result.getError());
        Assert.assertTrue(result.getError().startsWith("Unknown function: bdy at location 0"));
        Assert.assertTrue(result.getError().contains("'{{danger}}'"));
        Assert.assertEquals("Unknown function: bdy", result.getShortError());
        Assert.assertEquals(0, result.getIndex());
    }

    @Test
    public void testValidateLanguage() throws Exception {
        LanguageValidationResult result = CamelCatalogTest.catalog.validateLanguageExpression(null, "simple", "${body}");
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals("${body}", result.getText());
        result = CamelCatalogTest.catalog.validateLanguageExpression(null, "header", "foo");
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals("foo", result.getText());
        result = CamelCatalogTest.catalog.validateLanguagePredicate(null, "simple", "${body} > 10");
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals("${body} > 10", result.getText());
        result = CamelCatalogTest.catalog.validateLanguagePredicate(null, "header", "bar");
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals("bar", result.getText());
        result = CamelCatalogTest.catalog.validateLanguagePredicate(null, "foobar", "bar");
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("Unknown language foobar", result.getError());
    }

    @Test
    public void testValidateJSonPathLanguage() throws Exception {
        LanguageValidationResult result = CamelCatalogTest.catalog.validateLanguageExpression(null, "jsonpath", "$.store.book[?(@.price < 10)]");
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals("$.store.book[?(@.price < 10)]", result.getText());
        result = CamelCatalogTest.catalog.validateLanguageExpression(null, "jsonpath", "$.store.book[?(@.price ^^^ 10)]");
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("$.store.book[?(@.price ^^^ 10)]", result.getText());
        Assert.assertEquals("Illegal syntax: $.store.book[?(@.price ^^^ 10)]", result.getError());
    }

    @Test
    public void testSpringCamelContext() throws Exception {
        String json = CamelCatalogTest.catalog.modelJSonSchema("camelContext");
        Assert.assertNotNull(json);
        // validate we can parse the json
        ObjectMapper mapper = new ObjectMapper();
        JsonNode tree = mapper.readTree(json);
        Assert.assertNotNull(tree);
        Assert.assertTrue(json.contains("CamelContext using XML configuration"));
    }

    @Test
    public void testComponentAsciiDoc() throws Exception {
        String doc = CamelCatalogTest.catalog.componentAsciiDoc("mock");
        Assert.assertNotNull(doc);
        Assert.assertTrue(doc.contains("mock:someName"));
        doc = CamelCatalogTest.catalog.componentAsciiDoc("geocoder");
        Assert.assertNotNull(doc);
        Assert.assertTrue(doc.contains("looking up geocodes"));
        doc = CamelCatalogTest.catalog.componentAsciiDoc("smtp");
        Assert.assertNotNull(doc);
        Assert.assertTrue(doc.contains("The mail component"));
        doc = CamelCatalogTest.catalog.componentAsciiDoc("unknown");
        Assert.assertNull(doc);
    }

    @Test
    public void testTransactedAndPolicyNoOutputs() throws Exception {
        String json = CamelCatalogTest.catalog.modelJSonSchema("transacted");
        Assert.assertNotNull(json);
        Assert.assertTrue(json.contains("\"output\": false"));
        Assert.assertFalse(json.contains("\"outputs\":"));
        json = CamelCatalogTest.catalog.modelJSonSchema("policy");
        Assert.assertNotNull(json);
        Assert.assertTrue(json.contains("\"output\": false"));
        Assert.assertFalse(json.contains("\"outputs\":"));
    }

    @Test
    public void testDataFormatAsciiDoc() throws Exception {
        String doc = CamelCatalogTest.catalog.dataFormatAsciiDoc("json-jackson");
        Assert.assertNotNull(doc);
        Assert.assertTrue(doc.contains("Jackson dataformat"));
        doc = CamelCatalogTest.catalog.dataFormatAsciiDoc("bindy-csv");
        Assert.assertNotNull(doc);
        Assert.assertTrue(doc.contains("CsvRecord"));
    }

    @Test
    public void testLanguageAsciiDoc() throws Exception {
        String doc = CamelCatalogTest.catalog.languageAsciiDoc("jsonpath");
        Assert.assertNotNull(doc);
        Assert.assertTrue(doc.contains("JSonPath language"));
    }

    @Test
    public void testOtherAsciiDoc() throws Exception {
        String doc = CamelCatalogTest.catalog.otherAsciiDoc("swagger-java");
        Assert.assertNotNull(doc);
        Assert.assertTrue(doc.contains("Swagger"));
    }

    @Test
    public void testValidateEndpointTwitterSpecial() throws Exception {
        String uri = "twitter-search://java?{{%s}}";
        EndpointValidationResult result = CamelCatalogTest.catalog.validateEndpointProperties(uri);
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void testValidateEndpointJmsDefault() throws Exception {
        String uri = "jms:cheese?maxMessagesPerTask=-1";
        EndpointValidationResult result = CamelCatalogTest.catalog.validateEndpointProperties(uri);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(1, result.getDefaultValues().size());
        Assert.assertEquals("-1", result.getDefaultValues().get("maxMessagesPerTask"));
    }

    @Test
    public void testValidateEndpointConsumerOnly() throws Exception {
        String uri = "file:inbox?bufferSize=4096&readLock=changed&delete=true";
        EndpointValidationResult result = CamelCatalogTest.catalog.validateEndpointProperties(uri, false, true, false);
        Assert.assertTrue(result.isSuccess());
        uri = "file:inbox?bufferSize=4096&readLock=changed&delete=true&fileExist=Append";
        result = CamelCatalogTest.catalog.validateEndpointProperties(uri, false, true, false);
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("fileExist", result.getNotConsumerOnly().iterator().next());
    }

    @Test
    public void testValidateEndpointProducerOnly() throws Exception {
        String uri = "file:outbox?bufferSize=4096&fileExist=Append";
        EndpointValidationResult result = CamelCatalogTest.catalog.validateEndpointProperties(uri, false, false, true);
        Assert.assertTrue(result.isSuccess());
        uri = "file:outbox?bufferSize=4096&fileExist=Append&delete=true";
        result = CamelCatalogTest.catalog.validateEndpointProperties(uri, false, false, true);
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("delete", result.getNotProducerOnly().iterator().next());
    }

    @Test
    public void testNetty4Http4DynamicToIssue() throws Exception {
        String uri = "netty4-http:http://10.192.1.10:8080/client/alerts/summary?throwExceptionOnFailure=false";
        Map<String, String> params = CamelCatalogTest.catalog.endpointProperties(uri);
        params.remove("path");
        params.remove("throwExceptionOnFailure");
        String resolved = CamelCatalogTest.catalog.asEndpointUri("netty4-http", params, false);
        Assert.assertEquals("netty4-http:http:10.192.1.10:8080", resolved);
    }

    @Test
    public void testJSonSchemaHelper() throws Exception {
        String json = CatalogHelper.loadText(new FileInputStream("src/test/resources/org/foo/camel/dummy.json"));
        Assert.assertNotNull(json);
        // component
        List<Map<String, String>> rows = JSonSchemaHelper.parseJsonSchema("component", json, false);
        Assert.assertEquals(12, rows.size());
        Assert.assertTrue(JSonSchemaHelper.isComponentProducerOnly(rows));
        Assert.assertFalse(JSonSchemaHelper.isComponentConsumerOnly(rows));
        String desc = null;
        for (Map<String, String> row : rows) {
            if (row.containsKey("description")) {
                desc = row.get("description");
                break;
            }
        }
        Assert.assertEquals("The dummy component logs message exchanges to the underlying logging mechanism.", desc);
        // componentProperties
        rows = JSonSchemaHelper.parseJsonSchema("componentProperties", json, true);
        Assert.assertEquals(1, rows.size());
        Map<String, String> row = JSonSchemaHelper.getRow(rows, "exchangeFormatter");
        Assert.assertNotNull(row);
        Assert.assertEquals("org.apache.camel.spi.ExchangeFormatter", row.get("javaType"));
        Assert.assertEquals("Exchange Formatter", row.get("displayName"));
        // properties
        rows = JSonSchemaHelper.parseJsonSchema("properties", json, true);
        Assert.assertEquals(31, rows.size());
        row = JSonSchemaHelper.getRow(rows, "level");
        Assert.assertNotNull(row);
        Assert.assertEquals("INFO", row.get("defaultValue"));
        String enums = JSonSchemaHelper.getPropertyEnum(rows, "level");
        Assert.assertEquals("ERROR,WARN,INFO,DEBUG,TRACE,OFF", enums);
        Assert.assertEquals("Level", row.get("displayName"));
        row = JSonSchemaHelper.getRow(rows, "amount");
        Assert.assertNotNull(row);
        Assert.assertEquals("1", row.get("defaultValue"));
        Assert.assertEquals("Number of drinks in the order", row.get("description"));
        Assert.assertEquals("Amount", row.get("displayName"));
        row = JSonSchemaHelper.getRow(rows, "maxChars");
        Assert.assertNotNull(row);
        Assert.assertEquals("false", row.get("deprecated"));
        Assert.assertEquals("10000", row.get("defaultValue"));
        Assert.assertEquals("Max Chars", row.get("displayName"));
        row = JSonSchemaHelper.getRow(rows, "repeatCount");
        Assert.assertNotNull(row);
        Assert.assertEquals("long", row.get("javaType"));
        Assert.assertEquals("0", row.get("defaultValue"));
        Assert.assertEquals("Repeat Count", row.get("displayName"));
        row = JSonSchemaHelper.getRow(rows, "fontSize");
        Assert.assertNotNull(row);
        Assert.assertEquals("false", row.get("deprecated"));
        Assert.assertEquals("14", row.get("defaultValue"));
        Assert.assertEquals("Font Size", row.get("displayName"));
        row = JSonSchemaHelper.getRow(rows, "kerberosRenewJitter");
        Assert.assertNotNull(row);
        Assert.assertEquals("java.lang.Double", row.get("javaType"));
        Assert.assertEquals("0.05", row.get("defaultValue"));
        Assert.assertEquals("Kerberos Renew Jitter", row.get("displayName"));
    }
}

