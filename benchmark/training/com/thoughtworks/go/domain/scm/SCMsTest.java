/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.domain.scm;


import SCM.NAME;
import SCM.SCM_ID;
import com.thoughtworks.go.domain.config.Configuration;
import com.thoughtworks.go.domain.config.PluginConfiguration;
import com.thoughtworks.go.domain.packagerepository.ConfigurationPropertyMother;
import com.thoughtworks.go.plugin.access.scm.SCMMetadataStore;
import com.thoughtworks.go.plugin.access.scm.SCMProperty;
import com.thoughtworks.go.plugin.access.scm.SCMPropertyConfiguration;
import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class SCMsTest {
    @Test
    public void shouldCheckEqualityOfSCMs() {
        SCM scm = new SCM();
        SCMs scms = new SCMs(scm);
        Assert.assertThat(scms, Matchers.is(new SCMs(scm)));
    }

    @Test
    public void shouldFindSCMGivenTheSCMId() throws Exception {
        SCM scm1 = SCMMother.create("id1");
        SCM scm2 = SCMMother.create("id2");
        SCMs scms = new SCMs(scm1, scm2);
        Assert.assertThat(scms.find("id2"), Matchers.is(scm2));
    }

    @Test
    public void shouldReturnNullIfNoMatchingSCMFound() throws Exception {
        Assert.assertThat(new SCMs().find("not-found"), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldRemoveSCMById() throws Exception {
        SCM scm1 = SCMMother.create("id1");
        SCM scm2 = SCMMother.create("id2");
        SCMs scms = new SCMs(scm1, scm2);
        scms.removeSCM("id1");
        Assert.assertThat(scms, Matchers.contains(scm2));
    }

    @Test
    public void shouldThrowRuntimeExceptionWhenTryingToRemoveSCMIdWhichIsNotPresent() throws Exception {
        SCMs scms = new SCMs();
        try {
            scms.removeSCM("id1");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.is(String.format("Could not find SCM with id '%s'", "id1")));
        }
    }

    @Test
    public void shouldValidateForCaseInsensitiveNameAndIdUniqueness() {
        SCM duplicate1 = SCMMother.create("scm-id1");
        SCM duplicate2 = SCMMother.create("SCM-ID1");
        SCM unique = SCMMother.create("unique");
        SCMs scms = new SCMs(duplicate1, duplicate2, unique);
        scms.validate(null);
        Assert.assertThat(duplicate1.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(duplicate1.errors().getAllOn(NAME).contains(String.format("Cannot save SCM, found multiple SCMs called '%s'. SCM names are case-insensitive and must be unique.", duplicate1.getName())), Matchers.is(true));
        Assert.assertThat(duplicate2.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(duplicate2.errors().getAllOn(NAME).contains(String.format("Cannot save SCM, found multiple SCMs called '%s'. SCM names are case-insensitive and must be unique.", duplicate2.getName())), Matchers.is(true));
        Assert.assertThat(unique.errors().getAllOn(NAME), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldFailValidationIfMaterialWithDuplicateFingerprintIsFound() {
        String pluginId = "plugin-id";
        SCMPropertyConfiguration scmConfiguration = new SCMPropertyConfiguration();
        scmConfiguration.add(new SCMProperty("k1"));
        scmConfiguration.add(new SCMProperty("k2").with(REQUIRED, false).with(PART_OF_IDENTITY, false));
        SCMMetadataStore.getInstance().addMetadataFor(pluginId, new com.thoughtworks.go.plugin.access.scm.SCMConfigurations(scmConfiguration), null);
        SCM scm1 = SCMMother.create("1", "scm1", pluginId, "1.0", new Configuration(ConfigurationPropertyMother.create("k1", false, "v1")));
        SCM scm2 = SCMMother.create("2", "scm2", pluginId, "1.0", new Configuration(ConfigurationPropertyMother.create("k1", false, "v2")));
        SCM scm3 = SCMMother.create("3", "scm3", pluginId, "1.0", new Configuration(ConfigurationPropertyMother.create("k1", false, "v1")));
        SCM scm4 = SCMMother.create("4", "scm4", pluginId, "1.0", new Configuration(ConfigurationPropertyMother.create("k1", false, "V1")));
        SCM scm5 = SCMMother.create("5", "scm5", pluginId, "1.0", new Configuration(ConfigurationPropertyMother.create("k1", false, "v1"), ConfigurationPropertyMother.create("k2", false, "v2")));
        SCMs scms = new SCMs(scm1, scm2, scm3, scm4, scm5);
        scms.validate(null);
        Assert.assertThat(scm2.getFingerprint().equals(scm1.getFingerprint()), Matchers.is(false));
        Assert.assertThat(scm3.getFingerprint().equals(scm1.getFingerprint()), Matchers.is(true));
        Assert.assertThat(scm4.getFingerprint().equals(scm1.getFingerprint()), Matchers.is(false));
        Assert.assertThat(scm5.getFingerprint().equals(scm1.getFingerprint()), Matchers.is(true));
        String expectedErrorMessage = "Cannot save SCM, found duplicate SCMs. scm1, scm3, scm5";
        Assert.assertThat(scm1.errors().getAllOn(SCM_ID), Matchers.is(Arrays.asList(expectedErrorMessage)));
        Assert.assertThat(scm2.errors().getAllOn(SCM_ID), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(scm3.errors().getAllOn(SCM_ID), Matchers.is(Arrays.asList(expectedErrorMessage)));
        Assert.assertThat(scm4.errors().getAllOn(SCM_ID), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(scm5.errors().getAllOn(SCM_ID), Matchers.is(Arrays.asList(expectedErrorMessage)));
    }

    @Test
    public void shouldFindFunctionallyDuplicateSCMs() {
        Configuration config = new Configuration();
        config.addNewConfigurationWithValue("url", "url", false);
        PluginConfiguration pluginConfig = new PluginConfiguration("plugin_id", "1.0");
        SCM scm1 = new SCM("scmid", pluginConfig, config);
        scm1.setName("noName");
        SCMs scms = new SCMs(scm1);
        SCM scm2 = new SCM("scmid", new PluginConfiguration(), new Configuration());
        SCM scm3 = new SCM("id", pluginConfig, config);
        Assert.assertThat(scms.findDuplicate(scm2), Matchers.is(scm1));
        Assert.assertThat(scms.findDuplicate(scm3), Matchers.is(scm1));
    }

    @Test
    public void shouldDetermineIfAddingAnSCMWouldCreateDuplication() {
        Configuration config = new Configuration();
        config.addNewConfigurationWithValue("url", "url", false);
        PluginConfiguration pluginConfig = new PluginConfiguration("plugin_id", "1.0");
        SCM scm1 = new SCM("scmid", pluginConfig, config);
        scm1.setName("noName");
        SCMs scms = new SCMs(scm1);
        SCM scm2 = new SCM("scmid", new PluginConfiguration(), new Configuration());
        SCM scm3 = new SCM("id", pluginConfig, config);
        SCM scm4 = new SCM("something", new PluginConfiguration(), new Configuration());
        scm4.setName("noName");
        SCM scm5 = new SCM("arbitrary", new PluginConfiguration(), new Configuration());
        scm5.setName("aRandomName");
        Assert.assertThat(scms.canAdd(scm2), Matchers.is(false));
        Assert.assertThat(scms.canAdd(scm3), Matchers.is(false));
        Assert.assertThat(scms.canAdd(scm4), Matchers.is(false));
        Assert.assertThat(scms.canAdd(scm5), Matchers.is(true));
    }
}

