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


import AppleToolchain.DEFAULT_WARNINGS;
import J2ObjcLibrary.NO_ENTRY_CLASS_ERROR_MSG;
import ObjcProvider.HEADER;
import ObjcProvider.INCLUDE;
import ObjcProvider.LIBRARY;
import ObjcProvider.MODULE_MAP;
import ObjcProvider.SKYLARK_CONSTRUCTOR;
import ObjcProvider.SOURCE;
import ObjcProvider.UMBRELLA_HEADER;
import RepositoryName.MAIN;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.devtools.build.lib.actions.AbstractAction;
import com.google.devtools.build.lib.actions.Action;
import com.google.devtools.build.lib.actions.ActionAnalysisMetadata;
import com.google.devtools.build.lib.actions.ActionExecutionContext;
import com.google.devtools.build.lib.actions.ActionInputHelper;
import com.google.devtools.build.lib.actions.ActionInputPrefetcher;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.Artifact.SpecialArtifact;
import com.google.devtools.build.lib.actions.Artifact.TreeFileArtifact;
import com.google.devtools.build.lib.actions.CommandAction;
import com.google.devtools.build.lib.actions.util.ActionsTestUtil;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.analysis.actions.SpawnAction;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.rules.apple.AppleConfiguration;
import com.google.devtools.build.lib.rules.apple.AppleToolchain;
import com.google.devtools.build.lib.rules.cpp.CppModuleMapAction;
import com.google.devtools.build.lib.rules.cpp.UmbrellaHeaderAction;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.TestConstants;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import java.io.ByteArrayOutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit test for Java source file translation into ObjC in {@link com.google.devtools.build.lib.rules.java.JavaLibrary} and translated file compilation and linking
 * in {@link ObjcBinary} and {@link J2ObjcLibrary}.
 */
@RunWith(JUnit4.class)
public class BazelJ2ObjcLibraryTest extends J2ObjcLibraryTest {
    @Test
    public void testJ2ObjCInformationExportedFromJ2ObjcLibrary() throws Exception {
        ConfiguredTarget j2objcLibraryTarget = getConfiguredTarget("//java/com/google/dummy/test:transpile");
        ObjcProvider provider = j2objcLibraryTarget.get(SKYLARK_CONSTRUCTOR);
        assertThat(Artifact.toRootRelativePaths(provider.get(LIBRARY))).containsExactly(((TestConstants.TOOLS_REPOSITORY_PATH_PREFIX) + "third_party/java/j2objc/libjre_core_lib.a"), "java/com/google/dummy/test/libtest_j2objc.a");
        assertThat(Artifact.toRootRelativePaths(provider.get(HEADER))).containsExactly(((TestConstants.TOOLS_REPOSITORY_PATH_PREFIX) + "third_party/java/j2objc/jre_core.h"), "java/com/google/dummy/test/_j2objc/test/java/com/google/dummy/test/test.h");
        String execPath = (getConfiguration(j2objcLibraryTarget).getBinDirectory(MAIN).getExecPath()) + "/";
        assertThat(Iterables.transform(provider.get(INCLUDE), PathFragment::getSafePathString)).containsExactly((execPath + "java/com/google/dummy/test/_j2objc/test"));
    }

    @Test
    public void testJ2ObjCInformationExportedWithGeneratedJavaSources() throws Exception {
        scratch.file("java/com/google/test/in.txt");
        scratch.file("java/com/google/test/BUILD", "package(default_visibility=['//visibility:public'])", "genrule(", "    name = 'dummy_gen',", "    srcs = ['in.txt'],", "    outs = ['test.java'],", "    cmd = 'dummy'", ")", "", "java_library(", "    name = 'test',", "    srcs = [':test.java']", ")", "j2objc_library(", "    name = 'transpile',", "    deps = ['test'],", ")");
        ConfiguredTarget target = getConfiguredTarget("//java/com/google/test:transpile");
        ObjcProvider provider = target.get(SKYLARK_CONSTRUCTOR);
        String genfilesFragment = getConfiguration(target).getGenfilesFragment().toString();
        assertThat(Artifact.toRootRelativePaths(provider.get(LIBRARY))).containsExactly(((TestConstants.TOOLS_REPOSITORY_PATH_PREFIX) + "third_party/java/j2objc/libjre_core_lib.a"), "java/com/google/test/libtest_j2objc.a");
        assertThat(Artifact.toRootRelativePaths(provider.get(HEADER))).containsExactly(((TestConstants.TOOLS_REPOSITORY_PATH_PREFIX) + "third_party/java/j2objc/jre_core.h"), (("java/com/google/test/_j2objc/test/" + genfilesFragment) + "/java/com/google/test/test.h"));
        String execPath = (getConfiguration(target).getBinDirectory(MAIN).getExecPath()) + "/";
        assertThat(Iterables.transform(provider.get(INCLUDE), PathFragment::getSafePathString)).containsExactly(((execPath + "java/com/google/test/_j2objc/test/") + genfilesFragment), (execPath + "java/com/google/test/_j2objc/test"));
    }

    @Test
    public void testJ2ObjcProtoRuntimeLibraryAndHeadersExported() throws Exception {
        scratch.file("java/com/google/dummy/test/proto/test.java");
        scratch.file("java/com/google/dummy/test/proto/test.proto");
        scratch.file("java/com/google/dummy/test/proto/BUILD", "package(default_visibility=['//visibility:public'])", "proto_library(", "    name = 'test_proto',", "    srcs = ['test.proto'],", ")", "java_proto_library(", "    name = 'test_java_proto',", "    deps = [':test_proto'],", ")", "java_library(", "    name = 'test',", "    srcs = ['test.java'],", "    deps = [':test_java_proto']", ")", "j2objc_library(", "    name = 'transpile',", "    deps = ['test']", ")");
        ConfiguredTarget j2objcLibraryTarget = getConfiguredTarget("//java/com/google/dummy/test/proto:transpile");
        ObjcProvider provider = j2objcLibraryTarget.get(SKYLARK_CONSTRUCTOR);
        assertThat(Artifact.toRootRelativePaths(provider.get(LIBRARY))).containsExactly(((TestConstants.TOOLS_REPOSITORY_PATH_PREFIX) + "third_party/java/j2objc/libjre_core_lib.a"), ((TestConstants.TOOLS_REPOSITORY_PATH_PREFIX) + "third_party/java/j2objc/libproto_runtime.a"), "java/com/google/dummy/test/proto/libtest_j2objc.a", "java/com/google/dummy/test/proto/libtest_proto_j2objc.a");
        assertThat(Artifact.toRootRelativePaths(provider.get(HEADER))).containsExactly(((TestConstants.TOOLS_REPOSITORY_PATH_PREFIX) + "third_party/java/j2objc/jre_core.h"), ((TestConstants.TOOLS_REPOSITORY_PATH_PREFIX) + "third_party/java/j2objc/runtime.h"), "java/com/google/dummy/test/proto/test.j2objc.pb.h", "java/com/google/dummy/test/proto/_j2objc/test/java/com/google/dummy/test/proto/test.h");
    }

