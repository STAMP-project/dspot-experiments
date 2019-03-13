/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.client;


import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.drill.common.util.DrillVersionInfo;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import sqlline.Application;
import sqlline.CommandHandler;
import sqlline.OutputFormat;
import sqlline.SqlLine;
import sqlline.SqlLineOpts;


public class DrillSqlLineApplicationTest {
    private static Application application;

    @Test
    public void testInfoMessage() {
        String infoMessage = DrillSqlLineApplicationTest.application.getInfoMessage();
        Assert.assertThat(infoMessage, CoreMatchers.containsString("\"All code is guilty until proven innocent.\""));
    }

    @Test
    public void testVersion() {
        Assert.assertEquals(DrillVersionInfo.getVersion(), DrillSqlLineApplicationTest.application.getVersion());
    }

    @Test
    public void testDrivers() {
        Collection<String> drivers = DrillSqlLineApplicationTest.application.initDrivers();
        Assert.assertEquals(1L, drivers.size());
        Assert.assertEquals("org.apache.drill.jdbc.Driver", drivers.iterator().next());
    }

    @Test
    public void testOutputFormats() {
        SqlLine sqlLine = new SqlLine();
        Map<String, OutputFormat> outputFormats = DrillSqlLineApplicationTest.application.getOutputFormats(sqlLine);
        Assert.assertEquals(sqlLine.getOutputFormats(), outputFormats);
    }

    @Test
    public void testConnectionUrlExamples() {
        Collection<String> examples = DrillSqlLineApplicationTest.application.getConnectionUrlExamples();
        Assert.assertEquals(1L, examples.size());
        Assert.assertEquals("jdbc:drill:zk=local", examples.iterator().next());
    }

    @Test
    public void testCommandHandlers() {
        SqlLine sqlLine = new SqlLine();
        Collection<CommandHandler> commandHandlers = DrillSqlLineApplicationTest.application.getCommandHandlers(sqlLine);
        List<String> excludedCommands = Arrays.asList("describe", "indexes");
        List<CommandHandler> matchingCommands = commandHandlers.stream().filter(( c) -> c.getNames().stream().anyMatch(excludedCommands::contains)).collect(Collectors.toList());
        Assert.assertTrue(matchingCommands.isEmpty());
    }

    @Test
    public void testOpts() {
        SqlLine sqlLine = new SqlLine();
        SqlLineOpts opts = DrillSqlLineApplicationTest.application.getOpts(sqlLine);
        Assert.assertFalse(opts.getIncremental());
        Assert.assertEquals("TRANSACTION_NONE", opts.getIsolation());
        Assert.assertEquals(80, opts.getMaxColumnWidth());
        Assert.assertEquals(200, opts.getTimeout());
        Assert.assertEquals("obsidian", opts.getColorScheme());
        Assert.assertEquals("null", opts.getNullValue());
    }

    @Test
    public void testEmptyConfig() {
        DrillSqlLineApplication application = new DrillSqlLineApplication("missing.conf", "missing_override.conf");
        Assert.assertTrue(application.getConfig().isEmpty());
    }

    @Test
    public void testConfigWithoutOverride() {
        DrillSqlLineApplication application = new DrillSqlLineApplication("drill-sqlline.conf", "missing_override.conf");
        Assert.assertFalse(application.getConfig().isEmpty());
    }
}

