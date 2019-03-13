package com.tagtraum.perf.gcviewer.model;


import com.tagtraum.perf.gcviewer.UnittestHelper;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import static com.tagtraum.perf.gcviewer.UnittestHelper.FOLDER.OPENJDK;


public class GcResourceSeriesTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private GcResourceSeries resourceSeries;

    private List<GCResource> resourceList;

    @Test
    public void hasUnderlyingResourceChanged() throws Exception {
        for (GCResource resource : resourceList) {
            GCModel model = new GCModel();
            model.setURL(getResourceNameAsUrl());
            resource.setModel(model);
        }
        MatcherAssert.assertThat(resourceSeries.hasUnderlyingResourceChanged(), CoreMatchers.is(false));
        // Changing first resource doesn't register
        resourceList.get(0).setModel(new GCModel());
        MatcherAssert.assertThat(resourceSeries.hasUnderlyingResourceChanged(), CoreMatchers.is(false));
        // Changing last resource does register
        resourceList.get(((resourceList.size()) - 1)).setModel(new GCModel());
        MatcherAssert.assertThat(resourceSeries.hasUnderlyingResourceChanged(), CoreMatchers.is(true));
    }

    @Test
    public void buildName() throws Exception {
        MatcherAssert.assertThat(GcResourceSeries.buildName(resourceList), CoreMatchers.is((((UnittestHelper.getResource(OPENJDK.getFolderName()).getPath()) + "/") + "SampleSun1_8_0Series-Part1-3")));
    }

    @Test
    public void buildName_ForRotatedFile() throws Exception {
        MatcherAssert.assertThat(GcResourceSeries.buildName("garbageCollection.log.0", "garbageCollection.log.6.current"), CoreMatchers.is("garbageCollection.log.0-6"));
    }

    @Test
    public void buildName_WhenOnlyOneEntryExists() throws Exception {
        resourceList = new ArrayList();
        GcResourceFile gcResourceFile = new GcResourceFile(UnittestHelper.getResource(OPENJDK, "SampleSun1_8_0Series-Part1.txt").getPath());
        resourceList.add(gcResourceFile);
        MatcherAssert.assertThat(GcResourceSeries.buildName(resourceList), CoreMatchers.is(gcResourceFile.getResourceName()));
    }

    @Test
    public void buildName_WhenNoCommonPrefixExists() throws Exception {
        List<GCResource> resources = new ArrayList<>();
        GCResource resource1 = Mockito.mock(GCResource.class);
        Mockito.when(resource1.getResourceName()).thenReturn("abc.log");
        resources.add(resource1);
        GCResource resource2 = Mockito.mock(GCResource.class);
        Mockito.when(resource2.getResourceName()).thenReturn("xyz.log");
        resources.add(resource2);
        MatcherAssert.assertThat(GcResourceSeries.buildName(resources), CoreMatchers.is("abc.log-xyz.log"));
    }

    @Test
    public void buildName_WhenListIsEmpty() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        GcResourceSeries.buildName(new ArrayList());
    }
}

