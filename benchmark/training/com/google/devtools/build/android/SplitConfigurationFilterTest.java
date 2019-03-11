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
package com.google.devtools.build.android;


import com.google.common.collect.ImmutableList;
import com.google.common.testing.EqualsTester;
import com.google.devtools.build.android.SplitConfigurationFilter.ResourceConfiguration;
import com.google.devtools.build.android.SplitConfigurationFilter.UnrecognizedSplitsException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link SplitConfigurationFilter}.
 */
@RunWith(JUnit4.class)
public final class SplitConfigurationFilterTest {
    @Test
    public void mapFilenamesToSplitFlagsShouldReturnEmptyMapForEmptyInput() throws UnrecognizedSplitsException {
        assertThat(SplitConfigurationFilter.mapFilenamesToSplitFlags(ImmutableList.<String>of(), ImmutableList.<String>of())).isEmpty();
    }

    @Test
    public void mapFilenamesToSplitFlagsShouldMatchIdenticalFilenames() throws UnrecognizedSplitsException {
        assertThat(SplitConfigurationFilter.mapFilenamesToSplitFlags(ImmutableList.of("en"), ImmutableList.of("en"))).containsExactly("en", "en");
        assertThat(SplitConfigurationFilter.mapFilenamesToSplitFlags(ImmutableList.of("fr-v7"), ImmutableList.of("fr-v7"))).containsExactly("fr-v7", "fr-v7");
        assertThat(SplitConfigurationFilter.mapFilenamesToSplitFlags(ImmutableList.of("en_fr-v7"), ImmutableList.of("en,fr-v7"))).containsExactly("en_fr-v7", "en_fr-v7");
        assertThat(SplitConfigurationFilter.mapFilenamesToSplitFlags(ImmutableList.of("en", "fr-v7"), ImmutableList.of("en", "fr-v7"))).containsExactly("en", "en", "fr-v7", "fr-v7");
        assertThat(SplitConfigurationFilter.mapFilenamesToSplitFlags(ImmutableList.of("fr-v4", "fr-v7"), ImmutableList.of("fr-v4", "fr-v7"))).containsExactly("fr-v4", "fr-v4", "fr-v7", "fr-v7");
    }

    @Test
    public void mapFilenamesToSplitFlagsShouldMatchReorderedFilenames() throws UnrecognizedSplitsException {
        assertThat(SplitConfigurationFilter.mapFilenamesToSplitFlags(ImmutableList.of("en_fr_ja"), ImmutableList.of("ja,fr,en"))).containsExactly("en_fr_ja", "ja_fr_en");
        assertThat(SplitConfigurationFilter.mapFilenamesToSplitFlags(ImmutableList.of("zu_12key", "zu"), ImmutableList.of("12key,zu", "zu"))).containsExactly("zu_12key", "12key_zu", "zu", "zu");
        assertThat(SplitConfigurationFilter.mapFilenamesToSplitFlags(ImmutableList.of("land_car", "round_port"), ImmutableList.of("car,land", "port,round"))).containsExactly("land_car", "car_land", "round_port", "port_round");
    }

    @Test
    public void mapFilenamesToSplitFlagsShouldMatchVersionUpgradedFilenames() throws UnrecognizedSplitsException {
        assertThat(SplitConfigurationFilter.mapFilenamesToSplitFlags(ImmutableList.of("round-v23"), ImmutableList.of("round"))).containsExactly("round-v23", "round");
        assertThat(SplitConfigurationFilter.mapFilenamesToSplitFlags(ImmutableList.of("watch-v20", "watch-v23"), ImmutableList.of("watch", "watch-v23"))).containsExactly("watch-v20", "watch", "watch-v23", "watch-v23");
    }

    @Test
    public void equivalentFiltersShouldMatchFilterFromFilename() {
        // identical inputs (barring commas -> underscores) should naturally match
        assertMatchesFilterFromFilename("abc-def", "abc-def").isTrue();
        assertMatchesFilterFromFilename("abc-def-v5,ghi,jkl-lmno", "abc-def-v5_ghi_jkl-lmno").isTrue();
        // anything that results in matching sets should work, so case should be ignored
        assertMatchesFilterFromFilename("abc-dEF", "aBC-def").isTrue();
        assertMatchesFilterFromFilename("aBC-def", "abc-dEF").isTrue();
        // additionally, order of elements in the set should be ignored
        assertMatchesFilterFromFilename("abc,def", "def_abc").isTrue();
        assertMatchesFilterFromFilename("def,abc", "abc_def").isTrue();
    }

