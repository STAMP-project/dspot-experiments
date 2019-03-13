/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal.cli;


import ConfigurationProperties.USER_COMMAND_PACKAGES;
import com.examples.UserGfshCommand;
import java.util.Properties;
import org.apache.geode.management.cli.CliMetaData;
import org.apache.geode.management.cli.Disabled;
import org.apache.geode.management.cli.Result;
import org.apache.geode.management.internal.cli.result.ResultBuilder;
import org.apache.geode.management.internal.security.ResourceOperation;
import org.apache.geode.security.ResourcePermission.Operation;
import org.apache.geode.security.ResourcePermission.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.Completion;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;


/**
 * CommandManagerTest - Includes tests to check the CommandManager functions
 */
public class CommandManagerJUnitTest {
    private static final String COMMAND1_NAME = "command1";

    private static final String COMMAND1_NAME_ALIAS = "command1_alias";

    private static final String COMMAND2_NAME = "c2";

    private static final String COMMAND1_HELP = "help for " + (CommandManagerJUnitTest.COMMAND1_NAME);

    // ARGUMENTS
    private static final String ARGUMENT1_NAME = "argument1";

    private static final String ARGUMENT1_HELP = "help for argument1";

    private static final String ARGUMENT1_CONTEXT = "context for argument 1";

    private static final Completion[] ARGUMENT1_COMPLETIONS = new Completion[]{ new Completion("arg1"), new Completion("arg1alt") };

    private static final String ARGUMENT2_NAME = "argument2";

    private static final String ARGUMENT2_CONTEXT = "context for argument 2";

    private static final String ARGUMENT2_HELP = "help for argument2";

    private static final String ARGUMENT2_UNSPECIFIED_DEFAULT_VALUE = "{unspecified default value for argument2}";

    private static final Completion[] ARGUMENT2_COMPLETIONS = new Completion[]{ new Completion("arg2"), new Completion("arg2alt") };

    // OPTIONS
    private static final String OPTION1_NAME = "option1";

    private static final String OPTION1_SYNONYM = "opt1";

    private static final String OPTION1_HELP = "help for option1";

    private static final String OPTION1_CONTEXT = "context for option1";

    private static final String OPTION1_SPECIFIED_DEFAULT_VALUE = "{specified default value for option1}";

    private static final Completion[] OPTION1_COMPLETIONS = new Completion[]{ new Completion("option1"), new Completion("option1Alternate") };

    private static final String OPTION2_NAME = "option2";

    private static final String OPTION2_HELP = "help for option2";

    private static final String OPTION2_CONTEXT = "context for option2";

    private static final String OPTION2_SPECIFIED_DEFAULT_VALUE = "{specified default value for option2}";

    private static final String OPTION3_NAME = "option3";

    private static final String OPTION3_SYNONYM = "opt3";

    private static final String OPTION3_HELP = "help for option3";

    private static final String OPTION3_CONTEXT = "context for option3";

    private static final String OPTION3_SPECIFIED_DEFAULT_VALUE = "{specified default value for option3}";

    private static final String OPTION3_UNSPECIFIED_DEFAULT_VALUE = "{unspecified default value for option3}";

    private CommandManager commandManager;

    /**
     * tests loadCommands()
     */
    @Test
    public void testCommandManagerLoadCommands() {
        Assert.assertNotNull(commandManager);
        assertThat(commandManager.getCommandMarkers().size()).isGreaterThan(0);
        assertThat(commandManager.getConverters().size()).isGreaterThan(0);
    }

    /**
     * tests commandManagerInstance method
     */
    @Test
    public void testCommandManagerInstance() {
        Assert.assertNotNull(commandManager);
    }

    /**
     * Tests {@link CommandManager#loadPluginCommands()}.
     *
     * @since GemFire 8.1
     */
    @Test
    public void testCommandManagerLoadPluginCommands() {
        Assert.assertNotNull(commandManager);
        Assert.assertTrue("Should find listed plugin.", commandManager.getHelper().getCommands().contains("mock plugin command"));
        Assert.assertTrue("Should not find unlisted plugin.", (!(commandManager.getHelper().getCommands().contains("mock plugin command unlisted"))));
    }

    @Test
    public void testCommandManagerLoadsUserCommand() throws Exception {
        Properties props = new Properties();
        props.setProperty(USER_COMMAND_PACKAGES, "com.examples");
        CommandManager commandManager = new CommandManager(props, null);
        assertThat(commandManager.getCommandMarkers().stream().anyMatch(( c) -> c instanceof UserGfshCommand));
    }

    @Test
    public void commandManagerDoesNotAddUnsatisfiedFeatureFlaggedCommands() {
        System.setProperty("enabled.flag", "true");
        try {
            CommandMarker accessibleCommand = new CommandManagerJUnitTest.AccessibleCommand();
            CommandMarker enabledCommand = new CommandManagerJUnitTest.FeatureFlaggedAndEnabledCommand();
            CommandMarker reachableButDisabledCommand = new CommandManagerJUnitTest.FeatureFlaggedReachableCommand();
            CommandMarker unreachableCommand = new CommandManagerJUnitTest.FeatureFlaggedUnreachableCommand();
            commandManager.add(accessibleCommand);
            commandManager.add(enabledCommand);
            commandManager.add(reachableButDisabledCommand);
            commandManager.add(unreachableCommand);
            assertThat(commandManager.getCommandMarkers()).contains(accessibleCommand, enabledCommand);
            assertThat(commandManager.getCommandMarkers()).doesNotContain(reachableButDisabledCommand, unreachableCommand);
        } finally {
            System.clearProperty("enabled.flag");
        }
    }

