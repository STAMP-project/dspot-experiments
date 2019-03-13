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


import Location.BUILTIN;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.testing.EqualsTester;
import com.google.devtools.build.lib.packages.SkylarkInfo.Layout;
import com.google.devtools.build.lib.syntax.EvalException;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test class for {@link SkylarkInfo} and its subclasses.
 */
@RunWith(JUnit4.class)
public class SkylarkInfoTest {
    private static final Layout layoutF1F2 = new Layout(ImmutableList.of("f1", "f2"));

    private static final Layout invertedLayoutF2F1 = new Layout(ImmutableList.of("f2", "f1"));

    @Test
    public void layoutAccessors() {
        Layout layout = new Layout(ImmutableList.of("x", "y", "z"));
        assertThat(layout.size()).isEqualTo(3);
        assertThat(layout.hasField("x")).isTrue();
        assertThat(layout.hasField("q")).isFalse();
        assertThat(layout.getFieldIndex("z")).isEqualTo(2);
        assertThat(layout.getFields()).containsExactly("x", "y", "z").inOrder();
        assertThat(layout.entrySet().stream().map(Map.Entry::getKey).collect(ImmutableList.toImmutableList(ImmutableList))).containsExactly("x", "y", "z").inOrder();
    }

    @Test
    public void layoutDisallowsDuplicates() {
        MoreAsserts.assertThrows(IllegalArgumentException.class, () -> new Layout(ImmutableList.of("x", "y", "x")));
    }

    @Test
    public void layoutEquality() {
        new EqualsTester().addEqualityGroup(new Layout(ImmutableList.of("a", "b", "c")), new Layout(ImmutableList.of("a", "b", "c"))).addEqualityGroup(new Layout(ImmutableList.of("x", "y", "z"))).addEqualityGroup(new Layout(ImmutableList.of("c", "b", "a"))).testEquals();
    }

    @Test
    public void nullLocationDefaultsToBuiltin() throws Exception {
        SkylarkInfo info = SkylarkInfo.createSchemaless(SkylarkInfoTest.makeProvider(), ImmutableMap.of(), null);
        assertThat(info.getCreationLoc()).isEqualTo(BUILTIN);
    }

    @Test
    public void givenLayoutTakesPrecedenceOverProviderLayout() throws Exception {
        SkylarkProvider provider = SkylarkProvider.createUnexportedSchemaful(ImmutableList.of("f1", "f2"), BUILTIN);
        SkylarkInfo info = SkylarkInfo.createSchemaful(provider, SkylarkInfoTest.invertedLayoutF2F1, new Object[]{ 5, 4 }, BUILTIN);
        assertThat(info.getLayout()).isEqualTo(SkylarkInfoTest.invertedLayoutF2F1);// not the one in the provider

    }

    @Test
    public void schemafulValuesMustMatchLayoutArity() throws Exception {
        SkylarkProvider provider = SkylarkInfoTest.makeProvider();
        IllegalArgumentException expected = MoreAsserts.assertThrows(IllegalArgumentException.class, () -> SkylarkInfo.createSchemaful(provider, SkylarkInfoTest.layoutF1F2, new Object[]{ 4 }, BUILTIN));
        assertThat(expected).hasMessageThat().contains("Layout has length 2, but number of given values was 1");
    }

    @Test
    public void instancesOfUnexportedProvidersAreMutable() throws Exception {
        SkylarkProvider provider = SkylarkInfoTest.makeProvider();
        SkylarkInfo mapInfo = SkylarkInfoTest.makeSchemalessInfoWithF1F2Values(provider, 5, null);
        SkylarkInfo compactInfo = SkylarkInfoTest.makeSchemafulInfoWithF1F2Values(provider, 5, null);
        assertThat(mapInfo.isImmutable()).isFalse();
        assertThat(compactInfo.isImmutable()).isFalse();
    }

    @Test
    public void instancesOfExportedProvidersMayBeImmutable() throws Exception {
        SkylarkProvider provider = SkylarkInfoTest.makeExportedProvider();
        SkylarkInfo mapInfo = SkylarkInfoTest.makeSchemalessInfoWithF1F2Values(provider, 5, null);
        SkylarkInfo compactInfo = SkylarkInfoTest.makeSchemafulInfoWithF1F2Values(provider, 5, null);
        assertThat(mapInfo.isImmutable()).isTrue();
        assertThat(compactInfo.isImmutable()).isTrue();
    }

    @Test
    public void mutableIfContentsAreMutable() throws Exception {
        SkylarkProvider provider = SkylarkInfoTest.makeExportedProvider();
        SkylarkInfo mapInfo = SkylarkInfoTest.makeSchemalessInfoWithF1F2Values(provider, 5, new Object());
        SkylarkInfo compactInfo = SkylarkInfoTest.makeSchemafulInfoWithF1F2Values(provider, 5, new Object());
        assertThat(mapInfo.isImmutable()).isFalse();
        assertThat(compactInfo.isImmutable()).isFalse();
    }

    @Test
    public void equality_DifferentProviders() throws Exception {
        SkylarkProvider provider1 = SkylarkInfoTest.makeProvider();
        SkylarkProvider provider2 = SkylarkInfoTest.makeProvider();
        new EqualsTester().addEqualityGroup(SkylarkInfoTest.makeSchemalessInfoWithF1F2Values(provider1, 4, 5), SkylarkInfoTest.makeSchemafulInfoWithF1F2Values(provider1, 4, 5), SkylarkInfoTest.makeInvertedSchemafulInfoWithF1F2Values(provider1, 4, 5)).addEqualityGroup(SkylarkInfoTest.makeSchemalessInfoWithF1F2Values(provider2, 4, 5), SkylarkInfoTest.makeSchemafulInfoWithF1F2Values(provider2, 4, 5), SkylarkInfoTest.makeInvertedSchemafulInfoWithF1F2Values(provider2, 4, 5)).testEquals();
    }

