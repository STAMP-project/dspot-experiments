package org.mp4parser.tools;


import Path.component;
import junit.framework.Assert;
import org.junit.Test;
import org.mp4parser.IsoFile;


public class PathTest {
    IsoFile isoFile;

    @Test
    public void testComponentMatcher() {
        Assert.assertTrue(component.matcher("abcd").matches());
        Assert.assertTrue(component.matcher("xml ").matches());
        Assert.assertTrue(component.matcher("xml [1]").matches());
        Assert.assertTrue(component.matcher("..").matches());
    }
}