    @Test
    public void matchingSpecifiersShouldMatchFilterFromFilenameWhenVersionsAreHigherNotWhenLower() {
        // different (higher) versions, but same specifiers and same order
        assertMatchesFilterFromFilename("x-v5,y-v6", "x-v7_y-v9").isTrue();
        assertMatchesFilterFromFilename("x-v7,y-v9", "x-v5_y-v6").isFalse();
        // order of sorted set changes but matching specifiers still match and filename version > flag
        assertMatchesFilterFromFilename("x-v3,y-v6", "x-v23_y-v9").isTrue();
        assertMatchesFilterFromFilename("x-v23,y-v9", "x-v3_y-v6").isFalse();
        // specifier sets of multiple configs are the same
        assertMatchesFilterFromFilename("x-v3,x-v17", "x-v3_x-v17").isTrue();
        assertMatchesFilterFromFilename("x-v3,x-v17", "x-v17_x-v3").isTrue();
        assertMatchesFilterFromFilename("x-v17,x-v3", "x-v17_x-v3").isTrue();
        assertMatchesFilterFromFilename("x-v17,x-v3", "x-v3_x-v17").isTrue();
        // specifier sets are the same, but one of them got a version bump
        assertMatchesFilterFromFilename("x-v3,x-v17", "x-v14_x-v17").isTrue();
        assertMatchesFilterFromFilename("x-v14,x-v17", "x-v3_x-v17").isFalse();
    }

    @Test
    public void nonMatchingSpecifiersShouldNotMatchFilterFromFilename() {
        // completely disjoint specifier sets
        assertMatchesFilterFromFilename("x,y,z", "a_b_c").isFalse();
        assertMatchesFilterFromFilename("a,b,c", "x_y_z").isFalse();
        // different number of specifier sets, which otherwise match
        assertMatchesFilterFromFilename("x,y,z", "x_y").isFalse();
        assertMatchesFilterFromFilename("x,y", "x_y_z").isFalse();
        // same number of specifiers with one non-match
        assertMatchesFilterFromFilename("x,y,z", "a_y_z").isFalse();
        assertMatchesFilterFromFilename("a,b,c", "a_b_z").isFalse();
    }

    @Test
    public void splitConfigurationFilterShouldBeOrderedByConfigsThenFilename() {
        assertThat(// If all else is equal, break ties via filename (case does matter here)
        // In filters with the same number of configs, the highest version wins
        // It doesn't matter where in the input order that version is
        // Specifiers break ties on number of configs + highest version
        // Second highest version breaks ties (etc.)
        // Order doesn't matter but it does change the filename, hence sort order
        // Number of configs is the main criterion, so more sets of configs means later sort
        ImmutableList.of(SplitConfigurationFilter.fromFilenameSuffix("A"), SplitConfigurationFilter.fromFilenameSuffix("a"), SplitConfigurationFilter.fromFilenameSuffix("d-v5_e-v5_f-v5"), SplitConfigurationFilter.fromFilenameSuffix("a_b_c-v6"), SplitConfigurationFilter.fromFilenameSuffix("a-v7_b_c"), SplitConfigurationFilter.fromFilenameSuffix("d-v7_b_c"), SplitConfigurationFilter.fromFilenameSuffix("b-v2_d-v7_c"), SplitConfigurationFilter.fromFilenameSuffix("d-v7_b-v2_c"), SplitConfigurationFilter.fromFilenameSuffix("a_b_c_d_e_f_g_h_i_j_k_l_m"))).isStrictlyOrdered();
        // if they are actually equal, they will compare equal
        assertThat(SplitConfigurationFilter.fromFilenameSuffix("c-v13")).isEquivalentAccordingToCompareTo(SplitConfigurationFilter.fromFilenameSuffix("c-v13"));
        // if the only difference is split flag vs. filename suffix they will compare equal
        assertThat(SplitConfigurationFilter.fromSplitFlag("split,split")).isEquivalentAccordingToCompareTo(SplitConfigurationFilter.fromFilenameSuffix("split_split"));
    }

    @Test
    public void splitConfigurationFilterEqualsShouldPassEqualsTester() {
        // totally different set is also very clearly different
        // different filename producing equal set (input order is different) is still different
        // different filename producing equal set (elements parse the same) is still different
        // case matters for filenames
        // base example
        // identical clone
        // commas are converted for split flags, the result is equal
        new EqualsTester().addEqualityGroup(SplitConfigurationFilter.fromFilenameSuffix("abc-def_ghi_jkl"), SplitConfigurationFilter.fromFilenameSuffix("abc-def_ghi_jkl"), SplitConfigurationFilter.fromSplitFlag("abc-def,ghi,jkl")).addEqualityGroup(SplitConfigurationFilter.fromFilenameSuffix("aBC-dEF_ghi_jkl")).addEqualityGroup(SplitConfigurationFilter.fromFilenameSuffix("abc-def-v0_ghi_jkl")).addEqualityGroup(SplitConfigurationFilter.fromFilenameSuffix("ghi_abc-def_jkl")).addEqualityGroup(SplitConfigurationFilter.fromFilenameSuffix("some-other_specifiers")).testEquals();
    }

    @Test
    public void equalInputsShouldMatchConfigurationFromFilename() {
        // identical inputs should naturally match
        assertMatchesConfigurationFromFilename("abc-def", "abc-def").isTrue();
        assertMatchesConfigurationFromFilename("abc-def-v5", "abc-def-v5").isTrue();
        // case should be ignored
        assertMatchesConfigurationFromFilename("abc-dEF", "aBC-def").isTrue();
        assertMatchesConfigurationFromFilename("aBC-def", "abc-dEF").isTrue();
        // wildcards should be ignored
        assertMatchesConfigurationFromFilename("any-abc-def", "abc-any-def").isTrue();
        assertMatchesConfigurationFromFilename("abc-any-def", "any-abc-def").isTrue();
        // v0 should be ignored
        assertMatchesConfigurationFromFilename("abc-def", "abc-def-v0").isTrue();
        assertMatchesConfigurationFromFilename("abc-def-v0", "abc-def").isTrue();
    }

