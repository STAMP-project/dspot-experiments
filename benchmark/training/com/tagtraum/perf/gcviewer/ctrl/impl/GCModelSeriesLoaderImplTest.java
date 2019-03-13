package com.tagtraum.perf.gcviewer.ctrl.impl;


import com.tagtraum.perf.gcviewer.UnittestHelper;
import com.tagtraum.perf.gcviewer.ctrl.GCModelLoader;
import com.tagtraum.perf.gcviewer.model.GCResource;
import com.tagtraum.perf.gcviewer.model.GcResourceFile;
import com.tagtraum.perf.gcviewer.model.GcResourceSeries;
import java.util.ArrayList;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.tagtraum.perf.gcviewer.UnittestHelper.FOLDER.OPENJDK;


public class GCModelSeriesLoaderImplTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void getGcResource() throws Exception {
        ArrayList<GCResource> gcResourceList = new ArrayList<>();
        gcResourceList.add(new GcResourceFile(UnittestHelper.getResource(OPENJDK, "SampleSun1_8_0Series-Part1.txt").getPath()));
        gcResourceList.add(new GcResourceFile(UnittestHelper.getResource(OPENJDK, "SampleSun1_8_0Series-Part2.txt").getPath()));
        gcResourceList.add(new GcResourceFile(UnittestHelper.getResource(OPENJDK, "SampleSun1_8_0Series-Part3.txt").getPath()));
        GCModelLoader loader = new GCModelSeriesLoaderImpl(new GcResourceSeries(gcResourceList));
        Assert.assertThat(loader.getGcResource(), CoreMatchers.notNullValue());
    }

    @Test
    public void GCModelSeriesLoaderImpl_ForEmptySeries() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        new GCModelSeriesLoaderImpl(new GcResourceSeries(new ArrayList()));
    }

    @Test
    public void loadGcModel() throws Exception {
        ArrayList<GCResource> gcResourceList = new ArrayList<>();
        gcResourceList.add(new GcResourceFile(UnittestHelper.getResource(OPENJDK, "SampleSun1_8_0Series-Part1.txt").getPath()));
        gcResourceList.add(new GcResourceFile(UnittestHelper.getResource(OPENJDK, "SampleSun1_8_0Series-Part2.txt").getPath()));
        gcResourceList.add(new GcResourceFile(UnittestHelper.getResource(OPENJDK, "SampleSun1_8_0Series-Part3.txt").getPath()));
        GCModelSeriesLoaderImpl loader = new GCModelSeriesLoaderImpl(new GcResourceSeries(gcResourceList));
        Assert.assertThat(loader.loadGcModel(), CoreMatchers.notNullValue());
    }
}

