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
package com.google.devtools.build.lib.rules.cpp;


import CcCommon.SYSROOT_VARIABLE_NAME;
import LibraryToLinkValue.NAME_FIELD_NAME;
import Link.LinkTargetType.EXECUTABLE;
import LinkBuildVariables.FORCE_PIC;
import LinkBuildVariables.GENERATE_INTERFACE_LIBRARY;
import LinkBuildVariables.INTERFACE_LIBRARY_BUILDER;
import LinkBuildVariables.INTERFACE_LIBRARY_INPUT;
import LinkBuildVariables.INTERFACE_LIBRARY_OUTPUT;
import LinkBuildVariables.IS_CC_TEST;
import LinkBuildVariables.IS_USING_FISSION;
import LinkBuildVariables.LEGACY_LINK_FLAGS;
import LinkBuildVariables.LIBRARIES_TO_LINK;
import LinkBuildVariables.LIBRARY_SEARCH_DIRECTORIES;
import LinkBuildVariables.LINKER_PARAM_FILE;
import LinkBuildVariables.OUTPUT_EXECPATH;
import LinkBuildVariables.USER_LINK_FLAGS;
import LinkTargetType.NODEPS_DYNAMIC_LIBRARY;
import LinkTargetType.STATIC_LIBRARY;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.analysis.util.AnalysisMock;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.packages.util.MockCcSupport;
import com.google.devtools.build.lib.rules.cpp.CcToolchainVariables.VariableValue;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests that {@code CppLinkAction} is populated with the correct build variables.
 */
@RunWith(JUnit4.class)
public class LinkBuildVariablesTest extends LinkBuildVariablesTestCase {
    @Test
    public void testIsUsingFissionIsIdenticalForCompileAndLink() {
        assertThat(IS_USING_FISSION.getVariableName()).isEqualTo(CompileBuildVariables.IS_USING_FISSION.getVariableName());
    }

    @Test
    public void testForcePicBuildVariable() throws Exception {
        AnalysisMock.get().ccSupport().setupCrosstool(mockToolsConfig, MockCcSupport.SUPPORTS_PIC_FEATURE);
        useConfiguration("--force_pic");
        scratch.file("x/BUILD", "cc_binary(name = 'bin', srcs = ['a.cc'])");
        scratch.file("x/a.cc");
        ConfiguredTarget target = getConfiguredTarget("//x:bin");
        CcToolchainVariables variables = getLinkBuildVariables(target, EXECUTABLE);
        String variableValue = LinkBuildVariablesTestCase.getVariableValue(getRuleContext(), variables, FORCE_PIC.getVariableName());
        assertThat(variableValue).contains("");
    }

    @Test
    public void testLibrariesToLinkAreExported() throws Exception {
        AnalysisMock.get().ccSupport().setupCrosstool(mockToolsConfig, MockCcSupport.SUPPORTS_DYNAMIC_LINKER_FEATURE);
        useConfiguration();
        scratch.file("x/BUILD", "cc_library(name = 'foo', srcs = ['a.cc'])");
        scratch.file("x/a.cc");
        ConfiguredTarget target = getConfiguredTarget("//x:foo");
        CcToolchainVariables variables = getLinkBuildVariables(target, NODEPS_DYNAMIC_LIBRARY);
        VariableValue librariesToLinkSequence = variables.getVariable(LIBRARIES_TO_LINK.getVariableName());
        assertThat(librariesToLinkSequence).isNotNull();
        Iterable<? extends VariableValue> librariesToLink = librariesToLinkSequence.getSequenceValue(LIBRARIES_TO_LINK.getVariableName());
        assertThat(librariesToLink).hasSize(1);
        VariableValue nameValue = librariesToLink.iterator().next().getFieldValue(LIBRARIES_TO_LINK.getVariableName(), NAME_FIELD_NAME);
        assertThat(nameValue).isNotNull();
        String name = nameValue.getStringValue(NAME_FIELD_NAME);
        assertThat(name).matches(".*a\\..*o");
    }

