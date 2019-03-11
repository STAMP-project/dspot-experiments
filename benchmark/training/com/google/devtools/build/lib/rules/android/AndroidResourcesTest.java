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


import AndroidAaptVersion.AAPT;
import AndroidAaptVersion.AAPT2;
import AndroidAaptVersion.AUTO;
import AndroidRuleClasses.ANDROID_JAVA_SOURCE_JAR;
import AndroidRuleClasses.ANDROID_RESOURCES_APK;
import AndroidRuleClasses.ANDROID_R_TXT;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.analysis.RuleContext;
import com.google.devtools.build.lib.packages.RuleClass.ConfiguredTargetFactory.RuleErrorException;
import com.google.devtools.build.lib.rules.android.AndroidConfiguration.AndroidAaptVersion;
import com.google.devtools.build.lib.rules.android.databinding.DataBinding;
import com.google.devtools.build.lib.vfs.PathFragment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests {@link AndroidResources}
 */
@RunWith(JUnit4.class)
public class AndroidResourcesTest extends ResourceTestBase {
    private static final PathFragment DEFAULT_RESOURCE_ROOT = PathFragment.create(ResourceTestBase.RESOURCE_ROOT);

    private static final ImmutableList<PathFragment> RESOURCES_ROOTS = ImmutableList.of(AndroidResourcesTest.DEFAULT_RESOURCE_ROOT);

    @Before
    @Test
    public void testGetResourceRootsNoResources() throws Exception {
        assertThat(getResourceRoots()).isEmpty();
    }

    @Test
    public void testGetResourceRootsInvalidResourceDirectory() throws Exception {
        try {
            getResourceRoots("is-this-drawable-or-values/foo.xml");
            assertWithMessage("Expected exception not thrown!").fail();
        } catch (RuleErrorException e) {
            // expected
        }
        errorConsumer.assertAttributeError("resource_files", "is not in the expected resource directory structure");
    }

    @Test
    public void testGetResourceRootsMultipleRoots() throws Exception {
        try {
            getResourceRoots("subdir/values/foo.xml", "otherdir/values/bar.xml");
            assertWithMessage("Expected exception not thrown!").fail();
        } catch (RuleErrorException e) {
            // expected
        }
        errorConsumer.assertAttributeError("resource_files", "All resources must share a common directory");
    }

    @Test
    public void testGetResourceRoots() throws Exception {
        assertThat(getResourceRoots("values-hdpi/foo.xml", "values-mdpi/bar.xml")).isEqualTo(AndroidResourcesTest.RESOURCES_ROOTS);
    }

    @Test
    public void testGetResourceRootsCommonSubdirectory() throws Exception {
        assertThat(getResourceRoots("subdir/values-hdpi/foo.xml", "subdir/values-mdpi/bar.xml")).containsExactly(AndroidResourcesTest.DEFAULT_RESOURCE_ROOT.getRelative("subdir"));
    }

    @Test
    public void testFilterEmpty() throws Exception {
        assertFilter(ImmutableList.of(), ImmutableList.of());
    }

    @Test
    public void testFilterNoop() throws Exception {
        ImmutableList<Artifact> resources = getResources("values-en/foo.xml", "values-es/bar.xml");
        assertFilter(resources, resources);
    }

    @Test
    public void testFilterToEmpty() throws Exception {
        assertFilter(getResources("values-en/foo.xml", "values-es/bar.xml"), ImmutableList.of());
    }

    @Test
    public void testPartiallyFilter() throws Exception {
        Artifact keptResource = getResource("values-en/foo.xml");
        assertFilter(ImmutableList.of(keptResource, getResource("values-es/bar.xml")), ImmutableList.of(keptResource));
    }

    @Test
    public void testFilterIsDependency() throws Exception {
        Artifact keptResource = getResource("values-en/foo.xml");
        /* isDependency = */
        assertFilter(ImmutableList.of(keptResource, getResource("drawable/bar.png")), ImmutableList.of(keptResource), true);
    }

    @Test
    public void testFilterValidatedNoop() throws Exception {
        ImmutableList<Artifact> resources = getResources("values-en/foo.xml", "values-es/bar.xml");
        assertFilterValidated(resources, resources);
    }

