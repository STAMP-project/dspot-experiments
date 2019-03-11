package com.tagtraum.perf.gcviewer.ctrl.impl;


import com.tagtraum.perf.gcviewer.UnittestHelper;
import com.tagtraum.perf.gcviewer.model.GCResource;
import com.tagtraum.perf.gcviewer.model.GcResourceFile;
import com.tagtraum.perf.gcviewer.view.GCViewerGui;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static com.tagtraum.perf.gcviewer.UnittestHelper.FOLDER.OPENJDK;


/**
 * Unittest for main controller class of GCViewerGui ({@link GCModelLoaderControllerImpl}).
 * This is rather an integration test than a unittest, so if one of these tests fail, first
 * check other failures before checking here.
 *
 * @author <a href="mailto:gcviewer@gmx.ch">Joerg Wuethrich</a>
<p>created on: 05.01.2014</p>
 */
public class TestGCModelLoaderController {
    private GCModelLoaderControllerImpl controller;

    private GCViewerGui gcViewerGui;

    @Test
    public void openStringFail() throws Exception {
        controller.open(new GcResourceFile("does_not_exist"));
        Assert.assertThat("number of gcdocuments", controller.getGCViewerGui().getDesktopPane().getAllFrames().length, Matchers.is(1));
    }

    @Test
    public void openString() throws Exception {
        Assert.assertThat("number of gcdocuments before", controller.getGCViewerGui().getDesktopPane().getAllFrames().length, Matchers.is(0));
        controller.open(new GcResourceFile(UnittestHelper.getResourceAsString(OPENJDK, "SampleSun1_6_0CMS.txt")));
        Assert.assertThat("number of gcdocuments after", controller.getGCViewerGui().getDesktopPane().getAllFrames().length, Matchers.is(1));
    }

    @Test
    public void openAsSeries() throws Exception {
        Assert.assertThat("number of gcdocuments before", controller.getGCViewerGui().getDesktopPane().getAllFrames().length, Matchers.is(0));
        ArrayList<GCResource> gcResourceList = getGcResourcesForSeries();
        controller.openAsSeries(gcResourceList);
        Assert.assertThat("number of gcdocuments after", controller.getGCViewerGui().getDesktopPane().getAllFrames().length, Matchers.is(1));// Input files are merged -> only one file is open

        Assert.assertThat(getOpenResources(), Matchers.contains(new com.tagtraum.perf.gcviewer.model.GcResourceSeries(gcResourceList)));
    }

    /**
     * Test drag and drop action on GCViewerGui.
     */
    @Test
    public void dropOnDesktopPane() throws Exception {
        // TODO SWINGWORKER: test drag and drop on GCDocument
        Assert.assertThat("number of gcdocuments before", controller.getGCViewerGui().getDesktopPane().getAllFrames().length, Matchers.is(0));
        List<File> fileList = new ArrayList<File>();
        fileList.add(new File(UnittestHelper.getResource(OPENJDK, "SampleSun1_6_0G1_MarkStackFull.txt").getPath()));
        Transferable tr = Mockito.mock(Transferable.class);
        Mockito.when(tr.getTransferData(DataFlavor.javaFileListFlavor)).thenReturn(fileList);
        DropTargetDropEvent dte = Mockito.mock(DropTargetDropEvent.class);
        Mockito.when(dte.isDataFlavorSupported(DataFlavor.javaFileListFlavor)).thenReturn(true);
        Mockito.when(dte.getTransferable()).thenReturn(tr);
        DropTarget target = controller.getGCViewerGui().getDesktopPane().getDropTarget();
        target.drop(dte);
        Assert.assertThat("number of gcdocuments after drop", controller.getGCViewerGui().getDesktopPane().getAllFrames().length, Matchers.is(1));
    }

    @Test
    public void open_File() throws Exception {
        File[] files = new File[1];
        File file = new File(UnittestHelper.getResourceAsString(OPENJDK, "SampleSun1_6_0CMS.txt"));
        files[0] = file;
        controller.open(files);
        Assert.assertThat(getOpenResources(), Matchers.contains(new GcResourceFile(file)));
    }

    @Test
    public void open_GcResourceFile() throws Exception {
        GCResource resource = new GcResourceFile(UnittestHelper.getResourceAsString(OPENJDK, "SampleSun1_6_0CMS.txt"));
        controller.open(resource);
        Assert.assertThat(getOpenResources(), Matchers.contains(resource));
    }

    @Test
    public void open_GcResourceSeries() throws Exception {
        List<GCResource> resources = getGcResourcesForSeries();
        GCResource series = new com.tagtraum.perf.gcviewer.model.GcResourceSeries(resources);
        controller.open(series);
        Assert.assertThat(getOpenResources(), Matchers.contains(series));
    }
}

