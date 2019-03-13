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


import Property.SECURE;
import SCMConfiguration.DISPLAY_NAME;
import SCMConfiguration.DISPLAY_ORDER;
import SCMConfiguration.PART_OF_IDENTITY;
import SCMConfiguration.REQUIRED;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class SCMConfigurationsTest {
    @Test
    public void shouldGetAllSCMsSortedByDisplayOrder() throws Exception {
        SCMConfiguration c1 = new SCMConfiguration("k1").with(DISPLAY_ORDER, 2);
        SCMConfiguration c2 = new SCMConfiguration("k2").with(DISPLAY_ORDER, 0);
        SCMConfiguration c3 = new SCMConfiguration("k3").with(DISPLAY_ORDER, 1);
        SCMConfigurations scmConfigurations = new SCMConfigurations();
        scmConfigurations.add(c1);
        scmConfigurations.add(c2);
        scmConfigurations.add(c3);
        List<SCMConfiguration> scmConfigurationList = scmConfigurations.list();
        Assert.assertThat(scmConfigurationList.get(0), Matchers.is(c2));
        Assert.assertThat(scmConfigurationList.get(1), Matchers.is(c3));
        Assert.assertThat(scmConfigurationList.get(2), Matchers.is(c1));
    }

    @Test
    public void shouldConstructSCMConfiguration() throws Exception {
        SCMPropertyConfiguration scmPropertyConfiguration = new SCMPropertyConfiguration();
        scmPropertyConfiguration.add(new SCMProperty("k1", "v1").with(SECURE, Boolean.TRUE));
        SCMConfigurations scmConfigurations = new SCMConfigurations(scmPropertyConfiguration);
        Assert.assertThat(scmConfigurations.list().size(), Matchers.is(1));
        SCMConfiguration scmConfiguration = scmConfigurations.list().get(0);
        Assert.assertThat(scmConfiguration.getKey(), Matchers.is("k1"));
        Assert.assertThat(scmConfiguration.getValue(), Matchers.is("v1"));
        Assert.assertThat(scmConfiguration.getOption(REQUIRED), Matchers.is(true));
        Assert.assertThat(scmConfiguration.getOption(PART_OF_IDENTITY), Matchers.is(true));
        Assert.assertThat(scmConfiguration.getOption(SCMConfiguration.SECURE), Matchers.is(true));
        Assert.assertThat(scmConfiguration.getOption(DISPLAY_NAME), Matchers.is(""));
        Assert.assertThat(scmConfiguration.getOption(DISPLAY_ORDER), Matchers.is(0));
    }
}

