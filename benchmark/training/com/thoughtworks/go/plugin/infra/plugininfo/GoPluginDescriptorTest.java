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
package com.thoughtworks.go.plugin.infra.plugininfo;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class GoPluginDescriptorTest {
    @Test
    public void shouldMatchValidOSesAgainstCurrentOS() throws Exception {
        Assert.assertThat(descriptorWithTargetOSes().isCurrentOSValidForThisPlugin("Linux"), Matchers.is(true));
        Assert.assertThat(descriptorWithTargetOSes().isCurrentOSValidForThisPlugin("Windows"), Matchers.is(true));
        Assert.assertThat(descriptorWithTargetOSes("Linux").isCurrentOSValidForThisPlugin("Linux"), Matchers.is(true));
        Assert.assertThat(descriptorWithTargetOSes("Windows").isCurrentOSValidForThisPlugin("Linux"), Matchers.is(false));
        Assert.assertThat(descriptorWithTargetOSes("Windows", "Linux").isCurrentOSValidForThisPlugin("Linux"), Matchers.is(true));
        Assert.assertThat(descriptorWithTargetOSes("Windows", "SunOS", "Mac OS X").isCurrentOSValidForThisPlugin("Linux"), Matchers.is(false));
    }

    @Test
    public void shouldDoACaseInsensitiveMatchForValidOSesAgainstCurrentOS() throws Exception {
        Assert.assertThat(descriptorWithTargetOSes("linux").isCurrentOSValidForThisPlugin("Linux"), Matchers.is(true));
        Assert.assertThat(descriptorWithTargetOSes("LiNuX").isCurrentOSValidForThisPlugin("Linux"), Matchers.is(true));
        Assert.assertThat(descriptorWithTargetOSes("windows").isCurrentOSValidForThisPlugin("Linux"), Matchers.is(false));
        Assert.assertThat(descriptorWithTargetOSes("windOWS").isCurrentOSValidForThisPlugin("Linux"), Matchers.is(false));
        Assert.assertThat(descriptorWithTargetOSes("WinDOWs", "LINUx").isCurrentOSValidForThisPlugin("Linux"), Matchers.is(true));
        Assert.assertThat(descriptorWithTargetOSes("WINDows", "Sunos", "Mac os x").isCurrentOSValidForThisPlugin("Linux"), Matchers.is(false));
    }
}

