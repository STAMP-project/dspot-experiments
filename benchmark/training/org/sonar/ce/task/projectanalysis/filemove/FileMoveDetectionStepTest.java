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
package org.sonar.ce.task.projectanalysis.filemove;


import Component.Type.FILE;
import LoggerLevel.DEBUG;
import MovedFilesRepository.OriginalFile;
import System2.INSTANCE;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.utils.log.LogTester;
import org.sonar.ce.task.projectanalysis.analysis.Analysis;
import org.sonar.ce.task.projectanalysis.analysis.AnalysisMetadataHolderRule;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.component.FileAttributes;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.component.TreeRootHolderRule;
import org.sonar.ce.task.projectanalysis.source.SourceLinesHashRepository;
import org.sonar.ce.task.step.TestComputationStepContext;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;


public class FileMoveDetectionStepTest {
    private static final long SNAPSHOT_ID = 98765;

    private static final Analysis ANALYSIS = new Analysis.Builder().setId(FileMoveDetectionStepTest.SNAPSHOT_ID).setUuid("uuid_1").setCreatedAt(86521).build();

    private static final int ROOT_REF = 1;

    private static final int FILE_1_REF = 2;

    private static final int FILE_2_REF = 3;

    private static final int FILE_3_REF = 4;

    private static final String[] CONTENT1 = new String[]{ "package org.sonar.ce.task.projectanalysis.filemove;", "", "public class Foo {", "  public String bar() {", "    return \"Doh!\";", "  }", "}" };

    private static final String[] LESS_CONTENT1 = new String[]{ "package org.sonar.ce.task.projectanalysis.filemove;", "", "public class Foo {", "  public String foo() {", "    return \"Donut!\";", "  }", "}" };

    private static final String[] CONTENT_EMPTY = new String[]{ "" };

    private static final String[] CONTENT2 = new String[]{ "package org.sonar.ce.queue;", "", "import com.google.common.base.MoreObjects;", "import javax.annotation.CheckForNull;", "import javax.annotation.Nullable;", "import javax.annotation.concurrent.Immutable;", "", "import static com.google.common.base.Strings.emptyToNull;", "import static java.util.Objects.requireNonNull;", "", "@Immutable", "public class CeTask {", "", ",  private final String type;", ",  private final String uuid;", ",  private final String componentUuid;", ",  private final String componentKey;", ",  private final String componentName;", ",  private final String submitterLogin;", "", ",  private CeTask(Builder builder) {", ",    this.uuid = requireNonNull(emptyToNull(builder.uuid));", ",    this.type = requireNonNull(emptyToNull(builder.type));", ",    this.componentUuid = emptyToNull(builder.componentUuid);", ",    this.componentKey = emptyToNull(builder.componentKey);", ",    this.componentName = emptyToNull(builder.componentName);", ",    this.submitterLogin = emptyToNull(builder.submitterLogin);", ",  }", "", ",  public String getUuid() {", ",    return uuid;", ",  }", "", ",  public String getType() {", ",    return type;", ",  }", "", ",  @CheckForNull", ",  public String getComponentUuid() {", ",    return componentUuid;", ",  }", "", ",  @CheckForNull", ",  public String getComponentKey() {", ",    return componentKey;", ",  }", "", ",  @CheckForNull", ",  public String getComponentName() {", ",    return componentName;", ",  }", "", ",  @CheckForNull", ",  public String getSubmitterLogin() {", ",    return submitterLogin;", ",  }", ",}" };

    // removed immutable annotation
    private static final String[] LESS_CONTENT2 = new String[]{ "package org.sonar.ce.queue;", "", "import com.google.common.base.MoreObjects;", "import javax.annotation.CheckForNull;", "import javax.annotation.Nullable;", "", "import static com.google.common.base.Strings.emptyToNull;", "import static java.util.Objects.requireNonNull;", "", "public class CeTask {", "", ",  private final String type;", ",  private final String uuid;", ",  private final String componentUuid;", ",  private final String componentKey;", ",  private final String componentName;", ",  private final String submitterLogin;", "", ",  private CeTask(Builder builder) {", ",    this.uuid = requireNonNull(emptyToNull(builder.uuid));", ",    this.type = requireNonNull(emptyToNull(builder.type));", ",    this.componentUuid = emptyToNull(builder.componentUuid);", ",    this.componentKey = emptyToNull(builder.componentKey);", ",    this.componentName = emptyToNull(builder.componentName);", ",    this.submitterLogin = emptyToNull(builder.submitterLogin);", ",  }", "", ",  public String getUuid() {", ",    return uuid;", ",  }", "", ",  public String getType() {", ",    return type;", ",  }", "", ",  @CheckForNull", ",  public String getComponentUuid() {", ",    return componentUuid;", ",  }", "", ",  @CheckForNull", ",  public String getComponentKey() {", ",    return componentKey;", ",  }", "", ",  @CheckForNull", ",  public String getComponentName() {", ",    return componentName;", ",  }", "", ",  @CheckForNull", ",  public String getSubmitterLogin() {", ",    return submitterLogin;", ",  }", ",}" };

