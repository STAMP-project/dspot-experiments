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
package com.google.devtools.build.lib.repository;


import NullEventHandler.INSTANCE;
import SkyframeExecutor.DEFAULT_THREAD_COUNT;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.packages.Rule;
import com.google.devtools.build.lib.skyframe.RegisteredExecutionPlatformsFunction;
import com.google.devtools.build.lib.skyframe.RegisteredToolchainsFunction;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.skyframe.AbstractSkyKey;
import com.google.devtools.build.skyframe.EvaluationContext;
import com.google.devtools.build.skyframe.EvaluationResult;
import com.google.devtools.build.skyframe.EvaluationResultSubjectFactory;
import com.google.devtools.build.skyframe.SequentialBuildDriver;
import com.google.devtools.build.skyframe.SkyFunction;
import com.google.devtools.build.skyframe.SkyFunctionException;
import com.google.devtools.build.skyframe.SkyFunctionName;
import com.google.devtools.build.skyframe.SkyKey;
import com.google.devtools.build.skyframe.SkyValue;
import java.util.List;
import javax.annotation.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link ExternalPackageUtil}.
 */
@RunWith(JUnit4.class)
public class ExternalPackageUtilTest extends BuildViewTestCase {
    private static final EvaluationContext EVALUATION_OPTIONS = EvaluationContext.newBuilder().setKeepGoing(false).setNumThreads(DEFAULT_THREAD_COUNT).setEventHander(INSTANCE).build();

    private SequentialBuildDriver driver;

    @Test
    public void getRuleByName() throws Exception {
        if (!(analysisMock.isThisBazel())) {
            return;
        }
        scratch.overwriteFile("WORKSPACE", "local_repository(name = 'foo', path = 'path/to/repo')");
        SkyKey key = ExternalPackageUtilTest.getRuleByNameKey("foo");
        EvaluationResult<ExternalPackageUtilTest.GetRuleByNameValue> result = getRuleByName(key);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasNoError();
        Rule rule = result.get(key).rule();
        assertThat(rule).isNotNull();
        assertThat(rule.getName()).isEqualTo("foo");
    }

    @Test
    public void getRuleByName_missing() throws Exception {
        if (!(analysisMock.isThisBazel())) {
            return;
        }
        scratch.overwriteFile("WORKSPACE", "local_repository(name = 'foo', path = 'path/to/repo')");
        SkyKey key = ExternalPackageUtilTest.getRuleByNameKey("bar");
        EvaluationResult<ExternalPackageUtilTest.GetRuleByNameValue> result = getRuleByName(key);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasErrorEntryForKeyThat(key).hasExceptionThat().hasMessageThat().contains("The rule named 'bar' could not be resolved");
    }

    @Test
    public void getRegisteredToolchains() throws Exception {
        scratch.overwriteFile("WORKSPACE", "register_toolchains(", "  '//toolchain:tc1',", "  '//toolchain:tc2')");
        SkyKey key = ExternalPackageUtilTest.getRegisteredToolchainsKey();
        EvaluationResult<ExternalPackageUtilTest.GetRegisteredToolchainsValue> result = getRegisteredToolchains(key);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasNoError();
        // There are default toolchains that are always registered, so just check for the ones added
        assertThat(result.get(key).registeredToolchains()).containsAllOf("//toolchain:tc1", "//toolchain:tc2").inOrder();
    }

    @Test
    public void getRegisteredExecutionPlatforms() throws Exception {
        scratch.overwriteFile("WORKSPACE", "register_execution_platforms(", "  '//platform:ep1',", "  '//platform:ep2')");
        SkyKey key = ExternalPackageUtilTest.getRegisteredExecutionPlatformsKey();
        EvaluationResult<ExternalPackageUtilTest.GetRegisteredExecutionPlatformsValue> result = getRegisteredExecutionPlatforms(key);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasNoError();
        assertThat(result.get(key).registeredExecutionPlatforms()).containsExactly("//platform:ep1", "//platform:ep2").inOrder();
    }

