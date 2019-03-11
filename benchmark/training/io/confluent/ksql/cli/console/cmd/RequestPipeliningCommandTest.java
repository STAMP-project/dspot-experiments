/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.cli.console.cmd;


import RequestPipeliningCommand.NAME;
import com.google.common.collect.ImmutableList;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class RequestPipeliningCommandTest {
    @Mock
    private Supplier<Boolean> settingSupplier;

    @Mock
    private Consumer<Boolean> settingConsumer;

    private StringWriter out;

    private PrintWriter terminal;

    private RequestPipeliningCommand requestPipeliningCommand;

    @Test
    public void shouldGetHelp() {
        MatcherAssert.assertThat(requestPipeliningCommand.getHelpMessage(), CoreMatchers.containsString("View the current setting"));
        MatcherAssert.assertThat(requestPipeliningCommand.getHelpMessage(), CoreMatchers.containsString("Update the setting as specified."));
    }

    @Test
    public void shouldPrintCurrentSettingOfOn() {
        // Given:
        Mockito.when(settingSupplier.get()).thenReturn(true);
        // When:
        requestPipeliningCommand.execute(Collections.emptyList(), terminal);
        // Then:
        MatcherAssert.assertThat(out.toString(), CoreMatchers.containsString(String.format("Current %s configuration: ON", NAME)));
    }

    @Test
    public void shouldPrintCurrentSettingOfOff() {
        // Given:
        Mockito.when(settingSupplier.get()).thenReturn(false);
        // When:
        requestPipeliningCommand.execute(Collections.emptyList(), terminal);
        // Then:
        MatcherAssert.assertThat(out.toString(), CoreMatchers.containsString(String.format("Current %s configuration: OFF", NAME)));
    }

    @Test
    public void shouldUpdateSettingToOn() {
        // When:
        requestPipeliningCommand.execute(ImmutableList.of("on"), terminal);
        // Then:
        Mockito.verify(settingConsumer).accept(true);
    }

    @Test
    public void shouldUpdateSettingToOff() {
        // When:
        requestPipeliningCommand.execute(ImmutableList.of("OFF"), terminal);
        // Then:
        Mockito.verify(settingConsumer).accept(false);
    }

    @Test
    public void shouldRejectUpdateOnInvalidSetting() {
        // When:
        requestPipeliningCommand.execute(ImmutableList.of("bad"), terminal);
        // Then:
        Mockito.verify(settingConsumer, Mockito.never()).accept(ArgumentMatchers.anyBoolean());
        MatcherAssert.assertThat(out.toString(), CoreMatchers.containsString(String.format("Invalid %s setting: bad", NAME)));
        MatcherAssert.assertThat(out.toString(), CoreMatchers.containsString("Valid options are 'ON' and 'OFF'"));
    }
}

