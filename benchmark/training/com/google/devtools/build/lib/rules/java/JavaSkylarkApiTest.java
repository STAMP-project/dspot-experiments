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
package com.google.devtools.build.lib.rules.java;


import JavaInfo.PROVIDER;
import JavaSemantics.JAR;
import JavaSemantics.JAVA_SOURCE;
import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.util.ActionsTestUtil;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.collect.nestedset.NestedSet;
import com.google.devtools.build.lib.packages.SkylarkProvider.SkylarkKey;
import com.google.devtools.build.lib.packages.StructImpl;
import com.google.devtools.build.lib.rules.java.JavaRuleOutputJarsProvider.OutputJar;
import com.google.devtools.build.lib.syntax.SkylarkList;
import com.google.devtools.build.lib.syntax.SkylarkNestedSet;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.TestConstants;
import com.google.devtools.build.lib.util.FileType;
import com.google.devtools.build.lib.vfs.PathFragment;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests Skylark API for Java rules.
 */
@RunWith(JUnit4.class)
public class JavaSkylarkApiTest extends BuildViewTestCase {
    private static final String HOST_JAVA_RUNTIME_LABEL = (TestConstants.TOOLS_REPOSITORY) + "//tools/jdk:current_host_java_runtime";

    @Test
    public void testJavaRuntimeProviderJavaAbsolute() throws Exception {
        scratch.file("a/BUILD", "load(':rule.bzl', 'jrule')", "java_runtime(name='jvm', srcs=[], java_home='/foo/bar')", "java_runtime_alias(name='alias')", "jrule(name='r')");
        scratch.file("a/rule.bzl", "def _impl(ctx):", "  provider = ctx.attr._java_runtime[java_common.JavaRuntimeInfo]", "  return struct(", "    java_home_exec_path = provider.java_home,", "    java_executable_exec_path = provider.java_executable_exec_path,", "    java_home_runfiles_path = provider.java_home_runfiles_path,", "    java_executable_runfiles_path = provider.java_executable_runfiles_path,", "  )", "jrule = rule(_impl, attrs = { '_java_runtime': attr.label(default=Label('//a:alias'))})");
        useConfiguration("--javabase=//a:jvm");
        ConfiguredTarget ct = getConfiguredTarget("//a:r");
        @SuppressWarnings("unchecked")
        PathFragment javaHomeExecPath = ((PathFragment) (ct.get("java_home_exec_path")));
        assertThat(javaHomeExecPath.getPathString()).isEqualTo("/foo/bar");
        @SuppressWarnings("unchecked")
        PathFragment javaExecutableExecPath = ((PathFragment) (ct.get("java_executable_exec_path")));
        assertThat(javaExecutableExecPath.getPathString()).startsWith("/foo/bar/bin/java");
        @SuppressWarnings("unchecked")
        PathFragment javaHomeRunfilesPath = ((PathFragment) (ct.get("java_home_runfiles_path")));
        assertThat(javaHomeRunfilesPath.getPathString()).isEqualTo("/foo/bar");
        @SuppressWarnings("unchecked")
        PathFragment javaExecutableRunfiles = ((PathFragment) (ct.get("java_executable_runfiles_path")));
        assertThat(javaExecutableRunfiles.getPathString()).startsWith("/foo/bar/bin/java");
    }

    @Test
    public void testJavaRuntimeProviderJavaHermetic() throws Exception {
        scratch.file("a/BUILD", "load(':rule.bzl', 'jrule')", "java_runtime(name='jvm', srcs=[], java_home='foo/bar')", "java_runtime_alias(name='alias')", "jrule(name='r')");
        scratch.file("a/rule.bzl", "def _impl(ctx):", "  provider = ctx.attr._java_runtime[java_common.JavaRuntimeInfo]", "  return struct(", "    java_home_exec_path = provider.java_home,", "    java_executable_exec_path = provider.java_executable_exec_path,", "    java_home_runfiles_path = provider.java_home_runfiles_path,", "    java_executable_runfiles_path = provider.java_executable_runfiles_path,", "  )", "jrule = rule(_impl, attrs = { '_java_runtime': attr.label(default=Label('//a:alias'))})");
        useConfiguration("--javabase=//a:jvm");
        ConfiguredTarget ct = getConfiguredTarget("//a:r");
        @SuppressWarnings("unchecked")
        PathFragment javaHomeExecPath = ((PathFragment) (ct.get("java_home_exec_path")));
        assertThat(javaHomeExecPath.getPathString()).isEqualTo("a/foo/bar");
        @SuppressWarnings("unchecked")
        PathFragment javaExecutableExecPath = ((PathFragment) (ct.get("java_executable_exec_path")));
        assertThat(javaExecutableExecPath.getPathString()).startsWith("a/foo/bar/bin/java");
        @SuppressWarnings("unchecked")
        PathFragment javaHomeRunfilesPath = ((PathFragment) (ct.get("java_home_runfiles_path")));
        assertThat(javaHomeRunfilesPath.getPathString()).isEqualTo("a/foo/bar");
        @SuppressWarnings("unchecked")
        PathFragment javaExecutableRunfiles = ((PathFragment) (ct.get("java_executable_runfiles_path")));
        assertThat(javaExecutableRunfiles.getPathString()).startsWith("a/foo/bar/bin/java");
    }

    @Test
    public void testJavaRuntimeProviderJavaGenerated() throws Exception {
        scratch.file("a/BUILD", "load(':rule.bzl', 'jrule')", "genrule(name='gen', cmd='', outs=['foo/bar/bin/java'])", "java_runtime(name='jvm', srcs=[], java='foo/bar/bin/java')", "java_runtime_alias(name='alias')", "jrule(name='r')");
        scratch.file("a/rule.bzl", "def _impl(ctx):", "  provider = ctx.attr._java_runtime[java_common.JavaRuntimeInfo]", "  return struct(", "    java_home_exec_path = provider.java_home,", "    java_executable_exec_path = provider.java_executable_exec_path,", "    java_home_runfiles_path = provider.java_home_runfiles_path,", "    java_executable_runfiles_path = provider.java_executable_runfiles_path,", "  )", "jrule = rule(_impl, attrs = { '_java_runtime': attr.label(default=Label('//a:alias'))})");
        useConfiguration("--javabase=//a:jvm");
        ConfiguredTarget ct = getConfiguredTarget("//a:r");
        @SuppressWarnings("unchecked")
        PathFragment javaHomeExecPath = ((PathFragment) (ct.get("java_home_exec_path")));
        assertThat(javaHomeExecPath.getPathString()).isEqualTo(getGenfilesArtifactWithNoOwner("a/foo/bar").getExecPathString());
        @SuppressWarnings("unchecked")
        PathFragment javaExecutableExecPath = ((PathFragment) (ct.get("java_executable_exec_path")));
        assertThat(javaExecutableExecPath.getPathString()).startsWith(getGenfilesArtifactWithNoOwner("a/foo/bar/bin/java").getExecPathString());
        @SuppressWarnings("unchecked")
        PathFragment javaHomeRunfilesPath = ((PathFragment) (ct.get("java_home_runfiles_path")));
        assertThat(javaHomeRunfilesPath.getPathString()).isEqualTo("a/foo/bar");
        @SuppressWarnings("unchecked")
        PathFragment javaExecutableRunfiles = ((PathFragment) (ct.get("java_executable_runfiles_path")));
        assertThat(javaExecutableRunfiles.getPathString()).startsWith("a/foo/bar/bin/java");
    }

