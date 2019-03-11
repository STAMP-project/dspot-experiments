package org.nd4j.linalg.io;


import java.io.File;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class ClassPathResourceTest {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testDirExtractingIntelliJ() throws Exception {
        // https://github.com/deeplearning4j/deeplearning4j/issues/6483
        ClassPathResource cpr = new ClassPathResource("somedir");
        File f = testDir.newFolder();
        cpr.copyDirectory(f);
        File[] files = f.listFiles();
        Assert.assertEquals(1, files.length);
        Assert.assertEquals("afile.txt", files[0].getName());
    }
}

