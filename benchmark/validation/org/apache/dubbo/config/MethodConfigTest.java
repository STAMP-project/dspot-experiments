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


import Constants.ON_INVOKE_INSTANCE_KEY;
import Constants.ON_INVOKE_METHOD_KEY;
import Constants.ON_RETURN_INSTANCE_KEY;
import Constants.ON_RETURN_METHOD_KEY;
import Constants.ON_THROW_INSTANCE_KEY;
import Constants.ON_THROW_METHOD_KEY;
import ConsumerMethodModel.AsyncMethodInfo;
import com.alibaba.dubbo.config.ArgumentConfig;
import com.alibaba.dubbo.config.MethodConfig;
import com.alibaba.dubbo.config.org.apache.dubbo.config.ArgumentConfig;
import com.alibaba.dubbo.config.org.apache.dubbo.config.MethodConfig;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.dubbo.rpc.model.ConsumerMethodModel;
import org.apache.dubbo.service.Person;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class MethodConfigTest {
    @Test
    public void testName() throws Exception {
        MethodConfig method = new MethodConfig();
        method.setName("hello");
        MatcherAssert.assertThat(method.getName(), Matchers.equalTo("hello"));
        Map<String, String> parameters = new HashMap<String, String>();
        MethodConfig.appendParameters(parameters, method);
        MatcherAssert.assertThat(parameters, Matchers.not(Matchers.hasKey("name")));
    }

    @Test
    public void testStat() throws Exception {
        MethodConfig method = new MethodConfig();
        method.setStat(10);
        MatcherAssert.assertThat(method.getStat(), Matchers.equalTo(10));
    }

    @Test
    public void testRetry() throws Exception {
        MethodConfig method = new MethodConfig();
        method.setRetry(true);
        MatcherAssert.assertThat(method.isRetry(), Matchers.is(true));
    }

    @Test
    public void testReliable() throws Exception {
        MethodConfig method = new MethodConfig();
        method.setReliable(true);
        MatcherAssert.assertThat(method.isReliable(), Matchers.is(true));
    }

    @Test
    public void testExecutes() throws Exception {
        MethodConfig method = new MethodConfig();
        method.setExecutes(10);
        MatcherAssert.assertThat(method.getExecutes(), Matchers.equalTo(10));
    }

    @Test
    public void testDeprecated() throws Exception {
        MethodConfig method = new MethodConfig();
        method.setDeprecated(true);
        MatcherAssert.assertThat(method.getDeprecated(), Matchers.is(true));
    }

    @Test
    public void testArguments() throws Exception {
        MethodConfig method = new MethodConfig();
        ArgumentConfig argument = new ArgumentConfig();
        method.setArguments(Collections.singletonList(argument));
        MatcherAssert.assertThat(method.getArguments(), Matchers.contains(argument));
        MatcherAssert.assertThat(method.getArguments(), Matchers.<org.apache.dubbo.config.ArgumentConfig>hasSize(1));
    }

    @Test
    public void testSticky() throws Exception {
        MethodConfig method = new MethodConfig();
        method.setSticky(true);
        MatcherAssert.assertThat(method.getSticky(), Matchers.is(true));
    }

    @Test
    public void testConverMethodConfig2AsyncInfo() throws Exception {
        org.apache.dubbo.config.MethodConfig methodConfig = new org.apache.dubbo.config.MethodConfig();
        methodConfig.setOninvokeMethod("setName");
        methodConfig.setOninvoke(new Person());
        ConsumerMethodModel.AsyncMethodInfo methodInfo = org.apache.dubbo.config.MethodConfig.convertMethodConfig2AyncInfo(methodConfig);
        Assertions.assertTrue(methodInfo.getOninvokeMethod().equals(Person.class.getMethod("setName", String.class)));
    }

    @Test
    public void testOnreturn() throws Exception {
        MethodConfig method = new MethodConfig();
        method.setOnreturn("on-return-object");
        MatcherAssert.assertThat(method.getOnreturn(), Matchers.equalTo(((Object) ("on-return-object"))));
        Map<String, Object> attribute = new HashMap<String, Object>();
        MethodConfig.appendAttributes(attribute, method);
        MatcherAssert.assertThat(attribute, Matchers.hasEntry(((Object) (ON_RETURN_INSTANCE_KEY)), ((Object) ("on-return-object"))));
        Map<String, String> parameters = new HashMap<String, String>();
        MethodConfig.appendParameters(parameters, method);
        MatcherAssert.assertThat(parameters.size(), Matchers.is(0));
    }

    @Test
    public void testOnreturnMethod() throws Exception {
        MethodConfig method = new MethodConfig();
        method.setOnreturnMethod("on-return-method");
        MatcherAssert.assertThat(method.getOnreturnMethod(), Matchers.equalTo("on-return-method"));
        Map<String, Object> attribute = new HashMap<String, Object>();
        MethodConfig.appendAttributes(attribute, method);
        MatcherAssert.assertThat(attribute, Matchers.hasEntry(((Object) (ON_RETURN_METHOD_KEY)), ((Object) ("on-return-method"))));
        Map<String, String> parameters = new HashMap<String, String>();
        MethodConfig.appendParameters(parameters, method);
        MatcherAssert.assertThat(parameters.size(), Matchers.is(0));
    }

    @Test
    public void testOnthrow() throws Exception {
        MethodConfig method = new MethodConfig();
        method.setOnthrow("on-throw-object");
        MatcherAssert.assertThat(method.getOnthrow(), Matchers.equalTo(((Object) ("on-throw-object"))));
        Map<String, Object> attribute = new HashMap<String, Object>();
        MethodConfig.appendAttributes(attribute, method);
        MatcherAssert.assertThat(attribute, Matchers.hasEntry(((Object) (ON_THROW_INSTANCE_KEY)), ((Object) ("on-throw-object"))));
        Map<String, String> parameters = new HashMap<String, String>();
        MethodConfig.appendParameters(parameters, method);
        MatcherAssert.assertThat(parameters.size(), Matchers.is(0));
    }

    @Test
    public void testOnthrowMethod() throws Exception {
        MethodConfig method = new MethodConfig();
        method.setOnthrowMethod("on-throw-method");
        MatcherAssert.assertThat(method.getOnthrowMethod(), Matchers.equalTo("on-throw-method"));
        Map<String, Object> attribute = new HashMap<String, Object>();
        MethodConfig.appendAttributes(attribute, method);
        MatcherAssert.assertThat(attribute, Matchers.hasEntry(((Object) (ON_THROW_METHOD_KEY)), ((Object) ("on-throw-method"))));
        Map<String, String> parameters = new HashMap<String, String>();
        MethodConfig.appendParameters(parameters, method);
        MatcherAssert.assertThat(parameters.size(), Matchers.is(0));
    }

    @Test
    public void testOninvoke() throws Exception {
        MethodConfig method = new MethodConfig();
        method.setOninvoke("on-invoke-object");
        MatcherAssert.assertThat(method.getOninvoke(), Matchers.equalTo(((Object) ("on-invoke-object"))));
        Map<String, Object> attribute = new HashMap<String, Object>();
        MethodConfig.appendAttributes(attribute, method);
        MatcherAssert.assertThat(attribute, Matchers.hasEntry(((Object) (ON_INVOKE_INSTANCE_KEY)), ((Object) ("on-invoke-object"))));
        Map<String, String> parameters = new HashMap<String, String>();
        MethodConfig.appendParameters(parameters, method);
        MatcherAssert.assertThat(parameters.size(), Matchers.is(0));
    }

    @Test
    public void testOninvokeMethod() throws Exception {
        MethodConfig method = new MethodConfig();
        method.setOninvokeMethod("on-invoke-method");
        MatcherAssert.assertThat(method.getOninvokeMethod(), Matchers.equalTo("on-invoke-method"));
        Map<String, Object> attribute = new HashMap<String, Object>();
        MethodConfig.appendAttributes(attribute, method);
        MatcherAssert.assertThat(attribute, Matchers.hasEntry(((Object) (ON_INVOKE_METHOD_KEY)), ((Object) ("on-invoke-method"))));
        Map<String, String> parameters = new HashMap<String, String>();
        MethodConfig.appendParameters(parameters, method);
        MatcherAssert.assertThat(parameters.size(), Matchers.is(0));
    }

    @Test
    public void testReturn() throws Exception {
        MethodConfig method = new MethodConfig();
        method.setReturn(true);
        MatcherAssert.assertThat(method.isReturn(), Matchers.is(true));
    }
}

