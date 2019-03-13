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


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link RelativePackageNameResolver}.
 */
@RunWith(JUnit4.class)
public class RelativePackageNameResolverTest {
    private RelativePackageNameResolver resolver;

    @Test
    public void testRelativePackagesBelowOneLevelWork() throws Exception {
        createResolver("foo", true);
        assertResolvesTo("bar", "foo/bar");
        createResolver("foo/bar", true);
        assertResolvesTo("pear", "foo/bar/pear");
    }

    @Test
    public void testRelativePackagesBelowTwoLevelsWork() throws Exception {
        createResolver("foo/bar", true);
        assertResolvesTo("pear", "foo/bar/pear");
    }

    @Test
    public void testRelativePackagesAboveOneLevelWork() throws Exception {
        createResolver("foo", true);
        assertResolvesTo("../bar", "bar");
    }

    @Test
    public void testRelativePackagesAboveTwoLevelsWork() throws Exception {
        createResolver("foo/bar", true);
        assertResolvesTo("../../apple", "apple");
    }

    @Test
    public void testSimpleAbsolutePackagesWork() throws Exception {
        createResolver("foo", true);
        assertResolvesTo("//foo", "foo");
        assertResolvesTo("//foo/bar", "foo/bar");
    }

    @Test
    public void testBuildNotRemoved() throws Exception {
        createResolver("foo", false);
        assertResolvesTo("bar/BUILD", "foo/bar/BUILD");
    }

    @Test
    public void testBuildRemoved() throws Exception {
        createResolver("foo", true);
        assertResolvesTo("bar/BUILD", "foo/bar");
    }

    @Test
    public void testEmptyOffset() throws Exception {
        createResolver("", true);
        assertResolvesTo("bar", "bar");
        assertResolvesTo("bar/qux", "bar/qux");
    }

    @Test
    public void testTooFarUpwardsOneLevelThrows() throws Exception {
        createResolver("foo", true);
        try {
            resolver.resolve("../../bar");
            Assert.fail("InvalidPackageNameException expected");
        } catch (InvalidPackageNameException e) {
            // good
        }
    }

    @Test
    public void testTooFarUpwardsTwoLevelsThrows() throws Exception {
        createResolver("foo/bar", true);
        assertResolvesTo("../../orange", "orange");
        try {
            resolver.resolve("../../../orange");
            Assert.fail("InvalidPackageNameException expected");
        } catch (InvalidPackageNameException e) {
            // good
        }
    }
}

