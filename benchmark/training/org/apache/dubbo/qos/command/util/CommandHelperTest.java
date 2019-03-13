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
package org.apache.dubbo.qos.command.util;


import java.util.List;
import org.apache.dubbo.qos.command.GreetingCommand;
import org.apache.dubbo.qos.command.impl.Help;
import org.apache.dubbo.qos.command.impl.Ls;
import org.apache.dubbo.qos.command.impl.Offline;
import org.apache.dubbo.qos.command.impl.Online;
import org.apache.dubbo.qos.command.impl.Quit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class CommandHelperTest {
    @Test
    public void testHasCommand() throws Exception {
        Assertions.assertTrue(CommandHelper.hasCommand("greeting"));
        Assertions.assertFalse(CommandHelper.hasCommand("not-exiting"));
    }

    @Test
    public void testGetAllCommandClass() throws Exception {
        List<Class<?>> classes = CommandHelper.getAllCommandClass();
        MatcherAssert.assertThat(classes, Matchers.containsInAnyOrder(GreetingCommand.class, Help.class, Ls.class, Offline.class, Online.class, Quit.class));
    }

    @Test
    public void testGetCommandClass() throws Exception {
        MatcherAssert.assertThat(CommandHelper.getCommandClass("greeting"), Matchers.equalTo(GreetingCommand.class));
        Assertions.assertNull(CommandHelper.getCommandClass("not-exiting"));
    }
}

