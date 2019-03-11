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


import Property.DISPLAY_NAME;
import Property.DISPLAY_ORDER;
import Property.PART_OF_IDENTITY;
import Property.REQUIRED;
import Property.SECURE;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class SCMPropertyTest {
    @Test
    public void validateSCMPropertyDefaults() throws Exception {
        SCMProperty scmProperty = new SCMProperty("Test-Property");
        Assert.assertThat(scmProperty.getOptions().size(), Matchers.is(5));
        Assert.assertThat(scmProperty.getOption(REQUIRED), Matchers.is(true));
        Assert.assertThat(scmProperty.getOption(PART_OF_IDENTITY), Matchers.is(true));
        Assert.assertThat(scmProperty.getOption(SECURE), Matchers.is(false));
        Assert.assertThat(scmProperty.getOption(DISPLAY_NAME), Matchers.is(""));
        Assert.assertThat(scmProperty.getOption(DISPLAY_ORDER), Matchers.is(0));
        scmProperty = new SCMProperty("Test-Property", "Dummy Value");
        Assert.assertThat(scmProperty.getOptions().size(), Matchers.is(5));
        Assert.assertThat(scmProperty.getOption(REQUIRED), Matchers.is(true));
        Assert.assertThat(scmProperty.getOption(PART_OF_IDENTITY), Matchers.is(true));
        Assert.assertThat(scmProperty.getOption(SECURE), Matchers.is(false));
        Assert.assertThat(scmProperty.getOption(DISPLAY_NAME), Matchers.is(""));
        Assert.assertThat(scmProperty.getOption(DISPLAY_ORDER), Matchers.is(0));
    }
}

