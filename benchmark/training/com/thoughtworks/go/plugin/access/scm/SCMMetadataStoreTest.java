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
package com.thoughtworks.go.plugin.access.scm;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class SCMMetadataStoreTest {
    @Test
    public void shouldPopulateDataCorrectly() throws Exception {
        SCMConfigurations scmConfigurations = new SCMConfigurations();
        SCMView scmView = createSCMView("display-value", "template");
        SCMMetadataStore.getInstance().addMetadataFor("plugin-id", scmConfigurations, scmView);
        Assert.assertThat(SCMMetadataStore.getInstance().getConfigurationMetadata("plugin-id"), Matchers.is(scmConfigurations));
        Assert.assertThat(SCMMetadataStore.getInstance().getViewMetadata("plugin-id"), Matchers.is(scmView));
        Assert.assertThat(SCMMetadataStore.getInstance().displayValue("plugin-id"), Matchers.is("display-value"));
        Assert.assertThat(SCMMetadataStore.getInstance().template("plugin-id"), Matchers.is("template"));
        Assert.assertThat(SCMMetadataStore.getInstance().getConfigurationMetadata("some-plugin-which-does-not-exist"), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(SCMMetadataStore.getInstance().getViewMetadata("some-plugin-which-does-not-exist"), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(SCMMetadataStore.getInstance().displayValue("some-plugin-which-does-not-exist"), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(SCMMetadataStore.getInstance().template("some-plugin-which-does-not-exist"), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldBeAbleToCheckIfPluginExists() throws Exception {
        SCMConfigurations scmConfigurations = new SCMConfigurations();
        SCMView scmView = createSCMView(null, null);
        SCMMetadataStore.getInstance().addMetadataFor("plugin-id", scmConfigurations, scmView);
        Assert.assertThat(SCMMetadataStore.getInstance().hasPlugin("plugin-id"), Matchers.is(true));
        Assert.assertThat(SCMMetadataStore.getInstance().hasPlugin("some-plugin-which-does-not-exist"), Matchers.is(false));
    }
}

