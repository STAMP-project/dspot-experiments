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
package com.google.devtools.build.android;


import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.PatternFilenameFilter;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ManifestMergerAction}.
 */
@RunWith(JUnit4.class)
public class ManifestMergerActionTest {
    private Path working;

    @Test
    public void testMerge_GenerateDummyManifest() throws Exception {
        Files.createDirectories(working.resolve("output"));
        Path mergedManifest = working.resolve("output/mergedManifest.xml");
        ManifestMergerAction.main(new String[]{ "--customPackage", "foo.bar.baz", "--mergeType", "LIBRARY", "--manifestOutput", mergedManifest.toString() });
        assertThat(Joiner.on(" ").join(Files.readAllLines(mergedManifest, StandardCharsets.UTF_8)).replaceAll("\\s+", " ").trim()).isEqualTo(("<?xml version=\"1.0\" encoding=\"utf-8\"?> " + (((("<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\" " + "package=\"foo.bar.baz\" > ") + "<uses-sdk android:minSdkVersion=\"1\" /> ") + "<application /> ") + "</manifest>")));
    }

    @Test
    public void testMerge() throws Exception {
        String dataDir = Paths.get(System.getenv("TEST_WORKSPACE"), System.getenv("TEST_BINARY")).resolveSibling("testing/manifestmerge").toString().replace("\\", "/");
        final Path mergerManifest = ManifestMergerActionTest.rlocation((dataDir + "/merger/AndroidManifest.xml"));
        final Path mergeeManifestOne = ManifestMergerActionTest.rlocation((dataDir + "/mergeeOne/AndroidManifest.xml"));
        final Path mergeeManifestTwo = ManifestMergerActionTest.rlocation((dataDir + "/mergeeTwo/AndroidManifest.xml"));
        assertThat(mergerManifest.toFile().exists()).isTrue();
        assertThat(mergeeManifestOne.toFile().exists()).isTrue();
        assertThat(mergeeManifestTwo.toFile().exists()).isTrue();
        // The following code retrieves the path of the only AndroidManifest.xml in the expected/
        // manifests directory. Unfortunately, this test runs internally and externally and the files
        // have different names.
        final File expectedManifestDirectory = mergerManifest.getParent().resolveSibling("expected").toFile();
        final String[] debug = expectedManifestDirectory.list(new PatternFilenameFilter(".*AndroidManifest\\.xml$"));
        assertThat(debug).isNotNull();
        final File[] expectedManifestDirectoryManifests = expectedManifestDirectory.listFiles((File dir,String name) -> true);
        assertThat(expectedManifestDirectoryManifests).isNotNull();
        assertThat(expectedManifestDirectoryManifests).hasLength(1);
        final Path expectedManifest = expectedManifestDirectoryManifests[0].toPath();
        Files.createDirectories(working.resolve("output"));
        final Path mergedManifest = working.resolve("output/mergedManifest.xml");
        List<String> args = /* isLibrary */
        /* custom_package */
        generateArgs(mergerManifest, ImmutableMap.of(mergeeManifestOne, "mergeeOne", mergeeManifestTwo, "mergeeTwo"), false, ImmutableMap.of("applicationId", "com.google.android.apps.testapp"), "", mergedManifest);
        ManifestMergerAction.main(args.toArray(new String[0]));
        assertThat(Joiner.on(" ").join(Files.readAllLines(mergedManifest, StandardCharsets.UTF_8)).replaceAll("\\s+", " ").trim()).isEqualTo(Joiner.on(" ").join(Files.readAllLines(expectedManifest, StandardCharsets.UTF_8)).replaceAll("\\s+", " ").trim());
    }

    @Test
    public void fullIntegration() throws Exception {
        Files.createDirectories(working.resolve("output"));
        final Path binaryOutput = working.resolve("output/binaryManifest.xml");
        final Path libFooOutput = working.resolve("output/libFooManifest.xml");
        final Path libBarOutput = working.resolve("output/libBarManifest.xml");
        final Path binaryManifest = AndroidDataBuilder.of(working.resolve("binary")).createManifest("AndroidManifest.xml", "com.google.app", "").buildUnvalidated().getManifest();
        final Path libFooManifest = AndroidDataBuilder.of(working.resolve("libFoo")).createManifest("AndroidManifest.xml", "com.google.foo", " <application android:name=\"${applicationId}\" />").buildUnvalidated().getManifest();
        final Path libBarManifest = AndroidDataBuilder.of(working.resolve("libBar")).createManifest("AndroidManifest.xml", "com.google.bar", "<application android:name=\"${applicationId}\">", "<activity android:name=\".activityFoo\" />", "</application>").buildUnvalidated().getManifest();
        // libFoo manifest merging
        List<String> args = generateArgs(libFooManifest, ImmutableMap.<Path, String>of(), true, ImmutableMap.<String, String>of(), "", libFooOutput);
        ManifestMergerAction.main(args.toArray(new String[0]));
        assertThat(Joiner.on(" ").join(Files.readAllLines(libFooOutput, StandardCharsets.UTF_8)).replaceAll("\\s+", " ").trim()).contains(("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + ((("<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\"" + " package=\"com.google.foo\">") + " <application android:name=\"${applicationId}\" />") + "</manifest>")));
        // libBar manifest merging
        args = generateArgs(libBarManifest, ImmutableMap.<Path, String>of(), true, ImmutableMap.<String, String>of(), "com.google.libbar", libBarOutput);
        ManifestMergerAction.main(args.toArray(new String[0]));
        assertThat(Joiner.on(" ").join(Files.readAllLines(libBarOutput, StandardCharsets.UTF_8)).replaceAll("\\s+", " ").trim()).contains(("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + (((((" <manifest xmlns:android=\"http://schemas.android.com/apk/res/android\"" + " package=\"com.google.libbar\" >") + " <application android:name=\"${applicationId}\" >") + " <activity android:name=\"com.google.bar.activityFoo\" />") + " </application>") + " </manifest>")));
        // binary manifest merging
        args = /* library= */
        /* customPackage= */
        generateArgs(binaryManifest, ImmutableMap.of(libFooOutput, "libFoo", libBarOutput, "libBar"), false, ImmutableMap.of("applicationId", "com.google.android.app", "foo", "this \\\\: is \"a, \"bad string"), "", binaryOutput);
        ManifestMergerAction.main(args.toArray(new String[0]));
        assertThat(Joiner.on(" ").join(Files.readAllLines(binaryOutput, StandardCharsets.UTF_8)).replaceAll("\\s+", " ").trim()).contains(("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + (((((" <manifest xmlns:android=\"http://schemas.android.com/apk/res/android\"" + " package=\"com.google.android.app\" >") + " <application android:name=\"com.google.android.app\" >") + " <activity android:name=\"com.google.bar.activityFoo\" />") + " </application>") + " </manifest>")));
    }
}

