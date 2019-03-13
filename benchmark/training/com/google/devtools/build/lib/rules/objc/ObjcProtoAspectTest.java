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
package com.google.devtools.build.lib.rules.objc;


import ConfigurationDistinguisher.APPLEBIN_IOS;
import ObjcProtoProvider.SKYLARK_CONSTRUCTOR;
import com.google.common.collect.Iterables;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.TestConstants;
import com.google.devtools.build.lib.vfs.PathFragment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test case for the objc_proto_library aspect.
 */
@RunWith(JUnit4.class)
public final class ObjcProtoAspectTest extends ObjcRuleTestCase {
    @Test
    public void testObjcProtoAspectPropagatesProvider() throws Exception {
        scratch.file("x/BUILD", "proto_library(", "  name = 'protos',", "  srcs = ['data.proto'],", ")", "objc_proto_library(", "  name = 'x',", "  deps = [':protos'],", "  portable_proto_filters = ['data_filter.pbascii'],", ")");
        ConfiguredTarget topTarget = getObjcProtoAspectConfiguredTarget("//x:x");
        ObjcProtoProvider objcProtoProvider = topTarget.get(SKYLARK_CONSTRUCTOR);
        assertThat(objcProtoProvider).isNotNull();
    }

    @Test
    public void testObjcProtoAspectPropagatesProtobufProvider() throws Exception {
        scratch.file("x/BUILD", "proto_library(", "  name = 'protos',", "  srcs = ['data.proto'],", ")", "objc_proto_library(", "  name = 'x',", "  deps = [':protos'],", "  portable_proto_filters = ['data_filter.pbascii'],", ")");
        ConfiguredTarget topTarget = getObjcProtoAspectConfiguredTarget("//x:x");
        ObjcProtoProvider objcProtoProvider = topTarget.get(SKYLARK_CONSTRUCTOR);
        assertThat(objcProtoProvider).isNotNull();
        assertThat(Artifact.toExecPaths(objcProtoProvider.getProtobufHeaders())).containsExactly(((TestConstants.TOOLS_REPOSITORY_PATH_PREFIX) + "objcproto/include/header.h"));
        Artifact header = Iterables.getOnlyElement(objcProtoProvider.getProtobufHeaders());
        PathFragment includePath = header.getExecPath().getParentDirectory();
        PathFragment genIncludePath = PathFragment.create((((configurationGenfiles("x86_64", APPLEBIN_IOS, null)) + "/") + includePath));
        assertThat(objcProtoProvider.getProtobufHeaderSearchPaths()).containsExactly(includePath, genIncludePath);
    }

    @Test
    public void testObjcProtoAspectDoesNotPropagateProviderWhenNoProtos() throws Exception {
        scratch.file("x/BUILD", "objc_library(", "  name = 'x',", "  srcs = ['A.m'],", ")");
        ConfiguredTarget topTarget = getObjcProtoAspectConfiguredTarget("//x:x");
        ObjcProtoProvider objcProtoProvider = topTarget.get(SKYLARK_CONSTRUCTOR);
        assertThat(objcProtoProvider).isNull();
    }

    @Test
    public void testObjcProtoAspectBundlesDuplicateSymbols() throws Exception {
        scratch.file("x/BUILD", "objc_library(", "  name = 'x',", "  srcs = ['A.m'],", "  deps = [", "    ':objc_proto',", "    ':objc_proto_duplicate',", "  ],", ")", "proto_library(", "  name = 'protos',", "  srcs = ['data.proto'],", ")", "objc_proto_library(", "  name = 'objc_proto',", "  deps = [':protos'],", "  portable_proto_filters = ['data_filter.pbascii'],", ")", "objc_proto_library(", "  name = 'objc_proto_duplicate',", "  deps = [':protos'],", "  portable_proto_filters = ['data_filter.pbascii'],", ")");
        ConfiguredTarget topTarget = getObjcProtoAspectConfiguredTarget("//x:x");
        ObjcProtoProvider objcProtoProvider = topTarget.get(SKYLARK_CONSTRUCTOR);
        assertThat(objcProtoProvider).isNotNull();
        assertThat(Artifact.toExecPaths(Iterables.concat(objcProtoProvider.getProtoGroups()))).containsExactly("x/data.proto");
        assertThat(Artifact.toExecPaths(objcProtoProvider.getPortableProtoFilters())).containsExactly("x/data_filter.pbascii");
    }

