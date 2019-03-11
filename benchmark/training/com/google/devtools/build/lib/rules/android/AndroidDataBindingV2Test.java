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


import DataBindingV2Provider.NAME;
import DataBindingV2Provider.PROVIDER;
import JavaCompileInfo.javaCompileInfo;
import RepositoryName.MAIN;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.truth.Truth;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.extra.JavaCompileInfo;
import com.google.devtools.build.lib.actions.util.ActionsTestUtil;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.rules.android.databinding.DataBinding;
import com.google.devtools.build.lib.rules.android.databinding.DataBindingV2Provider;
import com.google.devtools.build.lib.rules.java.JavaCompileAction;
import com.google.devtools.build.lib.rules.java.JavaCompileActionTestHelper;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for Bazel's Android data binding v2 support.
 */
@RunWith(JUnit4.class)
public class AndroidDataBindingV2Test extends AndroidBuildViewTestCase {
    @Test
    public void basicDataBindingIntegration() throws Exception {
        writeDataBindingFiles();
        ConfiguredTarget ctapp = getConfiguredTarget("//java/android/binary:app");
        Set<Artifact> allArtifacts = actionsTestUtil().artifactClosureOf(getFilesToBuild(ctapp));
        // "Data binding"-enabled targets invoke resource processing with a request for data binding
        // output:
        Artifact libResourceInfoOutput = ActionsTestUtil.getFirstArtifactEndingWith(allArtifacts, "databinding/lib_with_databinding/layout-info.zip");
        assertThat(getGeneratingSpawnActionArgs(libResourceInfoOutput)).containsAllOf("--dataBindingInfoOut", libResourceInfoOutput.getExecPathString()).inOrder();
        Artifact binResourceInfoOutput = ActionsTestUtil.getFirstArtifactEndingWith(allArtifacts, "databinding/app/layout-info.zip");
        assertThat(getGeneratingSpawnActionArgs(binResourceInfoOutput)).containsAllOf("--dataBindingInfoOut", binResourceInfoOutput.getExecPathString()).inOrder();
        // Java compilation includes the data binding annotation processor, the resource processor's
        // output, and the auto-generated DataBindingInfo.java the annotation processor uses to figure
        // out what to do:
        JavaCompileAction libCompileAction = ((JavaCompileAction) (getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(allArtifacts, "lib_with_databinding.jar"))));
        assertThat(getProcessorNames(libCompileAction)).contains("android.databinding.annotationprocessor.ProcessDataBinding");
        assertThat(ActionsTestUtil.prettyArtifactNames(libCompileAction.getInputs())).containsAllOf("java/android/library/databinding/lib_with_databinding/layout-info.zip", "java/android/library/databinding/lib_with_databinding/DataBindingInfo.java");
        JavaCompileAction binCompileAction = ((JavaCompileAction) (getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(allArtifacts, "app.jar"))));
        assertThat(getProcessorNames(binCompileAction)).contains("android.databinding.annotationprocessor.ProcessDataBinding");
        assertThat(ActionsTestUtil.prettyArtifactNames(binCompileAction.getInputs())).containsAllOf("java/android/binary/databinding/app/layout-info.zip", "java/android/binary/databinding/app/DataBindingInfo.java");
    }

    @Test
    public void dataBindingCompilationUsesMetadataFromDeps() throws Exception {
        writeDataBindingFiles();
        ConfiguredTarget ctapp = getConfiguredTarget("//java/android/binary:app");
        Set<Artifact> allArtifacts = actionsTestUtil().artifactClosureOf(getFilesToBuild(ctapp));
        // The library's compilation doesn't include any of the -setter_store.bin, layoutinfo.bin, etc.
        // files that store a dependency's data binding results (since the library has no deps).
        // We check that they don't appear as compilation inputs.
        JavaCompileAction libCompileAction = ((JavaCompileAction) (getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(allArtifacts, "lib2_with_databinding.jar"))));
        assertThat(Iterables.filter(libCompileAction.getInputs(), ActionsTestUtil.getArtifactSuffixMatcher(".bin"))).isEmpty();
        // The binary's compilation includes the library's data binding results.
        JavaCompileAction binCompileAction = ((JavaCompileAction) (getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(allArtifacts, "app.jar"))));
        Iterable<Artifact> depMetadataInputs = Iterables.filter(binCompileAction.getInputs(), ActionsTestUtil.getArtifactSuffixMatcher(".bin"));
        final String appDependentLibArtifacts = (Iterables.getFirst(depMetadataInputs, null).getRoot().getExecPathString()) + "/java/android/binary/databinding/app/dependent-lib-artifacts/";
        ActionsTestUtil.execPaths(Iterables.filter(binCompileAction.getInputs(), ActionsTestUtil.getArtifactSuffixMatcher(".bin")));
        assertThat(ActionsTestUtil.execPaths(depMetadataInputs)).containsExactly(((appDependentLibArtifacts + "java/android/library/databinding/") + "lib_with_databinding/bin-files/android.library-android.library-br.bin"), ((appDependentLibArtifacts + "java/android/library/databinding/") + "lib_with_databinding/bin-files/android.library-android.library-setter_store.bin"), ((appDependentLibArtifacts + "java/android/library2/databinding/") + "lib2_with_databinding/bin-files/android.library2-android.library2-br.bin"));
    }

    @Test
    public void dataBindingAnnotationProcessorFlags() throws Exception {
        writeDataBindingFiles();
        ConfiguredTarget ctapp = getConfiguredTarget("//java/android/binary:app");
        Set<Artifact> allArtifacts = actionsTestUtil().artifactClosureOf(getFilesToBuild(ctapp));
        JavaCompileAction binCompileAction = ((JavaCompileAction) (getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(allArtifacts, "app.jar"))));
        String dataBindingFilesDir = targetConfig.getBinDirectory(MAIN).getExecPath().getRelative("java/android/binary/databinding/app").getPathString();
        ImmutableList<String> expectedJavacopts = // Note that this includes only android.library and not android.library2
        ImmutableList.of(("-Aandroid.databinding.bindingBuildFolder=" + dataBindingFilesDir), ("-Aandroid.databinding.generationalFileOutDir=" + dataBindingFilesDir), "-Aandroid.databinding.sdkDir=/not/used", "-Aandroid.databinding.artifactType=APPLICATION", "-Aandroid.databinding.exportClassListTo=/tmp/exported_classes", "-Aandroid.databinding.modulePackage=android.binary", "-Aandroid.databinding.minApi=14", "-Aandroid.databinding.enableV2=1", "-Aandroid.databinding.directDependencyPkgs=[android.library]");
        assertThat(JavaCompileActionTestHelper.getJavacArguments(binCompileAction)).containsAllIn(expectedJavacopts);
        // Regression test for b/63134122
        JavaCompileInfo javaCompileInfo = binCompileAction.getExtraActionInfo(actionKeyContext).getExtension(javaCompileInfo);
        assertThat(javaCompileInfo.getJavacOptList()).containsAllIn(expectedJavacopts);
    }

    @Test
    public void dataBindingAnnotationProcessorFlags_v3_4() throws Exception {
        useConfiguration("--experimental_android_databinding_v2", "--android_databinding_use_v3_4_args");
        writeDataBindingFiles();
        ConfiguredTarget ctapp = getConfiguredTarget("//java/android/binary:app");
        Set<Artifact> allArtifacts = actionsTestUtil().artifactClosureOf(getFilesToBuild(ctapp));
        JavaCompileAction binCompileAction = ((JavaCompileAction) (getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(allArtifacts, "app.jar"))));
        String dataBindingFilesDir = targetConfig.getBinDirectory(MAIN).getExecPath().getRelative("java/android/binary/databinding/app").getPathString();
        String inputDir = (dataBindingFilesDir + "/") + (DataBinding.DEP_METADATA_INPUT_DIR);
        String outputDir = (dataBindingFilesDir + "/") + (DataBinding.METADATA_OUTPUT_DIR);
        ImmutableList<String> expectedJavacopts = // Note that this includes only android.library and not android.library2
        ImmutableList.of(("-Aandroid.databinding.dependencyArtifactsDir=" + inputDir), ("-Aandroid.databinding.aarOutDir=" + outputDir), "-Aandroid.databinding.sdkDir=/not/used", "-Aandroid.databinding.artifactType=APPLICATION", "-Aandroid.databinding.exportClassListOutFile=/tmp/exported_classes", "-Aandroid.databinding.modulePackage=android.binary", "-Aandroid.databinding.minApi=14", "-Aandroid.databinding.enableV2=1", "-Aandroid.databinding.directDependencyPkgs=[android.library]");
        assertThat(JavaCompileActionTestHelper.getJavacArguments(binCompileAction)).containsAllIn(expectedJavacopts);
        JavaCompileInfo javaCompileInfo = binCompileAction.getExtraActionInfo(actionKeyContext).getExtension(javaCompileInfo);
        assertThat(javaCompileInfo.getJavacOptList()).containsAllIn(expectedJavacopts);
    }

    @Test
    public void dataBindingIncludesTransitiveDepsForLibsWithNoResources() throws Exception {
        writeDataBindingFilesWithNoResourcesDep();
        ConfiguredTarget ct = getConfiguredTarget("//java/android/binary:app");
        Set<Artifact> allArtifacts = actionsTestUtil().artifactClosureOf(getFilesToBuild(ct));
        // Data binding resource processing outputs are expected for the app and libs with resources.
        assertThat(ActionsTestUtil.getFirstArtifactEndingWith(allArtifacts, "databinding/lib_with_resource_files/layout-info.zip")).isNotNull();
        assertThat(ActionsTestUtil.getFirstArtifactEndingWith(allArtifacts, "databinding/app/layout-info.zip")).isNotNull();
        // Compiling the app's Java source includes data binding metadata from the resource-equipped
        // lib, but not the resource-empty one.
        JavaCompileAction binCompileAction = ((JavaCompileAction) (getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(allArtifacts, "app.jar"))));
        List<String> appJarInputs = ActionsTestUtil.prettyArtifactNames(binCompileAction.getInputs());
        String libWithResourcesMetadataBaseDir = "java/android/binary/databinding/app/" + ("dependent-lib-artifacts/java/android/lib_with_resource_files/databinding/" + "lib_with_resource_files/bin-files/android.lib_with_resource_files-");
        assertThat(appJarInputs).containsAllOf("java/android/binary/databinding/app/layout-info.zip", (libWithResourcesMetadataBaseDir + "android.lib_with_resource_files-br.bin"));
        for (String compileInput : appJarInputs) {
            assertThat(compileInput).doesNotMatch(".*lib_no_resource_files.*.bin");
        }
    }

    @Test
    public void libsWithNoResourcesOnlyRunAnnotationProcessor() throws Exception {
        // Bazel skips resource processing because there are no new resources to process. But it still
        // runs the annotation processor to ensure the Java compiler reads Java sources referenced by
        // the deps' resources (e.g. "<variable type="some.package.SomeClass" />"). Without this,
        // JavaBuilder's --reduce_classpath feature would strip out those sources as "unused" and fail
        // the binary's compilation with unresolved symbol errors.
        writeDataBindingFilesWithNoResourcesDep();
        ConfiguredTarget ct = getConfiguredTarget("//java/android/lib_no_resource_files");
        Iterable<Artifact> libArtifacts = getFilesToBuild(ct);
        assertThat(ActionsTestUtil.getFirstArtifactEndingWith(libArtifacts, "_resources.jar")).isNull();
        assertThat(ActionsTestUtil.getFirstArtifactEndingWith(libArtifacts, "layout-info.zip")).isNull();
        JavaCompileAction libCompileAction = ((JavaCompileAction) (getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(libArtifacts, "lib_no_resource_files.jar"))));
        // The annotation processor is attached to the Java compilation:
        assertThat(JavaCompileActionTestHelper.getJavacArguments(libCompileAction)).containsAllOf("--processors", "android.databinding.annotationprocessor.ProcessDataBinding");
        // The dummy .java file with annotations that trigger the annotation process is present:
        assertThat(ActionsTestUtil.prettyArtifactNames(libCompileAction.getInputs())).contains(("java/android/lib_no_resource_files/databinding/lib_no_resource_files/" + "DataBindingInfo.java"));
    }

    @Test
    public void missingDataBindingAttributeStillAnalyzes() throws Exception {
        // When a library is missing enable_data_binding = 1, we expect it to fail in execution (because
        // aapt doesn't know how to read the data binding expressions). But analysis should work.
        scratch.file("java/android/library/BUILD", "android_library(", "    name = 'lib_with_databinding',", "    enable_data_binding = 1,", "    manifest = 'AndroidManifest.xml',", "    srcs = ['MyLib.java'],", "    resource_files = [],", ")");
        scratch.file("java/android/library/MyLib.java", "package android.library; public class MyLib {};");
        scratch.file("java/android/binary/BUILD", "android_binary(", "    name = 'app',", "    enable_data_binding = 0,", "    manifest = 'AndroidManifest.xml',", "    srcs = ['MyApp.java'],", "    deps = ['//java/android/library:lib_with_databinding'],", ")");
        scratch.file("java/android/binary/MyApp.java", "package android.binary; public class MyApp {};");
        assertThat(getConfiguredTarget("//java/android/binary:app")).isNotNull();
    }

    @Test
    public void dataBindingProviderIsProvided() throws Exception {
        useConfiguration("--android_sdk=//sdk:sdk", "--experimental_android_databinding_v2");
        scratch.file("sdk/BUILD", "android_sdk(", "    name = 'sdk',", "    aapt = 'aapt',", "    aapt2 = 'aapt2',", "    adb = 'adb',", "    aidl = 'aidl',", "    android_jar = 'android.jar',", "    apksigner = 'apksigner',", "    dx = 'dx',", "    framework_aidl = 'framework_aidl',", "    main_dex_classes = 'main_dex_classes',", "    main_dex_list_creator = 'main_dex_list_creator',", "    proguard = 'proguard',", "    shrinked_android_jar = 'shrinked_android_jar',", "    zipalign = 'zipalign',", "    tags = ['__ANDROID_RULES_MIGRATION__'],", ")");
        scratch.file("java/a/BUILD", "android_library(", "    name = 'a', ", "    srcs = ['A.java'],", "    enable_data_binding = 1,", "    manifest = 'a/AndroidManifest.xml',", "    resource_files = ['res/values/a.xml'],", ")");
        scratch.file("java/b/BUILD", "android_library(", "    name = 'b', ", "    srcs = ['B.java'],", "    enable_data_binding = 1,", "    manifest = 'AndroidManifest.xml',", "    resource_files = ['res/values/a.xml'],", ")");
        ConfiguredTarget a = getConfiguredTarget("//java/a:a");
        final DataBindingV2Provider dataBindingV2Provider = a.get(PROVIDER);
        assertThat(dataBindingV2Provider).named(NAME).isNotNull();
        assertThat(dataBindingV2Provider.getSetterStores().stream().map(Artifact::getRootRelativePathString).collect(Collectors.toList())).containsExactly("java/a/databinding/a/bin-files/a-a-setter_store.bin");
        assertThat(dataBindingV2Provider.getClassInfos().stream().map(Artifact::getRootRelativePathString).collect(Collectors.toList())).containsExactly("java/a/databinding/a/class-info.zip");
        assertThat(dataBindingV2Provider.getTransitiveBRFiles().toCollection().stream().map(Artifact::getRootRelativePathString).collect(Collectors.toList())).containsExactly("java/a/databinding/a/bin-files/a-a-br.bin");
    }

    @Test
    public void ensureDataBindingProviderIsPropagatedThroughNonDataBindingLibs() throws Exception {
        useConfiguration("--android_sdk=//sdk:sdk", "--experimental_android_databinding_v2");
        scratch.file("sdk/BUILD", "android_sdk(", "    name = 'sdk',", "    aapt = 'aapt',", "    aapt2 = 'aapt2',", "    adb = 'adb',", "    aidl = 'aidl',", "    android_jar = 'android.jar',", "    apksigner = 'apksigner',", "    dx = 'dx',", "    framework_aidl = 'framework_aidl',", "    main_dex_classes = 'main_dex_classes',", "    main_dex_list_creator = 'main_dex_list_creator',", "    proguard = 'proguard',", "    shrinked_android_jar = 'shrinked_android_jar',", "    zipalign = 'zipalign',", "    tags = ['__ANDROID_RULES_MIGRATION__'],", ")");
        scratch.file("java/a/BUILD", "android_library(", "    name = 'a', ", "    srcs = ['A.java'],", "    enable_data_binding = 1,", "    manifest = 'AndroidManifest.xml',", "    resource_files = ['res/values/a.xml'],", ")");
        scratch.file("java/b/BUILD", "android_library(", "    name = 'b', ", "    srcs = ['B.java'],", "    deps = ['//java/a:a'],", ")");
        ConfiguredTarget b = getConfiguredTarget("//java/b:b");
        Truth.assertThat(b.get(PROVIDER)).named("DataBindingV2Info").isNotNull();
    }

    @Test
    public void testDataBindingCollectedThroughExports() throws Exception {
        useConfiguration("--android_sdk=//sdk:sdk", "--experimental_android_databinding_v2");
        scratch.file("sdk/BUILD", "android_sdk(", "    name = 'sdk',", "    aapt = 'aapt',", "    aapt2 = 'aapt2',", "    adb = 'adb',", "    aidl = 'aidl',", "    android_jar = 'android.jar',", "    apksigner = 'apksigner',", "    dx = 'dx',", "    framework_aidl = 'framework_aidl',", "    main_dex_classes = 'main_dex_classes',", "    main_dex_list_creator = 'main_dex_list_creator',", "    proguard = 'proguard',", "    shrinked_android_jar = 'shrinked_android_jar',", "    zipalign = 'zipalign',", "    tags = ['__ANDROID_RULES_MIGRATION__'],", ")");
        scratch.file("java/a/BUILD", "android_library(", "    name = 'a', ", "    srcs = ['A.java'],", "    enable_data_binding = 1,", "    manifest = 'AndroidManifest.xml',", "    resource_files = ['res/values/a.xml'],", ")");
        scratch.file("java/b/BUILD", "android_library(", "    name = 'b', ", "    srcs = ['B.java'],", "    enable_data_binding = 1,", "    manifest = 'AndroidManifest.xml',", "    resource_files = ['res/values/a.xml'],", ")");
        scratch.file("java/c/BUILD", "android_library(", "    name = 'c', ", "    exports = ['//java/a:a', '//java/b:b']", ")");
        ConfiguredTarget c = getConfiguredTarget("//java/c:c");
        DataBindingV2Provider provider = c.get(PROVIDER);
        assertThat(provider.getClassInfos().stream().map(Artifact::getRootRelativePathString).collect(Collectors.toList())).containsExactly("java/a/databinding/a/class-info.zip", "java/b/databinding/b/class-info.zip");
        assertThat(provider.getSetterStores().stream().map(Artifact::getRootRelativePathString).collect(Collectors.toList())).containsExactly("java/a/databinding/a/bin-files/a-a-setter_store.bin", "java/b/databinding/b/bin-files/b-b-setter_store.bin");
        assertThat(provider.getTransitiveBRFiles().toCollection().stream().map(Artifact::getRootRelativePathString).collect(Collectors.toList())).containsExactly("java/a/databinding/a/bin-files/a-a-br.bin", "java/b/databinding/b/bin-files/b-b-br.bin");
    }

    @Test
    public void testMultipleAndroidLibraryDepsWithSameJavaPackageRaisesError() throws Exception {
        String databindingRuntime = "//third_party/java/android/android_sdk_linux/extras/android/compatibility/databinding" + ":runtime";
        String supportAnnotations = "//third_party/java/android/android_sdk_linux/extras/android/compatibility/annotations" + ":annotations";
        scratch.file("java/com/lib/BUILD", "android_library(", "    name = 'lib',", "    srcs = ['User.java'],", "    manifest = 'AndroidManifest.xml',", "    resource_files = glob(['res/**']),", "    enable_data_binding = 1,", "    deps = [", (("        '" + databindingRuntime) + "',"), (("        '" + supportAnnotations) + "',"), "    ],", ")", "android_library(", "    name = 'lib2',", "    srcs = ['User2.java'],", "    manifest = 'AndroidManifest.xml',", "    resource_files = glob(['res2/**']),", "    enable_data_binding = 1,", "    deps = [", (("        '" + databindingRuntime) + "',"), (("        '" + supportAnnotations) + "',"), "    ],", ")");
        scratch.file("java/com/bin/BUILD", "android_binary(", "    name = 'bin',", "    srcs = ['MyActivity.java'],", "    manifest = 'AndroidManifest.xml',", "    enable_data_binding = 1,", "    deps = [", "        '//java/com/lib',", "        '//java/com/lib:lib2',", (("        '" + databindingRuntime) + "',"), (("        '" + supportAnnotations) + "',"), "    ],", ")");
        checkError("//java/com/bin:bin", ("Java package com.lib:\n" + ("    //java/com/lib:lib\n" + "    //java/com/lib:lib2")));
    }

    @Test
    public void testMultipleAndroidLibraryDepsWithSameJavaPackageThroughDiamondRaisesError() throws Exception {
        String databindingRuntime = "//third_party/java/android/android_sdk_linux/extras/android/compatibility/databinding" + ":runtime";
        String supportAnnotations = "//third_party/java/android/android_sdk_linux/extras/android/compatibility/annotations" + ":annotations";
        // The bin target depends on these target indirectly and separately through the libraries
        // in middleA and middleB.
        scratch.file("java/com/bottom/BUILD", "android_library(", "    name = 'lib',", "    srcs = ['User.java'],", "    manifest = 'AndroidManifest.xml',", "    resource_files = glob(['res/**']),", "    enable_data_binding = 1,", "    deps = [", (("        '" + databindingRuntime) + "',"), (("        '" + supportAnnotations) + "',"), "    ],", ")", "android_library(", "    name = 'lib2',", "    srcs = ['User2.java'],", "    manifest = 'AndroidManifest.xml',", "    resource_files = glob(['res2/**']),", "    enable_data_binding = 1,", "    deps = [", (("        '" + databindingRuntime) + "',"), (("        '" + supportAnnotations) + "',"), "    ],", ")");
        scratch.file("java/com/middleA/BUILD", "android_library(", "    name = 'lib',", "    srcs = ['UserMiddleA.java'],", "    manifest = 'AndroidManifest.xml',", "    deps = [", "        '//java/com/bottom:lib',", "    ],", ")");
        scratch.file("java/com/middleB/BUILD", "android_library(", "    name = 'lib',", "    srcs = ['UserMiddleB.java'],", "    manifest = 'AndroidManifest.xml',", "    deps = [", "        '//java/com/bottom:lib2',", "    ],", ")");
        scratch.file("java/com/bin/BUILD", "android_binary(", "    name = 'bin',", "    srcs = ['MyActivity.java'],", "    manifest = 'AndroidManifest.xml',", "    enable_data_binding = 1,", "    deps = [", "        '//java/com/middleA:lib',", "        '//java/com/middleB:lib',", (("        '" + databindingRuntime) + "',"), (("        '" + supportAnnotations) + "',"), "    ],", ")");
        checkError("//java/com/bin:bin", ("Java package com.bottom:\n" + ("    //java/com/bottom:lib\n" + "    //java/com/bottom:lib2")));
    }

    @Test
    public void testMultipleAndroidLibraryDepsWithSameJavaPackageThroughCustomPackageAttrRaisesError() throws Exception {
        String databindingRuntime = "//third_party/java/android/android_sdk_linux/extras/android/compatibility/databinding" + ":runtime";
        String supportAnnotations = "//third_party/java/android/android_sdk_linux/extras/android/compatibility/annotations" + ":annotations";
        // The bin target depends on these target indirectly and separately through the libraries
        // in middleA and middleB.
        scratch.file("libA/BUILD", "android_library(", "    name = 'libA',", "    srcs = ['UserA.java'],", "    custom_package = 'com.foo',", "    manifest = 'AndroidManifest.xml',", "    resource_files = glob(['res/**']),", "    enable_data_binding = 1,", "    deps = [", (("        '" + databindingRuntime) + "',"), (("        '" + supportAnnotations) + "',"), "    ],", ")");
        scratch.file("libB/BUILD", "android_library(", "    name = 'libB',", "    srcs = ['UserB.java'],", "    custom_package = 'com.foo',", "    manifest = 'AndroidManifest.xml',", "    resource_files = glob(['res/**']),", "    enable_data_binding = 1,", "    deps = [", (("        '" + databindingRuntime) + "',"), (("        '" + supportAnnotations) + "',"), "    ],", ")");
        scratch.file("java/com/bin/BUILD", "android_binary(", "    name = 'bin',", "    srcs = ['MyActivity.java'],", "    manifest = 'AndroidManifest.xml',", "    enable_data_binding = 1,", "    deps = [", "        '//libA:libA',", "        '//libB:libB',", (("        '" + databindingRuntime) + "',"), (("        '" + supportAnnotations) + "',"), "    ],", ")");
        checkError("//java/com/bin:bin", ("Java package com.foo:\n" + ("    //libA:libA\n" + "    //libB:libB")));
    }

    @Test
    public void testAndroidBinaryAndroidLibraryWithDatabindingSamePackageRaisesError() throws Exception {
        String databindingRuntime = "//third_party/java/android/android_sdk_linux/extras/android/compatibility/databinding" + ":runtime";
        String supportAnnotations = "//third_party/java/android/android_sdk_linux/extras/android/compatibility/annotations" + ":annotations";
        // The android_binary and android_library are in the same java package and have
        // databinding.
        scratch.file("java/com/bin/BUILD", "android_binary(", "    name = 'bin',", "    srcs = ['MyActivity.java'],", "    manifest = 'AndroidManifest.xml',", "    enable_data_binding = 1,", "    deps = [", "        ':lib',", (("        '" + databindingRuntime) + "',"), (("        '" + supportAnnotations) + "',"), "    ],", ")", "android_library(", "    name = 'lib',", "    srcs = ['User.java'],", "    manifest = 'LibManifest.xml',", "    resource_files = glob(['res/**']),", "    enable_data_binding = 1,", "    deps = [", (("        '" + databindingRuntime) + "',"), (("        '" + supportAnnotations) + "',"), "    ],", ")");
        checkError("//java/com/bin:bin", ("Java package com.bin:\n" + ("    //java/com/bin:bin\n" + "    //java/com/bin:lib")));
    }

    @Test
    public void testSameAndroidLibraryMultipleTimesThroughDiamondDoesNotRaiseSameJavaPackageError() throws Exception {
        String databindingRuntime = "//third_party/java/android/android_sdk_linux/extras/android/compatibility/databinding" + ":runtime";
        String supportAnnotations = "//third_party/java/android/android_sdk_linux/extras/android/compatibility/annotations" + ":annotations";
        // The bin target depends on this target twice: indirectly and separately through the libraries
        // in middleA and middleB, but this should not be a problem because it's the same library.
        scratch.file("java/com/bottom/BUILD", "android_library(", "    name = 'lib',", "    srcs = ['User.java'],", "    manifest = 'AndroidManifest.xml',", "    resource_files = glob(['res/**']),", "    enable_data_binding = 1,", "    deps = [", (("        '" + databindingRuntime) + "',"), (("        '" + supportAnnotations) + "',"), "    ],", ")");
        scratch.file("java/com/middleA/BUILD", "android_library(", "    name = 'lib',", "    srcs = ['UserMiddleA.java'],", "    manifest = 'AndroidManifest.xml',", "    deps = [", "        '//java/com/bottom:lib',", "    ],", ")");
        scratch.file("java/com/middleB/BUILD", "android_library(", "    name = 'lib',", "    srcs = ['UserMiddleB.java'],", "    manifest = 'AndroidManifest.xml',", "    deps = [", "        '//java/com/bottom:lib',", "    ],", ")");
        scratch.file("java/com/bin/BUILD", "android_binary(", "    name = 'bin',", "    srcs = ['MyActivity.java'],", "    manifest = 'AndroidManifest.xml',", "    enable_data_binding = 1,", "    deps = [", "        '//java/com/middleA:lib',", "        '//java/com/middleB:lib',", (("        '" + databindingRuntime) + "',"), (("        '" + supportAnnotations) + "',"), "    ],", ")");
        // Should not throw error.
        getConfiguredTarget("//java/com/bin:bin");
    }

    @Test
    public void testDependentLibraryJavaPackagesPassedFromLibraryWithExportsNoDatabinding() throws Exception {
        writeDataBindingFilesWithExports();
        ConfiguredTarget ctapp = getConfiguredTarget("//java/android/binary:app_dep_on_exports_no_databinding");
        Set<Artifact> allArtifacts = actionsTestUtil().artifactClosureOf(getFilesToBuild(ctapp));
        JavaCompileAction binCompileAction = ((JavaCompileAction) (getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(allArtifacts, "app_dep_on_exports_no_databinding.jar"))));
        ImmutableList<String> expectedJavacopts = ImmutableList.of("-Aandroid.databinding.directDependencyPkgs=[android.library1,android.library2]");
        assertThat(JavaCompileActionTestHelper.getJavacArguments(binCompileAction)).containsAllIn(expectedJavacopts);
    }

    @Test
    public void testDependentLibraryJavaPackagesPassedFromLibraryWithExportsAndDatabinding() throws Exception {
        writeDataBindingFilesWithExports();
        ConfiguredTarget ctapp = getConfiguredTarget("//java/android/binary:app_dep_on_exports_and_databinding");
        Set<Artifact> allArtifacts = actionsTestUtil().artifactClosureOf(getFilesToBuild(ctapp));
        JavaCompileAction binCompileAction = ((JavaCompileAction) (getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(allArtifacts, "app_dep_on_exports_and_databinding.jar"))));
        ImmutableList<String> expectedJavacopts = ImmutableList.of(("-Aandroid.databinding.directDependencyPkgs=" + "[android.lib_with_exports,android.library1,android.library2]"));
        assertThat(JavaCompileActionTestHelper.getJavacArguments(binCompileAction)).containsAllIn(expectedJavacopts);
    }

    @Test
    public void testNoDependentLibraryJavaPackagesIsEmptyBrackets() throws Exception {
        scratch.file("java/android/binary/BUILD", "android_binary(", "    name = 'app_databinding_no_deps',", "    enable_data_binding = 1,", "    manifest = 'AndroidManifest.xml',", "    srcs = ['MyApp.java'],", "    deps = [],", ")");
        ConfiguredTarget ctapp = getConfiguredTarget("//java/android/binary:app_databinding_no_deps");
        Set<Artifact> allArtifacts = actionsTestUtil().artifactClosureOf(getFilesToBuild(ctapp));
        JavaCompileAction binCompileAction = ((JavaCompileAction) (getGeneratingAction(ActionsTestUtil.getFirstArtifactEndingWith(allArtifacts, "app_databinding_no_deps.jar"))));
        ImmutableList<String> expectedJavacopts = ImmutableList.of("-Aandroid.databinding.directDependencyPkgs=[]");
        assertThat(JavaCompileActionTestHelper.getJavacArguments(binCompileAction)).containsAllIn(expectedJavacopts);
    }
}

