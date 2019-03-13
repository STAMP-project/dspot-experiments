/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.util;


import java.io.IOException;
import junitparams.JUnitParamsRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.runner.RunWith;


@RunWith(JUnitParamsRunner.class)
public class HostNameTest {
    private static final String EXPECTED_HOSTNAME = "expected-hostname";

    private static final String UNKNOWN = "unknown";

    @Rule
    public final EnvironmentVariables env = new EnvironmentVariables();

    @Rule
    public final RestoreSystemProperties sysProps = new RestoreSystemProperties();

    @Test
    public void execHostNameShouldNeverReturnNull() throws IOException {
        String result = new HostName().execHostName();
        assertThat(result).isNotNull();
    }
}

