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
package org.apache.geode.management.internal.cli.shell;


import Gfsh.ENV_APP_CONTEXT_PATH;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.shell.core.CommandResult;

import static Gfsh.LINE_INDENT;
import static Gfsh.LINE_SEPARATOR;


public class GfshAbstractUnitTest {
    protected Gfsh gfsh;

    protected String testString;

    @Test
    public void testWrapTest() {
        assertThat(Gfsh.wrapText(testString, 0, (-1))).isEqualTo(testString);
        assertThat(Gfsh.wrapText(testString, 0, 0)).isEqualTo(testString);
        assertThat(Gfsh.wrapText(testString, 0, 1)).isEqualTo(testString);
        assertThat(Gfsh.wrapText(testString, 0, 10)).isEqualTo((((("This is a" + (LINE_SEPARATOR)) + "test") + (LINE_SEPARATOR)) + "string."));
        assertThat(Gfsh.wrapText(testString, 1, 100)).isEqualTo(((LINE_INDENT) + (testString)));
        assertThat(Gfsh.wrapText(testString, 2, 100)).isEqualTo((((LINE_INDENT) + (LINE_INDENT)) + (testString)));
    }

    @Test
    public void wrapTextWithNoSpace() {
        assertThat(Gfsh.wrapText("for datasource", 0, 6)).isEqualTo((((("for" + (LINE_SEPARATOR)) + "datas") + (LINE_SEPARATOR)) + "ource"));
        assertThat(Gfsh.wrapText("for data sour ", 0, 6)).isEqualTo((((("for" + (LINE_SEPARATOR)) + "data") + (LINE_SEPARATOR)) + "sour "));
        assertThat(Gfsh.wrapText("for data sour ", 0, 5)).isEqualTo(((((("for" + (LINE_SEPARATOR)) + "data") + (LINE_SEPARATOR)) + "sour") + (LINE_SEPARATOR)));
    }

    @Test
    public void getAppContextPath() {
        gfsh = new Gfsh();
        assertThat(gfsh.getEnvAppContextPath()).isEqualTo("");
        gfsh.setEnvProperty(ENV_APP_CONTEXT_PATH, "test");
        assertThat(gfsh.getEnvAppContextPath()).isEqualTo("test");
    }

    @Test
    public void executeCommandShouldSubstituteVariablesWhenNeededAndDelegateToDefaultImplementation() {
        gfsh = Mockito.spy(Gfsh.class);
        CommandResult commandResult;
        // No '$' character, should only delegate to default implementation.
        commandResult = gfsh.executeCommand("echo --string=ApacheGeode!");
        assertThat(commandResult.isSuccess()).isTrue();
        Mockito.verify(gfsh, Mockito.times(0)).expandProperties("echo --string=ApacheGeode!");
        assertThat(getMessageFromContent()).isEqualTo("ApacheGeode!");
        // '$' character present, should expand properties and delegate to default implementation.
        commandResult = gfsh.executeCommand("echo --string=SYS_USER:${SYS_USER}");
        assertThat(commandResult.isSuccess()).isTrue();
        Mockito.verify(gfsh, Mockito.times(1)).expandProperties("echo --string=SYS_USER:${SYS_USER}");
        assertThat(getMessageFromContent()).isEqualTo(("SYS_USER:" + (System.getProperty("user.name"))));
        // '$' character present but not variable referenced, should try to expand, find nothing (no
        // replacement) and delegate to default implementation.
        commandResult = gfsh.executeCommand("echo --string=MyNameIs:$USER_NAME");
        assertThat(commandResult.isSuccess()).isTrue();
        Mockito.verify(gfsh, Mockito.times(1)).expandProperties("echo --string=MyNameIs:$USER_NAME");
        assertThat(getMessageFromContent()).isEqualTo("MyNameIs:$USER_NAME");
    }
}

