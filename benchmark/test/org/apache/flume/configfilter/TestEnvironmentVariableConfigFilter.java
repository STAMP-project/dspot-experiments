/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.configfilter;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;


public class TestEnvironmentVariableConfigFilter {
    public static final String MY_PASSWORD_KEY = "my_password_key";

    public static final String MY_PASSWORD_KEY_2 = "my_password_key2";

    public static final String FILTERED = "filtered";

    public static final String FILTERED_2 = "filtered2";

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Test
    public void filter() {
        environmentVariables.set(TestEnvironmentVariableConfigFilter.MY_PASSWORD_KEY, TestEnvironmentVariableConfigFilter.FILTERED);
        environmentVariables.set(TestEnvironmentVariableConfigFilter.MY_PASSWORD_KEY_2, TestEnvironmentVariableConfigFilter.FILTERED_2);
        ConfigFilter configFilter = new EnvironmentVariableConfigFilter();
        Assert.assertEquals(TestEnvironmentVariableConfigFilter.FILTERED, configFilter.filter(TestEnvironmentVariableConfigFilter.MY_PASSWORD_KEY));
        Assert.assertEquals(TestEnvironmentVariableConfigFilter.FILTERED_2, configFilter.filter(TestEnvironmentVariableConfigFilter.MY_PASSWORD_KEY_2));
    }

    @Test
    public void filterUnknownKey() {
        ConfigFilter configFilter = new EnvironmentVariableConfigFilter();
        Assert.assertNull(configFilter.filter("unknown"));
    }
}

