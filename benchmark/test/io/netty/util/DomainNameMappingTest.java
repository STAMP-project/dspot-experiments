/**
 * Copyright 2015 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.util;


import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("deprecation")
public class DomainNameMappingTest {
    // Deprecated API
    @Test(expected = NullPointerException.class)
    public void testNullDefaultValueInDeprecatedApi() {
        new DomainNameMapping<String>(null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullDomainNamePatternsAreForbiddenInDeprecatedApi() {
        new DomainNameMapping<String>("NotFound").add(null, "Some value");
    }

    @Test(expected = NullPointerException.class)
    public void testNullValuesAreForbiddenInDeprecatedApi() {
        new DomainNameMapping<String>("NotFound").add("Some key", null);
    }

    @Test
    public void testDefaultValueInDeprecatedApi() {
        DomainNameMapping<String> mapping = new DomainNameMapping<String>("NotFound");
        Assert.assertEquals("NotFound", mapping.map("not-existing"));
        mapping.add("*.netty.io", "Netty");
        Assert.assertEquals("NotFound", mapping.map("not-existing"));
    }

    @Test
    public void testStrictEqualityInDeprecatedApi() {
        DomainNameMapping<String> mapping = new DomainNameMapping<String>("NotFound").add("netty.io", "Netty").add("downloads.netty.io", "Netty-Downloads");
        Assert.assertEquals("Netty", mapping.map("netty.io"));
        Assert.assertEquals("Netty-Downloads", mapping.map("downloads.netty.io"));
        Assert.assertEquals("NotFound", mapping.map("x.y.z.netty.io"));
    }

    @Test
    public void testWildcardMatchesAnyPrefixInDeprecatedApi() {
        DomainNameMapping<String> mapping = new DomainNameMapping<String>("NotFound").add("*.netty.io", "Netty");
        Assert.assertEquals("Netty", mapping.map("netty.io"));
        Assert.assertEquals("Netty", mapping.map("downloads.netty.io"));
        Assert.assertEquals("Netty", mapping.map("x.y.z.netty.io"));
        Assert.assertEquals("NotFound", mapping.map("netty.io.x"));
    }

    @Test
    public void testFirstMatchWinsInDeprecatedApi() {
        Assert.assertEquals("Netty", new DomainNameMapping<String>("NotFound").add("*.netty.io", "Netty").add("downloads.netty.io", "Netty-Downloads").map("downloads.netty.io"));
        Assert.assertEquals("Netty-Downloads", new DomainNameMapping<String>("NotFound").add("downloads.netty.io", "Netty-Downloads").add("*.netty.io", "Netty").map("downloads.netty.io"));
    }

    @Test
    public void testToStringInDeprecatedApi() {
        DomainNameMapping<String> mapping = new DomainNameMapping<String>("NotFound").add("*.netty.io", "Netty").add("downloads.netty.io", "Netty-Downloads");
        Assert.assertEquals("DomainNameMapping(default: NotFound, map: {*.netty.io=Netty, downloads.netty.io=Netty-Downloads})", mapping.toString());
    }

    // Immutable DomainNameMapping Builder API
    @Test(expected = NullPointerException.class)
    public void testNullDefaultValue() {
        new DomainNameMappingBuilder<String>(null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullDomainNamePatternsAreForbidden() {
        new DomainNameMappingBuilder<String>("NotFound").add(null, "Some value");
    }

    @Test(expected = NullPointerException.class)
    public void testNullValuesAreForbidden() {
        new DomainNameMappingBuilder<String>("NotFound").add("Some key", null);
    }

    @Test
    public void testDefaultValue() {
        DomainNameMapping<String> mapping = new DomainNameMappingBuilder<String>("NotFound").add("*.netty.io", "Netty").build();
        Assert.assertEquals("NotFound", mapping.map("not-existing"));
    }

    @Test
    public void testStrictEquality() {
        DomainNameMapping<String> mapping = new DomainNameMappingBuilder<String>("NotFound").add("netty.io", "Netty").add("downloads.netty.io", "Netty-Downloads").build();
        Assert.assertEquals("Netty", mapping.map("netty.io"));
        Assert.assertEquals("Netty-Downloads", mapping.map("downloads.netty.io"));
        Assert.assertEquals("NotFound", mapping.map("x.y.z.netty.io"));
    }

    @Test
    public void testWildcardMatchesAnyPrefix() {
        DomainNameMapping<String> mapping = new DomainNameMappingBuilder<String>("NotFound").add("*.netty.io", "Netty").build();
        Assert.assertEquals("Netty", mapping.map("netty.io"));
        Assert.assertEquals("Netty", mapping.map("downloads.netty.io"));
        Assert.assertEquals("Netty", mapping.map("x.y.z.netty.io"));
        Assert.assertEquals("NotFound", mapping.map("netty.io.x"));
    }

    @Test
    public void testFirstMatchWins() {
        Assert.assertEquals("Netty", new DomainNameMappingBuilder<String>("NotFound").add("*.netty.io", "Netty").add("downloads.netty.io", "Netty-Downloads").build().map("downloads.netty.io"));
        Assert.assertEquals("Netty-Downloads", new DomainNameMappingBuilder<String>("NotFound").add("downloads.netty.io", "Netty-Downloads").add("*.netty.io", "Netty").build().map("downloads.netty.io"));
    }

    @Test
    public void testToString() {
        DomainNameMapping<String> mapping = new DomainNameMappingBuilder<String>("NotFound").add("*.netty.io", "Netty").add("downloads.netty.io", "Netty-Download").build();
        Assert.assertEquals("ImmutableDomainNameMapping(default: NotFound, map: {*.netty.io=Netty, downloads.netty.io=Netty-Download})", mapping.toString());
    }

    @Test
    public void testAsMap() {
        DomainNameMapping<String> mapping = new DomainNameMapping<String>("NotFound").add("netty.io", "Netty").add("downloads.netty.io", "Netty-Downloads");
        Map<String, String> entries = mapping.asMap();
        Assert.assertEquals(2, entries.size());
        Assert.assertEquals("Netty", entries.get("netty.io"));
        Assert.assertEquals("Netty-Downloads", entries.get("downloads.netty.io"));
    }

    @Test
    public void testAsMapWithImmutableDomainNameMapping() {
        DomainNameMapping<String> mapping = new DomainNameMappingBuilder<String>("NotFound").add("netty.io", "Netty").add("downloads.netty.io", "Netty-Downloads").build();
        Map<String, String> entries = mapping.asMap();
        Assert.assertEquals(2, entries.size());
        Assert.assertEquals("Netty", entries.get("netty.io"));
        Assert.assertEquals("Netty-Downloads", entries.get("downloads.netty.io"));
    }
}

