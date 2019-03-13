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
package com.google.devtools.build.lib.rules.android;


import AndroidResourcesInfo.PROVIDER;
import ImportDepsCheckingLevel.OFF;
import ImportDepsCheckingLevel.WARNING;
import OutputGroupInfo.HIDDEN_TOP_LEVEL;
import OutputGroupInfo.SKYLARK_CONSTRUCTOR;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.truth.Truth;
import com.google.devtools.build.lib.actions.Action;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.util.ActionsTestUtil;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.analysis.FilesToRunProvider;
import com.google.devtools.build.lib.analysis.OutputGroupInfo;
import com.google.devtools.build.lib.analysis.actions.SpawnAction;
import com.google.devtools.build.lib.analysis.configuredtargets.FileConfiguredTarget;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.collect.nestedset.NestedSet;
import com.google.devtools.build.lib.rules.java.JavaCompilationArgsProvider;
import com.google.devtools.build.lib.rules.java.JavaInfo;
import com.google.devtools.build.lib.rules.java.JavaRuleOutputJarsProvider;
import com.google.devtools.build.lib.rules.java.JavaRuleOutputJarsProvider.OutputJar;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link com.google.devtools.build.lib.rules.android.AarImport}.
 */
@RunWith(JUnit4.class)
public class AarImportTest extends BuildViewTestCase {
    @Test
    public void aapt2RTxtProvided() throws Exception {
        useConfiguration("--android_sdk=//aapt2/sdk:sdk", "--experimental_skip_parsing_action", "--android_aapt=aapt2");
        ConfiguredTarget libTarget = getConfiguredTarget("//a:library");
        NestedSet<Artifact> transitiveCompiledSymbols = libTarget.get(PROVIDER).getTransitiveCompiledSymbols();
        assertThat(transitiveCompiledSymbols).hasSize(2);
        assertThat(transitiveCompiledSymbols.toSet().stream().map(Artifact::getRootRelativePathString).collect(Collectors.toSet())).containsExactly("a/foo_symbols/symbols.zip", "a/library_symbols/symbols.zip");
        NestedSet<ValidatedAndroidResources> directResources = libTarget.get(PROVIDER).getDirectAndroidResources();
        assertThat(directResources).hasSize(1);
        ValidatedAndroidResources resourceContainer = directResources.iterator().next();
        Truth.assertThat(resourceContainer.getAapt2RTxt()).isNotNull();
    }

    @Test
    public void testResourcesProvided() throws Exception {
        ConfiguredTarget aarImportTarget = getConfiguredTarget("//a:foo");
        NestedSet<ValidatedAndroidResources> directResources = aarImportTarget.get(PROVIDER).getDirectAndroidResources();
        assertThat(directResources).hasSize(1);
        ValidatedAndroidResources resourceContainer = directResources.iterator().next();
        assertThat(resourceContainer.getManifest()).isNotNull();
        Artifact resourceTreeArtifact = Iterables.getOnlyElement(resourceContainer.getResources());
        assertThat(resourceTreeArtifact.isTreeArtifact()).isTrue();
        assertThat(resourceTreeArtifact.getExecPathString()).endsWith("_aar/unzipped/resources/foo");
        NestedSet<ParsedAndroidAssets> directAssets = aarImportTarget.get(AndroidAssetsInfo.PROVIDER).getDirectParsedAssets();
        assertThat(directAssets).hasSize(1);
        ParsedAndroidAssets assets = directAssets.iterator().next();
        assertThat(assets.getSymbols()).isNotNull();
        Artifact assetsTreeArtifact = Iterables.getOnlyElement(assets.getAssets());
        assertThat(assetsTreeArtifact.isTreeArtifact()).isTrue();
        assertThat(assetsTreeArtifact.getExecPathString()).endsWith("_aar/unzipped/assets/foo");
    }

