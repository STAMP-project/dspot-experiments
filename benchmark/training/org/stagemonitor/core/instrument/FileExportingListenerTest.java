package org.stagemonitor.core.instrument;


import FileExportingListener.exportedClasses;
import com.codahale.metrics.annotation.Timed;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;


public class FileExportingListenerTest {
    @Test
    public void testExportIncludedClasses() throws Exception {
        new FileExportingListenerTest.ExportMe();
        Assert.assertEquals(1, exportedClasses.size());
        Assert.assertTrue(exportedClasses.get(0).contains(FileExportingListenerTest.ExportMe.class.getName()));
        final File exportedClass = new File(exportedClasses.get(0));
        Assert.assertTrue(exportedClass.exists());
    }

    @Test
    public void testDoNotExportNotIncludedClasses() throws Exception {
        new FileExportingListenerTest.DoNotExportMe();
        Assert.assertEquals(0, exportedClasses.size());
    }

    private static class ExportMe {
        @Timed
        public void test() {
        }
    }

    private static class DoNotExportMe {}
}

