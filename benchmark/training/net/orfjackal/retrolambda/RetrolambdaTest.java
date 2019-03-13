/**
 * Copyright ? 2013-2017 Esko Luontola and other Retrolambda contributors
 */
/**
 * This software is released under the Apache License 2.0.
 */
/**
 * The license text is at http://www.apache.org/licenses/LICENSE-2.0
 */
package net.orfjackal.retrolambda;


import RetrolambdaApi.CLASSPATH;
import RetrolambdaApi.INPUT_DIR;
import RetrolambdaApi.OUTPUT_DIR;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class RetrolambdaTest {
    @Rule
    public final TemporaryFolder tempDir = new TemporaryFolder();

    private Path inputDir;

    private Path outputDir;

    private final List<Path> visitedFiles = new ArrayList<>();

    private final FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            visitedFiles.add(file);
            return FileVisitResult.CONTINUE;
        }
    };

    private Path file1;

    private Path file2;

    private Path fileInSubdir;

    private Path outsider;

    @Test
    public void by_default_visits_all_files_recursively() throws IOException {
        Retrolambda.visitFiles(inputDir, null, visitor);
        MatcherAssert.assertThat(visitedFiles, Matchers.containsInAnyOrder(file1, file2, fileInSubdir));
    }

    @Test
    public void when_included_files_is_set_then_visits_only_those_files() throws IOException {
        List<Path> includedFiles = Arrays.asList(file1, fileInSubdir);
        Retrolambda.visitFiles(inputDir, includedFiles, visitor);
        MatcherAssert.assertThat(visitedFiles, Matchers.containsInAnyOrder(file1, fileInSubdir));
    }

    @Test
    public void ignores_included_files_that_are_outside_the_input_directory() throws IOException {
        List<Path> includedFiles = Arrays.asList(file1, outsider);
        Retrolambda.visitFiles(inputDir, includedFiles, visitor);
        MatcherAssert.assertThat(visitedFiles, Matchers.containsInAnyOrder(file1));
    }

    @Test
    public void copies_resources_to_output_directory() throws Throwable {
        Properties p = new Properties();
        p.setProperty(INPUT_DIR, inputDir.toString());
        p.setProperty(OUTPUT_DIR, outputDir.toString());
        p.setProperty(CLASSPATH, "");
        Retrolambda.run(p);
        RetrolambdaTest.assertIsFile(outputDir.resolve("file1.txt"));
        RetrolambdaTest.assertIsFile(outputDir.resolve("subdir/file.txt"));
    }
}

