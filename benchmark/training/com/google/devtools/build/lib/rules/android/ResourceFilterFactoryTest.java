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
package com.google.devtools.build.lib.rules.android;


import Order.NAIVE_LINK_ORDER;
import ResourceFilterFactory.RESOURCE_CONFIGURATION_FILTERS_NAME;
import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.collect.nestedset.NestedSetBuilder;
import com.google.devtools.build.lib.collect.nestedset.Order;
import com.google.devtools.build.lib.packages.RuleClass.ConfiguredTargetFactory.RuleErrorException;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests {@link ResourceFilterFactory}.
 */
// TODO(asteinb): Test behavior not already covered in this test, and, when practical, move unit
// tests currently located in {@link AndroidBinaryTest} to this class instead.
@RunWith(JUnit4.class)
public class ResourceFilterFactoryTest extends ResourceTestBase {
    @Test
    public void testFilterInExecution() throws Exception {
        /* filterInAnalysis = */
        testNoopFilter("en", "hdpi", false, ImmutableList.of("values-en/foo.xml", "values/foo.xml", "values-hdpi/foo.png", "values-ldpi/foo.png"));
    }

    @Test
    public void testFilterEmpty() throws Exception {
        /* filterInAnalysis = */
        testNoopFilter("", "", true, ImmutableList.<String>of());
    }

    @Test
    public void testFilterDefaultAndNonDefault() throws Exception {
        /* filterInAnalysis = */
        testNoopFilter("en", "xhdpi,xxhdpi", true, ImmutableList.of("drawable/ic_clear.xml", "drawable-v21/ic_clear.xml"));
    }

    /**
     * Tests that version qualifiers are ignored for both resource qualifier and density filtering.
     */
    @Test
    public void testFilterVersionIgnored() throws Exception {
        /* filterInAnalysis = */
        testNoopFilter("v4", "hdpi", true, ImmutableList.of("drawable-hdpi-v4/foo.png", "drawable-hdpi-v11/foo.png"));
    }

    @Test
    public void testFilterByDensityPersistsOrdering() throws Exception {
        /* filterInAnalysis = */
        // If we add resources to the output list in density order, these resources will be
        // rearranged.
        // Filter out some resources to make sure the original list isn't just returned because the
        // filtering was a no-op.
        testFilter("", "hdpi,ldpi", true, ImmutableList.of("drawable-hdpi/foo.png", "drawable-ldpi/foo.png", "drawable-ldpi/bar.png", "drawable-hdpi/bar.png"), ImmutableList.of("drawable-mdpi/foo.png", "drawable-mdpi/bar.png"));
    }

    /**
     * Tests handling of Aapt's old region format
     */
    @Test
    public void testFilterOldLanguageFormat() throws Exception {
        /* filterInAnalysis = */
        testFilter("en_US", "", true, ImmutableList.of("values-en/foo.xml", "values-en-rUS/foo.xml"), ImmutableList.of("values-fr/foo.xml", "values-en-rCA/foo.xml"));
        errorConsumer.assertNoAttributeWarnings(RESOURCE_CONFIGURATION_FILTERS_NAME);
    }

    @Test
    public void testFilterOldLanguageFormatWithAdditionalQualifiers() throws Exception {
        /* filterInAnalysis = */
        testFilter("en_US-ldrtl", "", true, ImmutableList.of("values-en/foo.xml", "values-en-rUS/foo.xml"), ImmutableList.of("values-fr/foo.xml", "values-en-rCA/foo.xml"));
        errorConsumer.assertNoAttributeWarnings(RESOURCE_CONFIGURATION_FILTERS_NAME);
    }

    @Test
    public void testFilterOldLanguageFormatWithMcc() throws Exception {
        /* filterInAnalysis = */
        testFilter("mcc111-en_US", "", true, ImmutableList.of("values-en/foo.xml", "values-en-rUS/foo.xml"), ImmutableList.of("values-fr/foo.xml", "values-en-rCA/foo.xml"));
        errorConsumer.assertNoAttributeWarnings(RESOURCE_CONFIGURATION_FILTERS_NAME);
    }

    @Test
    public void testFilterOldLanguageFormatWithMccAndMnc() throws Exception {
        /* filterInAnalysis = */
        testFilter("mcc111-mnc111-en_US", "", true, ImmutableList.of("values-en/foo.xml", "values-en-rUS/foo.xml"), ImmutableList.of("values-fr/foo.xml", "values-en-rCA/foo.xml"));
        errorConsumer.assertNoAttributeWarnings(RESOURCE_CONFIGURATION_FILTERS_NAME);
    }