    @Test
    public void testLibrarySearchDirectoriesAreExported() throws Exception {
        AnalysisMock.get().ccSupport().setupCrosstool(mockToolsConfig);
        useConfiguration();
        scratch.file("x/BUILD", "cc_binary(name = 'bin', srcs = ['some-dir/bar.so'])");
        scratch.file("x/some-dir/bar.so");
        ConfiguredTarget target = getConfiguredTarget("//x:bin");
        CcToolchainVariables variables = getLinkBuildVariables(target, EXECUTABLE);
        List<String> variableValue = LinkBuildVariablesTestCase.getSequenceVariableValue(getRuleContext(), variables, LIBRARY_SEARCH_DIRECTORIES.getVariableName());
        assertThat(Iterables.getOnlyElement(variableValue)).contains("some-dir");
    }

    @Test
    public void testLinkerParamFileIsExported() throws Exception {
        AnalysisMock.get().ccSupport().setupCrosstool(mockToolsConfig);
        useConfiguration();
        scratch.file("x/BUILD", "cc_binary(name = 'bin', srcs = ['some-dir/bar.so'])");
        scratch.file("x/some-dir/bar.so");
        ConfiguredTarget target = getConfiguredTarget("//x:bin");
        CcToolchainVariables variables = getLinkBuildVariables(target, EXECUTABLE);
        String variableValue = LinkBuildVariablesTestCase.getVariableValue(getRuleContext(), variables, LINKER_PARAM_FILE.getVariableName());
        assertThat(variableValue).matches((".*bin/x/bin" + "-2.params$"));
    }

    @Test
    public void testInterfaceLibraryBuildingVariablesWhenLegacyGenerationPossible() throws Exception {
        AnalysisMock.get().ccSupport().setupCrosstool(mockToolsConfig, MockCcSupport.SUPPORTS_INTERFACE_SHARED_LIBRARIES_FEATURE, MockCcSupport.SUPPORTS_DYNAMIC_LINKER_FEATURE);
        useConfiguration();
        verifyIfsoVariables();
    }

    @Test
    public void testInterfaceLibraryBuildingVariablesWhenGenerationPossible() throws Exception {
        AnalysisMock.get().ccSupport().setupCrosstool(mockToolsConfig, MockCcSupport.SUPPORTS_DYNAMIC_LINKER_FEATURE, MockCcSupport.SUPPORTS_INTERFACE_SHARED_LIBRARIES_FEATURE);
        useConfiguration();
        verifyIfsoVariables();
    }

