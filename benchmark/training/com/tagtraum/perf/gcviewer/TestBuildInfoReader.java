package com.tagtraum.perf.gcviewer;


import com.tagtraum.perf.gcviewer.util.BuildInfoReader;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the class {@link BuildInfoReader} - makes sure that the properties from the file
 * can be read.
 *
 * @author <a href="mailto:gcviewer@gmx.ch">Joerg Wuethrich</a>
<p>created on: 05.12.2012</p>
 */
public class TestBuildInfoReader {
    @Test
    public void readVersion() {
        String version = BuildInfoReader.getVersion();
        Assert.assertThat("version", version, Matchers.notNullValue());
        Assert.assertThat("must not be empty", version, Matchers.not(Matchers.isEmptyOrNullString()));
    }

    @Test
    public void readBuildDate() {
        String buildDate = BuildInfoReader.getBuildDate();
        Assert.assertThat("buildDate", buildDate, Matchers.notNullValue());
        Assert.assertThat("must not be empty", buildDate, Matchers.not(Matchers.isEmptyOrNullString()));
    }
}

