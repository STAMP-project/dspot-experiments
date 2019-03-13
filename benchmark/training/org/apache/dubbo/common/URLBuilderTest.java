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


import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


public class URLBuilderTest {
    @Test
    public void testNoArgConstructor() {
        URL url = new URLBuilder().build();
        MatcherAssert.assertThat(url.toString(), CoreMatchers.equalTo(""));
    }

    @Test
    public void shouldAddParameter() {
        URL url1 = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan");
        URL url2 = // override
        // int
        // string
        URLBuilder.from(url1).addParameter("newKey1", "newValue1").addParameter("newKey2", 2).addParameter("version", 1).build();
        MatcherAssert.assertThat(url2.getParameter("newKey1"), CoreMatchers.equalTo("newValue1"));
        MatcherAssert.assertThat(url2.getParameter("newKey2"), CoreMatchers.equalTo("2"));
        MatcherAssert.assertThat(url2.getParameter("version"), CoreMatchers.equalTo("1"));
    }

    @Test
    public void shouldSet() {
        URL url1 = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan");
        URL url2 = URLBuilder.from(url1).setProtocol("rest").setUsername("newUsername").setPassword("newPassword").setHost("newHost").setPath("newContext").setPort(1234).build();
        MatcherAssert.assertThat(url2.getProtocol(), CoreMatchers.equalTo("rest"));
        MatcherAssert.assertThat(url2.getUsername(), CoreMatchers.equalTo("newUsername"));
        MatcherAssert.assertThat(url2.getPassword(), CoreMatchers.equalTo("newPassword"));
        MatcherAssert.assertThat(url2.getHost(), CoreMatchers.equalTo("newHost"));
        MatcherAssert.assertThat(url2.getPort(), CoreMatchers.equalTo(1234));
        MatcherAssert.assertThat(url2.getPath(), CoreMatchers.equalTo("newContext"));
        url2 = URLBuilder.from(url1).setAddress("newHost2:2345").build();
        MatcherAssert.assertThat(url2.getHost(), CoreMatchers.equalTo("newHost2"));
        MatcherAssert.assertThat(url2.getPort(), CoreMatchers.equalTo(2345));
    }

    @Test
    public void shouldClearParameters() {
        URL url1 = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan");
        URL url2 = URLBuilder.from(url1).clearParameters().build();
        MatcherAssert.assertThat(url2.getParameters().size(), CoreMatchers.equalTo(0));
    }

    @Test
    public void shouldRemoveParameters() {
        URL url1 = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan&key2=v2");
        URL url2 = URLBuilder.from(url1).removeParameters(Arrays.asList("key2", "application")).build();
        MatcherAssert.assertThat(url2.getParameters().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(url2.getParameter("version"), CoreMatchers.equalTo("1.0.0"));
    }

    @Test
    public void shouldAddIfAbsent() {
        URL url1 = URL.valueOf("dubbo://admin:hello1234@10.20.130.230:20880/context/path?version=1.0.0&application=morgan&key2=v2");
        URL url2 = // should not override
        URLBuilder.from(url1).addParameterIfAbsent("absentKey", "absentValue").addParameterIfAbsent("version", "2.0.0").build();
        MatcherAssert.assertThat(url2.getParameter("version"), CoreMatchers.equalTo("1.0.0"));
        MatcherAssert.assertThat(url2.getParameter("absentKey"), CoreMatchers.equalTo("absentValue"));
    }
}

