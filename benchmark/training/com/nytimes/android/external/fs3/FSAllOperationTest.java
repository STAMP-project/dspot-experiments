package com.nytimes.android.external.fs3;


import com.google.common.io.Files;
import com.nytimes.android.external.fs3.filesystem.FileSystem;
import com.nytimes.android.external.fs3.filesystem.FileSystemFactory;
import io.reactivex.Observable;
import java.io.File;
import java.io.IOException;
import okio.BufferedSource;
import org.junit.Test;


public class FSAllOperationTest {
    public static final String FOLDER = "type";

    public static final String INNER_FOLDER = "type2";

    public static final String CHALLAH = "Challah";

    public static final String CHALLAH_CHALLAH = "Challah_CHALLAH";

    @Test
    public void readAll() throws IOException {
        File tempDir = Files.createTempDir();
        FileSystem fileSystem = FileSystemFactory.create(tempDir);
        // write different data to File System for each barcode
        fileSystem.write(((FSAllOperationTest.FOLDER) + "/key.txt"), FSAllOperationTest.source(FSAllOperationTest.CHALLAH));
        fileSystem.write(((((FSAllOperationTest.FOLDER) + "/") + (FSAllOperationTest.INNER_FOLDER)) + "/key2.txt"), FSAllOperationTest.source(FSAllOperationTest.CHALLAH_CHALLAH));
        FSAllReader reader = new FSAllReader(fileSystem);
        // read back all values for the FOLDER
        Observable<BufferedSource> observable = reader.readAll(FSAllOperationTest.FOLDER);
        assertThat(observable.blockingFirst().readUtf8()).isEqualTo(FSAllOperationTest.CHALLAH);
        assertThat(observable.blockingLast().readUtf8()).isEqualTo(FSAllOperationTest.CHALLAH_CHALLAH);
    }

    @Test
    public void deleteAll() throws IOException {
        File tempDir = Files.createTempDir();
        FileSystem fileSystem = FileSystemFactory.create(tempDir);
        // write different data to File System for each barcode
        fileSystem.write(((FSAllOperationTest.FOLDER) + "/key.txt"), FSAllOperationTest.source(FSAllOperationTest.CHALLAH));
        fileSystem.write(((((FSAllOperationTest.FOLDER) + "/") + (FSAllOperationTest.INNER_FOLDER)) + "/key2.txt"), FSAllOperationTest.source(FSAllOperationTest.CHALLAH_CHALLAH));
        FSAllEraser eraser = new FSAllEraser(fileSystem);
        Observable<Boolean> observable = eraser.deleteAll(FSAllOperationTest.FOLDER);
        assertThat(observable.blockingFirst()).isEqualTo(true);
    }
}

