/**
 * Copyright 2018 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.skyframe;


import Transience.PERSISTENT;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.analysis.BlazeDirectories;
import com.google.devtools.build.lib.analysis.platform.ConstraintValueInfo;
import com.google.devtools.build.lib.analysis.util.AnalysisMock;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.rules.platform.ToolchainTestCase;
import com.google.devtools.build.lib.skyframe.ConstraintValueLookupUtil.InvalidConstraintValueException;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.skyframe.EvaluationResult;
import com.google.devtools.build.skyframe.EvaluationResultSubjectFactory;
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
 * Tests for {@link ConstraintValueLookupUtil}.
 */
@RunWith(JUnit4.class)
public class ConstraintValueLookupUtilTest extends ToolchainTestCase {
    /**
     * An {@link AnalysisMock} that injects {@link GetConstraintValueInfoFunction} into the Skyframe
     * executor.
     */
    private static final class AnalysisMockWithGetPlatformInfoFunction extends AnalysisMock.Delegate {
        AnalysisMockWithGetPlatformInfoFunction() {
            super(AnalysisMock.get());
        }

        @Override
        public ImmutableMap<SkyFunctionName, SkyFunction> getSkyFunctions(BlazeDirectories directories) {
            return ImmutableMap.<SkyFunctionName, SkyFunction>builder().putAll(super.getSkyFunctions(directories)).put(ConstraintValueLookupUtilTest.GET_CONSTRAINT_VALUE_INFO_FUNCTION, new ConstraintValueLookupUtilTest.GetConstraintValueInfoFunction()).build();
        }
    }

    @Test
    public void testConstraintValueLookup() throws Exception {
        ConfiguredTargetKey linuxKey = ConfiguredTargetKey.of(BuildViewTestCase.makeLabel("//constraints:linux"), targetConfigKey, false);
        ConfiguredTargetKey macKey = ConfiguredTargetKey.of(BuildViewTestCase.makeLabel("//constraints:mac"), targetConfigKey, false);
        ConstraintValueLookupUtilTest.GetConstraintValueInfoKey key = ConstraintValueLookupUtilTest.GetConstraintValueInfoKey.create(ImmutableList.of(linuxKey, macKey));
        EvaluationResult<ConstraintValueLookupUtilTest.GetConstraintValueInfoValue> result = getConstraintValueInfo(key);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasNoError();
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasEntryThat(key).isNotNull();
        List<ConstraintValueInfo> constraintValues = result.get(key).constraintValues();
        assertThat(constraintValues).contains(linuxConstraint);
        assertThat(constraintValues).contains(macConstraint);
        assertThat(constraintValues).hasSize(2);
    }

    @Test
    public void testConstraintValueLookup_targetNotConstraintValue() throws Exception {
        scratch.file("invalid/BUILD", "filegroup(name = 'not_a_constraint')");
        ConfiguredTargetKey targetKey = ConfiguredTargetKey.of(BuildViewTestCase.makeLabel("//invalid:not_a_constraint"), targetConfigKey, false);
        ConstraintValueLookupUtilTest.GetConstraintValueInfoKey key = ConstraintValueLookupUtilTest.GetConstraintValueInfoKey.create(ImmutableList.of(targetKey));
        EvaluationResult<ConstraintValueLookupUtilTest.GetConstraintValueInfoValue> result = getConstraintValueInfo(key);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasError();
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasErrorEntryForKeyThat(key).hasExceptionThat().isInstanceOf(InvalidConstraintValueException.class);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasErrorEntryForKeyThat(key).hasExceptionThat().hasMessageThat().contains("//invalid:not_a_constraint");
    }

    @Test
    public void testConstraintValueLookup_targetDoesNotExist() throws Exception {
        ConfiguredTargetKey targetKey = ConfiguredTargetKey.of(BuildViewTestCase.makeLabel("//fake:missing"), targetConfigKey, false);
        ConstraintValueLookupUtilTest.GetConstraintValueInfoKey key = ConstraintValueLookupUtilTest.GetConstraintValueInfoKey.create(ImmutableList.of(targetKey));
        EvaluationResult<ConstraintValueLookupUtilTest.GetConstraintValueInfoValue> result = getConstraintValueInfo(key);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasError();
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasErrorEntryForKeyThat(key).hasExceptionThat().isInstanceOf(InvalidConstraintValueException.class);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasErrorEntryForKeyThat(key).hasExceptionThat().hasMessageThat().contains("no such package 'fake': BUILD file not found on package path");
    }

    // Calls ConstraintValueLookupUtil.getConstraintValueInfo.
    private static final SkyFunctionName GET_CONSTRAINT_VALUE_INFO_FUNCTION = SkyFunctionName.createHermetic("GET_CONSTRAINT_VALUE_INFO_FUNCTION");

    @AutoValue
    abstract static class GetConstraintValueInfoKey implements SkyKey {
        @Override
        public SkyFunctionName functionName() {
            return ConstraintValueLookupUtilTest.GET_CONSTRAINT_VALUE_INFO_FUNCTION;
        }

        abstract Iterable<ConfiguredTargetKey> constraintValueKeys();

        public static ConstraintValueLookupUtilTest.GetConstraintValueInfoKey create(Iterable<ConfiguredTargetKey> constraintValueKeys) {
            return new AutoValue_ConstraintValueLookupUtilTest_GetConstraintValueInfoKey(constraintValueKeys);
        }
    }

    @AutoValue
    abstract static class GetConstraintValueInfoValue implements SkyValue {
        abstract List<ConstraintValueInfo> constraintValues();

        static ConstraintValueLookupUtilTest.GetConstraintValueInfoValue create(List<ConstraintValueInfo> constraintValues) {
            return new AutoValue_ConstraintValueLookupUtilTest_GetConstraintValueInfoValue(constraintValues);
        }
    }

    private static final class GetConstraintValueInfoFunction implements SkyFunction {
        @Nullable
        @Override
        public SkyValue compute(SkyKey skyKey, Environment env) throws SkyFunctionException, InterruptedException {
            ConstraintValueLookupUtilTest.GetConstraintValueInfoKey key = ((ConstraintValueLookupUtilTest.GetConstraintValueInfoKey) (skyKey));
            try {
                List<ConstraintValueInfo> constraintValues = ConstraintValueLookupUtil.getConstraintValueInfo(key.constraintValueKeys(), env);
                if (env.valuesMissing()) {
                    return null;
                }
                return ConstraintValueLookupUtilTest.GetConstraintValueInfoValue.create(constraintValues);
            } catch (InvalidConstraintValueException e) {
                throw new ConstraintValueLookupUtilTest.GetConstraintValueInfoFunctionException(e);
            }
        }

        @Nullable
        @Override
        public String extractTag(SkyKey skyKey) {
            return null;
        }
    }

    private static class GetConstraintValueInfoFunctionException extends SkyFunctionException {
        public GetConstraintValueInfoFunctionException(InvalidConstraintValueException e) {
            super(e, PERSISTENT);
        }
    }
}

