/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.util.validators;


import com.thoughtworks.go.util.SystemEnvironment;
import java.io.File;
import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.Mockito;


public class JettyWorkDirValidatorTest {
    private JettyWorkDirValidator jettyWorkDirValidator;

    @Mock
    public SystemEnvironment systemEnvironment;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File homeDir;

    @Test
    public void shouldSetJettyHomeAndBasePropertyIfItsNotSet() {
        Mockito.when(systemEnvironment.getPropertyImpl("jetty.home")).thenReturn("");
        Mockito.when(systemEnvironment.getPropertyImpl("user.dir")).thenReturn("junk");
        Validation val = new Validation();
        jettyWorkDirValidator.validate(val);
        Assert.assertThat(val.isSuccessful(), Matchers.is(true));
        Mockito.verify(systemEnvironment).getPropertyImpl("user.dir");
        Mockito.verify(systemEnvironment).setProperty("jetty.home", "junk");
    }

    @Test
    public void shouldSetJettyBaseToValueOfJettyHome() {
        Mockito.when(systemEnvironment.getPropertyImpl("jetty.home")).thenReturn("foo");
        Validation val = new Validation();
        jettyWorkDirValidator.validate(val);
        Assert.assertThat(val.isSuccessful(), Matchers.is(true));
        Mockito.verify(systemEnvironment).setProperty("jetty.base", "foo");
    }

    @Test
    public void shouldCreateWorkDirIfItDoesNotExist() {
        Mockito.when(systemEnvironment.getPropertyImpl("jetty.home")).thenReturn(homeDir.getAbsolutePath());
        Validation val = new Validation();
        jettyWorkDirValidator.validate(val);
        Assert.assertThat(val.isSuccessful(), Matchers.is(true));
        File work = new File(homeDir, "work");
        Assert.assertThat(work.exists(), Matchers.is(true));
    }

    @Test
    public void shouldNotCreateTheJettyHomeDirIfItDoesNotExist() {
        String jettyHome = "home_dir";
        Mockito.when(systemEnvironment.getPropertyImpl("jetty.home")).thenReturn(jettyHome);
        Validation val = new Validation();
        jettyWorkDirValidator.validate(val);
        Assert.assertThat(val.isSuccessful(), Matchers.is(true));
        Assert.assertThat(new File(jettyHome).exists(), Matchers.is(false));
    }

    @Test
    public void shouldRecreateWorkDirIfItExists() throws IOException {
        File oldWorkDir = new File(homeDir, "work");
        oldWorkDir.mkdir();
        new File(oldWorkDir, "junk.txt").createNewFile();
        Mockito.when(systemEnvironment.getPropertyImpl("jetty.home")).thenReturn(homeDir.getAbsolutePath());
        Validation val = new Validation();
        jettyWorkDirValidator.validate(val);
        Assert.assertThat(val.isSuccessful(), Matchers.is(true));
        File recreatedWorkDir = new File(homeDir, "work");
        Assert.assertThat(recreatedWorkDir.exists(), Matchers.is(true));
        Assert.assertThat(recreatedWorkDir.listFiles().length, Matchers.is(0));
    }
}

