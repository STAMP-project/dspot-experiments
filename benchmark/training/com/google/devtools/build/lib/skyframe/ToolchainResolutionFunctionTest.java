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
package com.google.devtools.build.lib.skyframe;


import AutoCodec.VisibleForSerialization;
import BuildConfigurationValue.Key;
import PlatformInfo.PROVIDER;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.testing.EqualsTester;
import com.google.devtools.build.lib.actions.util.InjectedActionLookupKey;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.analysis.TransitiveInfoProvider;
import com.google.devtools.build.lib.analysis.platform.PlatformInfo;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.events.Location;
import com.google.devtools.build.lib.packages.InfoInterface;
import com.google.devtools.build.lib.packages.NativeProvider;
import com.google.devtools.build.lib.packages.Provider;
import com.google.devtools.build.lib.rules.platform.ToolchainTestCase;
import com.google.devtools.build.lib.skyframe.serialization.autocodec.AutoCodec;
import com.google.devtools.build.lib.skylarkinterface.SkylarkPrinter;
import com.google.devtools.build.lib.syntax.EvalException;
import com.google.devtools.build.skyframe.EvaluationResult;
import com.google.devtools.build.skyframe.EvaluationResultSubjectFactory;
import com.google.devtools.build.skyframe.SkyKey;
import javax.annotation.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests for {@link ToolchainResolutionValue} and {@link ToolchainResolutionFunction}.
 */
@RunWith(JUnit4.class)
public class ToolchainResolutionFunctionTest extends ToolchainTestCase {
    @AutoCodec
    @AutoCodec.VisibleForSerialization
    static final ConfiguredTargetKey LINUX_CTKEY = Mockito.mock(ConfiguredTargetKey.class);

    @AutoCodec
    @AutoCodec.VisibleForSerialization
    static final ConfiguredTargetKey MAC_CTKEY = Mockito.mock(ConfiguredTargetKey.class);

    static {
        Mockito.when(ToolchainResolutionFunctionTest.LINUX_CTKEY.functionName()).thenReturn(InjectedActionLookupKey.INJECTED_ACTION_LOOKUP);
        Mockito.when(ToolchainResolutionFunctionTest.MAC_CTKEY.functionName()).thenReturn(InjectedActionLookupKey.INJECTED_ACTION_LOOKUP);
    }

    @Test
    public void testResolution_singleExecutionPlatform() throws Exception {
        SkyKey key = ToolchainResolutionValue.key(targetConfigKey, testToolchainTypeLabel, ToolchainResolutionFunctionTest.LINUX_CTKEY, ImmutableList.of(ToolchainResolutionFunctionTest.MAC_CTKEY));
        EvaluationResult<ToolchainResolutionValue> result = invokeToolchainResolution(key);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasNoError();
        ToolchainResolutionValue toolchainResolutionValue = result.get(key);
        assertThat(toolchainResolutionValue.availableToolchainLabels()).containsExactly(ToolchainResolutionFunctionTest.MAC_CTKEY, BuildViewTestCase.makeLabel("//toolchain:toolchain_2_impl"));
    }

    @Test
    public void testResolution_multipleExecutionPlatforms() throws Exception {
        addToolchain("extra", "extra_toolchain", ImmutableList.of("//constraints:linux"), ImmutableList.of("//constraints:linux"), "baz");
        rewriteWorkspace("register_toolchains(", "'//toolchain:toolchain_1',", "'//toolchain:toolchain_2',", "'//extra:extra_toolchain')");
        SkyKey key = ToolchainResolutionValue.key(targetConfigKey, testToolchainTypeLabel, ToolchainResolutionFunctionTest.LINUX_CTKEY, ImmutableList.of(ToolchainResolutionFunctionTest.LINUX_CTKEY, ToolchainResolutionFunctionTest.MAC_CTKEY));
        EvaluationResult<ToolchainResolutionValue> result = invokeToolchainResolution(key);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasNoError();
        ToolchainResolutionValue toolchainResolutionValue = result.get(key);
        assertThat(toolchainResolutionValue.availableToolchainLabels()).containsExactly(ToolchainResolutionFunctionTest.LINUX_CTKEY, BuildViewTestCase.makeLabel("//extra:extra_toolchain_impl"), ToolchainResolutionFunctionTest.MAC_CTKEY, BuildViewTestCase.makeLabel("//toolchain:toolchain_2_impl"));
    }

