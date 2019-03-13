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
package org.sonar.xoo.scm;


import Xoo.KEY;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.sonar.api.batch.fs.internal.DefaultFileSystem;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.scm.BlameCommand.BlameInput;
import org.sonar.api.batch.scm.BlameCommand.BlameOutput;
import org.sonar.api.batch.scm.BlameLine;
import org.sonar.api.utils.DateUtils;


public class XooBlameCommandTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private DefaultFileSystem fs;

    private File baseDir;

    private BlameInput input;

    @Test
    public void testBlame() throws IOException {
        File source = new File(baseDir, "src/foo.xoo");
        FileUtils.write(source, "sample content");
        File scm = new File(baseDir, "src/foo.xoo.scm");
        FileUtils.write(scm, "123,julien,2014-12-12\n234,julien,2014-12-24");
        DefaultInputFile inputFile = new TestInputFileBuilder("foo", "src/foo.xoo").setLanguage(KEY).setModuleBaseDir(baseDir.toPath()).build();
        fs.add(inputFile);
        BlameOutput result = Mockito.mock(BlameOutput.class);
        Mockito.when(input.filesToBlame()).thenReturn(Arrays.asList(inputFile));
        new XooBlameCommand().blame(input, result);
        Mockito.verify(result).blameResult(inputFile, Arrays.asList(new BlameLine().revision("123").author("julien").date(DateUtils.parseDate("2014-12-12")), new BlameLine().revision("234").author("julien").date(DateUtils.parseDate("2014-12-24"))));
    }

    @Test
    public void blame_containing_author_with_comma() throws IOException {
        File source = new File(baseDir, "src/foo.xoo");
        FileUtils.write(source, "sample content");
        File scm = new File(baseDir, "src/foo.xoo.scm");
        FileUtils.write(scm, "\"123\",\"john,doe\",\"2019-01-22\"");
        DefaultInputFile inputFile = new TestInputFileBuilder("foo", "src/foo.xoo").setLanguage(KEY).setModuleBaseDir(baseDir.toPath()).build();
        fs.add(inputFile);
        BlameOutput result = Mockito.mock(BlameOutput.class);
        Mockito.when(input.filesToBlame()).thenReturn(Arrays.asList(inputFile));
        new XooBlameCommand().blame(input, result);
        Mockito.verify(result).blameResult(inputFile, Collections.singletonList(new BlameLine().revision("123").author("john,doe").date(DateUtils.parseDate("2019-01-22"))));
    }
}

