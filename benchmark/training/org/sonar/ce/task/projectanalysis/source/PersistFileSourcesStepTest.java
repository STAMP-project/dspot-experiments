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


import DbFileSources.Data;
import DbFileSources.Line;
import LineHashVersion.WITHOUT_SIGNIFICANT_CODE;
import SourceLinesHashRepositoryImpl.LineHashesComputer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.ce.task.projectanalysis.component.TreeRootHolderRule;
import org.sonar.ce.task.projectanalysis.scm.Changeset;
import org.sonar.ce.task.projectanalysis.step.BaseStepTest;
import org.sonar.ce.task.step.TestComputationStepContext;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.protobuf.DbFileSources;
import org.sonar.db.source.FileSourceDto;


public class PersistFileSourcesStepTest extends BaseStepTest {
    private static final int FILE1_REF = 3;

    private static final String PROJECT_UUID = "PROJECT";

    private static final String PROJECT_KEY = "PROJECT_KEY";

    private static final String FILE1_UUID = "FILE1";

    private static final long NOW = 123456789L;

    private static final long PAST = 15000L;

    private System2 system2 = Mockito.mock(System2.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public DbTester dbTester = DbTester.create(system2);

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule();

    private SourceLinesHashRepository sourceLinesHashRepository = Mockito.mock(SourceLinesHashRepository.class);

    private LineHashesComputer lineHashesComputer = Mockito.mock(LineHashesComputer.class);

    private FileSourceDataComputer fileSourceDataComputer = Mockito.mock(FileSourceDataComputer.class);

    private FileSourceDataWarnings fileSourceDataWarnings = Mockito.mock(FileSourceDataWarnings.class);

    private DbClient dbClient = dbTester.getDbClient();

    private DbSession session = dbTester.getSession();

    private PersistFileSourcesStep underTest;

    @Test
    public void persist_sources() {
        List<String> lineHashes = Arrays.asList("137f72c3708c6bd0de00a0e5a69c699b", "e6251bcf1a7dc3ba5e7933e325bbe605");
        String sourceHash = "ee5a58024a155466b43bc559d953e018";
        DbFileSources.Data fileSourceData = Data.newBuilder().addAllLines(Arrays.asList(Line.newBuilder().setSource("line1").setLine(1).build(), Line.newBuilder().setSource("line2").setLine(2).build())).build();
        Mockito.when(fileSourceDataComputer.compute(fileComponent().build(), fileSourceDataWarnings)).thenReturn(new FileSourceDataComputer.Data(fileSourceData, lineHashes, sourceHash, null));
        underTest.execute(new TestComputationStepContext());
        assertThat(dbTester.countRowsOfTable("file_sources")).isEqualTo(1);
        FileSourceDto fileSourceDto = dbClient.fileSourceDao().selectByFileUuid(session, PersistFileSourcesStepTest.FILE1_UUID);
        assertThat(fileSourceDto.getProjectUuid()).isEqualTo(PersistFileSourcesStepTest.PROJECT_UUID);
        assertThat(fileSourceDto.getFileUuid()).isEqualTo(PersistFileSourcesStepTest.FILE1_UUID);
        assertThat(fileSourceDto.getBinaryData()).isNotEmpty();
        assertThat(fileSourceDto.getDataHash()).isNotEmpty();
        assertThat(fileSourceDto.getLineHashesVersion()).isEqualTo(WITHOUT_SIGNIFICANT_CODE.getDbValue());
        assertThat(fileSourceDto.getLineHashes()).isNotEmpty();
        assertThat(fileSourceDto.getCreatedAt()).isEqualTo(PersistFileSourcesStepTest.NOW);
        assertThat(fileSourceDto.getUpdatedAt()).isEqualTo(PersistFileSourcesStepTest.NOW);
        DbFileSources.Data data = fileSourceDto.getSourceData();
        assertThat(data.getLinesCount()).isEqualTo(2);
        assertThat(data.getLines(0).getLine()).isEqualTo(1);
        assertThat(data.getLines(0).getSource()).isEqualTo("line1");
        assertThat(data.getLines(1).getLine()).isEqualTo(2);
        assertThat(data.getLines(1).getSource()).isEqualTo("line2");
        Mockito.verify(fileSourceDataWarnings).commitWarnings();
    }

    @Test
    public void persist_source_hashes() {
        List<String> lineHashes = Arrays.asList("137f72c3708c6bd0de00a0e5a69c699b", "e6251bcf1a7dc3ba5e7933e325bbe605");
        String sourceHash = "ee5a58024a155466b43bc559d953e018";
        setComputedData(Data.newBuilder().build(), lineHashes, sourceHash, null);
        underTest.execute(new TestComputationStepContext());
        assertThat(dbTester.countRowsOfTable("file_sources")).isEqualTo(1);
        FileSourceDto fileSourceDto = dbClient.fileSourceDao().selectByFileUuid(session, PersistFileSourcesStepTest.FILE1_UUID);
        assertThat(fileSourceDto.getLineHashes()).containsExactly("137f72c3708c6bd0de00a0e5a69c699b", "e6251bcf1a7dc3ba5e7933e325bbe605");
        assertThat(fileSourceDto.getSrcHash()).isEqualTo("ee5a58024a155466b43bc559d953e018");
        Mockito.verify(fileSourceDataWarnings).commitWarnings();
    }

    @Test
    public void persist_coverage() {
        DbFileSources.Data dbData = Data.newBuilder().addLines(Line.newBuilder().setConditions(10).setCoveredConditions(2).setLineHits(1).setLine(1).build()).build();
        setComputedData(dbData);
        underTest.execute(new TestComputationStepContext());
        assertThat(dbTester.countRowsOfTable("file_sources")).isEqualTo(1);
        FileSourceDto fileSourceDto = dbClient.fileSourceDao().selectByFileUuid(session, PersistFileSourcesStepTest.FILE1_UUID);
        assertThat(fileSourceDto.getSourceData()).isEqualTo(dbData);
        Mockito.verify(fileSourceDataWarnings).commitWarnings();
    }

    @Test
    public void persist_scm() {
        DbFileSources.Data dbData = Data.newBuilder().addLines(Line.newBuilder().setScmAuthor("john").setScmDate(123456789L).setScmRevision("rev-1").build()).build();
        setComputedData(dbData);
        underTest.execute(new TestComputationStepContext());
        assertThat(dbTester.countRowsOfTable("file_sources")).isEqualTo(1);
        FileSourceDto fileSourceDto = dbClient.fileSourceDao().selectByFileUuid(session, PersistFileSourcesStepTest.FILE1_UUID);
        assertThat(fileSourceDto.getSourceData()).isEqualTo(dbData);
        assertThat(fileSourceDto.getRevision()).isNull();
        Mockito.verify(fileSourceDataWarnings).commitWarnings();
    }

    @Test
    public void persist_scm_some_lines() {
        DbFileSources.Data dbData = Data.newBuilder().addAllLines(Arrays.asList(Line.newBuilder().setScmAuthor("john").setScmDate(123456789L).setScmRevision("rev-1").build(), Line.newBuilder().setScmDate(223456789L).build(), Line.newBuilder().build())).build();
        setComputedData(dbData);
        underTest.execute(new TestComputationStepContext());
        assertThat(dbTester.countRowsOfTable("file_sources")).isEqualTo(1);
        FileSourceDto fileSourceDto = dbClient.fileSourceDao().selectByFileUuid(session, PersistFileSourcesStepTest.FILE1_UUID);
        DbFileSources.Data data = fileSourceDto.getSourceData();
        assertThat(data.getLinesList()).hasSize(3);
        assertThat(data.getLines(0).getScmAuthor()).isEqualTo("john");
        assertThat(data.getLines(0).getScmDate()).isEqualTo(123456789L);
        assertThat(data.getLines(0).getScmRevision()).isEqualTo("rev-1");
        assertThat(data.getLines(1).getScmAuthor()).isEmpty();
        assertThat(data.getLines(1).getScmDate()).isEqualTo(223456789L);
        assertThat(data.getLines(1).getScmRevision()).isEmpty();
        assertThat(data.getLines(2).getScmAuthor()).isEmpty();
        assertThat(data.getLines(2).getScmDate()).isEqualTo(0);
        assertThat(data.getLines(2).getScmRevision()).isEmpty();
        Mockito.verify(fileSourceDataWarnings).commitWarnings();
    }

    @Test
    public void persist_highlighting() {
        DbFileSources.Data dbData = Data.newBuilder().addLines(Line.newBuilder().setHighlighting("2,4,a").build()).build();
        setComputedData(dbData);
        underTest.execute(new TestComputationStepContext());
        assertThat(dbTester.countRowsOfTable("file_sources")).isEqualTo(1);
        FileSourceDto fileSourceDto = dbClient.fileSourceDao().selectByFileUuid(session, PersistFileSourcesStepTest.FILE1_UUID);
        DbFileSources.Data data = fileSourceDto.getSourceData();
        assertThat(data).isEqualTo(dbData);
        assertThat(data.getLinesList()).hasSize(1);
        assertThat(data.getLines(0).getHighlighting()).isEqualTo("2,4,a");
        Mockito.verify(fileSourceDataWarnings).commitWarnings();
    }

    @Test
    public void persist_symbols() {
        DbFileSources.Data dbData = Data.newBuilder().addAllLines(Arrays.asList(Line.newBuilder().setSymbols("2,4,1").build(), Line.newBuilder().build(), Line.newBuilder().setSymbols("1,3,1").build())).build();
        setComputedData(dbData);
        underTest.execute(new TestComputationStepContext());
        assertThat(dbTester.countRowsOfTable("file_sources")).isEqualTo(1);
        FileSourceDto fileSourceDto = dbClient.fileSourceDao().selectByFileUuid(session, PersistFileSourcesStepTest.FILE1_UUID);
        assertThat(fileSourceDto.getSourceData()).isEqualTo(dbData);
        Mockito.verify(fileSourceDataWarnings).commitWarnings();
    }

    @Test
    public void persist_duplication() {
        DbFileSources.Data dbData = Data.newBuilder().addLines(Line.newBuilder().addDuplication(2).build()).build();
        setComputedData(dbData);
        underTest.execute(new TestComputationStepContext());
        assertThat(dbTester.countRowsOfTable("file_sources")).isEqualTo(1);
        FileSourceDto fileSourceDto = dbClient.fileSourceDao().selectByFileUuid(session, PersistFileSourcesStepTest.FILE1_UUID);
        assertThat(fileSourceDto.getSourceData()).isEqualTo(dbData);
        Mockito.verify(fileSourceDataWarnings).commitWarnings();
    }

    @Test
    public void save_revision() {
        Changeset latest = Changeset.newChangesetBuilder().setDate(0L).setRevision("rev-1").build();
        setComputedData(Data.newBuilder().build(), Collections.singletonList("lineHashes"), "srcHash", latest);
        underTest.execute(new TestComputationStepContext());
        FileSourceDto fileSourceDto = dbClient.fileSourceDao().selectByFileUuid(session, PersistFileSourcesStepTest.FILE1_UUID);
        assertThat(fileSourceDto.getRevision()).isEqualTo("rev-1");
        Mockito.verify(fileSourceDataWarnings).commitWarnings();
    }

    @Test
    public void not_save_revision() {
        setComputedData(Data.newBuilder().build());
        underTest.execute(new TestComputationStepContext());
        FileSourceDto fileSourceDto = dbClient.fileSourceDao().selectByFileUuid(session, PersistFileSourcesStepTest.FILE1_UUID);
        assertThat(fileSourceDto.getRevision()).isNull();
        Mockito.verify(fileSourceDataWarnings).commitWarnings();
    }

    @Test
    public void not_update_sources_when_nothing_has_changed() {
        dbClient.fileSourceDao().insert(dbTester.getSession(), createDto());
        dbTester.getSession().commit();
        Changeset changeset = Changeset.newChangesetBuilder().setDate(1L).setRevision("rev-1").build();
        setComputedData(Data.newBuilder().build(), Collections.singletonList("lineHash"), "sourceHash", changeset);
        underTest.execute(new TestComputationStepContext());
        assertThat(dbTester.countRowsOfTable("file_sources")).isEqualTo(1);
        FileSourceDto fileSourceDto = dbClient.fileSourceDao().selectByFileUuid(session, PersistFileSourcesStepTest.FILE1_UUID);
        assertThat(fileSourceDto.getSrcHash()).isEqualTo("sourceHash");
        assertThat(fileSourceDto.getLineHashes()).isEqualTo(Collections.singletonList("lineHash"));
        assertThat(fileSourceDto.getCreatedAt()).isEqualTo(PersistFileSourcesStepTest.PAST);
        assertThat(fileSourceDto.getUpdatedAt()).isEqualTo(PersistFileSourcesStepTest.PAST);
        Mockito.verify(fileSourceDataWarnings).commitWarnings();
    }

    @Test
    public void update_sources_when_source_updated() {
        // Existing sources
        long past = 150000L;
        dbClient.fileSourceDao().insert(dbTester.getSession(), new FileSourceDto().setProjectUuid(PersistFileSourcesStepTest.PROJECT_UUID).setFileUuid(PersistFileSourcesStepTest.FILE1_UUID).setSrcHash("5b4bd9815cdb17b8ceae19eb1810c34c").setLineHashes(Collections.singletonList("6438c669e0d0de98e6929c2cc0fac474")).setDataHash("6cad150e3d065976c230cddc5a09efaa").setSourceData(Data.newBuilder().addLines(Line.newBuilder().setLine(1).setSource("old line").build()).build()).setCreatedAt(past).setUpdatedAt(past).setRevision("rev-0"));
        dbTester.getSession().commit();
        DbFileSources.Data newSourceData = Data.newBuilder().addLines(Line.newBuilder().setLine(1).setSource("old line").setScmDate(123456789L).setScmRevision("rev-1").setScmAuthor("john").build()).build();
        Changeset changeset = Changeset.newChangesetBuilder().setDate(1L).setRevision("rev-1").build();
        setComputedData(newSourceData, Collections.singletonList("6438c669e0d0de98e6929c2cc0fac474"), "5b4bd9815cdb17b8ceae19eb1810c34c", changeset);
        underTest.execute(new TestComputationStepContext());
        assertThat(dbTester.countRowsOfTable("file_sources")).isEqualTo(1);
        FileSourceDto fileSourceDto = dbClient.fileSourceDao().selectByFileUuid(session, PersistFileSourcesStepTest.FILE1_UUID);
        assertThat(fileSourceDto.getCreatedAt()).isEqualTo(past);
        assertThat(fileSourceDto.getUpdatedAt()).isEqualTo(PersistFileSourcesStepTest.NOW);
        assertThat(fileSourceDto.getRevision()).isEqualTo("rev-1");
        Mockito.verify(fileSourceDataWarnings).commitWarnings();
    }

    @Test
    public void update_sources_when_src_hash_is_missing() {
        dbClient.fileSourceDao().insert(dbTester.getSession(), createDto(( dto) -> dto.setSrcHash(null)));
        dbTester.getSession().commit();
        DbFileSources.Data sourceData = Data.newBuilder().build();
        setComputedData(sourceData, Collections.singletonList("lineHash"), "newSourceHash", null);
        underTest.execute(new TestComputationStepContext());
        assertThat(dbTester.countRowsOfTable("file_sources")).isEqualTo(1);
        FileSourceDto fileSourceDto = dbClient.fileSourceDao().selectByFileUuid(session, PersistFileSourcesStepTest.FILE1_UUID);
        assertThat(fileSourceDto.getCreatedAt()).isEqualTo(PersistFileSourcesStepTest.PAST);
        assertThat(fileSourceDto.getUpdatedAt()).isEqualTo(PersistFileSourcesStepTest.NOW);
        assertThat(fileSourceDto.getSrcHash()).isEqualTo("newSourceHash");
        Mockito.verify(fileSourceDataWarnings).commitWarnings();
    }

    @Test
    public void update_sources_when_revision_is_missing() {
        DbFileSources.Data sourceData = Data.newBuilder().addLines(Line.newBuilder().setLine(1).setSource("line").build()).build();
        dbClient.fileSourceDao().insert(dbTester.getSession(), createDto(( dto) -> dto.setRevision(null)));
        dbTester.getSession().commit();
        Changeset changeset = Changeset.newChangesetBuilder().setDate(1L).setRevision("revision").build();
        setComputedData(sourceData, Collections.singletonList("137f72c3708c6bd0de00a0e5a69c699b"), "29f25900140c94db38035128cb6de6a2", changeset);
        underTest.execute(new TestComputationStepContext());
        assertThat(dbTester.countRowsOfTable("file_sources")).isEqualTo(1);
        FileSourceDto fileSourceDto = dbClient.fileSourceDao().selectByFileUuid(session, PersistFileSourcesStepTest.FILE1_UUID);
        assertThat(fileSourceDto.getCreatedAt()).isEqualTo(PersistFileSourcesStepTest.PAST);
        assertThat(fileSourceDto.getUpdatedAt()).isEqualTo(PersistFileSourcesStepTest.NOW);
        assertThat(fileSourceDto.getRevision()).isEqualTo("revision");
        Mockito.verify(fileSourceDataWarnings).commitWarnings();
    }
}

