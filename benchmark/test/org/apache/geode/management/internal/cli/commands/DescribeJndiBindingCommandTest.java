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
package org.apache.geode.management.internal.cli.commands;


import JndiBindingsType.JndiBinding;
import java.util.List;
import org.apache.geode.cache.configuration.CacheConfig;
import org.apache.geode.cache.configuration.JndiBindingsType;
import org.apache.geode.distributed.internal.InternalConfigurationPersistenceService;
import org.apache.geode.test.junit.rules.GfshParserRule;
import org.junit.ClassRule;
import org.junit.Test;


public class DescribeJndiBindingCommandTest {
    @ClassRule
    public static GfshParserRule gfsh = new GfshParserRule();

    InternalConfigurationPersistenceService ccService;

    CacheConfig cacheConfig;

    private DescribeJndiBindingCommand command;

    JndiBinding binding;

    List<JndiBindingsType.JndiBinding> bindings;

    private static String COMMAND = "describe jndi-binding ";

    @Test
    public void describeJndiBinding() {
        bindings.add(binding);
        DescribeJndiBindingCommandTest.gfsh.executeAndAssertThat(command, ((DescribeJndiBindingCommandTest.COMMAND) + " --name=jndi-name")).statusIsSuccess().containsOutput("\"SIMPLE\",\"jndi-name\",\"org.postgresql.Driver\",\"MyUser\",\"jdbc:postgresql://localhost:5432/my_db\",\"\",\"\",\"\",\"\",\"\"");
    }

    @Test
    public void bindingEmpty() {
        bindings.clear();
        DescribeJndiBindingCommandTest.gfsh.executeAndAssertThat(command, ((DescribeJndiBindingCommandTest.COMMAND) + " --name=jndi-name")).statusIsError().containsOutput("jndi-name not found");
    }

    @Test
    public void bindingNotFound() {
        bindings.add(binding);
        DescribeJndiBindingCommandTest.gfsh.executeAndAssertThat(command, ((DescribeJndiBindingCommandTest.COMMAND) + " --name=bad-name")).statusIsError().containsOutput("bad-name not found");
    }
}

