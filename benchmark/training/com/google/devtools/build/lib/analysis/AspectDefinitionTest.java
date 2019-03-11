/**
 * Copyright 2015 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.analysis;


import AdvertisedProviderSet.ANY;
import AdvertisedProviderSet.EMPTY;
import BuildConfiguration.Fragment;
import BuildType.LABEL;
import FileTypeSet.NO_FILE;
import MissingFragmentPolicy.IGNORE;
import NoTransition.INSTANCE;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.devtools.build.lib.analysis.config.BuildConfiguration;
import com.google.devtools.build.lib.analysis.config.ConfigAwareAspectBuilder;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.packages.AdvertisedProviderSet;
import com.google.devtools.build.lib.packages.AspectDefinition;
import com.google.devtools.build.lib.packages.AspectParameters;
import com.google.devtools.build.lib.packages.Attribute;
import com.google.devtools.build.lib.packages.Attribute.LabelLateBoundDefault;
import com.google.devtools.build.lib.packages.Attribute.LateBoundDefault;
import com.google.devtools.build.lib.packages.NativeAspectClass;
import com.google.devtools.build.lib.skyframe.ConfiguredTargetAndData;
import com.google.devtools.build.lib.skylarkinterface.SkylarkModule;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for aspect definitions.
 */
@RunWith(JUnit4.class)
public class AspectDefinitionTest {
    /**
     * A dummy aspect factory. Is there to demonstrate how to define aspects and so that we can test
     * {@code attributeAspect}.
     */
    public static final class TestAspectClass extends NativeAspectClass implements ConfiguredAspectFactory {
        private AspectDefinition definition;

        public void setAspectDefinition(AspectDefinition definition) {
            this.definition = definition;
        }

        @Override
        public ConfiguredAspect create(ConfiguredTargetAndData ctadBase, RuleContext context, AspectParameters parameters, String toolsRepository) {
            throw new IllegalStateException();
        }

        @Override
        public AspectDefinition getDefinition(AspectParameters aspectParameters) {
            return definition;
        }
    }

    public static final AspectDefinitionTest.TestAspectClass TEST_ASPECT_CLASS = new AspectDefinitionTest.TestAspectClass();

    @Test
    public void testAspectWithImplicitOrLateboundAttribute_AddsToAttributeMap() throws Exception {
        Attribute implicit = Attribute.attr("$runtime", LABEL).value(Label.parseAbsoluteUnchecked("//run:time")).build();
        LabelLateBoundDefault<Void> latebound = LateBoundDefault.fromConstantForTesting(Label.parseAbsoluteUnchecked("//run:away"));
        AspectDefinition simple = new AspectDefinition.Builder(AspectDefinitionTest.TEST_ASPECT_CLASS).add(implicit).add(Attribute.attr(":latebound", LABEL).value(latebound)).build();
        assertThat(simple.getAttributes()).containsEntry("$runtime", implicit);
        assertThat(simple.getAttributes()).containsKey(":latebound");
        assertThat(simple.getAttributes().get(":latebound").getLateBoundDefault()).isEqualTo(latebound);
    }

