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
package org.sonar.server.plugins;


import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import java.io.File;
import java.net.URI;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.utils.HttpDownloader;
import org.sonar.core.platform.PluginInfo;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.platform.ServerFileSystem;
import org.sonar.updatecenter.common.Plugin;
import org.sonar.updatecenter.common.Release;
import org.sonar.updatecenter.common.UpdateCenter;
import org.sonar.updatecenter.common.Version;


public class PluginDownloaderTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private File downloadDir;

    private UpdateCenterMatrixFactory updateCenterMatrixFactory;

    private UpdateCenter updateCenter;

    private HttpDownloader httpDownloader;

    private PluginDownloader pluginDownloader;

    @Test
    public void clean_temporary_files_at_startup() throws Exception {
        FileUtils.touch(new File(downloadDir, "sonar-php.jar"));
        FileUtils.touch(new File(downloadDir, "sonar-js.jar.tmp"));
        assertThat(downloadDir.listFiles()).hasSize(2);
        pluginDownloader.start();
        File[] files = downloadDir.listFiles();
        assertThat(files).hasSize(1);
        assertThat(files[0].getName()).isEqualTo("sonar-php.jar");
    }

    @Test
    public void download_from_url() {
        Plugin test = Plugin.factory("test");
        Release test10 = setDownloadUrl("http://server/test-1.0.jar");
        test.addRelease(test10);
        Mockito.when(updateCenter.findInstallablePlugins("foo", create("1.0"))).thenReturn(Lists.newArrayList(test10));
        pluginDownloader.start();
        pluginDownloader.download("foo", create("1.0"));
        // SONAR-4523: do not corrupt JAR files when restarting the server while a plugin is being downloaded.
        // The JAR file is downloaded in a temp file
        Mockito.verify(httpDownloader).download(ArgumentMatchers.any(URI.class), ArgumentMatchers.argThat(new PluginDownloaderTest.HasFileName("test-1.0.jar.tmp")));
        assertThat(new File(downloadDir, "test-1.0.jar")).exists();
        assertThat(new File(downloadDir, "test-1.0.jar.tmp")).doesNotExist();
    }

    @Test
    public void download_when_update_center_is_unavailable_with_no_exception_thrown() {
        Mockito.when(updateCenterMatrixFactory.getUpdateCenter(ArgumentMatchers.anyBoolean())).thenReturn(Optional.absent());
        Plugin test = Plugin.factory("test");
        Release test10 = setDownloadUrl("http://server/test-1.0.jar");
        test.addRelease(test10);
        pluginDownloader.start();
        pluginDownloader.download("foo", create("1.0"));
    }

    /**
     * SONAR-4685
     */
    @Test
    public void download_from_redirect_url() {
        Plugin test = Plugin.factory("plugintest");
        Release test10 = setDownloadUrl("http://server/redirect?r=release&g=test&a=test&v=1.0&e=jar");
        test.addRelease(test10);
        Mockito.when(updateCenter.findInstallablePlugins("foo", create("1.0"))).thenReturn(Lists.newArrayList(test10));
        pluginDownloader.start();
        pluginDownloader.download("foo", create("1.0"));
        // SONAR-4523: do not corrupt JAR files when restarting the server while a plugin is being downloaded.
        // The JAR file is downloaded in a temp file
        Mockito.verify(httpDownloader).download(ArgumentMatchers.any(URI.class), ArgumentMatchers.argThat(new PluginDownloaderTest.HasFileName("plugintest-1.0.jar.tmp")));
        assertThat(new File(downloadDir, "plugintest-1.0.jar")).exists();
        assertThat(new File(downloadDir, "plugintest-1.0.jar.tmp")).doesNotExist();
    }

    @Test
    public void throw_exception_if_download_dir_is_invalid() throws Exception {
        ServerFileSystem fs = Mockito.mock(ServerFileSystem.class);
        // download dir is a file instead of being a directory
        File downloadDir = testFolder.newFile();
        Mockito.when(fs.getDownloadedPluginsDir()).thenReturn(downloadDir);
        pluginDownloader = new PluginDownloader(updateCenterMatrixFactory, httpDownloader, fs);
        try {
            pluginDownloader.start();
            Assert.fail();
        } catch (IllegalStateException e) {
            // ok
        }
    }

    @Test
    public void fail_if_no_compatible_plugin_found() {
        expectedException.expect(BadRequestException.class);
        pluginDownloader.download("foo", create("1.0"));
    }

    @Test
    public void download_from_file() throws Exception {
        Plugin test = Plugin.factory("test");
        File file = testFolder.newFile("test-1.0.jar");
        file.createNewFile();
        Release test10 = setDownloadUrl(("file://" + (FilenameUtils.separatorsToUnix(file.getCanonicalPath()))));
        test.addRelease(test10);
        Mockito.when(updateCenter.findInstallablePlugins("foo", create("1.0"))).thenReturn(Lists.newArrayList(test10));
        pluginDownloader.start();
        pluginDownloader.download("foo", create("1.0"));
        Mockito.verify(httpDownloader, Mockito.never()).download(ArgumentMatchers.any(URI.class), ArgumentMatchers.any(File.class));
        assertThat(noDownloadedFiles()).isGreaterThan(0);
    }

    @Test
    public void throw_exception_if_could_not_download() {
        Plugin test = Plugin.factory("test");
        Release test10 = setDownloadUrl("file://not_found");
        test.addRelease(test10);
        Mockito.when(updateCenter.findInstallablePlugins("foo", create("1.0"))).thenReturn(Lists.newArrayList(test10));
        pluginDownloader.start();
        try {
            pluginDownloader.download("foo", create("1.0"));
            Assert.fail();
        } catch (IllegalStateException e) {
            // ok
        }
    }

    @Test
    public void throw_exception_if_download_fail() {
        Plugin test = Plugin.factory("test");
        Release test10 = setDownloadUrl("http://server/test-1.0.jar");
        test.addRelease(test10);
        Mockito.when(updateCenter.findInstallablePlugins("foo", create("1.0"))).thenReturn(Lists.newArrayList(test10));
        Mockito.doThrow(new RuntimeException()).when(httpDownloader).download(ArgumentMatchers.any(URI.class), ArgumentMatchers.any(File.class));
        pluginDownloader.start();
        try {
            pluginDownloader.download("foo", create("1.0"));
            Assert.fail();
        } catch (IllegalStateException e) {
            // ok
        }
    }

    @Test
    public void read_download_folder() throws Exception {
        pluginDownloader.start();
        assertThat(noDownloadedFiles()).isZero();
        FileUtils.copyFileToDirectory(TestProjectUtils.jarOf("test-base-plugin"), downloadDir);
        assertThat(pluginDownloader.getDownloadedPlugins()).hasSize(1);
        PluginInfo info = pluginDownloader.getDownloadedPlugins().iterator().next();
        assertThat(info.getKey()).isEqualTo("testbase");
        assertThat(info.getName()).isEqualTo("Base Plugin");
        assertThat(info.getVersion()).isEqualTo(Version.create("0.1-SNAPSHOT"));
        assertThat(info.getMainClass()).isEqualTo("BasePlugin");
    }

    @Test
    public void getDownloadedPluginFilenames_reads_plugin_info_of_files_in_download_folder() throws Exception {
        pluginDownloader.start();
        assertThat(pluginDownloader.getDownloadedPlugins()).hasSize(0);
        File file1 = new File(downloadDir, "file1.jar");
        file1.createNewFile();
        File file2 = new File(downloadDir, "file2.jar");
        file2.createNewFile();
        assertThat(noDownloadedFiles()).isEqualTo(2);
    }

    @Test
    public void cancel_downloads() throws Exception {
        File file1 = new File(downloadDir, "file1.jar");
        file1.createNewFile();
        File file2 = new File(downloadDir, "file2.jar");
        file2.createNewFile();
        pluginDownloader.start();
        assertThat(noDownloadedFiles()).isGreaterThan(0);
        pluginDownloader.cancelDownloads();
        assertThat(noDownloadedFiles()).isZero();
    }

    // SONAR-5011
    @Test
    public void download_common_transitive_dependency() {
        Plugin test1 = Plugin.factory("test1");
        Release test1R = setDownloadUrl("http://server/test1-1.0.jar");
        test1.addRelease(test1R);
        Plugin test2 = Plugin.factory("test2");
        Release test2R = setDownloadUrl("http://server/test2-1.0.jar");
        test2.addRelease(test2R);
        Plugin testDep = Plugin.factory("testdep");
        Release testDepR = setDownloadUrl("http://server/testdep-1.0.jar");
        testDep.addRelease(testDepR);
        Mockito.when(updateCenter.findInstallablePlugins("test1", create("1.0"))).thenReturn(Lists.newArrayList(test1R, testDepR));
        Mockito.when(updateCenter.findInstallablePlugins("test2", create("1.0"))).thenReturn(Lists.newArrayList(test2R, testDepR));
        pluginDownloader.start();
        pluginDownloader.download("test1", create("1.0"));
        pluginDownloader.download("test2", create("1.0"));
        assertThat(new File(downloadDir, "test1-1.0.jar")).exists();
        assertThat(new File(downloadDir, "test2-1.0.jar")).exists();
        assertThat(new File(downloadDir, "testdep-1.0.jar")).exists();
    }

    class HasFileName implements ArgumentMatcher<File> {
        private final String name;

        HasFileName(String name) {
            this.name = name;
        }

        @Override
        public boolean matches(File file) {
            return file.getName().equals(name);
        }
    }
}