    @Test
    public void testNoIfsoBuildingWhenWhenThinLtoIndexing() throws Exception {
        // Make sure the interface shared object generation is enabled in the configuration
        // (which it is not by default for some windows toolchains)
        invalidatePackages(true);
        AnalysisMock.get().ccSupport().setupCrosstool(mockToolsConfig, MockCcSupport.THIN_LTO_CONFIGURATION, MockCcSupport.SUPPORTS_PIC_FEATURE, MockCcSupport.HOST_AND_NONHOST_CONFIGURATION, MockCcSupport.SUPPORTS_INTERFACE_SHARED_LIBRARIES_FEATURE, MockCcSupport.SUPPORTS_DYNAMIC_LINKER_FEATURE, MockCcSupport.SUPPORTS_START_END_LIB_FEATURE);
        useConfiguration("--features=thin_lto");
        scratch.file("x/BUILD", "cc_library(name = 'foo', srcs = ['a.cc'])");
        scratch.file("x/a.cc");
        ConfiguredTarget target = getConfiguredTarget("//x:foo");
        CppLinkAction linkAction = getCppLinkAction(target, NODEPS_DYNAMIC_LIBRARY);
        LtoBackendAction backendAction = ((LtoBackendAction) (getPredecessorByInputName(linkAction, "x/libfoo.so.lto/x/_objs/foo/a.pic.o")));
        assertThat(backendAction.getMnemonic()).isEqualTo("CcLtoBackendCompile");
        CppLinkAction indexAction = ((CppLinkAction) (getPredecessorByInputName(backendAction, "x/libfoo.so.lto/x/_objs/foo/a.pic.o.thinlto.bc")));
        CcToolchainVariables variables = indexAction.getLinkCommandLine().getBuildVariables();
        String interfaceLibraryBuilder = LinkBuildVariablesTestCase.getVariableValue(getRuleContext(), variables, INTERFACE_LIBRARY_BUILDER.getVariableName());
        String interfaceLibraryInput = LinkBuildVariablesTestCase.getVariableValue(getRuleContext(), variables, INTERFACE_LIBRARY_INPUT.getVariableName());
        String interfaceLibraryOutput = LinkBuildVariablesTestCase.getVariableValue(getRuleContext(), variables, INTERFACE_LIBRARY_OUTPUT.getVariableName());
        String generateInterfaceLibrary = LinkBuildVariablesTestCase.getVariableValue(getRuleContext(), variables, GENERATE_INTERFACE_LIBRARY.getVariableName());
        assertThat(generateInterfaceLibrary).isEqualTo("no");
        assertThat(interfaceLibraryInput).endsWith("ignored");
        assertThat(interfaceLibraryOutput).endsWith("ignored");
        assertThat(interfaceLibraryBuilder).endsWith("ignored");
    }

    @Test
    public void testInterfaceLibraryBuildingVariablesWhenGenerationNotAllowed() throws Exception {
        // Make sure the interface shared object generation is enabled in the configuration
        // (which it is not by default for some windows toolchains)
        AnalysisMock.get().ccSupport().setupCrosstool(mockToolsConfig, MockCcSupport.SUPPORTS_INTERFACE_SHARED_LIBRARIES_FEATURE);
        useConfiguration();
        scratch.file("x/BUILD", "cc_library(name = 'foo', srcs = ['a.cc'])");
        scratch.file("x/a.cc");
        ConfiguredTarget target = getConfiguredTarget("//x:foo");
        CcToolchainVariables variables = getLinkBuildVariables(target, STATIC_LIBRARY);
        String interfaceLibraryBuilder = LinkBuildVariablesTestCase.getVariableValue(getRuleContext(), variables, INTERFACE_LIBRARY_BUILDER.getVariableName());
        String interfaceLibraryInput = LinkBuildVariablesTestCase.getVariableValue(getRuleContext(), variables, INTERFACE_LIBRARY_INPUT.getVariableName());
        String interfaceLibraryOutput = LinkBuildVariablesTestCase.getVariableValue(getRuleContext(), variables, INTERFACE_LIBRARY_OUTPUT.getVariableName());
        String generateInterfaceLibrary = LinkBuildVariablesTestCase.getVariableValue(getRuleContext(), variables, GENERATE_INTERFACE_LIBRARY.getVariableName());
        assertThat(generateInterfaceLibrary).isEqualTo("no");
        assertThat(interfaceLibraryInput).endsWith("ignored");
        assertThat(interfaceLibraryOutput).endsWith("ignored");
        assertThat(interfaceLibraryBuilder).endsWith("ignored");
    }

    @Test
    public void testOutputExecpath() throws Exception {
        AnalysisMock.get().ccSupport().setupCrosstool(mockToolsConfig, MockCcSupport.SUPPORTS_DYNAMIC_LINKER_FEATURE);
        // Make sure the interface shared object generation is enabled in the configuration
        // (which it is not by default for some windows toolchains)
        scratch.file("x/BUILD", "cc_library(name = 'foo', srcs = ['a.cc'])");
        scratch.file("x/a.cc");
        ConfiguredTarget target = getConfiguredTarget("//x:foo");
        CcToolchainVariables variables = getLinkBuildVariables(target, NODEPS_DYNAMIC_LIBRARY);
        assertThat(LinkBuildVariablesTestCase.getVariableValue(getRuleContext(), variables, OUTPUT_EXECPATH.getVariableName())).endsWith("x/libfoo.so");
    }