    @Test
    public void testResourcesExtractor() throws Exception {
        ValidatedAndroidResources resourceContainer = getConfiguredTarget("//a:foo").get(PROVIDER).getDirectAndroidResources().toList().get(0);
        Artifact resourceTreeArtifact = resourceContainer.getResources().get(0);
        Artifact aarResourcesExtractor = getHostConfiguredTarget(((ruleClassProvider.getToolsRepository()) + "//tools/android:aar_resources_extractor")).getProvider(FilesToRunProvider.class).getExecutable();
        ParsedAndroidAssets assets = getConfiguredTarget("//a:foo").get(AndroidAssetsInfo.PROVIDER).getDirectParsedAssets().toList().get(0);
        Artifact assetsTreeArtifact = assets.getAssets().get(0);
        assertThat(getGeneratingSpawnAction(resourceTreeArtifact).getArguments()).containsExactly(aarResourcesExtractor.getExecPathString(), "--input_aar", "a/foo.aar", "--output_res_dir", resourceTreeArtifact.getExecPathString(), "--output_assets_dir", assetsTreeArtifact.getExecPathString());
    }

    @Test
    public void testDepsCheckerActionExistsForLevelError() throws Exception {
        useConfiguration("--experimental_import_deps_checking=ERROR");
        ConfiguredTarget aarImportTarget = getConfiguredTarget("//a:last");
        OutputGroupInfo outputGroupInfo = aarImportTarget.get(SKYLARK_CONSTRUCTOR);
        NestedSet<Artifact> outputGroup = outputGroupInfo.getOutputGroup(HIDDEN_TOP_LEVEL);
        assertThat(outputGroup).hasSize(2);
        // We should force asset merging to happen
        Artifact mergedAssetsZip = aarImportTarget.get(AndroidAssetsInfo.PROVIDER).getValidationResult();
        assertThat(outputGroup).contains(mergedAssetsZip);
        // Get the other artifact from the output group
        Artifact artifact = ActionsTestUtil.getFirstArtifactEndingWith(outputGroup, "jdeps.proto");
        assertThat(artifact.isTreeArtifact()).isFalse();
        assertThat(artifact.getExecPathString()).endsWith("_aar/last/jdeps.proto");
        SpawnAction checkerAction = getGeneratingSpawnAction(artifact);
        List<String> arguments = checkerAction.getArguments();
        assertThat(arguments).containsAllOf("--bootclasspath_entry", "--classpath_entry", "--directdep", "--input", "--checking_mode=error", "--rule_label", "//a:last", "--jdeps_output");
        AarImportTest.ensureArgumentsHaveClassEntryOptionWithSuffix(arguments, "/intermediate/classes_and_libs_merged.jar");
        assertThat(arguments.stream().filter(( arg) -> "--classpath_entry".equals(arg)).count()).isEqualTo(5);// transitive classpath

        assertThat(arguments.stream().filter(( arg) -> "--directdep".equals(arg)).count()).isEqualTo(1);// 1 declared dep

    }

    @Test
    public void testDepsCheckerActionExistsForLevelWarning() throws Exception {
        checkDepsCheckerActionExistsForLevel(WARNING, "warning");
    }

    @Test
    public void testDepsCheckerActionExistsForLevelOff() throws Exception {
        checkDepsCheckerActionExistsForLevel(OFF, "silence");
    }

    @Test
    public void testNativeLibsProvided() throws Exception {
        ConfiguredTarget androidLibraryTarget = getConfiguredTarget("//java:lib");
        NestedSet<Artifact> nativeLibs = androidLibraryTarget.get(AndroidNativeLibsInfo.PROVIDER).getNativeLibs();
        assertThat(nativeLibs).containsExactly(ActionsTestUtil.getFirstArtifactEndingWith(nativeLibs, "foo/native_libs.zip"), ActionsTestUtil.getFirstArtifactEndingWith(nativeLibs, "bar/native_libs.zip"), ActionsTestUtil.getFirstArtifactEndingWith(nativeLibs, "baz/native_libs.zip"));
    }

