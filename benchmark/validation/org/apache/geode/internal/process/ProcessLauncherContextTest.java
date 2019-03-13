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
import java.util.Properties;
import org.junit.Test;


/**
 * Unit tests for {@link ProcessLauncherContext}.
 */
public class ProcessLauncherContextTest {
    private boolean redirectOutput;

    private Properties overriddenDefaults;

    private StartupStatusListener startupListener;

    private List<String> statusMessageList;

    @Test
    public void isRedirectingOutput_defaultsToFalse() throws Exception {
        assertThat(ProcessLauncherContext.isRedirectingOutput()).isFalse();
    }

    @Test
    public void getOverriddenDefaults_defaultsToEmpty() throws Exception {
        assertThat(ProcessLauncherContext.getOverriddenDefaults()).isEmpty();
    }

    @Test
    public void getStartupListener_defaultsToNull() throws Exception {
        assertThat(ProcessLauncherContext.getStartupListener()).isNull();
    }

    @Test
    public void null_overriddenDefaults_throwsIllegalArgumentException() throws Exception {
        // arrange
        overriddenDefaults = null;
        // act/assert
        assertThatThrownBy(() -> set(redirectOutput, overriddenDefaults, startupListener)).isInstanceOf(NullPointerException.class).hasMessage("Invalid overriddenDefaults 'null' specified");
    }

    @Test
    public void null_startupListener_isAllowed() throws Exception {
        // arrange
        startupListener = null;
        // act
        ProcessLauncherContext.set(redirectOutput, overriddenDefaults, startupListener);
        // assert
        assertThat(ProcessLauncherContext.getStartupListener()).isNull();
    }

    @Test
    public void empty_overriddenDefaults_isAllowed() throws Exception {
        // act
        ProcessLauncherContext.set(redirectOutput, overriddenDefaults, startupListener);
        // assert
        assertThat(ProcessLauncherContext.getOverriddenDefaults()).isEmpty();
    }

    @Test
    public void isRedirectingOutput_returnsPassedValue() throws Exception {
        // arrange
        redirectOutput = true;
        // act
        ProcessLauncherContext.set(redirectOutput, overriddenDefaults, startupListener);
        // assert
        assertThat(ProcessLauncherContext.isRedirectingOutput()).isTrue();
    }

    @Test
    public void getOverriddenDefaults_returnsPassedInProps() throws Exception {
        // arrange
        overriddenDefaults.setProperty("key", "value");
        // act
        ProcessLauncherContext.set(redirectOutput, overriddenDefaults, startupListener);
        // assert
        assertThat(ProcessLauncherContext.getOverriddenDefaults()).hasSize(1).containsEntry("key", "value");
    }

    @Test
    public void getStartupListener_returnsPassedInListener() throws Exception {
        // arrange
        overriddenDefaults.setProperty("key", "value");
        // act
        ProcessLauncherContext.set(redirectOutput, overriddenDefaults, startupListener);
        // assert
        assertThat(ProcessLauncherContext.getStartupListener()).isSameAs(startupListener);
    }

    @Test
    public void remove_clearsOverriddenDefaults() throws Exception {
        // arrange
        overriddenDefaults.setProperty("key", "value");
        ProcessLauncherContext.set(false, overriddenDefaults, startupListener);
        // act
        ProcessLauncherContext.remove();
        // assert
        assertThat(ProcessLauncherContext.getOverriddenDefaults()).isEmpty();
    }

    @Test
    public void remove_unsetsRedirectOutput() throws Exception {
        // arrange
        redirectOutput = true;
        ProcessLauncherContext.set(redirectOutput, overriddenDefaults, startupListener);
        // act
        ProcessLauncherContext.remove();
        // assert
        assertThat(ProcessLauncherContext.isRedirectingOutput()).isFalse();
    }

    @Test
    public void remove_clearsStartupListener() throws Exception {
        // arrange
        startupListener = ( statusMessage) -> statusMessageList.add(statusMessage);
        ProcessLauncherContext.set(redirectOutput, overriddenDefaults, startupListener);
        // act
        ProcessLauncherContext.remove();
        // assert
        assertThat(ProcessLauncherContext.getStartupListener()).isNull();
    }

    @Test
    public void startupListener_installsInStartupStatus() throws Exception {
        // arrange
        startupListener = ( statusMessage) -> statusMessageList.add(statusMessage);
        // act
        ProcessLauncherContext.set(redirectOutput, overriddenDefaults, startupListener);
        // assert
        assertThat(StartupStatus.getStartupListener()).isSameAs(startupListener);
    }

    @Test
    public void remove_uninstallsInStartupStatus() throws Exception {
        // arrange
        startupListener = ( statusMessage) -> statusMessageList.add(statusMessage);
        ProcessLauncherContext.set(redirectOutput, overriddenDefaults, startupListener);
        // act
        ProcessLauncherContext.remove();
        // assert
        assertThat(StartupStatus.getStartupListener()).isNull();
    }
}