    @Test
    public void higherVersionSameSpecifiersShouldMatchConfigurationFromFilename() {
        // any version is higher than the default v0
        assertMatchesConfigurationFromFilename("abc-def", "abc-def-v1").isTrue();
        assertMatchesConfigurationFromFilename("abc-def-v1", "abc-def").isFalse();
        // same deal for explicit v0
        assertMatchesConfigurationFromFilename("abc-def-v0", "abc-def-v1").isTrue();
        assertMatchesConfigurationFromFilename("abc-def-v1", "abc-def-v0").isFalse();
        // higher versions are better of course
        assertMatchesConfigurationFromFilename("abc-def-v1", "abc-def-v999").isTrue();
        assertMatchesConfigurationFromFilename("abc-def-v999", "abc-def-v1").isFalse();
    }

    @Test
    public void nonMatchingSpecifiersShouldNotMatchConfigurationFromFilename() {
        // same version
        assertMatchesConfigurationFromFilename("abc", "ghi").isFalse();
        assertMatchesConfigurationFromFilename("abc-v0", "ghi-v0").isFalse();
        assertMatchesConfigurationFromFilename("abc-v15", "ghi-v15").isFalse();
        // different version
        assertMatchesConfigurationFromFilename("abc", "ghi-v15").isFalse();
        assertMatchesConfigurationFromFilename("abc-v0", "ghi-v15").isFalse();
        assertMatchesConfigurationFromFilename("abc-v15", "ghi-v19").isFalse();
    }

    @Test
    public void resourceConfigurationShouldBeOrderedByApiVersionThenSpecifiers() {
        assertThat(// -v0 and no -v0 sort equal given the same specifiers, so it's a tie
        // version ties are broken based on lexicographic string ordering
        // string ordering ignores case of the specifiers
        // higher API versions sort later regardless of the specifiers
        // wildcards ("any") are ignored when considering specifier order
        ImmutableList.of(ResourceConfiguration.fromString("a"), ResourceConfiguration.fromString("b-v0"), ResourceConfiguration.fromString("Z"), ResourceConfiguration.fromString("z-v6"), ResourceConfiguration.fromString("b-v13"), ResourceConfiguration.fromString("any-c-v13"))).isStrictlyOrdered();
        // if they are actually equal, they will compare equal
        assertThat(ResourceConfiguration.fromString("c-v13")).isEquivalentAccordingToCompareTo(ResourceConfiguration.fromString("c-v13"));
        // if the only difference is wildcards they will compare equal
        assertThat(ResourceConfiguration.fromString("any-c-v13")).isEquivalentAccordingToCompareTo(ResourceConfiguration.fromString("c-v13"));
        // if the only difference is specifying (or not specifying) -v0 they will compare equal
        assertThat(ResourceConfiguration.fromString("a-v0")).isEquivalentAccordingToCompareTo(ResourceConfiguration.fromString("a"));
        // if the only difference is case they will compare equal
        assertThat(ResourceConfiguration.fromString("z")).isEquivalentAccordingToCompareTo(ResourceConfiguration.fromString("Z"));
    }

    @Test
    public void resourceConfigurationShouldPassEqualsTester() {
        // not if the version is nonzero though
        // empty strings are equal
        // yes, even if they have a v0
        // or if there are some wildcards which will collapse to nothing
        // two configurations with version codes must also have EQUAL version codes
        // different version codes are parsed but result in a non-equal instance
        // empty segments are not collapsed
        // order matters
        // base example
        // identical clone
        // case doesn't matter
        // absent version code is equivalent to -v0
        // skip "any"
        new EqualsTester().addEqualityGroup(ResourceConfiguration.fromString("abc-def"), ResourceConfiguration.fromString("abc-def"), ResourceConfiguration.fromString("aBC-dEF"), ResourceConfiguration.fromString("abc-def-v0"), ResourceConfiguration.fromString("any-abc-any-def-any")).addEqualityGroup(ResourceConfiguration.fromString("def-abc")).addEqualityGroup(ResourceConfiguration.fromString("abc---def")).addEqualityGroup(ResourceConfiguration.fromString("abc-def-v15")).addEqualityGroup(ResourceConfiguration.fromString("abc-def-v12")).addEqualityGroup(ResourceConfiguration.fromString(""), ResourceConfiguration.fromString("v0"), ResourceConfiguration.fromString("any"), ResourceConfiguration.fromString("any-v0"), ResourceConfiguration.fromString("any-any-any"), ResourceConfiguration.fromString("any-any-any-v0")).addEqualityGroup(ResourceConfiguration.fromString("v15"), ResourceConfiguration.fromString("any-v15")).testEquals();
    }
}

