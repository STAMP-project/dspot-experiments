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
package org.sonar.scanner.genericcoverage;


import GenericTestExecutionSensor.OLD_UNIT_TEST_REPORT_PATHS_PROPERTY_KEY;
import LoggerLevel.INFO;
import LoggerLevel.WARN;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.config.Encryption;
import org.sonar.api.config.PropertyDefinitions;
import org.sonar.api.utils.log.LogTester;
import org.sonar.scanner.config.DefaultConfiguration;
import org.sonar.scanner.deprecated.test.TestPlanBuilder;


public class GenericTestExecutionSensorTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public LogTester logTester = new LogTester();

    @Test
    public void logWhenDeprecatedPropsAreUsed() throws IOException {
        File basedir = temp.newFolder();
        File report = new File(basedir, "report.xml");
        FileUtils.write(report, "<unitTest version=\"1\"><file path=\"A.java\"><testCase name=\"test1\" duration=\"500\"/></file></unitTest>", StandardCharsets.UTF_8);
        SensorContextTester context = SensorContextTester.create(basedir);
        Map<String, String> settings = new HashMap<>();
        settings.put(OLD_UNIT_TEST_REPORT_PATHS_PROPERTY_KEY, "report.xml");
        PropertyDefinitions defs = new PropertyDefinitions(GenericTestExecutionSensor.properties());
        DefaultConfiguration config = new org.sonar.scanner.scan.ProjectConfiguration(defs, new Encryption(null), settings);
        new GenericTestExecutionSensor(Mockito.mock(TestPlanBuilder.class), config).execute(context);
        assertThat(logTester.logs(WARN)).contains("Using 'unitTest' as root element of the report is deprecated. Please change to 'testExecutions'.", "Property 'sonar.genericcoverage.unitTestReportPaths' is deprecated. Please use 'sonar.testExecutionReportPaths' instead.");
        assertThat(logTester.logs(INFO)).contains("Imported test execution data for 0 files", "Test execution data ignored for 1 unknown files, including:\nA.java");
    }
}