    @Test
    public void testNativeLibsMakesItIntoApk() throws Exception {
        scratch.file("java/com/google/android/hello/BUILD", "aar_import(", "    name = 'my_aar',", "    aar = 'my_aar.aar',", ")", "android_binary(", "    name = 'my_app',", "    srcs = ['HelloApp.java'],", "    deps = [':my_aar'],", "    manifest = 'AndroidManifest.xml',", ")");
        ConfiguredTarget binary = getConfiguredTarget("//java/com/google/android/hello:my_app");
        SpawnAction apkBuilderAction = ((SpawnAction) (actionsTestUtil().getActionForArtifactEndingWith(getFilesToBuild(binary), "my_app_unsigned.apk")));
        assertThat(Iterables.find(apkBuilderAction.getArguments(), Predicates.containsPattern("_aar/my_aar/native_libs.zip$"))).isNotEmpty();
    }

    @Test
    public void testClassesJarProvided() throws Exception {
        ConfiguredTarget aarImportTarget = getConfiguredTarget("//a:foo");
        Iterable<OutputJar> outputJars = JavaInfo.getProvider(JavaRuleOutputJarsProvider.class, aarImportTarget).getOutputJars();
        assertThat(outputJars).hasSize(1);
        Artifact classesJar = outputJars.iterator().next().getClassJar();
        assertThat(classesJar.getFilename()).isEqualTo("classes_and_libs_merged.jar");
        SpawnAction jarMergingAction = ((SpawnAction) (getGeneratingAction(classesJar)));
        assertThat(jarMergingAction.getArguments()).contains("--dont_change_compression");
    }

    @Test
    public void testNoCustomJavaPackage() throws Exception {
        ValidatedAndroidResources resourceContainer = getConfiguredTarget("//a:foo").get(PROVIDER).getDirectAndroidResources().iterator().next();
        // aar_import should not set a custom java package. Instead aapt will read the
        // java package from the manifest.
        assertThat(resourceContainer.getJavaPackage()).isNull();
    }

