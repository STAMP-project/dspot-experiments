/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.api.utils;


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.utils.log.Logger;


public class ValidationMessagesTest {
    @Test
    public void emptyMessages() {
        ValidationMessages messages = ValidationMessages.create();
        assertThat(messages.hasErrors()).isFalse();
        assertThat(messages.hasWarnings()).isFalse();
        assertThat(messages.hasInfos()).isFalse();
        Logger logger = Mockito.mock(Logger.class);
        messages.log(logger);
        Mockito.verify(logger, Mockito.never()).error(ArgumentMatchers.anyString());
        Mockito.verify(logger, Mockito.never()).warn(ArgumentMatchers.anyString());
        Mockito.verify(logger, Mockito.never()).info(ArgumentMatchers.anyString());
        org.slf4j.Logger slf4j = Mockito.mock(org.slf4j.Logger.class);
        messages.log(slf4j);
        Mockito.verify(slf4j, Mockito.never()).error(ArgumentMatchers.anyString());
        Mockito.verify(slf4j, Mockito.never()).warn(ArgumentMatchers.anyString());
        Mockito.verify(slf4j, Mockito.never()).info(ArgumentMatchers.anyString());
    }

    @Test
    public void addError() {
        ValidationMessages messages = ValidationMessages.create();
        messages.addErrorText("my error");
        assertThat(messages.hasErrors()).isTrue();
        assertThat(messages.hasWarnings()).isFalse();
        assertThat(messages.hasInfos()).isFalse();
        assertThat(messages.getErrors()).hasSize(1);
        assertThat(messages.getErrors()).contains("my error");
        assertThat(messages.toString()).contains("my error");
        Logger logger = Mockito.mock(Logger.class);
        messages.log(logger);
        Mockito.verify(logger, Mockito.times(1)).error("my error");
        Mockito.verify(logger, Mockito.never()).warn(ArgumentMatchers.anyString());
        Mockito.verify(logger, Mockito.never()).info(ArgumentMatchers.anyString());
        org.slf4j.Logger slf4j = Mockito.mock(org.slf4j.Logger.class);
        messages.log(slf4j);
        Mockito.verify(slf4j, Mockito.times(1)).error("my error");
        Mockito.verify(slf4j, Mockito.never()).warn(ArgumentMatchers.anyString());
        Mockito.verify(slf4j, Mockito.never()).info(ArgumentMatchers.anyString());
    }
}

