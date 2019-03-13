package com.nytimes.android.external.fs3.filesystem;


import java.io.File;
import java.io.IOException;
import okio.Buffer;
import okio.BufferedSource;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class FSFileTest {
    private static final String TEST_FILE_PATH = "/test_file";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Mock
    File root;

    @Mock
    BufferedSource source;

    private FSFile fsFile;

    @Test
    public void closeSourceAfterWrite() throws IOException {
        Mockito.when(source.read(ArgumentMatchers.any(Buffer.class), ArgumentMatchers.anyByte())).thenReturn(Long.valueOf((-1)));
        fsFile.write(source);
        Mockito.verify(source).close();
    }
}