    @Test
    public void testFilterValidated() throws Exception {
        Artifact keptResource = getResource("values-en/foo.xml");
        assertFilterValidated(ImmutableList.of(keptResource, getResource("drawable/bar.png")), ImmutableList.of(keptResource));
    }

    @Test
    public void testParseNoCompile() throws Exception {
        useConfiguration("--android_aapt=aapt");
        RuleContext ruleContext = getRuleContext();
        ParsedAndroidResources parsed = assertParse(ruleContext, DataBinding.contextFrom(ruleContext, ruleContext.getConfiguration().getFragment(AndroidConfiguration.class)));
        // Since we are not using aapt2, there should be no compiled symbols
        assertThat(parsed.getCompiledSymbols()).isNull();
        // The parse action should take resources in and output symbols
        /* inputs = */
        /* outputs = */
        assertActionArtifacts(ruleContext, parsed.getResources(), ImmutableList.of(parsed.getSymbols()));
    }

    @Test
    public void testParseAndCompile() throws Exception {
        mockAndroidSdkWithAapt2();
        useConfiguration("--android_sdk=//sdk:sdk", "--android_aapt=aapt2");
        RuleContext ruleContext = getRuleContext();
        ParsedAndroidResources parsed = assertParse(ruleContext);
        assertThat(parsed.getCompiledSymbols()).isNotNull();
        // The parse action should take resources in and output symbols
        /* inputs = */
        /* outputs = */
        assertActionArtifacts(ruleContext, parsed.getResources(), ImmutableList.of(parsed.getSymbols()));
        // Since there was no data binding, the compile action should just take in resources and output
        // compiled symbols.
        /* inputs = */
        /* outputs = */
        assertActionArtifacts(ruleContext, parsed.getResources(), ImmutableList.of(parsed.getCompiledSymbols()));
    }

    @Test
    public void testParseWithDataBinding() throws Exception {
        mockAndroidSdkWithAapt2();
        useConfiguration("--android_sdk=//sdk:sdk", "--android_aapt=aapt2");
        RuleContext ruleContext = getRuleContextWithDataBinding();
        ParsedAndroidResources parsed = assertParse(ruleContext);
        // The parse action should take resources and busybox artifacts in and output symbols
        /* inputs = */
        /* outputs = */
        assertActionArtifacts(ruleContext, parsed.getResources(), ImmutableList.of(parsed.getSymbols()));
        // The compile action should take in resources and manifest in and output compiled symbols and
        // an unused data binding zip.
        /* inputs = */
        /* outputs = */
        assertActionArtifacts(ruleContext, ImmutableList.<Artifact>builder().addAll(parsed.getResources()).add(parsed.getManifest()).build(), ImmutableList.of(parsed.getCompiledSymbols(), ParsedAndroidResources.getDummyDataBindingArtifact(ruleContext)));
    }

    @Test
    public void testMergeDataBinding() throws Exception {
        useConfiguration("--android_aapt=aapt");
        RuleContext ruleContext = getRuleContextWithDataBinding();
        ParsedAndroidResources parsed = assertParse(ruleContext);
        MergedAndroidResources merged = parsed.merge(AndroidDataContext.forNative(ruleContext), ResourceDependencies.empty(), AndroidAaptVersion.chooseTargetAaptVersion(ruleContext));
        // Besides processed manifest, inherited values should be equal
        assertThat(parsed).isEqualTo(new ParsedAndroidResources(merged, parsed.getStampedManifest()));
        // There should be a new processed manifest
        assertThat(merged.getManifest()).isNotEqualTo(parsed.getManifest());
        assertThat(merged.getDataBindingInfoZip()).isNotNull();
        /* inputs = */
        /* outputs = */
        assertActionArtifacts(ruleContext, ImmutableList.<Artifact>builder().addAll(merged.getResources()).add(merged.getSymbols()).add(parsed.getManifest()).build(), ImmutableList.of(merged.getMergedResources(), merged.getClassJar(), merged.getDataBindingInfoZip(), merged.getManifest()));
    }

