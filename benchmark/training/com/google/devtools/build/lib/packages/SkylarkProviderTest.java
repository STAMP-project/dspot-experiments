/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.packages;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.testing.EqualsTester;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.packages.SkylarkProvider.SkylarkKey;
import com.google.devtools.build.lib.syntax.Printer;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link SkylarkProvider}.
 */
@RunWith(JUnit4.class)
public final class SkylarkProviderTest {
    @Test
    public void unexportedProvider_Accessors() {
        SkylarkProvider provider = /* location= */
        SkylarkProvider.createUnexportedSchemaless(null);
        assertThat(provider.isExported()).isFalse();
        assertThat(provider.getName()).isEqualTo("<no name>");
        assertThat(provider.getPrintableName()).isEqualTo("<no name>");
        assertThat(provider.getErrorMessageFormatForUnknownField()).isEqualTo("Object has no '%s' attribute.");
        assertThat(provider.isImmutable()).isFalse();
        assertThat(Printer.repr(provider)).isEqualTo("<provider>");
        MoreAsserts.assertThrows(IllegalStateException.class, () -> provider.getKey());
    }

    @Test
    public void exportedProvider_Accessors() throws Exception {
        SkylarkKey key = new SkylarkKey(Label.parseAbsolute("//foo:bar.bzl", ImmutableMap.of()), "prov");
        SkylarkProvider provider = /* location= */
        SkylarkProvider.createExportedSchemaless(key, null);
        assertThat(provider.isExported()).isTrue();
        assertThat(provider.getName()).isEqualTo("prov");
        assertThat(provider.getPrintableName()).isEqualTo("prov");
        assertThat(provider.getErrorMessageFormatForUnknownField()).isEqualTo("'prov' object has no attribute '%s'");
        assertThat(provider.isImmutable()).isTrue();
        assertThat(Printer.repr(provider)).isEqualTo("<provider>");
        assertThat(provider.getKey()).isEqualTo(key);
    }

    @Test
    public void schemalessProvider_Instantiation() throws Exception {
        SkylarkProvider provider = /* location= */
        SkylarkProvider.createUnexportedSchemaless(null);
        SkylarkInfo info = SkylarkProviderTest.instantiateWithA1B2C3(provider);
        assertThat(info.isCompact()).isFalse();
        SkylarkProviderTest.assertHasExactlyValuesA1B2C3(info);
    }

    @Test
    public void schemafulProvider_Instantiation() throws Exception {
        SkylarkProvider provider = /* location= */
        SkylarkProvider.createUnexportedSchemaful(ImmutableList.of("a", "b", "c"), null);
        SkylarkInfo info = SkylarkProviderTest.instantiateWithA1B2C3(provider);
        assertThat(info.isCompact()).isTrue();
        SkylarkProviderTest.assertHasExactlyValuesA1B2C3(info);
    }

    @Test
    public void schemalessProvider_GetFields() throws Exception {
        SkylarkProvider provider = /* location= */
        SkylarkProvider.createUnexportedSchemaless(null);
        assertThat(provider.getFields()).isNull();
    }

    @Test
    public void schemafulProvider_GetFields() throws Exception {
        SkylarkProvider provider = /* location= */
        SkylarkProvider.createUnexportedSchemaful(ImmutableList.of("a", "b", "c"), null);
        assertThat(provider.getFields()).containsExactly("a", "b", "c").inOrder();
    }

    @Test
    public void providerEquals() throws Exception {
        // All permutations of differing label and differing name.
        SkylarkKey keyFooA = new SkylarkKey(Label.parseAbsolute("//foo.bzl", ImmutableMap.of()), "provA");
        SkylarkKey keyFooB = new SkylarkKey(Label.parseAbsolute("//foo.bzl", ImmutableMap.of()), "provB");
        SkylarkKey keyBarA = new SkylarkKey(Label.parseAbsolute("//bar.bzl", ImmutableMap.of()), "provA");
        SkylarkKey keyBarB = new SkylarkKey(Label.parseAbsolute("//bar.bzl", ImmutableMap.of()), "provB");
        // 1 for each key, plus a duplicate for one of the keys, plus 2 that have no key.
        SkylarkProvider provFooA1 = /* location= */
        SkylarkProvider.createExportedSchemaless(keyFooA, null);
        SkylarkProvider provFooA2 = /* location= */
        SkylarkProvider.createExportedSchemaless(keyFooA, null);
        SkylarkProvider provFooB = /* location= */
        SkylarkProvider.createExportedSchemaless(keyFooB, null);
        SkylarkProvider provBarA = /* location= */
        SkylarkProvider.createExportedSchemaless(keyBarA, null);
        SkylarkProvider provBarB = /* location= */
        SkylarkProvider.createExportedSchemaless(keyBarB, null);
        SkylarkProvider provUnexported1 = /* location= */
        SkylarkProvider.createUnexportedSchemaless(null);
        SkylarkProvider provUnexported2 = /* location= */
        SkylarkProvider.createUnexportedSchemaless(null);
        // For exported providers, different keys -> unequal, same key -> equal. For unexported
        // providers it comes down to object identity.
        // reflexive equality (unexported)
        // reflexive equality (exported)
        new EqualsTester().addEqualityGroup(provFooA1, provFooA2).addEqualityGroup(provFooB).addEqualityGroup(provBarA, provBarA).addEqualityGroup(provBarB).addEqualityGroup(provUnexported1, provUnexported1).addEqualityGroup(provUnexported2).testEquals();
    }
}

