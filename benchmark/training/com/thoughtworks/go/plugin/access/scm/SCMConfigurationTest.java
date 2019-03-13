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


import SCMConfiguration.DISPLAY_NAME;
import SCMConfiguration.DISPLAY_ORDER;
import SCMConfiguration.REQUIRED;
import SCMConfiguration.SECURE;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class SCMConfigurationTest {
    @Test
    public void shouldGetOptionIfAvailable() {
        SCMConfiguration scmConfiguration = new SCMConfiguration("key");
        scmConfiguration.with(REQUIRED, true);
        Assert.assertThat(scmConfiguration.hasOption(REQUIRED), Matchers.is(true));
        Assert.assertThat(scmConfiguration.hasOption(SECURE), Matchers.is(false));
    }

    @Test
    public void shouldGetOptionValue() {
        SCMConfiguration scmConfiguration = new SCMConfiguration("key");
        scmConfiguration.with(DISPLAY_NAME, "some display name");
        scmConfiguration.with(DISPLAY_ORDER, 3);
        Assert.assertThat(scmConfiguration.getOption(DISPLAY_NAME), Matchers.is("some display name"));
        Assert.assertThat(scmConfiguration.getOption(DISPLAY_ORDER), Matchers.is(3));
    }

    @Test
    public void shouldSortByDisplayOrder() throws Exception {
        SCMConfiguration p1 = new SCMConfiguration("k1").with(DISPLAY_ORDER, 1);
        SCMConfiguration p2 = new SCMConfiguration("k2").with(DISPLAY_ORDER, 3);
        Assert.assertThat(p2.compareTo(p1), Matchers.is(2));
    }
}

