package com.nytimes.android.external.fs3;


import com.nytimes.android.external.fs3.filesystem.FileSystem;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.base.impl.BarCode;
import java.io.FileNotFoundException;
import java.io.IOException;
import okio.BufferedSource;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;


public class FilePersisterTest {
    @Mock
    FileSystem fileSystem;

    @Mock
    BufferedSource bufferedSource;

    private final BarCode simple = new BarCode("type", "key");

    private final String resolvedPath = new BarCodePathResolver().resolve(simple);

    private Persister<BufferedSource, BarCode> fileSystemPersister;

    @Test
    public void readExists() throws FileNotFoundException {
        Mockito.when(fileSystem.exists(resolvedPath)).thenReturn(true);
        Mockito.when(fileSystem.read(resolvedPath)).thenReturn(bufferedSource);
        BufferedSource returnedValue = fileSystemPersister.read(simple).blockingGet();
        assertThat(returnedValue).isEqualTo(bufferedSource);
    }

    @Test
    @SuppressWarnings("CheckReturnValue")
    public void readDoesNotExist() throws FileNotFoundException {
        Mockito.when(fileSystem.exists(resolvedPath)).thenReturn(false);
        fileSystemPersister.read(simple).test().assertError(FileNotFoundException.class);
    }

    @Test
    @SuppressWarnings("CheckReturnValue")
    public void writeThenRead() throws IOException {
        Mockito.when(fileSystem.read(resolvedPath)).thenReturn(bufferedSource);
        Mockito.when(fileSystem.exists(resolvedPath)).thenReturn(true);
        fileSystemPersister.write(simple, bufferedSource).blockingGet();
        BufferedSource source = fileSystemPersister.read(simple).blockingGet();
        InOrder inOrder = Mockito.inOrder(fileSystem);
        inOrder.verify(fileSystem).write(resolvedPath, bufferedSource);
        inOrder.verify(fileSystem).exists(resolvedPath);
        inOrder.verify(fileSystem).read(resolvedPath);
        assertThat(source).isEqualTo(bufferedSource);
    }
}

