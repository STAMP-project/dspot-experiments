package com.nytimes.android.external.fs3;


import RecordState.FRESH;
import RecordState.MISSING;
import RecordState.STALE;
import com.nytimes.android.external.fs3.filesystem.FileSystem;
import com.nytimes.android.external.store3.base.impl.BarCode;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okio.BufferedSource;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class RecordPersisterTest {
    @Mock
    FileSystem fileSystem;

    @Mock
    BufferedSource bufferedSource;

    private RecordPersister sourcePersister;

    private final BarCode simple = new BarCode("type", "key");

    @Test
    public void readExists() throws FileNotFoundException {
        Mockito.when(fileSystem.exists(simple.toString())).thenReturn(true);
        Mockito.when(fileSystem.read(simple.toString())).thenReturn(bufferedSource);
        BufferedSource returnedValue = sourcePersister.read(simple).blockingGet();
        assertThat(returnedValue).isEqualTo(bufferedSource);
    }

    @Test
    public void freshTest() {
        Mockito.when(fileSystem.getRecordState(TimeUnit.DAYS, 1L, SourcePersister.pathForBarcode(simple))).thenReturn(FRESH);
        assertThat(sourcePersister.getRecordState(simple)).isEqualTo(FRESH);
    }

    @Test
    public void staleTest() {
        Mockito.when(fileSystem.getRecordState(TimeUnit.DAYS, 1L, SourcePersister.pathForBarcode(simple))).thenReturn(STALE);
        assertThat(sourcePersister.getRecordState(simple)).isEqualTo(STALE);
    }

    @Test
    public void missingTest() {
        Mockito.when(fileSystem.getRecordState(TimeUnit.DAYS, 1L, SourcePersister.pathForBarcode(simple))).thenReturn(MISSING);
        assertThat(sourcePersister.getRecordState(simple)).isEqualTo(MISSING);
    }

    @Test
    @SuppressWarnings("CheckReturnValue")
    public void readDoesNotExist() throws FileNotFoundException {
        Mockito.when(fileSystem.exists(SourcePersister.pathForBarcode(simple))).thenReturn(false);
        sourcePersister.read(simple).test().assertError(FileNotFoundException.class);
    }

    @Test
    public void write() throws IOException {
        assertThat(sourcePersister.write(simple, bufferedSource).blockingGet()).isTrue();
    }

    @Test
    public void pathForBarcode() {
        assertThat(SourcePersister.pathForBarcode(simple)).isEqualTo("typekey");
    }
}

