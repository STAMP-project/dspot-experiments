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
package org.apache.dubbo.config;


import java.util.HashMap;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class AbstractMethodConfigTest {
    @Test
    public void testTimeout() throws Exception {
        AbstractMethodConfigTest.MethodConfig methodConfig = new AbstractMethodConfigTest.MethodConfig();
        setTimeout(10);
        MatcherAssert.assertThat(getTimeout(), Matchers.equalTo(10));
    }

    @Test
    public void testForks() throws Exception {
        AbstractMethodConfigTest.MethodConfig methodConfig = new AbstractMethodConfigTest.MethodConfig();
        setForks(10);
        MatcherAssert.assertThat(getForks(), Matchers.equalTo(10));
    }

    @Test
    public void testRetries() throws Exception {
        AbstractMethodConfigTest.MethodConfig methodConfig = new AbstractMethodConfigTest.MethodConfig();
        setRetries(3);
        MatcherAssert.assertThat(getRetries(), Matchers.equalTo(3));
    }

    @Test
    public void testLoadbalance() throws Exception {
        AbstractMethodConfigTest.MethodConfig methodConfig = new AbstractMethodConfigTest.MethodConfig();
        setLoadbalance("mockloadbalance");
        MatcherAssert.assertThat(getLoadbalance(), Matchers.equalTo("mockloadbalance"));
    }

    @Test
    public void testAsync() throws Exception {
        AbstractMethodConfigTest.MethodConfig methodConfig = new AbstractMethodConfigTest.MethodConfig();
        setAsync(true);
        MatcherAssert.assertThat(isAsync(), Matchers.is(true));
    }

    @Test
    public void testActives() throws Exception {
        AbstractMethodConfigTest.MethodConfig methodConfig = new AbstractMethodConfigTest.MethodConfig();
        setActives(10);
        MatcherAssert.assertThat(getActives(), Matchers.equalTo(10));
    }

    @Test
    public void testSent() throws Exception {
        AbstractMethodConfigTest.MethodConfig methodConfig = new AbstractMethodConfigTest.MethodConfig();
        setSent(true);
        MatcherAssert.assertThat(getSent(), Matchers.is(true));
    }

    @Test
    public void testMock() throws Exception {
        AbstractMethodConfigTest.MethodConfig methodConfig = new AbstractMethodConfigTest.MethodConfig();
        methodConfig.setMock(((Boolean) (null)));
        MatcherAssert.assertThat(getMock(), Matchers.isEmptyOrNullString());
        methodConfig.setMock(true);
        MatcherAssert.assertThat(getMock(), Matchers.equalTo("true"));
        setMock("return null");
        MatcherAssert.assertThat(getMock(), Matchers.equalTo("return null"));
        setMock("mock");
        MatcherAssert.assertThat(getMock(), Matchers.equalTo("mock"));
    }

    @Test
    public void testMerger() throws Exception {
        AbstractMethodConfigTest.MethodConfig methodConfig = new AbstractMethodConfigTest.MethodConfig();
        setMerger("merger");
        MatcherAssert.assertThat(getMerger(), Matchers.equalTo("merger"));
    }

    @Test
    public void testCache() throws Exception {
        AbstractMethodConfigTest.MethodConfig methodConfig = new AbstractMethodConfigTest.MethodConfig();
        setCache("cache");
        MatcherAssert.assertThat(getCache(), Matchers.equalTo("cache"));
    }

    @Test
    public void testValidation() throws Exception {
        AbstractMethodConfigTest.MethodConfig methodConfig = new AbstractMethodConfigTest.MethodConfig();
        setValidation("validation");
        MatcherAssert.assertThat(getValidation(), Matchers.equalTo("validation"));
    }

    @Test
    public void testParameters() throws Exception {
        AbstractMethodConfigTest.MethodConfig methodConfig = new AbstractMethodConfigTest.MethodConfig();
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("key", "value");
        setParameters(parameters);
        MatcherAssert.assertThat(getParameters(), Matchers.sameInstance(parameters));
    }

    private static class MethodConfig extends AbstractMethodConfig {}
}

