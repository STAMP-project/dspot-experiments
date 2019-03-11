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


import io.bootique.command.ManagedCommand;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.meta.application.OptionMetadata;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class JoptCliFactoryTest {
    private Map<String, ManagedCommand> commands;

    @Test
    public void testGet_HasOption() {
        addMockCommand(CommandMetadata.builder("c1").addOption(OptionMetadata.builder("me")));
        Assert.assertTrue(createCli("-m").hasOption("me"));
        Assert.assertTrue(createCli("--me").hasOption("me"));
        Assert.assertFalse(createCli("-m").hasOption("not_me"));
        Assert.assertTrue(createCli("-m").hasOption("m"));
    }

    @Test
    public void testOptionStrings_Short() {
        addMockCommand(CommandMetadata.builder("c1").addOption(OptionMetadata.builder("me").valueOptional(null)));
        assertEquals(createCli("-m v4").optionStrings("me"), "v4");
    }

    @Test
    public void testOptionStrings_Long_Equals() {
        addMockCommand(CommandMetadata.builder("c1").addOption(OptionMetadata.builder("me").valueOptional(null)));
        assertEquals(createCli("--me=v4").optionStrings("me"), "v4");
    }

    @Test
    public void testOptionStrings_Long_Space() {
        addMockCommand(CommandMetadata.builder("c1").addOption(OptionMetadata.builder("me").valueOptional(null)));
        assertEquals(createCli("--me v4").optionStrings("me"), "v4");
    }

    @Test
    public void testOptionStrings_Single_Mixed() {
        addMockCommand(CommandMetadata.builder("c1").addOption(OptionMetadata.builder("me").valueOptional(null)).addOption(OptionMetadata.builder("other").valueOptional(null)));
        assertEquals(createCli("--other v2 --me=v4").optionStrings("me"), "v4");
    }

    @Test
    public void testOptionStrings_Multiple_Mixed() {
        addMockCommand(CommandMetadata.builder("c1").addOption(OptionMetadata.builder("me").valueOptional(null)).addOption(OptionMetadata.builder("other").valueOptional(null)).addOption(OptionMetadata.builder("n").valueOptional(null)).addOption(OptionMetadata.builder("yes")));
        assertEquals(createCli("--me=v1 --other v2 -n v3 --me v4 --yes").optionStrings("me"), "v1", "v4");
    }

    @Test
    public void testStandaloneArguments_Mix() {
        addMockCommand(CommandMetadata.builder("c1").addOption(OptionMetadata.builder("me").valueOptional(null)).addOption(OptionMetadata.builder("other").valueOptional(null)).addOption(OptionMetadata.builder("yes")));
        assertEquals(createCli("a --me=v1 --other v2 b --me v4 --yes c d").standaloneArguments(), "a", "b", "c", "d");
    }

    @Test
    public void testStandaloneArguments_DashDash() {
        addMockCommand(CommandMetadata.builder("c1").addOption(OptionMetadata.builder("me").valueOptional(null)).addOption(OptionMetadata.builder("other").valueOptional(null)));
        assertEquals(createCli("a --me=v1 -- --other v2").standaloneArguments(), "a", "--other", "v2");
    }
}

