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
package com.google.devtools.build.lib.packages;


import MissingFragmentPolicy.CREATE_FAIL_ACTIONS;
import MissingFragmentPolicy.IGNORE;
import NoTransition.INSTANCE;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.devtools.build.lib.analysis.config.BuildOptions;
import com.google.devtools.build.lib.analysis.config.transitions.ConfigurationTransition;
import com.google.devtools.build.lib.skylarkinterface.SkylarkModule;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for the ConfigurationFragmentPolicy builder and methods.
 */
@RunWith(JUnit4.class)
public final class ConfigurationFragmentPolicyTest {
    @SkylarkModule(name = "test_fragment", doc = "first fragment")
    private static final class TestFragment {}

    @SkylarkModule(name = "other_fragment", doc = "second fragment")
    private static final class OtherFragment {}

    @SkylarkModule(name = "unknown_fragment", doc = "useless waste of permgen")
    private static final class UnknownFragment {}

    @Test
    public void testMissingFragmentPolicy() throws Exception {
        ConfigurationFragmentPolicy policy = new ConfigurationFragmentPolicy.Builder().setMissingFragmentPolicy(IGNORE).build();
        assertThat(policy.getMissingFragmentPolicy()).isEqualTo(IGNORE);
        ConfigurationFragmentPolicy otherPolicy = new ConfigurationFragmentPolicy.Builder().setMissingFragmentPolicy(CREATE_FAIL_ACTIONS).build();
        assertThat(otherPolicy.getMissingFragmentPolicy()).isEqualTo(CREATE_FAIL_ACTIONS);
    }

    @Test
    public void testRequiresConfigurationFragments_AddsToRequiredSet() throws Exception {
        // Although these aren't configuration fragments, there are no requirements as to what the class
        // has to be, so...
        ConfigurationFragmentPolicy policy = new ConfigurationFragmentPolicy.Builder().requiresConfigurationFragments(ImmutableSet.<Class<?>>of(Integer.class, String.class)).requiresConfigurationFragments(ImmutableSet.<Class<?>>of(String.class, Long.class)).build();
        assertThat(policy.getRequiredConfigurationFragments()).containsExactly(Integer.class, String.class, Long.class);
    }

    private static final ConfigurationTransition TEST_HOST_TRANSITION = new ConfigurationTransition() {
        @Override
        public List<BuildOptions> apply(BuildOptions buildOptions) {
            return ImmutableList.of(buildOptions);
        }

        @Override
        public String reasonForOverride() {
            return null;
        }

        @Override
        public boolean isHostTransition() {
            return true;
        }
    };

    @Test
    public void testRequiresConfigurationFragments_RequiredAndLegalForSpecifiedConfiguration() throws Exception {
        ConfigurationFragmentPolicy policy = new ConfigurationFragmentPolicy.Builder().requiresConfigurationFragments(ImmutableSet.<Class<?>>of(Integer.class)).requiresConfigurationFragments(ConfigurationFragmentPolicyTest.TEST_HOST_TRANSITION, ImmutableSet.<Class<?>>of(Long.class)).build();
        assertThat(policy.getRequiredConfigurationFragments()).containsAllOf(Integer.class, Long.class);
        assertThat(policy.isLegalConfigurationFragment(Integer.class)).isTrue();
        assertThat(policy.isLegalConfigurationFragment(Integer.class, INSTANCE)).isTrue();
        // TODO(mstaib): .isFalse() when dynamic configurations care which configuration a fragment was
        // specified for
        assertThat(policy.isLegalConfigurationFragment(Integer.class, ConfigurationFragmentPolicyTest.TEST_HOST_TRANSITION)).isTrue();
        assertThat(policy.isLegalConfigurationFragment(Long.class)).isTrue();
        // TODO(mstaib): .isFalse() when dynamic configurations care which configuration a fragment was
        // specified for
        assertThat(policy.isLegalConfigurationFragment(Long.class, INSTANCE)).isTrue();
        assertThat(policy.isLegalConfigurationFragment(Long.class, ConfigurationFragmentPolicyTest.TEST_HOST_TRANSITION)).isTrue();
        assertThat(policy.isLegalConfigurationFragment(String.class)).isFalse();
        assertThat(policy.isLegalConfigurationFragment(String.class, INSTANCE)).isFalse();
        assertThat(policy.isLegalConfigurationFragment(String.class, ConfigurationFragmentPolicyTest.TEST_HOST_TRANSITION)).isFalse();
    }