    @Test
    public void testJ2ObjcHeaderMapExportedInJavaLibrary() throws Exception {
        scratch.file("java/com/google/transpile/BUILD", "java_library(", "    name = 'dummy',", "    srcs = ['dummy.java']", ")");
        ConfiguredTarget target = getJ2ObjCAspectConfiguredTarget("//java/com/google/transpile:dummy");
        J2ObjcMappingFileProvider provider = target.getProvider(J2ObjcMappingFileProvider.class);
        assertThat(Iterables.getOnlyElement(provider.getHeaderMappingFiles()).getRootRelativePath().toString()).isEqualTo("java/com/google/transpile/dummy.mapping.j2objc");
    }

    @Test
    public void testDepsJ2ObjcHeaderMapExportedInJavaLibraryWithNoSourceFile() throws Exception {
        scratch.file("java/com/google/transpile/BUILD", "java_library(", "    name = 'dummy',", "    exports = ['//java/com/google/dep:dep'],", ")");
        scratch.file("java/com/google/dep/BUILD", "java_library(", "    name = 'dep',", "    srcs = ['dummy.java'],", ")");
        ConfiguredTarget target = getJ2ObjCAspectConfiguredTarget("//java/com/google/transpile:dummy");
        J2ObjcMappingFileProvider provider = target.getProvider(J2ObjcMappingFileProvider.class);
        assertThat(Iterables.getOnlyElement(provider.getHeaderMappingFiles()).getRootRelativePath().toString()).isEqualTo("java/com/google/dep/dep.mapping.j2objc");
    }

    @Test
    public void testJ2ObjcProtoClassMappingFilesExportedInJavaLibrary() throws Exception {
        scratch.file("java/com/google/dummy/test/proto/test.java");
        scratch.file("java/com/google/dummy/test/proto/test.proto");
        scratch.file("java/com/google/dummy/test/proto/BUILD", "package(default_visibility=['//visibility:public'])", "proto_library(", "    name = 'test_proto',", "    srcs = ['test.proto'],", ")", "java_proto_library(", "    name = 'test_java_proto',", "    deps = [':test_proto'],", ")", "java_library(", "    name = 'test',", "    srcs = ['test.java'],", "    deps = [':test_java_proto']", ")");
        ConfiguredTarget target = getJ2ObjCAspectConfiguredTarget("//java/com/google/dummy/test/proto:test");
        J2ObjcMappingFileProvider provider = target.getProvider(J2ObjcMappingFileProvider.class);
        Artifact classMappingFile = getGenfilesArtifact("test.clsmap.properties", getConfiguredTarget("//java/com/google/dummy/test/proto:test_proto", getAppleCrosstoolConfiguration()), getJ2ObjcAspect());
        assertThat(provider.getClassMappingFiles()).containsExactly(classMappingFile);
    }

    @Test
    public void testJavaProtoLibraryWithProtoLibrary() throws Exception {
        scratch.file("x/BUILD", "proto_library(", "    name = 'test_proto',", "    srcs = ['test.proto'],", ")", "java_proto_library(", "    name = 'test_java_proto',", "    deps = [':test_proto'],", ")", "java_library(", "    name = 'test',", "    srcs = ['test.java'],", "    deps = [':test_java_proto']", ")");
        ConfiguredTarget target = getJ2ObjCAspectConfiguredTarget("//x:test");
        ConfiguredTarget test = getConfiguredTarget("//x:test_proto", getAppleCrosstoolConfiguration());
        J2ObjcMappingFileProvider provider = target.getProvider(J2ObjcMappingFileProvider.class);
        Artifact classMappingFile = getGenfilesArtifact("test.clsmap.properties", test, getJ2ObjcAspect());
        assertThat(provider.getClassMappingFiles()).containsExactly(classMappingFile);
        ObjcProvider objcProvider = target.get(SKYLARK_CONSTRUCTOR);
        Artifact headerFile = getGenfilesArtifact("test.j2objc.pb.h", test, getJ2ObjcAspect());
        Artifact sourceFile = getGenfilesArtifact("test.j2objc.pb.m", test, getJ2ObjcAspect());
        assertThat(objcProvider.get(HEADER)).contains(headerFile);
        assertThat(objcProvider.get(SOURCE)).contains(sourceFile);
    }

    @Test
    public void testJavaProtoLibraryWithProtoLibrary_external() throws Exception {
        scratch.file("/bla/WORKSPACE");
        // Create the rule '@bla//foo:test_proto'.
        scratch.file("/bla/foo/BUILD", "package(default_visibility=['//visibility:public'])", "proto_library(", "    name = 'test_proto',", "    srcs = ['test.proto'],", ")", "java_proto_library(", "    name = 'test_java_proto',", "    deps = [':test_proto'])", "");
        String existingWorkspace = new String(FileSystemUtils.readContentAsLatin1(rootDirectory.getRelative("WORKSPACE")));
        scratch.overwriteFile("WORKSPACE", "local_repository(name = 'bla', path = '/bla/')", existingWorkspace);
        invalidatePackages();// A dash of magic to re-evaluate the WORKSPACE file.

        scratch.file("x/BUILD", "", "java_library(", "    name = 'test',", "    srcs = ['test.java'],", "    deps = ['@bla//foo:test_java_proto'])");
        ConfiguredTarget target = getJ2ObjCAspectConfiguredTarget("//x:test");
        ConfiguredTarget test = getConfiguredTarget("@bla//foo:test_proto", getAppleCrosstoolConfiguration());
        J2ObjcMappingFileProvider provider = target.getProvider(J2ObjcMappingFileProvider.class);
        Artifact classMappingFile = getGenfilesArtifact("../external/bla/foo/test.clsmap.properties", test, getJ2ObjcAspect());
        assertThat(provider.getClassMappingFiles()).containsExactly(classMappingFile);
        ObjcProvider objcProvider = target.get(SKYLARK_CONSTRUCTOR);
        Artifact headerFile = getGenfilesArtifact("../external/bla/foo/test.j2objc.pb.h", test, getJ2ObjcAspect());
        Artifact sourceFile = getGenfilesArtifact("../external/bla/foo/test.j2objc.pb.m", test, getJ2ObjcAspect());
        assertThat(objcProvider.get(HEADER)).contains(headerFile);
        assertThat(objcProvider.get(SOURCE)).contains(sourceFile);
        assertThat(objcProvider.get(INCLUDE)).contains(getConfiguration(target).getGenfilesFragment().getRelative("external/bla"));
    }

