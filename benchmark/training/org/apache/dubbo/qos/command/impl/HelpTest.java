/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.qos.command.impl;


import org.apache.dubbo.qos.command.CommandContext;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class HelpTest {
    @Test
    public void testMainHelp() throws Exception {
        Help help = new Help();
        String output = help.execute(Mockito.mock(CommandContext.class), null);
        MatcherAssert.assertThat(output, Matchers.containsString("greeting"));
        MatcherAssert.assertThat(output, Matchers.containsString("help"));
        MatcherAssert.assertThat(output, Matchers.containsString("ls"));
        MatcherAssert.assertThat(output, Matchers.containsString("online"));
        MatcherAssert.assertThat(output, Matchers.containsString("offline"));
        MatcherAssert.assertThat(output, Matchers.containsString("quit"));
    }

    @Test
    public void testGreeting() throws Exception {
        Help help = new Help();
        String output = help.execute(Mockito.mock(CommandContext.class), new String[]{ "greeting" });
        MatcherAssert.assertThat(output, Matchers.containsString("COMMAND NAME"));
        MatcherAssert.assertThat(output, Matchers.containsString("greeting"));
        MatcherAssert.assertThat(output, Matchers.containsString("EXAMPLE"));
        MatcherAssert.assertThat(output, Matchers.containsString("greeting dubbo"));
    }
}

