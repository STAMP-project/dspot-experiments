/**
 * Copyright 2019 The Nomulus Authors. All Rights Reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package google.registry.gradle.plugin;


import TaskData.State.FAILURE;
import TaskData.State.SUCCESS;
import TaskData.State.UP_TO_DATE;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import google.registry.gradle.plugin.ProjectData.TaskData;
import google.registry.gradle.plugin.ProjectData.TaskData.ReportFiles;
import java.nio.file.Paths;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link CoverPageGenerator}
 */
@RunWith(JUnit4.class)
public final class CoverPageGeneratorTest {
    private static final ProjectData EMPTY_PROJECT = ProjectData.builder().setName("project-name").setDescription("project-description").setGradleVersion("gradle-version").setProjectProperties(ImmutableMap.of("key", "value")).setSystemProperties(ImmutableMap.of()).setTasksRequested(ImmutableSet.of(":a:task1", ":a:task2")).build();

    private static final TaskData EMPTY_TASK_SUCCESS = TaskData.builder().setUniqueName("task-success").setDescription("a successful task").setState(SUCCESS).build();

    private static final TaskData EMPTY_TASK_FAILURE = TaskData.builder().setUniqueName("task-failure").setDescription("a failed task").setState(FAILURE).build();

    private static final TaskData EMPTY_TASK_UP_TO_DATE = TaskData.builder().setUniqueName("task-up-to-date").setDescription("an up-to-date task").setState(UP_TO_DATE).build();

    @Test
    public void testGetFilesToUpload_getEntryPoint_isIndexHtml() {
        CoverPageGenerator coverPageGenerator = new CoverPageGenerator(CoverPageGeneratorTest.EMPTY_PROJECT);
        assertThat(coverPageGenerator.getEntryPoint()).isEqualTo(Paths.get("index.html"));
    }

    @Test
    public void testGetFilesToUpload_containsEntryFile() {
        String content = getContentOfGeneratedFile(CoverPageGeneratorTest.EMPTY_PROJECT, "index.html");
        assertThat(content).contains("<span class=\"project_invocation\">./gradlew :a:task1 :a:task2 -P key=value</span>");
    }

    @Test
    public void testCoverPage_showsFailedTask() {
        String content = getCoverPage(CoverPageGeneratorTest.EMPTY_PROJECT.toBuilder().addTask(CoverPageGeneratorTest.EMPTY_TASK_FAILURE).build());
        assertThat(content).contains("task-failure");
        assertThat(content).contains("<p>FAILURE</p>");
        assertThat(content).doesNotContain("<p>SUCCESS</p>");
        assertThat(content).doesNotContain("<p>UP_TO_DATE</p>");
    }

    @Test
    public void testCoverPage_showsSuccessfulTask() {
        String content = getCoverPage(CoverPageGeneratorTest.EMPTY_PROJECT.toBuilder().addTask(CoverPageGeneratorTest.EMPTY_TASK_SUCCESS).build());
        assertThat(content).contains("task-success");
        assertThat(content).doesNotContain("<p>FAILURE</p>");
        assertThat(content).contains("<p>SUCCESS</p>");
        assertThat(content).doesNotContain("<p>UP_TO_DATE</p>");
    }

    @Test
    public void testCoverPage_showsUpToDateTask() {
        String content = getCoverPage(CoverPageGeneratorTest.EMPTY_PROJECT.toBuilder().addTask(CoverPageGeneratorTest.EMPTY_TASK_UP_TO_DATE).build());
        assertThat(content).contains("task-up-to-date");
        assertThat(content).doesNotContain("<p>FAILURE</p>");
        assertThat(content).doesNotContain("<p>SUCCESS</p>");
        assertThat(content).contains("<p>UP_TO_DATE</p>");
    }

    @Test
    public void testCoverPage_failedAreFirst() {
        String content = getCoverPage(CoverPageGeneratorTest.EMPTY_PROJECT.toBuilder().addTask(CoverPageGeneratorTest.EMPTY_TASK_UP_TO_DATE).addTask(CoverPageGeneratorTest.EMPTY_TASK_FAILURE).addTask(CoverPageGeneratorTest.EMPTY_TASK_SUCCESS).build());
        assertThat(content).contains("<p>FAILURE</p>");
        assertThat(content).contains("<p>SUCCESS</p>");
        assertThat(content).contains("<p>UP_TO_DATE</p>");
        assertThat(content).containsMatch("(?s)<p>FAILURE</p>.*<p>SUCCESS</p>");
        assertThat(content).containsMatch("(?s)<p>FAILURE</p>.*<p>UP_TO_DATE</p>");
        assertThat(content).doesNotContainMatch("(?s)<p>SUCCESS</p>.*<p>FAILURE</p>");
        assertThat(content).doesNotContainMatch("(?s)<p>UP_TO_DATE</p>.*<p>FAILURE</p>");
    }

