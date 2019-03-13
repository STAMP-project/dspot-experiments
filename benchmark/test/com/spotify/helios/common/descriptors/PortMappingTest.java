/**
 * -
 * -\-\-
 * Helios Client
 * --
 * Copyright (C) 2016 - 2017 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.helios.common.descriptors;


import com.fasterxml.jackson.databind.JsonMappingException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class PortMappingTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testJsonDeserializationFromFull() throws Exception {
        final PortMapping pm = portMappingFromResource("portmapping-full.json");
        Assert.assertThat(pm, Matchers.equalTo(PortMapping.builder().ip("1.2.3.4").externalPort(123).internalPort(456).protocol(PortMapping.UDP).build()));
    }

    @Test
    public void testJsonDeserializationFromPartial() throws Exception {
        final PortMapping pm = portMappingFromResource("portmapping-partial.json");
        Assert.assertThat(pm, Matchers.equalTo(PortMapping.builder().externalPort(123).internalPort(456).protocol(PortMapping.TCP).build()));
    }

    @Test
    public void testJsonDeserializationFromInternalPortOnly() throws Exception {
        final PortMapping pm = portMappingFromResource("portmapping-internal-port-only.json");
        Assert.assertThat(pm, Matchers.equalTo(PortMapping.builder().internalPort(456).protocol(PortMapping.TCP).build()));
    }

    @Test
    public void testJsonDeserializationInvalidIp() throws Exception {
        expectedException.expect(JsonMappingException.class);
        portMappingFromResource("portmapping-invalid-ip.json");
    }

    @Test
    public void testJsonDeserializationUnknownFields() throws Exception {
        final PortMapping pm = portMappingFromResource("portmapping-unknown-fields.json");
        Assert.assertThat(pm, Matchers.equalTo(PortMapping.builder().internalPort(456).externalPort(123).protocol(PortMapping.TCP).build()));
    }

    @Test
    public void testJsonSerialization() throws Exception {
        final PortMapping pm = PortMapping.builder().ip("1.2.3.4").internalPort(456).externalPort(123).protocol(PortMapping.UDP).build();
        final String json = PortMappingTest.toJson(pm);
        Assert.assertThat(json, Matchers.equalTo(stringFromResource("portmapping-full.json")));
    }

    @Test
    public void testJsonSerializationWithDefaults() throws Exception {
        final PortMapping pm = PortMapping.builder().internalPort(456).externalPort(123).build();
        final String json = PortMappingTest.toJson(pm);
        Assert.assertThat(json, Matchers.equalTo(stringFromResource("portmapping-defaults.json")));
    }

    @Test
    public void testJsonSerializationFromInternalPortOnly() throws Exception {
        final PortMapping pm = PortMapping.builder().internalPort(456).build();
        final String json = PortMappingTest.toJson(pm);
        Assert.assertThat(json, Matchers.equalTo(stringFromResource("portmapping-serialized-internal-port-only.json")));
    }
}

