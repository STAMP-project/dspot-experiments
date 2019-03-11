/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.configuration;


import java.util.ArrayList;
import java.util.List;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link ConfigOption}.
 */
public class ConfigOptionTest extends TestLogger {
    @Test
    public void testDeprecationFlagForDeprecatedKeys() {
        final ConfigOption<Integer> optionWithDeprecatedKeys = ConfigOptions.key("key").defaultValue(0).withDeprecatedKeys("deprecated1", "deprecated2");
        Assert.assertTrue(optionWithDeprecatedKeys.hasFallbackKeys());
        for (final FallbackKey fallbackKey : optionWithDeprecatedKeys.fallbackKeys()) {
            Assert.assertTrue("deprecated key not flagged as deprecated", fallbackKey.isDeprecated());
        }
    }

    @Test
    public void testDeprecationFlagForFallbackKeys() {
        final ConfigOption<Integer> optionWithFallbackKeys = ConfigOptions.key("key").defaultValue(0).withFallbackKeys("fallback1", "fallback2");
        Assert.assertTrue(optionWithFallbackKeys.hasFallbackKeys());
        for (final FallbackKey fallbackKey : optionWithFallbackKeys.fallbackKeys()) {
            Assert.assertFalse("falback key flagged as deprecated", fallbackKey.isDeprecated());
        }
    }

    @Test
    public void testDeprecationFlagForMixedAlternativeKeys() {
        final ConfigOption<Integer> optionWithMixedKeys = ConfigOptions.key("key").defaultValue(0).withDeprecatedKeys("deprecated1", "deprecated2").withFallbackKeys("fallback1", "fallback2");
        final List<String> fallbackKeys = new ArrayList<>(2);
        final List<String> deprecatedKeys = new ArrayList<>(2);
        for (final FallbackKey alternativeKey : optionWithMixedKeys.fallbackKeys()) {
            if (alternativeKey.isDeprecated()) {
                deprecatedKeys.add(alternativeKey.getKey());
            } else {
                fallbackKeys.add(alternativeKey.getKey());
            }
        }
        Assert.assertEquals(2, fallbackKeys.size());
        Assert.assertEquals(2, deprecatedKeys.size());
        Assert.assertThat(fallbackKeys, Matchers.containsInAnyOrder("fallback1", "fallback2"));
        Assert.assertThat(deprecatedKeys, Matchers.containsInAnyOrder("deprecated1", "deprecated2"));
    }
}

