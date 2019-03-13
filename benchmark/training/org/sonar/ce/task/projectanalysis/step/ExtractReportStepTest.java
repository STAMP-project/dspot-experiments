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
package org.sonar.ce.task.projectanalysis.step;


import CeTaskTypes.REPORT;
import LoggerLevel.DEBUG;
import System2.INSTANCE;
import java.io.File;
import java.io.InputStream;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.MessageException;
import org.sonar.api.utils.internal.JUnitTempFolder;
import org.sonar.api.utils.log.LogTester;
import org.sonar.ce.task.CeTask;
import org.sonar.ce.task.projectanalysis.batch.BatchReportDirectoryHolderImpl;
import org.sonar.ce.task.projectanalysis.batch.MutableBatchReportDirectoryHolder;
import org.sonar.ce.task.step.TestComputationStepContext;
import org.sonar.db.DbTester;


public class ExtractReportStepTest {
    private static final String TASK_UUID = "1";

    @Rule
    public JUnitTempFolder tempFolder = new JUnitTempFolder();

    @Rule
    public LogTester logTester = new LogTester();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    private MutableBatchReportDirectoryHolder reportDirectoryHolder = new BatchReportDirectoryHolderImpl();

    private CeTask ceTask = new CeTask.Builder().setOrganizationUuid("org1").setType(REPORT).setUuid(ExtractReportStepTest.TASK_UUID).build();

    private ExtractReportStep underTest = new ExtractReportStep(dbTester.getDbClient(), ceTask, tempFolder, reportDirectoryHolder);

    @Test
    public void fail_if_report_zip_does_not_exist() {
        expectedException.expect(MessageException.class);
        expectedException.expectMessage("Analysis report 1 is missing in database");
        underTest.execute(new TestComputationStepContext());
    }

    @Test
    public void unzip_report() throws Exception {
        logTester.setLevel(DEBUG);
        File reportFile = generateReport();
        try (InputStream input = FileUtils.openInputStream(reportFile)) {
            dbTester.getDbClient().ceTaskInputDao().insert(dbTester.getSession(), ExtractReportStepTest.TASK_UUID, input);
        }
        dbTester.getSession().commit();
        dbTester.getSession().close();
        underTest.execute(new TestComputationStepContext());
        // directory contains the uncompressed report (which contains only metadata.pb in this test)
        File unzippedDir = reportDirectoryHolder.getDirectory();
        assertThat(unzippedDir).isDirectory().exists();
        assertThat(unzippedDir.listFiles()).hasSize(1);
        assertThat(new File(unzippedDir, "metadata.pb")).hasContent("{metadata}");
        assertThat(logTester.logs(DEBUG)).anyMatch(( log) -> log.matches("Analysis report is \\d+ bytes uncompressed"));
    }
}