    @Test
    public void testDepsPropagatesMergedAarJars() throws Exception {
        Action appCompileAction = getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(actionsTestUtil().artifactClosureOf(getFileConfiguredTarget("//java:app.apk").getArtifact()), "libapp.jar"));
        assertThat(appCompileAction).isNotNull();
        assertThat(ActionsTestUtil.prettyArtifactNames(appCompileAction.getInputs())).containsAllOf("a/_aar/foo/classes_and_libs_merged.jar", "a/_aar/bar/classes_and_libs_merged.jar", "a/_aar/baz/classes_and_libs_merged.jar");
    }

    @Test
    public void testExportsPropagatesMergedAarJars() throws Exception {
        FileConfiguredTarget appTarget = getFileConfiguredTarget("//java:app.apk");
        Set<Artifact> artifacts = actionsTestUtil().artifactClosureOf(appTarget.getArtifact());
        ConfiguredTarget bar = getConfiguredTarget("//a:bar");
        Artifact barClassesJar = ActionsTestUtil.getFirstArtifactEndingWith(artifacts, "bar/classes_and_libs_merged.jar");
        // Verify that bar/classes_and_libs_merged.jar was in the artifact closure.
        assertThat(barClassesJar).isNotNull();
        assertThat(barClassesJar.getArtifactOwner().getLabel()).isEqualTo(bar.getLabel());
        ConfiguredTarget foo = getConfiguredTarget("//a:foo");
        Artifact fooClassesJar = ActionsTestUtil.getFirstArtifactEndingWith(artifacts, "foo/classes_and_libs_merged.jar");
        // Verify that foo/classes_and_libs_merged.jar was in the artifact closure.
        assertThat(fooClassesJar).isNotNull();
        assertThat(fooClassesJar.getArtifactOwner().getLabel()).isEqualTo(foo.getLabel());
        ConfiguredTarget baz = getConfiguredTarget("//java:baz.jar");
        Artifact bazJar = ActionsTestUtil.getFirstArtifactEndingWith(artifacts, "baz.jar");
        // Verify that baz.jar was in the artifact closure
        assertThat(bazJar).isNotNull();
        assertThat(bazJar.getArtifactOwner().getLabel()).isEqualTo(baz.getLabel());
    }

    @Test
    public void testExportsPropagatesResources() throws Exception {
        FileConfiguredTarget appTarget = getFileConfiguredTarget("//java:app.apk");
        Set<Artifact> artifacts = actionsTestUtil().artifactClosureOf(appTarget.getArtifact());
        ConfiguredTarget bar = getConfiguredTarget("//a:bar");
        Artifact barResources = ActionsTestUtil.getFirstArtifactEndingWith(artifacts, "_aar/unzipped/resources/bar");
        assertThat(barResources).isNotNull();
        assertThat(barResources.getArtifactOwner().getLabel()).isEqualTo(bar.getLabel());
        ConfiguredTarget foo = getConfiguredTarget("//a:foo");
        Artifact fooResources = ActionsTestUtil.getFirstArtifactEndingWith(artifacts, "_aar/unzipped/resources/foo");
        assertThat(fooResources).isNotNull();
        assertThat(fooResources.getArtifactOwner().getLabel()).isEqualTo(foo.getLabel());
    }

    @Test
    public void testJavaCompilationArgsProvider() throws Exception {
        ConfiguredTarget aarImportTarget = getConfiguredTarget("//a:bar");
        JavaCompilationArgsProvider provider = JavaInfo.getProvider(JavaCompilationArgsProvider.class, aarImportTarget);
        assertThat(provider).isNotNull();
        assertThat(artifactsToStrings(provider.getRuntimeJars())).containsExactly("bin a/_aar/bar/classes_and_libs_merged.jar", "bin a/_aar/foo/classes_and_libs_merged.jar", "bin a/_aar/baz/classes_and_libs_merged.jar", "src java/baz.jar");
        List<Artifact> compileTimeJavaDependencyArtifacts = provider.getCompileTimeJavaDependencyArtifacts().toList();
        assertThat(compileTimeJavaDependencyArtifacts).hasSize(2);
        assertThat(compileTimeJavaDependencyArtifacts.stream().filter(( artifact) -> artifact.getExecPathString().endsWith("/_aar/foo/jdeps.proto")).collect(Collectors.toList())).hasSize(1);
        assertThat(compileTimeJavaDependencyArtifacts.stream().filter(( artifact) -> artifact.getExecPathString().endsWith("/_aar/bar/jdeps.proto")).collect(Collectors.toList())).hasSize(1);
    }

    @Test
    public void testFailsWithoutAndroidSdk() throws Exception {
        scratch.file("sdk/BUILD", "alias(", "    name = 'sdk',", "    actual = 'doesnotexist',", ")");
        useConfiguration("--android_sdk=//sdk");
        checkError("aar", "aar", "No Android SDK found. Use the --android_sdk command line option to specify one.", "aar_import(", "    name = 'aar',", "    aar = 'a.aar',", ")");
    }

    @Test
    public void testExportsManifest() throws Exception {
        Artifact binaryMergedManifest = getConfiguredTarget("//java:app").get(ApkInfo.PROVIDER).getMergedManifest();
        // Compare root relative path strings instead of artifacts due to difference in configuration
        // caused by the Android split transition.
        assertThat(Iterables.transform(getGeneratingAction(binaryMergedManifest).getInputs(), Artifact::getRootRelativePathString)).containsAllOf(getAndroidManifest("//a:foo"), getAndroidManifest("//a:bar"));
    }

    @Test
    public void testTransitiveExports() throws Exception {
        assertThat(getConfiguredTarget("//a:bar").get(JavaInfo.PROVIDER).getTransitiveExports()).containsExactly(Label.parseAbsolute("//a:foo", ImmutableMap.of()), Label.parseAbsolute("//java:baz", ImmutableMap.of()));
    }
}