    @Test
    public void testJ2ObjcInfoExportedInJavaImport() throws Exception {
        scratch.file("java/com/google/transpile/BUILD", "java_import(", "    name = 'dummy',", "    jars = ['dummy.jar'],", "    srcjar = 'dummy.srcjar',", ")");
        ConfiguredTarget target = getJ2ObjCAspectConfiguredTarget("//java/com/google/transpile:dummy");
        J2ObjcMappingFileProvider provider = target.getProvider(J2ObjcMappingFileProvider.class);
        assertThat(Iterables.getOnlyElement(provider.getHeaderMappingFiles()).getRootRelativePath().toString()).isEqualTo("java/com/google/transpile/dummy.mapping.j2objc");
        assertThat(provider.getClassMappingFiles()).isEmpty();
    }

    @Test
    public void testMissingEntryClassesError() throws Exception {
        useConfiguration("--j2objc_dead_code_removal");
        checkError("java/com/google/dummy", "transpile", NO_ENTRY_CLASS_ERROR_MSG, "j2objc_library(name = 'transpile', deps = ['//java/com/google/dummy/test:test'])");
    }

    @Test
    public void testNoJ2ObjcDeadCodeRemovalActionWithoutOptFlag() throws Exception {
        useConfiguration("--noj2objc_dead_code_removal");
        addSimpleJ2ObjcLibraryWithEntryClasses();
        addSimpleBinaryTarget("//java/com/google/app/test:transpile");
        Artifact expectedPrunedSource = getBinArtifact(("_j2objc_pruned/app/java/com/google/app/test/_j2objc/test/" + "java/com/google/app/test/test_pruned.m"), getConfiguredTarget("//app:app"));
        assertThat(getGeneratingAction(expectedPrunedSource)).isNull();
    }

    @Test
    public void testExplicitJreDeps() throws Exception {
        ConfiguredTarget j2objcLibraryTarget = getConfiguredTarget("//java/com/google/dummy/test:transpile");
        ObjcProvider provider = j2objcLibraryTarget.get(SKYLARK_CONSTRUCTOR);
        // jre_io_lib and jre_emul_lib should be excluded.
        assertThat(Artifact.toRootRelativePaths(provider.get(LIBRARY))).containsExactly(((TestConstants.TOOLS_REPOSITORY_PATH_PREFIX) + "third_party/java/j2objc/libjre_core_lib.a"), "java/com/google/dummy/test/libtest_j2objc.a");
        assertThat(Artifact.toRootRelativePaths(provider.get(HEADER))).containsExactly(((TestConstants.TOOLS_REPOSITORY_PATH_PREFIX) + "third_party/java/j2objc/jre_core.h"), "java/com/google/dummy/test/_j2objc/test/java/com/google/dummy/test/test.h");
    }

    @Test
    public void testTranspilationActionTreeArtifactOutputsFromSourceJar() throws Exception {
        useConfiguration("--ios_cpu=i386", "--ios_minimum_os=1.0");
        scratch.file("java/com/google/transpile/dummy.java");
        scratch.file("java/com/google/transpile/dummyjar.srcjar");
        scratch.file("java/com/google/transpile/BUILD", "java_library(", "    name = 'dummy',", "    srcs = ['dummy.java', 'dummyjar.srcjar'],", ")");
        ConfiguredTarget target = getJ2ObjCAspectConfiguredTarget("//java/com/google/transpile:dummy");
        ObjcProvider provider = target.get(SKYLARK_CONSTRUCTOR);
        Artifact srcJarSources = ActionsTestUtil.getFirstArtifactEndingWith(provider.get(SOURCE), "source_files");
        Artifact srcJarHeaders = ActionsTestUtil.getFirstArtifactEndingWith(provider.get(HEADER), "header_files");
        assertThat(srcJarSources.getRootRelativePathString()).isEqualTo("java/com/google/transpile/_j2objc/src_jar_files/dummy/source_files");
        assertThat(srcJarHeaders.getRootRelativePathString()).isEqualTo("java/com/google/transpile/_j2objc/src_jar_files/dummy/header_files");
        assertThat(srcJarSources.isTreeArtifact()).isTrue();
        assertThat(srcJarHeaders.isTreeArtifact()).isTrue();
    }

    @Test
    public void testGeneratedTreeArtifactFromGenJar() throws Exception {
        useConfiguration("--ios_cpu=i386", "--ios_minimum_os=1.0");
        addSimpleJ2ObjcLibraryWithJavaPlugin();
        ConfiguredTarget j2objcLibraryTarget = getConfiguredTarget("//java/com/google/app/test:transpile");
        ObjcProvider provider = j2objcLibraryTarget.get(SKYLARK_CONSTRUCTOR);
        Artifact headers = ActionsTestUtil.getFirstArtifactEndingWith(provider.get(HEADER), "header_files");
        Artifact sources = ActionsTestUtil.getFirstArtifactEndingWith(provider.get(SOURCE), "source_files");
        assertThat(headers.isTreeArtifact()).isTrue();
        assertThat(sources.isTreeArtifact()).isTrue();
        SpawnAction j2objcAction = ((SpawnAction) (getGeneratingAction(headers)));
        assertThat(j2objcAction.getOutputs()).containsAllOf(headers, sources);
        assertContainsSublist(ImmutableList.copyOf(paramFileArgsForAction(j2objcAction)), ImmutableList.of("--output_gen_source_dir", sources.getExecPathString(), "--output_gen_header_dir", headers.getExecPathString()));
    }

