package com.tagtraum.perf.gcviewer.model;


import com.tagtraum.perf.gcviewer.model.AbstractGCEvent.Type;
import java.io.File;
import java.io.RandomAccessFile;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:gcviewer@gmx.ch">Joerg Wuethrich</a> <p>created on: 19.08.2015</p>
 */
public class TestGCResource {
    @Test
    public void hasUnderlyingResourceChanged() throws Exception {
        File testFile = File.createTempFile("GCResourceUnittest", ".txt");
        try {
            try (RandomAccessFile file = new RandomAccessFile(testFile, "rws")) {
                GCResource resource = new GcResourceFile(testFile.getAbsolutePath());
                Assert.assertThat("before", resource.hasUnderlyingResourceChanged(), Matchers.is(true));
                resource.getModel().add(new GCEvent(0.123, 100, 10, 1024, 0.2, Type.PAR_NEW));
                resource.getModel().setURL(testFile.toURI().toURL());
                Assert.assertThat("after initialisation", resource.hasUnderlyingResourceChanged(), Matchers.is(false));
                file.write("hello world".getBytes());
                Assert.assertThat("after file change", resource.hasUnderlyingResourceChanged(), Matchers.is(true));
            }
        } finally {
            Assert.assertThat(testFile.delete(), Matchers.is(true));
        }
    }
}