    @Test
    public void testAspectWithDuplicateAttribute_FailsToAdd() throws Exception {
        try {
            new AspectDefinition.Builder(AspectDefinitionTest.TEST_ASPECT_CLASS).add(Attribute.attr("$runtime", LABEL).value(Label.parseAbsoluteUnchecked("//run:time"))).add(Attribute.attr("$runtime", LABEL).value(Label.parseAbsoluteUnchecked("//oops")));
            Assert.fail();// expected IllegalArgumentException

        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testAspectWithUserVisibleAttribute_FailsToAdd() throws Exception {
        try {
            new AspectDefinition.Builder(AspectDefinitionTest.TEST_ASPECT_CLASS).add(Attribute.attr("invalid", LABEL).value(Label.parseAbsoluteUnchecked("//run:time")).allowedFileTypes(NO_FILE)).build();
            Assert.fail();// expected IllegalArgumentException

        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testAttributeAspect_WrapsAndAddsToMap() throws Exception {
        AspectDefinition withAspects = new AspectDefinition.Builder(AspectDefinitionTest.TEST_ASPECT_CLASS).propagateAlongAttribute("srcs").propagateAlongAttribute("deps").build();
        assertThat(withAspects.propagateAlong("srcs")).isTrue();
        assertThat(withAspects.propagateAlong("deps")).isTrue();
    }

    @Test
    public void testAttributeAspect_AllAttributes() throws Exception {
        AspectDefinition withAspects = new AspectDefinition.Builder(AspectDefinitionTest.TEST_ASPECT_CLASS).propagateAlongAllAttributes().build();
        assertThat(withAspects.propagateAlong("srcs")).isTrue();
        assertThat(withAspects.propagateAlong("deps")).isTrue();
    }

    @Test
    public void testRequireProvider_AddsToSetOfRequiredProvidersAndNames() throws Exception {
        AspectDefinition requiresProviders = new AspectDefinition.Builder(AspectDefinitionTest.TEST_ASPECT_CLASS).requireProviders(String.class, Integer.class).build();
        AdvertisedProviderSet expectedOkSet = AdvertisedProviderSet.builder().addNative(String.class).addNative(Integer.class).addNative(Boolean.class).build();
        assertThat(requiresProviders.getRequiredProviders().isSatisfiedBy(expectedOkSet)).isTrue();
        AdvertisedProviderSet expectedFailSet = AdvertisedProviderSet.builder().addNative(String.class).build();
        assertThat(requiresProviders.getRequiredProviders().isSatisfiedBy(expectedFailSet)).isFalse();
        assertThat(requiresProviders.getRequiredProviders().isSatisfiedBy(ANY)).isTrue();
        assertThat(requiresProviders.getRequiredProviders().isSatisfiedBy(EMPTY)).isFalse();
    }

    @Test
    public void testRequireProvider_AddsTwoSetsOfRequiredProvidersAndNames() throws Exception {
        AspectDefinition requiresProviders = new AspectDefinition.Builder(AspectDefinitionTest.TEST_ASPECT_CLASS).requireProviderSets(ImmutableList.of(ImmutableSet.<Class<?>>of(String.class, Integer.class), ImmutableSet.<Class<?>>of(Boolean.class))).build();
        AdvertisedProviderSet expectedOkSet1 = AdvertisedProviderSet.builder().addNative(String.class).addNative(Integer.class).build();
        AdvertisedProviderSet expectedOkSet2 = AdvertisedProviderSet.builder().addNative(Boolean.class).build();
        AdvertisedProviderSet expectedFailSet = AdvertisedProviderSet.builder().addNative(Float.class).build();
        assertThat(requiresProviders.getRequiredProviders().isSatisfiedBy(ANY)).isTrue();
        assertThat(requiresProviders.getRequiredProviders().isSatisfiedBy(expectedOkSet1)).isTrue();
        assertThat(requiresProviders.getRequiredProviders().isSatisfiedBy(expectedOkSet2)).isTrue();
        assertThat(requiresProviders.getRequiredProviders().isSatisfiedBy(expectedFailSet)).isFalse();
        assertThat(requiresProviders.getRequiredProviders().isSatisfiedBy(EMPTY)).isFalse();
    }

    @Test
    public void testRequireAspectClass_DefaultAcceptsNothing() {
        AspectDefinition noAspects = new AspectDefinition.Builder(AspectDefinitionTest.TEST_ASPECT_CLASS).build();
        AdvertisedProviderSet expectedFailSet = AdvertisedProviderSet.builder().addNative(Float.class).build();
        assertThat(noAspects.getRequiredProvidersForAspects().isSatisfiedBy(ANY)).isFalse();
        assertThat(noAspects.getRequiredProvidersForAspects().isSatisfiedBy(EMPTY)).isFalse();
        assertThat(noAspects.getRequiredProvidersForAspects().isSatisfiedBy(expectedFailSet)).isFalse();
    }

    @Test
    public void testNoConfigurationFragmentPolicySetup_HasNonNullPolicy() throws Exception {
        AspectDefinition noPolicy = new AspectDefinition.Builder(AspectDefinitionTest.TEST_ASPECT_CLASS).build();
        assertThat(noPolicy.getConfigurationFragmentPolicy()).isNotNull();
    }

    @Test
    public void testMissingFragmentPolicy_PropagatedToConfigurationFragmentPolicy() throws Exception {
        AspectDefinition missingFragments = new AspectDefinition.Builder(AspectDefinitionTest.TEST_ASPECT_CLASS).setMissingFragmentPolicy(IGNORE).build();
        assertThat(missingFragments.getConfigurationFragmentPolicy()).isNotNull();
        assertThat(missingFragments.getConfigurationFragmentPolicy().getMissingFragmentPolicy()).isEqualTo(IGNORE);
    }

    @Test
    public void testRequiresConfigurationFragments_PropagatedToConfigurationFragmentPolicy() throws Exception {
        AspectDefinition requiresFragments = new AspectDefinition.Builder(AspectDefinitionTest.TEST_ASPECT_CLASS).requiresConfigurationFragments(Integer.class, String.class).build();
        assertThat(requiresFragments.getConfigurationFragmentPolicy()).isNotNull();
        assertThat(requiresFragments.getConfigurationFragmentPolicy().getRequiredConfigurationFragments()).containsExactly(Integer.class, String.class);
    }

    private static class FooFragment extends BuildConfiguration.Fragment {}

    private static class BarFragment extends BuildConfiguration.Fragment {}

    @Test
    public void testRequiresHostConfigurationFragments_PropagatedToConfigurationFragmentPolicy() throws Exception {
        AspectDefinition requiresFragments = ConfigAwareAspectBuilder.of(new AspectDefinition.Builder(AspectDefinitionTest.TEST_ASPECT_CLASS)).requiresHostConfigurationFragments(AspectDefinitionTest.FooFragment.class, AspectDefinitionTest.BarFragment.class).originalBuilder().build();
        assertThat(requiresFragments.getConfigurationFragmentPolicy()).isNotNull();
        assertThat(requiresFragments.getConfigurationFragmentPolicy().getRequiredConfigurationFragments()).containsExactly(AspectDefinitionTest.FooFragment.class, AspectDefinitionTest.BarFragment.class);
    }

    @Test
    public void testRequiresConfigurationFragmentNames_PropagatedToConfigurationFragmentPolicy() throws Exception {
        AspectDefinition requiresFragments = new AspectDefinition.Builder(AspectDefinitionTest.TEST_ASPECT_CLASS).requiresConfigurationFragmentsBySkylarkModuleName(ImmutableList.of("test_fragment")).build();
        assertThat(requiresFragments.getConfigurationFragmentPolicy()).isNotNull();
        assertThat(requiresFragments.getConfigurationFragmentPolicy().isLegalConfigurationFragment(AspectDefinitionTest.TestFragment.class, INSTANCE)).isTrue();
    }

    @Test
    public void testRequiresHostConfigurationFragmentNames_PropagatedToConfigurationFragmentPolicy() throws Exception {
        AspectDefinition requiresFragments = ConfigAwareAspectBuilder.of(new AspectDefinition.Builder(AspectDefinitionTest.TEST_ASPECT_CLASS)).requiresHostConfigurationFragmentsBySkylarkModuleName(ImmutableList.of("test_fragment")).originalBuilder().build();
        assertThat(requiresFragments.getConfigurationFragmentPolicy()).isNotNull();
        assertThat(requiresFragments.getConfigurationFragmentPolicy().isLegalConfigurationFragment(AspectDefinitionTest.TestFragment.class, HostTransition.INSTANCE)).isTrue();
    }

    @Test
    public void testEmptySkylarkConfigurationFragmentPolicySetup_HasNonNullPolicy() throws Exception {
        AspectDefinition noPolicy = ConfigAwareAspectBuilder.of(new AspectDefinition.Builder(AspectDefinitionTest.TEST_ASPECT_CLASS)).requiresHostConfigurationFragmentsBySkylarkModuleName(ImmutableList.<String>of()).originalBuilder().requiresConfigurationFragmentsBySkylarkModuleName(ImmutableList.<String>of()).build();
        assertThat(noPolicy.getConfigurationFragmentPolicy()).isNotNull();
    }

    @SkylarkModule(name = "test_fragment", doc = "test fragment")
    private static final class TestFragment {}
}