    @Test
    public void testJ2ObjcHeaderMappingAction() throws Exception {
        scratch.file("java/com/google/transpile/BUILD", "java_library(", "    name = 'lib1',", "    srcs = ['libOne.java', 'jar.srcjar'],", "    deps = [':lib2']", ")", "", "java_library(", "    name = 'lib2',", "    srcs = ['libTwo.java'],", ")");
        ConfiguredTarget target = getJ2ObjCAspectConfiguredTarget("//java/com/google/transpile:lib1");
        J2ObjcMappingFileProvider mappingFileProvider = target.getProvider(J2ObjcMappingFileProvider.class);
        assertThat(Artifact.toRootRelativePaths(mappingFileProvider.getHeaderMappingFiles())).containsExactly("java/com/google/transpile/lib1.mapping.j2objc", "java/com/google/transpile/lib2.mapping.j2objc");
        Artifact mappingFile = ActionsTestUtil.getFirstArtifactEndingWith(mappingFileProvider.getHeaderMappingFiles(), "lib1.mapping.j2objc");
        SpawnAction headerMappingAction = ((SpawnAction) (getGeneratingAction(mappingFile)));
        String execPath = (getConfiguration(target).getBinDirectory(MAIN).getExecPath()) + "/";
        assertThat(Artifact.toRootRelativePaths(headerMappingAction.getInputs())).containsAllOf("java/com/google/transpile/libOne.java", "java/com/google/transpile/jar.srcjar");
        assertThat(headerMappingAction.getArguments()).containsExactly(((TestConstants.TOOLS_REPOSITORY_PATH_PREFIX) + "tools/j2objc/j2objc_header_map.py"), "--source_files", "java/com/google/transpile/libOne.java", "--source_jars", "java/com/google/transpile/jar.srcjar", "--output_mapping_file", (execPath + "java/com/google/transpile/lib1.mapping.j2objc")).inOrder();
    }

    @Test
    public void testJ2ObjcInformationExportedFromObjcLibrary() throws Exception {
        scratch.file("app/lib.m");
        scratch.file("app/BUILD", "package(default_visibility=['//visibility:public'])", "objc_library(", "    name = 'lib',", "    srcs = ['lib.m'],", "    deps = ['//java/com/google/dummy/test:transpile'],", ")");
        ConfiguredTarget objcTarget = getConfiguredTarget("//app:lib");
        ObjcProvider provider = objcTarget.get(SKYLARK_CONSTRUCTOR);
        assertThat(Artifact.toRootRelativePaths(provider.get(LIBRARY))).containsExactly(((TestConstants.TOOLS_REPOSITORY_PATH_PREFIX) + "third_party/java/j2objc/libjre_core_lib.a"), "java/com/google/dummy/test/libtest_j2objc.a", "app/liblib.a");
        assertThat(Artifact.toRootRelativePaths(provider.get(HEADER))).containsExactly(((TestConstants.TOOLS_REPOSITORY_PATH_PREFIX) + "third_party/java/j2objc/jre_core.h"), "java/com/google/dummy/test/_j2objc/test/java/com/google/dummy/test/test.h");
        String execPath = (getConfiguration(objcTarget).getBinDirectory(MAIN).getExecPath()) + "/";
        assertThat(Iterables.transform(provider.get(INCLUDE), PathFragment::getSafePathString)).containsExactly((execPath + "java/com/google/dummy/test/_j2objc/test"));
    }

    @Test
    public void testJ2ObjcInfoExportedInObjcLibraryFromRuntimeDeps() throws Exception {
        scratch.file("app/lib.m");
        scratch.file("app/BUILD", "package(default_visibility=['//visibility:public'])", "", "java_library(", "    name = 'dummyOne',", "    srcs = ['dummyOne.java'],", ")", "java_library(", "    name = 'dummyTwo',", "    srcs = ['dummyTwo.java'],", "    runtime_deps = [':dummyOne'],", ")", "j2objc_library(", "    name = 'transpile',", "    deps = [':dummyTwo'],", ")", "objc_library(", "    name = 'lib',", "    srcs = ['lib.m'],", "    deps = ['//app:transpile'],", ")");
        ConfiguredTarget objcTarget = getConfiguredTarget("//app:lib");
        ObjcProvider provider = objcTarget.get(SKYLARK_CONSTRUCTOR);
        assertThat(Artifact.toRootRelativePaths(provider.get(LIBRARY))).containsExactly(((TestConstants.TOOLS_REPOSITORY_PATH_PREFIX) + "third_party/java/j2objc/libjre_core_lib.a"), "app/libdummyOne_j2objc.a", "app/libdummyTwo_j2objc.a", "app/liblib.a");
        assertThat(Artifact.toRootRelativePaths(provider.get(HEADER))).containsExactly(((TestConstants.TOOLS_REPOSITORY_PATH_PREFIX) + "third_party/java/j2objc/jre_core.h"), "app/_j2objc/dummyOne/app/dummyOne.h", "app/_j2objc/dummyTwo/app/dummyTwo.h");
        String execPath = (getConfiguration(objcTarget).getBinDirectory(MAIN).getExecPath()) + "/";
        assertThat(Iterables.transform(provider.get(INCLUDE), PathFragment::getSafePathString)).containsExactly((execPath + "app/_j2objc/dummyOne"), (execPath + "app/_j2objc/dummyTwo"));
    }

