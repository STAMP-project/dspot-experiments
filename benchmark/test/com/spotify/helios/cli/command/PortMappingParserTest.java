/**
 * -
 * -\-\-
 * Helios Tools
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
package com.spotify.helios.cli.command;


import com.google.common.collect.ImmutableList;
import com.spotify.helios.cli.command.PortMappingParser.PortMappingWithName;
import com.spotify.helios.common.descriptors.PortMapping;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class PortMappingParserTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final List<PortMappingParserTest.TestData> GOOD_SPECS = ImmutableList.of(PortMappingParserTest.TestData.of("http=8080:80", "http", PortMapping.of(8080, 80, TCP)), PortMappingParserTest.TestData.of("foo=4711:80/tcp", "foo", PortMapping.of(4711, 80, TCP)), PortMappingParserTest.TestData.of("dns=53:53/udp", "dns", PortMapping.of(53, 53, UDP)), PortMappingParserTest.TestData.of("bar=4711", "bar", PortMapping.of(4711, null, TCP)), PortMappingParserTest.TestData.of("smtp=0.0.0.0:53:53/udp", "smtp", PortMapping.builder().ip("0.0.0.0").internalPort(53).externalPort(53).protocol(UDP).build()), PortMappingParserTest.TestData.of("mail=127.0.0.1:23:23/tcp", "mail", PortMapping.builder().ip("127.0.0.1").internalPort(23).externalPort(23).protocol(TCP).build()), PortMappingParserTest.TestData.of("qux=10.99.0.1:1001:1002", "qux", PortMapping.builder().ip("10.99.0.1").internalPort(1001).externalPort(1002).protocol(TCP).build()));

    private static final List<PortMappingParserTest.TestData> BAD_SPECS = ImmutableList.of(PortMappingParserTest.TestData.partial("smtp=localhost:53:53/udp"), PortMappingParserTest.TestData.partial("http8080:80"), PortMappingParserTest.TestData.partial("foo=80:4711:80/tcp"), PortMappingParserTest.TestData.partial("=53:53/udp"), PortMappingParserTest.TestData.partial("foo"));

    @Test
    public void testParsePortMappingGoodSpecs() throws Exception {
        for (final PortMappingParserTest.TestData d : PortMappingParserTest.GOOD_SPECS) {
            final PortMappingWithName mappingWithName = PortMappingParser.parsePortMapping(d.getSpec());
            final PortMapping portMapping = mappingWithName.portMapping();
            MatcherAssert.assertThat(mappingWithName.name(), Matchers.equalTo(d.getName()));
            MatcherAssert.assertThat(portMapping.getInternalPort(), Matchers.equalTo(d.getPortMapping().getInternalPort()));
            MatcherAssert.assertThat(portMapping.getExternalPort(), Matchers.equalTo(d.getPortMapping().getExternalPort()));
            MatcherAssert.assertThat(portMapping.getProtocol(), Matchers.equalTo(d.getPortMapping().getProtocol()));
        }
    }

    @Test
    public void testParsePortMappingBadSpecs() throws Exception {
        for (final PortMappingParserTest.TestData d : PortMappingParserTest.BAD_SPECS) {
            expectedException.expect(IllegalArgumentException.class);
            expectedException.expectMessage(("Bad port mapping: " + (d.getSpec())));
            PortMappingParser.parsePortMapping(d.getSpec());
        }
    }

    @Test
    public void testParsePortMappingBadIp() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("999.999.999.999 is not a valid IP address.");
        final PortMappingParserTest.TestData d = PortMappingParserTest.TestData.partial("mail=999.999.999.999:23:23/tcp");
        PortMappingParser.parsePortMapping(d.getSpec());
    }

    @Test
    public void testParsePortMappings() throws Exception {
        final Map<String, PortMapping> mappings = PortMappingParser.parsePortMappings(testDataToSpecs(PortMappingParserTest.GOOD_SPECS));
        final Map<String, PortMapping> expectedMappings = new HashMap<>();
        for (final PortMappingParserTest.TestData d : PortMappingParserTest.GOOD_SPECS) {
            expectedMappings.put(d.getName(), d.getPortMapping());
        }
        MatcherAssert.assertThat(mappings, Matchers.equalTo(expectedMappings));
    }

    @Test
    public void testParsePortMappingsDuplicateName() throws Exception {
        final ImmutableList<PortMappingParserTest.TestData> specs = ImmutableList.<PortMappingParserTest.TestData>builder().addAll(PortMappingParserTest.GOOD_SPECS).add(PortMappingParserTest.GOOD_SPECS.get(0)).build();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Duplicate port mapping name: " + (PortMappingParserTest.GOOD_SPECS.get(0).getName())));
        PortMappingParser.parsePortMappings(testDataToSpecs(specs));
    }

    private static class TestData {
        private final String spec;

        private final String name;

        private final PortMapping portMapping;

        private TestData(final String spec, final String name, final PortMapping portMapping) {
            this.spec = spec;
            this.name = name;
            this.portMapping = portMapping;
        }

        static PortMappingParserTest.TestData partial(final String spec) {
            return new PortMappingParserTest.TestData(spec, null, null);
        }

        public static PortMappingParserTest.TestData of(final String spec, final String name, final PortMapping portMapping) {
            return new PortMappingParserTest.TestData(spec, name, portMapping);
        }

        String getSpec() {
            return spec;
        }

        String getName() {
            return name;
        }

        PortMapping getPortMapping() {
            return portMapping;
        }
    }
}

