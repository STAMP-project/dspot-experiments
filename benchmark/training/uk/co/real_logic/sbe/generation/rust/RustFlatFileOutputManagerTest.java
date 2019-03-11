package uk.co.real_logic.sbe.generation.rust;


import java.io.File;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class RustFlatFileOutputManagerTest {
    @Rule
    public final TemporaryFolder folderRule = new TemporaryFolder();

    static final String PACKAGE_NAME = "uk.co.real_logic.test";

    static final String EXAMPLE_CLASS_NAME = "ExampleClassName";

    @Test
    public void shouldCreateFileUponConstruction() throws Exception {
        final File tempDir = folderRule.getRoot();
        final String tempDirName = tempDir.getAbsolutePath();
        final RustFlatFileOutputManager om = new RustFlatFileOutputManager(tempDirName, RustFlatFileOutputManagerTest.PACKAGE_NAME);
        final String expectedFullyQualifiedFilename = RustFlatFileOutputManagerTest.getExpectedFullFileName(tempDirName);
        final Path path = FileSystems.getDefault().getPath(expectedFullyQualifiedFilename);
        Assert.assertTrue(Files.exists(path));
        final String initialContents = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        Assert.assertTrue(initialContents.contains("Generated code"));
        final String arbitraryInput = "\narbitrary\n";
        Assert.assertFalse(initialContents.contains(arbitraryInput));
        try (Writer out = om.createOutput(RustFlatFileOutputManagerTest.EXAMPLE_CLASS_NAME)) {
            out.write(arbitraryInput);
        }
        final String postOutputContents = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        Assert.assertTrue(postOutputContents.contains("Generated code"));
        Assert.assertTrue(postOutputContents.contains(arbitraryInput));
    }

    @Test(expected = NullPointerException.class)
    public void nullDirectoryParamThrowsNPE() {
        new RustFlatFileOutputManager(null, RustFlatFileOutputManagerTest.PACKAGE_NAME);
    }

    @Test(expected = NullPointerException.class)
    public void nullPackageParamThrowsNpe() {
        new RustFlatFileOutputManager(folderRule.getRoot().getAbsolutePath(), null);
    }
}

