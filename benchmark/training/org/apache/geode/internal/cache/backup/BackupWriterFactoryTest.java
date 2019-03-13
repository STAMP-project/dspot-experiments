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
package org.apache.geode.internal.cache.backup;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import org.junit.Test;


public class BackupWriterFactoryTest {
    @Test
    public void returnsCorrectFactoryForName() {
        assertThat(BackupWriterFactory.getFactoryForType("FileSystem")).isEqualTo(BackupWriterFactory.FILE_SYSTEM);
    }

    @Test
    public void throwsExceptionWhenFactoryForInvalidNameGiven() {
        assertThatThrownBy(() -> BackupWriterFactory.getFactoryForType("badName")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getType() {
        assertThat(BackupWriterFactory.FILE_SYSTEM.getType()).isEqualTo("FileSystem");
    }

    @Test
    public void returnsCorrectWriterType() {
        Properties properties = new Properties();
        properties.setProperty(AbstractBackupWriterConfig.TYPE, BackupWriterFactory.FILE_SYSTEM.getType());
        properties.setProperty(AbstractBackupWriterConfig.TIMESTAMP, "yyyy-MM-dd-HH-mm-ss");
        properties.setProperty(FileSystemBackupWriterConfig.TARGET_DIR, "targetDir");
        properties.setProperty(FileSystemBackupWriterConfig.BASELINE_DIR, "baselineDir");
        assertThat(BackupWriterFactory.FILE_SYSTEM.createWriter(properties, "memberId")).isInstanceOf(FileSystemBackupWriter.class);
    }

    @Test
    public void returnedWriterHasAbsolutePathToBaselineDirectory() {
        Properties properties = new Properties();
        properties.setProperty(AbstractBackupWriterConfig.TYPE, BackupWriterFactory.FILE_SYSTEM.getType());
        properties.setProperty(AbstractBackupWriterConfig.TIMESTAMP, "yyyy-MM-dd-HH-mm-ss");
        properties.setProperty(FileSystemBackupWriterConfig.TARGET_DIR, "targetDir");
        properties.setProperty(FileSystemBackupWriterConfig.BASELINE_DIR, "baselineDir");
        BackupWriter writer = BackupWriterFactory.FILE_SYSTEM.createWriter(properties, "memberId");
        Path absoluteBaseLineDirectory = Paths.get("baselineDir").toAbsolutePath();
        assertThat(writer.getBaselineDirectory()).isAbsolute().isEqualTo(absoluteBaseLineDirectory);
    }
}