    @Rule
    public AnalysisMetadataHolderRule analysisMetadataHolder = new AnalysisMetadataHolderRule();

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule();

    @Rule
    public MutableMovedFilesRepositoryRule movedFilesRepository = new MutableMovedFilesRepositoryRule();

    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    @Rule
    public LogTester logTester = new LogTester();

    private DbClient dbClient = dbTester.getDbClient();

    private ComponentDto project;

    private SourceLinesHashRepository sourceLinesHash = Mockito.mock(SourceLinesHashRepository.class);

    private FileSimilarity fileSimilarity = new FileSimilarityImpl(new SourceSimilarityImpl());

    private FileMoveDetectionStepTest.CapturingScoreMatrixDumper scoreMatrixDumper = new FileMoveDetectionStepTest.CapturingScoreMatrixDumper();

    private FileMoveDetectionStepTest.RecordingMutableAddedFileRepository addedFileRepository = new FileMoveDetectionStepTest.RecordingMutableAddedFileRepository();

    private FileMoveDetectionStep underTest = new FileMoveDetectionStep(analysisMetadataHolder, treeRootHolder, dbClient, fileSimilarity, movedFilesRepository, sourceLinesHash, scoreMatrixDumper, addedFileRepository);

    @Test
    public void getDescription_returns_description() {
        assertThat(underTest.getDescription()).isEqualTo("Detect file moves");
    }

    @Test
    public void execute_detects_no_move_on_first_analysis() {
        analysisMetadataHolder.setBaseAnalysis(null);
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        assertThat(movedFilesRepository.getComponentsWithOriginal()).isEmpty();
        FileMoveDetectionStepTest.verifyStatistics(context, null, null, null, null);
    }

    @Test
    public void execute_detects_no_move_if_baseSnapshot_has_no_file_and_report_has_no_file() {
        analysisMetadataHolder.setBaseAnalysis(FileMoveDetectionStepTest.ANALYSIS);
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        assertThat(movedFilesRepository.getComponentsWithOriginal()).isEmpty();
        assertThat(addedFileRepository.getComponents()).isEmpty();
        FileMoveDetectionStepTest.verifyStatistics(context, 0, null, null, null);
    }

    @Test
    public void execute_detects_no_move_if_baseSnapshot_has_no_file() {
        analysisMetadataHolder.setBaseAnalysis(FileMoveDetectionStepTest.ANALYSIS);
        Component file1 = fileComponent(FileMoveDetectionStepTest.FILE_1_REF, null);
        Component file2 = fileComponent(FileMoveDetectionStepTest.FILE_2_REF, null);
        setFilesInReport(file1, file2);
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        assertThat(movedFilesRepository.getComponentsWithOriginal()).isEmpty();
        assertThat(addedFileRepository.getComponents()).containsOnly(file1, file2);
        FileMoveDetectionStepTest.verifyStatistics(context, 2, 0, 2, null);
    }

    @Test
    public void execute_detects_no_move_if_there_is_no_file_in_report() {
        analysisMetadataHolder.setBaseAnalysis(FileMoveDetectionStepTest.ANALYSIS);
        /* no components */
        insertFiles();
        setFilesInReport();
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        assertThat(movedFilesRepository.getComponentsWithOriginal()).isEmpty();
        assertThat(addedFileRepository.getComponents()).isEmpty();
        FileMoveDetectionStepTest.verifyStatistics(context, 0, null, null, null);
    }