    @Test
    public void testFilterSerbianLatinCharacters() throws Exception {
        /* filterInAnalysis = */
        // Serbian in explicitly Cyrillic characters should be filtered out.
        testFilter("sr-Latn,sr-rLatn,sr_Latn,b+sr+Latn", "", true, ImmutableList.of("values-sr/foo.xml", "values-b+sr+Latn/foo.xml", "values-sr-Latn/foo.xml", "values-sr-rLatn/foo.xml"), ImmutableList.of("values-b+sr+Cyrl/foo.xml"));
        errorConsumer.assertNoAttributeWarnings(RESOURCE_CONFIGURATION_FILTERS_NAME);
    }

    @Test
    public void testFilterSerbianCharactersNotSpecified() throws Exception {
        /* filterInAnalysis = */
        testNoopFilter("sr", "", true, ImmutableList.of("values-sr/foo.xml", "values-b+sr+Latn/foo.xml", "values-b+sr+Cyrl/foo.xml", "values-sr-Latn/foo.xml", "values-sr-rLatn/foo.xml"));
        errorConsumer.assertNoAttributeWarnings(RESOURCE_CONFIGURATION_FILTERS_NAME);
    }

    @Test
    public void testFilterSpanishLatinAmericaAndCaribbean() throws Exception {
        /* filterInAnalysis = */
        // Spanish with another region specified should be filtered out.
        testFilter("es-419,es_419,b+es+419", "", true, ImmutableList.of("values-es/foo.xml", "values-b+es+419/foo.xml", "values-es-419/foo.xml"), ImmutableList.of("values-es-rUS/foo.xml"));
        errorConsumer.assertNoAttributeWarnings(RESOURCE_CONFIGURATION_FILTERS_NAME);
    }

    @Test
    public void testFilterSpanishRegionNotSpecified() throws Exception {
        /* filterInAnalysis = */
        testNoopFilter("es", "", true, ImmutableList.of("values-es/foo.xml", "values-b+es+419/foo.xml", "values-es-rUS/foo.xml", "values-es-419/foo.xml"));
        errorConsumer.assertNoAttributeWarnings(RESOURCE_CONFIGURATION_FILTERS_NAME);
    }

    @Test
    public void testFilterDeprecatedQualifierFormatsRuleWarnings() throws RuleErrorException {
        ImmutableList<Artifact> badResources = getResources("values-es_US/foo.xml", "drawable-sr-Latn/foo.xml", "layout-es-419/foo.xml", "values-mcc310-es_US/foo.xml", "values-sr_rLatn/foo.xml", "drawable-es_419/foo.xml");
        ImmutableList<String> expectedWarnings = ImmutableList.of(("For resource folder drawable-sr-Latn, when referring to Serbian in Latin characters, " + "use of qualifier 'sr-Latn' is deprecated. Use 'b+sr+Latn' instead."), ("For resource folder layout-es-419, when referring to Spanish for Latin America and " + "the Caribbean, use of qualifier 'es-419' is deprecated. Use 'b+es+419' instead."), ("For resource folder values-es_US, when referring to locale qualifiers with regions, " + "use of qualifier 'es_US' is deprecated. Use 'es-rUS' instead."));
        ResourceFilterFactory filter = /* filterInAnalysis = */
        new ResourceFilterFactory(ImmutableList.of("en"), ImmutableList.of(), true);
        doFilter(filter, badResources);
        assertThat(errorConsumer.getAndClearRuleWarnings()).containsExactlyElementsIn(expectedWarnings);
        errorConsumer.assertNoAttributeWarnings(RESOURCE_CONFIGURATION_FILTERS_NAME);
        // Filtering again with this filter should not produce additional warnings
        doFilter(filter, badResources);
        errorConsumer.assertNoRuleWarnings();
        errorConsumer.assertNoAttributeWarnings(RESOURCE_CONFIGURATION_FILTERS_NAME);
        // Filtering with a new filter should produce warnings again, since it is working on a different
        // target
        filter = /* filterInAnalysis = */
        new ResourceFilterFactory(ImmutableList.of("en"), ImmutableList.of(), true);
        doFilter(filter, badResources);
        assertThat(errorConsumer.getAndClearRuleWarnings()).containsExactlyElementsIn(expectedWarnings);
        errorConsumer.assertNoAttributeWarnings(RESOURCE_CONFIGURATION_FILTERS_NAME);
    }

