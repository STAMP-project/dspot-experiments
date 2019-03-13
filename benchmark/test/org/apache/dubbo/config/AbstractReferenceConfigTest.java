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
package org.apache.dubbo.config;


import Constants.CLUSTER_STICKY_KEY;
import Constants.INVOKER_LISTENER_KEY;
import Constants.RECONNECT_KEY;
import Constants.REFERENCE_FILTER_KEY;
import Constants.STUB_EVENT_KEY;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class AbstractReferenceConfigTest {
    @Test
    public void testCheck() throws Exception {
        AbstractReferenceConfigTest.ReferenceConfig referenceConfig = new AbstractReferenceConfigTest.ReferenceConfig();
        setCheck(true);
        MatcherAssert.assertThat(isCheck(), Matchers.is(true));
    }

    @Test
    public void testInit() throws Exception {
        AbstractReferenceConfigTest.ReferenceConfig referenceConfig = new AbstractReferenceConfigTest.ReferenceConfig();
        setInit(true);
        MatcherAssert.assertThat(isInit(), Matchers.is(true));
    }

    @Test
    public void testGeneric() throws Exception {
        AbstractReferenceConfigTest.ReferenceConfig referenceConfig = new AbstractReferenceConfigTest.ReferenceConfig();
        setGeneric(true);
        MatcherAssert.assertThat(isGeneric(), Matchers.is(true));
        Map<String, String> parameters = new HashMap<String, String>();
        AbstractInterfaceConfig.appendParameters(parameters, referenceConfig);
        // FIXME: not sure why AbstractReferenceConfig has both isGeneric and getGeneric
        MatcherAssert.assertThat(parameters, Matchers.hasKey("generic"));
    }

    @Test
    public void testInjvm() throws Exception {
        AbstractReferenceConfigTest.ReferenceConfig referenceConfig = new AbstractReferenceConfigTest.ReferenceConfig();
        setInit(true);
        MatcherAssert.assertThat(isInit(), Matchers.is(true));
    }

    @Test
    public void testFilter() throws Exception {
        AbstractReferenceConfigTest.ReferenceConfig referenceConfig = new AbstractReferenceConfigTest.ReferenceConfig();
        setFilter("mockfilter");
        MatcherAssert.assertThat(getFilter(), Matchers.equalTo("mockfilter"));
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(REFERENCE_FILTER_KEY, "prefilter");
        AbstractInterfaceConfig.appendParameters(parameters, referenceConfig);
        MatcherAssert.assertThat(parameters, Matchers.hasValue("prefilter,mockfilter"));
    }

    @Test
    public void testListener() throws Exception {
        AbstractReferenceConfigTest.ReferenceConfig referenceConfig = new AbstractReferenceConfigTest.ReferenceConfig();
        setListener("mockinvokerlistener");
        MatcherAssert.assertThat(getListener(), Matchers.equalTo("mockinvokerlistener"));
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(INVOKER_LISTENER_KEY, "prelistener");
        AbstractInterfaceConfig.appendParameters(parameters, referenceConfig);
        MatcherAssert.assertThat(parameters, Matchers.hasValue("prelistener,mockinvokerlistener"));
    }

    @Test
    public void testLazy() throws Exception {
        AbstractReferenceConfigTest.ReferenceConfig referenceConfig = new AbstractReferenceConfigTest.ReferenceConfig();
        setLazy(true);
        MatcherAssert.assertThat(getLazy(), Matchers.is(true));
    }

    @Test
    public void testOnconnect() throws Exception {
        AbstractReferenceConfigTest.ReferenceConfig referenceConfig = new AbstractReferenceConfigTest.ReferenceConfig();
        setOnconnect("onConnect");
        MatcherAssert.assertThat(getOnconnect(), Matchers.equalTo("onConnect"));
        MatcherAssert.assertThat(getStubevent(), Matchers.is(true));
    }

    @Test
    public void testOndisconnect() throws Exception {
        AbstractReferenceConfigTest.ReferenceConfig referenceConfig = new AbstractReferenceConfigTest.ReferenceConfig();
        setOndisconnect("onDisconnect");
        MatcherAssert.assertThat(getOndisconnect(), Matchers.equalTo("onDisconnect"));
        MatcherAssert.assertThat(getStubevent(), Matchers.is(true));
    }

    @Test
    public void testStubevent() throws Exception {
        AbstractReferenceConfigTest.ReferenceConfig referenceConfig = new AbstractReferenceConfigTest.ReferenceConfig();
        setOnconnect("onConnect");
        Map<String, String> parameters = new HashMap<String, String>();
        AbstractInterfaceConfig.appendParameters(parameters, referenceConfig);
        MatcherAssert.assertThat(parameters, Matchers.hasKey(STUB_EVENT_KEY));
    }

    @Test
    public void testReconnect() throws Exception {
        AbstractReferenceConfigTest.ReferenceConfig referenceConfig = new AbstractReferenceConfigTest.ReferenceConfig();
        setReconnect("reconnect");
        Map<String, String> parameters = new HashMap<String, String>();
        AbstractInterfaceConfig.appendParameters(parameters, referenceConfig);
        MatcherAssert.assertThat(getReconnect(), Matchers.equalTo("reconnect"));
        MatcherAssert.assertThat(parameters, Matchers.hasKey(RECONNECT_KEY));
    }

    @Test
    public void testSticky() throws Exception {
        AbstractReferenceConfigTest.ReferenceConfig referenceConfig = new AbstractReferenceConfigTest.ReferenceConfig();
        setSticky(true);
        Map<String, String> parameters = new HashMap<String, String>();
        AbstractInterfaceConfig.appendParameters(parameters, referenceConfig);
        MatcherAssert.assertThat(getSticky(), Matchers.is(true));
        MatcherAssert.assertThat(parameters, Matchers.hasKey(CLUSTER_STICKY_KEY));
    }

    @Test
    public void testVersion() throws Exception {
        AbstractReferenceConfigTest.ReferenceConfig referenceConfig = new AbstractReferenceConfigTest.ReferenceConfig();
        setVersion("version");
        MatcherAssert.assertThat(getVersion(), Matchers.equalTo("version"));
    }

    @Test
    public void testGroup() throws Exception {
        AbstractReferenceConfigTest.ReferenceConfig referenceConfig = new AbstractReferenceConfigTest.ReferenceConfig();
        setGroup("group");
        MatcherAssert.assertThat(getGroup(), Matchers.equalTo("group"));
    }

    private static class ReferenceConfig extends AbstractReferenceConfig {}
}