    @Test
    public void execute_detects_no_move_if_file_key_exists_in_both_DB_and_report() {
        analysisMetadataHolder.setBaseAnalysis(FileMoveDetectionStepTest.ANALYSIS);
        Component file1 = fileComponent(FileMoveDetectionStepTest.FILE_1_REF, null);
        Component file2 = fileComponent(FileMoveDetectionStepTest.FILE_2_REF, null);
        insertFiles(file1.getUuid(), file2.getUuid());
        insertContentOfFileInDb(file1.getUuid(), FileMoveDetectionStepTest.CONTENT1);
        insertContentOfFileInDb(file2.getUuid(), FileMoveDetectionStepTest.CONTENT2);
        setFilesInReport(file2, file1);
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        assertThat(movedFilesRepository.getComponentsWithOriginal()).isEmpty();
        assertThat(addedFileRepository.getComponents()).isEmpty();
        FileMoveDetectionStepTest.verifyStatistics(context, 2, 2, 0, null);
    }

    @Test
    public void execute_detects_move_if_content_of_file_is_same_in_DB_and_report() {
        analysisMetadataHolder.setBaseAnalysis(FileMoveDetectionStepTest.ANALYSIS);
        Component file1 = fileComponent(FileMoveDetectionStepTest.FILE_1_REF, null);
        Component file2 = fileComponent(FileMoveDetectionStepTest.FILE_2_REF, FileMoveDetectionStepTest.CONTENT1);
        ComponentDto[] dtos = insertFiles(file1.getUuid());
        insertContentOfFileInDb(file1.getUuid(), FileMoveDetectionStepTest.CONTENT1);
        setFilesInReport(file2);
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        assertThat(movedFilesRepository.getComponentsWithOriginal()).containsExactly(file2);
        MovedFilesRepository.OriginalFile originalFile = movedFilesRepository.getOriginalFile(file2).get();
        assertThat(originalFile.getId()).isEqualTo(dtos[0].getId());
        assertThat(originalFile.getKey()).isEqualTo(dtos[0].getDbKey());
        assertThat(originalFile.getUuid()).isEqualTo(dtos[0].uuid());
        assertThat(addedFileRepository.getComponents()).isEmpty();
        FileMoveDetectionStepTest.verifyStatistics(context, 1, 1, 1, 1);
    }

    @Test
    public void execute_detects_no_move_if_content_of_file_is_not_similar_enough() {
        analysisMetadataHolder.setBaseAnalysis(FileMoveDetectionStepTest.ANALYSIS);
        Component file1 = fileComponent(FileMoveDetectionStepTest.FILE_1_REF, null);
        Component file2 = fileComponent(FileMoveDetectionStepTest.FILE_2_REF, FileMoveDetectionStepTest.LESS_CONTENT1);
        insertFiles(file1.getDbKey());
        insertContentOfFileInDb(file1.getDbKey(), FileMoveDetectionStepTest.CONTENT1);
        setFilesInReport(file2);
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        assertThat(movedFilesRepository.getComponentsWithOriginal()).isEmpty();
        assertThat(scoreMatrixDumper.scoreMatrix.getMaxScore()).isGreaterThan(0).isLessThan(FileMoveDetectionStep.MIN_REQUIRED_SCORE);
        assertThat(addedFileRepository.getComponents()).contains(file2);
        FileMoveDetectionStepTest.verifyStatistics(context, 1, 1, 1, 0);
    }

    @Test
    public void execute_detects_no_move_if_content_of_file_is_empty_in_DB() {
        analysisMetadataHolder.setBaseAnalysis(FileMoveDetectionStepTest.ANALYSIS);
        Component file1 = fileComponent(FileMoveDetectionStepTest.FILE_1_REF, null);
        Component file2 = fileComponent(FileMoveDetectionStepTest.FILE_2_REF, FileMoveDetectionStepTest.CONTENT1);
        insertFiles(file1.getDbKey());
        insertContentOfFileInDb(file1.getDbKey(), FileMoveDetectionStepTest.CONTENT_EMPTY);
        setFilesInReport(file2);
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        assertThat(movedFilesRepository.getComponentsWithOriginal()).isEmpty();
        assertThat(scoreMatrixDumper.scoreMatrix.getMaxScore()).isZero();
        assertThat(addedFileRepository.getComponents()).contains(file2);
        FileMoveDetectionStepTest.verifyStatistics(context, 1, 1, 1, 0);
    }

