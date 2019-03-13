/**
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.bootique.jopt;


import io.bootique.cli.Cli;
import io.bootique.command.ManagedCommand;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class JoptCliFactoryCommandNameTest {
    private Map<String, ManagedCommand> commands;

    @Test
    public void testCommandName_NoMatch() {
        addMockCommand("c1", "me", "them");
        Cli cli = createCli("--me");
        Assert.assertNull(cli.commandName());
    }

    @Test
    public void testCommandName_Match() {
        addMockCommand("c1", "me", "them");
        addMockCommand("c2", "us", "others");
        Cli cli = createCli("--me --c1");
        Assert.assertEquals("c1", cli.commandName());
    }

    @Test(expected = RuntimeException.class)
    public void testCommandName_MultipleMatches() {
        addMockCommand("c1", "me", "them");
        addMockCommand("c2", "us", "others");
        createCli("--me --c1 --c2");
    }
}

