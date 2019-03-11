/**
 * Copyright 2018 ThoughtWorks, Inc.
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
package com.thoughtworks.go.config;


import ArtifactType.external;
import BuildArtifactConfig.ARTIFACT_PLAN_DISPLAY_NAME;
import BuiltinArtifactConfig.DEST;
import BuiltinArtifactConfig.SRC;
import TestArtifactConfig.TEST_PLAN_DISPLAY_NAME;
import com.thoughtworks.go.config.validation.FilePathTypeValidator;
import com.thoughtworks.go.plugin.access.artifact.ArtifactMetadataStore;
import com.thoughtworks.go.plugin.api.info.PluginDescriptor;
import com.thoughtworks.go.plugin.domain.artifact.ArtifactPluginInfo;
import com.thoughtworks.go.plugin.domain.common.Metadata;
import com.thoughtworks.go.plugin.domain.common.PluginConfiguration;
import com.thoughtworks.go.security.CryptoException;
import com.thoughtworks.go.security.GoCipher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ArtifactConfigsTest {
    @Test
    public void shouldAddDuplicatedArtifactSoThatValidationKicksIn() throws Exception {
        final ArtifactConfigs artifactConfigs = new ArtifactConfigs();
        Assert.assertThat(artifactConfigs.size(), Matchers.is(0));
        artifactConfigs.add(new BuildArtifactConfig("src", "dest"));
        artifactConfigs.add(new BuildArtifactConfig("src", "dest"));
        Assert.assertThat(artifactConfigs.size(), Matchers.is(2));
    }

    @Test
    public void shouldLoadArtifactPlans() {
        HashMap<String, String> artifactPlan1 = new HashMap<>();
        artifactPlan1.put(BuildArtifactConfig.SRC, "blah");
        artifactPlan1.put(BuildArtifactConfig.DEST, "something");
        artifactPlan1.put("artifactTypeValue", TEST_PLAN_DISPLAY_NAME);
        HashMap<String, String> artifactPlan2 = new HashMap<>();
        artifactPlan2.put(BuildArtifactConfig.SRC, "blah2");
        artifactPlan2.put(BuildArtifactConfig.DEST, "something2");
        artifactPlan2.put("artifactTypeValue", ARTIFACT_PLAN_DISPLAY_NAME);
        List<HashMap> artifactPlansList = new ArrayList<>();
        artifactPlansList.add(artifactPlan1);
        artifactPlansList.add(artifactPlan2);
        ArtifactConfigs artifactConfigs = new ArtifactConfigs();
        artifactConfigs.setConfigAttributes(artifactPlansList);
        Assert.assertThat(artifactConfigs.size(), Matchers.is(2));
        TestArtifactConfig plan = new TestArtifactConfig();
        plan.setSource("blah");
        plan.setDestination("something");
        Assert.assertThat(artifactConfigs.get(0), Matchers.is(plan));
        Assert.assertThat(artifactConfigs.get(1), Matchers.is(new BuildArtifactConfig("blah2", "something2")));
    }

    @Test
    public void setConfigAttributes_shouldIgnoreEmptySourceAndDest() {
        HashMap<String, String> artifactPlan1 = new HashMap<>();
        artifactPlan1.put(BuildArtifactConfig.SRC, "blah");
        artifactPlan1.put(BuildArtifactConfig.DEST, "something");
        artifactPlan1.put("artifactTypeValue", TEST_PLAN_DISPLAY_NAME);
        HashMap<String, String> artifactPlan2 = new HashMap<>();
        artifactPlan2.put(BuildArtifactConfig.SRC, "blah2");
        artifactPlan2.put(BuildArtifactConfig.DEST, "something2");
        artifactPlan2.put("artifactTypeValue", ARTIFACT_PLAN_DISPLAY_NAME);
        HashMap<String, String> artifactPlan3 = new HashMap<>();
        artifactPlan3.put(BuildArtifactConfig.SRC, "");
        artifactPlan3.put(BuildArtifactConfig.DEST, "");
        artifactPlan3.put("artifactTypeValue", ARTIFACT_PLAN_DISPLAY_NAME);
        List<HashMap> artifactPlansList = new ArrayList<>();
        artifactPlansList.add(artifactPlan1);
        artifactPlansList.add(artifactPlan3);
        artifactPlansList.add(artifactPlan2);
        ArtifactConfigs artifactConfigs = new ArtifactConfigs();
        artifactConfigs.setConfigAttributes(artifactPlansList);
        Assert.assertThat(artifactConfigs.size(), Matchers.is(2));
        TestArtifactConfig plan = new TestArtifactConfig();
        plan.setSource("blah");
        plan.setDestination("something");
        Assert.assertThat(artifactConfigs.get(0), Matchers.is(plan));
        Assert.assertThat(artifactConfigs.get(1), Matchers.is(new BuildArtifactConfig("blah2", "something2")));
    }

    @Test
    public void setConfigAttributes_shouldSetExternalArtifactWithPlainTextValuesIfPluginIdIsProvided() {
        ArtifactPluginInfo artifactPluginInfo = Mockito.mock(ArtifactPluginInfo.class);
        PluginDescriptor pluginDescriptor = Mockito.mock(PluginDescriptor.class);
        Mockito.when(artifactPluginInfo.getDescriptor()).thenReturn(pluginDescriptor);
        Mockito.when(pluginDescriptor.id()).thenReturn("cd.go.artifact.foo");
        PluginConfiguration image = new PluginConfiguration("Image", new Metadata(true, true));
        PluginConfiguration tag = new PluginConfiguration("Tag", new Metadata(true, false));
        ArrayList<PluginConfiguration> pluginMetadata = new ArrayList<>();
        pluginMetadata.add(image);
        pluginMetadata.add(tag);
        Mockito.when(artifactPluginInfo.getArtifactConfigSettings()).thenReturn(new com.thoughtworks.go.plugin.domain.common.PluggableInstanceSettings(pluginMetadata));
        ArtifactMetadataStore.instance().setPluginInfo(artifactPluginInfo);
        HashMap<Object, Object> configurationMap1 = new HashMap<>();
        configurationMap1.put("Image", "gocd/gocd-server");
        configurationMap1.put("Tag", "v18.6.0");
        HashMap<String, Object> artifactPlan1 = new HashMap<>();
        artifactPlan1.put("artifactTypeValue", "Pluggable Artifact");
        artifactPlan1.put("id", "artifactId");
        artifactPlan1.put("storeId", "storeId");
        artifactPlan1.put("pluginId", "cd.go.artifact.foo");
        artifactPlan1.put("configuration", configurationMap1);
        List<Map> artifactPlansList = new ArrayList<>();
        artifactPlansList.add(artifactPlan1);
        ArtifactConfigs artifactConfigs = new ArtifactConfigs();
        artifactConfigs.setConfigAttributes(artifactPlansList);
        Assert.assertThat(artifactConfigs.size(), Matchers.is(1));
        PluggableArtifactConfig artifactConfig = ((PluggableArtifactConfig) (artifactConfigs.get(0)));
        Assert.assertThat(artifactConfig.getArtifactType(), Matchers.is(external));
        Assert.assertThat(artifactConfig.getId(), Matchers.is("artifactId"));
        Assert.assertThat(artifactConfig.getStoreId(), Matchers.is("storeId"));
        Assert.assertThat(artifactConfig.getConfiguration().getProperty("Image").isSecure(), Matchers.is(false));
    }

    @Test
    public void setConfigAttributes_shouldSetConfigurationAsIsIfPluginIdIsBlank() throws CryptoException {
        HashMap<Object, Object> imageMap = new HashMap<>();
        imageMap.put("value", new GoCipher().encrypt("some-encrypted-value"));
        imageMap.put("isSecure", "true");
        HashMap<Object, Object> tagMap = new HashMap<>();
        tagMap.put("value", "18.6.0");
        tagMap.put("isSecure", "false");
        HashMap<Object, Object> configurationMap1 = new HashMap<>();
        configurationMap1.put("Image", imageMap);
        configurationMap1.put("Tag", tagMap);
        HashMap<String, Object> artifactPlan1 = new HashMap<>();
        artifactPlan1.put("artifactTypeValue", "Pluggable Artifact");
        artifactPlan1.put("id", "artifactId");
        artifactPlan1.put("storeId", "storeId");
        artifactPlan1.put("pluginId", "");
        artifactPlan1.put("configuration", configurationMap1);
        List<Map> artifactPlansList = new ArrayList<>();
        artifactPlansList.add(artifactPlan1);
        ArtifactConfigs artifactConfigs = new ArtifactConfigs();
        artifactConfigs.setConfigAttributes(artifactPlansList);
        Assert.assertThat(artifactConfigs.size(), Matchers.is(1));
        PluggableArtifactConfig artifactConfig = ((PluggableArtifactConfig) (artifactConfigs.get(0)));
        Assert.assertThat(artifactConfig.getArtifactType(), Matchers.is(external));
        Assert.assertThat(artifactConfig.getId(), Matchers.is("artifactId"));
        Assert.assertThat(artifactConfig.getStoreId(), Matchers.is("storeId"));
        Assert.assertThat(artifactConfig.getConfiguration().getProperty("Image").getValue(), Matchers.is("some-encrypted-value"));
        Assert.assertThat(artifactConfig.getConfiguration().getProperty("Tag").getValue(), Matchers.is("18.6.0"));
    }

    @Test
    public void shouldClearAllArtifactsWhenTheMapIsNull() {
        ArtifactConfigs artifactConfigs = new ArtifactConfigs();
        artifactConfigs.add(new BuildArtifactConfig("src", "dest"));
        artifactConfigs.setConfigAttributes(null);
        Assert.assertThat(artifactConfigs.size(), Matchers.is(0));
    }

    @Test
    public void shouldValidateTree() {
        ArtifactConfigs artifactConfigs = new ArtifactConfigs();
        artifactConfigs.add(new BuildArtifactConfig("src", "dest"));
        artifactConfigs.add(new BuildArtifactConfig("src", "dest"));
        artifactConfigs.add(new BuildArtifactConfig("src", "../a"));
        artifactConfigs.validateTree(null);
        Assert.assertThat(artifactConfigs.get(0).errors().on(DEST), Matchers.is("Duplicate artifacts defined."));
        Assert.assertThat(artifactConfigs.get(0).errors().on(SRC), Matchers.is("Duplicate artifacts defined."));
        Assert.assertThat(artifactConfigs.get(1).errors().on(DEST), Matchers.is("Duplicate artifacts defined."));
        Assert.assertThat(artifactConfigs.get(1).errors().on(SRC), Matchers.is("Duplicate artifacts defined."));
        Assert.assertThat(artifactConfigs.get(2).errors().on(DEST), Matchers.is(("Invalid destination path. Destination path should match the pattern " + (FilePathTypeValidator.PATH_PATTERN))));
    }

    @Test
    public void shouldErrorOutWhenDuplicateArtifactConfigExists() {
        final ArtifactConfigs artifactConfigs = new ArtifactConfigs(new BuildArtifactConfig("src", "dest"));
        artifactConfigs.add(new BuildArtifactConfig("src", "dest"));
        artifactConfigs.add(new BuildArtifactConfig("src", "dest"));
        artifactConfigs.validate(null);
        Assert.assertFalse(artifactConfigs.get(0).errors().isEmpty());
        Assert.assertThat(artifactConfigs.get(0).errors().on(SRC), Matchers.is("Duplicate artifacts defined."));
        Assert.assertThat(artifactConfigs.get(0).errors().on(DEST), Matchers.is("Duplicate artifacts defined."));
        Assert.assertFalse(artifactConfigs.get(1).errors().isEmpty());
        Assert.assertThat(artifactConfigs.get(1).errors().on(SRC), Matchers.is("Duplicate artifacts defined."));
        Assert.assertThat(artifactConfigs.get(1).errors().on(DEST), Matchers.is("Duplicate artifacts defined."));
        Assert.assertFalse(artifactConfigs.get(2).errors().isEmpty());
        Assert.assertThat(artifactConfigs.get(2).errors().on(SRC), Matchers.is("Duplicate artifacts defined."));
        Assert.assertThat(artifactConfigs.get(2).errors().on(DEST), Matchers.is("Duplicate artifacts defined."));
    }

    @Test
    public void getArtifactConfigs_shouldReturnBuiltinArtifactConfigs() {
        ArtifactConfigs allConfigs = new ArtifactConfigs();
        allConfigs.add(new BuildArtifactConfig("src", "dest"));
        allConfigs.add(new BuildArtifactConfig("java", null));
        allConfigs.add(new PluggableArtifactConfig("s3", "cd.go.s3"));
        allConfigs.add(new PluggableArtifactConfig("docker", "cd.go.docker"));
        final List<BuiltinArtifactConfig> artifactConfigs = allConfigs.getBuiltInArtifactConfigs();
        Assert.assertThat(artifactConfigs, Matchers.hasSize(2));
        Assert.assertThat(artifactConfigs, Matchers.containsInAnyOrder(new BuildArtifactConfig("src", "dest"), new BuildArtifactConfig("java", null)));
    }

    @Test
    public void getPluggableArtifactConfigs_shouldReturnPluggableArtifactConfigs() {
        ArtifactConfigs allConfigs = new ArtifactConfigs();
        allConfigs.add(new BuildArtifactConfig("src", "dest"));
        allConfigs.add(new BuildArtifactConfig("java", null));
        allConfigs.add(new PluggableArtifactConfig("s3", "cd.go.s3"));
        allConfigs.add(new PluggableArtifactConfig("docker", "cd.go.docker"));
        final List<PluggableArtifactConfig> artifactConfigs = allConfigs.getPluggableArtifactConfigs();
        Assert.assertThat(artifactConfigs, Matchers.hasSize(2));
        Assert.assertThat(artifactConfigs, Matchers.containsInAnyOrder(new PluggableArtifactConfig("s3", "cd.go.s3"), new PluggableArtifactConfig("docker", "cd.go.docker")));
    }

    @Test
    public void findByArtifactId_shouldReturnPluggableArtifactConfigs() {
        ArtifactConfigs allConfigs = new ArtifactConfigs();
        allConfigs.add(new PluggableArtifactConfig("s3", "cd.go.s3"));
        allConfigs.add(new PluggableArtifactConfig("docker", "cd.go.docker"));
        final PluggableArtifactConfig s3 = allConfigs.findByArtifactId("s3");
        Assert.assertThat(s3, Matchers.is(new PluggableArtifactConfig("s3", "cd.go.s3")));
    }

    @Test
    public void findByArtifactId_shouldReturnNullWhenPluggableArtifactConfigNotExistWithGivenId() {
        ArtifactConfigs allConfigs = new ArtifactConfigs();
        allConfigs.add(new PluggableArtifactConfig("s3", "cd.go.s3"));
        allConfigs.add(new PluggableArtifactConfig("docker", "cd.go.docker"));
        final PluggableArtifactConfig s3 = allConfigs.findByArtifactId("foo");
        Assert.assertNull(s3);
    }
}