    @Test
    public void testObjcProtoAspectPropagatesGeneratedFilter() throws Exception {
        scratch.file("x/BUILD", "objc_library(", "  name = 'x',", "  srcs = ['A.m'],", "  deps = [", "    ':objc_proto',", "  ],", ")", "proto_library(", "  name = 'protos',", "  srcs = ['data.proto'],", ")", "objc_proto_library(", "  name = 'objc_proto',", "  deps = [':protos'],", ")");
        ConfiguredTarget topTarget = getObjcProtoAspectConfiguredTarget("//x:x");
        ObjcProtoProvider objcProtoProvider = topTarget.get(SKYLARK_CONSTRUCTOR);
        assertThat(objcProtoProvider).isNotNull();
        assertThat(Artifact.toExecPaths(objcProtoProvider.getPortableProtoFilters())).containsExactly(((configurationGenfiles("x86_64", APPLEBIN_IOS, null)) + "/x/_proto_filters/objc_proto/generated_filter_file.pbascii"));
    }

    @Test
    public void testObjcProtoAspectPropagatesFiltersFromDependenciesOfObjcProtoLibrary() throws Exception {
        scratch.file("x/BUILD", "objc_library(", "  name = 'x',", "  srcs = ['A.m'],", "  deps = [", "    ':objc_proto_all',", "  ],", ")", "proto_library(", "  name = 'protos',", "  srcs = ['data.proto'],", ")", "objc_proto_library(", "  name = 'objc_proto_all',", "  deps = [':objc_proto_1', ':objc_proto_2'],", ")", "objc_proto_library(", "  name = 'objc_proto_1',", "  deps = [':protos'],", "  portable_proto_filters = ['filter.pbascii'],", ")", "objc_proto_library(", "  name = 'objc_proto_2',", "  deps = [':protos'],", ")");
        ConfiguredTarget topTarget = getObjcProtoAspectConfiguredTarget("//x:x");
        ObjcProtoProvider objcProtoProvider = topTarget.get(SKYLARK_CONSTRUCTOR);
        assertThat(objcProtoProvider).isNotNull();
        assertThat(Artifact.toExecPaths(objcProtoProvider.getPortableProtoFilters())).containsAllOf("x/filter.pbascii", ((configurationGenfiles("x86_64", APPLEBIN_IOS, null)) + "/x/_proto_filters/objc_proto_2/generated_filter_file.pbascii"));
    }

    @Test
    public void testObjcProtoAspectPropagatesProviderThroughSkylarkRule() throws Exception {
        scratch.file("test_skylark/BUILD");
        scratch.file("test_skylark/top_level_stub.bzl", "def top_level_stub_impl(ctx):", "  deps = hasattr(ctx.attr.deps[0], 'ObjcProto')", "  return struct(dep = ctx.attr.deps[0])", "top_level_stub = rule(", "    top_level_stub_impl,", "    attrs = {", "        'deps': attr.label_list(", "             aspects=[apple_common.objc_proto_aspect],", "        ),", "    },", "    fragments = ['apple'],", ")");
        scratch.file("x/BUILD", "proto_library(", "  name = 'protos',", "  srcs = ['data.proto'],", ")", "objc_proto_library(", "  name = 'x',", "  deps = [':protos'],", "  portable_proto_filters = ['data_filter.pbascii'],", ")");
        scratch.file("bin/BUILD", "load('//test_skylark:top_level_stub.bzl', 'top_level_stub')", "top_level_stub(", "  name = 'link_target',", "  deps = ['//x:x'],", ")");
        ConfiguredTarget topTarget = getConfiguredTarget("//bin:link_target");
        ConfiguredTarget depTarget = ((ConfiguredTarget) (topTarget.get("dep")));
        ObjcProtoProvider objcProtoProvider = depTarget.get(SKYLARK_CONSTRUCTOR);
        assertThat(objcProtoProvider).isNotNull();
    }
}