    @Test
    public void testOutputExecpathIsNotExposedWhenThinLtoIndexing() throws Exception {
        AnalysisMock.get().ccSupport().setupCrosstool(mockToolsConfig, MockCcSupport.THIN_LTO_CONFIGURATION, MockCcSupport.HOST_AND_NONHOST_CONFIGURATION, MockCcSupport.SUPPORTS_DYNAMIC_LINKER_FEATURE, MockCcSupport.SUPPORTS_PIC_FEATURE, MockCcSupport.SUPPORTS_INTERFACE_SHARED_LIBRARIES_FEATURE, MockCcSupport.SUPPORTS_START_END_LIB_FEATURE);
        useConfiguration("--features=thin_lto");
        scratch.file("x/BUILD", "cc_library(name = 'foo', srcs = ['a.cc'])");
        scratch.file("x/a.cc");
        ConfiguredTarget target = getConfiguredTarget("//x:foo");
        CppLinkAction linkAction = getCppLinkAction(target, NODEPS_DYNAMIC_LIBRARY);
        LtoBackendAction backendAction = ((LtoBackendAction) (getPredecessorByInputName(linkAction, "x/libfoo.so.lto/x/_objs/foo/a.pic.o")));
        assertThat(backendAction.getMnemonic()).isEqualTo("CcLtoBackendCompile");
        CppLinkAction indexAction = ((CppLinkAction) (getPredecessorByInputName(backendAction, "x/libfoo.so.lto/x/_objs/foo/a.pic.o.thinlto.bc")));
        CcToolchainVariables variables = indexAction.getLinkCommandLine().getBuildVariables();
        assertThat(variables.isAvailable(OUTPUT_EXECPATH.getVariableName())).isFalse();
    }

    @Test
    public void testIsCcTestLinkActionBuildVariable() throws Exception {
        scratch.file("x/BUILD", "cc_test(name = 'foo_test', srcs = ['a.cc'])", "cc_binary(name = 'foo', srcs = ['a.cc'])");
        scratch.file("x/a.cc");
        ConfiguredTarget testTarget = getConfiguredTarget("//x:foo_test");
        CcToolchainVariables testVariables = getLinkBuildVariables(testTarget, LinkTargetType.EXECUTABLE);
        assertThat(testVariables.getVariable(IS_CC_TEST.getVariableName()).isTruthy()).isTrue();
        ConfiguredTarget binaryTarget = getConfiguredTarget("//x:foo");
        CcToolchainVariables binaryVariables = getLinkBuildVariables(binaryTarget, LinkTargetType.EXECUTABLE);
        assertThat(binaryVariables.getVariable(IS_CC_TEST.getVariableName()).isTruthy()).isFalse();
    }

    @Test
    public void testStripBinariesIsEnabledWhenStripModeIsAlwaysNoMatterWhat() throws Exception {
        scratch.file("x/BUILD", "cc_binary(name = 'foo', srcs = ['a.cc'])");
        scratch.file("x/a.cc");
        assertStripBinaryVariableIsPresent("always", "opt", true);
        assertStripBinaryVariableIsPresent("always", "fastbuild", true);
        assertStripBinaryVariableIsPresent("always", "dbg", true);
        assertStripBinaryVariableIsPresent("sometimes", "opt", false);
        assertStripBinaryVariableIsPresent("sometimes", "fastbuild", true);
        assertStripBinaryVariableIsPresent("sometimes", "dbg", false);
        assertStripBinaryVariableIsPresent("never", "opt", false);
        assertStripBinaryVariableIsPresent("never", "fastbuild", false);
        assertStripBinaryVariableIsPresent("never", "dbg", false);
    }

