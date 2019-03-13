/**
 * -
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 - 2017 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.docker.client.auth;


import com.google.common.collect.ImmutableMap;
import com.spotify.docker.client.DockerConfigReader;
import com.spotify.docker.client.messages.RegistryAuth;
import com.spotify.docker.client.messages.RegistryConfigs;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ConfigFileRegistryAuthSupplierTest {
    private final DockerConfigReader reader = Mockito.mock(DockerConfigReader.class);

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private File configFile;

    private ConfigFileRegistryAuthSupplier supplier;

    @Test
    public void testAuthFor_ConfigFileDoesNotExist() throws Exception {
        Assert.assertTrue("unable to delete file", configFile.delete());
        // sanity check
        Assert.assertFalse((("cannot continue this test if the file " + (configFile)) + " actually exists"), configFile.exists());
        Assert.assertThat(supplier.authFor("foo.example.net/bar:1.2.3"), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testAuthFor_ConfigFileEmptyFile() throws Exception {
        Assert.assertEquals("file is not empty", 0, Files.readAllBytes(configFile.toPath()).length);
        Assert.assertThat(supplier.authFor("foo.example.net/bar:1.2.3"), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testAuthFor_Success() throws Exception {
        final RegistryAuth auth = RegistryAuth.builder().username("abc123").build();
        Mockito.when(reader.authForRegistry(configFile.toPath(), "foo.example.net")).thenReturn(auth);
        Assert.assertThat(supplier.authFor("foo.example.net/bar:1.2.3"), Matchers.is(Matchers.equalTo(auth)));
    }

    @Test
    public void testAuthForSwarm_Unimplemented() throws Exception {
        Assert.assertThat(supplier.authForSwarm(), Matchers.is(Matchers.nullValue()));
        // force future implementors of this method to write a test
        Mockito.verify(reader, Mockito.never()).authForAllRegistries(ArgumentMatchers.any(Path.class));
    }

    @Test
    public void testAuthForBuild_ConfigFileDoesNotExist() throws Exception {
        Assert.assertTrue("unable to delete file", configFile.delete());
        // sanity check
        Assert.assertFalse((("cannot continue this test if the file " + (configFile)) + " actually exists"), configFile.exists());
        Assert.assertThat(supplier.authForBuild(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testAuthForBuild_ConfigFileEmptyFile() throws Exception {
        Assert.assertEquals("file is not empty", 0, Files.readAllBytes(configFile.toPath()).length);
        Assert.assertThat(supplier.authForBuild(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testAuthForBuild_Success() throws Exception {
        final RegistryConfigs configs = RegistryConfigs.create(ImmutableMap.of("server1", RegistryAuth.builder().serverAddress("server1").username("user1").password("pass1").build()));
        Mockito.when(reader.authForAllRegistries(configFile.toPath())).thenReturn(configs);
        Assert.assertThat(supplier.authForBuild(), Matchers.is(Matchers.equalTo(configs)));
    }
}

