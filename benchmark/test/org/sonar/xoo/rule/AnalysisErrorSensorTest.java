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
package org.sonar.xoo.rule;


import java.io.IOException;
import java.nio.file.Path;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.DefaultTextPointer;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.error.AnalysisError;
import org.sonar.api.batch.sensor.internal.SensorContextTester;


public class AnalysisErrorSensorTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private AnalysisErrorSensor sensor;

    private SensorContextTester context;

    @Test
    public void test() throws IOException {
        Path baseDir = temp.newFolder().toPath().toAbsolutePath();
        createErrorFile(baseDir);
        int[] startOffsets = new int[]{ 10, 20, 30, 40 };
        int[] endOffsets = new int[]{ 19, 29, 39, 49 };
        DefaultInputFile inputFile = new TestInputFileBuilder("foo", "src/foo.xoo").setLanguage("xoo").setOriginalLineStartOffsets(startOffsets).setOriginalLineEndOffsets(endOffsets).setModuleBaseDir(baseDir).setLines(4).build();
        context = SensorContextTester.create(baseDir);
        context.fileSystem().add(inputFile);
        sensor.execute(context);
        assertThat(context.allAnalysisErrors()).hasSize(1);
        AnalysisError error = context.allAnalysisErrors().iterator().next();
        assertThat(error.inputFile()).isEqualTo(inputFile);
        assertThat(error.location()).isEqualTo(new DefaultTextPointer(1, 4));
        assertThat(error.message()).isEqualTo("my error");
    }
}

