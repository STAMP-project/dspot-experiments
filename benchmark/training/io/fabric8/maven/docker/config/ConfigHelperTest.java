/**
 * Copyright 2016 Roland Huss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.maven.docker.config;


import ConfigHelper.EXTERNALCONFIG_ACTIVATION_PROPERTY;
import ConfigHelper.NameFormatter.IDENTITY;
import io.fabric8.maven.docker.util.AnsiLogger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Test;

import static ConfigHelper.EXTERNALCONFIG_ACTIVATION_PROPERTY;


/**
 *
 *
 * @author roland
 * @since 17/05/16
 */
public class ConfigHelperTest {
    private boolean resolverCalled;

    private boolean customizerCalled;

    @Test
    public void noName() throws Exception {
        try {
            List<ImageConfiguration> configs = Arrays.asList(new ImageConfiguration.Builder().build());
            ConfigHelper.resolveImages(null, configs, createResolver(), null, createCustomizer());
            Assert.fail();
        } catch (IllegalArgumentException exp) {
            Assert.assertTrue(exp.getMessage().contains("name"));
        }
    }

    @Test
    public void externalPropertyActivation() throws MojoFailureException {
        MavenProject project = new MavenProject();
        project.getProperties().put(EXTERNALCONFIG_ACTIVATION_PROPERTY, "anything");
        List<ImageConfiguration> images = Arrays.asList(new ImageConfiguration.Builder().name("test").build());
        ConfigHelper.validateExternalPropertyActivation(project, images);
        images = Arrays.asList(new ImageConfiguration.Builder().name("test").build(), new ImageConfiguration.Builder().name("test2").build());
        try {
            ConfigHelper.validateExternalPropertyActivation(project, images);
            Assert.fail();
        } catch (MojoFailureException ex) {
            Assert.assertTrue(ex.getMessage().contains((("Cannot use property " + (EXTERNALCONFIG_ACTIVATION_PROPERTY)) + " on projects with multiple images")));
        }
        // When one of the images are configured externally from other source, it is OK with two images.
        Map<String, String> externalConfig = new HashMap<>();
        images.get(0).setExternalConfiguration(externalConfig);
        externalConfig.put("type", "othermagic");
        ConfigHelper.validateExternalPropertyActivation(project, images);
        // Or if prefix is set explicitly
        externalConfig.put("type", "properties");
        externalConfig.put("prefix", "docker");
        ConfigHelper.validateExternalPropertyActivation(project, images);
        // But with default prefix it fails
        externalConfig.remove("prefix");
        try {
            ConfigHelper.validateExternalPropertyActivation(project, images);
            Assert.fail();
        } catch (MojoFailureException ex) {
            Assert.assertTrue(ex.getMessage().contains((("Cannot use property " + (EXTERNALCONFIG_ACTIVATION_PROPERTY)) + " on projects with multiple images")));
        }
        // With no external properly, it works.
        project.getProperties().clear();
        ConfigHelper.validateExternalPropertyActivation(project, images);
        // And if explicitly set to "skip" it works too.
        project.getProperties().put(EXTERNALCONFIG_ACTIVATION_PROPERTY, "skip");
        ConfigHelper.validateExternalPropertyActivation(project, images);
    }

    @Test
    public void simple() throws Exception {
        List<ImageConfiguration> configs = Arrays.asList(new ImageConfiguration.Builder().name("test").build());
        List<ImageConfiguration> result = ConfigHelper.resolveImages(null, configs, createResolver(), null, createCustomizer());
        Assert.assertEquals(1, result.size());
        Assert.assertTrue(resolverCalled);
        Assert.assertTrue(customizerCalled);
    }

    @Test
    public void registry() throws Exception {
        List<ImageConfiguration> configs = Arrays.asList(new ImageConfiguration.Builder().registry("docker.io").name("test").build());
        List<ImageConfiguration> result = ConfigHelper.resolveImages(null, configs, createResolver(), null, createCustomizer());
        Assert.assertEquals(1, result.size());
        Assert.assertTrue(resolverCalled);
        Assert.assertTrue(customizerCalled);
        Assert.assertEquals("docker.io", configs.get(0).getRegistry());
    }

    @Test
    public void filter() throws Exception {
        List<ImageConfiguration> configs = Arrays.asList(new ImageConfiguration.Builder().name("test").build());
        ConfigHelperTest.CatchingLog logCatcher = new ConfigHelperTest.CatchingLog();
        List<ImageConfiguration> result = ConfigHelper.resolveImages(new AnsiLogger(logCatcher, true, true), configs, createResolver(), "bla", createCustomizer());
        Assert.assertEquals(0, result.size());
        Assert.assertTrue(resolverCalled);
        Assert.assertTrue(customizerCalled);
        Assert.assertTrue(logCatcher.getWarnMessage().contains("test"));
        Assert.assertTrue(logCatcher.getWarnMessage().contains("bla"));
    }

    @Test
    public void initAndValidate() throws Exception {
        List<ImageConfiguration> configs = Arrays.asList(new ImageConfiguration.Builder().name("test").build());
        String api = ConfigHelper.initAndValidate(configs, "v1.16", IDENTITY, null);
        Assert.assertEquals("v1.16", api);
    }

    private class CatchingLog extends SystemStreamLog {
        private String warnMessage;

        @Override
        public void warn(CharSequence content) {
            this.warnMessage = content.toString();
            super.warn(content);
        }

        void reset() {
            warnMessage = null;
        }

        public String getWarnMessage() {
            return warnMessage;
        }
    }
}