    @Test
    public void testIsUsingFissionVariableUsingLegacyFields() throws Exception {
        scratch.file("x/BUILD", "cc_binary(name = 'foo', srcs = ['foo.cc'])");
        scratch.file("x/foo.cc");
        AnalysisMock.get().ccSupport().setupCrosstool(mockToolsConfig, MockCcSupport.PER_OBJECT_DEBUG_INFO_CONFIGURATION);
        useConfiguration("--fission=no");
        ConfiguredTarget target = getConfiguredTarget("//x:foo");
        CcToolchainVariables variables = getLinkBuildVariables(target, LinkTargetType.EXECUTABLE);
        assertThat(variables.isAvailable(IS_USING_FISSION.getVariableName())).isFalse();
        useConfiguration("--fission=yes");
        ConfiguredTarget fissionTarget = getConfiguredTarget("//x:foo");
        CcToolchainVariables fissionVariables = getLinkBuildVariables(fissionTarget, LinkTargetType.EXECUTABLE);
        assertThat(fissionVariables.isAvailable(IS_USING_FISSION.getVariableName())).isTrue();
    }

    @Test
    public void testIsUsingFissionVariable() throws Exception {
        scratch.file("x/BUILD", "cc_binary(name = 'foo', srcs = ['foo.cc'])");
        scratch.file("x/foo.cc");
        AnalysisMock.get().ccSupport().setupCrosstool(mockToolsConfig, MockCcSupport.PER_OBJECT_DEBUG_INFO_CONFIGURATION);
        useConfiguration("--fission=no");
        ConfiguredTarget target = getConfiguredTarget("//x:foo");
        CcToolchainVariables variables = getLinkBuildVariables(target, LinkTargetType.EXECUTABLE);
        assertThat(variables.isAvailable(IS_USING_FISSION.getVariableName())).isFalse();
        useConfiguration("--fission=yes");
        ConfiguredTarget fissionTarget = getConfiguredTarget("//x:foo");
        CcToolchainVariables fissionVariables = getLinkBuildVariables(fissionTarget, LinkTargetType.EXECUTABLE);
        assertThat(fissionVariables.isAvailable(IS_USING_FISSION.getVariableName())).isTrue();
    }

    @Test
    public void testSysrootVariable() throws Exception {
        AnalysisMock.get().ccSupport().setupCrosstool(mockToolsConfig, "builtin_sysroot: '/usr/local/custom-sysroot'");
        useConfiguration();
        scratch.file("x/BUILD", "cc_binary(name = 'foo', srcs = ['a.cc'])");
        scratch.file("x/a.cc");
        ConfiguredTarget testTarget = getConfiguredTarget("//x:foo");
        CcToolchainVariables testVariables = getLinkBuildVariables(testTarget, LinkTargetType.EXECUTABLE);
        assertThat(testVariables.isAvailable(SYSROOT_VARIABLE_NAME)).isTrue();
    }

    @Test
    public void testUserLinkFlagsWithLinkoptOption() throws Exception {
        useConfiguration("--linkopt=-bar");
        scratch.file("x/BUILD", "cc_binary(name = 'foo', srcs = ['a.cc'], linkopts = ['-foo'])");
        scratch.file("x/a.cc");
        ConfiguredTarget testTarget = getConfiguredTarget("//x:foo");
        CcToolchainVariables testVariables = getLinkBuildVariables(testTarget, LinkTargetType.EXECUTABLE);
        ImmutableList<String> userLinkFlags = CcToolchainVariables.toStringList(testVariables, USER_LINK_FLAGS.getVariableName());
        assertThat(userLinkFlags).containsAllOf("-foo", "-bar").inOrder();
        ImmutableList<String> legacyLinkFlags = CcToolchainVariables.toStringList(testVariables, LEGACY_LINK_FLAGS.getVariableName());
        assertThat(legacyLinkFlags).doesNotContain("-foo");
        assertThat(legacyLinkFlags).doesNotContain("-bar");
    }
}

