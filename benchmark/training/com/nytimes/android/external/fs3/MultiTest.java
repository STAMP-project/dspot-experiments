package com.nytimes.android.external.fs3;


import com.google.common.collect.ImmutableMap;
import com.nytimes.android.external.fs3.filesystem.FileSystem;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Test;


public class MultiTest {
    private static final Map<String, List<String>> fileData = ImmutableMap.<String, List<String>>builder().put("/foo/bar.txt", Arrays.asList("sfvSFv", "AsfgasFgae", "szfvzsfbzdsfb")).put("/foo/bar/baz.xyz", Arrays.asList("sasffvSFv", "AsfgsdvzsfbvasFgae", "szfvzsfszfvzsvbzdsfb")).build();

    @Test
    public void testDeleteAll() throws IOException {
        FileSystem fileSystem = createAndPopulateTestFileSystem();
        fileSystem.deleteAll("/");
        assertThat(fileSystem.list("/").size()).isZero();
    }

    @Test
    public void listNCompare() throws IOException {
        FileSystem fileSystem = createAndPopulateTestFileSystem();
        int assertCount = 0;
        for (String path : fileSystem.list("/")) {
            String data = fileSystem.read(path).readUtf8();
            List<String> written = MultiTest.fileData.get(path);
            String writtenData = written.get(((written.size()) - 1));
            assertThat(data).isEqualTo(writtenData);
            assertCount++;
        }
        assertThat(assertCount).isEqualTo(MultiTest.fileData.size());
    }
}