    @Test
    public void testInvalidHostJavabase() throws Exception {
        writeBuildFileForJavaToolchain();
        scratch.file("a/BUILD", "load(':rule.bzl', 'jrule')", "filegroup(name='fg')", "jrule(name='r', srcs=['S.java'])");
        scratch.file("a/rule.bzl", "def _impl(ctx):", "  output_jar = ctx.actions.declare_file('lib' + ctx.label.name + '.jar')", "  java_common.compile(", "    ctx,", "    source_files = ctx.files.srcs,", "    output = output_jar,", "    java_toolchain = ctx.attr._java_toolchain[java_common.JavaToolchainInfo],", "    host_javabase = ctx.attr._host_javabase", "  )", "  return struct()", "jrule = rule(", "  implementation = _impl,", "  outputs = {", "    'my_output': 'lib%{name}.jar'", "  },", "  attrs = {", "    'srcs': attr.label_list(allow_files=['.java']),", "    '_java_toolchain': attr.label(default = Label('//java/com/google/test:toolchain')),", "    '_host_javabase': attr.label(default = Label('//a:fg'))", "  },", "  fragments = ['java'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        getConfiguredTarget("//a:r");
        assertContainsEvent("must point to a Java runtime");
    }

    @Test
    public void testExposesJavaCommonProvider() throws Exception {
        scratch.file("java/test/BUILD", "load(':extension.bzl', 'my_rule')", "java_library(", "  name = 'dep',", "  srcs = [ 'Dep.java'],", ")", "my_rule(", "  name = 'my',", "  dep = ':dep',", ")");
        scratch.file("java/test/extension.bzl", "result = provider()", "def impl(ctx):", "   depj = ctx.attr.dep[java_common.provider]", "   return [result(", "             transitive_runtime_jars = depj.transitive_runtime_jars,", "             transitive_compile_time_jars = depj.transitive_compile_time_jars,", "             compile_jars = depj.compile_jars,", "             full_compile_jars = depj.full_compile_jars,", "             source_jars = depj.source_jars,", "             outputs = depj.outputs,", "          )]", "my_rule = rule(impl, attrs = { 'dep' : attr.label() })");
        ConfiguredTarget configuredTarget = getConfiguredTarget("//java/test:my");
        StructImpl info = ((StructImpl) (configuredTarget.get(new SkylarkKey(Label.parseAbsolute("//java/test:extension.bzl", ImmutableMap.of()), "result"))));
        SkylarkNestedSet transitiveRuntimeJars = ((SkylarkNestedSet) (info.getValue("transitive_runtime_jars")));
        SkylarkNestedSet transitiveCompileTimeJars = ((SkylarkNestedSet) (info.getValue("transitive_compile_time_jars")));
        SkylarkNestedSet compileJars = ((SkylarkNestedSet) (info.getValue("compile_jars")));
        SkylarkNestedSet fullCompileJars = ((SkylarkNestedSet) (info.getValue("full_compile_jars")));
        SkylarkList<Artifact> sourceJars = ((SkylarkList<Artifact>) (info.getValue("source_jars")));
        JavaRuleOutputJarsProvider outputs = ((JavaRuleOutputJarsProvider) (info.getValue("outputs")));
        assertThat(JavaSkylarkApiTest.artifactFilesNames(transitiveRuntimeJars.toCollection(Artifact.class))).containsExactly("libdep.jar");
        assertThat(JavaSkylarkApiTest.artifactFilesNames(transitiveCompileTimeJars.toCollection(Artifact.class))).containsExactly("libdep-hjar.jar");
        assertThat(transitiveCompileTimeJars.toCollection()).containsExactlyElementsIn(compileJars.toCollection());
        assertThat(JavaSkylarkApiTest.artifactFilesNames(fullCompileJars.toCollection(Artifact.class))).containsExactly("libdep.jar");
        assertThat(JavaSkylarkApiTest.artifactFilesNames(sourceJars)).containsExactly("libdep-src.jar");
        assertThat(outputs.getOutputJars()).hasSize(1);
        OutputJar output = outputs.getOutputJars().get(0);
        assertThat(output.getClassJar().getFilename()).isEqualTo("libdep.jar");
        assertThat(output.getIJar().getFilename()).isEqualTo("libdep-hjar.jar");
        assertThat(JavaSkylarkApiTest.artifactFilesNames(output.getSrcJars())).containsExactly("libdep-src.jar");
        assertThat(outputs.getJdeps().getFilename()).isEqualTo("libdep.jdeps");
    }

    @Test
    public void testJavaCommonCompileExposesOutputJarProvider() throws Exception {
        writeBuildFileForJavaToolchain();
        scratch.file("java/test/B.jar");
        scratch.file("java/test/BUILD", "load(':extension.bzl', 'my_rule')", "load(':custom_rule.bzl', 'java_custom_library')", "java_custom_library(", "name = 'dep',", "srcs = ['Main.java'],", "sourcepath = [':B.jar']", ")", "my_rule(", "  name = 'my',", "  dep = ':dep',", ")");
        scratch.file("java/test/extension.bzl", "result = provider()", "def impl(ctx):", "   depj = ctx.attr.dep[java_common.provider]", "   return [result(", "             transitive_runtime_jars = depj.transitive_runtime_jars,", "             transitive_compile_time_jars = depj.transitive_compile_time_jars,", "             compile_jars = depj.compile_jars,", "             full_compile_jars = depj.full_compile_jars,", "             source_jars = depj.source_jars,", "             outputs = depj.outputs,", "          )]", "my_rule = rule(impl, attrs = { 'dep' : attr.label() })");
        scratch.file("java/test/custom_rule.bzl", "def _impl(ctx):", "  output_jar = ctx.actions.declare_file('lib' + ctx.label.name + '.jar')", "  compilation_provider = java_common.compile(", "    ctx,", "    source_files = ctx.files.srcs,", "    output = output_jar,", "    deps = [],", "    sourcepath = ctx.files.sourcepath,", "    strict_deps = 'ERROR',", "    java_toolchain = ctx.attr._java_toolchain[java_common.JavaToolchainInfo],", "    host_javabase = ctx.attr._host_javabase[java_common.JavaRuntimeInfo]", "  )", "  return struct(", "    files = depset([output_jar]),", "    providers = [compilation_provider]", "  )", "java_custom_library = rule(", "  implementation = _impl,", "  outputs = {", "    'my_output': 'lib%{name}.jar'", "  },", "  attrs = {", "    'srcs': attr.label_list(allow_files=['.java']),", "    'sourcepath': attr.label_list(allow_files=['.jar']),", "    '_java_toolchain': attr.label(default = Label('//java/com/google/test:toolchain')),", "    '_host_javabase': attr.label(", (("        default = Label('" + (JavaSkylarkApiTest.HOST_JAVA_RUNTIME_LABEL)) + "'))"), "  },", "  fragments = ['java']", ")");
        ConfiguredTarget configuredTarget = getConfiguredTarget("//java/test:my");
        StructImpl info = ((StructImpl) (configuredTarget.get(new SkylarkKey(Label.parseAbsolute("//java/test:extension.bzl", ImmutableMap.of()), "result"))));
        JavaRuleOutputJarsProvider outputs = ((JavaRuleOutputJarsProvider) (info.getValue("outputs")));
        assertThat(outputs.getOutputJars()).hasSize(1);
        OutputJar outputJar = outputs.getOutputJars().get(0);
        assertThat(outputJar.getClassJar().getFilename()).isEqualTo("libdep.jar");
        assertThat(outputJar.getIJar().getFilename()).isEqualTo("libdep-hjar.jar");
        assertThat(ActionsTestUtil.prettyArtifactNames(outputJar.getSrcJars())).containsExactly("java/test/libdep-src.jar");
        assertThat(outputs.getJdeps().getFilename()).isEqualTo("libdep.jdeps");
        assertThat(outputs.getNativeHeaders().getFilename()).isEqualTo("libdep-native-header.jar");
    }

    /**
     * Tests that JavaInfo.java_annotation_processing returned from java_common.compile looks as
     * expected, and specifically, looks as if java_library was used instead.
     */
    @Test
    public void testJavaCommonCompileExposesAnnotationProcessingInfo() throws Exception {
        // Set up a Skylark rule that uses java_common.compile and supports annotation processing in
        // the same way as java_library, then use a helper method to test that the custom rule produces
        // the same annotation processing information as java_library would.
        writeBuildFileForJavaToolchain();
        scratch.file("java/test/custom_rule.bzl", "def _impl(ctx):", "  output_jar = ctx.actions.declare_file('lib' + ctx.label.name + '.jar')", "  return java_common.compile(", "    ctx,", "    source_files = ctx.files.srcs,", "    deps = [d[JavaInfo] for d in ctx.attr.deps],", "    exports = [e[JavaInfo] for e in ctx.attr.exports],", "    plugins = [p[JavaInfo] for p in ctx.attr.plugins],", "    output = output_jar,", "    java_toolchain = ctx.attr._java_toolchain[java_common.JavaToolchainInfo],", "    host_javabase = ctx.attr._host_javabase[java_common.JavaRuntimeInfo]", "  )", "java_custom_library = rule(", "  implementation = _impl,", "  outputs = {", "    'my_output': 'lib%{name}.jar'", "  },", "  attrs = {", "    'srcs': attr.label_list(allow_files=['.java']),", "    'deps': attr.label_list(),", "    'exports': attr.label_list(),", "    'plugins': attr.label_list(),", "    '_java_toolchain': attr.label(default = Label('//java/com/google/test:toolchain')),", "    '_host_javabase': attr.label(", (("        default = Label('" + (JavaSkylarkApiTest.HOST_JAVA_RUNTIME_LABEL)) + "'))"), "  },", "  fragments = ['java']", ")");
        /* toBeProcessedRuleName= */
        /* extraLoad= */
        testAnnotationProcessingInfoIsSkylarkAccessible("java_custom_library", "load(':custom_rule.bzl', 'java_custom_library')");
    }

    @Test
    public void testJavaCommonCompileCompilationInfo() throws Exception {
        writeBuildFileForJavaToolchain();
        scratch.file("java/test/BUILD", "load(':custom_rule.bzl', 'java_custom_library')", "java_custom_library(", "  name = 'custom',", "  srcs = ['Main.java'],", "  deps = [':dep']", ")", "java_library(", "  name = 'dep',", "  srcs = [ 'Dep.java'],", ")");
        scratch.file("java/test/custom_rule.bzl", "def _impl(ctx):", "  output_jar = ctx.actions.declare_file('lib' + ctx.label.name + '.jar')", "  deps = [dep[java_common.provider] for dep in ctx.attr.deps]", "  compilation_provider = java_common.compile(", "    ctx,", "    source_files = ctx.files.srcs,", "    output = output_jar,", "    deps = deps,", "    java_toolchain = ctx.attr._java_toolchain[java_common.JavaToolchainInfo],", "    host_javabase = ctx.attr._host_javabase[java_common.JavaRuntimeInfo],", "    javac_opts = ['-XDone -XDtwo'],", "  )", "  return struct(", "    files = depset([output_jar] + compilation_provider.source_jars),", "    providers = [compilation_provider]", "  )", "java_custom_library = rule(", "  implementation = _impl,", "  outputs = {", "    'my_output': 'lib%{name}.jar',", "    'my_src_output': 'lib%{name}-src.jar'", "  },", "  attrs = {", "    'srcs': attr.label_list(allow_files=['.java']),", "    'deps': attr.label_list(),", "    '_java_toolchain': attr.label(default = Label('//java/com/google/test:toolchain')),", "    '_host_javabase': attr.label(", (("        default = Label('" + (JavaSkylarkApiTest.HOST_JAVA_RUNTIME_LABEL)) + "'))"), "  },", "  fragments = ['java']", ")");
        ConfiguredTarget configuredTarget = getConfiguredTarget("//java/test:custom");
        JavaInfo info = configuredTarget.get(PROVIDER);
        JavaCompilationInfoProvider compilationInfo = info.getCompilationInfoProvider();
        assertThat(ActionsTestUtil.prettyArtifactNames(compilationInfo.getCompilationClasspath().toList())).containsExactly("java/test/libdep-hjar.jar");
        assertThat(ActionsTestUtil.prettyArtifactNames(compilationInfo.getRuntimeClasspath().toList())).containsExactly("java/test/libdep.jar", "java/test/libcustom.jar");
        assertThat(compilationInfo.getJavacOpts()).contains("-XDone");
    }

    @Test
    public void testJavaCommonCompileTransitiveSourceJars() throws Exception {
        writeBuildFileForJavaToolchain();
        scratch.file("java/test/BUILD", "load(':custom_rule.bzl', 'java_custom_library')", "java_custom_library(", "  name = 'custom',", "  srcs = ['Main.java'],", "  deps = [':dep']", ")", "java_library(", "  name = 'dep',", "  srcs = [ 'Dep.java'],", ")");
        scratch.file("java/test/custom_rule.bzl", "def _impl(ctx):", "  output_jar = ctx.actions.declare_file('lib' + ctx.label.name + '.jar')", "  deps = [dep[java_common.provider] for dep in ctx.attr.deps]", "  compilation_provider = java_common.compile(", "    ctx,", "    source_files = ctx.files.srcs,", "    output = output_jar,", "    deps = deps,", "    java_toolchain = ctx.attr._java_toolchain[java_common.JavaToolchainInfo],", "    host_javabase = ctx.attr._host_javabase[java_common.JavaRuntimeInfo]", "  )", "  return struct(", "    files = depset([output_jar] + compilation_provider.source_jars),", "    providers = [compilation_provider]", "  )", "java_custom_library = rule(", "  implementation = _impl,", "  outputs = {", "    'my_output': 'lib%{name}.jar',", "    'my_src_output': 'lib%{name}-src.jar'", "  },", "  attrs = {", "    'srcs': attr.label_list(allow_files=['.java']),", "    'deps': attr.label_list(),", "    '_java_toolchain': attr.label(default = Label('//java/com/google/test:toolchain')),", "    '_host_javabase': attr.label(", (("        default = Label('" + (JavaSkylarkApiTest.HOST_JAVA_RUNTIME_LABEL)) + "'))"), "  },", "  fragments = ['java']", ")");
        ConfiguredTarget configuredTarget = getConfiguredTarget("//java/test:custom");
        JavaInfo info = configuredTarget.get(PROVIDER);
        SkylarkList<Artifact> sourceJars = info.getSourceJars();
        NestedSet<Artifact> transitiveSourceJars = info.getTransitiveSourceJars();
        assertThat(JavaSkylarkApiTest.artifactFilesNames(sourceJars)).containsExactly("libcustom-src.jar");
        assertThat(JavaSkylarkApiTest.artifactFilesNames(transitiveSourceJars)).containsExactly("libdep-src.jar", "libcustom-src.jar");
        assertThat(getGeneratingAction(configuredTarget, "java/test/libcustom-src.jar")).isNotNull();
    }

    @Test
    public void testJavaCommonCompileSourceJarName() throws Exception {
        writeBuildFileForJavaToolchain();
        scratch.file("java/test/BUILD", "load(':custom_rule.bzl', 'java_custom_library')", "java_custom_library(", "  name = 'custom',", "  srcs = ['Main.java'],", "  deps = [':dep']", ")", "java_library(", "  name = 'dep',", "  srcs = [ 'Dep.java'],", ")");
        scratch.file("java/test/custom_rule.bzl", "def _impl(ctx):", "  output_jar = ctx.actions.declare_file('amazing.jar')", "  other_output_jar = ctx.actions.declare_file('wonderful.jar')", "  deps = [dep[java_common.provider] for dep in ctx.attr.deps]", "  compilation_provider = java_common.compile(", "    ctx,", "    source_files = ctx.files.srcs,", "    output = output_jar,", "    deps = deps,", "    java_toolchain = ctx.attr._java_toolchain[java_common.JavaToolchainInfo],", "    host_javabase = ctx.attr._host_javabase[java_common.JavaRuntimeInfo]", "  )", "  other_compilation_provider = java_common.compile(", "    ctx,", "    source_files = ctx.files.srcs,", "    output = other_output_jar,", "    deps = deps,", "    java_toolchain = ctx.attr._java_toolchain[java_common.JavaToolchainInfo],", "    host_javabase = ctx.attr._host_javabase[java_common.JavaRuntimeInfo]", "  )", "  result_provider = java_common.merge([compilation_provider, other_compilation_provider])", "  return struct(", "    files = depset([output_jar]),", "    providers = [result_provider]", "  )", "java_custom_library = rule(", "  implementation = _impl,", "  outputs = {", "    'my_output': 'amazing.jar',", "    'my_second_output': 'wonderful.jar'", "  },", "  attrs = {", "    'srcs': attr.label_list(allow_files=['.java']),", "    'deps': attr.label_list(),", "    '_java_toolchain': attr.label(default = Label('//java/com/google/test:toolchain')),", "    '_host_javabase': attr.label(", (("        default = Label('" + (JavaSkylarkApiTest.HOST_JAVA_RUNTIME_LABEL)) + "'))"), "  },", "  fragments = ['java']", ")");
        ConfiguredTarget configuredTarget = getConfiguredTarget("//java/test:custom");
        JavaInfo info = configuredTarget.get(PROVIDER);
        SkylarkList<Artifact> sourceJars = info.getSourceJars();
        NestedSet<Artifact> transitiveSourceJars = info.getTransitiveSourceJars();
        assertThat(JavaSkylarkApiTest.artifactFilesNames(sourceJars)).containsExactly("amazing-src.jar", "wonderful-src.jar");
        assertThat(JavaSkylarkApiTest.artifactFilesNames(transitiveSourceJars)).containsExactly("libdep-src.jar", "amazing-src.jar", "wonderful-src.jar");
    }

    @Test
    public void testJavaCommonCompileWithOnlyOneSourceJar() throws Exception {
        writeBuildFileForJavaToolchain();
        scratch.file("java/test/BUILD", "load(':custom_rule.bzl', 'java_custom_library')", "java_custom_library(", "  name = 'custom',", "  srcs = ['myjar-src.jar'],", ")");
        scratch.file("java/test/custom_rule.bzl", "def _impl(ctx):", "  output_jar = ctx.actions.declare_file('lib' + ctx.label.name + '.jar')", "  compilation_provider = java_common.compile(", "    ctx,", "    source_jars = ctx.files.srcs,", "    output = output_jar,", "    java_toolchain = ctx.attr._java_toolchain[java_common.JavaToolchainInfo],", "    host_javabase = ctx.attr._host_javabase[java_common.JavaRuntimeInfo]", "  )", "  return struct(", "    files = depset([output_jar]),", "    providers = [compilation_provider]", "  )", "java_custom_library = rule(", "  implementation = _impl,", "  outputs = {", "    'my_output': 'lib%{name}.jar'", "  },", "  attrs = {", "    'srcs': attr.label_list(allow_files=['.jar']),", "    '_java_toolchain': attr.label(default = Label('//java/com/google/test:toolchain')),", "    '_host_javabase': attr.label(", (("        default = Label('" + (JavaSkylarkApiTest.HOST_JAVA_RUNTIME_LABEL)) + "'))"), "  },", "  fragments = ['java']", ")");
        ConfiguredTarget configuredTarget = getConfiguredTarget("//java/test:custom");
        JavaInfo info = configuredTarget.get(PROVIDER);
        SkylarkList<Artifact> sourceJars = info.getSourceJars();
        assertThat(JavaSkylarkApiTest.artifactFilesNames(sourceJars)).containsExactly("libcustom-src.jar");
        JavaRuleOutputJarsProvider outputJars = info.getOutputJars();
        assertThat(outputJars.getOutputJars()).hasSize(1);
        OutputJar outputJar = outputJars.getOutputJars().get(0);
        assertThat(outputJar.getClassJar().getFilename()).isEqualTo("libcustom.jar");
        assertThat(outputJar.getSrcJar().getFilename()).isEqualTo("libcustom-src.jar");
        assertThat(outputJar.getIJar().getFilename()).isEqualTo("libcustom-hjar.jar");
        assertThat(outputJars.getJdeps().getFilename()).isEqualTo("libcustom.jdeps");
    }

    @Test
    public void testJavaCommonCompileWithOnlyOneSourceJarWithIncompatibleFlag() throws Exception {
        writeBuildFileForJavaToolchain();
        scratch.file("java/test/BUILD", "load(':custom_rule.bzl', 'java_custom_library')", "java_custom_library(", "  name = 'custom',", "  srcs = ['myjar-src.jar'],", ")");
        scratch.file("java/test/custom_rule.bzl", "def _impl(ctx):", "  output_jar = ctx.actions.declare_file('lib' + ctx.label.name + '.jar')", "  compilation_provider = java_common.compile(", "    ctx,", "    source_jars = ctx.files.srcs,", "    output = output_jar,", "    java_toolchain = ctx.attr._java_toolchain[java_common.JavaToolchainInfo],", "    host_javabase = ctx.attr._host_javabase[java_common.JavaRuntimeInfo]", "  )", "  return struct(", "    files = depset([output_jar]),", "    providers = [compilation_provider]", "  )", "java_custom_library = rule(", "  implementation = _impl,", "  outputs = {", "    'my_output': 'lib%{name}.jar'", "  },", "  attrs = {", "    'srcs': attr.label_list(allow_files=['.jar']),", "    '_java_toolchain': attr.label(default = Label('//java/com/google/test:toolchain')),", "    '_host_javabase': attr.label(", (("        default = Label('" + (JavaSkylarkApiTest.HOST_JAVA_RUNTIME_LABEL)) + "'))"), "  },", "  fragments = ['java']", ")");
        ConfiguredTarget configuredTarget = getConfiguredTarget("//java/test:custom");
        JavaInfo info = configuredTarget.get(PROVIDER);
        SkylarkList<Artifact> sourceJars = info.getSourceJars();
        assertThat(JavaSkylarkApiTest.artifactFilesNames(sourceJars)).containsExactly("libcustom-src.jar");
        JavaRuleOutputJarsProvider outputJars = info.getOutputJars();
        assertThat(outputJars.getOutputJars()).hasSize(1);
        OutputJar outputJar = outputJars.getOutputJars().get(0);
        assertThat(outputJar.getClassJar().getFilename()).isEqualTo("libcustom.jar");
        assertThat(outputJar.getSrcJar().getFilename()).isEqualTo("libcustom-src.jar");
        assertThat(outputJar.getIJar().getFilename()).isEqualTo("libcustom-hjar.jar");
        assertThat(outputJars.getJdeps().getFilename()).isEqualTo("libcustom.jdeps");
    }

    @Test
    public void testJavaCommonCompileCustomSourceJar() throws Exception {
        writeBuildFileForJavaToolchain();
        scratch.file("java/test/BUILD", "load(':custom_rule.bzl', 'java_custom_library')", "java_custom_library(", "  name = 'custom',", "  srcs = ['myjar-src.jar'],", ")");
        scratch.file("java/test/custom_rule.bzl", "def _impl(ctx):", "  output_jar = ctx.actions.declare_file('lib' + ctx.label.name + '.jar')", "  output_source_jar = ctx.actions.declare_file('lib' + ctx.label.name + '-mysrc.jar')", "  compilation_provider = java_common.compile(", "    ctx,", "    source_jars = ctx.files.srcs,", "    output = output_jar,", "    output_source_jar = output_source_jar,", "    java_toolchain = ctx.attr._java_toolchain[java_common.JavaToolchainInfo],", "    host_javabase = ctx.attr._host_javabase[java_common.JavaRuntimeInfo]", "  )", "  return struct(", "    files = depset([output_source_jar]),", "    providers = [compilation_provider]", "  )", "java_custom_library = rule(", "  implementation = _impl,", "  outputs = {", "    'my_output': 'lib%{name}.jar'", "  },", "  attrs = {", "    'srcs': attr.label_list(allow_files=['.jar']),", "    '_java_toolchain': attr.label(default = Label('//java/com/google/test:toolchain')),", "    '_host_javabase': attr.label(", (("        default = Label('" + (JavaSkylarkApiTest.HOST_JAVA_RUNTIME_LABEL)) + "'))"), "  },", "  fragments = ['java']", ")");
        ConfiguredTarget configuredTarget = getConfiguredTarget("//java/test:custom");
        JavaInfo info = configuredTarget.get(PROVIDER);
        SkylarkList<Artifact> sourceJars = info.getSourceJars();
        assertThat(JavaSkylarkApiTest.artifactFilesNames(sourceJars)).containsExactly("libcustom-mysrc.jar");
        JavaRuleOutputJarsProvider outputJars = info.getOutputJars();
        assertThat(outputJars.getOutputJars()).hasSize(1);
        OutputJar outputJar = outputJars.getOutputJars().get(0);
        assertThat(outputJar.getClassJar().getFilename()).isEqualTo("libcustom.jar");
        assertThat(outputJar.getSrcJar().getFilename()).isEqualTo("libcustom-mysrc.jar");
        assertThat(outputJar.getIJar().getFilename()).isEqualTo("libcustom-hjar.jar");
        assertThat(outputJars.getJdeps().getFilename()).isEqualTo("libcustom.jdeps");
    }

    @Test
    public void testJavaCommonCompileWithNoSources() throws Exception {
        writeBuildFileForJavaToolchain();
        scratch.file("java/test/BUILD", "load(':custom_rule.bzl', 'java_custom_library')", "java_custom_library(", "  name = 'custom',", ")");
        scratch.file("java/test/custom_rule.bzl", "def _impl(ctx):", "  output_jar = ctx.actions.declare_file('lib' + ctx.label.name + '.jar')", "  compilation_provider = java_common.compile(", "    ctx,", "    output = output_jar,", "    java_toolchain = ctx.attr._java_toolchain[java_common.JavaToolchainInfo],", "    host_javabase = ctx.attr._host_javabase[java_common.JavaRuntimeInfo]", "  )", "  return struct(", "    files = depset([output_jar]),", "    providers = [compilation_provider]", "  )", "java_custom_library = rule(", "  implementation = _impl,", "  outputs = {", "    'my_output': 'lib%{name}.jar'", "  },", "  attrs = {", "    '_java_toolchain': attr.label(default = Label('//java/com/google/test:toolchain')),", "    '_host_javabase': attr.label(", (("        default = Label('" + (JavaSkylarkApiTest.HOST_JAVA_RUNTIME_LABEL)) + "'))"), "  },", "  fragments = ['java']", ")");
        try {
            getConfiguredTarget("//java/test:custom");
        } catch (AssertionError e) {
            assertThat(e).hasMessageThat().contains("source_jars, sources, exports and exported_plugins cannot be simultaneously empty");
        }
    }

    @Test
    public void testJavaCommonCompileWithOnlyExportedPlugins() throws Exception {
        writeBuildFileForJavaToolchain();
        scratch.file("java/test/BUILD", "load(':custom_rule.bzl', 'java_custom_library')", "java_library(name = 'plugin_dep',", "    srcs = [ 'ProcessorDep.java'])", "java_plugin(name = 'plugin',", "    srcs = ['AnnotationProcessor.java'],", "    processor_class = 'com.google.process.stuff',", "    deps = [ ':plugin_dep' ])", "java_custom_library(", "  name = 'custom',", "  exported_plugins = [':plugin'],", ")");
        scratch.file("java/test/custom_rule.bzl", "def _impl(ctx):", "  output_jar = ctx.actions.declare_file('lib' + ctx.label.name + '.jar')", "  compilation_provider = java_common.compile(", "    ctx,", "    output = output_jar,", "    exported_plugins = [p[JavaInfo] for p in ctx.attr.exported_plugins],", "    java_toolchain = ctx.attr._java_toolchain[java_common.JavaToolchainInfo],", "    host_javabase = ctx.attr._host_javabase[java_common.JavaRuntimeInfo]", "  )", "  return [DefaultInfo(files=depset([output_jar])), compilation_provider]", "java_custom_library = rule(", "  implementation = _impl,", "  outputs = {", "    'my_output': 'lib%{name}.jar'", "  },", "  attrs = {", "    'exported_plugins': attr.label_list(),", "    '_java_toolchain': attr.label(default = Label('//java/com/google/test:toolchain')),", "    '_host_javabase': attr.label(", (("        default = Label('" + (JavaSkylarkApiTest.HOST_JAVA_RUNTIME_LABEL)) + "'))"), "  },", "  fragments = ['java']", ")");
        try {
            getConfiguredTarget("//java/test:custom");
        } catch (AssertionError e) {
            assertThat(e).hasMessageThat().contains("source_jars, sources, exports and exported_plugins cannot be simultaneously empty");
        }
    }

    @Test
    public void testJavaInfoWithNoSources() throws Exception {
        writeBuildFileForJavaToolchain();
        scratch.file("java/test/lib.jar");
        scratch.file("java/test/BUILD", "load(':custom_rule.bzl', 'java_custom_library')", "java_custom_library(", "  name = 'custom',", "  jar = 'lib.jar',", ")");
        scratch.file("java/test/custom_rule.bzl", "def _impl(ctx):", "  jar = ctx.file.jar", "  new = JavaInfo(output_jar = jar, use_ijar = False)", "  old = java_common.create_provider(", "      compile_time_jars = [jar],", "      transitive_compile_time_jars = [jar],", "      runtime_jars = [jar],", "      use_ijar = False,", "  )", "  java_info = java_common.merge([old, new])", "  return struct(providers = [java_info])", "java_custom_library = rule(", "  implementation = _impl,", "  attrs = {", "    'jar': attr.label(allow_files = True, single_file = True),", "    '_java_toolchain': attr.label(default = Label('//java/com/google/test:toolchain')),", "    '_host_javabase': attr.label(", (("        default = Label('" + (JavaSkylarkApiTest.HOST_JAVA_RUNTIME_LABEL)) + "'))"), "  },", "  fragments = ['java']", ")");
        JavaCompilationArgsProvider provider = JavaInfo.getProvider(JavaCompilationArgsProvider.class, getConfiguredTarget("//java/test:custom"));
        assertThat(ActionsTestUtil.prettyArtifactNames(provider.getDirectCompileTimeJars())).containsExactly("java/test/lib.jar");
    }

    @Test
    public void testExposesJavaSkylarkApiProvider() throws Exception {
        scratch.file("java/test/BUILD", "load(':extension.bzl', 'my_rule')", "java_library(", "  name = 'dep',", "  srcs = [ 'Dep.java'],", ")", "my_rule(", "  name = 'my',", "  dep = ':dep',", ")");
        scratch.file("java/test/extension.bzl", "result = provider()", "def impl(ctx):", "   depj = ctx.attr.dep.java", "   return [result(", "             source_jars = depj.source_jars,", "             transitive_deps = depj.transitive_deps,", "             transitive_runtime_deps = depj.transitive_runtime_deps,", "             transitive_source_jars = depj.transitive_source_jars,", "             outputs = depj.outputs.jars,", "          )]", "my_rule = rule(impl, attrs = { 'dep' : attr.label() })");
        ConfiguredTarget configuredTarget = getConfiguredTarget("//java/test:my");
        StructImpl info = ((StructImpl) (configuredTarget.get(new SkylarkKey(Label.parseAbsolute("//java/test:extension.bzl", ImmutableMap.of()), "result"))));
        SkylarkNestedSet sourceJars = ((SkylarkNestedSet) (info.getValue("source_jars")));
        SkylarkNestedSet transitiveDeps = ((SkylarkNestedSet) (info.getValue("transitive_deps")));
        SkylarkNestedSet transitiveRuntimeDeps = ((SkylarkNestedSet) (info.getValue("transitive_runtime_deps")));
        SkylarkNestedSet transitiveSourceJars = ((SkylarkNestedSet) (info.getValue("transitive_source_jars")));
        SkylarkList<OutputJar> outputJars = ((SkylarkList<OutputJar>) (info.getValue("outputs")));
        assertThat(JavaSkylarkApiTest.artifactFilesNames(sourceJars.toCollection(Artifact.class))).containsExactly("libdep-src.jar");
        assertThat(JavaSkylarkApiTest.artifactFilesNames(transitiveDeps.toCollection(Artifact.class))).containsExactly("libdep-hjar.jar");
        assertThat(JavaSkylarkApiTest.artifactFilesNames(transitiveRuntimeDeps.toCollection(Artifact.class))).containsExactly("libdep.jar");
        assertThat(JavaSkylarkApiTest.artifactFilesNames(transitiveSourceJars.toCollection(Artifact.class))).containsExactly("libdep-src.jar");
        assertThat(outputJars).hasSize(1);
        assertThat(outputJars.get(0).getClassJar().getFilename()).isEqualTo("libdep.jar");
    }

    /**
     * Tests that a java_library exposes java_processing_info as expected when annotation processing
     * is used.
     */
    @Test
    public void testJavaPlugin() throws Exception {
        /* toBeProcessedRuleName= */
        /* extraLoad= */
        testAnnotationProcessingInfoIsSkylarkAccessible("java_library", "");
    }

    @Test
    public void testJavaProviderFieldsAreSkylarkAccessible() throws Exception {
        // The Skylark evaluation itself will test that compile_jars and
        // transitive_runtime_jars are returning a list readable by Skylark with
        // the expected number of entries.
        scratch.file("java/test/extension.bzl", "result = provider()", "def impl(ctx):", "   java_provider = ctx.attr.dep[JavaInfo]", "   return [result(", "             compile_jars = java_provider.compile_jars,", "             transitive_runtime_jars = java_provider.transitive_runtime_jars,", "             transitive_compile_time_jars = java_provider.transitive_compile_time_jars,", "          )]", "my_rule = rule(impl, attrs = { ", "  'dep' : attr.label(), ", "  'cnt_cjar' : attr.int(), ", "  'cnt_rjar' : attr.int(), ", "})");
        scratch.file("java/test/BUILD", "load(':extension.bzl', 'my_rule')", "java_library(name = 'parent',", "    srcs = [ 'Parent.java'])", "java_library(name = 'jl',", "    srcs = ['Jl.java'],", "    deps = [ ':parent' ])", "my_rule(name = 'my', dep = ':jl', cnt_cjar = 1, cnt_rjar = 2)");
        // Now, get that information and ensure it is equal to what the jl java_library
        // was presenting
        ConfiguredTarget myConfiguredTarget = getConfiguredTarget("//java/test:my");
        ConfiguredTarget javaLibraryTarget = getConfiguredTarget("//java/test:jl");
        // Extract out the information from skylark rule
        StructImpl info = ((StructImpl) (myConfiguredTarget.get(new SkylarkKey(Label.parseAbsolute("//java/test:extension.bzl", ImmutableMap.of()), "result"))));
        SkylarkNestedSet rawMyCompileJars = ((SkylarkNestedSet) (info.getValue("compile_jars")));
        SkylarkNestedSet rawMyTransitiveRuntimeJars = ((SkylarkNestedSet) (info.getValue("transitive_runtime_jars")));
        SkylarkNestedSet rawMyTransitiveCompileTimeJars = ((SkylarkNestedSet) (info.getValue("transitive_compile_time_jars")));
        NestedSet<Artifact> myCompileJars = rawMyCompileJars.getSet(Artifact.class);
        NestedSet<Artifact> myTransitiveRuntimeJars = rawMyTransitiveRuntimeJars.getSet(Artifact.class);
        NestedSet<Artifact> myTransitiveCompileTimeJars = rawMyTransitiveCompileTimeJars.getSet(Artifact.class);
        // Extract out information from native rule
        JavaCompilationArgsProvider jlJavaCompilationArgsProvider = JavaInfo.getProvider(JavaCompilationArgsProvider.class, javaLibraryTarget);
        NestedSet<Artifact> jlCompileJars = jlJavaCompilationArgsProvider.getDirectCompileTimeJars();
        NestedSet<Artifact> jlTransitiveRuntimeJars = jlJavaCompilationArgsProvider.getRuntimeJars();
        NestedSet<Artifact> jlTransitiveCompileTimeJars = jlJavaCompilationArgsProvider.getTransitiveCompileTimeJars();
        // Using reference equality since should be precisely identical
        assertThat((myCompileJars == jlCompileJars)).isTrue();
        assertThat((myTransitiveRuntimeJars == jlTransitiveRuntimeJars)).isTrue();
        assertThat(myTransitiveCompileTimeJars).isEqualTo(jlTransitiveCompileTimeJars);
    }

    @Test
    public void testSkylarkApiProviderReexported() throws Exception {
        scratch.file("java/test/extension.bzl", "def impl(ctx):", "   dep_java = ctx.attr.dep.java", "   return struct(java = dep_java)", "my_rule = rule(impl, attrs = { ", "  'dep' : attr.label(), ", "})");
        scratch.file("java/test/BUILD", "load(':extension.bzl', 'my_rule')", "java_library(name = 'jl', srcs = ['Jl.java'])", "my_rule(name = 'my', dep = ':jl')");
        // Now, get that information and ensure it is equal to what the jl java_library
        // was presenting
        ConfiguredTarget myConfiguredTarget = getConfiguredTarget("//java/test:my");
        ConfiguredTarget javaLibraryTarget = getConfiguredTarget("//java/test:jl");
        assertThat(myConfiguredTarget.get("java")).isSameAs(javaLibraryTarget.get("java"));
    }

    @Test
    public void javaProviderFieldsAreCorrectAfterCreatingProvider() throws Exception {
        scratch.file("foo/extension.bzl", "def _impl(ctx):", "  my_provider = java_common.create_provider(", "        compile_time_jars = ctx.files.compile_time_jars,", "        use_ijar = False,", "        runtime_jars = ctx.files.runtime_jars,", "        transitive_compile_time_jars = ctx.files.transitive_compile_time_jars,", "        transitive_runtime_jars = ctx.files.transitive_runtime_jars,", "        source_jars = depset(ctx.files.source_jars))", "  return [my_provider]", "my_rule = rule(_impl, ", "    attrs = { ", "        'compile_time_jars' : attr.label_list(allow_files=['.jar']),", "        'full_compile_time_jars' : attr.label_list(allow_files=['.jar']),", "        'runtime_jars': attr.label_list(allow_files=['.jar']),", "        'transitive_compile_time_jars': attr.label_list(allow_files=['.jar']),", "        'transitive_runtime_jars': attr.label_list(allow_files=['.jar']),", "        'source_jars': attr.label_list(allow_files=['.jar'])", "})");
        scratch.file("foo/BUILD", "load(':extension.bzl', 'my_rule')", "my_rule(name = 'myrule',", "    compile_time_jars = ['liba.jar'],", "    runtime_jars = ['libb.jar'],", "    transitive_compile_time_jars = ['libc.jar'],", "    transitive_runtime_jars = ['libd.jar'],", ")");
        ConfiguredTarget target = getConfiguredTarget("//foo:myrule");
        JavaInfo info = target.get(PROVIDER);
        SkylarkNestedSet compileJars = info.getCompileTimeJars();
        assertThat(ActionsTestUtil.prettyArtifactNames(compileJars.getSet(Artifact.class))).containsExactly("foo/liba.jar");
        SkylarkNestedSet fullCompileJars = info.getFullCompileTimeJars();
        assertThat(ActionsTestUtil.prettyArtifactNames(fullCompileJars.getSet(Artifact.class))).containsExactly("foo/liba.jar");
        SkylarkNestedSet transitiveCompileTimeJars = info.getTransitiveCompileTimeJars();
        assertThat(ActionsTestUtil.prettyArtifactNames(transitiveCompileTimeJars.getSet(Artifact.class))).containsExactly("foo/liba.jar", "foo/libc.jar");
        SkylarkNestedSet transitiveRuntimeJars = info.getTransitiveRuntimeJars();
        assertThat(ActionsTestUtil.prettyArtifactNames(transitiveRuntimeJars.getSet(Artifact.class))).containsExactly("foo/libd.jar", "foo/libb.jar");
    }

    @Test
    public void javaProviderFieldsAreCorrectAfterCreatingProviderSomeEmptyFields() throws Exception {
        scratch.file("foo/extension.bzl", "def _impl(ctx):", "  my_provider = java_common.create_provider(", "        compile_time_jars = ctx.files.compile_time_jars,", "        use_ijar = False,", "        runtime_jars = [],", "        transitive_compile_time_jars = [],", "        transitive_runtime_jars = ctx.files.transitive_runtime_jars)", "  return [my_provider]", "my_rule = rule(_impl, ", "    attrs = { ", "        'compile_time_jars' : attr.label_list(allow_files=['.jar']),", "        'transitive_runtime_jars': attr.label_list(allow_files=['.jar']),", "})");
        scratch.file("foo/BUILD", "load(':extension.bzl', 'my_rule')", "my_rule(name = 'myrule',", "    compile_time_jars = ['liba.jar'],", "    transitive_runtime_jars = ['libd.jar'],", ")");
        ConfiguredTarget target = getConfiguredTarget("//foo:myrule");
        JavaInfo info = target.get(PROVIDER);
        SkylarkNestedSet compileJars = info.getCompileTimeJars();
        assertThat(ActionsTestUtil.prettyArtifactNames(compileJars.getSet(Artifact.class))).containsExactly("foo/liba.jar");
        SkylarkNestedSet transitiveCompileTimeJars = info.getTransitiveCompileTimeJars();
        assertThat(ActionsTestUtil.prettyArtifactNames(transitiveCompileTimeJars.getSet(Artifact.class))).containsExactly("foo/liba.jar");
        SkylarkNestedSet transitiveRuntimeJars = info.getTransitiveRuntimeJars();
        assertThat(ActionsTestUtil.prettyArtifactNames(transitiveRuntimeJars.getSet(Artifact.class))).containsExactly("foo/libd.jar");
    }

    @Test
    public void constructJavaProvider() throws Exception {
        scratch.file("foo/extension.bzl", "def _impl(ctx):", "  my_provider = java_common.create_provider(", "        compile_time_jars = depset(ctx.files.compile_time_jars),", "        use_ijar = False,", "        runtime_jars = depset(ctx.files.runtime_jars),", "        transitive_compile_time_jars = depset(ctx.files.transitive_compile_time_jars),", "        transitive_runtime_jars = depset(ctx.files.transitive_runtime_jars),", "        source_jars = depset(ctx.files.source_jars))", "  return [my_provider]", "my_rule = rule(_impl, ", "    attrs = { ", "        'compile_time_jars' : attr.label_list(allow_files=['.jar']),", "        'runtime_jars': attr.label_list(allow_files=['.jar']),", "        'transitive_compile_time_jars': attr.label_list(allow_files=['.jar']),", "        'transitive_runtime_jars': attr.label_list(allow_files=['.jar']),", "        'source_jars': attr.label_list(allow_files=['.jar'])", "})");
        scratch.file("foo/BUILD", "load(':extension.bzl', 'my_rule')", "my_rule(name = 'myrule',", "    compile_time_jars = ['liba.jar'],", "    runtime_jars = ['libb.jar'],", "    transitive_compile_time_jars = ['libc.jar'],", "    transitive_runtime_jars = ['libd.jar'],", "    source_jars = ['liba-src.jar'],", ")");
        ConfiguredTarget target = getConfiguredTarget("//foo:myrule");
        JavaCompilationArgsProvider provider = JavaInfo.getProvider(JavaCompilationArgsProvider.class, target);
        assertThat(provider).isNotNull();
        List<String> compileTimeJars = ActionsTestUtil.prettyArtifactNames(provider.getDirectCompileTimeJars());
        assertThat(compileTimeJars).containsExactly("foo/liba.jar");
        List<String> transitiveCompileTimeJars = ActionsTestUtil.prettyArtifactNames(provider.getTransitiveCompileTimeJars());
        assertThat(transitiveCompileTimeJars).containsExactly("foo/liba.jar", "foo/libc.jar");
        List<String> transitiveRuntimeJars = ActionsTestUtil.prettyArtifactNames(provider.getRuntimeJars());
        assertThat(transitiveRuntimeJars).containsExactly("foo/libd.jar", "foo/libb.jar");
        JavaSourceJarsProvider sourcesProvider = JavaInfo.getProvider(JavaSourceJarsProvider.class, target);
        List<String> sourceJars = ActionsTestUtil.prettyArtifactNames(sourcesProvider.getSourceJars());
        assertThat(sourceJars).containsExactly("foo/liba-src.jar");
    }

    @Test
    public void constructJavaProviderWithAnotherJavaProvider() throws Exception {
        scratch.file("foo/extension.bzl", "def _impl(ctx):", "  transitive_provider = java_common.merge(", "      [dep[JavaInfo] for dep in ctx.attr.deps])", "  my_provider = java_common.create_provider(", "        compile_time_jars = depset(ctx.files.compile_time_jars),", "        use_ijar = False,", "        runtime_jars = depset(ctx.files.runtime_jars))", "  return [java_common.merge([my_provider, transitive_provider])]", "my_rule = rule(_impl, ", "    attrs = { ", "        'compile_time_jars' : attr.label_list(allow_files=['.jar']),", "        'runtime_jars': attr.label_list(allow_files=['.jar']),", "        'deps': attr.label_list()", "})");
        scratch.file("foo/liba.jar");
        scratch.file("foo/libb.jar");
        scratch.file("foo/BUILD", "load(':extension.bzl', 'my_rule')", "java_library(name = 'java_dep',", "    srcs = ['A.java'])", "my_rule(name = 'myrule',", "    compile_time_jars = ['liba.jar'],", "    runtime_jars = ['libb.jar'],", "    deps = [':java_dep']", ")");
        ConfiguredTarget target = getConfiguredTarget("//foo:myrule");
        JavaCompilationArgsProvider provider = JavaInfo.getProvider(JavaCompilationArgsProvider.class, target);
        assertThat(provider).isNotNull();
        List<String> compileTimeJars = ActionsTestUtil.prettyArtifactNames(provider.getDirectCompileTimeJars());
        assertThat(compileTimeJars).containsExactly("foo/liba.jar", "foo/libjava_dep-hjar.jar");
        List<String> runtimeJars = ActionsTestUtil.prettyArtifactNames(provider.getRuntimeJars());
        assertThat(runtimeJars).containsExactly("foo/libb.jar", "foo/libjava_dep.jar");
    }

    @Test
    public void constructJavaProviderJavaLibrary() throws Exception {
        scratch.file("foo/extension.bzl", "def _impl(ctx):", "  my_provider = java_common.create_provider(", "        transitive_compile_time_jars = depset(ctx.files.transitive_compile_time_jars),", "        transitive_runtime_jars = depset(ctx.files.transitive_runtime_jars))", "  return [my_provider]", "my_rule = rule(_impl, ", "    attrs = { ", "        'transitive_compile_time_jars' : attr.label_list(allow_files=['.jar']),", "        'transitive_runtime_jars': attr.label_list(allow_files=['.jar'])", "})");
        scratch.file("foo/liba.jar");
        scratch.file("foo/libb.jar");
        scratch.file("foo/BUILD", "load(':extension.bzl', 'my_rule')", "my_rule(name = 'myrule',", "    transitive_compile_time_jars = ['liba.jar'],", "    transitive_runtime_jars = ['libb.jar']", ")", "java_library(name = 'java_lib',", "    srcs = ['C.java'],", "    deps = [':myrule']", ")");
        ConfiguredTarget target = getConfiguredTarget("//foo:java_lib");
        JavaCompilationArgsProvider provider = JavaInfo.getProvider(JavaCompilationArgsProvider.class, target);
        List<String> compileTimeJars = ActionsTestUtil.prettyArtifactNames(provider.getTransitiveCompileTimeJars());
        assertThat(compileTimeJars).containsExactly("foo/libjava_lib-hjar.jar", "foo/liba.jar");
        List<String> runtimeJars = ActionsTestUtil.prettyArtifactNames(provider.getRuntimeJars());
        assertThat(runtimeJars).containsExactly("foo/libjava_lib.jar", "foo/libb.jar");
    }

    @Test
    public void javaProviderExposedOnJavaLibrary() throws Exception {
        scratch.file("foo/extension.bzl", "my_provider = provider()", "def _impl(ctx):", "  dep_params = ctx.attr.dep[JavaInfo]", "  return [my_provider(p = dep_params)]", "my_rule = rule(_impl, attrs = { 'dep' : attr.label() })");
        scratch.file("foo/BUILD", "load(':extension.bzl', 'my_rule')", "java_library(name = 'jl', srcs = ['java/A.java'])", "my_rule(name = 'r', dep = ':jl')");
        ConfiguredTarget myRuleTarget = getConfiguredTarget("//foo:r");
        ConfiguredTarget javaLibraryTarget = getConfiguredTarget("//foo:jl");
        SkylarkKey myProviderKey = new SkylarkKey(Label.parseAbsolute("//foo:extension.bzl", ImmutableMap.of()), "my_provider");
        StructImpl declaredProvider = ((StructImpl) (myRuleTarget.get(myProviderKey)));
        Object javaProvider = declaredProvider.getValue("p");
        assertThat(javaProvider).isInstanceOf(JavaInfo.class);
        assertThat(javaLibraryTarget.get(PROVIDER)).isEqualTo(javaProvider);
    }

    @Test
    public void javaProviderPropagation() throws Exception {
        scratch.file("foo/extension.bzl", "def _impl(ctx):", "  dep_params = ctx.attr.dep[JavaInfo]", "  return [dep_params]", "my_rule = rule(_impl, attrs = { 'dep' : attr.label() })");
        scratch.file("foo/BUILD", "load(':extension.bzl', 'my_rule')", "java_library(name = 'jl', srcs = ['java/A.java'])", "my_rule(name = 'r', dep = ':jl')", "java_library(name = 'jl_top', srcs = ['java/C.java'], deps = [':r'])");
        ConfiguredTarget myRuleTarget = getConfiguredTarget("//foo:r");
        ConfiguredTarget javaLibraryTarget = getConfiguredTarget("//foo:jl");
        ConfiguredTarget topJavaLibraryTarget = getConfiguredTarget("//foo:jl_top");
        Object javaProvider = myRuleTarget.get(PROVIDER.getKey());
        assertThat(javaProvider).isInstanceOf(JavaInfo.class);
        JavaInfo jlJavaInfo = javaLibraryTarget.get(PROVIDER);
        assertThat((jlJavaInfo == javaProvider)).isTrue();
        JavaInfo jlTopJavaInfo = topJavaLibraryTarget.get(PROVIDER);
        JavaSkylarkApiTest.javaCompilationArgsHaveTheSameParent(jlJavaInfo.getProvider(JavaCompilationArgsProvider.class), jlTopJavaInfo.getProvider(JavaCompilationArgsProvider.class));
    }

    @Test
    public void skylarkJavaToJavaLibraryAttributes() throws Exception {
        scratch.file("foo/extension.bzl", "def _impl(ctx):", "  dep_params = ctx.attr.dep[JavaInfo]", "  return struct(providers = [dep_params])", "my_rule = rule(_impl, attrs = { 'dep' : attr.label() })");
        scratch.file("foo/BUILD", "load(':extension.bzl', 'my_rule')", "java_library(name = 'jl_bottom_for_deps', srcs = ['java/A.java'])", "java_library(name = 'jl_bottom_for_exports', srcs = ['java/A2.java'])", "java_library(name = 'jl_bottom_for_runtime_deps', srcs = ['java/A2.java'])", "my_rule(name = 'mya', dep = ':jl_bottom_for_deps')", "my_rule(name = 'myb', dep = ':jl_bottom_for_exports')", "my_rule(name = 'myc', dep = ':jl_bottom_for_runtime_deps')", "java_library(name = 'lib_exports', srcs = ['java/B.java'], deps = [':mya'],", "  exports = [':myb'], runtime_deps = [':myc'])", "java_library(name = 'lib_interm', srcs = ['java/C.java'], deps = [':lib_exports'])", "java_library(name = 'lib_top', srcs = ['java/D.java'], deps = [':lib_interm'])");
        assertNoEvents();
        // Test that all bottom jars are on the runtime classpath of lib_exports.
        ConfiguredTarget jlExports = getConfiguredTarget("//foo:lib_exports");
        JavaCompilationArgsProvider jlExportsProvider = JavaInfo.getProvider(JavaCompilationArgsProvider.class, jlExports);
        assertThat(ActionsTestUtil.prettyArtifactNames(jlExportsProvider.getRuntimeJars())).containsAllOf("foo/libjl_bottom_for_deps.jar", "foo/libjl_bottom_for_runtime_deps.jar", "foo/libjl_bottom_for_exports.jar");
        // Test that libjl_bottom_for_exports.jar is in the recursive java compilation args of lib_top.
        ConfiguredTarget jlTop = getConfiguredTarget("//foo:lib_interm");
        JavaCompilationArgsProvider jlTopProvider = JavaInfo.getProvider(JavaCompilationArgsProvider.class, jlTop);
        assertThat(ActionsTestUtil.prettyArtifactNames(jlTopProvider.getRuntimeJars())).contains("foo/libjl_bottom_for_exports.jar");
    }

    @Test
    public void skylarkJavaToJavaBinaryAttributes() throws Exception {
        scratch.file("foo/extension.bzl", "def _impl(ctx):", "  dep_params = ctx.attr.dep[JavaInfo]", "  return struct(providers = [dep_params])", "my_rule = rule(_impl, attrs = { 'dep' : attr.label() })");
        scratch.file("foo/BUILD", "load(':extension.bzl', 'my_rule')", "java_library(name = 'jl_bottom_for_deps', srcs = ['java/A.java'])", "java_library(name = 'jl_bottom_for_runtime_deps', srcs = ['java/A2.java'])", "my_rule(name = 'mya', dep = ':jl_bottom_for_deps')", "my_rule(name = 'myb', dep = ':jl_bottom_for_runtime_deps')", "java_binary(name = 'binary', srcs = ['java/B.java'], main_class = 'foo.A',", "  deps = [':mya'], runtime_deps = [':myb'])");
        assertNoEvents();
        // Test that all bottom jars are on the runtime classpath.
        ConfiguredTarget binary = getConfiguredTarget("//foo:binary");
        assertThat(ActionsTestUtil.prettyArtifactNames(binary.getProvider(JavaRuntimeClasspathProvider.class).getRuntimeClasspath())).containsAllOf("foo/libjl_bottom_for_deps.jar", "foo/libjl_bottom_for_runtime_deps.jar");
    }

    @Test
    public void skylarkJavaToJavaImportAttributes() throws Exception {
        scratch.file("foo/extension.bzl", "def _impl(ctx):", "  dep_params = ctx.attr.dep[JavaInfo]", "  return struct(providers = [dep_params])", "my_rule = rule(_impl, attrs = { 'dep' : attr.label() })");
        scratch.file("foo/BUILD", "load(':extension.bzl', 'my_rule')", "java_library(name = 'jl_bottom_for_deps', srcs = ['java/A.java'])", "java_library(name = 'jl_bottom_for_runtime_deps', srcs = ['java/A2.java'])", "my_rule(name = 'mya', dep = ':jl_bottom_for_deps')", "my_rule(name = 'myb', dep = ':jl_bottom_for_runtime_deps')", "java_import(name = 'import', jars = ['B.jar'], deps = [':mya'], runtime_deps = [':myb'])");
        assertNoEvents();
        // Test that all bottom jars are on the runtime classpath.
        ConfiguredTarget importTarget = getConfiguredTarget("//foo:import");
        JavaCompilationArgsProvider compilationProvider = JavaInfo.getProvider(JavaCompilationArgsProvider.class, importTarget);
        assertThat(ActionsTestUtil.prettyArtifactNames(compilationProvider.getRuntimeJars())).containsAllOf("foo/libjl_bottom_for_deps.jar", "foo/libjl_bottom_for_runtime_deps.jar");
    }

    @Test
    public void javaInfoSourceJarsExposed() throws Exception {
        scratch.file("foo/extension.bzl", "result = provider()", "def _impl(ctx):", "  return [result(source_jars = ctx.attr.dep[JavaInfo].source_jars)]", "my_rule = rule(_impl, attrs = { 'dep' : attr.label() })");
        scratch.file("foo/BUILD", "load(':extension.bzl', 'my_rule')", "java_library(name = 'my_java_lib_b', srcs = ['java/B.java'])", "java_library(name = 'my_java_lib_a', srcs = ['java/A.java'] , deps = [':my_java_lib_b'])", "my_rule(name = 'my_skylark_rule', dep = ':my_java_lib_a')");
        assertNoEvents();
        ConfiguredTarget myRuleTarget = getConfiguredTarget("//foo:my_skylark_rule");
        StructImpl info = ((StructImpl) (myRuleTarget.get(new SkylarkKey(Label.parseAbsolute("//foo:extension.bzl", ImmutableMap.of()), "result"))));
        @SuppressWarnings("unchecked")
        SkylarkList<Artifact> sourceJars = ((SkylarkList<Artifact>) (info.getValue("source_jars")));
        assertThat(ActionsTestUtil.prettyArtifactNames(sourceJars)).containsExactly("foo/libmy_java_lib_a-src.jar");
        assertThat(ActionsTestUtil.prettyArtifactNames(sourceJars)).doesNotContain("foo/libmy_java_lib_b-src.jar");
    }

    @Test
    public void testJavaInfoGetTransitiveSourceJars() throws Exception {
        scratch.file("foo/extension.bzl", "result = provider()", "def _impl(ctx):", "  return [result(property = ctx.attr.dep[JavaInfo].transitive_source_jars)]", "my_rule = rule(_impl, attrs = { 'dep' : attr.label() })");
        scratch.file("foo/BUILD", "load(':extension.bzl', 'my_rule')", "java_library(name = 'my_java_lib_c', srcs = ['java/C.java'])", "java_library(name = 'my_java_lib_b', srcs = ['java/B.java'], deps = [':my_java_lib_c'])", "java_library(name = 'my_java_lib_a', srcs = ['java/A.java'], deps = [':my_java_lib_b'])", "my_rule(name = 'my_skylark_rule', dep = ':my_java_lib_a')");
        assertNoEvents();
        ConfiguredTarget myRuleTarget = getConfiguredTarget("//foo:my_skylark_rule");
        StructImpl info = ((StructImpl) (myRuleTarget.get(new SkylarkKey(Label.parseAbsolute("//foo:extension.bzl", ImmutableMap.of()), "result"))));
        @SuppressWarnings("unchecked")
        SkylarkNestedSet sourceJars = ((SkylarkNestedSet) (info.getValue("property")));
        assertThat(ActionsTestUtil.prettyArtifactNames(sourceJars.getSet(Artifact.class))).containsExactly("foo/libmy_java_lib_a-src.jar", "foo/libmy_java_lib_b-src.jar", "foo/libmy_java_lib_c-src.jar");
    }

    @Test
    public void testJavaInfoGetTransitiveDeps() throws Exception {
        scratch.file("foo/extension.bzl", "result = provider()", "def _impl(ctx):", "  return [result(property = ctx.attr.dep[JavaInfo].transitive_deps)]", "my_rule = rule(_impl, attrs = { 'dep' : attr.label() })");
        scratch.file("foo/BUILD", "load(':extension.bzl', 'my_rule')", "java_library(name = 'my_java_lib_c', srcs = ['java/C.java'])", "java_library(name = 'my_java_lib_b', srcs = ['java/B.java'], deps = [':my_java_lib_c'])", "java_library(name = 'my_java_lib_a', srcs = ['java/A.java'], deps = [':my_java_lib_b'])", "my_rule(name = 'my_skylark_rule', dep = ':my_java_lib_a')");
        assertNoEvents();
        ConfiguredTarget myRuleTarget = getConfiguredTarget("//foo:my_skylark_rule");
        StructImpl info = ((StructImpl) (myRuleTarget.get(new SkylarkKey(Label.parseAbsolute("//foo:extension.bzl", ImmutableMap.of()), "result"))));
        @SuppressWarnings("unchecked")
        SkylarkNestedSet sourceJars = ((SkylarkNestedSet) (info.getValue("property")));
        assertThat(ActionsTestUtil.prettyArtifactNames(sourceJars.getSet(Artifact.class))).containsExactly("foo/libmy_java_lib_a-hjar.jar", "foo/libmy_java_lib_b-hjar.jar", "foo/libmy_java_lib_c-hjar.jar");
    }

    @Test
    public void testJavaInfoGetTransitiveRuntimeDeps() throws Exception {
        scratch.file("foo/extension.bzl", "result = provider()", "def _impl(ctx):", "  return [result(property = ctx.attr.dep[JavaInfo].transitive_runtime_deps)]", "my_rule = rule(_impl, attrs = { 'dep' : attr.label() })");
        scratch.file("foo/BUILD", "load(':extension.bzl', 'my_rule')", "java_library(name = 'my_java_lib_c', srcs = ['java/C.java'])", "java_library(name = 'my_java_lib_b', srcs = ['java/B.java'], deps = [':my_java_lib_c'])", "java_library(name = 'my_java_lib_a', srcs = ['java/A.java'], deps = [':my_java_lib_b'])", "my_rule(name = 'my_skylark_rule', dep = ':my_java_lib_a')");
        assertNoEvents();
        ConfiguredTarget myRuleTarget = getConfiguredTarget("//foo:my_skylark_rule");
        StructImpl info = ((StructImpl) (myRuleTarget.get(new SkylarkKey(Label.parseAbsolute("//foo:extension.bzl", ImmutableMap.of()), "result"))));
        @SuppressWarnings("unchecked")
        SkylarkNestedSet sourceJars = ((SkylarkNestedSet) (info.getValue("property")));
        assertThat(ActionsTestUtil.prettyArtifactNames(sourceJars.getSet(Artifact.class))).containsExactly("foo/libmy_java_lib_a.jar", "foo/libmy_java_lib_b.jar", "foo/libmy_java_lib_c.jar");
    }

    @Test
    public void testJavaInfoGetTransitiveExports() throws Exception {
        scratch.file("foo/extension.bzl", "result = provider()", "def _impl(ctx):", "  return [result(property = ctx.attr.dep[JavaInfo].transitive_exports)]", "my_rule = rule(_impl, attrs = { 'dep' : attr.label() })");
        scratch.file("foo/BUILD", "load(':extension.bzl', 'my_rule')", "java_library(name = 'my_java_lib_c', srcs = ['java/C.java'])", "java_library(name = 'my_java_lib_b', srcs = ['java/B.java'])", "java_library(name = 'my_java_lib_a', srcs = ['java/A.java'], ", "             deps = [':my_java_lib_b', ':my_java_lib_c'], ", "             exports = [':my_java_lib_b']) ", "my_rule(name = 'my_skylark_rule', dep = ':my_java_lib_a')");
        assertNoEvents();
        ConfiguredTarget myRuleTarget = getConfiguredTarget("//foo:my_skylark_rule");
        StructImpl info = ((StructImpl) (myRuleTarget.get(new SkylarkKey(Label.parseAbsolute("//foo:extension.bzl", ImmutableMap.of()), "result"))));
        @SuppressWarnings("unchecked")
        SkylarkNestedSet exports = ((SkylarkNestedSet) (info.getValue("property")));
        assertThat(exports.getSet(Label.class)).containsExactly(Label.parseAbsolute("//foo:my_java_lib_b", ImmutableMap.of()));
    }

    @Test
    public void testJavaInfoGetGenJarsProvider() throws Exception {
        scratch.file("foo/extension.bzl", "result = provider()", "def _impl(ctx):", "  return [result(property = ctx.attr.dep[JavaInfo].annotation_processing)]", "my_rule = rule(_impl, attrs = { 'dep' : attr.label() })");
        scratch.file("foo/BUILD", "load(':extension.bzl', 'my_rule')", "java_library(name = 'my_java_lib_a', srcs = ['java/A.java'], ", "             javacopts = ['-processor com.google.process.Processor'])", "my_rule(name = 'my_skylark_rule', dep = ':my_java_lib_a')");
        assertNoEvents();
        ConfiguredTarget myRuleTarget = getConfiguredTarget("//foo:my_skylark_rule");
        StructImpl info = ((StructImpl) (myRuleTarget.get(new SkylarkKey(Label.parseAbsolute("//foo:extension.bzl", ImmutableMap.of()), "result"))));
        JavaGenJarsProvider javaGenJarsProvider = ((JavaGenJarsProvider) (info.getValue("property")));
        assertThat(javaGenJarsProvider.getGenClassJar().getFilename()).isEqualTo("libmy_java_lib_a-gen.jar");
        assertThat(javaGenJarsProvider.getGenSourceJar().getFilename()).isEqualTo("libmy_java_lib_a-gensrc.jar");
    }

    @Test
    public void javaInfoGetCompilationInfoProvider() throws Exception {
        scratch.file("foo/extension.bzl", "result = provider()", "def _impl(ctx):", "  return [result(property = ctx.attr.dep[JavaInfo].compilation_info)]", "my_rule = rule(_impl, attrs = { 'dep' : attr.label() })");
        scratch.file("foo/BUILD", "load(':extension.bzl', 'my_rule')", "java_library(name = 'my_java_lib_a', srcs = ['java/A.java'])", "my_rule(name = 'my_skylark_rule', dep = ':my_java_lib_a')");
        assertNoEvents();
        ConfiguredTarget myRuleTarget = getConfiguredTarget("//foo:my_skylark_rule");
        StructImpl info = ((StructImpl) (myRuleTarget.get(new SkylarkKey(Label.parseAbsolute("//foo:extension.bzl", ImmutableMap.of()), "result"))));
        JavaCompilationInfoProvider javaCompilationInfoProvider = ((JavaCompilationInfoProvider) (info.getValue("property")));
        assertThat(ActionsTestUtil.prettyArtifactNames(javaCompilationInfoProvider.getRuntimeClasspath())).containsExactly("foo/libmy_java_lib_a.jar");
    }

    /* Test inspired by {@link AbstractJavaLibraryConfiguredTargetTest#testNeverlink}. */
    @Test
    public void javaCommonCompileNeverlink() throws Exception {
        writeBuildFileForJavaToolchain();
        scratch.file("java/test/BUILD", "load(':custom_rule.bzl', 'java_custom_library')", "java_binary(name = 'plugin',", "    deps = [ ':somedep'],", "    srcs = [ 'Plugin.java'],", "    main_class = 'plugin.start')", "java_custom_library(name = 'somedep',", "    srcs = ['Dependency.java'],", "    deps = [ ':eclipse' ])", "java_custom_library(name = 'eclipse',", "    neverlink = 1,", "    srcs = ['EclipseDependency.java'])");
        scratch.file("java/test/custom_rule.bzl", "def _impl(ctx):", "  output_jar = ctx.actions.declare_file('lib' + ctx.label.name + '.jar')", "  deps = [dep[java_common.provider] for dep in ctx.attr.deps]", "  compilation_provider = java_common.compile(", "    ctx,", "    source_files = ctx.files.srcs,", "    output = output_jar,", "    neverlink = ctx.attr.neverlink,", "    deps = deps,", "    java_toolchain = ctx.attr._java_toolchain[java_common.JavaToolchainInfo],", "    host_javabase = ctx.attr._host_javabase[java_common.JavaRuntimeInfo]", "  )", "  return struct(", "    files = depset([output_jar]),", "    providers = [compilation_provider]", "  )", "java_custom_library = rule(", "  implementation = _impl,", "  outputs = {", "    'my_output': 'lib%{name}.jar'", "  },", "  attrs = {", "    'srcs': attr.label_list(allow_files=['.java']),", "    'neverlink': attr.bool(),", "     'deps': attr.label_list(),", "    '_java_toolchain': attr.label(default = Label('//java/com/google/test:toolchain')),", (("    '_host_javabase': attr.label(default = Label('" + (JavaSkylarkApiTest.HOST_JAVA_RUNTIME_LABEL)) + "'))"), "  },", "  fragments = ['java']", ")");
        ConfiguredTarget target = getConfiguredTarget("//java/test:plugin");
        assertThat(actionsTestUtil().predecessorClosureAsCollection(getFilesToBuild(target), JAVA_SOURCE)).containsExactly("Plugin.java", "Dependency.java", "EclipseDependency.java");
        assertThat(ActionsTestUtil.baseNamesOf(FileType.filter(getRunfilesSupport(target).getRunfilesSymlinkTargets(), JAR))).isEqualTo("libsomedep.jar plugin.jar");
    }

    @Test
    public void strictDepsEnabled() throws Exception {
        scratch.file("foo/custom_library.bzl", "def _impl(ctx):", "  java_provider = java_common.merge([dep[JavaInfo] for dep in ctx.attr.deps])", "  if not ctx.attr.strict_deps:", "    java_provider = java_common.make_non_strict(java_provider)", "  return [java_provider]", "custom_library = rule(", "  attrs = {", "    'deps': attr.label_list(),", "    'strict_deps': attr.bool()", "  },", "  implementation = _impl", ")");
        scratch.file("foo/BUILD", "load(':custom_library.bzl', 'custom_library')", "custom_library(name = 'custom', deps = [':a'], strict_deps = True)", "java_library(name = 'a', srcs = ['java/A.java'], deps = [':b'])", "java_library(name = 'b', srcs = ['java/B.java'])");
        ConfiguredTarget myRuleTarget = getConfiguredTarget("//foo:custom");
        JavaCompilationArgsProvider javaCompilationArgsProvider = JavaInfo.getProvider(JavaCompilationArgsProvider.class, myRuleTarget);
        List<String> directJars = ActionsTestUtil.prettyArtifactNames(javaCompilationArgsProvider.getDirectCompileTimeJars());
        assertThat(directJars).containsExactly("foo/liba-hjar.jar");
    }

    @Test
    public void strictDepsDisabled() throws Exception {
        scratch.file("foo/custom_library.bzl", "def _impl(ctx):", "  java_provider = java_common.merge([dep[JavaInfo] for dep in ctx.attr.deps])", "  if not ctx.attr.strict_deps:", "    java_provider = java_common.make_non_strict(java_provider)", "  return [java_provider]", "custom_library = rule(", "  attrs = {", "    'deps': attr.label_list(),", "    'strict_deps': attr.bool()", "  },", "  implementation = _impl", ")");
        scratch.file("foo/BUILD", "load(':custom_library.bzl', 'custom_library')", "custom_library(name = 'custom', deps = [':a'], strict_deps = False)", "java_library(name = 'a', srcs = ['java/A.java'], deps = [':b'])", "java_library(name = 'b', srcs = ['java/B.java'])");
        ConfiguredTarget myRuleTarget = getConfiguredTarget("//foo:custom");
        JavaCompilationArgsProvider javaCompilationArgsProvider = JavaInfo.getProvider(JavaCompilationArgsProvider.class, myRuleTarget);
        List<String> directJars = ActionsTestUtil.prettyArtifactNames(javaCompilationArgsProvider.getRuntimeJars());
        assertThat(directJars).containsExactly("foo/liba.jar", "foo/libb.jar");
    }

    @Test
    public void strictJavaDepsFlagExposed_default() throws Exception {
        scratch.file("foo/rule.bzl", "result = provider()", "def _impl(ctx):", "  return [result(strict_java_deps=ctx.fragments.java.strict_java_deps)]", "myrule = rule(", "  implementation=_impl,", "  fragments = ['java']", ")");
        scratch.file("foo/BUILD", "load(':rule.bzl', 'myrule')", "myrule(name='myrule')");
        ConfiguredTarget configuredTarget = getConfiguredTarget("//foo:myrule");
        StructImpl info = ((StructImpl) (configuredTarget.get(new SkylarkKey(Label.parseAbsolute("//foo:rule.bzl", ImmutableMap.of()), "result"))));
        assertThat(((String) (info.getValue("strict_java_deps")))).isEqualTo("default");
    }

    @Test
    public void strictJavaDepsFlagExposed_error() throws Exception {
        scratch.file("foo/rule.bzl", "result = provider()", "def _impl(ctx):", "  return [result(strict_java_deps=ctx.fragments.java.strict_java_deps)]", "myrule = rule(", "  implementation=_impl,", "  fragments = ['java']", ")");
        scratch.file("foo/BUILD", "load(':rule.bzl', 'myrule')", "myrule(name='myrule')");
        useConfiguration("--strict_java_deps=ERROR");
        ConfiguredTarget configuredTarget = getConfiguredTarget("//foo:myrule");
        StructImpl info = ((StructImpl) (configuredTarget.get(new SkylarkKey(Label.parseAbsolute("//foo:rule.bzl", ImmutableMap.of()), "result"))));
        assertThat(((String) (info.getValue("strict_java_deps")))).isEqualTo("error");
    }

    @Test
    public void javaToolchainFlag_default() throws Exception {
        writeBuildFileForJavaToolchain();
        scratch.file("foo/rule.bzl", "result = provider()", "def _impl(ctx):", "  return [result(java_toolchain_label=ctx.attr._java_toolchain.label)]", "myrule = rule(", "  implementation=_impl,", "  fragments = ['java'],", "  attrs = { '_java_toolchain': attr.label(default=Label('//foo:alias')) }", ")");
        scratch.file("foo/BUILD", "load(':rule.bzl', 'myrule')", "java_toolchain_alias(name='alias')", "myrule(name='myrule')");
        ConfiguredTarget configuredTarget = getConfiguredTarget("//foo:myrule");
        StructImpl info = ((StructImpl) (configuredTarget.get(new SkylarkKey(Label.parseAbsolute("//foo:rule.bzl", ImmutableMap.of()), "result"))));
        Label javaToolchainLabel = ((Label) (info.getValue("java_toolchain_label")));
        assertThat(((javaToolchainLabel.toString().endsWith("jdk:remote_toolchain")) || (javaToolchainLabel.toString().endsWith("jdk:toolchain")))).isTrue();
    }

    @Test
    public void javaToolchainFlag_set() throws Exception {
        writeBuildFileForJavaToolchain();
        scratch.file("foo/rule.bzl", "result = provider()", "def _impl(ctx):", "  return [result(java_toolchain_label=ctx.attr._java_toolchain.label)]", "myrule = rule(", "  implementation=_impl,", "  fragments = ['java'],", "  attrs = { '_java_toolchain': attr.label(default=Label('//foo:alias')) }", ")");
        scratch.file("foo/BUILD", "load(':rule.bzl', 'myrule')", "java_toolchain_alias(name='alias')", "myrule(name='myrule')");
        useConfiguration("--java_toolchain=//java/com/google/test:toolchain");
        ConfiguredTarget configuredTarget = getConfiguredTarget("//foo:myrule");
        StructImpl info = ((StructImpl) (configuredTarget.get(new SkylarkKey(Label.parseAbsolute("//foo:rule.bzl", ImmutableMap.of()), "result"))));
        Label javaToolchainLabel = ((Label) (info.getValue("java_toolchain_label")));
        assertThat(javaToolchainLabel.toString()).isEqualTo("//java/com/google/test:toolchain");
    }

    @Test
    public void testIncompatibleDisallowLegacyJavaInfo() throws Exception {
        setSkylarkSemanticsOptions("--incompatible_disallow_legacy_javainfo");
        scratch.file("java/test/custom_rule.bzl", "def _impl(ctx):", "  jar = ctx.actions.declare_file('jar')", "  java_common.create_provider(", "      compile_time_jars = [jar],", "      transitive_compile_time_jars = [jar],", "      runtime_jars = [jar],", "      use_ijar = False,", "  )", "java_custom_library = rule(", "  implementation = _impl,", ")");
        checkError("java/test", "custom", ("java_common.create_provider is deprecated and cannot be used when " + "--incompatible_disallow_legacy_javainfo is set."), "load(':custom_rule.bzl', 'java_custom_library')", "java_custom_library(", "  name = 'custom',", ")");
    }

    @Test
    public void testIncompatibleDisallowLegacyJavaInfoWithFlag() throws Exception {
        setSkylarkSemanticsOptions("--incompatible_disallow_legacy_javainfo");
        setSkylarkSemanticsOptions("--experimental_java_common_create_provider_enabled_packages=java/test");
        scratch.file("java/test/custom_rule.bzl", "def _impl(ctx):", "  jar = ctx.file.jar", "  java_common.create_provider(", "      compile_time_jars = [jar],", "      transitive_compile_time_jars = [jar],", "      runtime_jars = [jar],", "      use_ijar = False,", "  )", "java_custom_library = rule(", "  implementation = _impl,", "  attrs = {", "    'jar': attr.label(allow_files = True, single_file = True),", "  }", ")");
        scratch.file("java/test/BUILD", "load(':custom_rule.bzl', 'java_custom_library')", "java_custom_library(", "  name = 'custom',", "  jar = 'lib.jar'", ")");
        assertThat(getConfiguredTarget("//java/test:custom")).isNotNull();
    }

    @Test
    public void testCompileExports() throws Exception {
        writeBuildFileForJavaToolchain();
        scratch.file("java/test/BUILD", "load(':custom_rule.bzl', 'java_custom_library')", "java_custom_library(", "  name = 'custom',", "  srcs = ['Main.java'],", "  exports = [':dep']", ")", "java_library(", "  name = 'dep',", "  srcs = [ 'Dep.java'],", ")");
        scratch.file("java/test/custom_rule.bzl", "def _impl(ctx):", "  output_jar = ctx.actions.declare_file('amazing.jar')", "  exports = [export[java_common.provider] for export in ctx.attr.exports]", "  compilation_provider = java_common.compile(", "    ctx,", "    source_files = ctx.files.srcs,", "    output = output_jar,", "    exports = exports,", "    java_toolchain = ctx.attr._java_toolchain[java_common.JavaToolchainInfo],", "    host_javabase = ctx.attr._host_javabase[java_common.JavaRuntimeInfo]", "  )", "  return struct(", "    files = depset([output_jar]),", "    providers = [compilation_provider]", "  )", "java_custom_library = rule(", "  implementation = _impl,", "  outputs = {", "    'output': 'amazing.jar',", "  },", "  attrs = {", "    'srcs': attr.label_list(allow_files=['.java']),", "    'exports': attr.label_list(),", "    '_java_toolchain': attr.label(default = Label('//java/com/google/test:toolchain')),", "    '_host_javabase': attr.label(", (("        default = Label('" + (JavaSkylarkApiTest.HOST_JAVA_RUNTIME_LABEL)) + "'))"), "  },", "  fragments = ['java']", ")");
        JavaInfo info = getConfiguredTarget("//java/test:custom").get(PROVIDER);
        assertThat(ActionsTestUtil.prettyArtifactNames(info.getTransitiveSourceJars())).containsExactly("java/test/amazing-src.jar", "java/test/libdep-src.jar");
        JavaCompilationArgsProvider provider = info.getProvider(JavaCompilationArgsProvider.class);
        assertThat(ActionsTestUtil.prettyArtifactNames(provider.getDirectCompileTimeJars())).containsExactly("java/test/amazing-hjar.jar", "java/test/libdep-hjar.jar");
        assertThat(ActionsTestUtil.prettyArtifactNames(provider.getCompileTimeJavaDependencyArtifacts())).containsExactly("java/test/amazing-hjar.jdeps", "java/test/libdep-hjar.jdeps");
    }

    @Test
    public void testCompileOutputJarHasManifestProto() throws Exception {
        writeBuildFileForJavaToolchain();
        scratch.file("foo/java_custom_library.bzl", "def _impl(ctx):", "  output_jar = ctx.actions.declare_file('lib%s.jar' % ctx.label.name)", "  compilation_provider = java_common.compile(", "    ctx,", "    source_files = ctx.files.srcs,", "    output = output_jar,", "    java_toolchain = ctx.attr._java_toolchain[java_common.JavaToolchainInfo],", "    host_javabase = ctx.attr._host_javabase[java_common.JavaRuntimeInfo]", "  )", "  return struct(", "    providers = [compilation_provider]", "  )", "java_custom_library = rule(", "  implementation = _impl,", "  attrs = {", "    'srcs': attr.label_list(allow_files=['.java']),", "    '_java_toolchain': attr.label(default = Label('//java/com/google/test:toolchain')),", "    '_host_javabase': attr.label(", (("        default = Label('" + (JavaSkylarkApiTest.HOST_JAVA_RUNTIME_LABEL)) + "'))"), "  },", "  fragments = ['java'],", ")");
        scratch.file("foo/BUILD", "load(':java_custom_library.bzl', 'java_custom_library')", "java_custom_library(name = 'b', srcs = ['java/B.java'])");
        ConfiguredTarget configuredTarget = getConfiguredTarget("//foo:b");
        JavaInfo info = configuredTarget.get(PROVIDER);
        JavaRuleOutputJarsProvider outputs = info.getOutputJars();
        assertThat(outputs.getOutputJars()).hasSize(1);
        OutputJar output = outputs.getOutputJars().get(0);
        assertThat(output.getManifestProto().getFilename()).isEqualTo("libb.jar_manifest_proto");
    }

    @Test
    public void testCompileWithNeverlinkDeps() throws Exception {
        writeBuildFileForJavaToolchain();
        scratch.file("foo/java_custom_library.bzl", "def _impl(ctx):", "  output_jar = ctx.actions.declare_file('lib%s.jar' % ctx.label.name)", "  deps = [deps[JavaInfo] for deps in ctx.attr.deps]", "  compilation_provider = java_common.compile(", "    ctx,", "    source_files = ctx.files.srcs,", "    output = output_jar,", "    deps = deps,", "    java_toolchain = ctx.attr._java_toolchain[java_common.JavaToolchainInfo],", "    host_javabase = ctx.attr._host_javabase[java_common.JavaRuntimeInfo]", "  )", "  return struct(", "    providers = [compilation_provider]", "  )", "java_custom_library = rule(", "  implementation = _impl,", "  attrs = {", "    'srcs': attr.label_list(allow_files=['.java']),", "    'deps': attr.label_list(),", "    '_java_toolchain': attr.label(default = Label('//java/com/google/test:toolchain')),", "    '_host_javabase': attr.label(", (("        default = Label('" + (JavaSkylarkApiTest.HOST_JAVA_RUNTIME_LABEL)) + "'))"), "  },", "  fragments = ['java'],", ")");
        scratch.file("foo/BUILD", "load(':java_custom_library.bzl', 'java_custom_library')", "java_library(name = 'b', srcs = ['java/B.java'], neverlink = 1)", "java_custom_library(name = 'a', srcs = ['java/A.java'], deps = [':b'])");
        ConfiguredTarget configuredTarget = getConfiguredTarget("//foo:a");
        JavaInfo info = configuredTarget.get(PROVIDER);
        assertThat(JavaSkylarkApiTest.artifactFilesNames(info.getTransitiveRuntimeJars().toCollection(Artifact.class))).containsExactly("liba.jar");
        assertThat(JavaSkylarkApiTest.artifactFilesNames(info.getTransitiveSourceJars())).containsExactly("liba-src.jar", "libb-src.jar");
        assertThat(JavaSkylarkApiTest.artifactFilesNames(info.getTransitiveCompileTimeJars().toCollection(Artifact.class))).containsExactly("liba-hjar.jar", "libb-hjar.jar");
    }

    @Test
    public void testCompileOutputJarNotInRuntimePathWithoutAnySourcesDefined() throws Exception {
        writeBuildFileForJavaToolchain();
        scratch.file("foo/java_custom_library.bzl", "def _impl(ctx):", "  output_jar = ctx.actions.declare_file('lib%s.jar' % ctx.label.name)", "  exports = [export[JavaInfo] for export in ctx.attr.exports]", "  compilation_provider = java_common.compile(", "    ctx,", "    source_files = ctx.files.srcs,", "    output = output_jar,", "    exports = exports,", "    java_toolchain = ctx.attr._java_toolchain[java_common.JavaToolchainInfo],", "    host_javabase = ctx.attr._host_javabase[java_common.JavaRuntimeInfo]", "  )", "  return struct(", "    providers = [compilation_provider]", "  )", "java_custom_library = rule(", "  implementation = _impl,", "  attrs = {", "    'srcs': attr.label_list(allow_files=['.java']),", "    'exports': attr.label_list(),", "    '_java_toolchain': attr.label(default = Label('//java/com/google/test:toolchain')),", "    '_host_javabase': attr.label(", (("        default = Label('" + (JavaSkylarkApiTest.HOST_JAVA_RUNTIME_LABEL)) + "'))"), "  },", "  fragments = ['java'],", ")");
        scratch.file("foo/BUILD", "load(':java_custom_library.bzl', 'java_custom_library')", "java_library(name = 'b', srcs = ['java/B.java'])", "java_custom_library(name = 'c', srcs = [], exports = [':b'])");
        ConfiguredTarget configuredTarget = getConfiguredTarget("//foo:c");
        JavaInfo info = configuredTarget.get(PROVIDER);
        assertThat(JavaSkylarkApiTest.artifactFilesNames(info.getTransitiveRuntimeJars().toCollection(Artifact.class))).containsExactly("libb.jar");
        assertThat(JavaSkylarkApiTest.artifactFilesNames(info.getTransitiveCompileTimeJars().toCollection(Artifact.class))).containsExactly("libb-hjar.jar");
        JavaRuleOutputJarsProvider outputs = info.getOutputJars();
        assertThat(outputs.getOutputJars()).hasSize(1);
        OutputJar output = outputs.getOutputJars().get(0);
        assertThat(output.getClassJar().getFilename()).isEqualTo("libc.jar");
        assertThat(output.getIJar()).isNull();
    }

    @Test
    public void testDisallowLegacyJavaProvider() throws Exception {
        setSkylarkSemanticsOptions("--incompatible_disallow_legacy_java_provider");
        scratch.file("foo/custom_rule.bzl", "def _impl(ctx):", "  ctx.attr.java_lib.java.source_jars", "java_custom_library = rule(", "  implementation = _impl,", "  attrs = {", "    'java_lib': attr.label(),", "   },", ")");
        scratch.file("foo/BUILD", "load(':custom_rule.bzl', 'java_custom_library')", "java_library(name = 'java_lib', srcs = ['java/A.java'])", "java_custom_library(name = 'custom_lib', java_lib = ':java_lib')");
        checkError("//foo:custom_lib", ("The .java provider is deprecated and cannot be used " + "when --incompatible_disallow_legacy_java_provider is set."));
    }

    @Test
    public void testConfiguredTargetHostJavabase() throws Exception {
        writeBuildFileForJavaToolchain();
        setSkylarkSemanticsOptions("--incompatible_use_toolchain_providers_in_java_common=true");
        scratch.file("a/BUILD", "load(':rule.bzl', 'jrule')", "java_runtime(name='jvm', srcs=[], java_home='/foo/bar')", "jrule(name='r', srcs=['S.java'])");
        scratch.file("a/rule.bzl", "def _impl(ctx):", "  output_jar = ctx.actions.declare_file('lib' + ctx.label.name + '.jar')", "  java_common.compile(", "    ctx,", "    source_files = ctx.files.srcs,", "    output = output_jar,", "    java_toolchain = ctx.attr._java_toolchain[java_common.JavaToolchainInfo],", "    host_javabase = ctx.attr._host_javabase", "  )", "  return struct()", "jrule = rule(", "  implementation = _impl,", "  outputs = {", "    'my_output': 'lib%{name}.jar'", "  },", "  attrs = {", "    'srcs': attr.label_list(allow_files=['.java']),", "    '_java_toolchain': attr.label(default = Label('//java/com/google/test:toolchain')),", "    '_host_javabase': attr.label(default = Label('//a:jvm'))", "  },", "  fragments = ['java'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        getConfiguredTarget("//a:r");
        assertContainsEvent("java_common.JavaRuntimeInfo");
    }

    @Test
    public void testConfiguredTargetToolchain() throws Exception {
        writeBuildFileForJavaToolchain();
        setSkylarkSemanticsOptions("--incompatible_use_toolchain_providers_in_java_common=true");
        scratch.file("a/BUILD", "load(':rule.bzl', 'jrule')", "java_runtime(name='jvm', srcs=[], java_home='/foo/bar')", "jrule(name='r', srcs=['S.java'])");
        scratch.file("a/rule.bzl", "def _impl(ctx):", "  output_jar = ctx.actions.declare_file('lib' + ctx.label.name + '.jar')", "  java_common.compile(", "    ctx,", "    source_files = ctx.files.srcs,", "    output = output_jar,", "    java_toolchain = ctx.attr._java_toolchain,", "    host_javabase = ctx.attr._host_javabase[java_common.JavaRuntimeInfo]", "  )", "  return struct()", "jrule = rule(", "  implementation = _impl,", "  outputs = {", "    'my_output': 'lib%{name}.jar'", "  },", "  attrs = {", "    'srcs': attr.label_list(allow_files=['.java']),", "    '_java_toolchain': attr.label(default = Label('//java/com/google/test:toolchain')),", "    '_host_javabase': attr.label(default = Label('//a:jvm'))", "  },", "  fragments = ['java'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        getConfiguredTarget("//a:r");
        assertContainsEvent("java_common.JavaToolchainInfo");
    }

    @Test
    public void defaultJavacOpts() throws Exception {
        writeBuildFileForJavaToolchain();
        scratch.file("a/rule.bzl", "def _impl(ctx):", "  return struct(", "    javac_opts = java_common.default_javac_opts(", "        java_toolchain = ctx.attr._java_toolchain[java_common.JavaToolchainInfo])", "    )", "get_javac_opts = rule(", "  _impl,", "  attrs = {", "    '_java_toolchain': attr.label(default = Label('//java/com/google/test:toolchain')),", "  }", ");");
        scratch.file("a/BUILD", "load(':rule.bzl', 'get_javac_opts')", "get_javac_opts(name='r')");
        ConfiguredTarget r = getConfiguredTarget("//a:r");
        // Use an extra variable in order to suppress the warning.
        @SuppressWarnings("unchecked")
        SkylarkList<String> javacopts = ((SkylarkList<String>) (r.get("javac_opts")));
        assertThat(String.join(" ", javacopts)).contains("-source 6 -target 6");
    }

    @Test
    public void defaultJavacOpts_toolchainProvider() throws Exception {
        writeBuildFileForJavaToolchain();
        scratch.file("a/rule.bzl", "def _impl(ctx):", "  return struct(", "    javac_opts = java_common.default_javac_opts(", "        java_toolchain = ctx.attr._java_toolchain[java_common.JavaToolchainInfo])", "    )", "get_javac_opts = rule(", "  _impl,", "  attrs = {", "    '_java_toolchain': attr.label(default = Label('//java/com/google/test:toolchain')),", "  }", ");");
        scratch.file("a/BUILD", "load(':rule.bzl', 'get_javac_opts')", "get_javac_opts(name='r')");
        ConfiguredTarget r = getConfiguredTarget("//a:r");
        // Use an extra variable in order to suppress the warning.
        @SuppressWarnings("unchecked")
        SkylarkList<String> javacopts = ((SkylarkList<String>) (r.get("javac_opts")));
        assertThat(String.join(" ", javacopts)).contains("-source 6 -target 6");
    }

    @Test
    public void testIsToolchainResolutionEnabled_disabled() throws Exception {
        useConfiguration("--experimental_use_toolchain_resolution_for_java_rules=false");
        assertThat(toolchainResolutionEnabled()).isFalse();
    }

    @Test
    public void testIsToolchainResolutionEnabled_enabled() throws Exception {
        useConfiguration("--experimental_use_toolchain_resolution_for_java_rules");
        assertThat(toolchainResolutionEnabled()).isTrue();
    }

    @Test
    public void testJavaRuntimeProviderFiles() throws Exception {
        scratch.file("a/a.txt", "hello");
        scratch.file("a/BUILD", "load(':rule.bzl', 'jrule')", "java_runtime(name='jvm', srcs=['a.txt'], java_home='foo/bar')", "java_runtime_alias(name='alias')", "jrule(name='r')");
        scratch.file("a/rule.bzl", "def _impl(ctx):", "  provider = ctx.attr._java_runtime[java_common.JavaRuntimeInfo]", "  return struct(", "    files = provider.files,", "  )", "jrule = rule(_impl, attrs = { '_java_runtime': attr.label(default=Label('//a:alias'))})");
        useConfiguration("--javabase=//a:jvm");
        ConfiguredTarget ct = getConfiguredTarget("//a:r");
        @SuppressWarnings("unchecked")
        SkylarkNestedSet files = ((SkylarkNestedSet) (ct.get("files")));
        assertThat(ActionsTestUtil.prettyArtifactNames(files.toCollection(Artifact.class))).containsExactly("a/a.txt");
    }
}

