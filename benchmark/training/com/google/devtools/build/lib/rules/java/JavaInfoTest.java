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
package com.google.devtools.build.lib.rules.java;


import JavaInfo.Builder;
import JavaInfo.EMPTY;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.cmdline.LabelSyntaxException;
import com.google.devtools.build.lib.collect.nestedset.NestedSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link JavaInfo}.
 */
@RunWith(JUnit4.class)
public class JavaInfoTest {
    @Test
    public void getTransitiveRuntimeJars_noJavaCompilationArgsProvider() {
        assertThat(EMPTY.getTransitiveRuntimeJars().isEmpty()).isTrue();
    }

    @Test
    public void getTransitiveCompileTimeJarsJars_noJavaCompilationArgsProvider() {
        assertThat(EMPTY.getTransitiveCompileTimeJars().isEmpty()).isTrue();
    }

    @Test
    public void getCompileTimeJarsJars_noJavaCompilationArgsProvider() {
        assertThat(EMPTY.getCompileTimeJars().isEmpty()).isTrue();
    }

    @Test
    public void getFullCompileTimeJarsJars_noJavaCompilationArgsProvider() {
        assertThat(EMPTY.getFullCompileTimeJars().isEmpty()).isTrue();
    }

    @Test
    public void getSourceJars_noJavaSourceJarsProvider() {
        assertThat(EMPTY.getSourceJars()).isEmpty();
    }

    @Test
    public void testMergeJavaExportsProvider() throws LabelSyntaxException {
        JavaInfo javaInfo1 = Builder.create().addProvider(JavaExportsProvider.class, createJavaExportsProvider("foo", 2)).build();
        JavaInfo javaInfo2 = Builder.create().addProvider(JavaExportsProvider.class, createJavaExportsProvider("bar", 3)).build();
        JavaInfo javaInfoMerged = JavaInfo.merge(ImmutableList.of(javaInfo1, javaInfo2));
        NestedSet<Label> labels = javaInfoMerged.getTransitiveExports();
        assertThat(labels).containsExactly(Label.parseAbsolute("//foo:foo0.bzl", ImmutableMap.of()), Label.parseAbsolute("//foo:foo1.bzl", ImmutableMap.of()), Label.parseAbsolute("//bar:bar0.bzl", ImmutableMap.of()), Label.parseAbsolute("//bar:bar1.bzl", ImmutableMap.of()), Label.parseAbsolute("//bar:bar2.bzl", ImmutableMap.of())).inOrder();
    }
}

