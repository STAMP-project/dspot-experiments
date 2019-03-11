/**
 * Copyright 2016 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.rules.proto;


import Deps.NON_STRICT;
import Deps.STRICT;
import Exports.DO_NOT_USE;
import Exports.USE;
import Order.STABLE_ORDER;
import ProtoCompileActionBuilder.STRICT_DEPS_FLAG_TEMPLATE;
import Services.ALLOW;
import Services.DISALLOW;
import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.ArtifactRoot;
import com.google.devtools.build.lib.analysis.FilesToRunProvider;
import com.google.devtools.build.lib.analysis.TransitiveInfoCollection;
import com.google.devtools.build.lib.analysis.actions.CustomCommandLine;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.collect.nestedset.NestedSetBuilder;
import com.google.devtools.build.lib.collect.nestedset.Order;
import com.google.devtools.build.lib.util.LazyString;
import com.google.devtools.build.lib.util.Pair;
import com.google.devtools.build.lib.vfs.Root;
import com.google.devtools.build.lib.vfs.inmemoryfs.InMemoryFileSystem;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Unit tests for {@link ProtoCompileActionBuilder}.
 */
@RunWith(JUnit4.class)
public class ProtoCompileActionBuilderTest {
    private static final InMemoryFileSystem FILE_SYSTEM = new InMemoryFileSystem();

    private final ArtifactRoot root = ArtifactRoot.asSourceRoot(Root.fromPath(ProtoCompileActionBuilderTest.FILE_SYSTEM.getPath("/")));

    private final ArtifactRoot derivedRoot = ArtifactRoot.asDerivedRoot(ProtoCompileActionBuilderTest.FILE_SYSTEM.getPath("/"), ProtoCompileActionBuilderTest.FILE_SYSTEM.getPath("/out"));

    @Test
    public void commandLine_basic() throws Exception {
        FilesToRunProvider plugin = /* runfilesSupport */
        new FilesToRunProvider(NestedSetBuilder.emptySet(STABLE_ORDER), null, artifact("//:dont-care", "protoc-gen-javalite.exe"));
        ProtoLangToolchainProvider toolchainNoPlugin = /* pluginExecutable= */
        /* runtime= */
        /* blacklistedProtos= */
        ProtoLangToolchainProvider.create("--java_out=param1,param2:$(OUT)", null, Mockito.mock(TransitiveInfoCollection.class), NestedSetBuilder.emptySet(Order.STABLE_ORDER));
        ProtoLangToolchainProvider toolchainWithPlugin = /* runtime= */
        /* blacklistedProtos= */
        ProtoLangToolchainProvider.create("--$(PLUGIN_OUT)=param3,param4:$(OUT)", plugin, Mockito.mock(TransitiveInfoCollection.class), NestedSetBuilder.emptySet(Order.STABLE_ORDER));
        CustomCommandLine cmdLine = /* protocOpts= */
        ProtoCompileActionBuilder.createCommandLineFromToolchains(ImmutableList.of(new com.google.devtools.build.lib.rules.proto.ProtoCompileActionBuilder.ToolchainInvocation("dontcare_because_no_plugin", toolchainNoPlugin, "foo.srcjar"), new com.google.devtools.build.lib.rules.proto.ProtoCompileActionBuilder.ToolchainInvocation("pluginName", toolchainWithPlugin, "bar.srcjar")), "bazel-out", /* directProtos */
        /* transitiveProtos */
        /* transitiveProtoSourceRoots= */
        /* strictImportableProtoSourceRoots= */
        /* strictImportableProtos= */
        /* exportedProtos = */
        protoInfo(ImmutableList.of(artifact("//:dont-care", "source_file.proto")), NestedSetBuilder.create(Order.STABLE_ORDER, artifact("//:dont-care", "import1.proto"), artifact("//:dont-care", "import2.proto")), NestedSetBuilder.emptySet(Order.STABLE_ORDER), NestedSetBuilder.create(STABLE_ORDER, "."), NestedSetBuilder.emptySet(Order.STABLE_ORDER), NestedSetBuilder.emptySet(Order.STABLE_ORDER)), Label.parseAbsoluteUnchecked("//foo:bar"), NON_STRICT, DO_NOT_USE, ALLOW, ImmutableList.of());
        assertThat(cmdLine.arguments()).containsExactly("--java_out=param1,param2:foo.srcjar", "--PLUGIN_pluginName_out=param3,param4:bar.srcjar", "--plugin=protoc-gen-PLUGIN_pluginName=protoc-gen-javalite.exe", "-Iimport1.proto=import1.proto", "-Iimport2.proto=import2.proto", "source_file.proto").inOrder();
    }

