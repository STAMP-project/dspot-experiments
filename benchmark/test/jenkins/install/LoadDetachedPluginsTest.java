/**
 * The MIT License
 *
 * Copyright 2017 CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jenkins.install;


import hudson.Plugin;
import hudson.PluginManagerUtil;
import hudson.util.VersionNumber;
import java.io.IOException;
import jenkins.plugins.DetachedPluginsUtil;
import jenkins.plugins.DetachedPluginsUtil.DetachedPlugin;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.RestartableJenkinsRule;
import org.jvnet.hudson.test.SmokeTest;
import org.jvnet.hudson.test.recipes.LocalData;


@Category(SmokeTest.class)
public class LoadDetachedPluginsTest {
    @Rule
    public RestartableJenkinsRule rr = PluginManagerUtil.newRestartableJenkinsRule();

    @Issue("JENKINS-48365")
    @Test
    @LocalData
    public void upgradeFromJenkins1() throws IOException {
        VersionNumber since = new VersionNumber("1.550");
        rr.then(( r) -> {
            List<DetachedPlugin> detachedPlugins = DetachedPluginsUtil.getDetachedPlugins(since);
            assertThat("Plugins have been detached since the pre-upgrade version", detachedPlugins.size(), greaterThan(4));
            assertThat("Plugins detached between the pre-upgrade version and the current version should be installed", getInstalledDetachedPlugins(r, detachedPlugins).size(), equalTo(detachedPlugins.size()));
            assertNoFailedPlugins(r);
        });
    }

    @Issue("JENKINS-48365")
    @Test
    @LocalData
    public void upgradeFromJenkins2() {
        VersionNumber since = new VersionNumber("2.0");
        rr.then(( r) -> {
            List<DetachedPlugin> detachedPlugins = DetachedPluginsUtil.getDetachedPlugins(since);
            assertThat("Plugins have been detached since the pre-upgrade version", detachedPlugins.size(), greaterThan(1));
            assertThat("Plugins detached between the pre-upgrade version and the current version should be installed", getInstalledDetachedPlugins(r, detachedPlugins).size(), equalTo(detachedPlugins.size()));
            assertNoFailedPlugins(r);
        });
    }

    @Issue("JENKINS-48604")
    @Test
    @LocalData
    public void upgradeFromJenkins2WithNewerDependency() {
        VersionNumber since = new VersionNumber("2.0");
        rr.then(( r) -> {
            List<DetachedPlugin> detachedPlugins = DetachedPluginsUtil.getDetachedPlugins(since);
            assertThat("Plugins have been detached since the pre-upgrade version", detachedPlugins.size(), greaterThan(1));
            assertThat("Plugins detached between the pre-upgrade version and the current version should be installed", getInstalledDetachedPlugins(r, detachedPlugins).size(), equalTo(detachedPlugins.size()));
            Plugin scriptSecurity = r.jenkins.getPlugin("script-security");
            assertThat("Script-security should be installed", scriptSecurity, notNullValue());
            assertThat("Dependencies of detached plugins should not be downgraded", scriptSecurity.getWrapper().getVersionNumber(), equalTo(new VersionNumber("1.34")));
            assertNoFailedPlugins(r);
        });
    }

    @Test
    @LocalData
    public void upgradeFromJenkins2WithOlderDependency() {
        VersionNumber since = new VersionNumber("2.0");
        rr.then(( r) -> {
            List<DetachedPlugin> detachedPlugins = DetachedPluginsUtil.getDetachedPlugins(since);
            assertThat("Plugins have been detached since the pre-upgrade version", detachedPlugins.size(), greaterThan(1));
            assertThat("Plugins detached between the pre-upgrade version and the current version should be installed", getInstalledDetachedPlugins(r, detachedPlugins).size(), equalTo(detachedPlugins.size()));
            Plugin scriptSecurity = r.jenkins.getPlugin("script-security");
            assertThat("Script-security should be installed", scriptSecurity, notNullValue());
            assertThat("Dependencies of detached plugins should be upgraded to the required version", scriptSecurity.getWrapper().getVersionNumber(), equalTo(new VersionNumber("1.18.1")));
            assertNoFailedPlugins(r);
        });
    }

    @Issue("JENKINS-48899")
    @Test
    @LocalData
    public void upgradeFromJenkins2WithNewerPlugin() {
        // @LocalData has command-launcher 1.2 installed, which should not be downgraded to the detached version: 1.0.
        VersionNumber since = new VersionNumber("2.0");
        rr.then(( r) -> {
            List<DetachedPlugin> detachedPlugins = DetachedPluginsUtil.getDetachedPlugins(since);
            assertThat("Plugins have been detached since the pre-upgrade version", detachedPlugins.size(), greaterThan(1));
            assertThat("Plugins detached between the pre-upgrade version and the current version should be installed", getInstalledDetachedPlugins(r, detachedPlugins).size(), equalTo(detachedPlugins.size()));
            Plugin commandLauncher = r.jenkins.getPlugin("command-launcher");
            assertThat("Installed detached plugins should not be overwritten by older versions", commandLauncher.getWrapper().getVersionNumber(), equalTo(new VersionNumber("1.2")));
            assertNoFailedPlugins(r);
        });
    }

    @Test
    public void newInstallation() {
        rr.then(( r) -> {
            List<DetachedPlugin> detachedPlugins = DetachedPluginsUtil.getDetachedPlugins();
            assertThat("Detached plugins should exist", detachedPlugins, not(empty()));
            assertThat("Detached plugins should not be installed on a new instance", getInstalledDetachedPlugins(r, detachedPlugins), empty());
            assertNoFailedPlugins(r);
        });
        rr.then(( r) -> {
            List<DetachedPlugin> detachedPlugins = DetachedPluginsUtil.getDetachedPlugins();
            assertThat("Detached plugins should exist", detachedPlugins, not(empty()));
            assertThat("Detached plugins should not be installed after restarting", getInstalledDetachedPlugins(r, detachedPlugins), empty());
            assertNoFailedPlugins(r);
        });
    }
}

