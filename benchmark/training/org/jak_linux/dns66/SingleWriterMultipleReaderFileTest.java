package org.jak_linux.dns66;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Created by jak on 08/04/17.
 */
@RunWith(PowerMockRunner.class)
public class SingleWriterMultipleReaderFileTest {
    private SingleWriterMultipleReaderFile reader;

    private File activeFile;

    private File workFile;

    private FileOutputStream fos;

    @Test
    public void testOpenRead() throws Exception {
        when(reader.openRead()).thenCallRealMethod();
        System.err.println(("Foo " + (reader)));
    }

    @Test
    @PrepareForTest({ FileOutputStream.class, SingleWriterMultipleReaderFile.class })
    public void testStartWrite_success() throws Exception {
        when(reader.startWrite()).thenCallRealMethod();
        when(workFile.exists()).thenReturn(false);
        when(workFile.getPath()).thenReturn("/nonexisting/path/for/dns66");
        whenNew(FileOutputStream.class).withAnyArguments().thenReturn(fos);
        Assert.assertSame(fos, reader.startWrite());
    }

    @Test
    public void testStartWrite_failDelete() throws Exception {
        when(reader.startWrite()).thenCallRealMethod();
        when(workFile.exists()).thenReturn(true);
        when(workFile.delete()).thenReturn(false);
        try {
            reader.startWrite();
            Assert.fail("Failure to delete work file not detected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    public void testFinishWrite_renameFailure() throws Exception {
        doCallRealMethod().when(reader).finishWrite(ArgumentMatchers.any(FileOutputStream.class));
        doNothing().when(reader).failWrite(ArgumentMatchers.any(FileOutputStream.class));
        when(workFile.renameTo(activeFile)).thenReturn(false);
        try {
            reader.finishWrite(fos);
            Mockito.verify(fos).close();
            Mockito.verify(reader).failWrite(fos);
            Assert.fail("Failing rename should fail finish");
        } catch (IOException e) {
            Assert.assertTrue(((e.getMessage()) + "wrong"), e.getMessage().contains("Cannot commit"));
        }
    }

    @Test
    public void testFinishWrite_closeFailure() throws Exception {
        doCallRealMethod().when(reader).finishWrite(ArgumentMatchers.any(FileOutputStream.class));
        doNothing().when(reader).failWrite(ArgumentMatchers.any(FileOutputStream.class));
        doThrow(new IOException("Not closing")).when(fos).close();
        when(workFile.renameTo(activeFile)).thenReturn(true);
        try {
            reader.finishWrite(fos);
            Mockito.verify(fos).close();
            Mockito.verify(reader).failWrite(fos);
            Assert.fail("Failing close should fail finish");
        } catch (IOException e) {
            Assert.assertTrue(((e.getMessage()) + "wrong"), e.getMessage().contains("Not closing"));
        }
    }

    @Test
    public void testFinishWrite_success() throws Exception {
        doCallRealMethod().when(reader).finishWrite(ArgumentMatchers.any(FileOutputStream.class));
        when(workFile.renameTo(activeFile)).thenReturn(true);
        try {
            reader.finishWrite(fos);
            Mockito.verify(fos).close();
        } catch (IOException e) {
            Assert.fail("Successful rename commits transaction");
        }
    }

    @Test
    public void testFailWrite() throws Exception {
        doCallRealMethod().when(reader).finishWrite(ArgumentMatchers.any(FileOutputStream.class));
        doCallRealMethod().when(reader).failWrite(ArgumentMatchers.any(FileOutputStream.class));
        when(workFile.delete()).thenReturn(true);
        try {
            reader.failWrite(fos);
        } catch (Exception e) {
            Assert.fail("Should not throw");
        }
        when(workFile.delete()).thenReturn(false);
        try {
            reader.failWrite(fos);
            Assert.fail("Should throw");
        } catch (Exception e) {
            // pass
        }
    }
}