    @Test
    public void testMergeCompiled() throws Exception {
        mockAndroidSdkWithAapt2();
        useConfiguration("--android_sdk=//sdk:sdk", "--android_aapt=aapt2", "--experimental_skip_parsing_action");
        RuleContext ruleContext = getRuleContext();
        ParsedAndroidResources parsed = assertParse(ruleContext);
        MergedAndroidResources merged = parsed.merge(AndroidDataContext.forNative(ruleContext), /* neverlink = */
        ResourceDependencies.fromRuleDeps(ruleContext, false), AndroidAaptVersion.chooseTargetAaptVersion(ruleContext));
        // Besides processed manifest, inherited values should be equal
        assertThat(parsed).isEqualTo(new ParsedAndroidResources(merged, parsed.getStampedManifest()));
        // There should be a new processed manifest
        assertThat(merged.getManifest()).isNotEqualTo(parsed.getManifest());
        assertThat(merged.getDataBindingInfoZip()).isNull();
        assertThat(merged.getCompiledSymbols()).isNotNull();
        // We use the compiled symbols file to build the resource class jar
        /* inputs = */
        /* outputs = */
        assertActionArtifacts(ruleContext, ImmutableList.<Artifact>builder().addAll(merged.getResources()).add(merged.getCompiledSymbols()).add(parsed.getManifest()).build(), ImmutableList.of(merged.getClassJar(), merged.getManifest()));
        // The old symbols file is still needed to build the merged resources zip
        /* inputs = */
        /* outputs = */
        assertActionArtifacts(ruleContext, ImmutableList.<Artifact>builder().addAll(merged.getResources()).add(merged.getSymbols()).add(parsed.getManifest()).build(), ImmutableList.of(merged.getMergedResources()));
    }

    @Test
    public void testValidateAapt() throws Exception {
        useConfiguration("--android_aapt=aapt");
        RuleContext ruleContext = getRuleContext();
        MergedAndroidResources merged = makeMergedResources(ruleContext);
        ValidatedAndroidResources validated = merged.validate(AndroidDataContext.forNative(ruleContext), AndroidAaptVersion.chooseTargetAaptVersion(ruleContext));
        // Inherited values should be equal
        assertThat(merged).isEqualTo(new MergedAndroidResources(validated));
        // aapt artifacts should be generated
        /* inputs = */
        /* outputs = */
        assertActionArtifacts(ruleContext, ImmutableList.of(validated.getMergedResources(), validated.getManifest()), ImmutableList.of(validated.getRTxt(), validated.getJavaSourceJar(), validated.getApk()));
        // aapt2 artifacts should not be generated
        assertThat(validated.getCompiledSymbols()).isNull();
        assertThat(validated.getAapt2RTxt()).isNull();
        assertThat(validated.getAapt2SourceJar()).isNull();
        assertThat(validated.getStaticLibrary()).isNull();
    }

    @Test
    public void testValidateAapt2() throws Exception {
        mockAndroidSdkWithAapt2();
        useConfiguration("--android_sdk=//sdk:sdk", "--android_aapt=aapt2");
        RuleContext ruleContext = getRuleContext();
        MergedAndroidResources merged = makeMergedResources(ruleContext);
        ValidatedAndroidResources validated = merged.validate(AndroidDataContext.forNative(ruleContext), AndroidAaptVersion.chooseTargetAaptVersion(ruleContext));
        // Inherited values should be equal
        assertThat(merged).isEqualTo(new MergedAndroidResources(validated));
        // aapt artifacts should be generated
        /* inputs = */
        /* outputs = */
        assertActionArtifacts(ruleContext, ImmutableList.of(validated.getMergedResources(), validated.getManifest()), ImmutableList.of(validated.getRTxt(), validated.getJavaSourceJar(), validated.getApk()));
        // aapt2 artifacts should be recorded
        assertThat(validated.getCompiledSymbols()).isNotNull();
        assertThat(validated.getAapt2RTxt()).isNotNull();
        assertThat(validated.getAapt2SourceJar()).isNotNull();
        assertThat(validated.getStaticLibrary()).isNotNull();
        // Compile the resources into compiled symbols files
        /* inputs = */
        /* outputs = */
        assertActionArtifacts(ruleContext, validated.getResources(), ImmutableList.of(validated.getCompiledSymbols()));
        // Use the compiled symbols and manifest to build aapt2 packaging outputs
        /* inputs = */
        /* outputs = */
        assertActionArtifacts(ruleContext, ImmutableList.of(validated.getCompiledSymbols(), validated.getManifest()), ImmutableList.of(validated.getAapt2RTxt(), validated.getAapt2SourceJar(), validated.getStaticLibrary()));
    }