    @Test
    public void testJ2ObjcAppearsInLinkArgs() throws Exception {
        scratch.file("java/c/y/BUILD", "java_library(", "    name = 'ylib',", "    srcs = ['lib.java'],", ")");
        scratch.file("x/BUILD", "j2objc_library(", "    name = 'j2',", "    deps = [ '//java/c/y:ylib' ],", (("    jre_deps = [ '" + (TestConstants.TOOLS_REPOSITORY)) + "//third_party/java/j2objc:jre_io_lib' ],"), ")", "apple_binary(", "    name = 'test',", "    platform_type = 'ios',", "    deps = [':main_lib'],", ")", "objc_library(", "    name = 'main_lib',", "    srcs = ['test.m'],", "    deps = [':j2'],", ")");
        CommandAction linkAction = linkAction("//x:test");
        ConfiguredTarget target = getConfiguredTargetInAppleBinaryTransition("//x:test");
        String binDir = getConfiguration(target).getBinDirectory(MAIN).getExecPathString();
        // All jre libraries mus appear after java libraries in the link order.
        assertThat(paramFileArgsForAction(linkAction)).containsAllOf((binDir + "/java/c/y/libylib_j2objc.a"), (((binDir + "/") + (TestConstants.TOOLS_REPOSITORY_PATH_PREFIX)) + "third_party/java/j2objc/libjre_io_lib.a"), (((binDir + "/") + (TestConstants.TOOLS_REPOSITORY_PATH_PREFIX)) + "third_party/java/j2objc/libjre_core_lib.a")).inOrder();
    }

    @Test
    public void testArchiveLinkActionWithTreeArtifactFromGenJar() throws Exception {
        useConfiguration("--ios_cpu=i386", "--ios_minimum_os=1.0");
        addSimpleJ2ObjcLibraryWithJavaPlugin();
        Artifact archive = j2objcArchive("//java/com/google/app/test:transpile", "test");
        CommandAction archiveAction = ((CommandAction) (getGeneratingAction(archive)));
        Artifact objectFilesFromGenJar = ActionsTestUtil.getFirstArtifactEndingWith(archiveAction.getInputs(), "source_files");
        Artifact normalObjectFile = ActionsTestUtil.getFirstArtifactEndingWith(archiveAction.getInputs(), "test.o");
        // Test that the archive obj list param file contains the individual object files inside
        // the object file tree artifact.
        assertThat(paramFileCommandLineForAction(archiveAction).arguments(J2ObjcLibraryTest.DUMMY_ARTIFACT_EXPANDER)).containsExactly(((objectFilesFromGenJar.getExecPathString()) + "/children1"), ((objectFilesFromGenJar.getExecPathString()) + "/children2"), normalObjectFile.getExecPathString());
    }

    @Test
    public void testJ2ObjCCustomModuleMap() throws Exception {
        useConfiguration("--experimental_objc_enable_module_maps");
        scratch.file("java/com/google/transpile/dummy.java");
        scratch.file("java/com/google/transpile/BUILD", "package(default_visibility=['//visibility:public'])", "java_library(", "    name = 'dummy',", "    srcs = ['dummy.java'],", ")");
        ConfiguredTarget target = getJ2ObjCAspectConfiguredTarget("//java/com/google/transpile:dummy");
        ObjcProvider provider = target.get(SKYLARK_CONSTRUCTOR);
        Artifact moduleMap = ActionsTestUtil.getFirstArtifactEndingWith(provider.get(MODULE_MAP), "dummy.modulemaps/module.modulemap");
        Artifact umbrellaHeader = ActionsTestUtil.getFirstArtifactEndingWith(provider.get(UMBRELLA_HEADER), "dummy.modulemaps/umbrella.h");
        CppModuleMapAction moduleMapAction = ((CppModuleMapAction) (getGeneratingAction(moduleMap)));
        UmbrellaHeaderAction umbrellaHeaderAction = ((UmbrellaHeaderAction) (getGeneratingAction(umbrellaHeader)));
        ActionExecutionContext dummyActionExecutionContext = new ActionExecutionContext(null, null, ActionInputPrefetcher.NONE, actionKeyContext, null, null, null, ImmutableMap.<String, String>of(), ImmutableMap.of(), J2ObjcLibraryTest.DUMMY_ARTIFACT_EXPANDER, null, null);
        ByteArrayOutputStream moduleMapStream = new ByteArrayOutputStream();
        ByteArrayOutputStream umbrellaHeaderStream = new ByteArrayOutputStream();
        moduleMapAction.newDeterministicWriter(dummyActionExecutionContext).writeOutputFile(moduleMapStream);
        umbrellaHeaderAction.newDeterministicWriter(dummyActionExecutionContext).writeOutputFile(umbrellaHeaderStream);
        String moduleMapContent = moduleMapStream.toString();
        String umbrellaHeaderContent = umbrellaHeaderStream.toString();
        assertThat(moduleMapContent).contains("umbrella header \"umbrella.h\"");
        assertThat(umbrellaHeaderContent).contains("/dummy.h");
        assertThat(umbrellaHeaderContent).contains("#include");
    }

    @Test
    public void testModuleMapFromGenJarTreeArtifact() throws Exception {
        useConfiguration("--ios_cpu=i386", "--ios_minimum_os=1.0");
        addSimpleJ2ObjcLibraryWithJavaPlugin();
        ConfiguredTarget j2objcLibraryTarget = getConfiguredTarget("//java/com/google/app/test:transpile");
        ObjcProvider provider = j2objcLibraryTarget.get(SKYLARK_CONSTRUCTOR);
        Artifact moduleMap = ActionsTestUtil.getFirstArtifactEndingWith(provider.get(MODULE_MAP), "test.modulemaps/module.modulemap");
        Artifact umbrellaHeader = ActionsTestUtil.getFirstArtifactEndingWith(provider.get(UMBRELLA_HEADER), "test.modulemaps/umbrella.h");
        CppModuleMapAction moduleMapAction = ((CppModuleMapAction) (getGeneratingAction(moduleMap)));
        UmbrellaHeaderAction umbrellaHeaderAction = ((UmbrellaHeaderAction) (getGeneratingAction(umbrellaHeader)));
        Artifact headers = ActionsTestUtil.getFirstArtifactEndingWith(provider.get(HEADER), "header_files");
        // Test that the module map action contains the header tree artifact as both the public header
        // and part of the action inputs.
        assertThat(moduleMapAction.getPublicHeaders()).contains(headers);
        assertThat(moduleMapAction.getInputs()).contains(headers);
        ActionExecutionContext dummyActionExecutionContext = new ActionExecutionContext(null, null, ActionInputPrefetcher.NONE, actionKeyContext, null, null, null, ImmutableMap.of(), ImmutableMap.of(), J2ObjcLibraryTest.DUMMY_ARTIFACT_EXPANDER, null, null);
        ByteArrayOutputStream moduleMapStream = new ByteArrayOutputStream();
        ByteArrayOutputStream umbrellaHeaderStream = new ByteArrayOutputStream();
        moduleMapAction.newDeterministicWriter(dummyActionExecutionContext).writeOutputFile(moduleMapStream);
        umbrellaHeaderAction.newDeterministicWriter(dummyActionExecutionContext).writeOutputFile(umbrellaHeaderStream);
        String moduleMapContent = moduleMapStream.toString();
        String umbrellaHeaderContent = umbrellaHeaderStream.toString();
        // Test that the module map content contains the individual headers inside the header tree
        // artifact.
        assertThat(moduleMapContent).contains("umbrella header \"umbrella.h\"");
        assertThat(umbrellaHeaderContent).contains(((headers.getExecPathString()) + "/children1"));
        assertThat(umbrellaHeaderContent).contains(((headers.getExecPathString()) + "/children2"));
    }