    @Test
    public void commandline_derivedArtifact() {
        // Verify that the command line contains the correct path to a generated protocol buffers.
        CustomCommandLine cmdLine = /* toolchainInvocations= */
        /* protocOpts= */
        ProtoCompileActionBuilder.createCommandLineFromToolchains(ImmutableList.of(), "bazel-out", /* directProtos */
        /* transitiveProtos */
        /* transitiveProtoSourceRoots= */
        /* strictImportableProtoSourceRoots= */
        /* strictImportableProtos= */
        /* exportedProtos = */
        protoInfo(ImmutableList.of(derivedArtifact("//:dont-care", "source_file.proto")), NestedSetBuilder.emptySet(Order.STABLE_ORDER), NestedSetBuilder.emptySet(Order.STABLE_ORDER), NestedSetBuilder.create(STABLE_ORDER, "."), NestedSetBuilder.emptySet(Order.STABLE_ORDER), NestedSetBuilder.emptySet(Order.STABLE_ORDER)), Label.parseAbsoluteUnchecked("//foo:bar"), NON_STRICT, DO_NOT_USE, ALLOW, ImmutableList.of());
        assertThat(cmdLine.arguments()).containsExactly("out/source_file.proto");
    }

    @Test
    public void commandLine_strictDeps() throws Exception {
        ProtoLangToolchainProvider toolchain = /* pluginExecutable= */
        /* runtime= */
        /* blacklistedProtos= */
        ProtoLangToolchainProvider.create("--java_out=param1,param2:$(OUT)", null, Mockito.mock(TransitiveInfoCollection.class), NestedSetBuilder.emptySet(Order.STABLE_ORDER));
        CustomCommandLine cmdLine = /* protocOpts= */
        ProtoCompileActionBuilder.createCommandLineFromToolchains(ImmutableList.of(new com.google.devtools.build.lib.rules.proto.ProtoCompileActionBuilder.ToolchainInvocation("dontcare", toolchain, "foo.srcjar")), "bazel-out", /* directProtos */
        /* transitiveProtos */
        /* transitiveProtoSourceRoots= */
        /* strictImportableProtoSourceRoots= */
        /* exportedProtos = */
        protoInfo(ImmutableList.of(artifact("//:dont-care", "source_file.proto")), NestedSetBuilder.create(Order.STABLE_ORDER, artifact("//:dont-care", "import1.proto"), artifact("//:dont-care", "import2.proto")), NestedSetBuilder.emptySet(Order.STABLE_ORDER), NestedSetBuilder.emptySet(Order.STABLE_ORDER), NestedSetBuilder.create(Order.STABLE_ORDER, Pair.of(artifact("//:dont-care", "import1.proto"), "import1.proto")), NestedSetBuilder.emptySet(Order.STABLE_ORDER)), Label.parseAbsoluteUnchecked("//foo:bar"), STRICT, DO_NOT_USE, ALLOW, ImmutableList.of());
        assertThat(cmdLine.arguments()).containsExactly("--java_out=param1,param2:foo.srcjar", "-Iimport1.proto=import1.proto", "-Iimport2.proto=import2.proto", "--direct_dependencies", "import1.proto", String.format(STRICT_DEPS_FLAG_TEMPLATE, "//foo:bar"), "source_file.proto").inOrder();
    }

    @Test
    public void commandLine_exports() throws Exception {
        ProtoLangToolchainProvider toolchain = /* pluginExecutable= */
        /* runtime= */
        /* blacklistedProtos= */
        ProtoLangToolchainProvider.create("--java_out=param1,param2:$(OUT)", null, Mockito.mock(TransitiveInfoCollection.class), NestedSetBuilder.emptySet(Order.STABLE_ORDER));
        CustomCommandLine cmdLine = /* protocOpts= */
        ProtoCompileActionBuilder.createCommandLineFromToolchains(ImmutableList.of(new com.google.devtools.build.lib.rules.proto.ProtoCompileActionBuilder.ToolchainInvocation("dontcare", toolchain, "foo.srcjar")), "bazel-out", /* directProtos */
        /* transitiveProtos */
        /* transitiveProtoSourceRoots= */
        /* strictImportableProtoSourceRoots= */
        /* strictImportableProtos= */
        /* exportedProtos = */
        protoInfo(ImmutableList.of(artifact("//:dont-care", "source_file.proto")), NestedSetBuilder.create(Order.STABLE_ORDER, artifact("//:dont-care", "import1.proto"), artifact("//:dont-care", "import2.proto")), NestedSetBuilder.emptySet(Order.STABLE_ORDER), NestedSetBuilder.emptySet(Order.STABLE_ORDER), NestedSetBuilder.emptySet(Order.STABLE_ORDER), NestedSetBuilder.create(Order.STABLE_ORDER, Pair.of(artifact("//:dont-care", "foo/export1.proto"), "export1.proto"))), Label.parseAbsoluteUnchecked("//foo:bar"), NON_STRICT, USE, ALLOW, ImmutableList.of());
        assertThat(cmdLine.arguments()).containsExactly("--java_out=param1,param2:foo.srcjar", "-Iimport1.proto=import1.proto", "-Iimport2.proto=import2.proto", "--allowed_public_imports", "export1.proto", "source_file.proto").inOrder();
    }

