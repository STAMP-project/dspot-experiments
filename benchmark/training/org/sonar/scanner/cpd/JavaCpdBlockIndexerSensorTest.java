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
package org.sonar.scanner.cpd;


import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.duplications.block.Block;
import org.sonar.scanner.cpd.index.SonarCpdBlockIndex;


public class JavaCpdBlockIndexerSensorTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private SensorContextTester context;

    @Mock
    private SonarCpdBlockIndex index;

    @Captor
    private ArgumentCaptor<List<Block>> blockCaptor;

    private DefaultInputFile file;

    @Test
    public void testExclusions() {
        file.setExcludedForDuplication(true);
        new JavaCpdBlockIndexerSensor(index).execute(context);
        Mockito.verifyZeroInteractions(index);
    }

    @Test
    public void testJavaIndexing() {
        new JavaCpdBlockIndexerSensor(index).execute(context);
        Mockito.verify(index).insert(ArgumentMatchers.eq(file), blockCaptor.capture());
        List<Block> blockList = blockCaptor.getValue();
        assertThat(blockList).hasSize(26);
    }
}