    @Test
    public void execute_detects_no_move_if_content_of_file_has_no_path_in_DB() {
        analysisMetadataHolder.setBaseAnalysis(FileMoveDetectionStepTest.ANALYSIS);
        Component file1 = fileComponent(FileMoveDetectionStepTest.FILE_1_REF, null);
        Component file2 = fileComponent(FileMoveDetectionStepTest.FILE_2_REF, FileMoveDetectionStepTest.CONTENT1);
        insertFiles(( key) -> newComponentDto(key).setPath(null), file1.getDbKey());
        insertContentOfFileInDb(file1.getDbKey(), FileMoveDetectionStepTest.CONTENT1);
        setFilesInReport(file2);
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        assertThat(movedFilesRepository.getComponentsWithOriginal()).isEmpty();
        assertThat(scoreMatrixDumper.scoreMatrix).isNull();
        assertThat(addedFileRepository.getComponents()).containsOnly(file2);
        FileMoveDetectionStepTest.verifyStatistics(context, 1, 0, 1, null);
    }

    @Test
    public void execute_detects_no_move_if_content_of_file_is_empty_in_report() {
        analysisMetadataHolder.setBaseAnalysis(FileMoveDetectionStepTest.ANALYSIS);
        Component file1 = fileComponent(FileMoveDetectionStepTest.FILE_1_REF, null);
        Component file2 = fileComponent(FileMoveDetectionStepTest.FILE_2_REF, FileMoveDetectionStepTest.CONTENT_EMPTY);
        insertFiles(file1.getDbKey());
        insertContentOfFileInDb(file1.getDbKey(), FileMoveDetectionStepTest.CONTENT1);
        setFilesInReport(file2);
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        assertThat(movedFilesRepository.getComponentsWithOriginal()).isEmpty();
        assertThat(scoreMatrixDumper.scoreMatrix.getMaxScore()).isZero();
        assertThat(addedFileRepository.getComponents()).contains(file2);
        FileMoveDetectionStepTest.verifyStatistics(context, 1, 1, 1, 0);
        assertThat(logTester.logs(DEBUG)).contains("max score in matrix is less than min required score (85). Do nothing.");
    }

    @Test
    public void execute_detects_no_move_if_two_added_files_have_same_content_as_the_one_in_db() {
        analysisMetadataHolder.setBaseAnalysis(FileMoveDetectionStepTest.ANALYSIS);
        Component file1 = fileComponent(FileMoveDetectionStepTest.FILE_1_REF, null);
        Component file2 = fileComponent(FileMoveDetectionStepTest.FILE_2_REF, FileMoveDetectionStepTest.CONTENT1);
        Component file3 = fileComponent(FileMoveDetectionStepTest.FILE_3_REF, FileMoveDetectionStepTest.CONTENT1);
        insertFiles(file1.getDbKey());
        insertContentOfFileInDb(file1.getDbKey(), FileMoveDetectionStepTest.CONTENT1);
        setFilesInReport(file2, file3);
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        assertThat(movedFilesRepository.getComponentsWithOriginal()).isEmpty();
        assertThat(scoreMatrixDumper.scoreMatrix.getMaxScore()).isEqualTo(100);
        assertThat(addedFileRepository.getComponents()).containsOnly(file2, file3);
        FileMoveDetectionStepTest.verifyStatistics(context, 2, 1, 2, 0);
    }

    @Test
    public void execute_detects_no_move_if_two_deleted_files_have_same_content_as_the_one_added() {
        analysisMetadataHolder.setBaseAnalysis(FileMoveDetectionStepTest.ANALYSIS);
        Component file1 = fileComponent(FileMoveDetectionStepTest.FILE_1_REF, null);
        Component file2 = fileComponent(FileMoveDetectionStepTest.FILE_2_REF, null);
        Component file3 = fileComponent(FileMoveDetectionStepTest.FILE_3_REF, FileMoveDetectionStepTest.CONTENT1);
        insertFiles(file1.getUuid(), file2.getUuid());
        insertContentOfFileInDb(file1.getUuid(), FileMoveDetectionStepTest.CONTENT1);
        insertContentOfFileInDb(file2.getUuid(), FileMoveDetectionStepTest.CONTENT1);
        setFilesInReport(file3);
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        assertThat(movedFilesRepository.getComponentsWithOriginal()).isEmpty();
        assertThat(scoreMatrixDumper.scoreMatrix.getMaxScore()).isEqualTo(100);
        assertThat(addedFileRepository.getComponents()).containsOnly(file3);
        FileMoveDetectionStepTest.verifyStatistics(context, 1, 2, 1, 0);
    }

