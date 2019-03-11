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


import ListMembersCommand.MEMBERS_SECTION;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.management.internal.cli.result.CommandResult;
import org.apache.geode.test.junit.rules.GfshParserRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;


public class ListMembersCommandTest {
    @ClassRule
    public static GfshParserRule gfsh = new GfshParserRule();

    private ListMembersCommand command;

    private Set<DistributedMember> members;

    private DistributedMember member1;

    private DistributedMember member2;

    @Test
    public void listMembersNoMemberFound() {
        ListMembersCommandTest.gfsh.executeAndAssertThat(command, "list members").containsOutput("No Members Found").statusIsSuccess();
    }

    @Test
    public void basicListMembers() {
        members.add(member1);
        CommandResult result = ListMembersCommandTest.gfsh.executeCommandWithInstance(command, "list members");
        Map<String, List<String>> table = result.getMapFromTableContent(MEMBERS_SECTION);
        assertThat(table.get("Name")).contains("name");
        assertThat(table.get("Id")).contains("id [Coordinator]");
    }

    @Test
    public void noCoordinator() {
        members.add(member1);
        Mockito.doReturn(null).when(command).getCoordinator();
        CommandResult result = ListMembersCommandTest.gfsh.executeCommandWithInstance(command, "list members");
        Map<String, List<String>> table = result.getMapFromTableContent(MEMBERS_SECTION);
        assertThat(table.get("Name")).contains("name");
        assertThat(table.get("Id")).contains("id");
    }

    @Test
    public void listMembersMultipleItems() {
        members.add(member1);
        members.add(member2);
        CommandResult result = ListMembersCommandTest.gfsh.executeCommandWithInstance(command, "list members");
        Map<String, List<String>> table = result.getMapFromTableContent(MEMBERS_SECTION);
        assertThat(table.get("Name")).contains("name", "name2");
        assertThat(table.get("Id")).contains("id [Coordinator]", "id2");
    }
}

