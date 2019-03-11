/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.ppio;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.GenericApplicationContext;


public class ProcessParameterIOTest {
    public static class TestType {}

    private static ProcessParameterIO testPPIO = new ProcessParameterIO(ProcessParameterIOTest.TestType.class, ProcessParameterIOTest.TestType.class, "testPPIO") {};

    private static GenericApplicationContext context = new GenericApplicationContext();

    @Test
    public void testFindAllWithNullContext() {
        List<ProcessParameterIO> matches = ProcessParameterIO.findAll(new org.geotools.data.Parameter("testPPIO", ProcessParameterIOTest.TestType.class), null);
        Assert.assertEquals(1, matches.size());
        Assert.assertSame(ProcessParameterIOTest.testPPIO, matches.get(0));
    }

    @Test
    public void testFindAllWithSameContext() {
        List<ProcessParameterIO> matches = ProcessParameterIO.findAll(new org.geotools.data.Parameter("testPPIO", ProcessParameterIOTest.TestType.class), ProcessParameterIOTest.context);
        Assert.assertEquals(1, matches.size());
        Assert.assertSame(ProcessParameterIOTest.testPPIO, matches.get(0));
    }

    @Test
    public void testFindAllWithDifferentContext() {
        GenericApplicationContext myContext = new GenericApplicationContext();
        myContext.refresh();
        List<ProcessParameterIO> matches = ProcessParameterIO.findAll(new org.geotools.data.Parameter("testPPIO", ProcessParameterIOTest.TestType.class), myContext);
        Assert.assertEquals(0, matches.size());
    }
}

