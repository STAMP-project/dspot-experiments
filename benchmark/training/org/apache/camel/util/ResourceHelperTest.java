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
package org.apache.camel.util;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.camel.CamelContext;
import org.apache.camel.TestSupport;
import org.apache.camel.converter.IOConverter;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.Registry;
import org.apache.camel.support.DefaultRegistry;
import org.apache.camel.support.ResourceHelper;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class ResourceHelperTest extends TestSupport {
    @Test
    public void testLoadFile() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.start();
        InputStream is = ResourceHelper.resolveMandatoryResourceAsInputStream(context, "file:src/test/resources/log4j2.properties");
        Assert.assertNotNull(is);
        String text = context.getTypeConverter().convertTo(String.class, is);
        Assert.assertNotNull(text);
        Assert.assertTrue(text.contains("rootLogger"));
        is.close();
        context.stop();
    }

    @Test
    public void testLoadFileWithSpace() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.start();
        TestSupport.createDirectory("target/data/my space");
        FileUtil.copyFile(new File("src/test/resources/log4j2.properties"), new File("target/data/my space/log4j2.properties"));
        InputStream is = ResourceHelper.resolveMandatoryResourceAsInputStream(context, "file:target/data/my%20space/log4j2.properties");
        Assert.assertNotNull(is);
        String text = context.getTypeConverter().convertTo(String.class, is);
        Assert.assertNotNull(text);
        Assert.assertTrue(text.contains("rootLogger"));
        is.close();
        context.stop();
    }

    @Test
    public void testLoadClasspath() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.start();
        InputStream is = ResourceHelper.resolveMandatoryResourceAsInputStream(context, "classpath:log4j2.properties");
        Assert.assertNotNull(is);
        String text = context.getTypeConverter().convertTo(String.class, is);
        Assert.assertNotNull(text);
        Assert.assertTrue(text.contains("rootLogger"));
        is.close();
        context.stop();
    }

    @Test
    public void testLoadRegistry() throws Exception {
        Registry registry = new DefaultRegistry();
        registry.bind("myBean", "This is a log4j logging configuration file");
        CamelContext context = new DefaultCamelContext(registry);
        context.start();
        InputStream is = ResourceHelper.resolveMandatoryResourceAsInputStream(context, "ref:myBean");
        Assert.assertNotNull(is);
        String text = context.getTypeConverter().convertTo(String.class, is);
        Assert.assertNotNull(text);
        Assert.assertTrue(text.contains("log4j"));
        is.close();
        context.stop();
    }

    @Test
    public void testLoadBeanDoubleColon() throws Exception {
        Registry registry = new DefaultRegistry();
        registry.bind("myBean", new AtomicReference<InputStream>(new ByteArrayInputStream("a".getBytes())));
        CamelContext context = new DefaultCamelContext(registry);
        context.start();
        InputStream is = ResourceHelper.resolveMandatoryResourceAsInputStream(context, "bean:myBean::get");
        Assert.assertNotNull(is);
        String text = context.getTypeConverter().convertTo(String.class, is);
        Assert.assertNotNull(text);
        Assert.assertEquals(text, "a");
        is.close();
        context.stop();
    }

    @Test
    public void testLoadBeanDoubleColonLong() throws Exception {
        Registry registry = new DefaultRegistry();
        registry.bind("my.company.MyClass", new AtomicReference<InputStream>(new ByteArrayInputStream("a".getBytes())));
        CamelContext context = new DefaultCamelContext(registry);
        context.start();
        InputStream is = ResourceHelper.resolveMandatoryResourceAsInputStream(context, "bean:my.company.MyClass::get");
        Assert.assertNotNull(is);
        String text = context.getTypeConverter().convertTo(String.class, is);
        Assert.assertNotNull(text);
        Assert.assertEquals(text, "a");
        is.close();
        context.stop();
    }

    @Test
    public void testLoadBeanDot() throws Exception {
        Registry registry = new DefaultRegistry();
        registry.bind("myBean", new AtomicReference<InputStream>(new ByteArrayInputStream("a".getBytes())));
        CamelContext context = new DefaultCamelContext(registry);
        context.start();
        InputStream is = ResourceHelper.resolveMandatoryResourceAsInputStream(context, "bean:myBean.get");
        Assert.assertNotNull(is);
        String text = context.getTypeConverter().convertTo(String.class, is);
        Assert.assertNotNull(text);
        Assert.assertEquals(text, "a");
        is.close();
        context.stop();
    }

    @Test
    public void testLoadClasspathDefault() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.start();
        InputStream is = ResourceHelper.resolveMandatoryResourceAsInputStream(context, "log4j2.properties");
        Assert.assertNotNull(is);
        String text = context.getTypeConverter().convertTo(String.class, is);
        Assert.assertNotNull(text);
        Assert.assertTrue(text.contains("rootLogger"));
        is.close();
        context.stop();
    }

    @Test
    public void testLoadFileNotFound() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.start();
        try {
            ResourceHelper.resolveMandatoryResourceAsInputStream(context, "file:src/test/resources/notfound.txt");
            Assert.fail("Should not find file");
        } catch (FileNotFoundException e) {
            Assert.assertTrue(e.getMessage().contains("notfound.txt"));
        }
        context.stop();
    }

    @Test
    public void testLoadClasspathNotFound() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.start();
        try {
            ResourceHelper.resolveMandatoryResourceAsInputStream(context, "classpath:notfound.txt");
            Assert.fail("Should not find file");
        } catch (FileNotFoundException e) {
            Assert.assertEquals("Cannot find resource: classpath:notfound.txt in classpath for URI: classpath:notfound.txt", e.getMessage());
        }
        context.stop();
    }

    @Test
    public void testLoadFileAsUrl() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.start();
        URL url = ResourceHelper.resolveMandatoryResourceAsUrl(context.getClassResolver(), "file:src/test/resources/log4j2.properties");
        Assert.assertNotNull(url);
        String text = context.getTypeConverter().convertTo(String.class, url);
        Assert.assertNotNull(text);
        Assert.assertTrue(text.contains("rootLogger"));
        context.stop();
    }

    @Test
    public void testLoadClasspathAsUrl() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.start();
        URL url = ResourceHelper.resolveMandatoryResourceAsUrl(context.getClassResolver(), "classpath:log4j2.properties");
        Assert.assertNotNull(url);
        String text = context.getTypeConverter().convertTo(String.class, url);
        Assert.assertNotNull(text);
        Assert.assertTrue(text.contains("rootLogger"));
        context.stop();
    }

    @Test
    public void testLoadCustomUrlasInputStream() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.start();
        String handlerPackageSystemProp = "java.protocol.handler.pkgs";
        String customUrlHandlerPackage = "org.apache.camel.urlhandler";
        TestSupport.registerSystemProperty(handlerPackageSystemProp, customUrlHandlerPackage, "|");
        InputStream is = ResourceHelper.resolveMandatoryResourceAsInputStream(context, "custom://hello");
        Assert.assertNotNull(is);
        Assert.assertEquals("hello", IOConverter.toString(IOHelper.buffered(new InputStreamReader(is, "UTF-8"))));
        context.stop();
    }

    @Test
    public void testLoadCustomUrlasInputStreamFail() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.start();
        try {
            InputStream is = ResourceHelper.resolveMandatoryResourceAsInputStream(context, "custom://hello");
            Assert.assertNotNull(is);
        } catch (Exception e) {
            Assert.assertEquals("unknown protocol: custom", e.getMessage());
        }
        context.stop();
    }

    @Test
    public void testLoadCustomUrl() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.start();
        String handlerPackageSystemProp = "java.protocol.handler.pkgs";
        String customUrlHandlerPackage = "org.apache.camel.urlhandler";
        TestSupport.registerSystemProperty(handlerPackageSystemProp, customUrlHandlerPackage, "|");
        URL url = ResourceHelper.resolveResourceAsUrl(context.getClassResolver(), "custom://hello");
        Assert.assertNotNull(url);
        String text = context.getTypeConverter().convertTo(String.class, url);
        Assert.assertNotNull(text);
        Assert.assertTrue(text.contains("hello"));
        context.stop();
    }

    @Test
    public void testLoadCustomUrlFail() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.start();
        try {
            ResourceHelper.resolveResourceAsUrl(context.getClassResolver(), "custom://hello");
        } catch (Exception e) {
            Assert.assertEquals("unknown protocol: custom", e.getMessage());
        }
        context.stop();
    }

    @Test
    public void testIsHttp() throws Exception {
        Assert.assertFalse(ResourceHelper.isHttpUri("direct:foo"));
        Assert.assertFalse(ResourceHelper.isHttpUri(""));
        Assert.assertFalse(ResourceHelper.isHttpUri(null));
        Assert.assertTrue(ResourceHelper.isHttpUri("http://camel.apache.org"));
        Assert.assertTrue(ResourceHelper.isHttpUri("https://camel.apache.org"));
    }

    @Test
    public void testGetScheme() throws Exception {
        Assert.assertEquals("file:", ResourceHelper.getScheme("file:myfile.txt"));
        Assert.assertEquals("classpath:", ResourceHelper.getScheme("classpath:myfile.txt"));
        Assert.assertEquals("http:", ResourceHelper.getScheme("http:www.foo.com"));
        Assert.assertEquals(null, ResourceHelper.getScheme("www.foo.com"));
        Assert.assertEquals(null, ResourceHelper.getScheme("myfile.txt"));
    }

    @Test
    public void testAppendParameters() throws Exception {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("foo", 123);
        params.put("bar", "yes");
        // should clear the map after usage
        Assert.assertEquals("http://localhost:8080/data?foo=123&bar=yes", ResourceHelper.appendParameters("http://localhost:8080/data", params));
        Assert.assertEquals(0, params.size());
    }
}

