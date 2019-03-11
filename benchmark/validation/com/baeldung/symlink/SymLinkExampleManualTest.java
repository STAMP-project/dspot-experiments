package com.baeldung.symlink;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;


public class SymLinkExampleManualTest {
    @Test
    public void whenUsingFiles_thenCreateSymbolicLink() throws IOException {
        SymLinkExample example = new SymLinkExample();
        Path filePath = example.createTextFile();
        Path linkPath = Paths.get(".", "symbolic_link.txt");
        example.createSymbolicLink(linkPath, filePath);
        Assert.assertTrue(Files.isSymbolicLink(linkPath));
    }

    @Test
    public void whenUsingFiles_thenCreateHardLink() throws IOException {
        SymLinkExample example = new SymLinkExample();
        Path filePath = example.createTextFile();
        Path linkPath = Paths.get(".", "hard_link.txt");
        example.createHardLink(linkPath, filePath);
        Assert.assertFalse(Files.isSymbolicLink(linkPath));
        Assert.assertEquals(filePath.toFile().length(), linkPath.toFile().length());
    }
}

