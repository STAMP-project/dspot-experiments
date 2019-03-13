/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.config;


import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ConfigDefaultsTest extends HazelcastTestSupport {
    private static final Config JAVA_CONFIG = ConfigDefaultsTest.javaConfig();

    private static final Config DEFAULT_XML_CONFIG = ConfigDefaultsTest.defaultXmlConfig();

    private static final Config EMPTY_XML_CONFIG = ConfigDefaultsTest.emptyXmlConfig();

    @Parameterized.Parameter
    public String name;

    @Parameterized.Parameter(1)
    public Config c1;

    @Parameterized.Parameter(2)
    public Config c2;

    @Test
    public void testCompatibility() {
        ConfigCompatibilityChecker.isCompatible(c1, c2);
    }
}