    @Test
    public void testJ2ObjCFullyLinkAction() throws Exception {
        AbstractAction linkAction = ((AbstractAction) (getGeneratingActionForLabel("//java/com/google/dummy/test:transpile_fully_linked.a")));
        String fullyLinkBinaryPath = Iterables.getOnlyElement(linkAction.getOutputs()).getExecPathString();
        assertThat(fullyLinkBinaryPath).contains("transpile_fully_linked.a");
    }

    @Test
    public void testObjcCompileAction() throws Exception {
        Artifact archive = j2objcArchive("//java/com/google/dummy/test:transpile", "test");
        CommandAction compileAction = getObjcCompileAction(archive, "test.o");
        assertThat(Artifact.toRootRelativePaths(compileAction.getPossibleInputsForTesting())).containsAllOf(((TestConstants.TOOLS_REPOSITORY_PATH_PREFIX) + "third_party/java/j2objc/jre_core.h"), "java/com/google/dummy/test/_j2objc/test/java/com/google/dummy/test/test.h", "java/com/google/dummy/test/_j2objc/test/java/com/google/dummy/test/test.m");
        assertThat(compileAction.getArguments()).containsAllOf("-fno-objc-arc", "-fno-strict-overflow");
    }

    @Test
    public void testJ2ObjcSourcesCompilationAndLinking() throws Exception {
        addSimpleBinaryTarget("//java/com/google/dummy/test:transpile");
        checkObjcArchiveAndLinkActions("//app:app", "libtest_j2objc.a", "test.o", ImmutableList.of(((TestConstants.TOOLS_REPOSITORY_PATH_PREFIX) + "third_party/java/j2objc/jre_core.h"), "java/com/google/dummy/test/_j2objc/test/java/com/google/dummy/test/test.h", "java/com/google/dummy/test/_j2objc/test/java/com/google/dummy/test/test.m"));
    }

    @Test
    public void testNestedJ2ObjcLibraryDeps() throws Exception {
        scratch.file("java/com/google/dummy/dummy.java");
        scratch.file("java/com/google/dummy/BUILD", "package(default_visibility=['//visibility:public'])", "java_library(", "    name = 'dummy',", "    srcs = ['dummy.java'],", ")", "", "j2objc_library(", "    name = 'transpile',", "    deps = [", "        ':dummy',", "        '//java/com/google/dummy/test:transpile',", "    ])");
        addSimpleBinaryTarget("//java/com/google/dummy:transpile");
        checkObjcArchiveAndLinkActions("//app:app", "libtest_j2objc.a", "test.o", ImmutableList.of(((TestConstants.TOOLS_REPOSITORY_PATH_PREFIX) + "third_party/java/j2objc/jre_core.h"), "java/com/google/dummy/test/_j2objc/test/java/com/google/dummy/test/test.h", "java/com/google/dummy/test/_j2objc/test/java/com/google/dummy/test/test.m"));
        checkObjcArchiveAndLinkActions("//app:app", "libdummy_j2objc.a", "dummy.o", ImmutableList.of(((TestConstants.TOOLS_REPOSITORY_PATH_PREFIX) + "third_party/java/j2objc/jre_core.h"), "java/com/google/dummy/_j2objc/dummy/java/com/google/dummy/dummy.h", "java/com/google/dummy/_j2objc/dummy/java/com/google/dummy/dummy.m"));
    }

    // Tests that a j2objc library can acquire java library information from a skylark rule target.
    @Test
    public void testJ2ObjcLibraryDepThroughSkylarkRule() throws Exception {
        scratch.file("examples/inner.java");
        scratch.file("examples/outer.java");
        scratch.file("examples/fake_rule.bzl", "def _fake_rule_impl(ctx):", "  myProvider = ctx.attr.deps[0][JavaInfo]", "  return struct(providers = [myProvider])", "", "fake_rule = rule(", "  implementation = _fake_rule_impl,", "  attrs = {'deps': attr.label_list()},", "  provides = [JavaInfo],", ")");
        scratch.file("examples/BUILD", "package(default_visibility=['//visibility:public'])", "load('//examples:fake_rule.bzl', 'fake_rule')", "java_library(", "    name = 'inner',", "    srcs = ['inner.java'],", ")", "fake_rule(", "    name = 'propagator',", "    deps = [':inner'],", ")", "java_library(", "    name = 'outer',", "    srcs = ['outer.java'],", "    deps = [':propagator'],", ")", "j2objc_library(", "    name = 'transpile',", "    deps = [", "        ':outer',", "    ],", ")", "objc_library(", "    name = 'lib',", "    srcs = ['lib.m'],", "    deps = [':transpile'],", ")");
        ConfiguredTarget objcTarget = getConfiguredTarget("//examples:lib");
        ObjcProvider provider = objcTarget.get(SKYLARK_CONSTRUCTOR);
        // The only way that //examples:lib can see inner's archive is through the skylark rule.
        assertThat(Artifact.toRootRelativePaths(provider.get(LIBRARY))).contains("examples/libinner_j2objc.a");
    }

    @Test
    public void testJ2ObjcTranspiledHeaderInCompilationAction() throws Exception {
        scratch.file("app/lib.m");
        scratch.file("app/BUILD", "package(default_visibility=['//visibility:public'])", "objc_library(", "    name = 'lib',", "    srcs = ['lib.m'],", "    deps = ['//java/com/google/dummy/test:transpile'],", ")");
        checkObjcCompileActions(getBinArtifact("liblib.a", getConfiguredTarget("//app:lib")), "lib.o", ImmutableList.of("java/com/google/dummy/test/_j2objc/test/java/com/google/dummy/test/test.h"));
    }

