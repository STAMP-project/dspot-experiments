package com.tagtraum.perf.gcviewer.ctrl.impl;


import com.tagtraum.perf.gcviewer.model.GCResource;
import com.tagtraum.perf.gcviewer.model.GcResourceFile;
import com.tagtraum.perf.gcviewer.model.GcResourceSeries;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 *
 * @author martin.geldmacher
 */
@RunWith(MockitoJUnitRunner.class)
public class GCModelLoaderFactoryTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void createFor_GcResourceFile() throws Exception {
        GCResource gcResource = Mockito.mock(GcResourceFile.class);
        MatcherAssert.assertThat(GCModelLoaderFactory.createFor(gcResource), CoreMatchers.instanceOf(GCModelLoaderImpl.class));
    }

    @Test
    public void createFor_GcResourceSeries() throws Exception {
        GCResource gcResource = Mockito.mock(GcResourceSeries.class);
        MatcherAssert.assertThat(GCModelLoaderFactory.createFor(gcResource), CoreMatchers.instanceOf(GCModelSeriesLoaderImpl.class));
    }

    @Test
    public void createFor_GcResourceUnknown() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        GCResource gcResource = Mockito.mock(GCResource.class);
        GCModelLoaderFactory.createFor(gcResource);
    }
}