    @Test
    public void otherParameters() throws Exception {
        CustomCommandLine cmdLine = /* protocOpts= */
        ProtoCompileActionBuilder.createCommandLineFromToolchains(ImmutableList.of(), "bazel-out", /* directProtos */
        /* transitiveProtos */
        /* transitiveProtoSourceRoots= */
        /* strictImportableProtoSourceRoots= */
        /* strictImportableProtos */
        /* exportedProtos = */
        protoInfo(ImmutableList.of(), NestedSetBuilder.emptySet(Order.STABLE_ORDER), NestedSetBuilder.emptySet(Order.STABLE_ORDER), NestedSetBuilder.emptySet(Order.STABLE_ORDER), NestedSetBuilder.emptySet(Order.STABLE_ORDER), NestedSetBuilder.emptySet(Order.STABLE_ORDER)), Label.parseAbsoluteUnchecked("//foo:bar"), STRICT, DO_NOT_USE, DISALLOW, ImmutableList.of("--foo", "--bar"));
        assertThat(cmdLine.arguments()).containsAllOf("--disallow_services", "--foo", "--bar");
    }

    @Test
    public void outReplacementAreLazilyEvaluated() throws Exception {
        final boolean[] hasBeenCalled = new boolean[1];
        hasBeenCalled[0] = false;
        CharSequence outReplacement = new LazyString() {
            @Override
            public String toString() {
                hasBeenCalled[0] = true;
                return "mu";
            }
        };
        ProtoLangToolchainProvider toolchain = /* pluginExecutable= */
        /* runtime= */
        /* blacklistedProtos= */
        ProtoLangToolchainProvider.create("--java_out=param1,param2:$(OUT)", null, Mockito.mock(TransitiveInfoCollection.class), NestedSetBuilder.emptySet(Order.STABLE_ORDER));
        CustomCommandLine cmdLine = /* protocOpts= */
        ProtoCompileActionBuilder.createCommandLineFromToolchains(ImmutableList.of(new com.google.devtools.build.lib.rules.proto.ProtoCompileActionBuilder.ToolchainInvocation("pluginName", toolchain, outReplacement)), "bazel-out", /* directProtos */
        /* transitiveProtos */
        /* transitiveProtoSourceRoots= */
        /* strictImportableProtoSourceRoots= */
        /* strictImportableProtos= */
        /* exportedProtos = */
        protoInfo(ImmutableList.of(), NestedSetBuilder.emptySet(Order.STABLE_ORDER), NestedSetBuilder.emptySet(Order.STABLE_ORDER), NestedSetBuilder.emptySet(Order.STABLE_ORDER), NestedSetBuilder.emptySet(Order.STABLE_ORDER), NestedSetBuilder.emptySet(Order.STABLE_ORDER)), Label.parseAbsoluteUnchecked("//foo:bar"), STRICT, DO_NOT_USE, ALLOW, ImmutableList.of());
        assertThat(hasBeenCalled[0]).isFalse();
        cmdLine.arguments();
        assertThat(hasBeenCalled[0]).isTrue();
    }

