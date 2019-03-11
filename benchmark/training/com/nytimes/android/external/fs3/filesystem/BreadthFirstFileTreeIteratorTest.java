package com.nytimes.android.external.fs3.filesystem;


import java.io.File;
import java.io.IOException;
import org.junit.Test;


public class BreadthFirstFileTreeIteratorTest {
    private File systemTempDir;

    @Test
    public void testHasNextEmpty() throws IOException {
        File hasNextDir = createDirWithSubFiles(systemTempDir, 0);
        BreadthFirstFileTreeIterator btfti = new BreadthFirstFileTreeIterator(hasNextDir);
        assertThat(btfti.hasNext()).isFalse();
    }

    @Test
    public void testHasNextOne() throws IOException {
        File hasNextDir = createDirWithSubFiles(systemTempDir, 1);
        BreadthFirstFileTreeIterator btfti = new BreadthFirstFileTreeIterator(hasNextDir);
        assertThat(btfti.hasNext()).isTrue();
        assertThat(btfti.next()).isNotNull();
        assertThat(btfti.hasNext()).isFalse();
    }

    @Test
    public void testHastNextMany() throws IOException {
        int fileCount = 30;
        File hasNextDir = createDirWithSubFiles(systemTempDir, fileCount);
        createDirWithSubFiles(hasNextDir, fileCount);
        BreadthFirstFileTreeIterator btfti = new BreadthFirstFileTreeIterator(hasNextDir);
        int counter = 0;
        while (btfti.hasNext()) {
            btfti.next();
            counter++;
        } 
        assertThat(counter).isEqualTo((fileCount * 2));
    }
}