    @Test
    public void testFilterResourceConflict() throws Exception {
        /* filterInAnalysis = */
        testNoopFilter("en", "hdpi", true, ImmutableList.of("first-subdir/res/drawable-en-hdpi/foo.png", "second-subdir/res/drawable-en-hdpi/foo.png"));
    }

    @Test
    public void testFilterLocalAndTransitive() throws Exception {
        Artifact localResourceToKeep = getResource("drawable-en-hdpi/local.png");
        Artifact localResourceToDiscard = getResource("drawable-en-ldpi/local.png");
        // These resources go in different ResourceContainers to ensure we are filter across all
        // resources.
        Artifact directResourceToKeep = getResource("direct/drawable-en-hdpi/direct.png");
        Artifact directResourceToDiscard = getResource("direct/drawable-en-ldpi/direct.png");
        Artifact transitiveResourceToKeep = getResource("transitive/drawable-en-hdpi/transitive.png");
        Artifact transitiveResourceToDiscard = getResource("transitive/drawable-en-ldpi/transitive.png");
        AndroidResources localResources = AndroidResources.forResources(errorConsumer, ImmutableList.of(localResourceToKeep, localResourceToDiscard), "resource_files");
        ResourceDependencies resourceDependencies = ResourceDependencies.empty().withResources(getResources(ImmutableList.of(transitiveResourceToDiscard), ImmutableList.of(transitiveResourceToKeep)), getResources(ImmutableList.of(directResourceToDiscard), ImmutableList.of(directResourceToKeep)), new NestedSetBuilder<Artifact>(Order.NAIVE_LINK_ORDER).add(directResourceToDiscard).add(directResourceToKeep).addTransitive(NestedSetBuilder.create(NAIVE_LINK_ORDER, transitiveResourceToDiscard, transitiveResourceToKeep)).build());
        ResourceFilterFactory resourceFilterFactory = /* filterInAnalysis = */
        makeResourceFilter("en", "hdpi", true);
        ResourceFilter filter = resourceFilterFactory.getResourceFilter(errorConsumer, resourceDependencies, localResources);
        assertThat(localResources.filterLocalResources(errorConsumer, filter).getResources()).containsExactly(localResourceToKeep);
        ResourceDependencies filteredResourceDeps = resourceDependencies.filter(errorConsumer, filter);
        // TODO: Remove - assert was same order before
        assertThat(resourceDependencies.getTransitiveResources()).containsAllOf(directResourceToKeep, transitiveResourceToKeep).inOrder();
        assertThat(filteredResourceDeps.getTransitiveResources()).containsExactly(directResourceToKeep, transitiveResourceToKeep).inOrder();
        List<ValidatedAndroidResources> directContainers = filteredResourceDeps.getDirectResourceContainers().toList();
        assertThat(directContainers).hasSize(2);
        ValidatedAndroidResources directToDiscard = directContainers.get(0);
        assertThat(directToDiscard.getResources()).isEmpty();
        assertThat(directToDiscard.getResourceRoots()).isEmpty();
        ValidatedAndroidResources directToKeep = directContainers.get(1);
        assertThat(directToKeep.getResources()).containsExactly(directResourceToKeep);
        assertThat(directToKeep.getResourceRoots()).containsExactly(directResourceToKeep.getExecPath().getParentDirectory().getParentDirectory());
        List<ValidatedAndroidResources> transitiveContainers = filteredResourceDeps.getTransitiveResourceContainers().toList();
        assertThat(transitiveContainers).hasSize(2);
        ValidatedAndroidResources transitiveToDiscard = transitiveContainers.get(0);
        assertThat(transitiveToDiscard.getResources()).isEmpty();
        assertThat(transitiveToDiscard.getResourceRoots()).isEmpty();
        ValidatedAndroidResources transitiveToKeep = transitiveContainers.get(1);
        assertThat(transitiveToKeep.getResources()).containsExactly(transitiveResourceToKeep);
        assertThat(transitiveToKeep.getResourceRoots()).containsExactly(transitiveResourceToKeep.getExecPath().getParentDirectory().getParentDirectory());
        // We tell the resource processing actions to ignore references to filtered resources from
        // dependencies.
        assertThat(resourceFilterFactory.getResourcesToIgnoreInExecution()).containsExactly("drawable-en-ldpi/direct.png", "drawable-en-ldpi/transitive.png");
    }

    @Test
    public void testIsPrefilteringFilterInExecution() throws Exception {
        /* filterInAnalysis = */
        assertIsPrefiltering(false, false);
    }

    @Test
    public void testIsPrefilteringFilterInAnalysis() throws Exception {
        /* filterInAnalysis = */
        assertIsPrefiltering(true, true);
    }
}

