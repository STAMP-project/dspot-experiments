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


import java.util.HashMap;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class ArgumentConfigTest {
    @Test
    public void testIndex() throws Exception {
        ArgumentConfig argument = new ArgumentConfig();
        argument.setIndex(1);
        MatcherAssert.assertThat(argument.getIndex(), Matchers.is(1));
    }

    @Test
    public void testType() throws Exception {
        ArgumentConfig argument = new ArgumentConfig();
        argument.setType("int");
        MatcherAssert.assertThat(argument.getType(), Matchers.equalTo("int"));
    }

    @Test
    public void testCallback() throws Exception {
        ArgumentConfig argument = new ArgumentConfig();
        argument.setCallback(true);
        MatcherAssert.assertThat(argument.isCallback(), Matchers.is(true));
    }

    @Test
    public void testArguments() throws Exception {
        ArgumentConfig argument = new ArgumentConfig();
        argument.setIndex(1);
        argument.setType("int");
        argument.setCallback(true);
        Map<String, String> parameters = new HashMap<String, String>();
        AbstractServiceConfig.appendParameters(parameters, argument);
        MatcherAssert.assertThat(parameters, Matchers.hasEntry("callback", "true"));
        MatcherAssert.assertThat(parameters.size(), Matchers.is(1));
    }
}

