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


import MultiArchSplitTransitionProvider.UNSUPPORTED_PLATFORM_TYPE_ERROR_FORMAT;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.devtools.build.lib.rules.objc.CompilationSupport.ExtraLinkArgs;
import com.google.devtools.build.lib.testutil.Scratch;
import java.io.IOException;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test case for apple_dyamic_library.
 */
@RunWith(JUnit4.class)
public class AppleDynamicLibraryTest extends ObjcRuleTestCase {
    static final RuleType RULE_TYPE = new RuleType("apple_binary") {
        @Override
        Iterable<String> requiredAttributes(Scratch scratch, String packageDir, Set<String> alreadyAdded) throws IOException {
            return Iterables.concat(ImmutableList.of("binary_type = 'dylib'"), AppleBinaryTest.RULE_TYPE.requiredAttributes(scratch, packageDir, Sets.union(alreadyAdded, ImmutableSet.of("binary_type"))));
        }
    };

    @Test
    public void testCcDependencyLinkoptsArePropagatedToLinkAction() throws Exception {
        checkCcDependencyLinkoptsArePropagatedToLinkAction(AppleDynamicLibraryTest.RULE_TYPE);
    }

    @Test
    public void testUnknownPlatformType() throws Exception {
        checkError("package", "test", String.format(UNSUPPORTED_PLATFORM_TYPE_ERROR_FORMAT, "meow_meow_os"), "apple_binary(name = 'test', binary_type = 'dylib', platform_type = 'meow_meow_os')");
    }

    @Test
    public void testProtoBundlingAndLinking() throws Exception {
        checkProtoBundlingAndLinking(AppleDynamicLibraryTest.RULE_TYPE);
    }

    @Test
    public void testProtoBundlingWithTargetsWithNoDeps() throws Exception {
        checkProtoBundlingWithTargetsWithNoDeps(AppleDynamicLibraryTest.RULE_TYPE);
    }

    @Test
    public void testCanUseCrosstool_singleArch() throws Exception {
        checkLinkingRuleCanUseCrosstool_singleArch(AppleDynamicLibraryTest.RULE_TYPE);
    }

    @Test
    public void testCanUseCrosstool_multiArch() throws Exception {
        checkLinkingRuleCanUseCrosstool_multiArch(AppleDynamicLibraryTest.RULE_TYPE);
    }

    @Test
    public void testAppleSdkIphoneosPlatformEnv() throws Exception {
        checkAppleSdkIphoneosPlatformEnv(AppleDynamicLibraryTest.RULE_TYPE);
    }

    @Test
    public void testXcodeVersionEnv() throws Exception {
        checkXcodeVersionEnv(AppleDynamicLibraryTest.RULE_TYPE);
    }

    @Test
    public void testAliasedLinkoptsThroughObjcLibrary() throws Exception {
        checkAliasedLinkoptsThroughObjcLibrary(AppleDynamicLibraryTest.RULE_TYPE);
    }

    @Test
    public void testObjcProviderLinkInputsInLinkAction() throws Exception {
        checkObjcProviderLinkInputsInLinkAction(AppleDynamicLibraryTest.RULE_TYPE);
    }

    @Test
    public void testAppleSdkVersionEnv() throws Exception {
        checkAppleSdkVersionEnv(AppleDynamicLibraryTest.RULE_TYPE);
    }

    @Test
    public void testNonDefaultAppleSdkVersionEnv() throws Exception {
        checkNonDefaultAppleSdkVersionEnv(AppleDynamicLibraryTest.RULE_TYPE);
    }

    @Test
    public void testAppleSdkDefaultPlatformEnv() throws Exception {
        checkAppleSdkDefaultPlatformEnv(AppleDynamicLibraryTest.RULE_TYPE);
    }

    @Test
    public void testAvoidDepsObjects_avoidViaCcLibrary() throws Exception {
        checkAvoidDepsObjects_avoidViaCcLibrary(AppleDynamicLibraryTest.RULE_TYPE);
    }

    @Test
    public void testLipoBinaryAction() throws Exception {
        checkLipoBinaryAction(AppleDynamicLibraryTest.RULE_TYPE);
    }

    @Test
    public void testWatchSimulatorDepCompile() throws Exception {
        checkWatchSimulatorDepCompile(AppleDynamicLibraryTest.RULE_TYPE);
    }

    @Test
    public void testMultiarchCcDep() throws Exception {
        checkMultiarchCcDep(AppleDynamicLibraryTest.RULE_TYPE);
    }

    @Test
    public void testWatchSimulatorLipoAction() throws Exception {
        checkWatchSimulatorLipoAction(AppleDynamicLibraryTest.RULE_TYPE);
    }

    @Test
    public void testFrameworkDepLinkFlags() throws Exception {
        checkFrameworkDepLinkFlags(AppleDynamicLibraryTest.RULE_TYPE, new ExtraLinkArgs("-dynamiclib"));
    }

    @Test
    public void testDylibDependencies() throws Exception {
        checkDylibDependencies(AppleDynamicLibraryTest.RULE_TYPE, new ExtraLinkArgs("-dynamiclib"));
    }

    @Test
    public void testMinimumOs() throws Exception {
        checkMinimumOsLinkAndCompileArg(AppleDynamicLibraryTest.RULE_TYPE);
    }

    @Test
    public void testMinimumOs_watchos() throws Exception {
        checkMinimumOsLinkAndCompileArg_watchos(AppleDynamicLibraryTest.RULE_TYPE);
    }

    @Test
    public void testMinimumOs_invalid() throws Exception {
        checkMinimumOs_invalid_nonVersion(AppleDynamicLibraryTest.RULE_TYPE);
    }

    @Test
    public void testAppleSdkWatchsimulatorPlatformEnv() throws Exception {
        checkAppleSdkWatchsimulatorPlatformEnv(AppleDynamicLibraryTest.RULE_TYPE);
    }

    @Test
    public void testAppleSdkWatchosPlatformEnv() throws Exception {
        checkAppleSdkWatchosPlatformEnv(AppleDynamicLibraryTest.RULE_TYPE);
    }

    @Test
    public void testAppleSdkTvsimulatorPlatformEnv() throws Exception {
        checkAppleSdkTvsimulatorPlatformEnv(AppleDynamicLibraryTest.RULE_TYPE);
    }

    @Test
    public void testAppleSdkTvosPlatformEnv() throws Exception {
        checkAppleSdkTvosPlatformEnv(AppleDynamicLibraryTest.RULE_TYPE);
    }

    @Test
    public void testWatchSimulatorLinkAction() throws Exception {
        checkWatchSimulatorLinkAction(AppleDynamicLibraryTest.RULE_TYPE);
    }

    @Test
    public void testAvoidDepsObjects() throws Exception {
        checkAvoidDepsObjects(AppleDynamicLibraryTest.RULE_TYPE);
    }

    @Test
    public void testMinimumOsDifferentTargets() throws Exception {
        checkMinimumOsDifferentTargets(AppleDynamicLibraryTest.RULE_TYPE, "_lipobin", "_bin");
    }

    @Test
    public void testDrops32BitArchitecture() throws Exception {
        verifyDrops32BitArchitecture(AppleDynamicLibraryTest.RULE_TYPE);
    }
}

