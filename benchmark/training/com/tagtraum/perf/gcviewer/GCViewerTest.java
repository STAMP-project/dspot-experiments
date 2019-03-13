package com.tagtraum.perf.gcviewer;


import com.tagtraum.perf.gcviewer.ctrl.impl.GCViewerGuiController;
import com.tagtraum.perf.gcviewer.model.GCResource;
import com.tagtraum.perf.gcviewer.model.GcResourceFile;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author martin.geldmacher
 */
public class GCViewerTest {
    @Test
    public void singleArgumentOpensGui() throws Exception {
        GCViewerGuiController controller = Mockito.mock(GCViewerGuiController.class);
        GCViewer gcViewer = new GCViewer(controller, new GCViewerArgsParser());
        String[] args = new String[]{ "some_gc.log" };
        int exitValue = gcViewer.doMain(args);
        Mockito.verify(controller).startGui(new GcResourceFile("some_gc.log"));
        MatcherAssert.assertThat("exitValue of doMain", exitValue, Matchers.is(0));
    }

    @Test
    public void singleArgumentWithSeriesOpensGui() throws Exception {
        GCViewerGuiController controller = Mockito.mock(GCViewerGuiController.class);
        GCViewer gcViewer = new GCViewer(controller, new GCViewerArgsParser());
        String[] args = new String[]{ "some_gc.log.0;some_gc.log.1;some_gc.log.2" };
        int exitValue = gcViewer.doMain(args);
        Mockito.verify(controller).startGui(new com.tagtraum.perf.gcviewer.model.GcResourceSeries(Arrays.asList(new GcResourceFile("some_gc.log.0"), new GcResourceFile("some_gc.log.1"), new GcResourceFile("some_gc.log.2"))));
        MatcherAssert.assertThat("result of doMain", exitValue, Matchers.is(0));
    }

    @Test
    public void moreThan3ArgumentsPrintsUsage() throws Exception {
        GCViewerGuiController controller = Mockito.mock(GCViewerGuiController.class);
        GCViewer gcViewer = new GCViewer(controller, new GCViewerArgsParser());
        String[] args = new String[]{ "argument1", "argument2", "argument3", "argument4" };
        int exitValue = gcViewer.doMain(args);
        Mockito.verify(controller, Mockito.never()).startGui(ArgumentMatchers.any(GCResource.class));
        MatcherAssert.assertThat("result of doMain", exitValue, Matchers.is((-3)));
    }

    @Test
    public void export() throws Exception {
        GCViewerGuiController controller = Mockito.mock(GCViewerGuiController.class);
        GCViewer gcViewer = new GCViewer(controller, new GCViewerArgsParser());
        String[] args = new String[]{ "target/test-classes/openjdk/SampleSun1_7_0-01_G1_young.txt", "target/export.csv", "target/export.png", "-t", "PLAIN" };
        int exitValue = gcViewer.doMain(args);
        Mockito.verify(controller, Mockito.never()).startGui(ArgumentMatchers.any(GCResource.class));
        MatcherAssert.assertThat("result of doMain", exitValue, Matchers.is(0));
    }

    @Test
    public void exportFileNotFound() throws Exception {
        GCViewerGuiController controller = Mockito.mock(GCViewerGuiController.class);
        GCViewer gcViewer = new GCViewer(controller, new GCViewerArgsParser());
        String[] args = new String[]{ "doesNotExist.log", "export.csv", "-t", "PLAIN" };
        int exitValue = gcViewer.doMain(args);
        Mockito.verify(controller, Mockito.never()).startGui(ArgumentMatchers.any(GCResource.class));
        MatcherAssert.assertThat("result of doMain", exitValue, Matchers.is((-1)));
    }

    @Test
    public void illegalExportFormat() throws Exception {
        GCViewer gcViewer = new GCViewer();
        String[] args = new String[]{ "-t", "INVALID" };
        int exitValue = gcViewer.doMain(args);
        MatcherAssert.assertThat("result of doMain", exitValue, Matchers.is((-2)));
    }
}

