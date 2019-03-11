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


import java.util.Collections;
import org.apache.geode.distributed.internal.InternalConfigurationPersistenceService;
import org.apache.geode.management.cli.Result;
import org.apache.geode.management.internal.cli.result.ResultBuilder;
import org.apache.geode.management.internal.cli.shell.Gfsh;
import org.apache.geode.management.internal.exceptions.EntityNotFoundException;
import org.junit.Test;
import org.mockito.Mockito;


public class GfshCommandJUnitTest {
    private InternalGfshCommand command;

    private Gfsh gfsh;

    private InternalConfigurationPersistenceService clusterConfigurationService;

    @Test
    public void persistClusterConfiguration() throws Exception {
        Mockito.when(command.getConfigurationPersistenceService()).thenReturn(null);
        Result result = ResultBuilder.createInfoResult("info");
        Runnable runnable = Mockito.mock(Runnable.class);
        command.persistClusterConfiguration(result, runnable);
        assertThat(result.failedToPersist()).isTrue();
        Mockito.when(command.getConfigurationPersistenceService()).thenReturn(clusterConfigurationService);
        command.persistClusterConfiguration(result, runnable);
        assertThat(result.failedToPersist()).isFalse();
    }

    @Test
    public void getMember() throws Exception {
        Mockito.doReturn(null).when(command).findMember("test");
        assertThatThrownBy(() -> command.getMember("test")).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    public void getMembers() throws Exception {
        String[] members = new String[]{ "member" };
        Mockito.doReturn(Collections.emptySet()).when(command).findMembers(members, null);
        assertThatThrownBy(() -> command.getMembers(members, null)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    public void getMembersIncludingLocators() throws Exception {
        String[] members = new String[]{ "member" };
        Mockito.doReturn(Collections.emptySet()).when(command).findMembersIncludingLocators(members, null);
        assertThatThrownBy(() -> command.getMembersIncludingLocators(members, null)).isInstanceOf(EntityNotFoundException.class);
    }
}