    @Test
    public void testResolution_noneFound() throws Exception {
        // Clear the toolchains.
        rewriteWorkspace();
        SkyKey key = ToolchainResolutionValue.key(targetConfigKey, testToolchainTypeLabel, ToolchainResolutionFunctionTest.LINUX_CTKEY, ImmutableList.of(ToolchainResolutionFunctionTest.MAC_CTKEY));
        EvaluationResult<ToolchainResolutionValue> result = invokeToolchainResolution(key);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasErrorEntryForKeyThat(key).hasExceptionThat().hasMessageThat().contains("no matching toolchain found for //toolchain:test_toolchain");
    }

    @Test
    public void testToolchainResolutionValue_equalsAndHashCode() {
        // Multiple execution platforms.
        // Different execution platform, different label.
        // Same execution platform, different label.
        // Different execution platform, same label.
        new EqualsTester().addEqualityGroup(ToolchainResolutionValue.create(testToolchainType, ImmutableMap.of(ToolchainResolutionFunctionTest.LINUX_CTKEY, BuildViewTestCase.makeLabel("//test:toolchain_impl_1"))), ToolchainResolutionValue.create(testToolchainType, ImmutableMap.of(ToolchainResolutionFunctionTest.LINUX_CTKEY, BuildViewTestCase.makeLabel("//test:toolchain_impl_1")))).addEqualityGroup(ToolchainResolutionValue.create(testToolchainType, ImmutableMap.of(ToolchainResolutionFunctionTest.MAC_CTKEY, BuildViewTestCase.makeLabel("//test:toolchain_impl_1")))).addEqualityGroup(ToolchainResolutionValue.create(testToolchainType, ImmutableMap.of(ToolchainResolutionFunctionTest.LINUX_CTKEY, BuildViewTestCase.makeLabel("//test:toolchain_impl_2")))).addEqualityGroup(ToolchainResolutionValue.create(testToolchainType, ImmutableMap.of(ToolchainResolutionFunctionTest.MAC_CTKEY, BuildViewTestCase.makeLabel("//test:toolchain_impl_2")))).addEqualityGroup(ToolchainResolutionValue.create(testToolchainType, ImmutableMap.<ConfiguredTargetKey, Label>builder().put(ToolchainResolutionFunctionTest.LINUX_CTKEY, BuildViewTestCase.makeLabel("//test:toolchain_impl_1")).put(ToolchainResolutionFunctionTest.MAC_CTKEY, BuildViewTestCase.makeLabel("//test:toolchain_impl_1")).build())).testEquals();
    }

    /**
     * Use custom class instead of mock to make sure that the dynamic codecs lookup is correct.
     */
    class SerializableConfiguredTarget implements ConfiguredTarget {
        private final PlatformInfo platform;

        SerializableConfiguredTarget(PlatformInfo platform) {
            this.platform = platform;
        }

        @Override
        public ImmutableCollection<String> getFieldNames() {
            return null;
        }

        @Nullable
        @Override
        public String getErrorMessageForUnknownField(String field) {
            return null;
        }

        @Nullable
        @Override
        public Object getValue(String name) {
            return null;
        }

        @Override
        public Label getLabel() {
            return null;
        }

        @Nullable
        @Override
        public Key getConfigurationKey() {
            return null;
        }

        @Nullable
        @Override
        public <P extends TransitiveInfoProvider> P getProvider(Class<P> provider) {
            return null;
        }

        @Nullable
        @Override
        public Object get(String providerKey) {
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends InfoInterface> T get(NativeProvider<T> provider) {
            if (PROVIDER.equals(provider)) {
                return ((T) (this.platform));
            }
            return provider.getValueClass().cast(get(provider.getKey()));
        }

        @Nullable
        @Override
        public InfoInterface get(Provider.Key providerKey) {
            return null;
        }

        @Override
        public void repr(SkylarkPrinter printer) {
        }

        @Override
        public Object getIndex(Object key, Location loc) throws EvalException {
            return null;
        }

        @Override
        public boolean containsKey(Object key, Location loc) throws EvalException {
            return false;
        }
    }
}

