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


import CliFunctionResult.StatusState;
import java.util.List;
import java.util.Set;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.management.internal.cli.functions.CliFunctionResult;
import org.apache.geode.test.junit.rules.GfshParserRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DestroyGatewaySenderCommandTest {
    @ClassRule
    public static GfshParserRule parser = new GfshParserRule();

    private DestroyGatewaySenderCommand command;

    private InternalCache cache;

    private List<CliFunctionResult> functionResults;

    private CliFunctionResult result1;

    private CliFunctionResult result2;

    @Test
    public void mandatoryOptions() throws Exception {
        assertThat(DestroyGatewaySenderCommandTest.parser.parse("destroy gateway-sender --member=A")).isNull();
    }

    @Test
    public void allFunctionReturnsOK() throws Exception {
        result1 = new CliFunctionResult("member", StatusState.OK, "result1");
        result2 = new CliFunctionResult("member", StatusState.OK, "result2");
        functionResults.add(result1);
        functionResults.add(result2);
        Mockito.doReturn(Mockito.mock(Set.class)).when(command).getMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        DestroyGatewaySenderCommandTest.parser.executeAndAssertThat(command, "destroy gateway-sender --id=1").statusIsSuccess().tableHasColumnWithValuesContaining("Message", "result1", "result2");
    }

    @Test
    public void oneFunctionReturnsError() throws Exception {
        result1 = new CliFunctionResult("member", StatusState.OK, "result1");
        result2 = new CliFunctionResult("member", StatusState.ERROR, "result2");
        functionResults.add(result1);
        functionResults.add(result2);
        Mockito.doReturn(Mockito.mock(Set.class)).when(command).getMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        DestroyGatewaySenderCommandTest.parser.executeAndAssertThat(command, "destroy gateway-sender --id=1").statusIsSuccess().tableHasColumnWithValuesContaining("Message", "result1", "result2");
    }

    @Test
    public void oneFunctionThrowsGeneralException() throws Exception {
        result1 = new CliFunctionResult("member", StatusState.OK, "result1");
        result2 = new CliFunctionResult("member", new Exception("something happened"), null);
        functionResults.add(result1);
        functionResults.add(result2);
        Mockito.doReturn(Mockito.mock(Set.class)).when(command).getMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        DestroyGatewaySenderCommandTest.parser.executeAndAssertThat(command, "destroy gateway-sender --id=1").statusIsSuccess().tableHasColumnWithValuesContaining("Message", "result1", "java.lang.Exception: something happened");
    }

    @Test
    public void putsIdOfDestroyedSenderInResult() throws Exception {
        result1 = new CliFunctionResult("member", StatusState.OK, "result1");
        functionResults.add(result1);
        Mockito.doReturn(Mockito.mock(Set.class)).when(command).getMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        String id = ((String) (DestroyGatewaySenderCommandTest.parser.executeAndAssertThat(command, "destroy gateway-sender --id=1").statusIsSuccess().getResultModel().getConfigObject()));
        assertThat(id).isEqualTo("1");
    }
}