    @Test
    public void testProtoToolchainForJ2ObjcFlag() throws Exception {
        useConfiguration("--proto_toolchain_for_java=//tools/proto/toolchains:java", "--proto_toolchain_for_j2objc=//tools/j2objc:alt_j2objc_proto_toolchain");
        scratch.file("tools/j2objc/proto_plugin_binary");
        scratch.file("tools/j2objc/alt_proto_runtime.h");
        scratch.file("tools/j2objc/alt_proto_runtime.m");
        scratch.file("tools/j2objc/some_blacklisted_proto.proto");
        scratch.overwriteFile("tools/j2objc/BUILD", "package(default_visibility=['//visibility:public'])", "exports_files(['j2objc_deploy.jar'])", "filegroup(", "    name = 'j2objc_wrapper',", "    srcs = ['j2objc_wrapper.py'],", ")", "filegroup(", "    name = 'blacklisted_protos',", "    srcs = ['some_blacklisted_proto.proto'],", ")", "filegroup(", "    name = 'j2objc_header_map',", "    srcs = ['j2objc_header_map.py'],", ")", "proto_lang_toolchain(", "    name = 'alt_j2objc_proto_toolchain',", "    command_line = '--PLUGIN_j2objc_out=file_dir_mapping,generate_class_mappings:$(OUT)',", "    plugin = ':alt_proto_plugin',", "    runtime = ':alt_proto_runtime',", "    blacklisted_protos = [':blacklisted_protos'],", ")", "proto_library(", "   name = 'blacklisted_proto_library',", "   srcs = ['some_blacklisted_proto.proto'],", ")", "objc_library(", "    name = 'alt_proto_runtime',", "    hdrs = ['alt_proto_runtime.h'],", "    srcs = ['alt_proto_runtime.m'],", ")", "filegroup(", "    name = 'alt_proto_plugin',", "    srcs = ['proto_plugin_binary']", ")");
        scratch.file("java/com/google/dummy/test/proto/test.java");
        scratch.file("java/com/google/dummy/test/proto/test.proto");
        scratch.file("java/com/google/dummy/test/proto/BUILD", "package(default_visibility=['//visibility:public'])", "proto_library(", "    name = 'test_proto',", "    srcs = ['test.proto'],", "    deps = ['//tools/j2objc:blacklisted_proto_library'],", ")", "java_proto_library(", "    name = 'test_java_proto',", "    deps = [':test_proto'],", ")", "java_library(", "    name = 'test',", "    srcs = ['test.java'],", "    deps = [':test_java_proto'])", "", "j2objc_library(", "    name = 'transpile',", "    deps = ['test'])");
        ConfiguredTarget j2objcLibraryTarget = getConfiguredTarget("//java/com/google/dummy/test/proto:transpile");
        ObjcProvider provider = j2objcLibraryTarget.get(SKYLARK_CONSTRUCTOR);
        assertThat(Artifact.toRootRelativePaths(provider.get(LIBRARY))).containsExactly(((TestConstants.TOOLS_REPOSITORY_PATH_PREFIX) + "third_party/java/j2objc/libjre_core_lib.a"), "tools/j2objc/libalt_proto_runtime.a", "java/com/google/dummy/test/proto/libtest_j2objc.a", "java/com/google/dummy/test/proto/libtest_proto_j2objc.a");
        assertThat(Artifact.toRootRelativePaths(provider.get(HEADER))).containsExactly(((TestConstants.TOOLS_REPOSITORY_PATH_PREFIX) + "third_party/java/j2objc/jre_core.h"), "tools/j2objc/alt_proto_runtime.h", "java/com/google/dummy/test/proto/test.j2objc.pb.h", "java/com/google/dummy/test/proto/_j2objc/test/java/com/google/dummy/test/proto/test.h");
    }

    @Test
    public void testJ2ObjcDeadCodeRemovalActionWithOptFlag() throws Exception {
        useConfiguration("--j2objc_dead_code_removal");
        addSimpleJ2ObjcLibraryWithEntryClasses();
        addSimpleBinaryTarget("//java/com/google/app/test:transpile");
        ConfiguredTarget appTarget = getConfiguredTargetInAppleBinaryTransition("//app:app");
        Artifact prunedArchive = getBinArtifact("_j2objc_pruned/app/java/com/google/app/test/libtest_j2objc_pruned.a", appTarget);
        Action action = getGeneratingAction(prunedArchive);
        ConfiguredTarget javaTarget = getConfiguredTargetInAppleBinaryTransition("//java/com/google/app/test:test");
        Artifact inputArchive = getBinArtifact("libtest_j2objc.a", javaTarget);
        Artifact headerMappingFile = getBinArtifact("test.mapping.j2objc", javaTarget);
        Artifact dependencyMappingFile = getBinArtifact("test.dependency_mapping.j2objc", javaTarget);
        Artifact archiveSourceMappingFile = getBinArtifact("test.archive_source_mapping.j2objc", javaTarget);
        String execPath = (getConfiguration(javaTarget).getBinDirectory(MAIN).getExecPath()) + "/";
        assertContainsSublist(ImmutableList.copyOf(paramFileArgsForAction(action)), new ImmutableList.Builder<String>().add("--input_archive").add(inputArchive.getExecPathString()).add("--output_archive").add(prunedArchive.getExecPathString()).add("--dummy_archive").add(((execPath + (TestConstants.TOOLS_REPOSITORY_PATH_PREFIX)) + "tools/objc/libdummy_lib.a")).add("--xcrunwrapper").add(ObjcRuleTestCase.MOCK_XCRUNWRAPPER_EXECUTABLE_PATH).add("--dependency_mapping_files").add(dependencyMappingFile.getExecPathString()).add("--header_mapping_files").add(headerMappingFile.getExecPathString()).add("--archive_source_mapping_files").add(archiveSourceMappingFile.getExecPathString()).add("--entry_classes").add("com.google.app.test.test").build());
        SpawnAction deadCodeRemovalAction = ((SpawnAction) (getGeneratingAction(prunedArchive)));
        assertContainsSublist(deadCodeRemovalAction.getArguments(), new ImmutableList.Builder<String>().add(((TestConstants.TOOLS_REPOSITORY_PATH_PREFIX) + "tools/objc/j2objc_dead_code_pruner.py")).build());
        assertThat(deadCodeRemovalAction.getOutputs()).containsExactly(prunedArchive);
    }

