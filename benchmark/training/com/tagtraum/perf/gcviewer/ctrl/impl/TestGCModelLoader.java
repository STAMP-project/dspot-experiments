package com.tagtraum.perf.gcviewer.ctrl.impl;


import com.tagtraum.perf.gcviewer.UnittestHelper;
import com.tagtraum.perf.gcviewer.model.GCModel;
import com.tagtraum.perf.gcviewer.model.GCResource;
import com.tagtraum.perf.gcviewer.model.GcResourceFile;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static com.tagtraum.perf.gcviewer.UnittestHelper.FOLDER.OPENJDK;


/**
 * Unittest for {@link GCModelLoaderImpl}.
 *
 * @author <a href="mailto:gcviewer@gmx.ch">Joerg Wuethrich</a>
<p>created on: 05.01.2014</p>
 */
public class TestGCModelLoader {
    @Test
    public void loadExistingFile() throws Exception {
        GCResource gcResource = new GcResourceFile(UnittestHelper.getResourceAsString(OPENJDK, "SampleSun1_6_0CMS.txt"));
        GCModelLoaderImpl loader = new GCModelLoaderImpl(gcResource);
        loader.execute();
        GCModel model = loader.get();
        Assert.assertThat("model", model, Matchers.notNullValue());
        Assert.assertThat("model.size", model.size(), Matchers.not(0));
    }
}