    @Test
    public void testCoverPage_failingTask_statusIsFailure() {
        String content = getCoverPage(CoverPageGeneratorTest.EMPTY_PROJECT.toBuilder().addTask(CoverPageGeneratorTest.EMPTY_TASK_UP_TO_DATE).addTask(CoverPageGeneratorTest.EMPTY_TASK_FAILURE).addTask(CoverPageGeneratorTest.EMPTY_TASK_SUCCESS).build());
        assertThat(content).contains("<title>Failed: task-failure</title>");
    }

    @Test
    public void testCoverPage_noFailingTask_statusIsSuccess() {
        String content = getCoverPage(CoverPageGeneratorTest.EMPTY_PROJECT.toBuilder().addTask(CoverPageGeneratorTest.EMPTY_TASK_UP_TO_DATE).addTask(CoverPageGeneratorTest.EMPTY_TASK_SUCCESS).build());
        assertThat(content).contains("<title>Success!</title>");
    }

    @Test
    public void testGetFilesToUpload_containsCssFile() {
        ImmutableMap<String, String> files = getGeneratedFiles(CoverPageGeneratorTest.EMPTY_PROJECT);
        assertThat(files).containsKey("css/style.css");
        assertThat(files.get("css/style.css")).contains("body {");
        assertThat(files.get("index.html")).contains("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\">");
    }

    @Test
    public void testCreateReportFiles_taskWithLog() {
        ImmutableMap<String, String> files = getGeneratedFiles(CoverPageGeneratorTest.EMPTY_PROJECT.toBuilder().addTask(CoverPageGeneratorTest.EMPTY_TASK_SUCCESS.toBuilder().setUniqueName("my:name").setLog(GcsPluginUtils.toByteArraySupplier("my log data")).build()).build());
        assertThat(files).containsEntry("logs/my:name.log", "my log data");
        assertThat(files.get("index.html")).contains("<a href=\"logs/my:name.log\">[log]</a>");
    }

    @Test
    public void testCreateReportFiles_taskWithoutLog() {
        ImmutableMap<String, String> files = getGeneratedFiles(CoverPageGeneratorTest.EMPTY_PROJECT.toBuilder().addTask(CoverPageGeneratorTest.EMPTY_TASK_SUCCESS.toBuilder().setUniqueName("my:name").build()).build());
        assertThat(files).doesNotContainKey("logs/my:name.log");
        assertThat(files.get("index.html")).contains("<span class=\"report_link_broken\">[log]</span>");
    }

    @Test
    public void testCreateReportFiles_taskWithFilledReport() {
        ImmutableMap<String, String> files = getGeneratedFiles(CoverPageGeneratorTest.EMPTY_PROJECT.toBuilder().addTask(CoverPageGeneratorTest.EMPTY_TASK_SUCCESS.toBuilder().putReport("someReport", ReportFiles.create(ImmutableMap.of(Paths.get("path", "report.txt"), GcsPluginUtils.toByteArraySupplier("report content")), Paths.get("path", "report.txt"))).build()).build());
        assertThat(files).containsEntry("path/report.txt", "report content");
        assertThat(files.get("index.html")).contains("<a href=\"path/report.txt\">[someReport]</a>");
    }

    @Test
    public void testCreateReportFiles_taskWithEmptyReport() {
        ImmutableMap<String, String> files = getGeneratedFiles(CoverPageGeneratorTest.EMPTY_PROJECT.toBuilder().addTask(CoverPageGeneratorTest.EMPTY_TASK_SUCCESS.toBuilder().putReport("someReport", ReportFiles.create(ImmutableMap.of(), Paths.get("path", "report.txt"))).build()).build());
        assertThat(files).doesNotContainKey("path/report.txt");
        assertThat(files.get("index.html")).contains("<span class=\"report_link_broken\">[someReport]</span>");
    }

    @Test
    public void testCreateReportFiles_taskWithLogAndMultipleReports() {
        ImmutableMap<String, String> files = getGeneratedFiles(CoverPageGeneratorTest.EMPTY_PROJECT.toBuilder().addTask(CoverPageGeneratorTest.EMPTY_TASK_SUCCESS.toBuilder().setUniqueName("my:name").setLog(GcsPluginUtils.toByteArraySupplier("log data")).putReport("filledReport", ReportFiles.create(ImmutableMap.of(Paths.get("path-filled", "report.txt"), GcsPluginUtils.toByteArraySupplier("report content"), Paths.get("path-filled", "other", "file.txt"), GcsPluginUtils.toByteArraySupplier("some other content")), Paths.get("path-filled", "report.txt"))).putReport("emptyReport", ReportFiles.create(ImmutableMap.of(), Paths.get("path-empty", "report.txt"))).build()).build());
        assertThat(files).containsEntry("path-filled/report.txt", "report content");
        assertThat(files).containsEntry("path-filled/other/file.txt", "some other content");
        assertThat(files).containsEntry("logs/my:name.log", "log data");
        assertThat(files.get("index.html")).contains("<a href=\"path-filled/report.txt\">[filledReport]</a>");
        assertThat(files.get("index.html")).contains("<a href=\"logs/my:name.log\">[log]</a>");
        assertThat(files.get("index.html")).contains("<span class=\"report_link_broken\">[emptyReport]</span>");
    }
}