    @Test
    public void testCompileActionTemplateFromGenJar() throws Exception {
        useConfiguration("--apple_platform_type=ios", "--cpu=ios_i386", "--ios_minimum_os=1.0");
        addSimpleJ2ObjcLibraryWithJavaPlugin();
        Artifact archive = j2objcArchive("//java/com/google/app/test:transpile", "test");
        CommandAction archiveAction = ((CommandAction) (getGeneratingAction(archive)));
        Artifact objectFilesFromGenJar = ActionsTestUtil.getFirstArtifactEndingWith(archiveAction.getInputs(), "source_files");
        assertThat(objectFilesFromGenJar.isTreeArtifact()).isTrue();
        assertThat(objectFilesFromGenJar.getRootRelativePathString()).isEqualTo("java/com/google/app/test/_objs/test/non_arc/source_files");
        ActionAnalysisMetadata actionTemplate = getActionGraph().getGeneratingAction(objectFilesFromGenJar);
        Artifact sourceFilesFromGenJar = ActionsTestUtil.getFirstArtifactEndingWith(actionTemplate.getInputs(), "source_files");
        Artifact headerFilesFromGenJar = ActionsTestUtil.getFirstArtifactEndingWith(actionTemplate.getInputs(), "header_files");
        assertThat(sourceFilesFromGenJar.getRootRelativePathString()).isEqualTo("java/com/google/app/test/_j2objc/src_jar_files/test/source_files");
        assertThat(headerFilesFromGenJar.getRootRelativePathString()).isEqualTo("java/com/google/app/test/_j2objc/src_jar_files/test/header_files");
        // The files contained inside the tree artifacts are not known until execution time.
        // Therefore we need to fake some files inside them to test the action template in this
        // analysis-time test.
        TreeFileArtifact oneSourceFileFromGenJar = ActionInputHelper.treeFileArtifact(((SpecialArtifact) (sourceFilesFromGenJar)), "children1.m");
        TreeFileArtifact oneObjFileFromGenJar = ActionInputHelper.treeFileArtifact(((SpecialArtifact) (objectFilesFromGenJar)), "children1.o");
        Iterable<CommandAction> compileActions = getActionsForInputsOfGeneratingActionTemplate(objectFilesFromGenJar, oneSourceFileFromGenJar);
        CommandAction compileAction = Iterables.getOnlyElement(compileActions);
        ConfiguredTarget j2objcLibraryTarget = getConfiguredTarget("//java/com/google/dummy/test:transpile");
        String genfilesFragment = getConfiguration(j2objcLibraryTarget).getGenfilesFragment().toString();
        String binFragment = getConfiguration(j2objcLibraryTarget).getBinFragment().toString();
        AppleConfiguration appleConfiguration = getConfiguration(j2objcLibraryTarget).getFragment(AppleConfiguration.class);
        String commandLine = Joiner.on(" ").join(compileAction.getArguments());
        ImmutableList<String> expectedArgs = new ImmutableList.Builder<String>().addAll(DEFAULT_WARNINGS.values()).add("-fexceptions").add("-fasm-blocks").add("-fobjc-abi-version=2").add("-fobjc-legacy-dispatch").add("-DOS_IOS").add("-mios-simulator-version-min=1.0").add("-arch", "i386").add("-isysroot").add(AppleToolchain.sdkDir()).add("-F").add(((AppleToolchain.sdkDir()) + "/Developer/Library/Frameworks")).add("-F").add(AppleToolchain.platformDeveloperFrameworkDir(appleConfiguration)).add("-O0").add("-DDEBUG=1").add("-iquote").add(".").add("-iquote").add(genfilesFragment).add("-I").add((binFragment + "/java/com/google/app/test/_j2objc/test")).add("-I").add(headerFilesFromGenJar.getExecPathString()).add("-fno-strict-overflow").add("-fno-objc-arc").add("-c").add(oneSourceFileFromGenJar.getExecPathString()).add("-o").add(oneObjFileFromGenJar.getExecPathString()).build();
        for (String expectedArg : expectedArgs) {
            assertThat(commandLine).contains(expectedArg);
        }
    }

    @Test
    public void testModuleMapsArePropagatedStrictly() throws Exception {
        useConfiguration("--experimental_objc_enable_module_maps", "--incompatible_strict_objc_module_maps");
        scratch.file("java/com/google/transpile/dummy.java");
        scratch.file("java/com/google/transpile/BUILD", "package(default_visibility=['//visibility:public'])", "java_library(", "    name = 'dummy1',", "    srcs = ['dummy.java'],", ")", "java_library(", "    name = 'dummy2',", "    srcs = ['dummy.java'],", ")", "java_library(", "    name = 'dummy3',", "    srcs = ['dummy.java'], deps = [':dummy2'],", ")", "j2objc_library(", "    name = 'lib1',", "    deps = [':dummy1'],", ")", "j2objc_library(", "    name = 'lib2',", "    deps = [':lib1', ':dummy3'],", ")");
        // Bazel doesn't give us a way to test the aspect directly on the java_library targets, so we
        // can only test propagation through the j2objc_libraries that attach the aspect.
        // lib1 should propagate the module map from its java_library dependency.
        assertThat(getFirstPropagatedModuleMap("//java/com/google/transpile:lib1", "dummy1.modulemaps/module.modulemap")).isNotNull();
        // lib2 should propagate the module maps from its transitive java_library dependencies...
        assertThat(getFirstPropagatedModuleMap("//java/com/google/transpile:lib2", "dummy2.modulemaps/module.modulemap")).isNotNull();
        assertThat(getFirstPropagatedModuleMap("//java/com/google/transpile:lib2", "dummy3.modulemaps/module.modulemap")).isNotNull();
        // ...but it should not propagate the module maps from its j2objc_library dependencies.
        assertThat(getFirstPropagatedModuleMap("//java/com/google/transpile:lib2", "dummy1.modulemaps/module.modulemap")).isNull();
    }
}