    private static final SkyFunctionName GET_RULE_BY_NAME_FUNCTION = SkyFunctionName.createHermetic("GET_RULE_BY_NAME");

    @AutoValue
    abstract static class GetRuleByNameValue implements SkyValue {
        abstract Rule rule();

        static ExternalPackageUtilTest.GetRuleByNameValue create(Rule rule) {
            return new AutoValue_ExternalPackageUtilTest_GetRuleByNameValue(rule);
        }
    }

    private static final class GetRuleByNameFunction implements SkyFunction {
        @Nullable
        @Override
        public SkyValue compute(SkyKey skyKey, Environment env) throws SkyFunctionException, InterruptedException {
            String ruleName = ((String) (skyKey.argument()));
            Rule rule = ExternalPackageUtil.getRuleByName(ruleName, env);
            if (rule == null) {
                return null;
            }
            return ExternalPackageUtilTest.GetRuleByNameValue.create(rule);
        }

        @Nullable
        @Override
        public String extractTag(SkyKey skyKey) {
            return null;
        }
    }

    private static final SkyFunctionName GET_REGISTERED_TOOLCHAINS_FUNCTION = SkyFunctionName.createHermetic("GET_REGISTERED_TOOLCHAINS");

    @AutoValue
    abstract static class GetRegisteredToolchainsValue implements SkyValue {
        abstract ImmutableList<String> registeredToolchains();

        static ExternalPackageUtilTest.GetRegisteredToolchainsValue create(Iterable<String> registeredToolchains) {
            return new AutoValue_ExternalPackageUtilTest_GetRegisteredToolchainsValue(ImmutableList.copyOf(registeredToolchains));
        }
    }

    private static final class GetRegisteredToolchainsFunction implements SkyFunction {
        @Nullable
        @Override
        public SkyValue compute(SkyKey skyKey, Environment env) throws SkyFunctionException, InterruptedException {
            List<String> registeredToolchains = RegisteredToolchainsFunction.getRegisteredToolchains(env);
            if (registeredToolchains == null) {
                return null;
            }
            return ExternalPackageUtilTest.GetRegisteredToolchainsValue.create(registeredToolchains);
        }

        @Nullable
        @Override
        public String extractTag(SkyKey skyKey) {
            return null;
        }
    }

    private static final SkyFunctionName GET_REGISTERED_EXECUTION_PLATFORMS_FUNCTION = SkyFunctionName.createHermetic("GET_REGISTERED_EXECUTION_PLATFORMS_FUNCTION");

    @AutoValue
    abstract static class GetRegisteredExecutionPlatformsValue implements SkyValue {
        abstract ImmutableList<String> registeredExecutionPlatforms();

        static ExternalPackageUtilTest.GetRegisteredExecutionPlatformsValue create(Iterable<String> registeredExecutionPlatforms) {
            return new AutoValue_ExternalPackageUtilTest_GetRegisteredExecutionPlatformsValue(ImmutableList.copyOf(registeredExecutionPlatforms));
        }
    }

    private static final class GetRegisteredExecutionPlatformsFunction implements SkyFunction {
        @Nullable
        @Override
        public SkyValue compute(SkyKey skyKey, Environment env) throws SkyFunctionException, InterruptedException {
            List<String> registeredExecutionPlatforms = RegisteredExecutionPlatformsFunction.getWorkspaceExecutionPlatforms(env);
            if (registeredExecutionPlatforms == null) {
                return null;
            }
            return ExternalPackageUtilTest.GetRegisteredExecutionPlatformsValue.create(registeredExecutionPlatforms);
        }

        @Nullable
        @Override
        public String extractTag(SkyKey skyKey) {
            return null;
        }
    }

    static class Key extends AbstractSkyKey<String> {
        private Key(String arg) {
            super(arg);
        }

        @Override
        public SkyFunctionName functionName() {
            return ExternalPackageUtilTest.GET_RULE_BY_NAME_FUNCTION;
        }
    }
}

