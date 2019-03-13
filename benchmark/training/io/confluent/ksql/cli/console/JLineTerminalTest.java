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
package io.confluent.ksql.cli.console;


import com.google.common.collect.ImmutableList;
import io.confluent.ksql.cli.console.KsqlTerminal.StatusClosable;
import java.util.function.Function;
import java.util.function.Predicate;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStyle;
import org.jline.utils.Status;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class JLineTerminalTest {
    @Mock
    private Predicate<String> cliLinePredicate;

    @Mock
    private Function<Terminal, Status> statusFactory;

    @Mock
    private Status statusBar;

    private JLineTerminal terminal;

    @Test
    public void shouldSetStatusMessage() {
        // When:
        terminal.setStatusMessage("test message");
        // Then:
        Mockito.verify(statusBar).update(ImmutableList.of(new org.jline.utils.AttributedString("test message", AttributedStyle.INVERSE)));
    }

    @Test
    public void shouldResetStatusMessage() {
        // Given:
        final StatusClosable closable = terminal.setStatusMessage("test message");
        Mockito.clearInvocations(statusBar);
        // When:
        closable.close();
        // Then:
        Mockito.verify(statusBar).update(ImmutableList.of(new org.jline.utils.AttributedString("", AttributedStyle.DEFAULT)));
    }
}

