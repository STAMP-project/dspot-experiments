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
package org.apache.camel.runtimecatalog.impl;


import java.util.HashMap;
import java.util.Map;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.runtimecatalog.EndpointValidationResult;
import org.apache.camel.runtimecatalog.LanguageValidationResult;
import org.apache.camel.runtimecatalog.RuntimeCamelCatalog;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RuntimeCamelCatalogTest {
    static RuntimeCamelCatalog catalog;

    private static final Logger LOG = LoggerFactory.getLogger(RuntimeCamelCatalogTest.class);

    @Test
    public void testFromCamelContext() throws Exception {
        String schema = new DefaultCamelContext().getExtension(RuntimeCamelCatalog.class).modelJSonSchema("choice");
        Assert.assertNotNull(schema);
    }

    @Test
    public void testJsonSchema() throws Exception {
        String schema = RuntimeCamelCatalogTest.catalog.modelJSonSchema("aggregate");
        Assert.assertNotNull(schema);
        // lets make it possible to find bean/method using both names
        schema = RuntimeCamelCatalogTest.catalog.modelJSonSchema("method");
        Assert.assertNotNull(schema);
        schema = RuntimeCamelCatalogTest.catalog.modelJSonSchema("bean");
        Assert.assertNotNull(schema);
    }

    @Test
    public void testAsEndpointUriMapFile() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("directoryName", "src/data/inbox");
        map.put("noop", "true");
        map.put("delay", "5000");
        String uri = RuntimeCamelCatalogTest.catalog.asEndpointUri("file", map, true);
        Assert.assertEquals("file:src/data/inbox?delay=5000&noop=true", uri);
        String uri2 = RuntimeCamelCatalogTest.catalog.asEndpointUriXml("file", map, true);
        Assert.assertEquals("file:src/data/inbox?delay=5000&amp;noop=true", uri2);
    }

    @Test
    public void testAsEndpointUriTimer() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("timerName", "foo");
        map.put("period", "5000");
        String uri = RuntimeCamelCatalogTest.catalog.asEndpointUri("timer", map, true);
        Assert.assertEquals("timer:foo?period=5000", uri);
    }

    @Test
    public void testAsEndpointUriPropertiesPlaceholders() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("timerName", "foo");
        map.put("period", "{{howoften}}");
        map.put("repeatCount", "5");
        String uri = RuntimeCamelCatalogTest.catalog.asEndpointUri("timer", map, true);
        Assert.assertEquals("timer:foo?period=%7B%7Bhowoften%7D%7D&repeatCount=5", uri);
        uri = RuntimeCamelCatalogTest.catalog.asEndpointUri("timer", map, false);
        Assert.assertEquals("timer:foo?period={{howoften}}&repeatCount=5", uri);
    }

    @Test
    public void testAsEndpointUriBeanLookup() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("resourceUri", "foo.xslt");
        map.put("converter", "#myConverter");
        String uri = RuntimeCamelCatalogTest.catalog.asEndpointUri("xslt", map, true);
        Assert.assertEquals("xslt:foo.xslt?converter=%23myConverter", uri);
        uri = RuntimeCamelCatalogTest.catalog.asEndpointUri("xslt", map, false);
        Assert.assertEquals("xslt:foo.xslt?converter=#myConverter", uri);
    }

    @Test
    public void testEndpointPropertiesPlaceholders() throws Exception {
        Map<String, String> map = RuntimeCamelCatalogTest.catalog.endpointProperties("timer:foo?period={{howoften}}&repeatCount=5");
        Assert.assertNotNull(map);
        Assert.assertEquals(3, map.size());
        Assert.assertEquals("foo", map.get("timerName"));
        Assert.assertEquals("{{howoften}}", map.get("period"));
        Assert.assertEquals("5", map.get("repeatCount"));
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
        Assert.assertEquals("log:foo?loggerLevel=WARN&multiline=true&showAll=true&style=Tab", RuntimeCamelCatalogTest.catalog.asEndpointUri("log", map, false));
    }

    @Test
    public void testAsEndpointUriLogShort() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("loggerName", "foo");
        map.put("loggerLevel", "DEBUG");
        Assert.assertEquals("log:foo?loggerLevel=DEBUG", RuntimeCamelCatalogTest.catalog.asEndpointUri("log", map, false));
    }

    @Test
    public void testAsEndpointUriWithplaceholder() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("name", "foo");
        map.put("blockWhenFull", "{{block}}");
        Assert.assertEquals("seda:foo?blockWhenFull={{block}}", RuntimeCamelCatalogTest.catalog.asEndpointUri("seda", map, false));
    }

    @Test
    public void testEndpointPropertiesSedaRequired() throws Exception {
        Map<String, String> map = RuntimeCamelCatalogTest.catalog.endpointProperties("seda:foo");
        Assert.assertNotNull(map);
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("foo", map.get("name"));
        map = RuntimeCamelCatalogTest.catalog.endpointProperties("seda:foo?blockWhenFull=true");
        Assert.assertNotNull(map);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("foo", map.get("name"));
        Assert.assertEquals("true", map.get("blockWhenFull"));
    }

    @Test
    public void validateProperties() throws Exception {
        // valid
        EndpointValidationResult result = RuntimeCamelCatalogTest.catalog.validateEndpointProperties("log:mylog");
        Assert.assertTrue(result.isSuccess());
        // unknown
        result = RuntimeCamelCatalogTest.catalog.validateEndpointProperties("log:mylog?level=WARN&foo=bar");
        Assert.assertFalse(result.isSuccess());
        Assert.assertTrue(result.getUnknown().contains("foo"));
        Assert.assertEquals(1, result.getNumberOfErrors());
        // enum
        result = RuntimeCamelCatalogTest.catalog.validateEndpointProperties("seda:foo?waitForTaskToComplete=blah");
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("blah", result.getInvalidEnum().get("waitForTaskToComplete"));
        Assert.assertEquals(1, result.getNumberOfErrors());
        // reference okay
        result = RuntimeCamelCatalogTest.catalog.validateEndpointProperties("seda:foo?queue=#queue");
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(0, result.getNumberOfErrors());
        // unknown component
        result = RuntimeCamelCatalogTest.catalog.validateEndpointProperties("foo:bar?me=you");
        Assert.assertFalse(result.isSuccess());
        Assert.assertTrue(result.getUnknownComponent().equals("foo"));
        Assert.assertEquals(1, result.getNumberOfErrors());
        // invalid boolean but default value
        result = RuntimeCamelCatalogTest.catalog.validateEndpointProperties("log:output?showAll=ggg");
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("ggg", result.getInvalidBoolean().get("showAll"));
        Assert.assertEquals(1, result.getNumberOfErrors());
        // dataset
        result = RuntimeCamelCatalogTest.catalog.validateEndpointProperties("dataset:foo?minRate=50");
        Assert.assertTrue(result.isSuccess());
        // time pattern
        result = RuntimeCamelCatalogTest.catalog.validateEndpointProperties("timer://foo?fixedRate=true&delay=0&period=2s");
        Assert.assertTrue(result.isSuccess());
        // reference lookup
        result = RuntimeCamelCatalogTest.catalog.validateEndpointProperties("timer://foo?fixedRate=#fixed&delay=#myDelay");
        Assert.assertTrue(result.isSuccess());
        // optional consumer. prefix
        result = RuntimeCamelCatalogTest.catalog.validateEndpointProperties("file:inbox?consumer.delay=5000&consumer.greedy=true");
        Assert.assertTrue(result.isSuccess());
        // optional without consumer. prefix
        result = RuntimeCamelCatalogTest.catalog.validateEndpointProperties("file:inbox?delay=5000&greedy=true");
        Assert.assertTrue(result.isSuccess());
        // mixed optional without consumer. prefix
        result = RuntimeCamelCatalogTest.catalog.validateEndpointProperties("file:inbox?delay=5000&consumer.greedy=true");
        Assert.assertTrue(result.isSuccess());
        // prefix
        result = RuntimeCamelCatalogTest.catalog.validateEndpointProperties("file:inbox?delay=5000&scheduler.foo=123&scheduler.bar=456");
        Assert.assertTrue(result.isSuccess());
        // stub
        result = RuntimeCamelCatalogTest.catalog.validateEndpointProperties("stub:foo?me=123&you=456");
        Assert.assertTrue(result.isSuccess());
        // lenient on
        result = RuntimeCamelCatalogTest.catalog.validateEndpointProperties("dataformat:string:marshal?foo=bar");
        Assert.assertTrue(result.isSuccess());
        // lenient off
        result = RuntimeCamelCatalogTest.catalog.validateEndpointProperties("dataformat:string:marshal?foo=bar", true);
        Assert.assertFalse(result.isSuccess());
        Assert.assertTrue(result.getUnknown().contains("foo"));
        // data format
        result = RuntimeCamelCatalogTest.catalog.validateEndpointProperties("dataformat:string:marshal?charset=utf-8", true);
        Assert.assertTrue(result.isSuccess());
        // incapable to parse
        result = RuntimeCamelCatalogTest.catalog.validateEndpointProperties("{{getFtpUrl}}?recursive=true");
        Assert.assertFalse(result.isSuccess());
        Assert.assertTrue(((result.getIncapable()) != null));
    }

    @Test
    public void validatePropertiesSummary() throws Exception {
        EndpointValidationResult result = RuntimeCamelCatalogTest.catalog.validateEndpointProperties("yammer:MESSAGES?blah=yada&accessToken=aaa&consumerKey=&useJson=no&initialDelay=five&pollStrategy=myStrategy");
        Assert.assertFalse(result.isSuccess());
        String reason = result.summaryErrorMessage(true);
        RuntimeCamelCatalogTest.LOG.info(reason);
        result = RuntimeCamelCatalogTest.catalog.validateEndpointProperties("jms:unknown:myqueue");
        Assert.assertFalse(result.isSuccess());
        reason = result.summaryErrorMessage(false);
        RuntimeCamelCatalogTest.LOG.info(reason);
    }

    @Test
    public void validateTimePattern() throws Exception {
        Assert.assertTrue(RuntimeCamelCatalogTest.catalog.validateTimePattern("0"));
        Assert.assertTrue(RuntimeCamelCatalogTest.catalog.validateTimePattern("500"));
        Assert.assertTrue(RuntimeCamelCatalogTest.catalog.validateTimePattern("10000"));
        Assert.assertTrue(RuntimeCamelCatalogTest.catalog.validateTimePattern("5s"));
        Assert.assertTrue(RuntimeCamelCatalogTest.catalog.validateTimePattern("5sec"));
        Assert.assertTrue(RuntimeCamelCatalogTest.catalog.validateTimePattern("5secs"));
        Assert.assertTrue(RuntimeCamelCatalogTest.catalog.validateTimePattern("3m"));
        Assert.assertTrue(RuntimeCamelCatalogTest.catalog.validateTimePattern("3min"));
        Assert.assertTrue(RuntimeCamelCatalogTest.catalog.validateTimePattern("3minutes"));
        Assert.assertTrue(RuntimeCamelCatalogTest.catalog.validateTimePattern("5m15s"));
        Assert.assertTrue(RuntimeCamelCatalogTest.catalog.validateTimePattern("1h"));
        Assert.assertTrue(RuntimeCamelCatalogTest.catalog.validateTimePattern("1hour"));
        Assert.assertTrue(RuntimeCamelCatalogTest.catalog.validateTimePattern("2hours"));
        Assert.assertFalse(RuntimeCamelCatalogTest.catalog.validateTimePattern("bla"));
        Assert.assertFalse(RuntimeCamelCatalogTest.catalog.validateTimePattern("2year"));
        Assert.assertFalse(RuntimeCamelCatalogTest.catalog.validateTimePattern("60darn"));
    }

    @Test
    public void testEndpointComponentName() throws Exception {
        String name = RuntimeCamelCatalogTest.catalog.endpointComponentName("jms:queue:foo");
        Assert.assertEquals("jms", name);
    }

    @Test
    public void testSimpleExpression() throws Exception {
        LanguageValidationResult result = RuntimeCamelCatalogTest.catalog.validateLanguageExpression(null, "simple", "${body}");
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals("${body}", result.getText());
        result = RuntimeCamelCatalogTest.catalog.validateLanguageExpression(null, "simple", "${body");
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("${body", result.getText());
        RuntimeCamelCatalogTest.LOG.info(result.getError());
        Assert.assertTrue(result.getError().startsWith("expected symbol functionEnd but was eol at location 5"));
        Assert.assertEquals("expected symbol functionEnd but was eol", result.getShortError());
        Assert.assertEquals(5, result.getIndex());
    }

    @Test
    public void testSimplePredicate() throws Exception {
        LanguageValidationResult result = RuntimeCamelCatalogTest.catalog.validateLanguagePredicate(null, "simple", "${body} == 'abc'");
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals("${body} == 'abc'", result.getText());
        result = RuntimeCamelCatalogTest.catalog.validateLanguagePredicate(null, "simple", "${body} > ${header.size");
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("${body} > ${header.size", result.getText());
        RuntimeCamelCatalogTest.LOG.info(result.getError());
        Assert.assertTrue(result.getError().startsWith("expected symbol functionEnd but was eol at location 22"));
        Assert.assertEquals("expected symbol functionEnd but was eol", result.getShortError());
        Assert.assertEquals(22, result.getIndex());
    }

    @Test
    public void testSimplePredicatePlaceholder() throws Exception {
        LanguageValidationResult result = RuntimeCamelCatalogTest.catalog.validateLanguagePredicate(null, "simple", "${body} contains '{{danger}}'");
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals("${body} contains '{{danger}}'", result.getText());
        result = RuntimeCamelCatalogTest.catalog.validateLanguagePredicate(null, "simple", "${bdy} contains '{{danger}}'");
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("${bdy} contains '{{danger}}'", result.getText());
        RuntimeCamelCatalogTest.LOG.info(result.getError());
        Assert.assertTrue(result.getError().startsWith("Unknown function: bdy at location 0"));
        Assert.assertTrue(result.getError().contains("'{{danger}}'"));
        Assert.assertEquals("Unknown function: bdy", result.getShortError());
        Assert.assertEquals(0, result.getIndex());
    }

    @Test
    public void testValidateLanguage() throws Exception {
        LanguageValidationResult result = RuntimeCamelCatalogTest.catalog.validateLanguageExpression(null, "simple", "${body}");
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals("${body}", result.getText());
        result = RuntimeCamelCatalogTest.catalog.validateLanguageExpression(null, "header", "foo");
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals("foo", result.getText());
        result = RuntimeCamelCatalogTest.catalog.validateLanguagePredicate(null, "simple", "${body} > 10");
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals("${body} > 10", result.getText());
        result = RuntimeCamelCatalogTest.catalog.validateLanguagePredicate(null, "header", "bar");
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals("bar", result.getText());
        result = RuntimeCamelCatalogTest.catalog.validateLanguagePredicate(null, "foobar", "bar");
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("Unknown language foobar", result.getError());
    }

    @Test
    public void testValidateEndpointConsumerOnly() throws Exception {
        String uri = "file:inbox?bufferSize=4096&readLock=changed&delete=true";
        EndpointValidationResult result = RuntimeCamelCatalogTest.catalog.validateEndpointProperties(uri, false, true, false);
        Assert.assertTrue(result.isSuccess());
        uri = "file:inbox?bufferSize=4096&readLock=changed&delete=true&fileExist=Append";
        result = RuntimeCamelCatalogTest.catalog.validateEndpointProperties(uri, false, true, false);
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("fileExist", result.getNotConsumerOnly().iterator().next());
    }

    @Test
    public void testValidateEndpointProducerOnly() throws Exception {
        String uri = "file:outbox?bufferSize=4096&fileExist=Append";
        EndpointValidationResult result = RuntimeCamelCatalogTest.catalog.validateEndpointProperties(uri, false, false, true);
        Assert.assertTrue(result.isSuccess());
        uri = "file:outbox?bufferSize=4096&fileExist=Append&delete=true";
        result = RuntimeCamelCatalogTest.catalog.validateEndpointProperties(uri, false, false, true);
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("delete", result.getNotProducerOnly().iterator().next());
    }
}