    @Test
    public void testGenerateRClass() throws Exception {
        RuleContext ruleContext = getRuleContext();
        Artifact rTxt = ruleContext.getImplicitOutputArtifact(ANDROID_R_TXT);
        ProcessedAndroidManifest manifest = getManifest();
        ProcessedAndroidData processedData = /* dataBindingInfoZip = */
        ProcessedAndroidData.of(makeParsedResources(ruleContext), AndroidAssets.from(ruleContext).process(AndroidDataContext.forNative(ruleContext), AssetDependencies.empty(), AndroidAaptVersion.chooseTargetAaptVersion(ruleContext)), manifest, rTxt, ruleContext.getImplicitOutputArtifact(ANDROID_JAVA_SOURCE_JAR), ruleContext.getImplicitOutputArtifact(ANDROID_RESOURCES_APK), null, /* neverlink = */
        ResourceDependencies.fromRuleDeps(ruleContext, false), null, null);
        ValidatedAndroidResources validated = processedData.generateRClass(AndroidDataContext.forNative(ruleContext), AndroidAaptVersion.chooseTargetAaptVersion(ruleContext)).getValidatedResources();
        // An action to generate the R.class file should be registered.
        /* inputs = */
        /* outputs = */
        assertActionArtifacts(ruleContext, ImmutableList.of(rTxt, manifest.getManifest()), ImmutableList.of(validated.getJavaClassJar()));
    }

    @Test
    public void testProcessBinaryDataGeneratesProguardOutput() throws Exception {
        RuleContext ruleContext = getRuleContext("android_binary", "manifest='AndroidManifest.xml',");
        AndroidDataContext dataContext = AndroidDataContext.forNative(ruleContext);
        ResourceApk resourceApk = ProcessedAndroidData.processBinaryDataFrom(dataContext, ruleContext, getManifest(), false, ImmutableMap.of(), AUTO, AndroidResources.empty(), AndroidAssets.empty(), ResourceDependencies.empty(), AssetDependencies.empty(), ResourceFilterFactory.empty(), ImmutableList.of(), false, null, null, DataBinding.contextFrom(ruleContext, dataContext.getAndroidConfig())).generateRClass(dataContext, AUTO);
        assertThat(resourceApk.getResourceProguardConfig()).isNotNull();
        assertThat(resourceApk.getMainDexProguardConfig()).isNotNull();
    }

    @Test
    public void test_incompatibleUseAapt2ByDefaultEnabled_targetsAapt2() throws Exception {
        mockAndroidSdkWithAapt2();
        useConfiguration("--android_sdk=//sdk:sdk", "--incompatible_use_aapt2_by_default");
        RuleContext ruleContext = getRuleContext("android_binary", "aapt_version = 'auto',", "manifest = 'AndroidManifest.xml',");
        assertThat(AndroidAaptVersion.chooseTargetAaptVersion(ruleContext)).isEqualTo(AAPT2);
    }

    @Test
    public void test_incompatibleUseAapt2ByDefaultDisabled_targetsAapt() throws Exception {
        mockAndroidSdkWithAapt2();
        useConfiguration("--android_sdk=//sdk:sdk", "--noincompatible_use_aapt2_by_default");
        RuleContext ruleContext = getRuleContext("android_binary", "aapt_version = 'auto',", "manifest = 'AndroidManifest.xml',");
        assertThat(AndroidAaptVersion.chooseTargetAaptVersion(ruleContext)).isEqualTo(AAPT);
    }
}