    @Test
    public void execute_detects_no_move_if_two_files_are_empty_in_DB() {
        analysisMetadataHolder.setBaseAnalysis(FileMoveDetectionStepTest.ANALYSIS);
        Component file1 = fileComponent(FileMoveDetectionStepTest.FILE_1_REF, null);
        Component file2 = fileComponent(FileMoveDetectionStepTest.FILE_2_REF, null);
        insertFiles(file1.getUuid(), file2.getUuid());
        insertContentOfFileInDb(file1.getUuid(), null);
        insertContentOfFileInDb(file2.getUuid(), null);
        setFilesInReport(file1, file2);
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        assertThat(movedFilesRepository.getComponentsWithOriginal()).isEmpty();
        assertThat(scoreMatrixDumper.scoreMatrix).isNull();
        assertThat(addedFileRepository.getComponents()).isEmpty();
        FileMoveDetectionStepTest.verifyStatistics(context, 2, 2, 0, null);
    }

    @Test
    public void execute_detects_several_moves() {
        // testing:
        // - file1 renamed to file3
        // - file2 deleted
        // - file4 untouched
        // - file5 renamed to file6 with a small change
        analysisMetadataHolder.setBaseAnalysis(FileMoveDetectionStepTest.ANALYSIS);
        Component file1 = fileComponent(FileMoveDetectionStepTest.FILE_1_REF, null);
        Component file2 = fileComponent(FileMoveDetectionStepTest.FILE_2_REF, null);
        Component file3 = fileComponent(FileMoveDetectionStepTest.FILE_3_REF, FileMoveDetectionStepTest.CONTENT1);
        Component file4 = fileComponent(5, new String[]{ "a", "b" });
        Component file5 = fileComponent(6, null);
        Component file6 = fileComponent(7, FileMoveDetectionStepTest.LESS_CONTENT2);
        ComponentDto[] dtos = insertFiles(file1.getUuid(), file2.getUuid(), file4.getUuid(), file5.getUuid());
        insertContentOfFileInDb(file1.getUuid(), FileMoveDetectionStepTest.CONTENT1);
        insertContentOfFileInDb(file2.getUuid(), FileMoveDetectionStepTest.LESS_CONTENT1);
        insertContentOfFileInDb(file4.getUuid(), new String[]{ "e", "f", "g", "h", "i" });
        insertContentOfFileInDb(file5.getUuid(), FileMoveDetectionStepTest.CONTENT2);
        setFilesInReport(file3, file4, file6);
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        assertThat(movedFilesRepository.getComponentsWithOriginal()).containsOnly(file3, file6);
        MovedFilesRepository.OriginalFile originalFile2 = movedFilesRepository.getOriginalFile(file3).get();
        assertThat(originalFile2.getId()).isEqualTo(dtos[0].getId());
        assertThat(originalFile2.getKey()).isEqualTo(dtos[0].getDbKey());
        assertThat(originalFile2.getUuid()).isEqualTo(dtos[0].uuid());
        MovedFilesRepository.OriginalFile originalFile5 = movedFilesRepository.getOriginalFile(file6).get();
        assertThat(originalFile5.getId()).isEqualTo(dtos[3].getId());
        assertThat(originalFile5.getKey()).isEqualTo(dtos[3].getDbKey());
        assertThat(originalFile5.getUuid()).isEqualTo(dtos[3].uuid());
        assertThat(scoreMatrixDumper.scoreMatrix.getMaxScore()).isGreaterThan(FileMoveDetectionStep.MIN_REQUIRED_SCORE);
        assertThat(addedFileRepository.getComponents()).isEmpty();
        FileMoveDetectionStepTest.verifyStatistics(context, 3, 4, 2, 2);
    }

