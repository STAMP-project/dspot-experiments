/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.data.store;


import PropertyDataStoreFactory.DIRECTORY.description;
import org.geotools.data.DataAccessFactory.Param;
import org.geotools.data.property.PropertyDataStoreFactory;
import org.geotools.util.SimpleInternationalString;
import org.junit.Assert;
import org.junit.Test;


public class ParamInfoTest {
    @Test
    public void testTitle() {
        Param param = new Param("abc", String.class, new SimpleInternationalString("the title"), new SimpleInternationalString("the description"), true, 1, 1, null, null);
        ParamInfo pi = new ParamInfo(param);
        Assert.assertEquals("the title", pi.getTitle());
    }

    @Test
    public void testDescription() {
        Param param = PropertyDataStoreFactory.DIRECTORY;
        ParamInfo pi = new ParamInfo(param);
        Assert.assertEquals(description.toString(), pi.getTitle());
    }
}

