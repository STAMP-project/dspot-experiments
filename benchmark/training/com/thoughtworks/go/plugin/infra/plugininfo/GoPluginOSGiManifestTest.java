/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.plugin.infra.plugininfo;


import com.googlecode.junit.ext.checkers.OSChecker;
import com.thoughtworks.go.plugin.activation.DefaultGoPluginActivator;
import java.io.File;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.osgi.framework.Constants;


public class GoPluginOSGiManifestTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static OSChecker WINDOWS = new OSChecker(OSChecker.WINDOWS);

    private File tmpDir;

    private File manifestFile;

    private File bundleLocation;

    private File bundleDependencyDir;

    private GoPluginOSGiManifestGenerator goPluginOSGiManifestGenerator;

    @Test
    public void shouldCreateABundleManifestFromTheGivenPluginDescriptor() throws Exception {
        if (GoPluginOSGiManifestTest.WINDOWS.satisfy()) {
            return;
        }
        Assert.assertThat(valueFor(Constants.BUNDLE_SYMBOLICNAME), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(valueFor(Constants.BUNDLE_CLASSPATH), Matchers.is(Matchers.nullValue()));
        GoPluginDescriptor descriptor = GoPluginDescriptor.usingId("pluginId", "some-plugin.jar", bundleLocation, true);
        GoPluginOSGiManifest manifest = new GoPluginOSGiManifest(descriptor);
        manifest.update();
        Assert.assertThat(valueFor(Constants.BUNDLE_SYMBOLICNAME), Matchers.is("pluginId"));
        Assert.assertThat(valueFor(Constants.BUNDLE_ACTIVATOR), Matchers.is(DefaultGoPluginActivator.class.getCanonicalName()));
        Assert.assertThat(valueFor(Constants.BUNDLE_CLASSPATH), Matchers.is("lib/go-plugin-activator.jar,.,lib/dependency.jar"));
        Assert.assertThat(descriptor.bundleSymbolicName(), Matchers.is("pluginId"));
        Assert.assertThat(descriptor.bundleClassPath(), Matchers.is("lib/go-plugin-activator.jar,.,lib/dependency.jar"));
        Assert.assertThat(descriptor.bundleActivator(), Matchers.is(DefaultGoPluginActivator.class.getCanonicalName()));
    }

    @Test
    public void shouldAddGoPluginActivatorJarToDependenciesOnlyOnceAtTheBeginning() throws Exception {
        if (GoPluginOSGiManifestTest.WINDOWS.satisfy()) {
            return;
        }
        FileUtils.writeStringToFile(new File(bundleLocation, "lib/go-plugin-activator.jar"), "Some data", StandardCharsets.UTF_8);
        GoPluginDescriptor descriptor = GoPluginDescriptor.usingId("pluginId", "some-plugin.jar", bundleLocation, true);
        goPluginOSGiManifestGenerator.updateManifestOf(descriptor);
        String classpathEntry = valueFor(Constants.BUNDLE_CLASSPATH);
        Assert.assertThat(classpathEntry, Matchers.is("lib/go-plugin-activator.jar,.,lib/dependency.jar"));
    }

    @Test
    public void shouldCreateManifestWithProperClassPathForAllDependencyJarsInPluginDependenciesDirectory() throws Exception {
        if (GoPluginOSGiManifestTest.WINDOWS.satisfy()) {
            return;
        }
        FileUtils.writeStringToFile(new File(bundleLocation, "lib/dependency-1.jar"), "Some data", StandardCharsets.UTF_8);
        FileUtils.writeStringToFile(new File(bundleLocation, "lib/dependency-2.jar"), "Some data", StandardCharsets.UTF_8);
        FileUtils.writeStringToFile(new File(bundleLocation, "lib/dependency-3.jar"), "Some data", StandardCharsets.UTF_8);
        GoPluginDescriptor descriptor = GoPluginDescriptor.usingId("pluginId", "some-plugin.jar", bundleLocation, true);
        goPluginOSGiManifestGenerator.updateManifestOf(descriptor);
        Assert.assertThat(valueFor(Constants.BUNDLE_SYMBOLICNAME), Matchers.is("pluginId"));
        String classpathEntry = valueFor(Constants.BUNDLE_CLASSPATH);
        Assert.assertThat(classpathEntry, Matchers.startsWith("lib/go-plugin-activator.jar,.,"));
        Assert.assertThat(classpathEntry, Matchers.containsString(",lib/dependency.jar"));
        Assert.assertThat(classpathEntry, Matchers.containsString(",lib/dependency-1.jar"));
        Assert.assertThat(classpathEntry, Matchers.containsString(",lib/dependency-2.jar"));
        Assert.assertThat(classpathEntry, Matchers.containsString(",lib/dependency-3.jar"));
    }

    @Test
    public void shouldCreateManifestWithProperClassPathWhenDependencyDirDoesNotExist() throws Exception {
        if (GoPluginOSGiManifestTest.WINDOWS.satisfy()) {
            return;
        }
        FileUtils.deleteDirectory(bundleDependencyDir);
        GoPluginDescriptor descriptor = GoPluginDescriptor.usingId("pluginId", "some-plugin.jar", bundleLocation, true);
        goPluginOSGiManifestGenerator.updateManifestOf(descriptor);
        Assert.assertThat(valueFor(Constants.BUNDLE_CLASSPATH), Matchers.is("lib/go-plugin-activator.jar,."));
    }

    @Test
    public void shouldMarkThePluginInvalidIfItsManifestAlreadyContainsSymbolicName() throws Exception {
        if (GoPluginOSGiManifestTest.WINDOWS.satisfy()) {
            return;
        }
        addHeaderToManifest(Constants.BUNDLE_SYMBOLICNAME, "Dummy Value");
        Assert.assertThat(valueFor(Constants.BUNDLE_SYMBOLICNAME), Matchers.is(Matchers.not(Matchers.nullValue())));
        Assert.assertThat(valueFor(Constants.BUNDLE_CLASSPATH), Matchers.is(Matchers.nullValue()));
        GoPluginDescriptor descriptor = GoPluginDescriptor.usingId("pluginId", "some-plugin.jar", bundleLocation, true);
        goPluginOSGiManifestGenerator.updateManifestOf(descriptor);
        Assert.assertThat(valueFor(Constants.BUNDLE_SYMBOLICNAME), Matchers.is("Dummy Value"));
        Assert.assertThat(descriptor.isInvalid(), Matchers.is(true));
    }

    @Test
    public void shouldOverrideTheBundleClassPathInTheManifestIfItAlreadyHasIt() throws Exception {
        if (GoPluginOSGiManifestTest.WINDOWS.satisfy()) {
            return;
        }
        addHeaderToManifest(Constants.BUNDLE_CLASSPATH, "Dummy Value");
        Assert.assertThat(valueFor(Constants.BUNDLE_SYMBOLICNAME), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(valueFor(Constants.BUNDLE_CLASSPATH), Matchers.is(Matchers.not(Matchers.nullValue())));
        GoPluginDescriptor descriptor = GoPluginDescriptor.usingId("pluginId", "some-plugin.jar", bundleLocation, true);
        goPluginOSGiManifestGenerator.updateManifestOf(descriptor);
        Assert.assertThat(valueFor(Constants.BUNDLE_CLASSPATH), Matchers.is("lib/go-plugin-activator.jar,.,lib/dependency.jar"));
        Assert.assertThat(descriptor.isInvalid(), Matchers.is(false));
    }

    @Test
    public void shouldOverrideTheBundleActivatorInTheManifestIfItAlreadyHasIt() throws Exception {
        if (GoPluginOSGiManifestTest.WINDOWS.satisfy()) {
            return;
        }
        addHeaderToManifest(Constants.BUNDLE_ACTIVATOR, "Dummy Value");
        Assert.assertThat(valueFor(Constants.BUNDLE_ACTIVATOR), Matchers.is(Matchers.not(Matchers.nullValue())));
        GoPluginDescriptor descriptor = GoPluginDescriptor.usingId("pluginId", "some-plugin.jar", bundleLocation, true);
        goPluginOSGiManifestGenerator.updateManifestOf(descriptor);
        Assert.assertThat(valueFor(Constants.BUNDLE_ACTIVATOR), Matchers.is(DefaultGoPluginActivator.class.getCanonicalName()));
        Assert.assertThat(descriptor.isInvalid(), Matchers.is(false));
    }

    @Test
    public void shouldCreateManifestWithProperClassPathWhenDependencyDirIsEmpty() throws Exception {
        if (GoPluginOSGiManifestTest.WINDOWS.satisfy()) {
            return;
        }
        FileUtils.deleteQuietly(new File(bundleLocation, "lib/dependency.jar"));
        Assert.assertThat(bundleDependencyDir.listFiles().length, Matchers.is(0));
        GoPluginDescriptor descriptor = GoPluginDescriptor.usingId("pluginId", "some-plugin.jar", bundleLocation, true);
        goPluginOSGiManifestGenerator.updateManifestOf(descriptor);
        Assert.assertThat(valueFor(Constants.BUNDLE_CLASSPATH), Matchers.is("lib/go-plugin-activator.jar,."));
    }

    @Test
    public void manifestCreatorShouldUpdateTheGoPluginManifest() throws Exception {
        if (GoPluginOSGiManifestTest.WINDOWS.satisfy()) {
            return;
        }
        Assert.assertThat(valueFor(Constants.BUNDLE_SYMBOLICNAME), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(valueFor(Constants.BUNDLE_CLASSPATH), Matchers.is(Matchers.nullValue()));
        GoPluginDescriptor descriptor = GoPluginDescriptor.usingId("pluginId", "some-plugin.jar", bundleLocation, true);
        goPluginOSGiManifestGenerator.updateManifestOf(descriptor);
        Assert.assertThat(valueFor(Constants.BUNDLE_SYMBOLICNAME), Matchers.is("pluginId"));
        Assert.assertThat(valueFor(Constants.BUNDLE_CLASSPATH), Matchers.is("lib/go-plugin-activator.jar,.,lib/dependency.jar"));
    }
}