    @Test
    public void equality_DifferentValues() throws Exception {
        SkylarkProvider provider = SkylarkInfoTest.makeProvider();
        // These comparisons include the case where the physical array is {4, 5} on both instances but
        // they compare different due to different layouts.
        new EqualsTester().addEqualityGroup(SkylarkInfoTest.makeSchemalessInfoWithF1F2Values(provider, 4, 5), SkylarkInfoTest.makeSchemafulInfoWithF1F2Values(provider, 4, 5), SkylarkInfoTest.makeInvertedSchemafulInfoWithF1F2Values(provider, 4, 5)).addEqualityGroup(SkylarkInfoTest.makeSchemalessInfoWithF1F2Values(provider, 5, 4), SkylarkInfoTest.makeSchemafulInfoWithF1F2Values(provider, 5, 4), SkylarkInfoTest.makeInvertedSchemafulInfoWithF1F2Values(provider, 5, 4)).addEqualityGroup(SkylarkInfoTest.makeSchemalessInfoWithF1F2Values(provider, 4, null), SkylarkInfoTest.makeSchemafulInfoWithF1F2Values(provider, 4, null), SkylarkInfoTest.makeInvertedSchemafulInfoWithF1F2Values(provider, 4, null)).testEquals();
    }

    @Test
    public void concatWithDifferentProvidersFails() throws Exception {
        SkylarkProvider provider1 = SkylarkInfoTest.makeProvider();
        SkylarkProvider provider2 = SkylarkInfoTest.makeProvider();
        SkylarkInfo info1 = SkylarkInfoTest.makeSchemalessInfoWithF1F2Values(provider1, 4, 5);
        SkylarkInfo info2 = SkylarkInfoTest.makeSchemalessInfoWithF1F2Values(provider2, 4, 5);
        EvalException expected = MoreAsserts.assertThrows(EvalException.class, () -> info1.getConcatter().concat(info1, info2, BUILTIN));
        assertThat(expected).hasMessageThat().contains("Cannot use '+' operator on instances of different providers");
    }

    @Test
    public void concatWithOverlappingFieldsFails() throws Exception {
        SkylarkProvider provider1 = SkylarkInfoTest.makeProvider();
        SkylarkInfo info1 = SkylarkInfoTest.makeSchemalessInfoWithF1F2Values(provider1, 4, 5);
        SkylarkInfo info2 = SkylarkInfoTest.makeSchemalessInfoWithF1F2Values(provider1, 4, null);
        EvalException expected = MoreAsserts.assertThrows(EvalException.class, () -> info1.getConcatter().concat(info1, info2, BUILTIN));
        assertThat(expected).hasMessageThat().contains("Cannot use '+' operator on provider instances with overlapping field(s): f1");
    }

    @Test
    public void compactConcatReturnsCompact() throws Exception {
        SkylarkProvider provider = SkylarkInfoTest.makeProvider();
        SkylarkInfo info1 = SkylarkInfoTest.makeSchemafulInfoWithF1F2Values(provider, 4, null);
        SkylarkInfo info2 = SkylarkInfoTest.makeSchemafulInfoWithF1F2Values(provider, null, 5);
        SkylarkInfo result = ((SkylarkInfo) (info1.getConcatter().concat(info1, info2, BUILTIN)));
        assertThat(result.isCompact()).isTrue();
        assertThat(result.getFieldNames()).containsExactly("f1", "f2");
        assertThat(result.getValue("f1")).isEqualTo(4);
        assertThat(result.getValue("f2")).isEqualTo(5);
    }

    @Test
    public void compactConcatWithDifferentLayoutsReturnsMap() throws Exception {
        SkylarkProvider provider = SkylarkInfoTest.makeProvider();
        SkylarkInfo info1 = SkylarkInfoTest.makeSchemafulInfoWithF1F2Values(provider, 4, null);
        SkylarkInfo info2 = SkylarkInfoTest.makeInvertedSchemafulInfoWithF1F2Values(provider, null, 5);
        SkylarkInfo result = ((SkylarkInfo) (info1.getConcatter().concat(info1, info2, BUILTIN)));
        assertThat(result.isCompact()).isFalse();
        assertThat(result.getFieldNames()).containsExactly("f1", "f2");
        assertThat(result.getValue("f1")).isEqualTo(4);
        assertThat(result.getValue("f2")).isEqualTo(5);
    }

    @Test
    public void allOtherConcatReturnsMap() throws Exception {
        SkylarkProvider provider = SkylarkInfoTest.makeProvider();
        SkylarkInfo info1 = SkylarkInfoTest.makeSchemalessInfoWithF1F2Values(provider, 4, null);
        SkylarkInfo info2 = SkylarkInfoTest.makeSchemafulInfoWithF1F2Values(provider, null, 5);
        SkylarkInfo result = ((SkylarkInfo) (info1.getConcatter().concat(info1, info2, BUILTIN)));
        assertThat(result.isCompact()).isFalse();
        assertThat(result.getFieldNames()).containsExactly("f1", "f2");
        assertThat(result.getValue("f1")).isEqualTo(4);
        assertThat(result.getValue("f2")).isEqualTo(5);
    }
}