    /**
     * Tests that if the same invocation-name is specified by more than one invocation,
     * ProtoCompileActionBuilder throws an exception.
     */
    @Test
    public void exceptionIfSameName() throws Exception {
        ProtoLangToolchainProvider toolchain1 = /* pluginExecutable= */
        /* runtime= */
        /* blacklistedProtos= */
        ProtoLangToolchainProvider.create("dontcare", null, Mockito.mock(TransitiveInfoCollection.class), NestedSetBuilder.emptySet(Order.STABLE_ORDER));
        ProtoLangToolchainProvider toolchain2 = /* pluginExecutable= */
        /* runtime= */
        /* blacklistedProtos= */
        ProtoLangToolchainProvider.create("dontcare", null, Mockito.mock(TransitiveInfoCollection.class), NestedSetBuilder.emptySet(Order.STABLE_ORDER));
        try {
            /* protocOpts= */
            ProtoCompileActionBuilder.createCommandLineFromToolchains(ImmutableList.of(new com.google.devtools.build.lib.rules.proto.ProtoCompileActionBuilder.ToolchainInvocation("pluginName", toolchain1, "outReplacement"), new com.google.devtools.build.lib.rules.proto.ProtoCompileActionBuilder.ToolchainInvocation("pluginName", toolchain2, "outReplacement")), "bazel-out", /* directProtos */
            /* transitiveProtos */
            /* transitiveProtoSourceRoots= */
            /* strictImportableProtoSourceRoots= */
            /* strictImportableProtos= */
            /* exportedProtos = */
            protoInfo(ImmutableList.of(), NestedSetBuilder.emptySet(Order.STABLE_ORDER), NestedSetBuilder.emptySet(Order.STABLE_ORDER), NestedSetBuilder.emptySet(Order.STABLE_ORDER), NestedSetBuilder.emptySet(Order.STABLE_ORDER), NestedSetBuilder.emptySet(Order.STABLE_ORDER)), Label.parseAbsoluteUnchecked("//foo:bar"), STRICT, DO_NOT_USE, ALLOW, ImmutableList.of());
            Assert.fail("Expected an exception");
        } catch (IllegalStateException e) {
            assertThat(e).hasMessageThat().isEqualTo(("Invocation name pluginName appears more than once. " + "This could lead to incorrect proto-compiler behavior"));
        }
    }

    @Test
    public void testProtoCommandLineArgv() throws Exception {
        assertThat(/* directDependencies */
        ProtoCompileActionBuilderTest.protoArgv(null, ImmutableList.of(derivedArtifact("//:dont-care", "foo.proto")), ImmutableList.of("."))).containsExactly("-Ifoo.proto=out/foo.proto");
        assertThat(/* directDependencies */
        ProtoCompileActionBuilderTest.protoArgv(ImmutableList.of(), ImmutableList.of(derivedArtifact("//:dont-care", "foo.proto")), ImmutableList.of("."))).containsExactly("-Ifoo.proto=out/foo.proto", "--direct_dependencies=");
        assertThat(/* directDependencies */
        ProtoCompileActionBuilderTest.protoArgv(ImmutableList.of(Pair.of(derivedArtifact("//:dont-care", "foo.proto"), null)), ImmutableList.of(derivedArtifact("//:dont-care", "foo.proto")), ImmutableList.of("."))).containsExactly("-Ifoo.proto=out/foo.proto", "--direct_dependencies", "foo.proto");
        assertThat(/* directDependencies */
        ProtoCompileActionBuilderTest.protoArgv(ImmutableList.of(Pair.of(derivedArtifact("//:dont-care", "foo.proto"), null), Pair.of(derivedArtifact("//:dont-care", "bar.proto"), null)), ImmutableList.of(derivedArtifact("//:dont-care", "foo.proto")), ImmutableList.of("."))).containsExactly("-Ifoo.proto=out/foo.proto", "--direct_dependencies", "foo.proto:bar.proto");
    }

    /**
     * Include-maps are the -Ivirtual=physical arguments passed to proto-compiler. When including a
     * file named 'foo/bar.proto' from an external repository 'bla', the include-map should be
     * -Ifoo/bar.proto=external/bla/foo/bar.proto. That is - 'virtual' should be the path relative to
     * the external repo root, and physical should be the physical file location.
     */
    @Test
    public void testIncludeMapsOfExternalFiles() throws Exception {
        assertThat(/* protosInDirectoDependencies */
        ProtoCompileActionBuilderTest.protoArgv(null, ImmutableList.of(artifact("@bla//foo:bar", "external/bla/foo/bar.proto")), ImmutableList.of("external/bla"))).containsExactly("-Ifoo/bar.proto=external/bla/foo/bar.proto");
    }

    @Test
    public void directDependenciesOnExternalFiles() throws Exception {
        ImmutableList<Artifact> protos = ImmutableList.of(artifact("@bla//foo:bar", "external/bla/foo/bar.proto"));
        ImmutableList<Pair<Artifact, String>> protosImports = ImmutableList.of(Pair.of(artifact("@bla//foo:bar", "external/bla/foo/bar.proto"), null));
        assertThat(ProtoCompileActionBuilderTest.protoArgv(protosImports, protos, ImmutableList.of("external/bla"))).containsExactly("-Ifoo/bar.proto=external/bla/foo/bar.proto", "--direct_dependencies", "foo/bar.proto");
    }
}

