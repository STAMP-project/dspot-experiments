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
package io.bootique.meta.application;


import io.bootique.cli.Cli;
import io.bootique.command.Command;
import io.bootique.command.CommandOutcome;
import org.junit.Assert;
import org.junit.Test;


public class CommandMetadataTest {
    @Test
    public void testGetShortName() {
        CommandMetadata md = CommandMetadata.builder(CommandMetadataTest.MyCommand.class).shortName('M').build();
        Assert.assertEquals("my", md.getName());
        Assert.assertEquals("M", md.getShortName());
    }

    @Test
    public void testGetName() {
        CommandMetadata md = CommandMetadata.builder(CommandMetadataTest.MyCommand.class).build();
        Assert.assertEquals("my", md.getName());
        Assert.assertEquals("m", md.getShortName());
    }

    @Test
    public void testGetName_CamelCase() {
        CommandMetadata md = CommandMetadata.builder(CommandMetadataTest.MyCamelCaseCommand.class).build();
        Assert.assertEquals("my-camel-case", md.getName());
        Assert.assertEquals("m", md.getShortName());
    }

    @Test
    public void testGetName_UpperCase() {
        CommandMetadata md = CommandMetadata.builder(CommandMetadataTest.MYXCommand.class).build();
        Assert.assertEquals("myx", md.getName());
        Assert.assertEquals("m", md.getShortName());
    }

    static class MyCommand implements Command {
        @Override
        public CommandOutcome run(Cli cli) {
            return CommandOutcome.succeeded();
        }
    }

    static class MyCamelCaseCommand implements Command {
        @Override
        public CommandOutcome run(Cli cli) {
            return CommandOutcome.succeeded();
        }
    }

    static class MYXCommand implements Command {
        @Override
        public CommandOutcome run(Cli cli) {
            return CommandOutcome.succeeded();
        }
    }
}

