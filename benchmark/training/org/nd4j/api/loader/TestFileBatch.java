package org.nd4j.api.loader;


import FileBatch.ORIGINAL_PATHS_FILENAME;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class TestFileBatch {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testFileBatch() throws Exception {
        File baseDir = testDir.newFolder();
        List<File> fileList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String s = "File contents - file " + i;
            File f = new File(baseDir, (("origFile" + i) + ".txt"));
            FileUtils.writeStringToFile(f, s, StandardCharsets.UTF_8);
            fileList.add(f);
        }
        FileBatch fb = FileBatch.forFiles(fileList);
        Assert.assertEquals(10, fb.getFileBytes().size());
        Assert.assertEquals(10, fb.getOriginalUris().size());
        for (int i = 0; i < 10; i++) {
            byte[] expBytes = ("File contents - file " + i).getBytes(StandardCharsets.UTF_8);
            byte[] actBytes = fb.getFileBytes().get(i);
            Assert.assertArrayEquals(expBytes, actBytes);
            String expPath = fileList.get(i).toURI().toString();
            String actPath = fb.getOriginalUris().get(i);
            Assert.assertEquals(expPath, actPath);
        }
        // Save and load:
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        fb.writeAsZip(baos);
        byte[] asBytes = baos.toByteArray();
        FileBatch fb2;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(asBytes)) {
            fb2 = FileBatch.readFromZip(bais);
        }
        Assert.assertEquals(fb.getOriginalUris(), fb2.getOriginalUris());
        Assert.assertEquals(10, fb2.getFileBytes().size());
        for (int i = 0; i < 10; i++) {
            Assert.assertArrayEquals(fb.getFileBytes().get(i), fb2.getFileBytes().get(i));
        }
        // Check that it is indeed a valid zip file:
        File f = testDir.newFile();
        f.delete();
        fb.writeAsZip(f);
        ZipFile zf = new ZipFile(f);
        Enumeration<? extends ZipEntry> e = zf.entries();
        int count = 0;
        Set<String> names = new HashSet<>();
        while (e.hasMoreElements()) {
            ZipEntry entry = e.nextElement();
            names.add(entry.getName());
        } 
        Assert.assertEquals(11, names.size());// 10 files, 1 "original file names" file

        Assert.assertTrue(names.contains(ORIGINAL_PATHS_FILENAME));
        for (int i = 0; i < 10; i++) {
            String n = ("file_" + i) + ".txt";
            Assert.assertTrue(n, names.contains(n));
        }
    }
}

