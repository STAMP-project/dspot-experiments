/**
 * Copyright 2016 ThoughtWorks, Inc.
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
package com.thoughtworks.go.buildsession;


import java.util.TimeZone;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class BuildVariablesTest {
    private BuildVariables bvs;

    @Test
    public void lookupCurrentDate() {
        TimeZone oldDefault = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        try {
            Assert.assertThat(bvs.lookup("date"), Matchers.is("1970-01-01 00:00:00 GMT"));
        } finally {
            TimeZone.setDefault(oldDefault);
        }
    }

    @Test
    public void lookupAgentLocation() {
        Assert.assertThat(bvs.lookup("agent.location"), Matchers.is("/home/lord-farquaad/builds"));
    }

    @Test
    public void lookupAgentHostname() {
        Assert.assertThat(bvs.lookup("agent.hostname"), Matchers.is("duloc"));
    }

    @Test
    public void lookupCrazyThingShouldReturnNull() {
        Assert.assertThat(bvs.lookup("questiontoanswer42"), Matchers.is(Matchers.nullValue()));
    }
}