    @Test
    public void execute_does_not_compute_any_distance_if_all_files_sizes_are_all_too_different() {
        analysisMetadataHolder.setBaseAnalysis(FileMoveDetectionStepTest.ANALYSIS);
        Component file1 = fileComponent(FileMoveDetectionStepTest.FILE_1_REF, null);
        Component file2 = fileComponent(FileMoveDetectionStepTest.FILE_2_REF, null);
        Component file3 = fileComponent(FileMoveDetectionStepTest.FILE_3_REF, FileMoveDetectionStepTest.arrayOf(118));
        Component file4 = fileComponent(5, FileMoveDetectionStepTest.arrayOf(25));
        insertFiles(file1.getDbKey(), file2.getDbKey());
        insertContentOfFileInDb(file1.getDbKey(), FileMoveDetectionStepTest.arrayOf(100));
        insertContentOfFileInDb(file2.getDbKey(), FileMoveDetectionStepTest.arrayOf(30));
        setFilesInReport(file3, file4);
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        assertThat(movedFilesRepository.getComponentsWithOriginal()).isEmpty();
        assertThat(scoreMatrixDumper.scoreMatrix.getMaxScore()).isZero();
        FileMoveDetectionStepTest.verifyStatistics(context, 2, 2, 2, 0);
    }

    /**
     * JH: A bug was encountered in the algorithm and I didn't manage to forge a simpler test case.
     */
    @Test
    public void real_life_use_case() throws Exception {
        analysisMetadataHolder.setBaseAnalysis(FileMoveDetectionStepTest.ANALYSIS);
        for (File f : FileUtils.listFiles(new File("src/test/resources/org/sonar/ce/task/projectanalysis/filemove/FileMoveDetectionStepTest/v1"), null, false)) {
            insertFiles(("uuid_" + (f.getName().hashCode())));
            insertContentOfFileInDb(("uuid_" + (f.getName().hashCode())), readLines(f));
        }
        Map<String, Component> comps = new HashMap<>();
        int i = 1;
        for (File f : FileUtils.listFiles(new File("src/test/resources/org/sonar/ce/task/projectanalysis/filemove/FileMoveDetectionStepTest/v2"), null, false)) {
            String[] lines = readLines(f);
            Component c = ReportComponent.builder(FILE, (i++)).setUuid(("uuid_" + (f.getName().hashCode()))).setKey(f.getName()).setName(f.getName()).setFileAttributes(new FileAttributes(false, null, lines.length)).build();
            comps.put(f.getName(), c);
            setFileLineHashesInReport(c, lines);
        }
        setFilesInReport(comps.values().toArray(new Component[0]));
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        Component makeComponentUuidAndAnalysisUuidNotNullOnDuplicationsIndex = comps.get("MakeComponentUuidAndAnalysisUuidNotNullOnDuplicationsIndex.java");
        Component migrationRb1238 = comps.get("1238_make_component_uuid_and_analysis_uuid_not_null_on_duplications_index.rb");
        Component addComponentUuidAndAnalysisUuidColumnToDuplicationsIndex = comps.get("AddComponentUuidAndAnalysisUuidColumnToDuplicationsIndex.java");
        assertThat(movedFilesRepository.getComponentsWithOriginal()).containsOnly(makeComponentUuidAndAnalysisUuidNotNullOnDuplicationsIndex, migrationRb1238, addComponentUuidAndAnalysisUuidColumnToDuplicationsIndex);
        assertThat(movedFilesRepository.getOriginalFile(makeComponentUuidAndAnalysisUuidNotNullOnDuplicationsIndex).get().getUuid()).isEqualTo(("uuid_" + ("MakeComponentUuidNotNullOnDuplicationsIndex.java".hashCode())));
        assertThat(movedFilesRepository.getOriginalFile(migrationRb1238).get().getUuid()).isEqualTo(("uuid_" + ("1242_make_analysis_uuid_not_null_on_duplications_index.rb".hashCode())));
        assertThat(movedFilesRepository.getOriginalFile(addComponentUuidAndAnalysisUuidColumnToDuplicationsIndex).get().getUuid()).isEqualTo(("uuid_" + ("AddComponentUuidColumnToDuplicationsIndex.java".hashCode())));
        FileMoveDetectionStepTest.verifyStatistics(context, comps.values().size(), 12, 6, 3);
    }

    private static class CapturingScoreMatrixDumper implements ScoreMatrixDumper {
        private ScoreMatrix scoreMatrix;

        @Override
        public void dumpAsCsv(ScoreMatrix scoreMatrix) {
            this.scoreMatrix = scoreMatrix;
        }
    }

    private static class RecordingMutableAddedFileRepository implements MutableAddedFileRepository {
        private final List<Component> components = new ArrayList<>();

        @Override
        public void register(Component file) {
            components.add(file);
        }

        @Override
        public boolean isAdded(Component component) {
            throw new UnsupportedOperationException("isAdded should not be called");
        }

        public List<Component> getComponents() {
            return components;
        }
    }
}

