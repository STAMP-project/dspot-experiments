package com.querydsl.jpa.codegen.ant;


import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.TemporaryFolder;


public class AntJPADomainExporterTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Rule
    public ErrorCollector errors = new ErrorCollector();

    @Test
    public void test() throws IOException {
        AntJPADomainExporter exporter = new AntJPADomainExporter();
        exporter.setNamePrefix("Q");
        exporter.setNameSuffix("");
        Path outputFolder = folder.getRoot().toPath();
        exporter.setTargetFolder(outputFolder.toFile().getAbsolutePath());
        exporter.setPersistenceUnitName("h2");
        exporter.execute();
        File origRoot = new File("../querydsl-jpa/target/generated-test-sources/java");
        Set<File> files = exporter.getGeneratedFiles();
        Assert.assertFalse(files.isEmpty());
        for (File file : files) {
            Path relativeFile = outputFolder.relativize(file.toPath());
            Path origFile = origRoot.toPath().resolve(relativeFile);
            String reference = Files.toString(origFile.toFile(), Charsets.UTF_8);
            String content = Files.toString(file, Charsets.UTF_8);
            errors.checkThat(("Mismatch for " + (file.getPath())), content, CoreMatchers.is(CoreMatchers.equalTo(reference)));
        }
    }
}

