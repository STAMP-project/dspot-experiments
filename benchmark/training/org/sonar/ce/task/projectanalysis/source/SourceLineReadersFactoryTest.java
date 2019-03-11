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
package org.sonar.ce.task.projectanalysis.source;


import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.ce.task.projectanalysis.batch.BatchReportReaderRule;
import org.sonar.ce.task.projectanalysis.component.TreeRootHolderRule;
import org.sonar.ce.task.projectanalysis.duplication.DuplicationRepositoryRule;
import org.sonar.ce.task.projectanalysis.scm.ScmInfoRepositoryRule;
import org.sonar.ce.task.projectanalysis.source.SourceLineReadersFactory.LineReadersImpl;


public class SourceLineReadersFactoryTest {
    private static final int FILE1_REF = 3;

    private static final String PROJECT_UUID = "PROJECT";

    private static final String PROJECT_KEY = "PROJECT_KEY";

    private static final String FILE1_UUID = "FILE1";

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule();

    @Rule
    public BatchReportReaderRule reportReader = new BatchReportReaderRule();

    @Rule
    public ScmInfoRepositoryRule scmInfoRepository = new ScmInfoRepositoryRule();

    @Rule
    public DuplicationRepositoryRule duplicationRepository = DuplicationRepositoryRule.create(treeRootHolder);

    private NewLinesRepository newLinesRepository = Mockito.mock(NewLinesRepository.class);

    private SourceLineReadersFactory underTest = new SourceLineReadersFactory(reportReader, scmInfoRepository, duplicationRepository, newLinesRepository);

    @Test
    public void should_create_readers() {
        initBasicReport(10);
        LineReadersImpl lineReaders = ((LineReadersImpl) (underTest.getLineReaders(fileComponent())));
        assertThat(lineReaders).isNotNull();
        assertThat(lineReaders.closeables).hasSize(3);
        assertThat(lineReaders.readers).hasSize(5);
    }
}

