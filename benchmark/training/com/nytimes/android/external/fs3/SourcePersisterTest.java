package com.nytimes.android.external.fs3;


import com.nytimes.android.external.fs3.filesystem.FileSystem;
import com.nytimes.android.external.store3.base.impl.BarCode;
import java.io.FileNotFoundException;
import java.io.IOException;
import okio.BufferedSource;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;


public class SourcePersisterTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    FileSystem fileSystem;

    @Mock
    BufferedSource bufferedSource;

    private SourcePersister sourcePersister;

    private final BarCode simple = new BarCode("type", "key");

    @Test
    public void readExists() throws FileNotFoundException {
        Mockito.when(fileSystem.exists(simple.toString())).thenReturn(true);
        Mockito.when(fileSystem.read(simple.toString())).thenReturn(bufferedSource);
        BufferedSource returnedValue = sourcePersister.read(simple).blockingGet();
        assertThat(returnedValue).isEqualTo(bufferedSource);
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