    /**
     * class that represents dummy commands
     */
    public static class Commands implements CommandMarker {
        @CliCommand(value = { CommandManagerJUnitTest.COMMAND1_NAME, CommandManagerJUnitTest.COMMAND1_NAME_ALIAS }, help = CommandManagerJUnitTest.COMMAND1_HELP)
        @CliMetaData(shellOnly = true, relatedTopic = { "relatedTopicOfCommand1" })
        @ResourceOperation(resource = Resource.CLUSTER, operation = Operation.READ)
        public static String command1(@CliOption(key = CommandManagerJUnitTest.ARGUMENT1_NAME, optionContext = CommandManagerJUnitTest.ARGUMENT1_CONTEXT, help = CommandManagerJUnitTest.ARGUMENT1_HELP, mandatory = true)
        String argument1, @CliOption(key = CommandManagerJUnitTest.ARGUMENT2_NAME, optionContext = CommandManagerJUnitTest.ARGUMENT2_CONTEXT, help = CommandManagerJUnitTest.ARGUMENT2_HELP, unspecifiedDefaultValue = CommandManagerJUnitTest.ARGUMENT2_UNSPECIFIED_DEFAULT_VALUE)
        String argument2, @CliOption(key = { CommandManagerJUnitTest.OPTION1_NAME, CommandManagerJUnitTest.OPTION1_SYNONYM }, help = CommandManagerJUnitTest.OPTION1_HELP, mandatory = true, optionContext = CommandManagerJUnitTest.OPTION1_CONTEXT, specifiedDefaultValue = CommandManagerJUnitTest.OPTION1_SPECIFIED_DEFAULT_VALUE)
        String option1, @CliOption(key = { CommandManagerJUnitTest.OPTION2_NAME }, help = CommandManagerJUnitTest.OPTION2_HELP, optionContext = CommandManagerJUnitTest.OPTION2_CONTEXT, specifiedDefaultValue = CommandManagerJUnitTest.OPTION2_SPECIFIED_DEFAULT_VALUE)
        String option2, @CliOption(key = { CommandManagerJUnitTest.OPTION3_NAME, CommandManagerJUnitTest.OPTION3_SYNONYM }, help = CommandManagerJUnitTest.OPTION3_HELP, optionContext = CommandManagerJUnitTest.OPTION3_CONTEXT, unspecifiedDefaultValue = CommandManagerJUnitTest.OPTION3_UNSPECIFIED_DEFAULT_VALUE, specifiedDefaultValue = CommandManagerJUnitTest.OPTION3_SPECIFIED_DEFAULT_VALUE)
        String option3) {
            return null;
        }

        @CliCommand({ CommandManagerJUnitTest.COMMAND2_NAME })
        @ResourceOperation(resource = Resource.CLUSTER, operation = Operation.READ)
        public static String command2() {
            return null;
        }

        @CliCommand({ "testParamConcat" })
        @ResourceOperation(resource = Resource.CLUSTER, operation = Operation.READ)
        public static Result testParamConcat(@CliOption(key = { "string" })
        String string, @CliOption(key = { "stringArray" })
        String[] stringArray, @CliOption(key = { "integer" })
        Integer integer, @CliOption(key = { "colonArray" })
        String[] colonArray) {
            return null;
        }

        @CliCommand({ "testMultiWordArg" })
        @ResourceOperation(resource = Resource.CLUSTER, operation = Operation.READ)
        public static Result testMultiWordArg(@CliOption(key = "arg1")
        String arg1, @CliOption(key = "arg2")
        String arg2) {
            return null;
        }

        @CliAvailabilityIndicator({ CommandManagerJUnitTest.COMMAND1_NAME })
        public boolean isAvailable() {
            return true;// always available on server

        }
    }

    public static class MockPluginCommand implements CommandMarker {
        @CliCommand("mock plugin command")
        @ResourceOperation(resource = Resource.CLUSTER, operation = Operation.READ)
        public Result mockPluginCommand() {
            return null;
        }
    }

    public static class MockPluginCommandUnlisted implements CommandMarker {
        @CliCommand("mock plugin command unlisted")
        @ResourceOperation(resource = Resource.CLUSTER, operation = Operation.READ)
        public Result mockPluginCommandUnlisted() {
            return null;
        }
    }

    class AccessibleCommand implements CommandMarker {
        @CliCommand("test-command")
        public Result ping() {
            return ResultBuilder.createInfoResult("pong");
        }

        @CliAvailabilityIndicator("test-command")
        public boolean always() {
            return true;
        }
    }

    @Disabled
    class FeatureFlaggedUnreachableCommand implements CommandMarker {
        @CliCommand("unreachable")
        public Result nothing() {
            throw new RuntimeException("You reached the body of a feature-flagged command.");
        }
    }

    @Disabled(unlessPropertyIsSet = "reachable.flag")
    class FeatureFlaggedReachableCommand implements CommandMarker {
        @CliCommand("reachable")
        public Result nothing() {
            throw new RuntimeException("You reached the body of a feature-flagged command.");
        }
    }

    @Disabled(unlessPropertyIsSet = "enabled.flag")
    class FeatureFlaggedAndEnabledCommand implements CommandMarker {
        @CliCommand("reachable")
        public Result nothing() {
            throw new RuntimeException("You reached the body of a feature-flagged command.");
        }
    }
}

