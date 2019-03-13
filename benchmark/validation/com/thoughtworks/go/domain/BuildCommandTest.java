/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2016 ThoughtWorks, Inc.
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
package com.thoughtworks.go.domain;


import com.thoughtworks.go.util.MapBuilder;
import com.thoughtworks.go.websocket.MessageEncoding;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class BuildCommandTest {
    @Test
    public void testGetArgs() {
        Assert.assertThat(getArrayArg("foo"), Matchers.is(new String[]{ "arg1", "arg2" }));
        Assert.assertThat(getBooleanArg("foo"), Matchers.is(true));
        Assert.assertThat(getBooleanArg("bar"), Matchers.is(false));
        Assert.assertThat(getStringArg("foo"), Matchers.is("bar"));
    }

    @Test
    public void defaultSubCommandsShouldBeEmpty() {
        Assert.assertThat(new BuildCommand("foo").getSubCommands().size(), Matchers.is(0));
        Assert.assertThat(getSubCommands().size(), Matchers.is(0));
    }

    @Test
    public void testDumpComposedCommand() {
        Assert.assertThat(BuildCommand.compose(new BuildCommand("bar1"), BuildCommand.compose(new BuildCommand("barz"))).dump(), Matchers.is("compose\n    bar1\n    compose\n        barz"));
    }

    @Test
    public void defaultRunIfIsPassed() {
        Assert.assertThat(new BuildCommand("cmd").getRunIfConfig(), Matchers.is("passed"));
        Assert.assertThat(new BuildCommand("cmd").runIf("any").getRunIfConfig(), Matchers.is("any"));
    }

    @Test
    public void encodeDecode() {
        BuildCommand bc = BuildCommand.compose(new BuildCommand("bar1", MapBuilder.map("arg1", "1", "arg2", "2")), BuildCommand.compose(new BuildCommand("barz")));
        bc.setRunIfConfig("any");
        bc.setTest(new BuildCommand("t", MapBuilder.map("k1", "v1")));
        bc.setOnCancel(BuildCommand.compose(BuildCommand.echo("foo"), BuildCommand.echo("bar")));
        Assert.assertThat(MessageEncoding.decodeData(MessageEncoding.encodeData(bc), BuildCommand.class), Matchers.is(bc));
    }
}

