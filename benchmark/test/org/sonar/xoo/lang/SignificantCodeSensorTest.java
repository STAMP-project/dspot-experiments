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
package org.sonar.xoo.lang;


import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;


public class SignificantCodeSensorTest {
    private SignificantCodeSensor sensor;

    private SensorContextTester context;

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private File baseDir;

    private InputFile inputFile;

    @Test
    public void testDescriptor() {
        sensor.describe(new DefaultSensorDescriptor());
    }

    @Test
    public void testNoExceptionIfNoFileWithOffsets() {
        context.fileSystem().add(inputFile);
        sensor.execute(context);
    }

    @Test
    public void testExecution() throws IOException {
        File significantCode = new File(baseDir, "src/foo.xoo.significantCode");
        FileUtils.write(significantCode, "1,1,4\n2,2,5");
        context.fileSystem().add(inputFile);
        sensor.execute(context);
        assertThat(context.significantCodeTextRange("foo:src/foo.xoo", 1)).isEqualTo(SignificantCodeSensorTest.range(1, 1, 4));
        assertThat(context.significantCodeTextRange("foo:src/foo.xoo", 2)).isEqualTo(SignificantCodeSensorTest.range(2, 2, 5));
    }
}

