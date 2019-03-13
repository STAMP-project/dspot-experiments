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
package org.apache.geode.internal.process;


import java.util.List;
import org.junit.Test;


public class StartupStatusTest {
    private StartupStatusListener listener;

    private List<String> statusMessageList;

    @Test
    public void getStartupListener_returnsNullByDefault() throws Exception {
        // act/assert
        assertThat(StartupStatus.getStartupListener()).isNull();
    }

    @Test
    public void setListener_null_clearsStartupListener() throws Exception {
        // arrange
        listener = null;
        // act
        StartupStatus.setListener(listener);
        // assert
        assertThat(StartupStatus.getStartupListener()).isNull();
    }

    @Test
    public void getStartupListener_returnsSetListener() throws Exception {
        // arrange
        StartupStatus.setListener(listener);
        // act/assert
        assertThat(StartupStatus.getStartupListener()).isSameAs(listener);
    }

    @Test
    public void clearListener_doesNothingIfNull() throws Exception {
        // arrange
        listener = null;
        StartupStatus.setListener(listener);
        assertThat(StartupStatus.getStartupListener()).isNull();
        // act
        StartupStatus.clearListener();
        // assert
        assertThat(StartupStatus.getStartupListener()).isNull();
    }

    @Test
    public void clearListener_unsetsListener() throws Exception {
        // arrange
        StartupStatus.setListener(listener);
        assertThat(StartupStatus.getStartupListener()).isNotNull();
        // act
        StartupStatus.clearListener();
        // assert
        assertThat(StartupStatus.getStartupListener()).isNull();
    }

    @Test
    public void startup_nullStringId_throwsIllegalArgumentException() throws Exception {
        // arrange
        String stringId = null;
        Object[] params = new Object[0];
        // act/assert
        assertThatThrownBy(() -> startup(stringId, params)).isInstanceOf(NullPointerException.class).hasMessage("Invalid msgId 'null' specified");
    }

    @Test
    public void startup_emptyParams() throws Exception {
        // arrange
        String stringId = "my string";
        Object[] params = new Object[0];
        // act
        StartupStatus.startup(stringId, params);
        // assert (does not throw)
        assertThat(StartupStatus.getStartupListener()).isNull();
    }

    @Test
    public void startup_doesNothingIfNoListener() throws Exception {
        // arrange
        String stringId = "my string";
        Object[] params = new Object[0];
        // act
        StartupStatus.startup(stringId, params);
        // assert (does nothing)
        assertThat(StartupStatus.getStartupListener()).isNull();
    }

    @Test
    public void startup_invokesListener() throws Exception {
        // arrange
        listener = ( statusMessage) -> statusMessageList.add(statusMessage);
        String stringId = "my string";
        Object[] params = new Object[0];
        StartupStatus.setListener(listener);
        // act
        StartupStatus.startup(stringId, params);
        // assert
        assertThat(statusMessageList).hasSize(1).contains("my string");
    }

    @Test
    public void startupTwice_invokesListenerTwice() throws Exception {
        // arrange
        listener = ( statusMessage) -> statusMessageList.add(statusMessage);
        String stringIdOne = "my string";
        String stringIdTwo = "other string";
        Object[] params = new Object[0];
        StartupStatus.setListener(listener);
        // act
        StartupStatus.startup(stringIdOne, params);
        StartupStatus.startup(stringIdTwo, params);
        // assert
        assertThat(statusMessageList).hasSize(2).contains("my string").contains("other string");
    }
}

