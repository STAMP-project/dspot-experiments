/**
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.bootique.config;


import io.bootique.cli.Cli;
import io.bootique.log.BootLogger;
import java.net.URL;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


public class CliConfigurationSourceTest {
    private BootLogger mockBootLogger;

    private Function<URL, String> configReader;

    @Test
    public void testGet_File() {
        Cli cli = CliConfigurationSourceTest.createCli("src/test/resources/io/bootique/config/test1.yml");
        String config = CliConfigurationSource.builder(mockBootLogger).cliConfigs(cli).build().get().map(configReader).collect(Collectors.joining(","));
        Assert.assertEquals("a: b", config);
    }

    @Test
    public void testGet_FileUrl() {
        String url = fileUrl("src/test/resources/io/bootique/config/test2.yml");
        Cli cli = CliConfigurationSourceTest.createCli(url);
        String config = CliConfigurationSource.builder(mockBootLogger).cliConfigs(cli).build().get().map(configReader).collect(Collectors.joining(","));
        Assert.assertEquals("c: d", config);
    }

    @Test
    public void testGet_MultipleFiles1() {
        Cli cli = CliConfigurationSourceTest.createCli("src/test/resources/io/bootique/config/test2.yml", "src/test/resources/io/bootique/config/test1.yml");
        String config = CliConfigurationSource.builder(mockBootLogger).cliConfigs(cli).build().get().map(configReader).collect(Collectors.joining(","));
        Assert.assertEquals("c: d,a: b", config);
    }

    @Test
    public void testGet_MultipleFiles2() {
        // change file order compared to testGet_MultipleFiles1
        Cli cli = CliConfigurationSourceTest.createCli("src/test/resources/io/bootique/config/test1.yml", "src/test/resources/io/bootique/config/test2.yml");
        String config = CliConfigurationSource.builder(mockBootLogger).cliConfigs(cli).build().get().map(configReader).collect(Collectors.joining(","));
        Assert.assertEquals("a: b,c: d", config);
    }

    @Test
    public void testGet_JarUrl() {
        String url = jarEntryUrl("src/test/resources/io/bootique/config/test3.jar", "com/foo/test3.yml");
        Cli cli = CliConfigurationSourceTest.createCli(url);
        String config = CliConfigurationSource.builder(mockBootLogger).cliConfigs(cli).build().get().map(configReader).collect(Collectors.joining(","));
        Assert.assertEquals("e: f", config);
    }

    @Test
    public void testGet_ClasspathUrl() {
        String url = "classpath:io/bootique/config/test2.yml";
        Cli cli = CliConfigurationSourceTest.createCli(url);
        String config = CliConfigurationSource.builder(mockBootLogger).cliConfigs(cli).build().get().map(configReader).collect(Collectors.joining(","));
        Assert.assertEquals("c: d", config);
    }
}

