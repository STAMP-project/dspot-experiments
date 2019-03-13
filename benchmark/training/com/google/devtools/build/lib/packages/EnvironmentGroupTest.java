/**
 * Copyright 2015 The Bazel Authors. All rights reserved.
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


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.cmdline.PackageIdentifier;
import com.google.devtools.build.lib.packages.util.PackageLoadingTestCase;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.RootedPath;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link EnvironmentGroup}. Note input validation is handled in
 * {@link PackageFactoryTest}.
 */
@RunWith(JUnit4.class)
public class EnvironmentGroupTest extends PackageLoadingTestCase {
    private Package pkg;

    private EnvironmentGroup group;

    @Test
    public void testGroupMembership() throws Exception {
        assertThat(group.getEnvironments()).isEqualTo(ImmutableSet.of(Label.parseAbsolute("//pkg:foo", ImmutableMap.of()), Label.parseAbsolute("//pkg:bar", ImmutableMap.of()), Label.parseAbsolute("//pkg:baz", ImmutableMap.of())));
    }

    @Test
    public void defaultsMembership() throws Exception {
        assertThat(group.getDefaults()).isEqualTo(ImmutableSet.of(Label.parseAbsolute("//pkg:foo", ImmutableMap.of())));
    }

    @Test
    public void isDefault() throws Exception {
        EnvironmentLabels unpackedGroup = group.getEnvironmentLabels();
        assertThat(unpackedGroup.isDefault(Label.parseAbsolute("//pkg:foo", ImmutableMap.of()))).isTrue();
        assertThat(unpackedGroup.isDefault(Label.parseAbsolute("//pkg:bar", ImmutableMap.of()))).isFalse();
        assertThat(unpackedGroup.isDefault(Label.parseAbsolute("//pkg:baz", ImmutableMap.of()))).isFalse();
        assertThat(unpackedGroup.isDefault(Label.parseAbsolute("//pkg:not_in_group", ImmutableMap.of()))).isFalse();
    }

    @Test
    public void fulfillers() throws Exception {
        EnvironmentLabels unpackedGroup = group.getEnvironmentLabels();
        assertThat(unpackedGroup.getFulfillers(Label.parseAbsolute("//pkg:baz", ImmutableMap.of()))).containsExactly(Label.parseAbsolute("//pkg:foo", ImmutableMap.of()), Label.parseAbsolute("//pkg:bar", ImmutableMap.of()));
        assertThat(unpackedGroup.getFulfillers(Label.parseAbsolute("//pkg:bar", ImmutableMap.of()))).containsExactly(Label.parseAbsolute("//pkg:foo", ImmutableMap.of()));
        assertThat(unpackedGroup.getFulfillers(Label.parseAbsolute("//pkg:foo", ImmutableMap.of()))).isEmpty();
    }

    @Test
    public void emptyGroupsNotAllowed() throws Exception {
        Path buildfile = scratch.file("a/BUILD", "environment_group(name = 'empty_group', environments = [], defaults = [])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        Package emptyGroupPkg = packageFactory.createPackageForTesting(PackageIdentifier.createInMainRepo("a"), RootedPath.toRootedPath(root, buildfile), getPackageManager(), reporter);
        assertThat(containsErrors()).isTrue();
        assertContainsEvent("environment group empty_group must contain at least one environment");
    }
}