    @Test
    public void testRequiresConfigurationFragments_MapSetsLegalityBySkylarkModuleName_NoRequires() throws Exception {
        ConfigurationFragmentPolicy policy = new ConfigurationFragmentPolicy.Builder().requiresConfigurationFragmentsBySkylarkModuleName(ImmutableSet.of("test_fragment")).requiresConfigurationFragmentsBySkylarkModuleName(ConfigurationFragmentPolicyTest.TEST_HOST_TRANSITION, ImmutableSet.of("other_fragment")).build();
        assertThat(policy.getRequiredConfigurationFragments()).isEmpty();
        assertThat(policy.isLegalConfigurationFragment(ConfigurationFragmentPolicyTest.TestFragment.class)).isTrue();
        assertThat(policy.isLegalConfigurationFragment(ConfigurationFragmentPolicyTest.TestFragment.class, INSTANCE)).isTrue();
        assertThat(policy.isLegalConfigurationFragment(ConfigurationFragmentPolicyTest.TestFragment.class, ConfigurationFragmentPolicyTest.TEST_HOST_TRANSITION)).isFalse();
        assertThat(policy.isLegalConfigurationFragment(ConfigurationFragmentPolicyTest.OtherFragment.class)).isTrue();
        assertThat(policy.isLegalConfigurationFragment(ConfigurationFragmentPolicyTest.OtherFragment.class, INSTANCE)).isFalse();
        assertThat(policy.isLegalConfigurationFragment(ConfigurationFragmentPolicyTest.OtherFragment.class, ConfigurationFragmentPolicyTest.TEST_HOST_TRANSITION)).isTrue();
        assertThat(policy.isLegalConfigurationFragment(ConfigurationFragmentPolicyTest.UnknownFragment.class)).isFalse();
        assertThat(policy.isLegalConfigurationFragment(ConfigurationFragmentPolicyTest.UnknownFragment.class, INSTANCE)).isFalse();
        assertThat(policy.isLegalConfigurationFragment(ConfigurationFragmentPolicyTest.UnknownFragment.class, ConfigurationFragmentPolicyTest.TEST_HOST_TRANSITION)).isFalse();
    }

    @Test
    public void testIncludeConfigurationFragmentsFrom_MergesWithExistingFragmentSet() throws Exception {
        ConfigurationFragmentPolicy basePolicy = new ConfigurationFragmentPolicy.Builder().requiresConfigurationFragmentsBySkylarkModuleName(ImmutableSet.of("test_fragment")).requiresConfigurationFragments(ImmutableSet.<Class<?>>of(Integer.class, Double.class)).build();
        ConfigurationFragmentPolicy addedPolicy = new ConfigurationFragmentPolicy.Builder().requiresConfigurationFragmentsBySkylarkModuleName(ImmutableSet.of("other_fragment")).requiresConfigurationFragmentsBySkylarkModuleName(ConfigurationFragmentPolicyTest.TEST_HOST_TRANSITION, ImmutableSet.of("other_fragment")).requiresConfigurationFragments(ImmutableSet.<Class<?>>of(Boolean.class)).requiresConfigurationFragments(ConfigurationFragmentPolicyTest.TEST_HOST_TRANSITION, ImmutableSet.<Class<?>>of(Character.class)).build();
        ConfigurationFragmentPolicy combinedPolicy = new ConfigurationFragmentPolicy.Builder().includeConfigurationFragmentsFrom(basePolicy).includeConfigurationFragmentsFrom(addedPolicy).build();
        assertThat(combinedPolicy.getRequiredConfigurationFragments()).containsExactly(Integer.class, Double.class, Boolean.class, Character.class);
        assertThat(combinedPolicy.isLegalConfigurationFragment(ConfigurationFragmentPolicyTest.TestFragment.class)).isTrue();
        assertThat(combinedPolicy.isLegalConfigurationFragment(ConfigurationFragmentPolicyTest.OtherFragment.class)).isTrue();
    }
}

