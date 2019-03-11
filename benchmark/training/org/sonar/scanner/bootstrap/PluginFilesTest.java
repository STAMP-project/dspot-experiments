/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.scanner.bootstrap;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.sonar.scanner.bootstrap.ScannerPluginInstaller.InstalledPlugin;


public class PluginFilesTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public MockWebServer server = new MockWebServer();

    private File userHome;

    private PluginFiles underTest;

    @Test
    public void get_jar_from_cache_if_present() throws Exception {
        PluginFilesTest.FileAndMd5 jar = createFileInCache("foo");
        File result = underTest.get(PluginFilesTest.newInstalledPlugin("foo", jar.md5)).get();
        PluginFilesTest.verifySameContent(result, jar);
        // no requests to server
        assertThat(server.getRequestCount()).isEqualTo(0);
    }

    @Test
    public void download_and_add_jar_to_cache_if_missing() throws Exception {
        PluginFilesTest.FileAndMd5 tempJar = new PluginFilesTest.FileAndMd5();
        enqueueDownload(tempJar);
        InstalledPlugin plugin = PluginFilesTest.newInstalledPlugin("foo", tempJar.md5);
        File result = underTest.get(plugin).get();
        PluginFilesTest.verifySameContent(result, tempJar);
        HttpUrl requestedUrl = server.takeRequest().getRequestUrl();
        assertThat(requestedUrl.encodedPath()).isEqualTo("/api/plugins/download");
        assertThat(requestedUrl.encodedQuery()).isEqualTo("plugin=foo&acceptCompressions=pack200");
        // get from cache on second call
        result = underTest.get(plugin).get();
        PluginFilesTest.verifySameContent(result, tempJar);
        assertThat(server.getRequestCount()).isEqualTo(1);
    }

    @Test
    public void download_compressed_and_add_uncompressed_to_cache_if_missing() throws Exception {
        PluginFilesTest.FileAndMd5 jar = new PluginFilesTest.FileAndMd5();
        enqueueCompressedDownload(jar, true);
        InstalledPlugin plugin = PluginFilesTest.newInstalledPlugin("foo", jar.md5);
        File result = underTest.get(plugin).get();
        verifySameContentAfterCompression(jar.file, result);
        RecordedRequest recordedRequest = server.takeRequest();
        assertThat(recordedRequest.getRequestUrl().queryParameter("acceptCompressions")).isEqualTo("pack200");
        // get from cache on second call
        result = underTest.get(plugin).get();
        verifySameContentAfterCompression(jar.file, result);
        assertThat(server.getRequestCount()).isEqualTo(1);
    }

    @Test
    public void return_empty_if_plugin_not_found_on_server() {
        server.enqueue(new MockResponse().setResponseCode(404));
        InstalledPlugin plugin = PluginFilesTest.newInstalledPlugin("foo", "abc");
        Optional<File> result = underTest.get(plugin);
        assertThat(result).isEmpty();
    }

    @Test
    public void fail_if_integrity_of_download_is_not_valid() throws IOException {
        PluginFilesTest.FileAndMd5 tempJar = new PluginFilesTest.FileAndMd5();
        enqueueDownload(tempJar.file, "invalid_hash");
        InstalledPlugin plugin = PluginFilesTest.newInstalledPlugin("foo", "abc");
        expectISE("foo", ("was expected to have checksum invalid_hash but had " + (tempJar.md5)));
        underTest.get(plugin);
    }

    @Test
    public void fail_if_integrity_of_compressed_download_is_not_valid() throws Exception {
        PluginFilesTest.FileAndMd5 jar = new PluginFilesTest.FileAndMd5();
        enqueueCompressedDownload(jar, false);
        expectISE("foo", "was expected to have checksum invalid_hash but had ");
        InstalledPlugin plugin = PluginFilesTest.newInstalledPlugin("foo", jar.md5);
        underTest.get(plugin).get();
    }

    @Test
    public void fail_if_md5_header_is_missing_from_response() throws IOException {
        File tempJar = temp.newFile();
        enqueueDownload(tempJar, null);
        InstalledPlugin plugin = PluginFilesTest.newInstalledPlugin("foo", "abc");
        expectISE("foo", "did not return header Sonar-MD5");
        underTest.get(plugin);
    }

    @Test
    public void fail_if_compressed_download_cannot_be_uncompressed() {
        MockResponse response = new MockResponse().setBody("not binary");
        response.setHeader("Sonar-MD5", DigestUtils.md5Hex("not binary"));
        response.setHeader("Sonar-UncompressedMD5", "abc");
        response.setHeader("Sonar-Compression", "pack200");
        server.enqueue(response);
        expectISE("foo", "Pack200 error");
        InstalledPlugin plugin = PluginFilesTest.newInstalledPlugin("foo", "abc");
        underTest.get(plugin).get();
    }

    @Test
    public void fail_if_server_returns_error() {
        server.enqueue(new MockResponse().setResponseCode(500));
        InstalledPlugin plugin = PluginFilesTest.newInstalledPlugin("foo", "abc");
        expectISE("foo", "returned code 500");
        underTest.get(plugin);
    }

    @Test
    public void download_a_new_version_of_plugin_during_blue_green_switch() throws IOException {
        PluginFilesTest.FileAndMd5 tempJar = new PluginFilesTest.FileAndMd5();
        enqueueDownload(tempJar);
        // expecting to download plugin foo with checksum "abc"
        InstalledPlugin pluginV1 = PluginFilesTest.newInstalledPlugin("foo", "abc");
        File result = underTest.get(pluginV1).get();
        PluginFilesTest.verifySameContent(result, tempJar);
        // new version of downloaded jar is put in cache with the new md5
        InstalledPlugin pluginV2 = PluginFilesTest.newInstalledPlugin("foo", tempJar.md5);
        result = underTest.get(pluginV2).get();
        PluginFilesTest.verifySameContent(result, tempJar);
        assertThat(server.getRequestCount()).isEqualTo(1);
        // v1 still requests server and downloads v2
        enqueueDownload(tempJar);
        result = underTest.get(pluginV1).get();
        PluginFilesTest.verifySameContent(result, tempJar);
        assertThat(server.getRequestCount()).isEqualTo(2);
    }

    @Test
    public void fail_if_cached_file_is_outside_cache_dir() throws IOException {
        PluginFilesTest.FileAndMd5 tempJar = new PluginFilesTest.FileAndMd5();
        enqueueDownload(tempJar);
        InstalledPlugin plugin = PluginFilesTest.newInstalledPlugin("foo/bar", "abc");
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Fail to download plugin [foo/bar]. Key is not valid.");
        underTest.get(plugin);
    }

    private class FileAndMd5 {
        private final File file;

        private final String md5;

        FileAndMd5(File file, String md5) {
            this.file = file;
            this.md5 = md5;
        }

        FileAndMd5() throws IOException {
            this.file = temp.newFile();
            FileUtils.write(this.file, RandomStringUtils.random(3));
            try (InputStream fis = FileUtils.openInputStream(this.file)) {
                this.md5 = DigestUtils.md5Hex(fis);
            } catch (IOException e) {
                throw new IllegalStateException(("Fail to compute md5 of " + (this.file)), e);
            }
        }
    }
}

