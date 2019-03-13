/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs.shell;


import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;


public class TestCommandFactory {
    static CommandFactory factory;

    static Configuration conf = new Configuration();

    @Test
    public void testRegistration() {
        Assert.assertArrayEquals(new String[]{  }, TestCommandFactory.factory.getNames());
        TestCommandFactory.factory.registerCommands(TestCommandFactory.TestRegistrar.class);
        String[] names = TestCommandFactory.factory.getNames();
        Assert.assertArrayEquals(new String[]{ "tc1", "tc2", "tc2.1" }, names);
        TestCommandFactory.factory.addClass(TestCommandFactory.TestCommand3.class, "tc3");
        names = TestCommandFactory.factory.getNames();
        Assert.assertArrayEquals(new String[]{ "tc1", "tc2", "tc2.1", "tc3" }, names);
        TestCommandFactory.factory.addClass(TestCommandFactory.TestCommand4.class, getName());
        names = TestCommandFactory.factory.getNames();
        Assert.assertArrayEquals(new String[]{ "tc1", "tc2", "tc2.1", "tc3", "tc4" }, names);
    }

    @Test
    public void testGetInstances() {
        TestCommandFactory.factory.registerCommands(TestCommandFactory.TestRegistrar.class);
        Command instance;
        instance = TestCommandFactory.factory.getInstance("blarg");
        Assert.assertNull(instance);
        instance = TestCommandFactory.factory.getInstance("tc1");
        Assert.assertNotNull(instance);
        Assert.assertEquals(TestCommandFactory.TestCommand1.class, instance.getClass());
        Assert.assertEquals("tc1", instance.getCommandName());
        instance = TestCommandFactory.factory.getInstance("tc2");
        Assert.assertNotNull(instance);
        Assert.assertEquals(TestCommandFactory.TestCommand2.class, instance.getClass());
        Assert.assertEquals("tc2", instance.getCommandName());
        instance = TestCommandFactory.factory.getInstance("tc2.1");
        Assert.assertNotNull(instance);
        Assert.assertEquals(TestCommandFactory.TestCommand2.class, instance.getClass());
        Assert.assertEquals("tc2.1", instance.getCommandName());
        TestCommandFactory.factory.addClass(TestCommandFactory.TestCommand4.class, "tc4");
        instance = TestCommandFactory.factory.getInstance("tc4");
        Assert.assertNotNull(instance);
        Assert.assertEquals(TestCommandFactory.TestCommand4.class, instance.getClass());
        Assert.assertEquals("tc4", instance.getCommandName());
        String usage = instance.getUsage();
        Assert.assertEquals("-tc4 tc4_usage", usage);
        Assert.assertEquals("tc4_description", instance.getDescription());
    }

    static class TestRegistrar {
        public static void registerCommands(CommandFactory factory) {
            factory.addClass(TestCommandFactory.TestCommand1.class, "tc1");
            factory.addClass(TestCommandFactory.TestCommand2.class, "tc2", "tc2.1");
        }
    }

    static class TestCommand1 extends FsCommand {}

    static class TestCommand2 extends FsCommand {}

    static class TestCommand3 extends FsCommand {}

    static class TestCommand4 extends FsCommand {
        static final String NAME = "tc4";

        static final String USAGE = "tc4_usage";

        static final String DESCRIPTION = "tc4_description";
    }
}

