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
package org.apache.geode.management.internal.cli.functions;


import ExportLogsCommand.DEFAULT_EXPORT_LOG_LEVEL;
import ExportLogsFunction.Args;
import Level.ALL;
import Level.DEBUG;
import Level.ERROR;
import org.apache.geode.internal.logging.log4j.LogLevel;
import org.apache.geode.test.junit.categories.GfshTest;
import org.apache.geode.test.junit.categories.LoggingTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ GfshTest.class, LoggingTest.class })
public class ExportLogsFunctionTest {
    @Test
    public void defaultExportLogLevelShouldBeAll() {
        Assert.assertTrue(DEFAULT_EXPORT_LOG_LEVEL.equals("ALL"));
        Assert.assertEquals(LogLevel.getLevel(DEFAULT_EXPORT_LOG_LEVEL), ALL);
    }

    @Test
    public void defaultExportLogLevelShouldBeAllViaArgs() {
        ExportLogsFunction.Args args = new ExportLogsFunction.Args("", "", "", false, false, false);
        Assert.assertEquals(args.getLogLevel(), ALL);
        ExportLogsFunction.Args args2 = new ExportLogsFunction.Args("", "", null, false, false, false);
        Assert.assertEquals(args2.getLogLevel(), ALL);
    }

    @Test
    public void argsCorrectlyBuildALogLevelFilter() {
        ExportLogsFunction.Args args = new ExportLogsFunction.Args(null, null, "info", false, false, false);
        assertThat(args.getLogLevel().toString()).isEqualTo("INFO");
        assertThat(args.isThisLogLevelOnly()).isFalse();
        assertThat(args.isIncludeLogs()).isTrue();
        assertThat(args.isIncludeStats()).isTrue();
    }

    @Test
    public void argsCorrectlyBuilt() {
        ExportLogsFunction.Args args = new ExportLogsFunction.Args(null, null, "error", true, true, false);
        assertThat(args.getLogLevel()).isEqualTo(ERROR);
        assertThat(args.isThisLogLevelOnly()).isTrue();
        assertThat(args.isIncludeLogs()).isTrue();
        assertThat(args.isIncludeStats()).isFalse();
    }

    @Test
    public void argsCorrectlyBuiltWithGeodeLevel() {
        ExportLogsFunction.Args args = new ExportLogsFunction.Args(null, null, "fine", true, true, false);
        assertThat(args.getLogLevel()).isEqualTo(DEBUG);
        assertThat(args.isThisLogLevelOnly()).isTrue();
        assertThat(args.isIncludeLogs()).isTrue();
        assertThat(args.isIncludeStats()).isFalse();
    }
}

