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
package com.google.devtools.build.android;


import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link RClassGeneratorAction}.
 */
@RunWith(JUnit4.class)
public class RClassGeneratorActionTest {
    private Path tempDir;

    /**
     * TODO(jvoung): use {@link AndroidDataBuilder} instead, once that's moved to this source tree.
     * This is a slimmed down version used to avoid dependencies.
     */
    private static class ManifestBuilder {
        private final Path root;

        private ManifestBuilder(Path root) {
            this.root = root;
        }

        public static RClassGeneratorActionTest.ManifestBuilder of(Path root) {
            return new RClassGeneratorActionTest.ManifestBuilder(root);
        }

        public Path createManifest(String path, String manifestPackage, String... lines) throws IOException {
            Path manifest = root.resolve(path);
            Files.createDirectories(root);
            Files.write(manifest, String.format(("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + (("<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\" " + " package=\"%s\">") + "%s</manifest>")), manifestPackage, Joiner.on("\n").join(lines)).getBytes(StandardCharsets.UTF_8));
            return manifest;
        }
    }

    @Test
    public void withBinaryAndLibraries() throws Exception {
        Path binaryManifest = RClassGeneratorActionTest.ManifestBuilder.of(tempDir.resolve("binary")).createManifest("AndroidManifest.xml", "com.google.app", "<application android:name=\"com.google.app\">", "<activity android:name=\"com.google.bar.activityFoo\" />", "</application>");
        Path libFooManifest = RClassGeneratorActionTest.ManifestBuilder.of(tempDir.resolve("libFoo")).createManifest("AndroidManifest.xml", "com.google.foo", "");
        Path libBarManifest = RClassGeneratorActionTest.ManifestBuilder.of(tempDir.resolve("libBar")).createManifest("AndroidManifest.xml", "com.google.bar", "");
        Path binarySymbols = createFile("R.txt", "int attr agility 0x7f010000", "int attr dexterity 0x7f010001", "int drawable heart 0x7f020000", "int id someTextView 0x7f080000", "int integer maxNotifications 0x7f090000", "int string alphabet 0x7f100000", "int string ok 0x7f100001");
        Path libFooSymbols = createFile("libFoo.R.txt", "int attr agility 0x1", "int id someTextView 0x1", "int string ok 0x1");
        Path libBarSymbols = createFile("libBar.R.txt", "int attr dexterity 0x1", "int drawable heart 0x1");
        Path jarPath = tempDir.resolve("app_resources.jar");
        RClassGeneratorAction.main(ImmutableList.<String>of("--primaryRTxt", binarySymbols.toString(), "--primaryManifest", binaryManifest.toString(), "--library", ((libFooSymbols + ",") + libFooManifest), "--library", ((libBarSymbols + ",") + libBarManifest), "--classJarOutput", jarPath.toString(), "--targetLabel", "//foo:foo").toArray(new String[0]));
        assertThat(Files.exists(jarPath)).isTrue();
        try (ZipFile zip = new ZipFile(jarPath.toFile())) {
            List<? extends ZipEntry> zipEntries = Collections.list(zip.entries());
            Iterable<String> entries = getZipFilenames(zipEntries);
            assertThat(entries).containsExactly("com/google/foo/R$attr.class", "com/google/foo/R$id.class", "com/google/foo/R$string.class", "com/google/foo/R.class", "com/google/bar/R$attr.class", "com/google/bar/R$drawable.class", "com/google/bar/R.class", "com/google/app/R$attr.class", "com/google/app/R$drawable.class", "com/google/app/R$id.class", "com/google/app/R$integer.class", "com/google/app/R$string.class", "com/google/app/R.class", "META-INF/", "META-INF/MANIFEST.MF");
            RClassGeneratorActionTest.ZipMtimeAsserter.assertEntries(zipEntries);
        }
        try (JarInputStream jar = new JarInputStream(Files.newInputStream(jarPath))) {
            assertThat(jar.getManifest().getMainAttributes().getValue("Target-Label")).isEqualTo("//foo:foo");
        }
    }

    @Test
    public void withNoBinaryAndLibraries() throws Exception {
        Path libFooManifest = RClassGeneratorActionTest.ManifestBuilder.of(tempDir.resolve("libFoo")).createManifest("AndroidManifest.xml", "com.google.foo", "");
        Path libBarManifest = RClassGeneratorActionTest.ManifestBuilder.of(tempDir.resolve("libBar")).createManifest("AndroidManifest.xml", "com.google.bar", "");
        Path libFooSymbols = createFile("libFoo.R.txt", "int attr agility 0x1", "int id someTextView 0x1", "int string ok 0x1");
        Path libBarSymbols = createFile("libBar.R.txt", "int attr dexterity 0x1", "int drawable heart 0x1");
        Path jarPath = tempDir.resolve("app_resources.jar");
        RClassGeneratorAction.main(ImmutableList.<String>of("--library", ((libFooSymbols + ",") + libFooManifest), "--library", ((libBarSymbols + ",") + libBarManifest), "--classJarOutput", jarPath.toString()).toArray(new String[0]));
        assertThat(Files.exists(jarPath)).isTrue();
        try (ZipFile zip = new ZipFile(jarPath.toFile())) {
            List<? extends ZipEntry> zipEntries = Collections.list(zip.entries());
            Iterable<String> entries = getZipFilenames(zipEntries);
            assertThat(entries).containsExactly("com/google/foo/R$attr.class", "com/google/foo/R$id.class", "com/google/foo/R$string.class", "com/google/foo/R.class", "com/google/bar/R$attr.class", "com/google/bar/R$drawable.class", "com/google/bar/R.class", "META-INF/", "META-INF/MANIFEST.MF");
            RClassGeneratorActionTest.assertFieldsFinal(jarPath, "com.google.foo.R$attr", true);
            RClassGeneratorActionTest.assertFieldsFinal(jarPath, "com.google.foo.R$id", true);
            RClassGeneratorActionTest.assertFieldsFinal(jarPath, "com.google.foo.R$string", true);
            RClassGeneratorActionTest.assertFieldsFinal(jarPath, "com.google.bar.R$attr", true);
            RClassGeneratorActionTest.assertFieldsFinal(jarPath, "com.google.bar.R$drawable", true);
            RClassGeneratorActionTest.ZipMtimeAsserter.assertEntries(zipEntries);
        }
    }

    @Test
    public void withNoBinaryAndLibraries_noFinalFields() throws Exception {
        Path libFooManifest = RClassGeneratorActionTest.ManifestBuilder.of(tempDir.resolve("libFoo")).createManifest("AndroidManifest.xml", "com.google.foo", "");
        Path libBarManifest = RClassGeneratorActionTest.ManifestBuilder.of(tempDir.resolve("libBar")).createManifest("AndroidManifest.xml", "com.google.bar", "");
        Path libFooSymbols = createFile("libFoo.R.txt", "int attr agility 0x1", "int id someTextView 0x1", "int string ok 0x1");
        Path libBarSymbols = createFile("libBar.R.txt", "int attr dexterity 0x1", "int drawable heart 0x1");
        Path jarPath = tempDir.resolve("app_resources.jar");
        RClassGeneratorAction.main(ImmutableList.<String>of("--library", ((libFooSymbols + ",") + libFooManifest), "--library", ((libBarSymbols + ",") + libBarManifest), "--nofinalFields", "--classJarOutput", jarPath.toString()).toArray(new String[0]));
        assertThat(Files.exists(jarPath)).isTrue();
        try (ZipFile zip = new ZipFile(jarPath.toFile())) {
            List<? extends ZipEntry> zipEntries = Collections.list(zip.entries());
            Iterable<String> entries = getZipFilenames(zipEntries);
            assertThat(entries).containsExactly("com/google/foo/R$attr.class", "com/google/foo/R$id.class", "com/google/foo/R$string.class", "com/google/foo/R.class", "com/google/bar/R$attr.class", "com/google/bar/R$drawable.class", "com/google/bar/R.class", "META-INF/", "META-INF/MANIFEST.MF");
            RClassGeneratorActionTest.assertFieldsFinal(jarPath, "com.google.foo.R$attr", false);
            RClassGeneratorActionTest.assertFieldsFinal(jarPath, "com.google.foo.R$id", false);
            RClassGeneratorActionTest.assertFieldsFinal(jarPath, "com.google.foo.R$string", false);
            RClassGeneratorActionTest.assertFieldsFinal(jarPath, "com.google.bar.R$attr", false);
            RClassGeneratorActionTest.assertFieldsFinal(jarPath, "com.google.bar.R$drawable", false);
            RClassGeneratorActionTest.ZipMtimeAsserter.assertEntries(zipEntries);
        }
    }

    @Test
    public void withBinaryNoLibraries() throws Exception {
        Path binaryManifest = RClassGeneratorActionTest.ManifestBuilder.of(tempDir.resolve("binary")).createManifest("AndroidManifest.xml", "com.google.app", "<application android:name=\"com.google.app\">", "<activity android:name=\"com.google.bar.activityFoo\" />", "</application>");
        Path binarySymbols = createFile("R.txt", "int attr agility 0x7f010000", "int attr dexterity 0x7f010001", "int drawable heart 0x7f020000", "int id someTextView 0x7f080000", "int integer maxNotifications 0x7f090000", "int string alphabet 0x7f100000", "int string ok 0x7f100001");
        Path jarPath = tempDir.resolve("app_resources.jar");
        RClassGeneratorAction.main(ImmutableList.<String>of("--primaryRTxt", binarySymbols.toString(), "--primaryManifest", binaryManifest.toString(), "--classJarOutput", jarPath.toString()).toArray(new String[0]));
        assertThat(Files.exists(jarPath)).isTrue();
        try (ZipFile zip = new ZipFile(jarPath.toFile())) {
            List<? extends ZipEntry> zipEntries = Collections.list(zip.entries());
            Iterable<String> entries = getZipFilenames(zipEntries);
            assertThat(entries).containsExactly("com/google/app/R$attr.class", "com/google/app/R$drawable.class", "com/google/app/R$id.class", "com/google/app/R$integer.class", "com/google/app/R$string.class", "com/google/app/R.class", "META-INF/", "META-INF/MANIFEST.MF");
            RClassGeneratorActionTest.assertFieldsFinal(jarPath, "com.google.app.R$attr", true);
            RClassGeneratorActionTest.assertFieldsFinal(jarPath, "com.google.app.R$drawable", true);
            RClassGeneratorActionTest.assertFieldsFinal(jarPath, "com.google.app.R$id", true);
            RClassGeneratorActionTest.assertFieldsFinal(jarPath, "com.google.app.R$integer", true);
            RClassGeneratorActionTest.assertFieldsFinal(jarPath, "com.google.app.R$string", true);
            RClassGeneratorActionTest.ZipMtimeAsserter.assertEntries(zipEntries);
        }
    }

    @Test
    public void withBinaryNoLibraries_noFinalFields() throws Exception {
        Path binaryManifest = RClassGeneratorActionTest.ManifestBuilder.of(tempDir.resolve("binary")).createManifest("AndroidManifest.xml", "com.google.app", "<application android:name=\"com.google.app\">", "<activity android:name=\"com.google.bar.activityFoo\" />", "</application>");
        Path binarySymbols = createFile("R.txt", "int attr agility 0x7f010000", "int attr dexterity 0x7f010001", "int drawable heart 0x7f020000", "int id someTextView 0x7f080000", "int integer maxNotifications 0x7f090000", "int string alphabet 0x7f100000", "int string ok 0x7f100001");
        Path jarPath = tempDir.resolve("app_resources.jar");
        RClassGeneratorAction.main(ImmutableList.<String>of("--primaryRTxt", binarySymbols.toString(), "--primaryManifest", binaryManifest.toString(), "--nofinalFields", "--classJarOutput", jarPath.toString()).toArray(new String[0]));
        assertThat(Files.exists(jarPath)).isTrue();
        try (ZipFile zip = new ZipFile(jarPath.toFile())) {
            List<? extends ZipEntry> zipEntries = Collections.list(zip.entries());
            Iterable<String> entries = getZipFilenames(zipEntries);
            assertThat(entries).containsExactly("com/google/app/R$attr.class", "com/google/app/R$drawable.class", "com/google/app/R$id.class", "com/google/app/R$integer.class", "com/google/app/R$string.class", "com/google/app/R.class", "META-INF/", "META-INF/MANIFEST.MF");
            RClassGeneratorActionTest.assertFieldsFinal(jarPath, "com.google.app.R$attr", false);
            RClassGeneratorActionTest.assertFieldsFinal(jarPath, "com.google.app.R$drawable", false);
            RClassGeneratorActionTest.assertFieldsFinal(jarPath, "com.google.app.R$id", false);
            RClassGeneratorActionTest.assertFieldsFinal(jarPath, "com.google.app.R$integer", false);
            RClassGeneratorActionTest.assertFieldsFinal(jarPath, "com.google.app.R$string", false);
            RClassGeneratorActionTest.ZipMtimeAsserter.assertEntries(zipEntries);
        }
    }

    @Test
    public void noBinary() throws Exception {
        Path jarPath = tempDir.resolve("app_resources.jar");
        RClassGeneratorAction.main(ImmutableList.<String>of("--classJarOutput", jarPath.toString()).toArray(new String[0]));
        assertThat(Files.exists(jarPath)).isTrue();
        try (ZipFile zip = new ZipFile(jarPath.toFile())) {
            List<? extends ZipEntry> zipEntries = Collections.list(zip.entries());
            Iterable<String> entries = getZipFilenames(zipEntries);
            assertThat(entries).containsExactly("META-INF/", "META-INF/MANIFEST.MF");
            RClassGeneratorActionTest.ZipMtimeAsserter.assertEntries(zipEntries);
        }
    }

    @Test
    public void customPackageForR() throws Exception {
        Path binaryManifest = RClassGeneratorActionTest.ManifestBuilder.of(tempDir.resolve("binary")).createManifest("AndroidManifest.xml", "com.google.app", "<application android:name=\"com.google.app\">", "<activity android:name=\"com.google.foo.activityFoo\" />", "</application>");
        Path libFooManifest = RClassGeneratorActionTest.ManifestBuilder.of(tempDir.resolve("libFoo")).createManifest("AndroidManifest.xml", "com.google.foo", "");
        Path binarySymbols = createFile("R.txt", "int attr agility 0x7f010000", "int integer maxNotifications 0x7f090000", "int string ok 0x7f100001");
        Path libFooSymbols = createFile("libFoo.R.txt", "int string ok 0x1");
        Path jarPath = tempDir.resolve("app_resources.jar");
        RClassGeneratorAction.main(ImmutableList.<String>of("--primaryRTxt", binarySymbols.toString(), "--primaryManifest", binaryManifest.toString(), "--packageForR", "com.custom.er", "--library", ((libFooSymbols + ",") + libFooManifest), "--classJarOutput", jarPath.toString()).toArray(new String[0]));
        assertThat(Files.exists(jarPath)).isTrue();
        try (ZipFile zip = new ZipFile(jarPath.toFile())) {
            List<? extends ZipEntry> zipEntries = Collections.list(zip.entries());
            Iterable<String> entries = getZipFilenames(zipEntries);
            assertThat(entries).containsExactly("com/google/foo/R$string.class", "com/google/foo/R.class", "com/custom/er/R$attr.class", "com/custom/er/R$integer.class", "com/custom/er/R$string.class", "com/custom/er/R.class", "META-INF/", "META-INF/MANIFEST.MF");
            RClassGeneratorActionTest.ZipMtimeAsserter.assertEntries(zipEntries);
        }
    }

    @Test
    public void noSymbolsNoRClass() throws Exception {
        Path binaryManifest = RClassGeneratorActionTest.ManifestBuilder.of(tempDir.resolve("binary")).createManifest("AndroidManifest.xml", "com.google.app", "<application android:name=\"com.google.app\">", "<activity android:name=\"com.google.foo.activityFoo\" />", "</application>");
        Path binarySymbols = createFile("R.txt", "");
        Path jarPath = tempDir.resolve("app_resources.jar");
        RClassGeneratorAction.main(ImmutableList.<String>of("--primaryRTxt", binarySymbols.toString(), "--primaryManifest", binaryManifest.toString(), "--classJarOutput", jarPath.toString()).toArray(new String[0]));
        assertThat(Files.exists(jarPath)).isTrue();
        try (ZipFile zip = new ZipFile(jarPath.toFile())) {
            List<? extends ZipEntry> zipEntries = Collections.list(zip.entries());
            Iterable<String> entries = getZipFilenames(zipEntries);
            assertThat(entries).containsExactly("META-INF/", "META-INF/MANIFEST.MF");
            RClassGeneratorActionTest.ZipMtimeAsserter.assertEntries(zipEntries);
        }
    }

    private static final class ZipMtimeAsserter {
        private static final long ZIP_EPOCH = Instant.parse("1980-01-01T00:00:00Z").getEpochSecond();

        private static final long ZIP_EPOCH_PLUS_ONE_DAY = Instant.parse("1980-01-02T00:00:00Z").getEpochSecond();

        public static void assertEntry(ZipEntry e) {
            // getLastModifiedTime().toMillis() returns milliseconds, Instant.getEpochSecond() returns
            // seconds.
            long mtime = (e.getLastModifiedTime().toMillis()) / 1000;
            // The ZIP epoch is the same as the MS-DOS epoch, 1980-01-01T00:00:00Z.
            // AndroidResourceOutputs.ZipBuilder sets this to most of its entries, except for .class files
            // for which the ZipBuilder increments the timestamp by 2 seconds.
            // We don't care about the details of this logic and asserting exact timestamps would couple
            // the test to the code too tightly, so here we only assert that the timestamp is on
            // 1980-01-01, ignoring the exact time.
            // AndroidResourceOutputs.ZipBuilde sets the ZIP epoch (same as the MS-DOS epoch,
            // 1980-01-01T00:00:00Z) as the timestamp for all of its entries (except .class files, for
            // which it sets a timestamp 2 seconds later than the DOS epoch).
            // We don't care about the exact timestamps though, only that they are stable, so let's just
            // assert that they are all on the day of 1980-01-01.
            if ((mtime < (RClassGeneratorActionTest.ZipMtimeAsserter.ZIP_EPOCH)) || (mtime > (RClassGeneratorActionTest.ZipMtimeAsserter.ZIP_EPOCH_PLUS_ONE_DAY))) {
                Assert.fail(String.format("e=(%s) mtime=(%s)", e.getName(), e.getLastModifiedTime()));
            }
        }

        public static void assertEntries(Iterable<? extends ZipEntry> entries) throws Exception {
            for (ZipEntry e : entries) {
                RClassGeneratorActionTest.ZipMtimeAsserter.assertEntry(e);
            }
        }
    }
}

