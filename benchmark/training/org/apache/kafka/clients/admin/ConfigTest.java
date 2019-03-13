/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.clients.admin;


import java.util.ArrayList;
import java.util.Collection;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class ConfigTest {
    private static final ConfigEntry E1 = new ConfigEntry("a", "b");

    private static final ConfigEntry E2 = new ConfigEntry("c", "d");

    private Config config;

    @Test
    public void shouldGetEntry() {
        MatcherAssert.assertThat(config.get("a"), CoreMatchers.is(ConfigTest.E1));
        MatcherAssert.assertThat(config.get("c"), CoreMatchers.is(ConfigTest.E2));
    }

    @Test
    public void shouldReturnNullOnGetUnknownEntry() {
        MatcherAssert.assertThat(config.get("unknown"), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldGetAllEntries() {
        MatcherAssert.assertThat(config.entries().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(config.entries(), CoreMatchers.hasItems(ConfigTest.E1, ConfigTest.E2));
    }

    @Test
    public void shouldImplementEqualsProperly() {
        final Collection<ConfigEntry> entries = new ArrayList<>();
        entries.add(ConfigTest.E1);
        MatcherAssert.assertThat(config, CoreMatchers.is(CoreMatchers.equalTo(config)));
        MatcherAssert.assertThat(config, CoreMatchers.is(CoreMatchers.equalTo(new Config(config.entries()))));
        MatcherAssert.assertThat(config, CoreMatchers.is(CoreMatchers.not(CoreMatchers.equalTo(new Config(entries)))));
        MatcherAssert.assertThat(config, CoreMatchers.is(CoreMatchers.not(CoreMatchers.equalTo(((Object) ("this"))))));
    }

    @Test
    public void shouldImplementHashCodeProperly() {
        final Collection<ConfigEntry> entries = new ArrayList<>();
        entries.add(ConfigTest.E1);
        MatcherAssert.assertThat(config.hashCode(), CoreMatchers.is(config.hashCode()));
        MatcherAssert.assertThat(config.hashCode(), CoreMatchers.is(new Config(config.entries()).hashCode()));
        MatcherAssert.assertThat(config.hashCode(), CoreMatchers.is(CoreMatchers.not(new Config(entries).hashCode())));
    }

    @Test
    public void shouldImplementToStringProperly() {
        MatcherAssert.assertThat(config.toString(), CoreMatchers.containsString(ConfigTest.E1.toString()));
        MatcherAssert.assertThat(config.toString(), CoreMatchers.containsString(ConfigTest.E2.toString()));
    }
}

