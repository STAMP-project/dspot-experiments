/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.common;


import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class URLTest {
    @Test
    public void test_valueOf_noProtocolAndHost() throws Exception {
        URL url = URL.valueOf("/context/path?version=1.0.0&application=morgan");
        Assertions.assertNull(url.getProtocol());
        Assertions.assertNull(url.getUsername());
        Assertions.assertNull(url.getPassword());
        Assertions.assertNull(url.getHost());
        Assertions.assertEquals(0, url.getPort());
        Assertions.assertEquals("context/path", url.getPath());
        Assertions.assertEquals(2, url.getParameters().size());
        Assertions.assertEquals("1.0.0", url.getParameter("version"));
        Assertions.assertEquals("morgan", url.getParameter("application"));
        url = URL.valueOf("context/path?version=1.0.0&application=morgan");
        // ^^^^^^^ Caution , parse as host
        Assertions.assertNull(url.getProtocol());
        Assertions.assertNull(url.getUsername());
        Assertions.assertNull(url.getPassword());
        Assertions.assertEquals("context", url.getHost());
        Assertions.assertEquals(0, url.getPort());
        Assertions.assertEquals("path", url.getPath());
        Assertions.assertEquals(2, url.getParameters().size());
        Assertions.assertEquals("1.0.0", url.getParameter("version"));
        Assertions.assertEquals("morgan", url.getParameter("application"));
    }

    @Test
    public void test_valueOf_noProtocol() throws Exception {
        URL url = URL.valueOf("10.20.130.230");
        Assertions.assertNull(url.getProtocol());
        Assertions.assertNull(url.getUsername());
        Assertions.assertNull(url.getPassword());
        Assertions.assertEquals("10.20.130.230", url.getHost());
        Assertions.assertEquals(0, url.getPort());
        Assertions.assertEquals(null, url.getPath());
        Assertions.assertEquals(0, url.getParameters().size());
        url = URL.valueOf("10.20.130.230:20880");
        Assertions.assertNull(url.getProtocol());
        Assertions.assertNull(url.getUsername());
        Assertions.assertNull(url.getPassword());
        Assertions.assertEquals("10.20.130.230", url.getHost());
        Assertions.assertEquals(20880, url.getPort());
        Assertions.assertEquals(null, url.getPath());
        Assertions.assertEquals(0, url.getParameters().size());
        url = URL.valueOf("10.20.130.230/context/path");
        Assertions.assertNull(url.getProtocol());
        Assertions.assertNull(url.getUsername());
        Assertions.assertNull(url.getPassword());
        Assertions.assertEquals("10.20.130.230", url.getHost());
        Assertions.assertEquals(0, url.getPort());
        Assertions.assertEquals("context/path", url.getPath());
        Assertions.assertEquals(0, url.getParameters().size());
        url = URL.valueOf("10.20.130.230:20880/context/path");
        Assertions.assertNull(url.getProtocol());
        Assertions.assertNull(url.getUsername());
        Assertions.assertNull(url.getPassword());
        Assertions.assertEquals("10.20.130.230", url.getHost());
        Assertions.assertEquals(20880, url.getPort());
        Assertions.assertEquals("context/path", url.getPath());
        Assertions.assertEquals(0, url.getParameters().size());
        url = URL.valueOf("admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan");
        Assertions.assertNull(url.getProtocol());
        Assertions.assertEquals("admin", url.getUsername());
        Assertions.assertEquals("hello1234", url.getPassword());
        Assertions.assertEquals("10.20.130.230", url.getHost());
        Assertions.assertEquals(20880, url.getPort());
        Assertions.assertEquals("context/path", url.getPath());
        Assertions.assertEquals(2, url.getParameters().size());
        Assertions.assertEquals("1.0.0", url.getParameter("version"));
        Assertions.assertEquals("morgan", url.getParameter("application"));
    }

    @Test
    public void test_valueOf_noHost() throws Exception {
        URL url = URL.valueOf("file:///home/user1/router.js");
        Assertions.assertEquals("file", url.getProtocol());
        Assertions.assertNull(url.getUsername());
        Assertions.assertNull(url.getPassword());
        Assertions.assertNull(url.getHost());
        Assertions.assertEquals(0, url.getPort());
        Assertions.assertEquals("home/user1/router.js", url.getPath());
        Assertions.assertEquals(0, url.getParameters().size());
        // Caution!!
        url = URL.valueOf("file://home/user1/router.js");
        // ^^ only tow slash!
        Assertions.assertEquals("file", url.getProtocol());
        Assertions.assertNull(url.getUsername());
        Assertions.assertNull(url.getPassword());
        Assertions.assertEquals("home", url.getHost());
        Assertions.assertEquals(0, url.getPort());
        Assertions.assertEquals("user1/router.js", url.getPath());
        Assertions.assertEquals(0, url.getParameters().size());
        url = URL.valueOf("file:/home/user1/router.js");
        Assertions.assertEquals("file", url.getProtocol());
        Assertions.assertNull(url.getUsername());
        Assertions.assertNull(url.getPassword());
        Assertions.assertNull(url.getHost());
        Assertions.assertEquals(0, url.getPort());
        Assertions.assertEquals("home/user1/router.js", url.getPath());
        Assertions.assertEquals(0, url.getParameters().size());
        url = URL.valueOf("file:///d:/home/user1/router.js");
        Assertions.assertEquals("file", url.getProtocol());
        Assertions.assertNull(url.getUsername());
        Assertions.assertNull(url.getPassword());
        Assertions.assertNull(url.getHost());
        Assertions.assertEquals(0, url.getPort());
        Assertions.assertEquals("d:/home/user1/router.js", url.getPath());
        Assertions.assertEquals(0, url.getParameters().size());
        url = URL.valueOf("file:///home/user1/router.js?p1=v1&p2=v2");
        Assertions.assertEquals("file", url.getProtocol());
        Assertions.assertNull(url.getUsername());
        Assertions.assertNull(url.getPassword());
        Assertions.assertNull(url.getHost());
        Assertions.assertEquals(0, url.getPort());
        Assertions.assertEquals("home/user1/router.js", url.getPath());
        Assertions.assertEquals(2, url.getParameters().size());
        Map<String, String> params = new HashMap<String, String>();
        params.put("p1", "v1");
        params.put("p2", "v2");
        Assertions.assertEquals(params, url.getParameters());
        url = URL.valueOf("file:/home/user1/router.js?p1=v1&p2=v2");
        Assertions.assertEquals("file", url.getProtocol());
        Assertions.assertNull(url.getUsername());
        Assertions.assertNull(url.getPassword());
        Assertions.assertNull(url.getHost());
        Assertions.assertEquals(0, url.getPort());
        Assertions.assertEquals("home/user1/router.js", url.getPath());
        Assertions.assertEquals(2, url.getParameters().size());
        params = new HashMap<String, String>();
        params.put("p1", "v1");
        params.put("p2", "v2");
        Assertions.assertEquals(params, url.getParameters());
    }

    @Test
    public void test_valueOf_WithProtocolHost() throws Exception {
        URL url = URL.valueOf("dubbo://10.20.130.230");
        Assertions.assertEquals("dubbo", url.getProtocol());
        Assertions.assertNull(url.getUsername());
        Assertions.assertNull(url.getPassword());
        Assertions.assertEquals("10.20.130.230", url.getHost());
        Assertions.assertEquals(0, url.getPort());
        Assertions.assertEquals(null, url.getPath());
        Assertions.assertEquals(0, url.getParameters().size());
        url = URL.valueOf("dubbo://10.20.130.230:20880/context/path");
        Assertions.assertEquals("dubbo", url.getProtocol());
        Assertions.assertNull(url.getUsername());
        Assertions.assertNull(url.getPassword());
        Assertions.assertEquals("10.20.130.230", url.getHost());
        Assertions.assertEquals(20880, url.getPort());
        Assertions.assertEquals("context/path", url.getPath());
        Assertions.assertEquals(0, url.getParameters().size());
        url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880");
        Assertions.assertEquals("dubbo", url.getProtocol());
        Assertions.assertEquals("admin", url.getUsername());
        Assertions.assertEquals("hello1234", url.getPassword());
        Assertions.assertEquals("10.20.130.230", url.getHost());
        Assertions.assertEquals(20880, url.getPort());
        Assertions.assertEquals(null, url.getPath());
        Assertions.assertEquals(0, url.getParameters().size());
        url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880?version=1.0.0");
        Assertions.assertEquals("dubbo", url.getProtocol());
        Assertions.assertEquals("admin", url.getUsername());
        Assertions.assertEquals("hello1234", url.getPassword());
        Assertions.assertEquals("10.20.130.230", url.getHost());
        Assertions.assertEquals(20880, url.getPort());
        Assertions.assertEquals(null, url.getPath());
        Assertions.assertEquals(1, url.getParameters().size());
        Assertions.assertEquals("1.0.0", url.getParameter("version"));
        url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan");
        Assertions.assertEquals("dubbo", url.getProtocol());
        Assertions.assertEquals("admin", url.getUsername());
        Assertions.assertEquals("hello1234", url.getPassword());
        Assertions.assertEquals("10.20.130.230", url.getHost());
        Assertions.assertEquals(20880, url.getPort());
        Assertions.assertEquals("context/path", url.getPath());
        Assertions.assertEquals(2, url.getParameters().size());
        Assertions.assertEquals("1.0.0", url.getParameter("version"));
        Assertions.assertEquals("morgan", url.getParameter("application"));
        url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan&noValue");
        Assertions.assertEquals("dubbo", url.getProtocol());
        Assertions.assertEquals("admin", url.getUsername());
        Assertions.assertEquals("hello1234", url.getPassword());
        Assertions.assertEquals("10.20.130.230", url.getHost());
        Assertions.assertEquals(20880, url.getPort());
        Assertions.assertEquals("context/path", url.getPath());
        Assertions.assertEquals(3, url.getParameters().size());
        Assertions.assertEquals("1.0.0", url.getParameter("version"));
        Assertions.assertEquals("morgan", url.getParameter("application"));
        Assertions.assertEquals("noValue", url.getParameter("noValue"));
    }

    // TODO Do not want to use spaces? See: DUBBO-502, URL class handles special conventions for special characters.
    @Test
    public void test_valueOf_spaceSafe() throws Exception {
        URL url = URL.valueOf("http://1.2.3.4:8080/path?key=value1 value2");
        Assertions.assertEquals("http://1.2.3.4:8080/path?key=value1 value2", url.toString());
        Assertions.assertEquals("value1 value2", url.getParameter("key"));
    }

    @Test
    public void test_noValueKey() throws Exception {
        URL url = URL.valueOf("http://1.2.3.4:8080/path?k0&k1=v1");
        Assertions.assertTrue(url.hasParameter("k0"));
        // If a Key has no corresponding Value, then the Key also used as the Value.
        Assertions.assertEquals("k0", url.getParameter("k0"));
    }

    @Test
    public void test_valueOf_Exception_noProtocol() throws Exception {
        try {
            URL.valueOf("://1.2.3.4:8080/path");
            Assertions.fail();
        } catch (IllegalStateException expected) {
            Assertions.assertEquals("url missing protocol: \"://1.2.3.4:8080/path\"", expected.getMessage());
        }
    }

    @Test
    public void test_getAddress() throws Exception {
        URL url1 = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan");
        Assertions.assertEquals("10.20.130.230:20880", url1.getAddress());
    }

    @Test
    public void test_getAbsolutePath() throws Exception {
        URL url = new URL("p1", "1.2.2.2", 33);
        Assertions.assertEquals(null, url.getAbsolutePath());
        url = new URL("file", null, 90, "/home/user1/route.js");
        Assertions.assertEquals("/home/user1/route.js", url.getAbsolutePath());
    }

    @Test
    public void test_equals() throws Exception {
        URL url1 = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan");
        Map<String, String> params = new HashMap<String, String>();
        params.put("version", "1.0.0");
        params.put("application", "morgan");
        URL url2 = new URL("dubbo", "admin", "hello1234", "10.20.130.230", 20880, "context/path", params);
        Assertions.assertEquals(url1, url2);
    }

    @Test
    public void test_toString() throws Exception {
        URL url1 = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan");
        MatcherAssert.assertThat(url1.toString(), CoreMatchers.anyOf(CoreMatchers.equalTo("dubbo://10.20.130.230:20880/context/path?version=1.0.0&application=morgan"), CoreMatchers.equalTo("dubbo://10.20.130.230:20880/context/path?application=morgan&version=1.0.0")));
    }

    @Test
    public void test_toFullString() throws Exception {
        URL url1 = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan");
        MatcherAssert.assertThat(url1.toFullString(), CoreMatchers.anyOf(CoreMatchers.equalTo("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan"), CoreMatchers.equalTo("dubbo://admin:hello1234@10.20.130.230:20880/context/path?application=morgan&version=1.0.0")));
    }

    @Test
    public void test_set_methods() throws Exception {
        URL url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan");
        url = url.setHost("host");
        Assertions.assertEquals("dubbo", url.getProtocol());
        Assertions.assertEquals("admin", url.getUsername());
        Assertions.assertEquals("hello1234", url.getPassword());
        Assertions.assertEquals("host", url.getHost());
        Assertions.assertEquals(20880, url.getPort());
        Assertions.assertEquals("context/path", url.getPath());
        Assertions.assertEquals(2, url.getParameters().size());
        Assertions.assertEquals("1.0.0", url.getParameter("version"));
        Assertions.assertEquals("morgan", url.getParameter("application"));
        url = url.setPort(1);
        Assertions.assertEquals("dubbo", url.getProtocol());
        Assertions.assertEquals("admin", url.getUsername());
        Assertions.assertEquals("hello1234", url.getPassword());
        Assertions.assertEquals("host", url.getHost());
        Assertions.assertEquals(1, url.getPort());
        Assertions.assertEquals("context/path", url.getPath());
        Assertions.assertEquals(2, url.getParameters().size());
        Assertions.assertEquals("1.0.0", url.getParameter("version"));
        Assertions.assertEquals("morgan", url.getParameter("application"));
        url = url.setPath("path");
        Assertions.assertEquals("dubbo", url.getProtocol());
        Assertions.assertEquals("admin", url.getUsername());
        Assertions.assertEquals("hello1234", url.getPassword());
        Assertions.assertEquals("host", url.getHost());
        Assertions.assertEquals(1, url.getPort());
        Assertions.assertEquals("path", url.getPath());
        Assertions.assertEquals(2, url.getParameters().size());
        Assertions.assertEquals("1.0.0", url.getParameter("version"));
        Assertions.assertEquals("morgan", url.getParameter("application"));
        url = url.setProtocol("protocol");
        Assertions.assertEquals("protocol", url.getProtocol());
        Assertions.assertEquals("admin", url.getUsername());
        Assertions.assertEquals("hello1234", url.getPassword());
        Assertions.assertEquals("host", url.getHost());
        Assertions.assertEquals(1, url.getPort());
        Assertions.assertEquals("path", url.getPath());
        Assertions.assertEquals(2, url.getParameters().size());
        Assertions.assertEquals("1.0.0", url.getParameter("version"));
        Assertions.assertEquals("morgan", url.getParameter("application"));
        url = url.setUsername("username");
        Assertions.assertEquals("protocol", url.getProtocol());
        Assertions.assertEquals("username", url.getUsername());
        Assertions.assertEquals("hello1234", url.getPassword());
        Assertions.assertEquals("host", url.getHost());
        Assertions.assertEquals(1, url.getPort());
        Assertions.assertEquals("path", url.getPath());
        Assertions.assertEquals(2, url.getParameters().size());
        Assertions.assertEquals("1.0.0", url.getParameter("version"));
        Assertions.assertEquals("morgan", url.getParameter("application"));
        url = url.setPassword("password");
        Assertions.assertEquals("protocol", url.getProtocol());
        Assertions.assertEquals("username", url.getUsername());
        Assertions.assertEquals("password", url.getPassword());
        Assertions.assertEquals("host", url.getHost());
        Assertions.assertEquals(1, url.getPort());
        Assertions.assertEquals("path", url.getPath());
        Assertions.assertEquals(2, url.getParameters().size());
        Assertions.assertEquals("1.0.0", url.getParameter("version"));
        Assertions.assertEquals("morgan", url.getParameter("application"));
    }

    @Test
    public void test_removeParameters() throws Exception {
        URL url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan&k1=v1&k2=v2");
        url = url.removeParameter("version");
        Assertions.assertEquals("dubbo", url.getProtocol());
        Assertions.assertEquals("admin", url.getUsername());
        Assertions.assertEquals("hello1234", url.getPassword());
        Assertions.assertEquals("10.20.130.230", url.getHost());
        Assertions.assertEquals(20880, url.getPort());
        Assertions.assertEquals("context/path", url.getPath());
        Assertions.assertEquals(3, url.getParameters().size());
        Assertions.assertEquals("morgan", url.getParameter("application"));
        Assertions.assertEquals("v1", url.getParameter("k1"));
        Assertions.assertEquals("v2", url.getParameter("k2"));
        Assertions.assertNull(url.getParameter("version"));
        url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan&k1=v1&k2=v2");
        url = url.removeParameters("version", "application", "NotExistedKey");
        Assertions.assertEquals("dubbo", url.getProtocol());
        Assertions.assertEquals("admin", url.getUsername());
        Assertions.assertEquals("hello1234", url.getPassword());
        Assertions.assertEquals("10.20.130.230", url.getHost());
        Assertions.assertEquals(20880, url.getPort());
        Assertions.assertEquals("context/path", url.getPath());
        Assertions.assertEquals(2, url.getParameters().size());
        Assertions.assertEquals("v1", url.getParameter("k1"));
        Assertions.assertEquals("v2", url.getParameter("k2"));
        Assertions.assertNull(url.getParameter("version"));
        Assertions.assertNull(url.getParameter("application"));
        url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan&k1=v1&k2=v2");
        url = url.removeParameters(Arrays.asList("version", "application"));
        Assertions.assertEquals("dubbo", url.getProtocol());
        Assertions.assertEquals("admin", url.getUsername());
        Assertions.assertEquals("hello1234", url.getPassword());
        Assertions.assertEquals("10.20.130.230", url.getHost());
        Assertions.assertEquals(20880, url.getPort());
        Assertions.assertEquals("context/path", url.getPath());
        Assertions.assertEquals(2, url.getParameters().size());
        Assertions.assertEquals("v1", url.getParameter("k1"));
        Assertions.assertEquals("v2", url.getParameter("k2"));
        Assertions.assertNull(url.getParameter("version"));
        Assertions.assertNull(url.getParameter("application"));
    }

    @Test
    public void test_addParameter() throws Exception {
        URL url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?application=morgan");
        url = url.addParameter("k1", "v1");
        Assertions.assertEquals("dubbo", url.getProtocol());
        Assertions.assertEquals("admin", url.getUsername());
        Assertions.assertEquals("hello1234", url.getPassword());
        Assertions.assertEquals("10.20.130.230", url.getHost());
        Assertions.assertEquals(20880, url.getPort());
        Assertions.assertEquals("context/path", url.getPath());
        Assertions.assertEquals(2, url.getParameters().size());
        Assertions.assertEquals("morgan", url.getParameter("application"));
        Assertions.assertEquals("v1", url.getParameter("k1"));
    }

    @Test
    public void test_addParameter_sameKv() throws Exception {
        URL url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?application=morgan&k1=v1");
        URL newUrl = url.addParameter("k1", "v1");
        Assertions.assertSame(newUrl, url);
    }

    @Test
    public void test_addParameters() throws Exception {
        URL url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?application=morgan");
        url = url.addParameters(CollectionUtils.toStringMap("k1", "v1", "k2", "v2"));
        Assertions.assertEquals("dubbo", url.getProtocol());
        Assertions.assertEquals("admin", url.getUsername());
        Assertions.assertEquals("hello1234", url.getPassword());
        Assertions.assertEquals("10.20.130.230", url.getHost());
        Assertions.assertEquals(20880, url.getPort());
        Assertions.assertEquals("context/path", url.getPath());
        Assertions.assertEquals(3, url.getParameters().size());
        Assertions.assertEquals("morgan", url.getParameter("application"));
        Assertions.assertEquals("v1", url.getParameter("k1"));
        Assertions.assertEquals("v2", url.getParameter("k2"));
        url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?application=morgan");
        url = url.addParameters("k1", "v1", "k2", "v2", "application", "xxx");
        Assertions.assertEquals("dubbo", url.getProtocol());
        Assertions.assertEquals("admin", url.getUsername());
        Assertions.assertEquals("hello1234", url.getPassword());
        Assertions.assertEquals("10.20.130.230", url.getHost());
        Assertions.assertEquals(20880, url.getPort());
        Assertions.assertEquals("context/path", url.getPath());
        Assertions.assertEquals(3, url.getParameters().size());
        Assertions.assertEquals("xxx", url.getParameter("application"));
        Assertions.assertEquals("v1", url.getParameter("k1"));
        Assertions.assertEquals("v2", url.getParameter("k2"));
        url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?application=morgan");
        url = url.addParametersIfAbsent(CollectionUtils.toStringMap("k1", "v1", "k2", "v2", "application", "xxx"));
        Assertions.assertEquals("dubbo", url.getProtocol());
        Assertions.assertEquals("admin", url.getUsername());
        Assertions.assertEquals("hello1234", url.getPassword());
        Assertions.assertEquals("10.20.130.230", url.getHost());
        Assertions.assertEquals(20880, url.getPort());
        Assertions.assertEquals("context/path", url.getPath());
        Assertions.assertEquals(3, url.getParameters().size());
        Assertions.assertEquals("morgan", url.getParameter("application"));
        Assertions.assertEquals("v1", url.getParameter("k1"));
        Assertions.assertEquals("v2", url.getParameter("k2"));
        url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?application=morgan");
        url = url.addParameter("k1", "v1");
        Assertions.assertEquals("dubbo", url.getProtocol());
        Assertions.assertEquals("admin", url.getUsername());
        Assertions.assertEquals("hello1234", url.getPassword());
        Assertions.assertEquals("10.20.130.230", url.getHost());
        Assertions.assertEquals(20880, url.getPort());
        Assertions.assertEquals("context/path", url.getPath());
        Assertions.assertEquals(2, url.getParameters().size());
        Assertions.assertEquals("morgan", url.getParameter("application"));
        Assertions.assertEquals("v1", url.getParameter("k1"));
        url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?application=morgan");
        url = url.addParameter("application", "xxx");
        Assertions.assertEquals("dubbo", url.getProtocol());
        Assertions.assertEquals("admin", url.getUsername());
        Assertions.assertEquals("hello1234", url.getPassword());
        Assertions.assertEquals("10.20.130.230", url.getHost());
        Assertions.assertEquals(20880, url.getPort());
        Assertions.assertEquals("context/path", url.getPath());
        Assertions.assertEquals(1, url.getParameters().size());
        Assertions.assertEquals("xxx", url.getParameter("application"));
    }

    @Test
    public void test_addParameters_SameKv() throws Exception {
        {
            URL url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?application=morgan&k1=v1");
            URL newUrl = url.addParameters(CollectionUtils.toStringMap("k1", "v1"));
            Assertions.assertSame(url, newUrl);
        }
        {
            URL url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?application=morgan&k1=v1&k2=v2");
            URL newUrl = url.addParameters(CollectionUtils.toStringMap("k1", "v1", "k2", "v2"));
            Assertions.assertSame(newUrl, url);
        }
    }

    @Test
    public void test_addParameterIfAbsent() throws Exception {
        URL url = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?application=morgan");
        url = url.addParameterIfAbsent("application", "xxx");
        Assertions.assertEquals("dubbo", url.getProtocol());
        Assertions.assertEquals("admin", url.getUsername());
        Assertions.assertEquals("hello1234", url.getPassword());
        Assertions.assertEquals("10.20.130.230", url.getHost());
        Assertions.assertEquals(20880, url.getPort());
        Assertions.assertEquals("context/path", url.getPath());
        Assertions.assertEquals(1, url.getParameters().size());
        Assertions.assertEquals("morgan", url.getParameter("application"));
    }

    @Test
    public void test_windowAbsolutePathBeginWithSlashIsValid() throws Exception {
        final String osProperty = System.getProperties().getProperty("os.name");
        if (!(osProperty.toLowerCase().contains("windows")))
            return;

        System.out.println("Test Windows valid path string.");
        File f0 = new File("C:/Windows");
        File f1 = new File("/C:/Windows");
        File f2 = new File("C:\\Windows");
        File f3 = new File("/C:\\Windows");
        File f4 = new File("\\C:\\Windows");
        Assertions.assertEquals(f0, f1);
        Assertions.assertEquals(f0, f2);
        Assertions.assertEquals(f0, f3);
        Assertions.assertEquals(f0, f4);
    }

    @Test
    public void test_javaNetUrl() throws Exception {
        java.net.URL url = new java.net.URL("http://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan#anchor1");
        Assertions.assertEquals("http", url.getProtocol());
        Assertions.assertEquals("admin:hello1234", url.getUserInfo());
        Assertions.assertEquals("10.20.130.230", url.getHost());
        Assertions.assertEquals(20880, url.getPort());
        Assertions.assertEquals("/context/path", url.getPath());
        Assertions.assertEquals("version=1.0.0&application=morgan", url.getQuery());
        Assertions.assertEquals("anchor1", url.getRef());
        Assertions.assertEquals("admin:hello1234@10.20.130.230:20880", url.getAuthority());
        Assertions.assertEquals("/context/path?version=1.0.0&application=morgan", url.getFile());
    }

    @Test
    public void test_Anyhost() throws Exception {
        URL url = URL.valueOf("dubbo://0.0.0.0:20880");
        Assertions.assertEquals("0.0.0.0", url.getHost());
        Assertions.assertTrue(url.isAnyHost());
    }

    @Test
    public void test_Localhost() throws Exception {
        URL url = URL.valueOf("dubbo://127.0.0.1:20880");
        Assertions.assertEquals("127.0.0.1", url.getHost());
        Assertions.assertTrue(url.isLocalHost());
        url = URL.valueOf("dubbo://127.0.1.1:20880");
        Assertions.assertEquals("127.0.1.1", url.getHost());
        Assertions.assertTrue(url.isLocalHost());
        url = URL.valueOf("dubbo://localhost:20880");
        Assertions.assertEquals("localhost", url.getHost());
        Assertions.assertTrue(url.isLocalHost());
    }

    @Test
    public void test_Path() throws Exception {
        URL url = new URL("dubbo", "localhost", 20880, "////path");
        Assertions.assertEquals("path", url.getPath());
    }

    @Test
    public void testAddParameters() throws Exception {
        URL url = URL.valueOf("dubbo://127.0.0.1:20880");
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("version", null);
        url.addParameters(parameters);
    }

    @Test
    public void testUserNamePasswordContainsAt() {
        // Test username or password contains "@"
        URL url = URL.valueOf("ad@min:hello@1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan");
        Assertions.assertNull(url.getProtocol());
        Assertions.assertEquals("ad@min", url.getUsername());
        Assertions.assertEquals("hello@1234", url.getPassword());
        Assertions.assertEquals("10.20.130.230", url.getHost());
        Assertions.assertEquals(20880, url.getPort());
        Assertions.assertEquals("context/path", url.getPath());
        Assertions.assertEquals(2, url.getParameters().size());
        Assertions.assertEquals("1.0.0", url.getParameter("version"));
        Assertions.assertEquals("morgan", url.getParameter("application"));
    }

    @Test
    public void testIpV6Address() {
        // Test username or password contains "@"
        URL url = URL.valueOf("ad@min111:haha@1234@2001:0db8:85a3:08d3:1319:8a2e:0370:7344:20880/context/path?version=1.0.0&application=morgan");
        Assertions.assertNull(url.getProtocol());
        Assertions.assertEquals("ad@min111", url.getUsername());
        Assertions.assertEquals("haha@1234", url.getPassword());
        Assertions.assertEquals("2001:0db8:85a3:08d3:1319:8a2e:0370:7344", url.getHost());
        Assertions.assertEquals(20880, url.getPort());
        Assertions.assertEquals("context/path", url.getPath());
        Assertions.assertEquals(2, url.getParameters().size());
        Assertions.assertEquals("1.0.0", url.getParameter("version"));
        Assertions.assertEquals("morgan", url.getParameter("application"));
    }

    @Test
    public void testIpV6AddressWithScopeId() {
        URL url = URL.valueOf("2001:0db8:85a3:08d3:1319:8a2e:0370:7344%5/context/path?version=1.0.0&application=morgan");
        Assertions.assertNull(url.getProtocol());
        Assertions.assertEquals("2001:0db8:85a3:08d3:1319:8a2e:0370:7344%5", url.getHost());
        Assertions.assertEquals(0, url.getPort());
        Assertions.assertEquals("context/path", url.getPath());
        Assertions.assertEquals(2, url.getParameters().size());
        Assertions.assertEquals("1.0.0", url.getParameter("version"));
        Assertions.assertEquals("morgan", url.getParameter("application"));
    }

    @Test
    public void testDefaultPort() {
        Assertions.assertEquals("10.20.153.10:2181", URL.appendDefaultPort("10.20.153.10:0", 2181));
        Assertions.assertEquals("10.20.153.10:2181", URL.appendDefaultPort("10.20.153.10", 2181));
    }
}

