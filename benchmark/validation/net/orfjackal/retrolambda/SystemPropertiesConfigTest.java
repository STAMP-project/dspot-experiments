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


import RetrolambdaApi.BYTECODE_VERSION;
import RetrolambdaApi.CLASSPATH;
import RetrolambdaApi.CLASSPATH_FILE;
import RetrolambdaApi.DEFAULT_METHODS;
import RetrolambdaApi.INCLUDED_FILES;
import RetrolambdaApi.INCLUDED_FILES_FILE;
import RetrolambdaApi.INPUT_DIR;
import RetrolambdaApi.OUTPUT_DIR;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import net.orfjackal.retrolambda.util.Bytecode;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


public class SystemPropertiesConfigTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Rule
    public final TemporaryFolder tempDir = new TemporaryFolder();

    private final Properties systemProperties = new Properties();

    @Test
    public void is_fully_configured_when_required_properties_are_set() {
        MatcherAssert.assertThat("before", config().isFullyConfigured(), is(false));
        systemProperties.setProperty(INPUT_DIR, "");
        systemProperties.setProperty(CLASSPATH, "");
        MatcherAssert.assertThat("after", config().isFullyConfigured(), is(true));
    }

    @Test
    public void can_use_alternative_parameter_instead_of_required_parameter() {
        systemProperties.setProperty(INPUT_DIR, "");
        systemProperties.setProperty(CLASSPATH_FILE, "");
        MatcherAssert.assertThat("is fully configured?", config().isFullyConfigured(), is(true));
    }

    @Test
    public void bytecode_version() {
        MatcherAssert.assertThat("defaults to Java 7", config().getBytecodeVersion(), is(51));
        MatcherAssert.assertThat("human printable format", Bytecode.getJavaVersion(config().getBytecodeVersion()), is("Java 7"));
        systemProperties.setProperty(BYTECODE_VERSION, "50");
        MatcherAssert.assertThat("can override the default", config().getBytecodeVersion(), is(50));
        MatcherAssert.assertThat("human printable format", Bytecode.getJavaVersion(config().getBytecodeVersion()), is("Java 6"));
    }

    @Test
    public void default_methods() {
        MatcherAssert.assertThat("defaults to disabled", config().isDefaultMethodsEnabled(), is(false));
        systemProperties.setProperty(DEFAULT_METHODS, "true");
        MatcherAssert.assertThat("can override the default", config().isDefaultMethodsEnabled(), is(true));
    }

    @Test
    public void input_directory_is_required() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Missing required property: retrolambda.inputDir");
        config().getInputDir();
    }

    @Test
    public void output_directory() {
        systemProperties.setProperty(INPUT_DIR, "input dir");
        MatcherAssert.assertThat("defaults to input dir", config().getOutputDir(), is(Paths.get("input dir")));
        systemProperties.setProperty(OUTPUT_DIR, "output dir");
        MatcherAssert.assertThat("can override the default", config().getOutputDir(), is(Paths.get("output dir")));
    }

    @Test
    public void classpath() {
        systemProperties.setProperty(CLASSPATH, "");
        MatcherAssert.assertThat("zero values", config().getClasspath(), is(empty()));
        systemProperties.setProperty(CLASSPATH, "one.jar");
        MatcherAssert.assertThat("one value", config().getClasspath(), is(Arrays.asList(Paths.get("one.jar"))));
        systemProperties.setProperty(CLASSPATH, (("one.jar" + (File.pathSeparator)) + "two.jar"));
        MatcherAssert.assertThat("multiple values", config().getClasspath(), is(Arrays.asList(Paths.get("one.jar"), Paths.get("two.jar"))));
    }

    @Test
    public void classpath_file() throws IOException {
        Path file = tempDir.newFile("classpath.txt").toPath();
        Files.write(file, Arrays.asList("", "", ""));// empty lines are ignored

        systemProperties.setProperty(CLASSPATH_FILE, file.toString());
        MatcherAssert.assertThat("zero values", config().getClasspath(), is(empty()));
        Files.write(file, Arrays.asList("one.jar"));
        systemProperties.setProperty(CLASSPATH_FILE, file.toString());
        MatcherAssert.assertThat("one value", config().getClasspath(), is(Arrays.asList(Paths.get("one.jar"))));
        Files.write(file, Arrays.asList("one.jar", "two.jar"));
        systemProperties.setProperty(CLASSPATH_FILE, file.toString());
        MatcherAssert.assertThat("multiple values", config().getClasspath(), is(Arrays.asList(Paths.get("one.jar"), Paths.get("two.jar"))));
    }

    @Test
    public void classpath_is_required() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Missing required property: retrolambda.classpath");
        config().getClasspath();
    }

    @Test
    public void included_files() {
        MatcherAssert.assertThat("not set", config().getIncludedFiles(), is(nullValue()));
        systemProperties.setProperty(INCLUDED_FILES, "");
        MatcherAssert.assertThat("zero values", config().getIncludedFiles(), is(empty()));
        systemProperties.setProperty(INCLUDED_FILES, "/foo/one.class");
        MatcherAssert.assertThat("one value", config().getIncludedFiles(), is(Arrays.asList(Paths.get("/foo/one.class"))));
        systemProperties.setProperty(INCLUDED_FILES, (("/foo/one.class" + (File.pathSeparator)) + "/foo/two.class"));
        MatcherAssert.assertThat("multiple values", config().getIncludedFiles(), is(Arrays.asList(Paths.get("/foo/one.class"), Paths.get("/foo/two.class"))));
    }

    @Test
    public void included_files_file() throws IOException {
        Path file = tempDir.newFile("includedFiles.txt").toPath();
        MatcherAssert.assertThat("not set", config().getIncludedFiles(), is(nullValue()));
        Files.write(file, Arrays.asList("", "", ""));// empty lines are ignored

        systemProperties.setProperty(INCLUDED_FILES_FILE, file.toString());
        MatcherAssert.assertThat("zero values", config().getIncludedFiles(), is(empty()));
        Files.write(file, Arrays.asList("one.class"));
        systemProperties.setProperty(INCLUDED_FILES_FILE, file.toString());
        MatcherAssert.assertThat("one value", config().getIncludedFiles(), is(Arrays.asList(Paths.get("one.class"))));
        Files.write(file, Arrays.asList("one.class", "two.class"));
        systemProperties.setProperty(INCLUDED_FILES_FILE, file.toString());
        MatcherAssert.assertThat("multiple values", config().getIncludedFiles(), is(Arrays.asList(Paths.get("one.class"), Paths.get("two.class"))));
    }
}

