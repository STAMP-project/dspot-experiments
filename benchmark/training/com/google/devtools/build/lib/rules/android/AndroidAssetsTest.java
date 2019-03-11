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
package com.google.devtools.build.lib.rules.android;


import AndroidAaptVersion.AAPT;
import AndroidAaptVersion.AAPT2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.devtools.build.lib.analysis.RuleContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests {@link AndroidAssets}
 */
@RunWith(JUnit4.class)
public class AndroidAssetsTest extends ResourceTestBase {
    @Test
    public void testParse() throws Exception {
        RuleContext ruleContext = getRuleContext();
        AndroidAssets assets = getLocalAssets();
        ParsedAndroidAssets parsed = assets.parse(AndroidDataContext.forNative(ruleContext), AAPT);
        // Assets should be unchanged
        assertThat(parsed.getAssets()).isEqualTo(assets.getAssets());
        assertThat(parsed.getAssetRoots()).isEqualTo(assets.getAssetRoots());
        // Label should be correct
        assertThat(parsed.getLabel()).isEqualTo(ruleContext.getLabel());
        // Symbols file should be created from raw assets
        /* inputs = */
        /* outputs = */
        assertActionArtifacts(ruleContext, assets.getAssets(), ImmutableList.of(parsed.getSymbols()));
        // There should be no compiled symbols
        assertThat(parsed.getCompiledSymbols()).isNull();
    }

    @Test
    public void testParseAapt2() throws Exception {
        mockAndroidSdkWithAapt2();
        useConfiguration("--android_sdk=//sdk:sdk");
        RuleContext ruleContext = getRuleContext();
        AndroidAssets assets = getLocalAssets();
        ParsedAndroidAssets parsed = assets.parse(AndroidDataContext.forNative(ruleContext), AAPT2);
        // Assets should be unchanged
        assertThat(parsed.getAssets()).isEqualTo(assets.getAssets());
        assertThat(parsed.getAssetRoots()).isEqualTo(assets.getAssetRoots());
        // Label should be correct
        assertThat(parsed.getLabel()).isEqualTo(ruleContext.getLabel());
        // There should be compiled symbols
        assertThat(parsed.getCompiledSymbols()).isNotNull();
        // Symbols and compiled symbols files should be created from raw assets
        /* inputs = */
        /* outputs = */
        assertActionArtifacts(ruleContext, assets.getAssets(), ImmutableList.of(parsed.getSymbols()));
        /* inputs = */
        /* outputs = */
        assertActionArtifacts(ruleContext, assets.getAssets(), ImmutableList.of(parsed.getCompiledSymbols()));
    }

    @Test
    public void testMergeNoDeps() throws Exception {
        RuleContext ruleContext = getRuleContext();
        ParsedAndroidAssets parsed = getLocalAssets().parse(AndroidDataContext.forNative(ruleContext), AAPT);
        MergedAndroidAssets merged = assertMerge(ruleContext, parsed, AssetDependencies.empty());
        // The assets can be correctly built into a provider
        AndroidAssetsInfo info = merged.toProvider();
        assertThat(info.getLabel()).isEqualTo(merged.getLabel());
        // The provider just has the local values
        assertThat(info.getAssets()).containsExactlyElementsIn(merged.getAssets()).inOrder();
        assertThat(info.getSymbols()).containsExactly(merged.getSymbols());
        assertThat(info.getDirectParsedAssets()).containsExactly(parsed);
        assertThat(info.getTransitiveParsedAssets()).isEmpty();
    }

    @Test
    public void testMergeNeverlink() throws Exception {
        RuleContext ruleContext = getRuleContext();
        ParsedAndroidAssets parsed = getLocalAssets().parse(AndroidDataContext.forNative(ruleContext), AAPT);
        AssetDependencies deps = /* neverlink = */
        makeDeps(ruleContext, true, AAPT);
        MergedAndroidAssets merged = assertMerge(ruleContext, parsed, deps);
        AndroidAssetsInfo info = merged.toProvider();
        assertThat(info.getLabel()).isEqualTo(merged.getLabel());
        // The provider should be empty because of neverlinking
        assertThat(info.getAssets()).isEmpty();
        assertThat(info.getSymbols()).isEmpty();
        assertThat(info.getDirectParsedAssets()).isEmpty();
        assertThat(info.getTransitiveParsedAssets()).isEmpty();
        assertThat(info.getCompiledSymbols()).isEmpty();
    }

    @Test
    public void testMerge() throws Exception {
        RuleContext ruleContext = getRuleContext();
        ParsedAndroidAssets parsed = getLocalAssets().parse(AndroidDataContext.forNative(ruleContext), AAPT);
        AssetDependencies deps = /* neverlink = */
        makeDeps(ruleContext, false, AAPT);
        MergedAndroidAssets merged = assertMerge(ruleContext, parsed, deps);
        AndroidAssetsInfo info = merged.toProvider();
        assertThat(info.getLabel()).isEqualTo(merged.getLabel());
        // The provider should have transitive and direct deps
        assertThat(info.getAssets()).containsExactlyElementsIn(Iterables.concat(parsed.getAssets(), deps.getTransitiveAssets())).inOrder();
        assertThat(info.getSymbols()).containsExactlyElementsIn(Iterables.concat(ImmutableList.of(parsed.getSymbols()), deps.getTransitiveSymbols())).inOrder();
        assertThat(info.getDirectParsedAssets()).containsExactly(parsed).inOrder();
        assertThat(info.getTransitiveParsedAssets()).containsExactlyElementsIn(Iterables.concat(deps.getTransitiveParsedAssets(), deps.getDirectParsedAssets())).inOrder();
        // There should be no compiled symbols
        assertThat(info.getCompiledSymbols()).isEmpty();
    }

    @Test
    public void testMergeAapt2() throws Exception {
        mockAndroidSdkWithAapt2();
        useConfiguration("--android_sdk=//sdk:sdk");
        RuleContext ruleContext = getRuleContext();
        ParsedAndroidAssets parsed = getLocalAssets().parse(AndroidDataContext.forNative(ruleContext), AAPT2);
        AssetDependencies deps = /* neverlink = */
        makeDeps(ruleContext, false, AAPT2);
        MergedAndroidAssets merged = assertMerge(ruleContext, parsed, deps);
        AndroidAssetsInfo info = merged.toProvider();
        assertThat(info.getLabel()).isEqualTo(merged.getLabel());
        // The provider should have transitive and direct deps
        assertThat(info.getAssets()).containsExactlyElementsIn(Iterables.concat(parsed.getAssets(), deps.getTransitiveAssets())).inOrder();
        assertThat(info.getSymbols()).containsExactlyElementsIn(Iterables.concat(ImmutableList.of(parsed.getSymbols()), deps.getTransitiveSymbols())).inOrder();
        assertThat(info.getCompiledSymbols()).containsExactlyElementsIn(Iterables.concat(ImmutableList.of(parsed.getCompiledSymbols()), deps.getTransitiveCompiledSymbols()));
        assertThat(info.getDirectParsedAssets()).containsExactly(parsed).inOrder();
        assertThat(info.getTransitiveParsedAssets()).containsExactlyElementsIn(Iterables.concat(deps.getTransitiveParsedAssets(), deps.getDirectParsedAssets())).inOrder();
    }
}

