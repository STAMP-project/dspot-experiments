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
package org.sonar.scanner.report;


import java.io.File;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.scanner.protocol.output.ScannerReportWriter;


public class SourcePublisherTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private SourcePublisher publisher;

    private File sourceFile;

    private ScannerReportWriter writer;

    private DefaultInputFile inputFile;

    @Test
    public void publishEmptySource() throws Exception {
        FileUtils.write(sourceFile, "", StandardCharsets.ISO_8859_1);
        publisher.publish(writer);
        File out = writer.getSourceFile(inputFile.scannerId());
        assertThat(FileUtils.readFileToString(out, StandardCharsets.UTF_8)).isEqualTo("");
    }

    @Test
    public void publishSourceWithLastEmptyLine() throws Exception {
        FileUtils.write(sourceFile, "1\n2\n3\n4\n", StandardCharsets.ISO_8859_1);
        publisher.publish(writer);
        File out = writer.getSourceFile(inputFile.scannerId());
        assertThat(FileUtils.readFileToString(out, StandardCharsets.UTF_8)).isEqualTo("1\n2\n3\n4\n");
    }

    @Test
    public void publishTestSource() throws Exception {
        FileUtils.write(sourceFile, "1\n2\n3\n4\n", StandardCharsets.ISO_8859_1);
        // sampleFile.setQualifier(Qualifiers.UNIT_TEST_FILE);
        publisher.publish(writer);
        File out = writer.getSourceFile(inputFile.scannerId());
        assertThat(FileUtils.readFileToString(out, StandardCharsets.UTF_8)).isEqualTo("1\n2\n3\n4\n");
    }

    @Test
    public void publishSourceWithLastLineNotEmpty() throws Exception {
        FileUtils.write(sourceFile, "1\n2\n3\n4\n5", StandardCharsets.ISO_8859_1);
        publisher.publish(writer);
        File out = writer.getSourceFile(inputFile.scannerId());
        assertThat(FileUtils.readFileToString(out, StandardCharsets.UTF_8)).isEqualTo("1\n2\n3\n4\n5");
    }

    @Test
    public void cleanLineEnds() throws Exception {
        FileUtils.write(sourceFile, "\n2\r\n3\n4\r5", StandardCharsets.ISO_8859_1);
        publisher.publish(writer);
        File out = writer.getSourceFile(inputFile.scannerId());
        assertThat(FileUtils.readFileToString(out, StandardCharsets.UTF_8)).isEqualTo("\n2\n3\n4\n5");
    }
}

